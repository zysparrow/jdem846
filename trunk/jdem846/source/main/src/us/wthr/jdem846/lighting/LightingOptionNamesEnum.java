package us.wthr.jdem846.lighting;

public enum LightingOptionNamesEnum
{
	LIGHT_SOURCE_SPECIFY_TYPE("us.wthr.jdem846.lightingContext.sourceSpecifyType"),
	LIGHTING_ENABLED("us.wthr.jdem846.lightingContext.lightingEnabled"),
	LIGHTING_AZIMUTH("us.wthr.jdem846.lightingContext.lightingAzimuth"),
	LIGHTING_ELEVATION("us.wthr.jdem846.lightingContext.lightingElevation"),
	LIGHTING_MULTIPLE("us.wthr.jdem846.lightingContext.lightingMultiple"),
	LIGHTING_ON_DATE("us.wthr.jdem846.lightingContext.lightOnDate"),
	RECALC_LIGHT_ON_EACH_POINT("us.wthr.jdem846.lightingContext.recalculateLightSourceOnEachPoint"),
	RELATIVE_LIGHT_INTENSITY("us.wthr.jdem846.lightingContext.relativeLightIntensity"),
	RELATIVE_DARK_INTENSITY("us.wthr.jdem846.lightingContext.relativeDarkIntensity"),
	SPOT_EXPONENT("us.wthr.jdem846.lightingContext.spotExponent"),
	RAY_TRACE_SHADOWS("us.wthr.jdem846.lightingContext.rayTraceShadows"),
	SHADOW_INTENSITY("us.wthr.jdem846.lightingContext.shadowIntensity"),
	LIGHT_ZENITH("us.wthr.jdem846.lightingContext.lightZenith"),
	DARK_ZENITH("us.wthr.jdem846.lightingContext.darkZenith");
	
	private final String optionName;
	
	LightingOptionNamesEnum(String optionName)
	{
		this.optionName = optionName;
	}
	
	public String optionName() { return optionName; }
}