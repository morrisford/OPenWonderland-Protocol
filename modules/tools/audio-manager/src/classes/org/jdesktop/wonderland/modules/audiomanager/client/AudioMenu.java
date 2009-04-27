/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * Sun designates this particular file as subject to the "Classpath" 
 * exception as provided by Sun in the License file that accompanied 
 * this code.
 */
package org.jdesktop.wonderland.modules.audiomanager.client;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.client.softphone.AudioQuality;
import org.jdesktop.wonderland.client.softphone.SoftphoneControlImpl;

/**
 *
 * @author paulby
 */
public class AudioMenu extends javax.swing.JPanel {

    private AudioMenuListener audioMenuListener;
    private static AudioMenu audioM = null;
    private JMenuItem softphoneMenuItem;
    private JMenuItem voiceChatMenuItem;
    private JCheckBoxMenuItem muteCheckBox;

    /** Creates new form AudioMenu */
    AudioMenu(final AudioMenuListener audioMenuListener) {
        initComponents();
        this.audioMenuListener = audioMenuListener;
        populateAudioQualityMenu();

        softphoneMenuItem = new JMenuItem("Softphone");
        softphoneMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (audioMenuListener != null) {
                    audioMenuListener.showSoftphone(softphoneMenuItem.isSelected());
                }
            }
        });

        muteCheckBox = new JCheckBoxMenuItem("Mute");
        muteCheckBox.setAccelerator(KeyStroke.getKeyStroke('['));
        muteCheckBox.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (audioMenuListener != null) {
                    audioMenuListener.mute(muteCheckBox.isSelected());
                }
            }
        });

        voiceChatMenuItem = new JMenuItem("Private Voice Chat");
        voiceChatMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (audioMenuListener != null) {
                    audioMenuListener.voiceChat();
                }
            }
        });
    }

    @Override
    public void setEnabled(boolean enabled) {
        audioMenu.setEnabled(enabled);
        softphoneMenuItem.setEnabled(enabled);
        muteCheckBox.setEnabled(enabled);
        voiceChatMenuItem.setEnabled(enabled);
    }

    public void addMenus() {
        JmeClientMain.getFrame().addToWindowMenu(softphoneMenuItem, 4);
        JmeClientMain.getFrame().addToToolsMenu(muteCheckBox, 0);
        JmeClientMain.getFrame().addToWindowMenu(voiceChatMenuItem, 3);
    }

    public void removeMenus() {
        JmeClientMain.getFrame().removeFromWindowMenu(softphoneMenuItem);
        JmeClientMain.getFrame().removeFromToolsMenu(muteCheckBox);
        JmeClientMain.getFrame().removeFromWindowMenu(voiceChatMenuItem);
    }

    public static AudioMenu getAudioMenu(AudioMenuListener audioMenuListener) {
        if (audioM == null) {
            audioM = new AudioMenu(audioMenuListener);
        }

        return audioM;
    }

    public static JMenuItem getAudioMenuItem(AudioMenuListener audioMenuListener) {
        return getAudioMenu(audioMenuListener).audioMenu;
    }

    public static void updateSoftphoneCheckBoxMenuItem(boolean selected) {
        audioM.softphoneMenuItem.setSelected(selected);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        audioMenu = new javax.swing.JMenu();
        audioQualityMenu = new javax.swing.JMenu();
        transferCallMenuItem = new javax.swing.JMenuItem();
        testAudioMenuItem = new javax.swing.JMenuItem();
        reconnectSoftphoneMenuItem = new javax.swing.JMenuItem();
        logAudioProblemMenuItem = new javax.swing.JMenuItem();

        audioMenu.setText("Audio");
        audioMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                audioMenuActionPerformed(evt);
            }
        });

        audioQualityMenu.setText("Audio Quality");
        audioQualityMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                audioQualityMenuActionPerformed(evt);
            }
        });
        audioMenu.add(audioQualityMenu);

        transferCallMenuItem.setText("Transfer Call...");
        transferCallMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transferCallMenuItemActionPerformed(evt);
            }
        });
        audioMenu.add(transferCallMenuItem);

        testAudioMenuItem.setText("Test Audio");
        testAudioMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testAudioMenuItemActionPerformed(evt);
            }
        });
        audioMenu.add(testAudioMenuItem);

        reconnectSoftphoneMenuItem.setText("Reconnect Softphone");
        reconnectSoftphoneMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reconnectSoftphoneMenuItemActionPerformed(evt);
            }
        });
        audioMenu.add(reconnectSoftphoneMenuItem);

        logAudioProblemMenuItem.setText("Log Audio Problem");
        logAudioProblemMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logAudioProblemMenuItemActionPerformed(evt);
            }
        });
        audioMenu.add(logAudioProblemMenuItem);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void testAudioMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testAudioMenuItemActionPerformed
        if (audioMenuListener != null) {
            audioMenuListener.testAudio();
        }
}//GEN-LAST:event_testAudioMenuItemActionPerformed

    private void reconnectSoftphoneMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reconnectSoftphoneMenuItemActionPerformed
        if (audioMenuListener != null) {
            audioMenuListener.reconnectSoftphone();
        }
}//GEN-LAST:event_reconnectSoftphoneMenuItemActionPerformed

    private void transferCallMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transferCallMenuItemActionPerformed
        if (audioMenuListener != null) {
            audioMenuListener.transferCall();
        }
}//GEN-LAST:event_transferCallMenuItemActionPerformed

    private void logAudioProblemMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logAudioProblemMenuItemActionPerformed
        if (audioMenuListener != null) {
            audioMenuListener.logAudioProblem();
        }
}//GEN-LAST:event_logAudioProblemMenuItemActionPerformed

    private void audioMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_audioMenuActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_audioMenuActionPerformed

private void audioQualityMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_audioQualityMenuActionPerformed
}//GEN-LAST:event_audioQualityMenuActionPerformed

    private void populateAudioQualityMenu() {
        ButtonGroup audioQualityButtons = new ButtonGroup();

        for (AudioQuality quality : AudioQuality.values()) {
            final AudioQuality fq = quality;
            JRadioButtonMenuItem mitem = new JRadioButtonMenuItem(new AbstractAction(quality.toString()) {

                public void actionPerformed(ActionEvent arg0) {
                    setAudioQuality(fq);
                }
            });

            audioQualityMenu.add(mitem);
            audioQualityButtons.add(mitem);

            SoftphoneControlImpl softphoneControlImpl = SoftphoneControlImpl.getInstance();

            if (quality.equals(softphoneControlImpl.getAudioQuality())) {
                mitem.setSelected(true);
            }
        }

        audioQualityMenu.setEnabled(true);
    }

    private void setAudioQuality(AudioQuality quality) {
        audioMenuListener.setAudioQuality(quality);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu audioMenu;
    private javax.swing.JMenu audioQualityMenu;
    private javax.swing.JMenuItem logAudioProblemMenuItem;
    private javax.swing.JMenuItem reconnectSoftphoneMenuItem;
    private javax.swing.JMenuItem testAudioMenuItem;
    private javax.swing.JMenuItem transferCallMenuItem;
    // End of variables declaration//GEN-END:variables
}
