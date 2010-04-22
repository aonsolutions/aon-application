package com.transtools.jdbc.test;

import com.transtools.ctsql.CtsqlDecimal;
import com.transtools.ctsql.CtsqlFactory;

import junit.framework.TestCase;

public class DecimalTest extends TestCase
{
	private static final double PRECISION = 0.000001;

	private double values[] =
	{
		0.0,
		00.0,
		1.0,
		-1.0,
		24325465,
		-34085484,
		22.56,
		234.556,
		-234.566,
		0.4789069457,
		-0.4578945894,
		784524323494.4590848994,
		-104534542697.7894592437,
		0.00000000001,
		-0.00000000001,
		0.9999999999,
		-0.9999999999
	};


	public DecimalTest(String name)
	{
		super(name);
	}

	public boolean equals( double d1, double d2 ) {
		double dif = Math.abs( d1 - d2 );
		return ( dif  < PRECISION );
	}

	public void testConversions()
	{
		for( int i = 0; i < values.length; i++ )
		{
			CtsqlDecimal decimal = CtsqlFactory.getFactory().getNewDecimal( values[i] );

			assertNotNull( decimal );

			double decimalValue = decimal.getAsDouble();

			assertTrue( "Error in getAsDouble. Expected: "+values[i]+" Result: "+decimalValue, equals( decimalValue, values[i] ) );
		}
	}

	public static void main(String[] args) {
		DecimalTest dt = new DecimalTest("dt" );

		dt.testConversions();
		System.out.println( "Done !" );
	}


}
