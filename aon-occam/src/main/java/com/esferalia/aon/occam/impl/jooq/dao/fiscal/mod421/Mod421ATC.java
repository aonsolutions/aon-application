package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod421;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.type.Mod421Key;
import com.esferalia.aon.watson.util.AonMathUtils;

abstract class Mod421ATC extends Mod421Declaration {

	@Override
	Mod421 initialize(AONContext ctx, Mod421 mod421) {
		mod421.setComplementaryDeclarationAvailable(true);
		mod421.setReplacementDeclarationAvailable(false);
		return super.initializeModel(ctx, mod421);
	}	

	@Override
	ComplementaryBeahaviour getComplementaryBehaviour(Mod421 mod) {
		return ComplementaryBeahaviour.REPLACEMENT;
	}
	
	@Override
	double getResult(Mod421 mod) {
		return mod.getAmount(Mod421Key.C19);
	}
	
//	@Override
//	protected Set<Alcatraz> createVatAccrualKeysFromInvoices(AONContext ctx, Mod421 mod421) {
//		final Set<Alcatraz> invoices = new HashSet<>();
//		Stream<VatContext> stream = null;
//		if (getComplementaryBehaviour(mod421) == ComplementaryBeahaviour.REPLACEMENT) {
//			stream =  VATDAO.getCritCajaInvoices(ctx,mod421);
//		} else {
//			stream = VATDAO.getNotInModelCritCajaInvoices(ctx,mod421);
//		}
//		stream.forEach( vc -> {
//			if (vc.isSales()) {
//				add(Mod421Key.CT_C62, mod421, vc.getBase());
//				add(Mod421Key.CT_C63, mod421, vc.getDeductibleQuota());
//			} else {
//				add(Mod421Key.CT_C74, mod421, vc.getBase());
//				add(Mod421Key.CT_C75, mod421, vc.getDeductibleQuota());
//			}
//			invoices.add(
//				new Alcatraz()
//					.setInvoice(vc.getInvoice())
//					.setFinance(vc.getFinance())
//					.setFinanceTracking(vc.getFinanceTracking()));
//		});
//		add(Mod421Key.CT_A08, mod421, AonMathUtils.isZero(mod421.getAmount(Mod421Key.CT_C75)) ? (0.0) : (1.0));
//		return invoices;
//	}

	protected static double getPendingCompesateAmounts(AONContext ctx, Mod421 mod) {
		return Mod421DAO.getLastPeriodEffectiveModels(ctx, mod)
			.mapToDouble(fm ->
				AonMathUtils.round((fm.isToCompensate() ? AonMathUtils.absRounded(fm.getDeclarationResult()) : 0.0))
			)
			.findFirst()
			.orElse(0.0);
	} 

}
