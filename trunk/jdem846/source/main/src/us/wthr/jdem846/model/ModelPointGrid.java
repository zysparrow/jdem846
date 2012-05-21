package us.wthr.jdem846.model;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;

public abstract class ModelPointGrid
{
	private static Log log = Logging.getLog(ModelPointGrid.class);
	
	protected double north;
	protected double south;
	protected double east;
	protected double west;
	protected double latitudeResolution;
	protected double longitudeResolution;
	
	protected int width;
	protected int height;
	
	protected int gridLength;

	
	public ModelPointGrid(double north, double south, double east, double west, double latitudeResolution, double longitudeResolution)
	{
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		this.latitudeResolution = latitudeResolution;
		this.longitudeResolution = longitudeResolution;
		
		double _height = (this.north - this.south) / latitudeResolution;
		double _width = (this.east - this.west) / longitudeResolution;
		
		this.height = (int) MathExt.ceil(_height);
		this.width = (int) MathExt.ceil(_width);
		
		gridLength = height * width;

		
	}
	
	public abstract void dispose();
	public abstract boolean isDisposed();
	public abstract void reset();
	
	public abstract ModelPoint get(double latitude, double longitude);
	
	
	protected int getIndex(double latitude, double longitude)
	{
		int column = (int) Math.floor((longitude - west) / longitudeResolution);
		int row = (int) Math.floor((north - latitude) / latitudeResolution);
		
		if (column < 0 || column >= width) {
			return -1;
		}
		
		if (row < 0 || row >= height) {
			return -1;
		}
		
		
		int index = row * width + column;
		return index;
	}
	
}