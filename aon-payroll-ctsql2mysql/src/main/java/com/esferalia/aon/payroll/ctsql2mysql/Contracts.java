package com.esferalia.aon.payroll.ctsql2mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;


public class Contracts implements IContracts {

	private Date 		fromDate;
	private Connection  mysqlConnection;
	private Connection  ctsqlConnection;
	
	private boolean 	checkFVisionado;
	
	private PreparedStatement emprperStmt;
	private PreparedStatement contractStmt;
	
	public Contracts(Connection mysqlConnection, Connection  ctsqlConnection) 
		throws SQLException {
		this(mysqlConnection, ctsqlConnection, null, false);
	}

	public Contracts(Connection mysqlConnection, Connection  ctsqlConnection, Date fromDate, boolean 	checkFVisionado ) 
		throws SQLException {
		this.fromDate = fromDate;
		this.checkFVisionado = checkFVisionado;
		this.mysqlConnection = mysqlConnection;
		this.ctsqlConnection = ctsqlConnection;
		
		this.emprperStmt = ctsqlConnection.prepareStatement(
			"SELECT persona.numdoc nif, emprnif.numdoc cif, emprper.fecalt alta"+
			" FROM persona , emprnif , emprper "+
			" WHERE persona.cdg = emprper.codper"+
			" AND  emprnif.cdg = emprper.codemp"+
			" AND emprper.cdg = ? ");
		this.contractStmt = mysqlConnection.prepareStatement(
			"SELECT *"+ 
			" FROM contract "+
			", registry AS person"+
			", workplace" + 
			", registry AS enterprise" + 
			" WHERE person.id = contract.person"+
			" AND workplace.id = contract.workplace " +
			" AND workplace.enterprise = enterprise.id" +
			" AND person.document = ?"+
			" AND enterprise.document = ?"+
			" AND contract.start_date = ? ");
	}
	
	@Override
	public boolean checkFVisionado() {
		return checkFVisionado;
	}
	
	@Override
	public boolean outOfDate(Date date) {
		if ( date == null )
			return false;
		if ( fromDate == null )
			return false;
		return fromDate.compareTo(date) > 0 ; 
	}

	@Override
	public Integer getContractId(Integer oldCdg) 
		throws SQLException{
		ResultSet emprperRs = null;
		ResultSet contractRs = null;
		try {
			this.emprperStmt.setInt(1, oldCdg);
			emprperRs = this.emprperStmt.executeQuery();
			if ( !emprperRs.next() ) {
				return null;
			}
			
			this.contractStmt.setString(1, emprperRs.getString("nif"));
			this.contractStmt.setString(2, emprperRs.getString("cif"));
			this.contractStmt.setDate(3, emprperRs.getDate("alta"));
			contractRs = this.contractStmt.executeQuery();
			if ( !contractRs.next()){
				return null;
			}
			return contractRs.getInt("id");
		}
		finally {
			if ( emprperRs != null ){
				emprperRs = null;
			}
			if ( contractRs != null ){
				contractRs = null;
			}
		}
	}

	@Override
	public boolean hasEmbargo(String concepto) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public FullEmbargo getEmbargo(String concepto) {
		// TODO Auto-generated method stub
		return null;
	}

}
