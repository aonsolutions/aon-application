package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;
import com.esferalia.aon.watson.util.AonMathUtils;

abstract class Mod303ARABA extends Mod303Declaration {

	@Override
	Mod303 initialize(AONContext ctx, Mod303 mod303) {
		mod303.setComplementaryDeclarationAvailable(true);
		mod303.setReplacementDeclarationAvailable(true);
		return super.initializeModel(ctx, mod303);
	}	

	@Override
	ComplementaryBeahaviour getComplementaryBehaviour(Mod303 mod) {
		return  mod.isComplementary()
			?ComplementaryBeahaviour.COMPLEMENTARY
			:ComplementaryBeahaviour.REPLACEMENT;
	}

	@Override
	double getResult(Mod303 mod) {
		return mod.getAmount(Mod303Key.AR_C080);
	}
	
	@Override
	void initializeSimplifiedRegime(AONContext ctx, Mod303 mod303) {
	}
	
	@Override
	protected Set<Integer> createVatAccrualKeysFromInvoices(AONContext ctx, Mod303 mod303) {
		final Set<Integer> invoices = new HashSet<>();
		Stream<VatContext> stream = null;
		if (getComplementaryBehaviour(mod303) == ComplementaryBeahaviour.REPLACEMENT) {
			stream =  VATDAO.getAccrualInvoices(ctx,mod303);
		} else {
			stream = VATDAO.getNotInModelAccrualInvoices(ctx,mod303);
		}
		stream.forEach( vc -> {
			if (vc.isSales()) {
				add(Mod303Key.AR_C180, mod303, vc.getBase());
				add(Mod303Key.AR_C181, mod303, vc.getDeductibleQuota());
			} else {
				add(Mod303Key.AR_C182, mod303, vc.getBase());
				add(Mod303Key.AR_C183, mod303, vc.getDeductibleQuota());
			}
			invoices.add(vc.getInvoice());
		});
		add(Mod303Key.AR_C911, mod303, AonMathUtils.isZero(mod303.getAmount(Mod303Key.CT_C75)) ? (0.0) : (1.0));
		return invoices;
	}
	
}
