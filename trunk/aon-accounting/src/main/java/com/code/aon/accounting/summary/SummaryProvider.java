package com.code.aon.accounting.summary;

import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;

public class SummaryProvider {

	private static final String PERCENT = "%";

	public SummaryCollection getSummaryCollection(SummaryProviderParameters params) throws ManagerBeanException {
		PreparedStatement sum = null;
		ResultSet sumSet = null;
		try {
			StringWriter sumStmt = new StringWriter();
			sumStmt.append("SELECT SUM(s.debit),SUM(s.credit)");
			if (params.isMonthlyGrouping()) {
				sumStmt.append(",MONTH(s.entry_date)");
			}
			if (!params.isBudgeted()) {
				sumStmt.append(" FROM account_summary s ");
			} else {
				sumStmt.append(" FROM account_budget_detail s ");
			}

			sumStmt.append(" WHERE s.account LIKE ?");
			if (params.getFromDate() != null) {
				sumStmt.append(" AND s.entry_date >= ?");
			}
			if (params.getToDate() != null) {
				sumStmt.append(" AND s.entry_date <= ?");
			}
			if (params.getPeriod() != null && params.getPeriod().getId() != null) {
				sumStmt.append(" AND s.account_period = ?");
			}
			if (params.getSecurityLevel() != null) {
				sumStmt.append(" AND s.security_level = ?");
			}
			if (params.isMonthlyGrouping()) {
				sumStmt.append("GROUP BY MONTH(s.entry_date)");
			}
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);

			Criteria criteria = new Criteria();
			if (!StringUtils.isEmpty(params.getAccountExpression())) {
				criteria.addExpression(ExpressionUtilities
						.getExpression(params.getAccountExpression(), accountBean
								.getFieldName(IAccountAlias.ACCOUNT_ID)));
			}
			if (!StringUtils.isEmpty(params.getAccountDescription())) {
				criteria.addExpression(ExpressionUtilities
						.getExpression(params.getAccountDescription(), accountBean
								.getFieldName(IAccountAlias.ACCOUNT_DESCRIPTION)));
			}
			if (!StringUtils.isEmpty(params.getAccountAlias())) {
				criteria.addExpression(ExpressionUtilities
						.getExpression(params.getAccountAlias(), accountBean
								.getFieldName(IAccountAlias.ACCOUNT_ALIAS)));
			}
			if (!params.isLowerLevelVisible()) {
				Expression e1 = ExpressionUtilities.getEqualExpression(accountBean
						.getFieldName(IAccountAlias.ACCOUNT_LEVEL), params.getAccountLevel());
				Expression e21 = ExpressionUtilities.getLessThanExpression(accountBean
						.getFieldName(IAccountAlias.ACCOUNT_LEVEL), params.getAccountLevel());
				Expression e22 = ExpressionUtilities.getEqualExpression(accountBean
						.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), true);
				Expression e2 = ExpressionUtilities.getAndExpression(e21, e22);
				Expression e = ExpressionUtilities.getOrExpression(e1, e2);
				criteria.addExpression(e);
			} else {
				Expression e = ExpressionUtilities.getLessThanOrEqualExpression(accountBean
						.getFieldName(IAccountAlias.ACCOUNT_LEVEL), params.getAccountLevel());
				criteria.addExpression(e);
			}

			List<ITransferObject> accountList = accountBean.getList(criteria);
			sum = HibernateUtil.getSQLConnection().prepareStatement(sumStmt.toString());
			int p = 2;
			if (params.getFromDate() != null) {
				sum.setDate(p, new java.sql.Date(params.getFromDate().getTime()));
				p++;
			}
			if (params.getToDate() != null) {
				sum.setDate(p, new java.sql.Date(params.getToDate().getTime()));
				p++;
			}
			if (params.getPeriod() != null && params.getPeriod().getId() != null) {
				sum.setString(p, params.getPeriod().getId());
				p++;
			}
			if (params.getSecurityLevel() != null) {
				sum.setInt(p, params.getSecurityLevel().ordinal());
			}
			boolean add;
			SummaryCollection sc = new SummaryCollection();
			for (ITransferObject to : accountList) {
				Account account = (Account) to;
				add = true;
				sum.setString(1, (account.getId() + PERCENT));
				sumSet = sum.executeQuery();
				double debit = 0.0;
				double credit = 0.0;
				Summary s = null;
				if (!params.isMonthlyGrouping()) {
					if (sumSet.next()) {
						debit = round(sumSet.getDouble(1));
						credit = round(sumSet.getDouble(2));
					}
					s = new Summary();
				} else {
					List<Double> months = new ArrayList<Double>(12);
					for (int i = 0; i < 12; i++) {
						months.add(new Double(0));
					}
					while (sumSet.next()) {
						debit = round(sumSet.getDouble(1));
						credit = round(sumSet.getDouble(2));
						if (account.getId().startsWith("7")) {
							months.set((sumSet.getInt(3) - 1), round(credit - debit));
						} else {
							months.set((sumSet.getInt(3) - 1), round(debit - credit));
						}

					}
					SummaryMonthly sm = new SummaryMonthly();
					sm.setMonths(months);
					s = sm;
				}
				if (!params.isZeroSumVisible() && (round(debit) == round(credit))) {
					add = false;
				}
				if (!params.isLowerLevelVisible() && !account.isEntryEnabled()
						&& account.getLevel() < params.getAccountLevel()) {
					add = false;
				}
				if (add) {
					s.setId(account.getId());
					s.setDescription(account.getDescription());
					s.setLastLevel(params.getAccountLevel() == account.getLevel()
							|| account.isEntryEnabled());
					s.setDebit(debit);
					s.setCredit(credit);
					sc.add(s);
				}
				sumSet.close();
			}
			return sc;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			try {
				if (sumSet != null) {
					sumSet.close();
				}
			} catch (Exception e) {
				// nada
			}
			try {
				if (sum != null) {
					sum.close();
				}
			} catch (Exception e) {
				// nada
			}
		}
	}

	public SummaryCollection getTotalExpensesSummaryCollection(SummaryProviderParameters params) throws ManagerBeanException {
		String accountExpression = ">=610&<7";
		params.setAccountExpression(accountExpression);
		return getSummaryCollection(params);
	}

	public SummaryCollection getGrossMarginSummaryCollection(SummaryProviderParameters params) throws ManagerBeanException {
		String accountExpression = "60*|7*";
		params.setAccountExpression(accountExpression);
		return getSummaryCollection(params);
	}

	private double round(double value) {
		double decimal = Math.pow(10, 2);
		return Math.round(decimal * value) / decimal;
	}

}
