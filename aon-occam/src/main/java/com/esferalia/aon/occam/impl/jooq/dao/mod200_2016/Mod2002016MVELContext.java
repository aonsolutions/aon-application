package com.esferalia.aon.occam.impl.jooq.dao.mod200_2016;


import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0001;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0002;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0003;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0004;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0005;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0006;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0009;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0010;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0012;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0013;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0015;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0017;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0018;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0019;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0021;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0022;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0024;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0025;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0030;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0034;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0036;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0038;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0046;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0047;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0048;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0049;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0050;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0051;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key.C0052;
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

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.mvel2.MVEL;

import com.esferalia.aon.occam.api.model.CompanyParticipation;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016.BalanceType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod2002016MVELContext implements Map<String, Object> { // extends AccMiningMVELContext {
	
	
	private Map<String, AccountBalance> accounts;
	private Map<String, Object> context;
	private EnumMap<Mod2002016Key,String> expressionMap = new EnumMap<Mod2002016Key,String>(Mod2002016Key.class);
	private IAccMiningKeyAccept resolver;
	private Stack<Mod2002016Key> stack = new Stack<Mod2002016Key>();

	
	private static final int LIM_1 = 300000;
	private static final int LIM_2 = 1000000;
	private static final int LIM_3 = 25000;
	
	private Mod2002016 mod200;
	
	public Mod2002016MVELContext(Mod2002016 mod200,IAccMiningKeyAccept resolver) {
		this.context = new HashMap<String, Object>();
		this.accounts = new HashMap<String, AccountBalance>();
		this.resolver = resolver;
		this.mod200 = mod200;
	}
	
	public Map<String, AccountBalance> getAccounts() {
		return accounts;
	}
	
	public void setAccounts(Map<String, AccountBalance> accounts) {
		this.accounts = accounts;
	}
	
	public EnumMap<Mod2002016Key,String> getExpressionMap() {
		return expressionMap;
	}

	public void setExpressionMap(EnumMap<Mod2002016Key,String> expressionMap) {
		this.expressionMap = expressionMap;
	}
	
	// ***********************************************************************
	// java.util.Map inherited methods.
	// ***********************************************************************
	
	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.context.containsKey( key ) || resolver.acceptKey(key);
	}
	
	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return this.context.entrySet();
	}

	@Override
	public Object get(Object keyObject) {
		Mod2002016Key key = Mod2002016Key.valueOf((String) keyObject);
		return getContainsKey(key) ? context.get(keyObject) : evaluate(key);
	}
	
	private Boolean getContainsKey(Mod2002016Key key) {
		boolean a = this.context.containsKey(key.toString());
		boolean b = !expressionMap.containsKey(key);
		boolean c = stack.contains(key);
		return a && (b || c);
//		return this.context.containsKey(key.toString()) || stack.contains(key);
	}
	
	public Object evaluate(Mod2002016Key key) {
		if (expressionMap != null) {
			String exp = expressionMap.get(key);
			if (AonStringUtils.isNotEmpty(exp)) {
				Object ret =  mvelEval(key,exp);
				if (ret != null) {
					put(key.toString(), ret);
					return ret;
				}
			}
		}
		return new Double(0);
	}
	
	public Object evaluateExpression(Mod2002016Key key,String expression) {
		return mvelEval(key,expression);
	}
	public boolean validateExpression(Mod2002016Key key,String expression) {
		if (!this.context.containsKey(key.toString())) this.context.put(key.toString(), 0.0);
		return (Boolean) mvelEval(key,expression);
	}
		
	private Object mvelEval(Mod2002016Key key,String expression) {
		try {
			stack.push(key);
			return MVEL.eval( expression , this , this);
		} catch (Throwable t) {
			System.out.println("ERROR key: " + key  + " exp: " + expression);
			throw t;
		} finally {
			stack.pop();
		}
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> keySet() {
		return this.context.keySet();
	}

	@Override
	public Object put(String key, Object d) {
		return this.context.put(key, d);
	}
	
	@Override
	public void putAll(Map<? extends String,? extends  Object> m) {
		this.context.putAll(m);
	}

	@Override
	public Double remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return this.context.size();
	}

	@Override
	public Collection<Object> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void finalize() throws Throwable {
		context = null;
		super.finalize();
	}

	
	public Boolean isChecked(Mod2002016Key key) {
		return (Boolean) get(key.toString());
	}
	public Boolean isCooperativa() {
		return isChecked(C0017) || isChecked(C0018) || isChecked(C0019); 
	}
	public Boolean isLimitEnabled() {
		return !(isChecked(C0009) || isChecked(C0010) || isChecked(C0024) || isChecked(C0025));
	}
	
	public Boolean isBalanceNormal() {
		return (mod200.getBalanceType() == BalanceType.NORMAL); 
	}
	public Boolean isBalanceAbreviado() {
		return (mod200.getBalanceType() == BalanceType.ABREVIADO);
	}
	public Boolean isBalancePymes() {
		return (mod200.getBalanceType() == BalanceType.PYMES); 
	}
	public Boolean isPygNormal() {
		return (mod200.getPygType() == BalanceType.NORMAL); 
	}
	public Boolean isPygAbreviado() {
		return (mod200.getPygType() == BalanceType.ABREVIADO);
	}
	public Boolean isPygPymes() {
		return (mod200.getPygType() == BalanceType.PYMES); 
	}
	
	public Double getValue(Mod2002016Key key) {
		Object o = get(key.toString());
		if (o == null) {
			return 0.0;
		}
		return (Double) o;
	}
	
	private Double roundKey(Mod2002016Key key) {
		return AonMathUtils.round(getValue(key));
	}
	
	public boolean equals(double value1,double value2) throws AonCoreException {
		return AonMathUtils.equals(value1,value2);
	}
	public boolean isZero(double value) throws AonCoreException {
		return AonMathUtils.isZero(value);
	}
	public boolean isNotZero(double value) throws AonCoreException {
		return AonMathUtils.isNotZero(value);
	}

	private int getDays() {
		if ( mod200.getPeriodType() == 3) {
			return (int) AonDateUtils.getDaysBetweenDates(mod200.getPeriodStart(), mod200.getPeriodEnd());
		}
		return 365;
	}
	
	public Boolean isBalNormal() {
		return (Boolean) get( C0050 );
	}
	public Boolean isBalAbreviado() {
		return (Boolean) get( C0051 );
	}
	public Boolean isBalPymes() {
		return (Boolean) get( C0052 );
	}

	public double computeC0027() throws AonCoreException {
		double lq552 = roundKey(LQ552);
		return (AonMathUtils.isGreatherThanZero(lq552))?0.0:1.0;
	}

	public double computeLQ558() throws AonCoreException {
		if ( isChecked(C0030) ) return roundKey(LQ558);
		if ( isChecked(C0047) ) return roundKey(LQ558);
		
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
		
		if ( isChecked(C0017) ) return roundKey(LQ558);
		if ( isChecked(C0018) ) return roundKey(LQ558);
		if ( isChecked(C0019) ) return roundKey(LQ558);
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
		if ( isChecked(C0049) ) return roundKey(LQ558);
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
		if ( AonMathUtils.isZero(d1004)) {
			return 0.0;
		} 	
		if (d1004>getLimit(LIM_3)){
			return getLimit(LIM_3);			
		} else {
			return d1004;				
		}
	}
	public double computeLQ562() throws AonCoreException {
		double lq521 = roundKey(LQ521);
		double lq558 = roundKey(LQ558);
		double lq559 = roundKey(LQ559);
		double lq1035 = roundKey(Mod2002016Key.LQ1035);
		double lq560 = roundKey(LQ560);
		double lq210 = roundKey(LQ210);
		double lq480 = roundKey(LQ480);
		double lq561 = roundKey(LQ561);
		double lq1330 = roundKey(Mod2002016Key.LQ1330);
		
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
			double lq520 = roundKey(LQ520);
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
		double lq501 = roundKey(Mod2002016Key.LQ501);
		double i0417 = roundKey(Mod2002016Key.I0417);
		double d0418 = roundKey(Mod2002016Key.D0418);
		double lq578 = roundKey(Mod2002016Key.LQ578);
		double lq579 = roundKey(Mod2002016Key.LQ579);
		double lq1029 = roundKey(Mod2002016Key.LQ1029);
		double lq1030 = roundKey(Mod2002016Key.LQ1030);
		double lq1031 = roundKey(Mod2002016Key.LQ1031);
		
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
		double lm1250 = roundKey(Mod2002016Key.LM1250); 
		double lm1251 = roundKey(Mod2002016Key.LM1251);
		double lm1252 = roundKey(Mod2002016Key.LM1252);
		double lm1253 = roundKey(Mod2002016Key.LM1253);
		double lm1254 = roundKey(Mod2002016Key.LM1254);
		double lm1256 = roundKey(Mod2002016Key.LM1256);
		double lm1258 = roundKey(Mod2002016Key.LM1258);
		double lm1259 = roundKey(Mod2002016Key.LM1259);
		double lm1249_1 = round( (lm1250 - lm1251 - lm1252 - lm1253 + lm1254) * 0.30);
		double lm1249_2 = round(lm1256+lm1258+lm1259);
		if ( lm1249_2 >= getLimit(LIM_2)) {
		 return lm1249_1>getLimit(LIM_2)?lm1249_1:getLimit(LIM_2);
		}
		return lm1249_1>lm1249_2?lm1249_1:lm1249_2;
	}
	
	public double computeLQ560() throws AonCoreException {
		double lq521 = roundKey(LQ521);
		double lq552 = roundKey(LQ552);
		double lq553 = roundKey(LQ553);
		double lq554 = roundKey(LQ554);
		double lq558 = roundKey(LQ558);
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
				return roundKey(LQ560);
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
	
	private double computePreBN599() throws AonCoreException {
		double BN592 = roundKey(Mod2002016Key.BN592);
		double BN595 = roundKey(Mod2002016Key.BN595);
		double BN596 = roundKey(Mod2002016Key.BN596);
		double BN597 = roundKey(Mod2002016Key.BN597);
		return (BN592-BN595-BN596-BN597);
	}
	public double computeBN599() throws AonCoreException {
		double BN599 = computePreBN599();
		if (isChecked(Mod2002016Key.C0028)) {
			double TR625 = roundKey(Mod2002016Key.TR625);
			return round(TR625 * BN599 / 100);
		}
		return round(BN599);
	}
	
	public double computeTR420() throws AonCoreException {
		if (isChecked(Mod2002016Key.C0028)) {
			double TR626 = roundKey(Mod2002016Key.TR626);
			return round(TR626 * computePreBN599() / 100);
		}
		return 0.0;
	}
	public double computeTR421() throws AonCoreException {
		if (isChecked(Mod2002016Key.C0028)) {
			double TR627 = roundKey(Mod2002016Key.TR627);
			return round(TR627 * computePreBN599() / 100);
		}
		return 0.0;
	}
	public double computeTR426() throws AonCoreException {
		if (isChecked(Mod2002016Key.C0028)) {
			double TR628 = roundKey(Mod2002016Key.TR628);
			return round(TR628 * computePreBN599() / 100);
		}
		return 0.0;
	}
	public double computeTR427() throws AonCoreException {
		if (isChecked(Mod2002016Key.C0028)) {
			double TR629 = roundKey(Mod2002016Key.TR629);
			return round(TR629 * computePreBN599() / 100);
		}
		return 0.0;
	}
	
	public double computeP1501() throws AonCoreException {
		if (mod200.getParticipationsOut() != null && mod200.getParticipationsOut().size() > 0) {
			double x = 0.0;
			for (CompanyParticipation p : mod200.getParticipationsOut()) {
				x += p.getNominalValue();
			}
			return round( x ); 
		}
		return 0.0;
	}

	public double computeP1502() throws AonCoreException {
		if (mod200.getParticipationsOut() != null && mod200.getParticipationsOut().size() > 0) {
			double x = 0.0;
			for (CompanyParticipation p : mod200.getParticipationsOut()) {
				x += p.getBookValue();
			}
			return round( x ); 
		}
		return 0.0;
	}

	public double computeP1503() throws AonCoreException {
		if (mod200.getParticipationsOut() != null && mod200.getParticipationsOut().size() > 0) {
			double x = 0.0;
			for (CompanyParticipation p : mod200.getParticipationsOut()) {
				x += p.getIncomes();
			}
			return round( x ); 
		}
		return 0.0;
	}

	public double computeP1504() throws AonCoreException {
		if (mod200.getParticipationsOut() != null && mod200.getParticipationsOut().size() > 0) {
			double x = 0.0;
			for (CompanyParticipation p : mod200.getParticipationsOut()) {
				x += p.getaValue();
			}
			return round( x ); 
		}
		return 0.0;
	}

	public double computeP1505() throws AonCoreException {
		if (mod200.getParticipationsOut() != null && mod200.getParticipationsOut().size() > 0) {
			double x = 0.0;
			for (CompanyParticipation p : mod200.getParticipationsOut()) {
				x += p.getbValue();
			}
			return round( x ); 
		}
		return 0.0;
	}

	public double computeP1506() throws AonCoreException {
		if (mod200.getParticipationsOut() != null && mod200.getParticipationsOut().size() > 0) {
			double x = 0.0;
			for (CompanyParticipation p : mod200.getParticipationsOut()) {
				x += p.getccValue();
			}
			return round( x ); 
		}
		return 0.0;
	}

	public double computeP1507() throws AonCoreException {
		if (mod200.getParticipationsOut() != null && mod200.getParticipationsOut().size() > 0) {
			double x = 0.0;
			for (CompanyParticipation p : mod200.getParticipationsOut()) {
				x += p.getcValue();
			}
			return round( x ); 
		}
		return 0.0;
	}

	public double computeP1508() throws AonCoreException {
		if (mod200.getParticipationsOut() != null && mod200.getParticipationsOut().size() > 0) {
			double x = 0.0;
			for (CompanyParticipation p : mod200.getParticipationsOut()) {
				x += p.getdValue();
			}
			return round( x ); 
		}
		return 0.0;
	}
	
	// ***********************************************************************
	// Métodos disponibles en las expresiones MVEL.
	
	/**
	 * Saldo Acreedor (Haber - Debe) del sumatorio de las cuentas indicadas en <i>accounts</i>.
	 * @param accounts
	 * @return
	 * @throws AonCoreException
	 */
	public double sab(int[] accounts  ) throws AonCoreException {
		return getCreditBalance(accounts);
	}

	/**
	 * Saldo Acreedor Positivo (Haber - Debe) de la cuenta indicada en <i>account</i>.
	 * @param accounts
	 * @return El dato si es positivo, en otro caso cero.
	 * @throws AonCoreException
	 */
	public double sabPositivo(int[] accounts) throws AonCoreException {
		double d = sab(accounts); 
		return d>0?d:0;
	}
	
	/**
	 * Saldo Acreedor (Haber - Debe) de la cuenta indicada en <i>account</i>.
	 * @param accounts
	 * @return
	 * @throws AonCoreException
	 */
	public double sab(int account) throws AonCoreException {
		return getCreditBalance(new int[]{account});
	}
	
	/**
	 * Saldo Acreedor Positivo (Haber - Debe) de la cuenta indicada en <i>account</i>.
	 * @param accounts
	 * @return El dato si es positivo, en otro caso cero.
	 * @throws AonCoreException
	 */
	public double sabPositivo(int account) throws AonCoreException {
		double d = sab(account); 
		return d>0?d:0;
	}

	/**
	 * Saldo Deudor (Debe - Haber) del sumatorio de las cuentas indicadas en <i>accounts</i>.
	 * @param accounts
	 * @return
	 * @throws AonCoreException
	 */
	public double sdb(int[] accounts) throws AonCoreException {
		return getDebitBalance(accounts);
	}
	/**
	 * Saldo Deudor Positivo (Haber - Debe) de la cuenta indicada en <i>account</i>.
	 * @param accounts
	 * @return El dato si es positivo, en otro caso cero.
	 * @throws AonCoreException
	 */
	public double sdbPositivo(int[] accounts) throws AonCoreException {
		double d = sdb(accounts); 
		return d>0?d:0;
	}
	/**
	 * Saldo Deudor (Debe - Haber) de la cuenta indicada en <i>account</i>.
	 * @param accounts
	 * @return
	 * @throws AonCoreException
	 */
	public double sdb(int account) throws AonCoreException {
		return getDebitBalance(new int[]{account});
	}
	/**
	 * Saldo Deudor Positivo (Haber - Debe) de la cuenta indicada en <i>account</i>.
	 * @param accounts
	 * @return El dato si es positivo, en otro caso cero.
	 * @throws AonCoreException
	 */
	public double sdbPositivo(int account) throws AonCoreException {
		double d = sdb(account); 
		return d>0?d:0;
	}
	
	/**
	 * Saldo Acreedor (Haber - Debe) del sumatorio de las cuentas indicadas en <i>accounts</i> antes del asiento de explotación.
	 * @param accounts
	 * @return
	 * @throws AonCoreException
	 */
	public double sap(int[] accounts) throws AonCoreException {
		return getCreditPyG(accounts);
	}

	/**
	 * Saldo Acreedor Positivo (Haber - Debe) de la cuenta indicada en <i>account</i> antes del asiento de explotación.
	 * @param accounts
	 * @return El dato si es positivo, en otro caso cero.
	 * @throws AonCoreException
	 */
	public double sapPositivo(int[]  accounts) throws AonCoreException {
		double d = sap(accounts); 
		return d>0?d:0;
	}
	
	/**
	 * Saldo Acreedor (Haber - Debe) de la cuenta indicada en <i>account</i> antes del asiento de explotación.
	 * @param accounts
	 * @return
	 * @throws AonCoreException
	 */
	public double sap(int account) throws AonCoreException {
		return getCreditPyG(new int[]{account});
	}
	
	/**
	 * Saldo Acreedor Positivo (Haber - Debe) de la cuenta indicada en <i>account</i> antes del asiento de explotación.
	 * @param accounts
	 * @return El dato si es positivo, en otro caso cero.
	 * @throws AonCoreException
	 */
	public double sapPositivo(int account) throws AonCoreException {
		double d = sap(account); 
		return d>0?d:0;
	}

	/**
	 * Saldo Deudor (Debe - Haber) del sumatorio de las cuentas indicadas en <i>accounts</i> antes del asiento de explotación.
	 * @param accounts
	 * @return
	 * @throws AonCoreException
	 */
	public double sdp(int[]  accounts  ) throws AonCoreException {
		return getDebitPyG(accounts);
	}
	/**
	 * Saldo Deudor Positivo (Haber - Debe) de la cuenta indicada en <i>account</i> antes del asiento de explotación.
	 * @param accounts
	 * @return El dato si es positivo, en otro caso cero.
	 * @throws AonCoreException
	 */
	public double sdpPositivo(int[] accounts) throws AonCoreException {
		double d = sdp(accounts); 
		return d>0?d:0;
	}
	/**
	 * Saldo Deudor (Debe - Haber) de la cuenta indicada en <i>account</i> antes del asiento de explotación.
	 * @param accounts
	 * @return
	 * @throws AonCoreException
	 */
	public double sdp(int account) throws AonCoreException {
		return getDebitPyG(new int[]{account});
	}
	/**
	 * Saldo Deudor Positivo (Haber - Debe) de la cuenta indicada en <i>account</i> antes del asiento de explotación.
	 * @param accounts
	 * @return El dato si es positivo, en otro caso cero.
	 * @throws AonCoreException
	 */
	public double sdpPositivo(int account) throws AonCoreException {
		double d = sdp(account); 
		return d>0?d:0;
	}
	/**
	 * Redondeo a dos decimales.
	 * @param value
	 * @return El dato a redondear.
	 * @throws AonCoreException
	 */
	public double round(double value) throws AonCoreException {
		return AonMathUtils.round(value); 
	}

	//------------------------------------------------------------------------------------------
	
	private double getCreditBalance(int[] accounts  ) throws AonCoreException {
		double d = 0.0;
		for (int account : accounts) {
			String acc = Integer.toString(account);
			if (this.accounts.containsKey(acc)) {
				AccountBalance ab = this.accounts.get(acc);
				d = d + ab.getCreditBalance();
			}
		}
		return d;
	}
	
	private double getDebitBalance(int[] accounts ) throws AonCoreException {
		double d = 0.0;
		for (int account : accounts) {
			String acc = Integer.toString(account);
			if (this.accounts.containsKey(acc)) {
				AccountBalance ab = this.accounts.get(acc);
				d = d + ab.getDebitBalance();
			}
		}
		return d;
	}
	
	private double getCreditPyG(int[] accounts  ) throws AonCoreException {
		double d = 0.0;
		for (int account : accounts) {
			String acc = Integer.toString(account);
			if (this.accounts.containsKey(acc)) {
				AccountBalance ab = this.accounts.get(acc);
				d = d + ab.getCreditPyG();
			}
		}
		return d;
	}
	
	private double getDebitPyG(int[] accounts ) throws AonCoreException {
		double d = 0.0;
		for (int account : accounts) {
			String acc = Integer.toString(account);
			if (this.accounts.containsKey(acc)) {
				AccountBalance ab = this.accounts.get(acc);
				d = d + ab.getDebitPyG();
			}
		}
		return d;
	}
			
}
