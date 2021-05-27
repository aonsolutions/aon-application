package com.esferalia.aon.payroll.ctsql2mysql;

import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.payroll.ctsql2mysql.AbstractMysqlDB.Contract_embargo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractMysqlDB.Salary_embargo;

public interface IContracts {
	
	public static class FullEmbargo {
		
		java.sql.Date	endDate;
		Contract_embargo contractEmbargo;
		List<Salary_embargo> salaryEmbargos; 

		public FullEmbargo() {
			endDate = null;
			contractEmbargo = new Contract_embargo();
			salaryEmbargos = new LinkedList<Salary_embargo>();
		}
		
		public void addEmbargo(Salary_embargo embargo,  java.sql.Date endDate){
			this.salaryEmbargos.add(embargo);
			checkEmbargoLeft(endDate);
		}

		private void checkEmbargoLeft(java.sql.Date endDate) {
			
			if ( this.endDate == null || MyContract.before(this.endDate, endDate)){
				this.endDate  = endDate;
			}
			
			double  left = this.contractEmbargo.amount;  
			for (Salary_embargo salary_embargo : this.salaryEmbargos) {
				left -= salary_embargo.amount;
			}
			
			if ( left < 1.00 ) {
				this.contractEmbargo.end_date = this.endDate;
			}
		}
	}

	public boolean checkFVisionado ( );

	public boolean outOfDate ( Date date );
	
	public Integer getContractId(Integer oldCdg) 
		throws SQLException;
	
	public boolean hasEmbargo(String concepto);
	
	public FullEmbargo getEmbargo (String concepto);
	
	
}
