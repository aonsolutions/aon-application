package com.code.aon.finance;

import java.util.Iterator;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.account.Account;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.esferalia.aon.entity.master.BankConceptDB;

@Entity
@Table(name="bank_concept")
public class BankConcept extends BankConceptDB {

	private static final long serialVersionUID = 1L;

    private Account account;

	@Transient
	public Account getAccount() {
		if (account == null) {
			String select = "select account from BankConceptAccount as bankConceptAccount " +
							" where bankConceptAccount.bankConcept.id = " + getId() +
							" and " + DomainManager.getSQLWhereClause("bankConceptAccount.domain");
			Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
	    	Query query = session.createQuery(select);
			Iterator<?> iterator = query.list().iterator();
			if (iterator.hasNext()) {
				setAccount((Account)iterator.next());
			}
		}
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}

}