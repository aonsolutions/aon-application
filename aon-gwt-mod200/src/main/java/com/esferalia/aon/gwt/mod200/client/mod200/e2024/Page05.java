// BALANCE: PATRIMONIO NETO Y PASIVO
package com.esferalia.aon.gwt.mod200.client.mod200.e2024;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2024.Model2002024.Model2002024PageCallback;
import com.esferalia.aon.occam.mod200.api.model.BalanceType;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2024.Mod2002024Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2024.Mod2002024Key;

public class Page05 extends PageAbs {

	public Page05( Model2002024PageCallback callback ) {
		super(callback);
	}
	
	@Override
	protected void initializeTable() {
		basePanel.clear();	
		addTable(AON.MSG.balancePasivo(), Mod2002024Constants.BALANCE_PASIVE_KEYS);
		paintFooterNote(basePanel, ACCOUNTING_STATEMENTS_FOOTER);
	}

	@Override	
	protected boolean isDisabled(IMod200Key key) {
		if (callback.getMod200Object().getMod200().getBalanceType() == BalanceType.NORMAL) {
			if (key == Mod2002024Key.BP191 
 			 || key == Mod2002024Key.BP195
			 || key == Mod2002024Key.BP202 
			 || key == Mod2002024Key.BP211
			 || key == Mod2002024Key.BP230
			 || key == Mod2002024Key.BP240
			 ) {
				return true;
			}
		}
		return super.isDisabled(key);
	}
}