package com.esferalia.aon.occam.server.accounting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDatabaseUtil;

public class SQLAccounting {
/*
	private static final String SELECT = "SELECT ae.entry_type,SUBSTRING(a.code,1,4) acc, SUM(aed.debit), SUM(aed.credit)" 
			+" FROM account_entry ae" 
			+" INNER JOIN account_entry_detail aed on aed.account_entry = ae.id" 
			+" INNER JOIN account a on aed.account = a.id" 
			+" where ae.domain = ? "
			+" AND ae.entry_date BETWEEN ? AND ?"
			+" AND ae.entry_type != 1"
			+" GROUP BY ae.entry_type,acc";

	public static synchronized Map<String, AccountBalance> getAccountBalances(Connection conn, AccMiningParameters params) 
				throws AonCoreException {
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
			throw new AonCoreException(e.getMessage(),e); 
		} finally {
			AonDatabaseUtil.closeQuietly(rs);
			AonDatabaseUtil.closeQuietly(ps);
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
*/	
}
