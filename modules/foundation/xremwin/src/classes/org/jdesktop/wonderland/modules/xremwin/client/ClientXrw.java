/**
 * Project Wonderland
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
 * $State$
 */
package org.jdesktop.wonderland.modules.xremwin.client;

import java.awt.Toolkit;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import com.jme.math.Vector3f;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.CreateWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.DestroyWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ShowWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ConfigureWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.PositionWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.RestackWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.WindowSetDecoratedMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.WindowSetBorderWidthMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.WindowSetUserDisplMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.WindowSetRotateYMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.DisplayPixelsMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.CopyAreaMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ControllerStatusMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.MessageArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ServerMessageType;
import org.jdesktop.wonderland.modules.appbase.client.utils.stats.StatisticsReporter;
import org.jdesktop.wonderland.modules.appbase.client.utils.stats.StatisticsSet;
import org.jdesktop.wonderland.modules.appbase.client.ProcessReporter;
import org.jdesktop.wonderland.common.ExperimentalAPI;

// TODO: 0.4 protocol: temporarily insert
import org.jdesktop.wonderland.modules.xremwin.client.Proto.DisplayCursorMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.MoveCursorMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ShowCursorMsgArgs;

/*
 * The generic Xrw client superclass. This is a protocol interpreter
 * for the Xremwin protocol. It communicates with a Server which 
 * represents either an Xremwin server or Xremwin master.
 *
 * Known Subclasses: ClientXrwMaster, ClientXrwSlave
 *
 * @author deronj
 */
@ExperimentalAPI
abstract class ClientXrw implements Runnable {

    // The connection to the XRemwin server or master.
    protected ServerProxy serverProxy;

    // The unique ID of this client connecting to the app (assigned by the master).
    protected int clientId;
    private CreateWindowMsgArgs createWinMsgArgs = new CreateWindowMsgArgs();
    private DestroyWindowMsgArgs destroyWinMsgArgs = new DestroyWindowMsgArgs();
    private ShowWindowMsgArgs showWinMsgArgs = new ShowWindowMsgArgs();
    private ConfigureWindowMsgArgs configureWinMsgArgs = new ConfigureWindowMsgArgs();
    private PositionWindowMsgArgs positionWinMsgArgs = new PositionWindowMsgArgs();
    private RestackWindowMsgArgs restackWinMsgArgs = new RestackWindowMsgArgs();
    private WindowSetDecoratedMsgArgs winSetDecoratedMsgArgs = new WindowSetDecoratedMsgArgs();
    private WindowSetBorderWidthMsgArgs winSetBorderWidthMsgArgs = new WindowSetBorderWidthMsgArgs();
    private WindowSetUserDisplMsgArgs winSetUserDisplMsgArgs = new WindowSetUserDisplMsgArgs();
    private WindowSetRotateYMsgArgs winSetRotateYMsgArgs = new WindowSetRotateYMsgArgs();
    private DisplayPixelsMsgArgs displayPixelsMsgArgs = new DisplayPixelsMsgArgs();
    private CopyAreaMsgArgs copyAreaMsgArgs = new CopyAreaMsgArgs();
    private ControllerStatusMsgArgs controllerStatusMsgArgs = new ControllerStatusMsgArgs();

    // TODO: 0.4 protocol: temporarily insert
    private DisplayCursorMsgArgs displayCursorMsgArgs = new DisplayCursorMsgArgs();
    private MoveCursorMsgArgs moveCursorMsgArgs = new MoveCursorMsgArgs();
    private ShowCursorMsgArgs showCursorMsgArgs = new ShowCursorMsgArgs();

    // true indicates the client thread should stop running
    protected boolean stop;

    // For debug
    //private static final boolean ENABLE_XREMWIN_STATS = false;
    private static boolean ENABLE_XREMWIN_STATS = false;
    private StatisticsReporter statReporter;
    private long numRequests = 0;
    private long displayPixelsNumBytes = 0;
    private long numCopyAreas = 0;

    // For debug
    private static boolean verbose = false;
    private static Level loggerLevelOrig;


    static {
        /* For debug
        System.err.println("logger level obj = " + AppXrw.logger.getLevel());
        if (AppXrw.logger.getLevel() != null) {
        System.err.println("logger level int = " + AppXrw.logger.getLevel().intValue());
        } else {
        System.exit(1);
        }
         */
        loggerLevelOrig = AppXrw.logger.getLevel();
        if (verbose) {
            AppXrw.logger.setLevel(Level.FINER);
        }
    }

    // For debug
    public static void toggleXremwinStatsEnable() {
        ENABLE_XREMWIN_STATS = !ENABLE_XREMWIN_STATS;
        System.err.println("Xremwin statistics are " +
                (ENABLE_XREMWIN_STATS ? "enabled" : "disabled"));
    }

    // For debug
    public static void toggleVerbosity() {
        verbose = !verbose;
        if (verbose) {
            AppXrw.logger.setLevel(Level.FINER);
        } else {
            AppXrw.logger.setLevel(loggerLevelOrig);
        }
        System.err.println("Xremwin verbosity is " +
                (verbose ? "enabled" : "disabled"));
    }
    /** The associated application */
    protected AppXrw app;
    /**
     * The control arbitrator used by this app.
     */
    protected ControlArbXrw controlArb;
    /**
     * The protocol interpreter thread (the main loop of the client)
     */
    protected Thread thread;
    /** The output reporter */
    protected ProcessReporter reporter;
    /** Is the server connected? */
    protected boolean serverConnected;
    /** The cell of the app */
    protected AppCellXrw cell;
    /** Used by the logging messages in this class */
    private int messageCounter = 0;

    /**
     * Create a new instance of ClientXrw.
     *
     * @param app The application for whom the client is operating.
     * @param controlArb The control arbiter for the app.
     * @param reporter Report output and exit status to this.
     * @throws InstantiationException If it could not make contact with the server.
     */
    public ClientXrw(AppXrw app, ControlArbXrw controlArb, ProcessReporter reporter)
            throws InstantiationException {
        this.app = app;
        this.controlArb = controlArb;
        this.reporter = reporter;

        // TODO: it would be nice to put the app instance name here
        thread = new Thread(this, "Remote Window Client");

    /* TODO
    if (ENABLE_XREMWIN_STATS) {
    statReporter = new StatisticsReporter(15, new Statistics(),
    new Statistics(),
    new Statistics());
    statReporter.start();
    }
     */
    }

    /**
     * Release resources held. 
     */
    public void cleanup() {
        if (!stop && thread != null) {
            stop = true;
            try {
                thread.join();
            } catch (InterruptedException ex) {
            }
            thread = null;
        }

        if (reporter != null) {
            reporter.cleanup();
            reporter = null;
        }

        if (controlArb != null) {
            controlArb.releaseControl();
            controlArb.cleanup();
            controlArb = null;
        }

        app = null;

        if (serverProxy != null) {
            serverProxy.cleanup();
            serverProxy = null;
        }

        cell = null;
    }

    /** 
     * Start the interpreter thread.
     */
    protected void start() {
        thread.start();
    }

    /**
     * The app associated with this client.
     */
    public AppXrw getApp() {
        return app;
    }

    /**
     * Used to associate this app with the given cell. 
     * May only be called one time.
     *
     * @param cell The world cell containing the app.
     * @throws IllegalArgumentException If the cell already is associated
     * with an app.
     * @throws IllegalStateException If the app is already associated 
     * with a cell.
     */
    public synchronized void setCell(AppCellXrw cell)
            throws IllegalArgumentException, IllegalStateException {
        this.cell = cell;
    }

    /** 
     * The  main loop of the client.
     */
    public void run() {

        while (serverConnected && !stop) {

            // Read message type from the server.
            ServerMessageType msgType = serverProxy.getMessageType();
            AppXrw.logger.severe("msgType " + (++messageCounter) + ": " + msgType);

            /* TODO
            if (ENABLE_XREMWIN_STATS) {
            synchronized (this) {
            numRequests++;
            }
            }
             */

            // Get the full message
            MessageArgs msgArgs = readMessageArgs(msgType);
            if (msgArgs != null) {
                AppXrw.logger.severe("msgArgs: " + msgArgs);
            }

            // Process the message
            processMessage(msgType);
        }
    }

    /**
     * Read the specific message arguments for the given message type.
     *
     * @param msgType The message type.
     */
    protected MessageArgs readMessageArgs(ServerMessageType msgType) {
        switch (msgType) {

            case CREATE_WINDOW:
                serverProxy.getData(createWinMsgArgs);
                return createWinMsgArgs;

            case DESTROY_WINDOW:
                serverProxy.getData(destroyWinMsgArgs);
                return destroyWinMsgArgs;

            case SHOW_WINDOW:
                serverProxy.getData(showWinMsgArgs);
                return showWinMsgArgs;

            case CONFIGURE_WINDOW:
                serverProxy.getData(configureWinMsgArgs);
                return configureWinMsgArgs;

            case POSITION_WINDOW:
                serverProxy.getData(positionWinMsgArgs);
                return positionWinMsgArgs;

            case RESTACK_WINDOW:
                serverProxy.getData(restackWinMsgArgs);
                return restackWinMsgArgs;

            case WINDOW_SET_DECORATED:
                serverProxy.getData(winSetDecoratedMsgArgs);
                return winSetDecoratedMsgArgs;

            case WINDOW_SET_BORDER_WIDTH:
                serverProxy.getData(winSetBorderWidthMsgArgs);
                return winSetBorderWidthMsgArgs;

            case WINDOW_SET_USER_DISPLACEMENT:
                serverProxy.getData(winSetUserDisplMsgArgs);
                return winSetUserDisplMsgArgs;

            case WINDOW_SET_ROTATE_Y:
                serverProxy.getData(winSetRotateYMsgArgs);
                return winSetRotateYMsgArgs;

            case BEEP:
                serverProxy.getData();
                return null;

            case DISPLAY_PIXELS:
                serverProxy.getData(displayPixelsMsgArgs);
                return displayPixelsMsgArgs;

            case COPY_AREA:
                serverProxy.getData(copyAreaMsgArgs);
                return copyAreaMsgArgs;

            case CONTROLLER_STATUS:
                serverProxy.getData(controllerStatusMsgArgs);
                return controllerStatusMsgArgs;

            // TODO: 0.4 protocol: temporarily insert
            case DISPLAY_CURSOR:
                serverProxy.getData(displayCursorMsgArgs);
                return displayCursorMsgArgs;

            // TODO: 0.4 protocol: temporarily insert
            case MOVE_CURSOR:
                serverProxy.getData(moveCursorMsgArgs);
                return moveCursorMsgArgs;

            // TODO: 0.4 protocol: temporarily insert
            case SHOW_CURSOR:
                serverProxy.getData(showCursorMsgArgs);
                return moveCursorMsgArgs;

            default:
                throw new RuntimeException("Unknown server message: " + msgType);
        }
    }

    /**
     * Process the message that has been read for the given message type.
     *
     * @param msgType The message type.
     */
    protected void processMessage(ServerMessageType msgType) {
        WindowXrw win;

        switch (msgType) {

            case SERVER_DISCONNECT:
                serverConnected = false;
                break;

            case CREATE_WINDOW:

                // We can't make windows visible until we have a cell
                if (cell == null) {
                    app.waitForCell();
                }

                win = lookupWindow(createWinMsgArgs.wid);
                if (win != null) {
                    AppXrw.logger.warning("CreateWindow: redundant create: wid = " + createWinMsgArgs.wid);
                } else {
                    createWindow(createWinMsgArgs);
                }
                break;

            case DESTROY_WINDOW:
                win = lookupWindow(destroyWinMsgArgs.wid);
                if (win == null) {
                    AppXrw.logger.warning("DestroyWindow: window doesn't exist: wid = " + destroyWinMsgArgs.wid);
                } else {
                    destroyWindow(win);
                }
                break;

            case SHOW_WINDOW:
                win = lookupWindow(showWinMsgArgs.wid);
                if (win == null) {
                    AppXrw.logger.warning("ShowWindow: window doesn't exist: wid = " + showWinMsgArgs.wid);
                } else {
                    /* TODO: 0.4 protocol:
                    WindowXrw transientFor = lookupWindow(showWinMsgArgs.transientFor);
                    win.setVisible(showWinMsgArgs.show, transientFor);
                     */
                    win.setVisible(showWinMsgArgs.show, null);
                }
                break;

            case CONFIGURE_WINDOW:
                win = lookupWindow(configureWinMsgArgs.wid);
                if (win == null) {
                    AppXrw.logger.warning("ConfigureWindow: window doesn't exist: wid = " + configureWinMsgArgs.wid);
                } else {
                    configureWindow(win, configureWinMsgArgs);
                }
                break;

            case POSITION_WINDOW:
                // If the move was made interactively by this client, ignore it */
                if (positionWinMsgArgs.clientId != clientId) {
                    win = lookupWindow(positionWinMsgArgs.wid);
                    if (win == null) {
                        AppXrw.logger.warning("PositionWindow: window doesn't exist: wid = " + positionWinMsgArgs.wid);
                    } else {
                        win.setLocation(positionWinMsgArgs.x, positionWinMsgArgs.y);
                    }
                }
                break;

            case RESTACK_WINDOW:
                // If the move was made interactively by this client, ignore it */
                if (restackWinMsgArgs.clientId != clientId) {
                    win = lookupWindow(restackWinMsgArgs.wid);
                    if (win == null) {
                        AppXrw.logger.warning("RestackWindow: window doesn't exist: wid = " + restackWinMsgArgs.wid);
                    } else {
                        WindowXrw sibwin = lookupWindow(restackWinMsgArgs.sibid);
                        if (sibwin == null) {
                            AppXrw.logger.warning("RestackWindow: sibling window doesn't exist: sibid = " +
                                    restackWinMsgArgs.sibid);
                        } else {
                            win.setSiblingAbove(sibwin);
                        }
                    }
                }
                break;

            case WINDOW_SET_DECORATED:
                win = lookupWindow(winSetDecoratedMsgArgs.wid);
                if (win == null) {
                    AppXrw.logger.warning("WindowSetDecorated: window doesn't exist: wid = " + winSetDecoratedMsgArgs.wid);
                } else {
                    win.setTopLevel(winSetDecoratedMsgArgs.decorated);
                }
                break;

            case WINDOW_SET_BORDER_WIDTH:
                win = lookupWindow(winSetDecoratedMsgArgs.wid);
                if (win == null) {
                    AppXrw.logger.warning("WindowSetBorderWidth: window doesn't exist: wid = " +
                            winSetBorderWidthMsgArgs.wid);
                } else {
                    win.setBorderWidth(winSetBorderWidthMsgArgs.borderWidth);
                }

                break;

            case WINDOW_SET_USER_DISPLACEMENT:
                // If this was performed interactively by this client, ignore it
                if (winSetUserDisplMsgArgs.clientId != clientId) {
                    win = lookupWindow(winSetUserDisplMsgArgs.wid);
                    if (win == null) {
                        AppXrw.logger.warning("WindowSetUserDispl: window doesn't exist: wid = " +
                                winSetUserDisplMsgArgs.wid);
                    } else {
                        /* TODO: Window2D config methods not yet impl
                        win.setUserDisplacement(win, winSetUserDisplMsgArgs.userDispl);
                         */
                    }
                    break;
                }

            case WINDOW_SET_ROTATE_Y:
                // If this was performed interactively by this client, ignore it
                if (winSetRotateYMsgArgs.clientId != clientId) {
                    win = lookupWindow(winSetRotateYMsgArgs.wid);
                    if (win == null) {
                        AppXrw.logger.warning("WindowSetRotateY: window doesn't exist: wid = " + winSetRotateYMsgArgs.wid);
                    } else {
                        /* TODO: Window2D config methods not yet impl
                        win.setRotateY(win, winSetRotateYMsgArgs.roty);
                         */
                    }
                }
                break;

            case BEEP:
                Toolkit.getDefaultToolkit().beep();
                break;

            case DISPLAY_PIXELS:
                win = lookupWindow(displayPixelsMsgArgs.wid);
                if (win == null) {
                    AppXrw.logger.warning("DisplayPixels: invalid window ID = " + displayPixelsMsgArgs.wid);
                    return;
                }
                processDisplayPixels(win, displayPixelsMsgArgs);
                break;

            case COPY_AREA:
                win = lookupWindow(copyAreaMsgArgs.wid);
                if (win == null) {
                    AppXrw.logger.warning("CopyArea: window doesn't exist: wid = " + copyAreaMsgArgs.wid);
                } else {
                    synchronized (this) {
                        numCopyAreas++;
                    }

                    win.copyArea(copyAreaMsgArgs.srcX, copyAreaMsgArgs.srcY,
                            copyAreaMsgArgs.width, copyAreaMsgArgs.height,
                            copyAreaMsgArgs.dstX, copyAreaMsgArgs.dstY);
                }
                break;

            case CONTROLLER_STATUS:
                processControllerStatus(controllerStatusMsgArgs);
                break;

            // TODO: 0.4 protocol: temporarily insert
            case DISPLAY_CURSOR:
            case MOVE_CURSOR:
            case SHOW_CURSOR:
                break;

            default:
                throw new RuntimeException("Internal error: no handler for message type : " + msgType);
        }
    }

    /**
     * Handle the ControllerStatus Message.
     *
     * @param msgArgs The arguments which have been read for the ControllerStatus message.
     */
    protected void processControllerStatus(ControllerStatusMsgArgs msgArgs) {

        switch (msgArgs.status) {

            case REFUSED:
                // We only care about our attempts that are refused
                if (msgArgs.clientId == clientId) {
                    controlArb.controlRefused();
                }
                break;

            case GAINED:
                // We only care about our attempts that succeed
                if (msgArgs.clientId == clientId) {
                    controlArb.controlGained();
                }
                break;

            case LOST:
                if (msgArgs.clientId == clientId) {
                    controlArb.controlLost();
                } else {
                    // Update control highlighting for other clients besides control loser
                    controlArb.setController(null);
                }
                break;
        }
    }

    /**
     * Given a window ID return the associated window.
     *
     * @param wid The X11 window ID.
     */
    protected WindowXrw lookupWindow(int wid) {
        synchronized (AppXrw.widToWindow) {
            return AppXrw.widToWindow.get(wid);
        }
    }

    /**
     * Associate this window ID with this window.
     *
     * @param wid The X11 window ID.
     * @param window The window to associate with the wid.
     */
    protected void addWindow(int wid, WindowXrw window) {
        synchronized (AppXrw.widToWindow) {
            AppXrw.widToWindow.put(wid, window);
        }
    }

    /**
     * Remove the association of fhis window ID with its window.
     *
     * @param window The window to disassociate from the wid.
     */
    protected void removeWindow(WindowXrw win) {
        synchronized (AppXrw.widToWindow) {
            AppXrw.widToWindow.remove(win.getWid());
        }
    }

    /**
     * Create a window. 
     *
     * @param msg The message arguments which have been read for the CreateWindow message.
     */
    protected WindowXrw createWindow(CreateWindowMsgArgs msg) {
        try {
            WindowXrw win = app.createWindow(msg.x, msg.y, msg.wAndBorder, msg.hAndBorder, msg.borderWidth,
                    msg.decorated, msg.wid);
            addWindow(msg.wid, win);
            return win;
        } catch (IllegalStateException ex) {
            AppXrw.logger.warning("CreateWindow: Cannot create window " + msg.wid);
            return null;
        }
    }

    /**
     * Destroy the given window.
     *
     * @param win The window to destroy.
     */
    private void destroyWindow(WindowXrw win) {
        removeWindow(win);
        win.cleanup();
    }

    /** 
     * Configure (that is, resize, move or restack) a window.
     *
     * @param win The window to configure.
     * @param msg The message arguments which have been read for the ConfigureWindow message.
     */
    private void configureWindow(WindowXrw win, ConfigureWindowMsgArgs msg) {

        // Is this a configure from ourselves or some other client?
        if (msg.clientId == clientId) {

            // Self configure: see if this is a size change
            if (msg.wAndBorder != win.getWidth() ||
                    msg.hAndBorder != win.getHeight()) {

                // Accept this self resize. This is because the user resize operation
                // is not completely finished until setDimensions is called with
                // the new width and height
            } else {
                // Not a resize. It's a move-or-restack-only from ourselves. Ignore it.
            }
        }

        WindowXrw sibWin = lookupWindow(msg.sibid);
        win.configure(msg.x, msg.y, msg.wAndBorder, msg.hAndBorder, sibWin);
    }

    /**
     * Process the display pixels message.
     *
     * @param win The window in which to display pixels.
     * @param displayPixelsMsgArgs The message arguments which have been read for the message.
     */
    private void processDisplayPixels(WindowXrw win, DisplayPixelsMsgArgs displayPixelsMsgArgs) {
        switch (displayPixelsMsgArgs.encoding) {

            case UNCODED:
                //displayRect(win, displayPixelsMsgArgs);
                throw new RuntimeException("UNCODED pixels from the xremwin server is no longersupported.");

            case RLE24:
                displayRectRle24(win, displayPixelsMsgArgs);
                break;

            default:
                throw new RuntimeException("Unknown pixel encoding " + displayPixelsMsgArgs.encoding);
        }
    }
    /**
     * Uncoded DisplayPixels (OBSOLETE)
     * If win == null we read the pixels but discard them.
    private void displayRect (WindowXrw win, DisplayPixelsMsgArgs dpMsgArgs) {

    //AppXrw.logger.finer("displayRect");
    //AppXrw.logger.finer("x = " + dpMsgArgs.x);
    //AppXrw.logger.finer("y = " + dpMsgArgs.y);
    //AppXrw.logger.finer("w = " + dpMsgArgs.w);
    //AppXrw.logger.finer("h = " + dpMsgArgs.h);

    serverProxy.setScanLineWidth(dpMsgArgs.w);

    int[] winPixels = new int[dpMsgArgs.w * dpMsgArgs.h];

    int dstIdx = 0;
    int dstNextLineIdx = 0;

    for (int y = 0; y < dpMsgArgs.h; y++) {
    dstNextLineIdx += dpMsgArgs.w;

    // Reads into scanLineBuf
    byte[] scanLineBytes = serverProxy.readScanLine();

    int srcIdx = 0;

    for (int i = 0;
    i < dpMsgArgs.w && srcIdx < scanLineBytes.length - 2;
    i++, srcIdx += 4) {

    //AppXrw.logger.finer("dstIdx = " + dstIdx);
    //AppXrw.logger.finer("srcIdx = " + srcIdx);
    //AppXrw.logger.finer("winPixels.length = " + winPixels.length);
    //AppXrw.logger.finer("scanLineBytes.length = " + scanLineBytes.length);

    // Note: source format is BGRX and dest format is XBGR
    winPixels[dstIdx++] =
    ((scanLineBytes[srcIdx + 2] & 0xff) << 16) |
    ((scanLineBytes[srcIdx + 1] & 0xff) <<  8) |
    (scanLineBytes[srcIdx + 0] & 0xff);
    }

    dstIdx = dstNextLineIdx;
    }

    //Debug: print all scanlines collected
    //printRLBegin();
    //for (int y = 0; y < dpMsgArgs.h; y++) {
    //    int idx = y * dpMsgArgs.w ;
    //    for (int i = 0; i < dpMsgArgs.w; i++) {
    //	printRL(winPixels[idx + i]);
    //    }
    //}
    //printRLEnd();

    if (win != null) {
    win.displayPixels(dpMsgArgs.x, dpMsgArgs.y, dpMsgArgs.w, dpMsgArgs.h, winPixels);
    }
    }
     */
    private int maxVerboseRuns = 120;
    private int numRunsReceived = 0;
    private byte[] chunkBuf = null;
    private int chunkBufSize = 0;

    /**
     * Decode a run-length encoded DisplayPixels message. If win == null we read the pixels but discard them.
     *
     * @param win The window in which to display pixels.
     * @param dpMsgArgs The message arguments which have been read for the message.
     */
    private void displayRectRle24(WindowXrw win, DisplayPixelsMsgArgs dpMsgArgs) {

        synchronized (this) {
            displayPixelsNumBytes += dpMsgArgs.w * dpMsgArgs.h * 4;
        }

        int h = dpMsgArgs.h;
        int numChunks = serverProxy.readRleInt();

        /*
        AppXrw.logger.finer("displayRectRle24");
        AppXrw.logger.finer("x = " + dpMsgArgs.x);
        AppXrw.logger.finer("y = " + dpMsgArgs.y);
        AppXrw.logger.finer("w = " + dpMsgArgs.w);
        AppXrw.logger.finer("h = " + h);
        AppXrw.logger.finer("numChunks = " + numChunks);
         */

        int[] winPixels = new int[dpMsgArgs.w * dpMsgArgs.h];

        int dstIdx = 0;
        int x = 0;
        int chunkCount = 0;

        while (numChunks-- > 0) {

            int chunkHeight = serverProxy.readRleInt();
            int chunkBytes = serverProxy.readRleInt();
            if (chunkBytes > chunkBufSize) {
                chunkBuf = new byte[chunkBytes];
                chunkBufSize = chunkBytes;
            }
            //AppXrw.logger.finer("chunkCount = " + chunkCount);
            //AppXrw.logger.finer("chunkBytes = " + chunkBytes);

            int dstNextLineIdx = dstIdx + chunkHeight * dpMsgArgs.w;

            // Read first chunk of data from server
            serverProxy.readRleChunk(chunkBuf, chunkBytes);

            int chunkOffset = 0;

            while (chunkBytes > 0) {
                int count = chunkBuf[chunkOffset + 3] & 0xFF;
                int pixel = ((chunkBuf[chunkOffset + 2] + 256) & 0xFF) << 16 |
                        ((chunkBuf[chunkOffset + 1] + 256) & 0xFF) << 8 |
                        ((chunkBuf[chunkOffset + 0] + 256) & 0xFF);
                /*
                if (numRunsReceived++ < maxVerboseRuns) {
                AppXrw.logger.finer("numRunsReceived = " + numRunsReceived);
                AppXrw.logger.finer("count = " + count);
                AppXrw.logger.finer("pixel = " + Integer.toHexString(pixel));
                }
                 */

                for (int i = 0; i < count; i++, x++) {
                    if (x >= dpMsgArgs.w) {
                        x = 0;
                        dstIdx += dpMsgArgs.w;
                    }
                    try {
                        // TODO: this works around an index-out-of-bounds problem. Why?
                        if ((dstIdx + x) < winPixels.length) {
                            winPixels[dstIdx + x] = pixel;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        AppXrw.logger.finer("*********** Array out of bounds!!!!!!!!!!!!!!!!");
                        AppXrw.logger.finer("winPixels.length = " + winPixels.length);
                        AppXrw.logger.finer("dstIdx + x = " + (dstIdx + x));
                        AppXrw.logger.finer("dstIdx = " + dstIdx);
                        AppXrw.logger.finer("x = " + x);
                        AppXrw.logger.finer("chunkCount = " + chunkCount);
                        AppXrw.logger.finer("chunkBytes = " + chunkBytes);
                    }
                }

                chunkOffset += 4;
                chunkBytes -= 4;
            }

            dstIdx = dstNextLineIdx;
            x = 0;

            chunkCount++;
        }

        // Now transfer the decoded pixels into the texture
        if (win != null) {
            // TODO: win.displayPixels(dpMsgArgs.x, dpMsgArgs.y, dpMsgArgs.w, h, winPixels);
        }
    }

    /**
     * Sends updates to the user displacement to the server.
     *
     * @param win The window being displaced.
     * @param userDispl The new displacement vector.
     */
    public void windowSetUserDisplacement(WindowXrw win, Vector3f userDispl) {
        int wid = ((WindowXrw) win).getWid();
        AppXrw.logger.finer("To server: SetUserDispl: wid = " + wid + ", userDispl = " + userDispl);

        try {
            serverProxy.windowSetUserDisplacement(clientId, wid, userDispl);
        } catch (IOException ex) {
            AppXrw.logger.warning("Client cannot send user displacement for window " + wid);
        }
    }

    /**
     * Sends updates to the window size to the server.
     *
     * @param win The window being displaced.
     * @param w The new width of the window.
     * @param h The new height of the window.
     */
    public void windowSetSize(WindowXrw win, int w, int h) {
        int wid = ((WindowXrw) win).getWid();
        AppXrw.logger.finer("To server: SetSize: wid = " + wid + ", wh = " + w + ", " + h);

        try {
            serverProxy.windowSetSize(clientId, wid, w, h);
        } catch (IOException ex) {
            AppXrw.logger.warning("Client cannot send size for window " + wid);
        }
    }

    /**
     * Sends updates to the window's Y rotation to the server.
     *
     * @param win The window being rotated.
     * @param angle The new Y rotation angle of the window.
     */
    public void windowSetRotateY(WindowXrw win, float angle) {
        int wid = ((WindowXrw) win).getWid();
        AppXrw.logger.finer("To server: SetRotateY: wid = " + wid + ", angle = " + angle);

        try {
            serverProxy.windowSetRotateY(clientId, wid, angle);
        } catch (IOException ex) {
            AppXrw.logger.warning("Client cannot send rotation Y for window " + wid);
        }
    }

    /**
     * Sends a message to the server telling it that the window has been moved to the front
     * of all other windows on the stack.
     *
     * @param win The window whose stack position has changed.
     */
    public void windowToFront(WindowXrw win) {
        int wid = win.getWid();
        AppXrw.logger.finer("To server: ToFront: wid = " + wid);

        try {
            serverProxy.windowToFront(clientId, wid);
        } catch (IOException ex) {
            AppXrw.logger.warning("Client cannot send toFront for window " + wid);
        }
    }

    /**
     * Close the given window.
     *
     * @param win The window to close.
     */
    public abstract void closeWindow(WindowXrw win);

    /*
     ** For Debug: Print pixel run lengths
     */
    private boolean printRLLastValueValid;
    private int printRLLastValue;
    private int printRLLastValueCount;

    private void printScanLine(byte[] scanLineBuf, int width) {
        printRLBegin();
        for (int i = 0; i < width; i++) {
            int m = (scanLineBuf[i * 4 + 2] & 0xFF) << 16 |
                    (scanLineBuf[i * 4 + 1] & 0xFF) << 8 |
                    (scanLineBuf[i * 4] & 0xFF);
            printRL(m);
        }
        printRLEnd();
    }

    private void printRLRun() {
        if (printRLLastValueCount > 0) {
            System.err.print(printRLLastValue);
            if (printRLLastValueCount > 1) {
                System.err.println(" x " + printRLLastValueCount);
            } else {
                System.err.println();
            }
        }
    }

    private void printRLBegin() {
        printRLLastValueValid = false;
        printRLLastValueCount = 0;
    }

    private void printRL(int value) {
        if (printRLLastValueValid && value == printRLLastValue) {
            printRLLastValueCount++;
        } else {
            printRLRun();

            printRLLastValueCount = 1;
            printRLLastValue = value;
            printRLLastValueValid = true;
        }
    }

    private void printRLEnd() {
        printRLRun();
    }

    public void printWindowTransformsAll() {
        synchronized (AppXrw.widToWindow) {
            Iterator it = AppXrw.widToWindow.values().iterator();
            while (it.hasNext()) {
                WindowXrw win = (WindowXrw) it.next();
                if (win.isVisible()) {
                    // TODO: notyet : win.printTransform();
                }
            }
        }
    }

    // Note: pixel buffer may be longer than w*h
    protected byte[] intAryToByteAry(int[] pixels, int w, int h) {
        byte[] bytes = new byte[w * h * 4];

        int srcIdx, dstIdx;
        for (srcIdx = 0  , dstIdx = 0;
                srcIdx < w * h;
                srcIdx++) {

            int pixel = pixels[srcIdx];
            bytes[dstIdx++] = (byte) ((pixel >> 24) & 0xff);
            bytes[dstIdx++] = (byte) ((pixel >> 16) & 0xff);
            bytes[dstIdx++] = (byte) ((pixel >> 8) & 0xff);
            bytes[dstIdx++] = (byte) (pixel & 0xff);
        }

        return bytes;
    }

    /**
     * Rearrange the window stack so that the windows are in the given order.
     *
     * @param order An array which indicates the order in which the windows
     * are to appear in the stack. The window at order[index] should have
     * stack position N-index, where N is the number of windows in the stack.
     */
    public void restackWindows(WindowXrw[] order) {
        app.restackWindows(order);
    }

    private class Statistics extends StatisticsSet {

        // The number of requests of any type received
        private long numRequests;

        // The number of Display Pixels message bytes received
        private long displayPixelsNumBytes;

        // The number of Copy Area messages received
        private long numCopyAreas;

        protected Statistics() {
            super("Xremwin");
        }

        protected boolean hasTriggered() {
            // Don't print stats for silent windows
            return numRequests != 0;
        }

        protected void probe() {
            synchronized (ClientXrw.this) {
                numRequests += ClientXrw.this.numRequests;
                displayPixelsNumBytes = ClientXrw.this.displayPixelsNumBytes;
                numCopyAreas = ClientXrw.this.numCopyAreas;
            }
        }

        protected void reset() {
            synchronized (ClientXrw.this) {
                ClientXrw.this.numRequests = 0;
                ClientXrw.this.displayPixelsNumBytes = 0;
                ClientXrw.this.numCopyAreas = 0;
            }
        }

        protected void accumulate(StatisticsSet cumulativeStats) {
            Statistics stats = (Statistics) cumulativeStats;
            stats.numRequests += numRequests;
            stats.displayPixelsNumBytes += displayPixelsNumBytes;
            stats.numCopyAreas += numCopyAreas;
        }

        protected void max(StatisticsSet maxStats) {
            Statistics stats = (Statistics) maxStats;
            stats.numRequests += max(stats.numRequests, numRequests);
            stats.displayPixelsNumBytes = max(stats.displayPixelsNumBytes, displayPixelsNumBytes);
            stats.numCopyAreas = max(stats.numCopyAreas, numCopyAreas);
        }

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendStats (StringBuffer sb) {
            sb.append("numRequests = " + numRequests + "\n");
            sb.append("displayPixelsNumBytes = " + displayPixelsNumBytes + "\n");
            sb.append("numCopyAreas = " + numCopyAreas + "\n");
        }

	/**
	 * {@inheritDoc}
	 */
	@Override
        protected void appendStatsAndRates (StringBuffer sb, double timeSecs) {
            appendStats(sb);

            // Calculate and print rates
            double numRequestsPerSec = numRequests / timeSecs;
            sb.append("numRequestsPerSec = " + numRequestsPerSec + "\n");
            double displayPixelsNumBytesPerSec = displayPixelsNumBytes / timeSecs;
            sb.append("displayPixelsNumBytes = " + displayPixelsNumBytesPerSec + "\n");
            double numCopyAreasPerSec = numCopyAreas / timeSecs;
            sb.append("numCopyAreas = " + numCopyAreasPerSec + "\n");
        }
    }
}
