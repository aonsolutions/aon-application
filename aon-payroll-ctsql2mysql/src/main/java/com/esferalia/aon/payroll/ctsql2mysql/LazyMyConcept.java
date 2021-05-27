package com.esferalia.aon.payroll.ctsql2mysql;

import java.sql.SQLException;

import com.esferalia.aon.salary.enumeration.PaymentType;

public class LazyMyConcept extends MyConcept {
	
	
	private AbstractCtsqlDB ctsqlDB;
	
	public LazyMyConcept(DefaultMysqlDB mysqlDB) {
		super(mysqlDB);
	}
	
	@Override
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
		this.ctsqlDB = ctsqlDB;
		// Do nothing. Postpone 'inserts' until they are really needed. 
	}

	
	@Override
	public Concept<PaymentType> getPaymentConcept(String codCom)
			throws SQLException {
		Concept<PaymentType> concept = 
				super.getPaymentConcept(codCom);
		if ( concept != null ) {
			return concept;
		} // Already inserted...
		ctsqlDB.visitComplemento(codCom, this);
		return super.getPaymentConcept(codCom);
	}
	
	
	@Override
	public Bonus getBonusConcept(Integer cdg) throws SQLException {
		Bonus bonus = super.getBonusConcept(cdg);
		if ( bonus != null ) {
			return bonus;
		} // Already inserted...
		ctsqlDB.visitTipboni(cdg, this);
		return super.getBonusConcept(cdg);
	}
	
	
}
