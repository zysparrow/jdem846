package us.wthr.jdem846.ui.options;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.OptionModelChangeEvent;
import us.wthr.jdem846.model.OptionModelChangeListener;
import us.wthr.jdem846.model.OptionModelContainer;
import us.wthr.jdem846.model.exceptions.InvalidProcessOptionException;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.model.processing.ModelProcessRegistry;
import us.wthr.jdem846.model.processing.ProcessInstance;
import us.wthr.jdem846.ui.base.ComboBox;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.ScrollPane;

@SuppressWarnings("serial")
public class ProcessTypeConfigurationPanel extends Panel
{
	private static Log log = Logging.getLog(ProcessTypeConfigurationPanel.class);
	
	private GridProcessingTypesEnum processType;
	
	private ProcessTypeListModel processTypeListModel;
	private ComboBox cmbProcessSelection;
	
	private List<OptionModel> providedOptionModelList = new LinkedList<OptionModel>();
	
	private String currentProcessId;
	private OptionModel currentOptionModel;
	private OptionModelContainer currentOptionModelContainer;
	private DynamicOptionsPanel currentOptionsPanel;
	private ScrollPane currentScrollPane;
	
	private OptionModelChangeListener propertyChangeListener;
	
	private List<ChangeListener> changeListeners = new LinkedList<ChangeListener>();
	
	public ProcessTypeConfigurationPanel(GridProcessingTypesEnum processType, String initialSelection)
	{
		this(processType, initialSelection, null);
	}
	
	public ProcessTypeConfigurationPanel(GridProcessingTypesEnum processType, String initialSelection, List<OptionModel> providedOptionModelList)
	{
		this.processType = processType;
		if (providedOptionModelList != null) {
			this.providedOptionModelList.addAll(providedOptionModelList);
		}
		
		processTypeListModel = new ProcessTypeListModel(processType);
		cmbProcessSelection = new ComboBox(processTypeListModel);
		
		
		
		propertyChangeListener = new OptionModelChangeListener() {
			public void onPropertyChanged(OptionModelChangeEvent e)
			{
				log.info("Property change for " + e.getPropertyName() + " from " + e.getOldValue() + " to " + e.getNewValue());
			}
		};
		
		
		ItemListener comboBoxItemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					onProcessSelectionChanged(processTypeListModel.getSelectedItemValue());
				}
					
			}
		};
		cmbProcessSelection.addItemListener(comboBoxItemListener);
		
		
		
		// Set Layout
		setLayout(new BorderLayout());
		
		add(cmbProcessSelection, BorderLayout.NORTH);
		
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		if (initialSelection != null) {
			processTypeListModel.setSelectedItemByValue(initialSelection);
			onProcessSelectionChanged(initialSelection);
		}
	}
	
	protected void onProcessSelectionChanged(String processId)
	{
		ProcessInstance processInstance = ModelProcessRegistry.getInstance(processId);
		
		if (processInstance != null) {
			log.info("Process Selected: " + processInstance.getId());
			this.currentProcessId = processInstance.getId();
			
			buildOptionsPanel(processInstance.getOptionModelClass());
			
		} else {
			log.info("Process not found with id " + processId);
		}
		
		
		
	}
	
	protected void buildOptionsPanel(Class<?> optionModelClass)
	{
		
		log.info("Building option panel for " + optionModelClass.getName());
		
		if (currentOptionModelContainer != null) {
			currentOptionModelContainer.removeOptionModelChangeListener(propertyChangeListener);
			currentOptionModelContainer = null;
		}
		
		if (currentOptionsPanel != null) {
			remove(currentOptionsPanel);
			currentOptionsPanel = null;
		}
		
		if (currentScrollPane != null) {
			remove(currentScrollPane);
			currentScrollPane = null;
		}
		
		currentOptionModel = getProvidedOptionModel(optionModelClass);
		if (currentOptionModel == null) {
			try {
				currentOptionModel = (OptionModel) optionModelClass.newInstance();
			} catch (Exception ex) {
				// TODO: Display error dialog
				log.error("Error creating instance of option model: " + ex.getMessage(), ex);
				return;
			}
		}
		
		currentOptionModelContainer = null;
		try {
			currentOptionModelContainer = new OptionModelContainer(currentOptionModel);
		} catch (InvalidProcessOptionException ex) {
			// TODO: Show error dialog
			log.error("Error creating option model container: " + ex.getMessage(), ex);
			return;
		}
		
		
		
		currentOptionModelContainer.addOptionModelChangeListener(propertyChangeListener);
		
		currentOptionsPanel = new DynamicOptionsPanel(currentOptionModelContainer);
		currentOptionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		currentScrollPane = new ScrollPane(currentOptionsPanel);
		add(currentScrollPane, BorderLayout.CENTER);
		
		this.validate();
	}

	protected OptionModel getProvidedOptionModel(Class<?> clazz)
	{
		
		for (OptionModel optionModel : this.providedOptionModelList) {
			if (optionModel.getClass().equals(clazz)) {
				return optionModel;
			}
		}
		
		return null;
		
	}
	
	
	public OptionModel getCurrentOptionModel()
	{
		return currentOptionModel;
	}

	public OptionModelContainer getCurrentOptionModelContainer()
	{
		return currentOptionModelContainer;
	}
	
	public String getCurrentProcessId()
	{
		return currentProcessId;
	}
	
	
	public void fireChangeListener()
	{
		
		ChangeEvent e = new ChangeEvent(this);
		
		for (ChangeListener listener : changeListeners) {
			listener.stateChanged(e);
		}
		
	}
	
	
	public void addChangeListener(ChangeListener listener)
	{
		this.changeListeners.add(listener);
	}
	
	
	public boolean removeChangeListener(ChangeListener listener)
	{
		return changeListeners.remove(listener);
	}
	
}