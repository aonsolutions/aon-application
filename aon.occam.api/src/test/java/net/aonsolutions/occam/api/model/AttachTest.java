package net.aonsolutions.occam.api.model;

import org.junit.jupiter.api.Test;

class AttachTest {
	
	@Test
	void testAttach() {
		Attach expected = AonMocker.mock(Attach.class);
		Attach actual = new Attach()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setScope(expected.getScope())
			.setAttachType(expected.getAttachType())
			.setAttachModule(expected.getAttachModule())
			.setAttachURL(expected.getAttachURL())
			.setMimeType(expected.getMimeType())
			.setDescription(expected.getDescription())
			.setData(expected.getData())
			.setDate(expected.getDate())
			.setType(expected.getType())
			.setDriveId(expected.getDriveId())
			.setConfidential(expected.isConfidential())
			.setCategory(expected.getCategory())
			.setDparentId(expected.getDparentId())
			.setSourceBatch(expected.getSourceBatch())
			.setSourceType(expected.getSourceType())
			.setIcon(expected.getIcon())
			.setMd5(expected.getMd5())
			.setIsDrive(expected.getIsDrive())
			.setCreationUser(expected.getCreationUser())
			.setCreationDate(expected.getCreationDate())
			.setModificationUser(expected.getModificationUser())
			.setModificationDate(expected.getModificationDate())			
		;
		AonAsserts.assertClassEquals(expected, actual);
	}
	

	
}
