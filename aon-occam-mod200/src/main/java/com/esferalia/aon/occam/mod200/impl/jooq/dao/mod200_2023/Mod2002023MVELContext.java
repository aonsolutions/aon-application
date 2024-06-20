package com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2023;

import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0001;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0002;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0003;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0004;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0005;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0006;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0008;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0009;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0010;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0012;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0013;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0014;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0015;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0017;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0018;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0019;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0021;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0022;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0024;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0025;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0030;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0034;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0036;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0038;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0046;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0047;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0048;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0049;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0057;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0058;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0063;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0064;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0066;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0069;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0071;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0072;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0079;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0080;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0083;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0084;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0085;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0088;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.LQ520;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.LQ521;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.LQ552;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.LQ553;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.LQ554;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.LQ558;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.LQ559;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.LQ560;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.LQ562;

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
import com.esferalia.aon.occam.mod200.api.model.UteBase;
import com.esferalia.aon.occam.mod200.api.model.UteParticipation;
import com.esferalia.aon.occam.mod200.api.model.UteParticipationBis;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023KeyDC;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod2002023MVELContext implements Map<String, Object> {
	
	private Map<String, AccountBalance> accounts;
	private Map<String, Object> context;
	private EnumMap<Mod2002023Key,String> expressionMap = new EnumMap<Mod2002023Key,String>(Mod2002023Key.class);	
	private IAccMiningKeyAccept resolver;
	private Stack<IMod200Key> stack = new Stack<IMod200Key>();
	
	private static final int LIM_1 = 300000;
	private static final int LIM_2 = 1000000;
	private static final int LIM_3 = 25000;
	
	private Mod2002023 mod200;
	
	public Mod2002023MVELContext(Mod2002023 mod200,IAccMiningKeyAccept resolver) {
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
	
	public EnumMap<Mod2002023Key,String> getExpressionMap() {
		return expressionMap;
	}

	public void setExpressionMap(EnumMap<Mod2002023Key,String> expressionMap) {
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

		// Comprobar si la clave es de Mod2002023Key
		IMod200Key key = Mod2002023Key.safeValueOf((String) keyObject);
		// Si no lo es comprobar si es de Mod2002023KeyDC
		if (key == null)
			key = Mod2002023KeyDC.safeValueOf((String) keyObject);
			
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
	
	public Object evaluateExpression(Mod2002023Key key,String expression) {
		return mvelEval(key,expression);
	}
	public boolean validateExpression(Mod2002023Key key,String expression) {
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

	public Boolean isChecked(Mod2002023Key key) {
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
	
	public Double getValue(Mod2002023Key key) {
		Object o = get(key.toString());
		if (o == null) {
			return 0.0;
		}
		return (Double) o;
	}
	
	private Double roundKey(Mod2002023Key key) {
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
		
		// Siempre que se marque la clave 00030, 00047, 00009, 00010 ó 00049 de caracteres, 
		// aunque esté combinada con otros caracteres, el tipo de gravamen quedará abierto (en blanco), 
		// sin validación, para su cumplimentación por el contribuyente
		if ( isChecked(C0030) || 
			 isChecked(C0047) ||
			 isChecked(C0009) || 
			 isChecked(C0010) ||				 
			 isChecked(C0049) )
			return roundKey(LQ558);
		
		if ( isChecked(C0088) ) return 23.0;
		if ( isChecked(C0083) ) return 15.0;
		if ( isChecked(C0063) ) return 15.0;
		if ( isChecked(C0066) ) return 25.0;
		
		// En caso de combinaciones de caracteres 00017 o 00018 con 00024, prevalecerá el tipo de gravamen más favorable
		if ( (isChecked(C0017) || isChecked(C0018)) && isChecked(C0024) ) {
			if (roundKey(LQ558) <= 30.0)
				return roundKey(LQ558);
			else return 30.0;			
		}
		
		if ( isChecked(C0071) ) return 15.0;
		if ( isChecked(C0024) ) return 30.0;	
		
		if ( isChecked(C0046) ) return 25.0;
		if ( isChecked(C0021) ) return 25.0;

		if ( isChecked(C0001) ) return 10.0;
		if ( isChecked(C0002) ) return 25.0;
		if ( isChecked(C0003) ) return 1.0;
		if ( isChecked(C0004) ) return 1.0;
		if ( isChecked(C0005) ) return 25.0;
		
		if ( isChecked(C0017) ) return roundKey(LQ558);
		if ( isChecked(C0018) ) return roundKey(LQ558);
		
		if ( (isChecked(C0013) || isChecked(C0085)) && (getValue(Mod2002023Key.UT060) == 100.0) ) return 0.0;
		
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
		double lq1330 = roundKey(Mod2002023Key.LQ1330);
		
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
		if (isChecked(C0022) && (isChecked(C0006) || isChecked(C0013) || isChecked(C0085) || isChecked(C0063) || isChecked(C0071) || isChecked(C0083) || isChecked(C0088))) {
			return getValue(LQ562);
		}
		
		if (isChecked(C0057)) {
			double lq521 = roundKey(LQ521);
			if (round(lq1330-lq521) > 0) {
				return round((lq1330 - lq521) * lq558 /100);
			}
			return 0;
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
		
		// Cálculo de la cuota integra con caracter general
		return round(lq1330 * lq558 / 100);
	}
	
	public double computeLQ550() throws AonCoreException {
		if (isChecked(C0022) || isChecked(Mod2002023Key.C0009) || isChecked(Mod2002023Key.C0010)) {
			if (isChecked(C0022)) {
				double lq578 = roundKey(Mod2002023Key.LQ578);
				double lq579 = roundKey(Mod2002023Key.LQ579);
				if (!isChecked(Mod2002023Key.C0009) && !isChecked(Mod2002023Key.C0010)) {
					if (lq578 > 0) {
						return lq578 + lq579;
					} else {
						return lq579;
					}
				} else {
					double lq1030 = roundKey(Mod2002023Key.LQ1030);
					double lq1031 = roundKey(Mod2002023Key.LQ1031);
					if ((lq578 + lq1030 + lq1031) > 0) {
						return lq578 + lq1030 + lq1031 + lq579;
					} else {
						return lq579;
					}
				}
			} else {
				double lq1029 = roundKey(Mod2002023Key.LQ1029);
				double lq1030 = roundKey(Mod2002023Key.LQ1030);
				if (isCooperativa()) {
					return lq1029 + lq1030;
				}
				double lq1031 = roundKey(Mod2002023Key.LQ1031);
				return lq1029 + lq1030 + lq1031;
			}
		}
		double lq501 = roundKey(Mod2002023Key.LQ501);
		double i0417 = roundKey(Mod2002023Key.I0417);
	 	double d0418 = roundKey(Mod2002023Key.D0418);
		return lq501 + i0417 - d0418;
	}
	
	public double computeLM1249() throws AonCoreException {
		double lm1250 = roundKey(Mod2002023Key.LM1250); 
		double lm1251 = roundKey(Mod2002023Key.LM1251);
		double lm1252 = roundKey(Mod2002023Key.LM1252);
		double lm1253 = roundKey(Mod2002023Key.LM1253);
		double lm1254 = roundKey(Mod2002023Key.LM1254);
		double lm1256 = roundKey(Mod2002023Key.LM1256);
		double lm1258 = roundKey(Mod2002023Key.LM1258);
		double lm1259 = roundKey(Mod2002023Key.LM1259);
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
			if (isChecked(C0057)) {
				return round((lq553 * lq558 / 100) + ((lq554-lq521) * 25 / 100) + (lq521 * 0));		
			}
			if (lq558 == 20 || lq558 == 25) {
				return round( (lq553 * lq558 / 100) + (lq554 * 25 / 100));
			}
		} else if (isChecked(C0019) ) {
			if (isChecked(C0088)) {
				return round(lq552*23/100);
			}
			if (isChecked(C0071) || isChecked(C0083)) {
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
			return round( (lq552 * lq558 / 100) );
		}
		return 0.0;
	}
	
	private double computePreBN599() throws AonCoreException {
		double BN592  = roundKey(Mod2002023Key.BN592 );
		double BN1785 = roundKey(Mod2002023Key.BN1785);
		double BN1786 = roundKey(Mod2002023Key.BN1786);
		double BN1787 = roundKey(Mod2002023Key.BN1787);
		double BN1788 = roundKey(Mod2002023Key.BN1788);
		double BN1789 = roundKey(Mod2002023Key.BN1789);
		double BN1790 = roundKey(Mod2002023Key.BN1790);
		double BN1791 = roundKey(Mod2002023Key.BN1791);
		double BN1792 = roundKey(Mod2002023Key.BN1792);
		double BN1793 = roundKey(Mod2002023Key.BN1793);
		double BN1794 = roundKey(Mod2002023Key.BN1794);
		double BN1795 = roundKey(Mod2002023Key.BN1795);
		double BN1796 = roundKey(Mod2002023Key.BN1796);
		double BN1797 = roundKey(Mod2002023Key.BN1797);
		double BN1798 = roundKey(Mod2002023Key.BN1798);
		double BN597  = roundKey(Mod2002023Key.BN597 );
		double BN1799 = roundKey(Mod2002023Key.BN1799);
		return BN592-BN1785-BN1786-BN1787-BN1788-BN1789-BN1790-BN1791-BN1792-BN1793-BN1794-BN1795-BN1796-BN1797-BN1798-BN597-BN1799;
	}
	
	public double computeBN599() throws AonCoreException {	
		if (isChecked(Mod2002023Key.C0028)) {
			double TR625 = roundKey(Mod2002023Key.TR625);
			return round(TR625 * computePreBN599() / 100);
		} else {
		    return computePreBN599();
		}
	}
	
	public double computeTR420() throws AonCoreException {
		if (isChecked(Mod2002023Key.C0028)) {
			double TR626 = roundKey(Mod2002023Key.TR626);
			return round(TR626 * computePreBN599() / 100);
		}
		return 0.0;
	}
	public double computeTR421() throws AonCoreException {
		if (isChecked(Mod2002023Key.C0028)) {
			double TR627 = roundKey(Mod2002023Key.TR627);
			return round(TR627 * computePreBN599() / 100);
		}
		return 0.0;
	}
	public double computeTR426() throws AonCoreException {
		if (isChecked(Mod2002023Key.C0028)) {
			double TR628 = roundKey(Mod2002023Key.TR628);
			return round(TR628 * computePreBN599() / 100);
		}
		return 0.0;
	}
	public double computeTR427() throws AonCoreException {
		if (isChecked(Mod2002023Key.C0028)) {
			double TR629 = roundKey(Mod2002023Key.TR629);
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
		if (isChecked(Mod2002023Key.C0013) || isChecked(Mod2002023Key.C0085) || isChecked(Mod2002023Key.C0014))
		{
			double x = roundKey(Mod2002023Key.LQ1330);
			
			// Si cambia el valor entonces actualizar tambien la base de la relación de 
			// socios, que se calcula en funcion de la casilla 1330
			if (x != roundKey(Mod2002023Key.UT1330))
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
	
	// Nuevas casillas de totales del apartado B.6 DE UTES (casillas 1277 y 1278)
	
	public double computeUT1277() throws AonCoreException {
		
		if (isChecked(Mod2002023Key.C0013) || isChecked(Mod2002023Key.C0085) || isChecked(Mod2002023Key.C0014))
		{
			double x = 0;
			for (UteBase b : mod200.getUteBases()) {				
				x = x + b.getBase();				  						
			}					 
			return round(x);
		}
		else return 0.0;
	}
	public double computeUT1278() throws AonCoreException {
		
		if (isChecked(Mod2002023Key.C0013) || isChecked(Mod2002023Key.C0085) || isChecked(Mod2002023Key.C0014))
		{
			double x = 0;
			for (UteBase b : mod200.getUteBases()) {				
				x = x + b.getAmount();				  						
			}					 
			return round(x);
		}
		else return 0.0;
	}
	
	// Casillas de totales del apartado Partícipes de agrupaciones de interés económico y UTES
	
	public double computeUT1279() throws AonCoreException {
		
		if (isChecked(Mod2002023Key.C0089))
		{
			double x = 0;
			for (UteParticipationBis up : mod200.getUteParticipationsBis()) {				
				x = x + up.getC01279();
			}
			return round(x);			
		}
		else return 0.0;
		
	}
	public double computeUT1455() throws AonCoreException {
		
		if (isChecked(Mod2002023Key.C0089))
		{
			double x = 0;
			for (UteParticipationBis up : mod200.getUteParticipationsBis()) {				
				x = x + up.getC01455();
			}
			return round(x);			
		}
		else return 0.0;
		
	}	
	public double computeUT1456() throws AonCoreException {
		
		if (isChecked(Mod2002023Key.C0089))
		{
			double x = 0;
			for (UteParticipationBis up : mod200.getUteParticipationsBis()) {				
				x = x + up.getC01456();
			}
			return round(x);			
		}
		else return 0.0;
		
	}	
	public double computeUT1458() throws AonCoreException {
		
		if (isChecked(Mod2002023Key.C0089))
		{
			double x = 0;
			for (UteParticipationBis up : mod200.getUteParticipationsBis()) {				
				x = x + up.getC01458();
			}
			return round(x);			
		}
		else return 0.0;
		
	}	
	public double computeUT1459() throws AonCoreException {
		
		if (isChecked(Mod2002023Key.C0089))
		{
			double x = 0;
			for (UteParticipationBis up : mod200.getUteParticipationsBis()) {				
				x = x + up.getC01459();
			}
			return round(x);			
		}
		else return 0.0;
		
	}	
	public double computeUT1460() throws AonCoreException {
		
		if (isChecked(Mod2002023Key.C0089))
		{
			double x = 0;
			for (UteParticipationBis up : mod200.getUteParticipationsBis()) {				
				x = x + up.getC01460();
			}
			return round(x);			
		}
		else return 0.0;
		
	}	
	public double computeUT1461() throws AonCoreException {
		
		if (isChecked(Mod2002023Key.C0089))
		{
			double x = 0;
			for (UteParticipationBis up : mod200.getUteParticipationsBis()) {				
				x = x + up.getC01461();
			}
			return round(x);			
		}
		else return 0.0;
		
	}	
	public double computeUT1467() throws AonCoreException {
		
		if (isChecked(Mod2002023Key.C0089))
		{
			double x = 0;
			for (UteParticipationBis up : mod200.getUteParticipationsBis()) {				
				x = x + up.getC01467();
			}
			return round(x);			
		}
		else return 0.0;
		
	}	
	public double computeUT1468() throws AonCoreException {
		
		if (isChecked(Mod2002023Key.C0089))
		{
			double x = 0;
			for (UteParticipationBis up : mod200.getUteParticipationsBis()) {				
				x = x + up.getC01468();
			}
			return round(x);			
		}
		else return 0.0;
		
	}	
	public double computeUT1523() throws AonCoreException {
		
		if (isChecked(Mod2002023Key.C0089))
		{
			double x = 0;
			for (UteParticipationBis up : mod200.getUteParticipationsBis()) {				
				x = x + up.getC01523();
			}
			return round(x);			
		}
		else return 0.0;
		
	}	
	public double computeUT1601() throws AonCoreException {
		
		if (isChecked(Mod2002023Key.C0089))
		{
			double x = 0;
			for (UteParticipationBis up : mod200.getUteParticipationsBis()) {				
				x = x + up.getC01601();
			}
			return round(x);			
		}
		else return 0.0;
		
	}	
	public double computeUT1638() throws AonCoreException {
		
		if (isChecked(Mod2002023Key.C0089))
		{
			double x = 0;
			for (UteParticipationBis up : mod200.getUteParticipationsBis()) {				
				x = x + up.getC01638();
			}
			return round(x);			
		}
		else return 0.0;
		
	}	
	public double computeUT1639() throws AonCoreException {
		
		if (isChecked(Mod2002023Key.C0089))
		{
			double x = 0;
			for (UteParticipationBis up : mod200.getUteParticipationsBis()) {				
				x = x + up.getC01639();
			}
			return round(x);			
		}
		else return 0.0;
		
	}	
	public double computeUT1640() throws AonCoreException {
		
		if (isChecked(Mod2002023Key.C0089))
		{
			double x = 0;
			for (UteParticipationBis up : mod200.getUteParticipationsBis()) {				
				x = x + up.getC01640();
			}
			return round(x);			
		}
		else return 0.0;
		
	}	
	public double computeUT1743() throws AonCoreException {
		
		if (isChecked(Mod2002023Key.C0089))
		{
			double x = 0;
			for (UteParticipationBis up : mod200.getUteParticipationsBis()) {				
				x = x + up.getC01743();
			}
			return round(x);			
		}
		else return 0.0;
		
	}	
	public double computeUT1909() throws AonCoreException {
		
		if (isChecked(Mod2002023Key.C0089))
		{
			double x = 0;
			for (UteParticipationBis up : mod200.getUteParticipationsBis()) {				
				x = x + up.getC01909();
			}
			return round(x);			
		}
		else return 0.0;
		
	}	
	public double computeUT1910() throws AonCoreException {
		
		if (isChecked(Mod2002023Key.C0089))
		{
			double x = 0;
			for (UteParticipationBis up : mod200.getUteParticipationsBis()) {				
				x = x + up.getC01910();
			}
			return round(x);			
		}
		else return 0.0;
		
	}	
	public double computeUT1911() throws AonCoreException {
		
		if (isChecked(Mod2002023Key.C0089))
		{
			double x = 0;
			for (UteParticipationBis up : mod200.getUteParticipationsBis()) {				
				x = x + up.getC01911();
			}
			return round(x);			
		}
		else return 0.0;
		
	}	
	public double computeUT1912() throws AonCoreException {
		
		if (isChecked(Mod2002023Key.C0089))
		{
			double x = 0;
			for (UteParticipationBis up : mod200.getUteParticipationsBis()) {				
				x = x + up.getC01912();
			}
			return round(x);			
		}
		else return 0.0;
		
	}	
	public double computeUT1934() throws AonCoreException {
		
		if (isChecked(Mod2002023Key.C0089))
		{
			double x = 0;
			for (UteParticipationBis up : mod200.getUteParticipationsBis()) {				
				x = x + up.getC01934();
			}
			return round(x);			
		}
		else return 0.0;
		
	}
	
	// Cálculo del importe de la columna 2 del desglose de la casilla [1033]
	// La clave 01033 (aumentos) sólo podrá tener contenido cuando la base imponible (clave 00552) sea negativa 
	// (excepto en los supuestos de extinción de entidad (clave 00072) y de último período permitido para la adición) 
	// y su importe máximo será el importe de dicha base negativa, excepto en los supuestos en que se cumplimente 
	// la clave 01962 en cuyo caso podrá exceder por el importe consignado en esta casilla.
	public double computeLQ1033_1(double col1, double col3, double suma) throws AonCoreException {
		
		double lq552 = getValue(Mod2002023Key.LQ552);
		double lq1962 = getValue(Mod2002023Key.LQ1962);
		
		if (lq552>=0 && !isChecked(C0072)) {
			// Base imponible [552] positiva o cero, [col2] = 0 
			return 0.0;
		} else {
			// Base imponible [552] negativa, [col2] = [col1] - [col3] con el limite de la [552]
			double col2 = col1-col3;
			double total = suma + col2;			
			if (total > (Math.abs(lq552)+lq1962)) {
				col2 = col2 - (total - (Math.abs(lq552)+lq1962));
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
		
		double lq552 = getValue(Mod2002023Key.LQ552);
		if (isChecked(Mod2002023Key.C0006) && !isChecked(Mod2002023Key.C0072) && lq552 > 0) {
			double lq1034 = roundKey(Mod2002023Key.LQ1034A);
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
		
		double lq550 = roundKey(Mod2002023Key.LQ550);
		double lq1032 = roundKey(Mod2002023Key.LQ1032);
		double lq547 = roundKey(Mod2002023Key.LQ547);
		double lq541 = roundKey(Mod2002023Key.LQ541);
		double lq564 = roundKey(Mod2002023Key.LQ564);

		if (isChecked(C0069) && lq541 > 0 && lq564 < 0)
			return lq541 + lq564 - lq547;
		else if (isChecked(C0069) && lq541 < 0 && lq564 >= 0)
			return lq564 - lq1032 - lq547;
		else
			return lq550 - lq1032 - lq547;
		
	}
	
	// Casilla 619: Cuota líquida mínima (art. 30 bis.2 LIS)  
	public double computeBN619() throws AonCoreException {
		
		// Estarán excluidos de la tributación mínima los contribuyentes que hayan marcado alguno de los 
		// caracteres 00001, 00003, 00004, 00009, 00010, 00012, 00047, 00048 y 00064. También estarán excluidos los 
		// contribuyentes que marquen el carácter 00008 junto con el 00084 que tributen al tipo de gravamen 
		// del 1% y los contribuyentes que hayan marcado la clave 00014 de caracteres (agrupaciones europeas de interés económico).		
		
		if (isChecked(C0001) || isChecked(C0003) || isChecked(C0004) || isChecked(C0009) || isChecked(C0010) || 
			isChecked(C0012) || isChecked(C0047) || isChecked(C0048) || isChecked(C0064) || isChecked(C0014)) {
			return 0.0;			
		}
		
		double lq558 = roundKey(Mod2002023Key.LQ558);  // Tipo de Gravamen
		if (isChecked(C0008) && isChecked(C0084) && lq558 == 1.0) {
			return 0.0;
		}
		
		// La tributación mínima es aplicable en los siguientes casos:
		// - Contribuyentes que marquen el supuesto 2 (INCN de al menos 20 millones de euros) (supuestos 2 o 3 en caso de cooperativas)
		// - Contribuyentes que marquen el caracter 00079 de la página 1 de la declaración (excepto supuestos excluidos).
		int volope = getValue(Mod2002023Key.VOLOPE).intValue();
		if (volope >= 2 || isChecked(C0079)) { 

			// CALCULO DE LA TRIBUTACION MINIMA

			double c01330 = roundKey(Mod2002023Key.LQ1330);
			double c00778 = roundKey(Mod2002023Key.I0778);
			double c00813 = roundKey(Mod2002023Key.D0813);
			double c00562 = roundKey(Mod2002023Key.LQ562);
			double c01038 = roundKey(Mod2002023Key.LQ1038);
			double c00559 = roundKey(Mod2002023Key.LQ559);
			double c00558 = roundKey(Mod2002023Key.LQ558);
			double c00012 = roundKey(Mod2002023Key.CP0012);
			double c00016 = roundKey(Mod2002023Key.CP0016);
			
			double m1 = (c01330-c00778+c00813) * 0.15;

			if (isChecked(C0071))
				m1 = (c01330-c00778+c00813) * 0.10;

			if (isChecked(C0024) || isChecked(C0034))
				m1 = (c01330-c00778+c00813) * 0.18;			
			
			if (isChecked(C0019))
			    m1 = (c00562 - (c00558 / 100 * (c00012 + c00016))) * 0.60;			         

			if (isChecked(C0017) || isChecked(C0018))
				m1 = (c00562 - ((c00558 / 100 * c00012) + (25.0 / 100 * c00016))) * 0.60;
			         
			if (isChecked(C0015) || isChecked(C0079)) {
				if (isChecked(C0024))
					m1 = (c01330-c00778+c00813-c00559) * 0.18;
				else
					m1 = (c01330-c00778+c00813-c00559) * 0.15;
			}
			
			double tramo1 = computeSection(TRAMO_1); 
			double tramo2 = computeSection(TRAMO_2); 
			double tramo3 = computeSection(TRAMO_3); 
			double tramo4 = computeSection(TRAMO_4); 
			
			double m2 = c00562 + c01038 - tramo1; 
			double m3 = m2 - tramo3;			
			if (m3<m1)
				tramo2 = 0;
			else if (tramo2 > m3-m1) {
				tramo2 = m3-m1;
			}
			double m4 = m3 - tramo2 - tramo4;
			
			// System.out.println("CUOTA MINIMA: M1="+m1+" M2="+m2+" M3="+m3+" M4="+m4+" tramo1="+tramo1+" tramo2="+tramo2+" tramo3="+tramo3+" tramo4="+tramo4);

			if (m4 > m1)
				return m1;
			else
				return m4;
		}
		else {
			// La tributación mínima no es aplicable
			return 0.0;
		}		
	
	}
	
	private double computeSection(Mod2002023Key[] section) {
		double result = 0;
		
		for (Mod2002023Key key : section) {
			result = result + roundKey(key);
		}
		
		return result;
	}
	
	// TRAMO 1 DE DEDUCCIONES Y BONIFICACIONES
	private static final Mod2002023Key[] TRAMO_1 = new Mod2002023Key[] {
			Mod2002023Key.BN567,
			Mod2002023Key.BN568,
			Mod2002023Key.BN563,
			Mod2002023Key.BN815,			
			Mod2002023Key.BN566,
			Mod2002023Key.BN576,
			Mod2002023Key.BN569,
			Mod2002023Key.BN581,
			Mod2002023Key.BN1287,
			Mod2002023Key.BN1290,
			Mod2002023Key.BN1293,
			Mod2002023Key.BN1296,
			Mod2002023Key.BN2313,
			Mod2002023Key.BN575,
			Mod2002023Key.BN577,
			Mod2002023Key.BN165,
			Mod2002023Key.BN169,
			Mod2002023Key.BN847,
			Mod2002023Key.BN638,
			Mod2002023Key.BN283,
			Mod2002023Key.BN894,
			Mod2002023Key.BN703,
			Mod2002023Key.BN286,
			Mod2002023Key.BN187,
			Mod2002023Key.BN826,
			Mod2002023Key.BN026,
			Mod2002023Key.BN002,
			Mod2002023Key.BN715,
			Mod2002023Key.BN029,
			Mod2002023Key.BN737,
			Mod2002023Key.BN718,
			Mod2002023Key.BN120,
			Mod2002023Key.BN723,
			Mod2002023Key.BN125,
			Mod2002023Key.BN741,
			Mod2002023Key.BN1598,
			Mod2002023Key.BN136,
			Mod2002023Key.BN1831,
			Mod2002023Key.BN1052,
			Mod2002023Key.BN2199,
			Mod2002023Key.BN1351,
			Mod2002023Key.BN2322,
			Mod2002023Key.BN1773,
			Mod2002023Key.BN205,
			Mod2002023Key.BN1836,
			Mod2002023Key.BN438,
			Mod2002023Key.BN128,
			Mod2002023Key.BN2204,
			Mod2002023Key.BN1318,
			Mod2002023Key.BN2327,
			Mod2002023Key.BN212,
			Mod2002023Key.BN493,
			Mod2002023Key.BN1472
	};
	
	// TRAMO 2 DEDUCCIONES
	private static final Mod2002023Key[] TRAMO_2 = new Mod2002023Key[] {			
			Mod2002023Key.BN583,
			Mod2002023Key.BN467,
			Mod2002023Key.BN498,
			Mod2002023Key.BN473,
			Mod2002023Key.BN181,
			Mod2002023Key.BN032,
			Mod2002023Key.BN532,
			Mod2002023Key.BN023,
			Mod2002023Key.BN946,
			Mod2002023Key.BN041,
			Mod2002023Key.BN961,
			Mod2002023Key.BN139,
			Mod2002023Key.BN185,
			Mod2002023Key.BN142,
			Mod2002023Key.BN458,
			Mod2002023Key.BN461,
			Mod2002023Key.BN967,
			Mod2002023Key.BN189,
			Mod2002023Key.BN1067,
			Mod2002023Key.BN1070,
			Mod2002023Key.BN1064,
			Mod2002023Key.BN804,
			Mod2002023Key.BN810,
			Mod2002023Key.BN591,
			Mod2002023Key.BN2295,
			Mod2002023Key.BN1056,
			Mod2002023Key.BN1618,
			Mod2002023Key.BN1621,
			Mod2002023Key.BN2298,
			Mod2002023Key.BN708,
			Mod2002023Key.BN1851,
			Mod2002023Key.BN1854,
			Mod2002023Key.BN2500,
			Mod2002023Key.BN1354,
			Mod2002023Key.BN750,			
			Mod2002023Key.BN2222,
			Mod2002023Key.BN2225,
			Mod2002023Key.BN2092,
			Mod2002023Key.BN1776,
			Mod2002023Key.BN753,			
			Mod2002023Key.BN2357,
			Mod2002023Key.BN2360,
			Mod2002023Key.BN2095,
			Mod2002023Key.BN1839,
			Mod2002023Key.BN756,			
			Mod2002023Key.BN229,
			Mod2002023Key.BN235,
			Mod2002023Key.BN2098,
			Mod2002023Key.BN2207,			
			Mod2002023Key.BN759,			
			Mod2002023Key.BN781,
			Mod2002023Key.BN787,
			Mod2002023Key.BN2146,
			Mod2002023Key.BN2330,			
			Mod2002023Key.BN762,
			Mod2002023Key.BN1875,
			Mod2002023Key.BN1895,
			Mod2002023Key.BN1849,
			Mod2002023Key.BN252,
			Mod2002023Key.BN745,
			Mod2002023Key.BN783,
			Mod2002023Key.BN2450,
			Mod2002023Key.BN1364,
			Mod2002023Key.BN1367,
			Mod2002023Key.BN796,
			Mod2002023Key.BN793,
			Mod2002023Key.BN799,
			Mod2002023Key.BN698,
			Mod2002023Key.BN888,
			Mod2002023Key.BN808,
			Mod2002023Key.BN2463,
			Mod2002023Key.BN1076,
			Mod2002023Key.BN2456,
			Mod2002023Key.BN1370,
			Mod2002023Key.BN1627,
			Mod2002023Key.BN1708,
			Mod2002023Key.BN2363,
			Mod2002023Key.BN2366,
			Mod2002023Key.BN2372,
			Mod2002023Key.BN2375,
			Mod2002023Key.BN2378,
			Mod2002023Key.BN260,
			Mod2002023Key.BN263,
			Mod2002023Key.BN269,
			Mod2002023Key.BN273,
			Mod2002023Key.BN295,
			Mod2002023Key.BN298,
			Mod2002023Key.BN316,
			Mod2002023Key.BN349,
			Mod2002023Key.BN353,
			Mod2002023Key.BN367,
			Mod2002023Key.BN401,
			Mod2002023Key.BN423,
			Mod2002023Key.BN428,
			Mod2002023Key.BN431,
			Mod2002023Key.BN434,
			Mod2002023Key.BN440,
			Mod2002023Key.BN453,
			Mod2002023Key.BN456,
			Mod2002023Key.BN469,
			Mod2002023Key.BN479,
			Mod2002023Key.BN502,
			Mod2002023Key.BN511,
			Mod2002023Key.BN523,
			Mod2002023Key.BN816,
			Mod2002023Key.BN2459,
			Mod2002023Key.BN875,
			Mod2002023Key.BN879,
			Mod2002023Key.BN906,
			Mod2002023Key.BN955,
			Mod2002023Key.BN1087,
			Mod2002023Key.BN1110,
			Mod2002023Key.BN1144,
			Mod2002023Key.BN1150,
			Mod2002023Key.BN1153,
			Mod2002023Key.BN1156,
			Mod2002023Key.BN1180,
			Mod2002023Key.BN1207,
			Mod2002023Key.BN1218,
			Mod2002023Key.BN1221,
			Mod2002023Key.BN1229,
			Mod2002023Key.BN1235,
			Mod2002023Key.BN1238,
			Mod2002023Key.BN1262,
			Mod2002023Key.BN1265,
			Mod2002023Key.BN1268,
			Mod2002023Key.BN1273,
			Mod2002023Key.BN1282,
			Mod2002023Key.BN1884,
			Mod2002023Key.BN1901,
			Mod2002023Key.BN1904,
			Mod2002023Key.BN1995,
			Mod2002023Key.BN1908,
			Mod2002023Key.BN2024,
			Mod2002023Key.BN2027,
			Mod2002023Key.BN2030,
			Mod2002023Key.BN2033,
			Mod2002023Key.BN2036,
			Mod2002023Key.BN2039,
			Mod2002023Key.BN2042,
			Mod2002023Key.BN2053,
			Mod2002023Key.BN2058,
			Mod2002023Key.BN2285,
			Mod2002023Key.BN089,
			Mod2002023Key.BN1684,
			Mod2002023Key.BN829,
			Mod2002023Key.BN252,
			Mod2002023Key.BN697,
			Mod2002023Key.BN1522,
			Mod2002023Key.BN991,
			Mod2002023Key.BN917,
			Mod2002023Key.BN998,
			Mod2002023Key.BN931,
			Mod2002023Key.BN247,
			Mod2002023Key.BN819,
			Mod2002023Key.BN934,
			Mod2002023Key.BN944,
			Mod2002023Key.BN994,
			Mod2002023Key.BN833,
			Mod2002023Key.BN950,
			Mod2002023Key.BN953,
			Mod2002023Key.BN1435,
			Mod2002023Key.BN836,
			Mod2002023Key.BN2473,
			Mod2002023Key.BN959,
			Mod2002023Key.BN1719,
			Mod2002023Key.BN839,
			Mod2002023Key.BN965,
			Mod2002023Key.BN971,
			Mod2002023Key.BN1951,
			Mod2002023Key.BN844,
			Mod2002023Key.BN975,
			Mod2002023Key.BN981,
			Mod2002023Key.BN2228,
			Mod2002023Key.BN869,
			Mod2002023Key.BN984,
			Mod2002023Key.BN1001,
			Mod2002023Key.BN2381,
			Mod2002023Key.BN873,
			Mod2002023Key.BN1017,
			Mod2002023Key.BN1035,
			Mod2002023Key.BN876,
			Mod2002023Key.BN892,
			Mod2002023Key.BN1062,
			Mod2002023Key.BN1074,
			Mod2002023Key.BN1324,
			Mod2002023Key.BN1327,
			Mod2002023Key.BN1372,
			Mod2002023Key.BN1375,
			Mod2002023Key.BN2576,
			Mod2002023Key.BN2692,
			Mod2002023Key.BN2695,
			Mod2002023Key.BN2698,
			Mod2002023Key.BN1437,
			Mod2002023Key.BN1440,
			Mod2002023Key.BN1444,
			Mod2002023Key.BN1723,
			Mod2002023Key.BN1955,
			Mod2002023Key.BN2232,
			Mod2002023Key.BN2385,
			Mod2002023Key.BN1084,
			Mod2002023Key.BN1379,
			Mod2002023Key.BN2703,
			Mod2002023Key.BN1446,
			Mod2002023Key.BN1449,
			Mod2002023Key.BN1453,
			Mod2002023Key.BN1727,
			Mod2002023Key.BN1959,
			Mod2002023Key.BN2236,
			Mod2002023Key.BN2389,
			Mod2002023Key.BN2478,
			Mod2002023Key.BN1383,
			Mod2002023Key.BN2707
	};

	// TRAMO 3 DE DEDUCCIONES 
	private static final Mod2002023Key[] TRAMO_3 = new Mod2002023Key[] {			
			Mod2002023Key.BN2082,
			Mod2002023Key.BN2085,
			Mod2002023Key.BN2089,
			Mod2002023Key.BN1917,
			Mod2002023Key.BN1920,
			Mod2002023Key.BN1923,
			Mod2002023Key.BN1926,
			Mod2002023Key.BN1898,
			Mod2002023Key.BN1929,
			Mod2002023Key.BN2191,
//			Mod2002023Key.BN881,  // EN EL PADIS ESTA PUESTO, PERO EN EL MODELO NO EXISTE, LA LINEA DEL 2004 SE QUITA
			Mod2002023Key.BN867,
			Mod2002023Key.BN940,
			Mod2002023Key.BN192,
			Mod2002023Key.BN614,
			Mod2002023Key.BN257,
			Mod2002023Key.BN855,
			Mod2002023Key.BN038,
			Mod2002023Key.BN858,
			Mod2002023Key.BN045,
			Mod2002023Key.BN861,
			Mod2002023Key.BN529,
			Mod2002023Key.BN864,
			Mod2002023Key.BN145,
			Mod2002023Key.BN884,
			Mod2002023Key.BN148,
			Mod2002023Key.BN789,
			Mod2002023Key.BN241,
			Mod2002023Key.BN1358,
			Mod2002023Key.BN1059,
			Mod2002023Key.BN1779,
			Mod2002023Key.BN802,
			Mod2002023Key.BN853,
			Mod2002023Key.BN2336,
			Mod2002023Key.BN1782,
			Mod2002023Key.BN2120,
			Mod2002023Key.BN2117,
			Mod2002023Key.BN2339,
			Mod2002023Key.BN2123,
			Mod2002023Key.BN2126,
			Mod2002023Key.BN2210,
			Mod2002023Key.BN2342,
			Mod2002023Key.BN2213,
			Mod2002023Key.BN2216,
			Mod2002023Key.BN2333,
			Mod2002023Key.BN2345,
			Mod2002023Key.BN2348,
			Mod2002023Key.BN2351,
			Mod2002023Key.BN238,
			Mod2002023Key.BN245,
			Mod2002023Key.BN218,
			Mod2002023Key.BN221,
			Mod2002023Key.BN712,
			Mod2002023Key.BN1913,
			Mod2002023Key.BN768,
			Mod2002023Key.BN771,
			Mod2002023Key.BN1615,
			Mod2002023Key.BN1800,
			Mod2002023Key.BN1803,
			Mod2002023Key.BN1806			
	};
	
	// TRAMO 4 DEDUCCIONES
	private static final Mod2002023Key[] TRAMO_4 = new Mod2002023Key[] {			
			Mod2002023Key.BN1932,
			Mod2002023Key.BN2149,
			Mod2002023Key.BN1939,
			Mod2002023Key.BN2153,
			Mod2002023Key.BN1943,
			Mod2002023Key.BN2157,
			Mod2002023Key.BN1947,
			Mod2002023Key.BN2161,
			Mod2002023Key.BN2110,
			Mod2002023Key.BN2165,
			Mod2002023Key.BN2129,
			Mod2002023Key.BN2169,
			Mod2002023Key.BN2133,
			Mod2002023Key.BN2173,
			Mod2002023Key.BN2137,
			Mod2002023Key.BN1310,
			Mod2002023Key.BN2141,
			Mod2002023Key.BN1314,
			Mod2002023Key.BN2355,
			Mod2002023Key.BN2466,
			Mod2002023Key.BN574,
			Mod2002023Key.BN977,
			Mod2002023Key.BN824,
			Mod2002023Key.BN850,
			Mod2002023Key.BN1125,
			Mod2002023Key.BN1129,
			Mod2002023Key.BN1428,
			Mod2002023Key.BN1432,
			Mod2002023Key.BN1712,
			Mod2002023Key.BN1716,
			Mod2002023Key.BN1970,
			Mod2002023Key.BN1974,
			Mod2002023Key.BN2247,
			Mod2002023Key.BN2251,
			Mod2002023Key.BN2393,
			Mod2002023Key.BN2397,
			Mod2002023Key.BN1092,
			Mod2002023Key.BN1096,
			Mod2002023Key.BN1387,
			Mod2002023Key.BN1391,
			Mod2002023Key.BN2757,
			Mod2002023Key.BN2762
	};
	
	
			
}