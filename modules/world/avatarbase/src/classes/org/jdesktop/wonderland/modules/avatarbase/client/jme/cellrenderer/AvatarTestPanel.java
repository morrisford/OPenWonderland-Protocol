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
package org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer;

import imi.character.CharacterEyes;
import imi.character.ninja.NinjaContext.TriggerNames;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarRendererChangeRequestEvent.AvatarQuality;

/**
 * A test to demonstrate triggering motion from other interfaces
 *
 * @author paulby
 */
public class AvatarTestPanel extends javax.swing.JPanel {

    private AvatarCharacter avatarCharacter;


    /** Creates new form AvatarTestPanel */
    public AvatarTestPanel() {
        initComponents();

        expressionCB.removeAllItems();
        expressionCB.addItem(TriggerNames.Smile);
        expressionCB.addItem(TriggerNames.Frown);
        expressionCB.addItem(TriggerNames.Scorn);

        setAvatarCharactar(null);
    }

    public void setAvatarCharactar(AvatarCharacter avatar) {
        avatarCharacter = avatar;

        boolean enabled = (avatar!=null);

        if (enabled) {
            actionCB.removeAllItems();
            for(String anim : avatar.getAnimations()) {
                actionCB.addItem(anim);
            }
        } else {
            actionCB.removeAllItems();
        }

        forwardB.setEnabled(enabled);
        rightB.setEnabled(enabled);
        backwardB.setEnabled(enabled);
        leftB.setEnabled(enabled);
        actionCB.setEnabled(enabled);
//        expressionCB.setEnabled(enabled);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSlider1 = new javax.swing.JSlider();
        jPanel1 = new javax.swing.JPanel();
        forwardB = new javax.swing.JButton();
        backwardB = new javax.swing.JButton();
        leftB = new javax.swing.JButton();
        rightB = new javax.swing.JButton();
        actionCB = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        expressionCB = new javax.swing.JComboBox();
        runActionB = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        renderQuality = new javax.swing.JComboBox();
        winkB = new javax.swing.JButton();
        eyeSelectionCB = new javax.swing.JComboBox();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Movement"));

        forwardB.setText("Forward");
        forwardB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                directionBMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                directionBMouseReleased(evt);
            }
        });

        backwardB.setText("Backward");
        backwardB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                directionBMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                directionBMouseReleased(evt);
            }
        });

        leftB.setText("Left");
        leftB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                directionBMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                directionBMouseReleased(evt);
            }
        });

        rightB.setText("Right");
        rightB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                directionBMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                directionBMouseReleased(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 303, Short.MAX_VALUE)
            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel1Layout.createSequentialGroup()
                    .add(31, 31, 31)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel1Layout.createSequentialGroup()
                            .add(74, 74, 74)
                            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(backwardB)
                                .add(forwardB))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 64, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(jPanel1Layout.createSequentialGroup()
                            .add(leftB)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 89, Short.MAX_VALUE)
                            .add(rightB)))
                    .add(31, 31, 31)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 123, Short.MAX_VALUE)
            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel1Layout.createSequentialGroup()
                    .add(10, 10, 10)
                    .add(forwardB)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(rightB)
                        .add(leftB))
                    .add(5, 5, 5)
                    .add(backwardB)
                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jLabel1.setText("Action");

        jLabel2.setText("Expression");

        expressionCB.setEnabled(false);
        expressionCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expressionCBActionPerformed(evt);
            }
        });

        runActionB.setText("Run");
        runActionB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runActionBActionPerformed(evt);
            }
        });

        jLabel3.setText("Render Quality");

        renderQuality.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "High", "Medium", "Low" }));
        renderQuality.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renderQualityActionPerformed(evt);
            }
        });

        winkB.setText("Wink");
        winkB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                winkBActionPerformed(evt);
            }
        });

        eyeSelectionCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Left Eye", "Right Eye" }));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(28, 28, 28)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel2)
                            .add(jLabel1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(actionCB, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 128, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(runActionB))
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(eyeSelectionCB, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(expressionCB, 0, 128, Short.MAX_VALUE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(winkB))))
                    .add(layout.createSequentialGroup()
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(renderQuality, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 128, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(90, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(8, 8, 8)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(runActionB)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel1)
                        .add(actionCB, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(expressionCB, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(eyeSelectionCB, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(winkB))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 18, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(renderQuality, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void directionBMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_directionBMousePressed
        if (evt.getSource()==forwardB) {
            avatarCharacter.triggerActionStart(TriggerNames.Move_Forward);
        } else if (evt.getSource()==rightB) {
            avatarCharacter.triggerActionStart(TriggerNames.Move_Right);
        } else if (evt.getSource()==backwardB) {
            avatarCharacter.triggerActionStart(TriggerNames.Move_Back);
        } else if (evt.getSource()==leftB) {
            avatarCharacter.triggerActionStart(TriggerNames.Move_Left);
        }

}//GEN-LAST:event_directionBMousePressed

    private void directionBMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_directionBMouseReleased
        if (evt.getSource()==forwardB) {
            avatarCharacter.triggerActionStop(TriggerNames.Move_Forward);
        } else if (evt.getSource()==rightB) {
            avatarCharacter.triggerActionStop(TriggerNames.Move_Right);
        } else if (evt.getSource()==backwardB) {
            avatarCharacter.triggerActionStop(TriggerNames.Move_Back);
        } else if (evt.getSource()==leftB) {
            avatarCharacter.triggerActionStop(TriggerNames.Move_Left);
        }

}//GEN-LAST:event_directionBMouseReleased

    private void expressionCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expressionCBActionPerformed
//        if (avatarCharacter!=null) {
//            avatarCharacter.triggerActionStart((TriggerNames) expressionCB.getSelectedItem());
//        }
    }//GEN-LAST:event_expressionCBActionPerformed

    private void runActionBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runActionBActionPerformed
        if (avatarCharacter!=null) {
            avatarCharacter.setAnimation((String) actionCB.getSelectedItem());

            // TODO trigger a single cycle of this animation
            avatarCharacter.triggerActionStart(TriggerNames.Punch);
        }
}//GEN-LAST:event_runActionBActionPerformed

    private void renderQualityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renderQualityActionPerformed
        switch(renderQuality.getSelectedIndex()) {
            case 0 :
                ClientContext.getInputManager().postEvent(new AvatarRendererChangeRequestEvent(AvatarQuality.High));
                break;
            case 1:
                ClientContext.getInputManager().postEvent(new AvatarRendererChangeRequestEvent(AvatarQuality.Medium));
                break;
            case 2 :
                ClientContext.getInputManager().postEvent(new AvatarRendererChangeRequestEvent(AvatarQuality.Low));
                break;
            default :
                Logger.getAnonymousLogger().severe("Unknown render quality");
                return;
        }
}//GEN-LAST:event_renderQualityActionPerformed

    private void winkBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_winkBActionPerformed
        // TODO add your handling code here:
        CharacterEyes eyes = avatarCharacter.getEyes();

        eyes.wink((eyeSelectionCB.getSelectedIndex()==1));

}//GEN-LAST:event_winkBActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox actionCB;
    private javax.swing.JButton backwardB;
    private javax.swing.JComboBox expressionCB;
    private javax.swing.JComboBox eyeSelectionCB;
    private javax.swing.JButton forwardB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JButton leftB;
    private javax.swing.JComboBox renderQuality;
    private javax.swing.JButton rightB;
    private javax.swing.JButton runActionB;
    private javax.swing.JButton winkB;
    // End of variables declaration//GEN-END:variables

}
