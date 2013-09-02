/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.projection.stress;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import topicevolutionvis.projection.ProjectionData;

/**
 *
 * @author USER
 */
public class StressJDialog extends javax.swing.JDialog {

    /**
     * Creates new form StressJDialog
     */
    public StressJDialog(java.awt.Frame parent) {
        super(parent);
        initComponents();
    }

    public void createStressChart(ProjectionData pdata) {
        XYSeriesCollection dataset = pdata.getStressSeries();

        //Static Stress
        JFreeChart chartStatic = ChartFactory.createXYLineChart(
                "Static Stress", // chart title
                "Year", // x axis label
                "Stress value", // y axis label
                new XYSeriesCollection(dataset.getSeries(0)), // data
                PlotOrientation.VERTICAL,
                false, // include legend
                true, // tooltips
                false // urls
                );

//        System.out.println("Stress Estatico");
//        for (int i = 0; i < dataset.getSeries(0).getItemCount(); i++) {
//            XYDataItem item = dataset.getSeries(0).getDataItem(i);
//            System.out.println(item.getYValue());
//        }

        ChartPanel chartStaticPanel = new ChartPanel(chartStatic);
        this.staticPanel.add(chartStaticPanel);
        XYPlot plot = chartStatic.getXYPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinePaint(Color.darkGray);
        TextTitle source = new TextTitle("Total value: " + pdata.getStaticStress());
        source.setFont(new Font("SansSerif", Font.PLAIN, 16));
        source.setPosition(RectangleEdge.BOTTOM);
        source.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        chartStatic.addSubtitle(source);
        plot.setDomainGridlinePaint(Color.darkGray);
        NumberAxis rangeAxis = (NumberAxis) plot.getDomainAxis();
        rangeAxis.setTickLabelFont(new Font("Arial", Font.BOLD, 12));
        rangeAxis.setTickUnit(new NumberTickUnit(rangeAxis.getTickUnit().getSize(), new DecimalFormat("###0")));
        rangeAxis.setVerticalTickLabels(true);

        //Static Stress
        JFreeChart chartDynamic = ChartFactory.createXYLineChart(
                "Dynamic Stress", // chart title
                "Year", // x axis label
                "Stress value", // y axis label
                new XYSeriesCollection(dataset.getSeries(1)), // data
                PlotOrientation.VERTICAL,
                false, // include legend
                true, // tooltips
                false // urls
                );
//        System.out.println("Stress Dinamico");
//        for (int i = 0; i < dataset.getSeries(1).getItemCount(); i++) {
//            XYDataItem item = dataset.getSeries(1).getDataItem(i);
//            System.out.println(item.getYValue());
//        }
        ChartPanel chartDynamicPanel = new ChartPanel(chartDynamic);
        this.dynamicPanel.add(chartDynamicPanel);
        this.staticPanel.add(chartStaticPanel);
        source = new TextTitle("Total value: " + pdata.getDynamicStress());
        source.setFont(new Font("SansSerif", Font.PLAIN, 16));
        source.setPosition(RectangleEdge.BOTTOM);
        source.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        chartDynamic.addSubtitle(source);
        plot = chartDynamic.getXYPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinePaint(Color.darkGray);
        plot.setDomainGridlinePaint(Color.darkGray);
        rangeAxis = (NumberAxis) plot.getDomainAxis();
        rangeAxis.setTickLabelFont(new Font("Arial", Font.BOLD, 12));
        rangeAxis.setTickUnit(new NumberTickUnit(rangeAxis.getTickUnit().getSize(), new DecimalFormat("###0")));
        rangeAxis.setVerticalTickLabels(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        staticPanel = new javax.swing.JPanel();
        dynamicPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Stress");
        setMinimumSize(new java.awt.Dimension(600, 450));
        setPreferredSize(new java.awt.Dimension(600, 450));

        staticPanel.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab("Static Stress", staticPanel);

        dynamicPanel.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab("Dynamic Stress", dynamicPanel);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel dynamicPanel;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel staticPanel;
    // End of variables declaration//GEN-END:variables
}
