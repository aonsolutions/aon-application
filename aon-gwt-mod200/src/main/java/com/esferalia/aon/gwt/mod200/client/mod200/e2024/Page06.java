// CUENTA DE PERDIDAS Y GANANCIAS
package com.esferalia.aon.gwt.mod200.client.mod200.e2024;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2024.Model2002024.Model2002024PageCallback;
import com.esferalia.aon.occam.mod200.api.model.BalanceType;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2024.Mod2002024Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2024.Mod2002024Key;

public class Page06 extends PageAbs {

	public Page06( Model2002024PageCallback callback ) {
		super(callback);
	}
	
	@Override
	protected void initializeTable() {
		addTable(AON.MSG.pyg(), Mod2002024Constants.PYG_KEYS);
		paintFooterNote(basePanel, ACCOUNTING_STATEMENTS_FOOTER);
	}
	
	@Override
	protected boolean isDisabled(IMod200Key key) {
		if (callback.getMod200Object().getMod200().getPygType() == BalanceType.NORMAL) {
			if (key == Mod2002024Key.PG255
			  ||key == Mod2002024Key.PG279
			  ||key == Mod2002024Key.PG309
			 ) {
				return true;
			}
		}
		return super.isDisabled(key);
	}

	@Override
	protected boolean isAvailable() {
		return super.isAvailable()
  		  && (callback.getMod200Object().getMod200().isNotChecked(Mod2002024Key.C0026));		
	}
}