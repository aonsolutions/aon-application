package com.transtools.jdbc.test;

import com.transtools.ctsql.CtsqlFactory;
import com.transtools.ctsql.CtsqlType;

import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.TestCase;

public class SetNullTest extends TestCase{

	protected Connection connection;

	public SetNullTest(String name)
	{
		super(name);
	}

	protected void setUp()
	{
		connection = SqlHelper.getConnection();
		assertNotNull( connection );
	}

	protected void tearDown()
	{
		try
		{
			connection.close();
		} catch(SQLException ex) {
			fail(ex.getMessage());
		}
	}

	public void testExecute() {
		CtsqlType ctsqlValue = null;

			ctsqlValue = CtsqlFactory.getFactory().getNewChar( null );
			assertNotNull( ctsqlValue );
			assertTrue( "Char isn't null", ctsqlValue.isNull() );

			ctsqlValue = CtsqlFactory.getFactory().getNewDate( null );
			assertNotNull( ctsqlValue );
			assertTrue( "Date isn't null", ctsqlValue.isNull() );

			ctsqlValue = CtsqlFactory.getFactory().getNewTime( null );
			assertNotNull( ctsqlValue );
			assertTrue( "Time isn't null", ctsqlValue.isNull() );

			ctsqlValue = CtsqlFactory.getFactory().getNewDecimal( null );
			assertNotNull( ctsqlValue );
			assertTrue( "Decimal isn't null", ctsqlValue.isNull() );

			ctsqlValue = CtsqlFactory.getFactory().getNewInteger( null );
			assertNotNull( ctsqlValue );
			assertTrue( "Integer isn't null", ctsqlValue.isNull() );

			ctsqlValue = CtsqlFactory.getFactory().getNewSmallint( null );
			assertNotNull( ctsqlValue );
			assertTrue( "Smallint isn't null", ctsqlValue.isNull() );
	}

}
