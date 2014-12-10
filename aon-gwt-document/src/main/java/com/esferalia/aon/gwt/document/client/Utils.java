package com.esferalia.aon.gwt.document.client;

import java.util.Vector;

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
	
	
	public static void main(String[] args) {
		String s = "ARISTIZABAL GARCIA, ROBINSON ( robinson-novus.aibanez.net)";
		System.out.println(s);
		System.out.println(getOracleString(s));
	}
}

