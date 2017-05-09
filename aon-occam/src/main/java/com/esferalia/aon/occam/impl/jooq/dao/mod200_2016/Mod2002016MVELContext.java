package com.esferalia.aon.occam.impl.jooq.dao.mod200_2016;


import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0001;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0002;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0003;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0004;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0005;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0006;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0012;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0013;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0015;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0017;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0018;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0019;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0021;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0022;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0024;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0030;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0034;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0036;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0038;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0046;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0047;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0048;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0049;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0057;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0058;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0063;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0064;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0066;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0071;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.LQ210;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.LQ480;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.LQ520;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.LQ521;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.LQ552;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.LQ553;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.LQ554;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.LQ558;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.LQ559;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.LQ560;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.LQ561;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.LQ562;

import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.esferalia.aon.occam.server.accounting.AccMiningMVELContext;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod2002016MVELContext extends AccMiningMVELContext {
	
	private static final int LIM_1 = 300000;
	private static final int LIM_2 = 1000000;
	private static final int LIM_3 = 25000;
	
	private Mod2002016 mod200;
	
	public Mod2002016MVELContext(Mod2002016 mod200,IAccMiningKeyAccept resolver) {
		super(resolver);
		this.mod200 = mod200;
	}
	
	protected Boolean isChecked(Mod2002016Key key) {
		return (Boolean) get(key.toString());
	}
	
	protected Double getValue(Mod2002016Key key) {
		return (Double) get(key.toString());
	}
	
	protected Double round(Mod2002016Key key) {
		return AonMathUtils.round(getValue(key));
	}
	private int getDays() {
		if ( mod200.getPeriodType() == 3) {
			return (int) AonDateUtils.getDaysBetweenDates(mod200.getPeriodStart(), mod200.getPeriodEnd());
		}
		return 365;
	}

	public double computeLQ558() throws AonCoreException {
		if ( isChecked(C0030) ) return round(getValue(LQ558));
		if ( isChecked(C0047) ) return round(getValue(LQ558));
		
		if ( isChecked(C0024)) return 28.0;
		if ( isChecked(C0063) ) return 15.0;
		if ( isChecked(C0066)) return 28.0;
		if ( isChecked(C0071)) return 15.0;
		
		if ( isChecked(C0046) ) return 28.0;
		if ( isChecked(C0021) ) return 28.0;

		if ( isChecked(C0001) ) return 10.0;
		if ( isChecked(C0002) ) return 25.0;
		if ( isChecked(C0003) ) return 1.0;
		if ( isChecked(C0004) ) return 1.0;
		if ( isChecked(C0005) ) return 25.0;
		
		if ( isChecked(C0017) ) return round(getValue(LQ558));
		if ( isChecked(C0018) ) return round(getValue(LQ558));
		if ( isChecked(C0019) ) return round(getValue(LQ558));
		if ( isChecked(C0006) ) return 25.0;
		if ( isChecked(C0006) && isChecked(C0057) && !isChecked(C0034)) return 25.0;
		if ( isChecked(C0006) && isChecked(C0034)) return 33.0;
		if ( isChecked(C0006) && !isChecked(C0034)) return 25.0;
		if ( isChecked(C0012) ) return 28.0;
		if ( isChecked(C0015) ) return 4.0;
		// --- 
		if ( isChecked(C0034) ) return 33.0;
		if ( isChecked(C0036) ) return 25.0;
		if ( isChecked(C0038) ) return 28.0;
		if ( isChecked(C0048) ) return 0.0;
		if ( isChecked(C0049) ) return round(getValue(LQ558));
		if ( isChecked(C0057) ) return 28.0;
		if ( isChecked(C0058) ) return 25.0;
		
		if ( isChecked(C0064) ) return 28.0;

		return 28.0;
	}
	private double getLimit(int limit) {
		return AonMathUtils.round( (double) limit * getDays() / 365);	
	}
	public double computeD1004() throws AonCoreException {
		double d1004 = getValue(Mod2002016Key.D1004);
		if (d1004>getLimit(LIM_3)){
			return getLimit(LIM_3);			
		} else {
			return d1004;				
		}
	}
	public double computeLQ562() throws AonCoreException {
		double lq521 = round(getValue(LQ521));
		double lq558 = round(getValue(LQ558));
		double lq559 = round(getValue(LQ559));
		double lq1035 = round(getValue(Mod2002016Key.LQ1035));
		double lq560 = round(getValue(LQ560));
		double lq210 = round(getValue(LQ210));
		double lq480 = round(getValue(LQ480));
		double lq561 = round(getValue(LQ561));
		double lq1330 = round(getValue(Mod2002016Key.LQ1330));
		
		if (isChecked(C0017) || isChecked(C0018) || isChecked(C0019)) {
			double lq562 = round(lq560+lq210-lq480-lq561);
			if (lq562 < 0) lq562 = 0;
			return lq562; 
		}
		
		if (lq1330 <= 0) return 0;
		
		if (isChecked(C0063)) {
			if (lq1330<=getLimit(LIM_1)){
				return round( lq1330*15/100);			
			} else {
				return (getLimit(LIM_1)*15/100) + (lq1330 - getLimit(LIM_1))*20/100;				
			}
		}
		if (isChecked(C0071)) {
			return round(lq1330 * lq558 / 100);
		}
		if (isChecked(C0022) &&  (isChecked(C0006) ||isChecked(C0013) || isChecked(C0063))) {
			return getValue(LQ562);
		}
		if (isChecked(C0006) && isChecked(C0034)) {
			return round(lq1330 * lq558 / 100);			
		}
		if (isChecked(C0006) && isChecked(C0046)) {
			return round(lq1330 * lq558 / 100);			
		}
		if (isChecked(C0006)) {
			if (lq1330<=getLimit(LIM_1)){
				return round( lq1330*25/100);			
			} else {
				return (getLimit(LIM_1)*25/100) + (lq1330 - getLimit(LIM_1))*28/100;				
			}
		}
		
		if (isChecked(C0015)) {
			if (isChecked(C0057)) {
				if (round(lq1330 - lq559 - lq521) > 0) {
					return round(((lq559+lq1035) * lq558 / 100) + (lq1330 - lq559 - lq521 - lq1035) * 28 / 100);	
				} else {
					return round((lq559 * lq558 / 100));
				}
			}
			return round(((lq559+lq1035) * lq558 / 100) + (lq1330 - lq559 - lq1035) * 28 / 100);
		}
		
		if (isChecked(C0030) || isChecked(C0047)) {
			return getValue(LQ562);
		}
	
		if (isChecked(C0012) || isChecked(C0064)) {
			double lq520 = round(getValue(LQ520));
			if (lq520>0) return round(lq520 * lq558 /100);
			return 0;
		}

		if (isChecked(C0057)) {
			if (round(lq1330-lq521) > 0) {
				return round((lq1330 -lq521)* lq558 /100);
			}
			return 0;
		}
		return round(lq1330 * lq558 / 100);
	}
	
	public double computeLQ550()  throws AonCoreException {
		double lq501 = round(getValue(Mod2002016Key.LQ501));
		double i0417 = round(getValue(Mod2002016Key.I0417));
		double d0418 = round(getValue(Mod2002016Key.D0418));
		double lq578 = round(getValue(Mod2002016Key.LQ578));
		double lq579 = round(getValue(Mod2002016Key.LQ579));
		double lq1029 = round(getValue(Mod2002016Key.LQ1029));
		double lq1030 = round(getValue(Mod2002016Key.LQ1030));
		double lq1031 = round(getValue(Mod2002016Key.LQ1031));
		
		if(!isChecked(C0022) && !isChecked(Mod2002016Key.C0009) && !isChecked(Mod2002016Key.C0010))
			return lq501 + i0417 - d0418;
		if(isChecked(C0022)){
			if(isChecked(Mod2002016Key.C0009) || isChecked(Mod2002016Key.C0010)){
				if((lq578 + lq1030 + lq1031) > 0)
					return lq578 + lq1030 + lq1031 + lq579;
				else return lq579;
			}
			if(lq578>0) return lq578 + lq579;
			else return lq579; 
		}
		if(isChecked(Mod2002016Key.C0009) || isChecked(Mod2002016Key.C0010)){
			if(isChecked(C0017) || isChecked(C0018) || isChecked(C0019)){
				return lq1029 +lq1030;
			} 
			return lq1029 +lq1030 + lq1031;
		}
		return lq501 + i0417 - d0418;
	}
	public double computeLM1249() throws AonCoreException {
		double lm1250 = round(getValue(Mod2002016Key.LM1250)); 
		double lm1251 = round(getValue(Mod2002016Key.LM1251));
		double lm1252 = round(getValue(Mod2002016Key.LM1252));
		double lm1253 = round(getValue(Mod2002016Key.LM1253));
		double lm1254 = round(getValue(Mod2002016Key.LM1254));
		double lm1256 = round(getValue(Mod2002016Key.LM1256));
		double lm1258 = round(getValue(Mod2002016Key.LM1258));
		double lm1259 = round(getValue(Mod2002016Key.LM1259));
		double lm1249 = round( (lm1250 - lm1251 - lm1252 - lm1253 + lm1254) * 0.30);
		if ( (lm1256 + lm1258 + lm1259) >= getLimit(LIM_2)) {
		 return lm1249>getLimit(LIM_2)?lm1249:getLimit(LIM_2);
		}
		return lm1249>(lm1256 + lm1258 +lm1259)?lm1249:(lm1256 + lm1258 +lm1259);
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
