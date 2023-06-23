package com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2022;

import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0001;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0002;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0003;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0004;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0005;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0006;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key.C0008;
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
	
	// FALTA - Casilla 619: Cuota líquida mínima (art. 30 bis.2 LIS)  
	// PAGINAS 175-185 DEL PADIS - NUEVA CASILLA COMPROBAR COMO SE CALCULA
	public double computeBN619() throws AonCoreException {
		
		// Estarán excluidos de la tributación mínima los contribuyentes que hayan marcado alguno de los 
		// caracteres 00001, 00003, 00004, 00012, 00047, 00048 y 00064. También estarán excluidos los 
		// contribuyentes que marquen el carácter 00008 junto con el 00084 que tributen al tipo de gravamen 
		// del 1% y los contribuyentes que hayan marcado la clave 00014 de caracteres (agrupaciones europeas de interés económico).		
		
		if (isChecked(C0001) || isChecked(C0003) || isChecked(C0004) || isChecked(C0012) ||
			isChecked(C0047) || isChecked(C0048) || isChecked(C0064) || isChecked(C0014)) {
			return 0.0;			
		}
		
		double lq558 = roundKey(Mod2002022Key.LQ558);  // Tipo de Gravamen
		if (isChecked(C0008) && isChecked(C0084) && lq558 == 1.0) {
			return 0.0;
		}
		
		// La tributación mínima es aplicable en los siguientes casos:
		// - Contribuyentes que marquen los supuestos 2 (INCN de al menos 20 millones de euros pero inferior a 60) o 3 (INCN de al menos 60 millones de euros) del apartado de importe neto de la cifra de negocios de la página 1 de la declaración (excepto supuestos excluidos).
		// - Contribuyentes que marquen el caracter 00079 de la página 1 de la declaración (excepto supuestos excluidos).
		int volope = getValue(Mod2002022Key.VOLOPE).intValue();
		if (volope == 2 || volope == 3 || isChecked(C0079)) {

			// CALCULO DE LA TRIBUTACION MINIMA

//		Se podrá marcar un check que llevará el siguiente texto
//		:
//		"Inoperatividad del orden de cumplimentación de deduccionesdel tramo 2".
//		En el caso de que se marque este check será el contribuyente el que cumplimente de forma manual los importes de las deducciones del tramo 2 correspondientes a la columna deducción aplicada (no estará operativo el check de aplicar el importe máximo). Los importes de deducciones que ya se hubieran cumplimentado en el tramo 2 se mantendrán en las casillas correspondientes a las columnas "importe pendiente ejercicios anteriores/generado en el ejercicio" pero se borrarán los importes que figuren en las casillas "importe deducción aplicada en esta liquidación" para que sea el contribuyente quien introduzca de forma manual los importes que desee aplicar. En caso de desmarcar el check se volverá a la situación anterior

// CALCULO DE LA CASILLA 619
			
//			ESPECIFICACIONES INCLUIDAS EN SOCIEDADES WEB 2022 
//			M1 = clave 01330 x 0,15 
//			 Si 00071--> M1 = clave 01330 x 0,10 
//
//			 Si 00024 y 00034--> M1= clave 01330 x 0,18 
//
//			 Si 00017, 00018 y 00019--> M1 = clave 00562 x 0,60 
//
//			 Si 00015 y 00079 --> M1= clave (01330 - 00559) x 0,15 
//
//			 Si 00015 o 00079 con 00024--> M1= clave (01330 - 00559) x 0,18 
//
//			M2 = 00562 + 01038 - tramo1 
//			M3 = M2 - tramo3 
//			M4= M3 - tramo 2 (aplicado con su límite) - tramo 4. 
//
//			Empresas de desarrollo de software 
//			SOCIEDADES WEB 2022 Página 14 
//			184 
//			 Si M3 > M1 Aviso POP UP: "Podrá aplicarse deducciones del tramo 2 (casillas 00583, 00585, 00584, 00588, 00565, 01040 y/o 01041)por el importe de XXXXX euros (será la diferencia entre M3 y M1)". 
//
//			 Si M3 < M1 Tramo 2 = 0. Si tramo 2 (deducciones aplicadas) distinto de 0 Error POP UP 
//
//			 Si deducciones aplicadas de tramo 2 > M3 - M1 Error POP UP 
//
//			Traslado a la casilla 00619 : 
//			Si M4 > M1  M1 a la 00619 
//			Si M1 >= M4  M4 a la 00619
		
			
//Cuando se marque el check : "Inoperatividad del orden de cumplimentación de deducciones del tramo 2", el cálculo se realizará: 
//Con carácter general--> M1 = clave 01330 x 0,15 
//Si 00071--> M1 = clave 01330 x 0,10 
//Si 00024 y 00034--> M1= clave 01330 x 0,18 
//Si 00017, 00018 y 00019--> M1 = clave 00562 + 01038 x 0,60 
//Si 00015 y 00079 --> M1= clave (01330 - 00559) x 0,15 
//Si 00015 o 00079 con 00024--> M1= clave (01330 - 00559) x 0,18 
//M2 = 00562 + 01038 - tramo1 
//M3 = M2 - tramo 2 aplicado con su límite - tramo 4 
//M4= M3 - tramo4 
//
//Si M2 > M1 Aviso: "Podrá aplicarse deducciones del tramo 2 (casillas 00583, 00585, 00584, 00588, 00565, 01040 y/o 01041)por el importe XXXXX euros (será la diferencia entre M2 y M1)". POP UP 
//Si M2 < M1 Tramo 2 = 0. Si tramo 2 distinto de 0 Error POP UP 
//Si deducciones aplicadas de tramo 2 > M2 - M1 Error POP UP 
//Traslado a la 00619 
//Si M4 > M1 M1 a la 00619 
//Si M1 >= M4 M4 a la 00619

			double lq1330 = roundKey(Mod2002022Key.LQ1330);
			double lq562 = roundKey(Mod2002022Key.LQ562);
			double lq1038 = roundKey(Mod2002022Key.LQ1038);
			double lq559 = roundKey(Mod2002022Key.LQ559);

			double m1 = lq1330 * 0.15;

			if (isChecked(C0071))
				m1 = lq1330 * 0.10;

			if (isChecked(C0024) || isChecked(C0034))
				m1 = lq1330 * 0.18;

			if (isChecked(C0017) || isChecked(C0018) || isChecked(C0019))
				m1 = (lq562 + lq1038) * 0.60;

			if (isChecked(C0015) || isChecked(C0079)) {
				if (isChecked(C0024))
					m1 = (lq1330 - lq559) * 0.18;
				else
					m1 = (lq1330 - lq559) * 0.15;
			}
			
			// ESTO ES HACIENDO EL CALCULO DE LA SEGUNDA FORMA, IGUAL HAY QUE HACERLO DE LA PRIMERA

			double tramo1 = ComputeTramo(TRAMO_1); // FALTA
			double tramo2 = ComputeTramo(TRAMO_2); // FALTA tramo 2 aplicado con su límite
			double tramo4 = ComputeTramo(TRAMO_4); // FALTA

			double m2 = lq562 + lq1038 - tramo1;
			double m3 = m2 - tramo2 - tramo4;
			double m4 = m3 - tramo4;

			// Traslado a la 00619
			// Si M4 > M1 M1 a la 00619
			// Si M1 >= M4 M4 a la 00619
			
			System.out.println("CUOTA MINIMA: M1="+m1+" M2="+m2+" M3="+m3+" M4="+m4+" tramo1="+tramo1+" tramo2="+tramo2+" tramo4="+tramo4);

			if (m4 > m1)
				return m1;
			else
				return m4;
		}
		else {
			// La tributación mínima no es aplicable
			System.out.println("CUOTA MINIMA: NO APLICABLE");
			return 0.0;
		}		
	
	}
	
	private double ComputeTramo(Mod2002022Key[] tramo) {
		double result = 0;
		
		for (Mod2002022Key key : tramo) {
			result = result + roundKey(key);
		}
		
		return result;
	}
	
//	TRAMO 1 DE DEDUCCIONES Y BONIFICACIONES
//	BONIFICACIONES
//	- 00567 Bonificación rentas obtenidas en Ceuta y Melilla (artículo 33 L.I.S.)
//	- 00568 Bonificaciones por prestación de servicios (artículo 34 L.I.S.)
//	- 00563 Bonificación rendimientos por ventas bienes corporales producidos en Canarias (artículo 26 de la Ley 19/1994)
//	- 00566 Bonificaciones sociedades cooperativas (Ley 20/1990) Empresas de desarrollo de software
//	- 00576 Bonificaciones entidades dedicadas al arrendamiento de viviendas
//	- 00569 Otras bonificaciones
//	BONIFICACIONES NAVIERAS EN CANARIAS
//	- 00581 Bonificaciones empresas navieras en Canarias (artículo 76 de la Ley 19/1994)
//	DEDUCCIÓN POR INVERSIONES Y GASTOS REALIZADOS POR LAS AUTORIDADES PORTUARIAS
//	- 01287 Deducción autoridades portuarias 2020
//	- 01290 Deducción autoridades portuarias 2021
//	- 01293 Deducción autoridades portuarias 2022 (*)
//	- 01296 Deducción autoridades portuarias 2022
//	DEDUCCIONES DOBLE IMPOSICIÓN
//	Para su aplicación se tendrá en cuenta el año límite de aplicación:
//	- 00575 Transparencia fiscal internacional
//	- 00577 Deducciones por doble imposición intersocietaria al 5/10% (cooperativas)
//	- 00847 Deduc. doble imp. interna 2008
//	- 00638 Deduc. doble imp. Internacional 2005
//	- 00283 Deduc. doble imp. interna 2009
//	- 00894 Deduc. doble imp. internacional 2006
//	- 00703 Deduc. doble imp. interna 2010
//	- 00286 Deduc. doble imp. internacional 2007
//	- 00187 Deduc. doble imp. interna 2011
//	- 00826 Deduc. doble imp. internacional 2008
//	- 00026 Deduc. doble imp. interna 2012
//	- 00002 Deduc. doble imp. internacional 2009
//	- 00715 Deduc. doble imp. interna 2013
//	- 00029 Deduc. doble imp. internacional 2010 Empresas de desarrollo de software
//	SOCIEDADES WEB 2022 Páginas 17, 18, 18 bis y 18 ter
//	317
//	- 00737 Deduc. doble imp. interna 2014
//	- 00718 Deduc. doble imp. internacional 2011
//	- 00120 Deduc. doble imp. interna DT 23.1 LIS 2015
//	- 00723 Deduc. doble imp. internacional 2012
//	- 00125 Deduc. doble imp. Interna DT 23.1 LIS 2016
//	- 00741 Deduc. doble imp. internacional 2013
//	- 01598 Deduc. doble imp. interna DT 23.1 LIS 2017
//	- 00136 Deduc. doble imp. internacional 2014
//	- 01831 Deduc. doble imp. interna DT 23.1 LIS 2018
//	- 01052 Deduc. doble imp. internacional 2015
//	- 02199 Deduc. doble imp. interna DT 23.1 LIS 2019
//	- 01351 Deduc. doble imp. internacional 2016
//	- 02322 Deduc. doble imp. interna DT 23.1 LIS 2020
//	- 01773 Deduc. doble imp. internacional 2017
//	- 00205 Deduc. doble imp. interna DT 23.1 LIS 2021
//	- 01836 Deduc. doble imp. internacional 2018
//	- 00438 Deduc. doble imp. interna DT 23.1 LIS 2022 (*)
//	- 00128 Deduc. doble imp. interna DT 23.1 LIS 2022
//	- 02204 Deduc. doble imp. internacional 2019
//	- 02327 Deduc. doble imp. internacional 2020
//	- 00212 Deduc. doble imp. internacional 2021
//	- 00493 Deduc. doble imp. internacional 2022(*)
//	- 00165 Deduc. doble imp. internacional 2022 Imp. soportado por el contribuyente
//	- 00169 Deduc. doble imp. internacional 2022: Dividendos y particip. en beneficios
	
	private static final Mod2002022Key[] TRAMO_1 = new Mod2002022Key[] {
			Mod2002022Key.BN567,
			Mod2002022Key.BN568,
			Mod2002022Key.BN563,
			Mod2002022Key.BN566,
			Mod2002022Key.BN576,
			Mod2002022Key.BN569,
			Mod2002022Key.BN581,
			Mod2002022Key.BN1287,
			Mod2002022Key.BN1290,
			Mod2002022Key.BN1293,
			Mod2002022Key.BN1296,
			Mod2002022Key.BN575,
			Mod2002022Key.BN577,
			Mod2002022Key.BN847,
			Mod2002022Key.BN638,
			Mod2002022Key.BN283,
			Mod2002022Key.BN894,
			Mod2002022Key.BN703,
			Mod2002022Key.BN286,
			Mod2002022Key.BN187,
			Mod2002022Key.BN826,
			Mod2002022Key.BN026,
			Mod2002022Key.BN002,
			Mod2002022Key.BN715,
			Mod2002022Key.BN029,
			Mod2002022Key.BN737,
			Mod2002022Key.BN718,
			Mod2002022Key.BN120,
			Mod2002022Key.BN723,
			Mod2002022Key.BN125,
			Mod2002022Key.BN741,
			Mod2002022Key.BN1598,
			Mod2002022Key.BN136,
			Mod2002022Key.BN1831,
			Mod2002022Key.BN1052,
			Mod2002022Key.BN2199,
			Mod2002022Key.BN1351,
			Mod2002022Key.BN2322,
			Mod2002022Key.BN1773,
			Mod2002022Key.BN205,
			Mod2002022Key.BN1836,
			Mod2002022Key.BN438,
			Mod2002022Key.BN128,
			Mod2002022Key.BN2204,
			Mod2002022Key.BN2327,
			Mod2002022Key.BN212,
			Mod2002022Key.BN493,
			Mod2002022Key.BN165,
			Mod2002022Key.BN169
	};
	
	
//	TRAMO 2 DEDUCCIONES
//	OTRAS DEDUCCIONES
//	- 00583 Apoyo fiscal a la inversión y otras
//	DEDUCCIONES DE AÑOS ANTERIORES POR INVERSIONES (EXCEPTO CANARIAS) Empresas de desarrollo de software
//	2004
//	- 00289 Suma deducciones Cap. IV Tít. VI Ley 43/95 2004
//	2005
//	- 00467 Suma deducciones Cap. IV Tít. VI Ley 43/95 y RDLeg. 4/2004 2005
//	2006
//	- 00498 Suma deducciones Cap. IV Tít. VI Ley 43/95 y RDLeg. 4/2004 2006
//	2007
//	- 00473 Suma deducciones Cap. IV Tít. VI Ley 43/95 y RDLeg. 4/2004 2007
//	- 00005 Deducción art. 42 RDLeg. 4/2004 2007
//	2008
//	- 00181 Suma deducciones Cap. IV Tít. VI Ley 43/95 y RDLeg. 4/2004 2008
//	- 00032 Deducción art. 42 RDLeg. 4/2004 2008
//	2009
//	- 00532 Suma deducciones Cap. IV Tít. VI Ley 43/95 y RDLeg. 4/2004 2009
//	- 00023 Deducción art. 42 RDLeg. 4/2004 2009
//	2010
//	- 00946 Suma deducciones Cap. IV Tít. VI Ley 43/95 y RDLeg. 4/2004 2010
//	- 00041 Deducción art. 42 RDLeg. 4/2004 2010
//	2011
//	- 00961 Suma deducciones Cap. IV Tít. VI Ley 43/95 y RDLeg. 4/2004 2011
//	- 00139 Deducción art. 42 RDLeg. 4/2004 2011
//	2012
//	- 00185 Suma deducciones Cap. IV Tít. VI Ley 43/95 y RDLeg. 4/2004 2012
//	- 00142 Deducción art. 42 RDLeg. 4/2004 2012
//	2013
//	- 00458 Investigación y desarrollo (CT) 2013
//	- 00461 Innovación tecnológica (IT) 2013
//	- 00967 Suma deducciones Cap. IV Tít. VI Ley 43/95 y RDLeg. 4/2004 2013
//	- 00189 Deducción art. 42 RDLeg. 4/2004 2013
//	2014
//	- 01067 Investigación y desarrollo (CT) 2014
//	- 01070 Innovación tecnológica (IT) 2014
//	- 01064 Suma deducciones Cap. IV Tít. VI Ley 43/95, RDLeg. 4/2004 y LIS 2014
//	- 00804 Deducción art. 42 RDLeg. 4/2004 2014
//	2015
//	- 00810 Investigación y desarrollo (CT) 2015
//	- 00591 Innovación tecnológica (IT) 2015
//	- 02295 Suma deducciones Cap. IV Tít. VI Ley 43/95, RDLeg. 4/2004 y LIS 2015
//	- 01056 Deducción DT 24ª. 7 LIS 2015
//	2016
//	- 01618 Investigación y desarrollo (CT) 2016
//	- 01621 Innovación tecnológica (IT) 2016
//	- 02298 Suma deducciones Cap. IV Tít. VI Ley 43/95, RDLeg. 4/2004 y LIS 2016
//	- 00708 Deducción DT 24ª. 7 LIS 2016
//	2017
//	- 00750 Periodificación 2017
//	- 01851 Investigación y desarrollo (CT) 2017
//	- 01854 Innovación tecnológica (IT) 2017
//	- 02500 Suma deducciones Cap. IV Tít. VI Ley 43/95, RDLeg. 4/2004 y LIS 2017
//	- 01354 Deducción DT 24ª. 7 LIS 2017
//	02018
//	- 00753 Periodificación 2018
//	- 02222 Investigación y desarrollo (CT) 2018 Empresas de desarrollo de software
//	- 02225 Innovación tecnológica (IT) 2018
//	- 02092 Suma deducciones Cap. IV Tít. VI Ley 43/95, RDLeg. 4/2004 y LIS 2018
//	- 01776 Deducción DT 24ª. 7 LIS 2018
//	2019
//	- 00756 Periodificación 2019
//	- 02357 Investigación y desarrollo (CT) 2019
//	- 02360 Innovación tecnológica (IT) 2019
//	- 02095 Suma deducciones Cap. IV Tít. VI Ley 43/95, RDLeg. 4/2004 y LIS 2019
//	- 01839 Deducción DT 24ª. 7 LIS 2019
//	2020
//	- 00759 Periodificación 2020
//	- 00229 Investigación y desarrollo (CT) 2020
//	- 00235 Innovación tecnológica (IT) 2020
//	- 02098 Suma deducciones Cap. IV Tít. VI Ley 43/95, RDLeg. 4/2004 y LIS 2020
//	- 02207 Deducción DT 24ª. 7 LIS 2020
//	2021
//	- 00762 Periodificación 2021
//	- 00781 Investigación y desarrollo (CT) 2021
//	- 00787 Innovación tecnológica (IT) 2021
//	- 02146 Suma deducciones Cap. IV Tít. VI Ley 43/95, RDLeg. 4/2004 y LIS 2021
//	- 02330 Deducción DT 24ª. 7 LIS 2021
//	DEDUCCIONES DE 2022 POR INVERSIONES (EXCEPTO CANARIAS)
//	- 00745 Periodificación 2022(*)
//	- 00783 Periodificación 2022
//	- 02450 Suma deducciones Cap. IV Tít. VI Ley 43/95, RDLeg. 4/2004 y LIS 2022(*)
//	- 01364 Investigación y desarrollo (CT) 2022(*)
//	- 01367 Innovación tecnológica (IT) 2022(*)
//	- 00796 Deducción por creación empleo trabaj. con discapacidad (CE) Empresas de desarrollo de software
//	- 00793 Contribuciones empresariales a sistemas de previsión social empresarial (CPS)
//	- 00799 Investigación y desarrollo (CT)
//	- 00698 Innovación tecnológica (IT)
//	- 00888 Deducción por inversión de beneficios (IB)
//	- 00808 Producciones cinematográficas españolas (PC)
//	- 02463 Financiador: producciones cinematográficas españolas (FPC)
//	- 01076 Productor: Espectáculos en vivo de artes escénicas y musicales (EV)
//	- 02456 Financiador: espectáculos en vivo de artes escénicas y musicales (FEV)
//	- 01370 Gastos e inversiones de sociedades forestales (SF)
//	- 01627 4ª Edición de la Barcelona World Race (4BWR)
//	- 01639 V Centenario de la expedición de la primera vuelta al mundo de Fernando de Magallanes y Juan Sebastián Elcano (EPVM)
//	- 01708 Plan Decenio Millarium Monserrat 1025-2025 (PDMM)
//	- 01908 Año Santo Jacobeo 2021 (J21)
//	- 01911 VIII Centenario de la Catedral de Burgos 2021 (CB21)
//	- 01935 Expo Dubai 2020 (D20)
//	- 02363 Plan Berlanga (PB)
//	- 02366 Alicante 2021. Salida vuelta al mundo a vela (A21)
//	- 02369 España País Invitado de Honor en la Feria del Libro de Fráncfort en 2022 (E22)
//	- 02372 Plan de Fomento de la ópera en la Calle del Teatro Real (FO)
//	- 02375 175 Aniversario de la construcción del Gran Teatre del Liceu (TL)
//	- 02378 Gran Premio de España de Fórmula 1 (F1)
//	- 00255 El tiempo de la Libertad. Comuneros V Centenario (CVC)
//	- 00260 Bicentenario de la independencia de las Repúblicas Iberoamericanas (BIR)
//	- 00263 150 aniversario de creación de la Academia de España en Roma (AER)
//	- 00269 Celebración del Summit MADBLUE (SM)
//	- 00273 30 aniversario de la Escuela Superior de Música Reina Sofía (RS)
//	- 00292 Año Santo Guadalupense 2021 (G21)
//	- 00295 Torneo Davis Cup Madrid (DCM)
//	- 00298 Madrid Horse Week 21/23 (HW21)
//	- 00316 Centenario del Rugby en España y de la Unió Esportiva Santboiana (CRE)
//	- 00349 Solheim Cup 2023 (SC23)
//	- 00353 IX Centenario de la Reconquista de Sigüenza (CRS) Empresas de desarrollo de software
//	- 00367 Barcelona Mobile World Capital (BMWC)
//	- 00401 Valencia, Capital Mundial del Diseño 2022/Valencia World Design Capital 2022 (VD22)
//	- 00407 Cincuenta aniversario de la Universidad Nacional de Educación a Distancia (UNED)
//	- 00423 Centenario de Revista de Occidente (RO)
//	- 00428 50 aniversario del fallecimiento de Clara Campoamor. 90 años del inicio de una democracia plena (CC)
//	- 00431 V Centenario del fallecimiento de Elio Antonio de Nebrija (FN)
//	- 00434 Nuevas Metas II (NMII)
//	- 00440 Andalucía Región Europea del Deporte 2021 (AD21)
//	- 00453 75 aniversario de la Ópera en Oviedo (OD)
//	- 00456 Hábitos saludables para el control del riesgo Cardiovascular	«Aprender a cuidarnos» (HSRC)
//	- 00469 Mundiales Bádminton España (MBE)
//	- 00479 Centenario de la Batalla de Covadonga-Cuadonga (CBC)
//	- 00502 VII Centenario de la Catedral de Palencia 2021-2022 (CCP)
//	- 00511 FITUR especial: recuperación turismo (FE)
//	- 00523 Programa Deporte Inclusivo II (DI2)
//	- 00542 Valencia 2020-2021, Año Jubilar. Camino del Santo Cáliz (VAJ)
//	- 00801 Enfermedades Neurodegenerativas. Año Internacional de la Investigación e Innovación. Período 2021-2022 (EN21)
//	- 00816 50 aniversario del Hospital Sant Joan de Deu (HSJD)
//	- 02459 Andalucía Valderrama Masters 2022/2024 (AVM22)
//	- 00875 Bicentenario de la Policía Nacional (PN)
//	- 00879 Centenario Federación Aragonesa de Fútbol (FAF)
//	- 00906 Plan 2030 de Apoyo al Deporte de Base (P30)
//	- 00955 Universo Mujer III (UM3)
//	- 01087 Programa de preparación de los deportistas españoles de los Juegos de París 2024 (P24)
//	- 01110 100 años del fallecimiento de Joaquín Sorolla (JS)
//	- 01144 20 Aniversario de Primavera Sound (PS)
//	- 01150 Centenario del nacimiento de Victoria de los Ángeles (VA)
//	- 01153 Conmemoración del 50 aniversario de la muerte del artista español Pablo Picasso (50P) Empresas de desarrollo de software
//	- 01156 Todos contra el cáncer (TC)
//	- 01180 Año de Investigación Santiago Ramón y Cajal 2022 (RC)
//	- 01207 Año Jubilar Lebaniego 2023-2024 (L23)
//	- 01218 Mundo Voluntario 2030/35º Aniversario Plataforma del Voluntariado de España (V30)
//	- 01221 7ª Conferencia Mundial sobre Turismo Enológico de la OMT 2023 (7TE)
//	- 01229 Caravaca de la Cruz 2024. Año Jubilar (C24)
//	- 01235 Bicentenario del Ateneo de Madrid (BA)
//	- 01238 Barcelona Equestrian Challenge (4ª Edición) (BE4)
//	- 01262 200 Aniversario del Passeig de Grácia (PG)
//	- 01265 Reconstrucción de la Piscina Histórica cubierta de saltos del Club Natació Barcelona (CNB)
//	- 01268 ALIMENTARIA 2022 y HOSTELCO 2022 (AH)
//	- 01273 Barcelona Music Lab. El futuro de la música (ML)
//	- 01278 Global Mobility Call (GM)
//	- 01282 South Summit 2022-2024 (SS)
//	- 01684 Otras deducciones relativas a programas de apoyo a acontecimientos de excepcional interés público
//	- 00829 Diferimiento deducc. Cap. IV Tít. VI Ley 43/95, RDLeg. 4/2004 (DT 24ª.3 LIS) y LIS 2022
//	- 00252 Deducción DT 24ª. 7 LIS 2022(*)
//	- 00697 Deducción DT 24ª. 7 LIS 2022
//	DEDUCCIONES LEY 49/2002. AÑOS 2012 A 2022
//	- 00905 Donac. a ent. sin fines de lucro (Ley 49/2002) 2012
//	- 00901 Donac. Actividades prioritarias de mecenazgo 2012
//	- 00991 Donac. a ent. sin fines de lucro (Ley 49/2002) 2013
//	- 00917 Donac. Actividades prioritarias de mecenazgo 2013
//	- 00998 Donac. a ent. sin fines de lucro (Ley 49/2002) 2014
//	- 00931 Donac. Actividades prioritarias de mecenazgo 2014
//	- 00247 Donac. a ent. sin fines de lucro (Ley 49/2002) sin reiteración 2015
//	- 00819 Donac. a ent. sin fines de lucro (Ley 49/2002) con reiteración 2015
//	- 00934 Donac. Actividades prioritarias de mecenazgo sin reiteración 2015 Empresas de desarrollo de software
//	- 00944 Donac. Actividades prioritarias de mecenazgo con reiteración 2015
//	- 00994 Donac. a ent. sin fines de lucro (Ley 49/2002) sin reiteración 2016
//	- 00833 Donac. a ent. sin fines de lucro (Ley 49/2002) con reiteración 2016
//	- 00950 Donac. Actividades prioritarias de mecenazgo sin reiteración 2016
//	- 00953 Donac. Actividades prioritarias de mecenazgo con reiteración 2016
//	- 01435 Donac. a ent. sin fines de lucro (Ley 49/2002) sin reiteración 2017
//	- 00836 Donac. a ent. sin fines de lucro (Ley 49/2002) con reiteración 2017
//	- 02473 Donac. Actividades prioritarias de mecenazgo sin reiteración 2017
//	- 00959 Donac. Actividades prioritarias de mecenazgo con reiteración 2017
//	- 01719 Donac. a ent. sin fines de lucro (Ley 49/2002) sin reiteración 2018
//	- 00839 Donac. a ent. sin fines de lucro (Ley 49/2002) con reiteración 2018
//	- 00965 Donac. Actividades prioritarias de mecenazgo sin reiteración 2018
//	- 00971 Donac. Actividades prioritarias de mecenazgo con reiteración 2018
//	- 01951 Donac. a ent. sin fines de lucro (Ley 49/2002) sin reiteración 2019
//	- 00844 Donac. a ent. sin fines de lucro (Ley 49/2002) con reiteración 2019
//	- 00975 Donac. Actividades prioritarias de mecenazgo sin reiteración 2019
//	- 00981 Donac. Actividades prioritarias de mecenazgo con reiteración 2019
//	- 02228 Donac. a ent. sin fines de lucro (Ley 49/2002) sin reiteración 2020
//	- 00869 Donac. a ent. sin fines de lucro (Ley 49/2002) con reiteración 2020
//	- 00984 Donac. Actividades prioritarias de mecenazgo sin reiteración 2020
//	- 01001 Donac. Actividades prioritarias de mecenazgo con reiteración 2020
//	- 02381 Donac. a ent. sin fines de lucro (Ley 49/2002) sin reiteración 2021
//	- 00873 Donac. a ent. sin fines de lucro (Ley 49/2002) con reiteración 2021
//	- 01017 Donac. Actividades prioritarias de mecenazgo sin reiteración 2021
//	- 01035 Donac. Actividades prioritarias de mecenazgo con reiteración 2021
//	- 00876 Donac. a ent. sin fines de lucro (Ley 49/2002) sin reiteración 2022 (*)
//	- 00892 Donac. a ent. sin fines de lucro (Ley 49/2002) con reiteración 2022 (*)
//	- 01062 Donac. Actividades prioritarias de mecenazgo sin reiteración 2022 (*)
//	- 01074 Donac. Actividades prioritarias de mecenazgo con reiteración 2022 (*)
//	- 01324 Donac. a ent. sin fines de lucro (Ley 49/2002) sin reiteración 2022
//	- 01327 Donac. a ent. sin fines de lucro (Ley 49/2002) con reiteración 2022
//	- 01372 Donac. Actividades prioritarias de mecenazgo sin reiteración 2022
//	- 01375 Donac. Actividades prioritarias de mecenazgo con reiteración 2022 Empresas de desarrollo de software
//	DEDUCCIONES POR REVERSIÓN DE MEDIDAS TEMPORALES
//	- 01437 Deducción por reversión de medidas temporales DT 37ª.1 LIS 2015
//	- 01440 Deducción por reversión de medidas temporales DT 37ª.1 LIS 2016
//	- 01444 Deducción por reversión de medidas temporales DT 37ª.1 LIS 2017
//	- 01723 Deducción por reversión de medidas temporales DT 37ª.1 LIS 2018
//	- 01955 Deducción por reversión de medidas temporales DT 37ª.1 LIS 2019
//	- 02232 Deducción por reversión de medidas temporales DT 37ª.1 LIS 2020
//	- 02385 Deducción por reversión de medidas temporales DT 37ª.1 LIS 2021
//	- 01084 Deducción por reversión de medidas temporales DT 37ª.1 LIS 2022 (*)
//	- 01379 Deducción por reversión de medidas temporales DT 37ª.1 LIS 2022
//	- 01446 Deducción por reversión de medidas temporales DT 37ª.2 LIS 2015
//	- 01449 Deducción por reversión de medidas temporales DT 37ª.2 LIS 2016
//	- 01453 Deducción por reversión de medidas temporales DT 37ª.2 LIS 2017
//	- 01727 Deducción por reversión de medidas temporales DT 37ª.2 LIS 2018
//	- 01959 Deducción por reversión de medidas temporales DT 37ª.2 LIS 2019
//	- 02236 Deducción por reversión de medidas temporales DT 37ª.2 LIS 2020
//	- 02389 Deducción por reversión de medidas temporales DT 37ª.2 LIS 2021
//	- 02478 Deducción por reversión de medidas temporales DT 37ª.2 LIS 2022 (*)
//	- 01383 Deducción por reversión de medidas temporales DT 37ª.2 LIS 2022
//
//	TRAMO 3 DE DEDUCCIONES Empresas de desarrollo de software
//	DEDUCCIONES POR INVERSIONES EN CANARIAS (LEY 20/1991 y LEY 19/1994) (excepto cinematográficas extranjeras en Canarias)
//	- 02082 Inversiones en territ. África occidental y gastos de propaganda y publicidad 2015
//	- 02085 Inversiones en territ. África occidental y gastos de propaganda y publicidad 2016
//	- 02089 Inversiones en territ. África occidental y gastos de propaganda y publicidad 2017
//	- 01917 Inversiones en territ. África occidental y gastos de propaganda y publicidad 2018
//	- 01920 Inversiones en territ. África occidental y gastos de propaganda y publicidad 2019
//	- 01923 Inversiones en territ. África occidental y gastos de propaganda y publicidad 2020
//	- 01926 Inversiones en territ. África occidental y gastos de propaganda y publicidad 2021
//	- 01929 Inversiones en territ. África occidental y gastos de propaganda y publicidad 2022(*)
//	- 02191 Inversiones en territ. África occidental y gastos de propaganda y publicidad 2022
//	- 00881 Inversiones en Canarias (Ley 20/91) 2004
//	- 00867 Inversiones en Canarias (Ley 20/91) 2005
//	- 00940 Inversiones en Canarias (Ley 20/91) 2006
//	- 00192 Inversiones en Canarias (Ley 20/91) 2007
//	- 00614 Inversiones en Canarias (Ley 20/91) 2008
//	- 00257 Inversiones en Canarias (Ley 20/91) 2009
//	- 00855 Activos fijos (Ley 20/91) 2010
//	- 00038 Inversiones en Canarias (Ley 20/91) 2010
//	- 00858 Activos fijos (Ley 20/91) 2011
//	- 00045 Inversiones en Canarias (Ley 20/91) 2011
//	- 00861 Activos fijos (Ley 20/91) 2012
//	- 00529 Inversiones en Canarias (Ley 20/91) 2012
//	- 00864 Activos fijos (Ley 20/91) 2013
//	- 00145 Inversiones en Canarias (Ley 20/91) 2013
//	- 00884 Activos fijos (Ley 20/91) 2014
//	- 00148 Inversiones en Canarias (Ley 20/91) 2014
//	- 00789 Activos fijos (Ley 20/91) 2015
//	- 00241 Inversiones en Canarias (Ley 20/91) 2015
//	- 01358 Activos fijos (Ley 20/91) 2016
//	- 01059 Inversiones en Canarias (Ley 20/91) 2016
//	- 01779 Activos fijos (Ley 20/91) 2017 Empresas de desarrollo de software
//	- 00802 Inversiones en Canarias (Ley 20/91) 2017
//	- 00853 Activos fijos (Ley 20/91) 2018
//	- 02336 Activos fijos en La Palma, La Gomera y El Hierro 2018
//	- 01782 Inversiones en Canarias (Ley 20/91) 2018
//	- 02120 Inversiones en La Palma, La Gomera y El Hierro 2018
//	- 02117 Activos fijos (Ley 20/91) 2019
//	- 02339 Activos fijos en La Palma, La Gomera y El Hierro 2019
//	- 02123 Inversiones en Canarias (Ley 20/91) 2019
//	- 02126 Inversiones en La Palma, La Gomera y El Hierro 2019
//	- 02210 Activos fijos (Ley 20/91) 2020
//	- 02342 Activos fijos en La Palma, La Gomera y El Hierro 2020
//	- 02213 Inversiones en Canarias (Ley 20/91) 2020
//	- 02216 Inversiones en La Palma, La Gomera y El Hierro 2020
//	- 02333 Activos fijos (Ley 20/91) 2021
//	- 02345 Activos fijos en La Palma, La Gomera y El Hierro 2021
//	- 02348 Inversiones en Canarias (Ley 20/91) 2021
//	- 02351 Inversiones en La Palma, La Gomera y El Hierro 2021
//	- 00238 Activos fijos (Ley 20/91) 2022(*)
//	- 00245 Activos fijos en La Palma, La Gomera y El Hierro 2022(*)
//	- 00218 Inversiones en Canarias (Ley 20/91) 2022(*)
//	- 00221 Inversiones en La Palma, La Gomera y El Hierro 2022(*)
//	- 00712 Activos fijos (Ley 20/91) 2022
//	- 01913 Activos fijos en La Palma, La Gomera y El Hierro 2022
//	- 00768 Inversiones en Canarias (Ley 20/91) 2022
//	- 00771 Inversiones en La Palma, La Gomera y El Hierro 2022

	
	
	
//	TRAMO 4 DEDUCCIONES
//	DEDUCCIONES POR PRODUCCIONES CINEMATOGRÁFICAS EXTRANJERAS (ART. 36.2 LIS) Y PRODUCCIONES CINEMATOGRÁFICAS EXTRANJERAS EN CANARIAS (ART. 36.2 LIS Y DA 14 LEY 19/1994)
//	- 01932 Deducciones por producciones cinematográficas extranjeras (art. 36.2 LIS) 2015 Empresas de desarrollo de software
//	- 02149 Deducciones por producciones cinematográficas extranjeras en Canarias (art. 36.2 LIS y DA 14ª Ley 19/1994) 2015
//	- 01939 Deducciones por producciones cinematográficas extranjeras (art. 36.2 LIS) 2016
//	- 02153 Deducciones por producciones cinematográficas extranjeras en Canarias (art. 36.2 LIS y DA 14ª Ley 19/1994) 2016
//	- 01943 Deducciones por producciones cinematográficas extranjeras (art. 36.2 LIS) 2017
//	- 02157 Deducciones por producciones cinematográficas extranjeras en Canarias (art. 36.2 LIS y DA 14ª Ley 19/1994) 2017
//	- 01947 Deducciones por producciones cinematográficas extranjeras (art. 36.2 LIS) 2018
//	- 02161 Deducciones por producciones cinematográficas extranjeras en Canarias (art. 36.2 LIS y DA 14ª Ley 19/1994) 2018
//	- 02110 Deducciones por producciones cinematográficas extranjeras (art. 36.2 LIS) 2019
//	- 02165 Deducciones por producciones cinematográficas extranjeras en Canarias (art. 36.2 LIS y DA 14ª Ley 19/1994) 2019
//	- 02129 Deducciones por producciones cinematográficas extranjeras (art. 36.2 LIS) 2020
//	- 02169 Deducciones por producciones cinematográficas extranjeras en Canarias (art. 36.2 LIS y DA 14ª Ley 19/1994) 2020
//	- 02133 Deducciones por producciones cinematográficas extranjeras (art. 36.2 LIS) 2021
//	- 02173 Deducciones por producciones cinematográficas extranjeras en Canarias (art. 36.2 LIS y DA 14ª Ley 19/1994) 2021
//	- 02137 Deducciones por producciones cinematográficas extranjeras (art. 36.2 LIS) 2022 (*)
//	- 01310 Deducciones por producciones cinematográficas extranjeras en Canarias (art. 36.2 LIS y DA 14ª Ley 19/1994) 2022 (*)
//	- 02141 Deducciones por producciones cinematográficas extranjeras (art. 36.2 LIS) 2022
//	- 01314 Deducciones por producciones cinematográficas extranjeras en Canarias (art. 36.2 LIS y DA 14ª Ley 19/1994) 2022
//	DEDUCCIONES I+D+I EXCLUIDAS DE LÍMITE
//	- 00574 Deducciones I+D+i excluidas de límite. Investigación y desarrollo (CTE) 2013
//	- 00977 Deducciones I+D+i excluidas de límite. Innovación tecnológica (ITE) 2013
//	- 00824 Deducciones I+D+i excluidas de límite. Investigación y desarrollo (CTE) 2014
//	- 00850 Deducciones I+D+i excluidas de límite. Innovación tecnológica (ITE) 2014 Empresas de desarrollo de software
//	- 01125 Deducciones I+D+i excluidas de límite. Investigación y desarrollo (CTE) 2015
//	- 01129 Deducciones I+D+i excluidas de límite. Innovación tecnológica (ITE) 2015
//	- 01428 Deducciones I+D+i excluidas de límite. Investigación y desarrollo (CTE) 2016
//	- 01432 Deducciones I+D+i excluidas de límite. Innovación tecnológica (ITE) 2016
//	- 01712 Deducciones I+D+i excluidas de límite. Investigación y desarrollo (CTE) 2017
//	- 01716 Deducciones I+D+i excluidas de límite. Innovación tecnológica (ITE) 2017
//	- 01970 Deducciones I+D+i excluidas de límite. Investigación y desarrollo (CTE) 2018
//	- 01974 Deducciones I+D+i excluidas de límite. Innovación tecnológica (ITE) 2018
//	- 02247 Deducciones I+D+i excluidas de límite. Investigación y desarrollo (CTE) 2019
//	- 02251 Deducciones I+D+i excluidas de límite. Innovación tecnológica (ITE) 2019
//	- 02393 Deducciones I+D+i excluidas de límite. Investigación y desarrollo (CTE) 2020
//	- 02397 Deducciones I+D+i excluidas de límite. Innovación tecnológica (ITE) 2020
//	- 01092 Deducciones I + D + i excluidas de límite. Investigación y desarrollo (CTE) 2021
//	- 01096 Deducciones I + D + i excluidas de límite. Innovación tecnológica (ITE) 2021
//	- 01387 Deducciones I + D + i excluidas de límite. Investigación y desarrollo (CTE) 2022 (*)
//	- 01391 Deducciones I + D + i excluidas de límite. Innovación tecnológica (ITE) 2022 (*)	
//
	
	///////////////////////////// AQUI ESTAN LOS ARRAYS
	
//	TRAMO 2 DEDUCCIONES
	
	private static final Mod2002022Key[] TRAMO_2 = new Mod2002022Key[] {			
			Mod2002022Key.BN583,
			Mod2002022Key.BN289,
			Mod2002022Key.BN467,
			Mod2002022Key.BN498,
			Mod2002022Key.BN473,
			Mod2002022Key.BN005,
			Mod2002022Key.BN181,
			Mod2002022Key.BN032,
			Mod2002022Key.BN532,
			Mod2002022Key.BN023,
			Mod2002022Key.BN946,
			Mod2002022Key.BN041,
			Mod2002022Key.BN961,
			Mod2002022Key.BN139,
			Mod2002022Key.BN185,
			Mod2002022Key.BN142,
			Mod2002022Key.BN458,
			Mod2002022Key.BN461,
			Mod2002022Key.BN967,
			Mod2002022Key.BN189,
			Mod2002022Key.BN1067,
			Mod2002022Key.BN1070,
			Mod2002022Key.BN1064,
			Mod2002022Key.BN804,
			Mod2002022Key.BN810,
			Mod2002022Key.BN591,
			Mod2002022Key.BN2295,
			Mod2002022Key.BN1056,
			Mod2002022Key.BN1618,
			Mod2002022Key.BN1621,
			Mod2002022Key.BN2298,
			Mod2002022Key.BN708,
			Mod2002022Key.BN750,
			Mod2002022Key.BN1851,
			Mod2002022Key.BN1854,
			Mod2002022Key.BN2500,
			Mod2002022Key.BN1354,
			Mod2002022Key.BN753,
			Mod2002022Key.BN2222,
			Mod2002022Key.BN2225,
			Mod2002022Key.BN2092,
			Mod2002022Key.BN1776,
			Mod2002022Key.BN756,
			Mod2002022Key.BN2357,
			Mod2002022Key.BN2360,
			Mod2002022Key.BN2095,
			Mod2002022Key.BN1839,
			Mod2002022Key.BN759,
			Mod2002022Key.BN229,
			Mod2002022Key.BN235,
			Mod2002022Key.BN2098,
			Mod2002022Key.BN2207,
			Mod2002022Key.BN762,
			Mod2002022Key.BN781,
			Mod2002022Key.BN787,
			Mod2002022Key.BN2146,
			Mod2002022Key.BN2330,
			Mod2002022Key.BN745,
			Mod2002022Key.BN783,
			Mod2002022Key.BN2450,
			Mod2002022Key.BN1364,
			Mod2002022Key.BN1367,
			Mod2002022Key.BN796,
			Mod2002022Key.BN793,
			Mod2002022Key.BN799,
			Mod2002022Key.BN698,
			Mod2002022Key.BN888,
			Mod2002022Key.BN808,
			Mod2002022Key.BN2463,
			Mod2002022Key.BN1076,
			Mod2002022Key.BN2456,
			Mod2002022Key.BN1370,
			Mod2002022Key.BN1627,
			Mod2002022Key.BN1639,
			Mod2002022Key.BN1708,
			Mod2002022Key.BN1908,
			Mod2002022Key.BN1911,
			Mod2002022Key.BN1935,
			Mod2002022Key.BN2363,
			Mod2002022Key.BN2366,
			Mod2002022Key.BN2369,
			Mod2002022Key.BN2372,
			Mod2002022Key.BN2375,
			Mod2002022Key.BN2378,
			Mod2002022Key.BN255,
			Mod2002022Key.BN260,
			Mod2002022Key.BN263,
			Mod2002022Key.BN269,
			Mod2002022Key.BN273,
			Mod2002022Key.BN292,
			Mod2002022Key.BN295,
			Mod2002022Key.BN298,
			Mod2002022Key.BN316,
			Mod2002022Key.BN349,
			Mod2002022Key.BN353,
			Mod2002022Key.BN367,
			Mod2002022Key.BN401,
			Mod2002022Key.BN407,
			Mod2002022Key.BN423,
			Mod2002022Key.BN428,
			Mod2002022Key.BN431,
			Mod2002022Key.BN434,
			Mod2002022Key.BN440,
			Mod2002022Key.BN453,
			Mod2002022Key.BN456,
			Mod2002022Key.BN469,
			Mod2002022Key.BN479,
			Mod2002022Key.BN502,
			Mod2002022Key.BN511,
			Mod2002022Key.BN523,
			Mod2002022Key.BN542,
			Mod2002022Key.BN801,
			Mod2002022Key.BN816,
			Mod2002022Key.BN2459,
			Mod2002022Key.BN875,
			Mod2002022Key.BN879,
			Mod2002022Key.BN906,
			Mod2002022Key.BN955,
			Mod2002022Key.BN1087,
			Mod2002022Key.BN1110,
			Mod2002022Key.BN1144,
			Mod2002022Key.BN1150,
			Mod2002022Key.BN1153,
			Mod2002022Key.BN1156,
			Mod2002022Key.BN1180,
			Mod2002022Key.BN1207,
			Mod2002022Key.BN1218,
			Mod2002022Key.BN1221,
			Mod2002022Key.BN1229,
			Mod2002022Key.BN1235,
			Mod2002022Key.BN1238,
			Mod2002022Key.BN1262,
			Mod2002022Key.BN1265,
			Mod2002022Key.BN1268,
			Mod2002022Key.BN1273,
			Mod2002022Key.BN1278,
			Mod2002022Key.BN1282,
			Mod2002022Key.BN1684,
			Mod2002022Key.BN829,
			Mod2002022Key.BN252,
			Mod2002022Key.BN697,
			Mod2002022Key.BN905,
			Mod2002022Key.BN901,
			Mod2002022Key.BN991,
			Mod2002022Key.BN917,
			Mod2002022Key.BN998,
			Mod2002022Key.BN931,
			Mod2002022Key.BN247,
			Mod2002022Key.BN819,
			Mod2002022Key.BN934,
			Mod2002022Key.BN944,
			Mod2002022Key.BN994,
			Mod2002022Key.BN833,
			Mod2002022Key.BN950,
			Mod2002022Key.BN953,
			Mod2002022Key.BN1435,
			Mod2002022Key.BN836,
			Mod2002022Key.BN2473,
			Mod2002022Key.BN959,
			Mod2002022Key.BN1719,
			Mod2002022Key.BN839,
			Mod2002022Key.BN965,
			Mod2002022Key.BN971,
			Mod2002022Key.BN1951,
			Mod2002022Key.BN844,
			Mod2002022Key.BN975,
			Mod2002022Key.BN981,
			Mod2002022Key.BN2228,
			Mod2002022Key.BN869,
			Mod2002022Key.BN984,
			Mod2002022Key.BN1001,
			Mod2002022Key.BN2381,
			Mod2002022Key.BN873,
			Mod2002022Key.BN1017,
			Mod2002022Key.BN1035,
			Mod2002022Key.BN876,
			Mod2002022Key.BN892,
			Mod2002022Key.BN1062,
			Mod2002022Key.BN1074,
			Mod2002022Key.BN1324,
			Mod2002022Key.BN1327,
			Mod2002022Key.BN1372,
			Mod2002022Key.BN1375,
			Mod2002022Key.BN1437,
			Mod2002022Key.BN1440,
			Mod2002022Key.BN1444,
			Mod2002022Key.BN1723,
			Mod2002022Key.BN1955,
			Mod2002022Key.BN2232,
			Mod2002022Key.BN2385,
			Mod2002022Key.BN1084,
			Mod2002022Key.BN1379,
			Mod2002022Key.BN1446,
			Mod2002022Key.BN1449,
			Mod2002022Key.BN1453,
			Mod2002022Key.BN1727,
			Mod2002022Key.BN1959,
			Mod2002022Key.BN2236,
			Mod2002022Key.BN2389,
			Mod2002022Key.BN2478,
			Mod2002022Key.BN1383
	};
	
	private static final Mod2002022Key[] TRAMO_3 = new Mod2002022Key[] {			
			Mod2002022Key.BN2082,
			Mod2002022Key.BN2085,
			Mod2002022Key.BN2089,
			Mod2002022Key.BN1917,
			Mod2002022Key.BN1920,
			Mod2002022Key.BN1923,
			Mod2002022Key.BN1926,
			Mod2002022Key.BN1929,
			Mod2002022Key.BN2191,
			Mod2002022Key.BN881,
			Mod2002022Key.BN867,
			Mod2002022Key.BN940,
			Mod2002022Key.BN192,
			Mod2002022Key.BN614,
			Mod2002022Key.BN257,
			Mod2002022Key.BN855,
			Mod2002022Key.BN038,
			Mod2002022Key.BN858,
			Mod2002022Key.BN045,
			Mod2002022Key.BN861,
			Mod2002022Key.BN529,
			Mod2002022Key.BN864,
			Mod2002022Key.BN145,
			Mod2002022Key.BN884,
			Mod2002022Key.BN148,
			Mod2002022Key.BN789,
			Mod2002022Key.BN241,
			Mod2002022Key.BN1358,
			Mod2002022Key.BN1059,
			Mod2002022Key.BN1779,
			Mod2002022Key.BN802,
			Mod2002022Key.BN853,
			Mod2002022Key.BN2336,
			Mod2002022Key.BN1782,
			Mod2002022Key.BN2120,
			Mod2002022Key.BN2117,
			Mod2002022Key.BN2339,
			Mod2002022Key.BN2123,
			Mod2002022Key.BN2126,
			Mod2002022Key.BN2210,
			Mod2002022Key.BN2342,
			Mod2002022Key.BN2213,
			Mod2002022Key.BN2216,
			Mod2002022Key.BN2333,
			Mod2002022Key.BN2345,
			Mod2002022Key.BN2348,
			Mod2002022Key.BN2351,
			Mod2002022Key.BN238,
			Mod2002022Key.BN245,
			Mod2002022Key.BN218,
			Mod2002022Key.BN221,
			Mod2002022Key.BN712,
			Mod2002022Key.BN1913,
			Mod2002022Key.BN768,
			Mod2002022Key.BN771,
	};
	
	private static final Mod2002022Key[] TRAMO_4 = new Mod2002022Key[] {	
			Mod2002022Key.BN1932,
			Mod2002022Key.BN2149,
			Mod2002022Key.BN1939,
			Mod2002022Key.BN2153,
			Mod2002022Key.BN1943,
			Mod2002022Key.BN2157,
			Mod2002022Key.BN1947,
			Mod2002022Key.BN2161,
			Mod2002022Key.BN2110,
			Mod2002022Key.BN2165,
			Mod2002022Key.BN2129,
			Mod2002022Key.BN2169,
			Mod2002022Key.BN2133,
			Mod2002022Key.BN2173,
			Mod2002022Key.BN2137,
			Mod2002022Key.BN1310,
			Mod2002022Key.BN2141,
			Mod2002022Key.BN1314,
			Mod2002022Key.BN574,
			Mod2002022Key.BN977,
			Mod2002022Key.BN824,
			Mod2002022Key.BN850,
			Mod2002022Key.BN1125,
			Mod2002022Key.BN1129,
			Mod2002022Key.BN1428,
			Mod2002022Key.BN1432,
			Mod2002022Key.BN1712,
			Mod2002022Key.BN1716,
			Mod2002022Key.BN1970,
			Mod2002022Key.BN1974,
			Mod2002022Key.BN2247,
			Mod2002022Key.BN2251,
			Mod2002022Key.BN2393,
			Mod2002022Key.BN2397,
			Mod2002022Key.BN1092,
			Mod2002022Key.BN1096,
			Mod2002022Key.BN1387,
			Mod2002022Key.BN1391	
	};	
	
			
}


