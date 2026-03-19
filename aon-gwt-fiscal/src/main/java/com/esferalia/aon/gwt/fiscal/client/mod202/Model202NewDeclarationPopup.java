package com.esferalia.aon.gwt.fiscal.client.mod202;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod202.Model202.Model202Callback;
import com.esferalia.aon.gwt.fiscal.client.model.NewDeclarationPopup;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

@Deprecated
public class Model202NewDeclarationPopup extends NewDeclarationPopup<Mod202,Model202ModuleOptions>{
	// FALTA - BORRAR ARCHIVO
	private Model202NewDeclarationPopup() {
		super(null, null);
	}

//	private class PeriodListBox extends ListBox {
//
//		private PeriodListBox() {
//			setWidth("90px");
//			addItem( "1\u00BA Periodo.", Integer.toString( Period.T1.ordinal() ) );
//			addItem( "2\u00BA Periodo.", Integer.toString( Period.T2.ordinal() ) );
//			addItem( "3\u00BA Periodo.", Integer.toString( Period.T3.ordinal() ) );
//		}
//
//		public Period getValue() {
//			return Period.values()[ AonNumberUtils.toInteger( getSelectedValue() ) ];
//		}
//	}
//
//	public Model202NewDeclarationPopup(Mod202 mod202, Model202Callback callback) {
//		super(mod202, callback);
//	}
//	
//	@Override
//	protected void paintAdministration(Mod202 mod202) {
//		tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
//		tab.setWidget(row, 0, new Label(AON.MSG.administration()));
//		mod202.setAdministration( Administration.COMMON_TERRITORY );
//		replacement.setVisible(mod202.isReplacementDeclarationAvailable());
//		complementary.setVisible(mod202.isComplementaryDeclarationAvailable());
//		previousLabel.setVisible(mod202.isReplacedNumberAvailable());
//		previous.setVisible(mod202.isReplacedNumberAvailable());
//		tab.setWidget(row, 1, new Label(Administration.COMMON_TERRITORY.getDescription()));
//		row++;
//	}
//	
//	@Override
//	protected void paintPeriod(Mod202 mod202) {
//		tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
//		tab.setWidget(row, 0, new Label(AON.MSG.period()));
//		final PeriodListBox periodList = new PeriodListBox();
//		if (mod202.getPeriod() != null) {
//			for (int i = 0; i < periodList.getItemCount(); i++) {
//				Integer value = AonNumberUtils.toInteger(periodList.getValue(i));
//				if (value != null && mod202.getPeriod().ordinal() == value) {
//					periodList.setSelectedIndex(i);
//					break;
//				}
//			}
//		}
//		periodList.addChangeHandler( event -> mod202.setPeriod( periodList.getValue() ));
//		tab.setWidget(row, 1, periodList);
//		row++;
//	}
//	
//	@Override
//	protected void paintModelSpecificPanel(Mod202 mod202) {
//		tab.setWidget(row, 0, new Label(Mod202Key.X00.getDescription()));
//		tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
//		
//		final ListBox r21Box = new ListBox();
//		r21Box.setWidth("350px");
//		r21Box.addItem(AON.MSG.calculation0(), "0");
//		r21Box.addItem(AON.MSG.calculation1(), "1");
//		r21Box.addItem(AON.MSG.calculation2(), "2");
//		mod202.putAmount(Mod202Key.X00, 0); 
//		r21Box.addChangeHandler( event -> mod202.putAmount(Mod202Key.X00, r21Box.getSelectedIndex()));
//		tab.setWidget(row, 1, r21Box);
//		row++;
//	}
	
}
