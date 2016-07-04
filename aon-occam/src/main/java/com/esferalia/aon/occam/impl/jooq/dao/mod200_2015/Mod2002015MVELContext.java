package com.esferalia.aon.occam.impl.jooq.dao.mod200_2015;


import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0001;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0002;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0003;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0004;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0005;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0006;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0012;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0015;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0017;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0018;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0019;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0022;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0024;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0030;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0034;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0036;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0038;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0046;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0047;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0048;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0049;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0056;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0057;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0058;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.C0063;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.LQ210;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.LQ480;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.LQ520;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.LQ521;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.LQ552;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.LQ553;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.LQ554;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.LQ558;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.LQ559;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.LQ560;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.LQ561;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key.LQ562;

import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key;
import com.esferalia.aon.occam.server.accounting.AccMiningMVELContext;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod2002015MVELContext extends AccMiningMVELContext {
	
	private static final int LIM_1 = 300000;
//	private static final int LIM_2 = 1000000;
	
	private Mod2002015 mod200;
	
	public Mod2002015MVELContext(Mod2002015 mod200,IAccMiningKeyAccept resolver) {
		super(resolver);
		this.mod200 = mod200;
	}
	
	protected Boolean isChecked(Mod2002015Key key) {
		return (Boolean) get(key.toString());
	}
	
	protected Double getValue(Mod2002015Key key) {
		return (Double) get(key.toString());
	}
	
	protected Double round(Mod2002015Key key) {
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

		if ( isChecked(C0063) ) return 15.0;
		if(isChecked(Mod2002015Key.C0071)) return 15.0;
		if ( isChecked(C0024) && isChecked(Mod2002015Key.C0066)) return 28.0;
		if ( isChecked(C0024)) return 30.0;
		if ( isChecked(C0046) ) return 28.0;

		if ( isChecked(C0001) ) return 10.0;
		if ( isChecked(C0002) ) return 25.0;
		if ( isChecked(C0003) ) return 1.0;
		if ( isChecked(C0004) ) return 1.0;
		if ( isChecked(C0005) ) return 25.0;
		if ( isChecked(C0017) ) return round(getValue(LQ558));
		if ( isChecked(C0018) ) return round(getValue(LQ558));
		if ( isChecked(C0019) ) return round(getValue(LQ558));
		if ( isChecked(C0006) && isChecked(C0056)) return 25.0;
		if ( isChecked(C0006) && isChecked(C0034) && isChecked(Mod2002015Key.C0066)) return 28.0;
		if ( isChecked(C0006) && isChecked(C0034)) return 33.0;
		if ( isChecked(C0006) && !isChecked(C0034)) return 25.0;
		if ( isChecked(C0012) ) return 28.0;
		if ( isChecked(C0015) ) return 4.0;
		// --- 
		if ( isChecked(C0034) && isChecked(Mod2002015Key.C0066)) return 28.0;
		if ( isChecked(C0034) ) return 33.0;
		if ( isChecked(C0036) ) return 25.0;
		if ( isChecked(C0038) ) return 28.0;
		if ( isChecked(C0048) ) return 0.0;
		if ( isChecked(C0049) ) return round(getValue(LQ558));
		if ( isChecked(C0056) ) return 25.0;
		if ( isChecked(C0057) && (isChecked(C0056) || isChecked(C0006)))
			return 25.0;
		if( isChecked(C0057)) return 28.0;
		if ( isChecked(C0058) ) return 25.0;
		if(isChecked(Mod2002015Key.C0064)) return 28.0;

		return 28.0;
	}
	private double getLimit(int limit) {
		return AonMathUtils.round(limit * getDays() / 365);	
	}
		
	public double computeLQ562() throws AonCoreException {
		double lq521 = round(getValue(LQ521));
		double lq552 = round(getValue(LQ552));
		double lq558 = round(getValue(LQ558));
		double lq559 = round(getValue(LQ559));
		double lq1035 = round(getValue(Mod2002015Key.LQ1035));
		double lq560 = round(getValue(LQ560));
		double lq210 = round(getValue(LQ210));
		double lq480 = round(getValue(LQ480));
//		double lq408 = round(getValue(LQ408));
		double lq561 = round(getValue(LQ561));
		double lq1330 = round(getValue(Mod2002015Key.LQ1330));
		
		if (isChecked(C0017) || isChecked(C0018) || isChecked(C0019)) {
//			double lq562 = round(lq560+lq210-lq480+lq408-lq561);
			double lq562 = round(lq560+lq210-lq480-lq561);
			if (lq562 < 0) lq562 = 0;
			return lq562; 
		}
		
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
					return round(((lq559+lq1035) * lq558 / 100) + (lq552 - lq559 - lq521 - lq1035) * 28 / 100);	
				} else {
					return round((lq559 * lq558 / 100));
				}
			}
			return round(((lq559+lq1035) * lq558 / 100) + (lq552 - lq559 - lq1035) * 28 / 100);
		}
		if (isChecked(C0006)) {
			if (lq552<=getLimit(LIM_1)){
				return round( lq552*25/100);			
			} else {
				return (getLimit(LIM_1)*25/100) + (lq552 - getLimit(LIM_1))*28/100;				
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
	
	public double computeLQ550()  throws AonCoreException {
		double lq501 = round(getValue(Mod2002015Key.LQ501));
		double i0417 = round(getValue(Mod2002015Key.I0417));
		double d0418 = round(getValue(Mod2002015Key.D0418));
		double lq578 = round(getValue(Mod2002015Key.LQ578));
		double lq579 = round(getValue(Mod2002015Key.LQ579));
		double lq1029 = round(getValue(Mod2002015Key.LQ1029));
		double lq1030 = round(getValue(Mod2002015Key.LQ1030));
		double lq1031 = round(getValue(Mod2002015Key.LQ1031));
		
		if(!isChecked(C0022) && !isChecked(Mod2002015Key.C0009) && !isChecked(Mod2002015Key.C0010))
			return lq501 + i0417 + d0418;
		if(isChecked(C0022)){
			if(isChecked(Mod2002015Key.C0009) || isChecked(Mod2002015Key.C0010)){
				if((lq578 + lq1030 + lq1031) > 0)
					return lq578 + lq1030 + lq1031 + lq579;
				else return lq579;
			}
			if(lq578>0) return lq578 + lq579;
			else return lq579; 
		}
		if(isChecked(Mod2002015Key.C0009) || isChecked(Mod2002015Key.C0010)){
			if(isChecked(C0017) || isChecked(C0018) || isChecked(C0019)){
				return lq1029 +lq1030;
			} 
			return lq1029 +lq1030 + lq1031;
		}
		return lq501 + i0417 + d0418;
	}
	
	public double computeLM043() throws AonCoreException {
//		double lm175 = round(getValue(LM175));
//		double lm176 = round(getValue(LM176));
//		double lm177 = round(getValue(LM177));
//		double lm178 = round(getValue(LM178));
//		double lm179 = round(getValue(LM179));
//		double lm253 = round(getValue(LM253));
//		double lm258 = round(getValue(LM258));
//		double lm043 = round( (lm175-lm176-lm177-lm178+lm179) * 0.30); 
//		if ( (lm253 + lm258) >= getLimit(LIM_2)) {
//			 return lm043>getLimit(LIM_2)?lm043:getLimit(LIM_2);
//		}
//		return lm043>(lm253 + lm258)?lm043:(lm253 + lm258);
		return -99999.99;
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
