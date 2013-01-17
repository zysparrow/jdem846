package us.wthr.jdem846.modelgrid;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.buffers.BufferFactory;
import us.wthr.jdem846.buffers.IFloatBuffer;
import us.wthr.jdem846.buffers.IIntBuffer;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.util.ColorUtil;

public class BufferedModelGrid extends BaseModelGrid
{
	private static Log log = Logging.getLog(BufferedModelGrid.class);

	private IFloatBuffer elevationGrid;
	private IIntBuffer rgbaGrid;
	//private FloatBuffer elevationGrid;
	//private IntBuffer rgbaGrid;

	private boolean isDisposed = false;
	
	
	public BufferedModelGrid(double north, double south, double east, double west, double latitudeResolution, double longitudeResolution, double minimum, double maximum, int width, int height)
	{
		super(north, south, east, west, latitudeResolution, longitudeResolution, minimum, maximum, width, height);

		log.info("Allocating elevation and RGBA grid buffers of length " + gridLength);
		
		rgbaGrid = BufferFactory.allocateIntBuffer(gridLength);
		elevationGrid = BufferFactory.allocateFloatBuffer(gridLength);
		
		//ByteBuffer bb = ByteBuffer.allocateDirect((int)gridLength * (Integer.SIZE / 8));
		//rgbaGrid = bb.asIntBuffer();
		
	//	bb = ByteBuffer.allocateDirect((int)gridLength * (Float.SIZE / 8));
		//elevationGrid = bb.asFloatBuffer();
		
		
		//elevationGrid = FloatBuffer.allocate((int)gridLength);
		//rgbaGrid = IntBuffer.allocate((int)gridLength);
		//elevationGrid = new float[(int) gridLength];
		//rgbaGrid = new int[(int) gridLength];
	}
	
	public BufferedModelGrid(double north, double south, double east, double west, double latitudeResolution, double longitudeResolution, double minimum, double maximum)
	{
		super(north, south, east, west, latitudeResolution, longitudeResolution, minimum, maximum);

		log.info("Allocating elevation and RGBA grid buffers of length " + gridLength);
		
		rgbaGrid = BufferFactory.allocateIntBuffer(gridLength);
		elevationGrid = BufferFactory.allocateFloatBuffer(gridLength);
		//ByteBuffer bb = ByteBuffer.allocateDirect((int)gridLength * (Integer.SIZE / 8));
		//rgbaGrid = bb.asIntBuffer();
		
		//bb = ByteBuffer.allocateDirect((int)gridLength * (Float.SIZE / 8));
		//elevationGrid = bb.asFloatBuffer();
		
		//elevationGrid = new float[(int) gridLength];
		//rgbaGrid = new int[(int) gridLength];
		//elevationGrid = FloatBuffer.allocate((int)gridLength);
		//rgbaGrid = IntBuffer.allocate((int)gridLength);
		// reset();
	}

	
	
	@Override
	public void dispose()
	{
		elevationGrid = null;
		rgbaGrid = null;
	}

	@Override
	public boolean isDisposed()
	{
		return isDisposed;
	}

	@Override
	public void reset()
	{

		for (long i = 0; i < gridLength; i++) {
			elevationGrid.put(i, (float)DemConstants.ELEV_UNDETERMINED);
			rgbaGrid.put(i, 0x0);
			//elevationGrid[i] = (float) DemConstants.ELEV_UNDETERMINED;
			//rgbaGrid[i] = 0x0;
		}

	}

	@Override
	public IIntBuffer getModelTexture()
	{
		return rgbaGrid;
	}

	@Override
	public double getElevationByIndex(int index) throws DataSourceException
	{
		if (index >= 0 && index < this.gridLength) {
			return elevationGrid.get(index);
		} else {
			return DemConstants.ELEV_NO_DATA;
		}
	}
	
	@Override
	public void setElevationByIndex(int index, double elevation) throws DataSourceException 
	{
		if (index >= 0 && index < this.gridLength) {
			elevationGrid.put(index, (float)elevation);
			getElevationHistogramModel().add(elevation);
		}
	}

	@Override
	public double getElevation(double latitude, double longitude, boolean basic) 
	{
		int index = getIndex(latitude, longitude);
		return getElevationByIndex(index);
	}

	@Override
	public void setElevation(double latitude, double longitude, double elevation)
	{
		int index = getIndex(latitude, longitude);
		setElevationByIndex(index, elevation);
	}

	
	
	
	@Override
	public void getRgbaByIndex(int index, int[] fill) throws DataSourceException
	{
		ColorUtil.intToRGBA(getRgbaByIndex(index), fill);
	}

	
	
	@Override
	public int getRgbaByIndex(int index) throws DataSourceException
	{
		if (index >= 0 && index < this.gridLength) {
			return rgbaGrid.get(index);
		} else {
			return 0x0;
		}
	}

	@Override
	public void setRgbaByIndex(int index, int rgba) throws DataSourceException
	{
		if (index >= 0 && index < this.gridLength) {
			rgbaGrid.put(index, rgba);
		}
	}

	@Override
	public void setRgbaByIndex(int index, int[] rgba) throws DataSourceException
	{
		this.setRgbaByIndex(index, ColorUtil.rgbaToInt(rgba));
	}
	
	
	
	@Override
	public void getRgba(double latitude, double longitude, int[] fill)
	{
		ColorUtil.intToRGBA(getRgba(latitude, longitude), fill);
	}

	
	
	@Override
	public int getRgba(double latitude, double longitude)
	{
		int index = getIndex(latitude, longitude);
		return getRgbaByIndex(index);
	}

	@Override
	public void setRgba(double latitude, double longitude, int rgba)
	{
		int index = getIndex(latitude, longitude);
		setRgbaByIndex(index, rgba);
	}

	@Override
	public void setRgba(double latitude, double longitude, int[] rgba)
	{
		this.setRgba(latitude, longitude, ColorUtil.rgbaToInt(rgba));
	}


}
