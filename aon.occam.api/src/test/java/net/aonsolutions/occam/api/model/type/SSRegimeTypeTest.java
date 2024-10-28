package net.aonsolutions.occam.api.model.type;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.occam.api.AbstractOccamApiTest;
import net.aonsolutions.occam.api.model.AonRandom;
import net.aonsolutions.occam.api.model.type.SSRegimeType.SSRegimeTypeVisitor;

class SSRegimeTypeTest extends AbstractOccamApiTest {
	
	@RepeatedTest(3)
	void valueTest() {
		SSRegimeType m = AonRandom.getEnum(SSRegimeType.class);
		assertSame(m , SSRegimeType.values()[m.value()]);
	}
	
	@RepeatedTest(3)
	void valueByte() {
		SSRegimeType m = AonRandom.getEnum(SSRegimeType.class);
		assertTrue(SSRegimeType.value( (Byte) null).isEmpty());
		assertTrue(SSRegimeType.value( Byte.MIN_VALUE ).isEmpty());
		assertTrue(SSRegimeType.value( Byte.MAX_VALUE).isEmpty());
		
		Optional<SSRegimeType> om = SSRegimeType.value( m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}

	@RepeatedTest(3)
	void valueInteger() {
		SSRegimeType m = AonRandom.getEnum(SSRegimeType.class);
		assertTrue(SSRegimeType.value( (Integer) null).isEmpty());
		assertTrue(SSRegimeType.value( Integer.MIN_VALUE ).isEmpty());
		assertTrue(SSRegimeType.value( Integer.MAX_VALUE).isEmpty());
		Optional<SSRegimeType> om = SSRegimeType.value( (int) m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@Test
	void testVisitor() {
		SSRegimeTypeVisitor<SSRegimeType> visitor = new SSRegimeTypeVisitor<SSRegimeType>() {
			@Override public SSRegimeType visitGeneralRegime(SSRegimeType ssRegimeType) { return SSRegimeType.GENERAL;}
			@Override public SSRegimeType visitAgriculturalRegime(SSRegimeType ssRegimeType) { return SSRegimeType.AGRICULTURAL;}
			@Override public SSRegimeType visitDomesticEmployeesRegime(SSRegimeType ssRegimeType) { return SSRegimeType.DOMESTIC_EMPLOYEES;}
			@Override public SSRegimeType visitSelfEmployedRegime(SSRegimeType ssRegimeType) { return SSRegimeType.SELF_EMPLOYED;}
			@Override public SSRegimeType visitCoalMiningRegime(SSRegimeType ssRegimeType) { return SSRegimeType.COAL_MINING;}
			@Override public SSRegimeType visitSeaWorkersRegime(SSRegimeType ssRegimeType) { return SSRegimeType.SEA_WORKERS;}
			@Override public SSRegimeType visitStudentInsuranceRegime(SSRegimeType ssRegimeType) { return SSRegimeType.STUDENT_INSURANCE;}
			@Override public SSRegimeType visitArtistRegime(SSRegimeType ssRegimeType) { return SSRegimeType.ARTIST;}
			@Override public SSRegimeType visitIsfasRegime(SSRegimeType ssRegimeType) { return SSRegimeType.ISFAS;}
			@Override public SSRegimeType visitMufaceRegime(SSRegimeType ssRegimeType) { return SSRegimeType.MUFACE;}
		}; 
		AonCollectionUtils.stream(SSRegimeType.values())
			.forEach(a -> assertSame(a, a.accept(visitor)));
	}

}
