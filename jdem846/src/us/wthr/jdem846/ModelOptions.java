/*
 * Copyright (C) 2011 Kevin M. Gill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wthr.jdem846;

import java.awt.Color;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import us.wthr.jdem846.project.ProjectModel;
import us.wthr.jdem846.render.mapprojection.MapProjectionEnum;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;
import us.wthr.jdem846.util.ColorSerializationUtil;
import us.wthr.jdem846.util.NumberFormattingUtil;

/** Options for model processing.
 * 
 * @author Kevin M. Gill
 *
 */
public class ModelOptions
{
	public static final int SPOT_EXPONENT_MINIMUM = 1;
	public static final int SPOT_EXPONENT_MAXIMUM = 5;
	

	
	private Map<String, String> optionsMap = new HashMap<String, String>();

	
	private String userScript = null;
	private ScriptLanguageEnum scriptLanguage = null;
	
	private Projection projection = new Projection();
	private String writeTo = null;
	
	public ModelOptions()
	{

		for (ModelOptionNamesEnum optionName : ModelOptionNamesEnum.values()) {
			String property = JDem846Properties.getProperty(optionName.optionName());
			if (property != null) {
				setOption(optionName.optionName(), property);
			}
		}

		String scriptLanguageString = JDem846Properties.getProperty(ModelOptionNamesEnum.USER_SCRIPT_LANGUAGE.optionName());
		setScriptLanguage(scriptLanguageString);
		
		this.projection.setRotateX(getDoubleOption(ModelOptionNamesEnum.PROJECTION_ROTATE_X));
		this.projection.setRotateY(getDoubleOption(ModelOptionNamesEnum.PROJECTION_ROTATE_Y));
		this.projection.setRotateZ(getDoubleOption(ModelOptionNamesEnum.PROJECTION_ROTATE_Z));
		
		this.projection.setShiftX(getDoubleOption(ModelOptionNamesEnum.PROJECTION_SHIFT_X));
		this.projection.setShiftY(getDoubleOption(ModelOptionNamesEnum.PROJECTION_SHIFT_Y));
		this.projection.setShiftZ(getDoubleOption(ModelOptionNamesEnum.PROJECTION_SHIFT_Z));
		
	}
	
	public Set<String> getOptionNames()
	{
		return optionsMap.keySet();
	}
	
	public void setOption(String name, Object value)
	{
		String sValue = null;
		
		if (value == null) {
			return;
		}
		
		if (value instanceof String) {
			sValue = (String) value;
		} else if (value instanceof Integer ||
					value instanceof Double ||
					value instanceof Long ||
					value instanceof Float) {
			sValue = NumberFormattingUtil.format(value);
		} else if (value instanceof Boolean){
			sValue = Boolean.toString((Boolean)value);
		} else {
			throw new InvalidParameterException("Invalid parameter type: " + value.getClass().getName());
		}
		
		optionsMap.put(name, sValue);
	}
	
	public void setOption(ModelOptionNamesEnum name, Object value)
	{
		setOption(name.optionName(), value);
	}
	

	
	public String getOption(String name)
	{
		return optionsMap.get(name);
	}
	
	public String getOption(ModelOptionNamesEnum name)
	{
		return getOption(name.optionName());
	}
	
	public boolean hasOption(String name)
	{
		return (optionsMap.containsKey(name));
	}
	
	public boolean hasOption(ModelOptionNamesEnum name)
	{
		return hasOption(name.optionName());
	}
	
	public String removeOption(String name)
	{
		return optionsMap.remove(name);
	}
	
	public String removeOption(ModelOptionNamesEnum name)
	{
		return removeOption(name.optionName());
	}
	
	public boolean getBooleanOption(String name)
	{
		if (hasOption(name))
			return Boolean.parseBoolean(getOption(name));
		else
			return false;
	}
	
	public boolean getBooleanOption(ModelOptionNamesEnum name)
	{
		return getBooleanOption(name.optionName());
	}
	
	public int getIntegerOption(String name)
	{
		if (hasOption(name))
			return Integer.parseInt(getOption(name));
		else
			return 0;
	}
	
	public int getIntegerOption(ModelOptionNamesEnum name)
	{
		return getIntegerOption(name.optionName());
	}
	
	public double getDoubleOption(String name)
	{
		if (hasOption(name))
			return Double.parseDouble(getOption(name));
		else
			return 0.0;
	}
	
	public double getDoubleOption(ModelOptionNamesEnum name)
	{
		return getDoubleOption(name.optionName());
	}
	
	public float getFloatOption(String name)
	{
		if (hasOption(name))
			return Float.parseFloat(getOption(name));
		else
			return 0.0f;
	}
	
	public float getFloatOption(ModelOptionNamesEnum name)
	{
		return getFloatOption(name.optionName());
	}
	
	public long getLongOption(String name)
	{
		if (hasOption(name))
			return Long.parseLong(getOption(name));
		else
			return 0;
	}
	
	public long getLongOption(ModelOptionNamesEnum name)
	{
		return getLongOption(name.optionName());
	}

	
	/** Synchronizes values from this object to a ProjectModel object.
	 * 
	 * @param projectModel A ProjectModel to synchronize to
	 */
	public void syncToProjectModel(ProjectModel projectModel)
	{
		for (String optionName : getOptionNames()) {
			projectModel.setOption(optionName, optionsMap.get(optionName).toString());
		}

		projectModel.setOption(ModelOptionNamesEnum.PROJECTION_ROTATE_X, projection.getRotateX());
		projectModel.setOption(ModelOptionNamesEnum.PROJECTION_ROTATE_Y, projection.getRotateY());
		projectModel.setOption(ModelOptionNamesEnum.PROJECTION_ROTATE_Z, projection.getRotateZ());
		
		projectModel.setOption(ModelOptionNamesEnum.PROJECTION_SHIFT_X, projection.getShiftX());
		projectModel.setOption(ModelOptionNamesEnum.PROJECTION_SHIFT_Y, projection.getShiftY());
		projectModel.setOption(ModelOptionNamesEnum.PROJECTION_SHIFT_Z, projection.getShiftZ());
		
		projectModel.setUserScript(getUserScript());
		projectModel.setScriptLanguage(getScriptLanguage());
		
	}
	
	/** Synchronizes values from a ProjectModel object to this object.
	 * 
	 * @param projectModel A ProjectModel to synchronize from.
	 */
	public void syncFromProjectModel(ProjectModel projectModel)
	{
		
		for (String optionName : projectModel.getOptionKeys()) {
			String optionValue = projectModel.getOption(optionName);
			if (optionValue != null) {
				this.setOption(optionName, optionValue);
			}
		}
		
		if (projectModel.getUserScript() != null) {
			this.setUserScript(projectModel.getUserScript());
		}
		
		if (projectModel.getScriptLanguage() != null) {
			this.setScriptLanguage(projectModel.getScriptLanguage());
		}
		
		this.projection.setRotateX(projectModel.getDoubleOption(ModelOptionNamesEnum.PROJECTION_ROTATE_X));
		this.projection.setRotateY(projectModel.getDoubleOption(ModelOptionNamesEnum.PROJECTION_ROTATE_Y));
		this.projection.setRotateZ(projectModel.getDoubleOption(ModelOptionNamesEnum.PROJECTION_ROTATE_Z));
		
		this.projection.setShiftX(projectModel.getDoubleOption(ModelOptionNamesEnum.PROJECTION_SHIFT_X));
		this.projection.setShiftY(projectModel.getDoubleOption(ModelOptionNamesEnum.PROJECTION_SHIFT_Y));
		this.projection.setShiftZ(projectModel.getDoubleOption(ModelOptionNamesEnum.PROJECTION_SHIFT_Z));
		
		
	}
	
	
	public String getEngine() 
	{
		return getOption(ModelOptionNamesEnum.ENGINE);
	}

	public void setEngine(String engine)
	{
		setOption(ModelOptionNamesEnum.ENGINE, engine);
	}


	public double getLightingMultiple() 
	{
		return getDoubleOption(ModelOptionNamesEnum.LIGHTING_MULTIPLE);
	}


	public void setLightingMultiple(double lightingMultiple)
	{
		setOption(ModelOptionNamesEnum.LIGHTING_MULTIPLE, lightingMultiple);
	}


	public double getRelativeLightIntensity()
	{
		return getDoubleOption(ModelOptionNamesEnum.RELATIVE_LIGHT_INTENSITY);
	}


	public void setRelativeLightIntensity(double relativeLightIntensity)
	{
		setOption(ModelOptionNamesEnum.RELATIVE_LIGHT_INTENSITY, relativeLightIntensity);
	}


	public double getRelativeDarkIntensity()
	{
		return getDoubleOption(ModelOptionNamesEnum.RELATIVE_DARK_INTENSITY);
	}


	public void setRelativeDarkIntensity(double relativeDarkIntensity)
	{
		setOption(ModelOptionNamesEnum.RELATIVE_DARK_INTENSITY, relativeDarkIntensity);
	}


	public int getSpotExponent()
	{
		return getIntegerOption(ModelOptionNamesEnum.SPOT_EXPONENT);
	}
	
	/** Sets the spot exponent for the intensity distribution of the lighting. 
	 * 
	 * @param spotExponent A value between 1.0 and 10.0 (default: 1.0)
	 */
	public void setSpotExponent(int spotExponent)
	{
		setOption(ModelOptionNamesEnum.SPOT_EXPONENT, spotExponent);
	}

	public double getLightingAzimuth()
	{
		return getDoubleOption(ModelOptionNamesEnum.LIGHTING_AZIMUTH);
	}


	public void setLightingAzimuth(double lightingAzimuth)
	{
		setOption(ModelOptionNamesEnum.LIGHTING_AZIMUTH, lightingAzimuth);
	}

	public double getLightingElevation()
	{
		return getDoubleOption(ModelOptionNamesEnum.LIGHTING_ELEVATION);
	}




	public void setLightingElevation(double lightingElevation)
	{
		setOption(ModelOptionNamesEnum.LIGHTING_ELEVATION, lightingElevation);
	}




	public int getTileSize() 
	{
		return getIntegerOption(ModelOptionNamesEnum.TILE_SIZE);
	}

	public void setTileSize(int tileSize) 
	{
		setOption(ModelOptionNamesEnum.TILE_SIZE, tileSize);
	}

	


	public String getBackgroundColor()
	{
		return getOption(ModelOptionNamesEnum.BACKGROUND_COLOR);
	}

	public Color getBackgroundColorInstance()
	{
		String colorString = getBackgroundColor();
		if (colorString != null) {
			return ColorSerializationUtil.stringToColor(colorString);
		} else {
			return null;
		}
	}


	public void setBackgroundColor(String backgroundColor)
	{
		setOption(ModelOptionNamesEnum.BACKGROUND_COLOR, backgroundColor);
	}

	public void setBackgroundColor(Color backgroundColor)
	{
		setOption(ModelOptionNamesEnum.BACKGROUND_COLOR, ColorSerializationUtil.colorToString(backgroundColor));
	}


	public boolean isHillShading() 
	{
		return getBooleanOption(ModelOptionNamesEnum.HILLSHADING);
	}


	public void setHillShading(boolean hillShading) 
	{
		setOption(ModelOptionNamesEnum.HILLSHADING, hillShading);
	}


	public int getHillShadeType()
	{
		return getIntegerOption(ModelOptionNamesEnum.HILLSHADE_TYPE);
	}


	public void setHillShadeType(int hillShadeType) 
	{
		setOption(ModelOptionNamesEnum.HILLSHADE_TYPE, hillShadeType);
	}

	public boolean getDoublePrecisionHillshading()
	{
		return getBooleanOption(ModelOptionNamesEnum.DOUBLE_PRECISION_HILLSHADING);
	}

	public void setDoublePrecisionHillshading(boolean doublePrecisionHillshading)
	{
		setOption(ModelOptionNamesEnum.DOUBLE_PRECISION_HILLSHADING, doublePrecisionHillshading);
	}
	
	public String getColoringType()
	{
		return getOption(ModelOptionNamesEnum.COLORING_TYPE);
	}


	public void setColoringType(String coloringType)
	{
		setOption(ModelOptionNamesEnum.COLORING_TYPE, coloringType);
	}


	public int getWidth() 
	{
		return getIntegerOption(ModelOptionNamesEnum.WIDTH);
	}


	public void setWidth(int width) 
	{
		setOption(ModelOptionNamesEnum.WIDTH, width);
	}


	public int getHeight()
	{
		return getIntegerOption(ModelOptionNamesEnum.HEIGHT);
	}


	public void setHeight(int height) 
	{
		setOption(ModelOptionNamesEnum.HEIGHT, height);
	}


	public int getGridSize()
	{
		return getIntegerOption(ModelOptionNamesEnum.GRID_SIZE);
	}

	public void setGridSize(int gridSize)
	{
		setOption(ModelOptionNamesEnum.GRID_SIZE, gridSize);
	}

	public double getElevationMultiple()
	{
		return getDoubleOption(ModelOptionNamesEnum.ELEVATION_MULTIPLE);
	}



	public void setElevationMultiple(double elevationMultiple)
	{
		setOption(ModelOptionNamesEnum.ELEVATION_MULTIPLE, elevationMultiple);
	}



	public String getGradientLevels()
	{
		return getOption(ModelOptionNamesEnum.GRADIENT_LEVELS);
	}

	public void setGradientLevels(String gradientLevels)
	{
		setOption(ModelOptionNamesEnum.GRADIENT_LEVELS, gradientLevels);
	}


	public String getPrecacheStrategy() 
	{
		return getOption(ModelOptionNamesEnum.PRECACHE_STRATEGY);
	}


	public void setPrecacheStrategy(String precacheStrategy) 
	{
		setOption(ModelOptionNamesEnum.PRECACHE_STRATEGY, precacheStrategy);
	}


	public boolean isAntialiased() 
	{
		return getBooleanOption(ModelOptionNamesEnum.ANTIALIASED);
	}


	public void setAntialiased(boolean antialiased) 
	{
		setOption(ModelOptionNamesEnum.ANTIALIASED, antialiased);
	}

	public boolean getUseSimpleCanvasFill()
	{
		return getBooleanOption(ModelOptionNamesEnum.USE_SIMPLE_CANVAS_FILL);
	}
	
	public void setUseSimpleCanvasFill(boolean useSimpleCanvasFill)
	{
		setOption(ModelOptionNamesEnum.USE_SIMPLE_CANVAS_FILL, useSimpleCanvasFill);
	}
	

	public String getWriteTo()
	{
		return writeTo;
	}

	public void setWriteTo(String writeTo)
	{
		this.writeTo = writeTo;
	}
	
	
	
	public Projection getProjection()
	{
		return projection;
	}


	public void setProjection(Projection projection)
	{
		this.projection = projection;
	}

	public String getUserScript()
	{
		return userScript;
	}

	public void setUserScript(String userScript)
	{
		this.userScript = userScript;
	}

	public ScriptLanguageEnum getScriptLanguage()
	{
		return scriptLanguage;
	}

	public void setScriptLanguage(ScriptLanguageEnum scriptLanguage)
	{
		this.scriptLanguage = scriptLanguage;
	}
	
	public void setScriptLanguage(String scriptLanguageString)
	{
		ScriptLanguageEnum scriptLanguage = ScriptLanguageEnum.getLanguageFromString(scriptLanguageString);
		this.setScriptLanguage(scriptLanguage);
	}
	
	public MapProjectionEnum getMapProjection()
	{
		String identifier = getOption(ModelOptionNamesEnum.MAP_PROJECTION);
		if (identifier == null) {
			return null;
		}
		
		return MapProjectionEnum.getMapProjectionEnumFromIdentifier(identifier);
		
	}
	
	public void setMapProjection(String identifier)
	{
		setOption(ModelOptionNamesEnum.MAP_PROJECTION, identifier);
	}
	
	public void setMapProjection(MapProjectionEnum projectionEnum)
	{
		setMapProjection(projectionEnum.identifier());
	}
	
	
	
	/** Creates a value-by-value copy of this object.
	 * 
	 * @return A value-by-value copy of this object.
	 */
	public ModelOptions copy()
	{
		ModelOptions clone = new ModelOptions();
		
		for (String optionName : getOptionNames()) {
			clone.setOption(optionName, optionsMap.get(optionName).toString());
		}
		
		
		if (this.writeTo != null) {
			clone.writeTo = this.writeTo.toString();
		}
		
		if (projection != null) {
			clone.projection = this.projection.copy();
		}
		
		if (scriptLanguage != null) {
			clone.scriptLanguage = this.scriptLanguage;
		}
		
		if (userScript != null) {
			clone.userScript = this.userScript.toString();
		}
		
		return clone;
	}
}
