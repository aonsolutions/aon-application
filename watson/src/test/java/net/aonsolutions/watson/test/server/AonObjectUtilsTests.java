package net.aonsolutions.watson.test.server;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.aonsolutions.watson.client.MutableBoolean;
import net.aonsolutions.watson.server.AonObjectUtils;


class AonObjectUtilsTests {
	
	@Test()
	void equalsTest() {
		Object o1 = new Object();
		Object o2 = new Object();
		
		assertTrue( AonObjectUtils.equals(null, null) );
		assertFalse( AonObjectUtils.equals(o1, null) );
		assertFalse( AonObjectUtils.equals(null, o1) );
		assertFalse( AonObjectUtils.equals(o1, o2) );
		assertTrue( AonObjectUtils.equals(o1, o1) );
		assertTrue( AonObjectUtils.equals(o2, o2) );
		
		Integer i1 = Integer.valueOf(1);
		Integer i2 = Integer.valueOf(2);
		assertFalse( AonObjectUtils.equals(i1, null) );
		assertFalse( AonObjectUtils.equals(null, i1) );
		assertFalse( AonObjectUtils.equals(i1, i2) );
		assertTrue( AonObjectUtils.equals(i1, i1) );
		assertTrue( AonObjectUtils.equals(i2, i2) );
	}
	
	@Test()
	void notEqualsTest() {
		Object o1 = new Object();
		Object o2 = new Object();
		
		assertFalse( AonObjectUtils.notEquals(null, null) );
		assertTrue( AonObjectUtils.notEquals(o1, null) );
		assertTrue( AonObjectUtils.notEquals(null, o1) );
		assertTrue( AonObjectUtils.notEquals(o1, o2) );
		assertFalse( AonObjectUtils.notEquals(o1, o1) );
		assertFalse( AonObjectUtils.notEquals(o2, o2) );
		
		Integer i1 = Integer.valueOf(1);
		Integer i2 = Integer.valueOf(2);
		assertTrue( AonObjectUtils.notEquals(i1, null) );
		assertTrue( AonObjectUtils.notEquals(null, i1) );
		assertTrue( AonObjectUtils.notEquals(i1, i2) );
		assertFalse( AonObjectUtils.notEquals(i1, i1) );
		assertFalse( AonObjectUtils.notEquals(i2, i2) );
	}
	
	@Test()
	void defaultIfNullTest() {
		assertTrue( AonObjectUtils.defaultIfNull(null, true) ); 		
		assertFalse( AonObjectUtils.defaultIfNull(null, false) );
		assertTrue( AonObjectUtils.defaultIfNull(true, false) );
		assertFalse( AonObjectUtils.defaultIfNull(false, true) );
	}
	
	@Test()
	void ifNotNullDoTest() {
		assertNull( AonObjectUtils.ifNotNullGet(null, x -> x));
		
		MutableBoolean bool = new MutableBoolean( false );
		AonObjectUtils.ifNotNullGet(bool, x -> {
			x.setValue(true);
			return x;
		});
		assertTrue( bool.booleanValue() );
	}
	
	@Test()
	void ifTrueTest() {
		MutableBoolean bool = new MutableBoolean( false );
		AonObjectUtils.ifTrue(true, () -> bool.setValue(true));
		assertTrue( bool.booleanValue() );
		
		MutableBoolean bool1 = new MutableBoolean( false );
		AonObjectUtils.ifTrue(false, () -> bool1.setValue(true));
		assertFalse( bool1.booleanValue() );
	}
}
