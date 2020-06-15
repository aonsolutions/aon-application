package com.esferalia.aon.occam.impl.jooq.validation;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;
import static com.esferalia.aon.jooq.tables.Amortization.AMORTIZATION;
import static com.esferalia.aon.jooq.tables.BankConcept.BANK_CONCEPT;
import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Tax.TAX;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceTaxAccount.INVOICE_TAX_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceDua.INVOICE_DUA;
import static com.esferalia.aon.jooq.tables.Loan.LOAN;
import static com.esferalia.aon.jooq.tables.PmTypeDetail.PM_TYPE_DETAIL;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;


import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

import org.jooq.Field;
import org.jooq.TableLike;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountValidation {

	/**
	 * El dominio de la cuenta no puede estar vacio.
	 */
	public static BiConsumer<Account,AONContext> EMPTY_DOMAIN = (account,ctx) -> {
		if (account.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * El código de cuenta contable es un dato obligatorio.
	 */
	public static BiConsumer<Account,AONContext> EMPTY_CODE = (account,ctx) -> {
		if (AonStringUtils.isBlank(account.getCode()))
			throw new AonCoreException(AonError.ACCOUNT_EMPTY_CODE.getMessage());
	};
	
	/**
	 * La descripcion de cuenta contable es un dato obligatorio.
	 */
	public static BiConsumer<Account,AONContext> EMPTY_DESCRIPTION = (account,ctx) -> {
		if (AonStringUtils.isBlank(account.getDescription()))
			throw new AonCoreException(AonError.ACCOUNT_EMPTY_DESCRIPTION.getMessage());
	};

	/**
	 * La longitd de la cuenta debe ser 1,2,3,4, ó 9
	 */
	public static BiConsumer<Account,AONContext> VALID_LENGTH = (account,ctx) -> {
		if (AonStringUtils.length(account.getCode()) != 1
		 && AonStringUtils.length(account.getCode()) != 2
		 && AonStringUtils.length(account.getCode()) != 3
		 && AonStringUtils.length(account.getCode()) != 4
		 && AonStringUtils.length(account.getCode()) != 9)
			throw new AonCoreException(AonError.ACCOUNT_INVALID_LENGTH.format(account.getCode()));
		
	};
	/**
	 * La cuenta contable debe ser numerica
	 */
	public static BiConsumer<Account,AONContext> NUMERIC_CODE = (account,ctx) -> {
		if (!AonStringUtils.isNumeric(account.getCode()))
			throw new AonCoreException(AonError.ACCOUNT_NO_NUMERIC.getMessage());
	};
	
	/**
	 * Los niveles inferiores de la cuenta deben existir.
	 */
	public static BiConsumer<Account,AONContext> LOW_LEVEL_EXISTS = (account,ctx) -> {
		int level = (byte) ((account.getCode().length() > 4)? 5: account.getCode().length());
		if (level>1) {
			int parentLevel = level - 1;
			String parentCode = AonStringUtils.substring(account.getCode(),0, parentLevel);
			Account a = AccountDAO.get(ctx, ACCOUNT.CODE.equal(parentCode));
			if (a == null)
				throw new AonCoreException(AonError.ACCOUNT_LOW_LEVEL_NOT_PRESENT.getMessage());
		}
	};
	
	/**
	 * La cuenta contable no puede estar duplicada.
	 */
	public static BiConsumer<Account,AONContext> DUPLICATED_CODE = (account,ctx) -> {
			Account duplicated = AccountDAO.get(ctx, 
					ACCOUNT.CODE.equal(account.getCode())
					.and(account.getId() == null? DSL.trueCondition() : ACCOUNT.ID.ne(account.getId()) )
					);
			if (duplicated != null) {
				throw new AonCoreException(AonError.ACCOUNT_DUPLICATED_CODE.format(account.getFullName(),duplicated.getFullName()));
			}
	};
	
	public static void validate(AONContext ctx, Account account)
			throws AonCoreException {
		EMPTY_DOMAIN
			.andThen(EMPTY_CODE)
			.andThen(EMPTY_DESCRIPTION)
			.andThen(VALID_LENGTH)
			.andThen(NUMERIC_CODE)
			.andThen(LOW_LEVEL_EXISTS)
			.andThen(DUPLICATED_CODE)
			.accept(account, ctx);

	}

	public static List<String> check(AONContext ctx, Account account) throws AonCoreException {
		LinkedList<String> messages = new LinkedList<String>(); 
		try {EMPTY_DOMAIN.accept(account, ctx);} catch (AonCoreException e) {messages.add( e.getMessage());}
		try {EMPTY_CODE.accept(account, ctx);} catch (AonCoreException e) {messages.add( e.getMessage());}
		try {EMPTY_DESCRIPTION.accept(account, ctx);} catch (AonCoreException e) {messages.add( e.getMessage());}
		try {VALID_LENGTH.accept(account, ctx);} catch (AonCoreException e) {messages.add( e.getMessage());}
		try {NUMERIC_CODE.accept(account, ctx);} catch (AonCoreException e) {messages.add( e.getMessage());}
		try {LOW_LEVEL_EXISTS.accept(account, ctx);} catch (AonCoreException e) {messages.add( e.getMessage());}
		try {DUPLICATED_CODE.accept(account, ctx);} catch (AonCoreException e) {messages.add( e.getMessage());}
		return messages;
		

	}

	/**
	 * No se puede borrar una cuenta de diferente dominio.
	 */
	public static BiConsumer<Account,AONContext> FROM_PARENT_DOMAIN_CHECK = (account,ctx) -> {
		if (account.getDomain() != ctx.getDomainId()) 
			throw new AonCoreException(AonError.ACCOUNT_PARENT_ACCOUNT.getMessage());
	};
	/**
	 * No se puede borrar una cuenta de diferente dominio.
	 */
	public static BiConsumer<Account,AONContext> HIGH_LEVEL_EXISTS = (account,ctx) -> {
		int level = (byte) ((account.getCode().length() > 4)? 5: account.getCode().length());
		if (level< 5) {
			Account a = AccountDAO.get(ctx, 
					ACCOUNT.CODE.like(account.getCode() + "%")
					.and(ACCOUNT.ID.ne(account.getId()))
					);
			if (a != null)
				throw new AonCoreException(AonError.ACCOUNT_HIGH_LEVEL_PRESENT.getMessage());
		}
	};

	public static BiConsumer<Account,AONContext> CHECK_IF_PRESENT_ACCOUNT_ENTRY_DETAIL_ACCOUNT = (account,ctx) -> {
		if ( exists(ctx,account,ACCOUNT_ENTRY_DETAIL,ACCOUNT_ENTRY_DETAIL.ACCOUNT) )
			throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_ACCOUNT_ENTRY_DETAIL_ACCOUNT.getMessage());
	};

	public static BiConsumer<Account,AONContext> CHECK_IF_PRESENT_ACCOUNT_ENTRY_DETAIL_BAL_ACCOUNT = (account,ctx) -> {
		if ( exists(ctx,account,ACCOUNT_ENTRY_DETAIL,ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT) )
			throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_ACCOUNT_ENTRY_DETAIL_BAL_ACCOUNT.getMessage());
	};
	// ----
	public static BiConsumer<Account,AONContext> CHECK_IF_PRESENT_AMORTIZATION_ACC_ACCOUNT = (account,ctx) -> {
		if ( exists(ctx,account,AMORTIZATION,AMORTIZATION.ACCUMULATED_ACCOUNT) )
			throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_AMORTIZATION_ACCOUNT.getMessage());
	};
	public static BiConsumer<Account,AONContext> CHECK_IF_PRESENT_AMORTIZATION_ALL_ACCOUNT = (account,ctx) -> {
		if ( exists(ctx,account,AMORTIZATION,AMORTIZATION.ALLOCATION_ACCOUNT) )
			throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_AMORTIZATION_ACCOUNT.getMessage());
	};
	public static BiConsumer<Account,AONContext> CHECK_IF_PRESENT_AMORTIZATION_FIX_ACCOUNT = (account,ctx) -> {
		if ( exists(ctx,account,AMORTIZATION,AMORTIZATION.FIXED_ASSET_ACCOUNT) )
			throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_AMORTIZATION_ACCOUNT.getMessage());
	};
	public static BiConsumer<Account,AONContext> CHECK_IF_PRESENT_BANK_CONCEPT_ACCOUNT = (account,ctx) -> {
		if ( exists(ctx,account,BANK_CONCEPT,BANK_CONCEPT.ACCOUNT) )
			throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_BANK_CONCEPT_ACCOUNT.getMessage());
	};
	public static BiConsumer<Account,AONContext> CHECK_IF_PRESENT_CREDITOR_ACCOUNT = (account,ctx) -> {
		if ( exists(ctx,account,CREDITOR,CREDITOR.ACCOUNT) )
			throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_CREDITOR_ACCOUNT.getMessage());
	};
	public static BiConsumer<Account,AONContext> CHECK_IF_PRESENT_CUSTOMER_ACCOUNT = (account,ctx) -> {
		if ( exists(ctx,account,CUSTOMER,CUSTOMER.ACCOUNT) )
			throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_CUSTOMER_ACCOUNT.getMessage());
	};
	public static BiConsumer<Account,AONContext> CHECK_IF_PRESENT_SUPPLIER_ACCOUNT = (account,ctx) -> {
		if ( exists(ctx,account,SUPPLIER,SUPPLIER.ACCOUNT) )
			throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_SUPPLIER_ACCOUNT.getMessage());
	};
	public static BiConsumer<Account,AONContext> CHECK_IF_PRESENT_PRODUCT_PUR_ACCOUNT = (account,ctx) -> {
		if ( exists(ctx,account,PRODUCT,PRODUCT.PURCHASE_ACCOUNT) )
			throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_PRODUCT_ACCOUNT.getMessage());
	};
	public static BiConsumer<Account,AONContext> CHECK_IF_PRESENT_PRODUCT_SAL_ACCOUNT = (account,ctx) -> {
		if ( exists(ctx,account,PRODUCT,PRODUCT.SALES_ACCOUNT) )
			throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_PRODUCT_ACCOUNT.getMessage());
	};
	public static BiConsumer<Account,AONContext> CHECK_IF_PRESENT_TAX_PUR_ACCOUNT = (account,ctx) -> {
		if ( exists(ctx,account,TAX,TAX.PURCHASE_ACCOUNT) )
			throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_TAX_ACCOUNT.getMessage());
	};
	public static BiConsumer<Account,AONContext> CHECK_IF_PRESENT_TAX_SAL_ACCOUNT = (account,ctx) -> {
		if ( exists(ctx,account,TAX,TAX.SALES_ACCOUNT) )
			throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_TAX_ACCOUNT.getMessage());
	};
	public static BiConsumer<Account,AONContext> CHECK_IF_PRESENT_INV_DET_ACCOUNT = (account,ctx) -> {
		if ( exists(ctx,account,INVOICE_DETAIL_ACCOUNT,INVOICE_DETAIL_ACCOUNT.ACCOUNT) )
			throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_INVOICE_DETAIL_ACCOUNT.getMessage());
	};
	public static BiConsumer<Account,AONContext> CHECK_IF_PRESENT_INV_DUA_DUTY_ACCOUNT = (account,ctx) -> {
		if ( exists(ctx,account,INVOICE_DUA,INVOICE_DUA.DUTY_ACCOUNT) )
			throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_INVOICE_DUA_ACCOUNT.getMessage());
	};
	public static BiConsumer<Account,AONContext> CHECK_IF_PRESENT_INV_DUA_VAT_ACCOUNT = (account,ctx) -> {
		if ( exists(ctx,account,INVOICE_DUA,INVOICE_DUA.VAT_ACCOUNT) )
			throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_INVOICE_DUA_ACCOUNT.getMessage());
	};
	public static BiConsumer<Account,AONContext> CHECK_IF_PRESENT_INV_TAX_ACCOUNT = (account,ctx) -> {
		if ( exists(ctx,account,INVOICE_TAX_ACCOUNT,INVOICE_TAX_ACCOUNT.ACCOUNT) )
			throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_INVOICE_TAX_ACCOUNT.getMessage());
	};
	public static BiConsumer<Account,AONContext> CHECK_IF_PRESENT_LOAN_ACCOUNT = (account,ctx) -> {
		if ( exists(ctx,account,LOAN,LOAN.ACCOUNT) )
			throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_LOAN_ACCOUNT.getMessage());
	};
	public static BiConsumer<Account,AONContext> CHECK_IF_PRESENT_PM_TYPE_ACCOUNT = (account,ctx) -> {
		if ( exists(ctx,account,PM_TYPE_DETAIL,PM_TYPE_DETAIL.ACCOUNT) )
			throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_PM_TYPE_ACCOUNT.getMessage());
	};
	public static BiConsumer<Account,AONContext> CHECK_IF_PRESENT_RBANK_ACCOUNT = (account,ctx) -> {
		if ( exists(ctx,account,RBANK,RBANK.ACCOUNT) )
			throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_RBANK_ACCOUNT.getMessage());
	};

	/*
	if ( exists(ctx,account,rbank                  , account             ))
	*/
	public static boolean exists(AONContext ctx,Account account,TableLike<?> table, Field<Integer> column ) {
		return ctx.getDslContext().select( column )
				.from(table)
				.where(column.eq(account.getId()))
				.stream()
				.map( rec -> rec.getValue(column))
				.findFirst()
				.orElse(null) != null;
		
	}

	public static void validateDeletion(AONContext ctx, Account account) {
		FROM_PARENT_DOMAIN_CHECK
			.andThen(HIGH_LEVEL_EXISTS)
			.andThen(CHECK_IF_PRESENT_ACCOUNT_ENTRY_DETAIL_ACCOUNT)
			.andThen(CHECK_IF_PRESENT_ACCOUNT_ENTRY_DETAIL_BAL_ACCOUNT)
			.andThen(CHECK_IF_PRESENT_CREDITOR_ACCOUNT)
			.andThen(CHECK_IF_PRESENT_CUSTOMER_ACCOUNT)
			.andThen(CHECK_IF_PRESENT_SUPPLIER_ACCOUNT)
			.andThen(CHECK_IF_PRESENT_PRODUCT_PUR_ACCOUNT)
			.andThen(CHECK_IF_PRESENT_PRODUCT_SAL_ACCOUNT)
			.andThen(CHECK_IF_PRESENT_AMORTIZATION_ACC_ACCOUNT)
			.andThen(CHECK_IF_PRESENT_AMORTIZATION_ALL_ACCOUNT)
			.andThen(CHECK_IF_PRESENT_AMORTIZATION_FIX_ACCOUNT)
			.andThen(CHECK_IF_PRESENT_TAX_PUR_ACCOUNT)
			.andThen(CHECK_IF_PRESENT_TAX_SAL_ACCOUNT)
			.andThen(CHECK_IF_PRESENT_INV_DET_ACCOUNT)
			.andThen(CHECK_IF_PRESENT_INV_TAX_ACCOUNT)
			.andThen(CHECK_IF_PRESENT_PM_TYPE_ACCOUNT)
			.andThen(CHECK_IF_PRESENT_BANK_CONCEPT_ACCOUNT)
			.andThen(CHECK_IF_PRESENT_RBANK_ACCOUNT)
			.andThen(CHECK_IF_PRESENT_LOAN_ACCOUNT)
			.andThen(CHECK_IF_PRESENT_INV_DUA_DUTY_ACCOUNT)
			.andThen(CHECK_IF_PRESENT_INV_DUA_VAT_ACCOUNT)
			.accept(account, ctx);
	}
}
