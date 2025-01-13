package net.aonsolutions.occam.api.model.type;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;

import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.AbstractOccamApiTest;
import net.aonsolutions.occam.api.model.AonRandom;

class TagTypeTest extends AbstractOccamApiTest {
	
	@RepeatedTest(3)
	void valueTest() {
		TagType m = AonRandom.getEnum(TagType.class);
		assertSame(m , TagType.values()[m.value()]);
	}
	
	@RepeatedTest(3)
	void valueByte() {
		TagType m = AonRandom.getEnum(TagType.class);
		assertTrue(TagType.value( (Byte) null).isEmpty());
		assertTrue(TagType.value( Byte.MIN_VALUE ).isEmpty());
		assertTrue(TagType.value( Byte.MAX_VALUE).isEmpty());
		
		Optional<TagType> om = TagType.value( m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}

	@RepeatedTest(3)
	void valueInteger() {
		TagType m = AonRandom.getEnum(TagType.class);
		assertTrue(TagType.value( (Integer) null).isEmpty());
		assertTrue(TagType.value( Integer.MIN_VALUE ).isEmpty());
		assertTrue(TagType.value( Integer.MAX_VALUE).isEmpty());
		Optional<TagType> om = TagType.value( (int) m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@RepeatedTest(3)
	void valueString() {
		TagType m = AonRandom.getEnum(TagType.class);
		assertTrue(TagType.value( (String) null).isEmpty());
		assertTrue(TagType.value( "" ).isEmpty());
		assertTrue(TagType.value( "12345|@#" ).isEmpty());
		String n1 = m.name();
		Optional<TagType> om = TagType.value( n1 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n2 = AonStringUtils.lowerCase(n1);
		om = TagType.value( n2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n3 = AonStringUtils.upperCase(n1);
		om = TagType.value( n3 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
	}
	
}