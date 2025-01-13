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

import net.aonsolutions.occam.api.model.metadata.TagMetadata;
import net.aonsolutions.occam.api.model.metadata.TagMetadata.TagMetadataVisitor;
import net.aonsolutions.occam.api.model.type.TagType;

class TagTest {

	@Test
	void testTag() {
		Tag expected = AonMocker.mock(Tag.class);
		Tag actual = new Tag()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setName(expected.getName())
			.setType(expected.getType())
			.setColor(expected.getColor())
			.setDeleted( expected.isDeleted() )
		;
		AonAsserts.assertClassEquals(expected, actual);
	}

	@Test
	void testTagMetadataVisitor() {
		TagMetadataVisitor<TagMetadata> v = new TagMetadataVisitor<>() {
			 @Override public TagMetadata visitId() {return TagMetadata.ID;}
			 @Override public TagMetadata visitDomain() {return TagMetadata.DOMAIN;}
			 @Override public TagMetadata visitName() {return TagMetadata.NAME;}
			 @Override public TagMetadata visitType() {return TagMetadata.TYPE;}
			 @Override public TagMetadata visitColor() {return TagMetadata.COLOR;}
		};
		AonCollectionUtils.stream(TagMetadata.values())
			.forEach(a -> assertSame(a, a.visit(v)));
	}
	
	@Test
	void testDirtyId() {
		Tag ent = new Tag();
		ent.setId(1);
		assertTrue( ent.isDirty(TagMetadata.ID) );
	}
	
	@Test
	void testDirtyDomain() {
		Tag ent = new Tag();
		ent.setDomain(1);
		assertTrue( ent.isDirty(TagMetadata.DOMAIN) );
	}
	
	@Test
	void testDirtyName() {
		Tag ent = new Tag();
		ent.setName("1");
		assertTrue( ent.isDirty(TagMetadata.NAME) );
	}
	
	@Test
	void testDirtyType() {
		Tag ent = new Tag();
		ent.setType(TagType.CERTIFICATE);
		assertTrue( ent.isDirty(TagMetadata.TYPE) );
	}
	
	@Test
	void testDirtyColor() {
		Tag ent = new Tag();
		ent.setColor("1");
		assertTrue( ent.isDirty(TagMetadata.COLOR) );
	}
	@Test
	void testTagEquals() {
		Tag a1 = new Tag().setId(1);
		assertEquals(a1,a1);
		assertNotEquals(a1, (Tag) null);
		assertNotEquals(null,a1);
		assertNotEquals(a1,new Object());
		assertNotEquals(a1,new Account());

		Tag a2 = new Tag().setId(1);
		assertEquals(a1,a2);
		Tag a3 = new Tag().setId(3);
		assertNotEquals(a1,a3);
	}

	@Test
	void testHashcode() {
		List<Tag> objects = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			objects.add(new Tag().setId(i));
		}
		Set<Integer> hashCodes = new HashSet<>();
		for (Tag obj : objects) {
			hashCodes.add(obj.hashCode());
		}
		assertEquals(objects.size(), hashCodes.size(), 10);
	}
}
