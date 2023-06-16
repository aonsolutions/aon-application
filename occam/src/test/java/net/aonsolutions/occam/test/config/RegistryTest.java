package net.aonsolutions.occam.test.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.config.Registry;
import net.aonsolutions.occam.api.constants.Country;
import net.aonsolutions.occam.api.constants.DocumentType;
import net.aonsolutions.occam.api.metadata.RegistryMetadata;
import net.aonsolutions.occam.api.metadata.RegistryMetadata.RegistryMetadataVisitor;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class RegistryTest extends AbstractOccamTest {

	@Test()
	void dirtyIdTest() {
		Registry d = new Registry();
		d.setId(1);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyDomainTest() {
		Registry d = new Registry();
		d.setDomain(1);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyDocumentTest() {
		Registry d = new Registry();
		d.setDocument(AonRandom.string(10));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyDocumentTypeTest() {
		Registry d = new Registry();
		d.setDocumentType(DocumentType.CIF);
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyDocumentCountryTest() {
		Registry d = new Registry();
		d.setDocumentCountry(Country.ES);
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyNameTest() {
		Registry d = new Registry();
		d.setName(AonRandom.string(10));
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyAliasTest() {
		Registry d = new Registry();
		d.setAlias(AonRandom.string(10));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyNationalityTest() {
		Registry d = new Registry();
		d.setNationality(Country.ES);
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyEnableHeredityTest() {
		Registry d = new Registry().setConfidential(true);
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyMarkTrueTest() {
		Registry d = new Registry();
		d.setDocumentType( DocumentType.CIF );
		d.setId( null );
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyMarkFalseTest() {
		Registry d = new Registry();
		d.setId( null );
		d.setName( null );
		assertFalse(d.isDirty());
	}
	
	@Test()
	void selectedMarkTest() {
		Registry d = new Registry();
		d.setSelected( true );
		assertTrue(d.isSelected());
	}
	
	@Test()
	void equalsTest() {
		Registry d1 = new Registry();
		Registry d2 = null;
		assertNotEquals(d1,d2);
		assertEquals(d1,d1);
		d2 = new Registry();
		assertEquals(d1,d2);
		d1.setId(1);
		assertNotEquals(d1,d2);
		d2.setId(1);
		assertEquals(d1,d2);
	}

	@Test()
	void metadataVisitorTest() {
		RegistryMetadataVisitor<RegistryMetadata> visitor = new RegistryMetadataVisitor<>() {
			@Override public RegistryMetadata visitId() { return RegistryMetadata.ID; }
			@Override public RegistryMetadata visitDomain() { return RegistryMetadata.DOMAIN; }
			@Override public RegistryMetadata visitDocument() { return RegistryMetadata.DOCUMENT; }
			@Override public RegistryMetadata visitDocumentType() { return RegistryMetadata.DOCUMENT_TYPE; }
			@Override public RegistryMetadata visitDocumentCountry() { return RegistryMetadata.DOCUMENT_COUNTRY; }
			@Override public RegistryMetadata visitName() { return RegistryMetadata.NAME; }
			@Override public RegistryMetadata visitAlias() { return RegistryMetadata.ALIAS; }
			@Override public RegistryMetadata visitNationality() { return RegistryMetadata.NATIONALITY; }
			@Override public RegistryMetadata visitConfidential() { return RegistryMetadata.CONFIDENTIAL; }
		};

		Arrays.stream(RegistryMetadata.values()).forEach( dm -> {
			assertEquals( dm, dm.visit(visitor));
		});		
	}
}