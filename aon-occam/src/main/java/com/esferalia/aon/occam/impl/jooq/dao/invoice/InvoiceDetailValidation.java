package com.esferalia.aon.occam.impl.jooq.dao.invoice;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class InvoiceDetailValidation {
	
	private InvoiceDetailValidation() {
		
	}

	/**
	 * El origen de la linea de factura es un dato obligatorio.
	 */
	private static final BiConsumer<AONContext,InvoiceDetail> EMPTY_SOURCE = (ctx,det) -> {
		if (det.getSource() == null ) {
			throw new AonCoreException(AonError.INVOICE_EMPTY_SOURCE.getMessage());
		}
	};

	/**
	 * El centro de trabajo es un dato obligatorio.
	 */
	private static final BiConsumer<AONContext,InvoiceDetail> EMPTY_WORKPLACE = (ctx,det) -> {
		if(det.getWorkplace() == null || det.getWorkplace().getId() == null) {
			throw new AonCoreException(AonError.INVOICE_EMPTY_WORKPLACE.getMessage());
		}
	};

	static void validate(AONContext ctx, InvoiceDetail detail) {
		EMPTY_SOURCE
			.andThen(EMPTY_WORKPLACE)
			.accept(ctx,detail);
	}


}
