package net.aonsolutions.core.dbutils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;

import org.junit.jupiter.api.Test;

public class AonSQLFileTestCase {

	@Test
	public void testGetStatement() throws AonSQLException {
		InputStream input = AonSQLFileTestCase.class.getClassLoader().getResourceAsStream(
				"net/aonsolutions/core/dbutils/create_procedure.sql");
		AonSQLFile aonSQLFile = new AonSQLFile(input);
		while( aonSQLFile.ready() ) {
			aonSQLFile.getStatement();
		}
		assertEquals(false, aonSQLFile.ready());
	}
	
}
