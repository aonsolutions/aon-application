package com.code.aon.finance.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.finance.CustomerFee;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceAttachment;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.InvoicingGroup;
import com.code.aon.finance.InvoicingGroupDetail;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IFinanceAlias {



	/** 
	* DAOConstantsEntry for CustomerFee entity.
	*/ 
	DAOConstantsEntry CUSTOMER_FEE_ENTRY = DAOConstants.getDAOConstant(CustomerFee.class);

	/** 
	* Alias value: CustomerFee_id
	* Hibernate value: CustomerFee.id
	*/
	String  CUSTOMER_FEE_ID = CUSTOMER_FEE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CustomerFee_customer_id
	* Hibernate value: CustomerFee.customer.id
	*/
	String  CUSTOMER_FEE_CUSTOMER_ID = CUSTOMER_FEE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CustomerFee_customer_registry_name
	* Hibernate value: CustomerFee.customer.registry.name
	*/
	String  CUSTOMER_FEE_CUSTOMER_REGISTRY_NAME = CUSTOMER_FEE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CustomerFee_customer_registry_surname
	* Hibernate value: CustomerFee.customer.registry.surname
	*/
	String  CUSTOMER_FEE_CUSTOMER_REGISTRY_SURNAME = CUSTOMER_FEE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: CustomerFee_customer_status
	* Hibernate value: CustomerFee.customer.status
	*/
	String  CUSTOMER_FEE_CUSTOMER_STATUS = CUSTOMER_FEE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: CustomerFee_customer_scope_id
	* Hibernate value: CustomerFee.customer.scope.id
	*/
	String  CUSTOMER_FEE_CUSTOMER_SCOPE_ID = CUSTOMER_FEE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: CustomerFee_item_id
	* Hibernate value: CustomerFee.item.id
	*/
	String  CUSTOMER_FEE_ITEM_ID = CUSTOMER_FEE_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: CustomerFee_description
	* Hibernate value: CustomerFee.description
	*/
	String  CUSTOMER_FEE_DESCRIPTION = CUSTOMER_FEE_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: CustomerFee_quantity
	* Hibernate value: CustomerFee.quantity
	*/
	String  CUSTOMER_FEE_QUANTITY = CUSTOMER_FEE_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: CustomerFee_price
	* Hibernate value: CustomerFee.price
	*/
	String  CUSTOMER_FEE_PRICE = CUSTOMER_FEE_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: CustomerFee_discountExpression
	* Hibernate value: CustomerFee.discountExpression
	*/
	String  CUSTOMER_FEE_DISCOUNT_EXPRESSION = CUSTOMER_FEE_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: CustomerFee_initialDate
	* Hibernate value: CustomerFee.initialDate
	*/
	String  CUSTOMER_FEE_INITIAL_DATE = CUSTOMER_FEE_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: CustomerFee_finalDate
	* Hibernate value: CustomerFee.finalDate
	*/
	String  CUSTOMER_FEE_FINAL_DATE = CUSTOMER_FEE_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: CustomerFee_billingDate
	* Hibernate value: CustomerFee.billingDate
	*/
	String  CUSTOMER_FEE_BILLING_DATE = CUSTOMER_FEE_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: CustomerFee_period
	* Hibernate value: CustomerFee.period
	*/
	String  CUSTOMER_FEE_PERIOD = CUSTOMER_FEE_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: CustomerFee_securityLevel
	* Hibernate value: CustomerFee.securityLevel
	*/
	String  CUSTOMER_FEE_SECURITY_LEVEL = CUSTOMER_FEE_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: CustomerFee_workPlace_id
	* Hibernate value: CustomerFee.workPlace.id
	*/
	String  CUSTOMER_FEE_WORK_PLACE_ID = CUSTOMER_FEE_ENTRY.getAliasNames()[16];



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
	* Alias value: Finance_bank_code
	* Hibernate value: Finance.bank.code
	*/
	String  FINANCE_BANK_CODE = FINANCE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Finance_concept
	* Hibernate value: Finance.concept
	*/
	String  FINANCE_CONCEPT = FINANCE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Finance_dueDate
	* Hibernate value: Finance.dueDate
	*/
	String  FINANCE_DUE_DATE = FINANCE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Finance_expenses
	* Hibernate value: Finance.expenses
	*/
	String  FINANCE_EXPENSES = FINANCE_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Finance_financeStatus
	* Hibernate value: Finance.financeStatus
	*/
	String  FINANCE_FINANCE_STATUS = FINANCE_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Finance_id
	* Hibernate value: Finance.id
	*/
	String  FINANCE_ID = FINANCE_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Finance_invoice_id
	* Hibernate value: Finance.invoice.id
	*/
	String  FINANCE_INVOICE_ID = FINANCE_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Finance_payMethod_id
	* Hibernate value: Finance.payMethod.id
	*/
	String  FINANCE_PAY_METHOD_ID = FINANCE_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Finance_payMethod_type
	* Hibernate value: Finance.payMethod.type
	*/
	String  FINANCE_PAY_METHOD_TYPE = FINANCE_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Finance_payment
	* Hibernate value: Finance.payment
	*/
	String  FINANCE_PAYMENT = FINANCE_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Finance_registry_id
	* Hibernate value: Finance.registry.id
	*/
	String  FINANCE_REGISTRY_ID = FINANCE_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Finance_securityLevel
	* Hibernate value: Finance.securityLevel
	*/
	String  FINANCE_SECURITY_LEVEL = FINANCE_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Finance_invoice_series
	* Hibernate value: Finance.invoice.series
	*/
	String  FINANCE_INVOICE_SERIES = FINANCE_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Finance_invoice_number
	* Hibernate value: Finance.invoice.number
	*/
	String  FINANCE_INVOICE_NUMBER = FINANCE_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Finance_invoice_referenceCode
	* Hibernate value: Finance.invoice.referenceCode
	*/
	String  FINANCE_INVOICE_REFERENCE_CODE = FINANCE_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Finance_invoice_issueDate
	* Hibernate value: Finance.invoice.issueDate
	*/
	String  FINANCE_INVOICE_ISSUE_DATE = FINANCE_ENTRY.getAliasNames()[18];



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
	* Alias value: FinanceBatchDetail_financeBatch_issueDate
	* Hibernate value: FinanceBatchDetail.financeBatch.issueDate
	*/
	String  FINANCE_BATCH_DETAIL_FINANCE_BATCH_ISSUE_DATE = FINANCE_BATCH_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: FinanceBatchDetail_finance_invoice_series
	* Hibernate value: FinanceBatchDetail.finance.invoice.series
	*/
	String  FINANCE_BATCH_DETAIL_FINANCE_INVOICE_SERIES = FINANCE_BATCH_DETAIL_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: FinanceBatchDetail_finance_invoice_number
	* Hibernate value: FinanceBatchDetail.finance.invoice.number
	*/
	String  FINANCE_BATCH_DETAIL_FINANCE_INVOICE_NUMBER = FINANCE_BATCH_DETAIL_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: FinanceBatchDetail_finance_dueDate
	* Hibernate value: FinanceBatchDetail.finance.dueDate
	*/
	String  FINANCE_BATCH_DETAIL_FINANCE_DUE_DATE = FINANCE_BATCH_DETAIL_ENTRY.getAliasNames()[8];



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
	* Alias value: Invoice_referenceCode
	* Hibernate value: Invoice.referenceCode
	*/
	String  INVOICE_REFERENCE_CODE = INVOICE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Invoice_registry_id
	* Hibernate value: Invoice.registry.id
	*/
	String  INVOICE_REGISTRY_ID = INVOICE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Invoice_registryAddress_id
	* Hibernate value: Invoice.registryAddress.id
	*/
	String  INVOICE_REGISTRY_ADDRESS_ID = INVOICE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Invoice_issueDate
	* Hibernate value: Invoice.issueDate
	*/
	String  INVOICE_ISSUE_DATE = INVOICE_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Invoice_taxDate
	* Hibernate value: Invoice.taxDate
	*/
	String  INVOICE_TAX_DATE = INVOICE_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Invoice_securityLevel
	* Hibernate value: Invoice.securityLevel
	*/
	String  INVOICE_SECURITY_LEVEL = INVOICE_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Invoice_status
	* Hibernate value: Invoice.status
	*/
	String  INVOICE_STATUS = INVOICE_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Invoice_registryName
	* Hibernate value: Invoice.registryName
	*/
	String  INVOICE_REGISTRY_NAME = INVOICE_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Invoice_registryDocument
	* Hibernate value: Invoice.registryDocument
	*/
	String  INVOICE_REGISTRY_DOCUMENT = INVOICE_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Invoice_type
	* Hibernate value: Invoice.type
	*/
	String  INVOICE_TYPE = INVOICE_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Invoice_taxFree
	* Hibernate value: Invoice.taxFree
	*/
	String  INVOICE_TAX_FREE = INVOICE_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Invoice_surcharge
	* Hibernate value: Invoice.surcharge
	*/
	String  INVOICE_SURCHARGE = INVOICE_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Invoice_withholding
	* Hibernate value: Invoice.withholding
	*/
	String  INVOICE_WITHHOLDING = INVOICE_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Invoice_comments
	* Hibernate value: Invoice.comments
	*/
	String  INVOICE_COMMENTS = INVOICE_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Invoice_investment
	* Hibernate value: Invoice.investment
	*/
	String  INVOICE_INVESTMENT = INVOICE_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Invoice_transaction
	* Hibernate value: Invoice.transaction
	*/
	String  INVOICE_TRANSACTION = INVOICE_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Invoice_signed
	* Hibernate value: Invoice.signed
	*/
	String  INVOICE_SIGNED = INVOICE_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Invoice_scope_id
	* Hibernate value: Invoice.scope.id
	*/
	String  INVOICE_SCOPE_ID = INVOICE_ENTRY.getAliasNames()[20];



	/** 
	* DAOConstantsEntry for InvoiceAddress entity.
	*/ 
	DAOConstantsEntry INVOICE_ADDRESS_ENTRY = DAOConstants.getDAOConstant(InvoiceAddress.class);

	/** 
	* Alias value: InvoiceAddress_address
	* Hibernate value: InvoiceAddress.address
	*/
	String  INVOICE_ADDRESS_ADDRESS = INVOICE_ADDRESS_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: InvoiceAddress_address2
	* Hibernate value: InvoiceAddress.address2
	*/
	String  INVOICE_ADDRESS_ADDRESS2 = INVOICE_ADDRESS_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: InvoiceAddress_city
	* Hibernate value: InvoiceAddress.city
	*/
	String  INVOICE_ADDRESS_CITY = INVOICE_ADDRESS_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: InvoiceAddress_geozone_id
	* Hibernate value: InvoiceAddress.geozone.id
	*/
	String  INVOICE_ADDRESS_GEOZONE_ID = INVOICE_ADDRESS_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: InvoiceAddress_id
	* Hibernate value: InvoiceAddress.id
	*/
	String  INVOICE_ADDRESS_ID = INVOICE_ADDRESS_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: InvoiceAddress_invoice_id
	* Hibernate value: InvoiceAddress.invoice.id
	*/
	String  INVOICE_ADDRESS_INVOICE_ID = INVOICE_ADDRESS_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: InvoiceAddress_zip
	* Hibernate value: InvoiceAddress.zip
	*/
	String  INVOICE_ADDRESS_ZIP = INVOICE_ADDRESS_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for InvoiceAttachment entity.
	*/ 
	DAOConstantsEntry INVOICE_ATTACHMENT_ENTRY = DAOConstants.getDAOConstant(InvoiceAttachment.class);

	/** 
	* Alias value: InvoiceAttachment_data
	* Hibernate value: InvoiceAttachment.data
	*/
	String  INVOICE_ATTACHMENT_DATA = INVOICE_ATTACHMENT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: InvoiceAttachment_description
	* Hibernate value: InvoiceAttachment.description
	*/
	String  INVOICE_ATTACHMENT_DESCRIPTION = INVOICE_ATTACHMENT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: InvoiceAttachment_id
	* Hibernate value: InvoiceAttachment.id
	*/
	String  INVOICE_ATTACHMENT_ID = INVOICE_ATTACHMENT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: InvoiceAttachment_invoice_id
	* Hibernate value: InvoiceAttachment.invoice.id
	*/
	String  INVOICE_ATTACHMENT_INVOICE_ID = INVOICE_ATTACHMENT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: InvoiceAttachment_mimeType
	* Hibernate value: InvoiceAttachment.mimeType
	*/
	String  INVOICE_ATTACHMENT_MIME_TYPE = INVOICE_ATTACHMENT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: InvoiceAttachment_size
	* Hibernate value: InvoiceAttachment.size
	*/
	String  INVOICE_ATTACHMENT_SIZE = INVOICE_ATTACHMENT_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for InvoiceDetail entity.
	*/ 
	DAOConstantsEntry INVOICE_DETAIL_ENTRY = DAOConstants.getDAOConstant(InvoiceDetail.class);

	/** 
	* Alias value: InvoiceDetail_sourceId
	* Hibernate value: InvoiceDetail.sourceId
	*/
	String  INVOICE_DETAIL_SOURCE_ID = INVOICE_DETAIL_ENTRY.getAliasNames()[0];

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
	* Alias value: InvoiceDetail_invoice_type
	* Hibernate value: InvoiceDetail.invoice.type
	*/
	String  INVOICE_DETAIL_INVOICE_TYPE = INVOICE_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: InvoiceDetail_item_id
	* Hibernate value: InvoiceDetail.item.id
	*/
	String  INVOICE_DETAIL_ITEM_ID = INVOICE_DETAIL_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: InvoiceDetail_line
	* Hibernate value: InvoiceDetail.line
	*/
	String  INVOICE_DETAIL_LINE = INVOICE_DETAIL_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: InvoiceDetail_price
	* Hibernate value: InvoiceDetail.price
	*/
	String  INVOICE_DETAIL_PRICE = INVOICE_DETAIL_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: InvoiceDetail_quantity
	* Hibernate value: InvoiceDetail.quantity
	*/
	String  INVOICE_DETAIL_QUANTITY = INVOICE_DETAIL_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: InvoiceDetail_source
	* Hibernate value: InvoiceDetail.source
	*/
	String  INVOICE_DETAIL_SOURCE = INVOICE_DETAIL_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: InvoiceDetail_item_product_type
	* Hibernate value: InvoiceDetail.item.product.type
	*/
	String  INVOICE_DETAIL_ITEM_PRODUCT_TYPE = INVOICE_DETAIL_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: InvoiceDetail_item_product_code
	* Hibernate value: InvoiceDetail.item.product.code
	*/
	String  INVOICE_DETAIL_ITEM_PRODUCT_CODE = INVOICE_DETAIL_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: InvoiceDetail_taxableBase
	* Hibernate value: InvoiceDetail.taxableBase
	*/
	String  INVOICE_DETAIL_TAXABLE_BASE = INVOICE_DETAIL_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: InvoiceDetail_taxes
	* Hibernate value: InvoiceDetail.taxes
	*/
	String  INVOICE_DETAIL_TAXES = INVOICE_DETAIL_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: InvoiceDetail_workPlace_id
	* Hibernate value: InvoiceDetail.workPlace.id
	*/
	String  INVOICE_DETAIL_WORK_PLACE_ID = INVOICE_DETAIL_ENTRY.getAliasNames()[15];



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
	* Alias value: InvoiceTax_invoiceDetail_invoice_id
	* Hibernate value: InvoiceTax.invoiceDetail.invoice.id
	*/
	String  INVOICE_TAX_INVOICE_DETAIL_INVOICE_ID = INVOICE_TAX_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: InvoiceTax_taxType
	* Hibernate value: InvoiceTax.taxType
	*/
	String  INVOICE_TAX_TAX_TYPE = INVOICE_TAX_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: InvoiceTax_percentage
	* Hibernate value: InvoiceTax.percentage
	*/
	String  INVOICE_TAX_PERCENTAGE = INVOICE_TAX_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: InvoiceTax_surcharge
	* Hibernate value: InvoiceTax.surcharge
	*/
	String  INVOICE_TAX_SURCHARGE = INVOICE_TAX_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: InvoiceTax_quota
	* Hibernate value: InvoiceTax.quota
	*/
	String  INVOICE_TAX_QUOTA = INVOICE_TAX_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: InvoiceTax_surchargeQuota
	* Hibernate value: InvoiceTax.surchargeQuota
	*/
	String  INVOICE_TAX_SURCHARGE_QUOTA = INVOICE_TAX_ENTRY.getAliasNames()[7];



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
	* Alias value: Creditor_transaction
	* Hibernate value: Creditor.transaction
	*/
	String  CREDITOR_TRANSACTION = CREDITOR_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Creditor_scope_id
	* Hibernate value: Creditor.scope.id
	*/
	String  CREDITOR_SCOPE_ID = CREDITOR_ENTRY.getAliasNames()[9];



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
	* Alias value: FinanceTracking_finance_invoice_id
	* Hibernate value: FinanceTracking.finance.invoice.id
	*/
	String  FINANCE_TRACKING_FINANCE_INVOICE_ID = FINANCE_TRACKING_ENTRY.getAliasNames()[6];



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
	* Alias value: InvoicingGroup_parent_surname
	* Hibernate value: InvoicingGroup.parent.surname
	*/
	String  INVOICING_GROUP_PARENT_SURNAME = INVOICING_GROUP_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: InvoicingGroup_parent_name
	* Hibernate value: InvoicingGroup.parent.name
	*/
	String  INVOICING_GROUP_PARENT_NAME = INVOICING_GROUP_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for InvoicingGroupDetail entity.
	*/ 
	DAOConstantsEntry INVOICING_GROUP_DETAIL_ENTRY = DAOConstants.getDAOConstant(InvoicingGroupDetail.class);

	/** 
	* Alias value: InvoicingGroupDetail_id
	* Hibernate value: InvoicingGroupDetail.id
	*/
	String  INVOICING_GROUP_DETAIL_ID = INVOICING_GROUP_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: InvoicingGroupDetail_invoicingGroup_id
	* Hibernate value: InvoicingGroupDetail.invoicingGroup.id
	*/
	String  INVOICING_GROUP_DETAIL_INVOICING_GROUP_ID = INVOICING_GROUP_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: InvoicingGroupDetail_child_id
	* Hibernate value: InvoicingGroupDetail.child.id
	*/
	String  INVOICING_GROUP_DETAIL_CHILD_ID = INVOICING_GROUP_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: InvoicingGroupDetail_child_surname
	* Hibernate value: InvoicingGroupDetail.child.surname
	*/
	String  INVOICING_GROUP_DETAIL_CHILD_SURNAME = INVOICING_GROUP_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: InvoicingGroupDetail_child_name
	* Hibernate value: InvoicingGroupDetail.child.name
	*/
	String  INVOICING_GROUP_DETAIL_CHILD_NAME = INVOICING_GROUP_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: InvoicingGroupDetail_grouped
	* Hibernate value: InvoicingGroupDetail.grouped
	*/
	String  INVOICING_GROUP_DETAIL_GROUPED = INVOICING_GROUP_DETAIL_ENTRY.getAliasNames()[5];


}