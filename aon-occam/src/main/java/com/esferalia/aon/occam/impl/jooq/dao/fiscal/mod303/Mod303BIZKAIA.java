package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.occam.impl.jooq.dao.mod390HF.Mod390HFDAO;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;
import com.esferalia.aon.watson.util.AonMathUtils;

abstract class Mod303BIZKAIA extends Mod303Declaration {

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
		return mod.getAmount(Mod303Key.BZ_C043);
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
					add(Mod303Key.BZ_C200, mod303, vc.getBase());
					add(Mod303Key.BZ_C201, mod303, vc.getDeductibleQuota());
				} else {
					add(Mod303Key.BZ_C202, mod303, vc.getBase());
					add(Mod303Key.BZ_C203, mod303, vc.getDeductibleQuota());
				}
				invoices.add(vc.getInvoice());
			});
		add(Mod303Key.BZ_C187, mod303, AonMathUtils.isZero(mod303.getAmount(Mod303Key.CT_C75)) ? (0.0) : (1.0));
		return invoices;
	}
	
	protected static double getPendingCompesateAmounts(AONContext ctx, Mod303 mod) {
		if ( mod.isFirstPeriod() ) {
			return Mod390HFDAO.getMod390HFs( ctx, mod.getDomain() )
				.filter(m390 -> m390.getYear() ==  (mod.getYear() - 1) )
				.filter(m390 -> m390.getAdministration() ==  mod.getAdministration() )
				.filter(Mod390HF::isToCompensate)
				.mapToDouble(fm -> AonMathUtils.round(fm.getAmount(Mod390Key.BZ_C110) * (-1)))
				.findFirst()
				.orElse(0.0);
		}
		
		return Mod303DAO.getLastPeriodEffectiveModels(ctx, mod)
			.filter(Mod303::isToCompensate)
			.mapToDouble(fm -> AonMathUtils.round(fm.getAmount(Mod303Key.BZ_C043) * (-1)))
			.findFirst()
			.orElse(0.0);
	}	


}
