package com.esferalia.aon.occam.api;

import java.util.List;

import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.SecurityImpl;

public class AUTH {
	
    private AUTH() {
       
    }
	
	private static ISecurity getSecurity() {
		return new SecurityImpl();
	}	

	public static Auth getAuthByUuid(String uuid) {
	    return getSecurity().getAuthByUuid(uuid); 
	}
    
	public static Auth getAuthByEmail(String email) {
        return getSecurity().getAuthByEmail(email); 
    }
	
    public static Auth getAuthByPhone(String phone) {
        return getSecurity().getAuthByPhone(phone); 
    }
    
    public static Auth getAuthByDocument(String document) {
        return getSecurity().getAuthByDocument(document); 
    }
	
    public static List<Auth> getAuths(List<String> uuids) {
       return getSecurity().getAuthList(uuids);
    }
	
	public static Auth saveAuth(Auth auth) {
	    return getSecurity().saveAuth(auth);
	}
	
	public static void savePassword(String uuid, String pass) {
        getSecurity().saveAuthPassword(uuid, pass);
    }
	
	public static void saveAvatar(String uuid, byte[] data, MimeType mimetype) {
        getSecurity().saveAuthAvatar(uuid, data, mimetype);
    }
	
	public static void backup(String email) {
	    getSecurity().backup(email);
	}
}
