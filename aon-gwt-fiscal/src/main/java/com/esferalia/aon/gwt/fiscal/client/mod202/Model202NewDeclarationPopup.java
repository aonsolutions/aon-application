package com.esferalia.aon.gwt.fiscal.client.mod202;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.gwt.fiscal.client.model.NewDeclarationPopup;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class Model202NewDeclarationPopup extends NewDeclarationPopup<Mod202>{

	public Model202NewDeclarationPopup(IFiscalModelCallback<Mod202> callback) {
		super(callback);
	}
	
	protected void paintModelSpecificPanel() {
		tab.setWidget(row, 0, new Label(Mod202Key.X00.getDescription()));
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		
		final ListBox r21Box = new ListBox();
		r21Box.setWidth("350px");
		r21Box.addItem(AON.MSG.calculation0(), "0");
		r21Box.addItem(AON.MSG.calculation1(), "1");
		r21Box.addItem(AON.MSG.calculation2(), "2");
		callback.getFiscalModel().putAmount(Mod202Key.X00, 0); 
		r21Box.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				callback.getFiscalModel().putAmount(Mod202Key.X00, r21Box.getSelectedIndex());
			}
		});
		tab.setWidget(row, 1, r21Box);
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
	}
	
}
