package com.esferalia.aon.gwt.fiscal.client.mod421;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.fiscal.client.mod421.Model421ATC2026SimplifiedRegimeActivities.IModel421ATCSimplifiedRegimeCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod421Activity;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

class Model421ATC2026ActivityTable extends FlowPanel implements HasSelectionHandlers<Mod421Activity> {
	
	private AonDisplayGrid grid;
	private Integer selectedIndex;
	private final IModel421ATCSimplifiedRegimeCallback callback;
	
	protected Model421ATC2026ActivityTable(IModel421ATCSimplifiedRegimeCallback callback) {
		setStyleName(AON.CSS.aonWidthAll());
		this.callback = callback;
		grid = new AonDisplayGrid();
		grid.addStyleName(AON.CSS.aonNoPadding());
		grid.addStyleName(AON.CSS.aonBlockCenter());
		grid.addStyleName(AON.CSS.aonWidthAlmostAll());

		add(grid);
		paint();
	}
	
	protected void paint( ) {
		grid.clear();
		int i = 1;
		for (Mod421Activity act : callback.getModel().getActivityList()) {
			final int currentIndex = i - 1;
			act.setIndex(currentIndex);
			AonDisplayGridRow actRow = grid.addRow();
			if (AonNumberUtils.equals(selectedIndex , currentIndex)) {
				actRow.addStyleName(AON.CSS.aonBackgroundLigthBlue());
			}
			String label = AonStringUtils.abbreviate(act.getFullDescription(), 60);
			if (AonStringUtils.isBlank(label)) {
				label = "Actividad en r\u00E9gimen simplificado n\u00BA " + i;				
			}
			actRow.addCell( new Label( label ), AON.CSS.aonFlexGrow1() );
			actRow.addClickHandler(event -> {
				if (callback.getModel().isEditable() || act.isNotEmpty()) {
					clearSelectedIndex();
					selectedIndex = currentIndex;
					actRow.addStyleName(AON.CSS.aonBackgroundLigthBlue());
					SelectionEvent.<Mod421Activity>fire(Model421ATC2026ActivityTable.this, act);
				}
			});
			i++;
		}
		
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

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Mod421Activity> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}	
}
