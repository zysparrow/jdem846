package us.wthr.jdem846.render;

import java.awt.Color;

import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class ModelDimensions2D
{
	private static Log log = Logging.getLog(ModelDimensions2D.class);
	
	private int tileSize;
	private int dataRows;
	private int dataColumns;
	private float sizeRatio;
	private int outputWidth;
	private int outputHeight;
	private float xDim;
	private float yDim;
	private float numTilesHorizontal;
	private float numTilesVertical;
	private long tileOutputWidth;
	private long tileOutputHeight;
	private long tileCount;
	
	protected ModelDimensions2D()
	{
		
	}
	
	
	public static ModelDimensions2D getModelDimensions(DataPackage dataPackage, ModelOptions modelOptions)
	{
		ModelDimensions2D modelDimensions = new ModelDimensions2D();
		
		modelDimensions.tileSize = modelOptions.getTileSize();
		modelDimensions.dataRows = (int) dataPackage.getRows();
		modelDimensions.dataColumns = (int) dataPackage.getColumns();
		
		if (modelDimensions.tileSize > modelDimensions.dataRows && modelDimensions.dataRows > modelDimensions.dataColumns)
			modelDimensions.tileSize = modelDimensions.dataRows;
		
		if (modelDimensions.tileSize > modelDimensions.dataColumns && modelDimensions.dataColumns > modelDimensions.dataRows)
			modelDimensions.tileSize = modelDimensions.dataColumns;

	
		modelDimensions.sizeRatio = 1.0f;
		modelDimensions.outputWidth = modelOptions.getWidth();
		modelDimensions.outputHeight = modelOptions.getHeight();

		if (modelDimensions.dataRows > modelDimensions.dataColumns) {
			modelDimensions.sizeRatio = (float)modelDimensions.dataColumns / (float)modelDimensions.dataRows;
			modelDimensions.outputWidth = Math.round(((float) modelDimensions.outputHeight) * modelDimensions.sizeRatio);
		} else if (modelDimensions.dataColumns > modelDimensions.dataRows) {
			modelDimensions.sizeRatio = (float)modelDimensions.dataRows / (float)modelDimensions.dataColumns;
			modelDimensions.outputHeight = Math.round(((float)modelDimensions.outputWidth) * modelDimensions.sizeRatio);
		}
		
		log.info("Output width/height: " + modelDimensions.outputWidth + "/" + modelDimensions.outputHeight);
		
		float xdimRatio = (float)modelDimensions.outputWidth / (float)modelDimensions.dataColumns;
		modelDimensions.xDim = dataPackage.getAvgXDim() / xdimRatio;
		//dataPackage.setAvgXDim(xDim);

		float ydimRatio = (float)modelDimensions.outputHeight / (float)modelDimensions.dataRows;
		modelDimensions.yDim = dataPackage.getAvgYDim() / ydimRatio;
		//dataPackage.setAvgYDim(yDim);
		//log.info("X/Y Dimension (cellsize): " + xDim + "/" + yDim);
		

		modelDimensions.numTilesHorizontal = ((float)modelDimensions.dataColumns) / ((float)modelDimensions.tileSize);
		modelDimensions.numTilesVertical = ((float)modelDimensions.dataRows) / ((float)modelDimensions.tileSize);
		
		modelDimensions.tileOutputWidth = Math.round(((float)modelDimensions.outputWidth) / modelDimensions.numTilesHorizontal);
		modelDimensions.tileOutputHeight = Math.round(((float)modelDimensions.outputHeight) / modelDimensions.numTilesVertical);
		
		modelDimensions.tileCount = (int) (Math.ceil(((double)modelDimensions.dataRows / (double)modelDimensions.tileSize)) * Math.ceil(((double)modelDimensions.dataColumns / (double)modelDimensions.tileSize)));
		
		return modelDimensions;
	}


	public int getTileSize()
	{
		return tileSize;
	}


	public int getDataRows()
	{
		return dataRows;
	}


	public int getDataColumns()
	{
		return dataColumns;
	}


	public float getSizeRatio()
	{
		return sizeRatio;
	}


	public int getOutputWidth()
	{
		return outputWidth;
	}


	public int getOutputHeight()
	{
		return outputHeight;
	}


	public float getxDim()
	{
		return xDim;
	}


	public float getyDim()
	{
		return yDim;
	}


	public float getNumTilesHorizontal()
	{
		return numTilesHorizontal;
	}


	public float getNumTilesVertical()
	{
		return numTilesVertical;
	}


	public long getTileOutputWidth()
	{
		return tileOutputWidth;
	}


	public long getTileOutputHeight()
	{
		return tileOutputHeight;
	}


	public long getTileCount()
	{
		return tileCount;
	}
	
	
	
}