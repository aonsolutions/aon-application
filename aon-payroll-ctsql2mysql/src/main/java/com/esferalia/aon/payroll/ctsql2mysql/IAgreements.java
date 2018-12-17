package com.esferalia.aon.payroll.ctsql2mysql;

import java.sql.SQLException;

import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprper;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percep;

public interface IAgreements {

	public Integer getAgreement(String oldCdg) ;
	
	public Integer getAgreementLevel(String codCon, String oldCdg);

	public String getAgreementCategory(String codCon, String nivel, String oldCdg);
	
	public boolean containsExtra(String oldCdg, String codCom );

	public String insertAgreementCategory(String codCon, String nivel, String oldCdg) 
		throws SQLException;
	
	public int hasPayment(String cdg, String nivel, String codcom,  Percep percep) 
		throws SQLException;
	
	public boolean inherits(Emprper emprper, String codcon, String nivel  ) 
		throws SQLException;

	
}
