// TRIBUTACION CONJUNTA
package com.esferalia.aon.gwt.mod200.client.mod200.e2020;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2020.Model2002020.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020Key;
import com.google.gwt.user.client.ui.FlexTable;

public class Page13 extends PageAbs {
	
	private static String[] KEY_DESCRIPTIONS = new String[] {
		 ""
		,"Cuota del ejercicio a ingresar o a devolver."
		,"Pago fraccionado 1\u00BA."
		,"Pago fraccionado 2\u00BA."
		,"Pago fraccionado 3\u00BA."
		,"Cuota diferencial."
		,"Incremento por p\u00E9rdida beneficios fiscales per\u00EDodos anteriores."
		,"Incremento por incumplimiento de requisitos SOCIMI."
		,"Intereses de demora."
		,"Complementaria: Importe ingreso/devoluci\u00F3n efectuada de la declaraci\u00F3n originaria."
		,"Abono de deducciones I+D+i por insuficiencia de cuota (opci\u00F3n art. 44.2 RDLeg. 4/2004 y art. 39.2 LIS)"
		,"Abono de deducciones por producciones cinematogr\u00E1ficas extranjeras (art. 39.3 LIS)"
		,"L\u00EDquido a ingresar o a devolver."
		,"Abono por conversi\u00F3n de activos por impuesto diferido en cr\u00E9dito exigible frente a la Administraci\u00F3n tributaria (art. 130 LIS)"
		,"Compensaci\u00F3n por conversi\u00F3n de activos por impuesto diferido en cr\u00E9dito exigible frente a la Administraci\u00F3n tributaria (art. 130 LIS)"
	};
	
	public Page13( Model200PageCallback callback ) {
		super(callback);
		addBasePanel();
		initializeTable();
	}
	
	@Override
	protected void initializeTable() {
		
		basePanel.clear();
		
		addTable(AON.MSG.combinedTaxation1(), Mod2002020Constants.COMBINED_TAXATION_1);

		addTable(AON.MSG.combinedTaxation2(), Mod2002020Constants.COMBINED_TAXATION_2);
		
		basePanel.add(getTitle(AON.MSG.combinedTaxation3()));
		
		FlexTable table2 = new FlexTable();
		basePanel.add(table2);

		getFlexTable(table2, 0, new String[]{"","ARABA","GIPUZKOA","BIZKAIA","NAVARRA","TOTAL"});
		table2.addStyleName(AON.CSS.aonWidthAlmostAll());
		table2.addStyleName(AON.CSS.aonBlockCenter());
		table2.getColumnFormatter().setWidth(0, "auto");
		int row = 1;
		for (Mod2002020Key[] keys : Mod2002020Constants.COMBINED_TAXATION_3) {
			boolean paintDescription = true;	
			for (int i = 0; i< keys.length; i++) {
				if (keys[i] != null && callback.getMod200Object().isVisible(keys[i])) {
					if (paintDescription) {
						paintDescription(table2, KEY_DESCRIPTIONS[row], row, 0, (row==1 || row == 5 || row == 12));
						paintDescription = false;
					}
					paintKeyField(table2,keys[i],row, i+1, 8);
				}
			}
			row++;
		}
	}
	
	@Override
	protected void populate() {}
	
	@Override
	protected boolean isAvailable() {
		boolean av = super.isAvailable()
  		  && (callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0028));
		return av;
	}
	
}
