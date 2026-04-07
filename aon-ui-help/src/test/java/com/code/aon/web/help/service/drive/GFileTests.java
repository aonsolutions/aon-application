package com.code.aon.web.help.service.drive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import com.google.api.services.drive.model.File;

public class GFileTests {
	

	
	@Test 
	public void instantiateFromNull() {
		try {
			GFile.from(null);
		} catch (Exception e) {
			fail("GFile was not properly instanciated from null value, ended with the following exception: " + e.getMessage());
		}
	}
	
	@Test
	public void instantiateFromEmptyFile() {
		try { 
			
			final File file = new File();
			final GFile gfile = GFile.from(file);
			
			assertEquals(null, gfile.getId());
			assertEquals(null, gfile.getDownloadUrl());
			assertEquals(null, gfile.getName());
			assertNotEquals(null, gfile.getParents());
			assertEquals(null, gfile.getPreviewUrl());
			assertEquals(null, gfile.getType());
		
		} catch(Exception e) {
			fail("GFile was not properly instanciated from empty File object, ended with the following exception: " + e.getMessage());
		}		
	}
	
	
	

}
