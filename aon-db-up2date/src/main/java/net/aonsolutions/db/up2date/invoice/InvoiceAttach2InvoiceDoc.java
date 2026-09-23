package net.aonsolutions.db.up2date.invoice;

import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceAttach.INVOICE_ATTACH;
import static com.esferalia.aon.jooq.tables.InvoiceDoc.INVOICE_DOC;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.code.aon.finance.enumeration.InvoiceAttachmentType;
import com.code.aon.finance.enumeration.InvoiceType;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.InvoiceAttachRecord;
import com.esferalia.aon.jooq.tables.records.InvoiceDocRecord;
import com.esferalia.aon.jooq.tables.records.InvoiceRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.db.up2date.Update;
import solutions.aon.aws.s3.S3;

public class InvoiceAttach2InvoiceDoc implements Update {

	private static final Logger LOGGER = Logger.getLogger(InvoiceAttach2InvoiceDoc.class.getName());
	
	public static final InvoiceAttach2InvoiceDoc INVOICEATTACH2INVOICEDOC = 
			new InvoiceAttach2InvoiceDoc(getWhere());

	private Condition condition ;
	
	private InvoiceAttach2InvoiceDoc(Condition condition) {
		this.condition = condition;
	}

	@Override
	public void upgrade(Connection connection) {
		String s3Bucket = getBucket();

		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		
		DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		String currentSchema = dslContext.dsl().select(DSL.currentSchema()).fetchOneInto(String.class);
		if ( !getDatabasePattern().matcher(currentSchema).matches() ) {
			LOGGER.info(String.format("currentSchema=%1$s, not matching database pattern, skipping", currentSchema));
			return;
		}
				
		dslContext.dsl()
		.select(
			DOMAIN.ID,
			DOMAIN.NAME,
			DOMAIN.ACTIVE,
			DOMAIN.LASTACCESS_DATE,
			INVOICE.ID,
			INVOICE.TYPE,
			INVOICE.RDOCUMENT,
			INVOICE.ISSUE_DATE,
			REGISTRY.ID,
			REGISTRY.DOCUMENT,
			INVOICE_ATTACH.ID,
			INVOICE_ATTACH.TYPE,	
			INVOICE_ATTACH.MIMETYPE,	
			INVOICE_ATTACH.ATTACH_DATE,	
			INVOICE_ATTACH.DESCRIPTION	
		)
		.from(DOMAIN)
		.innerJoin(COMPANY).on(DOMAIN.ID.eq(COMPANY.DOMAIN))
		.innerJoin(INVOICE).on(DOMAIN.ID.eq(INVOICE.DOMAIN))
		.leftJoin(REGISTRY).on(INVOICE.REGISTRY.eq(REGISTRY.ID))
		.innerJoin(INVOICE_ATTACH).on(INVOICE.ID.eq(INVOICE_ATTACH.INVOICE))
		.where(condition)
		.and(INVOICE_ATTACH.ID.ge(0))
		.and(INVOICE_ATTACH.TYPE.eq((byte)InvoiceAttachmentType.INVOICE.ordinal()))
		.fetchLazy()
		.forEach( record -> {
			DomainRecord domain = record.into(DOMAIN);
			InvoiceRecord invoice = record.into(INVOICE);
			RegistryRecord registry = record.into(REGISTRY);
			InvoiceAttachRecord attach = record.into(INVOICE_ATTACH);
			
			LOGGER.info(String.format("domain=%1$s, invoice=%2$s, attach=%3$s, processing", domain.getName(), invoice.getId(), attach.getId()));
			
			byte[] data = dslContext.select().from(INVOICE_ATTACH).where(INVOICE_ATTACH.ID.eq(attach.getId())).fetchOne(INVOICE_ATTACH.DATA);
			if ( data == null || data.length == 0 ) {
				LOGGER.finest(String.format("domain=%1$s, invoice=%2$s, attach=%3$s, data is null or empty", domain.getName(), invoice.getId(), attach.getId()));
				return;
			}

			LOGGER.info(String.format("domain=%1$s, invoice=%2$s, attach=%3$s, data length=%4$d", domain.getName(), invoice.getId(), attach.getId(), data.length));
			
			
			String s3Key = String.format(
					"%1$s/%2$s/%3$s/%4$tF/%5$s", 
					domain.getName(), 
					getType(invoice),
					getDocument(invoice, registry),
					invoice.getIssueDate(),
					md5(data));
			
			if ( getTry() ) {
				LOGGER.info(String.format("domain=%1$s, invoice=%2$s, attach=%3$s, s3Key=%4$s, try mode, not uploading", domain.getName(), invoice.getId(), attach.getId(), s3Key));
				return;
			}
			
			LOGGER.info(String.format("domain=%1$s, invoice=%2$s, attach=%3$s, s3Key=%4$s, uploading", domain.getName(), invoice.getId(), attach.getId(), s3Key));
			
			dslContext.transaction( config -> {
				
				Map<String, String> metadata = new HashMap<>();
				metadata.put("INVOICE_ATTACH", Integer.toString(attach.getId()));
				
				upload(s3Bucket, s3Key, data, metadata);
				
				InvoiceDocRecord doc =
				config.dsl()
				.select()
				.from(INVOICE_DOC)
				.where(INVOICE_DOC.S3_KEY.eq(s3Key))
				.and(INVOICE_DOC.INVOICE.eq(invoice.getId()))
				.fetchOptionalInto(INVOICE_DOC)
				.orElseGet(() -> config.dsl().newRecord(INVOICE_DOC));

				doc.setS3Key(s3Key);
				doc.setS3Bucket(s3Bucket);
				doc.setDomain(domain.getId());
				doc.setType(attach.getType());
				doc.setInvoice(invoice.getId());
				doc.setMimetype(attach.getMimetype());
				doc.setAttachDate(attach.getAttachDate());
				doc.setDescription(attach.getDescription());
				doc.store();

				config.dsl().delete(INVOICE_ATTACH)
				.where(INVOICE_ATTACH.ID.eq(attach.getId()))
				.execute();

//				config.dsl().update(INVOICE_ATTACH)
//				.set(INVOICE_ATTACH.ID, -doc.getId())
//				.set(INVOICE_ATTACH.DATA, s3Key.getBytes())
//				.where(INVOICE_ATTACH.ID.eq(attach.getId()))
//				.execute();
				
			});
			
			LOGGER.info(String.format("domain=%1$s, invoice=%2$s, attach=%3$s, s3Key=%4$s, uploaded", domain.getName(), invoice.getId(), attach.getId(), s3Key));
			
		});
		
	
	}
	
	private static void upload(String s3Bucket, String s3Key, byte[] data, Map<String, String> metadata) {
		S3 s3 = S3.getInstance();
		if ( !s3.existBucket(s3Bucket))
			s3.createBucket(s3Bucket);
		if ( !s3.existObject(s3Bucket, s3Key))
			s3.upload(s3Bucket, s3Key, data, metadata);
	}

	private static boolean getTry() {
		return Boolean.getBoolean("try");
	}

	private static String getBucket() {
		return System.getProperty("bucket", "aon-invoice-doc");
	}

	private static Condition getWhere() {
		return DSL.condition(System.getProperty("where", " 1 = 2 "));
	}
		
	private static Pattern getDatabasePattern() {
		return Pattern.compile(System.getProperty("database", "none database"), Pattern.DOTALL);
	}

	private static String md5(byte [] data)  {
		try {
			MessageDigest md5Digest = MessageDigest.getInstance("MD5");
			BigInteger md5Integer = new BigInteger(1, md5Digest.digest(data)); 
			return String.format("%1$032x", md5Integer);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	

	private static String getDocument(InvoiceRecord invoice, RegistryRecord registry) {
		String rDocument = invoice.getRdocument();
		if ( AonStringUtils.isNotBlank(rDocument) ) 
			return rDocument;
		if ( registry == null ) 
			return "unknown";
		rDocument = registry.getDocument();
		if ( AonStringUtils.isNotBlank(rDocument) ) 
			return rDocument;
		
		return "unknown";
	}

	private static String getType(InvoiceRecord invoice) {
		return getName(InvoiceType.class, invoice.getType());
	}
	
	private static <E extends Enum<?>, N extends Number> String getName(Class<E> enumClass, N value) {
		return getName(enumClass, value, "unknown");
	}
	
	private static <E extends Enum<?>, N extends Number> String getName(Class<E> enumClass, N value, String defaultValue) {
		return Arrays.stream(enumClass.getEnumConstants())
				.filter( e -> e.ordinal() == value.intValue())
				.findFirst().map(Enum::name)
				.map(String::toLowerCase)
				.orElse(defaultValue);
	}
	
	public static void main(String[] args) {
		System.out.println(getTry());
	}
	

}
