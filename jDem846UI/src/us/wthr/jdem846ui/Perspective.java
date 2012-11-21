package us.wthr.jdem846ui;


import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import us.wthr.jdem846ui.views.LogConsoleView;
import us.wthr.jdem846ui.views.PreviewView;
import us.wthr.jdem846ui.views.RenderedModelListView;
import us.wthr.jdem846ui.views.RenderedModelPropertiesView;
import us.wthr.jdem846ui.views.ScriptEditorView;
import us.wthr.jdem846ui.views.data.DataView;
import us.wthr.jdem846ui.views.modelconfig.ModelConfigurationView;

public class Perspective implements IPerspectiveFactory {

	/**
	 * The ID of the perspective as specified in the extension.
	 */
	public static final String ID = "jDem846UI.perspective";
	
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		
		
		IFolderLayout topLeftFolder = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f, editorArea);
		topLeftFolder.addView(DataView.ID);
	
		
		IFolderLayout bottomLeftFolder = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.70f, "topLeft");
		bottomLeftFolder.addView(RenderedModelListView.ID);
		
		//layout.addStandaloneView(DataView.ID,  false, IPageLayout.LEFT, 0.25f, editorArea);
		//layout.addStandaloneView(DataView.ID,  false, IPageLayout.LEFT, 0.25f, editorArea);
		//layout.addStandaloneView(RenderedModelListView.ID,  false, IPageLayout., 0.25f, editorArea);
		
		IFolderLayout rightFolder = layout.createFolder("right", IPageLayout.RIGHT, 0.60f, editorArea);
		rightFolder.addView(ModelConfigurationView.ID);
		rightFolder.addView(RenderedModelPropertiesView.ID);
		
		
		IFolderLayout centerFolder = layout.createFolder("center", IPageLayout.TOP, 0.75f, editorArea);
		centerFolder.addView(PreviewView.ID);
		centerFolder.addView(ScriptEditorView.ID);
		
		IFolderLayout bottomFolder = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.25f, editorArea);
		bottomFolder.addView(LogConsoleView.ID);
		
		layout.getViewLayout(PreviewView.ID).setCloseable(false);
		layout.getViewLayout(ScriptEditorView.ID).setCloseable(false);
		layout.getViewLayout(DataView.ID).setCloseable(false);
		layout.getViewLayout(ModelConfigurationView.ID).setCloseable(false);
		layout.getViewLayout(RenderedModelPropertiesView.ID).setCloseable(false);
		layout.getViewLayout(RenderedModelListView.ID).setCloseable(false);
		
	}

}
