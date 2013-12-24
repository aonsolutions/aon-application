package com.esferalia.aon.gwt.fiscal.sql;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.fiscal.shared.AonSQLException;
import com.esferalia.aon.gwt.fiscal.shared.Mod390;

public class SQLMod390 {
	private static Logger LOGGER = Logger.getLogger(SQLMod390.class.getName());

	// @formatter:off

	// @formatter:on

	public static Mod390 save(Connection conn, Mod390 mod390) throws AonSQLException {
		throw new UnsupportedOperationException("No implementado");
	}


	public static ArrayList<Mod390> getByDomain(int domain, Connection conn)
			throws AonSQLException {
		ArrayList<Mod390> list = new ArrayList<Mod390>();
		return list;
	}

	public static Mod390 getById(int id, Connection conn)
			throws AonSQLException {
		throw new UnsupportedOperationException("No implementado");
	}

	public static void delete(Connection conn, Mod390 mod390)
			throws AonSQLException {
		throw new UnsupportedOperationException("No implementado");
	}
}
