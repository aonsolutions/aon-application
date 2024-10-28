package net.aonsolutions.occam.api.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.AbstractOccamApiTest;
import net.aonsolutions.occam.api.model.AonRandom;
import net.aonsolutions.occam.api.model.type.DocumentType.DocumentTypeVisitor;

class DocumentTypeTest extends AbstractOccamApiTest {
	
	@RepeatedTest(3)
	void valueStaTest() {
		Byte nullValue = DocumentType.value( (DocumentType) null);
		assertNull(nullValue);
		
		DocumentType m = AonRandom.getEnum(DocumentType.class);
		Byte b = DocumentType.value(m);
		assertEquals(b.byteValue() , m.value());
		
	}

	@RepeatedTest(3)
	void valueTest() {
		DocumentType m = AonRandom.getEnum(DocumentType.class);
		assertSame(m , DocumentType.values()[m.value()]);
	}
	
	@RepeatedTest(3)
	void valueByte() {
		DocumentType m = AonRandom.getEnum(DocumentType.class);
		assertTrue(DocumentType.value( (Byte) null).isEmpty());
		assertTrue(DocumentType.value( Byte.MIN_VALUE ).isEmpty());
		assertTrue(DocumentType.value( Byte.MAX_VALUE).isEmpty());
		
		Optional<DocumentType> om = DocumentType.value( m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}

	@RepeatedTest(3)
	void valueInteger() {
		DocumentType m = AonRandom.getEnum(DocumentType.class);
		assertTrue(DocumentType.value( (Integer) null).isEmpty());
		assertTrue(DocumentType.value( Integer.MIN_VALUE ).isEmpty());
		assertTrue(DocumentType.value( Integer.MAX_VALUE).isEmpty());
		Optional<DocumentType> om = DocumentType.value( (int) m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@RepeatedTest(3)
	void valueString() {
		DocumentType m = AonRandom.getEnum(DocumentType.class);
		assertTrue(DocumentType.value( (String) null).isEmpty());
		assertTrue(DocumentType.value( "" ).isEmpty());
		assertTrue(DocumentType.value( "12345|@#" ).isEmpty());
		String n1 = m.name();
		Optional<DocumentType> om = DocumentType.value( n1 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n2 = AonStringUtils.lowerCase(n1);
		om = DocumentType.value( n2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n3 = AonStringUtils.upperCase(n1);
		om = DocumentType.value( n3 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
	}

	@Test
	void testVisitor() {
		DocumentTypeVisitor<DocumentType> visitor = new DocumentTypeVisitor<DocumentType>() {
			@Override public DocumentType visitNif() {return DocumentType.NIF;}
			@Override public DocumentType visitCif() {return DocumentType.CIF;}
			@Override public DocumentType visitNie() {return DocumentType.NIE;}
			@Override public DocumentType visitPassport() {return DocumentType.PASSPORT;}
			@Override public DocumentType visitWorkPermit() {return DocumentType.WORK_PERMIT;}
			@Override public DocumentType visitCommunityCard() {return DocumentType.COMMUNITY_CARD;}
			@Override public DocumentType visitOther() {return DocumentType.OTHER;}
			@Override public DocumentType visitNotCensused() {return DocumentType.NOT_CENSUSED;}
		}; 
		AonCollectionUtils.stream(DocumentType.values())
			.forEach(a -> assertSame(a, a.visit(visitor)));
	}
	
}