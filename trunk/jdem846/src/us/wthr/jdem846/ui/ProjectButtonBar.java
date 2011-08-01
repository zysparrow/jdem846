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

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.i18n.I18N;

@SuppressWarnings("serial")
public class ProjectButtonBar extends JToolBar
{
	public static final int BTN_ADD = 0;
	public static final int BTN_REMOVE = 1;
	public static final int BTN_CREATE = 2;
	
	private List<ButtonClickedListener> buttonClickedListeners = new LinkedList<ButtonClickedListener>();
	
	private ToolbarButton jbtnAdd;
	private ToolbarButton jbtnRemove;
	private ToolbarButton jbtnCreate;
	
	public ProjectButtonBar()
	{
		// Create components
		
		
		jbtnAdd = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.projectButtonBar.addButton"), JDem846Properties.getProperty("us.wthr.jdem846.icons.16x16") + "/list-add.png", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(BTN_ADD);
			}
		});
		
		jbtnRemove = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.projectButtonBar.removeButton"), JDem846Properties.getProperty("us.wthr.jdem846.icons.16x16") + "/list-remove.png", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(BTN_REMOVE);
			}
		});
		
		jbtnCreate = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.projectButtonBar.createButton"), JDem846Properties.getProperty("us.wthr.jdem846.icons.16x16") + "/stock_update-data.png", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(BTN_CREATE);
			}
		});

		// Set tooltips
		jbtnAdd.setToolTipText(I18N.get("us.wthr.jdem846.ui.projectButtonBar.addTooltip"));
		jbtnRemove.setToolTipText(I18N.get("us.wthr.jdem846.ui.projectButtonBar.removeTooltip"));
		jbtnCreate.setToolTipText(I18N.get("us.wthr.jdem846.ui.projectButtonBar.createTooltip"));
		

		this.setMargin(new Insets(3, 3, 3, 3));
		
		// Create layout
		add(jbtnAdd);
		add(jbtnRemove);
		addSeparator();
		add(jbtnCreate);
	}
	
	public void setButtonEnabled(int button, boolean enabled)
	{
		switch(button) {
		case BTN_ADD:
			jbtnAdd.setEnabled(enabled);
			break;
		case BTN_REMOVE:
			jbtnRemove.setEnabled(enabled);
			break;
		case BTN_CREATE:
			jbtnCreate.setEnabled(enabled);
			break;
		}
	}
	
	
	public void addButtonClickedListener(ButtonClickedListener listener)
	{
		buttonClickedListeners.add(listener);
	}
	
	protected void fireButtonClickedListeners(int button)
	{
		for (ButtonClickedListener listener : buttonClickedListeners) {
			switch(button) {
			case BTN_ADD:
				listener.onAddClicked();
				break;
			case BTN_REMOVE:
				listener.onRemoveClicked();
				break;
			case BTN_CREATE:
				listener.onCreateClicked();
				break;
			}
		}
	}
	
	public interface ButtonClickedListener
	{
		public void onAddClicked();
		public void onRemoveClicked();
		public void onCreateClicked();
	}
	
}
