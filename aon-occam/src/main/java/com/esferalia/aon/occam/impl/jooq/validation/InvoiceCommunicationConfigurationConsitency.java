package com.esferalia.aon.occam.impl.jooq.validation;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.impl.jooq.dao.EnterpriseDataDAO;
import com.esferalia.aon.watson.server.AonDateUtils;

public class InvoiceCommunicationConfigurationConsitency {

	private InvoiceCommunicationConfigurationConsitency() {
	}
	
	public static InvoiceCommunicationConfiguration check(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
//		checkAdministration(ctx, domainId, config);
		checkNoSif(ctx, domainId, config);
		checkSif(ctx, domainId, config);
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
	
		return config;
	}
	
//	private static void checkAdministration(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
//		config.getAdministrationStream()
//			.filter( cc -> cc.getAdministration().filter( a -> a.isUnknown()).isPresent() )
//			.forEach( cc -> EnterpriseDataDAO.delete(ctx, cc.getId()) );
//	}

	private static void checkNoSif(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
		config.getNoSifData()
			.map( cc -> cc.getStartDate() )
			.map( AonDateUtils::previousDay )
			.ifPresent( closeDate -> {
				config.getSifData().ifPresent( cc -> EnterpriseDataDAO.updateEndDate(ctx, cc, closeDate));
				config.getTbaiData().ifPresent( cc -> EnterpriseDataDAO.updateEndDate(ctx, cc, closeDate));
				config.getNoVerifactuData().ifPresent( cc -> EnterpriseDataDAO.updateEndDate(ctx, cc, closeDate));
				config.getVerifactuData().ifPresent( cc -> EnterpriseDataDAO.updateEndDate(ctx, cc, closeDate));
			});
	}
	
	private static void checkSif(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config) {
		config.getSifData()
		.map( cc -> cc.getStartDate() )
		.map( AonDateUtils::previousDay )
		.ifPresent( closeDate -> {
			config.getLroeData().ifPresent( cc -> EnterpriseDataDAO.updateEndDate(ctx, cc, closeDate));
			config.getTbaiData().ifPresent( cc -> EnterpriseDataDAO.updateEndDate(ctx, cc, closeDate));
			config.getNoVerifactuData().ifPresent( cc -> EnterpriseDataDAO.updateEndDate(ctx, cc, closeDate));
			config.getVerifactuData().ifPresent( cc -> EnterpriseDataDAO.updateEndDate(ctx, cc, closeDate));
			config.getSiiData().ifPresent( cc -> EnterpriseDataDAO.updateEndDate(ctx, cc, closeDate));
		});
		
//	if(config.isSif() && !config.isSifTest()) {
//		if(config.isAEAT() || config.isCanarias()) {
//			if(!config.hasVerifactuInvoice() && !config.hasNoVerifactuInvoice() && !config.hasSifInvoice()) {
//				EnterpriseDataDAO.update(ctx, config.getSifData().setName(EnterpriseDataNames.ICC_NO_VERIFACTU.name()));
//				fillSif(ctx, domainId, config);
//				fillNoVerifactu(ctx, domainId, config);	
//			}
//			
//			if(!config.hasVerifactuInvoice()) {
//				if(config.willBeNoVerifactu()) {
//					config.getNoVerifactuDataHistory().stream().filter(f -> f.getStartDate().after(new Date())).findFirst()
//					.ifPresent(ed -> EnterpriseDataDAO.delete(ctx, ed.getId()));
//				}
//				EnterpriseDataDAO.update(ctx, config.getSifData().setName(EnterpriseDataNames.ICC_NO_VERIFACTU.name()));
//				fillSif(ctx, domainId, config);
//				fillNoVerifactu(ctx, domainId, config);
//				
//				ctx.getDslContext().update(INVOICE_INFO)
//				.set(INVOICE_INFO.TYPE, InvoiceCommunicationType.NO_VERIFACTU.value())
//				.where(INVOICE_INFO.DOMAIN.eq(domainId)
//					.and(INVOICE_INFO.TYPE.eq(InvoiceCommunicationType.SIF.value()))
//					.and(INVOICE_INFO.CREATION_DATE.ge(AonDateUtils.toTimestamp(AonDateUtils.getYearFirstDay(new Date()))))
//				)
//				.execute();
//				
//				ctx.getDslContext().update(INVOICE_BATCH)
//				.set(INVOICE_BATCH.TYPE, InvoiceCommunicationType.NO_VERIFACTU.value())
//				.where(INVOICE_BATCH.DOMAIN.eq(domainId)
//					.and(INVOICE_BATCH.TYPE.eq(InvoiceCommunicationType.SIF.value()))
//					.and(INVOICE_BATCH.CREATION_DATE.ge(AonDateUtils.toTimestamp(AonDateUtils.getYearFirstDay(new Date()))))
//				)
//				.execute();
//				
//				ctx.getDslContext().update(DATA_RESPONSE)
//				.set(DATA_RESPONSE.SOURCE, DataResponseSource.NO_VERIFACTU.value())
//				.where(DATA_RESPONSE.DOMAIN.eq(domainId)
//					.and(DATA_RESPONSE.SOURCE.eq(DataResponseSource.SIF.value()))
//					.and(DATA_RESPONSE.CREATION_DATE.ge(AonDateUtils.toTimestamp(AonDateUtils.getYearFirstDay(new Date()))))
//				)
//				.execute();
//			}
//			
//			if(config.hasVerifactuInvoice()) {
//				updateEndDate(ctx, domainId, config.getSifData(), new Date());
//				fillSif(ctx, domainId, config);
//				
//				EnterpriseData vd = new EnterpriseData()
//						.setDomain(domainId)
//						.setEnterprise( config.getSifData().getEnterprise() )
//						.setName(EnterpriseDataNames.ICC_VERIFACTU.name())
//						.setStartDate( new Date() );
//				EnterpriseDataDAO.insert(ctx, vd);
//			}
//		}
//	}
		
	}
	

}
