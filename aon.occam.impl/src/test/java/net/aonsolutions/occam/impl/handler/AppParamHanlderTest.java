package net.aonsolutions.occam.impl.handler;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.aonsolutions.occam.api.model.Account;
import net.aonsolutions.occam.api.model.ApplicationParameter;
import net.aonsolutions.occam.api.model.type.AppParam;
import net.aonsolutions.occam.impl.AbstractOccamImplTest;

class AppParamHanlderTest extends AbstractOccamImplTest {
	
	@Test
	void testBetaEnabled() {
		if ( !ctx.getApplicationParameters(DOMAIN_ID).isBetaEnabled()) {
			ApplicationParameter appParam = new ApplicationParameter()
				.setDomain(DOMAIN_ID)
				.setParam(AppParam.AON_BETA_ENABLED)
				.setValue(true);
			AppParamHandler.save(ctx, DOMAIN_ID, appParam);
			boolean isBeta = ctx.getApplicationParameters(DOMAIN_ID).isBetaEnabled() ; 
			assertTrue( isBeta );
		}
		
	}
		
	@Test
	void testAlphaEnabled() {
		if ( !ctx.getApplicationParameters(DOMAIN_ID).isAlphaEnabled()) {
			ApplicationParameter appParam = new ApplicationParameter()
				.setDomain(DOMAIN_ID)
				.setParam(AppParam.AON_ALPHA_ENABLED)
				.setValue(true);
			AppParamHandler.save(ctx, DOMAIN_ID, appParam);
			boolean isBeta = ctx.getApplicationParameters(DOMAIN_ID).isAlphaEnabled() ; 
			assertTrue( isBeta );
		}
		
	}

	@Test
	void testSalesDefaultAccount() {
		Account account = ctx.getApplicationParameters(DOMAIN_ID).getSalesDefaultAccount().orElse(null);
		if ( account == null) {
			account = AccountHandler.get(ctx, DOMAIN_ID, "700000000").orElse(null);
			assertNotNull(account);
			ApplicationParameter appParam = new ApplicationParameter()
				.setDomain(DOMAIN_ID)
				.setParam(AppParam.ACC_DEFAULT_SALES_ACC)
				.setValue(account.getId());
			AppParamHandler.save(ctx, DOMAIN_ID, appParam);
		}
	}
	
	@Test
	void testPurchaseDefaultAccount() {
		Account account = ctx.getApplicationParameters(DOMAIN_ID).getPurchaseDefaultAccount().orElse(null);
		if ( account == null) {
			account = AccountHandler.get(ctx, DOMAIN_ID, "600000000").orElse(null);
			assertNotNull(account);
			ApplicationParameter appParam = new ApplicationParameter()
				.setDomain(DOMAIN_ID)
				.setParam(AppParam.ACC_DEFAULT_PURCHASE_ACC)
				.setValue(account.getId());
			AppParamHandler.save(ctx, DOMAIN_ID, appParam);
		}
	}
	
	
	@Test
	void testOutputVatDefaultAccount() {
		Account account = ctx.getApplicationParameters(DOMAIN_ID).getOutputVatDefaultAccount().orElse(null);
		if ( account == null) {
			account = AccountHandler.get(ctx, DOMAIN_ID, "477000000").orElse(null);
			assertNotNull(account);
			ApplicationParameter appParam = new ApplicationParameter()
				.setDomain(DOMAIN_ID)
				.setParam(AppParam.ACC_DEFAULT_CHARGED_VAT_ACC)
				.setValue(account.getId());
			AppParamHandler.save(ctx, DOMAIN_ID, appParam);
		}
	}
	
	@Test
	void testInputVatDefaultAccount() {
		Account account = ctx.getApplicationParameters(DOMAIN_ID).getInputVatDefaultAccount().orElse(null);
		if ( account == null) {
			account = AccountHandler.get(ctx, DOMAIN_ID, "472000000").orElse(null);
			assertNotNull(account);
			ApplicationParameter appParam = new ApplicationParameter()
				.setDomain(DOMAIN_ID)
				.setParam(AppParam.ACC_DEFAULT_PAID_VAT_ACC)
				.setValue(account.getId());
			AppParamHandler.save(ctx, DOMAIN_ID, appParam);
		}
	}
	
	@Test
	void testPrepaymentDefaultAccount() {
		Account account = ctx.getApplicationParameters(DOMAIN_ID).getPrepaymentDefaultAccount().orElse(null);
		if ( account == null) {
			account = AccountHandler.get(ctx, DOMAIN_ID, "555900000").orElse(null);
			assertNotNull(account);
			ApplicationParameter appParam = new ApplicationParameter()
				.setDomain(DOMAIN_ID)
				.setParam(AppParam.ACC_DEFAULT_PREPAYMENT_ACC)
				.setValue(account.getId());
			AppParamHandler.save(ctx, DOMAIN_ID, appParam);
		}
	}

	@Test
	void testWitholdingCreditorDefaultAccount() {
		Account account = ctx.getApplicationParameters(DOMAIN_ID).getWitholdingCreditorDefaultAccount().orElse(null);
		if ( account == null) {
			account = AccountHandler.get(ctx, DOMAIN_ID, "475100000").orElse(null);
			assertNotNull(account);
			ApplicationParameter appParam = new ApplicationParameter()
				.setDomain(DOMAIN_ID)
				.setParam(AppParam.ACC_DEFAULT_CHARGED_RET_ACC)
				.setValue(account.getId());
			AppParamHandler.save(ctx, DOMAIN_ID, appParam);
		}
	}

}


/*
	public Optional<Integer> getDirectTaxAdjustAccount() {
		return integer(AppParam.ACC_DIRECT_TAX_ADJUST_ACC);
	}
	public Optional<Account> getVatNegativeAdjustAccount() {
		return account(AppParam.ACC_VAT_NEGATIVE_ADJUST_ACC);
	}

 */
