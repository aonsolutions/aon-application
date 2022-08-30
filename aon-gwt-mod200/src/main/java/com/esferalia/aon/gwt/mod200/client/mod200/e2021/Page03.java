// BALANCE: ACTIVO
package com.esferalia.aon.gwt.mod200.client.mod200.e2021;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2021.Model2002021.Model200PageCallback;
import com.esferalia.aon.occam.mod200.api.model.BalanceType;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Key;

public class Page03 extends PageAbs {

	public Page03( Model200PageCallback callback ) {
		super(callback);
	}
	
	@Override
	protected void initializeTable() {		
		basePanel.clear();		
		addTable(AON.MSG.balanceActivo(), Mod2002021Constants.BALANCE_ACTIVE_KEYS);
		paintFooterNote(basePanel, ACCOUNTING_STATEMENTS_FOOTER);
	}

	@Override	
	protected boolean isDisabled(IMod200Key key) {
		if (callback.getMod200Object().getMod200().getBalanceType() == BalanceType.NORMAL) {
			if (key == Mod2002021Key.BA111
			 || key == Mod2002021Key.BA115
			 || key == Mod2002021Key.BA138 
			 || key == Mod2002021Key.BA177 ) {
				return true;
			}
		}
		return super.isDisabled(key);
	}	
	
	
}
