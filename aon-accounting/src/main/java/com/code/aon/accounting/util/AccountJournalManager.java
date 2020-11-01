package com.code.aon.accounting.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.Period;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.SecurityLevel;

import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;

public class AccountJournalManager {

	private IManagerBean bean;

	public IManagerBean getBean() throws ManagerBeanException {
		if (bean == null) {
			bean = BeanManager.getManagerBean(AccountEntry.class);
		}
		return bean;
	}

	private void execute( Connection conn, String sql ) throws SQLException {
		Statement stmt = null; 
		try {
			stmt = conn.createStatement(); 
			stmt.execute(sql);
		}
		finally {
			if ( stmt != null  )
				stmt.close();
		}
	}

	public void regenerateJournalCounter(String domainName, Period period,SecurityLevel securityLevel) throws ManagerBeanException {
		String select = "SELECT id,entry_type,IFNULL(ELT(entry_type+1,0,3,2),1) okOrder"
				+" FROM account_entry" 
				+" WHERE " + DomainManager.getSQLWhereClause("domain")
				+" AND account_period = ?";
		if (securityLevel != null) {
			select += " AND security_level = ?";
		}
		select += " ORDER by okOrder,entry_date,id";
		Connection conn = null;
		PreparedStatement ps = null; 
		ResultSet rs = null;
		PreparedStatement update = null;
		try {
			conn = DatabaseUtil.getConnection(domainName);
			ps = conn.prepareStatement(select,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			update = conn.prepareStatement("UPDATE account_entry set journal = ? WHERE id = ?");
			ps.setInt(1, period.getId());
			if (securityLevel != null) {
				ps.setInt(2, securityLevel.ordinal());
			}
			execute(conn,"BEGIN");
			rs = ps.executeQuery();
			int count = 1;
			while (rs.next()) {
				update.setInt(1, count);
				update.setInt(2, rs.getInt(1));
				update.execute();
				++count;
			}
			execute(conn,"COMMIT");
		} catch (SQLException e) {
			try {
				execute(conn,"ROLLBACK");
			} catch (SQLException e1) {
			}
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (AonConnectionException e) {
			try {
				execute(conn,"ROLLBACK");
			} catch (SQLException e1) {
			}
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}
}
