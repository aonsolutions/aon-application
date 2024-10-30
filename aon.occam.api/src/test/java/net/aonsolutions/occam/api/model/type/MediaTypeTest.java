package net.aonsolutions.occam.api.model.type;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.AbstractOccamApiTest;
import net.aonsolutions.occam.api.model.AonRandom;
import net.aonsolutions.occam.api.model.type.MediaType.MediaTypeVisitor;

class MediaTypeTest extends AbstractOccamApiTest {
	
	@RepeatedTest(3)
	void valueTest() {
		MediaType m = AonRandom.getEnum(MediaType.class);
		assertSame(m , MediaType.values()[m.value()]);
	}
	
	@RepeatedTest(3)
	void valueByte() {
		MediaType m = AonRandom.getEnum(MediaType.class);
		assertTrue(MediaType.value( (Byte) null).isEmpty());
		assertTrue(MediaType.value( Byte.MIN_VALUE ).isEmpty());
		assertTrue(MediaType.value( Byte.MAX_VALUE).isEmpty());
		
		Optional<MediaType> om = MediaType.value( m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}

	@RepeatedTest(3)
	void valueInteger() {
		MediaType m = AonRandom.getEnum(MediaType.class);
		assertTrue(MediaType.value( (Integer) null).isEmpty());
		assertTrue(MediaType.value( Integer.MIN_VALUE ).isEmpty());
		assertTrue(MediaType.value( Integer.MAX_VALUE).isEmpty());
		Optional<MediaType> om = MediaType.value( (int) m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@RepeatedTest(3)
	void valueString() {
		MediaType m = AonRandom.getEnum(MediaType.class);
		assertTrue(MediaType.value( (String) null).isEmpty());
		assertTrue(MediaType.value( "" ).isEmpty());
		assertTrue(MediaType.value( "12345|@#" ).isEmpty());
		String n1 = m.name();
		Optional<MediaType> om = MediaType.value( n1 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n2 = AonStringUtils.lowerCase(n1);
		om = MediaType.value( n2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n3 = AonStringUtils.upperCase(n1);
		om = MediaType.value( n3 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
	}

	@Test
	void testVisitor() {
		MediaTypeVisitor<MediaType> visitor = new MediaTypeVisitor<MediaType>() {
			@Override public MediaType visitUnknown() {return MediaType.UNKNOWN;}
			@Override public MediaType visitFixedPhone() {return MediaType.FIXED_PHONE;}
			@Override public MediaType visitCellular() {return MediaType.CELLULAR;}
			@Override public MediaType visitFax() {return MediaType.FAX;}
			@Override public MediaType visitEmail() {return MediaType.EMAIL;}
			@Override public MediaType visitWeb() {return MediaType.WEB;}
		}; 
		AonCollectionUtils.stream(MediaType.values())
			.forEach(a -> assertSame(a, a.visit(visitor)));
	}
	
}