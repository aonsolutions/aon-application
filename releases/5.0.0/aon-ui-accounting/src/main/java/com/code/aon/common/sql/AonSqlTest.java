package com.code.aon.common.sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;

import com.code.aon.common.dao.hibernate.HibernateUtil;

public class AonSqlTest {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws FileNotFoundException, AonSQLException {
		Connection c = HibernateUtil.getSQLConnection();
		File file = new File("/tmp/balance_def.sql");
		FileInputStream input = new FileInputStream( file );
		
		AonSQLFile sqlFile = new AonSQLFile( input );
		AonSQLScript script = new AonSQLScript( sqlFile, c);
		System.out.println( "START");
		script.execute();
		System.out.println( "STOP");
	} 
}
