package com.code.aon.web.help.service.drive;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.code.aon.web.help.service.drive.exception.GoogleDriveException;
import com.google.api.services.drive.Drive;

public class DriveServiceTests {

	private static Drive connection;
	
	@BeforeAll
	public static void connect() throws GoogleDriveException {
		DriveServiceTests.connection = DriveService.connect();
	}


	@Test
	public void DriveGetFileNotExistingTest() {
		try {
			DriveService.getFile(connection, "FILE_NOT_EXISTING.MP3", "NOT_A_PARENT");			
		} catch (Exception e) {
			fail("Google Drive API request ended with an exception: " + e.getMessage());
		}
	}

	@Test 
	public void GetFileWithoutParentTest() {
		try {
			DriveService.getFile(connection, null, null);
		} catch (Exception e) {
			fail("Google Drive API request ended with an exception: " + e.getMessage());
		}
	}
	
	@Test
	public void DriveGetFileByIdNotExistingTest() {
		try {
			DriveService.getFileById(connection, "NOT_AN_ID");			
		} catch (Exception e) {
			fail("Google Drive API request ended with an exception: " + e.getMessage());
		}
	}

	
	@Test
	public void ListNotExistingDirectory() {
		try {
			DriveService.ListDirectory(connection,"NOT_EXISTING_DIRECTORY");
		} catch (Exception e) {
			fail("Google Drive API request ended with an exception: " + e.getMessage());
		}
	}
	
	@Test
	public void ListNullDirectory() {
		try {
			DriveService.ListDirectory(connection, null);
		} catch (Exception e) {
			fail("Google Drive API request ended with an exception: " + e.getMessage());
		}
	}
	
}
