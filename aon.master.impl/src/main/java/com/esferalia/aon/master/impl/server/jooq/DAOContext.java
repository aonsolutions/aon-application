package com.esferalia.aon.master.impl.server.jooq;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.shared.commons.AonEnumUtils;

public class DAOContext {

	private DSLContext dslContext;
	private DAOSecurityContext securityContext;

	public DAOContext(DSLContext dslContext, DAOSecurityContext securityContext) {
		this.dslContext = dslContext;
		this.securityContext = securityContext;
	}

	public DSLContext getDslContext() {
		return dslContext;
	}

	public DAOSecurityContext getSecurityContext() {
		return securityContext;
	}

	public DAOContext getNested(Configuration configuration) {
		return new DAOContext(DSL.using(configuration), securityContext);
	}

	public Condition getDomainInheritanceCondition(Integer domainId,
			TableField<? extends Record, java.lang.Integer> field) {
		DomainRecord record = dslContext.fetchOne(DOMAIN,
				DOMAIN.ID.equal(domainId));
		if (AonEnumUtils.getBoolean(record.getValue(DOMAIN.ENABLEHEREDITY))) {
			Integer parentDomain = record.getValue(DOMAIN.PARENT);
			return (field.equal(domainId)).or(field.equal(parentDomain));
		} else {
			return (field.equal(domainId));
		}
	}
	
}
