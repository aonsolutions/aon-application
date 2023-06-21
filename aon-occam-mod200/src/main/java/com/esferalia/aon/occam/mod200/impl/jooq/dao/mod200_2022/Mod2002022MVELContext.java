package com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2022;

import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0001;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0002;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0003;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0004;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0005;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0006;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0009;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0010;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0012;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0013;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0014;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0015;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0017;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0018;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0019;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0021;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0022;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0024;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0025;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0030;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0034;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0036;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0038;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0046;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0047;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0048;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0049;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0056;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0057;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0058;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0063;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0064;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0066;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0069;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0071;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0072;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0078;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0079;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0080;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0081;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0082;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0083;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0084;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.LQ520;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.LQ521;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.LQ552;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.LQ553;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.LQ554;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.LQ558;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.LQ559;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.LQ560;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.LQ562;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.mvel2.MVEL;

import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;
import com.esferalia.aon.occam.mod200.api.model.BalanceType;
import com.esferalia.aon.occam.mod200.api.model.EcpnType;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyParticipation;
import com.esferalia.aon.occam.mod200.api.model.UteParticipation;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022KeyDC;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod2002022MVELContext implements Map<String, Object> { 
	
	private Map<String, AccountBalance> accounts;
	private Map<String, Object> context;
	private EnumMap<Mod2002022Key,String> expressionMap = new EnumMap<Mod2002022Key,String>(Mod2002022Key.class);	
	private IAccMiningKeyAccept resolver;
	private Stack<IMod200Key> stack = new Stack<IMod200Key>();
	
	private static final int LIM_1 = 300000;
	private static final int LIM_2 = 1000000;
	private static final int LIM_3 = 25000;
	
	private Mod2002022 mod200;
	
	public Mod2002022MVELContext(Mod2002022 mod200,IAccMiningKeyAccept resolver) {
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
	
	public EnumMap<Mod2002022Key,String> getExpressionMap() {
		return expressionMap;
	}

	public void setExpressionMap(EnumMap<Mod2002022Key,String> expressionMap) {
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

		// Comprobar si la clave es de Mod2002022Key
		IMod200Key key = Mod2002022Key.safeValueOf((String) keyObject);
		// Si no lo es comprobar si es de Mod2002022KeyDC
		if (key == null)
			key = Mod2002022KeyDC.safeValueOf((String) keyObject);
			
		return getContainsKey(key) ? context.get(keyObject) : evaluate(key);
		
	}
	
	private Boolean getContainsKey(IMod200Key key) {
		boolean a = this.context.containsKey(key.toString());
		boolean b = !expressionMap.containsKey(key);
		boolean c = stack.contains(key);
		return a && (b || c);
	}
	
	public Object evaluate(IMod200Key key) {
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
		return 0.0; 		
	}
	
	public Object evaluateExpression(Mod2002022Key key,String expression) {
		return mvelEval(key,expression);
	}
	public boolean validateExpression(Mod2002022Key key,String expression) {
		if (!this.context.containsKey(key.toString())) this.context.put(key.toString(), 0.0);
		return (Boolean) mvelEval(key,expression);
	}
		
	private Object mvelEval(IMod200Key key,String expression) {
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

	// ***********************************************************************
	// Métodos disponibles en las expresiones MVEL.
	// ***********************************************************************
	
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
	
	public Boolean isChecked(Mod2002022Key key) {
		return (Boolean) get(key.toString());
	}
	public Boolean isCooperativa() {
		return isChecked(C0017) || isChecked(C0018) || isChecked(C0019); 
	}
	public Boolean isGroup() {
		return isChecked(C0009) || isChecked(C0010); 
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
	
	public Boolean isEcpnNormal() {
		return (mod200.getEcpnType() == EcpnType.NORMAL); 
	}
	public Boolean isEcpnPymes() {
		return (mod200.getEcpnType() == EcpnType.PYMES); 
	}
	
	public Double getValue(Mod2002022Key key) {
		Object o = get(key.toString());
		if (o == null) {
			return 0.0;
		}
		return (Double) o;
	}
	
	private Double roundKey(Mod2002022Key key) {
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
	
	// Tipo de Gravamen (Casilla 558)
	public double computeLQ558() throws AonCoreException {
		
		if ( isChecked(C0030) || 
			 isChecked(C0047) ||
			 isChecked(C0078) || 
			 isChecked(C0081) ||
			 isChecked(C0082) ||
			 isChecked(C0056) ||
			 isChecked(C0069) ||
			 isChecked(C0084) )
			return roundKey(LQ558);
		
		if ( isChecked(C0083) ) return 15.0;
		if ( isChecked(C0063) ) return 15.0;
		if ( isChecked(C0066) ) return 25.0;
		if ( isChecked(C0071) ) return 15.0;
		if ( isChecked(C0024) ) return 30.0;	
		
		if ( isChecked(C0046) ) return 25.0;
		if ( isChecked(C0021) ) return 25.0;

		if ( isChecked(C0001) ) return 10.0;
		if ( isChecked(C0002) ) return 25.0;
		if ( isChecked(C0003) ) return 1.0;
		if ( isChecked(C0004) ) return 1.0;
		if ( isChecked(C0005) ) return 25.0;
		
		if ( isChecked(C0009) ) return roundKey(LQ558);
		if ( isChecked(C0010) ) return roundKey(LQ558);
		
		if ( isChecked(C0017) ) return roundKey(LQ558);
		if ( isChecked(C0018) ) return roundKey(LQ558);
		
		if ( isChecked(C0013) && (getValue(Mod2002022Key.UT060) == 100.0) ) return 0.0;
		
		if ( isChecked(C0006) && isChecked(C0034)) return 30.0;
		if ( isChecked(C0057) && isChecked(C0006) && isChecked(C0034)) return 30.0;
		if ( isChecked(C0015) || isChecked(C0079) ) return 4.0;
		if ( isChecked(C0006) ) return 25.0;
		
		if ( isChecked(C0012) ) return 25.0;
		if ( isChecked(C0014) ) return 0.0;
		 
		if ( isChecked(C0034) ) return 30.0;
		if ( isChecked(C0036) ) return 25.0;
		if ( isChecked(C0038) ) return 25.0;
		
		if ( isChecked(C0048) ) return 0.0;
		if ( isChecked(C0049) ) return roundKey(LQ558);	
		if ( isChecked(C0057) && !isChecked(C0006) && isChecked(C0034)) return 30.0;
		if ( isChecked(C0057) ) return 25.0;
		if ( isChecked(C0058) ) return 25.0;

		if ( isChecked(C0064) ) return 25.0;
		
		if ( isChecked(C0080) ) return 25.0;
 
		return 25.0;
	}
	
	private double getLimit(int limit) {
		return AonMathUtils.round( (double) limit * getDays() / 365);	
	}
	
	public double computeD1004(double d1004) throws AonCoreException {
		if (d1004>getLimit(LIM_3)){
			return getLimit(LIM_3);			
		} else {
			return d1004;				
		}
	}
	
	// Cuota Integra (Casilla 562)
	public double computeLQ562() throws AonCoreException {
		
		double lq558 = roundKey(LQ558);
		double lq1330 = roundKey(Mod2002022Key.LQ1330);
		
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
			return round(lq1330 * lq558 / 100);
		}
		
		if (isChecked(C0015) || isChecked(C0079)) {
			double lq559 = roundKey(LQ559);
			double lq521 = roundKey(LQ521);
			if (isChecked(C0057)) {
				if (round(lq1330 - lq559 - lq521) > 0) {
					return round(((lq559) * lq558 / 100) + (lq1330 - lq559 - lq521) * 25 / 100);	
				} else {
					return round((lq559 * lq558 / 100));
				}
			}
			return round(((lq559) * lq558 / 100) + (lq1330 - lq559) * 25 / 100);
		}
		
		if (isChecked(C0030) || isChecked(C0047)) {
			return getValue(LQ562);
		}
	
		if (isChecked(C0012) || isChecked(C0064)) {
			double lq520 = roundKey(LQ520);
			if (lq520>0) return round(lq520 * lq558 /100);
			return 0;
		}

		// FALTA - REVISAR CUANDO ESTE DISPONIBLE EL SERVICIO DE VALIDACION
//		Si marca la casilla 00057 y Siempre que la (01330 - 00521) > 0:
//			Cuando esté combinada con la clave 00006 (empresa de reducida dimensión), o clave 00063 (aplicable el tipo de gravamen reducido para entidades de nueva creación), o clave 00071, o clave 00083 => sustituir todas las referencias a la 01330 por (01330- 00521)		
		if (isChecked(C0057)) {
			double lq521 = roundKey(LQ521);
			if (round(lq1330-lq521) > 0) {
				return round((lq1330 -lq521)* lq558 /100);
			}
			return 0;
		}
		// Cálculo de la cuota integra con caracter general
		return round(lq1330 * lq558 / 100);
	}
	
	public double computeLQ550() throws AonCoreException {
		if (isChecked(C0022) || isChecked(Mod2002022Key.C0009) || isChecked(Mod2002022Key.C0010)) {
			if (isChecked(C0022)) {
				double lq578 = roundKey(Mod2002022Key.LQ578);
				double lq579 = roundKey(Mod2002022Key.LQ579);
				if (!isChecked(Mod2002022Key.C0009) && !isChecked(Mod2002022Key.C0010)) {
					if (lq578 > 0) {
						return lq578 + lq579;
					} else {
						return lq579;
					}
				} else {
					double lq1030 = roundKey(Mod2002022Key.LQ1030);
					double lq1031 = roundKey(Mod2002022Key.LQ1031);
					if ((lq578 + lq1030 + lq1031) > 0) {
						return lq578 + lq1030 + lq1031 + lq579;
					} else {
						return lq579;
					}
				}
			} else {
				double lq1029 = roundKey(Mod2002022Key.LQ1029);
				double lq1030 = roundKey(Mod2002022Key.LQ1030);
				if (isCooperativa()) {
					return lq1029 + lq1030;
				}
				double lq1031 = roundKey(Mod2002022Key.LQ1031);
				return lq1029 + lq1030 + lq1031;
			}
		}
		double lq501 = roundKey(Mod2002022Key.LQ501);
		double i0417 = roundKey(Mod2002022Key.I0417);
	 	double d0418 = roundKey(Mod2002022Key.D0418);
		return lq501 + i0417 - d0418;
	}
	
	public double computeLM1249() throws AonCoreException {
		double lm1250 = roundKey(Mod2002022Key.LM1250); 
		double lm1251 = roundKey(Mod2002022Key.LM1251);
		double lm1252 = roundKey(Mod2002022Key.LM1252);
		double lm1253 = roundKey(Mod2002022Key.LM1253);
		double lm1254 = roundKey(Mod2002022Key.LM1254);
		double lm1256 = roundKey(Mod2002022Key.LM1256);
		double lm1258 = roundKey(Mod2002022Key.LM1258);
		double lm1259 = roundKey(Mod2002022Key.LM1259);
		double lm1249_1 = round((lm1250 - lm1251 - lm1252 - lm1253 + lm1254) * 0.30);
		double lm1249_2 = round(lm1256+lm1258+lm1259);
		if (isChecked(C0072)) {
			return lm1249_2;
		}
		else {
			if (lm1249_2 >= getLimit(LIM_2)) {
			 return lm1249_1>getLimit(LIM_2)?lm1249_1:getLimit(LIM_2);
			}
			return lm1249_1>lm1249_2?lm1249_1:lm1249_2;
		}
	}
	
	public double computeLQ560() throws AonCoreException {
		double lq521 = roundKey(LQ521);
		double lq552 = roundKey(LQ552);
		double lq553 = roundKey(LQ553);
		double lq554 = roundKey(LQ554);
		double lq558 = roundKey(LQ558);
		if (isChecked(C0017) || isChecked(C0018)) {
			if (isChecked(C0071)) {
				return round(lq552*15/100);
			}
			if (isChecked(C0057) && !isChecked(C0063)) {
				return round( (lq553 * lq558 / 100) + (lq554 * 25 / 100) + (lq521 * 0));		
			}
			if (isChecked(C0063)) {
				if (lq552<=getLimit(LIM_1)){
					return round( lq552*15/100);			
				} else {
					return (getLimit(LIM_1)*15/100) + (lq552 - getLimit(LIM_1))*20/100;				
				}
			}
			if (lq558 == 20 || lq558 == 25) {
				return round( (lq553 * lq558 / 100) + (lq554 * 25 / 100));
			}
		} else if (isChecked(C0019) ) {
			if (isChecked(C0071)) {
				return round(lq552*15/100);
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
				return round( ((lq552 - lq521) * lq558 / 100) + (lq521 *0));
			}
			return round( (lq553 * lq558 / 100) );			
		}
		return 0.0;
	}
	
	private double computePreBN599() throws AonCoreException {
		double BN592  = roundKey(Mod2002022Key.BN592 );
		double BN1785 = roundKey(Mod2002022Key.BN1785);
		double BN1786 = roundKey(Mod2002022Key.BN1786);
		double BN1787 = roundKey(Mod2002022Key.BN1787);
		double BN1788 = roundKey(Mod2002022Key.BN1788);
		double BN1789 = roundKey(Mod2002022Key.BN1789);
		double BN1790 = roundKey(Mod2002022Key.BN1790);
		double BN1791 = roundKey(Mod2002022Key.BN1791);
		double BN1792 = roundKey(Mod2002022Key.BN1792);
		double BN1793 = roundKey(Mod2002022Key.BN1793);
		double BN1794 = roundKey(Mod2002022Key.BN1794);
		double BN1795 = roundKey(Mod2002022Key.BN1795);
		double BN1796 = roundKey(Mod2002022Key.BN1796);
		double BN1797 = roundKey(Mod2002022Key.BN1797);
		double BN1798 = roundKey(Mod2002022Key.BN1798);
		double BN597  = roundKey(Mod2002022Key.BN597 );
		double BN1799 = roundKey(Mod2002022Key.BN1799);
		return BN592-BN1785-BN1786-BN1787-BN1788-BN1789-BN1790-BN1791-BN1792-BN1793-BN1794-BN1795-BN1796-BN1797-BN1798-BN597-BN1799;
	}
	
	public double computeBN599() throws AonCoreException {	
		if (isChecked(Mod2002022Key.C0028)) {
			double TR625 = roundKey(Mod2002022Key.TR625);
			return round(TR625 * computePreBN599() / 100);
		} else {
		    return computePreBN599();
		}
	}
	
	public double computeTR420() throws AonCoreException {
		if (isChecked(Mod2002022Key.C0028)) {
			double TR626 = roundKey(Mod2002022Key.TR626);
			return round(TR626 * computePreBN599() / 100);
		}
		return 0.0;
	}
	public double computeTR421() throws AonCoreException {
		if (isChecked(Mod2002022Key.C0028)) {
			double TR627 = roundKey(Mod2002022Key.TR627);
			return round(TR627 * computePreBN599() / 100);
		}
		return 0.0;
	}
	public double computeTR426() throws AonCoreException {
		if (isChecked(Mod2002022Key.C0028)) {
			double TR628 = roundKey(Mod2002022Key.TR628);
			return round(TR628 * computePreBN599() / 100);
		}
		return 0.0;
	}
	public double computeTR427() throws AonCoreException {
		if (isChecked(Mod2002022Key.C0028)) {
			double TR629 = roundKey(Mod2002022Key.TR629);
			return round(TR629 * computePreBN599() / 100);
		}
		return 0.0;
	}
	
	public double computeP1501() throws AonCoreException {
		if (mod200.getParticipationsOut() != null && mod200.getParticipationsOut().size() > 0) {
			double x = 0.0;
			for (Mod200CompanyParticipation p : mod200.getParticipationsOut()) {
				x += p.getNominalValue();
			}
			return round( x ); 
		}
		return 0.0;
	}
	
	public double computeP1502() throws AonCoreException {
		if (mod200.getParticipationsOut() != null && mod200.getParticipationsOut().size() > 0) {
			double x = 0.0;
			for (Mod200CompanyParticipation p : mod200.getParticipationsOut()) {
				x += p.getBookValue();
			}
			return round( x ); 
		}
		return 0.0;
	}
	
	public double computeP1503() throws AonCoreException {
		if (mod200.getParticipationsOut() != null && mod200.getParticipationsOut().size() > 0) {
			double x = 0.0;
			for (Mod200CompanyParticipation p : mod200.getParticipationsOut()) {
				x += p.getIncomes();
			}
			return round( x ); 
		}
		return 0.0;
	}

	public double computeP1504() throws AonCoreException {
		if (mod200.getParticipationsOut() != null && mod200.getParticipationsOut().size() > 0) {
			double x = 0.0;
			for (Mod200CompanyParticipation p : mod200.getParticipationsOut()) {
				x += p.getValueCorrection();
			}
			return round( x ); 
		}
		return 0.0;
	}

	public double computeP1506() throws AonCoreException {
		if (mod200.getParticipationsOut() != null && mod200.getParticipationsOut().size() > 0) {
			double x = 0.0;
			for (Mod200CompanyParticipation p : mod200.getParticipationsOut()) {
				x += p.getAccountingElimination();
			}
			return round( x ); 
		}
		return 0.0;
	}
	
	public double computeP1809() {
		if (mod200.getParticipationsOut() != null && mod200.getParticipationsOut().size() > 0) {
			double x = 0.0;
			for (Mod200CompanyParticipation p : mod200.getParticipationsOut()) {
				x += p.getValuesElimination();
			}
			return round( x ); 
		}
		return 0.0;		
	}
	
	public double computeP1810() {
		if (mod200.getParticipationsOut() != null && mod200.getParticipationsOut().size() > 0) {
			double x = 0.0;
			for (Mod200CompanyParticipation p : mod200.getParticipationsOut()) {
				x += p.getAdjustmentDecrease();
			}
			return round( x ); 
		}
		return 0.0;		
	}

	public double computeP1507() throws AonCoreException {
		if (mod200.getParticipationsOut() != null && mod200.getParticipationsOut().size() > 0) {
			double x = 0.0;
			for (Mod200CompanyParticipation p : mod200.getParticipationsOut()) {
				x += p.getCorrectionEffect();
			}
			return round( x ); 
		}
		return 0.0;
	}

	public double computeP1508() throws AonCoreException {
		if (mod200.getParticipationsOut() != null && mod200.getParticipationsOut().size() > 0) {
			double x = 0.0;
			for (Mod200CompanyParticipation p : mod200.getParticipationsOut()) {
				x += p.getCorrectionsBalance();
			}
			return round( x ); 
		}
		return 0.0;
	}
	
	public double computeUT1330() throws AonCoreException {
		
		// La clave UT1330 será igual a la LQ1330 
		if (isChecked(Mod2002022Key.C0013) || isChecked(Mod2002022Key.C0014))
		{
			double x = roundKey(Mod2002022Key.LQ1330);
			
			// Si cambia el valor entonces actualizar tambien la base de la relación de 
			// socios, que se calcula en funcion de la casilla 1330
			if (x != roundKey(Mod2002022Key.UT1330))
			{		
				if (mod200.getUteParticipations() != null && mod200.getUteParticipations().size() > 0) {
					for (UteParticipation p : mod200.getUteParticipations()) {
						p.setBase(round(x*p.getPercent()/100));						
					}					 
				}
			}
			
			return round( x );
		}
		else return 0.0;
	}
	
	// Cálculo del importe de la columna 2 del desglose de la casilla [1033]
	public double computeLQ1033_1(double col1, double col3, double suma) throws AonCoreException {
		
		double lq552 = getValue(Mod2002022Key.LQ552);
		
		if (lq552>=0) {
			// Base imponible [552] positiva o cero, [col2] = 0 
			return 0.0;
		} else {
			// Base imponible [552] negativa, [col2] = [col1] - [col3] con el limite de la [552]
			double col2 = col1-col3;
			double total = suma + col2;			
			if (total > Math.abs(lq552)) {
				col2 = col2 - (total - Math.abs(lq552));
			}
			return col2;		
		}		
	}
	
	// La clave 01034 sólo puede tener contenido si se ha marcado la clave 00006 de caracteres de la
	// declaración.
	// La clave 01034 (disminuciones) sólo podrá tener contenido cuando la base imponible (clave
	// 00552) sea positiva(excepto en los supuestos que también se haya marcado la clave 00072 de 
	// caracteres "extinción de entidad", en cuyo caso la clave 01034 permanecerá cerrada sin posibilidad 
	// de cumplimentación), y su importe máximo será el 10% de dicha base positiva y no puede superar
	// el millón de euros si el periodo impositivo es igual al año o si su período impositivo tiene una
	// duración inferior al año el importe máximo será = 1.000.000 x d/365.	
	public double computeLQ1034A() throws AonCoreException {
		
		double lq552 = getValue(Mod2002022Key.LQ552);
		if (isChecked(Mod2002022Key.C0006) && !isChecked(Mod2002022Key.C0072) && lq552 > 0) {
			double lq1034 = roundKey(Mod2002022Key.LQ1034A);
			if (lq1034 > round(lq552*10/100))
				lq1034 = round(lq552*10/100);
			if (lq1034 > getLimit(LIM_2))
				lq1034 = getLimit(LIM_2);
			return lq1034;			
		} else {
			return 0.0;
		}
		
	}
	
	// Base Imponible (Casilla 552)
	// Caso General: 00552 = 00550 - 01032 - 00547
	// Para el régimen especial de buques y empresas navieras en Canarias (caracter 69)
	// 1. Cuando 00541 sea positiva y 00564 negativa: 00552 = 00541 + 00564 - 00547
	// 2. Cuando 00541 sea negativa y 00564 positiva o cero: 00552 = 00564 - 01032 - 00547			
	public double computeLQ552() throws AonCoreException {
		
		double lq550 = roundKey(Mod2002022Key.LQ550);
		double lq1032 = roundKey(Mod2002022Key.LQ1032);
		double lq547 = roundKey(Mod2002022Key.LQ547);
		double lq541 = roundKey(Mod2002022Key.LQ541);
		double lq564 = roundKey(Mod2002022Key.LQ564);

		if (isChecked(C0069) && lq541 > 0 && lq564 < 0)
			return lq541 + lq564 - lq547;
		else if (isChecked(C0069) && lq541 < 0 && lq564 >= 0)
			return lq564 - lq1032 - lq547;
		else
			return lq550 - lq1032 - lq547;
		
	}
			
}
