package com.esferalia.aon.gwt.fiscal.client.mod303;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2018.FarmerIVA;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class Model303AEAT2023ActivityFarmerSelection extends ScrollPanel implements HasSelectionHandlers<FarmerIVA>{

	Model303AEAT2023ActivityFarmerSelection() {
		setStyleName(AON.CSS.aonScrollArea());
		
		AonDisplayGrid table = new AonDisplayGrid();
		table.addStyleName(AON.CSS.aonWidthAll());
		table.addHeaderRow()
			.addCell( new Label(AON.MSG.code()))
			.addCell( new Label(AON.MSG.description()));

		for (FarmerIVA epi : FarmerIVA.values()) {
			AonDisplayGridRow row = table.addRow();
			row.addStyleName(AON.CSS.aonClickable());
			row.addCell( new Label(epi.getCode()), AON.CSS.aonBold())
			   .addCell( new Label(epi.getDescription()), AON.CSS.aonFlexGrow1(), AON.CSS.aonWrap());
			row.addClickHandler(event -> {
				SelectionEvent.fire(Model303AEAT2023ActivityFarmerSelection.this, epi);
			});	
		}
		this.setWidget(table);
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<FarmerIVA> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

}
