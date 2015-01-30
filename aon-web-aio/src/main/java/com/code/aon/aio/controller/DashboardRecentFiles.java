package com.code.aon.aio.controller;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.MimeType;

public class DashboardRecentFiles implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	Integer id;
	String name;
	String type;
	String category;
	String	date;
	long size;
	String sizeString;
	String icon;
	Byte mimetype;
	String driveId;
	
	public DashboardRecentFiles(){
		//Constructor
	}
	public DashboardRecentFiles(Integer id, String name, String type , String  category , long size, String date) {
		this.id = id;
		this.type = type;
		this.name = name;
		this.size = size;
		this.date = date;
		this.category = category;	
		
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void settype(String type) {
		this.type = type;
	}
	public void setname(String name) {
		this.name = name;
	}

	public String getDriveId() {
		return driveId;
	}
	
	public void setDriveId(String driveId) {
		this.driveId = driveId;
	}
	
	public Byte getMimetype() {
		return mimetype;
	}
	
	public void setMimetype(Byte mimetype) {
		this.mimetype = mimetype;
	}
	
	public void setsize(long size) {
		this.size = size;
	}
	public void setcategory(String category){
		this.category = category;
	}
	public String gettype() {
		return type;		
	}
	public String getname() {
		return name;
	}
	public long getsize() {
		return size;
	}
	public String getcategory(){
		return category;
	}
	public String getDate() {
		
		return date;
	}
	public void setDate(String date) {
		
		this.date = formatDate(date);
	}
	public String getSizeString() {
		return FileUtils.byteCountToDisplaySize(size); 
	}
	public void setSizeString(String sizeString) {
		this.sizeString = sizeString;
	}
	
	
	public String getIcon() {
		return icon;
	}
	public void setIcon(Integer icon) {		
		if (icon.equals(-1))
			this.icon = "aon-icon-google-drive-unknown";

		else {
			
			short type = icon.shortValue();
			MimeType t = MimeType.values()[type];
			this.icon = icon(t.getName());
			
			/*if (t.getName().equals("application/pdf"))
				this.icon = "aon-icon-google-drive-pdf";
			else if (t.getName().equals("aapplication/msword"))
				this.icon = "aon-icon-google-drive-word";
			else if (isImage(t.getName()))
				this.icon = "aon-icon-google-drive-image";
			else
				this.icon = "aon-icon-google-drive-unknown";*/
		}

	}
	
	private Boolean isImage(String name) {
		Integer pos  = name.indexOf("/");

		return name.substring(0,pos).equals("image");
	}
	private String formatDate( String date){
		Integer pos = date.indexOf("-");
		String año = date.substring(0,pos);
		Integer pos1 = date.substring(pos+1).indexOf("-");
		String mes = date.substring(pos+1).substring(0,pos1);
		String dia = date.substring(pos+1).substring(pos1+1);
		return dia+"-"+mes+"-"+año;
	}
	
	private String icon(String m) {
		if(m.contains("audio")) return "aon-icon-google-drive-audio";
		if(m.contains("image")) return "aon-icon-google-drive-image";
		if(m.contains("video")) return "aon-icon-google-drive-mov";
		switch (m) {
		case "application/vnd.google-apps.audio":return "aon-icon-google-drive-audio";
		case "application/vnd.google-apps.document":return "aon-icon-google-drive-docs";
		case "application/vnd.google-apps.drawing":return "aon-icon-google-drive-drawing";
		case "application/vnd.google-apps.folder":return "aon-icon-google-drive-folder";
		case "application/vnd.google-apps.form":return "aon-icon-google-drive-form";
		case "application/vnd.google-apps.photo":return "aon-icon-google-drive-image";
		case "application/vnd.google-apps.presentation":return "aon-icon-google-drive-presentation";
		case "application/vnd.google-apps.spreadsheet":return "aon-icon-google-drive-calc";
		case "application/vnd.google-apps.video":return "aon-icon-google-drive-mov";
		
		case "application/msword":
		case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
		case "application/vnd.openxmlformats-officedocument.wordprocessingml.template":
		case "application/vnd.ms-word.document.macroEnabled.12":
		case "application/vnd.ms-word.template.macroEnabled.12":
			return "aon-icon-google-drive-word";
		case "application/vnd.ms-excel": 
		case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet": 
		case "application/vnd.openxmlformats-officedocument.spreadsheetml.template": 
		case "application/vnd.ms-excel.sheet.macroEnabled.12": 
		case "application/vnd.ms-excel.template.macroEnabled.12": 
		case "application/vnd.ms-excel.addin.macroEnabled.12": 
		case "application/vnd.ms-excel.sheet.binary.macroEnabled.12": 
			return "aon-icon-google-drive-excel";
		case "application/vnd.ms-powerpoint": 
		case "application/vnd.openxmlformats-officedocument.presentationml.presentation": 
		case "application/vnd.openxmlformats-officedocument.presentationml.template": 
		case "application/vnd.openxmlformats-officedocument.presentationml.slideshow": 
		case "application/vnd.ms-powerpoint.addin.macroEnabled.12": 
		case "application/vnd.ms-powerpoint.presentation.macroEnabled.12": 
		case "application/vnd.ms-powerpoint.slideshow.macroEnabled.12": 
			return "aon-icon-google-drive-power-point";
		case "application/pdf": return "aon-icon-google-drive-pdf-sinfondo";
		
		case "application/x-rar-compressed": return "aon-icon-google-drive-zip";
		case "application/zip": return "aon-icon-google-drive-zip";
		default:
			return "aon-icon-google-drive-unknown";
		}
	}

}
