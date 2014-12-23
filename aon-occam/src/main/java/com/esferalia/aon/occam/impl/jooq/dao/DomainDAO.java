package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jooq.Record6;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;

public class DomainDAO {

	private static Logger LOGGER = Logger.getLogger(DomainDAO.class.getName());

	public static Domain getDomain(AONContext ctx, int domain) {

		Record6<Integer, String, Integer, Byte, Byte, Byte> record = ctx
				.getDslContext()
				.select(DOMAIN.ID, DOMAIN.NAME, DOMAIN.PARENT, DOMAIN.TYPE,
						DOMAIN.DOMAINMANAGEMENT, DOMAIN.ACTIVE).from(DOMAIN)
				.where(DOMAIN.ID.equal(domain)).fetchOne();
		LOGGER.log(Level.INFO, "GET Domain ( dom: " + domain + ")");
		Domain dom = new Domain();
		if (record != null) {
			dom.setId(record.getValue(DOMAIN.ID));
			dom.setName(record.getValue(DOMAIN.NAME));
			boolean parent = (record.getValue(DOMAIN.PARENT) == null);
			Byte dm = record.getValue(DOMAIN.DOMAINMANAGEMENT);
			boolean domainManagement = (dm != null && dm == 1);
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
