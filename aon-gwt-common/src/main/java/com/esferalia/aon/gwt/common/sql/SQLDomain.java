package com.esferalia.aon.gwt.common.sql;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.Record6;

import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.gwt.common.shared.Domain;

public class SQLDomain {

	private static Logger LOGGER = Logger.getLogger(SQLDomain.class.getName());
	
	public static Domain getDomain(DSLContext ctx,int domain) throws AonSQLException {
		
		List<Record6<Integer,String,Integer,Byte,Byte,Byte>> record = 
				ctx.select(DOMAIN.ID,DOMAIN.NAME,DOMAIN.PARENT,DOMAIN.TYPE,DOMAIN.DOMAINMANAGEMENT,DOMAIN.ACTIVE)
					.from(DOMAIN)
					.where(DOMAIN.ID.equal(domain))
					.fetch();
		LOGGER.log(Level.INFO, "GET Domain ( dom: " + domain + ")");
		Domain dom = new Domain();
		for (Record6<Integer,String,Integer,Byte,Byte,Byte> rec : record ) {
			dom.setId(rec.getValue(DOMAIN.ID));
			dom.setName(rec.getValue(DOMAIN.NAME));
			boolean parent = (rec.getValue(DOMAIN.PARENT) == null);
			Byte dm = rec.getValue(DOMAIN.DOMAINMANAGEMENT);
			boolean domainManagement =  (dm != null && dm == 1);
			if (!parent) {
				dom.setChild(true);
				dom.setStandalone(false);
				dom.setParent(false);
			} else {
				if (domainManagement) {
					dom.setParent(true);
					dom.setStandalone(false);
					dom.setChild(false);
				} else {
					dom.setParent(false);
					dom.setStandalone(true);
					dom.setChild(false);
				}
			}
		}
		return dom;
	}

}
