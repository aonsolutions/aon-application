
package com.code.aon.accounting.mvel;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.mvel2.MVEL;

import com.code.aon.accounting.summary.SummaryCollection;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;

public class BalanceMVELContext implements Map<String, Object> {
	
	private Map<String, Object> context;

	private SummaryProvider summaryProvider;
	private SummaryProviderParameters params;

	public BalanceMVELContext(SummaryProviderParameters params) {
		context = new HashMap<String, Object>();
		this.params = params;
		this.summaryProvider = new SummaryProvider();
	}
	public SummaryProviderParameters getParams() {
		return params;
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
		return this.context.containsKey( (String) key );
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
	public Object get(Object key) {
		Object obj = null;
		if (this.context.containsKey(key) ) {
			obj = this.context.get(key);
			if (obj instanceof BalanceItem) {
				BalanceItem ae = (BalanceItem) obj;
				obj = (ae.isResolved())?ae.getValue():( (Double) evaluate(ae));
			}
		} 
		return obj;
	}

	public Object getObject(Object key) {
		Object obj = this.context.get(key);
		if (obj instanceof BalanceItem) {
			BalanceItem ae = (BalanceItem) obj;
			if (!ae.isResolved()) {
				evaluate(ae);	
			}
		}
		return obj;
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
	public Object put(String key, Object ae) {
		return this.context.put(key, ae);
	}
	
	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public BalanceItem remove(Object key) {
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
	
	private Object evaluate(BalanceItem ae) {
		if ( this.context.containsKey(ae.getCode()) ) {
			if ( ae.isResolved() ) {
				return this.context.get(ae.getCode());
			}
		}
		String exp = ae.getExpression().getExpression();
		double d = 0.0;
		if (StringUtils.isNotEmpty(exp)) {
			Object ret = MVEL.eval( exp , this , this);
			Number n = (Number) ret;
			d = n.doubleValue();
		}
		ae.setValue( d );
		ae.setResolved(true);
		return d;
	}
	
	// Métodos disponibles en las expresiones MVEL.
	
	/**
	 * Saldo Acreedor (Haber - Debe) del sumatorio de las cuentas indicadas en <i>accounts</i>.
	 * @param accounts
	 * @return
	 * @throws BalanceException
	 */
	public double sa(int ... accounts  ) throws BalanceException {
		return getCreditBalance(accounts);
	}
	/**
	 * Saldo Acreedor (Haber - Debe) de la cuenta indicada en <i>account</i>.
	 * @param accounts
	 * @return
	 * @throws BalanceException
	 */
	public double sa(int account) throws BalanceException {
		return getCreditBalance(new int[]{account});
	}
	
	/**
	 * Saldo Deudor (Debe - Haber) del sumatorio de las cuentas indicadas en <i>accounts</i>.
	 * @param accounts
	 * @return
	 * @throws BalanceException
	 */
	public double sd(int ... accounts  ) throws BalanceException {
		return getDebitBalance(accounts);
	}
	/**
	 * Saldo Deudor (Debe - Haber) de la cuenta indicada en <i>account</i>.
	 * @param accounts
	 * @return
	 * @throws BalanceException
	 */
	public double sd(int account) throws BalanceException {
		return getDebitBalance(new int[]{account});
	}
	
	//------------------------------------------------------------------------------------------
	
	private double getCreditBalance(int ... accounts  ) throws BalanceException {
		SummaryCollection summaryCollection = getSummaryCollection(params,accounts);
		return CommonUtil.round(summaryCollection.getCredit() - summaryCollection.getDebit());
	}
	
	private double getDebitBalance(int ... accounts ) throws BalanceException {
		SummaryCollection summaryCollection = getSummaryCollection(params,accounts);
		return CommonUtil.round(summaryCollection.getDebit() - summaryCollection.getCredit());
	}

	private SummaryCollection getSummaryCollection(SummaryProviderParameters params, int ... accounts) throws BalanceException {
		try {
			String expr = getQLExpression( accounts );
			params.setAccountExpression(expr);
			return summaryProvider.getSummaryCollection(params,false);
		} catch (ManagerBeanException e) {
			throw new BalanceException(e.getMessage(),e); 
		}
	}
	private String getQLExpression(int[] accounts) {
		StringBuffer buf = new StringBuffer(); 
		for (int a : accounts ) {
			if (buf.length() > 0) {
				buf.append('|');	
			}
			buf.append(a);
			buf.append('*');
		}
		return buf.toString();
	}
}
