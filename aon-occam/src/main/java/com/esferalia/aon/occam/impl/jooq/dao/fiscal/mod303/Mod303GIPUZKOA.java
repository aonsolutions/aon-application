package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;

abstract class Mod303GIPUZKOA extends Mod303Declaration {

	@Override
	Mod303 initialize(AONContext ctx, Mod303 mod303) {
		mod303.setComplementaryDeclarationAvailable(true);
		mod303.setReplacementDeclarationAvailable(false);
		return super.initializeModel(ctx, mod303);
	}	

	@Override
	ComplementaryBeahaviour getComplementaryBehaviour(Mod303 mod) {
		return ComplementaryBeahaviour.COMPLEMENTARY;
	}

	@Override
	double getResult(Mod303 mod) {
		return mod.getAmount(Mod303Key.GP_C035);
	}
	
	@Override
	void initializeSimplifiedRegime(AONContext ctx, Mod303 mod303) {
	}
	
	@Override
	protected Set<Integer> createVatAccrualKeysFromInvoices(AONContext ctx, Mod303 mod303) {
		final Set<Integer> invoices = new HashSet<>();
		Stream<VatContext> stream = null;
		if (mod303.isDiffCalculationMandatory() ) {
			mod303.setGenerateFromYearStart(true);
			stream =  VATDAO.getAccrualInvoices(ctx,mod303);
		} else if (mustApplyReplacementSearch(mod303)) {
			stream =  VATDAO.getAccrualInvoices(ctx,mod303);
		} else {
			stream = VATDAO.getNotInModelAccrualInvoices(ctx,mod303);
		}
		stream.forEach( vc -> {
			if (vc.isSales()) {
				add(Mod303Key.GP_C047, mod303, vc.getBase());
				add(Mod303Key.GP_C048, mod303, vc.getDeductibleQuota());
			} else {
				add(Mod303Key.GP_C049, mod303, vc.getBase());
				add(Mod303Key.GP_C050, mod303, vc.getDeductibleQuota());
			}
			invoices.add(vc.getInvoice());
		});
		return invoices;
	}
}
