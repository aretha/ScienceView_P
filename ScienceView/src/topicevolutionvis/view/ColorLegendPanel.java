/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ColorLegendPanel.java
 *
 * Created on Jan 18, 2011, 1:59:53 PM
 */
package topicevolutionvis.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalSliderUI;
import topicevolutionvis.view.color.ColorTable;

/**
 *
 * @author barbosaa
 */
public class ColorLegendPanel extends javax.swing.JPanel {

    private LegendPanel legendPanel;
    TemporalProjectionViewer parent;

    /**
     * Creates new form ColorLegendPanel
     */
    public ColorLegendPanel(TemporalProjectionViewer parent) {
        initComponents();
        this.parent = parent;
        legendPanel = new LegendPanel();
        legendPanel.setMinimumSize(new Dimension(100, 11));
        legendPanel.setPreferredSize(new Dimension(100, 11));
        //  legendPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    }

    public void addYearLegend() {
        this.add(legendPanel, BorderLayout.NORTH);
        this.validate();
    }

    public void removeYearLegend() {
        this.remove(legendPanel);
        this.validate();
    }

    public void setValue(int n) {
        this.yearsSlider.setValue(n);
    }

    public int getValue() {
        return this.yearsSlider.getValue();
    }

    public int getMaximum() {
        return this.yearsSlider.getMaximum();
    }

    public void setMinimum(int minimum) {
        this.yearsSlider.setMinimum(minimum);
    }

    public void setMaximum(int maximum) {
        this.yearsSlider.setMaximum(maximum);
    }

    public void setMajorTickSpacing(int n) {
        this.yearsSlider.setMajorTickSpacing(n);
    }

    public void setLabelTable(Dictionary labels) {
        this.yearsSlider.setLabelTable(labels);
    }

    public void setPaintLabels(boolean b) {
        this.yearsSlider.setPaintLabels(b);
    }

    public void addChangeListener(ChangeListener l) {
        yearsSlider.addChangeListener(l);
    }

    private class LegendPanel extends JPanel {

        private double incr;
        private MyBasicSliderUI slider_ui = (MyBasicSliderUI) yearsSlider.getUI();
        List<Integer> years;
        private ColorTable colorTable = parent.getColorTable();

        public LegendPanel() {


            MouseListener listener = new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
                        int index = parent.getYearIndexForValue(slider_ui.getValueForXPosition(e.getX()));
                        int year = parent.getTemporalProjection().getYearWithIndex(index);
                        Color newColor = JColorChooser.showDialog(null, "Choose a new color for year " + year, colorTable.getColor(index * incr));
                        if (newColor != null) {
                            colorTable.setColorAt(index * incr, newColor);
                            repaint();
                            parent.updateImage();
                        }
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setCursor(Cursor.getDefaultCursor());
                }
            };

            addMouseListener(listener);

            this.setToolTipText("Change the color for this year");
        }

        @Override
        public void paintComponent(java.awt.Graphics g) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
            double xPos, xPos_next;

            years = Collections.list(yearsSlider.getLabelTable().keys());
            Collections.sort(years);
            incr = 1.0 / (years.size() - 1.0);
            for (int i = 0; i < years.size(); i++) {
                g2.setColor(colorTable.getColor(i * incr));
                xPos = slider_ui.getXPositionForValue(years.get(i));
                if (i != years.size() - 1) {
                    xPos_next = slider_ui.getXPositionForValue(years.get(i + 1));
                } else {
                    xPos_next = slider_ui.getXPositionForValue(yearsSlider.getMaximum());
                }
                g2.fill(new Rectangle2D.Double(xPos, this.getY(), xPos_next - xPos, 10));
            }
        }
    }

    private class MyBasicSliderUI extends MetalSliderUI {

        public int getXPositionForValue(int value) {
            return this.xPositionForValue(value);
        }

        public int getValueForXPosition(int x) {
            return this.valueForXPosition(x);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        yearsSlider = new javax.swing.JSlider();
        yearsSlider.setUI(new MyBasicSliderUI());

        setLayout(new java.awt.BorderLayout());

        yearsSlider.setMajorTickSpacing(1);
        yearsSlider.setMaximum(0);
        yearsSlider.setPaintLabels(true);
        yearsSlider.setPaintTicks(true);
        yearsSlider.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                yearsSliderComponentResized(evt);
            }
        });
        add(yearsSlider, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void yearsSliderComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_yearsSliderComponentResized
        legendPanel.repaint();
    }//GEN-LAST:event_yearsSliderComponentResized
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JSlider yearsSlider;
    // End of variables declaration//GEN-END:variables
}
