package com.esferalia.aon.accounting.mining.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.accounting.mining.shared.AccMiningException;
import com.esferalia.aon.accounting.mining.shared.AccMiningParameters;
import com.esferalia.aon.accounting.mining.shared.AccountBalance;
import com.esferalia.aon.accounting.mining.shared.AccountingPeriod;
import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.gwt.common.sql.SQLUtils;

public class SQLAccounting {

	//@formatter:off
	private static final String SELECT_PERIOD = 
			"SELECT id,initiation_date,deadline,status"
			+" FROM account_period"
			+" WHERE domain = ?"
			+" AND name = ?";
	private static final String SELECT = "SELECT ae.entry_type,SUBSTRING(a.code,1,4) acc, SUM(aed.debit), SUM(aed.credit)" 
			+" FROM account_entry ae" 
			+" INNER JOIN account_entry_detail aed on aed.account_entry = ae.id" 
			+" INNER JOIN account a on aed.account = a.id" 
			+" where ae.domain = ? "
			+" AND ae.entry_date BETWEEN ? AND ?"
			+" AND ae.entry_type != 1"
			+" GROUP BY ae.entry_type,acc";
	//@formatter:on

	public static AccountingPeriod getPeriod(int domain,int year, Connection conn)
			throws AonSQLException {
		String select = SELECT_PERIOD;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, domain);
			stmt.setString(2, Integer.toString(year));
			rs = stmt.executeQuery();
			AccountingPeriod period = new AccountingPeriod();
			while (rs.next()) {
				period.setId(rs.getInt(1));
				period.setStart(rs.getDate(2));
				period.setEnd(rs.getDate(3));
				period.setClosed( rs.getInt(4) == 4);
			}
			return period;
		} catch (Throwable e) {
			throw new AonSQLException(e);
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}
	}

	public static synchronized Map<String, AccountBalance> getAccountBalances(Connection conn, AccMiningParameters params) throws AccMiningException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(SELECT);
			ps.setInt(1, params.getDomain());
			ps.setDate(2, new java.sql.Date(params.getStartDate().getTime()));
			ps.setDate(3, new java.sql.Date(params.getEndDate().getTime()));
			rs = ps.executeQuery();
			Map<String, AccountBalance> map = new HashMap<String, AccountBalance>();
			int type;
			String account = null;
			double debit;
			double credit;
			while (rs.next()) {
				type = rs.getInt(1);
				account = rs.getString(2);
				debit = rs.getDouble(3);
				credit = rs.getDouble(4);
				putAccountBalance(map,type,account.substring(0,1), debit,credit);
				putAccountBalance(map,type,account.substring(0,2), debit,credit);
				putAccountBalance(map,type,account.substring(0,3), debit,credit);
				putAccountBalance(map,type,account, debit,credit);
			}
			return map;
		} catch (SQLException e) {
			throw new AccMiningException(e.getMessage(),e); 
		} finally {
			closeQuietly(rs);
			closeQuietly(ps);
		}
	}

	private static void putAccountBalance(Map<String, AccountBalance> map,int type, String account,double debit, double credit) {
		if (map.containsKey(account)) {
			AccountBalance ac = map.get(account);
			ac.add(type,debit, credit);
		} else {
			map.put(account, new AccountBalance(type,debit,credit));	
		}
	}
    
    public static void closeQuietly(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
			}
		}
	}
    
    public static void closeQuietly(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
			}
		}
	}

	public static String getAccountName(Connection conn, int domainId, String code) throws AccMiningException {
		String SELECT_ACCOUNT_NAME = 
				"SELECT a.description "
				+" FROM account a"
				+" WHERE (a.domain = ? or a.domain=(SELECT d.parent from domain d where d.id = ?))"
				+" AND code = ?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(SELECT_ACCOUNT_NAME);
			ps.setInt(1, domainId);
			ps.setInt(2, domainId);
			ps.setString(3, code);
			
			rs = ps.executeQuery();
			String account = null;
			if (rs.next()) {
				account = rs.getString(1);
			}
			return account;
		} catch (SQLException e) {
			throw new AccMiningException(e.getMessage(),e); 
		} finally {
			closeQuietly(rs);
			closeQuietly(ps);
		}
	}
	
}
