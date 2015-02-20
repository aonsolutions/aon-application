package com.code.aon.dbutils;

import java.io.InputStream;

import org.junit.Test;

public class AonSQLFileTestCase {

	@Test
	public void testGetStatement() throws AonSQLException {
		InputStream input = AonSQLFileTestCase.class.getClassLoader().getResourceAsStream(
				"com/code/aon/dbutils/create_procedure.sql");
		AonSQLFile aonSQLFile = new AonSQLFile(input);
		aonSQLFile.getStatement();
		org.junit.Assert.assertEquals(false, aonSQLFile.ready());
	}
	

}
