package com.code.aon.finance;

import java.util.Iterator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.account.Account;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name = "bank_concept")
public class BankConcept implements ITransferObject {

	private static final long serialVersionUID = 2507939943449179318L;

	private Integer id;
    private String name;
    private Account account;

    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    
    @Column(length=32, nullable = false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Transient
	public Account getAccount() {
		if (account == null) {
			String select = "select account from BankConceptAccount as bankConceptAccount " +
							"where bankConceptAccount.bankConcept.id = " + getId();
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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final BankConcept o = (BankConcept) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.name,o.name)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.name)		
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}