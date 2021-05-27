package com.code.aon.google.apis.jooq;

import static com.esferalia.aon.jooq.tables.CommercialTracking.COMMERCIAL_TRACKING;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.ProjectCommercial.PROJECT_COMMERCIAL;
import static com.esferalia.aon.jooq.tables.Raddinfo.RADDINFO;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;

import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

import org.jooq.Record1;
import org.jooq.Result;

import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.CommercialActivity;
import com.esferalia.aon.occam.api.model.CommercialTracking;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class DBCalendar {
	
	public static Vector<String> getCommercial(Domain domain,User user, Integer id){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			
			Result<Record1<String>> data = ctx.getDslContext()
					.select(REGISTRY.NAME)
					.from(COMMERCIAL_TRACKING).join(REGISTRY).on(COMMERCIAL_TRACKING.SELLER.eq(REGISTRY.ID))
					.where(COMMERCIAL_TRACKING.ID.eq(id))
					.fetch();
			Vector<String> vector = new Vector<String>();
			for (Record1<String> record1 : data) {
				vector.add(record1.value1());
			}
			return vector;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Vector<String> getSellerEmails(Domain domain, User user, CommercialTracking ct ){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			
			Result<Record1<String>> data = ctx.getDslContext()
					.select(RMEDIA.VALUE)
					.from(RMEDIA).join(COMMERCIAL_TRACKING).on(COMMERCIAL_TRACKING.SELLER.eq(RMEDIA.REGISTRY))
					.where(RMEDIA.MEDIA.eq((byte)4))
						.and(COMMERCIAL_TRACKING.ID.eq(ct.getId()))
						.and(RMEDIA.COMMERCIAL.eq((byte)1))
					.fetch();
			Vector<String> vector = new Vector<String>();
			for (Record1<String> record1 : data) {
				
				vector.add(record1.value1());
			}
			return vector;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static String getSellerEmail(Domain domain, User user, CommercialTracking ct){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			Result<Record1<String>> data = ctx.getDslContext()
					.select(RADDINFO.VALUE)
					.from(RADDINFO)
					.where(RADDINFO.REGISTRY.eq(ct.getSeller()))
					.and(RADDINFO.ATTRIBUTE.eq("GOOGLEMAIL"))
					.fetch();
			String email = null;
			for (Record1<String> record1 : data) {
				email = record1.value1();
			}
			return email;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void setSellerEmail(Domain domain, User user, CommercialTracking ct, String email){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());

			ctx.getDslContext().insertInto(RADDINFO, RADDINFO.DOMAIN, RADDINFO.REGISTRY, RADDINFO.ATTRIBUTE, RADDINFO.VALUE, RADDINFO.VALUE_DATE)
					.values(ct.getDomain(),ct.getSeller(),"GOOGLEMAIL",email,new Date(new java.util.Date().getTime())).execute();
			
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	// getCommercialTrackingKey && getCommercialTracking
	public static LinkedList<CommercialTracking> getCommercialTrackingList(Domain domain, User user){
		return AON.getCommercialTrackingList(domain.getName(), domain.getId(), user.getLogin(),
				f -> f.getDomainProperty().eq(domain.getId()));
	}
	
	// getCommercialTrackingOne
	public static CommercialTracking getCommercialTrackingOne(Domain domain, User user, Integer ctId){
		return AON.getCommercialTracking(domain.getName(), domain.getId(), user.getLogin(),
				f -> f.getIdProperty().eq(ctId));
	}
	
	// setEventId
	public static void updateEventId(Domain domain, User user, Integer ctId, String eventId){
		AON.updateEventId(domain.getName(), domain.getId(), user.getLogin(), ctId, eventId);
	}
	
	//getEnterpriseEmail
	public static String getEnterpriseEmail(Domain domain, User user){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			
			return ctx.getDslContext().select(RMEDIA.VALUE)
				.from(ENTERPRISE).join(RMEDIA).on(RMEDIA.REGISTRY.eq(ENTERPRISE.REGISTRY))
				.where(RMEDIA.MEDIA.eq((byte)4))
				.and(ENTERPRISE.DOMAIN.eq(domain.getId()))
				.limit(1).fetchOne(RMEDIA.VALUE);
		} finally {
			if(ctx != null) ctx.close();
		}
	}

	//getSellerEmail
	public static String getSellerEmail(Domain domain, User user, Integer ctId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			
			return ctx.getDslContext().select(RMEDIA.VALUE)
				.from(COMMERCIAL_TRACKING).join(RMEDIA).on(RMEDIA.REGISTRY.eq(COMMERCIAL_TRACKING.SELLER))
				.where(RMEDIA.MEDIA.eq((byte)4))
				.and(COMMERCIAL_TRACKING.DOMAIN.eq(ctId))
				.limit(1).fetchOne(RMEDIA.VALUE);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	// getCommercialTrackingAll
	public static Map<Integer,LinkedList<CommercialTracking>> getCommercialTrackingAll(User user){
		Map<String, Integer> domainMap = DBSync.getDomainMap();
		Map<Integer, LinkedList<CommercialTracking>> map = new HashMap<Integer, LinkedList<CommercialTracking>>();
		for (String domainName : domainMap.keySet()) {
			LinkedList<CommercialTracking> list = AON.getCommercialTrackingList(domainName, domainMap.get(domainName), user.getLogin(),
					f-> f.getDomainProperty().eq(domainMap.get(domainName)));
			map.put(domainMap.get(domainName), list);
		}
		return map;
	}
	
	//getProject
	public static Project getProject(Domain domain, User user, CommercialTracking commercialTracking){
		return AON.getProject(domain.getName(), domain.getId(), user.getLogin(),
				f -> f.getIdProperty().eq(commercialTracking.getProjectCommercial()));
	}
	
	//getProjectAll
	public static LinkedList<Project> getProjectAll(Domain domain, User user){
		return AON.getProjectList(domain.getName(), domain.getId(), user.getLogin(), null);
	}

	//getActivity
	public static CommercialActivity getActivity(Domain domain, User user, CommercialTracking commercialTracking){
		return AON.getCommercialActivity(domain.getName(), domain.getId(), user.getLogin(),
				f ->  f.getIdProperty().eq(commercialTracking.getActivity()));
	}

	//getPotencialClient
	public static Registry getPotencialClient(Domain domain, User user, CommercialTracking commercialTracking){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			
			RegistryRecord rr = ctx.getDslContext().select()
					.from(REGISTRY).join(PROJECT_COMMERCIAL).on(REGISTRY.ID.eq(PROJECT_COMMERCIAL.TARGET))
					.where(PROJECT_COMMERCIAL.PROJECT.eq(commercialTracking.getProjectCommercial()))
					.limit(1).fetchInto(REGISTRY).stream().findFirst().orElse(null);
			
			return new Registry()
					.setAlias(rr.getAlias())
					.setDocument(rr.getDocument())
					.setDocumentCountry(Country.safeValueOf(rr.getDocumentCountry()))
					.setDocumentType(DocumentType.safeValueOf( rr.getDocumentType()))
					.setDomain(new Domain().setId(rr.getDomain()))
					.setId(rr.getId())
					.setName(rr.getName())
					.setNationality(Country.safeValueOf(rr.getNationality()))
					.setSecurityLevel(SecurityLevel.safeValueOf( rr.getSecurityLevel()))
					.setLegalPerson( AonEnumUtils.getBoolean( rr.getType() ));
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}	
}
