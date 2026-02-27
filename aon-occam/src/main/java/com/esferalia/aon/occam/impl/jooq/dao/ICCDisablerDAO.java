package com.esferalia.aon.occam.impl.jooq.dao;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.EnterpriseDataNames;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType.InvoiceCommunicationTypeAccepter;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;

class ICCDisablerDAO {
	
	private static record DisablerContext (
		  AONContext ctx
		, Integer domainId
		, InvoiceCommunicationConfiguration config
		, Date atDate
		, CommunicationData toDisable) { }
	;
	
	static InvoiceCommunicationConfiguration close(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config, Date atDate, CommunicationData toDisable) {
		DisablerContext dc = new DisablerContext( ctx, domainId, config, atDate, toDisable);
		return close(dc);
	}

	static InvoiceCommunicationConfiguration close(AONContext ctx, Integer domainId, Date atDate, EnterpriseDataNames name, Date endDate) {
		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, domainId);
		CommunicationData toDisable = config.getData( name, atDate)
			.map( d -> EnterpriseDataDAO.setEndDate( d, endDate)) 
			.orElseThrow( () -> new AonCoreException( 
				MessageFormat.format( InvoiceCommunicationError.ICC_6004.getMessage()
					, InvoiceCommunicationType.get(name).map(t -> t.getAbbr()).orElse( "?" )
					, atDate)))
		;
		DisablerContext dc = new DisablerContext( ctx, domainId, config, atDate, toDisable);
		return close(dc);
	}
	
	private static InvoiceCommunicationConfiguration close(DisablerContext dc){
		if (dc.atDate == null
		 || dc.toDisable == null 
		 || dc.toDisable.getDataName() == null 
		 || dc.toDisable.getEndDate() == null) { 
			throw new AonCoreException( InvoiceCommunicationError.ICC_5000.getMessage() );
		}
		CommunicationDisabler ce = new CommunicationDisabler( dc );
		if ( dc.toDisable.isNoSif() ) {
			return ce.visitNoSif();
		} 
		return dc.toDisable.getCommunicationType()
			.map( t -> t.accept( ce ) )
			.orElse( dc.config );
		
	}
	
	static InvoiceCommunicationConfiguration disable(AONContext ctx, Integer domainId, Date atDate, EnterpriseDataNames name, Date endDate) {
		InvoiceCommunicationConfiguration config = close(ctx, domainId, atDate, name, endDate );
		return InvoiceCommunicationDAO.save(ctx, domainId, config);
	}
	
	private static class CommunicationDisabler implements InvoiceCommunicationTypeAccepter<InvoiceCommunicationConfiguration> {
		
		private final DisablerContext ec;
		
		CommunicationDisabler(DisablerContext context) {
			this.ec = context;
		}
		
		private void tryToEnablePrevious(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config, CommunicationData toDisable) {
			Date prev = AonDateUtils.previousDay(toDisable.getStartDate());
			config.dataStream()
				.filter( d -> AonDateUtils.isSameDay( prev, d.getEndDate()) )
				.forEach( prevCommunicationData -> {
					prevCommunicationData.setEndDate( null );
					toDisable.setDeleted( true );
				})
			;
		}
		
		private InvoiceCommunicationConfiguration disable(CommunicationData toDisable) {
			// Nos aseguramos de que no haya facturas comunicadas con fecha posterior a la fecha de cierre
			toDisable.getCommunicationType()
				.ifPresent( t -> checkCommunicatedInvoices(ec.ctx, ec.domainId, t, toDisable.getEndDate() ));

			// Aseguramos la comunicación para el día siguiente.
			Date nextDay = AonDateUtils.nextDay( toDisable.getEndDate() );
			List<InvoiceCommunicationType> newTypes = ec.config.getTypes( nextDay );
			if (AonCollectionUtils.isEmpty( newTypes )) {
				// Si no hay tipos de comunicación para el día siguiente, intentamos habilitar el periodo anterior.
				if (!hasCommunicatedInvoices(ec.ctx, ec.domainId, toDisable)) {
					tryToEnablePrevious( ec.ctx, ec.domainId, ec.config,  toDisable);
				}
			}
			
			// Si no hay tipos de comunicación para el día siguiente Lanzamos una excepción.
			List<InvoiceCommunicationType> newTypes2 = ec.config.getTypes( nextDay );
			if (AonCollectionUtils.isEmpty( newTypes2 )) {
				ec.config.getAdministration()
					.ifPresent( a -> {
						if (a.isAEAT() || a.isCanarias()) {
							throw new AonCoreException( InvoiceCommunicationError.ICC_6002.getMessage() );
						} else if (a.isAraba() || a.isGipuzkoa() ) {
							throw new AonCoreException( InvoiceCommunicationError.ICC_6006.getMessage() );
						} else if (a.isBizkaia()) {
							throw new AonCoreException( InvoiceCommunicationError.ICC_6005.getMessage() );
						}
					});
				throw new AonCoreException( InvoiceCommunicationError.ICC_6002.getMessage() );
			}
			return ec.config;
		}
		
		private boolean hasCommunicatedInvoices(AONContext ctx, Integer domainId, CommunicationData d) {
			return d.getCommunicationType()
				.map( DataResponseSource::safeValueOf )
				.filter( s -> s != null )
				.map( s -> DataResponseDAO.has(ctx, domainId, s, d.getStartDate(), d.getEndDate()) )
				.orElse( false );
		}
		
		private void checkCommunicatedInvoices(AONContext ctx, Integer domainId, InvoiceCommunicationType t, Date endDate) {
			DataResponseSource.optOf(t)
				.filter( s -> s != null )
				.filter( s -> DataResponseDAO.hasNotBeyond(ctx, domainId, s, endDate) )
				.orElseThrow( () -> new AonCoreException( 
					MessageFormat.format( InvoiceCommunicationError.ICC_6003.getMessage()
							, t.getAbbr()
							, endDate)));
		}
		
		@Override 
		public InvoiceCommunicationConfiguration visitNO_VERIFACTU() {
			return disable(ec.toDisable);
		}
		
		@Override 
		public InvoiceCommunicationConfiguration visitVERIFACTU() {
			return disable(ec.toDisable); 
		}
		@Override 
		public InvoiceCommunicationConfiguration visitSII() {
			return disable(ec.toDisable);
		}

		public InvoiceCommunicationConfiguration visitNoSif() {return ec.config;}
		@Override public InvoiceCommunicationConfiguration visitSIF() {return ec.config;}
		@Override public InvoiceCommunicationConfiguration visitLROE() {return ec.config;}
		@Override public InvoiceCommunicationConfiguration visitTBAI() {return ec.config;}
		
		@Override public InvoiceCommunicationConfiguration visitSERES() { return ec.config; }
		@Override public InvoiceCommunicationConfiguration visitEMAIL() { return ec.config; }
		@Override public InvoiceCommunicationConfiguration visitCLOSING() { return ec.config; }
		@Override public InvoiceCommunicationConfiguration visitFACTURAE() { return ec.config; }
	}
	
	/*
	
	static InvoiceCommunicationConfiguration enable(AONContext ctx, Integer domainId, Administration admon, CommunicationData data) {
		if (admon == null
		 || data == null 
		 || data.getStartDate() == null
		 || data.getName() == null 
		 || data.getDataName() == null) throw new AonCoreException( InvoiceCommunicationError.ICC_5000.getMessage() );
		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, domainId);
		
		enableAdministration( config, admon, data.getStartDate());
		
		CommunicationEnabler ce = new CommunicationEnabler( new EnablerContext(config, admon, data) );
		if ( data.isNoSif() ) {
			ce.visitNoSif();
		} else {
			data.getCommunicationType().ifPresent( t -> t.accept( ce ) );
		}
		return InvoiceCommunicationDAO.save(ctx, domainId, config);
	}
	
	static void enableAdministration(InvoiceCommunicationConfiguration config, Administration admon, Date date) {
		config.getAdministrationData(date)
		.ifPresentOrElse(
			currentAdmonData -> {
				currentAdmonData.getAdministration()
					.ifPresentOrElse( 
						a -> {
							if ( a != admon) {
								EnterpriseDataDAO.closeData( currentAdmonData, date);
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
	
	private static class CommunicationEnabler implements InvoiceCommunicationTypeAccepter<Void> {
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
		
		public Void visitNoSif() {
			if (isNotEnabled( ICC_NO_SIF ) ) {
				AonCollectionUtils.stream(EnterpriseDataNames.getInvoiceCommunicationTypesNames())
					.filter( n -> n != ICC_LROE )
					.filter( n -> n != ICC_SII )
					.forEach( n -> 
						ec.config.getData(n, ec.data.getStartDate())
						.ifPresent( d -> EnterpriseDataDAO.closeData(d, ec.data.getStartDate())) 
					);
				enable();
			}
			return null;
		}
		
		@Override 
		public Void visitVERIFACTU() {
			if (isNotEnabled( InvoiceCommunicationType.VERIFACTU ) ) {
				if (ec.config.isNotAEAT(ec.data.getStartDate()) && ec.config.isNotCanarias(ec.data.getStartDate())) {
					throw new AonCoreException( InvoiceCommunicationError.ICC_5001.getMessage() );
				}
				if (ec.config.isLroe( ec.data.getStartDate() )) throw new AonCoreException( InvoiceCommunicationError.ICC_5011.getMessage() );
				if (ec.config.isTbai( ec.data.getStartDate() )) throw new AonCoreException( InvoiceCommunicationError.ICC_5007.getMessage() );
				
				ec.config.getNoSifData(ec.data.getStartDate()).ifPresent( d -> EnterpriseDataDAO.closeData(d, ec.data.getStartDate()));
				ec.config.getSifData(ec.data.getStartDate()).ifPresent( d -> EnterpriseDataDAO.closeData(d, ec.data.getStartDate()));
				ec.config.getNoVerifactuData(ec.data.getStartDate()).ifPresent( d -> EnterpriseDataDAO.closeData(d, ec.data.getStartDate()));
				
				Date nextYearFirstDay = AonDateUtils.getYearFirstDay(AonDateUtils.getCurrentYear() + 1);
				boolean hasSii = ec.config.getSiiData(ec.data.getStartDate()).isPresent(); 
				if ( hasSii) {
					if (!AonDateUtils.isSameDay( ec.data.getStartDate(), nextYearFirstDay)) {
						throw new AonCoreException( InvoiceCommunicationError.ICC_6000.getMessage() );
					}
					if ( hasSii ) ec.config.getSiiData(ec.data.getStartDate()).ifPresent( d -> EnterpriseDataDAO.closeData(d, ec.data.getStartDate()));
					
				}
				enable();
			}
			return null; 
		}
		@Override 
		public Void visitNO_VERIFACTU() {
			if (isNotEnabled( InvoiceCommunicationType.NO_VERIFACTU ) ) {
				if (ec.config.isNotAEAT(ec.data.getStartDate()) && ec.config.isNotCanarias(ec.data.getStartDate())) {
					throw new AonCoreException( InvoiceCommunicationError.ICC_5001.getMessage() );
				}
				if (ec.config.isLroe( ec.data.getStartDate() )) throw new AonCoreException( InvoiceCommunicationError.ICC_5012.getMessage() );
				if (ec.config.isTbai( ec.data.getStartDate() )) throw new AonCoreException( InvoiceCommunicationError.ICC_5008.getMessage() );
				
				ec.config.getNoSifData(ec.data.getStartDate()).ifPresent( d -> EnterpriseDataDAO.closeData(d, ec.data.getStartDate()));
				ec.config.getSifData(ec.data.getStartDate()).ifPresent( d -> EnterpriseDataDAO.closeData(d, ec.data.getStartDate()));
				
				Date nextYearFirstDay = AonDateUtils.getYearFirstDay(AonDateUtils.getCurrentYear() + 1);
				boolean hasSii = ec.config.getSiiData(ec.data.getStartDate()).isPresent(); 
				boolean hasVerifactu = ec.config.getVerifactuData(ec.data.getStartDate()).isPresent();
				if ( hasSii || hasVerifactu) {
					if (!AonDateUtils.isSameDay( ec.data.getStartDate(), nextYearFirstDay)) {
						throw new AonCoreException( InvoiceCommunicationError.ICC_6001.getMessage() );
					}
					if ( hasSii ) ec.config.getSiiData(ec.data.getStartDate()).ifPresent( d -> EnterpriseDataDAO.closeData(d, ec.data.getStartDate()));
					if ( hasVerifactu ) ec.config.getVerifactuData(ec.data.getStartDate()).ifPresent( d -> EnterpriseDataDAO.closeData(d, ec.data.getStartDate()));
				}
				
				enable(); 
			}
			return null; 
		}
		
		@Override 
		public Void visitSIF() { 
			if (isNotEnabled( InvoiceCommunicationType.SIF ) ) {
				if (ec.config.isNotNavarra(ec.data.getStartDate()) ) {
					throw new AonCoreException( InvoiceCommunicationError.ICC_5005.getMessage() );
				}
				ec.config.getNoSifData(ec.data.getStartDate()).ifPresent( d -> EnterpriseDataDAO.closeData(d, ec.data.getStartDate()));
				enable();
			}
			return null; 
		}
		
		@Override 
		public Void visitLROE() {
			if (isNotEnabled( InvoiceCommunicationType.LROE ) ) {
				if (ec.config.isNotBizkaia(ec.data.getStartDate()) ) {
					throw new AonCoreException( InvoiceCommunicationError.ICC_5006.getMessage() );
				}
				ec.config.getNoSifData(ec.data.getStartDate()).ifPresent( d -> EnterpriseDataDAO.closeData(d, ec.data.getStartDate()));
				ec.config.getSifData(ec.data.getStartDate()).ifPresent( d -> EnterpriseDataDAO.closeData(d, ec.data.getStartDate()));
				enable(); 
			}
			return null; 
		}

		@Override 
		public Void visitTBAI() {
			if (isNotEnabled( InvoiceCommunicationType.TBAI ) ) {
				if (ec.config.isNotAraba(ec.data.getStartDate()) && ec.config.isNotGipuzkoa(ec.data.getStartDate())) {
					throw new AonCoreException( InvoiceCommunicationError.ICC_5003.getMessage() );
				}
				ec.config.getNoSifData(ec.data.getStartDate()).ifPresent( d -> EnterpriseDataDAO.closeData(d, ec.data.getStartDate()));
				ec.config.getSifData(ec.data.getStartDate()).ifPresent( d -> EnterpriseDataDAO.closeData(d, ec.data.getStartDate()));
				enable();
			}
			return null; 
		}
		
		
		@Override 
		public Void visitSII() {
			if (isNotEnabled( InvoiceCommunicationType.SII ) ) {
				if (ec.config.isBizkaia(ec.data.getStartDate()) || ec.config.isUnknown( ec.data.getStartDate() )) {
					throw new AonCoreException( InvoiceCommunicationError.ICC_5004.getMessage() );
				}
				ec.config.getNoSifData(ec.data.getStartDate()).ifPresent( d -> EnterpriseDataDAO.closeData(d, ec.data.getStartDate()));
				enable(); 
			}
			return null; 
		}
		
		@Override public Void visitSERES() { return null; }
		@Override public Void visitEMAIL() { return null; }
		@Override public Void visitCLOSING() { return null; }
		@Override public Void visitFACTURAE() { return null; }
	}
	*/	
}
