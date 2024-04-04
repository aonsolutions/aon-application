package com.esferalia.aon.gwt.fiscal.client.mod131;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.occam.api.model.fiscal.modules.IEpigraph;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2018.Epigraph;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class EpigraphPanel extends AonCustomDialog implements HasSelectionHandlers<IEpigraph> {

	protected EpigraphPanel(Integer year) {
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

		LinkedList<IEpigraph> epigraphs = new LinkedList<IEpigraph>();
		if (year < 2018) {
			for (com.esferalia.aon.occam.api.model.fiscal.modules.Modules2016.Epigraph epigraph 
				: com.esferalia.aon.occam.api.model.fiscal.modules.Modules2016.Epigraph.values()) {
				if (epigraph.hasIRPFModules()) {
					epigraphs.add(epigraph);
				}
			}
		} else {
			for (Epigraph epigraph : Epigraph.values()) {
				if (epigraph.hasIRPFModules()) {
					epigraphs.add(epigraph);
				}
			}
		}

		for (IEpigraph epi : epigraphs) {
			AonDisplayGridRow row = table.addRow();
			row.addStyleName(AON.CSS.aonClickable());
			row.addCell( new Label(epi.getEpigraph()), AON.CSS.aonBold())
			   .addCell( new Label(epi.getDescription()), AON.CSS.aonFlexGrow1(), AON.CSS.aonWrap());
			row.addClickHandler(event -> {
				hide();
				SelectionEvent.fire(EpigraphPanel.this, epi);
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
	public HandlerRegistration addSelectionHandler(SelectionHandler<IEpigraph> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

}
