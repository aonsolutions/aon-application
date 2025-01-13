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

import net.aonsolutions.occam.api.model.metadata.ProductMetadata;
import net.aonsolutions.occam.api.model.metadata.ProductMetadata.ProductMetadataVisitor;
import net.aonsolutions.occam.api.model.type.ProductKind;
import net.aonsolutions.occam.api.model.type.ProductStatus;
import net.aonsolutions.occam.api.model.type.ProductType;

class ProductTest {

	@Test
	void testProduct() {
		Product expected = AonMocker.mock(Product.class);
		Product actual = new Product()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setName(expected.getName())
			.setCode(expected.getCode())
			.setKind(expected.getKind())
			.setBrand(expected.getBrand().orElse(null))
			.setCategory(expected.getCategory().orElse(null))
			.setInventoriable(expected.isInventoriable())
			.setSerializable(expected.isSerializable())
			.setLotable(expected.isLotable())
			.setStatus(expected.getStatus())
			.setVat(expected.getVat().orElse(null))
			.setRetention(expected.getRetention().orElse(null))
			.setType(expected.getType())
			.setManufactured(expected.isManufactured())
			.setComposition(expected.isComposition())
			.setCompositionPrice(expected.isCompositionPrice())
			.setPackaged(expected.isPackaged())
			.setSalesAccount(expected.getSalesAccount().orElse(null))
			.setPurchaseAccount(expected.getPurchaseAccount().orElse(null))
			.setPerishable(expected.isPerishable())
			.setDaysToExpire(expected.getDaysToExpire())
			.setCreationUser(expected.getCreationUser())
			.setCreationDate(expected.getCreationDate())
			.setModificationUser(expected.getModificationUser())
			.setModificationDate(expected.getModificationDate())
			.setDeleted( expected.isDeleted() )
		;
		AonAsserts.assertClassEquals(expected, actual);
	}

	@Test
	void testProductMetadataVisitor() {
		ProductMetadataVisitor<ProductMetadata> v = new ProductMetadataVisitor<>() {
			 @Override public ProductMetadata visitId() {return ProductMetadata.ID;}
			 @Override public ProductMetadata visitDomain() {return ProductMetadata.DOMAIN;}
			 @Override public ProductMetadata visitName() {return ProductMetadata.NAME;}
			 @Override public ProductMetadata visitCode() {return ProductMetadata.CODE;}
			 @Override public ProductMetadata visitKind() {return ProductMetadata.KIND;}
			 @Override public ProductMetadata visitBrand() {return ProductMetadata.BRAND;}
			 @Override public ProductMetadata visitCategory() {return ProductMetadata.CATEGORY;}
			 @Override public ProductMetadata visitInventoriable() {return ProductMetadata.INVENTORIABLE;}
			 @Override public ProductMetadata visitSerializable() {return ProductMetadata.SERIALIZABLE;}
			 @Override public ProductMetadata visitLotable() {return ProductMetadata.LOTABLE;}
			 @Override public ProductMetadata visitStatus() {return ProductMetadata.STATUS;}
			 @Override public ProductMetadata visitVat() {return ProductMetadata.VAT;}
			 @Override public ProductMetadata visitRetention() {return ProductMetadata.RETENTION;}
			 @Override public ProductMetadata visitType() {return ProductMetadata.TYPE;}
			 @Override public ProductMetadata visitManufactured() {return ProductMetadata.MANUFACTURED;}
			 @Override public ProductMetadata visitComposition() {return ProductMetadata.COMPOSITION;}
			 @Override public ProductMetadata visitCompositionPrice() {return ProductMetadata.COMPOSITION_PRICE;}
			 @Override public ProductMetadata visitPackaged() {return ProductMetadata.PACKAGED;}
			 @Override public ProductMetadata visitSalesAccount() {return ProductMetadata.SALES_ACCOUNT;}
			 @Override public ProductMetadata visitPurchaseAccount() {return ProductMetadata.PURCHASE_ACCOUNT;}
			 @Override public ProductMetadata visitPerishable() {return ProductMetadata.PERISHABLE;}
			 @Override public ProductMetadata visitDaysToExpire() {return ProductMetadata.DAYS_TO_EXPIRE;}
		};
		AonCollectionUtils.stream(ProductMetadata.values())
			.forEach(a -> assertSame(a, a.visit(v)));
	}
	
	@Test
	void testDirtyId() {
		Product ent = new Product();
		ent.setId(1);
		assertTrue( ent.isDirty(ProductMetadata.ID) );
	}
	
	@Test
	void testDirtyDomain() {
		Product ent = new Product();
		ent.setDomain(1);
		assertTrue( ent.isDirty(ProductMetadata.DOMAIN) );
	}
	
	@Test
	void testDirtyName() {
		Product ent = new Product();
		ent.setName("1");
		assertTrue( ent.isDirty(ProductMetadata.NAME) );
	}
	
	@Test
	void testDirtyCode() {
		Product ent = new Product();
		ent.setCode("1");
		assertTrue( ent.isDirty(ProductMetadata.CODE) );
	}
	
	@Test
	void testDirtyKind() {
		Product ent = new Product();
		ent.setKind(ProductKind.PURCHASE);
		assertTrue( ent.isDirty(ProductMetadata.KIND) );
	}
	
	@Test
	void testDirtyBrand() {
		Product ent = new Product();
		ent.setBrand( new Brand());
		assertTrue( ent.isDirty(ProductMetadata.BRAND) );
	}
	
	@Test
	void testDirtyCategory() {
		Product ent = new Product();
		ent.setCategory( new ProductCategory());
		assertTrue( ent.isDirty(ProductMetadata.CATEGORY) );
	}
	
	@Test
	void testDirtyInventoriable() {
		Product ent = new Product();
		ent.setInventoriable(true);
		assertTrue( ent.isDirty(ProductMetadata.INVENTORIABLE) );
	}
	
	@Test
	void testDirtySerializable() {
		Product ent = new Product();
		ent.setSerializable(true);
		assertTrue( ent.isDirty(ProductMetadata.SERIALIZABLE) );
	}
	
	@Test
	void testDirtyLotable() {
		Product ent = new Product();
		ent.setLotable(true);
		assertTrue( ent.isDirty(ProductMetadata.LOTABLE) );
	}
	
	@Test
	void testDirtyStatus() {
		Product ent = new Product();
		ent.setStatus(ProductStatus.ACTIVE);
		assertTrue( ent.isDirty(ProductMetadata.STATUS) );
	}
	
	@Test
	void testDirtyVat() {
		Product ent = new Product();
		ent.setVat( new Tax());
		assertTrue( ent.isDirty(ProductMetadata.VAT) );
	}
	
	@Test
	void testDirtyRetention() {
		Product ent = new Product();
		ent.setRetention(new Tax());
		assertTrue( ent.isDirty(ProductMetadata.RETENTION) );
	}
	
	@Test
	void testDirtyType() {
		Product ent = new Product();
		ent.setType(ProductType.AUXILIARY);
		assertTrue( ent.isDirty(ProductMetadata.TYPE) );
	}
	
	@Test
	void testDirtyManufactured() {
		Product ent = new Product();
		ent.setManufactured(true);
		assertTrue( ent.isDirty(ProductMetadata.MANUFACTURED) );
	}
	
	@Test
	void testDirtyComposition() {
		Product ent = new Product();
		ent.setComposition(true);
		assertTrue( ent.isDirty(ProductMetadata.COMPOSITION) );
	}
	
	@Test
	void testDirtyCompositionPrice() {
		Product ent = new Product();
		ent.setCompositionPrice(true);
		assertTrue( ent.isDirty(ProductMetadata.COMPOSITION_PRICE) );
	}
	
	@Test
	void testDirtyPackaged() {
		Product ent = new Product();
		ent.setPackaged(true);
		assertTrue( ent.isDirty(ProductMetadata.PACKAGED) );
	}
	
	@Test
	void testDirtySalesAccount() {
		Product ent = new Product();
		ent.setSalesAccount( new Account());
		assertTrue( ent.isDirty(ProductMetadata.SALES_ACCOUNT) );
	}
	
	@Test
	void testDirtyPurchaseAccount() {
		Product ent = new Product();
		ent.setPurchaseAccount(new Account());
		assertTrue( ent.isDirty(ProductMetadata.PURCHASE_ACCOUNT) );
	}
	
	@Test
	void testDirtyPerishable() {
		Product ent = new Product();
		ent.setPerishable(true);
		assertTrue( ent.isDirty(ProductMetadata.PERISHABLE) );
	}
	
	@Test
	void testDirtyDaysToExpire() {
		Product ent = new Product();
		ent.setDaysToExpire(1);
		assertTrue( ent.isDirty(ProductMetadata.DAYS_TO_EXPIRE) );
	}
	@Test
	void testProductEquals() {
		Product a1 = new Product().setId(1);
		assertEquals(a1,a1);
		assertNotEquals(a1, (Product) null);
		assertNotEquals(null,a1);
		assertNotEquals(a1,new Object());
		assertNotEquals(a1,new Account());

		Product a2 = new Product().setId(1);
		assertEquals(a1,a2);
		Product a3 = new Product().setId(3);
		assertNotEquals(a1,a3);
	}

	@Test
	void testHashcode() {
		List<Product> objects = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			objects.add(new Product().setId(i));
		}
		Set<Integer> hashCodes = new HashSet<>();
		for (Product obj : objects) {
			hashCodes.add(obj.hashCode());
		}
		assertEquals(objects.size(), hashCodes.size(), 10);
	}
}
