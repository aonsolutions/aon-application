package com.code.aon.accounting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.entity.master.AmortizationTypeDB;

@Entity
@Table(name="amortization_type")
public class AmortizationType extends AmortizationTypeDB {

	private static final long serialVersionUID = 1L;
	private static final String DESCRIPTION = "description";
	private static final Logger LOGGER = LoggerFactory.getLogger(AmortizationType.class.getName());
	
	private static final String SELECT = "SELECT" 
		    +" acc.description " + DESCRIPTION 
			+" FROM account acc"
			+" INNER JOIN domain dom on acc.domain = dom.id" 
			+"  AND (dom.id = ? or dom.parent = ?)"
			+" WHERE acc.code = ?"
			+" GROUP BY acc.code"
			+" LIMIT 1";

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
		Connection c = HibernateUtil.getSQLConnection(sessionFactoryName);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = c.prepareStatement(SELECT,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setInt(1, DomainManager.getCurrentDomain());
			ps.setInt(2, DomainManager.getCurrentDomain());
			ps.setString(3, account);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString(DESCRIPTION);
			}
			return null;
		} catch (SQLException e ) {
			LOGGER.error(e.getMessage(),e);
			return null;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
}