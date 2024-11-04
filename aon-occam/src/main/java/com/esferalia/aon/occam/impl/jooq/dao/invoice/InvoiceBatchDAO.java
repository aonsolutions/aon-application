package com.esferalia.aon.occam.impl.jooq.dao.invoice;

import static com.esferalia.aon.jooq.tables.InvoiceBatch.INVOICE_BATCH;

import java.sql.Timestamp;
import java.util.Date;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.InvoiceBatchFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.InvoiceBatchProperties;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatch;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.FilterDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;

public class InvoiceBatchDAO {
	
	private InvoiceBatchDAO() {
	
	}
	
	private static final InvoiceBatchPropertiesDAO INVOICE_BATCH_PROPERTIES = new InvoiceBatchPropertiesDAO();
	public static class InvoiceBatchPropertiesDAO implements InvoiceBatchProperties {
		
		public Condition[] getConditions(InvoiceBatchFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_BATCH.ID);}
		@Override public Property<Integer> getDomainProperty(){return new FilterDAO.PropertyDAO<>(INVOICE_BATCH.DOMAIN);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_BATCH.DESCRIPTION);}
		@Override public Property<Timestamp> getDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_BATCH.DATE);}
		@Override public Property<Timestamp> getEndDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_BATCH.END_DATE);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_BATCH.TYPE);}
		@Override public Property<Byte> getOperationProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_BATCH.OPERATION);}
		@Override public Property<Integer> getDataResponseProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_BATCH.DATA_RESPONSE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_BATCH.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_BATCH.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_BATCH.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_BATCH.MODIFICATION_DATE);}
	}
	
	public static SelectConditionStep<Record> select(AONContext ctx, InvoiceBatchFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(INVOICE_BATCH)
				.where(INVOICE_BATCH_PROPERTIES.getConditions(filter));
	}
	
	public static InvoiceBatch get(AONContext ctx, InvoiceBatchFilter filter) {
		return select(ctx, filter).limit(1)
			.fetch().stream().map(new InvoiceBatchFiller())
			.findFirst().orElse(new InvoiceBatch());
	}
	
	public static Stream<InvoiceBatch> getStream(AONContext ctx, InvoiceBatchFilter filter) {
		return select(ctx, filter)
			.fetch().stream().map(new InvoiceBatchFiller());
	}
	
	public static InvoiceBatch save(AONContext ctx, InvoiceBatch invoiceBatch) {
		InvoiceBatchValidation.autoComplete(ctx, invoiceBatch);
		InvoiceBatchValidation.validate(ctx, invoiceBatch);
		return invoiceBatch.getId() != null 
			? update(ctx, invoiceBatch)
			: insert(ctx, invoiceBatch);
	}
	
	public static InvoiceBatch update(AONContext ctx, InvoiceBatch invoiceBatch) {
		ctx.getDslContext().update(INVOICE_BATCH)
		.set(INVOICE_BATCH.DOMAIN, invoiceBatch.getDomain())		
		.set(INVOICE_BATCH.DESCRIPTION, invoiceBatch.getDescription())
		.set(INVOICE_BATCH.DATE, AonDateUtils.toTimestamp(invoiceBatch.getDate()))
		.set(INVOICE_BATCH.END_DATE, AonDateUtils.toTimestamp(invoiceBatch.getEndDate()))
		.set(INVOICE_BATCH.TYPE, invoiceBatch.getType().value())
		.set(INVOICE_BATCH.OPERATION, invoiceBatch.getOperation().value())
		.set(INVOICE_BATCH.DATA_RESPONSE, invoiceBatch.getDataResponse())
		.set(INVOICE_BATCH.MD5, invoiceBatch.getMd5())
		.set(INVOICE_BATCH.MODIFICATION_USER, ctx.getUser())
		.set(INVOICE_BATCH.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
		.where(INVOICE_BATCH.ID.eq(invoiceBatch.getId()))
		.execute();
		return invoiceBatch;
	}
	
	public static InvoiceBatch insert(AONContext ctx, InvoiceBatch invoiceBatch) {
		Integer id = ctx.getDslContext().insertInto(INVOICE_BATCH)
				.set(INVOICE_BATCH.DOMAIN, invoiceBatch.getDomain())
				.set(INVOICE_BATCH.DESCRIPTION, invoiceBatch.getDescription())
				.set(INVOICE_BATCH.DATE, AonDateUtils.toTimestamp(invoiceBatch.getDate()))
				.set(INVOICE_BATCH.END_DATE, AonDateUtils.toTimestamp(invoiceBatch.getEndDate()))
				.set(INVOICE_BATCH.TYPE, invoiceBatch.getType().value())
				.set(INVOICE_BATCH.OPERATION, invoiceBatch.getOperation().value())
				.set(INVOICE_BATCH.DATA_RESPONSE, invoiceBatch.getDataResponse())
				.set(INVOICE_BATCH.MD5, invoiceBatch.getMd5())
				.set(INVOICE_BATCH.CREATION_USER, ctx.getUser())
				.set(INVOICE_BATCH.CREATION_DATE, new Timestamp( System.currentTimeMillis()))
			.returning(INVOICE_BATCH.ID).fetchOne().getId();
		return invoiceBatch.setId(id);
	}	

	public static void delete(AONContext ctx, Integer id){
		delete(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()).and(f.getIdProperty().eq(id)));
	}
	
	public static void delete(AONContext ctx, InvoiceBatchFilter filter) {
		ctx.getDslContext().delete(INVOICE_BATCH)
		.where(INVOICE_BATCH_PROPERTIES.getConditions(filter))
		.execute();
	}
	
	public static class InvoiceBatchFiller extends Filler implements Function<Record, InvoiceBatch> {

		@Override
		public InvoiceBatch apply(Record r) {
			return build(r);
		}
		
		public static InvoiceBatch build(Record r) {
			return new InvoiceBatch()
				.setId(getValue(r, INVOICE_BATCH.ID))
				.setDescription(getValue(r, INVOICE_BATCH.DESCRIPTION))
				.setDomain(getValue(r, INVOICE_BATCH.DOMAIN))
				.setDate(getValue(r, INVOICE_BATCH.DATE))
				.setEndDate(getValue(r, INVOICE_BATCH.END_DATE))
				.setType(InvoiceCommunicationType.safeValueOf(getValue(r, INVOICE_BATCH.TYPE)))
				.setOperation(InvoiceCommunicationOperation.safeValueOf(getValue(r, INVOICE_BATCH.OPERATION)))
				.setDataResponse(getValue(r, INVOICE_BATCH.DATA_RESPONSE))
				.setMd5(getValue(r, INVOICE_BATCH.MD5))
				.setCreationUser(getValue(r, INVOICE_BATCH.CREATION_USER))
				.setCreationDate(getValue(r, INVOICE_BATCH.CREATION_DATE))
				.setModificationUser(getValue(r, INVOICE_BATCH.MODIFICATION_USER))
				.setModificationDate(getValue(r, INVOICE_BATCH.MODIFICATION_DATE));
		}
	}
	
	private static class InvoiceBatchValidation {
		
		private InvoiceBatchValidation() {
	
		}
		
		public static final BiConsumer<AONContext, InvoiceBatch> EMPTY_DOMAIN = (ctx, invoiceBatch) -> {
			if (invoiceBatch.getDomain() == null) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		public static final BiConsumer<AONContext, InvoiceBatch> EMPTY_TYPE = (ctx, invoiceBatch) -> {
			if (invoiceBatch.getType() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format("type")) ;
		};
		
		public static final BiConsumer<AONContext, InvoiceBatch> EMPTY_OPERATION = (ctx, invoiceBatch) -> {
			if (invoiceBatch.getOperation() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format("operation")) ;
		};
		
		public static void validate(AONContext ctx, InvoiceBatch invoiceBatch) throws AonCoreException {
				EMPTY_DOMAIN
				.andThen(EMPTY_TYPE)
				.andThen(EMPTY_OPERATION)
				.accept(ctx, invoiceBatch);
		}
		
		public static final BiConsumer<AONContext, InvoiceBatch> COMPLETE_DATE = (ctx, invoiceBatch) -> {
			if(invoiceBatch.getDate() == null) {
				invoiceBatch.setDate(new Date());
			}
		};

		public static void autoComplete(AONContext ctx, InvoiceBatch invoiceBatch) throws AonCoreException {
			COMPLETE_DATE
			.accept(ctx, invoiceBatch);

		}
		
	}
}
