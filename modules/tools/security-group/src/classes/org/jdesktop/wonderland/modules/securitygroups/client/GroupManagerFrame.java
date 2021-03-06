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
package org.jdesktop.wonderland.modules.securitygroups.client;

import java.awt.Component;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.common.login.CredentialManager;
import org.jdesktop.wonderland.modules.securitygroups.common.GroupDTO;
import org.jdesktop.wonderland.modules.securitygroups.common.GroupUtils;

/**
 *
 * @author jkaplan
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class GroupManagerFrame extends JFrame implements ListSelectionListener {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/securitygroups/client/Bundle");
    private String baseUrl;
    private CredentialManager cm;
    private DefaultTableModel tableModel;

    /** Creates new form GroupManagerFrame */
    public GroupManagerFrame(String serverUrl, CredentialManager cm) {
        this.baseUrl = serverUrl;
        this.cm = cm;

        initComponents();
        tableModel = new DefaultTableModel(
            new Object [][] {},
            new String [] {
                BUNDLE.getString("Group_Name"),
                BUNDLE.getString("Members")
            }
        ) {
            Class[] types = new Class[]{
                String.class,
                Integer.class
            };
            boolean[] canEdit = new boolean[]{
                false,
                false
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };
        groupTable.setModel(tableModel);

        groupTable.getSelectionModel().addListSelectionListener(this);
        groupTable.getColumnModel().getColumn(0).setCellRenderer(
                new GroupNameRenderer());

        loadGroups();
    }

    protected void loadGroups() {
        Set<GroupDTO> groups;

        String filter = filterTF.getText();
        try {
            if (filter.trim().length() == 0) {
                groups = GroupUtils.getGroupsForUser(baseUrl, cm.getUsername(),
                        false, cm);
            } else {
                groups = GroupUtils.getGroups(baseUrl, filter, false, cm);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        } catch (JAXBException je) {
            je.printStackTrace();
            return;
        }

        // clear existing rows
        tableModel.setRowCount(0);

        // now add in the groups
        for (GroupDTO g : groups) {
            tableModel.addRow(new Object[]{g, g.getMemberCount()});
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        GroupDTO selected = null;

        if (!e.getValueIsAdjusting()) {
            selected = getSelectedGroup();
        }

        if (selected != null) {
            System.out.println("Selected: " + selected.getId() + " editable: " +
                    selected.isEditable());
        }

        boolean editable = (selected != null && selected.isEditable());
        editButton.setEnabled(editable);
        removeButton.setEnabled(editable);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        groupTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        filterTF = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        filterButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/securitygroups/client/Bundle"); // NOI18N
        setTitle(bundle.getString("GroupManagerFrame.title")); // NOI18N

        groupTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Group Name", "Members"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        groupTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(groupTable);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 13));
        jLabel1.setText(bundle.getString("GroupManagerFrame.jLabel1.text")); // NOI18N

        filterTF.setFont(new java.awt.Font("Dialog", 0, 13));

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 13));
        jLabel2.setText(bundle.getString("GroupManagerFrame.jLabel2.text")); // NOI18N

        filterButton.setFont(new java.awt.Font("Dialog", 0, 13));
        filterButton.setText(bundle.getString("GroupManagerFrame.filterButton.text")); // NOI18N
        filterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterButtonActionPerformed(evt);
            }
        });

        addButton.setFont(new java.awt.Font("Dialog", 0, 13));
        addButton.setText(bundle.getString("GroupManagerFrame.addButton.text")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        editButton.setFont(new java.awt.Font("Dialog", 0, 13));
        editButton.setText(bundle.getString("GroupManagerFrame.editButton.text")); // NOI18N
        editButton.setEnabled(false);
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        removeButton.setFont(new java.awt.Font("Dialog", 0, 13));
        removeButton.setText(bundle.getString("GroupManagerFrame.removeButton.text")); // NOI18N
        removeButton.setEnabled(false);
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        closeButton.setFont(new java.awt.Font("Dialog", 0, 13));
        closeButton.setText(bundle.getString("GroupManagerFrame.closeButton.text")); // NOI18N
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 262, Short.MAX_VALUE)
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(filterTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 116, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(filterButton)
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(addButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(editButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(removeButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                        .add(261, 261, 261))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(closeButton)
                        .addContainerGap())))
        );

        layout.linkSize(new java.awt.Component[] {closeButton, editButton, filterButton, removeButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(filterButton)
                    .add(filterTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(addButton)
                    .add(editButton)
                    .add(removeButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(closeButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        showGroupEditor(null);
}//GEN-LAST:event_addButtonActionPerformed

    private void filterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterButtonActionPerformed
        loadGroups();
    }//GEN-LAST:event_filterButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        showGroupEditor(getSelectedGroup());
    }//GEN-LAST:event_editButtonActionPerformed

    private void showGroupEditor(GroupDTO group) {
        GroupEditorFrame gef = new GroupEditorFrame(this, baseUrl, group, cm);
        gef.setVisible(true);
    }

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        GroupDTO selected = getSelectedGroup();
        if (selected != null) {
            try {
                GroupUtils.removeGroup(baseUrl, selected.getId(), cm);
                loadGroups();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_closeButtonActionPerformed

    private GroupDTO getSelectedGroup() {
        int row = groupTable.getSelectedRow();
        if (row >= 0) {
            return (GroupDTO) tableModel.getValueAt(row, 0);
        } else {
            return null;
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton editButton;
    private javax.swing.JButton filterButton;
    private javax.swing.JTextField filterTF;
    private javax.swing.JTable groupTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton removeButton;
    // End of variables declaration//GEN-END:variables

    class GroupNameRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            GroupDTO group = (GroupDTO) value;
            String name = group.getId();

            return super.getTableCellRendererComponent(table, name, isSelected,
                    hasFocus, row, column);
        }
    }
}
