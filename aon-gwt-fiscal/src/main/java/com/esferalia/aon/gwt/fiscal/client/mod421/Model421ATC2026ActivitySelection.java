package com.esferalia.aon.gwt.fiscal.client.mod421;

import java.util.Arrays;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.occam.api.model.fiscal.modules.ModulesCanarias2026.EpigraphCanarias;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class Model421ATC2026ActivitySelection extends ScrollPanel implements HasSelectionHandlers<EpigraphCanarias>{

	protected Model421ATC2026ActivitySelection() {
		setStyleName(AON.CSS.aonScrollArea());
		
		AonDisplayGrid table = new AonDisplayGrid();
		table.addStyleName(AON.CSS.aonWidthAll());
		table.addHeaderRow()
			.addCell( new Label(AON.MSG.code()))
			.addCell( new Label(AON.MSG.description()));
		
		Arrays.stream(EpigraphCanarias.values())
			.forEach(epi -> {
				AonDisplayGridRow row = table.addRow();
				row.addStyleName(AON.CSS.aonClickable());
				row.addCell( new Label(epi.getEpigraph()), AON.CSS.aonBold())
				   .addCell( new Label(epi.getDescription()), AON.CSS.aonFlexGrow1(), AON.CSS.aonWrap());
				row.addClickHandler(event -> {
					SelectionEvent.fire(Model421ATC2026ActivitySelection.this, epi);
				});	
			});
		
		this.setWidget(table);
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<EpigraphCanarias> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

}
