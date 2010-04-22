package com.code.aon.accounting.balance;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.accounting.Balance;
import com.code.aon.accounting.BalanceDetail;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.summary.SummaryCollection;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;

public class BalanceManager {
	
	private static final String EMPTY = "";
	private static final String OPEN_BRACKET = "(";
	private static final String CLOSE_BRACKET = ")";
	private static final String PIPE = "|";
	private static final String ASTERISK = "*";
	private static final String QUESTION_MARK = "?";
	private static final String COMMA = ",";
	
	private List<BalanceItem> list;
	
	public List<BalanceItem> getList() {
		return list;
	}
	public void setList(List<BalanceItem> list) {
		this.list = list;
	}

	public List<BalanceItem> getBalanceCollection(SummaryProviderParameters parameters,Balance balance) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			
			dumpTable(balance);
			resolveTable(parameters,balance);
			cleanTable();

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);

			return list;

		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				throw new ManagerBeanException(daoe.getMessage(),daoe);	
			}
			throw new ManagerBeanException(e.getMessage(),e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}

				
	}

	private void dumpTable(Balance balance) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(BalanceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IAccountingAlias.BALANCE_DETAIL_BALANCE_ID),
				balance.getId());
		criteria.addOrder(bean.getFieldName(IAccountingAlias.BALANCE_DETAIL_SORT_KEY));
		List<ITransferObject> balanceDetailList = bean.getList(criteria);
		list = new LinkedList<BalanceItem>();
		for (ITransferObject to : balanceDetailList) {
			BalanceDetail bd = (BalanceDetail) to;
			BalanceItem b = new BalanceItem();
			b.setDetail(bd);
			list.add(b);
		}
		
	}

	private void resolveTable(SummaryProviderParameters parameters,Balance balance) throws ManagerBeanException {
		try {
			SummaryProviderParameters previous = parameters.clone();
			changeParameters(previous);
			for (BalanceItem item: list) {
				if (!item.isResolved()) {
					resolveItem(item,parameters,previous);
				}
			}
		} catch (CloneNotSupportedException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		}
		
	}

	private void cleanTable() {
		List<BalanceItem> removableItems = new LinkedList<BalanceItem>();
		for (BalanceItem item: list) {
			if (!item.isVisible()) {
				removableItems.add(item);
			} else if (item.getDetail().isZeroFlag() && (item.getAmount() == null || CommonUtil.round(item.getAmount()) == 0.0)) {
				removableItems.add(item);
			}
		}
		list.removeAll(removableItems);
	}

	private void resolveItem(BalanceItem item,SummaryProviderParameters parameters,SummaryProviderParameters previous) throws ManagerBeanException {
		BalanceDetail bd = item.getDetail();
		if (StringUtils.isNotBlank(bd.getAccounts())) {
			if (bd.isInternalCalculation()) {
				resolveInternalCalculation(item,parameters,previous);
			} else {
				resolveExternalCalculation(item,parameters,previous);
			}
		}
	}

	private void resolveInternalCalculation(BalanceItem item, SummaryProviderParameters parameters, SummaryProviderParameters previous) throws ManagerBeanException {
		BalanceDetail bd = item.getDetail();
		String[] tokens = StringUtils.split(bd.getAccounts(),COMMA);
		for (String token:tokens) {
			token = token.trim();
			if (StringUtils.isNotBlank(token)) {
				String t = token;
				boolean negative = false;
				boolean greatherThanZero = false;
				if (token.startsWith(OPEN_BRACKET) && token.endsWith(CLOSE_BRACKET)) {
					t = token.replace(OPEN_BRACKET, EMPTY).replace(CLOSE_BRACKET, EMPTY);
					negative = true;
				}
				BalanceItem bi = searchItem( t );
				if (!bi.isResolved()) {
					resolveItem(bi,parameters,previous);	
				}
				if (negative) {
					item.subtract(bi);	
				} else {
					item.add(bi);
				}
			}
		}
	}

	private void resolveExternalCalculation(BalanceItem item, SummaryProviderParameters parameters, SummaryProviderParameters previous) throws ManagerBeanException {
		BalanceDetail bd = item.getDetail();
		String[] tokens = StringUtils.split(bd.getAccounts(),COMMA);
		StringBuilder positiveExp = new StringBuilder();
		List<String> conditionalPositiveExp = new LinkedList<String>();
		StringBuilder negativeExp = new StringBuilder();
		List<String> conditionalNegativeExp = new LinkedList<String>();
		for (String token:tokens) {
			token = token.trim();
			if (StringUtils.isNotBlank(token)) {
				StringBuilder tmpExp = positiveExp;
				List<String> tmpConditionalExp = conditionalPositiveExp;
				if (token.startsWith(OPEN_BRACKET) && token.endsWith(CLOSE_BRACKET)) {
					token = token.replace(OPEN_BRACKET, EMPTY).replace(CLOSE_BRACKET, EMPTY);
					tmpExp = negativeExp;
					tmpConditionalExp = conditionalNegativeExp;
				}
				if (token.startsWith(QUESTION_MARK)) {
					String t = token.replace(QUESTION_MARK, EMPTY);
					tmpConditionalExp.add(t+ASTERISK);
				} else {
					tmpExp.append(tmpExp.length()>0?PIPE:EMPTY);
					tmpExp.append(token);	
					tmpExp.append(ASTERISK);
				}
			}
		}
		Double pAmount = new Double(0.0);
		Double pPreviousAmount = new Double(0.0);
		if (positiveExp.length() > 0) {
			parameters.setAccountExpression(positiveExp.toString());
			previous.setAccountExpression(positiveExp.toString());
			pAmount = getAccountsAmount(parameters,bd.isCreditNature());
			pPreviousAmount = getAccountsAmount(previous,bd.isCreditNature());
		}
		if ( conditionalPositiveExp.size() > 0 ) {
			for (String exp: conditionalPositiveExp ) {
				parameters.setAccountExpression(exp);
				previous.setAccountExpression(exp);
				Double a = getAccountsAmount(parameters,bd.isCreditNature());
				if (bd.isCreditNature() && a > 0 ) {
					pAmount = CommonUtil.round(pAmount + a);
				}
				Double p = getAccountsAmount(previous,bd.isCreditNature());
				if (bd.isCreditNature() && p > 0 ) { 
					pPreviousAmount = CommonUtil.round(pPreviousAmount + p);
				}
			}
		}
		Double nAmount = new Double(0.0);
		Double nPreviousAmount = new Double(0.0);
		if (negativeExp.length() > 0) {
			parameters.setAccountExpression(negativeExp.toString());
			previous.setAccountExpression(negativeExp.toString());
			nAmount = getAccountsAmount(parameters,!bd.isCreditNature());
			nPreviousAmount = getAccountsAmount(previous,!bd.isCreditNature());
		}
		if ( conditionalNegativeExp.size() > 0 ) {
			for (String exp: conditionalNegativeExp ) {
				parameters.setAccountExpression(exp);
				previous.setAccountExpression(exp);
				Double a = getAccountsAmount(parameters,!bd.isCreditNature());
				if (!bd.isCreditNature() && a > 0 ) {
					nAmount = CommonUtil.round(nAmount + a);
				}
				Double p = getAccountsAmount(previous,!bd.isCreditNature());
				if (!bd.isCreditNature() && p > 0 ) { 
					nPreviousAmount = CommonUtil.round(nPreviousAmount + p);
				}
			}
		}
		item.setAmount( CommonUtil.round(pAmount - nAmount));
		item.setPreviousAmount(CommonUtil.round(pPreviousAmount - nPreviousAmount));
	}
		
	private BalanceItem searchItem(String token) throws ManagerBeanException {
		for (BalanceItem item: list) {
			if (ObjectUtils.equals(item.getDetail().getCode(), token)) {
				return item;
			}
		}
		throw new ManagerBeanException("La clave interna " + token + " no está definida");
	}

	private Double getAccountsAmount(SummaryProviderParameters params, boolean creditNature) throws ManagerBeanException {
		SummaryCollection summaryCollection = new SummaryCollection();
		SummaryProvider summaryProvider = new SummaryProvider();
		summaryCollection = summaryProvider.getSummaryCollection(params,false);
		if (creditNature) {
			return CommonUtil.round(summaryCollection.getCredit() - summaryCollection.getDebit());
		}
		return CommonUtil.round(summaryCollection.getDebit() - summaryCollection.getCredit());
	}
	
	private void changeParameters(SummaryProviderParameters previous) throws ManagerBeanException {
		if (previous.getFromDate() != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(previous.getFromDate());
			c.add(Calendar.YEAR, -1);
			previous.setFromDate(c.getTime());
		}
		if (previous.getToDate() != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(previous.getToDate());
			c.add(Calendar.YEAR, -1);
			previous.setToDate(c.getTime());
		}
		if (previous.getPeriod() != null) {
			IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
			Criteria criteria = new Criteria();
			String deadlineAlias = periodBean.getFieldName(IAccountingAlias.PERIOD_INITIATION_DATE);
			criteria.addLessThanExpression(deadlineAlias, previous.getPeriod().getInitiationDate());
			criteria.addOrder(deadlineAlias, false);
			Iterator<ITransferObject> iter = periodBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				previous.setPeriod( (Period) iter.next());
			} else {
				previous.setPeriod( null );
			}
		}
	}

	// ********************************************************************************
	// ********************************************************************************
	// ********************************************************************************
	public static void main(String[] args) throws ManagerBeanException {
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
		Period period = (Period) periodBean.get("2009");

		IManagerBean bean = BeanManager.getManagerBean(Balance.class);
		Balance balance = (Balance) bean.get(new Integer(3));

		BalanceManager bm = new BalanceManager();
		SummaryProviderParameters parameters = new SummaryProviderParameters();
		parameters = new SummaryProviderParameters();
		parameters.setPeriod(period);
		parameters.setFromDate(null);
		parameters.setToDate(null);
		parameters.setDate(new Date());
		parameters.setAccountExpression(null);
		parameters.setLowerLevelVisible(false);
		parameters.setNoTouchedAccountVisible(false);
		parameters.setRowsPerPage(20);
		parameters.setAccountLevel(4);
		parameters.setBudgeted(false);
		List<BalanceItem> list = bm.getBalanceCollection(parameters, balance);
		System.out.println(  );
		System.out.println(  );
		System.out.println(  );
		System.out.println( "************** " + balance.getName() + "********************");
		for (BalanceItem item: list) {
			System.out.println(  item.toString() );
		}
	}
	
}
