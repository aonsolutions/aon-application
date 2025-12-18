package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.DataAttach.DATA_ATTACH;
import static com.esferalia.aon.jooq.tables.DataResponse.DATA_RESPONSE;
import static com.esferalia.aon.jooq.tables.DataResponseDetail.DATA_RESPONSE_DETAIL;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceBatch.INVOICE_BATCH;
import static com.esferalia.aon.jooq.tables.InvoiceBatchDetail.INVOICE_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceInfo.INVOICE_INFO;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.json.JSONObject;

import com.esferalia.aon.jooq.tables.DataAttach;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationHistory;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationParams;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DataRequestType;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO.InvoiceFiller;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO.InvoiceInfoFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceCommunicationDAO {
	
	private static final DataAttach DATA_ATTACH_REQUEST = DATA_ATTACH.as("data_attach_request");
	private static final DataAttach DATA_ATTACH_RESPONSE = DATA_ATTACH.as("data_attach_response");
	
	private InvoiceCommunicationDAO() {

	}
	
	// *************************************************************
	// ********************** [CONFIGURATION] **********************
	// *************************************************************
	
	private enum InvoiceCommunicationConfigurationParams {
		// -------------------------------------------------------------------- [TicketBAI]
		TBAI_ACTIVE { 
			@Override 
			public InvoiceCommunicationConfiguration fillValue(InvoiceCommunicationConfiguration config, ApplicationParameter param) {
				return config.setTbai(param.trueValue());
			}

			@Override
			public void save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
				AppParamDAO.save(ctx, domainId,  AppParam.TBAI_ACTIVE, Boolean.toString(config.isTbai()));
				if(config.isTbai()) {
					AppParamDAO.save(ctx, domainId,  AppParam.APP_SALE_INVOICE_TEMPLATE_PARAM, "default");
				}
			}
		},
		TBAI_TEST {
			@Override
			public InvoiceCommunicationConfiguration fillValue(InvoiceCommunicationConfiguration config, ApplicationParameter param) {
				return config.setTbaiTest(param.trueValue());
			}

			@Override
			public void save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
				AppParamDAO.save(ctx, domainId, AppParam.TBAI_TEST, Boolean.toString(config.isTbaiTest()));
			}
		},
		TBAI_INCLUDE_DATE {
			@Override
			public InvoiceCommunicationConfiguration fillValue(InvoiceCommunicationConfiguration config, ApplicationParameter param) {
				return config.setTbaiIncludeDate(AonDateUtils.parse(param.getValue(), YYYY_MM_DD));
			}

			@Override
			public void save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
				String date = config.getTbaiIncludeDate() != null
					? AonDateUtils.format(config.getTbaiIncludeDate(), YYYY_MM_DD)
					: null;
				AppParamDAO.save(ctx, domainId,  AppParam.TBAI_INCLUDE_DATE, date);
			}
		},
		TBAI_REGISTRY_DATE {
			@Override
			public InvoiceCommunicationConfiguration fillValue(InvoiceCommunicationConfiguration config, ApplicationParameter param) {
				return config.setTbaiRegistryDate(param.getValue());
			}

			@Override
			public void save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
				AppParamDAO.save(ctx, domainId, AppParam.TBAI_REGISTRY_DATE, config.getTbaiRegistryDate());
			}
		},
		TBAI_SKIP_TRACKING {
			@Override
			public InvoiceCommunicationConfiguration fillValue(InvoiceCommunicationConfiguration config, ApplicationParameter param) {
				return config.setSkipTracking(param.trueValue());
			}

			@Override
			public void save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
				AppParamDAO.save(ctx, domainId, AppParam.TBAI_SKIP_TRACKING, Boolean.toString(config.isSkipTracking()));
			}
		},
		
		// -------------------------------------------------------------------- [VERIFACTU]
		
		VERIFACTU_ACTIVE { 
			@Override 
			public InvoiceCommunicationConfiguration fillValue(InvoiceCommunicationConfiguration config, ApplicationParameter param) {
				return config.setVerifactu(param.trueValue());
			}

			@Override
			public void save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
				AppParamDAO.save(ctx, domainId, AppParam.VERIFACTU_ACTIVE, Boolean.toString(config.isVerifactu()));
				if(config.isVerifactu()) {
					AppParamDAO.save(ctx, domainId, AppParam.APP_SALE_INVOICE_TEMPLATE_PARAM, "default");
				}
			}
		},
		VERIFACTU_TEST {
			@Override
			public InvoiceCommunicationConfiguration fillValue(InvoiceCommunicationConfiguration config, ApplicationParameter param) {
				return config.setVerifactuTest(param.trueValue());
			}

			@Override
			public void save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
				AppParamDAO.save(ctx, domainId, AppParam.VERIFACTU_TEST, Boolean.toString(config.isVerifactuTest()));
			}
		},
		VERIFACTU_INCLUDE_DATE {
			@Override
			public InvoiceCommunicationConfiguration fillValue(InvoiceCommunicationConfiguration config, ApplicationParameter param) {
				return config.setVerifactuIncludeDate(AonDateUtils.parse(param.getValue(), YYYY_MM_DD));
			}

			@Override
			public void save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
				String date = config.getVerifactuIncludeDate() != null
					? AonDateUtils.format(config.getVerifactuIncludeDate(), YYYY_MM_DD)
					: null;
				AppParamDAO.save(ctx, domainId,  AppParam.VERIFACTU_INCLUDE_DATE, date);
			}
		},
		VERIFACTU_REGISTRY_DATE {
			@Override
			public InvoiceCommunicationConfiguration fillValue(InvoiceCommunicationConfiguration config, ApplicationParameter param) {
				return config.setVerifactuRegistryDate(param.getValue());
				
			}

			@Override
			public void save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
				AppParamDAO.save(ctx, domainId, AppParam.VERIFACTU_REGISTRY_DATE, config.getVerifactuRegistryDate());
			}
		},
		
		// -------------------------------------------------------------------- [SII]
		
		SII_ACTIVE { 
			@Override 
			public InvoiceCommunicationConfiguration fillValue(InvoiceCommunicationConfiguration config, ApplicationParameter param) {
				return config.setSii(param.trueValue());
				
				// PREPARE
				
			}

			@Override
			public void save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
				AppParamDAO.save(ctx,  domainId, AppParam.SII_ACTIVE, Boolean.toString(config.isSii()));
			}
		},
		SII_TEST {
			@Override
			public InvoiceCommunicationConfiguration fillValue(InvoiceCommunicationConfiguration config, ApplicationParameter param) {
				return config.setSiiTest(param.trueValue());
			}

			@Override
			public void save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
				AppParamDAO.save(ctx, domainId, AppParam.SII_TEST, Boolean.toString(config.isSiiTest()));
			}
		},
		SII_INCLUDE_DATE {
			@Override
			public InvoiceCommunicationConfiguration fillValue(InvoiceCommunicationConfiguration config, ApplicationParameter param) {
				config.setSiiIncludeDate(AonDateUtils.parse(param.getValue(), YYYY_MM_DD));
				if(config.getSiiIncludeDate() == null) {
					String defaultDate = Administration.COMMON_TERRITORY== config.getAdministration() ? "2017-07-01" : "2018-01-01";
					config.setSiiIncludeDate(AonDateUtils.parse(defaultDate, YYYY_MM_DD));
				}
				return config;
			}

			@Override
			public void save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
				String date = config.getSiiIncludeDate() != null
					? AonDateUtils.format(config.getSiiIncludeDate(), YYYY_MM_DD)
					: null;
				AppParamDAO.save(ctx, domainId,  AppParam.SII_INCLUDE_DATE, date);
			}
		},
		SII_REGISTRY_DATE {
			@Override
			public InvoiceCommunicationConfiguration fillValue(InvoiceCommunicationConfiguration config, ApplicationParameter param) {
				return config.setSiiRegistryDate(param.getValue());
			}

			@Override
			public void save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
				AppParamDAO.save(ctx, domainId, AppParam.SII_REGISTRY_DATE, config.getSiiRegistryDate());
				if("audit".equalsIgnoreCase(config.getSiiRegistryDate()) ) {
					AppParamDAO.save(ctx, domainId, AppParam.FS_MODEL_CFG_SII, "R");
				}
			}
		},
		SII_AUTOSEND {
			@Override
			public InvoiceCommunicationConfiguration fillValue(InvoiceCommunicationConfiguration config, ApplicationParameter param) {
				return config.setSiiAutosend(param.trueValue());
				
			}

			@Override
			public void save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
				AppParamDAO.save(ctx, domainId, AppParam.SII_AUTOSEND, Boolean.toString(config.isSiiAutosend()));
			}
		},
		SII_PREPARE_NEW_SII {
			@Override
			public InvoiceCommunicationConfiguration fillValue(InvoiceCommunicationConfiguration config, ApplicationParameter param) {
				return config.setPrepareNewSii(param.trueValue());
				
			}

			@Override
			public void save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
				// Nothing
			}
		},
		;
		
		private static final String YYYY_MM_DD = "yyyy-MM-dd";
		
		public static Optional<InvoiceCommunicationConfigurationParams> safeValueOf( String name ) {
			return AonCollectionUtils.stream(InvoiceCommunicationConfigurationParams.values())
				.filter( t -> AonStringUtils.equals(name , t.name()))
				.findAny();
		}

		public abstract InvoiceCommunicationConfiguration fillValue( InvoiceCommunicationConfiguration config, ApplicationParameter param);
		public abstract void save( AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config);
		
	}
	
	
	// ------------------------------------------------------ [READ]
	public static InvoiceCommunicationConfiguration get(AONContext ctx, int domainId) {
		ctx.checkRead();
		InvoiceCommunicationConfiguration configuration = new InvoiceCommunicationConfiguration();
		Administration admon = AppParamDAO.get(ctx, domainId, AppParam.FS_DEFAULT_ADMINISTRATION)
			.map( ApplicationParameter::getValue )
			.map( Integer::parseInt )
			.map( Administration::safeValueOf )
			.orElse( Administration.UNKNOWN )
		;
		configuration.setAdministration(admon);
		fillVerifactu(ctx, domainId, configuration);
		fillTbai(ctx, domainId, configuration);
		fillSii(ctx, domainId, configuration);
		return configuration;
	}

	private static void fillTbai(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
		if ( !config.isVerifactu() && !config.isSii() ) {
			AppParamDAO.getByPattern(ctx, domainId, "TBAI_%")
			.forEach(r -> InvoiceCommunicationConfigurationParams.safeValueOf(r.getName() ) .ifPresent(t -> t.fillValue(config, r)));
		}
	}
	private static void fillVerifactu(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
		if ( !config.isTbai() && !config.isSii() ) {
			AppParamDAO.getByPattern(ctx, domainId, "VERIFACTU_%")
				.forEach(r -> InvoiceCommunicationConfigurationParams.safeValueOf(r.getName() ) .ifPresent(t -> t.fillValue(config, r)));
		}
	}
	private static void fillSii(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
		if ( !config.isTbai() && !config.isVerifactu() ) {
			AppParamDAO.getByPattern(ctx, domainId, "SII_%")
				.forEach(r -> InvoiceCommunicationConfigurationParams.safeValueOf(r.getName() ) .ifPresent(t -> t.fillValue(config, r)));
		}
		
		// ------------- ¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?
		// ------------- ¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?
		AppParamDAO.get(ctx, domainId, AppParam.FS_MODEL_CFG_SII)
			.ifPresent( p -> config.setSiiRegistryDate("R".equalsIgnoreCase(p.getValue()) ? "audit" : "tax"));
		// ------------- ¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?
		// ------------- ¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?
		
	}

	// ----------------------------------------------------- [WRITE]
	public static InvoiceCommunicationConfiguration save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
		ctx.checkWrite();
		saveVerifactu(ctx, domainId, config);
		saveTbai(ctx, domainId, config);
		saveSii(ctx, domainId, config);
		return get(ctx, domainId);
	} 

	private static void saveVerifactu(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
		if ( !config.isTbai() && !config.isSii() ) {
			AonCollectionUtils.stream(InvoiceCommunicationConfigurationParams.values())
				.forEach(t -> t.save(ctx, domainId, config));
		}
	}
	private static void saveTbai(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
		if ( !config.isVerifactu() && !config.isSii() ) {
			AonCollectionUtils.stream(InvoiceCommunicationConfigurationParams.values())
				.forEach(t -> t.save(ctx, domainId, config));
		}
	}
	private static void saveSii(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
		if ( !config.isTbai() && !config.isVerifactu() ) {
			AonCollectionUtils.stream(InvoiceCommunicationConfigurationParams.values())
				.forEach(t -> t.save(ctx, domainId, config));
		}
	}
	
	// *************************************************************
	// ************************** [INVOICES] ***********************
	// *************************************************************
	
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
	
	// *************************************************************
	// ************************** [HISTORY] ************************
	// *************************************************************
	
	public static LinkedList<InvoiceCommunicationHistory> getHistory(AONContext ctx, Integer invoiceId, Function<InvoiceCommunicationHistory, LinkedList<String>> messagesExtractor) {
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
			.leftOuterJoin(DATA_ATTACH_REQUEST).on(DATA_ATTACH_REQUEST.SOURCE_ID.eq(DATA_RESPONSE.ID)
				.and(DATA_ATTACH_REQUEST.SOURCE.eq(DataAttachSource.VERIFACTU.value())
				.and(DATA_ATTACH_REQUEST.TYPE.eq(DataAttachType.REQUEST.value())))
			)
			.leftOuterJoin(DATA_ATTACH_RESPONSE).on(DATA_ATTACH_RESPONSE.SOURCE_ID.eq(DATA_RESPONSE.ID)
				.and(DATA_ATTACH_RESPONSE.SOURCE.eq(DataAttachSource.VERIFACTU.value()))
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

	
	// *************************************************************
	// *********** INVOICE COMMUNICATION COMMON ********************
	// *************************************************************

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
	
	public static InvoiceInfo saveInvoiceInfo(AONContext ctx, Domain domain, Integer invoiceId, InvoiceCommunicationType communicationType, InvoiceCommunicationStatus status) {
		InvoiceInfo info = InvoiceInfoDAO.get(ctx, invoiceId, communicationType)
			.orElse( 
				new InvoiceInfo()
					.setDomain(domain.getId())
					.setInvoice(invoiceId)
					.setType(communicationType)
			)
		;
		info.setStatus(status);
		return InvoiceInfoDAO.save(ctx, info);
	}
	
	// *************************************************************
	// ********************** [PREPARE NEW SII] ********************
	// *************************************************************
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
