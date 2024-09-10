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
import com.esferalia.aon.occam.api.model.Filter.InvoiceCommunicationTrackingFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.InvoiceCommunicationTrackingProperties;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatch;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatchDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationTracking;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.FilterDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceOLDDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceBatchDAO.InvoiceBatchFiller;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceBatchDetailDAO.InvoiceBatchDetailFiller;

public class InvoiceCommunicationTrackingDAO {
	
	private InvoiceCommunicationTrackingDAO() {
	
	}
	
	private static final InvoiceCommunicationTrackingPropertiesDAO INVOICE_COMMUNICATION_TRACKING_PROPERTIES = new InvoiceCommunicationTrackingPropertiesDAO();
	public static class InvoiceCommunicationTrackingPropertiesDAO implements InvoiceCommunicationTrackingProperties {
		
		public Condition[] getConditions(InvoiceCommunicationTrackingFilter filter) {
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
	
	
	public static SelectConditionStep<Record> select(AONContext ctx, InvoiceCommunicationTrackingFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(INVOICE_BATCH)
				.join(INVOICE_BATCH_DETAIL).on(INVOICE_BATCH.ID.eq(INVOICE_BATCH_DETAIL.INVOICE_BATCH))
				.where(INVOICE_COMMUNICATION_TRACKING_PROPERTIES.getConditions(filter));
	}

	public static Stream<InvoiceCommunicationTracking> getStream(AONContext ctx, InvoiceCommunicationTrackingFilter filter) {
		return select(ctx, filter)
			.fetch().stream().map(new InvoiceCommunicationTrackingFiller());
	}
	
	public static List<InvoiceCommunicationTracking> getList(AONContext ctx, InvoiceCommunicationTrackingFilter filter) {
		return select(ctx, filter)
			.fetch().stream().map(new InvoiceCommunicationTrackingFiller())
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static InvoiceCommunicationTracking get(AONContext ctx, InvoiceCommunicationTrackingFilter filter) {
		return select(ctx, filter).limit(1)
			.fetch().stream().map(new InvoiceCommunicationTrackingFiller())
			.findFirst().orElse(new InvoiceCommunicationTracking());
	}
	
	public static InvoiceCommunicationTracking save(AONContext ctx, InvoiceCommunicationTracking invoiceCommunicationTracking) {
		Invoice inv = InvoiceOLDDAO.getInvoice(ctx, invoiceCommunicationTracking.getInvoiceBatchDetail().getInvoice());
		if(inv != null && inv.getId() != null) {
			InvoiceBatch invoiceBatch = InvoiceBatchDAO.save(ctx, invoiceCommunicationTracking.getInvoiceBatch());
			invoiceCommunicationTracking.setInvoiceBatch(invoiceBatch);
		
			invoiceCommunicationTracking.getInvoiceBatchDetail().setInvoiceBatch(invoiceBatch.getId());
			InvoiceBatchDetail invoiceBatchDetail = InvoiceBatchDetailDAO.save(ctx, invoiceCommunicationTracking.getInvoiceBatchDetail());
			invoiceCommunicationTracking.setInvoiceBatchDetail(invoiceBatchDetail);
		}
		return invoiceCommunicationTracking;
	}
	
	public static void delete(AONContext ctx, Integer invoiceId) {
		Integer[] array = InvoiceBatchDetailDAO.getStream(ctx, f -> f.getInvoiceProperty().eq(invoiceId))
				.map(InvoiceBatchDetail::getInvoiceBatch)
				.toArray(Integer[]::new);
		InvoiceBatchDetailDAO.delete(ctx, f -> f.getInvoiceProperty().eq(invoiceId));
		InvoiceBatchDAO.delete(ctx, f -> f.getIdProperty().in(array));
	}

	public static class InvoiceCommunicationTrackingFiller extends Filler implements Function<Record, InvoiceCommunicationTracking> {

		@Override
		public InvoiceCommunicationTracking apply(Record r) {
			return build(r);
		}
		
		public static InvoiceCommunicationTracking build(Record r) {
			return new InvoiceCommunicationTracking()
				.setInvoiceBatch(InvoiceBatchFiller.build(r))
				.setInvoiceBatchDetail(InvoiceBatchDetailFiller.build(r));
		}
	}
}
