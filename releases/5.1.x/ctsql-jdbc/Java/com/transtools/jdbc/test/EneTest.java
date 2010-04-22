package com.transtools.jdbc.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

public class EneTest extends TestCase {

    protected Connection connection;

    public EneTest(String name)
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
			SqlHelper sqlHelper = new SqlHelper( connection );    	
    	
        try
        {
    				sqlHelper.createTableEx( "ENE", new String[] { "A CHAR(1)" } );
        } catch(SQLException ex) {
           fail(ex.getMessage());
        }

        try
        {
						Statement stmt = connection.createStatement();
						stmt.execute( "INSERT INTO ENE (A) VALUES (\"ñ\")" );
						stmt.close();
				} catch(SQLException ex) {
           fail(ex.getMessage());
        }
        
        try
        {
    				sqlHelper.dropTable( "ENE" );
        } catch(SQLException ex) {
           fail(ex.getMessage());
        }
    }

}
