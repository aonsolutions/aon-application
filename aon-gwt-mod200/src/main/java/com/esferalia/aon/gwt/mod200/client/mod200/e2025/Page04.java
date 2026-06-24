// BALANCE: ACTIVO
package com.esferalia.aon.gwt.mod200.client.mod200.e2025;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2025.Model2002025.Model2002025PageCallback;
import com.esferalia.aon.occam.mod200.api.model.BalanceType;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025Key;

public class Page04 extends PageAbs {

	public Page04( Model2002025PageCallback callback ) {
		super(callback);
	}
	
	@Override
	protected void initializeTable() {		
		basePanel.clear();		
		addTable(AON.MSG.balanceActivo(), Mod2002025Constants.BALANCE_ACTIVE_KEYS);
		paintFooterNote(basePanel, ACCOUNTING_STATEMENTS_FOOTER);
	}

	@Override	
	protected boolean isDisabled(IMod200Key key) {
		if (callback.getMod200Object().getMod200().getBalanceType() == BalanceType.NORMAL) {
			if (key == Mod2002025Key.BA111
			 || key == Mod2002025Key.BA115
			 || key == Mod2002025Key.BA138 
			 || key == Mod2002025Key.BA177 ) {
				return true;
			}
		}
		return super.isDisabled(key);
	}	
	
}