package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Cnae2025Rate.CNAE2025_RATE;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.occam.api.AONContext;


public class SQLCnae2025 {
	
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
	
	Map<String, Rates> ratesMap ;
	
	public SQLCnae2025(Connection connection, Date startDate, Date endDate) throws SQLException{
		loadRates(connection, startDate, endDate);
	}
	
	public Double getItRate(String cnae2025 ){
		Rates rates = getRates(cnae2025);
		return rates != null ? rates.getIt() : null;
	}

	public Double getImsRate(String cnae2025 ){
		Rates rates = getRates(cnae2025);
		return rates != null ? rates.getIms() : null;
	}

	private Rates getRates(String cnae2025 ){
		for ( String code = cnae2025; code.length() > 1 ; code = code.substring(0, code.length() - 1) ) {
			Rates rates = ratesMap.get(code);
			if( rates != null ) { 
				return rates;
			}
		}
		return null;
	}
	
	private void loadRates(Connection connection, Date startDate, Date endDate) throws SQLException{
		this.ratesMap = new HashMap<>();
		new AONContext(connection).getDslContext()
		.select(CNAE2025_RATE.asterisk()).from(CNAE2025_RATE).forEach( cnae2025RateRecord -> {
			Rates rates = new Rates();
			String cnae2025Code = cnae2025RateRecord.get(CNAE2025_RATE.CODE);
			Double itAmount = cnae2025RateRecord.get(CNAE2025_RATE.IT_AMOUNT);
			rates.setIt(itAmount == null ? 0.00 : itAmount);
			Double imsAmount = cnae2025RateRecord.get(CNAE2025_RATE.IMS_AMOUNT);
			rates.setIms(imsAmount == null ? 0.00 : imsAmount	);
			this.ratesMap.put (cnae2025Code, rates);
		});
	}
	
	
}
