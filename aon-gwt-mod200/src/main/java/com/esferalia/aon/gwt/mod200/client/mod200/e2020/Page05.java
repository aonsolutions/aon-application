// CUENTA DE PERDIDAS Y GANANCIAS
package com.esferalia.aon.gwt.mod200.client.mod200.e2020;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2020.Model2002020.Model200PageCallback;
import com.esferalia.aon.occam.mod200.api.model.BalanceType;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2020.Mod2002020Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2020.Mod2002020Key;

public class Page05 extends PageAbs {

	public Page05( Model200PageCallback callback ) {
		super(callback);
		addBasePanel();
		initializeTable();		
	}
	
	protected void initializeTable() {
		addTable(AON.MSG.pyg(), Mod2002020Constants.PYG_KEYS);
	}
	
	@Override
	protected void populate() {}
	
	@Override
	protected boolean isDisabled(IMod200Key key) {
		if (callback.getMod200Object().getMod200().getPygType() == BalanceType.NORMAL) {
			if (key == Mod2002020Key.PG255
			  ||key == Mod2002020Key.PG279
			  ||key == Mod2002020Key.PG309
			 ) {
				return true;
			}
		}
		return super.isDisabled(key);
	}

	@Override
	protected boolean isAvailable() {
		boolean av = super.isAvailable()
  		  && (callback.getMod200Object().getMod200().isNotChecked(Mod2002020Key.C0026));
		return av;
	}
}
