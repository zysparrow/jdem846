package us.wthr.jdem846.model.processing.coloring;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.model.ModelGrid;
import us.wthr.jdem846.model.ModelPoint;
import us.wthr.jdem846.model.ModelPointHandler;
import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.model.processing.AbstractGridProcessor;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.model.processing.GridProcessor;


@GridProcessing(id="us.wthr.jdem846.model.processing.coloring.TerrainRuggednessIndexColoringProcessor",
				name="TRI Color Process",
				type=GridProcessingTypesEnum.COLORING,
				optionModel=TerrainRuggednessIndexColoringOptionModel.class,
				enabled=true
)
/** TRI (Terrain Ruggedness Index) is calculated using difference in the 
 * elevation of the eight surrounding points to the center. If a particular
 * point contains invalid data, it is discarded and the divisor is reduced
 * by one. If no valid data is found in the surrounding points, the result
 * will be zero (0).
 * 
 * Equation: (Ec denotes the central elevation)
 * sqrt([(E1 - Ec)^2 + (E2 - Ec)^2 + ... + (E8 - Ec)^2] / 8)
 * 
 * @author Kevin M. Gill
 *
 */
public class TerrainRuggednessIndexColoringProcessor extends AbstractGridProcessor implements GridProcessor, ModelPointHandler
{

	private static Log log = Logging.getLog(TerrainRuggednessIndexColoringProcessor.class);
	
	private double latitudeResolution;
	private double longitudeResolution;
	
	private double[] gridElevationBuffer = new double[8];
	
	// TODO: Make these configurable
	private int[] minTriColor = {0, 255, 0, 255};
	private int[] maxTriColor = {255, 0, 0, 255};
	private int[] colorBuffer = new int[4];
	
	private double minTri = 10000000;
	private double maxTri = -10000000;
	
	private double pass = 0;
	
	private int band = 1;
	private int bandHalf = 1;
	private ModelColoring modelColoring;
	
	
	
	private TerrainRuggednessIndexColoringOptionModel optionModel;
	
	public TerrainRuggednessIndexColoringProcessor()
	{
		
	}
	
	public TerrainRuggednessIndexColoringProcessor(ModelContext modelContext, ModelGrid modelGrid)
	{
		super(modelContext, modelGrid);
	}
	
	@Override
	public void prepare() throws RenderEngineException
	{
		optionModel = (TerrainRuggednessIndexColoringOptionModel) this.getProcessOptionModel();
		
		latitudeResolution = getModelDimensions().getOutputLatitudeResolution();
		longitudeResolution = getModelDimensions().getOutputLongitudeResolution();
		
		modelColoring = ColoringRegistry.getInstance(optionModel.getColorTint()).getImpl();
		
		band = optionModel.getBand();
		bandHalf = (int) Math.round(((double)band / 2.0));
		
		if (bandHalf < 1) {
			bandHalf = 1;
		}
		
		pass = 0;
	}
	
	
	@Override
	public void process() throws RenderEngineException
	{
		log.info("TRI Processor 1st Pass...");
		super.process();
		
		log.info("Minimum TRI: " + minTri);
		log.info("Maximum TRI: " + maxTri);
		
		log.info("TRI Processor 2nd Pass...");
		pass++;
		super.process();
	}
	
	@Override
	public void onModelPoint(double latitude, double longitude)
			throws RenderEngineException
	{
		
		ModelPoint modelPoint = modelGrid.get(latitude, longitude);
		
		if (pass == 0) {
			firstPass(modelPoint, latitude, longitude);
		} else {
			secondPass(modelPoint, latitude, longitude);
		}
		
	}
	
	protected void firstPass(ModelPoint modelPoint, double latitude, double longitude)
	{
		double tri = calculateTri(modelPoint, latitude, longitude);
		
		minTri = MathExt.min(minTri, tri);
		maxTri = MathExt.max(maxTri, tri);
	}
	
	protected void secondPass(ModelPoint modelPoint, double latitude, double longitude)
	{
		double tri = calculateTri(modelPoint, latitude, longitude);
		
		double ratio = (tri - minTri) / (maxTri - minTri);

		modelColoring.getColorByPercent(ratio, colorBuffer);
		
		
		modelPoint.setRgba(colorBuffer);
	}
	
	protected double calculateTri(ModelPoint modelPoint, double latitude, double longitude)
	{
		
		
		double c = modelPoint.getElevation();
		
		
		double north = latitude + (latitudeResolution * bandHalf);
		double south = latitude - (latitudeResolution * bandHalf);
		
		double east = longitude + (longitudeResolution * bandHalf);
		double west = longitude - (longitudeResolution * bandHalf);
		
		double samples = 0.0;
		double elevationSum = 0.0;
		double elevationPoint = 0.0;
		
		for (double lat = north; lat >= south; lat-=latitudeResolution) {
			
			for (double lon = west; lon <= east; lon+=longitudeResolution) {
				elevationPoint = getElevationAtPoint(lat, lon);
				
				if (elevationPoint != DemConstants.ELEV_NO_DATA) {
					elevationSum += MathExt.sqr(elevationPoint - c);;
					samples++;
				}
			}
			
		}
		
		double tri = 0;
		
		if (samples > 0) {
			tri = MathExt.sqrt(elevationSum / samples);
		}
		
		return tri;
		
		/*
		gridElevationBuffer[0] = getElevationAtPoint(latitude + latitudeResolution, longitude);
		gridElevationBuffer[1] = getElevationAtPoint(latitude + latitudeResolution, longitude - longitudeResolution);
		gridElevationBuffer[2] = getElevationAtPoint(latitude, longitude - longitudeResolution);
		gridElevationBuffer[3] = getElevationAtPoint(latitude - latitudeResolution, longitude - longitudeResolution);
		gridElevationBuffer[4] = getElevationAtPoint(latitude - latitudeResolution, longitude);
		gridElevationBuffer[5] = getElevationAtPoint(latitude - latitudeResolution, longitude + longitudeResolution);
		gridElevationBuffer[6] = getElevationAtPoint(latitude, longitude + longitudeResolution);
		gridElevationBuffer[7] = getElevationAtPoint(latitude + latitudeResolution, longitude + longitudeResolution);
		
		
		double validPoints = 0;
		
		double a = 0;
		for (double sample : gridElevationBuffer) {
			if (sample != DemConstants.ELEV_NO_DATA) {
				a += MathExt.sqr(sample - c);
				validPoints++;
			}
		}
		
		
		double tri = 0;
		
		if (validPoints > 0) {
			tri = MathExt.sqrt(a / validPoints);
		}
		
		return tri;
		
		*/
	}
	
}