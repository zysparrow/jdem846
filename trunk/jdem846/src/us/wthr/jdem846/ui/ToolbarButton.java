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

package us.wthr.jdem846.ui;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.JButton;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.util.ImageIcons;

@SuppressWarnings("serial")
public class ToolbarButton extends JButton
{
	private static Log log = Logging.getLog(ToolbarButton.class);
	
	private boolean textIsDisplayed = true;
	private boolean mouseIsOver = false;
	private String text;
	
	public ToolbarButton(String text, String iconPath, ActionListener actionListener)
	{
		super(text);
		try {
			setIcon(ImageIcons.loadImageIcon(iconPath));
		} catch (IOException ex) {
			log.warn("Failed to load icon: " + ex.getMessage(), ex);
		}
		
		this.addActionListener(actionListener);
		
		this.setMargin(new Insets(3, 3, 3, 3));
		
		textIsDisplayed = true;
		addMouseHandlers();
	}
	
	
	public ToolbarButton(String iconPath, ActionListener actionListener)
	{
		try {
			setIcon(ImageIcons.loadImageIcon(iconPath));
		} catch (IOException ex) {
			log.warn("Failed to load icon: " + ex.getMessage(), ex);
		}
		
		this.addActionListener(actionListener);
		
		textIsDisplayed = false;
		addMouseHandlers();
		setMouseOutStyle();
	}

	protected void setMouseOverStyle()
	{
		this.setOpaque(true);
		this.setBorderPainted(true);
	}
	
	protected void setMouseOutStyle()
	{
		this.setOpaque(false);
		this.setBorderPainted(false);
	}
	
	protected void addMouseHandlers()
	{
		this.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e)
			{
				mouseIsOver = true;
			}
			public void mouseExited(MouseEvent e)
			{
				mouseIsOver = false;
			}
		});
	}
	
	@Override
	public void paint(Graphics g)
	{
		if (mouseIsOver || textIsDisplayed) {
			setMouseOverStyle();
		} else {
			setMouseOutStyle();
		}
		super.paint(g);
	}
	
	
	public boolean isTextDisplayed()
	{
		return textIsDisplayed;
	}


	public void setTextDisplayed(boolean textIsDisplayed)
	{
		this.textIsDisplayed = textIsDisplayed;
		
		if (textIsDisplayed) {
			super.setText(text);
		} else {
			super.setText(null);
		}
	}

	
	
	
	
}
