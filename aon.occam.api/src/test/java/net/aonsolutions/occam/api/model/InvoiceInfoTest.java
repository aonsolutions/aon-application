package net.aonsolutions.occam.api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.occam.api.model.metadata.InvoiceInfoMetadata;
import net.aonsolutions.occam.api.model.metadata.MetadataVisitor.InvoiceInfoMetadataVisitor;
import net.aonsolutions.occam.api.model.type.InvoiceCommunicationStatus;
import net.aonsolutions.occam.api.model.type.InvoiceCommunicationType;

class InvoiceInfoTest {

	@Test
	void testInvoiceInfo() {
		InvoiceInfo expected = AonMocker.mock(InvoiceInfo.class);
		InvoiceInfo actual = new InvoiceInfo()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setInvoice(expected.getInvoice())
			.setType(expected.getType())
			.setStatus(expected.getStatus())
			.setCreationUser(expected.getCreationUser())
			.setCreationDate(expected.getCreationDate())
			.setModificationUser(expected.getModificationUser())
			.setModificationDate(expected.getModificationDate())
			.setDeleted( expected.isDeleted() )
		;
		AonAsserts.assertClassEquals(expected, actual);
	}

	@Test
	void testInvoiceInfoMetadataVisitor() {
		InvoiceInfoMetadataVisitor<InvoiceInfoMetadata> v = new InvoiceInfoMetadataVisitor<>() {
			 @Override public InvoiceInfoMetadata visitId() {return InvoiceInfoMetadata.ID;}
			 @Override public InvoiceInfoMetadata visitDomain() {return InvoiceInfoMetadata.DOMAIN;}
			 @Override public InvoiceInfoMetadata visitInvoice() {return InvoiceInfoMetadata.INVOICE;}
			 @Override public InvoiceInfoMetadata visitType() {return InvoiceInfoMetadata.TYPE;}
			 @Override public InvoiceInfoMetadata visitStatus() {return InvoiceInfoMetadata.STATUS;}
		};
		AonCollectionUtils.stream(InvoiceInfoMetadata.values())
			.forEach(a -> assertSame(a, a.visit(v)));
	}
	
	@Test
	void testDirtyId() {
		InvoiceInfo ent = new InvoiceInfo();
		ent.setId(1);
		assertTrue( ent.isDirty(InvoiceInfoMetadata.ID) );
	}
	
	@Test
	void testDirtyDomain() {
		InvoiceInfo ent = new InvoiceInfo();
		ent.setDomain(1);
		assertTrue( ent.isDirty(InvoiceInfoMetadata.DOMAIN) );
	}
	
	@Test
	void testDirtyInvoice() {
		InvoiceInfo ent = new InvoiceInfo();
		ent.setInvoice(1);
		assertTrue( ent.isDirty(InvoiceInfoMetadata.INVOICE) );
	}
	
	@Test
	void testDirtyType() {
		InvoiceInfo ent = new InvoiceInfo();
		ent.setType(InvoiceCommunicationType.EMAIL);
		assertTrue( ent.isDirty(InvoiceInfoMetadata.TYPE) );
	}
	
	@Test
	void testDirtyStatus() {
		InvoiceInfo ent = new InvoiceInfo();
		ent.setStatus( InvoiceCommunicationStatus.CANCELLED );
		assertTrue( ent.isDirty(InvoiceInfoMetadata.STATUS) );
	}
	@Test
	void testInvoiceInfoEquals() {
		InvoiceInfo a1 = new InvoiceInfo().setId(1);
		assertEquals(a1,a1);
		assertNotEquals(a1,null);
		assertNotEquals(null,a1);
		assertNotEquals(a1,new Object());
		assertNotEquals(a1,new Account());

		InvoiceInfo a2 = new InvoiceInfo().setId(1);
		assertEquals(a1,a2);
		InvoiceInfo a3 = new InvoiceInfo().setId(3);
		assertNotEquals(a1,a3);
	}

	@Test
	void testHashcode() {
		List<InvoiceInfo> objects = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			objects.add(new InvoiceInfo().setId(i));
		}
		Set<Integer> hashCodes = new HashSet<>();
		for (InvoiceInfo obj : objects) {
			hashCodes.add(obj.hashCode());
		}
		assertEquals(objects.size(), hashCodes.size(), 10);
	}
}
