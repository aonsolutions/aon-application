package net.aonsolutions.watson.test.client.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.aonsolutions.watson.client.util.AonNumberUtils;


class AonNumberUtilsTests {

	static final double e1 = 0.54;
	static final double e10 = 0.544;
	
	static final double d1 = 0.55;
	static final double d10 = 0.545;
	static final double d11 = 0.547;
	
	@Test
	void roundTest(){
    	assertEquals(e1, AonNumberUtils.round(e10));
    	assertEquals(d1, AonNumberUtils.round(d10));
    	assertEquals(d1, AonNumberUtils.round(d11));
    }

	@Test
    void equalsTest(){
		Integer i1 = Integer.valueOf(1);
		Integer i2 = Integer.valueOf(2);
		assertFalse( AonNumberUtils.equals(i1, null) );
		assertFalse( AonNumberUtils.equals(null, i1) );
		assertFalse( AonNumberUtils.equals(i1, i2) );
		assertTrue( AonNumberUtils.equals(i1, i1) );
		assertTrue( AonNumberUtils.equals(i2, i2) );
	}
    
    @Test
    void notEqualsTest(){
		Integer i1 = Integer.valueOf(1);
		Integer i2 = Integer.valueOf(2);
		assertTrue( AonNumberUtils.notEquals(i1, null) );
		assertTrue( AonNumberUtils.notEquals(null, i1) );
		assertTrue( AonNumberUtils.notEquals(i1, i2) );
		assertFalse( AonNumberUtils.notEquals(i1, i1) );
		assertFalse( AonNumberUtils.notEquals(i2, i2) );
	}
    
    @Test
    void toStringTest(){
    	Integer i1 = null;
    	assertNull( AonNumberUtils.toString(i1) );
    	i1 = Integer.valueOf(1);
    	assertEquals( "1", AonNumberUtils.toString(i1) );
    	
    	Double d1 = null;
    	assertNull( AonNumberUtils.toString(d1) );
    	d1 = Double.valueOf(1);
    	assertEquals( "1.0", AonNumberUtils.toString(d1) );
    	
    	Long l1 = null;
    	assertNull( AonNumberUtils.toString(l1) );
    	l1 = Long.valueOf(1);
    	assertEquals( "1", AonNumberUtils.toString(l1) );

    	Float f1 = null;
    	assertNull( AonNumberUtils.toString(f1) );
    	f1 = Float.valueOf(1);
    	assertEquals( "1.0", AonNumberUtils.toString(f1) );
    }

    @Test
    void toIntegerTest(){
    	Long l1 = null;
    	assertNull( AonNumberUtils.toInteger(l1) );
    	l1 = Long.valueOf(1);
    	assertEquals( 1, AonNumberUtils.toInteger(l1) );

    	Double d1 = null;
    	assertNull( AonNumberUtils.toInteger(d1) );
    	d1 = Double.valueOf(1);
    	assertEquals( 1, AonNumberUtils.toInteger(d1) );
    	d1 = Double.valueOf(1.1);
    	assertEquals( 1, AonNumberUtils.toInteger(d1) );
    }

}
