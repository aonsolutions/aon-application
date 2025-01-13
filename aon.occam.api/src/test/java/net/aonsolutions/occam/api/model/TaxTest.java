package net.aonsolutions.occam.api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.occam.api.model.metadata.TaxMetadata;
import net.aonsolutions.occam.api.model.metadata.TaxMetadata.TaxMetadataVisitor;
import net.aonsolutions.occam.api.model.type.TaxType;
import net.aonsolutions.occam.api.model.type.VatDeductionType;
import net.aonsolutions.occam.api.model.type.WithholdingType;

class TaxTest {

	@Test
	void testTax() {
		Tax expected = AonMocker.mock(Tax.class);
		Tax actual = new Tax()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setName(expected.getName())
			.setType(expected.getType())
			.setPercentage(expected.getPercentage())
			.setSurcharge(expected.getSurcharge())
			.setStartDate(expected.getStartDate())
			.setVatDeductionType(expected.getVatDeductionType())
			.setWithholdingType(expected.getWithholdingType())
			.setSalesAccount(expected.getSalesAccount().orElse(null))
			.setPurchaseAccount(expected.getPurchaseAccount().orElse(null))
			.setCreationUser(expected.getCreationUser())
			.setCreationDate(expected.getCreationDate())
			.setModificationUser(expected.getModificationUser())
			.setModificationDate(expected.getModificationDate())
			.setDeleted( expected.isDeleted() )
		;
		AonAsserts.assertClassEquals(expected, actual);
	}

	@Test
	void testTaxMetadataVisitor() {
		TaxMetadataVisitor<TaxMetadata> v = new TaxMetadataVisitor<>() {
			 @Override public TaxMetadata visitId() {return TaxMetadata.ID;}
			 @Override public TaxMetadata visitDomain() {return TaxMetadata.DOMAIN;}
			 @Override public TaxMetadata visitName() {return TaxMetadata.NAME;}
			 @Override public TaxMetadata visitTaxType() {return TaxMetadata.TAX_TYPE;}
			 @Override public TaxMetadata visitPercentage() {return TaxMetadata.PERCENTAGE;}
			 @Override public TaxMetadata visitSurcharge() {return TaxMetadata.SURCHARGE;}
			 @Override public TaxMetadata visitStartDate() {return TaxMetadata.START_DATE;}
			 @Override public TaxMetadata visitVatDeductionType() {return TaxMetadata.VAT_DEDUCTION_TYPE;}
			 @Override public TaxMetadata visitWithholdingType() {return TaxMetadata.WITHHOLDING_TYPE;}
			 @Override public TaxMetadata visitSalesAccount() {return TaxMetadata.SALES_ACCOUNT;}
			 @Override public TaxMetadata visitPurchaseAccount() {return TaxMetadata.PURCHASE_ACCOUNT;}
		};
		AonCollectionUtils.stream(TaxMetadata.values())
			.forEach(a -> assertSame(a, a.visit(v)));
	}
	
	@Test
	void testDirtyId() {
		Tax ent = new Tax();
		ent.setId(1);
		assertTrue( ent.isDirty(TaxMetadata.ID) );
	}
	
	@Test
	void testDirtyDomain() {
		Tax ent = new Tax();
		ent.setDomain(1);
		assertTrue( ent.isDirty(TaxMetadata.DOMAIN) );
	}
	
	@Test
	void testDirtyName() {
		Tax ent = new Tax();
		ent.setName("1");
		assertTrue( ent.isDirty(TaxMetadata.NAME) );
	}
	
	@Test
	void testDirtyTaxType() {
		Tax ent = new Tax();
		ent.setType(TaxType.RETENTION);
		assertTrue( ent.isDirty(TaxMetadata.TAX_TYPE) );
	}
	
	@Test
	void testDirtyPercentage() {
		Tax ent = new Tax();
		ent.setPercentage(1);
		assertTrue( ent.isDirty(TaxMetadata.PERCENTAGE) );
	}
	
	@Test
	void testDirtySurcharge() {
		Tax ent = new Tax();
		ent.setSurcharge(1);
		assertTrue( ent.isDirty(TaxMetadata.SURCHARGE) );
	}
	
	@Test
	void testDirtyStartDate() {
		Tax ent = new Tax();
		ent.setStartDate(new Date());
		assertTrue( ent.isDirty(TaxMetadata.START_DATE) );
	}
	
	@Test
	void testDirtyVatDeductionType() {
		Tax ent = new Tax();
		ent.setVatDeductionType(VatDeductionType.WITH_RIGHT);
		assertTrue( ent.isDirty(TaxMetadata.VAT_DEDUCTION_TYPE) );
	}
	
	@Test
	void testDirtyWithholdingType() {
		Tax ent = new Tax();
		ent.setWithholdingType(WithholdingType.FARMER);
		assertTrue( ent.isDirty(TaxMetadata.WITHHOLDING_TYPE) );
	}
	
	@Test
	void testDirtySalesAccount() {
		Tax ent = new Tax();
		ent.setSalesAccount(new Account());
		assertTrue( ent.isDirty(TaxMetadata.SALES_ACCOUNT) );
	}
	
	@Test
	void testDirtyPurchaseAccount() {
		Tax ent = new Tax();
		ent.setPurchaseAccount(new Account());
		assertTrue( ent.isDirty(TaxMetadata.PURCHASE_ACCOUNT) );
	}
	@Test
	void testTaxEquals() {
		Tax a1 = new Tax().setId(1);
		assertEquals(a1,a1);
		assertNotEquals(a1, (Tax) null);
		assertNotEquals(null,a1);
		assertNotEquals(a1,new Object());
		assertNotEquals(a1,new Account());

		Tax a2 = new Tax().setId(1);
		assertEquals(a1,a2);
		Tax a3 = new Tax().setId(3);
		assertNotEquals(a1,a3);
	}

	@Test
	void testHashcode() {
		List<Tax> objects = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			objects.add(new Tax().setId(i));
		}
		Set<Integer> hashCodes = new HashSet<>();
		for (Tax obj : objects) {
			hashCodes.add(obj.hashCode());
		}
		assertEquals(objects.size(), hashCodes.size(), 10);
	}
}
