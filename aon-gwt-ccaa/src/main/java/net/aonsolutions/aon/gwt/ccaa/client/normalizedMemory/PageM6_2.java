package net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2PDepositConstants;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.aon.gwt.ccaa.client.Deposit2;

public class PageM6_2 extends PageAbs {
	
	private static final String[] MRN_HEADER_1 = new String[] {
		"Instrumentos de Patrimonio"
		,"Valores representativos de deuda"
		,"Cr\u00e9ditos, derivados y otros"
		,"TOTAL"
	};
	
	private static final String[] MRN_HEADER_2 = new String[] {
		"Inversiones mantenidas hasta el vencimiento"
		,"Inversiones en el patrimonio de empresa del grupo, multigrupo y asociadas"
		,"Activos financieros disponibles para la venta"
	};
	
	private static final String[] MRN_HEADER_3 = new String[] {
		"Valores representativos de deuda"
		,"Cr\u00e9ditos, derivados y otros (3)"
		,"TOTAL"
	};
	
	private static final String[] MRN_HEADER_4 = new String[] {
		"Activos a valor razonable con cambios en p\u00e9rdidas y ganancias"
		,"Activos mantenidos para negociar"
		,"Activos disponibles para la venta"
		,"TOTAL"
	};

	private static final String[] MRN_ABREVIATE_HEADER_5 = new String[] {
		"P\u00e9rdidas por deteriodo al final del ejercicio # (1)"
		,"(+/-) Variaci\u00f3n deteriodo a p\u00e9rdidas y ganancias"
		,"(+) Variaci\u00f3n contra patrimonio neto"
		,"(-) Salidas y reducciones"
		,"(+/-) Traspasos y otras variaciones (combinaciones de negocio, etc.)"
		,"P\u00e9rdida por deteriodo al final del ejercicio @ (2)"
	};
	
	private static final String[] MRN_PYMES_HEADER_5 = new String[] {
		"P\u00e9rdidas por deteriodo al final del ejercicio # (1)"
		,"(+/-) Variaci\u00f3n deteriodo a p\u00e9rdidas y ganancias"		
		,"(-) Salidas y reducciones"
		,"(+/-) Traspasos y otras variaciones (combinaciones de negocio, etc.)"
		,"P\u00e9rdida por deteriodo al final del ejercicio @ (2)"
	};

	interface PageBinder extends UiBinder<Widget, PageM6_2> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);
	
	@UiField(provided = true) FlexTable table1;
	@UiField(provided = true) FlexTable table2;
	@UiField(provided = true) FlexTable table3;
	@UiField(provided = true) FlexTable table4;
	@UiField(provided = true) FlexTable table5;
	
	@UiField TabPanel tabPanel;
	@UiField InlineLabel abreviateCLabel;
	@UiField InlineLabel table4Label;
	@UiField InlineLabel table5Label;
	
	public PageM6_2(Deposit2 deposit) {
		super(deposit);

		table1 = new FlexTable();
		table2 = new FlexTable();
		table3 = new FlexTable();
		table4 = new FlexTable();
		table5 = new FlexTable();
		tabPanel = new TabPanel();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		tabPanel.selectTab(0);
	
		initializeTable();
	}

	@Override
	protected void initializeTable() {
		String[][] AUXILIARES = new String[][] {
			new String[] {AON.MSG.fiscalYear() + " " + getYear(), AON.MSG.fiscalYear() + " " + (getYear() - 1)}
			, new String[] {"Largo plazo", "Corto plazo"}
		};
		if(getYear() < 2016){
			if(tabPanel.getTabBar().getSelectedTab() != 0)
				tabPanel.selectTab(tabPanel.getTabBar().getSelectedTab());
			else tabPanel.selectTab(0);
		} else {
			table5Label.setText("Valoraci\u00f3n y variaciones de valor de inversiones financieras valoradas a valor razonable" );
			tabPanel.getTabBar().setTabEnabled(0, false);
			tabPanel.selectTab(1);
		}
		if (isPymes()) {
			table4Label.setText("Movimiento de las cuentas correctoras representativas de las p\u00e9rdidas por deterioro originadas por el riesgo de cr\u00e9dito");
			table5Label.setText("Valor razonable y variaciones en el valor de activos financieros valorados a valor razonable");
			defineMRNTable(table, MRN_HEADER_1, AUXILIARES[0], D2PDepositConstants.MRN6_PYMES_KEYS_1, 2);
			defineMRNTable(table1, MRN_HEADER_1, AUXILIARES[0], D2PDepositConstants.MRN6_PYMES_KEYS_2, 2);
			//NO HAY table2
			defineMRNTable(table3, MRN_HEADER_3, AUXILIARES[1], D2PDepositConstants.MRN6_PYMES_KEYS_4, 2);
			defineMRNTable(table4, MRN_HEADER_4, null, D2PDepositConstants.MRN6_PYMES_KEYS_5, 1);
			defineMRNTable(table5, MRN_PYMES_HEADER_5, null, D2DepositConstants.MRN6_PYMES_KEYS_6, 1);
			abreviateCLabel.setText("");
		}
		else {
			defineMRNTable(table, MRN_HEADER_1, AUXILIARES[0], D2DepositConstants.MRN6_ABREVIATE_KEYS_1, 2);
			defineMRNTable(table1, MRN_HEADER_1, AUXILIARES[0], D2DepositConstants.MRN6_ABREVIATE_KEYS_2, 2);
			defineMRNTable(table2, AUXILIARES[0], MRN_HEADER_2, D2DepositConstants.MRN6_ABREVIATE_KEYS_3, 3);
			
			defineMRNTable(table3, MRN_HEADER_3, AUXILIARES[1], D2DepositConstants.MRN6_ABREVIATE_KEYS_4, 2);
			defineMRNTable(table4, MRN_HEADER_4, null, D2DepositConstants.MRN6_ABREVIATE_KEYS_5, 1);
			defineMRNTable(table5, MRN_ABREVIATE_HEADER_5, null, D2DepositConstants.MRN6_ABREVIATE_KEYS_6, 1);
		}	
	}
	
	protected void defineMRNTable (FlexTable tab, String[] headers, String[] footers, D2DepositKey[][] keys, int colSpan) {
		
		tab.setWidth("100%");
		tab.setCellSpacing(0);
		int row = 0;
		int col = 0;
		tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonWidthAuto());
		tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonNowrap());
		col++;
		
		for (String primary : headers) {
			tab.getFlexCellFormatter().setColSpan(row, col, colSpan);
			
			tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonWidth140());
			tab.setWidget(row, col, new Label(getDescription(primary)));	
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderBottom());
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
			++col;
		}
		
		if (footers != null) {
			
			++row;
			col = 1;
			
			for (int x = 0; x < headers.length; x++) {
				
				for (String secundary : footers) {

					tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonWidth140());
					tab.setWidget(row, col, new Label(putYear(secundary, getYear())));	
					tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
					tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderBottom());
					tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
					++col;
				}
			}
		}
		
		++row;
		col = 0;
		
		for (D2DepositKey[] innerKeys : keys) {
			col = 0;
			paintKeyDescription(tab, innerKeys[0], row, col);
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
			col++;
			for (D2DepositKey key : innerKeys ) {
				
				paintKeyField(tab,key,row,col,(col==1), AonStringUtils.substring(key.getCode(), 0, 4)); 
				col++;
			}
			++row;
		}
	}
}
