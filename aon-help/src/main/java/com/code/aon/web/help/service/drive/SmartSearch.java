package com.code.aon.web.help.service.drive;

import java.util.LinkedList;

import com.code.aon.web.help.service.drive.exception.GoogleDriveException;
import com.google.api.services.drive.Drive;

public class SmartSearch {

	
	public static void searchVideosMatching(String name) {
		
		try {
			Drive connection = DriveService.connect();
			
			DriveService.createTree(connection);
			
			/*
			LinkedList<GFile> results = DriveService.searchVideoMatching(connection, name);
			
			results.forEach(f -> {
				System.out.println(f.getName());
				System.out.println(f.getParents() + "\n"); 
			});
			*/
		
		
		} catch (GoogleDriveException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public static void searchPdfMatching(String name) {
		
		
		
		
	}
	
	public static void searchPdfContaining(String name) {
		
		
		
	}
	

	/**
	 *  ECE
	 * --------------------------------------
	 */
	public static void main(String[] args) {
		searchVideosMatching("facturas");
	}
}
