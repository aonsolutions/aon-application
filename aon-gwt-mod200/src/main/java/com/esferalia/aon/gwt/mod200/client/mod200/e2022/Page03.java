// BALANCE: ACTIVO
package com.esferalia.aon.gwt.mod200.client.mod200.e2022;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2022.Model2002022.Model2002022PageCallback;
import com.esferalia.aon.occam.mod200.api.model.BalanceType;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key;

public class Page03 extends PageAbs {

	public Page03( Model2002022PageCallback callback ) {
		super(callback);
	}
	
	@Override
	protected void initializeTable() {		
		basePanel.clear();		
		addTable(AON.MSG.balanceActivo(), Mod2002022Constants.BALANCE_ACTIVE_KEYS);
		paintFooterNote(basePanel, ACCOUNTING_STATEMENTS_FOOTER);
	}

	@Override	
	protected boolean isDisabled(IMod200Key key) {
		if (callback.getMod200Object().getMod200().getBalanceType() == BalanceType.NORMAL) {
			if (key == Mod2002022Key.BA111
			 || key == Mod2002022Key.BA115
			 || key == Mod2002022Key.BA138 
			 || key == Mod2002022Key.BA177 ) {
				return true;
			}
		}
		return super.isDisabled(key);
	}	
	
}
