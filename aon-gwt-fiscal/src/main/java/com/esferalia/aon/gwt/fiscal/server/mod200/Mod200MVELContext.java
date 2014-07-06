package com.esferalia.aon.gwt.fiscal.server.mod200;

import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.*;

import com.esferalia.aon.accounting.mining.server.AccMiningMVELContext;
import com.esferalia.aon.accounting.mining.server.IAccMiningKeyAccept;
import com.esferalia.aon.accounting.mining.shared.AccMiningException;
import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key;

public class Mod200MVELContext extends AccMiningMVELContext {

	public Mod200MVELContext(IAccMiningKeyAccept resolver) {
		super(resolver);
	}
	protected Boolean isChecked(Mod200Key key) {
		return (Boolean) get(key.toString());
	}
	protected Double getValue(Mod200Key key) {
		return (Double) get(key.toString());
	}
	protected Double round(Mod200Key key) {
		return AonUtil.round(getValue(key));
	}
	

	public double computeLQ558() throws AccMiningException {
		if ( isChecked(C0001) ) return 10.0;
		if ( isChecked(C0002) ) return 25.0;
		if ( isChecked(C0003) ) return 1.0;
		if ( isChecked(C0004) ) return 1.0;
		if ( isChecked(C0005) ) return 25.0;
		if ( isChecked(C0006) && !isChecked(C0034)) return 25.0;
		if ( isChecked(C0006) && isChecked(C0034)) return 35.0;
		if ( isChecked(C0006) && isChecked(C0056)) return 20.0;
		if ( isChecked(C0012) ) return 30.0;
		if ( isChecked(C0015) ) return 4.0;
		if ( isChecked(C0017) ) return 20.0;
		if ( isChecked(C0018) ) return 20.0;
		if ( isChecked(C0034) ) return 35.0;
		if ( isChecked(C0036) ) return 25.0;
		if ( isChecked(C0038) ) return 30.0;
		if ( isChecked(C0038) ) return 30.0;
		if ( isChecked(C0046) ) return 30.0;
		if ( isChecked(C0048) ) return 0.0;
		if ( isChecked(C0056) ) return 20.0;
		if ( isChecked(C0057) ) return 20.0;
		if ( isChecked(C0058) ) return 25.0;
		if ( isChecked(C0063) ) return 15.0;
		
		return 30.0;
	}
	
	public double computeLQ562() throws AccMiningException {
		double lq552 = round(getValue(LQ552));
		double lq558 = round(getValue(LQ558));
		double lq559 = round(getValue(LQ559));
		
		if (lq552 <= 0) return 0;
		
		if (isChecked(C0006)) {
			if (lq552<=300000){
				return round( lq552*25/100);			
			} else {
				return (300000*25/100) + (lq552 - 300000)*30/100;				
			}
		}
		
		if (isChecked(C0015)) {
			return round((lq559 * lq558 / 100) + (lq552 - lq559) * 30 / 100);
		}
		
		if (isChecked(C0012)) {
			double lq520 = round(getValue(LQ520));
			if (lq520>0) return round(lq520 * lq558 /100);
			return 0;
		}
		
		if (isChecked(C0057)) {
			double lq521 = round(getValue(Mod200Key.LQ521));
			if (round(lq552-lq521) > 0) {
				return round((lq552 -lq521)* lq558 /100);
			}
			return 0;
		}
		return round(lq552 * lq558 / 100);
	}
	
}
