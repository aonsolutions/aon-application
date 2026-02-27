package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_ADMINISTRATION;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_LROE;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_NO_SIF;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_SII;

import java.util.Date;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.EnterpriseData;
import com.esferalia.aon.occam.api.model.EnterpriseDataNames;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType.InvoiceCommunicationTypeAccepter;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;

class ICCEnablerDAO {
	
	private static class EnablerContext {
		AONContext ctx;
		Integer domainId;
		InvoiceCommunicationConfiguration config;
		Administration admon;
		CommunicationData data;
		
		EnablerContext(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config, Administration admon, CommunicationData data) {
			this.ctx = ctx;
			this.domainId = domainId;
			this.config = config;
			this.admon = admon;
			this.data = data;
		}
	}
	
	static InvoiceCommunicationConfiguration enable(AONContext ctx, Integer domainId, Administration admon, CommunicationData data) {
		if (admon == null
		 || data == null 
		 || data.getStartDate() == null
		 || data.getName() == null 
		 || data.getDataName() == null) throw new AonCoreException( InvoiceCommunicationError.ICC_5000.getMessage() );
		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, domainId);
		
		enableAdministration( config, admon, data.getStartDate());
		
		EnablerContext ec = new EnablerContext(ctx, domainId, config, admon, data);
		CommunicationEnabler ce = new CommunicationEnabler( ec );
		if ( data.isNoSif() ) {
			ec.config = ce.visitNoSif();
		} else {
			ec.config = data.getCommunicationType()
				.map( t -> t.accept( ce ) )
				.orElse( config );
		}
		return InvoiceCommunicationDAO.save(ctx, domainId, ec.config);
	}
	
	static void enableAdministration(InvoiceCommunicationConfiguration config, Administration admon, Date date) {
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
	
	private static EnterpriseData closeData(EnterpriseData enterpriseData, Date date) {
		Date endDate = AonDateUtils.previousDay(date);
		return EnterpriseDataDAO.setEndDate(enterpriseData, endDate);
	}
	
	private static class CommunicationEnabler implements InvoiceCommunicationTypeAccepter<InvoiceCommunicationConfiguration> {
		private final EnablerContext ec;
		
		CommunicationEnabler(EnablerContext context) {
			this.ec = context;
		}
		
		private boolean isEnabled( EnterpriseDataNames n ) {
			return ec.config.getData( n , ec.data.getStartDate())
				.filter( d -> d.getDataName() == ec.data.getDataName() )
				.isPresent();
		}
		private boolean isNotEnabled(EnterpriseDataNames n) {
			return !isEnabled( n );
		}
		
		private boolean isEnabled( InvoiceCommunicationType t ) {
			return ec.config.getData( t , ec.data.getStartDate())
				.filter( d -> d.getDataName() == ec.data.getDataName() )
				.isPresent();
		}
		private boolean isNotEnabled(InvoiceCommunicationType t) {
			return !isEnabled( t );
		}
		
		private void enable() {
			ec.config.addData( new CommunicationData()
				.setDataName(ec.data.getDataName())
				.setTest( ec.data.isTest() )
				.setStartDate(ec.data.getStartDate())
				.setExemptType( ec.data.getExemptType()) 
			);
		}
		
		public InvoiceCommunicationConfiguration visitNoSif() {
			if (isNotEnabled( ICC_NO_SIF ) ) {
				AonCollectionUtils.stream(EnterpriseDataNames.getInvoiceCommunicationTypesNames())
					.filter( n -> n != ICC_LROE )
					.filter( n -> n != ICC_SII )
					.forEach( n -> 
						ec.config.getData(n, ec.data.getStartDate())
						.ifPresent( d -> closeData(d, ec.data.getStartDate())) 
					);
				enable();
			}
			return ec.config;
		}
		
		@Override 
		public InvoiceCommunicationConfiguration visitVERIFACTU() {
			if (isNotEnabled( InvoiceCommunicationType.VERIFACTU ) ) {
				if (ec.config.isNotAEAT(ec.data.getStartDate()) && ec.config.isNotCanarias(ec.data.getStartDate())) {
					throw new AonCoreException( InvoiceCommunicationError.ICC_5001.getMessage() );
				}
				enable();
				if (ec.config.isLroe( ec.data.getStartDate() )) throw new AonCoreException( InvoiceCommunicationError.ICC_5011.getMessage() );
				if (ec.config.isTbai( ec.data.getStartDate() )) throw new AonCoreException( InvoiceCommunicationError.ICC_5007.getMessage() );
				
				ec.config.getNoSifData(ec.data.getStartDate()).ifPresent( d -> closeData(d, ec.data.getStartDate()));
				ec.config.getSifData(ec.data.getStartDate()).ifPresent( d -> closeData(d, ec.data.getStartDate()));
				
				ec.config.getNoVerifactuData(ec.data.getStartDate())
					.ifPresent( toDisable ->  {
						Date prevDay = AonDateUtils.previousDay(ec.data.getStartDate());
						toDisable.setEndDate(prevDay);
						ec.config = ICCDisablerDAO.close(ec.ctx, ec.domainId, ec.config, prevDay, toDisable);
				});
				
				Date nextYearFirstDay = AonDateUtils.getYearFirstDay(AonDateUtils.getCurrentYear() + 1);
				boolean hasSii = ec.config.getSiiData(ec.data.getStartDate()).isPresent(); 
				if ( hasSii) {
					if (!AonDateUtils.isSameDay( ec.data.getStartDate(), nextYearFirstDay)) {
						throw new AonCoreException( InvoiceCommunicationError.ICC_6000.getMessage() );
					}
					if ( hasSii ) ec.config.getSiiData(ec.data.getStartDate()).ifPresent( d -> closeData(d, ec.data.getStartDate()));
					
				}
			}
			return ec.config; 
		}
		@Override 
		public InvoiceCommunicationConfiguration visitNO_VERIFACTU() {
			if (isNotEnabled( InvoiceCommunicationType.NO_VERIFACTU ) ) {
				if (ec.config.isNotAEAT(ec.data.getStartDate()) && ec.config.isNotCanarias(ec.data.getStartDate())) {
					throw new AonCoreException( InvoiceCommunicationError.ICC_5001.getMessage() );
				}
				enable(); 
				if (ec.config.isLroe( ec.data.getStartDate() )) throw new AonCoreException( InvoiceCommunicationError.ICC_5012.getMessage() );
				if (ec.config.isTbai( ec.data.getStartDate() )) throw new AonCoreException( InvoiceCommunicationError.ICC_5008.getMessage() );
				
				ec.config.getNoSifData(ec.data.getStartDate()).ifPresent( d -> closeData(d, ec.data.getStartDate()));
				ec.config.getSifData(ec.data.getStartDate()).ifPresent( d -> closeData(d, ec.data.getStartDate()));
				
				Date nextYearFirstDay = AonDateUtils.getYearFirstDay(AonDateUtils.getCurrentYear() + 1);
				boolean hasSii = ec.config.getSiiData(ec.data.getStartDate()).isPresent(); 
				boolean hasVerifactu = ec.config.getVerifactuData(ec.data.getStartDate()).isPresent();
				if ( hasSii || hasVerifactu) {
					if (!AonDateUtils.isSameDay( ec.data.getStartDate(), nextYearFirstDay)) {
						throw new AonCoreException( InvoiceCommunicationError.ICC_6001.getMessage() );
					}
					if ( hasSii ) ec.config.getSiiData(ec.data.getStartDate()).ifPresent( d -> closeData(d, ec.data.getStartDate()));
					if ( hasVerifactu ) ec.config.getVerifactuData(ec.data.getStartDate()).ifPresent( d -> closeData(d, ec.data.getStartDate()));
				}
			}
			return ec.config;
		}
		
		@Override 
		public InvoiceCommunicationConfiguration visitSIF() { 
			if (isNotEnabled( InvoiceCommunicationType.SIF ) ) {
				if (ec.config.isNotNavarra(ec.data.getStartDate()) ) {
					throw new AonCoreException( InvoiceCommunicationError.ICC_5005.getMessage() );
				}
				ec.config.getNoSifData(ec.data.getStartDate()).ifPresent( d -> closeData(d, ec.data.getStartDate()));
				enable();
			}
			return ec.config;
		}
		
		@Override 
		public InvoiceCommunicationConfiguration visitLROE() {
			if (isNotEnabled( InvoiceCommunicationType.LROE ) ) {
				if (ec.config.isNotBizkaia(ec.data.getStartDate()) ) {
					throw new AonCoreException( InvoiceCommunicationError.ICC_5006.getMessage() );
				}
				ec.config.getNoSifData(ec.data.getStartDate()).ifPresent( d -> closeData(d, ec.data.getStartDate()));
				ec.config.getSifData(ec.data.getStartDate()).ifPresent( d -> closeData(d, ec.data.getStartDate()));
				enable(); 
			}
			return ec.config;
		}

		@Override 
		public InvoiceCommunicationConfiguration visitTBAI() {
			if (isNotEnabled( InvoiceCommunicationType.TBAI ) ) {
				if (ec.config.isNotAraba(ec.data.getStartDate()) && ec.config.isNotGipuzkoa(ec.data.getStartDate())) {
					throw new AonCoreException( InvoiceCommunicationError.ICC_5003.getMessage() );
				}
				ec.config.getNoSifData(ec.data.getStartDate()).ifPresent( d -> closeData(d, ec.data.getStartDate()));
				ec.config.getSifData(ec.data.getStartDate()).ifPresent( d -> closeData(d, ec.data.getStartDate()));
				enable();
			}
			return ec.config;
		}
		
		
		@Override 
		public InvoiceCommunicationConfiguration visitSII() {
			if (isNotEnabled( InvoiceCommunicationType.SII ) ) {
				if (ec.config.isBizkaia(ec.data.getStartDate()) || ec.config.isUnknown( ec.data.getStartDate() )) {
					throw new AonCoreException( InvoiceCommunicationError.ICC_5004.getMessage() );
				}
				enable();
				
				ec.config.getNoSifData(ec.data.getStartDate()).ifPresent( d -> closeData(d, ec.data.getStartDate()));
				ec.config.getNoVerifactuData(ec.data.getStartDate())
					.ifPresent( toDisable ->  {
						Date prevDay = AonDateUtils.previousDay(ec.data.getStartDate());
						toDisable.setEndDate(prevDay);
						ec.config = ICCDisablerDAO.close(ec.ctx, ec.domainId, ec.config, prevDay, toDisable);
				});

			}
			return ec.config;
		}
		
		@Override public InvoiceCommunicationConfiguration visitSERES() { return ec.config; }
		@Override public InvoiceCommunicationConfiguration visitEMAIL() { return ec.config; }
		@Override public InvoiceCommunicationConfiguration visitCLOSING() { return ec.config; }
		@Override public InvoiceCommunicationConfiguration visitFACTURAE() { return ec.config; }
	}
	
}
