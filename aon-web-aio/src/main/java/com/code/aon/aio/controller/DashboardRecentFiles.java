package com.code.aon.aio.controller;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.gwt.document.client.Utils;

public class DashboardRecentFiles implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	String name;
	String type;
	String category;
	String	date;
	long size;
	String sizeString;
	String icon;
	
	public DashboardRecentFiles(){
		//Constructor
	}
	public DashboardRecentFiles(String name, String type , String  category , long size, String date) {
		this.type = type;
		this.name = name;
		this.size = size;
		this.date = date;
		this.category = category;	
		
	}
	
	public void settype(String type) {
		this.type = type;
	}
	public void setname(String name) {
		this.name = name;
		
		
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
			this.icon = Utils.icon(t.getName());
			
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

}
