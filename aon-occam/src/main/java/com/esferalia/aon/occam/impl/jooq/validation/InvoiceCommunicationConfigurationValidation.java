package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.watson.error.AonCoreException;

public class InvoiceCommunicationConfigurationValidation {
	
	public static final BiConsumer<InvoiceCommunicationConfiguration,AONContext> NO_SIF_VALIDATION = (icc, ctx) -> {
		if(icc.isNoSif()) {
			if(icc.isTbai()) {
				throw new AonCoreException("No se puede activar TBAI cuando No SIF está activado");
			}
			
			if(icc.isVerifactu()) {
				throw new AonCoreException("No se puede activar Verifactu cuando No SIF está activado");
			}
			
			if(icc.isNoVerifactu()) {
				throw new AonCoreException("No se puede activar No Verifactu cuando No SIF está activado");
			}
			
			if(icc.isSif()) {
				throw new AonCoreException("No se puede activar SIF cuando No SIF está activado");
			}
		}
	};
	
	public static final BiConsumer<InvoiceCommunicationConfiguration,AONContext> TBAI_VALIDATION = (icc, ctx) -> {
		if(icc.isTbai()) {
			if(!icc.isAraba() && !icc.isGipuzkoa()) {
				throw new AonCoreException("TBAI solo puede activarse para las provincias de Araba o Gipuzkoa");
			}
			
			if(icc.isVerifactu()) {
				throw new AonCoreException("No se puede activar TBAI y Verifactu a la vez");
			}
			
			if(icc.isNoVerifactu()) {
				throw new AonCoreException("No se puede activar TBAI y No Verifactu a la vez");
			}
			
			if(icc.isSif()) {
				throw new AonCoreException("No se puede activar TBAI y SIF a la vez");
			}
			
			if(icc.isLroe()) {
				throw new AonCoreException("No se puede activar TBAI y LROE/Ticket Bai a la vez");
			}
		}
	};
	
	public static final BiConsumer<InvoiceCommunicationConfiguration,AONContext> LROE_VALIDATION = (icc, ctx) -> {
		if(icc.isLroe()) {
			if(!icc.isBizkaia()) {
				throw new AonCoreException("LROE/Ticket Bai solo puede activarse para las provincia de Bizkaia");
			}
			
			if(icc.isVerifactu()) {
				throw new AonCoreException("No se puede activar LROE/Ticket Bai y Verifactu a la vez");
			}
			
			if(icc.isNoVerifactu()) {
				throw new AonCoreException("No se puede activar LROE/Ticket Bai y No Verifactu a la vez");
			}
			
			if(icc.isSif()) {
				throw new AonCoreException("No se puede activar LROE/Ticket Bai y SIF a la vez");
			}
			
			if(icc.isSii()) {
				throw new AonCoreException("No se puede activar LROE/Ticket Bai y SII a la vez");
			}
			
			if(icc.isTbai()) {
				throw new AonCoreException("No se puede activar LROE/Ticket Bai y TBAI a la vez");
			}
		}
	};
	
	public static final BiConsumer<InvoiceCommunicationConfiguration,AONContext> VERIFACTU_VALIDATION = (icc, ctx) -> {
		if(icc.isVerifactu()) {
			if(!icc.isAEAT() && !icc.isCanarias()) {
				throw new AonCoreException("Verifactu solo puede activarse para AEAT o Canarias");
			}
			
			if(icc.isNoVerifactu()) {
				throw new AonCoreException("No se puede activar Verifactu y No Verifactu a la vez");
			}
			
			if(icc.isSif()) {
				throw new AonCoreException("No se puede activar Verifactu y SIF a la vez");
			}
			
			if(icc.isSii()) {
				throw new AonCoreException("No se puede activar Verifactu y SII a la vez");
			}
			
			if(icc.isTbai()) {
				throw new AonCoreException("No se puede activar Verifactu y TBAI a la vez");
			}
			
			if(icc.isLroe()) {
				throw new AonCoreException("No se puede activar Verifactu y LROE/Ticket Bai a la vez");
			}			
		}
	};
	
	public static final BiConsumer<InvoiceCommunicationConfiguration,AONContext> NO_VERIFACTU_VALIDATION = (icc, ctx) -> {
		if(icc.isVerifactu()) {
			if(!icc.isAEAT() && !icc.isCanarias()) {
				throw new AonCoreException("Verifactu solo puede activarse para AEAT o Canarias");
			}
			
			if(icc.isVerifactu()) {
				throw new AonCoreException("No se puede activar No Verifactu y Verifactu a la vez");
			}
			
			if(icc.isSif()) {
				throw new AonCoreException("No se puede activar No Verifactu y SIF a la vez");
			}
			
			if(icc.isSii()) {
				throw new AonCoreException("No se puede activar No Verifactu y SII a la vez");
			}
			
			if(icc.isTbai()) {
				throw new AonCoreException("No se puede activar No Verifactu y TBAI a la vez");
			}
			
			if(icc.isLroe()) {
				throw new AonCoreException("No se puede activar No Verifactu y LROE/Ticket Bai a la vez");
			}			
		}
	};
	
	public static final BiConsumer<InvoiceCommunicationConfiguration,AONContext> SII_VALIDATION = (icc, ctx) -> {
		if(icc.isSii()) {
			if(icc.isVerifactu()) {
				throw new AonCoreException("No se puede activar SII y Verifactu a la vez");
			}
			
			if(icc.isNoVerifactu()) {
				throw new AonCoreException("No se puede activar SII y No Verifactu a la vez");
			}
			
			if(icc.isSif()) {
				throw new AonCoreException("No se puede activar SII y SIF a la vez");
			}
			
			if(icc.isLroe()) {
				throw new AonCoreException("No se puede activar SII y LROE a la vez");
			}			
		}
	};
	
	public static final BiConsumer<InvoiceCommunicationConfiguration,AONContext> SIF_VALIDATION = (icc, ctx) -> {
		if(icc.isSif()) {
			if(icc.isVerifactu()) {
				throw new AonCoreException("No se puede activar SIF y Verifactu a la vez");
			}
			
			if(icc.isNoVerifactu()) {
				throw new AonCoreException("No se puede activar SIF y No Verifactu a la vez");
			}
			
			if(icc.isSii()) {
				throw new AonCoreException("No se puede activar SIF y SII a la vez");
			}
			
			if(icc.isLroe()) {
				throw new AonCoreException("No se puede activar SIF y LROE a la vez");
			}
			
			if(icc.isTbai()) {
				throw new AonCoreException("No se puede activar SIF y TBAI a la vez");
			}
			
		}
	};	
	
	public static void validate(AONContext ctx, InvoiceCommunicationConfiguration icc) throws AonCoreException{
		NO_SIF_VALIDATION
		.andThen(TBAI_VALIDATION)
		.andThen(LROE_VALIDATION)
		.andThen(VERIFACTU_VALIDATION)
		.andThen(NO_VERIFACTU_VALIDATION)
		.andThen(SII_VALIDATION)
		.andThen(SIF_VALIDATION)
		.accept(icc, ctx);
	}
}
