/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdesktop.wonderland.server.comms;

import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import org.jdesktop.wonderland.common.comms.ProtocolVersion;
import org.jdesktop.wonderland.common.comms.WonderlandProtocolVersion;

/**
 *
 * @author morrisford
 */
/**
 * The new XYZZY communications protocol used by Morris' new clients
 * @author jkaplan
 */
public class XYZZYCommsProtocol implements CommunicationsProtocol
    {
    /**
     * Get the name of this protocol
     * @return "wonderland_client"
     */
    public String getName()
        {
        return "XYZZY";
        }

    /**
     * Get the version of this protocol
     * @return the protocol version
     */
    public ProtocolVersion getVersion()
        {
        return XYZZYProtocolVersion.VERSION;
        }

    public WonderlandSessionListener createSessionListener(ClientSession session, ProtocolVersion version, CommunicationsProtocol cp)
        {
        return new WonderlandSessionListener(session, cp);
        }

//    public ClientSessionListener createSessionListener(ClientSession session, ProtocolVersion version) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
    }
