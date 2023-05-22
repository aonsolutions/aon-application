package com.code.aon.accounting;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.entity.master.AmortizationTypeDB;

@Entity
@Table(name="amortization_type")
@Heritable
public class AmortizationType extends AmortizationTypeDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final String SELECT = "SELECT" 
		    +" account.description " 
			+" FROM account, domain"
			+" WHERE account.code = ?"
			+"  AND account.domain = domain.id" 
			+"  AND (domain.id = ? or domain.parent = ?)"
			+" GROUP BY account.code";

	@Transient
    public int getYears() {
		if (getPercentage() != null && getPercentage()!= 0) {
			return (int) CommonUtil.round( 100 / getPercentage(),0);	
		}
		return 0;
	}

	public void setYears(int years) {
		if (years != 0) {
			setPercentage(CommonUtil.round( 100.0 / years));
		} else {
			setPercentage(0.0);	
		}
	}
	
	
	@Transient
	public String getFixedAssetAccountDescription() {
		if (StringUtils.isNotBlank(getFixedAssetAccount())) {
			return getAccountDescription( getFixedAssetAccount() );
		}
		return null;
	}

	@Transient
	public String getAccumulatedAccountDescription() {
		if (StringUtils.isNotBlank(getAccumulatedAccount())) {
			return getAccountDescription( getAccumulatedAccount() );
		}
		return null;
	}

	@Transient
	public String getAllocationAccountDescription() {
		if (StringUtils.isNotBlank(getAllocationAccount())) {
			return getAccountDescription( getAllocationAccount() );
		}
		return null;
	}

	@Transient
	private String getAccountDescription(String account) {
		String sessionFactoryName =HibernateUtil.getSessionFactoryName(AmortizationType.class.getName());
		Session session = null;  
		session = HibernateUtil.getSession(sessionFactoryName);
		Query query = session.createSQLQuery(SELECT);
		query.setString(0, account);
		query.setInteger(1, DomainManager.getCurrentDomain());
		query.setInteger(2, DomainManager.getCurrentDomain());
		return (String) query.uniqueResult();
	}
	
}