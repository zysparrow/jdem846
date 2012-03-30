package us.wthr.jdem846.render;

import us.wthr.jdem846.math.MathExt;

public class ZBuffer
{
	private final static double NO_VALUE = Double.NaN;
	
	private int width;
	private int height;
	
	private int subpixelWidth;
	
	private int bufferLength;
	private double[] buffer;
	
	public ZBuffer(int width, int height, int subpixelWidth)
	{
		this.width = width;
		this.height = height;
		this.subpixelWidth = subpixelWidth;
		
		
		bufferLength = width * height * (int) MathExt.sqr(subpixelWidth);
		buffer = new double[bufferLength];
		
		reset();
	}
	
	public void reset()
	{
		if (buffer == null) {
			return;
		}
		
		for (int i = 0; i < bufferLength; i++) {
			buffer[i] = NO_VALUE;
		}
	}
	
	
	
	
	public void set(double x, double y, double z)
	{
		double f = 1.0 / this.subpixelWidth;
		x = MathExt.round(x / f) * f;
		y = MathExt.round(y / f) * f;
		
		int _x = (int) MathExt.floor(x);
		int _y = (int) MathExt.floor(y);
		
		int _xSub = (int) ((x - (double)_x) / f);
		int _ySub = (int) ((y - (double)_y) / f);
		
		int index = ((_y * this.width) * this.subpixelWidth) + _ySub + (_x * this.subpixelWidth + _xSub);
		
		if (index >= 0 && index < this.bufferLength) {
			buffer[index] = z;
		} else {
			// TODO: Throw
		}
	}
	
	public double get(double x, double y)
	{
		double f = 1.0 / this.subpixelWidth;
		x = MathExt.round(x / f) * f;
		y = MathExt.round(y / f) * f;
		
		int _x = (int) MathExt.floor(x);
		int _y = (int) MathExt.floor(y);
		
		int _xSub = (int) ((x - (double)_x) / f);
		int _ySub = (int) ((y - (double)_y) / f);
		
		int index = ((_y * this.width) * this.subpixelWidth) + _ySub + (_x * this.subpixelWidth + _xSub);
		
		if (index >= 0 && index < this.bufferLength) {
			return buffer[index];
		} else {
			return Double.NaN;
			// TODO: Throw
		}
	}
	
	
	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public int getSubpixelWidth()
	{
		return subpixelWidth;
	}
	
	
	public boolean isVisible(double x, double y, double z)
	{
		double _z = get(x, y);
		if (Double.isNaN(_z) || (z > _z && !Double.isNaN(_z))) {
			return true;
		} else {
			return false;
		}
	}

}
