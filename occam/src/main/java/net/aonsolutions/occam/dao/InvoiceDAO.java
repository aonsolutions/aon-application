package net.aonsolutions.occam.dao;

import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectSelectStep;
import org.jooq.SelectWithTiesAfterOffsetStep;
import org.jooq.impl.DSL;

import net.aonsolutions.occam.api.AONContext;
import net.aonsolutions.occam.api.Filter.Property;
import net.aonsolutions.occam.api.constants.Country;
import net.aonsolutions.occam.api.constants.DocumentType;
import net.aonsolutions.occam.api.constants.InvoiceType;
import net.aonsolutions.occam.api.constants.SecurityLevel;
import net.aonsolutions.occam.api.filter.InvoiceFacade.InvoiceBuilder;
import net.aonsolutions.occam.api.filter.InvoiceFacade.InvoiceBuilderFactory;
import net.aonsolutions.occam.api.filter.InvoiceFacade.InvoiceFilter;
import net.aonsolutions.occam.api.filter.InvoiceFacade.InvoiceFilters;
import net.aonsolutions.occam.api.invoice.Invoice;
import net.aonsolutions.occam.dao.Fillers.AuditFiller;

public class InvoiceDAO {
	
	private InvoiceDAO() {

	}
	
	// Field para que salgan ordenado primero 
	// compras,gastos y gastos no .ded y luego ventas.
	// En la select se complementa con invoice.type
	private static final Field<Integer> INVOICE_ORDERED_TYPE = DSL.decode()
		   .when(INVOICE.TYPE.equal((byte) 0), 0)
		   .when(INVOICE.TYPE.equal((byte) 1), 1)
		   .when(INVOICE.TYPE.equal((byte) 2), 0)
		   .when(INVOICE.TYPE.equal((byte) 3), 0);

	private static final InvoiceFilterDAO INVOICE_FILTER = new InvoiceFilterDAO();
	private static class InvoiceFilterDAO implements InvoiceFilters {
		@Override public Property<Integer> withId() {return new PropertyDAO<>(INVOICE.ID);}
		@Override public Property<Integer> withDomain(){return new PropertyDAO<>(INVOICE.DOMAIN);}
		@Override public Property<Byte> withType() {return new PropertyDAO<>(INVOICE.TYPE);}
		@Override public Property<String> withSeries() {return new PropertyDAO<>(INVOICE.SERIES);}
		@Override public Property<Integer> withNumber() {return new PropertyDAO<>(INVOICE.NUMBER);}
		@Override public Property<String> withReferenceCode() {return new PropertyDAO<>(INVOICE.REFERENCE_CODE);}
		@Override public Property<java.sql.Date> withIssueDate() {return new DatePropertyDAO(INVOICE.ISSUE_DATE);}
		@Override public Property<java.sql.Date> withTaxDate() {return new DatePropertyDAO(INVOICE.TAX_DATE);}
		@Override public Property<Byte> withConfidential() {return new PropertyDAO<>(INVOICE.SECURITY_LEVEL);}
		@Override public Property<Integer> withRegistry(){return new PropertyDAO<>(INVOICE.REGISTRY);}
		@Override public Property<String> withRegistryDocument(){return new PropertyDAO<>(INVOICE.RDOCUMENT);}
		@Override public Property<Byte> withRegistryDocumentType(){return new PropertyDAO<>(INVOICE.RDOCUMENT_TYPE);}
		@Override public Property<String> withRegistryDocumentCountry(){return new PropertyDAO<>(INVOICE.RDOCUMENT_COUNTRY);}
		@Override public Property<String> withRegistryName(){return new PropertyDAO<>(INVOICE.RNAME);}
		@Override public Property<Integer> withActivity(){return new PropertyDAO<>(INVOICE.ACTIVITY);}
		@Override public Property<String> withActivityDescription(){return new PropertyDAO<>(ENTERPRISE_ACTIVITY.DESCRIPTION);}
		@Override public Property<String> withActivityEpigraph(){return new PropertyDAO<>(IAE.EPIGRAPH);}
		@Override public Property<String> withCreationUser() {return new PropertyDAO<>(INVOICE.CREATION_USER);}
		@Override public Property<Timestamp> withCreationDate() {return new PropertyDAO<>(INVOICE.CREATION_DATE);}
		@Override public Property<String> withModificationUser() {return new PropertyDAO<>(INVOICE.MODIFICATION_USER);}
		@Override public Property<Timestamp> withModificationDate() {return new PropertyDAO<>(INVOICE.MODIFICATION_DATE);}
	}

	private static class InvoiceSelectBuilderDAO implements InvoiceBuilder<Stream<Invoice>> {
		@SuppressWarnings("rawtypes")
		private static final Field[] INVOICE_BASIC_FIELDS = new Field[]{
				 INVOICE.ID	
				,INVOICE.DOMAIN
				,INVOICE.TYPE
				,INVOICE_ORDERED_TYPE
				,INVOICE.SERIES
				,INVOICE.NUMBER
				,INVOICE.REFERENCE_CODE
				,INVOICE.ISSUE_DATE
				,INVOICE.TAX_DATE
				,INVOICE.SECURITY_LEVEL
				
				,INVOICE.REGISTRY
				,INVOICE.RDOCUMENT
				,INVOICE.RDOCUMENT_TYPE
				,INVOICE.RDOCUMENT_COUNTRY
				,INVOICE.RNAME
				
				,INVOICE.CREATION_USER
				,INVOICE.CREATION_DATE
				,INVOICE.MODIFICATION_USER
				,INVOICE.MODIFICATION_DATE
				
				,ENTERPRISE_ACTIVITY.ID			
				,ENTERPRISE_ACTIVITY.DESCRIPTION
				,IAE.EPIGRAPH
			};

		protected SelectSelectStep<Record> select;
		protected SelectJoinStep<Record> from;
		protected SelectConditionStep<Record> where;
		protected SelectWithTiesAfterOffsetStep<Record> limit;
		
		public InvoiceSelectBuilderDAO( AONContext ctx, InvoiceFilter filter ) {
			this.select = ctx.getDslContext().select(INVOICE_BASIC_FIELDS);
			this.from = select.from(INVOICE)
				.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(INVOICE.ACTIVITY))
				.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE));
			this.where(filter);
		}
		
		@Override
		public Stream<Invoice> build( ) {
			return ((limit == null)?this.where:this.limit)
				.fetch()
				.stream()
				.map(new InvoiceFiller());
		}
		
		private InvoiceSelectBuilderDAO where(InvoiceFilter filter) {
			if ( filter.filter(INVOICE_FILTER) instanceof FilterDAO filterDAO) {
				this.where = this.from.where(  filterDAO.getCondition() );
				return this;
			}
			throw new IllegalArgumentException("Filter can not be null.");
		}

		@Override
		public InvoiceSelectBuilderDAO limit(int offest, int rows) {
			this.limit = this.where.limit(offest, rows);
			return this;
		}

		@Override
		public InvoiceSelectBuilderDAO full() {
			// return invoice full data
			return this;
		}

	}
	
	private static class InvoiceFiller implements Function<Record,Invoice> {
		
		@Override
		public Invoice apply(Record r) {
			return map(r, Invoice::new);
		}
		
		Invoice map(Record r, Supplier<Invoice> supplier) {
			return supplier.get()
				.setId(FillerUtils.getValue(r,INVOICE.ID))
				.setDomain(FillerUtils.getValue(r,INVOICE.DOMAIN))
				.setType(InvoiceType.safeValueOf(r.getValue(INVOICE.TYPE)).orElse(null))
				.setSeries(FillerUtils.getValue(r,INVOICE.SERIES))
				.setNumber(FillerUtils.getValue(r,INVOICE.NUMBER))
				.setReferenceCode(FillerUtils.getValue(r,INVOICE.REFERENCE_CODE))
				.setIssueDate(FillerUtils.getValue(r,INVOICE.ISSUE_DATE))
				.setTaxDate(FillerUtils.getValue(r,INVOICE.TAX_DATE))
				.setConfidential(SecurityLevel.safeValueOf(r.getValue(INVOICE.SECURITY_LEVEL)).orElse(null) == SecurityLevel.CONFIDENTIAL)
				.setRegistry(FillerUtils.getValue(r,INVOICE.REGISTRY))
				.setRegistryDocument(FillerUtils.getValue(r,INVOICE.RDOCUMENT))
				.setRegistryDocumentType(DocumentType.safeValueOf(r.getValue(INVOICE.RDOCUMENT_TYPE)).orElse(null) )
				.setRegistryDocumentCountry(Country.safeValueOf(r.getValue(INVOICE.RDOCUMENT_COUNTRY)).orElse(null))
				.setRegistryName(FillerUtils.getValue(r,INVOICE.RNAME))
				.setActivity( new ActivityDAO.ActivityFiller().apply(r).orElse(null) )
				.setAudit( new AuditFiller().apply(r) )
				.setDirty(false)
			;
		}
	}
	
	private static InvoiceSelectBuilderDAO getBuilder( AONContext ctx, InvoiceFilter filter ) {
		return new InvoiceSelectBuilderDAO(ctx,filter);
	}
	
	public static Optional<Invoice> get(AONContext ctx, InvoiceFilter filter){
		return getStream(ctx, filter, b -> b).findFirst(); 
	}
	public static Optional<Invoice> get(AONContext ctx, InvoiceFilter filter, InvoiceBuilderFactory<Stream<Invoice>> factory){
		return getStream(ctx, filter, factory).findFirst(); 
	}

	public static Stream<Invoice> getStream(AONContext ctx, InvoiceFilter filter){
		return getStream(ctx, filter, b -> b); 
	}
	
	public static Stream<Invoice> getStream(AONContext ctx, InvoiceFilter filter, InvoiceBuilderFactory<Stream<Invoice>> factory){
		DAOUtils.checkNullFactory(factory);
		DAOUtils.checkNullFilter(filter);
		return factory.create( getBuilder(ctx,filter)).build();
	}
	
}
