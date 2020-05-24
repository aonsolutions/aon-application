package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class AccountingInvoiceValidation {

	private static class AonConfigurationContext {
		
		private AONContext ctx;
		private AonConfiguration config;
		
		public AonConfigurationContext (AONContext ctx,AonConfiguration config) {
			this.ctx = ctx;
			this.config = config;
		}
		private AONContext getContext() {
			return ctx;
		}
		private AonConfiguration getConfiguration() {
			return config;
		}
	}
	
	/**
	 * Si la factura es un DUA debe haber una factura vinculada.
	 */
	private static BiConsumer<AccountingInvoice,AonConfigurationContext> DUA_EMPTY_NATIONAL_INVOICE = (ai,ctx) -> {
		if (ai.getInvoice() == null
		 || ai.getInvoice().getId() == null) {
			throw new AonCoreException(AonError.INVOICE_DUA_NATIONAL_INVOICE_EMPTY.getMessage());
		}
	};
	
	/**
	 * Si la factura es un DUA debe haber una factura vinculada.
	 */
	private static BiConsumer<AccountingInvoice,AonConfigurationContext> DUA_EMPTY_IMPORT_INVOICE = (ai,ctx) -> {
		if (ai.getDuaInvoice() == null
		 || ai.getDuaInvoice().getAccountingInvoice() == null 
		 || ai.getDuaInvoice().getAccountingInvoice().getInvoice() == null 
		 || ai.getDuaInvoice().getAccountingInvoice().getInvoice().getId() == null) {
			throw new AonCoreException(AonError.INVOICE_DUA_IMPORT_INVOICE_EMPTY.getMessage());
		}
	};
	/**
	 * Si la factura es un DUA debe haber una información del DUA.
	 */
	private static BiConsumer<AccountingInvoice,AonConfigurationContext> DUA_EMPTY_INFO = (ai,ctx) -> {
		if (ai.getDuaInvoice() == null || ai.getDuaInvoice().getInfo() == null) {
			throw new AonCoreException(AonError.INVOICE_DUA_INFO_EMPTY.getMessage());
		}
	};
	/**
	 * Si la factura es un DUA debe haber una información del DUA. Cuenta contable de aranceles.
	 */
	private static BiConsumer<AccountingInvoice,AonConfigurationContext> DUA_EMPTY_DUTY_ACCOUNT = (ai,ctx) -> {
		if (ai.getDuaInvoice().getInfo().getDutyAccount() == null || ai.getDuaInvoice().getInfo().getDutyAccount().getId() == null) {
			throw new AonCoreException(AonError.INVOICE_DUA_INFO_EMPTY.getMessage());
		}
	};
	/**
	 * Si la factura es un DUA debe haber una información del DUA. Cuenta contable de IVA..
	 */
	private static BiConsumer<AccountingInvoice,AonConfigurationContext> DUA_EMPTY_VAT_ACCOUNT = (ai,ctx) -> {
		if (ai.getDuaInvoice().getInfo().getDutyAccount() == null || ai.getDuaInvoice().getInfo().getDutyAccount().getId() == null) {
			throw new AonCoreException(AonError.INVOICE_DUA_VAT_ACCOUNT_EMPTY.getMessage());
		}
	};
	

	public static void validateDUAInvoice(AONContext ctx, AonConfiguration config, AccountingInvoice ai)
			throws AonCoreException {
		DUA_EMPTY_NATIONAL_INVOICE
		.andThen(DUA_EMPTY_IMPORT_INVOICE)
		.andThen(DUA_EMPTY_INFO)
		.andThen(DUA_EMPTY_DUTY_ACCOUNT)
		.andThen(DUA_EMPTY_VAT_ACCOUNT)
		.accept(ai, new AonConfigurationContext(ctx, config));
	}

}
