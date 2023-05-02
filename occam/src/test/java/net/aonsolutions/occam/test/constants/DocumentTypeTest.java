package net.aonsolutions.occam.test.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.constants.DocumentType;
import net.aonsolutions.occam.api.constants.DocumentType.DocumentTypeVisitor;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class DocumentTypeTest extends AbstractOccamTest {

	@Test()
	void nullIndexSafeByteValueTest() {
		assertTrue(DocumentType.safeValueOf((Byte) null).isEmpty());
	}
	@Test()
	void wrongIndexSafeByteValueTest() {
		assertTrue(DocumentType.safeValueOf((byte) DocumentType.values().length).isEmpty());
	}
	@Test()
	void rightIndexSafeByteValueTest() {
		DocumentType dt = AonRandom.getDocumentType().get();
		assertEquals(dt, DocumentType.safeValueOf( dt.value() ).get());
	}

	@Test()
	void nullIndexSafeValueTest() {
		assertTrue(DocumentType.safeValueOf( (Integer) null).isEmpty());
	}
	@Test()
	void wrongMinIndexSafeValueTest() {
		assertTrue(DocumentType.safeValueOf(Integer.MIN_VALUE).isEmpty());
	}
	@Test()
	void wrongMaxIndexSafeValueTest() {
		assertTrue(DocumentType.safeValueOf(Integer.MAX_VALUE).isEmpty());
	}
	@Test()
	void rightIndexSafeValueTest() {
		DocumentType dt = AonRandom.getDocumentType().get();
		assertEquals(dt, DocumentType.safeValueOf( dt.ordinal() ).get());
	}
	
	@Test()
	void nullNameSafeValueTest() {
		assertTrue(DocumentType.safeValueOf( (String) null ).isEmpty());
	}
	@Test()
	void wrongNameSafeValueTest() {
		assertTrue(DocumentType.safeValueOf( "" ).isEmpty());
	}
	@Test()
	void rightNameSafeValueTest() {
		DocumentType dt = AonRandom.getDocumentType().get();
		assertEquals(dt,DocumentType.safeValueOf( dt.toString() ).get());
	}
	
	@Test()
	void visitorTest() {
		DocumentTypeVisitor<Boolean,DocumentType> visitor = new DocumentTypeVisitor<Boolean,DocumentType>() {
			@Override public Boolean visitNif(DocumentType t) {return t == DocumentType.NIF;}
			@Override public Boolean visitCif(DocumentType t) {return t == DocumentType.CIF;}
			@Override public Boolean visitNie(DocumentType t) {return t == DocumentType.NIE;}
			@Override public Boolean visitPassport(DocumentType t) {return t == DocumentType.PASSPORT;}
			@Override public Boolean visitWorkPermit(DocumentType t) {return t == DocumentType.WORK_PERMIT;}
			@Override public Boolean visitCommunityCard(DocumentType t) {return t == DocumentType.COMMUNITY_CARD;}
			@Override public Boolean visitOther(DocumentType t) {return t == DocumentType.OTHER;}
			@Override public Boolean visitNotCensused(DocumentType t) {return t == DocumentType.NOT_CENSUSED;}
		};
		Arrays.stream(DocumentType.values()).forEach( dt -> assertTrue(dt.visit(visitor, dt)));
	}
	
}
