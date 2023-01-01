// BALANCE: PATRIMONIO NETO Y PASIVO
package com.esferalia.aon.gwt.mod200.client.mod200.e2022;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2022.Model2002022.Model200PageCallback;
import com.esferalia.aon.occam.mod200.api.model.BalanceType;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key;

public class Page04 extends PageAbs {

	public Page04( Model200PageCallback callback ) {
		super(callback);
	}
	
	@Override
	protected void initializeTable() {
		basePanel.clear();	
		addTable(AON.MSG.balancePasivo(), Mod2002022Constants.BALANCE_PASIVE_KEYS);
		paintFooterNote(basePanel, ACCOUNTING_STATEMENTS_FOOTER);
	}

	@Override	
	protected boolean isDisabled(IMod200Key key) {
		if (callback.getMod200Object().getMod200().getBalanceType() == BalanceType.NORMAL) {
			if (key == Mod2002022Key.BP191 
 			 || key == Mod2002022Key.BP195
			 || key == Mod2002022Key.BP202 
			 || key == Mod2002022Key.BP211
			 || key == Mod2002022Key.BP230
			 || key == Mod2002022Key.BP240
			 ) {
				return true;
			}
		}
		return super.isDisabled(key);
	}
}
