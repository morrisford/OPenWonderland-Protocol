/**
 * Project Wonderland
 *
 * $Id$
 * 
 * Copyright (c) 2004-2008, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * $Revision$
 * $Date$
 */
package org.jdesktop.wonderland.client.cell;

import com.jme.bounding.BoundingVolume;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.cell.view.ViewCell;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.MultipleParentException;
import org.jdesktop.wonderland.common.cell.config.CellConfig;

/**
 * A basic implementation of core cell cache features. This is a convenience class
 * designed to be called from more complete cache implementations. 
 * 
 * @author paulby
 */
public class CellCacheBasicImpl implements CellCache, CellCacheConnection.CellCacheMessageListener {

    private Map<CellID, Cell> cells = Collections.synchronizedMap(new HashMap());
    private Set<Cell> rootCells = Collections.synchronizedSet(new HashSet());
    
    protected static Logger logger = Logger.getLogger(CellCacheBasicImpl.class.getName());
    
    private ViewCell viewCell = null;

    /** the classloader to use when instantiating classes */
    private ClassLoader classLoader;
    
    /** the session this cache is associated with */
    private WonderlandSession session;
    
    /** the connection for sending cell cache information */
    private CellCacheConnection cellCacheConnection;
    
    /** the connection for sending cell information */
    private CellChannelConnection cellChannelConnection;

    private ExecutorService cacheExecutor = Executors.newSingleThreadExecutor();
    
    /**
     * Create a new cache implementation
     * @param session the WonderlandSession the cache is associated with
     * @param cellCacheConnection the connection for sending cell cache
     * information
     * @param cellChannelConnection the connectiong for sending cell channel
     * messages
     */
    public CellCacheBasicImpl(WonderlandSession session,
                              ClassLoader classLoader,
                              CellCacheConnection cellCacheConnection,
                              CellChannelConnection cellChannelConnection) {
        this.session = session;
        this.classLoader = classLoader;
        this.cellCacheConnection = cellCacheConnection;
        this.cellChannelConnection = cellChannelConnection;
        
        ClientContext.registerCellCache(this, session);
        if (this.classLoader == null) {
            this.classLoader = getClass().getClassLoader();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Cell getCell(CellID cellId) {
        return cells.get(cellId);
    }
    
    /**
     * Return the set of cells in this cache
     * @return
     */
    public Cell[] getCells() {
        return (Cell[]) cells.values().toArray(new Cell[cells.size()]);
    }

    /**
     * {@inheritDoc}
     */
    public Cell loadCell(CellID cellId, 
                         String className, 
                         BoundingVolume localBounds, 
                         CellID parentCellID, 
                         CellTransform cellTransform, 
                         CellConfig setup,
                         String cellName) {
        if (cells.containsKey(cellId)) {
            logger.severe("Attempt to create cell that already exists "+cellId);
            return null;
        }

        logger.fine("creating cell "+className+" "+cellId);
        Cell cell = instantiateCell(className, cellId);
        if (cell==null)
            return null;     // Instantiation failed, error has already been logged
        
        cell.setName(cellName);
        Cell parent = cells.get(parentCellID);
        if (parent!=null) {
            try {
                parent.addChild(cell);
            } catch (MultipleParentException ex) {
                logger.log(Level.SEVERE, "Failed to load cell", ex);
            }
        }

        cell.setLocalBounds(localBounds);
        cell.setLocalTransform(cellTransform, TransformChangeListener.ChangeSource.SERVER_ADJUST);
//        System.out.println("Loading Cell "+className+" "+cellTransform.getTranslation(null));

        cells.put(cellId, cell);

        // record the set of root cells
        if (parent==null) {
            rootCells.add(cell);
        }

        if (setup!=null)
            cell.configure(setup);

        // Force the cell to create the JME renderer entity
        createCellRenderer(cell);

        if (viewCell!=null) {
            // No point in makeing cells active if we don't have a view
            // The changeCellStatus actually changes the status on another thread
            // so we don't perform geometry load operations on the DS listener thread
            changeCellStatus(cell, CellStatus.ACTIVE);
        }

        return cell;
    }

    /**
     * Create a the cell renderer for this cache.
     * @param cell the cell to create a renderer for
     */
    protected CellRenderer createCellRenderer(Cell cell) {
        return cell.getCellRenderer(ClientContext.getRendererType());
    }

    /**
     * Unload the cell from memory, sets the Cells status to DISK
     * @param cellId
     */
    public void unloadCell(CellID cellId) {
        Cell cell = cells.remove(cellId);
        cell.setStatus(CellStatus.DISK);
    }

    /**
     * {@inheritDoc}
     */
    public void deleteCell(CellID cellId) {
        // TODO - remove local resources from client asset cache as long
        // as they are not shared
        Cell cell = cells.remove(cellId);
        cell.setStatus(CellStatus.DISK);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<Cell> getRootCells() {
        return new LinkedList(rootCells);
    }
    
    private Cell instantiateCell(String className, CellID cellId) {
        Cell cell;
        
        try {
            Class clazz = Class.forName(className, true, classLoader);
            Constructor constructor = clazz.getConstructor(CellID.class, CellCache.class);
            cell = (Cell) constructor.newInstance(cellId, this);
        } catch(Exception e) {
            logger.log(Level.SEVERE, "Problem instantiating cell "+className, e);
            return null;
        }
        
        return cell;
    }

    /**
     * {@inheritDoc}
     */
    public void setViewCell(ViewCell viewCell) {
        this.viewCell = viewCell;
        
        // Activate all current cells
        synchronized(cells) {
            for(Cell cell : cells.values()) {
                if (cell.getStatus().ordinal()<CellStatus.ACTIVE.ordinal())
                    changeCellStatus(cell, CellStatus.ACTIVE);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public ViewCell getViewCell() {
        return viewCell;
    }
    
    /**
     * {@inheritDoc}
     */
    public WonderlandSession getSession() {
        return session;
    }

    
    public CellChannelConnection getCellChannelConnection() {
        return cellChannelConnection;
    }

    /**
     * Cell status changes can take a while so should not be performed on the
     * Darkstar listener thread that calls the cache methods. Therefore
     * schedule a task actually apply the status change to the cell.
     * @param cell
     * @param status
     */
    private void changeCellStatus(Cell cell, CellStatus status) {
        cacheExecutor.submit(new CellStatusChanger(cell, status));
    }


    private class CellStatusChanger implements Runnable {

        private Cell cell;
        private CellStatus cellStatus;

        public CellStatusChanger(Cell cell, CellStatus status) {
            this.cell = cell;
            this.cellStatus = status;
        }

        public void run() {
            cell.setStatus(cellStatus);
        }

    }
}
