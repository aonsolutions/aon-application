package com.esferalia.aon.gwt.fiscal.server.mod200;

import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.*;

import com.esferalia.aon.accounting.mining.server.AccMiningMVELContext;
import com.esferalia.aon.accounting.mining.server.IAccMiningKeyAccept;
import com.esferalia.aon.accounting.mining.shared.AccMiningException;
import com.esferalia.aon.gwt.common.server.DateUtil;
import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key;

public class Mod200MVELContext extends AccMiningMVELContext {
	
	private Mod200 mod200;  
	
	public Mod200MVELContext(Mod200 mod200,IAccMiningKeyAccept resolver) {
		super(resolver);
		this.mod200 = mod200;
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
	private int getDays() {
		if ( mod200.getPeriodType() == 3) {
			return (int) DateUtil.getDaysBetweenDates(mod200.getPeriodStart(), mod200.getPeriodStart());
		}
		return 365;
	}

	public double computeLQ558() throws AccMiningException {
		if ( isChecked(C0030) ) return round(getValue(LQ558));
		if ( isChecked(C0063) ) return 15.0;
		if ( isChecked(C0046) ) return 30.0;

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
		// ---
		if ( isChecked(C0034) ) return 35.0;
		if ( isChecked(C0036) ) return 25.0;
		if ( isChecked(C0038) ) return 30.0;
		if ( isChecked(C0047) ) return round(getValue(LQ558));
		if ( isChecked(C0048) ) return 0.0;
		if ( isChecked(C0049) ) return round(getValue(LQ558));
		if ( isChecked(C0056) ) return 20.0;
		if ( isChecked(C0057) ) return 20.0;
		if ( isChecked(C0058) ) return 25.0;
		// TODO
		if ( isChecked(C0017) ) return 20.0;
		if ( isChecked(C0018) ) return 20.0;
		return 30.0;
	}
	private double getLimit() {
		return AonUtil.round(300000 * getDays() / 365);	
	}
		
	public double computeLQ562() throws AccMiningException {
		double lq521 = round(getValue(LQ521));
		double lq552 = round(getValue(LQ552));
		double lq558 = round(getValue(LQ558));
		double lq559 = round(getValue(LQ559));
		
		if (lq552 <= 0) return 0;
		if (isChecked(C0006) && isChecked(C0022) && (isChecked(C0056) || isChecked(C0063))) {
			return getValue(LQ562);
		}
		if (isChecked(C0063)) {
			if (lq552<=getLimit()){
				return round( lq552*15/100);			
			} else {
				return (getLimit()*15/100) + (lq552 - getLimit())*20/100;				
			}
		}
		if (isChecked(C0006)) {
			if (lq552<=getLimit()){
				return round( lq552*25/100);			
			} else {
				return (getLimit()*25/100) + (lq552 - getLimit())*30/100;				
			}
		}
		if (isChecked(C0056)) {
			if (lq552<=getLimit()){
				return round( lq552*20/100);			
			} else {
				return (getLimit()*20/100) + (lq552 - getLimit())*25/100;				
			}
		}
		
		if (isChecked(C0015)) {
			if (isChecked(C0057)) {
				if (round(lq552 - lq559 - lq521) > 0) {
					return round((lq559 * lq558 / 100) + (lq552 - lq559 - lq521) * 30 / 100);	
				} else {
					return round((lq559 * lq558 / 100));
				}
			}
			return round((lq559 * lq558 / 100) + (lq552 - lq559) * 30 / 100);
		}
		
		if (isChecked(C0030) || isChecked(C0047)) {
			return getValue(LQ562);
		}
		
		if (isChecked(C0012)) {
			double lq520 = round(getValue(LQ520));
			if (lq520>0) return round(lq520 * lq558 /100);
			return 0;
		}
		
		if (isChecked(C0057)) {
			if (round(lq552-lq521) > 0) {
				return round((lq552 -lq521)* lq558 /100);
			}
			return 0;
		}
		
		if (isChecked(C0056)) {
			if (round(lq552-lq521) > 0) {
				return round((lq552 -lq521)* lq558 /100);
			}
			return 0;
		}
		
		return round(lq552 * lq558 / 100);
	}
	
}
