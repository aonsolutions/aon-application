package com.esferalia.aon.payroll.ctsql2mysql;

import java.sql.SQLException;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class Ctsql2MysqlTest 
    extends TestCase
{
	
	
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public Ctsql2MysqlTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( Ctsql2MysqlTest.class );
    }


    /**
     * Rigourous Test :-)
     * @throws SQLException 
     * @throws ClassNotFoundException 
     */
    public void testCtsql2Mysql() throws SQLException, ClassNotFoundException
    {
	/*
    	String args [] = {
    			"-dryrun",
    			"-mysqlurl", "jdbc:mysql://127.0.0.1:3306/rtrepiana-esferalia-com",
    			"-ctsqlurl", "jdbc:ctsql://192.168.2.100:1101/empre055;DBPATH=/usr/share/ctsql/data;RTRIMCHAR=true"
    	} ;
    	Ctsql2Mysql.main(args);
	*/
    }
     
    
}
