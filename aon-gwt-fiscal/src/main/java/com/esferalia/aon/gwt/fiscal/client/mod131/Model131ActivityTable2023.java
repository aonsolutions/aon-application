package com.esferalia.aon.gwt.fiscal.client.mod131;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

class Model131ActivityTable2023 extends FlowPanel implements HasSelectionHandlers<Mod131Activity> {
	
	private AonDisplayGrid grid;
	
	protected Model131ActivityTable2023() {
		setStyleName(AON.CSS.aonWidthAll());
		
		grid = new AonDisplayGrid();
		grid.addStyleName(AON.CSS.aonNoPadding());
		grid.addStyleName(AON.CSS.aonBlockCenter());
		grid.addStyleName(AON.CSS.aonWidthAlmostAll());

		add(grid);
	}
	
	protected void paint( LinkedList<Mod131Activity> activities ) {
		grid.clear();
		Label actLabel = new Label( AON.MSG.activity() );
		Label labelC = new Label( AON.MSG.netYield());
		Label labelE = new Label( AON.MSG.percent());
		Label labelI = new Label( AON.MSG.result());
		grid.addHeaderRow()
			.addCell( actLabel )
			.addCell( labelC , AON.CSS.aonTextRight(),AON.CSS.aonWidth80())
			.addCell( labelE , AON.CSS.aonTextRight(),AON.CSS.aonWidth80())
			.addCell( labelI , AON.CSS.aonTextRight(),AON.CSS.aonWidth80())
		;
		
		for (Mod131Activity act : activities) {
			AonDisplayGridRow actRow = grid.addRow();
			actRow
				.addCell( new Label( AonStringUtils.abbreviate(act.getFullDescription(), 100) ), AON.CSS.aonFlexGrow1() )
				.addCell( new Label( AON.FMT.format(act.getNet()) ), AON.CSS.aonTextRight())
				.addCell( new Label( AON.FMT.format(act.getPor()) ), AON.CSS.aonTextRight())
				.addCell( new Label( AON.FMT.format(act.getRes()) ), AON.CSS.aonTextRight())
			;
			actRow.addClickHandler(event -> SelectionEvent.<Mod131Activity>fire(Model131ActivityTable2023.this, act) );
		}
		
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Mod131Activity> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}	
}
