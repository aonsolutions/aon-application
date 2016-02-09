package com.code.aon.config.util;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.IBankAccountContainer;

public class BankUtil {

	public static void fillBankAccountData(IBankAccountContainer bac) {
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		Session session = HibernateUtil.getSession(sessionFactoryName);

		if (bac.getBankAccount().getCountry() != null && bac.getBankAccount().getBankCode() != null) {
			String hqlQuery = "SELECT alias, bic, CASE WHEN bank_account = '" + bac.getBankAccount().getIban() + "' THEN 0 ELSE 1 END" +
					" FROM RegistryBank" +
					" WHERE " + DomainManager.getSQLWhereClause("domain") + 
					" AND registry NOT IN (SELECT id FROM Company)" +
					" AND (bank_account = '" + bac.getBankAccount().getIban() + "'" +
					" OR bank_account LIKE '" + bac.getBankAccount().getCountry().getValue() + "__" + bac.getBankAccount().getBankCode() + "%')" +
					" AND alias IS NOT NULL AND alias != ''" +
					" AND bic IS NOT NULL AND bic != ''" +
					" ORDER BY 3, id DESC";

			Query query = session.createQuery(hqlQuery);
			Iterator<?> iterator = query.list().iterator();
			if (iterator.hasNext()) {
				Object[] results = (Object[])iterator.next();
				if (results != null) {
					bac.setBankAlias((String)results[0]);
					bac.setBic((String)results[1]);
				}
			}

			if (StringUtils.isBlank(bac.getBic())) {
				BankBic11 bankBic = BankBic11.getBankBic11(bac.getBankAccount().getBankCode());
				if (bankBic != null) {
					bac.setBankAlias(StringUtils.left(bankBic.getDescription(), 25));
					bac.setBic(bankBic.getBic());					
				}
			}
		}
	}

}
