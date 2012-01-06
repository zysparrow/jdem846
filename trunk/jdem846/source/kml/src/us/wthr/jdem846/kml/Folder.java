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

package us.wthr.jdem846.kml;

import java.util.LinkedList;
import java.util.List;

import org.dom4j.Element;

public class Folder extends Container
{
	
	
	
	
	public Folder()
	{
		
	}
	
	public Folder(String name)
	{
		setName(name);
	}
	
	public Folder(String name, String description)
	{
		setName(name);
		setDescription(description);
	}
	
	
	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("Folder");
		loadKmlChildren(element);
	}

}