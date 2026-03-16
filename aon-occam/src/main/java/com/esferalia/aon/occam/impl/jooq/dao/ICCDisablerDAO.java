package com.esferalia.aon.occam.impl.jooq.dao;

import java.text.MessageFormat;
import java.util.Date;

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

	private static InvoiceCommunicationConfiguration close(AONContext ctx, Integer domainId, CommunicationData toDisable) {
		Date atDate = toDisable.getStartDate();
		EnterpriseDataNames name = toDisable.getDataName();
		Date endDate = toDisable.getEndDate();
		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, domainId);
		CommunicationData ensuredToDisable = config.getData( name, atDate)
			.orElse( toDisable );
		ensuredToDisable = EnterpriseDataDAO.setEndDate( ensuredToDisable, endDate);
		DisablerContext dc = new DisablerContext( ctx, domainId, config, atDate, ensuredToDisable);
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
	
	static InvoiceCommunicationConfiguration disable(AONContext ctx, Integer domainId, CommunicationData toDisable) {
		InvoiceCommunicationConfiguration config = close(ctx, domainId, toDisable );
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

			// Aseguramos la comunicación para el día siguiente.....
			Date nextDay = AonDateUtils.nextDay( toDisable.getEndDate() );
			if (AonCollectionUtils.isEmpty( ec.config.getTypes( nextDay ) )) {
				// Si no hay tipos de comunicación para el día siguiente, se intenta habilitar el periodo anterior.
				if (!hasCommunicatedInvoices(ec.ctx, ec.domainId, toDisable)) {
					tryToEnablePrevious( ec.ctx, ec.domainId, ec.config,  toDisable);
				}
			}
			
			// Si aún así no hay tipos de comunicación para el día siguiente se lanza una excepción.
			if (AonCollectionUtils.isEmpty( ec.config.getTypes( nextDay ) )) {
				throw new AonCoreException( InvoiceCommunicationError.ICC_6002.getMessage() );
			} else {
				// Si hay tipos de comunicación para el día siguiente, se elimina el periodo cerrado, 
				// siempre que no haya facturas comunicadas en dicho periodo.
				if (!hasCommunicatedInvoices(ec.ctx, ec.domainId, toDisable)) {
					toDisable.setDeleted( true );
				}
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
		
		@Override public InvoiceCommunicationConfiguration visitNO_VERIFACTU() 	{return disable(ec.toDisable);}
		@Override public InvoiceCommunicationConfiguration visitVERIFACTU()  	{return disable(ec.toDisable);}
		@Override public InvoiceCommunicationConfiguration visitSII() 			{return disable(ec.toDisable);}
		@Override public InvoiceCommunicationConfiguration visitSIF() 			{return disable(ec.toDisable);}
		@Override public InvoiceCommunicationConfiguration visitLROE() 			{return disable(ec.toDisable);}
		@Override public InvoiceCommunicationConfiguration visitTBAI() 			{return disable(ec.toDisable);}
		public InvoiceCommunicationConfiguration visitNoSif() 					{return disable(ec.toDisable);}
		
		@Override public InvoiceCommunicationConfiguration visitSERES() { return ec.config; }
		@Override public InvoiceCommunicationConfiguration visitEMAIL() { return ec.config; }
		@Override public InvoiceCommunicationConfiguration visitCLOSING() { return ec.config; }
		@Override public InvoiceCommunicationConfiguration visitFACTURAE() { return ec.config; }
	}
	
}
