package com.esferalia.aon.gwt.fiscal.client.mod200.e2018;

import com.esferalia.aon.gwt.fiscal.client.mod200.e2018.Model2002018.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class Page13 extends PageAbs {
	
	private static String[] KEY_DESCRIPTIONS = new String[] {
		 ""
		,"Cuota del ejercicio a ingresar o a devolver."
		,"1\u00BA Pago fraccionados 1\u00BA."
		,"2\u00BA Pago fraccionados 2\u00BA."
		,"3\u00BA Pago fraccionados 3\u00BA."
		,"Cuota diferencial."
		,"Incremento por p\u00E9rdida beneficios fiscales per\u00EDodos anteriores."
		,"Incremento por incumplimiento de requisitos SOCIMI."
		,"Intereses de demora."
		,"Importe ingreso/devoluci\u00F3n efectuada de la declaraci\u00F3n originaria."
		,"Abono de deducciones I+D+i por insufi ciencia de cuota (opci\u00F3n art. 44.2 RDL 4/2004 y art. 39.2 LIS)"
		,"Abono de deducciones por producciones extranjeras (art. 39.3 LIS)"
		,"L\u00EDquido a ingresar o a devolver."
		,"Abono por conversi\u00F3n de activos por impuesto diferido en cr\u00E9dito exigible frente a la Administraci\u00F3n tributaria (art. 130 LIS)"
		,"Compensaci\u00F3n por conversi\u00F3n de activos por impuesto diferido en cr\u00E9dito exigible frente a la Administraci\u00F3n tributaria (art. 130 LIS)"
	};
	
	interface PageBinder extends
			UiBinder<Widget, Page13> {
	}

	private static final PageBinder pageBinder = GWT
			.create(PageBinder.class);

	@UiField(provided = true)
	FlexTable table;
	@UiField(provided = true)
	FlexTable table1;
	@UiField(provided = true)
	FlexTable table2;
	
	public Page13( Model200PageCallback callback ) {
		super(callback);
		table  = new FlexTable();
		table1 = new FlexTable();
		table2 = new FlexTable();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		initializeTable();
	}
	
	@Override
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(1, "200px");
		
		int row = 0;
		for (final Mod2002018Key key : Mod2002018Constants.COMBINED_TAXATION_1) {
			if (callback.getMod200Object().isVisible(key)) {
				row = paintKey(table,key,row);
			}
		}
		
		paintTable(table1, Mod2002018Constants.COMBINED_TAXATION_2);

		getFlexTable(table2,0, new String[]{"","ARABA","GIPUZKOA","BIZKAIA","NAVARRA","TOTAL"});
		table2.getColumnFormatter().setWidth(0, "auto");
		row = 1;
		for (Mod2002018Key[] keys : Mod2002018Constants.COMBINED_TAXATION_3) {
			boolean paintDescription = true;	
			for (int i = 0; i< keys.length; i++) {
				if (keys[i] != null && callback.getMod200Object().isVisible(keys[i])) {
					if (paintDescription) {
						paintDescription(table2, KEY_DESCRIPTIONS[row], row, 0, (row==0 || row == 4 || row == 11));
						paintDescription = false;
					}
					paintKeyField(table2,keys[i],row, i+1, 8);
				}
			}
			row++;
		}
	}
	
	private void paintTable(FlexTable table, Mod2002018Key[] keys) {
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(1, "200px");	
		int row = 0;
		for (Mod2002018Key key : keys) {
			if (key != null && callback.getMod200Object().isVisible(key)) {
				paintKeyDescription(table, key, row, 0);
				paintKeyField(table,key,row, 1);
			}
			row++;
		}
	}
	
	@Override
	protected void populate() {}
	
	@Override
	protected boolean isAvailable() {
		boolean av = super.isAvailable()
  		  && (callback.getMod200Object().getMod200().isChecked(Mod2002018Key.C0028));
		return av;
	}
	
}
