package com.esferalia.aon.occam.server.accounting;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.mvel2.MVEL;

import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccMiningMVELContext implements Map<String, Object> {

	private Map<String, AccountBalance> accounts;
	private Map<String, Object> context;
	private Map<String, String> expressionMap;
	private IAccMiningKeyAccept resolver;
	private Stack<String> stack = new Stack<String>();
	
	public AccMiningMVELContext(IAccMiningKeyAccept resolver) {
		this.context = new HashMap<String, Object>();
		this.accounts = new HashMap<String, AccountBalance>();
		this.expressionMap = new HashMap<String, String>();
		this.resolver = resolver;
	}

	public Map<String, AccountBalance> getAccounts() {
		return accounts;
	}
	
	public void setAccounts(Map<String, AccountBalance> accounts) {
		this.accounts = accounts;
	}
	
	public Map<String, String> getExpressionMap() {
		return expressionMap;
	}

	public void setExpressionMap(Map<String, String> expressionMap) {
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
		return getContainsKey(keyObject) ? context.get(keyObject) : evaluate(keyObject.toString());
	}
	
	private Boolean getContainsKey(Object keyObject) {
		return this.context.containsKey(keyObject) 
			&& (!expressionMap.containsKey(keyObject) || stack.contains(keyObject));
	}
	
	public Object evaluate(String key) {
		if (expressionMap != null) {
			String exp = expressionMap.get(key);
			if (AonStringUtils.isNotEmpty(exp)) {
				Object ret =  mvelEval(key,exp);
				if (ret != null) {
					put(key, ret);
					return ret;
				}
			}
		}
		return new Double(0);
	}
	
	public Object evaluateExpression(String key,String expression) {
		return mvelEval(key,expression);
	}
	
	private Object mvelEval(String key,String expression) {
		try {
			stack.push(key);
			return MVEL.eval( expression , this , this);
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
				ab.setChecked(true);
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
				ab.setChecked(true);
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
				ab.setChecked(true);
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
				ab.setChecked(true);
				d = d + ab.getDebitPyG();
			}
		}
		return d;
	}
	
}
