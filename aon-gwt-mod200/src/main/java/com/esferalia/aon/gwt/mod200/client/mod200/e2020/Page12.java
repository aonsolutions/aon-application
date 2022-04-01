// LIQUIDACION (V): CUOTA A INGRESAR O DEVOLVER, PAGOS FRACCIONADOS, LIQUIDO A INGRESAR O DEVOLVER
package com.esferalia.aon.gwt.mod200.client.mod200.e2020;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2020.Model2002020.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020Key;
import com.google.gwt.user.client.ui.FlexTable;

public class Page12 extends PageAbs {

//	interface PageBinder extends
//			UiBinder<Widget, Page12> {
//	}

//	private static final PageBinder pageBinder = GWT
//			.create(PageBinder.class);

//	@UiField(provided = true)
//	FlexTable table;
//	@UiField(provided = true)
//	FlexTable table1;
//	@UiField(provided = true)
//	FlexTable table2;
//	@UiField(provided = true)
//	FlexTable table3;
//	@UiField(provided = true)
//	FlexTable table4;

	private static final String[] HEADERS_1 = new String[]{
			"Efectuados a la entidad",
			"Imputados por AIEs y UTEs"};
	
	private static final String[] HEADERS_2 = new String[]{
			"Estado",
			"D.Forales/Navarra (totales)"};

	private static final String[] HEADERS_3 = new String[]{
			"Total (Estado+DF/N)",
			"Estado",
			"D.Forales/Navarra (totales)"};
	
	public Page12( Model200PageCallback callback ) {
		super(callback);
//		table  = new FlexTable();
//		table1 = new FlexTable();
//		table2 = new FlexTable();
//		table3 = new FlexTable();
//		table4 = new FlexTable();
//		Widget ui = pageBinder.createAndBindUi(this);
//		initWidget(ui);
		addBasePanel();
		initializeTable();
	}
	
	@Override
	protected void initializeTable() {
		
		basePanel.clear();
		
//		table.setWidth("100%");
//		table.setCellSpacing(0);
//		table.getColumnFormatter().setWidth(1, "200px");
		
//		paintTable(table , Mod2002020Constants.LIQUIDATION_V_KEYS_1, 2, "retention");
//		paintTable(table1, Mod2002020Constants.LIQUIDATION_V_KEYS_2, 2, "diputation");
//		paintTable(table2, Mod2002020Constants.LIQUIDATION_V_KEYS_3, 2, null);
//		paintTable(table3, Mod2002020Constants.LIQUIDATION_V_KEYS_4, 2, null);
//		paintTable(table4, Mod2002020Constants.LIQUIDATION_V_KEYS_5, 3, null);
		
//		basePanel.add(getTitle(AON.MSG.yearQuota()));
		paintTable(addTable(AON.MSG.yearQuota(), 2), Mod2002020Constants.LIQUIDATION_V_KEYS_1, HEADERS_1);
		paintTable(addTable(2), Mod2002020Constants.LIQUIDATION_V_KEYS_2, HEADERS_2);
		
//		basePanel.add(getTitle(AON.MSG.splittedPayments()));
		paintTable(addTable(AON.MSG.splittedPayments(), 2), Mod2002020Constants.LIQUIDATION_V_KEYS_3, HEADERS_2);
		
//		basePanel.add(getTitle(AON.MSG.netQuota()));
		paintTable(addTable(AON.MSG.netQuota(),2), Mod2002020Constants.LIQUIDATION_V_KEYS_4, HEADERS_2);
		paintTable(addTable(3), Mod2002020Constants.LIQUIDATION_V_KEYS_5, HEADERS_3);		
		
	}
	
//	private void paintTable(FlexTable table, Mod2002020Key[][] liquidationKeys, int numCols, String header) {
	private void paintTable(FlexTable table, Mod2002020Key[][] liquidationKeys, String... headers) {
//		table.setWidth("100%");
//		table.setCellSpacing(0);
//		for (int i = 0; i < numCols; i++  ) {
//			table.getColumnFormatter().setWidth((i+1), "200px");	
//		}
		
		int row = 0;
		
		paintEmptyCell(table, row, 0);
		int col = 1;
		for (String s : headers) {
			paintTitle(table, s, row, col);
			col++;			
		}
		
		row++;
		
//		if (header=="retention") {
//			table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
//			table.getFlexCellFormatter().setColSpan(row, 0, 0);
//			table.setWidget(row, 1, new Label("Efectuados a la entidad"));
//			table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
//			table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
//			table.setWidget(row, 2, new Label("Imputados por AIEs y UTEs"));
//			table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
//			table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
//			++row;
//		} 
		
		for (Mod2002020Key[] keys : liquidationKeys) {
			boolean paintDescription = true;							
			for (int i = 0; i < keys.length; i++) {
				if (keys[i] != null) {
					if (paintDescription) {
						paintKeyDescription(table, keys[i], row, 0);
						paintDescription = false;
					}
					paintKeyField(table,keys[i], row, i+1);
				}
			}
			row++;
		}
		
	}

	@Override
	protected void populate() {}
	
}



