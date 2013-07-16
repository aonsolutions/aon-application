package com.esferalia.aon.payroll.ctsql2mysql;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

public class Ctsql2MysqlSync {

	private static void sync(Ctsql2Mysql ctsql2Mysql) throws SQLException {

		Connection ctsqlConnection = null;
		Connection mysqlConnection = null;

		try {
			ctsqlConnection = ctsql2Mysql.getCtsqlConnection();

			mysqlConnection = ctsql2Mysql.getMysqlConnection();
			mysqlConnection.setAutoCommit(false);

			MysqlSync mysqlSync = new MysqlSync(mysqlConnection,
					ctsql2Mysql.getImagesDir());

			CtsqlDB ctsqlReader = new CtsqlDB(ctsqlConnection);

			mysqlSync.sync(ctsqlReader);

			if (!ctsql2Mysql.isDryRun()) {
				mysqlConnection.commit();
			}
		} finally {
			if (ctsqlConnection != null)
				ctsqlConnection.close();
			if (mysqlConnection != null)
				mysqlConnection.close();
		}

	}

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		
		Class<?> returnType = null;
		
		;
		
		
		Method methdos [] = Object.class.getMethods() ;
		for (Method method : methdos) {
			System.out.println(method.getName() + " = " + ( method.getReturnType() == void.class ));
		}
		
		
		return;
		
		
		
		//Ctsql2Mysql ctsql2Mysql = new Ctsql2Mysql(args);
		//Ctsql2MysqlSync.sync(ctsql2Mysql);
		
		
	}

}
