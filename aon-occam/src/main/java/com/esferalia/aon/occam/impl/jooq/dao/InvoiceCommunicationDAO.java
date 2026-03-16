package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.DataAttach.DATA_ATTACH;
import static com.esferalia.aon.jooq.tables.DataResponse.DATA_RESPONSE;
import static com.esferalia.aon.jooq.tables.DataResponseDetail.DATA_RESPONSE_DETAIL;
import static com.esferalia.aon.jooq.tables.EnterpriseData.ENTERPRISE_DATA;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceBatch.INVOICE_BATCH;
import static com.esferalia.aon.jooq.tables.InvoiceBatchDetail.INVOICE_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceInfo.INVOICE_INFO;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.json.JSONObject;

import com.esferalia.aon.jooq.tables.DataAttach;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseDataNames;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationHistory;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationParams;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DataRequestType;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.ExemptType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.EnterpriseDataDAO.EnterpriseDataFiller;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO.InvoiceFiller;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO.InvoiceInfoFiller;
import com.esferalia.aon.occam.impl.jooq.validation.InvoiceCommunicationConfigurationConsitency;
import com.esferalia.aon.occam.impl.jooq.validation.InvoiceCommunicationConfigurationValidation;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceCommunicationDAO {
	
	private static final DataAttach DATA_ATTACH_REQUEST = DATA_ATTACH.as("data_attach_request");
	private static final DataAttach DATA_ATTACH_RESPONSE = DATA_ATTACH.as("data_attach_response");
	private static final String TEST = "test";
	private static final String ADMINISTRATION = "administration";
	private static final String EXEMPT_TYPE = "exemptType";
	
	public static final String[] SUPPORTED_TYPES = new String[] {
		EnterpriseDataNames.ICC_ADMINISTRATION.name(),
		EnterpriseDataNames.ICC_LROE.name(),
		EnterpriseDataNames.ICC_SII.name(),
		EnterpriseDataNames.ICC_TBAI.name(),
		EnterpriseDataNames.ICC_VERIFACTU.name(),
		EnterpriseDataNames.ICC_NO_VERIFACTU.name(),
		EnterpriseDataNames.ICC_SIF.name(),
		EnterpriseDataNames.ICC_NO_SIF.name(),
	};
	
	private InvoiceCommunicationDAO() {

	}
	
	// ------------------------------------------------------------- 
	// ------------------------------------------------------ [READ]
	// ------------------------------------------------------------- 
	public static InvoiceCommunicationConfiguration get(AONContext ctx, int domainId) {
		ctx.checkRead();
		InvoiceCommunicationConfiguration config = new InvoiceCommunicationConfiguration();
		fillDatas(ctx, domainId, config);

		// Fecha de registro contable que se envía al LROE. Fecha de Auditoria (creation_date) o Fecha de IVA (tax_date)
		AppParamDAO.get(ctx, domainId, AppParam.TBAI_REGISTRY_DATE).ifPresent( p -> config.setLroeRegistryDate(p.getValue()));

		// Fecha de registro contable que se envía al SII. Fecha de Auditoria (creation_date) o Fecha de IVA (tax_date)
		AppParamDAO.get(ctx, domainId, AppParam.FS_MODEL_CFG_SII)
			.ifPresent( p -> config.setSiiRegistryDate("R".equalsIgnoreCase(p.getValue()) ? "audit" : "tax"));
		
		// true si el dominio ya está preparado para la nueva pantalla del SII.
		AppParamDAO.get(ctx, domainId, AppParam.SII_PREPARE_NEW_SII)
			.ifPresent( p -> config.setPrepareNewSii( p.trueValue() ));

		InvoiceCommunicationConfigurationConsitency.check( ctx, domainId, config);
		return config;
	}
	
	private static void fillDatas(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
		if ( config == null ) throw new AonCoreException("No config");
		
		// Se recuperan primero las administraciones para luego asignarlas a las comunicaciones que no tengan 
		// administración asignada pero que coincidan en fecha de inicio.
		InvoiceCommunicationConfiguration administrationsConfig = new InvoiceCommunicationConfiguration();
		ctx.getDslContext()
			.select()
			.from(ENTERPRISE_DATA)
			.where(ENTERPRISE_DATA.DOMAIN.eq(domainId)
				.and(ENTERPRISE_DATA.NAME.eq(EnterpriseDataNames.ICC_ADMINISTRATION.name())))
			.orderBy(ENTERPRISE_DATA.NAME, ENTERPRISE_DATA.START_DATE)
			.fetch()
			.stream()
			.map( r -> new CommunicationDataFiller().apply(r) )
			.forEach( administrationsConfig::addData )
		;
		
		// Se busca en el resto de tipos de comunicación y se asigna la administración correspondiente a las 
		// que no tengan administración pero coincidan en fecha de inicio con alguna administración.
		ctx.getDslContext()
			.select()
			.from(ENTERPRISE_DATA)
			.where(ENTERPRISE_DATA.DOMAIN.eq(domainId)
					.and(ENTERPRISE_DATA.NAME.in(SUPPORTED_TYPES)))
			.orderBy(ENTERPRISE_DATA.NAME, ENTERPRISE_DATA.START_DATE)
			.fetch()
			.stream()
			.map( r -> new CommunicationDataFiller().apply(r) )
			.map( d -> {
				if (d.getAdministration().isEmpty()) {
					administrationsConfig
						.dataStream()
						.filter( ad -> ad.getDataName() == EnterpriseDataNames.ICC_ADMINISTRATION )	
						.filter(ad -> ad.inRange(d.getStartDate()))
						.findFirst()
						.flatMap( ad -> ad.getAdministration() )
						.ifPresent( a -> d.setAdministration(a));
				}
				return d;
			})
			.forEach( config::addData )
		;
		
		config.dataStream()
			.filter( d -> !d.isAdministration())
			.filter( d -> d.getAdministration().isEmpty() )
			.forEach( d -> {
				administrationsConfig
					.dataStream()
					.filter( ad -> ad.getDataName() == EnterpriseDataNames.ICC_ADMINISTRATION )	
					.filter(ad -> ad.inRange(d.getStartDate()))
					.findFirst()
					.flatMap( ad -> ad.getAdministration() )
					.ifPresent( a -> d.setAdministration(a));
				printEnterpriseData(d);
		});
	}
	
	public static class CommunicationDataFiller extends Filler implements Function<Record, CommunicationData> {

		@Override
		public CommunicationData apply(Record r) {
			CommunicationData cc = new EnterpriseDataFiller<CommunicationData>().apply(r, CommunicationData::new);
			String exp = cc.getExpression();
			if ( AonStringUtils.isNotBlank(exp)) {
				try {
					JSONObject json = new JSONObject(exp);
					cc.setTest( JsonUtils.getboolean(json, TEST) );
					cc.setAdministration( Administration.safeValueOf(JsonUtils.getString(json, ADMINISTRATION)));					
					ExemptType.safeValueOf(JsonUtils.getString(json, EXEMPT_TYPE)).ifPresent( cc::setExemptType );
				} catch (Exception e) {
					if (cc.isAdministration() ) {
						cc.setAdministration( Administration.safeValueOf(cc.getExpression()));					
					} else {
						if (AonStringUtils.equalsIgnoreCase(TEST, exp)) {
							cc.setTest(true);
						}
					}
				}
			}
			cc.setDirty(false);
			return cc;
		}
		
		
	}

	// ------------------------------------------------------------- 
	// ----------------------------------------------------- [WRITE]
	// -------------------------------------------------------------
	
	private static void printEnterpriseData(CommunicationData ed) {
		String startDate = MessageFormat.format("{0,date,dd/MM/yyyy}", ed.getStartDate());
		String endDate = ed.getEndDate() == null
			? "--/--/----"
			:MessageFormat.format("{0,date,dd/MM/yyyy}", ed.getEndDate());
		System.out.println( (ed.isDirty() ? "(*)" : "   ") 
			+ (ed.isDeleted() ? " (D) " : "     ") 
			+ " - "
			+ AonStringUtils.rightPad( "(" + (ed.getId() == null ? "" : AonNumberUtils.toString(ed.getId())) + ")", 10)
			+ AonStringUtils.rightPad(startDate, 15)
			+ AonStringUtils.rightPad(endDate, 15)
			+ AonStringUtils.rightPad( AonStringUtils.defaultIfBlank(ed.getName()), 25)
			+ AonStringUtils.rightPad( AonStringUtils.defaultIfBlank(ed.getAdministration().map(Enum::name).orElse("---------")), 20)
			+ AonStringUtils.rightPad( AonStringUtils.defaultIfBlank(ed.getExpression()) , 20)
		);
	}

	public static InvoiceCommunicationConfiguration save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
		ctx.checkWrite();
		InvoiceCommunicationConfigurationValidation.validate(config);
		for (CommunicationData d : config.getDataList()) {
			if ( !d.isAdministration()) {
				JSONObject json = new JSONObject();
				if (d.isTest()) {
					json.put(TEST, true);
				}
				json.put(EXEMPT_TYPE, d.getExemptType().map( ExemptType::name ).orElse(null) );
				json.put(ADMINISTRATION, d.getAdministration().map( Enum::name).orElse(null));
				if (!JsonUtils.isEmpty(json)) {
					d.setExpression( json.toString() );
				}
			}
			printEnterpriseData( d );
			EnterpriseDataDAO.save(ctx, d );
		}
		
		AppParamDAO.save(ctx, domainId, AppParam.FS_MODEL_CFG_SII, 
			"audit".equalsIgnoreCase( config.getSiiRegistryDate() ) ? "R" : "T");	
		AppParamDAO.save(ctx, domainId, AppParam.TBAI_REGISTRY_DATE, config.getLroeRegistryDate());
		
		return get(ctx, domainId);
	} 
	
	// ------------------------------------------------------------- 
	// -------------------------------------------------- [INVOICES]
	// -------------------------------------------------------------
	
	public static Stream<Invoice> getInvoices(AONContext ctx, InvoiceCommunicationParams params) {
		if (params == null) throw new AonCoreException("No params");
		if (params.getDomain() == null) throw new AonCoreException("No domain");
		if (params.getCommunicationType() == null) throw new AonCoreException("No type");
		return ctx.getDslContext().select()
			.from(INVOICE)
			.leftOuterJoin(INVOICE_INFO).on(INVOICE_INFO.INVOICE.eq(INVOICE.ID)) 
			.where(getFilter(params))
			.and(INVOICE.NUMBER.gt(0))
			.orderBy(INVOICE.ISSUE_DATE.desc(), INVOICE.ID.desc())
			.limit(params.getSafePerPage())
			.offset(params.getOffset())
			.fetch()
			.stream()
			.map( r -> InvoiceFiller.buildInvoice(r).putInvoiceInfo(InvoiceInfoFiller.build(ctx,r)))
		;
	}
	
	private static Condition getFilter(InvoiceCommunicationParams params) {
		Condition c = INVOICE.DOMAIN.eq(params.getDomain())
			.and(INVOICE_INFO.TYPE.isNull().or(INVOICE_INFO.TYPE.eq(params.getCommunicationType().value())))
		;
		
		if (params.getFrom() != null) c = c.and(INVOICE.ISSUE_DATE.ge(AonDateUtils.toSql( params.getFrom())));
		if (params.getTo()   != null) c = c.and(INVOICE.ISSUE_DATE.le(AonDateUtils.toSql( params.getTo())));
		
		if(AonCollectionUtils.isNotEmpty(params.getType())) {
			LinkedList<Byte> types = AonCollectionUtils.stream(params.getType())
				.filter(t -> t != null)
				.map(t -> t.value())
				.collect(Collectors.toCollection(LinkedList<Byte>::new));
			c = c.and(INVOICE.TYPE.in(types));
		}
		
    	if (AonStringUtils.isNotBlank(params.getQuery())) {
    		c = c.and(INVOICE.REFERENCE_CODE.like("%" + params.getQuery() + "%")
  				.or(INVOICE.RNAME.like("%" + params.getQuery() + "%")));
    	}
    	
    	if (params.getCommunicationStatus() != null) {
    		Condition c1 = INVOICE_INFO.STATUS.eq(params.getCommunicationStatus().value());
    		if (params.getCommunicationStatus() == InvoiceCommunicationStatus.PENDING) {
				c1 = INVOICE_INFO.STATUS.isNull().or(c1);
			}
			c = c.and(c1);
    	}
    	return c;
    }
	
	// -------------------------------------------------------------
	// --------------------------------------------------- [HISTORY]
	// -------------------------------------------------------------
	
	private static final Field<Byte> DATA_ATTACH_SOURCE_FIELD = DSL.decode(INVOICE_BATCH.TYPE,
		InvoiceCommunicationType.SII.value(),DataAttachSource.SII.value(),
		InvoiceCommunicationType.TBAI.value(),DataAttachSource.TBAI.value(),
		InvoiceCommunicationType.LROE.value(),DataAttachSource.LROE.value(),
		InvoiceCommunicationType.SERES.value(),DataAttachSource.SERES.value(),
//		InvoiceCommunicationType.EMAIL.value(),DataAttachSource.EMAIL.value(),
//		InvoiceCommunicationType.CLOSING.value(),DataAttachSource.CLOSING.value(),
		InvoiceCommunicationType.VERIFACTU.value(),DataAttachSource.VERIFACTU.value(),
		InvoiceCommunicationType.NO_VERIFACTU.value(),DataAttachSource.NO_VERIFACTU.value(),
		InvoiceCommunicationType.SIF.value(),DataAttachSource.SIF.value(),
		InvoiceCommunicationType.FACTURAE.value(),DataAttachSource.FACTURAE.value()
	);		
	
	public static List<InvoiceCommunicationHistory> getHistory(AONContext ctx, Integer invoiceId, Function<InvoiceCommunicationHistory, List<String>> messagesExtractor) {
		return ctx.getDslContext().select(
				INVOICE_BATCH.DATE,
				INVOICE_BATCH.TYPE,
				INVOICE_BATCH.OPERATION,
				INVOICE_BATCH.CREATION_DATE,
				INVOICE_BATCH.CREATION_USER,
				INVOICE_BATCH_DETAIL.STATUS,
				DATA_RESPONSE.DATA_REQUEST,
				DATA_RESPONSE.ID,
				DATA_ATTACH_REQUEST.ID,
				DATA_ATTACH_RESPONSE.ID,
				DATA_ATTACH_RESPONSE.DATA
			)
			.from(INVOICE_BATCH_DETAIL)
			.join(INVOICE_BATCH).on(INVOICE_BATCH.ID.eq(INVOICE_BATCH_DETAIL.INVOICE_BATCH))
			.join(DATA_RESPONSE).on(DATA_RESPONSE.ID.eq(INVOICE_BATCH.DATA_RESPONSE))
			.leftOuterJoin(DATA_ATTACH_REQUEST).on(DATA_ATTACH_REQUEST.SOURCE_ID.eq(DATA_RESPONSE.DATA_REQUEST)
				.and(DATA_ATTACH_REQUEST.SOURCE.eq(DATA_ATTACH_SOURCE_FIELD)
				.and(DATA_ATTACH_REQUEST.TYPE.eq(DataAttachType.REQUEST.value())))
			)
			.leftOuterJoin(DATA_ATTACH_RESPONSE).on(DATA_ATTACH_RESPONSE.SOURCE_ID.eq(DATA_RESPONSE.ID)
				.and(DATA_ATTACH_RESPONSE.SOURCE.eq(DATA_ATTACH_SOURCE_FIELD))
				.and(DATA_ATTACH_RESPONSE.TYPE.in(DataAttachType.RESPONSE_OK.value(), DataAttachType.RESPONSE_ERROR.value()))
			)
			.where( INVOICE_BATCH_DETAIL.INVOICE.eq(invoiceId))
			.orderBy(INVOICE_BATCH.TYPE, INVOICE_BATCH.CREATION_DATE.desc() )
			.fetch()
			.stream()
			.map( r -> new InvoiceCommunicationHistory()
				.setInvoiceId(invoiceId)
				.setDate(r.getValue(INVOICE_BATCH.DATE))
				.setCreationUser(r.getValue(INVOICE_BATCH.CREATION_USER))
				.setType(InvoiceCommunicationType.safeValueOf(r.getValue(INVOICE_BATCH.TYPE)))
				.setOperation(InvoiceCommunicationOperation.safeValueOf(r.getValue(INVOICE_BATCH.OPERATION)))
				.setStatus(InvoiceCommunicationStatus.safeValueOf(r.getValue(INVOICE_BATCH_DETAIL.STATUS)))
				.setRequestUrl(getAttachUrl(ctx.getDomainName(), ctx.getDomainId(), r.getValue(DATA_ATTACH_REQUEST.ID)))
				.setResponseUrl(getAttachUrl(ctx.getDomainName(), ctx.getDomainId(), r.getValue(DATA_ATTACH_RESPONSE.ID)))
				.setResponseData(r.getValue(DATA_ATTACH_RESPONSE.DATA))
			)
			.map( h  -> (messagesExtractor != null)?h.setResponseMessages( messagesExtractor.apply(h) ):h )
			.collect(Collectors.toCollection(LinkedList::new))
		;
	}
	
	private static String getAttachUrl(String domainName,Integer domainId, Integer attachId) {
		if (attachId == null) return null;
		JSONObject attachData = new JSONObject()
			.put("domain_name", domainName)
			.put("domain_id", domainId)
			.put("id", attachId)
			.put("attach_type", AttachType.DATA.getName())
		;
		String result = Base64.getEncoder().encodeToString(attachData.toString().getBytes(StandardCharsets.UTF_8));
		return "ms/api/file/" +  result;
	}

	// -------------------------------------------------------------
	// ------------------------------ [INVOICE COMMUNICATION COMMON]
	// -------------------------------------------------------------

	public static DataRequest saveRequest(AONContext ctx, Domain domain, InvoiceCommunicationType communicationType, byte[] request) {
		if(communicationType == null) {
			throw new AonCoreException("No communication type");
		}
		
		DataRequest dataRequest = new DataRequest()
			.setDomain(domain.getId())
			.setDate(new Date())
			.setBlackBox("")
			.setType(DataRequestType.safeValueOf(communicationType));
		dataRequest = DataRequestDAO.save(ctx, dataRequest);
			
		Attach attach = new Attach()
			.setDomain(domain)
			.setAttachType(AttachType.DATA)
			.setType(DataAttachType.REQUEST.value())
			.setSource(DataAttachSource.safeValueOf(communicationType).value())
			.setSourceId(dataRequest.getId())
			.setMimeType(MimeType.XML)
			.setData(request);
		AttachmentDAO.insertDataAttach(ctx, attach);
		return dataRequest;		
	}
	
	public static DataResponse saveResponse(AONContext ctx, Domain domain,  InvoiceCommunicationType communicationType, DataRequest dataRequest, byte[] response) {
		if(communicationType == null) {
			throw new AonCoreException("No communication type");
		}
		
		DataResponse dataResponse = new DataResponse()
			.setDomain(dataRequest.getDomain())
			.setResponseDate(new Date())
			.setDataRequest(dataRequest.getId())
			.setCode("")
			.setSource(DataResponseSource.safeValueOf(communicationType));
		DataResponseDAO.insertDataResponse(ctx, dataResponse);
		Attach attach = new Attach()
			.setDomain(domain)
			.setAttachType(AttachType.DATA)
			.setType(DataAttachType.RESPONSE_OK.value())
			.setSource(DataAttachSource.safeValueOf(communicationType).value())
			.setSourceId(dataResponse.getId())
			.setMimeType(MimeType.XML)
			.setData(response);
		AttachmentDAO.insertDataAttach(ctx, attach);
		
		return dataResponse;		
	}
	
	public static InvoiceInfo saveInvoiceInfo(AONContext ctx, Integer domainId, Integer invoiceId, InvoiceCommunicationType communicationType, InvoiceCommunicationStatus status) {
		InvoiceInfo info = InvoiceInfoDAO.get(ctx, invoiceId, communicationType)
			.orElse( 
				new InvoiceInfo()
					.setDomain(domainId)
					.setInvoice(invoiceId)
					.setType(communicationType)
			)
		;
		info.setStatus(status);
		return InvoiceInfoDAO.save(ctx, info);
	}
	
	// -------------------------------------------------------------
	// ------------------------------------------- [PREPARE NEW SII]
	// -------------------------------------------------------------

	public static void prepareNewSii(AONContext ctx) {
		ctx.getDslContext()
			.select(DATA_RESPONSE.SOURCE_ID,DATA_RESPONSE_DETAIL.DATA_VALUE)
			.from(DATA_RESPONSE)
			.join(INVOICE).on(INVOICE.ID.eq(DATA_RESPONSE.SOURCE_ID))
			.join(DATA_RESPONSE_DETAIL).on(DATA_RESPONSE.ID.eq(DATA_RESPONSE_DETAIL.DATA_RESPONSE))
			.where(DATA_RESPONSE.DOMAIN.eq(ctx.getDomainId()))
			.and(DATA_RESPONSE.SOURCE.eq(DataResponseSource.SII_INVOICE.value()))
			.and(DATA_RESPONSE_DETAIL.DOMAIN.eq(ctx.getDomainId()))
			.and(DATA_RESPONSE_DETAIL.DATA_VARIABLE.eq("status"))
			.fetch()
			.stream()
			.forEach( r -> {
				Integer invoiceId = r.getValue(DATA_RESPONSE.SOURCE_ID);
				String st = r.getValue(DATA_RESPONSE_DETAIL.DATA_VALUE);
				InvoiceInfo info = new InvoiceInfo()
					.setDomain(ctx.getDomainId())
					.setInvoice(invoiceId)
					.setType(InvoiceCommunicationType.SII)
					.setStatus(InvoiceCommunicationStatus.safeValueOf(st));
				InvoiceInfoDAO.save(ctx, info);
		});	
	
		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.SII_PREPARE_NEW_SII.toString(),
				Boolean.toString(true));
	}
}

