package com.esferalia.aon.occam.api.model.doc;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.doc.ExternalStorage.ExternalStorageVisitor;
import com.esferalia.aon.occam.test.Repeat;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ExternalStorageTest {
	
	@Repeat(3)
	public void valueTest() {
		ExternalStorage m = AonRandom.getEnum(ExternalStorage.class);
		assertEquals(m , ExternalStorage.values()[m.value()]);
	}
	
	@Repeat(3)
	public void valueByte() {
		ExternalStorage m = AonRandom.getEnum(ExternalStorage.class);
		assertNull(ExternalStorage.safeValueOf( (Byte) null) );
		assertNull(ExternalStorage.safeValueOf( Byte.MIN_VALUE ));
		assertNull(ExternalStorage.safeValueOf( Byte.MAX_VALUE));
		
		ExternalStorage om = ExternalStorage.safeValueOf( m.value() );
		assertNotNull(om );
		assertEquals(m, om );
	}

	@Repeat(3)
	public void valueInteger() {
		ExternalStorage m = AonRandom.getEnum(ExternalStorage.class);
		assertNull(ExternalStorage.safeValueOf( (Integer) null) );
		assertNull(ExternalStorage.safeValueOf( Integer.MIN_VALUE ) );
		assertNull(ExternalStorage.safeValueOf( Integer.MAX_VALUE) );
		
		ExternalStorage om = ExternalStorage.safeValueOf( (int) m.value() );
		assertNotNull(om );
		assertEquals(m, om );
	}
	
	@Repeat(3)
	public void valueString() {
		ExternalStorage m = AonRandom.getEnum(ExternalStorage.class);
		assertNull(ExternalStorage.safeValueOf( (String) null));
		assertNull(ExternalStorage.safeValueOf( "" ));
		assertNull(ExternalStorage.safeValueOf( "12345|@#" ));
		
		String n1 = m.name();
		ExternalStorage om = ExternalStorage.safeValueOf( n1 );
		assertNotNull(om );
		assertEquals(m, om);
		
		String n2 = AonStringUtils.lowerCase(n1);
		om = ExternalStorage.safeValueOf( n2 );
		assertNotNull(om );
		assertEquals(m, om);
		
		String n3 = AonStringUtils.upperCase(n1);
		om = ExternalStorage.safeValueOf( n3 );
		assertNotNull(om );
		assertEquals(m, om);
		
	}
	
	@Test
	public void testVisitor() {
		ExternalStorageVisitor<ExternalStorage> v = new ExternalStorageVisitor<>() {
			@Override public ExternalStorage visitAon() { return ExternalStorage.AON; }
			@Override public ExternalStorage visitDrive() { return ExternalStorage.DRIVE; }
			@Override public ExternalStorage visitAws() { return ExternalStorage.AWS; }
			@Override public ExternalStorage visitScaleway() { return ExternalStorage.SCALEWAY;}
		};
		AonCollectionUtils.stream(ExternalStorage.values())
			.forEach(a -> assertEquals(a, a.visit(v)));
	}
	
}