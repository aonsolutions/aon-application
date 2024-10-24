package net.aonsolutions.occam.api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.occam.api.model.metadata.InvoiceAddressMetadata;
import net.aonsolutions.occam.api.model.metadata.InvoiceAddressMetadata.InvoiceAddressMetadataVisitor;
import net.aonsolutions.occam.api.model.type.StreetType;

class InvoiceAddressTest {

	@Test
	void testInvoiceAddress() {
		InvoiceAddress expected = AonMocker.mock(InvoiceAddress.class);
		InvoiceAddress actual = new InvoiceAddress()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setInvoice(expected.getInvoice())
			.setStreetType(expected.getStreetType())
			.setAddress(expected.getAddress())
			.setNumber(expected.getNumber())
			.setAddress2(expected.getAddress2())
			.setZip(expected.getZip())
			.setCity(expected.getCity())
			.setProvince(expected.getProvince())
			.setGeozone(expected.getGeozone().orElse(null))
			.setParent(expected.getParent().orElse(null))
			.setDeleted(expected.isDeleted())
		;
		AonAsserts.assertClassEquals(expected, actual);
	}

	@Test
	void testInvoiceAddressMetadataVisitor() {
		InvoiceAddressMetadataVisitor<InvoiceAddressMetadata> v = new InvoiceAddressMetadataVisitor<>() {
			 @Override public InvoiceAddressMetadata visitId() {return InvoiceAddressMetadata.ID;}
			 @Override public InvoiceAddressMetadata visitDomain() {return InvoiceAddressMetadata.DOMAIN;}
			 @Override public InvoiceAddressMetadata visitInvoice() {return InvoiceAddressMetadata.INVOICE;}
			 @Override public InvoiceAddressMetadata visitStreetType() {return InvoiceAddressMetadata.STREET_TYPE;}
			 @Override public InvoiceAddressMetadata visitAddress() {return InvoiceAddressMetadata.ADDRESS;}
			 @Override public InvoiceAddressMetadata visitNumber() {return InvoiceAddressMetadata.NUMBER;}
			 @Override public InvoiceAddressMetadata visitAddress2() {return InvoiceAddressMetadata.ADDRESS2;}
			 @Override public InvoiceAddressMetadata visitZip() {return InvoiceAddressMetadata.ZIP;}
			 @Override public InvoiceAddressMetadata visitCity() {return InvoiceAddressMetadata.CITY;}
			 @Override public InvoiceAddressMetadata visitProvince() {return InvoiceAddressMetadata.PROVINCE;}
			 @Override public InvoiceAddressMetadata visitGeozone() {return InvoiceAddressMetadata.GEOZONE;}
			 @Override public InvoiceAddressMetadata visitParent() {return InvoiceAddressMetadata.PARENT;}
		};
		AonCollectionUtils.stream(InvoiceAddressMetadata.values())
			.forEach(a -> assertSame(a, a.visit(v)));
	}
	
	@Test
	void testDirtyId() {
		InvoiceAddress ent = new InvoiceAddress();
		ent.setId(1);
		assertTrue( ent.isDirty(InvoiceAddressMetadata.ID) );
	}
	
	@Test
	void testDirtyDomain() {
		InvoiceAddress ent = new InvoiceAddress();
		ent.setDomain(1);
		assertTrue( ent.isDirty(InvoiceAddressMetadata.DOMAIN) );
	}
	
	@Test
	void testDirtyInvoice() {
		InvoiceAddress ent = new InvoiceAddress();
		ent.setInvoice(1);
		assertTrue( ent.isDirty(InvoiceAddressMetadata.INVOICE) );
	}
	
	@Test
	void testDirtyStreetType() {
		InvoiceAddress ent = new InvoiceAddress();
		ent.setStreetType( StreetType.AD );
		assertTrue( ent.isDirty(InvoiceAddressMetadata.STREET_TYPE) );
	}
	
	@Test
	void testDirtyAddress() {
		InvoiceAddress ent = new InvoiceAddress();
		ent.setAddress("1");
		assertTrue( ent.isDirty(InvoiceAddressMetadata.ADDRESS) );
	}
	
	@Test
	void testDirtyNumber() {
		InvoiceAddress ent = new InvoiceAddress();
		ent.setNumber("1");
		assertTrue( ent.isDirty(InvoiceAddressMetadata.NUMBER) );
	}
	
	@Test
	void testDirtyAddress2() {
		InvoiceAddress ent = new InvoiceAddress();
		ent.setAddress2("1");
		assertTrue( ent.isDirty(InvoiceAddressMetadata.ADDRESS2) );
	}
	
	@Test
	void testDirtyZip() {
		InvoiceAddress ent = new InvoiceAddress();
		ent.setZip("1");
		assertTrue( ent.isDirty(InvoiceAddressMetadata.ZIP) );
	}
	
	@Test
	void testDirtyCity() {
		InvoiceAddress ent = new InvoiceAddress();
		ent.setCity("1");
		assertTrue( ent.isDirty(InvoiceAddressMetadata.CITY) );
	}
	
	@Test
	void testDirtyProvince() {
		InvoiceAddress ent = new InvoiceAddress();
		ent.setProvince("1");
		assertTrue( ent.isDirty(InvoiceAddressMetadata.PROVINCE) );
	}
	
	@Test
	void testDirtyGeozone() {
		InvoiceAddress ent = new InvoiceAddress();
		ent.setGeozone(new Geozone().setId(1));
		assertTrue( ent.isDirty(InvoiceAddressMetadata.GEOZONE) );
	}
	
	@Test
	void testDirtyParent() {
		InvoiceAddress ent = new InvoiceAddress();
		ent.setParent(new Geozone().setId(1));
		assertTrue( ent.isDirty(InvoiceAddressMetadata.PARENT) );
	}
	
	@Test
	void testInvoiceAddressEquals() {
		InvoiceAddress a1 = new InvoiceAddress().setId(1);
		assertEquals(a1,a1);
		assertNotEquals(a1,null);
		assertNotEquals(null,a1);
		assertNotEquals(a1,new Object());
		assertNotEquals(a1,new Account());

		InvoiceAddress a2 = new InvoiceAddress().setId(1);
		assertEquals(a1,a2);
		InvoiceAddress a3 = new InvoiceAddress().setId(3);
		assertNotEquals(a1,a3);
	}
	
	@Test
	void testHashcode() {
		List<InvoiceAddress> objects = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			objects.add(new InvoiceAddress().setId(i));
		}
		Set<Integer> hashCodes = new HashSet<>();
		for (InvoiceAddress obj : objects) {
			hashCodes.add(obj.hashCode());
		}
		assertEquals(objects.size(), hashCodes.size(), 10);
	}
	
	@Test
	void testFromRegistryAddress() {
		InvoiceAddress ia = InvoiceAddress.from(null);
		assertNull(ia);
		
		RegistryAddress ra = AonMocker.mock(RegistryAddress.class);
		ia = InvoiceAddress.from(ra);								
		assertNotNull(ia);
		assertNull(ia.getId());
		assertNull(ia.getInvoice());
		assertEquals(ra.getDomain(),ia.getDomain());
		assertEquals(ra.getStreetType(),ia.getStreetType());
		assertEquals(ra.getAddress(),ia.getAddress());
		assertEquals(ra.getNumber(),ia.getNumber());
		assertEquals(ra.getAddress2(),ia.getAddress2());
		assertEquals(ra.getZip(),ia.getZip());
		assertEquals(ra.getCity(),ia.getCity());
		assertEquals(ra.getGeozone().map(Geozone::getName).orElse(null),ia.getProvince());
		assertEquals(ra.getGeozone().orElse(null),ia.getGeozone().orElse(null));
		assertEquals(ra.getParent().orElse(null),ia.getParent().orElse(null));
	}
	
}
