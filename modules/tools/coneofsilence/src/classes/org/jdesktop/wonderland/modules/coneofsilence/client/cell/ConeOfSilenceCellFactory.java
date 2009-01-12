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

package org.jdesktop.wonderland.modules.coneofsilence.client.cell;

import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.registry.CellFactory;
import org.jdesktop.wonderland.client.cell.registry.CellPaletteInfo;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.cell.state.CellServerState.Rotation;
import org.jdesktop.wonderland.modules.coneofsilence.common.ConeOfSilenceCellSetup;

import com.jme.math.Vector3f;

/**
 * The cell factory for the sample cell.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ConeOfSilenceCellFactory implements CellFactory {

    public String[] getExtensions() {
        return new String[] {};
    }

    public <T extends CellServerState> T getDefaultCellSetup() {
        // Create a setup with some default values
        ConeOfSilenceCellSetup setup = new ConeOfSilenceCellSetup();
        setup.setName("COS");
        setup.setFullVolumeRadius(2.0);

	Vector3f axis = new Vector3f((float) 1, (float) 0, (float) 0);
	/*
	 * Try rotating 45 degrees to see what that does.
	 */
	setup.setRotation(new Rotation(axis, (float) Math.PI / 4));

        Logger.getLogger(ConeOfSilenceCellFactory.class.getName()).warning("COS!!!!");
        return (T)setup;
    }

    public CellPaletteInfo getCellPaletteInfo() {
        return new ConeOfSilenceCellPaletteInfo();
    }
}
