package com.esferalia.aon.gwt.viewer.shared;

public class Icon {
	
	public static String icon(String m) {
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
