package net.aonsolutions.occam.dao;

import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;

import java.sql.Timestamp;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectJoinStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.watson.util.AonEnumUtils;

import net.aonsolutions.occam.api.AONContext;
import net.aonsolutions.occam.api.Filter.Property;
import net.aonsolutions.occam.api.constants.Country;
import net.aonsolutions.occam.api.constants.DocumentType;
import net.aonsolutions.occam.api.constants.InvoiceType;
import net.aonsolutions.occam.api.constants.SecurityLevel;
import net.aonsolutions.occam.api.filter.InvoiceFacade.InvoiceFilter;
import net.aonsolutions.occam.api.filter.InvoiceFacade.InvoiceProperties;
import net.aonsolutions.occam.api.invoice.InvoiceMin;

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
	
	private static final Field<?>[] INVOICE_MIN_FIELDS = new Field[]{
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
	
	private static final InvoicePropertiesDAO INVOICE_PROPERTIES = new InvoicePropertiesDAO();
	private static class InvoicePropertiesDAO implements InvoiceProperties {
		
		public Select<Record> build(SelectJoinStep<Record> select, InvoiceFilter filter) {
			if (filter == null) throw new IllegalArgumentException("Filter can not be null.");
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		@Override public Property<Integer> getIdProperty() {return new PropertyDAO<>(INVOICE.ID);}
		@Override public Property<Integer> getDomainProperty(){return new PropertyDAO<>(INVOICE.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty(){return new PropertyDAO<>(INVOICE.REGISTRY);}
		@Override public Property<String> getRegistryDocumentProperty(){return new PropertyDAO<>(INVOICE.RDOCUMENT);}
		@Override public Property<String> getRegistryNameProperty(){return new PropertyDAO<>(INVOICE.RNAME);}
		@Override public Property<java.sql.Date> getIssueDateProperty() {return new DatePropertyDAO(INVOICE.ISSUE_DATE);}
		@Override public Property<Byte> getTypeProperty() {return new PropertyDAO<>(INVOICE.TYPE);}
		@Override public Property<Byte> getConfidentialProperty() {return new PropertyDAO<>(INVOICE.SECURITY_LEVEL);}
		@Override public Property<java.sql.Date> getTaxDateProperty() {return new DatePropertyDAO(INVOICE.TAX_DATE);}
		@Override public Property<String> getSeriesProperty() {return new PropertyDAO<>(INVOICE.SERIES);}
 		@Override public Property<Integer> getNumberProperty() {return new PropertyDAO<>(INVOICE.NUMBER);}
 		@Override public Property<String> getReferenceCodeProperty() {return new PropertyDAO<>(INVOICE.REFERENCE_CODE);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new PropertyDAO<>(INVOICE.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new PropertyDAO<>(INVOICE.CREATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new PropertyDAO<>(INVOICE.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new PropertyDAO<>(INVOICE.MODIFICATION_USER);}

	}

	private static class MinimalInvoiceFiller  implements Function<Record,InvoiceMin> {

		@Override
		public InvoiceMin apply(Record rec) {
			return new InvoiceMin()
				.setId(rec.getValue(INVOICE.ID))
				.setDomain(rec.getValue(INVOICE.DOMAIN))
				.setType(AonEnumUtils.enumValue(InvoiceType.class,rec.getValue(INVOICE.TYPE)))
				.setSeries(rec.getValue(INVOICE.SERIES))
				.setNumber(rec.getValue(INVOICE.NUMBER))
				.setReferenceCode(rec.getValue(INVOICE.REFERENCE_CODE))
				.setIssueDate(rec.getValue(INVOICE.ISSUE_DATE))
				.setTaxDate(rec.getValue(INVOICE.TAX_DATE))
				.setConfidential(AonEnumUtils.enumValue(SecurityLevel.class,rec.getValue(INVOICE.SECURITY_LEVEL)) == SecurityLevel.CONFIDENTIAL)
				.setRegistry(rec.getValue(INVOICE.REGISTRY))
				.setRegistryDocument(rec.getValue(INVOICE.RDOCUMENT))
				.setRegistryDocumentType(AonEnumUtils.enumValue(DocumentType.class,rec.getValue(INVOICE.RDOCUMENT_TYPE)))
				.setRegistryDocumentCountry(Country.safeValueOf(rec.getValue(INVOICE.RDOCUMENT_COUNTRY)))
				.setCreationUser(rec.getValue(INVOICE.CREATION_USER))
				.setCreationDate(rec.getValue(INVOICE.CREATION_DATE))
				.setModificationUser(rec.getValue(INVOICE.MODIFICATION_USER))
				.setModificationDate(rec.getValue(INVOICE.MODIFICATION_DATE))
				.setActivityId(rec.getValue(ENTERPRISE_ACTIVITY.ID))
				.setActivityDescription(rec.getValue(ENTERPRISE_ACTIVITY.DESCRIPTION))
				.setActivityEpigraph(rec.getValue(IAE.EPIGRAPH))
				.setDirty(false)
			;
		}
	}

	private static SelectOnConditionStep<Record> getSelect(AONContext ctx) {
		return ctx.getDslContext()
			.select(INVOICE_MIN_FIELDS)
			.from(INVOICE)	
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(INVOICE.ACTIVITY))
			.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE));
	}

	public static Stream<InvoiceMin> getInvoices(AONContext ctx,InvoiceFilter filter) {
		// ---------
		// Domain???
		// ---------
		ctx.checkRead();
		return INVOICE_PROPERTIES.build(getSelect( ctx ), filter)
			.fetch()
			.stream()
			.map(new MinimalInvoiceFiller());
	}
	
}
