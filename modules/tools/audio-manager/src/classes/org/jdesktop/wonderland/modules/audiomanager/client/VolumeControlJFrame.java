/*
 * VolumeControlJFrame.java
 *
 * Created on February 5, 2009, 10:33 AM
 */

package org.jdesktop.wonderland.modules.audiomanager.client;

import org.jdesktop.wonderland.common.cell.CellID;

/**
 *
 * @author  jp
 */
public class VolumeControlJFrame extends javax.swing.JFrame {

    private CellID cellID;
    private VolumeChangeListener listener;
    private String name;
    private String otherCallID;

    /** Creates new form VolumeControlJFrame */
    public VolumeControlJFrame() {
        initComponents();
    }

    public VolumeControlJFrame(CellID cellID, VolumeChangeListener listener, String name, String otherCallID) {
	this.cellID = cellID;
	this.listener = listener;
	this.otherCallID = otherCallID;

        initComponents();

	setTitle(name);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        volumeControlSlider = new javax.swing.JSlider();

        setTitle("Volume Control");

        volumeControlSlider.setMajorTickSpacing(1);
        volumeControlSlider.setMaximum(10);
        volumeControlSlider.setMinorTickSpacing(1);
        volumeControlSlider.setPaintLabels(true);
        volumeControlSlider.setPaintTicks(true);
        volumeControlSlider.setSnapToTicks(true);
        volumeControlSlider.setValue(5);
        volumeControlSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                volumeControlSliderStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(47, 47, 47)
                .add(volumeControlSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 263, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(64, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(volumeControlSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void volumeControlSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_volumeControlSliderStateChanged
    javax.swing.JSlider source = (javax.swing.JSlider) evt.getSource();

    double volume = source.getValue();

    if (volume > 5) {
	volume = 1 + ((volume - 5) * .6);
    } else {
	volume /= 5.;
    }

    listener.volumeChanged(cellID, otherCallID, volume);
}//GEN-LAST:event_volumeControlSliderStateChanged

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VolumeControlJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider volumeControlSlider;
    // End of variables declaration//GEN-END:variables

}
