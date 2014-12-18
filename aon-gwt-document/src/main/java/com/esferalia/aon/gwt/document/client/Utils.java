package com.esferalia.aon.gwt.document.client;

import java.util.Vector;

import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.google.sql.AbstractSQL.Domain;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class Utils {


	public static boolean isNotAlpKey(int code) {
	    switch (code) {
	      case KeyCodes.KEY_ALT:
	      case KeyCodes.KEY_CAPS_LOCK:
	      case KeyCodes.KEY_CONTEXT_MENU:
	      case KeyCodes.KEY_CTRL:
	      case KeyCodes.KEY_DOWN:
	      case KeyCodes.KEY_END:
	      case KeyCodes.KEY_ENTER:
	      case KeyCodes.KEY_ESCAPE:
	      case KeyCodes.KEY_F1:
	      case KeyCodes.KEY_F2:
	      case KeyCodes.KEY_F3:
	      case KeyCodes.KEY_F4:
	      case KeyCodes.KEY_F5:
	      case KeyCodes.KEY_F6:
	      case KeyCodes.KEY_F7:
	      case KeyCodes.KEY_F8:
	      case KeyCodes.KEY_F9:
	      case KeyCodes.KEY_F10:
	      case KeyCodes.KEY_F11:
	      case KeyCodes.KEY_F12:
	      case KeyCodes.KEY_FIRST_MEDIA_KEY:
	      case KeyCodes.KEY_HOME:
	      case KeyCodes.KEY_INSERT:
	      case KeyCodes.KEY_LAST_MEDIA_KEY:
	      case KeyCodes.KEY_LEFT:
	      case KeyCodes.KEY_MAC_ENTER:
	      case KeyCodes.KEY_MAC_FF_META:
	      case KeyCodes.KEY_NUMLOCK:
	      case KeyCodes.KEY_PAGEDOWN:
	      case KeyCodes.KEY_PAGEUP:
	      case KeyCodes.KEY_PAUSE:
	      case KeyCodes.KEY_PRINT_SCREEN:
	      case KeyCodes.KEY_RIGHT:
	      case KeyCodes.KEY_SCROLL_LOCK:
	      case KeyCodes.KEY_SHIFT:
	      case KeyCodes.KEY_SPACE:
	      case KeyCodes.KEY_TAB:
	      case KeyCodes.KEY_UP:
	        return true;
	      default:
	        return false;
	    }
	  }

	static MultiWordSuggestOracle createOracle(Vector<com.esferalia.aon.gwt.document.shared.Domain> vector) {
		MultiWordSuggestOracle oracleSons = new MultiWordSuggestOracle();

		for (com.esferalia.aon.gwt.document.shared.Domain d : vector) {
			oracleSons.add(d.getDescription()+" ( "+d.getName()+")");
		}
		return oracleSons;
	}

	static String getOracleString(String s){
		String aux;
		Integer pos = s.indexOf('(');
		aux = s.substring(pos+2,s.length()-1);
		return aux;
	}
	
	static MultiWordSuggestOracle createOracle2(Vector<String> l) {
		Vector<Suggestion> suggestions = new Vector<SuggestOracle.Suggestion>();
		
		for (String string : l) {
			suggestions.add(new Suggestion() {
				//Sustituye el string en SuggestBox.
				@Override
				public String getReplacementString() {
					return  "1";
				}
				
				//Sustituye el string en el popUp.
				@Override
				public String getDisplayString() {
					return  "1";
				}
			});
		}
		
		return new MultiWordSuggestOracle();
	}
	
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
	
	public static Boolean isGdocs(String m){
		switch (m) {
			case "application/vnd.google-apps.document":
			case "application/vnd.google-apps.drawing":
			case "application/vnd.google-apps.folder":
			case "application/vnd.google-apps.form":
			case "application/vnd.google-apps.presentation":
			case "application/vnd.google-apps.spreadsheet":
				return true;
			default:
				return false;
			}
		
	}
	public static void main(String[] args) {
		String s = "ARISTIZABAL GARCIA, ROBINSON ( robinson-novus.aibanez.net)";
		System.out.println(s);
		System.out.println(getOracleString(s));
	}
	
	
}

