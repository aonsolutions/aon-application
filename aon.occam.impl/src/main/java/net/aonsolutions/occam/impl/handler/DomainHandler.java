package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.util.Optional;

import org.jooq.Record;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.Domain;
import net.aonsolutions.occam.api.model.type.AonStatus;
import net.aonsolutions.occam.api.model.type.DomainType;
import net.aonsolutions.occam.impl.AONContext;

class DomainHandler {
	
	private DomainHandler() {
		
	}
 	
	static SelectJoinStep<Record> select(AONContext ctx){
		return ctx.getDslContext().select()
			.from(DOMAIN);
	}

	static Optional<Domain> get(AONContext ctx, Integer id){
		ctx.checkRead();
		if (id == null) return Optional.empty();
		return select(ctx)
			.where(DOMAIN.ID.eq(id))
			.fetch()
			.stream()
			.map(new DomainFiller())
			.findFirst()
		;
	}
	
	static Optional<Domain> get(AONContext ctx, String name){
		ctx.checkRead();
		if (AonStringUtils.isBlank(name)) return Optional.empty();
		return select(ctx)
			.where(DOMAIN.NAME.eq(name))
			.fetch()
			.stream()
			.map(new DomainFiller())
			.findFirst()
		;
	}

	static class DomainFiller extends Filler<Domain>  {
		
		@Override
		public Domain apply(Record r) {
			return build(r);
		}
		
		public static Domain build(Record r) {
			return buildDomain(r, DOMAIN);
		}
		
		public static Domain buildDomain(Record r, com.esferalia.aon.jooq.tables.Domain table) {
			return fillDomain(r, new Domain(), table);
		}
		
		public static Domain fillDomain(Record r, Domain domain, com.esferalia.aon.jooq.tables.Domain table) {
			return domain
				.setId(getValue(r, table.ID))
				.setName(getValue(r, table.NAME))
				.setDescription(getValue(r, table.DESCRIPTION))
				.setParentId(getValue(r, table.PARENT))
				.setDomainType( DomainType.value(getValue(r, table.TYPE)).orElse(null) )
				.setScope(getValue(r, table.SCOPE))
				.setHeredityEnabled(getBoolean(r, table.ENABLEHEREDITY))
				.setDomainManagement(getBoolean(r, table.DOMAINMANAGEMENT))
				.setDisableDomainManagement(getBoolean(r, table.DISABLEDOMAINMANAGEMENT))
				.setMaxDocumentSize(getValue(r, table.MAXDOCUMENTSIZE))
				.setMaxTotalDocumentSize(getValue(r, table.MAXTOTALDOCUMENTSIZE))
				.setMaxDefinedUsers(getValue(r, table.MAXDEFINEDUSERS))
				.setActive(getBoolean(r, table.ACTIVE))
				.setOwner(getValue(r, table.OWNER))
				.setCreationUser(getValue(r, table.CREATION_USER))
				.setCreationDate(getValue(r, table.CREATION_DATE))
				.setModificationUser(getValue(r, table.MODIFICATION_USER))
				.setModificationDate(getValue(r, table.MODIFICATION_DATE))
				.setLastAccessUser(getValue(r, table.LASTACCESS_USER))
				.setLastAccessDate(getValue(r, table.LASTACCESS_DATE))
				.setExpirationDate(getValue(r, table.EXPIRATIONDATE))
				.setAonCustomer(getValue(r, table.AONCUSTOMER))
				.setAonStatus(AonStatus.value(r.getValue(table.AONSTATUS)).orElse(null))
			;	
		}
	}
}
