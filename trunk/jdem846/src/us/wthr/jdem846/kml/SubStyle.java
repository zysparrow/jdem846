package us.wthr.jdem846.kml;

import org.dom4j.Element;

public class SubStyle extends KmlElement
{
	
	
	
	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("SubStyle");
		loadKmlChildren(element);
	}
}
