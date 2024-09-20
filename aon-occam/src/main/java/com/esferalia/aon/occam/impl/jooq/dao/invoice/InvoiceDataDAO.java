package com.esferalia.aon.occam.impl.jooq.dao.invoice;

import static com.esferalia.aon.jooq.tables.InvoiceData.INVOICE_DATA;

import java.sql.Date;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.InvoiceDataFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.InvoiceDataProperties;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceData;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.FilterDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;

public class InvoiceDataDAO {
	
	private InvoiceDataDAO() {
	
	}
	
	private static final InvoiceDataPropertiesDAO INVOICE_DATA_PROPERTIES = new InvoiceDataPropertiesDAO();
	public static class InvoiceDataPropertiesDAO implements InvoiceDataProperties {
		
		public Condition[] getConditions(InvoiceDataFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DATA.ID);}
		@Override public Property<Integer> getDomainProperty(){return new FilterDAO.PropertyDAO<>(INVOICE_DATA.DOMAIN);}
		@Override public Property<Integer> getInvoiceProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DATA.INVOICE);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DATA.NAME);}
		@Override public Property<String> getValueProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DATA.VALUE);}
		@Override public Property<Date> getStartDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DATA.START_DATE);}
		@Override public Property<Date> getEndDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DATA.END_DATE);}

	}
	
	
	public static SelectConditionStep<Record> select(AONContext ctx, InvoiceDataFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(INVOICE_DATA)
				.where(INVOICE_DATA_PROPERTIES.getConditions(filter));
	}
	
	public static InvoiceData get(AONContext ctx, InvoiceDataFilter filter) {
		return select(ctx, filter).limit(1)
			.fetch().stream().map(new InvoiceDataFiller())
			.findFirst().orElse(new InvoiceData());
	}
	
	public static InvoiceData save(AONContext ctx, InvoiceData invoiceData) {
		InvoiceDataValidation.validate(ctx, invoiceData);
		Invoice inv = InvoiceDAO.getInvoice(ctx, invoiceData.getInvoice());
		if(inv != null && inv.getId() != null) {
			invoiceData = invoiceData.getId() != null 
					? update(ctx, invoiceData)
					: insert(ctx, invoiceData);
		}
		return invoiceData;
	}
	
	public static InvoiceData update(AONContext ctx, InvoiceData invoiceData) {
		ctx.getDslContext().update(INVOICE_DATA)
		.set(INVOICE_DATA.DOMAIN, invoiceData.getDomain())
		.set(INVOICE_DATA.INVOICE, invoiceData.getInvoice())
		.set(INVOICE_DATA.NAME, invoiceData.getName())
		.set(INVOICE_DATA.VALUE, invoiceData.getValue())
		.set(INVOICE_DATA.START_DATE,invoiceData.getStartDate() != null
			? AonDateUtils.toSql(invoiceData.getStartDate())
			: AonDateUtils.toSql(new java.util.Date()))
		.set(INVOICE_DATA.END_DATE,AonDateUtils.toSql(invoiceData.getEndDate()))
		.where(INVOICE_DATA.ID.eq(invoiceData.getId()))
		.execute();
		return invoiceData;
	}
	
	public static InvoiceData insert(AONContext ctx, InvoiceData invoiceData) {
		Integer id = ctx.getDslContext().insertInto(INVOICE_DATA)
				.set(INVOICE_DATA.DOMAIN, invoiceData.getDomain())
				.set(INVOICE_DATA.INVOICE, invoiceData.getInvoice())
				.set(INVOICE_DATA.NAME, invoiceData.getName())
				.set(INVOICE_DATA.VALUE, invoiceData.getValue())
				.set(INVOICE_DATA.START_DATE,invoiceData.getStartDate() != null
					? AonDateUtils.toSql(invoiceData.getStartDate())
					: AonDateUtils.toSql(new java.util.Date()))
				.set(INVOICE_DATA.END_DATE,AonDateUtils.toSql(invoiceData.getEndDate()))
			.returning(INVOICE_DATA.ID).fetchOne().getId();
		return invoiceData.setId(id);
	}	

	public static void delete(AONContext ctx, Integer id){
		delete(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()).and(f.getIdProperty().eq(id)));
	}
	
	public static void delete(AONContext ctx, InvoiceDataFilter filter){
		ctx.getDslContext().delete(INVOICE_DATA)
		.where(INVOICE_DATA_PROPERTIES.getConditions(filter))
		.execute();
	}
	
	public static class InvoiceDataFiller extends Filler implements Function<Record, InvoiceData> {

		@Override
		public InvoiceData apply(Record r) {
			return build(r);
		}
		
		public static InvoiceData build(Record r) {
			return new InvoiceData()
				.setId(r.getValue(INVOICE_DATA.ID))
				.setDomain(r.getValue(INVOICE_DATA.DOMAIN))
				.setInvoice(r.getValue(INVOICE_DATA.INVOICE))
				.setName(getValue(r, INVOICE_DATA.NAME))
				.setValue(getValue(r, INVOICE_DATA.VALUE))
				.setStartDate(getValue(r, INVOICE_DATA.START_DATE))
				.setEndDate(getValue(r, INVOICE_DATA.END_DATE))
				;
		}
	}
	
	private static class InvoiceDataValidation {
		
		private InvoiceDataValidation() {
	
		}
		
		public static final BiConsumer<AONContext, InvoiceData> EMPTY_DOMAIN = (ctx, invoiceData) -> {
			if (invoiceData.getDomain() == null) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		public static final BiConsumer<AONContext, InvoiceData> EMPTY_INVOICE = (ctx, invoiceData) -> {
			if (invoiceData.getInvoice() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format("invoice")) ;
		};

		public static final BiConsumer<AONContext, InvoiceData> EMPTY_NAME = (ctx, invoiceData) -> {
			if (invoiceData.getName() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format("name")) ;
		};
	
		public static final BiConsumer<AONContext, InvoiceData> EMPTY_VALUE = (ctx, invoiceData) -> {
			if (invoiceData.getValue() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format("value")) ;
		};
		
		public static void validate(AONContext ctx, InvoiceData invoiceData) throws AonCoreException {
				EMPTY_DOMAIN
				.andThen(EMPTY_INVOICE)
				.andThen(EMPTY_NAME)
				.andThen(EMPTY_VALUE)
				.accept(ctx, invoiceData);
		}	
	}
}
