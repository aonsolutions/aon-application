package com.esferalia.aon.accounting.mining.server;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.mvel2.MVEL;

import com.esferalia.aon.accounting.mining.shared.AccMiningException;
import com.esferalia.aon.accounting.mining.shared.AccMiningUtils;
import com.esferalia.aon.accounting.mining.shared.AccountBalance;

public class AccMiningMVELContext implements Map<String, Object> {

	private Map<String, AccountBalance> accounts;
	private Map<String, Object> context;
	private Map<String, String> expressionMap;
	private IAccMiningKeyAccept resolver;
	
	public AccMiningMVELContext( IAccMiningKeyAccept resolver) {
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
		String key = (String) keyObject;
		Object obj = null;
		if (this.context.containsKey(key)) {
			obj = this.context.get(key);
		} else {
			obj = evaluate(key);
		}
		return obj;
	}

	public Object evaluate(String key) {
		if (expressionMap != null) {
			String exp = expressionMap.get(key);
			Object ret = null;
			if (AccMiningUtils.isNotEmpty(exp)) {
				ret = MVEL.eval( exp , this , this);
			}
			if (ret != null) {
				put(key, ret);
				return ret;
			}
		}
		return new Double(0);
	}
	public Object evaluateExpression(String expression) {
		return MVEL.eval( expression , this , this);
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
	 * @throws AccMiningException
	 */
	public double sa(int ... accounts  ) throws AccMiningException {
		return getCreditBalance(accounts);
	}

	/**
	 * Saldo Acreedor Positivo (Haber - Debe) de la cuenta indicada en <i>account</i>.
	 * @param accounts
	 * @return El dato si es positivo, en otro caso cero.
	 * @throws AccMiningException
	 */
	public double saPositivo(int ... accounts) throws AccMiningException {
		double d = sa(accounts); 
		return d>0?d:0;
	}
	
	/**
	 * Saldo Acreedor (Haber - Debe) de la cuenta indicada en <i>account</i>.
	 * @param accounts
	 * @return
	 * @throws AccMiningException
	 */
	public double sa(int account) throws AccMiningException {
		return getCreditBalance(new int[]{account});
	}
	
	/**
	 * Saldo Acreedor Positivo (Haber - Debe) de la cuenta indicada en <i>account</i>.
	 * @param accounts
	 * @return El dato si es positivo, en otro caso cero.
	 * @throws AccMiningException
	 */
	public double saPositivo(int account) throws AccMiningException {
		double d = sa(account); 
		return d>0?d:0;
	}

	/**
	 * Saldo Deudor (Debe - Haber) del sumatorio de las cuentas indicadas en <i>accounts</i>.
	 * @param accounts
	 * @return
	 * @throws AccMiningException
	 */
	public double sd(int ... accounts  ) throws AccMiningException {
		return getDebitBalance(accounts);
	}
	/**
	 * Saldo Deudor Positivo (Haber - Debe) de la cuenta indicada en <i>account</i>.
	 * @param accounts
	 * @return El dato si es positivo, en otro caso cero.
	 * @throws AccMiningException
	 */
	public double sdPositivo(int ... accounts) throws AccMiningException {
		double d = sd(accounts); 
		return d>0?d:0;
	}
	/**
	 * Saldo Deudor (Debe - Haber) de la cuenta indicada en <i>account</i>.
	 * @param accounts
	 * @return
	 * @throws AccMiningException
	 */
	public double sd(int account) throws AccMiningException {
		return getDebitBalance(new int[]{account});
	}
	/**
	 * Saldo Deudor Positivo (Haber - Debe) de la cuenta indicada en <i>account</i>.
	 * @param accounts
	 * @return El dato si es positivo, en otro caso cero.
	 * @throws AccMiningException
	 */
	public double sdPositivo(int account) throws AccMiningException {
		double d = sd(account); 
		return d>0?d:0;
	}

	//------------------------------------------------------------------------------------------
	
	private double getCreditBalance(int ... accounts  ) throws AccMiningException {
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
	
	private double getDebitBalance(int ... accounts ) throws AccMiningException {
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
	
}
