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

import net.aonsolutions.occam.api.model.metadata.BrandMetadata;
import net.aonsolutions.occam.api.model.metadata.BrandMetadata.BrandMetadataVisitor;

class BrandTest {

	@Test
	void testBrand() {
		Brand expected = AonMocker.mock(Brand.class);
		Brand actual = new Brand()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setName(expected.getName())
			.setDeleted( expected.isDeleted() )
		;
		AonAsserts.assertClassEquals(expected, actual);
	}

	@Test
	void testBrandMetadataVisitor() {
		BrandMetadataVisitor<BrandMetadata> v = new BrandMetadataVisitor<>() {
			 @Override public BrandMetadata visitId() {return BrandMetadata.ID;}
			 @Override public BrandMetadata visitDomain() {return BrandMetadata.DOMAIN;}
			 @Override public BrandMetadata visitName() {return BrandMetadata.NAME;}
		};
		AonCollectionUtils.stream(BrandMetadata.values())
			.forEach(a -> assertSame(a, a.visit(v)));
	}
	
	@Test
	void testDirtyId() {
		Brand ent = new Brand();
		ent.setId(1);
		assertTrue( ent.isDirty(BrandMetadata.ID) );
	}
	
	@Test
	void testDirtyDomain() {
		Brand ent = new Brand();
		ent.setDomain(1);
		assertTrue( ent.isDirty(BrandMetadata.DOMAIN) );
	}
	
	@Test
	void testDirtyName() {
		Brand ent = new Brand();
		ent.setName("1");
		assertTrue( ent.isDirty(BrandMetadata.NAME) );
	}
	@Test
	void testBrandEquals() {
		Brand a1 = new Brand().setId(1);
		assertEquals(a1,a1);
		assertNotEquals(a1, (Brand) null);
		assertNotEquals(null,a1);
		assertNotEquals(a1,new Object());
		assertNotEquals(a1,new Account());

		Brand a2 = new Brand().setId(1);
		assertEquals(a1,a2);
		Brand a3 = new Brand().setId(3);
		assertNotEquals(a1,a3);
	}

	@Test
	void testHashcode() {
		List<Brand> objects = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			objects.add(new Brand().setId(i));
		}
		Set<Integer> hashCodes = new HashSet<>();
		for (Brand obj : objects) {
			hashCodes.add(obj.hashCode());
		}
		assertEquals(objects.size(), hashCodes.size(), 10);
	}
}
