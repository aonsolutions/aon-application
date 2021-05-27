package com.esferalia.aon.payroll.ctsql2mysql;

import java.sql.SQLException;

public class LazyMyPerson extends MyPerson {

	private AbstractCtsqlDB ctsqlDB;
	
	public LazyMyPerson(DefaultMysqlDB defaultMysqlDB) throws SQLException {
		super(defaultMysqlDB);
	}
	
	
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
		this.ctsqlDB = ctsqlDB;
		// Do nothing. Postpone 'inserts' until they are really needed. 
	};

	@Override
	public Integer getPerson(Integer oldCdg) {
		Integer person = super.getPerson(oldCdg);
		if ( person != null ) {
			return person;
		} // Already insertsed...
		try {
			ctsqlDB.visitPersona(oldCdg, this);
			return super.getPerson(oldCdg);
		} catch (SQLException e) {
			return null;
		}
	}

	@Override
	public String getName(Integer oldCdg) {
		String name = super.getName(oldCdg);
		if ( name != null ) {
			return name;
		} // Already insertsed...
		try {
			ctsqlDB.visitPersona(oldCdg, this);
			return super.getName(oldCdg);
		} catch (SQLException e) {
			return null;
		}
	}
	
	@Override
	public String getNumDoc(Integer oldCdg) {
		String numDoc = super.getNumDoc(oldCdg);
		if ( numDoc != null ) {
			return numDoc;
		} // Already insertsed...
		try {
			ctsqlDB.visitPersona(oldCdg, this);
			return super.getNumDoc(oldCdg);
		} catch (SQLException e) {
			return null;
		}
	}
	
}


