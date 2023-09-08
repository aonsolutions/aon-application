package net.aonsolutions.aon.gwt.warehouse.client.elaboration.nuevo;

import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSearchBox;
import com.google.gwt.user.client.ui.SimplePanel;

import net.aonsolutions.aon.gwt.warehouse.shared.ElaborationParams;

public class ElaborationEntryPoint extends AonTemplate2 {

	@Override
	public void onModuleLoad() {
		super.onModuleLoad();
		load();
	}

	private void load() {
		loadToolbar();
		loadContent();
	}

	private void loadToolbar() {
		getDockLayoutPanel().setWidgetSize(getToolbar(), 50);
		AonToolbar toolbar = new AonToolbar("Elaboraciones");
		
		AonToolbarSearchBox searchBox = new AonToolbarSearchBox() {
			
			@Override
			public void onValueChange(String value) {

			}
		};
		toolbar.showSearchPanel(searchBox);
		setToolbar(toolbar);
	}

	public void loadContent() {
		setContent(new ElaborationGrid(getOccam(), new ElaborationParams()));
	}
	
	
}
