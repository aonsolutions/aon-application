package com.code.aon.ui.accounting.check;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.accounting.Balance;
import com.code.aon.accounting.BalanceDetail;
import com.code.aon.accounting.enumeration.BalanceType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;

public class BalanceCheck implements IAccountCheck{

	private String label = "Chequeo de cuentas ausentes o duplicadas en los balances oficiales.";
	private boolean enabled;
	private List <ICheckEntry> list;
	
	/**
	 * Comprueba que todos los apuntes tengan lineas
	 */
	@Override
	public void onExecute(AccountingCheckParams params) throws AccountingCheckException{
		list = new LinkedList<ICheckEntry>();
		boolean prev = HibernateUtil.mustCloseSession();
		try {
			HibernateUtil.setCloseSession(false);
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			String idAlias = accountBean.getFieldName(IAccountAlias.ACCOUNT_ID);
			String entryAlias = accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED);
			IManagerBean balanceBean = BeanManager.getManagerBean(Balance.class);
			for (ITransferObject to: balanceBean.getList(null)) {
				Balance balance = (Balance) to;
				if (balance.getType() != BalanceType.CUSTOM) {
					if (balance.getLines() != null && balance.getLines().size() > 0) {
						StringBuilder buf = new StringBuilder(","); 
						for (BalanceDetail detail: balance.getLines() ) {
							if (!detail.isInternalCalculation() && !StringUtils.isEmpty( detail.getAccounts())) {
								if (buf.length() > 0) {
									buf.append(",");
								}
								buf.append( detail.getAccounts());
							}
						}
						if (buf.length() > 0) {
							buf.append(",");
						}
						Criteria criteria = new Criteria();
						criteria.addEqualExpression(entryAlias, true);
						if (balance.getType() == BalanceType.CLOSING) {
							criteria.addExpression(idAlias, "1*|2*|3*|4*|5*");
						} else if (balance.getType() == BalanceType.OPERATING) {
							criteria.addExpression(idAlias, "6*|7*");
						} else if (balance.getType() == BalanceType.PATRIMONY) {
							criteria.addExpression(idAlias, "8*|9*");
						}
						List<ITransferObject> accounts = accountBean.getList(criteria);
						for (ITransferObject accountTo: accounts) {
							Account account = (Account) accountTo;
							String id1 = "," + account.getId().substring(0,1) + ",";
							String id2 = "," + account.getId().substring(0,2) + ",";
							String id3 = "," + account.getId().substring(0,3) + ",";
							String id4 = "," + account.getId().substring(0,4) + ",";
							int count1 = StringUtils.countMatches(buf.toString(), id1);
							int count2 = StringUtils.countMatches(buf.toString(), id2);
							int count3 = StringUtils.countMatches(buf.toString(), id3);
							int count4 = StringUtils.countMatches(buf.toString(), id4);
							int sum = count1 + count2 + count3 + count4; 
							if ( sum > 1) {
								BalanceCheckEntry e = new BalanceCheckEntry();
								e.setMessage( "[" + account.getId() + "] cuenta definida dos veces en el balance '" + balance.getName() + "'");
								e.setTo(account);
								list.add(e);
							} else if ( sum == 0) {
								BalanceCheckEntry e = new BalanceCheckEntry();
								e.setMessage( "[" + account.getId() + "] Cuenta no reflejada en el balance '" + balance.getName() + "'" );
								e.setTo(account);
								list.add(e);
							}
						}
					}
				}
			}
		} catch (ManagerBeanException e) {
			throw new AccountingCheckException(e.getMessage(),e);
		} catch (ExpressionException e) {
			throw new AccountingCheckException(e.getMessage(),e);
		} finally {
			HibernateUtil.setCloseSession(prev);
		}
	}
	
	@Override
	public List<ICheckEntry> getCheckList() {
		return list;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String getLabel() {
		return label;
	}

}