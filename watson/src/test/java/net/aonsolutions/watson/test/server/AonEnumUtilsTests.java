package net.aonsolutions.watson.test.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.aonsolutions.watson.server.AonEnumUtils;


class AonEnumUtilsTests {
	private static final Byte ZERO_BYTE = Byte.valueOf("0");
	private static final Byte ONE_BYTE = Byte.valueOf("1");
	
	private static enum ENUMERATION {
		ZERO,ONE,TWO,THREE;
	}
	
	@Test()
	void byteTest() {
		
		assertNull( AonEnumUtils.getByte( (Boolean) null ));
		
		assertEquals( ZERO_BYTE , AonEnumUtils.getByte( false ));
		assertEquals( ZERO_BYTE , AonEnumUtils.getByte( Boolean.FALSE));
		
		assertEquals( ONE_BYTE , AonEnumUtils.getByte( true ));
		assertEquals( ONE_BYTE , AonEnumUtils.getByte( Boolean.TRUE ));
		
		ENUMERATION en = null;
		assertNull( AonEnumUtils.getByte( en ));
		assertEquals( ZERO_BYTE , AonEnumUtils.getByte( ENUMERATION.ZERO ));
		assertEquals( ONE_BYTE , AonEnumUtils.getByte( ENUMERATION.ONE ));
		
	}
	
	@Test()
	void booleanTest() {
		Byte byteObject =  null;
		assertFalse( AonEnumUtils.getBoolean( byteObject ));
		
		assertFalse( AonEnumUtils.getBoolean( Byte.valueOf((byte) 0) ));
		assertTrue( AonEnumUtils.getBoolean( Byte.valueOf((byte) 1) ));
		assertFalse( AonEnumUtils.getBoolean( Byte.valueOf((byte) 2) ));
		
		Boolean boolObject =  null;
		assertFalse( AonEnumUtils.getBoolean( boolObject ));
		assertTrue( AonEnumUtils.getBoolean( true ));
		assertFalse( AonEnumUtils.getBoolean( false ));
	}
	
}
