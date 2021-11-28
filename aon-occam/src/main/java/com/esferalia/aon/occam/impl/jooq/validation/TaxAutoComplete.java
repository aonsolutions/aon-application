package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.error.AonCoreException;

public class TaxAutoComplete {
	
	
	private TaxAutoComplete() {
		throw new IllegalStateException("Utility class");
	}
	
	public static final BiConsumer<AONContext, Tax> COMPLETE_VAT_DEDUCTION_TYPE = (ctx, tax) -> {
		if (tax.getVatDeductionType() == null) {
			tax.setVatDeductionType(VatDeductionType.WITH_RIGHT);
		}
	};
	
	public static final BiConsumer<AONContext, Tax> COMPLETE_WITHHOLDING_DEDUCTION_TYPE = (ctx, tax) -> {
		if (tax.getWithholdingType() == null) {
			tax.setWithholdingType(WithholdingType.PROFESSIONAL);
		}
	};
	
	public static void autoComplete(AONContext ctx, Tax tax) throws AonCoreException {
		COMPLETE_VAT_DEDUCTION_TYPE
		.andThen(COMPLETE_WITHHOLDING_DEDUCTION_TYPE)
		.accept(ctx, tax);
	}


}
