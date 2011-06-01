/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdesktop.wonderland.server.comms;

import org.jdesktop.wonderland.common.comms.DefaultProtocolVersion;

/**
 *
 * @author morrisford
 */
public class XYZZYProtocolVersion extends DefaultProtocolVersion
    {
    public static final String PROTOCOL_NAME = "XYZZY";
    public static final XYZZYProtocolVersion VERSION = new XYZZYProtocolVersion();

    private XYZZYProtocolVersion() {
        super (0, 0, 1);
    }
}
