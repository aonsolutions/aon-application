package com.code.aon.accounting.util;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.AonException;
import com.code.aon.AonVersion;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.pool.AonConnectionException;

public class AccountingFinanceChecker implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final String UNION =  " UNION ";
	
	private static final String COMMON_SELECT_1 = 
		"SELECT a.id,a.code"
		+	" ,a.description "
		+	" ,c.registry "
		+   " ,ROUND( SUM(aed.debit),2) DEBIT"
		+   " ,ROUND( SUM(aed.credit),2) CREDIT";

	private String CREDITOR_SELECT = COMMON_SELECT_1  
			 +",IFNULL("
			 +" (SELECT ROUND(SUM(f.amount),2)"
		     +" 	FROM finance f"
		     +"  INNER JOIN invoice i ON f.invoice = i.id and i.issue_date <= ?"
			 +"  WHERE f.registry = c.registry "
			 +"  AND  ( f.status IN (0,1) "
			 +"    OR ( f.status > 1 AND ("
			 +" 	SELECT ft.type FROM finance_tracking ft "
			 +" 		WHERE ft.finance = f.id "
			 +"			AND ft.id > 0 AND ft.tracking_date <= ? "
			 +" 		ORDER BY ft.id DESC LIMIT 1) NOT IN (1,4) ))),0) FINANCE_AMOUNT"
			 +" FROM account_entry_detail aed "
			 +" INNER JOIN account_entry ae ON aed.account_entry = ae.id"
			 +" INNER JOIN account a ON aed.account = a.id"
			 +" INNER JOIN creditor c ON a.id = c.account"
			 +" WHERE aed.domain = ?"
			 +" AND ae.entry_date <= ?"
			 +" AND a.code like ?"
			 +" GROUP BY a.id, a.code , a.description, c.registry"
			 +" HAVING ABS(ROUND(DEBIT - CREDIT,2)) <> FINANCE_AMOUNT";
	
	private String SUPPLIER_SELECT = COMMON_SELECT_1  
			 +",IFNULL("
			 +" (SELECT ROUND(SUM(f.amount),2)"
		     +" 	FROM finance f"
		     +"  INNER JOIN invoice i ON f.invoice = i.id and i.issue_date <= ?"
			 +"  WHERE f.registry = c.registry "
			 +"  AND  ( f.status IN (0,1) "
			 +"    OR ( f.status > 1 AND ("
			 +" 	SELECT ft.type FROM finance_tracking ft "
			 +" 		WHERE ft.finance = f.id "
			 +"			AND ft.id > 0 AND ft.tracking_date <= ? "
			 +" 		ORDER BY ft.id DESC LIMIT 1) NOT IN (1,4) ))),0) FINANCE_AMOUNT"
			 +" FROM account_entry_detail aed "
			 +" INNER JOIN account_entry ae ON aed.account_entry = ae.id"
			 +" INNER JOIN account a ON aed.account = a.id"
			 +" INNER JOIN supplier c ON a.id = c.account"
			 +" WHERE aed.domain = ?"
			 +" AND ae.entry_date <= ?"
			 +" AND a.code like ?"
			 +" GROUP BY a.id, a.code , a.description, c.registry"
			 +" HAVING ABS(ROUND(DEBIT - CREDIT,2)) <> FINANCE_AMOUNT";

	private String CUSTOMER_SELECT = COMMON_SELECT_1   
			 +",IFNULL("
			 +" (SELECT ROUND(SUM(f.amount),2)"
		     +" 	FROM finance f"
		     +"  INNER JOIN invoice i ON f.invoice = i.id and i.issue_date <= ?"
			 +"  WHERE f.registry = c.registry "
			 +"  AND  ( f.status IN (0,1) "
			 +"    OR ( f.status > 1 AND ("
			 +" 	SELECT ft.type FROM finance_tracking ft "
			 +" 		WHERE ft.finance = f.id "
			 +"			AND ft.id > 0 AND ft.tracking_date <= ? "
			 +" 		ORDER BY ft.id DESC LIMIT 1) NOT IN (1,4) ))),0) FINANCE_AMOUNT"
			 +" FROM account_entry_detail aed "
			 +" INNER JOIN account_entry ae ON aed.account_entry = ae.id"
			 +" INNER JOIN account a ON aed.account = a.id"
			 +" INNER JOIN customer c ON a.id = c.account"
			 +" WHERE aed.domain = ?"
			 +" AND ae.entry_date <= ?"
			 +" AND a.code like ?"
			 +" GROUP BY a.id, a.code , a.description, c.registry"
			 +" HAVING ABS(ROUND(CREDIT - DEBIT,2)) <> FINANCE_AMOUNT";

	public List<AccountingFinanceCheck> getChecks(Connection conn,AccountingFinanceCheckerParams params) throws AonException {
		List<AccountingFinanceCheck> list = new LinkedList<AccountingFinanceCheck>();
		String SELECT = "";
		if (params.isCreditorsEnabled()) {
			SELECT = CREDITOR_SELECT;
		}
		if (params.isSuppliersEnabled()) {
			SELECT = SELECT + (SELECT.length()==0?"":UNION) + SUPPLIER_SELECT;
		}
		if (params.isCustomersEnabled()) {
			SELECT = SELECT + (SELECT.length()==0?"":UNION) + CUSTOMER_SELECT;
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(SELECT);
			int i = 1;
			if (params.isCreditorsEnabled()) {
				ps.setDate(i++, new java.sql.Date(params.getDeadline().getTime()));
				ps.setDate(i++, new java.sql.Date(params.getDeadline().getTime()));
				ps.setInt(i++, params.getDomain());
				ps.setDate(i++, new java.sql.Date(params.getDeadline().getTime()));
				ps.setString(i++, "410%");
			}
			if (params.isSuppliersEnabled()) {
				ps.setDate(i++, new java.sql.Date(params.getDeadline().getTime()));
				ps.setDate(i++, new java.sql.Date(params.getDeadline().getTime()));
				ps.setInt(i++, params.getDomain());
				ps.setDate(i++, new java.sql.Date(params.getDeadline().getTime()));
				ps.setString(i++, "400%");
			}
			if (params.isCustomersEnabled()) {
				ps.setDate(i++, new java.sql.Date(params.getDeadline().getTime()));
				ps.setDate(i++, new java.sql.Date(params.getDeadline().getTime()));
				ps.setInt(i++, params.getDomain());
				ps.setDate(i++, new java.sql.Date(params.getDeadline().getTime()));
				ps.setString(i++, "430%");
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				AccountingFinanceCheck check = new AccountingFinanceCheck();
				check.setAccountId( rs.getInt(1) );
				check.setAccountCode( rs.getString(2) );
				check.setAccountDescription( rs.getString(3) );
				check.setRegistryId( rs.getInt(4) );
				check.setDebit(rs.getDouble(5));
				check.setCredit(rs.getDouble(6));
				check.setFinBalance(rs.getDouble(7));
				list.add(check);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			throw new AonException(e.getMessage(),e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
		}
		return list;
	}
	
	public List<StrippedStatement> getStrippedStatement(Connection conn, AccountingFinanceCheckerParams params ) throws AonException {
		String DEFAULT_DOCUMENT = "APUNTES SIN N\u00DAMERO DE DOCUMENTO";
		String SELECT_STRIPPED_STATEMENT =
				"SELECT IF(TRIM(aed.document_number) = '','"+DEFAULT_DOCUMENT+"',IFNULL(aed.document_number,'"+DEFAULT_DOCUMENT+"')) DOCUMENT"
				+ ",ROUND(SUM(aed.debit),2) DEBIT"
				+ ",ROUND(SUM(aed.credit),2) CREDIT"
				+ ",IF(ROUND(SUM(aed.debit),2) - ROUND(SUM(aed.credit),2) = 0,1,0) DIFF"
				+ ",(SELECT ROUND(SUM(f.amount),2)"
					     +" 	FROM finance f"
					     +"  INNER JOIN invoice i ON f.invoice = i.id and i.issue_date <= ?"
						 +"  WHERE f.domain = ?"
					     + " AND aed.document_number IS NOT NULL " 
					     + " AND TRIM(aed.document_number) != ''"
						 + " AND TRIM(f.concept) = TRIM(aed.document_number)"
						 +"  AND  ( f.status IN (0,1) "
						 +"    OR ( f.status > 1 AND "
						 +" 	(SELECT ft.type FROM finance_tracking ft "
						 +" 		WHERE ft.finance = f.id "
						 +"			AND ft.id > 0 AND ft.tracking_date <= ? "
						 +" 		ORDER BY ft.id DESC LIMIT 1) NOT IN (1,2,4)))) FINANCE_AMOUNT "		
				+" FROM account a"
				+" INNER JOIN account_entry_detail aed ON aed.account = a.id"
				+" INNER JOIN account_entry ae ON aed.account_entry = ae.id"
				+" WHERE a.code = ?"
				+"  AND aed.domain = ?"
				+"  AND ae.entry_date <= ?"
				+" GROUP BY DOCUMENT"
				+" ORDER BY DOCUMENT DESC";
		List<StrippedStatement> list = new LinkedList<StrippedStatement>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(SELECT_STRIPPED_STATEMENT);
			ps.setDate(1, new java.sql.Date(params.getDeadline().getTime()));
			ps.setInt(2, params.getDomain());
			ps.setDate(3, new java.sql.Date(params.getDeadline().getTime()));
			ps.setString(4, params.getAccountCode());
			ps.setInt(5, params.getDomain());
			ps.setDate(6, new java.sql.Date(params.getDeadline().getTime()));
			rs = ps.executeQuery();
			while (rs.next()) {
				StrippedStatement ss = new StrippedStatement();
				ss.setDocumentNumber(rs.getString(1));
				ss.setDebit(rs.getDouble(2));
				ss.setCredit(rs.getDouble(3));
				ss.setFinanceAmount(rs.getDouble(5));
				list.add(ss);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			throw new AonException(e.getMessage(),e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
			
		}
		return list;
	}

	public static void main(String[] args) throws AonConnectionException, AonException {
		Connection c = DatabaseUtil.getConnection("sig.esferalia.com");
		AccountingFinanceCheckerParams params = new AccountingFinanceCheckerParams();
		params.setCreditorsEnabled(true);
		params.setCustomersEnabled(true);
		params.setSuppliersEnabled(true);
		params.setDeadline(new Date());
		params.setDomain(1);
		AccountingFinanceChecker checker = new AccountingFinanceChecker();
		checker.getChecks(c, params);
	}
}
