package us.wthr.jdem846.ui.histogram;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.ui.base.Panel;

import org.jfree.chart.*;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.statistics.*;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.AreaRenderer;

@SuppressWarnings("serial")
public class TonalHistogram extends Panel
{
	private int channels;
	private TonalHistogramModel histogramModel;
	private BufferedImage histogram;
	
	public TonalHistogram()
	{
		this(null, Channels.CHANNEL_1 | Channels.CHANNEL_2 | Channels.CHANNEL_3);
	}
	
	public TonalHistogram(TonalHistogramModel histogramModel, int channels)
	{
		this.channels = channels;
		this.setHistogramModel(histogramModel);
		this.setOpaque(false);
		this.setLayout(new BorderLayout());
		
		this.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e)
			{
				//createHistogram();
				repaint();
			}
			
		});
	}
	
	
	public void setHistogramModel(TonalHistogramModel histogramModel)
	{
		this.histogramModel = histogramModel;
		createHistogram();
	}
	
	public void createHistogram()
	{
		
		if (histogramModel == null) {
			return;
		}
		
	
		
		
		double[][] data = new double[3][255];
		for (int i = 0; i < 255; i++) {
			int c0 = histogramModel.getChannel0().distribution[i];
			int c1 = histogramModel.getChannel1().distribution[i];
			int c2 = histogramModel.getChannel2().distribution[i];
			
			data[0][i] = c0;
			data[1][i] = c1;
			data[2][i] = c2;
			
		}
		
		CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
	            "", "", data
	    );
		
		
		String plotTitle = null;
		String xaxis = null;//"Elevation (m)";
		String yaxis = null;//"Freq.";
		
		PlotOrientation orientation = PlotOrientation.VERTICAL; 
		boolean legend = false; 
		boolean toolTips = true;
		boolean urls = false; 
		JFreeChart chart = ChartFactory.createAreaChart(
				plotTitle,             	// chart title
				xaxis,               	// domain axis label
				yaxis,                  // range axis label
	            dataset,                // data
	            orientation, 			// orientation
	            legend,                 // include legend
	            toolTips,               // tooltips
	            urls                    // urls
	        );
		
		CategoryPlot plot = chart.getCategoryPlot();
		plot.getDomainAxis().setTickMarksVisible(false);
		plot.getDomainAxis().setMinorTickMarksVisible(false);
		plot.getDomainAxis().setTickLabelsVisible(false);
		plot.getDomainAxis().setMinorTickMarksVisible(false);
		plot.getRangeAxis().setMinorTickMarksVisible(false);
		plot.getRangeAxis().setTickLabelsVisible(false);
		
		HistogramRenderer renderer = new HistogramRenderer();
		plot.setRenderer(renderer);

		ChartPanel chartPanel = new ChartPanel(chart);
		add(chartPanel, BorderLayout.CENTER);

	}
	

}
