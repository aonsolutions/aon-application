package net.aonsolutions.occam.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.ResultQuery;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.watson.server.AonArrayUtils;

import net.aonsolutions.occam.api.AON;
import net.aonsolutions.occam.api.AONContext;
import net.aonsolutions.occam.api.Filter.Property;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.api.constants.AonStatus;
import net.aonsolutions.occam.api.constants.DomainType;
import net.aonsolutions.occam.api.filter.DomainFacade.DomainBuilder;
import net.aonsolutions.occam.api.filter.DomainFacade.DomainBuilderFactory;
import net.aonsolutions.occam.api.filter.DomainFacade.DomainFilter;
import net.aonsolutions.occam.api.filter.DomainFacade.DomainFilters;

public class DomainDAO {
	
	private DomainDAO() {
		
	}
	private static final com.esferalia.aon.jooq.tables.Domain PARENT_DOMAIN = DOMAIN.as("parent_domain");
	
	private static final DomainFilterDAO DOMAIN_FILTER = new DomainFilterDAO();
	private static class DomainFilterDAO implements DomainFilters {
		@Override public Property<Integer> withId() {return new PropertyDAO<>(DOMAIN.ID);}
		@Override public Property<String> withName() {return new PropertyDAO<>(DOMAIN.NAME);}
		@Override public Property<String> withDescription() {return new PropertyDAO<>(DOMAIN.DESCRIPTION);}
		@Override public Property<Integer> withParent() {return new PropertyDAO<>(DOMAIN.PARENT);}
		@Override public Property<Byte> withType() {return new PropertyDAO<>(DOMAIN.TYPE);}
		@Override public Property<Integer> withScope() {return new PropertyDAO<>(DOMAIN.SCOPE);}
		@Override public Property<String> withSubDomainSuffix() {return new PropertyDAO<>(DOMAIN.SUBDOMAINSUFFIX);}
		@Override public Property<Byte> withEnableHeredity() {return new PropertyDAO<>(DOMAIN.ENABLEHEREDITY);}
		@Override public Property<Byte> withDomainManagement() {return new PropertyDAO<>(DOMAIN.DOMAINMANAGEMENT);}
		@Override public Property<Byte> withDisableDomainManagement() {return new PropertyDAO<>(DOMAIN.DISABLEDOMAINMANAGEMENT);}
		@Override public Property<Integer> withMaxDocumentSize() {return new PropertyDAO<>(DOMAIN.MAXDOCUMENTSIZE);}
		@Override public Property<Integer> withMaxTotalDocumentSize() {return new PropertyDAO<>(DOMAIN.MAXTOTALDOCUMENTSIZE);}
		@Override public Property<Integer> withMaxDefinedUsers() {return new PropertyDAO<>(DOMAIN.MAXDEFINEDUSERS);}
		@Override public Property<Byte> withActive() {return new PropertyDAO<>(DOMAIN.ACTIVE);}
		@Override public Property<String> withOwner() {return new PropertyDAO<>(DOMAIN.OWNER);}
		@Override public Property<String> withCreationUser() {return new PropertyDAO<>(DOMAIN.CREATION_USER);}
		@Override public Property<Timestamp> withCreationDate() {return new PropertyDAO<>(DOMAIN.CREATION_DATE);}
		@Override public Property<String> withModificationUser() {return new PropertyDAO<>(DOMAIN.MODIFICATION_USER);}
		@Override public Property<Timestamp> withModificationDate() {return new PropertyDAO<>(DOMAIN.MODIFICATION_DATE);}
		@Override public Property<Date> withExpirationDate() {return new PropertyDAO<>(DOMAIN.EXPIRATIONDATE);}
		@Override public Property<String> withLastAccessUser() {return new PropertyDAO<>(DOMAIN.LASTACCESS_USER);}
		@Override public Property<Timestamp> withLastAccessDate() {return new PropertyDAO<>(DOMAIN.LASTACCESS_DATE);}
		@Override public Property<Integer> withAonCustomer() {return new PropertyDAO<>(DOMAIN.AONCUSTOMER);}
		@Override public Property<Byte> withAonStatus() {return new PropertyDAO<>(DOMAIN.AONSTATUS);}
	}
	
	private static class DomainSelectBuilderDAO implements DomainBuilder<Stream<Domain>> {
		
		private static final Field<?>[] DOMAIN_BASIC_FIELDS = new Field[]{
			DOMAIN.ID,DOMAIN.NAME,DOMAIN.DESCRIPTION
		};
		
		private static final Field<?>[] DOMAIN_PARENT_BASIC_FIELDS = new Field[]{
			PARENT_DOMAIN.ID,PARENT_DOMAIN.NAME,PARENT_DOMAIN.DESCRIPTION
		};

		private static final Field<?>[] DOMAIN_AUDIT_FIELDS = new Field[]{
			DOMAIN.CREATION_USER,DOMAIN.CREATION_DATE,DOMAIN.MODIFICATION_USER,DOMAIN.MODIFICATION_DATE
		};

		private static final Field<?>[] DOMAIN_OTHER_FIELDS = new Field[]{
			 DOMAIN.PARENT
			,DOMAIN.TYPE
			,DOMAIN.SCOPE
			,DOMAIN.SUBDOMAINSUFFIX
			,DOMAIN.ENABLEHEREDITY
			,DOMAIN.DOMAINMANAGEMENT
			,DOMAIN.DISABLEDOMAINMANAGEMENT
			,DOMAIN.MAXDOCUMENTSIZE
			,DOMAIN.MAXTOTALDOCUMENTSIZE
			,DOMAIN.MAXDEFINEDUSERS
			,DOMAIN.ACTIVE
			,DOMAIN.OWNER
			,DOMAIN.EXPIRATIONDATE
			,DOMAIN.LASTACCESS_USER
			,DOMAIN.LASTACCESS_DATE
			,DOMAIN.AONCUSTOMER
			,DOMAIN.AONSTATUS
		};
		
		private final AONContext ctx;
		private final DomainFilter filter;
		private boolean limit = false;
		private int offset;
		private int rows;
		
		private boolean withAllRow = false;
		private boolean withParent = false;
		private boolean withAudit = false;
		private boolean withUsers = false;

		public DomainSelectBuilderDAO( AONContext ctx, DomainFilter filter ) {
			this.ctx = ctx;
			this.filter = filter;
		}
		
		@Override
		public Stream<Domain> build( ) {
			SelectJoinStep<Record> step1 = ctx.getDslContext().select( getSelectedFields() )
				.from(DOMAIN);
			if ( withParent ) {
				step1 = step1.leftOuterJoin(PARENT_DOMAIN).on(PARENT_DOMAIN.ID.eq(DOMAIN.PARENT));	
			}
			SelectConditionStep<Record> step2 = step1.where( getWhere() );
			ResultQuery<Record> step3 =  limit?step2.limit(offset,rows):step2;
			if ( limit ) {
				step1.limit(offset,rows);
			}
			DomainFiller filler = new DomainFiller(); 
			return  step3
				.fetch()
				.stream()
				.map(filler::apply )
				// ---------------------------------- [EUKE]
				// Esto es una cochinada que hay que cambiar.
				// Hacer el stream contra las tablas que sea necesario y usar 
				// reduce/gruping para organizar la informacion como debe ser.
				// Este código no debe salir a producción en ningún caso.
				// SE ABRE OTRA CONNECTION A LA BD cuando es absolutamente innecesario. 
				.map(d -> withUsers
					?d.setUsers(AON.getUsers(ctx.getOccam(), p -> p.withDomain().eq(d.getId())).collect(Collectors.toCollection(LinkedList::new)))
					:d)
				// -----------------
				;
		}
		
		private Field<?>[] getSelectedFields() {
			Field<?>[] fields = new Field<?>[] {};
			fields = AonArrayUtils.addAll(fields,DOMAIN_BASIC_FIELDS);
			if (withAllRow) {
				fields = AonArrayUtils.addAll(fields,DOMAIN_OTHER_FIELDS);	
			}
			if (withAudit) {
				fields = AonArrayUtils.addAll(fields,DOMAIN_AUDIT_FIELDS);
			}
			if (withParent) {
				fields = AonArrayUtils.addAll(fields,DOMAIN_PARENT_BASIC_FIELDS);
			}
			return fields;
		}

		private Condition getWhere() {
			if ( filter.filter(DOMAIN_FILTER) instanceof FilterDAO filterDAO) {
				return filterDAO.getCondition();
			}
			throw new IllegalArgumentException("Filter can not be null.");
		}

		@Override
		public DomainSelectBuilderDAO limit(final int offset, final int rows) {
			this.limit = true;
			this.offset = offset;
			this.rows = rows;
			return this;
		}
		
		@Override
		public DomainBuilder<Stream<Domain>> withAllRow() {
			withAllRow = true;
			return this;
		}
		
		@Override
		public DomainBuilder<Stream<Domain>> withUsers() {
			withUsers = true;
			return this;
		}
		@Override
		public DomainBuilder<Stream<Domain>> withAudit() {
			withAudit = true;
			return this;
		}
		
		@Override
		public DomainBuilder<Stream<Domain>> withParent() {
			withParent = true;
			return this;
		}

		@Override
		public DomainSelectBuilderDAO full() {
			withAllRow = true;
			withParent = true;
			withAudit = true;
			return this;
		}

		
		private class DomainFiller extends Filler<Domain> implements Function<Record,Domain> {
			
			@Override
			public Domain apply(Record r) {
				final Domain domain = new Domain()
					.setId(getValue(r,DOMAIN.ID))
					.setName(getValue(r,DOMAIN.NAME))
					.setDescription(getValue(r,DOMAIN.DESCRIPTION));
				if (DomainSelectBuilderDAO.this.withAllRow) {
					domain
						.setType( DomainType.safeValueOf( getValue(r,DOMAIN.TYPE)).orElse(null))
						.setScope(getValue(r,DOMAIN.SCOPE))
						.setSubDomainSuffix(getValue(r,DOMAIN.SUBDOMAINSUFFIX))
						.setEnableHeredity(getBoolean(r, DOMAIN.ENABLEHEREDITY))
						.setDomainManagement(getBoolean(r, DOMAIN.DOMAINMANAGEMENT))
						.setDisableDomainManagement(getBoolean(r, DOMAIN.DISABLEDOMAINMANAGEMENT))
						.setMaxDocumentSize(getValue(r, DOMAIN.MAXDOCUMENTSIZE))
						.setMaxTotalDocumentSize(getValue(r, DOMAIN.MAXTOTALDOCUMENTSIZE))
						.setMaxDefinedUsers(getValue(r,DOMAIN.MAXDEFINEDUSERS))
						.setActive(getBoolean(r, DOMAIN.ACTIVE))
						.setOwner(getValue(r, DOMAIN.OWNER))
						.setExpirationDate(getValue(r, DOMAIN.EXPIRATIONDATE))
						.setLastAccessUser(getValue(r, DOMAIN.LASTACCESS_USER))
						.setLastAccessDate(getValue(r, DOMAIN.LASTACCESS_DATE))
						.setAonCustomer(getValue(r,DOMAIN.AONCUSTOMER))
						.setAonStatus( AonStatus.safeValueOf( getValue(r,DOMAIN.AONSTATUS)).orElse(null))
						;	
				}
				if (DomainSelectBuilderDAO.this.withAudit) {
					domain
						.setCreationUser(getValue(r, DOMAIN.CREATION_USER))
						.setCreationDate(getValue(r, DOMAIN.CREATION_DATE))
						.setModificationUser(getValue(r, DOMAIN.MODIFICATION_USER))
						.setModificationDate(getValue(r, DOMAIN.MODIFICATION_DATE));
				}
				if (DomainSelectBuilderDAO.this.withParent) {
					domain.setParent(new Domain()
						.setId(getValue(r,PARENT_DOMAIN.PARENT))
						.setName(getValue(r,PARENT_DOMAIN.NAME))
						.setDescription(getValue(r,PARENT_DOMAIN.DESCRIPTION))
					);
				}
				return domain.setDirty(false);	
			}
		}
		
	}

	private static DomainSelectBuilderDAO getBuilder( AONContext ctx, DomainFilter filter ) {
		return new DomainSelectBuilderDAO(ctx,filter);
	}
	
	public static Optional<Domain> get(AONContext ctx, DomainFilter filter){
		return get(ctx, filter, b -> b); 
	}
	public static Optional<Domain> get(AONContext ctx, DomainFilter filter, DomainBuilderFactory factory){
		return getStream(ctx, filter, factory).findFirst();
	}

	public static Stream<Domain> getStream(AONContext ctx, DomainFilter filter){
		return getStream(ctx, filter, b -> b); 
	}
	
	public static Stream<Domain> getStream(AONContext ctx, DomainFilter filter, DomainBuilderFactory factory){
		ctx.checkRead();
		DAOUtils.checkNullFactory(factory);
		DAOUtils.checkNullFilter(filter);
		return factory.create( getBuilder(ctx,filter)).build();
	}
	
}
