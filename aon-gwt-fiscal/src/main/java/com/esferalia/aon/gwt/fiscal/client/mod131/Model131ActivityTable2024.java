package com.esferalia.aon.gwt.fiscal.client.mod131;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.fiscal.client.mod131.Model1312024Activities.IModel131ActivitiesCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

class Model131ActivityTable2024 extends FlowPanel implements HasSelectionHandlers<Mod131Activity> {
	
	private Integer selectedIndex;
	private AonDisplayGrid grid;
	
	protected Model131ActivityTable2024(IModel131ActivitiesCallback callback) {
		setStyleName(AON.CSS.aonWidthAll());
		
		grid = new AonDisplayGrid();
		grid.addStyleName(AON.CSS.aonNoPadding());
		grid.addStyleName(AON.CSS.aonBlockCenter());
		grid.addStyleName(AON.CSS.aonWidthAlmostAll());
		add(grid);
		
		paint(callback);
	}
	
	protected void paint( IModel131ActivitiesCallback callback ) {
		grid.clear();
		
		grid.addHeaderRow().addCell( new Label( AON.MSG.activity() ) );
		
		int i = 1;
		for (Mod131Activity act : callback.getModel().getActivities()) {
			final int currentIndex = i - 1;
			act.setIndex(currentIndex);
			AonDisplayGridRow actRow = grid.addRow();
			if (AonNumberUtils.equals(selectedIndex , currentIndex)) {
				actRow.addStyleName(AON.CSS.aonBackgroundLigthBlue());
			}
			String label = AonStringUtils.abbreviate(act.getFullDescription(), 60);
			if (AonStringUtils.isBlank(label)) {
				label = "Actividad en estimaci\u00F3n objetiva n\u00AA " + i;				
			}
			actRow.addCell( new Label( label ), AON.CSS.aonFlexGrow1() );
			actRow.addClickHandler(event -> {
				clearSelectedIndex();
				selectedIndex = currentIndex;
				actRow.addStyleName(AON.CSS.aonBackgroundLigthBlue());
				SelectionEvent.<Mod131Activity>fire(Model131ActivityTable2024.this, act);
			});
			i++;
		}
		
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Mod131Activity> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	public Integer getSelectedIndex() {
		return selectedIndex;
	}
	public void clearSelectedIndex() {
		selectedIndex = null;
		for ( int i = 0; i < grid.getWidgetCount(); i++) {
			grid.getWidget(i).removeStyleName(AON.CSS.aonBackgroundLigthBlue());
		}
	}
}
