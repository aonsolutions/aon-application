package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PageM15 extends PageAbs {

	interface PageBinder extends UiBinder<Widget, PageM15> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);


	


	public PageM15() {
		super();

		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}
	
	public PageM15(Enterprise enterprise, NormalizedMemory nm) {
		super();
		this.enterprise = enterprise;
		this.normalizedMemory = nm;

		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}

	@Override
	protected void initializeTable() {
		FlexTable.FlexCellFormatter flexCellFormatter =table.getFlexCellFormatter();
		flexCellFormatter.setColSpan(0, 1, 2);
		flexCellFormatter.setColSpan(0, 2, 2);
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(1, "200px");
		table.getColumnFormatter().setWidth(2, "200px");
		table.getColumnFormatter().setWidth(3, "200px");
		table.getColumnFormatter().setWidth(4, "200px");

		int row = 0;
		table.setWidget(row, 1, new Label("Ejercicio 2014"));
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 2, new Label("Ejercicio 2013"));
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		
		++row;
		table.setWidget(row, 0, new Label("Pagos del ejercicio"));
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 1, new Label("Importe"));
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 2, new Label("%"));
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 3, new Label("Importe"));
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 4, new Label("%"));
		table.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextCenter());
		++row;
		for(Integer i = 0; i< D2DepositConstants.MA15_ABREVIATE_KEYS.length; i+=4){
			D2DepositKey[] d2 = new D2DepositKey[]{
					D2DepositConstants.MA15_ABREVIATE_KEYS[i],
					D2DepositConstants.MA15_ABREVIATE_KEYS[i+1],
					D2DepositConstants.MA15_ABREVIATE_KEYS[i+2],
					D2DepositConstants.MA15_ABREVIATE_KEYS[i+3],
			};
			row = paintKey(table, d2 , row);
		}

		
	}
	

	protected int paintKey(FlexTable tab, D2DepositKey[] keys,  int row) {
		paintKeyDescription(tab, keys[0], row, 0);
		for (Integer i = 0; i < keys.length ; i++) {
			paintKeyField(tab,keys[i],row,i+1);
		}
		return  ++row;
	}

}
