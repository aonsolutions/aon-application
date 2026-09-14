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
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.json.JSONObject;

import com.esferalia.aon.jooq.tables.DataAttach;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
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
import com.esferalia.aon.occam.api.model.finance.InvoiceBatch;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatchDetail;
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
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceBatchDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceBatchDetailDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO.InvoiceInfoFiller;
import com.esferalia.aon.occam.impl.jooq.validation.InvoiceCommunicationConfigurationValidation;
import com.esferalia.aon.occam.impl.jooq.validation.InvoiceValidation;
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
			public EnterpriseData fillValue(AONContext ctx, Administration administration, EnterpriseData data, ApplicationParameter param ) {
				return param.trueValue()
					? data.setName(administration.isBizkaia() ? EnterpriseDataNames.ICC_LROE.name() : EnterpriseDataNames.ICC_TBAI.name())
					: data;
			}
			
			@Override
			public void save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
				AppParamDAO.save(ctx, domainId,  AppParam.TBAI_ACTIVE, Boolean.toString(config.isTbai()));
				if(config.hasCommunication()) {
					AppParamDAO.save(ctx, domainId,  AppParam.APP_SALE_INVOICE_TEMPLATE_PARAM, "default");
				}
			}
		},
		TBAI_TEST {
			@Override
			public EnterpriseData fillValue(AONContext ctx, Administration administration, EnterpriseData data, ApplicationParameter param) {
				return data.setExpression(param.trueValue()? "test" : "prod");
			}

			@Override
			public void save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
				AppParamDAO.save(ctx, domainId, AppParam.TBAI_TEST, Boolean.toString(config.isTbaiTest()));
			}
		},
		TBAI_INCLUDE_DATE {
			@Override
			public EnterpriseData fillValue(AONContext ctx, Administration administration, EnterpriseData data, ApplicationParameter param) {
				Date date = AonDateUtils.parse(param.getValue(), YYYY_MM_DD);
				if(date == null) {
					date = DataResponseDAO.getFirstDataResponseDate(ctx, data.getDomain(), administration.isBizkaia()
							? DataResponseSource.LROE : DataResponseSource.TBAI);
				}
				return data.setStartDate(date);
			}

			@Override
			public void save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
				// NO HACE NADA!
			}
		},
		
		// -------------------------------------------------------------------- [VERIFACTU]
		
		VERIFACTU_ACTIVE { 
			@Override 
			public EnterpriseData fillValue(AONContext ctx, Administration administration, EnterpriseData data, ApplicationParameter param) {
				return  param.trueValue()
					? data.setName(EnterpriseDataNames.ICC_VERIFACTU.name())
					: data;
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
			public EnterpriseData fillValue(AONContext ctx, Administration administration, EnterpriseData data, ApplicationParameter param) {
				return data.setExpression(param.trueValue()? "test" : "prod");
			}

			@Override
			public void save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
				AppParamDAO.save(ctx, domainId, AppParam.VERIFACTU_TEST, Boolean.toString(config.isVerifactuTest()));
			}
		},
		VERIFACTU_INCLUDE_DATE {
			@Override
			public EnterpriseData fillValue(AONContext ctx, Administration administration, EnterpriseData data, ApplicationParameter param) {
				Date date = AonDateUtils.parse(param.getValue(), YYYY_MM_DD);
				if(date == null) {
					date = DataResponseDAO.getFirstDataResponseDate(ctx, data.getDomain(), DataResponseSource.VERIFACTU);
				}
				return data.setStartDate(date);
			}

			@Override
			public void save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
				// NO HACE NADA!
			}
		},
		
		// -------------------------------------------------------------------- [SII]
		
		SII_ACTIVE { 
			@Override 
			public EnterpriseData fillValue(AONContext ctx, Administration administration, EnterpriseData data, ApplicationParameter param) {
				return param.trueValue()
					? data.setName(EnterpriseDataNames.ICC_SII.name())
					: data;
			}

			@Override
			public void save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
				AppParamDAO.save(ctx,  domainId, AppParam.SII_ACTIVE, Boolean.toString(config.isSii()));
			}
		},
		SII_TEST {
			@Override
			public EnterpriseData fillValue(AONContext ctx, Administration administration, EnterpriseData data, ApplicationParameter param) {
				return data.setExpression(param.trueValue()? "test" : "prod");
			}

			@Override
			public void save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
				AppParamDAO.save(ctx, domainId, AppParam.SII_TEST, Boolean.toString(config.isSiiTest()));
			}
		},
		SII_INCLUDE_DATE {
			@Override
			public EnterpriseData fillValue(AONContext ctx, Administration administration, EnterpriseData data, ApplicationParameter param) {
				Date date = AonDateUtils.parse(param.getValue(), YYYY_MM_DD);
				if(date == null) {
					date = DataResponseDAO.getFirstDataResponseDate(ctx, data.getDomain(), DataResponseSource.SII);
				}
				return data.setStartDate(date);
			}

			@Override
			public void save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
				// NO HACE NADA!
			}
		}
		;
		
		private static final String YYYY_MM_DD = "yyyy-MM-dd";
		
		public static Optional<InvoiceCommunicationConfigurationParams> safeValueOf( String name ) {
			return AonCollectionUtils.stream(InvoiceCommunicationConfigurationParams.values())
				.filter( t -> AonStringUtils.equals(name , t.name()))
				.findAny();
		}

		public abstract EnterpriseData fillValue(AONContext ctx, Administration administration, EnterpriseData config, ApplicationParameter param);
		public abstract void save( AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config);
		
	}
	
	
	// ------------------------------------------------------ [READ]
	public static InvoiceCommunicationConfiguration get(AONContext ctx, int domainId) {	
		return get(ctx, domainId, false);
	}

	public static InvoiceCommunicationConfiguration get(AONContext ctx, int domainId, boolean check) {
		ctx.checkRead();
		InvoiceCommunicationConfiguration configuration = new InvoiceCommunicationConfiguration();

		fillAdministration(ctx, domainId, configuration);
		fillTbai(ctx, domainId, configuration, check);
		fillLroe(ctx, domainId, configuration, check);
		fillSii(ctx, domainId, configuration, check);
		fillVerifactu(ctx, domainId, configuration, check);
		fillNoVerifactu(ctx, domainId, configuration, check);
		fillSif(ctx, domainId, configuration, check);
		fillNoSif(ctx, domainId, configuration);
		
		return checkConfigurationConsistency(ctx, domainId, configuration, check);
	}
	
	private static void fillAdministration(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
		config.setAdministrationHistory(getIccHistory(ctx, domainId, EnterpriseDataNames.ICC_ADMINISTRATION) );
		EnterpriseData data = getIccData(config.getAdministrationHistory());
		if(data != null) config.setAdministration(Administration.safeValueOf(data.getExpression()));
		if(config.getAdministration() == null) {
			EnterpriseDAO.opt(ctx, f -> f.getDomainProperty().eq(domainId))
			.ifPresent( enterprise -> {
				Administration admon = AppParamDAO.get(ctx, domainId, AppParam.FS_DEFAULT_ADMINISTRATION)
						.map( ApplicationParameter::getValue )
						.map( Integer::parseInt )
						.map( Administration::safeValueOf )
						.orElse( Administration.UNKNOWN );
					EnterpriseData ed = EnterpriseDataDAO.insert(ctx, new EnterpriseData()
						.setDomain(domainId)
						.setEnterprise(enterprise.getId())
						.setName(EnterpriseDataNames.ICC_ADMINISTRATION.name())
						.setExpression(admon.name())
						.setStartDate(AonDateUtils.today()));
					config.getAdministrationHistory().add(ed);
					config.setAdministration(admon);	
			});
		}
	}

	private static void fillTbai(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config, boolean check) {
		config.setTbaiDataHistory( getIccHistory(ctx, domainId, EnterpriseDataNames.ICC_TBAI) );
		config.setTbaiData( getIccData( config.getTbaiDataHistory() ) );
		
		if(check) config.setTbaiInvoice(DataResponseDAO.has(ctx, domainId, DataResponseSource.TBAI, AonDateUtils.getCurrentYear()));
		
		if(config.getTbaiDataHistory().isEmpty() && (config.isAraba() || config.isGipuzkoa())) {
			EnterpriseDAO.opt(ctx, f -> f.getDomainProperty().eq(domainId))
			.ifPresent( enterprise -> {
				EnterpriseData oldTbaiData = new EnterpriseData()
					.setDomain(domainId)
					.setEnterprise(enterprise.getId());
				AppParamDAO.getByPattern(ctx, domainId, "TBAI_%")
				.forEach(r -> InvoiceCommunicationConfigurationParams.safeValueOf(r.getName() ) .ifPresent(t -> t.fillValue(ctx, config.getAdministration(), oldTbaiData, r)));
						
				if(AonStringUtils.isNotBlank(oldTbaiData.getName())) {
					if(oldTbaiData.getStartDate() == null) {
						oldTbaiData.setStartDate(AonDateUtils.today());
					}	
				
					config.setTbaiData(EnterpriseDataDAO.insert(ctx, oldTbaiData));
					config.getTbaiDataHistory().add(oldTbaiData);
				}
			});
		}
		AppParamDAO.get(ctx, domainId, AppParam.TBAI_ACTIVE).ifPresent(r -> {
			AppParamDAO.delete(ctx, domainId, AppParam.TBAI_ACTIVE);
			AppParamDAO.delete(ctx, domainId, AppParam.TBAI_TEST);
			AppParamDAO.delete(ctx, domainId, AppParam.TBAI_INCLUDE_DATE);			
		});

	}
	
	private static void fillLroe(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config, boolean check) {
		config.setLroeDataHistory(getIccHistory(ctx, domainId, EnterpriseDataNames.ICC_LROE));
		config.setLroeData(getIccData( config.getLroeDataHistory() ));
		
		if(check) config.setLroeInvoice(DataResponseDAO.has(ctx, domainId, DataResponseSource.LROE, AonDateUtils.getCurrentYear()));
		
		if(config.getLroeDataHistory().isEmpty() && config.isBizkaia()) {
			EnterpriseDAO.opt(ctx, f -> f.getDomainProperty().eq(domainId))
			.ifPresent( enterprise -> {
				EnterpriseData oldLroeData = new EnterpriseData()
					.setDomain(domainId)
					.setEnterprise(enterprise.getId());
				AppParamDAO.getByPattern(ctx, domainId, "TBAI_%")
				.forEach(r -> InvoiceCommunicationConfigurationParams.safeValueOf(r.getName() ) .ifPresent(t -> t.fillValue(ctx, config.getAdministration(), oldLroeData, r)));
				if(AonStringUtils.isNotBlank(oldLroeData.getName())) {
					if(oldLroeData.getStartDate() == null) {
						oldLroeData.setStartDate(AonDateUtils.today());
					}
					config.setLroeData(EnterpriseDataDAO.insert(ctx, oldLroeData));
					config.getLroeDataHistory().add(oldLroeData);
				}
			});
		}
		
		AppParamDAO.get(ctx, domainId, AppParam.TBAI_ACTIVE).ifPresent(r -> {
			AppParamDAO.delete(ctx, domainId, AppParam.TBAI_ACTIVE);
			AppParamDAO.delete(ctx, domainId, AppParam.TBAI_TEST);
			AppParamDAO.delete(ctx, domainId, AppParam.TBAI_INCLUDE_DATE);			
		});
		
		// Fecha de registro contable que se envía al LROE. Fecha de Auditoria (creation_date) o Fecha de IVA (tax_date)
		AppParamDAO.get(ctx, domainId, AppParam.TBAI_REGISTRY_DATE)
		.ifPresent( p -> config.setLroeRegistryDate(p.getValue()));
	}
	
	private static void fillVerifactu(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config, boolean check) {
		config.setVerifactuDataHistory(getIccHistory(ctx, domainId, EnterpriseDataNames.ICC_VERIFACTU));
		config.setVerifactuData(getIccData(config.getVerifactuDataHistory()));
		if(check) config.setVerifactuInvoice(DataResponseDAO.has(ctx, domainId, DataResponseSource.VERIFACTU, AonDateUtils.getCurrentYear()));
		if(config.getVerifactuDataHistory().isEmpty()) {
			EnterpriseDAO.opt(ctx, f -> f.getDomainProperty().eq(domainId))
			.ifPresent( enterprise -> {
				EnterpriseData oldVerifactuData = new EnterpriseData()
					.setDomain(domainId)
					.setEnterprise(enterprise.getId());
				AppParamDAO.getByPattern(ctx, domainId, "VERIFACTU_%")
				.forEach(r -> InvoiceCommunicationConfigurationParams.safeValueOf(r.getName() ) .ifPresent(t -> t.fillValue(ctx, config.getAdministration(), oldVerifactuData, r)));
				if(AonStringUtils.isNotBlank(oldVerifactuData.getName())) {
					if(oldVerifactuData.getStartDate() == null) {
						oldVerifactuData.setStartDate(AonDateUtils.today());
					}
					config.setVerifactuData(EnterpriseDataDAO.insert(ctx, oldVerifactuData));
					config.getVerifactuDataHistory().add(oldVerifactuData);
				}
			});
		}
		
		AppParamDAO.get(ctx, domainId, AppParam.VERIFACTU_ACTIVE).ifPresent(r -> {
			AppParamDAO.delete(ctx, domainId, AppParam.VERIFACTU_ACTIVE);
			AppParamDAO.delete(ctx, domainId, AppParam.VERIFACTU_TEST);
			AppParamDAO.delete(ctx, domainId, AppParam.VERIFACTU_INCLUDE_DATE);			
		});
	}
	
	private static void fillSii(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config, boolean check) {
		config.setSiiDataHistory(getIccHistory(ctx, domainId, EnterpriseDataNames.ICC_SII));
		config.setSiiData(getIccData(config.getSiiDataHistory()));
		
		if(check) config.setSiiInvoice(DataResponseDAO.has(ctx, domainId, DataResponseSource.SII, AonDateUtils.getCurrentYear()));

		if(config.getSiiDataHistory().isEmpty()) {
			EnterpriseDAO.opt(ctx, f -> f.getDomainProperty().eq(domainId))
			.ifPresent( enterprise -> {
				EnterpriseData oldSiiData = new EnterpriseData()
					.setDomain(domainId)
					.setEnterprise(enterprise.getId());
				AppParamDAO.getByPattern(ctx, domainId, "SII_%")
				.forEach(r -> InvoiceCommunicationConfigurationParams.safeValueOf(r.getName() ) .ifPresent(t -> t.fillValue(ctx, config.getAdministration(), oldSiiData, r)));
				if(AonStringUtils.isNotBlank(oldSiiData.getName())) {
					if(oldSiiData.getStartDate() == null) {
						oldSiiData.setStartDate(AonDateUtils.today());
					}
					config.setSiiData(EnterpriseDataDAO.insert(ctx, oldSiiData));
					config.getSiiDataHistory().add(oldSiiData);
				}
			});
		}
		
		AppParamDAO.get(ctx, domainId, AppParam.SII_ACTIVE).ifPresent(r -> {
			AppParamDAO.delete(ctx, domainId, AppParam.SII_ACTIVE);
			AppParamDAO.delete(ctx, domainId, AppParam.SII_TEST);
			AppParamDAO.delete(ctx, domainId, AppParam.SII_INCLUDE_DATE);			
		});
		
		// Fecha de registro contable que se envía al SII. Fecha de Auditoria (creation_date) o Fecha de IVA (tax_date)
		AppParamDAO.get(ctx, domainId, AppParam.FS_MODEL_CFG_SII)
			.ifPresent( p -> config.setSiiRegistryDate("R".equalsIgnoreCase(p.getValue()) ? "audit" : "tax"));
		
		// true si el dominio ya está preparado para la nueva pantalla del SII.
		AppParamDAO.get(ctx, domainId, AppParam.SII_PREPARE_NEW_SII)
		.ifPresent( p -> config.setPrepareNewSii( p.trueValue() ));
		
	}
	
	private static void fillNoVerifactu(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config, boolean check) {
		config.setNoVerifactuDataHistory(getIccHistory(ctx, domainId, EnterpriseDataNames.ICC_NO_VERIFACTU));
		config.setNoVerifactuData(getIccData(config.getNoVerifactuDataHistory()));
		if(check) config.setNoVerifactuInvoice(DataResponseDAO.has(ctx, domainId, DataResponseSource.NO_VERIFACTU, AonDateUtils.getCurrentYear()));
	}
	
	private static void fillSif(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config, boolean check) {
		config.setSifDataHistory(getIccHistory(ctx, domainId, EnterpriseDataNames.ICC_SIF));
		config.setSifData(getIccData(config.getSifDataHistory()));
		if(check) config.setSifInvoice(DataResponseDAO.has(ctx, domainId, DataResponseSource.SIF, AonDateUtils.getCurrentYear()));
	}
	
	private static void fillNoSif(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
		config.setNoSifDataHistory(getIccHistory(ctx, domainId, EnterpriseDataNames.ICC_NO_SIF));
		config.setNoSifData(getIccData(config.getNoSifDataHistory()));
	}
	
	private static InvoiceCommunicationConfiguration checkConfigurationConsistency(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config, boolean check) {
		if(config.isNoSif()) {
			if(config.isSif()) {
				updateEndDate(ctx, domainId, config.getSifData(), config.getNoSifData().getStartDate());
				fillSif(ctx, domainId, config, check);
			}
			
			if(config.isTbai()) {
				updateEndDate(ctx, domainId, config.getTbaiData(), config.getNoSifData().getStartDate());
				fillTbai(ctx, domainId, config, check);
			}
			
			if(config.isNoVerifactu()) {
				updateEndDate(ctx, domainId, config.getNoVerifactuData(), config.getNoSifData().getStartDate());
				fillNoVerifactu(ctx, domainId, config, check);
			}
			
			if(config.isVerifactu()) {
				updateEndDate(ctx, domainId, config.getVerifactuData(), config.getNoSifData().getStartDate());
				fillVerifactu(ctx, domainId, config, check);
			}
		}
		
		if(config.isSif() && !config.isSifTest()) {
			if(config.isLroe()) {
				updateEndDate(ctx, domainId, config.getSifData(), config.getLroeData().getStartDate());
				fillSif(ctx, domainId, config, check);
			}
			
			if(config.isTbai()) {
				updateEndDate(ctx, domainId, config.getSifData(), config.getTbaiData().getStartDate());
				fillSif(ctx, domainId, config, check);
			}
			
			if(config.isNoVerifactu()) {
				updateEndDate(ctx, domainId, config.getSifData(), config.getNoVerifactuData().getStartDate());
				fillSif(ctx, domainId, config, check);
			}
			
			if(config.isVerifactu()) {
				updateEndDate(ctx, domainId, config.getSifData(), config.getVerifactuData().getStartDate());
				fillSif(ctx, domainId, config, check);
			}
			
			if(config.isSii()) {
				updateEndDate(ctx, domainId, config.getSifData(), config.getSiiData().getStartDate());
				fillSif(ctx, domainId, config, check);
			}
		
			if(config.isAEAT() || config.isCanarias()) {
				if(!config.hasVerifactuInvoice() && !config.hasNoVerifactuInvoice() && !config.hasSifInvoice()) {
					EnterpriseDataDAO.update(ctx, config.getSifData().setName(EnterpriseDataNames.ICC_NO_VERIFACTU.name()));
					fillSif(ctx, domainId, config, check);
					fillNoVerifactu(ctx, domainId, config, check);	
				}
				
				if(!config.hasVerifactuInvoice()) {
					if(config.willBeNoVerifactu()) {
						config.getNoVerifactuDataHistory().stream().filter(f -> f.getStartDate().after(new Date())).findFirst()
						.ifPresent(ed -> EnterpriseDataDAO.delete(ctx, ed.getId()));
					}
					EnterpriseDataDAO.update(ctx, config.getSifData().setName(EnterpriseDataNames.ICC_NO_VERIFACTU.name()));
					fillSif(ctx, domainId, config, check);
					fillNoVerifactu(ctx, domainId, config, check);
					
					ctx.getDslContext().update(INVOICE_INFO)
					.set(INVOICE_INFO.TYPE, InvoiceCommunicationType.NO_VERIFACTU.value())
					.where(INVOICE_INFO.DOMAIN.eq(domainId)
						.and(INVOICE_INFO.TYPE.eq(InvoiceCommunicationType.SIF.value()))
						.and(INVOICE_INFO.CREATION_DATE.ge(AonDateUtils.toTimestamp(AonDateUtils.getYearFirstDay(new Date()))))
					)
					.execute();
					
					ctx.getDslContext().update(INVOICE_BATCH)
					.set(INVOICE_BATCH.TYPE, InvoiceCommunicationType.NO_VERIFACTU.value())
					.where(INVOICE_BATCH.DOMAIN.eq(domainId)
						.and(INVOICE_BATCH.TYPE.eq(InvoiceCommunicationType.SIF.value()))
						.and(INVOICE_BATCH.CREATION_DATE.ge(AonDateUtils.toTimestamp(AonDateUtils.getYearFirstDay(new Date()))))
					)
					.execute();
					
					ctx.getDslContext().update(DATA_RESPONSE)
					.set(DATA_RESPONSE.SOURCE, DataResponseSource.NO_VERIFACTU.value())
					.where(DATA_RESPONSE.DOMAIN.eq(domainId)
						.and(DATA_RESPONSE.SOURCE.eq(DataResponseSource.SIF.value()))
						.and(DATA_RESPONSE.CREATION_DATE.ge(AonDateUtils.toTimestamp(AonDateUtils.getYearFirstDay(new Date()))))
					)
					.execute();
				}
				
				if(config.hasVerifactuInvoice()) {
					updateEndDate(ctx, domainId, config.getSifData(), new Date());
					fillSif(ctx, domainId, config, check);
					
					EnterpriseData vd = new EnterpriseData()
							.setDomain(domainId)
							.setEnterprise( config.getSifData().getEnterprise() )
							.setName(EnterpriseDataNames.ICC_VERIFACTU.name())
							.setStartDate( new Date() );
					EnterpriseDataDAO.insert(ctx, vd);
				}
			}
		}
		
		return config;
	}
	
	private static void updateEndDate(AONContext ctx, Integer domainId, EnterpriseData data, Date endDate) {
		data.setEndDate(endDate);
		EnterpriseDataDAO.update(ctx, data);
	}
	
	private static List<EnterpriseData> getIccHistory(AONContext ctx, Integer domainId, EnterpriseDataNames name) {
		return EnterpriseDataDAO.getList(ctx, f -> f.getDomainProperty().eq(domainId).and(f.getNameProperty().eq(name.name())));
	}
	
	private static EnterpriseData getIccData(List<EnterpriseData> history) {
		if(history == null || history.isEmpty()) return null;
		return history.stream().filter(f -> (f.getStartDate() != null && f.getStartDate().before(new Date()))
				&& (f.getEndDate() == null || f.getEndDate().after(new Date()))).findFirst().orElse(null);
	}

	// ----------------------------------------------------- [WRITE]
	
	public static InvoiceCommunicationConfiguration save(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
		ctx.checkWrite();
		InvoiceCommunicationConfigurationValidation.validate(ctx, config);
		saveAdministration(ctx, domainId, config);
		saveTbai(ctx, domainId, config);
		saveLroe(ctx, domainId, config);
		saveSii(ctx, domainId, config);
		saveVerifactu(ctx, domainId, config);
		saveNoVerifactu(ctx, domainId, config);
		saveSif(ctx, domainId, config);
		saveNoSif(ctx, domainId, config);
		return get(ctx, domainId, true);
	} 
	
	private static void saveConfiguration(AONContext ctx, Integer domainId, List<EnterpriseData> history) {
		history.stream().forEach(data -> {
			if(data.getId() == null) {
				EnterpriseDataDAO.insert(ctx, data.setDomain(domainId));
			} else if(data.isUpdated()) {
				EnterpriseDataDAO.update(ctx, data);
			} else if(data.isRemoved()) {
				EnterpriseDataDAO.delete(ctx, data.getId());
			}
		});
	}
	
	private static void saveAdministration(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
		saveConfiguration(ctx, domainId, config.getAdministrationHistory());
	}
	
	private static void saveTbai(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
		saveConfiguration(ctx, domainId, config.getTbaiDataHistory());
	}

	private static void saveSii(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
		saveConfiguration(ctx, domainId, config.getSiiDataHistory());
		AppParamDAO.save(ctx, domainId, AppParam.FS_MODEL_CFG_SII, 
			"audit".equalsIgnoreCase( config.getSiiRegistryDate() ) ? "R" : "T");	
	}
	
	private static void saveLroe(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
		saveConfiguration(ctx, domainId, config.getLroeDataHistory());
		AppParamDAO.save(ctx, domainId, AppParam.TBAI_REGISTRY_DATE, config.getLroeRegistryDate());
	}
	
	private static void saveVerifactu(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
		saveConfiguration(ctx, domainId, config.getVerifactuDataHistory());
	}
	
	private static void saveNoVerifactu(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
		saveConfiguration(ctx, domainId, config.getNoVerifactuDataHistory());
	}
	
	private static void saveSif(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
		saveConfiguration(ctx, domainId, config.getSifDataHistory());
	}
	
	private static void saveNoSif(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
		saveConfiguration(ctx, domainId, config.getNoSifDataHistory());
	}
	
	// *************************************************************
	// ************************** [INVOICES] ***********************
	// *************************************************************
	
	public static Stream<Invoice> getInvoices(AONContext ctx, InvoiceCommunicationParams params) {
		if (params == null) throw new AonCoreException("No params");
		if (params.getDomain() == null) throw new AonCoreException("No domain");
		if (params.getCommunicationType() == null) throw new AonCoreException("No type");

		Condition invoiceInfoCondition = INVOICE_INFO.INVOICE.eq(INVOICE.ID);
		if(params.getCommunicationType() != null) 
			invoiceInfoCondition = invoiceInfoCondition.and(INVOICE_INFO.TYPE.eq(params.getCommunicationType().value()));
		return ctx.getDslContext().select()
			.from(INVOICE)
			.leftOuterJoin(INVOICE_INFO).on(invoiceInfoCondition)
			.where(getFilter(params))
			.and(INVOICE.NUMBER.gt(0))
			.and(InvoiceDAO.NOT_ANNULLED)
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
//			.and(INVOICE_INFO.TYPE.isNull().or(INVOICE_INFO.TYPE.eq(params.getCommunicationType().value())))
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

	public static InvoiceBatch saveAccept(AONContext ctx, Domain domain, InvoiceCommunicationType communicationType, byte[] request, byte[] response) {
		return save(ctx, domain, communicationType, InvoiceCommunicationOperation.REGISTER, request, response);
	}
	
	public static InvoiceBatch saveCancel(AONContext ctx, Domain domain, InvoiceCommunicationType communicationType, byte[] request, byte[] response) {
		return save(ctx, domain, communicationType, InvoiceCommunicationOperation.ANNULMENT, request, response);
	}
	
	public static InvoiceBatch saveModify(AONContext ctx, Domain domain, InvoiceCommunicationType communicationType, byte[] request, byte[] response) {
		return save(ctx, domain, communicationType, InvoiceCommunicationOperation.MODIFICATION, request, response);
	}
	
	public static InvoiceBatch save(AONContext ctx, Domain domain, InvoiceCommunicationType communicationType, InvoiceCommunicationOperation operation, byte[] request, byte[] response) {
		DataRequest dataRequest = saveRequest(ctx, domain, communicationType, request);
		DataResponse dataResponse = saveResponse(ctx, domain, communicationType, dataRequest, response);		
		return saveInvoiceBatch(ctx, domain, dataResponse, communicationType, operation);
	}
	
	public static void saveInvoice(AONContext ctx, Domain domain, InvoiceBatch batch, Integer invoiceId, InvoiceCommunicationStatus status) {
		saveInvoiceInfo(ctx, domain.getId(), invoiceId, batch.getType(), status);	
		saveInvoiceBatchdetail(ctx, batch, invoiceId, status);
	}
	
	/**
	 * Fichero enviado en la ultima comunicacion de la factura del tipo y de la
	 * operacion indicados, o vacio si la factura no se ha comunicado asi todavia.
	 */
	public static Optional<byte[]> getLastRequest(AONContext ctx, Integer invoiceId, InvoiceCommunicationType communicationType, InvoiceCommunicationOperation operation) {
		Byte attachSource = DataAttachSource.safeByteOf(communicationType);
		if (invoiceId == null || communicationType == null || operation == null || attachSource == null) {
			return Optional.empty();
		}
		
		return ctx.getDslContext().select(DATA_ATTACH.DATA)
			.from(INVOICE_BATCH_DETAIL)
			.join(INVOICE_BATCH).on(INVOICE_BATCH.ID.eq(INVOICE_BATCH_DETAIL.INVOICE_BATCH))
			.join(DATA_RESPONSE).on(DATA_RESPONSE.ID.eq(INVOICE_BATCH.DATA_RESPONSE))
			.join(DATA_ATTACH).on(DATA_ATTACH.SOURCE_ID.eq(DATA_RESPONSE.DATA_REQUEST)
				.and(DATA_ATTACH.SOURCE.eq(attachSource))
				.and(DATA_ATTACH.TYPE.eq(DataAttachType.REQUEST.value()))
			)
			.where(INVOICE_BATCH_DETAIL.INVOICE.eq(invoiceId))
			.and(INVOICE_BATCH.TYPE.eq(communicationType.value()))
			.and(INVOICE_BATCH.OPERATION.eq(operation.value()))
			.orderBy(INVOICE_BATCH.DATE.desc())
			.limit(1)
			.fetchOptional(DATA_ATTACH.DATA);
	}
	
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
	
	public static InvoiceBatch saveInvoiceBatch(AONContext ctx, Domain domain, DataResponse dataResponse, InvoiceCommunicationType communicationType, InvoiceCommunicationOperation operation) {
		InvoiceBatch invoiceBatch = new InvoiceBatch()
			.setDomain(domain.getId())
			.setDate(new Date())
			.setDataResponse(dataResponse.getId())
			.setType(communicationType)
			.setOperation(operation);
		return InvoiceBatchDAO.save(ctx, invoiceBatch);
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
	
	public static InvoiceBatchDetail saveInvoiceBatchdetail(AONContext ctx, InvoiceBatch invoiceBatch, Integer invoice, InvoiceCommunicationStatus status) {
		InvoiceBatchDetail ibd = new InvoiceBatchDetail()
			.setDomain(invoiceBatch.getDomain())
			.setInvoiceBatch(invoiceBatch.getId())
			.setInvoice(invoice)
			.setStatus(status);
		return InvoiceBatchDetailDAO.save(ctx, ibd);
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

	public static void prepareNewSii(AONContext ctx, Integer year) {
		Date from = AonDateUtils.getYearFirstDay(year);
		Date to = AonDateUtils.getYearLastDay(year);
		ctx.getDslContext()
			.select(DATA_RESPONSE.SOURCE_ID,DATA_RESPONSE_DETAIL.DATA_VALUE)
			.from(DATA_RESPONSE)
			.join(INVOICE).on(INVOICE.ID.eq(DATA_RESPONSE.SOURCE_ID))
			.join(DATA_RESPONSE_DETAIL).on(DATA_RESPONSE.ID.eq(DATA_RESPONSE_DETAIL.DATA_RESPONSE))
			.where(DATA_RESPONSE.DOMAIN.eq(ctx.getDomainId()))
			.and(DATA_RESPONSE.SOURCE.eq(DataResponseSource.SII_INVOICE.value()))
			.and(DATA_RESPONSE_DETAIL.DOMAIN.eq(ctx.getDomainId()))
			.and(DATA_RESPONSE_DETAIL.DATA_VARIABLE.eq("status"))
			.and(DATA_RESPONSE.CREATION_DATE.ge(AonDateUtils.toTimestamp(from)))
			.and(DATA_RESPONSE.CREATION_DATE.le(AonDateUtils.toTimestamp(to)))
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
	}
	
	public static void validatePreCommunicationInvoiceAnnulment(AONContext ctx, Invoice invoice) {
		InvoiceValidation.validatePreCommunicationInvoiceAnnulment(ctx, null, invoice);
	}
}
