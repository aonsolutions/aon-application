package com.esferalia.aon.payroll.ctsql2mysql;

import java.sql.SQLException;
import java.util.Date;

import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percep;
import com.esferalia.aon.payroll.ctsql2mysql.MyAgreement.PercepPercnivComparator;

public class LazyMyAgreement extends MyAgreement {
	
	
	private AbstractCtsqlDB ctsqlDB;
	
	public LazyMyAgreement(DefaultMysqlDB mysqlDB, IConcepts concepts) {
		super(mysqlDB, concepts);
	}
	
	public LazyMyAgreement(DefaultMysqlDB mysqlDB, IConcepts concepts, Date startDate ) {
		super(mysqlDB, concepts, startDate);
	}
	
	@Override
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
		init(ctsqlDB);
		this.ctsqlDB = ctsqlDB;
		// Do nothing. Postpone 'inserts' until they are really needed. 
	}
	
	@Override
	public Integer getAgreement(String oldCdg) {
		Integer agreement = 
				super.getAgreement(oldCdg);
		if ( agreement != null ) {
			return agreement;
		} // Already inserted...
		
		try {
			ctsqlDB.visitConvenio(oldCdg, this);
			return super.getAgreement(oldCdg);
		} catch (SQLException e) {
			return null;
		}
	}
	
	
	@Override
	public boolean containsExtra(String oldCdg, String codCom) {
		getAgreement(oldCdg);
		return super.containsExtra(oldCdg, codCom);
	}
	
	@Override
	public String getAgreementCategory(String codCon, String nivel,
			String oldCdg) {
		getAgreement(codCon);
		return super.getAgreementCategory(oldCdg, nivel, oldCdg);
	}
	
	@Override
	public int hasPayment(String cdg, String nivel, String codcom, Percep percep)
			throws SQLException {
		getAgreement(cdg);
		return super.hasPayment(cdg, nivel, codcom, percep);
		
	}
}
