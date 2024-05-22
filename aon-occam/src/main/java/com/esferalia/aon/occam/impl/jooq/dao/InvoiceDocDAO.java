package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDoc.INVOICE_DOC;

import java.sql.Date;
import java.util.Optional;
import java.util.function.Function;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.InvoiceDocFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.InvoiceDocProperties;
import com.esferalia.aon.occam.api.model.attachment.InvoiceAttachmentType;
import com.esferalia.aon.occam.api.model.doc.InvoiceDoc;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;

public class InvoiceDocDAO {
	
	private InvoiceDocDAO() {
		
	}
	
	protected static class InvoiceDocPropertiesDAO implements InvoiceDocProperties{
		protected Select<Record> build(SelectJoinStep<Record> select, InvoiceDocFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(InvoiceDocFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DOC.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DOC.DOMAIN);}
		@Override public Property<Integer> getInvoiceProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DOC.INVOICE);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DOC.DESCRIPTION);}
 		@Override public Property<Date> getDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DOC.ATTACH_DATE);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DOC.TYPE);}
		@Override public Property<Byte> getMimeTypeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DOC.MIMETYPE);}
	}
	
	private static final InvoiceDocPropertiesDAO PROPERTIES_DAO = new InvoiceDocPropertiesDAO();
	
	public  static Optional<InvoiceDoc> get(AONContext ctx, InvoiceDocFilter filter ) {
		return ctx.getDslContext().select()
				.from(INVOICE_DOC)
				.where(PROPERTIES_DAO.getConditions(filter))
				.limit(1)
				.fetch().stream().map(new InvoiceDocFiller()).findFirst();
	}
	
	public static InvoiceDoc save(AONContext ctx, InvoiceDoc doc) {
		return doc.getId() != null
				? update(ctx, doc)
				: insert(ctx, doc);
	}
	
	public static InvoiceDoc insert(AONContext ctx, InvoiceDoc doc) {
		Integer id =ctx.getDslContext().insertInto(INVOICE_DOC)
			.set(INVOICE_DOC.DOMAIN, doc.getDomain())
			.set(INVOICE_DOC.INVOICE, doc.getInvoice())
			.set(INVOICE_DOC.MIMETYPE, doc.getMimeType().value())
			.set(INVOICE_DOC.DESCRIPTION, doc.getDescription())
			.set(INVOICE_DOC.TYPE, doc.getType().value())
			.set(INVOICE_DOC.ATTACH_DATE, AonDateUtils.toSql(doc.getDate()))
			.set(INVOICE_DOC.S3_KEY, doc.getS3Key())
			.returning(INVOICE_DETAIL.ID).fetchOne().getId();
		return doc.setId(id);
	}
	
	public static InvoiceDoc update(AONContext ctx, InvoiceDoc doc) {
		ctx.getDslContext().update(INVOICE_DOC)
			.set(INVOICE_DOC.DOMAIN, doc.getDomain())
			.set(INVOICE_DOC.INVOICE, doc.getInvoice())
			.set(INVOICE_DOC.MIMETYPE, doc.getMimeType().value())
			.set(INVOICE_DOC.DESCRIPTION, doc.getDescription())
			.set(INVOICE_DOC.TYPE, doc.getType().value())
			.set(INVOICE_DOC.ATTACH_DATE, AonDateUtils.toSql(doc.getDate()))
			.set(INVOICE_DOC.S3_KEY, doc.getS3Key())
			.where(INVOICE_DOC.ID.eq(doc.getId()))
			.execute();
		return doc;
	}
	
	public static class InvoiceDocFiller extends Filler implements Function<Record, InvoiceDoc> {
		@Override
		public InvoiceDoc apply(Record r) {
			return build(r);
		}
		
		public static InvoiceDoc build(Record r) {
			return new InvoiceDoc()
					.setId(getValue(r, INVOICE_DOC.ID))
					.setDomain(getValue(r, INVOICE_DOC.DOMAIN))
					.setInvoice(getValue(r, INVOICE_DOC.INVOICE))
					.setDescription(getValue(r, INVOICE_DOC.DESCRIPTION))
					.setDate(getValue(r, INVOICE_DOC.ATTACH_DATE))
					.setMimeType(MimeType.safeValueOf(getByte(r, INVOICE_DOC.MIMETYPE)))
					.setType(InvoiceAttachmentType.safeValueOf(getByte(r, INVOICE_DOC.TYPE)))
					.setS3Key(getValue(r, INVOICE_DOC.S3_KEY));
		}
	}
}
