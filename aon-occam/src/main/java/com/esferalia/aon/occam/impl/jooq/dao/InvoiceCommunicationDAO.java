package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.DataAttach.DATA_ATTACH;
import static com.esferalia.aon.jooq.tables.DataResponse.DATA_RESPONSE;
import static com.esferalia.aon.jooq.tables.DataResponseDetail.DATA_RESPONSE_DETAIL;
import static com.esferalia.aon.jooq.tables.EnterpriseData.ENTERPRISE_DATA;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceBatch.INVOICE_BATCH;
import static com.esferalia.aon.jooq.tables.InvoiceBatchDetail.INVOICE_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceInfo.INVOICE_INFO;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_ADMINISTRATION;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_LROE;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_NO_SIF;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_NO_VERIFACTU;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_SIF;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_SII;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_TBAI;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_VERIFACTU;
import static com.esferalia.aon.occam.api.model.type.Administration.ALAVA;
import static com.esferalia.aon.occam.api.model.type.Administration.BIZKAIA;
import static com.esferalia.aon.occam.api.model.type.Administration.CANARIAS;
import static com.esferalia.aon.occam.api.model.type.Administration.COMMON_TERRITORY;
import static com.esferalia.aon.occam.api.model.type.Administration.GIPUZKOA;
import static com.esferalia.aon.occam.api.model.type.Administration.NAVARRA;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseData;
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
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationParams;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType.InvoiceCommunicationTypeAccepter;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DataRequestType;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
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
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceCommunicationDAO {
	
	private static final DataAttach DATA_ATTACH_REQUEST = DATA_ATTACH.as("data_attach_request");
	private static final DataAttach DATA_ATTACH_RESPONSE = DATA_ATTACH.as("data_attach_response");
	public static final String ICC_PREFIX = "ICC_%";
	
	private static record EnablerContext (
		InvoiceCommunicationConfiguration config
		, Administration admon
		, EnterpriseDataNames name
		, Date date
		, boolean test) {}
	
	private InvoiceCommunicationDAO() {

	}
	
	// ------------------------------------------------------ [READ]
	public static InvoiceCommunicationConfiguration get(AONContext ctx, int domainId) {
		ctx.checkRead();
		InvoiceCommunicationConfiguration config = new InvoiceCommunicationConfiguration();
		fillDatas(ctx, domainId, config);

//		config.setTbaiInvoice(DataResponseDAO.has(ctx, domainId, DataResponseSource.TBAI, AonDateUtils.getCurrentYear()));
//		config.setLroeInvoice(DataResponseDAO.has(ctx, domainId, DataResponseSource.LROE, AonDateUtils.getCurrentYear()));
//		config.setSiiInvoice(DataResponseDAO.has(ctx, domainId, DataResponseSource.SII, AonDateUtils.getCurrentYear()));
//		config.setVerifactuInvoice(DataResponseDAO.has(ctx, domainId, DataResponseSource.VERIFACTU, AonDateUtils.getCurrentYear()));	
//		config.setSifInvoice(DataResponseDAO.has(ctx, domainId, DataResponseSource.SIF, AonDateUtils.getCurrentYear()));
//		config.setNoVerifactuInvoice(DataResponseDAO.has(ctx, domainId, DataResponseSource.NO_VERIFACTU, AonDateUtils.getCurrentYear()));
		
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
		ctx.getDslContext()
			.select()
			.from(ENTERPRISE_DATA)
			.where(ENTERPRISE_DATA.DOMAIN.eq(domainId)
					.and(ENTERPRISE_DATA.NAME.like(ICC_PREFIX)))
			.orderBy(ENTERPRISE_DATA.NAME, ENTERPRISE_DATA.START_DATE)
			.fetch()
			.stream()
			.map( r -> new CommunicationDataFiller().apply(r) )
			.forEach( cc -> config.addData(cc) )
		;
	}
	
	static class CommunicationDataFiller extends Filler implements Function<Record, CommunicationData> {

		@Override
		public CommunicationData apply(Record r) {
			EnterpriseData cc = new EnterpriseDataFiller<>().apply(r, CommunicationData::new);
			return (CommunicationData) cc;
		}
		
		
	}

//	private static InvoiceCommunicationConfiguration checkConfigurationConsistency(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
//		if(config.isNoSif()) {
//			if(config.isSif()) {
//				updateEndDate(ctx, domainId, config.getSifData(), config.getNoSifData().getStartDate());
//				fillSif(ctx, domainId, config);
//			}
//			
//			if(config.isTbai()) {
//				updateEndDate(ctx, domainId, config.getTbaiData(), config.getNoSifData().getStartDate());
//				fillTbai(ctx, domainId, config);
//			}
//			
//			if(config.isNoVerifactu()) {
//				updateEndDate(ctx, domainId, config.getNoVerifactuData(), config.getNoSifData().getStartDate());
//				fillNoVerifactu(ctx, domainId, config);
//			}
//			
//			if(config.isVerifactu()) {
//				updateEndDate(ctx, domainId, config.getVerifactuData(), config.getNoSifData().getStartDate());
//				fillVerifactu(ctx, domainId, config);
//			}
//		}
//		
//		if(config.isSif() && !config.isSifTest()) {
//			if(config.isLroe()) {
//				updateEndDate(ctx, domainId, config.getSifData(), config.getLroeData().getStartDate());
//				fillSif(ctx, domainId, config);
//			}
//			
//			if(config.isTbai()) {
//				updateEndDate(ctx, domainId, config.getSifData(), config.getTbaiData().getStartDate());
//				fillSif(ctx, domainId, config);
//			}
//			
//			if(config.isNoVerifactu()) {
//				updateEndDate(ctx, domainId, config.getSifData(), config.getNoVerifactuData().getStartDate());
//				fillSif(ctx, domainId, config);
//			}
//			
//			if(config.isVerifactu()) {
//				updateEndDate(ctx, domainId, config.getSifData(), config.getVerifactuData().getStartDate());
//				fillSif(ctx, domainId, config);
//			}
//			
//			if(config.isSii()) {
//				updateEndDate(ctx, domainId, config.getSifData(), config.getSiiData().getStartDate());
//				fillSif(ctx, domainId, config);
//			}
//		
//			if(config.isAEAT() || config.isCanarias()) {
//				if(!config.hasVerifactuInvoice() && !config.hasNoVerifactuInvoice() && !config.hasSifInvoice()) {
//					EnterpriseDataDAO.update(ctx, config.getSifData().setName(EnterpriseDataNames.ICC_NO_VERIFACTU.name()));
//					fillSif(ctx, domainId, config);
//					fillNoVerifactu(ctx, domainId, config);	
//				}
//				
//				if(!config.hasVerifactuInvoice()) {
//					if(config.willBeNoVerifactu()) {
//						config.getNoVerifactuDataHistory().stream().filter(f -> f.getStartDate().after(new Date())).findFirst()
//						.ifPresent(ed -> EnterpriseDataDAO.delete(ctx, ed.getId()));
//					}
//					EnterpriseDataDAO.update(ctx, config.getSifData().setName(EnterpriseDataNames.ICC_NO_VERIFACTU.name()));
//					fillSif(ctx, domainId, config);
//					fillNoVerifactu(ctx, domainId, config);
//					
//					ctx.getDslContext().update(INVOICE_INFO)
//					.set(INVOICE_INFO.TYPE, InvoiceCommunicationType.NO_VERIFACTU.value())
//					.where(INVOICE_INFO.DOMAIN.eq(domainId)
//						.and(INVOICE_INFO.TYPE.eq(InvoiceCommunicationType.SIF.value()))
//						.and(INVOICE_INFO.CREATION_DATE.ge(AonDateUtils.toTimestamp(AonDateUtils.getYearFirstDay(new Date()))))
//					)
//					.execute();
//					
//					ctx.getDslContext().update(INVOICE_BATCH)
//					.set(INVOICE_BATCH.TYPE, InvoiceCommunicationType.NO_VERIFACTU.value())
//					.where(INVOICE_BATCH.DOMAIN.eq(domainId)
//						.and(INVOICE_BATCH.TYPE.eq(InvoiceCommunicationType.SIF.value()))
//						.and(INVOICE_BATCH.CREATION_DATE.ge(AonDateUtils.toTimestamp(AonDateUtils.getYearFirstDay(new Date()))))
//					)
//					.execute();
//					
//					ctx.getDslContext().update(DATA_RESPONSE)
//					.set(DATA_RESPONSE.SOURCE, DataResponseSource.NO_VERIFACTU.value())
//					.where(DATA_RESPONSE.DOMAIN.eq(domainId)
//						.and(DATA_RESPONSE.SOURCE.eq(DataResponseSource.SIF.value()))
//						.and(DATA_RESPONSE.CREATION_DATE.ge(AonDateUtils.toTimestamp(AonDateUtils.getYearFirstDay(new Date()))))
//					)
//					.execute();
//				}
//				
//				if(config.hasVerifactuInvoice()) {
//					updateEndDate(ctx, domainId, config.getSifData(), new Date());
//					fillSif(ctx, domainId, config);
//					
//					EnterpriseData vd = new EnterpriseData()
//							.setDomain(domainId)
//							.setEnterprise( config.getSifData().getEnterprise() )
//							.setName(EnterpriseDataNames.ICC_VERIFACTU.name())
//							.setStartDate( new Date() );
//					EnterpriseDataDAO.insert(ctx, vd);
//				}
//			}
//		}
//		
//		return config;
//	}
//	
//	private static void updateEndDate(AONContext ctx, Integer domainId, EnterpriseData data, Date endDate) {
//		data.setEndDate(endDate);
//		EnterpriseDataDAO.update(ctx, data);
//	}
//	
//	private static List<EnterpriseData> getIccHistory(AONContext ctx, Integer domainId, EnterpriseDataNames name) {
//		return EnterpriseDataDAO.getList(ctx, f -> f.getDomainProperty().eq(domainId).and(f.getNameProperty().eq(name.name())));
//	}
//	
//	private static EnterpriseData getIccData(List<EnterpriseData> history) {
//		if(history == null || history.isEmpty()) return null;
//		return history.stream().filter(f -> (f.getStartDate() != null && f.getStartDate().before(new Date()))
//				&& (f.getEndDate() == null || f.getEndDate().after(new Date()))).findFirst().orElse(null);
//	}

	// ----------------------------------------------------- [ENABLE METHODS]

	public static Date minusOneDay(Date endDate) {
	    if (endDate == null) return null;
	    Date date = new java.util.Date(endDate.getTime());
	    Instant instant = date.toInstant();
	    ZoneId zone = ZoneId.systemDefault();
	    LocalDate localDate = instant.atZone(zone).toLocalDate().minusDays(1);
	    return Date.from(localDate.atStartOfDay(zone).toInstant());
	}
	
	private static InvoiceCommunicationConfiguration enable(AONContext ctx, Integer domainId, Administration admon, EnterpriseDataNames name, Date date, boolean test) {
		if (admon == null || name == null || date == null) throw new AonCoreException( InvoiceCommunicationError.ICC_5000.getMessage() );
		InvoiceCommunicationConfiguration config = get(ctx, domainId);
		
		enableAdministration( config, admon, date);
		
		CommunicationEnabler ce = new CommunicationEnabler( new EnablerContext(config, admon, name, date, test) );
		if ( name == ICC_NO_SIF ) {
			ce.visitNoSif();
		} else {
			InvoiceCommunicationType.get(name).ifPresent( t -> t.accept( ce ));  
		}
		return save(ctx, domainId, config);
	}
	
	private static void enableAdministration(InvoiceCommunicationConfiguration config, Administration admon, Date date) {
		config.getAdministrationData(date)
		.ifPresentOrElse(
			currentAdmonData -> {
				currentAdmonData.getAdministration()
					.ifPresentOrElse( 
						a -> {
							if ( a != admon) {
								closeData( currentAdmonData, date);
								config.addData( new CommunicationData()
										.setDataName(ICC_ADMINISTRATION)
										.setExpression(admon.name())
										.setStartDate(date) );
							}
						}
						,() -> {
							currentAdmonData
								.setExpression(admon.name())
								.setStartDate(date);
						}
					);
			}
			,() -> {
				config.addData( new CommunicationData()
					.setDataName(ICC_ADMINISTRATION)
					.setExpression(admon.name())
					.setStartDate(date) );
				
			}
		);
	}

	private static void closeData(CommunicationData currentAdmonData, Date date) {
		Date endDate = minusOneDay(date);
		EnterpriseDataDAO.setEndDate(currentAdmonData, endDate);
	}

	// -----------------------------------------------------
	// -------------------------------- [ Enable TicketBai ]
	// -----------------------------------------------------

	public static InvoiceCommunicationConfiguration enableTbaiAraba(AONContext ctx, Integer domainId, Date date) {
		return enable(ctx, domainId, ALAVA, ICC_TBAI, date, false);
	}
	public static InvoiceCommunicationConfiguration enableTbaiArabaTest(AONContext ctx, Integer domainId, Date date) {
		return enable(ctx, domainId, ALAVA, ICC_TBAI, date, true);
	}
	
	public static InvoiceCommunicationConfiguration enableTbaiGipuzkoa(AONContext ctx, Integer domainId, Date date) {
		return enable(ctx, domainId, GIPUZKOA, ICC_TBAI, date, false);
	}
	public static InvoiceCommunicationConfiguration enableTbaiGipuzkoaTest(AONContext ctx, Integer domainId, Date date) {
		return enable(ctx, domainId, GIPUZKOA, ICC_TBAI, date, true);
	}
	
	// -----------------------------------------------------
	// ------------------------------------- [ Enable LROE ]
	// -----------------------------------------------------
	public static InvoiceCommunicationConfiguration enableLroe(AONContext ctx, Integer domainId, Date date) {
		return enable(ctx, domainId, BIZKAIA, ICC_LROE, date, false);
	}
	public static InvoiceCommunicationConfiguration enableLroeTest(AONContext ctx, Integer domainId, Date date) {
		return enable(ctx, domainId, BIZKAIA, ICC_LROE, date, true);
	}

	// -----------------------------------------------------
	// --------------------------------- [ Enable Verifactu]
	// -----------------------------------------------------
	public static InvoiceCommunicationConfiguration enableVerifactu(AONContext ctx, Integer domainId, Date date) {
		return enableVerifactu(ctx, domainId, COMMON_TERRITORY, date, false);
	}
	public static InvoiceCommunicationConfiguration enableVerifactuTest(AONContext ctx, Integer domainId, Date date) {
		return enableVerifactu(ctx, domainId, COMMON_TERRITORY, date, true);
	}
	public static InvoiceCommunicationConfiguration enableVerifactuCanarias(AONContext ctx, Integer domainId, Date date) {
		return enableVerifactu(ctx, domainId, CANARIAS, date, false);
	}
	public static InvoiceCommunicationConfiguration enableVerifactuCanariasTest(AONContext ctx, Integer domainId, Date date) {
		return enableVerifactu(ctx, domainId, CANARIAS, date, true);
	}
	private static InvoiceCommunicationConfiguration enableVerifactu(AONContext ctx, Integer domainId, Administration admon, Date date, boolean test) {
		return enable(ctx, domainId, admon, ICC_VERIFACTU, date, test);
	}
	
	// -----------------------------------------------------
	// ----------------------------- [ Enable No Verifactu ]
	// -----------------------------------------------------
	public static InvoiceCommunicationConfiguration enableNoVerifactu(AONContext ctx, Integer domainId, Date date) {
		return enableNoVerifactu(ctx, domainId, COMMON_TERRITORY, date, false);
	}
	public static InvoiceCommunicationConfiguration enableNoVerifactuTest(AONContext ctx, Integer domainId, Date date) {
		return enableNoVerifactu(ctx, domainId, COMMON_TERRITORY, date, true);
	}
	public static InvoiceCommunicationConfiguration enableNoVerifactuCanarias(AONContext ctx, Integer domainId, Date date) {
		return enableNoVerifactu(ctx, domainId, CANARIAS, date, false);
	}
	public static InvoiceCommunicationConfiguration enableNoVerifactuCanariasTest(AONContext ctx, Integer domainId, Date date) {
		return enableNoVerifactu(ctx, domainId, CANARIAS, date, true);
	}
	private static InvoiceCommunicationConfiguration enableNoVerifactu(AONContext ctx, Integer domainId, Administration admon, Date date, boolean test) {
		return enable(ctx, domainId, admon, ICC_NO_VERIFACTU, date, test);
	}
	
	// -----------------------------------------------------
	// -------------------------------------- [ Enable SII ]
	// -----------------------------------------------------
	public static InvoiceCommunicationConfiguration enableSii(AONContext ctx, Integer domainId, Date date) {
		return enableSii(ctx, domainId, COMMON_TERRITORY, date, false);
	}
	public static InvoiceCommunicationConfiguration enableSiiTest(AONContext ctx, Integer domainId, Date date) {
		return enableSii(ctx, domainId, COMMON_TERRITORY, date, true);
	}
	public static InvoiceCommunicationConfiguration enableSiiCanarias(AONContext ctx, Integer domainId, Date date) {
		return enableSii(ctx, domainId, CANARIAS, date, false);
	}
	public static InvoiceCommunicationConfiguration enableSiiCanariasTest(AONContext ctx, Integer domainId, Date date) {
		return enableSii(ctx, domainId, CANARIAS, date, true);
	}
	public static InvoiceCommunicationConfiguration enableSiiNavarra(AONContext ctx, Integer domainId, Date date) {
		return enableSii(ctx, domainId, NAVARRA, date, false);
	}
	public static InvoiceCommunicationConfiguration enableSiiNavarraTest(AONContext ctx, Integer domainId, Date date) {
		return enableSii(ctx, domainId, NAVARRA, date, true);
	}
	public static InvoiceCommunicationConfiguration enableSiiAraba(AONContext ctx, Integer domainId, Date date) {
		return enableSii(ctx, domainId, ALAVA, date, false);
	}
	public static InvoiceCommunicationConfiguration enableSiiArabaTest(AONContext ctx, Integer domainId, Date date) {
		return enableSii(ctx, domainId, ALAVA, date, true);
	}
	public static InvoiceCommunicationConfiguration enableSiiGipuzkoa(AONContext ctx, Integer domainId, Date date) {
		return enableSii(ctx, domainId, GIPUZKOA, date, false);
	}
	public static InvoiceCommunicationConfiguration enableSiiGipuzkoaTest(AONContext ctx, Integer domainId, Date date) {
		return enableSii(ctx, domainId, GIPUZKOA, date, true);
	}
	private static InvoiceCommunicationConfiguration enableSii(AONContext ctx, Integer domainId, Administration admon, Date date, boolean test) {
		return enable(ctx, domainId, admon, ICC_SII, date, test);
	}
	
	
	// -----------------------------------------------------
	// -------------------------------------- [ Enable SIF ]
	// -----------------------------------------------------
	public static InvoiceCommunicationConfiguration enableSif(AONContext ctx, Integer domainId, Administration admon, Date date) {
		return enable(ctx, domainId, admon, ICC_SIF, date, false);
	}
	public static InvoiceCommunicationConfiguration enableSifTest(AONContext ctx, Integer domainId, Administration admon, Date date) {
		return enable(ctx, domainId, admon, ICC_SIF, date, true);
	}
	
	// -----------------------------------------------------
	// ----------------------------------- [ Enable NO SIF ]
	// -----------------------------------------------------
	public static InvoiceCommunicationConfiguration enableNoSif(AONContext ctx, Integer domainId, Administration admon, Date date) {
		return enable(ctx, domainId, admon, ICC_NO_SIF, date, false);
	}

	// ----------------------------------------------------- [WRITE]
	public static InvoiceCommunicationConfiguration save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
		ctx.checkWrite();
		InvoiceCommunicationConfigurationValidation.validate(ctx, config);
		for (CommunicationData d : config.getDataList()) {
			EnterpriseDataDAO.save(ctx, d );
		}
		
		AppParamDAO.save(ctx, domainId, AppParam.FS_MODEL_CFG_SII, 
			"audit".equalsIgnoreCase( config.getSiiRegistryDate() ) ? "R" : "T");	
		AppParamDAO.save(ctx, domainId, AppParam.TBAI_REGISTRY_DATE, config.getLroeRegistryDate());
		
		return get(ctx, domainId);
	} 

//	private static void saveConfiguration(AONContext ctx, Integer domainId, List<EnterpriseData> history) {
//		history.stream().forEach(data -> {
//			if(data.getId() == null) {
//				EnterpriseDataDAO.insert(ctx, data.setDomain(domainId));
//			} else if(data.isUpdated()) {
//				EnterpriseDataDAO.update(ctx, data);
//			} else if(data.isRemoved()) {
//				EnterpriseDataDAO.delete(ctx, data.getId());
//			}
//		});
//	}
//	
//	private static void saveAdministration(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
//		saveConfiguration(ctx, domainId, config.getAdministrationHistory());
//	}
//	
//	private static void saveTbai(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
//		saveConfiguration(ctx, domainId, config.getTbaiDataHistory());
//	}
//
//	private static void saveSii(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
//		saveConfiguration(ctx, domainId, config.getSiiDataHistory());
//		AppParamDAO.save(ctx, domainId, AppParam.FS_MODEL_CFG_SII, 
//			"audit".equalsIgnoreCase( config.getSiiRegistryDate() ) ? "R" : "T");	
//	}
//	
//	private static void saveLroe(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
//		saveConfiguration(ctx, domainId, config.getLroeDataHistory());
//		AppParamDAO.save(ctx, domainId, AppParam.TBAI_REGISTRY_DATE, config.getLroeRegistryDate());
//	}
//	
//	private static void saveVerifactu(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
//		saveConfiguration(ctx, domainId, config.getVerifactuDataHistory());
//	}
//	
//	private static void saveNoVerifactu(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
//		saveConfiguration(ctx, domainId, config.getNoVerifactuDataHistory());
//	}
//	
//	private static void saveSif(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
//		saveConfiguration(ctx, domainId, config.getSifDataHistory());
//	}
//	
//	private static void saveNoSif(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
//		saveConfiguration(ctx, domainId, config.getNoSifDataHistory());
//	}
	
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

	private static class CommunicationEnabler implements InvoiceCommunicationTypeAccepter<Void> {
		
		private final EnablerContext ec;
		
		CommunicationEnabler(EnablerContext context) {
			this.ec = context;
		}
		
		private boolean isEnabled( InvoiceCommunicationType t ) {
			return ec.config.getData( t , ec.date)
				.filter( d -> d.getDataName() == ec.name )
				.isPresent();
		}
		private boolean isNotEnabled(InvoiceCommunicationType t) {
			return !isEnabled( t );
		}
		
		private void enable() {
			ec.config.addData( new CommunicationData()
				.setDataName(ec.name)
				.setTest( ec.test )
				.setStartDate(ec.date) 
			);
		}
		
		public Void visitNoSif() {
			AonCollectionUtils.stream(EnterpriseDataNames.getInvoiceCommunicationTypesNames())
				.filter( n -> n != ICC_NO_SIF )
				.filter( n -> n != ICC_LROE )
				.filter( n -> n != ICC_SII )
				.forEach( n -> ec.config.getData(n, ec.date).ifPresent( d -> closeData(d, ec.date)) );
			enable();
			return null;
		}
		
		@Override 
		public Void visitVERIFACTU() {
			if (isNotEnabled( InvoiceCommunicationType.VERIFACTU ) ) {
				if (!ec.config.isAEAT(ec.date) && !ec.config.isCanarias(ec.date)) {
					throw new AonCoreException( InvoiceCommunicationError.ICC_5001.getMessage() );
				}
				if (ec.config.isLroe( ec.date )) throw new AonCoreException( InvoiceCommunicationError.ICC_5011.getMessage() );
				if (ec.config.isTbai( ec.date )) throw new AonCoreException( InvoiceCommunicationError.ICC_5007.getMessage() );
				
				ec.config.getNoSifData(ec.date).ifPresent( d -> closeData(d, ec.date));
				ec.config.getSifData(ec.date).ifPresent( d -> closeData(d, ec.date));
				ec.config.getNoVerifactuData(ec.date).ifPresent( d -> closeData(d, ec.date));
				
				Date nextYearFirstDay = AonDateUtils.getYearFirstDay(AonDateUtils.getCurrentYear() + 1);
				boolean hasSii = ec.config.getSiiData(ec.date).isPresent(); 
				if ( hasSii) {
					if (!AonDateUtils.isSameDay( ec.date, nextYearFirstDay)) {
						throw new AonCoreException( InvoiceCommunicationError.ICC_6000.getMessage() );
					}
					if ( hasSii ) ec.config.getSiiData(ec.date).ifPresent( d -> closeData(d, ec.date));
					
				}
				enable();
			}
			return null; 
		}
		@Override 
		public Void visitNO_VERIFACTU() {
			if (isNotEnabled( InvoiceCommunicationType.NO_VERIFACTU ) ) {
				if (!ec.config.isAEAT(ec.date) && !ec.config.isCanarias(ec.date)) {
					throw new AonCoreException( InvoiceCommunicationError.ICC_5001.getMessage() );
				}
				if (ec.config.isLroe( ec.date )) throw new AonCoreException( InvoiceCommunicationError.ICC_5012.getMessage() );
				if (ec.config.isTbai( ec.date )) throw new AonCoreException( InvoiceCommunicationError.ICC_5008.getMessage() );
				
				ec.config.getNoSifData(ec.date).ifPresent( d -> closeData(d, ec.date));
				ec.config.getSifData(ec.date).ifPresent( d -> closeData(d, ec.date));
				
				Date nextYearFirstDay = AonDateUtils.getYearFirstDay(AonDateUtils.getCurrentYear() + 1);
				boolean hasSii = ec.config.getSiiData(ec.date).isPresent(); 
				boolean hasVerifactu = ec.config.getVerifactuData(ec.date).isPresent();
				if ( hasSii || hasVerifactu) {
					if (!AonDateUtils.isSameDay( ec.date, nextYearFirstDay)) {
						throw new AonCoreException( InvoiceCommunicationError.ICC_6001.getMessage() );
					}
					if ( hasSii ) ec.config.getSiiData(ec.date).ifPresent( d -> closeData(d, ec.date));
					if ( hasVerifactu ) ec.config.getVerifactuData(ec.date).ifPresent( d -> closeData(d, ec.date));
				}
				
				enable(); 
			}
			return null; 
		}
		
		@Override 
		public Void visitSIF() { 
			if (isNotEnabled( InvoiceCommunicationType.SIF ) ) {
				if (!ec.config.isNavarra(ec.date) ) {
					throw new AonCoreException( InvoiceCommunicationError.ICC_5005.getMessage() );
				}
				ec.config.getNoSifData(ec.date).ifPresent( d -> closeData(d, ec.date));
				enable();
			}
			return null; 
		}
		
		// ------ TODO ---------------
		@Override public Void visitSII() { enable(); return null; }
		@Override public Void visitTBAI() { enable(); return null; }
		@Override public Void visitLROE() { enable(); return null; }
		// ---------------------------
		// ---------------------------
		
		@Override public Void visitSERES() { return null; }
		@Override public Void visitEMAIL() { return null; }
		@Override public Void visitCLOSING() { return null; }
		@Override public Void visitFACTURAE() { return null; }
	}
	
}
