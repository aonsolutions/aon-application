// BALANCE: ACTIVO
package com.esferalia.aon.gwt.mod200.client.mod200.e2020;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2020.Model2002020.Model200PageCallback;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2020.Mod2002020Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2020.Mod2002020Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2020.Mod2002020.BalanceType;

public class Page03 extends PageAbs {

	public Page03( Model200PageCallback callback ) {
		super(callback);
		addBasePanel();
		initializeTable();
	}
	
	@Override
	protected void initializeTable() {		
		basePanel.clear();		
		addTable(AON.MSG.balanceActivo(), Mod2002020Constants.BALANCE_ACTIVE_KEYS);		
	}

	@Override
	protected void populate() {}
	
	@Override	
	protected boolean isDisabled(IMod200Key key) {
		if (callback.getMod200Object().getMod200().getBalanceType() == BalanceType.NORMAL) {
			if (key == Mod2002020Key.BA111
			 || key == Mod2002020Key.BA115
			 || key == Mod2002020Key.BA138 
			 || key == Mod2002020Key.BA177 ) {
				return true;
			}
		}
		return super.isDisabled(key);
	}
}
