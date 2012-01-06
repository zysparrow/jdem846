package us.wthr.jdem846.rasterdata.gridfloat;

import java.io.File;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.AbstractRasterDataProvider;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataLatLongBox;

public class GridFloatRasterDataProvider extends AbstractRasterDataProvider
{
	private static Log log = Logging.getLog(GridFloatRasterDataProvider.class);
	
	private GridFloatHeader header;
	private CachingGridFloatDataReader dataReader;
	private File dataFile;
	
	private boolean isDisposed = false;
	
	
	
	public GridFloatRasterDataProvider()
	{
		
	}


	@Override
	public void create(String filePath) throws DataSourceException
	{
		dataFile = new File(filePath);
		if (!dataFile.exists()) {
			throw new DataSourceException("GridFloat data file not found at " + filePath);
		}
		
		String headerFilePath = filePath.replace(".flt", ".hdr");
		header = new GridFloatHeader(headerFilePath);
		
		this.setColumns(header.getColumns());
		this.setRows(header.getRows());
		
		this.setNorth(header.getyLowerLeft() + (header.getCellSize() * header.getRows()));
		this.setSouth(header.getyLowerLeft());
		this.setEast(header.getxLowerLeft() + (header.getCellSize() * header.getColumns()));
		this.setWest(header.getxLowerLeft());
		
		this.setLatitudeResolution(header.getCellSize());
		this.setLongitudeResolution(header.getCellSize());
		
		dataReader = new CachingGridFloatDataReader(dataFile, header.getRows(), header.getColumns(), header.getByteOrder());
	
		log.info("Created GridFloat raster data provider...");
		log.info("    North/South: " + getNorth() + "/" + getSouth());
		log.info("    East/West: " + getEast() + "/" + getWest());
		log.info("    Rows/Columns: " + getRows() + "/" + getColumns());
		log.info("    Latitude Resolution: " + getLatitudeResolution());
		log.info("    Longitude Resolution: " + getLongitudeResolution());
	}

	
	public String getFilePath()
	{
		return dataFile.getAbsolutePath();
	}

	@Override
	public void dispose() throws DataSourceException
	{
		if (isDisposed()) {
			throw new DataSourceException("Raster data provider already disposed.");
		}
		
		if (!dataReader.isDisposed()) {
			dataReader.dispose();
		}
		
		
		// TODO: Finish
		isDisposed = true;
	}

	@Override
	public boolean isDisposed()
	{
		return isDisposed;
	}
	

	@Override
	public double getData(int row, int column) throws DataSourceException
	{
		if (isDisposed()) {
			throw new DataSourceException("Data raster provider has been disposed.");
		}
		
		double data = dataReader.get(row, column);
		
		if (data == header.getNoData())
			data = DemConstants.ELEV_NO_DATA;
		
		return data;
	}


	@Override
	public boolean fillBuffer(double north, double south, double east,
			double west) throws DataSourceException
	{
		
		RasterDataLatLongBox bufferBox = new RasterDataLatLongBox(north, south, east, west);
		if (!this.intersects(bufferBox)) {
			return false;
		}
		
		// Adjust the range to fit what this data supports
		

		// TODO: Too simplistic
		if (north > this.getNorth())
			north = this.getNorth();
		if (south < this.getSouth())
			south = this.getSouth();
		if (east > this.getEast())
			east = this.getEast();
		if (west < this.getWest())
			west = this.getWest();
		
		// TODO: Too simplistic
		int x = (int) Math.floor(this.longitudeToColumn(west));
		int y = (int) Math.floor(this.latitudeToRow(north));

		int x2 = (int) Math.ceil(this.longitudeToColumn(east));
		int y2 = (int) Math.ceil(this.latitudeToRow(south));
		
		// We add 2 columns & 2 rows to support data point interpolation (done in AbstractRasterDataProvider)
		int columns = x2 - x + 2;
		int rows = y2 - y + 2;

		if (columns <= 0 || rows <= 0) {
			return false;
		}
		
		if (x + columns > header.getColumns()) {
			columns = header.getColumns() - x;
		}
		
		if (y + rows > header.getRows()) {
			rows = header.getRows() - y;
		}
		
		log.info("Filling raster buffer...");
		boolean status;
		try {
			if ((status = dataReader.fillBuffer(x, y, columns, rows))) {
				log.info("Filled raster buffer");
			} else {
				log.info("Raster buffer not filled.");
			}
		} catch (Exception ex) {
			throw new DataSourceException("Error attempting to cache more data than actually exists: " + ex.getMessage(), ex);
		}
		return status;
	}
	
	public boolean isBufferFilled()
	{
		return dataReader.isBufferFilled();
	}

	@Override
	public void clearBuffer() throws DataSourceException
	{
		log.info("Clearing Buffer!");
		dataReader.clearBuffer();
	}
	
	public RasterData copy() throws DataSourceException
	{
		if (isDisposed()) {
			throw new DataSourceException("Cannot copy object: already disposed");
		}
		
		GridFloatRasterDataProvider clone = new GridFloatRasterDataProvider();
		clone.create(this.dataFile.getAbsolutePath());

		
		return clone;
	}
}