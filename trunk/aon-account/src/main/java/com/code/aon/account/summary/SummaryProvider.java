package com.code.aon.account.summary;

import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.NumberUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;

public class SummaryProvider {

	public SummaryCollection getSummaryCollection(SummaryProviderParameters params)
			throws SQLException, ManagerBeanException, ExpressionException {

		StringWriter sumStmt = new StringWriter();
		sumStmt.append("SELECT SUM(s.debit),SUM(s.credit) FROM account_summary s ");
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
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);

		Criteria criteria = new Criteria();
		if (!StringUtils.isEmpty(params.getAccountExpression())) {
			criteria.addExpression(ExpressionUtilities.getExpression(params.getAccountExpression(),
					accountBean.getFieldName(IAccountAlias.ACCOUNT_ID)));
		}
		// if (!params.isLowerLevelVisible()) {
		// criteria.addExpression(ExpressionUtilities.getEqualExpression(
		// accountBean
		// .getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new
		// Boolean(true)));
		// }
		List<ITransferObject> accountList = accountBean.getList(criteria);
		PreparedStatement sum = HibernateUtil.getSQLConnection().prepareStatement(
				sumStmt.toString());
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
		}
		boolean add;
		SummaryCollection sc = new SummaryCollection();
		for (ITransferObject to : accountList) {
			Account account = (Account) to;
			add = true;
			sum.setString(1, (account.getId() + "%"));
			ResultSet sumSet = sum.executeQuery();
			double debit = 0.0;
			double credit = 0.0;
			if (sumSet.next()) {
				debit = round(sumSet.getDouble(1));
				credit = round(sumSet.getDouble(2));
			}
			if (!params.isZeroSumVisible() && (round(debit) == round(credit))) {
				add = false;
			}
			if (account.getLevel() > params.getAccountLevel()) {
				add = false;
			} else {
				if (!params.isLowerLevelVisible() && !account.isEntryEnabled()
						&& account.getLevel() < params.getAccountLevel()) {
					add = false;
				}
			}

			if (add) {
				Summary s = new Summary();
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
		sum.close();
		return sc;
	}

	private double round(double value) {
		double decimal = Math.pow(10, 2);
		return Math.round(decimal * value) / decimal;
	}

}
