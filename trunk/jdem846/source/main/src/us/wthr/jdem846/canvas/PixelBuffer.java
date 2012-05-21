package us.wthr.jdem846.canvas;

import us.wthr.jdem846.canvas.util.ColorUtil;


public class PixelBuffer extends AbstractBuffer
{

	private int[] buffer;
	
	
	public PixelBuffer(int width, int height, int subpixelWidth)
	{
		super(width, height, subpixelWidth);
		buffer = new int[getBufferLength()];
		
		reset();
	}
	
	public void reset()
	{
		reset(0x0);
	}
	
	public void reset(int backgroundColor)
	{
		if (buffer == null) {
			return;
		}
		
		for (int i = 0; i < getBufferLength(); i++) {
			buffer[i] = backgroundColor;
		}
	}
	

	
	public void set(double x, double y, int rgba)
	{
		int index = this.getIndex(x, y);
		
		if (index >= 0 && index < getBufferLength()) {
			
			int existing = get(x, y);
			if (existing != 0x0) {
				rgba = ColorUtil.overlayColor(rgba, existing);
			}
			
			buffer[index] = rgba;
		} else {
			// TODO: Throw
		}
	}
	
	public int get(double x, double y)
	{
		int index = this.getIndex(x, y);
		
		if (index >= 0 && index < getBufferLength()) {
			return buffer[index];
		} else {
			return 0x0;
			// TODO: Throw
		}
	}
	
	
	

	
}
