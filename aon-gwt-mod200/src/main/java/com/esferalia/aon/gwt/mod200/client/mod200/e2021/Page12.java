// LIQUIDACION (V): CUOTA A INGRESAR O DEVOLVER, PAGOS FRACCIONADOS, LIQUIDO A INGRESAR O DEVOLVER
package com.esferalia.aon.gwt.mod200.client.mod200.e2021;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2021.Model2002021.Model200PageCallback;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Key;

public class Page12 extends PageAbs {

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
		addBasePanel();
		initializeTable();
	}
	
	@Override
	protected void initializeTable() {
		
		basePanel.clear();
		
		paintTable(addTable(AON.MSG.yearQuota(), 2), Mod2002021Constants.LIQUIDATION_V_KEYS_1, HEADERS_1);
		
		paintTable(addTable(2), Mod2002021Constants.LIQUIDATION_V_KEYS_2, HEADERS_2, Mod2002021Key.BN599);
		
		paintTable(addTable(AON.MSG.splittedPayments(), 2), Mod2002021Constants.LIQUIDATION_V_KEYS_3, HEADERS_2, Mod2002021Key.BN611);
		
		paintTable(addTable(AON.MSG.netQuota(),2), Mod2002021Constants.LIQUIDATION_V_KEYS_4, HEADERS_2);
		
		paintTable(addTable(3), Mod2002021Constants.LIQUIDATION_V_KEYS_5, HEADERS_3, Mod2002021Key.BN621);		
		
		paintTable(addTable("Regularizaci\u00F3n mediante autoliquidaci\u00F3n complementaria",2), Mod2002021Constants.LIQUIDATION_V_KEYS_6, HEADERS_2, Mod2002021Key.LQ1586);
		
		paintTable(addTable("Opci\u00F3n de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS)",2), Mod2002021Constants.LIQUIDATION_V_KEYS_7, HEADERS_2, Mod2002021Key.LQ2846);
		
		paintTable(addTable("Regularizaci\u00F3n de fraccionamiento art. 19.1 LIS mediante autoliquidaci\u00F3n complementaria en plazo voluntario",2), Mod2002021Constants.LIQUIDATION_V_KEYS_8, HEADERS_2, Mod2002021Key.LQ2850);
		
		paintTable(addTable("Conversi\u00F3n de activos por impuesto diferido en cr\u00E9dito exigible frente a la Administraci\u00F3n tributaria (art. 130 LIS)",2), Mod2002021Constants.LIQUIDATION_V_KEYS_9, HEADERS_3);
		
		paintTable(addTable("Regularizaci\u00F3n de conversi\u00F3n de activos por impuesto diferido mediante autoliquidaci\u00F3n complementaria",2), Mod2002021Constants.LIQUIDATION_V_KEYS_10, HEADERS_3);
		
	}
	
//	private void paintTable(FlexTable table, Mod2002021Key[][] liquidationKeys, String... headers) {
//	
//		int row = 0;
//		
//		paintEmptyCell(table, row, 0);
//		int col = 1;
//		for (String s : headers) {
//			paintTitle(table, s, row, col);
//			col++;			
//		}
//		
//		row++;
//		
//		for (Mod2002021Key[] keys : liquidationKeys) {
//			boolean paintDescription = true;							
//			for (int i = 0; i < keys.length; i++) {
//				if (keys[i] != null) {
//					if (paintDescription) {
////						paintKeyDescription(table, keys[i], row, 0);
////						// Casillas 599, 611, 621, 1586, 2846, 2850, la descripcion va en negrita
////						if (keys[i] == Mod2002021Key.BN599 || 
////							keys[i] == Mod2002021Key.BN611 ||
////							keys[i] == Mod2002021Key.BN621 ||
////							keys[i] == Mod2002021Key.LQ1586 ||
////							keys[i] == Mod2002021Key.LQ2846 ||
////							keys[i] == Mod2002021Key.LQ2850) {
////							table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
////						}
//						
//						// Casillas 599, 611, 621, 1586, 2846, 2850, la descripcion va en negrita
//						boolean bold = (keys[i] == Mod2002021Key.BN599 || 
//										keys[i] == Mod2002021Key.BN611 ||
//										keys[i] == Mod2002021Key.BN621 ||
//										keys[i] == Mod2002021Key.LQ1586 ||
//										keys[i] == Mod2002021Key.LQ2846 ||
//										keys[i] == Mod2002021Key.LQ2850); 
//						
////						paintKeyDescription(table, keys[i], row, 0);
//						paintDescription(table, keys[i].getDescription(), row, 0, bold, 0);
//						
//						paintDescription = false;
//					}
//					paintKeyField(table,keys[i], row, i+1);
//				}
//			}
//			row++;
//		}
//		
//	}

	@Override
	protected void populate() {}
	
}



