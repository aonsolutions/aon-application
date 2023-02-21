package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO.Alcatraz;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;
import com.esferalia.aon.watson.util.AonMathUtils;

abstract class Mod303AEAT extends Mod303Declaration {

	@Override
	Mod303 initialize(AONContext ctx, Mod303 mod303) {
		mod303.setComplementaryDeclarationAvailable(true);
		mod303.setReplacementDeclarationAvailable(false);
		return super.initializeModel(ctx, mod303);
	}	

	@Override
	ComplementaryBeahaviour getComplementaryBehaviour(Mod303 mod) {
		return ComplementaryBeahaviour.REPLACEMENT;
	}
	
	@Override
	double getResult(Mod303 mod) {
		return mod.getAmount(Mod303Key.CT_C71);
	}
	
	@Override
	protected Set<Alcatraz> createVatAccrualKeysFromInvoices(AONContext ctx, Mod303 mod303) {
		final Set<Alcatraz> invoices = new HashSet<>();
		Stream<VatContext> stream = null;
		if (getComplementaryBehaviour(mod303) == ComplementaryBeahaviour.REPLACEMENT) {
			stream =  VATDAO.getCritCajaInvoices(ctx,mod303);
		} else {
			stream = VATDAO.getNotInModelCritCajaInvoices(ctx,mod303);
		}
		stream.forEach( vc -> {
			if (vc.isSales()) {
				add(Mod303Key.CT_C62, mod303, vc.getBase());
				add(Mod303Key.CT_C63, mod303, vc.getDeductibleQuota());
			} else {
				add(Mod303Key.CT_C74, mod303, vc.getBase());
				add(Mod303Key.CT_C75, mod303, vc.getDeductibleQuota());
			}
			invoices.add(
				new Alcatraz()
					.setInvoice(vc.getInvoice())
					.setFinance(vc.getFinance())
					.setFinanceTracking(vc.getFinanceTracking()));
		});
		add(Mod303Key.CT_A08, mod303, AonMathUtils.isZero(mod303.getAmount(Mod303Key.CT_C75)) ? (0.0) : (1.0));
		return invoices;
	}

	protected static double getPendingCompesateAmounts(AONContext ctx, Mod303 mod) {
		return Mod303DAO.getLastPeriodEffectiveModels(ctx, mod)
			.mapToDouble(fm ->
				AonMathUtils.round(
					(fm.isToCompensate()? AonMathUtils.absRounded(fm.getDeclarationResult()):0.0) 
					+ fm.getAmount(Mod303Key.CT_C87))
			)
			.findFirst()
			.orElse(0.0);
	} 

}
