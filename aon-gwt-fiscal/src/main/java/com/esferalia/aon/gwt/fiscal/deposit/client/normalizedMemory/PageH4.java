package com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2PDepositConstants;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

public class PageH4 extends PageAbs {
	private static String[] ECPN_ABREVIATE_HEADER = new String[] {
		 "Capital escriturado [01]"
		,"Capital (no exigido) [02]"
		,"Prima de emisi\u00F3n [03]"
		,"Reservas [04]"
		,"Acciones y participaciones en patrimonio propias [05]"
		,"Resultados de ejercicios anteriores [06]"
		,"Otras aportaciones de socios [07]"
		,"Resultados del ejercicio [08]"
		,"Dividendo a cuenta [09]"
		,"Otros instrumentos de patrmonio neto [10]"
		,"Ajustes por cambios de valor [11]"
		,"Subvenciones, donaciones y legados recibidos [12]"
		,"Total [13]"
	};
	
	private static String[] ECPN_PYMES_HEADER = new String[] {
		 "Capital escriturado [01]"
		,"Capital (no exigido) [02]"
		,"Prima de emisi\u00F3n [03]"
		,"Reservas [04]"
		,"Acciones y participaciones en patrimonio propias [05]"
		,"Resultados de ejercicios anteriores [06]"
		,"Otras aportaciones de socios [07]"
		,"Resultados del ejercicio [08]"
		,"Dividendo a cuenta [09]"
		,"Ajustes en patrimonio neto [11]"
		,"Subvenciones, donaciones y legados recibidos [12]"
		,"Total [13]"
	};
	
	interface PageBinder extends UiBinder<Widget, PageH4> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	@UiField FlexTable table1;
	@UiField TabPanel tabPanel;

	public PageH4() {
		super();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		tabPanel.selectTab(0);
	}
	
	public PageH4(Enterprise enterprise, NormalizedMemory nm, Integer year) {
		this();
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		this.year = year;
	}

	@Override
	protected void initializeTable() {
		if (isPymes()) {
			defineECPNTable(table1,ECPN_PYMES_HEADER,D2PDepositConstants.ECPN_PYMES_KEYS);
			tabPanel. getTabBar().setTabEnabled(0, false);
			tabPanel.selectTab(1);	
		} else {
			defineBalanceTable(table,AON.MSG.patrimonioIngresos(),D2DepositConstants.ECPN_INCOMES_KEYS);
			defineECPNTable(table1,ECPN_ABREVIATE_HEADER,D2DepositConstants.ECPN_ABREVIATE_KEYS);
			
			if(tabPanel.getTabBar().getSelectedTab() != 0)
				tabPanel.selectTab(tabPanel.getTabBar().getSelectedTab());
			else tabPanel.selectTab(0);
		}
	}
	
	protected void defineECPNTable( FlexTable tab, String[] headers, D2DepositHeaderKey[][] keys){
		tab.setWidth("100%");
		tab.setCellSpacing(0);
		int row = 0;
		int col = 0;
		tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonWidthAuto());
		tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonNowrap());
		col++;
		for (String header : headers) {
			tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonWidth140());
			tab.setWidget(row, col, new Label(header));	
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderBottom());
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
			++col;
		}
		++row;
		for (D2DepositHeaderKey[] innerKeys : keys) {
			col = 0;
			paintKeyDescription(tab, innerKeys[0], row, col);
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
			col++;
			for (D2DepositHeaderKey key : innerKeys ) {
				paintKeyField(tab,key,row,col,(col==1),AonStringUtils.substring(key.getCode(), 0,3)); 
				col++;
			}
			++row;
		}
	}
}
