package com.esferalia.aon.occam.impl.jooq.dao;

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

import java.text.MessageFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.Optional;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.EnterpriseDataNames;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType.InvoiceCommunicationTypeAccepter;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.impl.jooq.validation.InvoiceCommunicationConfigurationValidation;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonObjectUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;

class ICCEnablerDAO {

	private static final EnterpriseDataNames[] SUPPORTED_TYPES = new EnterpriseDataNames[] {
		ICC_NO_SIF, ICC_SIF, ICC_SII , ICC_TBAI, ICC_LROE, ICC_VERIFACTU, ICC_NO_VERIFACTU};
	private static final EnumMap<Administration, EnterpriseDataNames[]> ADMON_COMPATIBLE_TYPES = new EnumMap<>(Administration.class);
	static {
		ADMON_COMPATIBLE_TYPES.put(NAVARRA, new EnterpriseDataNames[] {ICC_NO_SIF, ICC_SIF, ICC_SII} );
		ADMON_COMPATIBLE_TYPES.put(ALAVA, new EnterpriseDataNames[] {ICC_NO_SIF, ICC_TBAI, ICC_SII} );
		ADMON_COMPATIBLE_TYPES.put(GIPUZKOA, new EnterpriseDataNames[] {ICC_NO_SIF, ICC_TBAI, ICC_SII} );
		ADMON_COMPATIBLE_TYPES.put(BIZKAIA, new EnterpriseDataNames[] {ICC_NO_SIF, ICC_LROE} );
		ADMON_COMPATIBLE_TYPES.put(CANARIAS, new EnterpriseDataNames[] {ICC_NO_SIF, ICC_VERIFACTU, ICC_NO_VERIFACTU, ICC_SII} );
		ADMON_COMPATIBLE_TYPES.put(COMMON_TERRITORY, new EnterpriseDataNames[] {ICC_NO_SIF, ICC_VERIFACTU, ICC_NO_VERIFACTU, ICC_SII} );
	}
	private static final EnumMap<EnterpriseDataNames, EnterpriseDataNames[]> COMMUNICATION_COMPATIBLE_TYPES = new EnumMap<>(EnterpriseDataNames.class);
	static {
		COMMUNICATION_COMPATIBLE_TYPES.put(ICC_SII, new EnterpriseDataNames[] {ICC_NO_SIF, ICC_SIF, ICC_TBAI} );
		COMMUNICATION_COMPATIBLE_TYPES.put(ICC_NO_SIF, new EnterpriseDataNames[] {ICC_SII, ICC_LROE} );
		COMMUNICATION_COMPATIBLE_TYPES.put(ICC_TBAI, new EnterpriseDataNames[] {ICC_SII} );
	}
	
	private static class EnablerContext {
		AONContext ctx;
		Integer domainId;
		InvoiceCommunicationConfiguration config;
		CommunicationData data;
		
		EnablerContext(AONContext ctx, Integer domainId, InvoiceCommunicationConfiguration config, CommunicationData data) {
			this.ctx = ctx;
			this.domainId = domainId;
			this.config = config;
			this.data = data;
		}
	}
	
	static InvoiceCommunicationConfiguration enable(AONContext ctx, Integer domainId, CommunicationData data) {
		InvoiceCommunicationConfigurationValidation.validate(data);
		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, domainId);
		EnablerContext ec = new EnablerContext(ctx, domainId, config, data);
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
			ec.config.addData( ec.data);
		}
		
		private boolean updatable() {
			if ( isNotEnabled( ec.data.getDataName() )) return true;
			CommunicationData toEnable = ec.data;
			return ec.config.getData( ec.data.getDataName(), ec.data.getStartDate())
				.map( enabled -> {
					// Si no tienen la misma fecha de inicio.
					if ( AonDateUtils.isSameDay( enabled.getStartDate() , toEnable.getStartDate()) ) return true;
					// Si no tienen la misma fecha de fin.
					if ( AonDateUtils.isSameDay( enabled.getEndDate() , toEnable.getEndDate()) ) return true;
					// Si no tienen la misma administración.
					if ( AonObjectUtils.presentAndEquals( enabled.getAdministration() , toEnable.getAdministration()) ) return true;
					// Si no tienen el mismo tipo de exención.
					if ( AonObjectUtils.optionalEquals( enabled.getExemptType() , toEnable.getExemptType()) ) return true;
					// Si no tienen el mismo valor de test
					if ( enabled.isTest() == toEnable.isTest() ) return true;
					
					// Son iguales, no es necesario actualizar.
					return false;
				})
				.orElse(true);	// No debería entrar aquí porque el método isNotEnabled ya lo ha comprobado.
		}
		
		private void closeIncompatibleTypes() {
			closeOtherAdmonTypes();
			closeAdmonIncompatibleTypes();
			closeCommunicationIncompatibleTypes();
		}
		
		private void closeOtherAdmonTypes() {
			Date prevDay = AonDateUtils.previousDay(ec.data.getStartDate());
			ec.config.dataStream()
				.filter( d -> AonObjectUtils.optionalNotEquals(d.getAdministration(), ec.data.getAdministration() )  )
				.map( toDisable -> toDisable.setEndDate(prevDay) )
				.forEach( toDisable -> ec.config = ICCDisablerDAO.close(ec.ctx, ec.domainId, ec.config, prevDay, toDisable) );
		}
		
		private void closeAdmonIncompatibleTypes() {
			Date prevDay = AonDateUtils.previousDay(ec.data.getStartDate());
			ec.data.getAdministration()
				.ifPresent( a -> 
					AonCollectionUtils.stream(SUPPORTED_TYPES)
				    	.filter(t -> AonCollectionUtils.notIn(ADMON_COMPATIBLE_TYPES.get(a), t))
				    	.map(t -> ec.config.getData(t, ec.data.getStartDate()))
				    	.flatMap(Optional::stream)
				    	.map( toDisable -> 
				    		toDisable.setEndDate(prevDay)
				    	)
				    	.forEach( toDisable -> ec.config = ICCDisablerDAO.close(ec.ctx, ec.domainId, ec.config, prevDay, toDisable))
			);
		}
		
		private void closeCommunicationIncompatibleTypes() {
			Date prevDay = AonDateUtils.previousDay(ec.data.getStartDate());
			AonCollectionUtils.stream(SUPPORTED_TYPES)
		    	.filter( st -> AonCollectionUtils.notIn(COMMUNICATION_COMPATIBLE_TYPES.get(ec.data.getDataName()), st))
		    	.map(st -> ec.config.getData(st, ec.data.getStartDate()))
		    	.flatMap(Optional::stream)
		    	.filter( toDisable -> ec.data != toDisable)	// No cerrar el mismo que se está habilitando.)
		    	.map( toDisable -> toDisable.setEndDate(prevDay))
		    	.forEach( toDisable -> ec.config = ICCDisablerDAO.close(ec.ctx, ec.domainId, ec.config, prevDay, toDisable))
	    	;
		}

		private void validateIncompatibleTypes() {
			AonCollectionUtils.stream(SUPPORTED_TYPES)
		    	.filter( st -> AonCollectionUtils.notIn(COMMUNICATION_COMPATIBLE_TYPES.get(ec.data.getDataName()), st))
		    	.map(st -> ec.config.getData(st, ec.data.getStartDate()))
		    	.flatMap(Optional::stream)
		    	.map( ed -> ed.getDataName())
		    	.findAny()
		    	.ifPresent( type ->  {
		    		throw new AonCoreException( MessageFormat.format( InvoiceCommunicationError.ICC_5100.getMessage(), type.getLabel() ) );
		    	});
		}
		
		public InvoiceCommunicationConfiguration visitNoSif() {
			if (updatable()) {
				enable();
				closeIncompatibleTypes();
			}
			return ec.config;
		}
		
		private Date getNextYearFirstDay() {
			return AonDateUtils.getYearFirstDay(AonDateUtils.getCurrentYear() + 1);
		}
		
		@Override 
		public InvoiceCommunicationConfiguration visitVERIFACTU() {
			if (updatable()) {
				boolean hasSii = ec.config.isSii(ec.data.getStartDate());
				if ( hasSii
					&& AonDateUtils.isNotSameDay( ec.data.getStartDate(), getNextYearFirstDay())) {
					throw new AonCoreException( InvoiceCommunicationError.ICC_6000.getMessage() );
				}
				enable();
				closeIncompatibleTypes();
			}
			return ec.config; 
		}

		@Override 
		public InvoiceCommunicationConfiguration visitNO_VERIFACTU() {
			if (updatable()) {
				boolean hasSii = ec.config.isSii(ec.data.getStartDate()); 
				boolean hasVerifactu = ec.config.isVerifactu(ec.data.getStartDate());
				if ( ( hasSii || hasVerifactu)
					&& AonDateUtils.isNotSameDay( ec.data.getStartDate(), getNextYearFirstDay())) {
					throw new AonCoreException( InvoiceCommunicationError.ICC_6001.getMessage() );
				}
				enable(); 
				closeIncompatibleTypes();
			}
			return ec.config;
		}
		
		@Override 
		public InvoiceCommunicationConfiguration visitSIF() { 
			if (updatable()) {
				enable(); 
				closeIncompatibleTypes();
			}
			return ec.config;
		}
		
		@Override 
		public InvoiceCommunicationConfiguration visitLROE() {
			if (updatable()) {
				enable(); 
				closeIncompatibleTypes();
			}
			return ec.config;
		}

		@Override 
		public InvoiceCommunicationConfiguration visitTBAI() {
			if (updatable()) {
				enable(); 
				closeIncompatibleTypes();
			}
			return ec.config;
		}
		
		
		@Override 
		public InvoiceCommunicationConfiguration visitSII() {
			if (isNotEnabled( InvoiceCommunicationType.SII ) ) {
				validateIncompatibleTypes();
				enable(); 
				closeIncompatibleTypes();
			}
			return ec.config;
		}

//		private void print( String msg) {
//			System.out.println( "---- " + msg + " ----" );
//			ec.config.dataStream()
//			.forEach( ed -> {
//				String startDate = MessageFormat.format("{0,date,dd/MM/yyyy}", ed.getStartDate());
//				String endDate = ed.getEndDate() == null
//					? "--/--/----"
//					:MessageFormat.format("{0,date,dd/MM/yyyy}", ed.getEndDate());
//				System.out.println( (ed.isDirty() ? "(*)" : "   ") 
//					+ (ed.isDeleted() ? " (D) " : "     ") 
//					+ " - "
//					+ AonStringUtils.rightPad( "(" + (ed.getId() == null ? "" : AonNumberUtils.toString(ed.getId())) + ")", 10)
//					+ AonStringUtils.rightPad(startDate, 15)
//					+ AonStringUtils.rightPad(endDate, 15)
//					+ AonStringUtils.rightPad( AonStringUtils.defaultIfBlank(ed.getName()), 25)
//					+ AonStringUtils.rightPad( AonStringUtils.defaultIfBlank(ed.getExpression()) , 20)
//				);
//			}
//			);
//			System.out.println( "-----------------------------" );
//			System.out.println();
//		}

		
		@Override public InvoiceCommunicationConfiguration visitSERES() { return ec.config; }
		@Override public InvoiceCommunicationConfiguration visitEMAIL() { return ec.config; }
		@Override public InvoiceCommunicationConfiguration visitCLOSING() { return ec.config; }
		@Override public InvoiceCommunicationConfiguration visitFACTURAE() { return ec.config; }
	}
	
}
