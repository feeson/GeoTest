package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

class Window extends ApplicationFrame {
    static XYSeries series;
    public void drawPoint(double x,double y){
        series.add(x,y);
    }
    public Window(String title) {
        super(title);
        series= new XYSeries("Points");
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createScatterPlot(title, "X", "Y",
                                                          dataset, PlotOrientation.VERTICAL, true, false, false);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 500));
        setContentPane(chartPanel);
        this.pack();
        this.setVisible(true);
    }
}
