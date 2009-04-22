package org.jdesktop.wonderland.modules.audiomanager.client;

import org.jdesktop.wonderland.client.ClientContext;

import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellCache;

import org.jdesktop.wonderland.common.auth.WonderlandIdentity;

import org.jdesktop.wonderland.common.cell.CellID;

import org.jdesktop.wonderland.modules.audiomanager.common.messages.VoiceChatBusyMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.VoiceChatInfoRequestMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.VoiceChatJoinMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.VoiceChatJoinAcceptedMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.VoiceChatJoinRequestMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.VoiceChatLeaveMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.VoiceChatMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.VoiceChatMessage.ChatType;

import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManager;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManagerFactory;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManagerListener;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManagerListener.ChangeType;
import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;

import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.NameTag;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.NameTag.EventType;

import org.jdesktop.wonderland.client.comms.WonderlandSession;

import java.io.IOException;

import java.util.ArrayList;

import java.util.concurrent.ConcurrentHashMap;

import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import java.awt.Point;

/*
 * IncomingCallDialog.java
 *
 * Created on April 20, 2009, 12:31 PM
 */



/**
 *
 * @author  jp
 */
public class IncomingCallDialog extends javax.swing.JFrame implements PresenceManagerListener {

    private static final Logger logger =
        Logger.getLogger(IncomingCallDialog.class.getName());

    private ChatType chatType = ChatType.PRIVATE;

    private AudioManagerClient client;
    private WonderlandSession session;

    private CellID cellID;

    private String group;

    private PresenceManager pm;

    private PresenceInfo caller;

    /** Creates new form IncomingCallDialog */
    public IncomingCallDialog() {
        initComponents();
    }

    public IncomingCallDialog(AudioManagerClient client, WonderlandSession session, 
	    CellID cellID, VoiceChatJoinRequestMessage message) {

        this.client = client;
        this.cellID = cellID;
        this.session = session;

        initComponents();

	group = message.getGroup();

	caller = message.getCaller();

	callerText.setText(caller.usernameAlias);

        pm = PresenceManagerFactory.getPresenceManager(session);

        pm.addPresenceManagerListener(this);

        setVisible(true);
    }

    public void presenceInfoChanged(PresenceInfo presenceInfo, ChangeType type) {
    }

    public void aliasChanged(String previousAlias, PresenceInfo presenceInfo) {
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        titleLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        secretRadioButton = new javax.swing.JRadioButton();
        privateRadioButton = new javax.swing.JRadioButton();
        publicRadioButton = new javax.swing.JRadioButton();
        answerButton = new javax.swing.JButton();
        ignoreButton = new javax.swing.JButton();
        busyButton = new javax.swing.JButton();
        callerText = new javax.swing.JLabel();

        titleLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 15));
        titleLabel.setText("Incoming call from:");

        jLabel1.setText("Privacy:");

        buttonGroup1.add(secretRadioButton);
        secretRadioButton.setText("Secret");
        secretRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                secretRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(privateRadioButton);
        privateRadioButton.setSelected(true);
        privateRadioButton.setText("Private");
        privateRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                privateRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(publicRadioButton);
        publicRadioButton.setText("Public");
        publicRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                publicRadioButtonActionPerformed(evt);
            }
        });

        answerButton.setText("Answer");
        answerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                answerButtonActionPerformed(evt);
            }
        });

        ignoreButton.setText("Ignore");
        ignoreButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ignoreButtonActionPerformed(evt);
            }
        });

        busyButton.setText("Busy");
        busyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                busyButtonActionPerformed(evt);
            }
        });

        callerText.setText("                           ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(secretRadioButton)
                        .addGap(18, 18, 18)
                        .addComponent(privateRadioButton)
                        .addContainerGap(146, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(titleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                                .addComponent(callerText, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(answerButton, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                                .addGap(31, 31, 31)
                                .addComponent(busyButton, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(38, 38, 38)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(publicRadioButton)
                                    .addComponent(ignoreButton, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(30, 30, 30))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(callerText)
                    .addComponent(titleLabel))
                .addGap(121, 121, 121)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(secretRadioButton)
                    .addComponent(privateRadioButton)
                    .addComponent(publicRadioButton))
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(answerButton)
                    .addComponent(busyButton)
                    .addComponent(ignoreButton))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void answerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_answerButtonActionPerformed
    
    PresenceInfo callee = pm.getPresenceInfo(cellID);

    System.out.println("Answer button pressed for " + callee);

    if (callee == null) {
        logger.warning("Can't find presence info for " + cellID);
        return;
    }

    session.send(client, new VoiceChatJoinAcceptedMessage(group, callee, chatType));
 
    logger.info("Sent join message");

    InCallDialog inCallDialog = new InCallDialog(client, session, cellID, group);

    inCallDialog.setLocation(new Point((int) getLocation().getX() + getWidth(), (int) getLocation().getY()));

    setVisible(false);
}//GEN-LAST:event_answerButtonActionPerformed

private void busyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_busyButtonActionPerformed
    PresenceInfo callee = pm.getPresenceInfo(cellID);
 
    session.send(client, new VoiceChatBusyMessage(group, caller, callee, chatType));
}//GEN-LAST:event_busyButtonActionPerformed

private void ignoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ignoreButtonActionPerformed
    setVisible(false);
}//GEN-LAST:event_ignoreButtonActionPerformed

private void publicRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_publicRadioButtonActionPerformed
    chatType = ChatType.PUBLIC;
}//GEN-LAST:event_publicRadioButtonActionPerformed

private void secretRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_secretRadioButtonActionPerformed
    chatType = ChatType.SECRET;
}//GEN-LAST:event_secretRadioButtonActionPerformed

private void privateRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_privateRadioButtonActionPerformed
    chatType = ChatType.PRIVATE;
}//GEN-LAST:event_privateRadioButtonActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new IncomingCallDialog().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton answerButton;
    private javax.swing.JButton busyButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel callerText;
    private javax.swing.JButton ignoreButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JRadioButton privateRadioButton;
    private javax.swing.JRadioButton publicRadioButton;
    private javax.swing.JRadioButton secretRadioButton;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables

}