// CUENTA DE PERDIDAS Y GANANCIAS
package com.esferalia.aon.gwt.mod200.client.mod200.e2022;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2022.Model2002022.Model2002022PageCallback;
import com.esferalia.aon.occam.mod200.api.model.BalanceType;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key;

public class Page05 extends PageAbs {

	public Page05( Model2002022PageCallback callback ) {
		super(callback);
	}
	
	@Override
	protected void initializeTable() {
		addTable(AON.MSG.pyg(), Mod2002022Constants.PYG_KEYS);
		paintFooterNote(basePanel, ACCOUNTING_STATEMENTS_FOOTER);
	}
	
	@Override
	protected boolean isDisabled(IMod200Key key) {
		if (callback.getMod200Object().getMod200().getPygType() == BalanceType.NORMAL) {
			if (key == Mod2002022Key.PG255
			  ||key == Mod2002022Key.PG279
			  ||key == Mod2002022Key.PG309
			 ) {
				return true;
			}
		}
		return super.isDisabled(key);
	}

	@Override
	protected boolean isAvailable() {
		return super.isAvailable()
  		  && (callback.getMod200Object().getMod200().isNotChecked(Mod2002022Key.C0026));		
	}
}

