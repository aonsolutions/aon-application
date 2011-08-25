package com.esferalia.aon.payroll.calculator.sql;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.code.aon.config.CNAE;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.sql.AbstractSQL;
import com.esferalia.aon.payroll.sql.SQLConstants.Cnae2009RateColumns;


public class SQLCnae2009 {
	
	private static class Rates {
		private Double it;
		private Double ims;
		
		public Double getIt() {
			return it;
		}
		public void setIt(Double it) {
			this.it = it;
		}
		public Double getIms() {
			return ims;
		}
		public void setIms(Double ims) {
			this.ims = ims;
		}
		
	}
	
	Map<Integer, Rates> ratesMap ;
	
	public SQLCnae2009(Connection connection, Date startDate, Date endDate) throws SQLException{
		loadRates(connection, startDate, endDate);
	}
	
	public Double getItRate(Integer cnae2009 ){
		Rates rates = getRates(cnae2009);
		return rates != null ? rates.getIt() : null;
	}

	public Double getImsRate(Integer cnae2009 ){
		Rates rates = getRates(cnae2009);
		return rates != null ? rates.getIms() : null;
	}

	private Rates getRates(int cnae2009 ){
		
		for ( int code = cnae2009; code > 0 ; code = code / 10 ) {
			Rates rates = ratesMap.get(code);
			if( rates != null ) { 
				return rates;
			}
		}
		return null;
	}
	
	private void loadRates(Connection connection, Date startDate, Date endDate) throws SQLException{
		
		ResultSet rs = null;
		PreparedStatement stmt  = null;
		try {
			stmt = connection.prepareStatement(
					"SELECT *"
					+" FROM cnae2009_rate"
					+" WHERE start_date <= ? "					 
					+" AND ( end_date  IS NULL"
					+" OR end_date >= ? )" );
			stmt.setDate(1, new java.sql.Date(startDate.getTime()));
			stmt.setDate(2, new java.sql.Date(endDate.getTime()));
			rs = stmt.executeQuery();
			ratesMap = new HashMap<Integer, Rates>();
			while ( rs.next() ) {
				Rates rates = new Rates();
				Integer cnae2009 = rs.getInt(Cnae2009RateColumns.CNAE2009);
				BigDecimal it = rs.getBigDecimal(Cnae2009RateColumns.IT_AMOUNT);
				rates.setIt(it == null ? 0.00 : it.doubleValue());
				BigDecimal ims = rs.getBigDecimal(Cnae2009RateColumns.IMS_AMOUNT);
				rates.setIms(ims == null ? 0.00 : ims.doubleValue());
				ratesMap.put (cnae2009, rates);
			}
		}finally {
			if ( rs != null)
				rs.close();
			if ( stmt != null)
				stmt.close();
		}
	}
	
	
}
