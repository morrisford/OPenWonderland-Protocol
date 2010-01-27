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
package org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.ResourceBundle;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.client.cell.view.ViewCell;
import org.jdesktop.wonderland.client.jme.JmeClientMain;

/**
 *
 * @author paulby
 */
public class BoundsDebugger extends javax.swing.JFrame {

    private final static ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/avatarbase/client/jme/cellrenderer/resources/Bundle");
    private final HashMap<String, AvatarImiJME> avatarMap = new HashMap();

    /** Creates new form BoundsDebugger */
    public BoundsDebugger() {
        initComponents();
        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        JLabel jLabel1 = new JLabel(BUNDLE.getString("Username"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 7);
        avatarPanel.add(jLabel1, gridBagConstraints);

        JLabel jLabel2 = new JLabel(BUNDLE.getString("Print_Avatar_Bounds"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        avatarPanel.add(jLabel2, gridBagConstraints);

        JMenuItem editorMI = new JMenuItem(
                BUNDLE.getString("Avatar_Bounds_Debugger..."));
        editorMI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                BoundsDebugger.this.setVisible(true);
            }
        });
        JmeClientMain.getFrame().addToToolsMenu(editorMI, 2);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        avatarPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/avatarbase/client/jme/cellrenderer/resources/Bundle"); // NOI18N
        setTitle(bundle.getString("BoundsDebugger.title")); // NOI18N

        avatarPanel.setLayout(new java.awt.GridBagLayout());

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        avatarPanel.add(jPanel1, gridBagConstraints);

        jScrollPane1.setViewportView(avatarPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 274, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(126, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    void remove(AvatarImiJME avatar) {
        if (!(avatar.getCell() instanceof ViewCell))
            return;

        synchronized(avatarMap) {
            avatarMap.remove(((ViewCell)avatar.getCell()).getIdentity().getUsername());
        }
    }

    void add(AvatarImiJME avatar) {
        if (!(avatar.getCell() instanceof ViewCell))
            return;

        synchronized(avatarMap) {
            final String username = ((ViewCell)avatar.getCell()).getIdentity().getUsername();
            avatarMap.put(username, avatar);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
                    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 7);
                    avatarPanel.add(new JLabel(username), gridBagConstraints);
                    final JCheckBox cb = new JCheckBox();
                    gridBagConstraints = new java.awt.GridBagConstraints();
                    gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
                    avatarPanel.add(cb, gridBagConstraints);
                    cb.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            AvatarImiJME a = null;
                            synchronized(avatarMap) {
                                a = avatarMap.get(username);
                            }
                            if (a==null) {
                                System.err.println("Unknown username "+username);
                            }
                            a.getAvatarCharacter().getJScene().setPrintCullInfo(cb.isSelected());
                        }
                    });
                    avatarPanel.validate();
                }
            });
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel avatarPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

}
