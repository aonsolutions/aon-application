package com.esferalia.aon.occam.impl.jooq.dao.mod200_2014;


import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.C0001;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.C0002;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.C0003;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.C0004;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.C0005;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.C0006;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.C0012;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.C0015;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.C0017;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.C0018;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.C0019;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.C0022;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.C0030;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.C0034;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.C0036;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.C0038;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.C0046;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.C0047;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.C0048;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.C0049;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.C0056;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.C0057;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.C0058;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.C0063;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.LM175;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.LM176;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.LM177;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.LM178;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.LM179;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.LM253;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.LM258;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.LQ520;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.LQ521;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.LQ552;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.LQ553;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.LQ554;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.LQ558;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.LQ559;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.LQ560;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key.LQ562;

import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key;
import com.esferalia.aon.occam.server.accounting.AccMiningMVELContext;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod2002014MVELContext extends AccMiningMVELContext {
	
	private static final int LIM_1 = 300000;
	private static final int LIM_2 = 1000000;
	
	private Mod2002014 mod200;
	
	public Mod2002014MVELContext(Mod2002014 mod200,IAccMiningKeyAccept resolver) {
		super(resolver);
		this.mod200 = mod200;
	}
	
	protected Boolean isChecked(Mod2002014Key key) {
		return (Boolean) get(key.toString());
	}
	
	protected Double getValue(Mod2002014Key key) {
		return (Double) get(key.toString());
	}
	
	protected Double round(Mod2002014Key key) {
		return AonMathUtils.round(getValue(key));
	}
	private int getDays() {
		if ( mod200.getPeriodType() == 3) {
			return (int) AonDateUtils.getDaysBetweenDates(mod200.getPeriodStart(), mod200.getPeriodStart());
		}
		return 365;
	}

	public double computeLQ558() throws AonCoreException {
		if ( isChecked(C0030) ) return round(getValue(LQ558));
		if ( isChecked(C0063) ) return 15.0;
		if ( isChecked(C0046) ) return 30.0;

		if ( isChecked(C0001) ) return 10.0;
		if ( isChecked(C0002) ) return 25.0;
		if ( isChecked(C0003) ) return 1.0;
		if ( isChecked(C0004) ) return 1.0;
		if ( isChecked(C0005) ) return 25.0;
		if ( isChecked(C0006) && isChecked(C0056)) return 20.0;
		if ( isChecked(C0006) && isChecked(C0034)) return 35.0;
		if ( isChecked(C0006) && !isChecked(C0034)) return 25.0;
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
		if ( isChecked(C0017) ) return round(getValue(LQ558));
		if ( isChecked(C0018) ) return round(getValue(LQ558));
		if ( isChecked(C0019) ) return round(getValue(LQ558));
		return 30.0;
	}
	private double getLimit(int limit) {
		return AonMathUtils.round(limit * getDays() / 365);	
	}
		
	public double computeLQ562() throws AonCoreException {
		double lq521 = round(getValue(LQ521));
		double lq552 = round(getValue(LQ552));
		double lq558 = round(getValue(LQ558));
		double lq559 = round(getValue(LQ559));
		
		if (lq552 <= 0) return 0;
		if (isChecked(C0006) && isChecked(C0022) && (isChecked(C0056) || isChecked(C0063))) {
			return getValue(LQ562);
		}
		if (isChecked(C0063)) {
			if (lq552<=getLimit(LIM_1)){
				return round( lq552*15/100);			
			} else {
				return (getLimit(LIM_1)*15/100) + (lq552 - getLimit(LIM_1))*20/100;				
			}
		}
		if (isChecked(C0056)) {
			if (lq552<=getLimit(LIM_1)){
				return round( lq552*20/100);			
			} else {
				return (getLimit(LIM_1)*20/100) + (lq552 - getLimit(LIM_1))*25/100;				
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
		if (isChecked(C0006)) {
			if (lq552<=getLimit(LIM_1)){
				return round( lq552*25/100);			
			} else {
				return (getLimit(LIM_1)*25/100) + (lq552 - getLimit(LIM_1))*30/100;				
			}
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
		return round(lq552 * lq558 / 100);
	}
	
	public double computeLM043() throws AonCoreException {
		double lm175 = round(getValue(LM175));
		double lm176 = round(getValue(LM176));
		double lm177 = round(getValue(LM177));
		double lm178 = round(getValue(LM178));
		double lm179 = round(getValue(LM179));
		double lm253 = round(getValue(LM253));
		double lm258 = round(getValue(LM258));
		double lm043 = round( (lm175-lm176-lm177-lm178+lm179) * 0.30); 
		if ( (lm253 + lm258) >= getLimit(LIM_2)) {
			 return lm043>getLimit(LIM_2)?lm043:getLimit(LIM_2);
		}
		return lm043>(lm253 + lm258)?lm043:(lm253 + lm258);
	}
	
	public double computeLQ560() throws AonCoreException {
		double lq521 = round(getValue(LQ521));
		double lq552 = round(getValue(LQ552));
		double lq553 = round(getValue(LQ553));
		double lq554 = round(getValue(LQ554));
		double lq558 = round(getValue(LQ558));
		if (isChecked(C0017) || isChecked(C0018)) {
			if (isChecked(C0057) && !isChecked(C0063)) {
				return round( (lq553 * lq558 / 100) + (lq554 * 30 / 100) + (lq521 * 0));		
			}
			if (isChecked(C0063)) {
				if (lq552<=getLimit(LIM_1)){
					return round( lq552*15/100);			
				} else {
					return (getLimit(LIM_1)*15/100) + (lq552 - getLimit(LIM_1))*20/100;				
				}
			}
			return round( (lq553 * lq558 / 100) + (lq554 * 30 / 100));
		} else if (isChecked(C0019) ) {
			if (isChecked(C0006)) {
				if (lq552<=getLimit(LIM_1)){
					return round( lq552*25/100);			
				} else {
					return (getLimit(LIM_1)*25/100) + (lq552 - getLimit(LIM_1))*30/100;				
				}
			}
			if (isChecked(C0030) || isChecked(C0047)) {
				return round(getValue(LQ560));
			}
			if (isChecked(C0056)) {
				if (lq552<=getLimit(LIM_1)){
					return round( lq552*20/100);			
				} else {
					return (getLimit(LIM_1)*20/100) + (lq552 - getLimit(LIM_1))*25/100;				
				}
			}
			if (isChecked(C0063)) {
				if (lq552<=getLimit(LIM_1)){
					return round( lq552*15/100);			
				} else {
					return (getLimit(LIM_1)*15/100) + (lq552 - getLimit(LIM_1))*20/100;				
				}
			}
			if (isChecked(C0057)) {
				return round( ((lq552 - lq521) * lq558 / 100) + (lq554 * 30 / 100) + (lq521 *0));
			}
			return round( (lq553 * lq558 / 100) );			
		}
		return 0.0;
	}
	
}
