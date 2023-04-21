// ECPN: ESTADO DE INGRESOS Y GASTOS RECONOCIDOS
package com.esferalia.aon.gwt.mod200.client.mod200.e2022;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2022.Model2002022.Model200PageCallback;
import com.esferalia.aon.occam.mod200.api.model.EcpnType;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key;

public class Page06 extends PageAbs {
	
	public Page06( Model200PageCallback callback ) {
		super(callback);
	}
	
	@Override
	protected void initializeTable() {
		addTable(AON.MSG.patrimonioIngresos() + " (*)", Mod2002022Constants.ECPN_INCOME_KEYS);
		paintFooterNote(basePanel,"(*) El estado de cambios en el patrimonio neto ser\u00E1 de cumplimentaci\u00F3n voluntaria si se utiliza el modelo abreviado o PYMES del PGC.");
		paintFooterNote(basePanel, ACCOUNTING_STATEMENTS_FOOTER);
	}
	
	@Override
	protected boolean isDisabled(IMod200Key key) {
		if (callback.getMod200Object().getMod200().getEcpnType() == EcpnType.NORMAL) {
			if (key == Mod2002022Key.T0336 || key == Mod2002022Key.T0346) {
				return true;
			}
		}
		return super.isDisabled(key);
	}

	@Override
	protected boolean isAvailable() {
		return super.isAvailable()
  		  && ( (callback.getMod200Object().getMod200().isChecked(Mod2002022Key.C0075)) ||
  			   (callback.getMod200Object().getMod200().isChecked(Mod2002022Key.C0076)) ||
  			   (callback.getMod200Object().getMod200().isChecked(Mod2002022Key.C0077)) );  				  
	}
}

