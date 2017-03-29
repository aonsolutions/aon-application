package com.esferalia.aon.gwt.fiscal.client.mod202;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.gwt.fiscal.client.model.NewDeclarationPopup;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class Model202NewDeclarationPopup extends NewDeclarationPopup<Mod202>{

	private class PeriodListBox extends ListBox {

		private PeriodListBox() {
			setWidth("90px");
			addItem( "1\u00BA Periodo.", Integer.toString( Period.T1.ordinal() ) );
			addItem( "2\u00BA Periodo.", Integer.toString( Period.T2.ordinal() ) );
			addItem( "3\u00BA Periodo.", Integer.toString( Period.T3.ordinal() ) );
		}

		public Period getValue() {
			if (getSelectedIndex() == 0) return null;
			return Period.values()[ AonNumberUtils.toInteger( getSelectedValue() ) ];
		}
	}

	
	public Model202NewDeclarationPopup(IFiscalModelCallback<Mod202> callback) {
		super(callback);
	}
	
	protected void paintAdministration() {
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.administration()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		callback.getFiscalModel().setAdministration( Administration.COMMON_TERRITORY );
		replacement.setVisible(callback.getFiscalModel().isReplacementDeclarationAvailable());
		complementary.setVisible(callback.getFiscalModel().isComplementaryDeclarationAvailable());
		previousLabel.setVisible(callback.getFiscalModel().isReplacedNumberAvailable());
		previous.setVisible(callback.getFiscalModel().isReplacedNumberAvailable());
		tab.setWidget(row, 1, new Label(Administration.COMMON_TERRITORY.getDescription()));
		row++;
	}
	
	protected void paintPeriod() {
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.period()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		final PeriodListBox periodList = new PeriodListBox();
		if (callback.getFiscalModel().getPeriod() != null) {
			for (int i = 0; i < periodList.getItemCount(); i++) {
				Integer value = AonNumberUtils.toInteger(periodList.getValue(i));
				if (value != null && callback.getFiscalModel().getPeriod().ordinal() == value) {
					periodList.setSelectedIndex(i);
					break;
				}
			}
		}
		periodList.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				callback.getFiscalModel().setPeriod( periodList.getValue() );
			}
		});
		tab.setWidget(row, 1, periodList);
		row++;
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
