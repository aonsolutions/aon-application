package com.esferalia.aon.payroll.calculator.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.LRUCacheFactory;
import com.esferalia.aon.salary.expression.ExpressionScope;


public class SQLAgreementPaymentsFactory 
	implements LRUCacheFactory<Integer, Collection<IContractPayment>> {

	private static final String SQL = 
		"SELECT * " 
		+", " + ExpressionScope.AGREEMENT.ordinal() + " AS " + SQLContractPayment.SCOPE_ALIAS
		+" FROM agreement_payment AS " + SQLContractPayment.PAYMENT_ALIAS
		+" LEFT JOIN  payment_concept" 							// LEFT JOIN: payment_concept puede ser NULL
		+"	ON payment_concept = payment_concept.id"
		+" WHERE agreement = ?"
		+" AND start_date <= ?"
		+" AND ( end_date IS NULL"
		+" OR end_date >= ? )";
	
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
	public Collection<IContractPayment> create(Integer agreementId) {
		if ( agreementId == null ){
			return Collections.emptyList();
		}// LRUCache<K,V> as LinkedHasMap accepts null keys and/or values.
		
		ResultSet rs = null;
		try {
			stmt.setInt(1, agreementId);
			rs = stmt.executeQuery();
			return SQLCollections.paymentsCollection(rs);
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
		this.stmt.setDate(2, new java.sql.Date(endDate.getTime()) );
		this.stmt.setDate(3, new java.sql.Date(startDate.getTime()) );
	}
	
	
	
}
