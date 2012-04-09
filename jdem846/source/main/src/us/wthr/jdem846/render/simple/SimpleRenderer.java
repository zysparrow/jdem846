package us.wthr.jdem846.render.simple;

import java.awt.Color;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.geom.Edge;
import us.wthr.jdem846.geom.Line;
import us.wthr.jdem846.geom.Vertex;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.image.ImageDataContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.canvas.CanvasProjection;
import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.render.render3.ModelBuilder;
import us.wthr.jdem846.render.render3.ModelGrid;
import us.wthr.jdem846.render.render3.ModelRenderer;

public class SimpleRenderer 
{

	private static Log log = Logging.getLog(SimpleRenderer.class);
	
	private int[] sourceLineColor = {Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), 255};
	private int[] baseGridColor = {Color.LIGHT_GRAY.getRed(), Color.LIGHT_GRAY.getGreen(), Color.LIGHT_GRAY.getBlue(), 255};
	private boolean paintGlobalBaseGrid = true;
	
	
	private ModelContext modelContext;
	private ModelBuilder modelBuilder;
	private ModelGrid modelGrid;
	private ModelRenderer modelRenderer;
	
	private MapPoint point = new MapPoint();
	private CanvasProjection projection;
	
	public SimpleRenderer(ModelContext modelContext)
	{
		this.modelContext = modelContext;
		
	}
	
	public void setModelContext(ModelContext modelContext)
	{
		this.modelContext = modelContext;
	}
	
	public void prepare(boolean resetCache) throws RenderEngineException
	{
		log.info("Resetting simple renderer cache");
		
		
		
		double north = modelContext.getNorth();
		double south = modelContext.getSouth();
		double east = modelContext.getEast();
		double west = modelContext.getWest();
		
		double latitudeSlices = modelContext.getModelOptions().getDoubleOption("us.wthr.jdem846.modelOptions.simpleRenderer.latitudeSlices");
		double longitudeSlices = modelContext.getModelOptions().getDoubleOption("us.wthr.jdem846.modelOptions.simpleRenderer.longitudeSlices");
		
		double latitudeResolution = modelContext.getModelDimensions().getOutputLatitudeResolution();
		double longitudeResolution = modelContext.getModelDimensions().getOutputLongitudeResolution();
		
		latitudeResolution = (north - south - latitudeResolution) / latitudeSlices;
		longitudeResolution = (east - west - longitudeResolution) / longitudeSlices;
		
		modelContext.getModelDimensions().outputLatitudeResolution = latitudeResolution;
		modelContext.getModelDimensions().outputLongitudeResolution = longitudeResolution;
		
		
		
		if (resetCache || modelGrid == null) {
			
			if (modelGrid != null) {
				modelGrid.dispose();
			}
			
			modelGrid = new ModelGrid(modelContext.getNorth(), 
					modelContext.getSouth(), 
					modelContext.getEast(), 
					modelContext.getWest(), 
					modelContext.getModelDimensions().getOutputLatitudeResolution(), 
					modelContext.getModelDimensions().getOutputLongitudeResolution());
		}
		
		
		
		if (modelBuilder != null) {
			modelBuilder.dispose();
		}
		modelBuilder = new ModelBuilder(modelContext, modelGrid);
		modelBuilder.prepare();
		
		
		if (resetCache || modelRenderer == null) {
			
			if (modelRenderer != null) {
				modelRenderer.dispose();
			}
			
			modelRenderer = new ModelRenderer(modelContext, modelGrid);
		}
		modelRenderer.setModelContext(modelContext);
		modelRenderer.prepare();
		
		
		modelBuilder.setUseScripting(false);
		modelBuilder.setRunLoadProcessor(resetCache);
		modelBuilder.setRunColorProcessor(resetCache);
		modelBuilder.getGridLoadProcessor().setUseScripting(false);
		modelBuilder.getGridLoadProcessor().setTiledPrecaching(false);
		modelBuilder.getGridLoadProcessor().setAverageOverlappedData(false);
		modelBuilder.getGridLoadProcessor().setGetStandardResolutionElevation(true);
		modelBuilder.getGridLoadProcessor().setInterpolateData(false);
		modelBuilder.getGridColorProcessor().setUseScripting(false);
		modelBuilder.getGridHillshadeProcessor().setRayTraceShadows(false);
		modelBuilder.getGridHillshadeProcessor().setRecalcLightOnEachPoint(false);
	
		projection = modelContext.getModelCanvas().getCanvasProjection();
		
		paintGlobalBaseGrid = modelContext.getModelOptions().getBooleanOption("us.wthr.jdem846.modelOptions.simpleRenderer.paintGlobalBaseGrid");

		
	}
	
	
	public void render() throws RenderEngineException
	{

		log.info("Rendering model simple image");
		
		//ModelCanvas modelCanvas = modelContext.getModelCanvas();
		
		
		/*
		if (modelContext.getModelOptions().getBooleanOption("us.wthr.jdem846.modelOptions.simpleRenderer.paintLightSourceLines")) {
			try {
				paintLightSourceLines(modelCanvas);
			} catch (Exception ex) {
				log.error("Error painting light source lines: " + ex.getMessage(), ex);
			}
		}
		
		if (modelContext.getModelOptions().getBooleanOption("us.wthr.jdem846.modelOptions.simpleRenderer.paintBaseGrid")) {
			try {
				paintBasicGrid(modelCanvas);
			} catch (Exception ex) {
				log.error("Error painting base grid: " + ex.getMessage(), ex);
			}
		}
		*/
		
		
		
		if (modelContext.getModelOptions().getBooleanOption("us.wthr.jdem846.modelOptions.simpleRenderer.paintRasterPreview")) {
			
			modelBuilder.process();
			modelRenderer.process();
			
		}
		
		
		
		
		
	}
	

	

	protected void paintLightSourceLines(ModelCanvas canvas) throws Exception
	{
		
		RasterDataContext rasterDataContext = modelContext.getRasterDataContext();
		ImageDataContext imageDataContext = modelContext.getImageDataContext();
		
		if (rasterDataContext.getRasterDataListSize() == 0 && imageDataContext.getImageListSize() == 0) {
			return;
		}
		
		if (modelContext.getLightingContext() == null || !modelContext.getLightingContext().isLightingEnabled()) {
			log.info("Lighting not enabled, skipping light source lines");
			return;
		}
		
		double minElevation = modelContext.getRasterDataContext().getDataMinimumValue() - 10;
		
		double north = modelContext.getNorth();
		double south = modelContext.getSouth();
		double east = modelContext.getEast();
		double west = modelContext.getWest();
		
		double latitudeResolution = modelContext.getRasterDataContext().getLatitudeResolution();
		double longitudeResolution = modelContext.getRasterDataContext().getLongitudeResolution();
		double centerLatitude = (north + south) / 2.0;
		double centerLongitude = (east + west) / 2.0;
		double metersResolution = modelContext.getRasterDataContext().getMetersResolution();
		
		double latRes = modelContext.getRasterDataContext().getLatitudeResolution();
		double effLatRes = modelContext.getRasterDataContext().getEffectiveLatitudeResolution();
		metersResolution = metersResolution / (latRes / effLatRes);
		
		double radiusInterval = MathExt.sqrt(MathExt.sqr(latitudeResolution) + MathExt.sqr(longitudeResolution));
		
		
		//Line line = new Line();
		
		
		double[] points = new double[3];
		double radius = MathExt.sqrt(MathExt.sqr(north - south) + MathExt.sqr(east - west));
			
		double solarAzimuth = modelContext.getLightingContext().getLightingAzimuth();
		double solarElevation = modelContext.getLightingContext().getLightingElevation();
		
		Spheres.getPoint3D(solarAzimuth, solarElevation, radius, points);
	
		double latitude = centerLatitude + points[0];
		double longitude = centerLongitude - points[2];
		double resolution = (points[1] / radiusInterval);
		double elevation = (resolution * metersResolution);

		
		Vertex vMid = createVertex(centerLatitude, centerLongitude, minElevation, sourceLineColor);
		Vertex vHigh = createVertex(latitude, longitude, elevation, sourceLineColor);
		Vertex vLow = createVertex(latitude, longitude, minElevation, sourceLineColor);
		
		Edge e = new Edge(vMid, vHigh);
    	Line l = new Line();
    	l.addEdge(e);
    	canvas.drawShape(l, sourceLineColor);
    	
    	e = new Edge(vMid, vLow);
    	l = new Line();
    	l.addEdge(e);
    	canvas.drawShape(l, sourceLineColor);

	}
	

	protected void paintBasicGrid(ModelCanvas canvas) throws Exception
	{
		
		RasterDataContext rasterDataContext = modelContext.getRasterDataContext();
		ImageDataContext imageDataContext = modelContext.getImageDataContext();
		
		if (rasterDataContext.getRasterDataListSize() == 0 && imageDataContext.getImageListSize() == 0) {
			return;
		}
		
		double north = modelContext.getNorth();
		double south = modelContext.getSouth();
		double east = modelContext.getEast();
		double west = modelContext.getWest();
		
		if (paintGlobalBaseGrid) {
			north = 90;
			south = -90;
			east = 180;
			west = -180;
		}
		
		Line line = new Line();

		line.addEdge(createEdge(north, west, -1.0, south, west, -1.0));
		line.addEdge(createEdge(south, west, -1.0, south, east, -1.0));
		line.addEdge(createEdge(south, east, -1.0, north, east, -1.0));
		line.addEdge(createEdge(north, east, -1.0, north, west, -1.0));
		
		
		double strips = 10.0;
		double slices = 20.0;
		
		double strip_step = (north - south) / strips;
		double slice_step = (east - west) / slices;
		
		Edge e;
		Line l;
		
		double minElevation = modelContext.getRasterDataContext().getDataMinimumValue() - 10;
		
		for (double phi = south; phi < north/* - strip_step*/; phi +=strip_step) {
            for (double theta = west; theta < east/*-slice_step*/; theta+=slice_step) {
            	
            	
            	Vertex v0 = createVertex(phi, theta, minElevation, baseGridColor);
            	Vertex v1 = createVertex(phi, theta+slice_step, minElevation, baseGridColor);
            	Vertex v2 = createVertex(phi+strip_step, theta, minElevation, baseGridColor);
            	Vertex v3 = createVertex(phi+strip_step, theta+slice_step, minElevation, baseGridColor);
            	
            	e = new Edge(v0, v1);
            	l = new Line();
            	l.addEdge(e);
            	canvas.drawShape(l, baseGridColor);
            	
            	
            	e = new Edge(v0, v2);
            	l = new Line();
            	l.addEdge(e);
            	canvas.drawShape(l, baseGridColor);
            	
            	e = new Edge(v1, v3);
            	l = new Line();
            	l.addEdge(e);
            	canvas.drawShape(l, baseGridColor);
            	
            	e = new Edge(v2, v3);
            	l = new Line();
            	l.addEdge(e);
            	canvas.drawShape(l, baseGridColor);
			}
			
		}

	}
	
	
	

	
	
	
	
	
	
	
	protected Edge createEdge(double lat0, double lon0, double elev0, double lat1, double lon1, double elev1) throws MapProjectionException
    {
		return createEdge(lat0, lon0, elev0, null, lat1, lon1, elev1, null);
    }
	
	protected Edge createEdge(double lat0, double lon0, double elev0, int[] rgba0, double lat1, double lon1, double elev1, int[] rgba1) throws MapProjectionException
    {

		Vertex v0 = createVertex(lat0, lon0, elev0, rgba0);
		Vertex v1 = createVertex(lat1, lon1, elev1, rgba1);
    	return new Edge(v0, v1);
    	
    }
	
	protected Vertex createVertex(double lat, double lon, double elev, int[] rgba) throws MapProjectionException
	{
    	projection.getPoint(lat, lon, elev, point);
    	
    	double x = point.column;
    	double y = point.row;
    	double z = point.z;
    	//double z = modelContext.getRasterDataContext().getDataMinimumValue();//point.z;
		
    	Vertex v = new Vertex(x, y, z, rgba);
    	return v;
	}
	

	
}