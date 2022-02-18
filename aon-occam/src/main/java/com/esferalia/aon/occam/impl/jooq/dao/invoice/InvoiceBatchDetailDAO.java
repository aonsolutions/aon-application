package com.esferalia.aon.occam.impl.jooq.dao.invoice;

import static com.esferalia.aon.jooq.tables.InvoiceBatchDetail.INVOICE_BATCH_DETAIL;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.InvoiceBatchDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.InvoiceBatchDetailProperties;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatchDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.FilterDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class InvoiceBatchDetailDAO {
	
	private InvoiceBatchDetailDAO() {
	
	}
	
	private static final InvoiceBatchDetailPropertiesDAO INVOICE_BATCH_DETAIL_PROPERTIES = new InvoiceBatchDetailPropertiesDAO();
	public static class InvoiceBatchDetailPropertiesDAO implements InvoiceBatchDetailProperties {
		
		public Condition[] getConditions(InvoiceBatchDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_BATCH_DETAIL.ID);}
		@Override public Property<Integer> getDomainProperty(){return new FilterDAO.PropertyDAO<>(INVOICE_BATCH_DETAIL.DOMAIN);}
		@Override public Property<Integer> getInvoiceProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_BATCH_DETAIL.INVOICE);}
		@Override public Property<Integer> getInvoiceBatchProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_BATCH_DETAIL.INVOICE_BATCH);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_BATCH_DETAIL.STATUS);}
	}
	
	
	public static SelectConditionStep<Record> select(AONContext ctx, InvoiceBatchDetailFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(INVOICE_BATCH_DETAIL)
				.where(INVOICE_BATCH_DETAIL_PROPERTIES.getConditions(filter));
	}
	
	public static InvoiceBatchDetail get(AONContext ctx, InvoiceBatchDetailFilter filter) {
		return select(ctx, filter).limit(1)
			.fetch().stream().map(new InvoiceBatchDetailFiller())
			.findFirst().orElse(new InvoiceBatchDetail());
	}
	
	public static InvoiceBatchDetail save(AONContext ctx, InvoiceBatchDetail invoiceBatchDetail) {
		InvoiceBatchDetailValidation.validate(ctx, invoiceBatchDetail);
		invoiceBatchDetail = invoiceBatchDetail.getId() != null 
			? update(ctx, invoiceBatchDetail)
			: insert(ctx, invoiceBatchDetail);
		return invoiceBatchDetail;
	}
	
	public static InvoiceBatchDetail update(AONContext ctx, InvoiceBatchDetail invoiceBatchDetail) {
		ctx.getDslContext().update(INVOICE_BATCH_DETAIL)
		.set(INVOICE_BATCH_DETAIL.DOMAIN, invoiceBatchDetail.getDomain())
		.set(INVOICE_BATCH_DETAIL.INVOICE, invoiceBatchDetail.getInvoice())
		.set(INVOICE_BATCH_DETAIL.INVOICE_BATCH, invoiceBatchDetail.getInvoice())
		.set(INVOICE_BATCH_DETAIL.STATUS, invoiceBatchDetail.getStatus().value())
		.where(INVOICE_BATCH_DETAIL.ID.eq(invoiceBatchDetail.getId()))
		.execute();
		return invoiceBatchDetail;
	}
	
	public static InvoiceBatchDetail insert(AONContext ctx, InvoiceBatchDetail invoiceBatchDetail) {
		Integer id = ctx.getDslContext().insertInto(INVOICE_BATCH_DETAIL)
				.set(INVOICE_BATCH_DETAIL.DOMAIN, invoiceBatchDetail.getDomain())
				.set(INVOICE_BATCH_DETAIL.INVOICE, invoiceBatchDetail.getInvoice())
				.set(INVOICE_BATCH_DETAIL.INVOICE_BATCH, invoiceBatchDetail.getInvoiceBatch())
				.set(INVOICE_BATCH_DETAIL.STATUS, invoiceBatchDetail.getStatus().value())
			.returning(INVOICE_BATCH_DETAIL.ID).fetchOne().getId();
		return invoiceBatchDetail.setId(id);
	}	

	public static void delete(AONContext ctx, Integer id){
		delete(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()).and(f.getIdProperty().eq(id)));
	}
	
	public static void delete(AONContext ctx, InvoiceBatchDetailFilter filter){
		ctx.getDslContext().delete(INVOICE_BATCH_DETAIL)
		.where(INVOICE_BATCH_DETAIL_PROPERTIES.getConditions(filter))
		.execute();
	}
	
	public static class InvoiceBatchDetailFiller extends Filler implements Function<Record, InvoiceBatchDetail> {

		@Override
		public InvoiceBatchDetail apply(Record r) {
			return build(r);
		}
		
		public static InvoiceBatchDetail build(Record r) {
			return new InvoiceBatchDetail()
				.setId(r.getValue(INVOICE_BATCH_DETAIL.ID))
				.setDomain(r.getValue(INVOICE_BATCH_DETAIL.DOMAIN))
				.setInvoice(r.getValue(INVOICE_BATCH_DETAIL.INVOICE))
				.setInvoiceBatch(r.getValue(INVOICE_BATCH_DETAIL.INVOICE_BATCH))
				.setStatus(InvoiceCommunicationStatus.safeValueOf(r.getValue(INVOICE_BATCH_DETAIL.STATUS)));
		}
	}
	
	private static class InvoiceBatchDetailValidation {
		
		private InvoiceBatchDetailValidation() {
	
		}
		
		public static final BiConsumer<AONContext, InvoiceBatchDetail> EMPTY_DOMAIN = (ctx, invoiceBatchDetail) -> {
			if (invoiceBatchDetail.getDomain() == null) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		public static final BiConsumer<AONContext, InvoiceBatchDetail> EMPTY_INVOICE = (ctx, invoiceBatchDetail) -> {
			if (invoiceBatchDetail.getInvoice() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format("invoice")) ;
		};
		
		public static final BiConsumer<AONContext, InvoiceBatchDetail> EMPTY_INVOICE_BATCH = (ctx, invoiceBatchDetail) -> {
			if (invoiceBatchDetail.getInvoiceBatch() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format("invoice batch")) ;
		};
		
		public static final BiConsumer<AONContext, InvoiceBatchDetail> EMPTY_STATUS = (ctx, invoiceBatchDetail) -> {
			if (invoiceBatchDetail.getStatus() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format("status")) ;
		};
		
		public static void validate(AONContext ctx, InvoiceBatchDetail invoiceBatchDetail) throws AonCoreException {
				EMPTY_DOMAIN
				.andThen(EMPTY_INVOICE)
				.andThen(EMPTY_INVOICE_BATCH)
				.andThen(EMPTY_STATUS)
				.accept(ctx, invoiceBatchDetail);
		}

	}
}
