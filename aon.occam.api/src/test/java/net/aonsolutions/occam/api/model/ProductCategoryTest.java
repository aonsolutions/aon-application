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

import net.aonsolutions.occam.api.model.metadata.ProductCategoryMetadata;
import net.aonsolutions.occam.api.model.metadata.ProductCategoryMetadata.ProductCategoryMetadataVisitor;

class ProductCategoryTest {

	@Test
	void testProductCategory() {
		ProductCategory expected = AonMocker.mock(ProductCategory.class);
		ProductCategory actual = new ProductCategory()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setName(expected.getName())
			.setDetail(expected.getDetail())
			.setDetail2(expected.getDetail2())
			.setDetail3(expected.getDetail3())
			.setDeleted( expected.isDeleted() )
		;
		AonAsserts.assertClassEquals(expected, actual);
	}

	@Test
	void testProductCategoryMetadataVisitor() {
		ProductCategoryMetadataVisitor<ProductCategoryMetadata> v = new ProductCategoryMetadataVisitor<>() {
			 @Override public ProductCategoryMetadata visitId() {return ProductCategoryMetadata.ID;}
			 @Override public ProductCategoryMetadata visitDomain() {return ProductCategoryMetadata.DOMAIN;}
			 @Override public ProductCategoryMetadata visitName() {return ProductCategoryMetadata.NAME;}
			 @Override public ProductCategoryMetadata visitDetail() {return ProductCategoryMetadata.DETAIL;}
			 @Override public ProductCategoryMetadata visitDetail2() {return ProductCategoryMetadata.DETAIL2;}
			 @Override public ProductCategoryMetadata visitDetail3() {return ProductCategoryMetadata.DETAIL3;}
		};
		AonCollectionUtils.stream(ProductCategoryMetadata.values())
			.forEach(a -> assertSame(a, a.visit(v)));
	}
	
	@Test
	void testDirtyId() {
		ProductCategory ent = new ProductCategory();
		ent.setId(1);
		assertTrue( ent.isDirty(ProductCategoryMetadata.ID) );
	}
	
	@Test
	void testDirtyDomain() {
		ProductCategory ent = new ProductCategory();
		ent.setDomain(1);
		assertTrue( ent.isDirty(ProductCategoryMetadata.DOMAIN) );
	}
	
	@Test
	void testDirtyName() {
		ProductCategory ent = new ProductCategory();
		ent.setName("1");
		assertTrue( ent.isDirty(ProductCategoryMetadata.NAME) );
	}
	
	@Test
	void testDirtyDetail() {
		ProductCategory ent = new ProductCategory();
		ent.setDetail("1");
		assertTrue( ent.isDirty(ProductCategoryMetadata.DETAIL) );
	}
	
	@Test
	void testDirtyDetail2() {
		ProductCategory ent = new ProductCategory();
		ent.setDetail2("1");
		assertTrue( ent.isDirty(ProductCategoryMetadata.DETAIL2) );
	}
	
	@Test
	void testDirtyDetail3() {
		ProductCategory ent = new ProductCategory();
		ent.setDetail3("1");
		assertTrue( ent.isDirty(ProductCategoryMetadata.DETAIL3) );
	}
	@Test
	void testProductCategoryEquals() {
		ProductCategory a1 = new ProductCategory().setId(1);
		assertEquals(a1,a1);
		assertNotEquals(a1, (ProductCategory) null);
		assertNotEquals(null,a1);
		assertNotEquals(a1,new Object());
		assertNotEquals(a1,new Account());

		ProductCategory a2 = new ProductCategory().setId(1);
		assertEquals(a1,a2);
		ProductCategory a3 = new ProductCategory().setId(3);
		assertNotEquals(a1,a3);
	}

	@Test
	void testHashcode() {
		List<ProductCategory> objects = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			objects.add(new ProductCategory().setId(i));
		}
		Set<Integer> hashCodes = new HashSet<>();
		for (ProductCategory obj : objects) {
			hashCodes.add(obj.hashCode());
		}
		assertEquals(objects.size(), hashCodes.size(), 10);
	}
}
