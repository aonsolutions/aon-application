package com.esferalia.aon.payroll.calculator.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.ISystemPayment;
import com.esferalia.aon.payroll.calculator.LRUCacheFactory;
import com.esferalia.aon.payroll.calculator.SimpleSystemPayment;
import com.esferalia.aon.payroll.sql.AbstractSQL.AgreementExtra;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementExtraColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SystemPaymentColumns;
import com.esferalia.aon.salary.expression.ExpressionScope;


public class SQLAgreementPaymentsFactory 
	implements LRUCacheFactory<AgreementKey, Collection<ISystemPayment>> {
	
	
	public interface IExtraPayment extends ISystemPayment {
		
		int getExtraId();
		String getExtraStartDate();
		String getExtraEndDate();
		String getExtraIssueDate();
	}

	public static class SimpleExtraPayment extends SimpleSystemPayment implements IExtraPayment  {
		
		
		private int extraId;
		private String extraStartDate;
		private String extraEndDate;
		private String extraIssueDate;

		public SimpleExtraPayment(IContractPayment contractPayment, int domain) {
			super(contractPayment, domain);
		}
		
		@Override
		public int getExtraId() {
			return extraId;
		}
		
		@Override
		public String getExtraStartDate() {
			return extraStartDate;
		}

		@Override
		public String getExtraEndDate() {
			return extraEndDate;
		}
		
		@Override
		public String getExtraIssueDate() {
			return extraIssueDate;
		}
		
		public void setExtraId(int extraId) {
			this.extraId = extraId;
		}
		
		public void setExtraStartDate(String extraStartDate) {
			this.extraStartDate = extraStartDate;
		}
		
		public void setExtraEndDate(String extraEndDate) {
			this.extraEndDate = extraEndDate;
		}
		
		public void setExtraIssueDate(String extraIssueDate) {
			this.extraIssueDate = extraIssueDate;
		}
		
	}
	
	private static final String SQL = 
		"SELECT * " 
		+", " + ExpressionScope.AGREEMENT.ordinal() + " AS " + SQLContractPayment.SCOPE_ALIAS
		+" FROM agreement_payment AS " + SQLContractPayment.PAYMENT_ALIAS
		+" LEFT JOIN  payment_concept" 							// LEFT JOIN: payment_concept puede ser NULL
		+"	ON (payment_concept = payment_concept.id)"
		+" LEFT JOIN agreement_extra" 							// LEFT JOIN: agreement_extra puede ser NULL
		+"	ON ("+ SQLContractPayment.PAYMENT_ALIAS +".id = agreement_extra.agreement_payment)"
		+" WHERE "+SQLContractPayment.PAYMENT_ALIAS +".domain = ?"
		+" AND "+SQLContractPayment.PAYMENT_ALIAS + ".agreement = ?"
		+" AND "+SQLContractPayment.PAYMENT_ALIAS + ".start_date <= ?"
		+" AND ( "+SQLContractPayment.PAYMENT_ALIAS + ".end_date IS NULL"
		+" OR "+SQLContractPayment.PAYMENT_ALIAS + ".end_date >= ? )"
		;
	
	private PreparedStatement 		stmt;

	public SQLAgreementPaymentsFactory(Connection connection,Date startDate, Date endDate ) 
	throws SQLException {
		this(connection, startDate, endDate, null);
	}
	
	public SQLAgreementPaymentsFactory(Connection connection,Date startDate, Date endDate, Criteria criteria ) 
	throws SQLException {
		initAgreementStmt(connection, startDate, endDate, criteria);
	}

	public void close() 
	throws SQLException {
		if ( stmt != null ) {
			stmt.close();
			stmt = null;
		}
	}
	
	@Override
	public Collection<ISystemPayment> create(AgreementKey agreementKey) {
		if ( agreementKey == null ){
			return Collections.emptyList();
		}// LRUCache<K,V> as LinkedHasMap accepts null keys and/or values.
		
		ResultSet rs = null;
		try {
			stmt.setInt(1, agreementKey.getDomain());
			stmt.setInt(2, agreementKey.getId());
			rs = stmt.executeQuery();
			return extraPaymentsCollection(rs);
		}catch (SQLException e) {
			//TODO : ¿ Deberiamos crear una excepción espefícica como CreateException ? 
			throw new RuntimeException(e);
		}
		finally {
			if ( rs != null )
				try {
					rs.close();
				} catch (SQLException e) {
					//TODO : ¿ Debemos lanzar una excpeción o no ? 
				}
				rs = null; // Para el garbage collector 
		}
	}

	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}

	private void initAgreementStmt(Connection connection, Date startDate, Date endDate, Criteria criteria)
	throws SQLException {
		String sql = CriteriaUtilities.toSQLString(criteria, SQL);    
		this.stmt  = 
			connection.prepareStatement(sql);
		this.stmt.setDate(3, new java.sql.Date(endDate.getTime()) );
		this.stmt.setDate(4, new java.sql.Date(startDate.getTime()) );
	}
	
	
	
	private static Collection<ISystemPayment> extraPaymentsCollection(ResultSet rs) 
			throws SQLException {
			
			SQLContractPayment sqlContractPayments = 
				new SQLContractPayment(rs);
			List<ISystemPayment> systemPaymentList = 
				new ArrayList<ISystemPayment>();
			
			for (IContractPayment sqlContractPayment : sqlContractPayments) {
				
				SimpleExtraPayment extraPayment = 
					new SimpleExtraPayment(sqlContractPayment, rs.getInt(SystemPaymentColumns.DOMAIN));
				
				extraPayment.setExtraId(rs.getInt(SQLConstants.AGREEMENT_EXTRA + "." + AgreementExtraColumns.ID));
				extraPayment.setExtraStartDate(rs.getString(SQLConstants.AGREEMENT_EXTRA + "." + AgreementExtraColumns.START_DATE));
				extraPayment.setExtraEndDate(rs.getString(SQLConstants.AGREEMENT_EXTRA + "." + AgreementExtraColumns.END_DATE));
				extraPayment.setExtraIssueDate(rs.getString(SQLConstants.AGREEMENT_EXTRA + "." + AgreementExtraColumns.ISSUE_DATE));
				
				systemPaymentList.add(extraPayment);
			}
			
			return systemPaymentList;
		}
	
	
	
}
