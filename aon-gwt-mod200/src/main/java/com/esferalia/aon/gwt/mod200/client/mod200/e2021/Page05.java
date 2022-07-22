// CUENTA DE PERDIDAS Y GANANCIAS
package com.esferalia.aon.gwt.mod200.client.mod200.e2021;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2021.Model2002021.Model200PageCallback;
import com.esferalia.aon.occam.mod200.api.model.BalanceType;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Key;

public class Page05 extends PageAbs {

	public Page05( Model200PageCallback callback ) {
		super(callback);
	}
	
	@Override
	protected void initializeTable() {
		addTable(AON.MSG.pyg(), Mod2002021Constants.PYG_KEYS);
		paintFooterNote(basePanel, ACCOUNTING_STATEMENTS_FOOTER);
	}
	
//	@Override
//	protected void populate() {}
	
	@Override
	protected boolean isDisabled(IMod200Key key) {
		if (callback.getMod200Object().getMod200().getPygType() == BalanceType.NORMAL) {
			if (key == Mod2002021Key.PG255
			  ||key == Mod2002021Key.PG279
			  ||key == Mod2002021Key.PG309
			 ) {
				return true;
			}
		}
		return super.isDisabled(key);
	}

	@Override
	protected boolean isAvailable() {
		boolean av = super.isAvailable()
  		  && (callback.getMod200Object().getMod200().isNotChecked(Mod2002021Key.C0026));
		return av;
	}
}
