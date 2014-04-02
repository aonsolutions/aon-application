package com.code.aon.ui.accounting.check.modules.balance;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.accounting.Balance;
import com.code.aon.accounting.BalanceDetail;
import com.code.aon.accounting.enumeration.BalanceType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.check.AonCheckException;
import com.code.aon.ui.accounting.check.CheckCategory;
import com.code.aon.ui.accounting.check.CheckParams;
import com.code.aon.ui.accounting.check.ICheckEntry;
import com.code.aon.ui.accounting.check.ICheckModule;
import com.code.aon.ui.accounting.controller.AccountingCollectionsController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class BalanceCheck implements ICheckModule{

	private String label = "Chequeo de cuentas ausentes o duplicadas en los balances oficiales.";
	private boolean enabled;
	private List <ICheckEntry> list;
	
	/**
	 * Comprueba que todos los apuntes tengan lineas
	 */
	@Override
	public void onExecute(CheckParams params) throws AonCheckException{
		list = new LinkedList<ICheckEntry>();
		boolean prev = HibernateUtil.mustCloseSession();
		try {
			HibernateUtil.setCloseSession(false);
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			String codeAlias = accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE);
			String entryAlias = accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED);
			AccountingCollectionsController acc = (AccountingCollectionsController) AonUtil.getRegisteredBean(IAccountingConstants.ACCOUNTING_COLLECTIONS_CONTROLLER_NAME);
			for (BalanceType bt : BalanceType.values()) {
				List<SelectItem> balances = acc.getBalances(bt);
				for (SelectItem si: balances) {
					Balance balance = (Balance) si.getValue();
					if (balance.getType() != BalanceType.CUSTOM) {
						if (balance.getLines() != null && balance.getLines().size() > 0) {
							StringBuilder buf = new StringBuilder(",");
							StringBuilder dualAccounts = new StringBuilder(",");
							for (BalanceDetail detail: balance.getLines() ) {
								if (!detail.isInternalCalculation() && !StringUtils.isEmpty( detail.getAccounts())) {
									String accounts = detail.getAccounts();
									if (StringUtils.isNotEmpty(accounts)) {
										String[] accs = StringUtils.split(accounts, ',');
										for (String a :accs) {
											a = a.replace("(", "");
											a = a.replace(")", "");
											if (StringUtils.contains(a, "?")) {
												a = a.replace("?","");
												dualAccounts.append(a);
												dualAccounts.append(",");
											}
											buf.append( a );
											buf.append( "," );
										}
									}
									
								}
							}
							Criteria criteria = new Criteria();
							criteria.addEqualExpression(entryAlias, true);
							if (balance.getType() == BalanceType.CLOSING) {
								criteria.addExpression(codeAlias, "1*|2*|3*|4*|5*");
							} else if (balance.getType() == BalanceType.OPERATING) {
								criteria.addExpression(codeAlias, "6*|7*");
							} else if (balance.getType() == BalanceType.PATRIMONY) {
								criteria.addExpression(codeAlias, "8*|9*");
							}
							List<ITransferObject> accounts = accountBean.getList(criteria);
							for (ITransferObject accountTo: accounts) {
								Account account = (Account) accountTo;
								String id1 = "," + account.getCode().substring(0,1) + ",";
								String id2 = "," + account.getCode().substring(0,2) + ",";
								String id3 = "," + account.getCode().substring(0,3) + ",";
								String id4 = "," + account.getCode().substring(0,4) + ",";
								int count1 = StringUtils.countMatches(buf.toString(), id1);
								int count2 = StringUtils.countMatches(buf.toString(), id2);
								int count3 = StringUtils.countMatches(buf.toString(), id3);
								int count4 = StringUtils.countMatches(buf.toString(), id4);
								int sum = count1 + count2 + count3 + count4;
								String msg = null;
								if ( sum > 1) {
									int dualCount1 = StringUtils.countMatches(dualAccounts.toString(), id1);
									int dualCount2 = StringUtils.countMatches(dualAccounts.toString(), id2);
									int dualCount3 = StringUtils.countMatches(dualAccounts.toString(), id3);
									int dualCount4 = StringUtils.countMatches(dualAccounts.toString(), id4);
									int dualSum = dualCount1 + dualCount2 + dualCount3 + dualCount4;
									if ( dualSum == 0) {
										msg = "[" + account.getCode() + "] Cuenta definida dos veces en el balance '" + balance.getName() + "'.";
									}
								} else if ( sum == 0) {
									msg ="[" + account.getCode() + "] Cuenta no reflejada en el balance '" + balance.getName() + "'";
								}
								if (msg != null) {
									BalanceCheckEntry e = new BalanceCheckEntry();
									if (DomainManager.getCurrentDomain() != balance.getDomain()) {
										msg += " Balance definido en entorno superior"; 
									}
									e.setMessage( msg );
									e.setTo(account);
									list.add(e);
								}
							}
						}
					}
				}
			
			}
		} catch (ManagerBeanException e) {
			throw new AonCheckException(e.getMessage(),e);
		} catch (ExpressionException e) {
			throw new AonCheckException(e.getMessage(),e);
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
	
	@Override
	public CheckCategory getCategory() {
		return CheckCategory.ACCOUNTING;
	}

}
