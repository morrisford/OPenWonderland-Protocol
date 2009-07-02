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
package org.jdesktop.wonderland.client.jme;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.client.login.ServerSessionManager;

/**
 *
 * @author jkaplan
 */
public class ReconnectFrame extends javax.swing.JDialog implements Runnable {
    private static final Logger logger =
            Logger.getLogger(ReconnectFrame.class.getName());

    private JmeClientMain main;
    private ServerSessionManager mgr;

    private Thread reconnectThread;

    /** Creates new form ReconnectFrame */
    public ReconnectFrame(JmeClientMain main, ServerSessionManager mgr) {
        super (main.getFrame().getFrame(), false);
        
        this.main = main;
        this.mgr = mgr;
        
        initComponents();

        reconnectThread = new Thread(this, "Reconnect to server");
        reconnectThread.start();
    }

    public void run() {
        try {
            do {
                System.out.println("[ReconnectFrame] sleeping");
                Thread.sleep(5000);
            } while (mgr.getDetails().getDarkstarServers() == null ||
                     mgr.getDetails().getDarkstarServers().length == 0);

            System.out.println("[ReconnectFrame] loading server " +
                        Arrays.toString(mgr.getDetails().getDarkstarServers()));

            main.loadServer(mgr.getServerURL());
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Error reconnecting to server " +
                       mgr.getServerURL(), ex);
            String msg = "Unable to reconnect to server: \n" +
                         ex.getMessage();

            JOptionPane.showMessageDialog(this, msg, "Error connecting to server",
                                          JOptionPane.ERROR_MESSAGE);
        } catch (InterruptedException ie) {
            // all done
        } finally {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ReconnectFrame.this.setVisible(false);
                    ReconnectFrame.this.dispose();
                }
            });
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Reconnect to server");

        jLabel1.setText("Server disconnected.  Attempting to reconnect...");

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(224, Short.MAX_VALUE)
                .add(cancelButton)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jLabel1)
                .add(12, 12, 12)
                .add(cancelButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        reconnectThread.interrupt();
    }//GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables

}
