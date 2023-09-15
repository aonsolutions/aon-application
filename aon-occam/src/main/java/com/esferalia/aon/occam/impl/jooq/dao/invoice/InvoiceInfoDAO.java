package com.esferalia.aon.occam.impl.jooq.dao.invoice;

import static com.esferalia.aon.jooq.tables.InvoiceInfo.INVOICE_INFO;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.InvoiceInfoFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.InvoiceInfoProperties;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.finance.OldInvoiceCommunicationType;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.FilterDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class InvoiceInfoDAO {
	
	private InvoiceInfoDAO() {
	
	}
	
	private static final InvoiceInfoPropertiesDAO INVOICE_INFO_PROPERTIES = new InvoiceInfoPropertiesDAO();
	public static class InvoiceInfoPropertiesDAO implements InvoiceInfoProperties {
		
		public Condition[] getConditions(InvoiceInfoFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_INFO.ID);}
		@Override public Property<Integer> getDomainProperty(){return new FilterDAO.PropertyDAO<>(INVOICE_INFO.DOMAIN);}
		@Override public Property<Integer> getInvoiceProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_INFO.INVOICE);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_INFO.TYPE);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_INFO.STATUS);}
	}
	
	
	public static SelectConditionStep<Record> select(AONContext ctx, InvoiceInfoFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(INVOICE_INFO)
				.where(INVOICE_INFO_PROPERTIES.getConditions(filter));
	}
	
	public static InvoiceInfo get(AONContext ctx, InvoiceInfoFilter filter) {
		return select(ctx, filter).limit(1)
			.fetch().stream().map(new InvoiceInfoFiller())
			.findFirst().orElse(new InvoiceInfo());
	}
	
	public static InvoiceInfo save(AONContext ctx, InvoiceInfo invoiceInfo) {
		InvoiceInfoValidation.autoComplete(ctx, invoiceInfo);
		InvoiceInfoValidation.validate(ctx, invoiceInfo);
		Invoice inv = InvoiceDAO.getInvoice(ctx, invoiceInfo.getInvoice());
		if(inv != null && inv.getId() != null) {
			invoiceInfo = invoiceInfo.getId() != null 
					? update(ctx, invoiceInfo)
					: insert(ctx, invoiceInfo);
		}
		return invoiceInfo;
	}
	
	public static InvoiceInfo update(AONContext ctx, InvoiceInfo invoiceInfo) {
		ctx.getDslContext().update(INVOICE_INFO)
		.set(INVOICE_INFO.DOMAIN, invoiceInfo.getDomain())
		.set(INVOICE_INFO.INVOICE, invoiceInfo.getInvoice())
		.set(INVOICE_INFO.TYPE, invoiceInfo.getType().value())
		.set(INVOICE_INFO.STATUS, invoiceInfo.getStatus().value())
		.where(INVOICE_INFO.ID.eq(invoiceInfo.getId()))
		.execute();
		return invoiceInfo;
	}
	
	public static InvoiceInfo insert(AONContext ctx, InvoiceInfo invoiceInfo) {
		Integer id = ctx.getDslContext().insertInto(INVOICE_INFO)
				.set(INVOICE_INFO.DOMAIN, invoiceInfo.getDomain())
				.set(INVOICE_INFO.INVOICE, invoiceInfo.getInvoice())
				.set(INVOICE_INFO.TYPE, invoiceInfo.getType().value())
				.set(INVOICE_INFO.STATUS, invoiceInfo.getStatus().value())
			.returning(INVOICE_INFO.ID).fetchOne().getId();
		return invoiceInfo.setId(id);
	}	

	public static void delete(AONContext ctx, Integer id){
		delete(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()).and(f.getIdProperty().eq(id)));
	}
	
	public static void delete(AONContext ctx, InvoiceInfoFilter filter){
		ctx.getDslContext().delete(INVOICE_INFO)
		.where(INVOICE_INFO_PROPERTIES.getConditions(filter))
		.execute();
	}
	
	public static class InvoiceInfoFiller extends Filler implements Function<Record, InvoiceInfo> {

		@Override
		public InvoiceInfo apply(Record r) {
			return build(r);
		}
		
		public static InvoiceInfo build(Record r) {
			return new InvoiceInfo()
				.setId(r.getValue(INVOICE_INFO.ID))
				.setDomain(r.getValue(INVOICE_INFO.DOMAIN))
				.setInvoice(r.getValue(INVOICE_INFO.INVOICE))
				.setType(OldInvoiceCommunicationType.safeValueOf(r.getValue(INVOICE_INFO.TYPE)))
				.setStatus(InvoiceCommunicationStatus.safeValueOf(r.getValue(INVOICE_INFO.STATUS)));
		}
	}
	
	private static class InvoiceInfoValidation {
		
		private InvoiceInfoValidation() {
	
		}
		
		public static final BiConsumer<AONContext, InvoiceInfo> EMPTY_DOMAIN = (ctx, invoiceInfo) -> {
			if (invoiceInfo.getDomain() == null) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		public static final BiConsumer<AONContext, InvoiceInfo> EMPTY_INVOICE = (ctx, invoiceInfo) -> {
			if (invoiceInfo.getInvoice() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format("invoice")) ;
		};
		
		public static final BiConsumer<AONContext, InvoiceInfo> EMPTY_TYPE = (ctx, invoiceInfo) -> {
			if (invoiceInfo.getType() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format("type")) ;
		};
		
		public static final BiConsumer<AONContext, InvoiceInfo> EMPTY_STATUS = (ctx, invoiceInfo) -> {
			if (invoiceInfo.getStatus() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format("status")) ;
		};
		
		public static void validate(AONContext ctx, InvoiceInfo invoiceInfo) throws AonCoreException {
				EMPTY_DOMAIN
				.andThen(EMPTY_INVOICE)
				.andThen(EMPTY_TYPE)
				.andThen(EMPTY_STATUS)
				.accept(ctx, invoiceInfo);
		}
		
		public static final BiConsumer<AONContext, InvoiceInfo> COMPLETE_STATUS = (ctx, invoiceInfo) -> {
			if(invoiceInfo.getStatus() == null) {
				invoiceInfo.setStatus(InvoiceCommunicationStatus.PENDING);
			}
		};

		public static void autoComplete(AONContext ctx, InvoiceInfo invoiceInfo) throws AonCoreException {
			COMPLETE_STATUS
			.accept(ctx, invoiceInfo);

		}
		
	}
}
