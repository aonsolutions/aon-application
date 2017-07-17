package com.code.aon.ui.company.controller;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ui.util.AonUtil;

public class CompanyDisplay implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyDisplay.class.getName());
	
	private String companyLabel;
	
	private boolean init;
	
	private boolean withLogo;

	public String getCompanyLabel() {
		return companyLabel;
	}

	public void setCompanyLabel(String companyLabel) {
		this.companyLabel = companyLabel;
	}
	
	public boolean isWithLogo() {
		return withLogo;
	}

	public boolean isShow() throws AonConnectionException {
		if (! this.init ) {
			init(AonUtil.getServerName());
		}
		return !StringUtils.isEmpty(this.companyLabel) || withLogo;
	}
	
	private Object[] getCompany( Connection connection, Integer domainId ) throws SQLException {
		QueryRunner run = new QueryRunner();
		ResultSetHandler<Object[]> h = new ArrayHandler();
		return run.query( connection, 
			    "SELECT r.id, r.name FROM company as c, registry as r WHERE c.domain=? AND c.registry = r.id LIMIT 1",
			    h, domainId); 
	}			
	
	public boolean calculateWithLogo( Connection connection, Integer domainId, Integer companyId ) {
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<Integer> h = new ScalarHandler<Integer>();
			Integer result = run.query( connection, 
				    "SELECT id FROM rattach WHERE domain = ? and registry =? and type=0 and data is not null LIMIT 1",
				    h, domainId, companyId);
			return result != null;
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return false;			
	}	
	
	public void init( String host ) throws AonConnectionException {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(host);
			if ( connection != null ) {
				Integer domainId = DatabaseUtil.getDomain(connection, host);
				if (domainId != null) {
					Object[] values = getCompany(connection, domainId);
					if (values != null) {
						Integer companyId = (Integer) values[0];
						if ( companyId != null ) {
							this.companyLabel = (String) values[1];
							this.withLogo = calculateWithLogo(connection, domainId, companyId);
						}
					}
				}			
			}
		} catch ( SQLException e ) {
			LOGGER.error( "Error getting company name and logo", e );
			throw new AonConnectionException(e.getMessage(),e);
		} catch ( Throwable th ) {
			LOGGER.error( "Error getting company name and logo", th );
			throw new AonConnectionException(th.getMessage(),th);
		} finally {
			DatabaseUtil.closeQuietly(connection);
			this.init = true;
		}
	}
	
}
