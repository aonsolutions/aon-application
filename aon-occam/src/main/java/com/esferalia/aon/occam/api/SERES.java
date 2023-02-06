package com.esferalia.aon.occam.api;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.seres.EdiCodes;
import com.esferalia.aon.occam.api.model.seres.SeresInfo;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.impl.jooq.SeresImpl;

public class SERES {

	private SERES() {
		throw new IllegalStateException("Utility class");
	}
	
	private static ISeres getSeres() {
		return new SeresImpl();
	}
	
	public static SeresInfo getSeresInfo(Domain domain, User user) {
		return getSeresInfo(domain.getName(), domain.getId(), user.getLogin());		
	}
	
	public static SeresInfo getSeresInfo(Domain domain, String login) {
		return getSeresInfo(domain.getName(), domain.getId(), login);
	}
	
	public static SeresInfo getSeresInfo(String domainName, Integer domainId, String login) {
		try(CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getSeres().getSeresInfo(ctx);
		}
	}

	public static EdiCodes getEdiCodes(Domain domain, User user, Delivery delivery){
		return getEdiCodes(domain.getName(), domain.getId(), user.getLogin(), delivery);
	}
	
	public static EdiCodes getEdiCodes(Domain domain, String login, Delivery delivery){
		return getEdiCodes(domain.getName(), domain.getId(), login, delivery);
	}
	
	public static EdiCodes getEdiCodes(String domainName, Integer domainId, String login, Delivery delivery){
		try(CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getSeres().getEdiCodes(ctx, delivery);
		}
	}
	
	
	public static EdiCodes getEdiCodes(Domain domain, User user, Invoice invoice){
        return getEdiCodes(domain.getName(), domain.getId(), user.getLogin(), invoice);
    }
    
    public static EdiCodes getEdiCodes(Domain domain, String login, Invoice invoice){
        return getEdiCodes(domain.getName(), domain.getId(), login, invoice);
    }
    
    public static EdiCodes getEdiCodes(String domainName, Integer domainId, String login, Invoice invoice){
        try(CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
            return getSeres().getEdiCodes(ctx, invoice);
        }
    }
	

}