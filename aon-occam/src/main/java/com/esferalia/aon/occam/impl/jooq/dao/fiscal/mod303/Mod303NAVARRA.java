package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO.Alcatraz;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;
import com.esferalia.aon.watson.util.AonMathUtils;

abstract class Mod303NAVARRA extends Mod303Declaration {

	@Override
	Mod303 initialize(AONContext ctx, Mod303 mod303) {
		mod303.setComplementaryDeclarationAvailable(false);
		mod303.setReplacementDeclarationAvailable(true);
		return super.initializeModel(ctx, mod303);
	}	

	@Override
	ComplementaryBeahaviour getComplementaryBehaviour(Mod303 mod) {
		return ComplementaryBeahaviour.REPLACEMENT;
	}

	@Override
	double getResult(Mod303 mod) {
		return mod.getAmount(Mod303Key.NF_063);
	}
	
	@Override
	void initializeSimplifiedRegime(AONContext ctx, Mod303 mod303) {
	}
	
	@Override
	protected Set<Alcatraz> createVatAccrualKeysFromInvoices(AONContext ctx, Mod303 mod303) {
		final Set<Alcatraz> invoices = new HashSet<>();
		Stream<VatContext> stream = null;
		if (mod303.isDiffCalculationMandatory() ) {
			mod303.setGenerateFromYearStart(true);
			stream =  VATDAO.getAccrualInvoices(ctx,mod303);
		} else if (mustApplyReplacementSearch(mod303)) {
			stream =  VATDAO.getAccrualInvoices(ctx,mod303);
		} else {
			stream = VATDAO.getNotInModelAccrualInvoices(ctx,mod303);
		}
		stream
			.filter(vc -> vc.isSales())
			.forEach( vc -> {
				add(Mod303Key.NF_194, mod303, vc.getBase());
				invoices.add(
					new Alcatraz()
						.setInvoice(vc.getInvoice())
						.setFinance(vc.getFinance())
						.setFinanceTracking(vc.getFinanceTracking()));

		});
		return invoices;
	}
	
	protected static double getPendingCompesateAmounts(AONContext ctx, Mod303 mod) {
		int y = mod.getYear();
		Period pe = null;  
		if ( mod.isFirstPeriod() ) {
			y = y - 1;
			pe = (mod.isMonthPeriod())?Period.M12:Period.T4;
		} else {
			pe = Period.values()[ mod.getPeriod().ordinal() - 1 ]; 
		}
		final Period period = pe;
		final int year = y; 
		return Mod303DAO.getMod303s(ctx, mod.getDomain(),  p -> 
				p.getYearProperty().eq(year)
				.and(p.getPeriodProperty().eq(period.value()))
				.and(p.getAdministrationProperty().eq(mod.getAdministration().value())))
			.filter(Mod303::isToCompensate)
			.mapToDouble(fm -> AonMathUtils.round(fm.getDeclarationResult() * (-1)))
			.findFirst()
			.orElse(0.0);
	}	

	@Override
	public Mod303Key getRegularizationKey() {
		return Mod303Key.NF_450;
	}
}
