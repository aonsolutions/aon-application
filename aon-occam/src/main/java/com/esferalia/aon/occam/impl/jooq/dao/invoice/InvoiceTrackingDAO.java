package com.esferalia.aon.occam.impl.jooq.dao.invoice;

import static com.esferalia.aon.jooq.tables.InvoiceBatch.INVOICE_BATCH;
import static com.esferalia.aon.jooq.tables.InvoiceBatchDetail.INVOICE_BATCH_DETAIL;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.InvoiceTrackingFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.InvoiceTrackingProperties;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatch;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatchDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTracking;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.FilterDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceBatchDAO.InvoiceBatchFiller;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceBatchDetailDAO.InvoiceBatchDetailFiller;

public class InvoiceTrackingDAO {
	
	private InvoiceTrackingDAO() {
	
	}
	
	private static final InvoiceTrackingPropertiesDAO INVOICE_TRACKING_PROPERTIES = new InvoiceTrackingPropertiesDAO();
	public static class InvoiceTrackingPropertiesDAO implements InvoiceTrackingProperties {
		
		public Condition[] getConditions(InvoiceTrackingFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}
		// INVOICE BATCH DETAIL
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_BATCH_DETAIL.ID);}
		@Override public Property<Integer> getDomainProperty(){return new FilterDAO.PropertyDAO<>(INVOICE_BATCH_DETAIL.DOMAIN);}
		@Override public Property<Integer> getInvoiceProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_BATCH_DETAIL.INVOICE);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_BATCH_DETAIL.STATUS);}
		
		// INVOICE BATCH
		@Override public Property<Timestamp> getDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_BATCH.DATE);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_BATCH.TYPE);}
		@Override public Property<Byte> getOperationProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_BATCH.OPERATION);}
		@Override public Property<Integer> getDataResponseProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_BATCH.DATA_RESPONSE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_BATCH.CREATION_USER);}
	}
	
	
	public static SelectConditionStep<Record> select(AONContext ctx, InvoiceTrackingFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(INVOICE_BATCH)
				.join(INVOICE_BATCH_DETAIL).on(INVOICE_BATCH.ID.eq(INVOICE_BATCH_DETAIL.INVOICE_BATCH))
				.where(INVOICE_TRACKING_PROPERTIES.getConditions(filter));
	}

	public static Stream<InvoiceTracking> getStream(AONContext ctx, InvoiceTrackingFilter filter) {
		return select(ctx, filter)
			.fetch().stream().map(new InvoiceTrackingFiller());
	}
	
	public static List<InvoiceTracking> getList(AONContext ctx, InvoiceTrackingFilter filter) {
		return select(ctx, filter)
			.fetch().stream().map(new InvoiceTrackingFiller())
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static InvoiceTracking get(AONContext ctx, InvoiceTrackingFilter filter) {
		return select(ctx, filter).limit(1)
			.fetch().stream().map(new InvoiceTrackingFiller())
			.findFirst().orElse(new InvoiceTracking());
	}
	
	public static InvoiceTracking save(AONContext ctx, InvoiceTracking invoiceTracking) {
		Invoice inv = InvoiceDAO.getInvoice(ctx, invoiceTracking.getInvoiceBatchDetail().getInvoice());
		if(inv != null && inv.getId() != null) {
			InvoiceBatch invoiceBatch = InvoiceBatchDAO.save(ctx, invoiceTracking.getInvoiceBatch());
			invoiceTracking.setInvoiceBatch(invoiceBatch);
		
			invoiceTracking.getInvoiceBatchDetail().setInvoiceBatch(invoiceBatch.getId());
			InvoiceBatchDetail invoiceBatchDetail = InvoiceBatchDetailDAO.save(ctx, invoiceTracking.getInvoiceBatchDetail());
			invoiceTracking.setInvoiceBatchDetail(invoiceBatchDetail);
		}
		return invoiceTracking;
	}
	
	public static void delete(AONContext ctx, Integer invoiceId) {
		Integer[] array = InvoiceBatchDetailDAO.getStream(ctx, f -> f.getInvoiceProperty().eq(invoiceId))
				.map(InvoiceBatchDetail::getInvoiceBatch)
				.toArray(Integer[]::new);
		InvoiceBatchDetailDAO.delete(ctx, f -> f.getInvoiceProperty().eq(invoiceId));
		InvoiceBatchDAO.delete(ctx, f -> f.getIdProperty().in(array));
	}

	public static class InvoiceTrackingFiller extends Filler implements Function<Record, InvoiceTracking> {

		@Override
		public InvoiceTracking apply(Record r) {
			return build(r);
		}
		
		public static InvoiceTracking build(Record r) {
			return new InvoiceTracking()
				.setInvoiceBatch(InvoiceBatchFiller.build(r))
				.setInvoiceBatchDetail(InvoiceBatchDetailFiller.build(r));
		}
	}
}
