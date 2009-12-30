package com.code.aon.finance.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.finance.Bank;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.PayMethod;
import com.code.aon.finance.RegistryBank;
import com.code.aon.finance.RegistryPayMethod;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.InvoicingGroup;
import com.code.aon.finance.InvoicingGroupDetail;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IFinanceAlias {



	/** 
	* DAOConstantsEntry for Bank entity.
	*/ 
	DAOConstantsEntry BANK_ENTRY = DAOConstants.getDAOConstant(Bank.class);

	/** 
	* Alias value: Bank_code
	* Hibernate value: Bank.code
	*/
	String  BANK_CODE = BANK_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Bank_id
	* Hibernate value: Bank.id
	*/
	String  BANK_ID = BANK_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Bank_name
	* Hibernate value: Bank.name
	*/
	String  BANK_NAME = BANK_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for Finance entity.
	*/ 
	DAOConstantsEntry FINANCE_ENTRY = DAOConstants.getDAOConstant(Finance.class);

	/** 
	* Alias value: Finance_amount
	* Hibernate value: Finance.amount
	*/
	String  FINANCE_AMOUNT = FINANCE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Finance_bankAccount
	* Hibernate value: Finance.bankAccount
	*/
	String  FINANCE_BANK_ACCOUNT = FINANCE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Finance_bank_id
	* Hibernate value: Finance.bank.id
	*/
	String  FINANCE_BANK_ID = FINANCE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Finance_concept
	* Hibernate value: Finance.concept
	*/
	String  FINANCE_CONCEPT = FINANCE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Finance_dueDate
	* Hibernate value: Finance.dueDate
	*/
	String  FINANCE_DUE_DATE = FINANCE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Finance_expenses
	* Hibernate value: Finance.expenses
	*/
	String  FINANCE_EXPENSES = FINANCE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Finance_financeStatus
	* Hibernate value: Finance.financeStatus
	*/
	String  FINANCE_FINANCE_STATUS = FINANCE_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Finance_id
	* Hibernate value: Finance.id
	*/
	String  FINANCE_ID = FINANCE_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Finance_invoice_id
	* Hibernate value: Finance.invoice.id
	*/
	String  FINANCE_INVOICE_ID = FINANCE_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Finance_payMethod_id
	* Hibernate value: Finance.payMethod.id
	*/
	String  FINANCE_PAY_METHOD_ID = FINANCE_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Finance_payMethod_type
	* Hibernate value: Finance.payMethod.type
	*/
	String  FINANCE_PAY_METHOD_TYPE = FINANCE_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Finance_payment
	* Hibernate value: Finance.payment
	*/
	String  FINANCE_PAYMENT = FINANCE_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Finance_registry_id
	* Hibernate value: Finance.registry.id
	*/
	String  FINANCE_REGISTRY_ID = FINANCE_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Finance_securityLevel
	* Hibernate value: Finance.securityLevel
	*/
	String  FINANCE_SECURITY_LEVEL = FINANCE_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Finance_invoice_series
	* Hibernate value: Finance.invoice.series
	*/
	String  FINANCE_INVOICE_SERIES = FINANCE_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Finance_invoice_number
	* Hibernate value: Finance.invoice.number
	*/
	String  FINANCE_INVOICE_NUMBER = FINANCE_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Finance_invoice_issueDate
	* Hibernate value: Finance.invoice.issueDate
	*/
	String  FINANCE_INVOICE_ISSUE_DATE = FINANCE_ENTRY.getAliasNames()[16];



	/** 
	* DAOConstantsEntry for FinanceBatch entity.
	*/ 
	DAOConstantsEntry FINANCE_BATCH_ENTRY = DAOConstants.getDAOConstant(FinanceBatch.class);

	/** 
	* Alias value: FinanceBatch_description
	* Hibernate value: FinanceBatch.description
	*/
	String  FINANCE_BATCH_DESCRIPTION = FINANCE_BATCH_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: FinanceBatch_financeBatchStatus
	* Hibernate value: FinanceBatch.financeBatchStatus
	*/
	String  FINANCE_BATCH_FINANCE_BATCH_STATUS = FINANCE_BATCH_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: FinanceBatch_financeBatchType
	* Hibernate value: FinanceBatch.financeBatchType
	*/
	String  FINANCE_BATCH_FINANCE_BATCH_TYPE = FINANCE_BATCH_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: FinanceBatch_id
	* Hibernate value: FinanceBatch.id
	*/
	String  FINANCE_BATCH_ID = FINANCE_BATCH_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: FinanceBatch_issueDate
	* Hibernate value: FinanceBatch.issueDate
	*/
	String  FINANCE_BATCH_ISSUE_DATE = FINANCE_BATCH_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: FinanceBatch_payment
	* Hibernate value: FinanceBatch.payment
	*/
	String  FINANCE_BATCH_PAYMENT = FINANCE_BATCH_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: FinanceBatch_registryBank_id
	* Hibernate value: FinanceBatch.registryBank.id
	*/
	String  FINANCE_BATCH_REGISTRY_BANK_ID = FINANCE_BATCH_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for FinanceBatchDetail entity.
	*/ 
	DAOConstantsEntry FINANCE_BATCH_DETAIL_ENTRY = DAOConstants.getDAOConstant(FinanceBatchDetail.class);

	/** 
	* Alias value: FinanceBatchDetail_amount
	* Hibernate value: FinanceBatchDetail.amount
	*/
	String  FINANCE_BATCH_DETAIL_AMOUNT = FINANCE_BATCH_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: FinanceBatchDetail_financeBatch_id
	* Hibernate value: FinanceBatchDetail.financeBatch.id
	*/
	String  FINANCE_BATCH_DETAIL_FINANCE_BATCH_ID = FINANCE_BATCH_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: FinanceBatchDetail_finance_id
	* Hibernate value: FinanceBatchDetail.finance.id
	*/
	String  FINANCE_BATCH_DETAIL_FINANCE_ID = FINANCE_BATCH_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: FinanceBatchDetail_id
	* Hibernate value: FinanceBatchDetail.id
	*/
	String  FINANCE_BATCH_DETAIL_ID = FINANCE_BATCH_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: FinanceBatchDetail_status
	* Hibernate value: FinanceBatchDetail.status
	*/
	String  FINANCE_BATCH_DETAIL_STATUS = FINANCE_BATCH_DETAIL_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for Invoice entity.
	*/ 
	DAOConstantsEntry INVOICE_ENTRY = DAOConstants.getDAOConstant(Invoice.class);

	/** 
	* Alias value: Invoice_id
	* Hibernate value: Invoice.id
	*/
	String  INVOICE_ID = INVOICE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Invoice_series
	* Hibernate value: Invoice.series
	*/
	String  INVOICE_SERIES = INVOICE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Invoice_number
	* Hibernate value: Invoice.number
	*/
	String  INVOICE_NUMBER = INVOICE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Invoice_registry_id
	* Hibernate value: Invoice.registry.id
	*/
	String  INVOICE_REGISTRY_ID = INVOICE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Invoice_registryAddress_id
	* Hibernate value: Invoice.registryAddress.id
	*/
	String  INVOICE_REGISTRY_ADDRESS_ID = INVOICE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Invoice_issueDate
	* Hibernate value: Invoice.issueDate
	*/
	String  INVOICE_ISSUE_DATE = INVOICE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Invoice_securityLevel
	* Hibernate value: Invoice.securityLevel
	*/
	String  INVOICE_SECURITY_LEVEL = INVOICE_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Invoice_status
	* Hibernate value: Invoice.status
	*/
	String  INVOICE_STATUS = INVOICE_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Invoice_registryName
	* Hibernate value: Invoice.registryName
	*/
	String  INVOICE_REGISTRY_NAME = INVOICE_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Invoice_registryDocument
	* Hibernate value: Invoice.registryDocument
	*/
	String  INVOICE_REGISTRY_DOCUMENT = INVOICE_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Invoice_type
	* Hibernate value: Invoice.type
	*/
	String  INVOICE_TYPE = INVOICE_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Invoice_taxFree
	* Hibernate value: Invoice.taxFree
	*/
	String  INVOICE_TAX_FREE = INVOICE_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Invoice_surcharge
	* Hibernate value: Invoice.surcharge
	*/
	String  INVOICE_SURCHARGE = INVOICE_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Invoice_withholding
	* Hibernate value: Invoice.withholding
	*/
	String  INVOICE_WITHHOLDING = INVOICE_ENTRY.getAliasNames()[13];



	/** 
	* DAOConstantsEntry for InvoiceDetail entity.
	*/ 
	DAOConstantsEntry INVOICE_DETAIL_ENTRY = DAOConstants.getDAOConstant(InvoiceDetail.class);

	/** 
	* Alias value: InvoiceDetail_deliveryDetail
	* Hibernate value: InvoiceDetail.deliveryDetail
	*/
	String  INVOICE_DETAIL_DELIVERY_DETAIL = INVOICE_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: InvoiceDetail_description
	* Hibernate value: InvoiceDetail.description
	*/
	String  INVOICE_DETAIL_DESCRIPTION = INVOICE_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: InvoiceDetail_discountExpression
	* Hibernate value: InvoiceDetail.discountExpression
	*/
	String  INVOICE_DETAIL_DISCOUNT_EXPRESSION = INVOICE_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: InvoiceDetail_id
	* Hibernate value: InvoiceDetail.id
	*/
	String  INVOICE_DETAIL_ID = INVOICE_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: InvoiceDetail_invoice_id
	* Hibernate value: InvoiceDetail.invoice.id
	*/
	String  INVOICE_DETAIL_INVOICE_ID = INVOICE_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: InvoiceDetail_item_id
	* Hibernate value: InvoiceDetail.item.id
	*/
	String  INVOICE_DETAIL_ITEM_ID = INVOICE_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: InvoiceDetail_line
	* Hibernate value: InvoiceDetail.line
	*/
	String  INVOICE_DETAIL_LINE = INVOICE_DETAIL_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: InvoiceDetail_price
	* Hibernate value: InvoiceDetail.price
	*/
	String  INVOICE_DETAIL_PRICE = INVOICE_DETAIL_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: InvoiceDetail_quantity
	* Hibernate value: InvoiceDetail.quantity
	*/
	String  INVOICE_DETAIL_QUANTITY = INVOICE_DETAIL_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: InvoiceDetail_source
	* Hibernate value: InvoiceDetail.source
	*/
	String  INVOICE_DETAIL_SOURCE = INVOICE_DETAIL_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: InvoiceDetail_item_product_type
	* Hibernate value: InvoiceDetail.item.product.type
	*/
	String  INVOICE_DETAIL_ITEM_PRODUCT_TYPE = INVOICE_DETAIL_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: InvoiceDetail_taxableBase
	* Hibernate value: InvoiceDetail.taxableBase
	*/
	String  INVOICE_DETAIL_TAXABLE_BASE = INVOICE_DETAIL_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: InvoiceDetail_taxes
	* Hibernate value: InvoiceDetail.taxes
	*/
	String  INVOICE_DETAIL_TAXES = INVOICE_DETAIL_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: InvoiceDetail_workPlace_id
	* Hibernate value: InvoiceDetail.workPlace.id
	*/
	String  INVOICE_DETAIL_WORK_PLACE_ID = INVOICE_DETAIL_ENTRY.getAliasNames()[13];



	/** 
	* DAOConstantsEntry for InvoiceTax entity.
	*/ 
	DAOConstantsEntry INVOICE_TAX_ENTRY = DAOConstants.getDAOConstant(InvoiceTax.class);

	/** 
	* Alias value: InvoiceTax_id
	* Hibernate value: InvoiceTax.id
	*/
	String  INVOICE_TAX_ID = INVOICE_TAX_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: InvoiceTax_invoiceDetail_id
	* Hibernate value: InvoiceTax.invoiceDetail.id
	*/
	String  INVOICE_TAX_INVOICE_DETAIL_ID = INVOICE_TAX_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: InvoiceTax_percentage
	* Hibernate value: InvoiceTax.percentage
	*/
	String  INVOICE_TAX_PERCENTAGE = INVOICE_TAX_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: InvoiceTax_surcharge
	* Hibernate value: InvoiceTax.surcharge
	*/
	String  INVOICE_TAX_SURCHARGE = INVOICE_TAX_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: InvoiceTax_taxType
	* Hibernate value: InvoiceTax.taxType
	*/
	String  INVOICE_TAX_TAX_TYPE = INVOICE_TAX_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for PayMethod entity.
	*/ 
	DAOConstantsEntry PAY_METHOD_ENTRY = DAOConstants.getDAOConstant(PayMethod.class);

	/** 
	* Alias value: PayMethod_id
	* Hibernate value: PayMethod.id
	*/
	String  PAY_METHOD_ID = PAY_METHOD_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: PayMethod_name
	* Hibernate value: PayMethod.name
	*/
	String  PAY_METHOD_NAME = PAY_METHOD_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: PayMethod_type
	* Hibernate value: PayMethod.type
	*/
	String  PAY_METHOD_TYPE = PAY_METHOD_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for RegistryBank entity.
	*/ 
	DAOConstantsEntry REGISTRY_BANK_ENTRY = DAOConstants.getDAOConstant(RegistryBank.class);

	/** 
	* Alias value: RegistryBank_bankAccount
	* Hibernate value: RegistryBank.bankAccount
	*/
	String  REGISTRY_BANK_BANK_ACCOUNT = REGISTRY_BANK_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: RegistryBank_bank_id
	* Hibernate value: RegistryBank.bank.id
	*/
	String  REGISTRY_BANK_BANK_ID = REGISTRY_BANK_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: RegistryBank_id
	* Hibernate value: RegistryBank.id
	*/
	String  REGISTRY_BANK_ID = REGISTRY_BANK_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: RegistryBank_registry_id
	* Hibernate value: RegistryBank.registry.id
	*/
	String  REGISTRY_BANK_REGISTRY_ID = REGISTRY_BANK_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: RegistryBank_sufix
	* Hibernate value: RegistryBank.sufix
	*/
	String  REGISTRY_BANK_SUFIX = REGISTRY_BANK_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for RegistryPayMethod entity.
	*/ 
	DAOConstantsEntry REGISTRY_PAY_METHOD_ENTRY = DAOConstants.getDAOConstant(RegistryPayMethod.class);

	/** 
	* Alias value: RegistryPayMethod_daysBetweenPayments
	* Hibernate value: RegistryPayMethod.daysBetweenPayments
	*/
	String  REGISTRY_PAY_METHOD_DAYS_BETWEEN_PAYMENTS = REGISTRY_PAY_METHOD_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: RegistryPayMethod_daysToFirstPayment
	* Hibernate value: RegistryPayMethod.daysToFirstPayment
	*/
	String  REGISTRY_PAY_METHOD_DAYS_TO_FIRST_PAYMENT = REGISTRY_PAY_METHOD_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: RegistryPayMethod_id
	* Hibernate value: RegistryPayMethod.id
	*/
	String  REGISTRY_PAY_METHOD_ID = REGISTRY_PAY_METHOD_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: RegistryPayMethod_numberOfPayments
	* Hibernate value: RegistryPayMethod.numberOfPayments
	*/
	String  REGISTRY_PAY_METHOD_NUMBER_OF_PAYMENTS = REGISTRY_PAY_METHOD_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: RegistryPayMethod_paymentDays
	* Hibernate value: RegistryPayMethod.paymentDays
	*/
	String  REGISTRY_PAY_METHOD_PAYMENT_DAYS = REGISTRY_PAY_METHOD_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: RegistryPayMethod_payment_id
	* Hibernate value: RegistryPayMethod.payment.id
	*/
	String  REGISTRY_PAY_METHOD_PAYMENT_ID = REGISTRY_PAY_METHOD_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: RegistryPayMethod_registry_id
	* Hibernate value: RegistryPayMethod.registry.id
	*/
	String  REGISTRY_PAY_METHOD_REGISTRY_ID = REGISTRY_PAY_METHOD_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for Creditor entity.
	*/ 
	DAOConstantsEntry CREDITOR_ENTRY = DAOConstants.getDAOConstant(Creditor.class);

	/** 
	* Alias value: Creditor_id
	* Hibernate value: Creditor.id
	*/
	String  CREDITOR_ID = CREDITOR_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Creditor_registry_id
	* Hibernate value: Creditor.registry.id
	*/
	String  CREDITOR_REGISTRY_ID = CREDITOR_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Creditor_status
	* Hibernate value: Creditor.status
	*/
	String  CREDITOR_STATUS = CREDITOR_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Creditor_registry_name
	* Hibernate value: Creditor.registry.name
	*/
	String  CREDITOR_REGISTRY_NAME = CREDITOR_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Creditor_registry_surname
	* Hibernate value: Creditor.registry.surname
	*/
	String  CREDITOR_REGISTRY_SURNAME = CREDITOR_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Creditor_registry_alias
	* Hibernate value: Creditor.registry.alias
	*/
	String  CREDITOR_REGISTRY_ALIAS = CREDITOR_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Creditor_registry_document
	* Hibernate value: Creditor.registry.document
	*/
	String  CREDITOR_REGISTRY_DOCUMENT = CREDITOR_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Creditor_withholding
	* Hibernate value: Creditor.withholding
	*/
	String  CREDITOR_WITHHOLDING = CREDITOR_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Creditor_scope_id
	* Hibernate value: Creditor.scope.id
	*/
	String  CREDITOR_SCOPE_ID = CREDITOR_ENTRY.getAliasNames()[8];



	/** 
	* DAOConstantsEntry for FinanceTracking entity.
	*/ 
	DAOConstantsEntry FINANCE_TRACKING_ENTRY = DAOConstants.getDAOConstant(FinanceTracking.class);

	/** 
	* Alias value: FinanceTracking_amount
	* Hibernate value: FinanceTracking.amount
	*/
	String  FINANCE_TRACKING_AMOUNT = FINANCE_TRACKING_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: FinanceTracking_description
	* Hibernate value: FinanceTracking.description
	*/
	String  FINANCE_TRACKING_DESCRIPTION = FINANCE_TRACKING_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: FinanceTracking_finance_id
	* Hibernate value: FinanceTracking.finance.id
	*/
	String  FINANCE_TRACKING_FINANCE_ID = FINANCE_TRACKING_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: FinanceTracking_id
	* Hibernate value: FinanceTracking.id
	*/
	String  FINANCE_TRACKING_ID = FINANCE_TRACKING_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: FinanceTracking_trackingDate
	* Hibernate value: FinanceTracking.trackingDate
	*/
	String  FINANCE_TRACKING_TRACKING_DATE = FINANCE_TRACKING_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: FinanceTracking_type
	* Hibernate value: FinanceTracking.type
	*/
	String  FINANCE_TRACKING_TYPE = FINANCE_TRACKING_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for InvoicingGroup entity.
	*/ 
	DAOConstantsEntry INVOICING_GROUP_ENTRY = DAOConstants.getDAOConstant(InvoicingGroup.class);

	/** 
	* Alias value: InvoicingGroup_id
	* Hibernate value: InvoicingGroup.id
	*/
	String  INVOICING_GROUP_ID = INVOICING_GROUP_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: InvoicingGroup_parent_id
	* Hibernate value: InvoicingGroup.parent.id
	*/
	String  INVOICING_GROUP_PARENT_ID = INVOICING_GROUP_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for InvoicingGroupDetail entity.
	*/ 
	DAOConstantsEntry INVOICING_GROUP_DETAIL_ENTRY = DAOConstants.getDAOConstant(InvoicingGroupDetail.class);

	/** 
	* Alias value: InvoicingGroupDetail_child_id
	* Hibernate value: InvoicingGroupDetail.child.id
	*/
	String  INVOICING_GROUP_DETAIL_CHILD_ID = INVOICING_GROUP_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: InvoicingGroupDetail_grouped
	* Hibernate value: InvoicingGroupDetail.grouped
	*/
	String  INVOICING_GROUP_DETAIL_GROUPED = INVOICING_GROUP_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: InvoicingGroupDetail_id
	* Hibernate value: InvoicingGroupDetail.id
	*/
	String  INVOICING_GROUP_DETAIL_ID = INVOICING_GROUP_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: InvoicingGroupDetail_invoicingGroup_id
	* Hibernate value: InvoicingGroupDetail.invoicingGroup.id
	*/
	String  INVOICING_GROUP_DETAIL_INVOICING_GROUP_ID = INVOICING_GROUP_DETAIL_ENTRY.getAliasNames()[3];


}