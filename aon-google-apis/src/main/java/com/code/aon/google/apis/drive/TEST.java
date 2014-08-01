package com.code.aon.google.apis.drive;

import java.sql.SQLException;

import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.DriveData;
import com.code.aon.google.apis.FileInfo;

public class TEST {
	
	public static void main(String[] args) throws SQLException {
		String key= "novus.aibanez.net";
		DriveData dd = DatabaseSync.getDomainFiles(key);
		for (FileInfo attach : dd.getAttachs()) {
			System.out.println(attach.getTitle()+"---"+attach.getType());
		}
	}
	

}
