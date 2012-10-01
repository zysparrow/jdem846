package us.wthr.jdem846.model.processing.util;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.ScriptingException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.math.Vectors;
import us.wthr.jdem846.model.ModelPoint;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846.scripting.ScriptProxy;

public class LightingCalculator
{
	private static Log log = Logging.getLog(LightingCalculator.class);
	
	private LightingValues lightingValues;

	private double[] emmisiveColor = {1.0, 1.0, 1.0, 1.0};
	private double[] ambientColor = {1.0, 1.0, 1.0, 1.0};
	private double[] diffuseColor = {1.0, 1.0, 1.0, 1.0};
	//private double[] specularColor = {1.0, 1.0, 1.0, 1.0};
	private double[] specularColor = {0.8, 0.8, 0.8, 0.8};
	
	private boolean useDistanceAttenuation = true;
	private double attenuationRadius = 2000;
	private double blockShadowIntensity = 0.4;

	//double eye[] = {0.000001,0.000001,0.000001};
	double eye[] = {1.0, 0.0, 1.0};
	
	double[] E = new double[3];
	double[] P = new double[3];
	double[] color = new double[4];
	double[] N = new double[3];
	double[] L =  new double[3];
	double[] V = new double[3];
	double[] H = new double[3];
	
	protected ViewPerspective viewPerspective;
	
	protected ScriptProxy scriptProxy;
	
	public LightingCalculator()
	{
		
	}
	
	public LightingCalculator(double emmisive, double ambient, double diffuse, double specular, double blockShadowIntensity, ModelContext modelContext)
	{
		this(emmisive, ambient, diffuse, specular, blockShadowIntensity, null, modelContext.getScriptingContext().getScriptProxy());
	}
	
	public LightingCalculator(double emmisive, double ambient, double diffuse, double specular, double blockShadowIntensity, ViewPerspective viewPerspective, ModelContext modelContext)
	{
		this(emmisive, ambient, diffuse, specular, blockShadowIntensity, viewPerspective, modelContext.getScriptingContext().getScriptProxy());
	}
	
	public LightingCalculator(double emmisive, double ambient, double diffuse, double specular, double blockShadowIntensity)
	{
		this(emmisive, ambient, diffuse, specular, blockShadowIntensity, null, (ScriptProxy) null);
	}
	
	public LightingCalculator(double emmisive, double ambient, double diffuse, double specular, double blockShadowIntensity, ViewPerspective viewPerspective)
	{
		this(emmisive, ambient, diffuse, specular, blockShadowIntensity, viewPerspective, (ScriptProxy) null);
	}
	
	public LightingCalculator(double emmisive, double ambient, double diffuse, double specular, double blockShadowIntensity, ViewPerspective viewPerspective, ScriptProxy scriptProxy)
	{
		lightingValues = new LightingValues(emmisive, ambient, diffuse, specular);
		lightingValues.emmisiveLight = lightingValues.emmisiveLevel;
		lightingValues.ambientLight = lightingValues.ambientLevel;
		
		this.scriptProxy = scriptProxy;
		
		setBlockShadowIntensity(blockShadowIntensity);
		this.viewPerspective = viewPerspective;
		
		//Spheres.getPoint3D(-90.0, 0, modelRadius*10, eye);
		
		//Vectors.rotate(0.0, viewPerspective.getRotateY(), 0.0, eye);
		//Vectors.rotate(viewPerspective.getRotateX(), 0.0, 0.0, eye);
	}

	
	public void calculateColor(double[] normal, double latitude, double longitude, double radius, double shininess, double blockDistance, double[] lightSource, int[] rgba)
	{
		Spheres.getPoint3D(longitude, latitude, radius, P);
		
		if (viewPerspective != null) {
			Vectors.rotate(viewPerspective.getRotateX(), viewPerspective.getRotateY(), 0.0, P, Vectors.YXZ);
		}
		

		N[0] = normal[0];
		N[1] = normal[1];
		N[2] = normal[2];
		
		color[0] = (double) rgba[0] / 255.0;
		color[1] = (double) rgba[1] / 255.0;
		color[2] = (double) rgba[2] / 255.0;

		
		lightingValues.emmisiveLight = lightingValues.emmisiveLevel;
		lightingValues.ambientLight = lightingValues.ambientLevel;

		
		Vectors.inverse(lightSource, L);
		//Vectors.copy(lightSource, L);
		Vectors.subtract(L, P, L);
		Vectors.normalize(L, L);
		
		//double dot = Vectors.dotProduct(L, N);
		//double dot = Vectors.dotProduct(L, N);
		double dot = Vectors.dotProduct(N, L);
		double dp = Vectors.dotProduct(L, N) * 2.0;
		for (int i = 0; i<3; i++) {
            N[i] = N[i]*dp;
        }
		
		//lightingValues.diffuseLight = dot;
		//lightingValues.diffuseLight = MathExt.max(0, Vectors.dotProduct(H, N));
		//lightingValues.diffuseLight = MathExt.max(0, Vectors.dotProduct(N, L));
		//lightingValues.diffuseLight = Vectors.dotProduct(N, L);
		lightingValues.diffuseLight = dot;
		//lightingValues.diffuseLight  = Vectors.dotProduct(N, L);
		if (dot < 0) {
			//lightingValues.diffuseLight *= -1.0;
			//lightingValues.diffuseLight = 0;
		}
		
		
		lightingValues.specularLight = 0;
		if (lightingValues.diffuseLight > 0) {
			
			Vectors.subtract(N, L, H);
			Vectors.normalize(H, H);
			
			//double specDot = Vectors.dotProduct(E, H);
			double specDot = Vectors.dotProduct(H, E);
			if (shininess != 1.0) {
				specDot = MathExt.pow(specDot, shininess);
			}
			
			lightingValues.specularLight = specDot;
			if (lightingValues.specularLight < 0) {
				lightingValues.specularLight = 0;
			}
		}
		
		
		//lightingValues.specularLight = MathExt.pow(Vectors.dotProduct(E, H), shininess);
		//lightingValues.specularLight = MathExt.pow(MathExt.max(0, Vectors.dotProduct(E, H)), shininess);
		
		//if (lightingValues.diffuseLight <= 0) {
		//	lightingValues.specularLight = 0;
		//}
		
		//log.info("Specular Light: " + lightingValues.specularLight);
		
		//double effectiveSpecular = lightingValues.specularLevel;
		
		onLightLevels(latitude, longitude);
		
		
		lightingValues.emmisiveColor[0] = color[0] * lightingValues.emmisiveLight;
		lightingValues.emmisiveColor[1] = color[1] * lightingValues.emmisiveLight;
		lightingValues.emmisiveColor[2] = color[2] * lightingValues.emmisiveLight;
		

		
		lightingValues.ambientColor[0] = color[0] * lightingValues.ambientLight;
		lightingValues.ambientColor[1] = color[1] * lightingValues.ambientLight;
		lightingValues.ambientColor[2] = color[2] * lightingValues.ambientLight;
		
		lightingValues.diffuseColor[0] = lightingValues.diffuseLevel * color[0] * lightingValues.diffuseLight;
		lightingValues.diffuseColor[1] = lightingValues.diffuseLevel * color[1] * lightingValues.diffuseLight;
		lightingValues.diffuseColor[2] = lightingValues.diffuseLevel * color[2] * lightingValues.diffuseLight;
		
		//lightingValues.specularColor[0] = lightingValues.specularLevel * color[0] * lightingValues.specularLight;
		//lightingValues.specularColor[1] = lightingValues.specularLevel * color[1] * lightingValues.specularLight;
		//lightingValues.specularColor[2] = lightingValues.specularLevel * color[2] * lightingValues.specularLight;
		
		lightingValues.specularColor[0] = lightingValues.specularLevel * specularColor[0] * lightingValues.specularLight;
		lightingValues.specularColor[1] = lightingValues.specularLevel * specularColor[1] * lightingValues.specularLight;
		lightingValues.specularColor[2] = lightingValues.specularLevel * specularColor[2] * lightingValues.specularLight;


		// Add and clamp color channels
		rgba[0] = (int) clamp(255.0 * (lightingValues.emmisiveColor[0] + lightingValues.ambientColor[0] + lightingValues.diffuseColor[0] + lightingValues.specularColor[0]));
		rgba[1] = (int) clamp(255.0 * (lightingValues.emmisiveColor[1] + lightingValues.ambientColor[1] + lightingValues.diffuseColor[1] + lightingValues.specularColor[1]));
		rgba[2] = (int) clamp(255.0 * (lightingValues.emmisiveColor[2] + lightingValues.ambientColor[2] + lightingValues.diffuseColor[2] + lightingValues.specularColor[2]));

	}
	
	
	protected double clamp(double c)
	{
		if (c > 255)
			c = 255;
		if (c < 0)
			c = 0;
		return c;
	}
	
	public void setEye(double[] eye)
	{
		this.E[0] = eye[0];
		this.E[1] = eye[1];
		this.E[2] = eye[2];
		
	}
	
	public double getEmmisive()
	{
		return lightingValues.emmisiveLevel;
	}

	public void setEmmisive(double emmisive)
	{
		lightingValues.emmisiveLevel = emmisive;
		lightingValues.emmisiveLight = lightingValues.emmisiveLevel;
	}

	public double getAmbient()
	{
		return lightingValues.ambientLevel;
	}

	public void setAmbient(double ambient)
	{
		this.lightingValues.ambientLevel = ambient;
		lightingValues.ambientLight = lightingValues.ambientLevel;
	}

	public double getDiffuse()
	{
		return lightingValues.diffuseLevel;
	}

	public void setDiffuse(double diffuse)
	{
		lightingValues.diffuseLevel = diffuse;
	}

	public double getSpecular()
	{
		return lightingValues.specularLevel;
	}

	public void setSpecular(double specular)
	{
		lightingValues.specularLevel = specular;
	}

	public double getBlockShadowIntensity()
	{
		return blockShadowIntensity;
	}

	public void setBlockShadowIntensity(double blockShadowIntensity)
	{
		this.blockShadowIntensity = blockShadowIntensity;
	}

	public boolean getUseDistanceAttenuation()
	{
		return useDistanceAttenuation;
	}

	public void setUseDistanceAttenuation(boolean useDistanceAttenuation)
	{
		this.useDistanceAttenuation = useDistanceAttenuation;
	}

	public double getAttenuationRadius()
	{
		return attenuationRadius;
	}

	public void setAttenuationRadius(double attenuationRadius)
	{
		this.attenuationRadius = attenuationRadius;
	}

	
	protected void onLightLevels(double latitude, double longitude)
	{
		
		if (scriptProxy != null) {
			try {
				scriptProxy.onLightLevels(latitude, longitude, lightingValues);
			} catch (ScriptingException ex) {
				log.error("Error running light levels callback: " + ex.getMessage(), ex);
			}
		}
	}
	
	
	public void setSpecularColor(double[] specularColor)
	{
		this.specularColor = specularColor;
	}
	
	public double[] getSpecularColor()
	{
		return this.specularColor;
	}
	
}	