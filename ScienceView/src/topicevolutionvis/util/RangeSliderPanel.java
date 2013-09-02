/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RangeSliderPanel.java
 *
 * Created on Sep 29, 2010, 11:19:42 AM
 */
package topicevolutionvis.util;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author barbosaa
 */
public class RangeSliderPanel extends javax.swing.JPanel implements ChangeListener {

    private int minimum, maximum, lowValue, highValue;
    JTextField rangeTextField;
    JLabel minLabel, maxLabel, rangeLabel;
    JRangeSlider slider;
    boolean similarity = false;

    /** Creates new form RangeSliderPanel */
    public RangeSliderPanel(int minimum, int maximum, int lowValue, int highValue) {
        initComponents();
        this.minimum = minimum;
        this.maximum = maximum;
        this.lowValue = lowValue;
        this.highValue = highValue;
        addComponents();
    }

    public void setIsSimilarity(boolean similarity) {
        this.similarity = similarity;
    }

    @Override
    public void setEnabled(boolean enabled) {
        rangeTextField.setEnabled(enabled);
        minLabel.setEnabled(enabled);
        maxLabel.setEnabled(enabled);
        rangeLabel.setEnabled(enabled);
        slider.setEnabled(enabled);
//        if(!enabled){
//            this.minLabel.setText(" ");
//            this.maxLabel.setText(" ");
//            this.rangeTextField.setText(" ");
//        }
    }

    public void setParameters(int minimum, int maximum, int lowValue, int highValue) {
        slider.setMinimum(minimum);
        slider.setMaximum(maximum);
        slider.setLowValue(lowValue);
        slider.setHighValue(highValue);
        if (this.similarity) {
            minLabel.setText("0");
            maxLabel.setText("1");
        } else {
            minLabel.setText(Integer.toString(minimum));
            maxLabel.setText(Integer.toString(maximum));
        }

    }

    public void addChangeListener(ChangeListener cl) {
        this.slider.addChangeListener(cl);
    }

    private void addComponents() {
        Dimension labelSize = new Dimension(30, 14);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(3, 3, 3, 3);
        minLabel = new JLabel();
        minLabel.setSize(labelSize);
        minLabel.setMinimumSize(labelSize);
        minLabel.setMaximumSize(labelSize);
        this.add(minLabel, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.insets = new Insets(3, 3, 3, 3);
        c.fill = GridBagConstraints.HORIZONTAL;
        slider = new JRangeSlider(minimum, maximum, lowValue, highValue, JRangeSlider.HORIZONTAL);
        this.add(slider, c);
        slider.addChangeListener(this);

        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 1;
        c.insets = new Insets(3, 3, 3, 3);
        maxLabel = new JLabel();
        maxLabel.setSize(labelSize);
        maxLabel.setMinimumSize(labelSize);
        maxLabel.setMaximumSize(labelSize);
        this.add(maxLabel, c);

        JPanel describeRangePanel = new JPanel();
        describeRangePanel.setLayout(new GridBagLayout());
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(3, 3, 3, 3);
        c.anchor = GridBagConstraints.WEST;
        rangeLabel = new JLabel("Range:");
        describeRangePanel.add(rangeLabel, c);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(3, 3, 3, 3);
        c.anchor = GridBagConstraints.WEST;
        rangeTextField = new JTextField("", 8);
        rangeTextField.setEditable(false);
        rangeTextField.setCaretPosition(0);
        describeRangePanel.add(rangeTextField, c);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.anchor = GridBagConstraints.WEST;
        this.add(describeRangePanel, c);

    }

    private void setRangeValues(float lowValue, float highValue) {
        if (!this.similarity) {
            this.rangeTextField.setText("[" + (int) lowValue + "," + (int) highValue + "]");
        } else {
            this.rangeTextField.setText("[" + lowValue + "," + highValue + "]");
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        setRangeValues(getLowValue(), getHighValue());
    }

    public float getLowValue() {
        if (!this.similarity) {
            return this.slider.getLowValue();
        }
        return ((float) this.slider.getLowValue()) / 10.0f;
    }

    public float getHighValue() {
        if (!this.similarity) {
            return this.slider.getHighValue();
        }
        return ((float) this.slider.getHighValue()) / 10.0f;
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

        setLayout(new java.awt.GridBagLayout());
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
