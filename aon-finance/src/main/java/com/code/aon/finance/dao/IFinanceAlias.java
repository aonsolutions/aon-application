package com.code.aon.finance.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.finance.BankConcept;
import com.code.aon.finance.BankStatement;
import com.code.aon.finance.BankStatementLink;
import com.code.aon.finance.CashFlowForecast;
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
	* DAOConstantsEntry for BankConcept entity.
	*/ 
	DAOConstantsEntry BANK_CONCEPT_ENTRY = DAOConstants.getDAOConstant(BankConcept.class);

	/** 
	* Alias value: BankConcept_id
	* Hibernate value: BankConcept.id
	*/
	String  BANK_CONCEPT_ID = BANK_CONCEPT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: BankConcept_name
	* Hibernate value: BankConcept.name
	*/
	String  BANK_CONCEPT_NAME = BANK_CONCEPT_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for BankStatement entity.
	*/ 
	DAOConstantsEntry BANK_STATEMENT_ENTRY = DAOConstants.getDAOConstant(BankStatement.class);

	/** 
	* Alias value: BankStatement_amount
	* Hibernate value: BankStatement.amount
	*/
	String  BANK_STATEMENT_AMOUNT = BANK_STATEMENT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: BankStatement_comments
	* Hibernate value: BankStatement.comments
	*/
	String  BANK_STATEMENT_COMMENTS = BANK_STATEMENT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: BankStatement_commonConcept
	* Hibernate value: BankStatement.commonConcept
	*/
	String  BANK_STATEMENT_COMMON_CONCEPT = BANK_STATEMENT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: BankStatement_description
	* Hibernate value: BankStatement.description
	*/
	String  BANK_STATEMENT_DESCRIPTION = BANK_STATEMENT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: BankStatement_document
	* Hibernate value: BankStatement.document
	*/
	String  BANK_STATEMENT_DOCUMENT = BANK_STATEMENT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: BankStatement_id
	* Hibernate value: BankStatement.id
	*/
	String  BANK_STATEMENT_ID = BANK_STATEMENT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: BankStatement_lotNumber
	* Hibernate value: BankStatement.lotNumber
	*/
	String  BANK_STATEMENT_LOT_NUMBER = BANK_STATEMENT_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: BankStatement_operationDate
	* Hibernate value: BankStatement.operationDate
	*/
	String  BANK_STATEMENT_OPERATION_DATE = BANK_STATEMENT_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: BankStatement_ownConcept
	* Hibernate value: BankStatement.ownConcept
	*/
	String  BANK_STATEMENT_OWN_CONCEPT = BANK_STATEMENT_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: BankStatement_payment
	* Hibernate value: BankStatement.payment
	*/
	String  BANK_STATEMENT_PAYMENT = BANK_STATEMENT_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: BankStatement_reference1
	* Hibernate value: BankStatement.reference1
	*/
	String  BANK_STATEMENT_REFERENCE1 = BANK_STATEMENT_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: BankStatement_reference2
	* Hibernate value: BankStatement.reference2
	*/
	String  BANK_STATEMENT_REFERENCE2 = BANK_STATEMENT_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: BankStatement_registryBank_id
	* Hibernate value: BankStatement.registryBank.id
	*/
	String  BANK_STATEMENT_REGISTRY_BANK_ID = BANK_STATEMENT_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: BankStatement_reliability
	* Hibernate value: BankStatement.reliability
	*/
	String  BANK_STATEMENT_RELIABILITY = BANK_STATEMENT_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: BankStatement_securityLevel
	* Hibernate value: BankStatement.securityLevel
	*/
	String  BANK_STATEMENT_SECURITY_LEVEL = BANK_STATEMENT_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: BankStatement_status
	* Hibernate value: BankStatement.status
	*/
	String  BANK_STATEMENT_STATUS = BANK_STATEMENT_ENTRY.getAliasNames()[15];



	/** 
	* DAOConstantsEntry for BankStatementLink entity.
	*/ 
	DAOConstantsEntry BANK_STATEMENT_LINK_ENTRY = DAOConstants.getDAOConstant(BankStatementLink.class);

	/** 
	* Alias value: BankStatementLink_amount
	* Hibernate value: BankStatementLink.amount
	*/
	String  BANK_STATEMENT_LINK_AMOUNT = BANK_STATEMENT_LINK_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: BankStatementLink_bankStatement_id
	* Hibernate value: BankStatementLink.bankStatement.id
	*/
	String  BANK_STATEMENT_LINK_BANK_STATEMENT_ID = BANK_STATEMENT_LINK_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: BankStatementLink_bankStatement_operationDate
	* Hibernate value: BankStatementLink.bankStatement.operationDate
	*/
	String  BANK_STATEMENT_LINK_BANK_STATEMENT_OPERATION_DATE = BANK_STATEMENT_LINK_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: BankStatementLink_bankStatement_payment
	* Hibernate value: BankStatementLink.bankStatement.payment
	*/
	String  BANK_STATEMENT_LINK_BANK_STATEMENT_PAYMENT = BANK_STATEMENT_LINK_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: BankStatementLink_bankStatement_securityLevel
	* Hibernate value: BankStatementLink.bankStatement.securityLevel
	*/
	String  BANK_STATEMENT_LINK_BANK_STATEMENT_SECURITY_LEVEL = BANK_STATEMENT_LINK_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: BankStatementLink_bankStatement_status
	* Hibernate value: BankStatementLink.bankStatement.status
	*/
	String  BANK_STATEMENT_LINK_BANK_STATEMENT_STATUS = BANK_STATEMENT_LINK_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: BankStatementLink_id
	* Hibernate value: BankStatementLink.id
	*/
	String  BANK_STATEMENT_LINK_ID = BANK_STATEMENT_LINK_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: BankStatementLink_linkedBankStatementLink
	* Hibernate value: BankStatementLink.linkedBankStatementLink
	*/
	String  BANK_STATEMENT_LINK_LINKED_BANK_STATEMENT_LINK = BANK_STATEMENT_LINK_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: BankStatementLink_linkedBankStatementLink_id
	* Hibernate value: BankStatementLink.linkedBankStatementLink.id
	*/
	String  BANK_STATEMENT_LINK_LINKED_BANK_STATEMENT_LINK_ID = BANK_STATEMENT_LINK_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: BankStatementLink_source
	* Hibernate value: BankStatementLink.source
	*/
	String  BANK_STATEMENT_LINK_SOURCE = BANK_STATEMENT_LINK_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: BankStatementLink_sourceDate
	* Hibernate value: BankStatementLink.sourceDate
	*/
	String  BANK_STATEMENT_LINK_SOURCE_DATE = BANK_STATEMENT_LINK_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: BankStatementLink_sourceId
	* Hibernate value: BankStatementLink.sourceId
	*/
	String  BANK_STATEMENT_LINK_SOURCE_ID = BANK_STATEMENT_LINK_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: BankStatementLink_status
	* Hibernate value: BankStatementLink.status
	*/
	String  BANK_STATEMENT_LINK_STATUS = BANK_STATEMENT_LINK_ENTRY.getAliasNames()[12];



	/** 
	* DAOConstantsEntry for CashFlowForecast entity.
	*/ 
	DAOConstantsEntry CASH_FLOW_FORECAST_ENTRY = DAOConstants.getDAOConstant(CashFlowForecast.class);

	/** 
	* Alias value: CashFlowForecast_amount
	* Hibernate value: CashFlowForecast.amount
	*/
	String  CASH_FLOW_FORECAST_AMOUNT = CASH_FLOW_FORECAST_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CashFlowForecast_april
	* Hibernate value: CashFlowForecast.april
	*/
	String  CASH_FLOW_FORECAST_APRIL = CASH_FLOW_FORECAST_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CashFlowForecast_august
	* Hibernate value: CashFlowForecast.august
	*/
	String  CASH_FLOW_FORECAST_AUGUST = CASH_FLOW_FORECAST_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CashFlowForecast_december
	* Hibernate value: CashFlowForecast.december
	*/
	String  CASH_FLOW_FORECAST_DECEMBER = CASH_FLOW_FORECAST_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: CashFlowForecast_description
	* Hibernate value: CashFlowForecast.description
	*/
	String  CASH_FLOW_FORECAST_DESCRIPTION = CASH_FLOW_FORECAST_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: CashFlowForecast_dueDate
	* Hibernate value: CashFlowForecast.dueDate
	*/
	String  CASH_FLOW_FORECAST_DUE_DATE = CASH_FLOW_FORECAST_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: CashFlowForecast_february
	* Hibernate value: CashFlowForecast.february
	*/
	String  CASH_FLOW_FORECAST_FEBRUARY = CASH_FLOW_FORECAST_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: CashFlowForecast_id
	* Hibernate value: CashFlowForecast.id
	*/
	String  CASH_FLOW_FORECAST_ID = CASH_FLOW_FORECAST_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: CashFlowForecast_january
	* Hibernate value: CashFlowForecast.january
	*/
	String  CASH_FLOW_FORECAST_JANUARY = CASH_FLOW_FORECAST_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: CashFlowForecast_july
	* Hibernate value: CashFlowForecast.july
	*/
	String  CASH_FLOW_FORECAST_JULY = CASH_FLOW_FORECAST_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: CashFlowForecast_june
	* Hibernate value: CashFlowForecast.june
	*/
	String  CASH_FLOW_FORECAST_JUNE = CASH_FLOW_FORECAST_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: CashFlowForecast_march
	* Hibernate value: CashFlowForecast.march
	*/
	String  CASH_FLOW_FORECAST_MARCH = CASH_FLOW_FORECAST_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: CashFlowForecast_may
	* Hibernate value: CashFlowForecast.may
	*/
	String  CASH_FLOW_FORECAST_MAY = CASH_FLOW_FORECAST_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: CashFlowForecast_november
	* Hibernate value: CashFlowForecast.november
	*/
	String  CASH_FLOW_FORECAST_NOVEMBER = CASH_FLOW_FORECAST_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: CashFlowForecast_october
	* Hibernate value: CashFlowForecast.october
	*/
	String  CASH_FLOW_FORECAST_OCTOBER = CASH_FLOW_FORECAST_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: CashFlowForecast_payment
	* Hibernate value: CashFlowForecast.payment
	*/
	String  CASH_FLOW_FORECAST_PAYMENT = CASH_FLOW_FORECAST_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: CashFlowForecast_paymentDay
	* Hibernate value: CashFlowForecast.paymentDay
	*/
	String  CASH_FLOW_FORECAST_PAYMENT_DAY = CASH_FLOW_FORECAST_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: CashFlowForecast_registryBank_id
	* Hibernate value: CashFlowForecast.registryBank.id
	*/
	String  CASH_FLOW_FORECAST_REGISTRY_BANK_ID = CASH_FLOW_FORECAST_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: CashFlowForecast_september
	* Hibernate value: CashFlowForecast.september
	*/
	String  CASH_FLOW_FORECAST_SEPTEMBER = CASH_FLOW_FORECAST_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: CashFlowForecast_startDate
	* Hibernate value: CashFlowForecast.startDate
	*/
	String  CASH_FLOW_FORECAST_START_DATE = CASH_FLOW_FORECAST_ENTRY.getAliasNames()[19];



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
	* Alias value: CustomerFee_customer_status
	* Hibernate value: CustomerFee.customer.status
	*/
	String  CUSTOMER_FEE_CUSTOMER_STATUS = CUSTOMER_FEE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: CustomerFee_customer_scope_id
	* Hibernate value: CustomerFee.customer.scope.id
	*/
	String  CUSTOMER_FEE_CUSTOMER_SCOPE_ID = CUSTOMER_FEE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: CustomerFee_line
	* Hibernate value: CustomerFee.line
	*/
	String  CUSTOMER_FEE_LINE = CUSTOMER_FEE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: CustomerFee_item_id
	* Hibernate value: CustomerFee.item.id
	*/
	String  CUSTOMER_FEE_ITEM_ID = CUSTOMER_FEE_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: CustomerFee_item_product_category_id
	* Hibernate value: CustomerFee.item.product.category.id
	*/
	String  CUSTOMER_FEE_ITEM_PRODUCT_CATEGORY_ID = CUSTOMER_FEE_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: CustomerFee_description
	* Hibernate value: CustomerFee.description
	*/
	String  CUSTOMER_FEE_DESCRIPTION = CUSTOMER_FEE_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: CustomerFee_quantity
	* Hibernate value: CustomerFee.quantity
	*/
	String  CUSTOMER_FEE_QUANTITY = CUSTOMER_FEE_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: CustomerFee_price
	* Hibernate value: CustomerFee.price
	*/
	String  CUSTOMER_FEE_PRICE = CUSTOMER_FEE_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: CustomerFee_discountExpression
	* Hibernate value: CustomerFee.discountExpression
	*/
	String  CUSTOMER_FEE_DISCOUNT_EXPRESSION = CUSTOMER_FEE_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: CustomerFee_initialDate
	* Hibernate value: CustomerFee.initialDate
	*/
	String  CUSTOMER_FEE_INITIAL_DATE = CUSTOMER_FEE_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: CustomerFee_finalDate
	* Hibernate value: CustomerFee.finalDate
	*/
	String  CUSTOMER_FEE_FINAL_DATE = CUSTOMER_FEE_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: CustomerFee_billingDate
	* Hibernate value: CustomerFee.billingDate
	*/
	String  CUSTOMER_FEE_BILLING_DATE = CUSTOMER_FEE_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: CustomerFee_period
	* Hibernate value: CustomerFee.period
	*/
	String  CUSTOMER_FEE_PERIOD = CUSTOMER_FEE_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: CustomerFee_securityLevel
	* Hibernate value: CustomerFee.securityLevel
	*/
	String  CUSTOMER_FEE_SECURITY_LEVEL = CUSTOMER_FEE_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: CustomerFee_workPlace_id
	* Hibernate value: CustomerFee.workPlace.id
	*/
	String  CUSTOMER_FEE_WORK_PLACE_ID = CUSTOMER_FEE_ENTRY.getAliasNames()[17];



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
	* Hibernate value: Finance.invoice<id
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
	* Alias value: Finance_registryName
	* Hibernate value: Finance.registryName
	*/
	String  FINANCE_REGISTRY_NAME = FINANCE_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Finance_registryDocument
	* Hibernate value: Finance.registryDocument
	*/
	String  FINANCE_REGISTRY_DOCUMENT = FINANCE_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Finance_registryDocumentType
	* Hibernate value: Finance.registryDocumentType
	*/
	String  FINANCE_REGISTRY_DOCUMENT_TYPE = FINANCE_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Finance_registryDocumentCountry
	* Hibernate value: Finance.registryDocumentCountry
	*/
	String  FINANCE_REGISTRY_DOCUMENT_COUNTRY = FINANCE_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Finance_securityLevel
	* Hibernate value: Finance.securityLevel
	*/
	String  FINANCE_SECURITY_LEVEL = FINANCE_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Finance_scope_id
	* Hibernate value: Finance.scope.id
	*/
	String  FINANCE_SCOPE_ID = FINANCE_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Finance_invoice_series
	* Hibernate value: Finance.invoice<series
	*/
	String  FINANCE_INVOICE_SERIES = FINANCE_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Finance_invoice_number
	* Hibernate value: Finance.invoice<number
	*/
	String  FINANCE_INVOICE_NUMBER = FINANCE_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Finance_invoice_referenceCode
	* Hibernate value: Finance.invoice<referenceCode
	*/
	String  FINANCE_INVOICE_REFERENCE_CODE = FINANCE_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Finance_invoice_issueDate
	* Hibernate value: Finance.invoice<issueDate
	*/
	String  FINANCE_INVOICE_ISSUE_DATE = FINANCE_ENTRY.getAliasNames()[23];



	/** 
	* DAOConstantsEntry for FinanceBatch entity.
	*/ 
	DAOConstantsEntry FINANCE_BATCH_ENTRY = DAOConstants.getDAOConstant(FinanceBatch.class);

	/** 
	* Alias value: FinanceBatch_bankStatementLink
	* Hibernate value: FinanceBatch.bankStatementLink
	*/
	String  FINANCE_BATCH_BANK_STATEMENT_LINK = FINANCE_BATCH_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: FinanceBatch_bankStatementLink_id
	* Hibernate value: FinanceBatch.bankStatementLink.id
	*/
	String  FINANCE_BATCH_BANK_STATEMENT_LINK_ID = FINANCE_BATCH_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: FinanceBatch_bankStatementLink_bankStatement_id
	* Hibernate value: FinanceBatch.bankStatementLink.bankStatement.id
	*/
	String  FINANCE_BATCH_BANK_STATEMENT_LINK_BANK_STATEMENT_ID = FINANCE_BATCH_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: FinanceBatch_description
	* Hibernate value: FinanceBatch.description
	*/
	String  FINANCE_BATCH_DESCRIPTION = FINANCE_BATCH_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: FinanceBatch_financeBatchStatus
	* Hibernate value: FinanceBatch.financeBatchStatus
	*/
	String  FINANCE_BATCH_FINANCE_BATCH_STATUS = FINANCE_BATCH_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: FinanceBatch_financeBatchType
	* Hibernate value: FinanceBatch.financeBatchType
	*/
	String  FINANCE_BATCH_FINANCE_BATCH_TYPE = FINANCE_BATCH_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: FinanceBatch_id
	* Hibernate value: FinanceBatch.id
	*/
	String  FINANCE_BATCH_ID = FINANCE_BATCH_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: FinanceBatch_issueDate
	* Hibernate value: FinanceBatch.issueDate
	*/
	String  FINANCE_BATCH_ISSUE_DATE = FINANCE_BATCH_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: FinanceBatch_payment
	* Hibernate value: FinanceBatch.payment
	*/
	String  FINANCE_BATCH_PAYMENT = FINANCE_BATCH_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: FinanceBatch_registryBank_id
	* Hibernate value: FinanceBatch.registryBank.id
	*/
	String  FINANCE_BATCH_REGISTRY_BANK_ID = FINANCE_BATCH_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: FinanceBatch_securityLevel
	* Hibernate value: FinanceBatch.securityLevel
	*/
	String  FINANCE_BATCH_SECURITY_LEVEL = FINANCE_BATCH_ENTRY.getAliasNames()[10];



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
	* Alias value: FinanceBatchDetail_financeBatch_securityLevel
	* Hibernate value: FinanceBatchDetail.financeBatch.securityLevel
	*/
	String  FINANCE_BATCH_DETAIL_FINANCE_BATCH_SECURITY_LEVEL = FINANCE_BATCH_DETAIL_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: FinanceBatchDetail_finance_invoice_referenceCode
	* Hibernate value: FinanceBatchDetail.finance.invoice<referenceCode
	*/
	String  FINANCE_BATCH_DETAIL_FINANCE_INVOICE_REFERENCE_CODE = FINANCE_BATCH_DETAIL_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: FinanceBatchDetail_finance_concept
	* Hibernate value: FinanceBatchDetail.finance.concept
	*/
	String  FINANCE_BATCH_DETAIL_FINANCE_CONCEPT = FINANCE_BATCH_DETAIL_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: FinanceBatchDetail_finance_dueDate
	* Hibernate value: FinanceBatchDetail.finance.dueDate
	*/
	String  FINANCE_BATCH_DETAIL_FINANCE_DUE_DATE = FINANCE_BATCH_DETAIL_ENTRY.getAliasNames()[9];



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
	* Alias value: Invoice_registryDocumentType
	* Hibernate value: Invoice.registryDocumentType
	*/
	String  INVOICE_REGISTRY_DOCUMENT_TYPE = INVOICE_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Invoice_registryDocumentCountry
	* Hibernate value: Invoice.registryDocumentCountry
	*/
	String  INVOICE_REGISTRY_DOCUMENT_COUNTRY = INVOICE_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Invoice_type
	* Hibernate value: Invoice.type
	*/
	String  INVOICE_TYPE = INVOICE_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Invoice_taxFree
	* Hibernate value: Invoice.taxFree
	*/
	String  INVOICE_TAX_FREE = INVOICE_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Invoice_surcharge
	* Hibernate value: Invoice.surcharge
	*/
	String  INVOICE_SURCHARGE = INVOICE_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Invoice_withholding
	* Hibernate value: Invoice.withholding
	*/
	String  INVOICE_WITHHOLDING = INVOICE_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Invoice_comments
	* Hibernate value: Invoice.comments
	*/
	String  INVOICE_COMMENTS = INVOICE_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Invoice_investment
	* Hibernate value: Invoice.investment
	*/
	String  INVOICE_INVESTMENT = INVOICE_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Invoice_transaction
	* Hibernate value: Invoice.transaction
	*/
	String  INVOICE_TRANSACTION = INVOICE_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Invoice_signed
	* Hibernate value: Invoice.signed
	*/
	String  INVOICE_SIGNED = INVOICE_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Invoice_scope_id
	* Hibernate value: Invoice.scope.id
	*/
	String  INVOICE_SCOPE_ID = INVOICE_ENTRY.getAliasNames()[22];



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
	* Alias value: InvoiceAddress_number
	* Hibernate value: InvoiceAddress.number
	*/
	String  INVOICE_ADDRESS_NUMBER = INVOICE_ADDRESS_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: InvoiceAddress_streetType
	* Hibernate value: InvoiceAddress.streetType
	*/
	String  INVOICE_ADDRESS_STREET_TYPE = INVOICE_ADDRESS_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: InvoiceAddress_zip
	* Hibernate value: InvoiceAddress.zip
	*/
	String  INVOICE_ADDRESS_ZIP = INVOICE_ADDRESS_ENTRY.getAliasNames()[8];



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
	* Alias value: InvoiceTax_vatDeductionType
	* Hibernate value: InvoiceTax.vatDeductionType
	*/
	String  INVOICE_TAX_VAT_DEDUCTION_TYPE = INVOICE_TAX_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: InvoiceTax_withholdingType
	* Hibernate value: InvoiceTax.withholdingType
	*/
	String  INVOICE_TAX_WITHHOLDING_TYPE = INVOICE_TAX_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: InvoiceTax_deductibleQuota
	* Hibernate value: InvoiceTax.deductibleQuota
	*/
	String  INVOICE_TAX_DEDUCTIBLE_QUOTA = INVOICE_TAX_ENTRY.getAliasNames()[10];



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
	* Alias value: Creditor_registry_alias
	* Hibernate value: Creditor.registry.alias
	*/
	String  CREDITOR_REGISTRY_ALIAS = CREDITOR_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Creditor_registry_document
	* Hibernate value: Creditor.registry.document
	*/
	String  CREDITOR_REGISTRY_DOCUMENT = CREDITOR_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Creditor_withholding
	* Hibernate value: Creditor.withholding
	*/
	String  CREDITOR_WITHHOLDING = CREDITOR_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Creditor_transaction
	* Hibernate value: Creditor.transaction
	*/
	String  CREDITOR_TRANSACTION = CREDITOR_ENTRY.getAliasNames()[7];

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
	* Alias value: FinanceTracking_bankStatementLink
	* Hibernate value: FinanceTracking.bankStatementLink
	*/
	String  FINANCE_TRACKING_BANK_STATEMENT_LINK = FINANCE_TRACKING_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: FinanceTracking_bankStatementLink_id
	* Hibernate value: FinanceTracking.bankStatementLink.id
	*/
	String  FINANCE_TRACKING_BANK_STATEMENT_LINK_ID = FINANCE_TRACKING_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: FinanceTracking_bankStatementLink_bankStatement_id
	* Hibernate value: FinanceTracking.bankStatementLink.bankStatement.id
	*/
	String  FINANCE_TRACKING_BANK_STATEMENT_LINK_BANK_STATEMENT_ID = FINANCE_TRACKING_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: FinanceTracking_description
	* Hibernate value: FinanceTracking.description
	*/
	String  FINANCE_TRACKING_DESCRIPTION = FINANCE_TRACKING_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: FinanceTracking_id
	* Hibernate value: FinanceTracking.id
	*/
	String  FINANCE_TRACKING_ID = FINANCE_TRACKING_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: FinanceTracking_payMethodTypeDetail_id
	* Hibernate value: FinanceTracking.payMethodTypeDetail.id
	*/
	String  FINANCE_TRACKING_PAY_METHOD_TYPE_DETAIL_ID = FINANCE_TRACKING_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: FinanceTracking_recorded
	* Hibernate value: FinanceTracking.recorded
	*/
	String  FINANCE_TRACKING_RECORDED = FINANCE_TRACKING_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: FinanceTracking_registryBank_id
	* Hibernate value: FinanceTracking.registryBank.id
	*/
	String  FINANCE_TRACKING_REGISTRY_BANK_ID = FINANCE_TRACKING_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: FinanceTracking_trackingDate
	* Hibernate value: FinanceTracking.trackingDate
	*/
	String  FINANCE_TRACKING_TRACKING_DATE = FINANCE_TRACKING_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: FinanceTracking_type
	* Hibernate value: FinanceTracking.type
	*/
	String  FINANCE_TRACKING_TYPE = FINANCE_TRACKING_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: FinanceTracking_finance_amount
	* Hibernate value: FinanceTracking.finance.amount
	*/
	String  FINANCE_TRACKING_FINANCE_AMOUNT = FINANCE_TRACKING_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: FinanceTracking_finance_bankAccount
	* Hibernate value: FinanceTracking.finance.bankAccount
	*/
	String  FINANCE_TRACKING_FINANCE_BANK_ACCOUNT = FINANCE_TRACKING_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: FinanceTracking_finance_bank_id
	* Hibernate value: FinanceTracking.finance.bank.id
	*/
	String  FINANCE_TRACKING_FINANCE_BANK_ID = FINANCE_TRACKING_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: FinanceTracking_finance_bank_code
	* Hibernate value: FinanceTracking.finance.bank.code
	*/
	String  FINANCE_TRACKING_FINANCE_BANK_CODE = FINANCE_TRACKING_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: FinanceTracking_finance_concept
	* Hibernate value: FinanceTracking.finance.concept
	*/
	String  FINANCE_TRACKING_FINANCE_CONCEPT = FINANCE_TRACKING_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: FinanceTracking_finance_dueDate
	* Hibernate value: FinanceTracking.finance.dueDate
	*/
	String  FINANCE_TRACKING_FINANCE_DUE_DATE = FINANCE_TRACKING_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: FinanceTracking_finance_expenses
	* Hibernate value: FinanceTracking.finance.expenses
	*/
	String  FINANCE_TRACKING_FINANCE_EXPENSES = FINANCE_TRACKING_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: FinanceTracking_finance_financeStatus
	* Hibernate value: FinanceTracking.finance.financeStatus
	*/
	String  FINANCE_TRACKING_FINANCE_FINANCE_STATUS = FINANCE_TRACKING_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: FinanceTracking_finance_id
	* Hibernate value: FinanceTracking.finance.id
	*/
	String  FINANCE_TRACKING_FINANCE_ID = FINANCE_TRACKING_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: FinanceTracking_finance_invoice_id
	* Hibernate value: FinanceTracking.finance.invoice<id
	*/
	String  FINANCE_TRACKING_FINANCE_INVOICE_ID = FINANCE_TRACKING_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: FinanceTracking_finance_payMethod_id
	* Hibernate value: FinanceTracking.finance.payMethod.id
	*/
	String  FINANCE_TRACKING_FINANCE_PAY_METHOD_ID = FINANCE_TRACKING_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: FinanceTracking_finance_payMethod_type
	* Hibernate value: FinanceTracking.finance.payMethod.type
	*/
	String  FINANCE_TRACKING_FINANCE_PAY_METHOD_TYPE = FINANCE_TRACKING_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: FinanceTracking_finance_payment
	* Hibernate value: FinanceTracking.finance.payment
	*/
	String  FINANCE_TRACKING_FINANCE_PAYMENT = FINANCE_TRACKING_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: FinanceTracking_finance_registryName
	* Hibernate value: FinanceTracking.finance.registryName
	*/
	String  FINANCE_TRACKING_FINANCE_REGISTRY_NAME = FINANCE_TRACKING_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: FinanceTracking_finance_registry_id
	* Hibernate value: FinanceTracking.finance.registry.id
	*/
	String  FINANCE_TRACKING_FINANCE_REGISTRY_ID = FINANCE_TRACKING_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: FinanceTracking_securityLevel
	* Hibernate value: FinanceTracking.finance.securityLevel
	*/
	String  FINANCE_TRACKING_SECURITY_LEVEL = FINANCE_TRACKING_ENTRY.getAliasNames()[26];

	/** 
	* Alias value: FinanceTracking_finance_invoice_referenceCode
	* Hibernate value: FinanceTracking.finance.invoice<referenceCode
	*/
	String  FINANCE_TRACKING_FINANCE_INVOICE_REFERENCE_CODE = FINANCE_TRACKING_ENTRY.getAliasNames()[27];

	/** 
	* Alias value: FinanceTracking_finance_scope_id
	* Hibernate value: FinanceTracking.finance.scope.id
	*/
	String  FINANCE_TRACKING_FINANCE_SCOPE_ID = FINANCE_TRACKING_ENTRY.getAliasNames()[28];



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
	* Alias value: InvoicingGroup_parent_name
	* Hibernate value: InvoicingGroup.parent.name
	*/
	String  INVOICING_GROUP_PARENT_NAME = INVOICING_GROUP_ENTRY.getAliasNames()[2];



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
	* Alias value: InvoicingGroupDetail_child_name
	* Hibernate value: InvoicingGroupDetail.child.name
	*/
	String  INVOICING_GROUP_DETAIL_CHILD_NAME = INVOICING_GROUP_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: InvoicingGroupDetail_grouped
	* Hibernate value: InvoicingGroupDetail.grouped
	*/
	String  INVOICING_GROUP_DETAIL_GROUPED = INVOICING_GROUP_DETAIL_ENTRY.getAliasNames()[4];


}