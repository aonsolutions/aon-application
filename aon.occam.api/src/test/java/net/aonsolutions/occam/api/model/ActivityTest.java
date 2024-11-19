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

import net.aonsolutions.occam.api.model.metadata.ActivityMetadata;
import net.aonsolutions.occam.api.model.metadata.ActivityMetadata.ActivityMetadataVisitor;
import net.aonsolutions.occam.api.model.type.IRPFRegime;
import net.aonsolutions.occam.api.model.type.VATExemptionCause;
import net.aonsolutions.occam.api.model.type.VATRegime;

class ActivityTest {

	@Test
	void testActivity() {
		Activity expected = AonMocker.mock(Activity.class);
		Activity actual = new Activity()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setDescription(expected.getDescription())
			.setIae(expected.getIae().orElse(null))
			.setCnae(expected.getCnae().orElse(null))
			.setSurcharge(expected.isSurcharge())
			.setVatRegime(expected.getVatRegime())
			.setVatExemptionCause(expected.getVatExemptionCause())
			.setIrpfRegime(expected.getIrpfRegime())
			.setStartDate(expected.getStartDate())
			.setEndDate(expected.getEndDate())
			.setMain(expected.isMain())
			.setDeleted(expected.isDeleted())
			.setSelected(expected.isSelected())
		;
		AonAsserts.assertClassEquals(expected, actual);
	}

	@Test
	void testActivityMetadataVisitor() {
		ActivityMetadataVisitor<ActivityMetadata> v = new ActivityMetadataVisitor<>() {
			 @Override public ActivityMetadata visitId() {return ActivityMetadata.ID;}
			 @Override public ActivityMetadata visitDomain() {return ActivityMetadata.DOMAIN;}
			 @Override public ActivityMetadata visitDescription() {return ActivityMetadata.DESCRIPTION;}
			 @Override public ActivityMetadata visitIae() {return ActivityMetadata.IAE;}
			 @Override public ActivityMetadata visitCnae() {return ActivityMetadata.CNAE;}
			 @Override public ActivityMetadata visitSurcharge() {return ActivityMetadata.SURCHARGE;}
			 @Override public ActivityMetadata visitVatRegime() {return ActivityMetadata.VAT_REGIME;}
			 @Override public ActivityMetadata visitVatExemptionCause() {return ActivityMetadata.VAT_EXEMPTION_CAUSE;}
			 @Override public ActivityMetadata visitIrpfRegime() {return ActivityMetadata.IRPF_REGIME;}
			 @Override public ActivityMetadata visitStartDate() {return ActivityMetadata.START_DATE;}
			 @Override public ActivityMetadata visitEndDate() {return ActivityMetadata.END_DATE;}
			 @Override public ActivityMetadata visitMain() {return ActivityMetadata.MAIN;}
		};
		AonCollectionUtils.stream(ActivityMetadata.values())
			.forEach(a -> assertSame(a, a.visit(v)));
	}
	
	@Test
	void testDirtyId() {
		Activity ent = new Activity();
		ent.setId(1);
		assertTrue( ent.isDirty(ActivityMetadata.ID) );
	}
	
	@Test
	void testDirtyDomain() {
		Activity ent = new Activity();
		ent.setDomain(1);
		assertTrue( ent.isDirty(ActivityMetadata.DOMAIN) );
	}
	
	@Test
	void testDirtyDescription() {
		Activity ent = new Activity();
		ent.setDescription("1");
		assertTrue( ent.isDirty(ActivityMetadata.DESCRIPTION) );
	}
	
	@Test
	void testDirtyIae() {
		Activity ent = new Activity();
		ent.setIae( new Iae());
		assertTrue( ent.isDirty(ActivityMetadata.IAE) );
	}
	
	@Test
	void testDirtyCnae() {
		Activity ent = new Activity();
		ent.setCnae(new Cnae());
		assertTrue( ent.isDirty(ActivityMetadata.CNAE) );
	}
	
	@Test
	void testDirtySurcharge() {
		Activity ent = new Activity();
		ent.setSurcharge(true);
		assertTrue( ent.isDirty(ActivityMetadata.SURCHARGE) );
	}
	
	@Test
	void testDirtyVatRegime() {
		Activity ent = new Activity();
		ent.setVatRegime( VATRegime.EXEMPT );
		assertTrue( ent.isDirty(ActivityMetadata.VAT_REGIME) );
	}
	
	@Test
	void testDirtyVatExemptionCause() {
		Activity ent = new Activity();
		ent.setVatExemptionCause(VATExemptionCause.E3);
		assertTrue( ent.isDirty(ActivityMetadata.VAT_EXEMPTION_CAUSE) );
	}
	
	@Test
	void testDirtyRetentionRegime() {
		Activity ent = new Activity();
		ent.setIrpfRegime( IRPFRegime.NORMAL );
		assertTrue( ent.isDirty(ActivityMetadata.IRPF_REGIME) );
	}
	
	@Test
	void testDirtyStartDate() {
		Activity ent = new Activity();
		ent.setStartDate(new Date());
		assertTrue( ent.isDirty(ActivityMetadata.START_DATE) );
	}
	
	@Test
	void testDirtyEndDate() {
		Activity ent = new Activity();
		ent.setEndDate(new Date());
		assertTrue( ent.isDirty(ActivityMetadata.END_DATE) );
	}
	
	@Test
	void testDirtyPrincipal() {
		Activity ent = new Activity();
		ent.setMain(true);
		assertTrue( ent.isDirty(ActivityMetadata.MAIN) );
	}

	@Test
	void testActivityEquals() {
		Activity a1 = new Activity().setId(1);
		assertEquals(a1,a1);
		assertNotEquals(a1,null);
		assertNotEquals(null,a1);
		assertNotEquals(a1,new Object());
		assertNotEquals(a1,new Activity());
		
		Activity a2 = new Activity().setId(1);
		assertEquals(a1,a2);
		
		Activity a3 = new Activity().setId(3);
		assertNotEquals(a1,a3);
	}

	
	@Test
	void testHashcode() {
	    List<Activity> objects = new ArrayList<>();
	    for (int i = 0; i < 1000; i++) {
	        objects.add(new Activity().setId(i));
	    }
	    Set<Integer> hashCodes = new HashSet<>();
	    for (Activity obj : objects) {
	        hashCodes.add(obj.hashCode());
	    }
	    assertEquals(objects.size(), hashCodes.size(), 10);
	}
}
