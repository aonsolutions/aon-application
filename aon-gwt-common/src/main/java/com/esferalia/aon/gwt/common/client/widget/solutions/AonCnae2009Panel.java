package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.occam.api.model.type.CNAE2009;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class AonCnae2009Panel extends AonCustomDialog implements HasSelectionHandlers<CNAE2009>{

	public AonCnae2009Panel() {
		setVisible(false);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		setModal(true);
		setCaption("C.N.A.E. 2009");
		showCloseButton(true);

		ScrollPanel scroll = new ScrollPanel();
		scroll.setWidth("550px");
		scroll.setHeight("500px");
		scroll.setStyleName(AON.CSS.aonPadding());
		
		AonDisplayGrid table = new AonDisplayGrid();
		table.addStyleName(AON.CSS.aonWidthAll());
		table.addHeaderRow()
			.addCell( new Label(AON.MSG.code()))
			.addCell( new Label(AON.MSG.description()));

		for (CNAE2009 cnae :  CNAE2009.values()) {
			AonDisplayGridRow row = table.addRow();
			row.addStyleName(AON.CSS.aonClickable());
			row.addCell( new Label(cnae.getCode()), AON.CSS.aonBold())
			   .addCell( new Label(cnae.getDescription()), AON.CSS.aonFlexGrow1(), AON.CSS.aonWrap());
			row.addClickHandler(event -> {
				hide();
				SelectionEvent.fire(AonCnae2009Panel.this, cnae );
			});	
		}
		scroll.setWidget(table);
		this.setWidget(scroll);
	}
	
	public void onShow() {
		center();
		show();
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<CNAE2009> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

}
