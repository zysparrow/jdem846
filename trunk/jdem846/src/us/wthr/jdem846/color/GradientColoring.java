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

package us.wthr.jdem846.color;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import us.wthr.jdem846.exception.GradientLoadException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class GradientColoring implements ModelColoring
{
	private static Log log = Logging.getLog(GradientColoring.class);
	
	
	private static final int UNITS_PERCENT = 0;
	private static final int UNITS_METERS = 10;
	
	private String name;
	private String identifier;
	private boolean needsMinMaxElevation;
	private int units;

	private static DemColor defaultColor = new DemColor(0, 0, 0, 0xFF);
	private GradientLoader gradient;
	private String configFile = null;
	private GradientColorStop[] colorStops = null;
	                                       
	public GradientColoring(String configFile) throws GradientLoadException
	{
		this.configFile = configFile;
		reset();
	}
	

	@Override
	public void reset() throws GradientLoadException
	{
		//URL url = this.getClass().getResource(configFile);
		try {
			gradient = GradientLoader.loadGradient(new File(configFile));
		} catch (Exception ex) {
			throw new GradientLoadException(configFile, "Invalid gradient file location: " + ex.getMessage(), ex);
		}
		this.name = gradient.getName();
		this.identifier = gradient.getIdentifier();
		this.needsMinMaxElevation = gradient.needsMinMaxElevation();
		if (gradient.getUnits().equalsIgnoreCase("percent")) {
			this.units = UNITS_PERCENT;
		} else if (gradient.getUnits().equalsIgnoreCase("meters")) {
			this.units = UNITS_METERS;
		} else {
			throw new GradientLoadException(configFile, "Unsupported unit of measurement: " + gradient.getUnits());
		}
		
		colorStops = new GradientColorStop[gradient.getColorStops().size()];
		gradient.getColorStops().toArray(colorStops);
	}
	
	@Override
	public GradientLoader getGradientLoader()
	{
		return gradient;
	}
	
	
	
	public String getName()
	{
		return name;
	}


	public String getIdentifier()
	{
		return identifier;
	}


	public boolean needsMinMaxElevation()
	{
		return needsMinMaxElevation;
	}


	public String getUnits()
	{
		return gradient.getUnits();
	}


	@Override
	public void getColorByMeters(double meters, int[] color) 
	{
		GradientColorStop lower = null;
		GradientColorStop upper = null;
		
		for (GradientColorStop stop : colorStops) {
			if (stop.getPosition() <= meters) {
				lower = stop;
			}
			if (stop.getPosition() >= meters) {
				upper = stop;
				break;
			}
		}
		
		if (upper == null)
			upper = lower;
		
		if (lower == null)
			lower = upper;
		
		if (upper == null && lower == null) {
			defaultColor.toList(color);
			return;
			//return defaultColor.getCopy();
		}
		
		
		double color_ratio = (meters - lower.getPosition()) / (upper.getPosition() - lower.getPosition());
		if (Double.isNaN(color_ratio))
			color_ratio = 1.0;

		double red = (lower.getColor().getRed() * (1.0 - color_ratio)) + (upper.getColor().getRed() * color_ratio);
		double green = (lower.getColor().getGreen() * (1.0 - color_ratio)) + (upper.getColor().getGreen() * color_ratio);
		double blue = (lower.getColor().getBlue() * (1.0 - color_ratio)) + (upper.getColor().getBlue() * color_ratio);

		color[0] = (int)Math.round((red * 0xFF));
		color[1] = (int)Math.round((green * 0xFF));
		color[2] = (int)Math.round((blue * 0xFF));
		color[3] = 0xFF;
		
		
	}
	
	@Override
	public void getColorByPercent(double ratio, int[] color) 
	{
		
		if (ratio < 0 || ratio > 1) {
			defaultColor.toList(color);
			return;
			//return defaultColor.getCopy();
		}
		
		GradientColorStop lower = null;
		GradientColorStop upper = null;
		
		for (GradientColorStop stop : colorStops) {
			if (stop.getPosition() <= ratio) {
				lower = stop;
			}
			if (stop.getPosition() >= ratio) {
				upper = stop;
				break;
			}
		}
		
		if (upper == null)
			upper = lower;
		
		if (lower == null)
			lower = upper;

		if (upper == null && lower == null) {
			defaultColor.toList(color);
			return;
			//return defaultColor.getCopy();
		}
		
		
		if (ratio == 0.0f || (upper.getPosition() - lower.getPosition()) == 0.0f) {
			lower.getColor().toList(color);
			return;
			//return lower.getColor().getCopy();
		}
		
		double color_ratio = (ratio - lower.getPosition()) / (upper.getPosition() - lower.getPosition());
		
		double red = (lower.getColor().getRed() * (1.0 - color_ratio)) + (upper.getColor().getRed() * color_ratio);
		double green = (lower.getColor().getGreen() * (1.0 - color_ratio)) + (upper.getColor().getGreen() * color_ratio);
		double blue = (lower.getColor().getBlue() * (1.0 - color_ratio)) + (upper.getColor().getBlue() * color_ratio);

		color[0] = (int)Math.round((red * 0xFF));
		color[1] = (int)Math.round((green * 0xFF));
		color[2] = (int)Math.round((blue * 0xFF));
		color[3] = 0xFF;
		//return new DemColor(red, green, blue, 0xFF);
	}

	@Override
	public void getGradientColor(float elevation, float minElevation, float maxElevation, int[] color) 
	{
		if (units == UNITS_PERCENT) {
			double ratio = (elevation - minElevation) / (maxElevation - minElevation);
			
			if (ratio <= 0)
				ratio = .001;
			
			getColorByPercent(ratio, color);
		} else if (units == UNITS_METERS) {
			getColorByMeters(elevation, color);
		}
		
		
		//return getColor(ratio);
	}
	
	
	public double getMinimumSupported()
	{
		if (units == UNITS_PERCENT) {
			return 0.0;
		} else if (units == UNITS_METERS) {
			return colorStops[0].getPosition();
		} else {
			return 0.0;
		}
	}
	
	public double getMaximumSupported()
	{
		if (units == UNITS_PERCENT) {
			return 1.0;
		} else if (units == UNITS_METERS) {
			return colorStops[colorStops.length - 1].getPosition();
		} else {
			return 0;
		}
	}
	
}