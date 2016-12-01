package com.code.aon.webservice.util;

import com.esferalia.aon.occam.api.model.type.MediaType;

public class Icon {
	
	public static String rmediaIcon(Byte media) {
		if(media.equals(MediaType.CELLULAR.value()))
			return "settings-cell";
		if(media.equals(MediaType.FIXED_PHONE.value()))
			return "settings-phone";//communication:call
		if(media.equals(MediaType.FAX.value()))
			return "print";
		if(media.equals(MediaType.EMAIL.value()))
			return "mail";
		if(media.equals(MediaType.WEB.value()))
			return "http";//av:web
		return "";
	}
}
