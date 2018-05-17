package net.aonsolutions.core.dbutils;

import java.io.InputStream;

import org.junit.Test;

public class AonSQLFileTestCase {

	@Test
	public void testGetStatement() throws AonSQLException {
		InputStream input = AonSQLFileTestCase.class.getClassLoader().getResourceAsStream(
				"net/aonsolutions/core/dbutils/create_procedure.sql");
		AonSQLFile aonSQLFile = new AonSQLFile(input);
		while( aonSQLFile.ready() ) {
			System.out.println(aonSQLFile.getStatement());
		}
		org.junit.Assert.assertEquals(false, aonSQLFile.ready());
	}
	
}
