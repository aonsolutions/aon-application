package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.InvoiceAttach.INVOICE_ATTACH;
import static com.esferalia.aon.jooq.tables.InvoiceDoc.INVOICE_DOC;

import java.sql.Date;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.InvoiceDocFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.InvoiceDocProperties;
import com.esferalia.aon.occam.api.model.attachment.InvoiceAttachmentType;
import com.esferalia.aon.occam.api.model.doc.ExternalStorage;
import com.esferalia.aon.occam.api.model.doc.ExternalStorage.ExternalStorageVisitor;
import com.esferalia.aon.occam.api.model.doc.InvoiceDoc;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.http.AonURIBuilder;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceDocDAO {
	
	private InvoiceDocDAO() {
		
	}

	// ******************************************************************************
	// **************************************** [INVOICE_DOC OR INVOICE_ATTACH] *****
	// ******************************************************************************
	
	public static Optional<InvoiceDoc> get(AONContext ctx, int domain, Integer invoiceId) {
		ctx.checkRead();
		return getInvoiceDoc(ctx, domain, invoiceId)
			.or(() -> getInvoiceAttach(ctx, domain, invoiceId) )
		;
	}

	// ******************************************************************************
	// ********************************************************** [INVOICE_DOC] *****
	// ******************************************************************************

	private static final InvoiceDocPropertiesDAO PROPERTIES_DAO = new InvoiceDocPropertiesDAO();
	private static class InvoiceDocPropertiesDAO implements InvoiceDocProperties{
		
		private Condition getCondition(InvoiceDocFilter filter) {
			return Optional.ofNullable(filter.filter(this))
				.map( f -> (FilterDAO) f)
				.map( f -> f.getCondition())
				.orElse( DSL.noCondition())
			;
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DOC.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DOC.DOMAIN);}
		@Override public Property<Integer> getInvoiceProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DOC.INVOICE);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DOC.DESCRIPTION);}
 		@Override public Property<Date> getDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DOC.ATTACH_DATE);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DOC.TYPE);}
		@Override public Property<Byte> getMimeTypeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DOC.MIMETYPE);}
	}
	
	// ----------------------------------------------------------------------  [READ]
	private static SelectConditionStep<Record> selectInvoiceDoc(AONContext ctx, int domain) {
		return ctx.getDslContext().select()
			.from(INVOICE_DOC)
			.join(DOMAIN).on(DOMAIN.ID.eq(INVOICE_DOC.DOMAIN))
			.where(INVOICE_DOC.DOMAIN.eq(domain))
		;
	}
		
	private static Optional<InvoiceDoc> getInvoiceDoc(AONContext ctx, int domain, Integer invoiceId ) {
		return getInvoiceDoc(ctx, domain, f -> f.getInvoiceProperty().eq(invoiceId));
	}
	private static Optional<InvoiceDoc> getInvoiceDoc(AONContext ctx, int domain, InvoiceDocFilter filter ) {
		return selectInvoiceDoc(ctx, domain)
			.and(PROPERTIES_DAO.getCondition(filter))
			.and(INVOICE_DOC.TYPE.eq(InvoiceAttachmentType.INVOICE.value()))
			.limit(1)
			.fetch()
			.stream()
			.map(new InvoiceDocFiller())
			.findFirst()
		;
	}
	
	// ---------------------------------------------------------------------- [WRITE]
	public static InvoiceDoc save(AONContext ctx, InvoiceDoc doc) {
		ctx.checkWrite();
		InvoiceDocValidator.validate(ctx, doc);
		return doc.getId() != null
			? update(ctx, doc)
			: insert(ctx, doc);
	}
	
	private static InvoiceDoc insert(AONContext ctx, InvoiceDoc doc) {
		getInvoiceDoc(ctx, doc.getDomain(), doc.getInvoice())
			.ifPresentOrElse( 
				d -> update(ctx, doc.setId( d.getId()))
				,() -> {
					doc.setId( ctx.getDslContext().insertInto(INVOICE_DOC)
						.set(INVOICE_DOC.DOMAIN, doc.getDomain())
						.set(INVOICE_DOC.INVOICE, doc.getInvoice())
						.set(INVOICE_DOC.MIMETYPE, doc.getMimeType().value())
						.set(INVOICE_DOC.DESCRIPTION, doc.getDescription())
						.set(INVOICE_DOC.TYPE, doc.getType().value())
						.set(INVOICE_DOC.ATTACH_DATE, AonDateUtils.toSql(doc.getDate()))
						.set(INVOICE_DOC.S3_BUCKET, doc.getS3Bucket())
						.set(INVOICE_DOC.S3_KEY, doc.getS3Key())
						.returning(INVOICE_DOC.ID)
						.fetchOne()
						.getId()
					);
					ctx.log().debug("INSERT INVOICE DOC invoice: {0} id: {1}",doc.getInvoice(),doc.getId());
				}
		);
		return doc;
	}
	
	private static InvoiceDoc update(AONContext ctx, InvoiceDoc doc) {
		int i = ctx.getDslContext().update(INVOICE_DOC)
			.set(INVOICE_DOC.DOMAIN, doc.getDomain())
			.set(INVOICE_DOC.INVOICE, doc.getInvoice())
			.set(INVOICE_DOC.MIMETYPE, doc.getMimeType().value())
			.set(INVOICE_DOC.DESCRIPTION, doc.getDescription())
			.set(INVOICE_DOC.TYPE, doc.getType().value())
			.set(INVOICE_DOC.ATTACH_DATE, AonDateUtils.toSql(doc.getDate()))
			.set(INVOICE_DOC.S3_BUCKET, doc.getS3Bucket())
			.set(INVOICE_DOC.S3_KEY, doc.getS3Key())
			.where(INVOICE_DOC.ID.eq(doc.getId()))
			.execute();
		ctx.log().debug("UPDATE INVOICE DOC id: {0} ({1} rows)",doc.getId(),i);
		return doc;
	}

	public static void delete(AONContext ctx, Integer invoiceId) {
		ctx.checkWrite();
		int count = ctx.getDslContext()
			.delete(INVOICE_DOC)
			.where(INVOICE_DOC.INVOICE.equal(invoiceId))
			.execute();
		ctx.log().debug("DELETE INVOICE_DOC adjuntos de la factura: {0} ({1} filas)",invoiceId,count);
	}
	
	private static class InvoiceDocFiller extends Filler implements Function<Record, InvoiceDoc> {
		@Override
		public InvoiceDoc apply(Record r) {
			return build(r);
		}
		
		public static InvoiceDoc build(Record r) {
			if ( !checkField(r, INVOICE_DOC.ID) ) return null;
			InvoiceDoc doc = new InvoiceDoc()
				.setId(getValue(r, INVOICE_DOC.ID))
				.setDomain(getValue(r, INVOICE_DOC.DOMAIN))
				.setInvoice(getValue(r, INVOICE_DOC.INVOICE))
				.setDescription(getValue(r, INVOICE_DOC.DESCRIPTION))
				.setDate(getValue(r, INVOICE_DOC.ATTACH_DATE))
				.setMimeType(MimeType.safeValueOf(getByte(r, INVOICE_DOC.MIMETYPE)))
				.setType(InvoiceAttachmentType.safeValueOf(getByte(r, INVOICE_DOC.TYPE)))
				.setExternalStorage(ExternalStorage.AWS)
				.setS3Bucket(getValue(r, INVOICE_DOC.S3_BUCKET))
				.setS3Key(getValue(r, INVOICE_DOC.S3_KEY))
				;
			return doc
				.setUrl(AON.getShortURL("laburr", buildUrl(getValue(r, DOMAIN.NAME), doc)));
			
		}
	}

	private static String buildUrl(String domain, InvoiceDoc doc) {
		if (doc.getExternalStorage() == null) return null;
		AonURIBuilder builder = new AonURIBuilder()
			.setScheme("https")
			.setHost(domain)
			.setPath("/ms/api/doc")
			.setParameter(IJsonNames.DOMAIN_ID, AonNumberUtils.toString(doc.getDomain()))
			.setParameter(IJsonNames.SOURCE,"invoice")
		;
		return doc.getExternalStorage().visit( 
			new ExternalStorageVisitor<AonURIBuilder>() {
				@Override
				public AonURIBuilder visitAon() {
					return builder.setParameter(IJsonNames.AON_ID, AonNumberUtils.toString(doc.getAonId()));
				}
	
				@Override
				public AonURIBuilder visitDrive() {
					return builder.setParameter(IJsonNames.DRIVE_ID,doc.getDriveId());
				}
	
				@Override
				public AonURIBuilder visitAws() {
					return builder
						.setParameter(IJsonNames.AON_TABLE,doc.getAonTable())
						.setParameter(IJsonNames.S3_BUCKET,doc.getS3Bucket())
						.setParameter(IJsonNames.S3_KEY,doc.getS3Key());
				}
	
				@Override
				public AonURIBuilder visitScaleway() {
					return visitAws();
				}
			}
		).toString();
	}
/*
/*
	private static String buildUrl(String domain, InvoiceDoc doc) {
		StringBuilder builder = new StringBuilder();
		builder.append("https://" + domain +  "/ms/api/doc?domain=" + doc.getDomain());
		builder.append("&source=invoice");
		builder.append("&storage=" + doc.getExternalStorage().value());
		if(doc.getExternalStorage().isAws() || doc.getExternalStorage().isScaleway()) {
			builder.append("&aonTable=invoice_doc");
			builder.append("&s3Bucket=" + doc.getS3Bucket());
			builder.append("&s3Key=" + doc.getS3Key());
		} else if(doc.getExternalStorage().isDrive()) {
			builder.append("&driveId=" + doc.getDriveId());
		} else {
			builder.append("&aonId=" + doc.getAonId());
		}
		return builder.toString();
	}
*/
 
	private static class InvoiceDocValidator {
		
		private static record InvoiceDocValidatorContext(AONContext ctx, InvoiceDoc doc) {}
		
		private static final Consumer<InvoiceDocValidatorContext> EMPTY_DOMAIN = idc -> {
			if (idc.doc.getDomain() == null || idc.doc.getDomain() == 0) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};

		private static final Consumer<InvoiceDocValidatorContext> EMPTY_MIME_TYPE = idc -> {
			if (idc.doc.getMimeType() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format( IJsonNames.MIME_TYPE ));
		};

		private static final Consumer<InvoiceDocValidatorContext> EMPTY_TYPE = idc -> {
			if (idc.doc.getType() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format( IJsonNames.TYPE ));
		};

		private static final Consumer<InvoiceDocValidatorContext> EMPTY_INVOICE = idc -> {
			if (idc.doc.getInvoice() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format( IJsonNames.INVOICE ));
		};

		private static final Consumer<InvoiceDocValidatorContext> EMPTY_EXTERNAL_STORAGE = idc -> {
			if (idc.doc.getExternalStorage() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format( IJsonNames.EXTERNAL_STORAGE ));
		};

		private static final Consumer<InvoiceDocValidatorContext> VALID_EXTERNAL_STORAGE = idc -> 
			idc.doc.getExternalStorage().visit(new ExternalStorageVisitor<Void>() {
				@Override
				public Void visitAon() {
					if (idc.doc.getAonId() == null) {
						throw new AonCoreException(AonError.EMPTY_DATA.format( IJsonNames.AON_ID));
					}
					return null;
				}
	
				@Override
				public Void visitDrive() {
					if (AonStringUtils.isBlank(idc.doc.getDriveId())) {
						throw new AonCoreException(AonError.EMPTY_DATA.format( IJsonNames.DRIVE_ID));
					}
					return null;
				}
	
				@Override
				public Void visitAws() {
					if (AonStringUtils.isBlank(idc.doc.getS3Bucket())) {
						throw new AonCoreException(AonError.EMPTY_DATA.format( IJsonNames.S3_BUCKET));
					}
					if (AonStringUtils.isBlank(idc.doc.getS3Key())) {
						throw new AonCoreException(AonError.EMPTY_DATA.format( IJsonNames.S3_KEY));
					}
					return null;
				}
	
				@Override
				public Void visitScaleway() {
					return visitAws();
				}
			}
		);

		private static final Consumer<InvoiceDocValidatorContext> OVERFLOW_DESCRIPTION = idc -> {
			if (AonStringUtils.length( idc.doc.getDescription()) > INVOICE_DOC.DESCRIPTION.getDataType().length()) {
				throw new AonCoreException(AonError.INVALID_LENGTH.format(IJsonNames.DESCRIPTION,INVOICE_DOC.DESCRIPTION.getDataType().length()));
			}
		};
		
		public static void validate(AONContext ctx,InvoiceDoc doc) throws AonCoreException {
			EMPTY_DOMAIN
			.andThen(EMPTY_MIME_TYPE)
			.andThen(EMPTY_TYPE)
			.andThen(EMPTY_INVOICE)
			.andThen(EMPTY_EXTERNAL_STORAGE)
			.andThen(VALID_EXTERNAL_STORAGE)
			.andThen(OVERFLOW_DESCRIPTION)
			.accept(new InvoiceDocValidatorContext(ctx,doc));
		}

	}

	
	// ******************************************************************************
	// ******************************************************* [INVOICE_ATTACH] *****
	// ******************************************************************************
	
	private static Optional<InvoiceDoc> getInvoiceAttach(AONContext ctx, int domain, Integer invoiceId ) {
		return getInvoiceAttach(ctx, domain, f -> f.getInvoiceProperty().eq(invoiceId));
	}
	private static Optional<InvoiceDoc> getInvoiceAttach(AONContext ctx, int domain, InvoiceDocFilter filter ) {
		return ctx.getDslContext().select()
			.from(INVOICE_ATTACH)
			.join(DOMAIN).on(DOMAIN.ID.eq(INVOICE_ATTACH.DOMAIN))
			.where(INVOICE_ATTACH_PROPERTIES_DAO.getCondition(filter))
			.and( INVOICE_ATTACH.DOMAIN.eq(domain) )
			.and( INVOICE_ATTACH.TYPE.eq(InvoiceAttachmentType.INVOICE.value()) )
			.limit(1)
			.fetch()
			.stream()
			.map(new InvoiceAttachFiller())
			.findFirst()
		;
	}

	private static final InvoiceAttachPropertiesDAO INVOICE_ATTACH_PROPERTIES_DAO = new InvoiceAttachPropertiesDAO();
	private static class InvoiceAttachPropertiesDAO implements InvoiceDocProperties {
		protected Condition getCondition(InvoiceDocFilter filter) {
			return Optional.ofNullable(filter.filter(this))
				.map( f -> (FilterDAO) f)
				.map( f -> f.getCondition())
				.orElse( DSL.noCondition())
			;
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_ATTACH.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_ATTACH.DOMAIN);}
		@Override public Property<Integer> getInvoiceProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_ATTACH.INVOICE);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_ATTACH.DESCRIPTION);}
 		@Override public Property<Date> getDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_ATTACH.ATTACH_DATE);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_ATTACH.TYPE);}
		@Override public Property<Byte> getMimeTypeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_ATTACH.MIMETYPE);}
	}

	private static class InvoiceAttachFiller extends Filler implements Function<Record, InvoiceDoc> {
		@Override
		public InvoiceDoc apply(Record r) {
			return build(r);
		}
		
		public static InvoiceDoc build(Record r) {
			if ( !checkField(r, INVOICE_ATTACH.ID) ) return null;
			InvoiceDoc doc = new InvoiceDoc()
				.setDomain(getValue(r, INVOICE_ATTACH.DOMAIN))
				.setInvoice(getValue(r, INVOICE_ATTACH.INVOICE))
				.setDescription(getValue(r, INVOICE_ATTACH.DESCRIPTION))
				.setDate(getValue(r, INVOICE_ATTACH.ATTACH_DATE))
				.setMimeType(MimeType.safeValueOf(getByte(r, INVOICE_ATTACH.MIMETYPE)))
				.setType(InvoiceAttachmentType.safeValueOf(getByte(r, INVOICE_ATTACH.TYPE)))
				.setAonId(getValue(r, INVOICE_ATTACH.ID))
				.setDriveId(getValue(r, INVOICE_ATTACH.DRIVEID))
				.setExternalStorage(getValue(r, 
					INVOICE_ATTACH.DRIVEID) != null 
						? ExternalStorage.DRIVE 
						: ExternalStorage.AON)
			;
			doc.setUrl(AON.getShortURL("laburr", buildUrl(getValue(r, DOMAIN.NAME), doc)));
			return doc;
		}
	}

	
}
