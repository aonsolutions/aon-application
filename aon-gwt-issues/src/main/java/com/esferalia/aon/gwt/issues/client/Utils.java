package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AonRole;
import com.google.gwt.event.dom.client.KeyCodes;

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
	
	public static String checkString(String str) {
		return checkSaltosDeLinea(checkComillas(str));
	}
	
	private static String checkComillas(String str) {
		String[] array = str.split("\"");
		String s = array[0];
		for(Integer i = 1; i < array.length; i++){
			s = s + "\\\"" + array[i];
		}
		if(str.substring(str.length()-1).equals("\""))
			s = s + "\\\"";
		return s;
	}

	private static String checkSaltosDeLinea(String str) {
		String[] array = str.split("\n");
		String s = array[0];
		for(Integer i = 1; i < array.length; i++){
			s = s + "\\n" + array[i];
		}
		if(str.substring(str.length()-1).equals("\n"))
			s = s + "\\n";
		return s;
	}
	
    public static Boolean isAdmin(User user) {
    	for(Integer i = 0; i < user.getUserRoles().length; i++){
    		if(user.getUserRoles()[i] != null && 	
    			(user.getUserRoles()[i].equals(AonRole.ADMIN) ||
    			user.getUserRoles()[i].equals(AonRole.CALL_CENTER_MANAGER))){
    			return true;
    		}
    	}
    	return false;
	}
	
}
