package com.esferalia.aon.payroll.ctsql2mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.esferalia.aon.salary.enumeration.PaymentType;

public class Concepts implements IConcepts {
	
	private PreparedStatement 	stmt;
	private Connection 			connection;
	
	
	public Concepts(Connection connection) throws SQLException{
		this.connection = connection;
		this.stmt = this.connection.prepareStatement(
				"SELECT * FROM payment_concept WHERE code = ?");
	}
	
	@Override
	public Concept<PaymentType> getConcept(String codCom) 
		throws SQLException {
		ResultSet rs = null;
		try {
			String code = MyConcept.formatCode(codCom);
			stmt.setString(1, code );
			rs = stmt.executeQuery();
			
			if ( !rs.next() ) {
				return null;
			}
				
			Integer id = rs.getInt("id");
			String description = rs.getString("description");
			PaymentType type = null;
			if ( rs.getObject("type") != null ) {
				int ordinal = rs.getInt("type");
				type = int2PaymentType(ordinal); 
			}
			String quote = null; // TODO: Recuperar tipCot de codcom ¿ podemos ?.
			Concept<PaymentType> concept = 
				new Concept<PaymentType>(id, 
										code, 
										type, 
										quote, 
										description);
			return concept;
		}finally {
			if ( rs != null ){
				rs.close();
			}
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		if ( this.stmt != null ){
			this.stmt.close();
		}
		super.finalize();
	}
	
	private PaymentType int2PaymentType ( int ordinal ) {
		PaymentType values [] = PaymentType.values();
		for (PaymentType paymentType : values) {
			if ( paymentType.ordinal() == ordinal)
				return paymentType;
		}
		return null;
	}
	
}
