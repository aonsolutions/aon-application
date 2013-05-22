package com.code.aon.ui.finance;

public interface IFinanceMessages {

	String BUNDLE_KEY = "financeBundle";
	
	String VALIDATE_FINANCES_GENERATION_ERROR_KEY = "finance_validate_finances_generation_error";
	String GENERATE_FINANCES_ERROR_KEY = "finance_generate_finances_error";
	String CALCULATE_FINANCES_AMOUNT_ERROR_KEY = "finance_calculate_finances_amount_error";
	String EMPTY_INVOICE_LIST_ERROR_KEY = "finance_empty_invoice_list_error";
	String UNABLE_RECORD_INACCURACY_ERROR_KEY = "finance_unable_record_inaccuracy_error";
	String UNABLE_RECORD_NO_AMORTIZATION_ERROR_KEY = "finance_unable_record_no_amortization_error";
	String PAYMENT_INVALID_AMOUNT_ERROR = "finance_payment_invalid_amount_error";
	String PAYMENT_NOT_MATCH_AMOUNT_ERROR = "finance_payment_not_match_amount_error";
	String PAYMENT_PAY_METHOD_UNDEFINED_ERROR = "finance_payment_pay_method_undefined_error";
	String FINANCE_TRACKING_FRACTIONED = "finance_tracking_fractioned";
	String FINANCE_TRACKING_GROUPED = "finance_tracking_grouped";
	String FINANCE_TRACKING_PAYMENT_PRINT = "finance_tracking_payment_print";
	String FINANCE_TRACKING_SETTLED = "finance_tracking_settled";
	String FINANCE_TRACKING_BATCHED = "finance_tracking_batched";
	String FINANCE_TRACKING_RECORDED = "finance_tracking_recorded";
	String FINANCE_BATCH_DATE_ERROR = "finance_batch_date_error";
	String FINANCE_BATCH_DISK_ERROR = "finance_batch_disk_error";
	String FINANCE_BATCH_UNRECORD_ERROR = "finance_batch_unrecord_error";
	String NO_INVOICE_KEY = "finance_invoicing_no_invoice";
	String FINANCE_INVOICE_EMAIL_SUBJECT = "finance_invoice_email_subject";
	String FINANCE_EINVOICE_EMAIL_SUBJECT = "finance_einvoice_email_subject";
	String FINANCE_INVOICE_EMAIL_BODY = "finance_invoice_email_body";
	String FINANCE_INVOICE_WITHOUT_EMAIL = "finance_invoice_without_email";
	String FINANCE_INVOICE_SEND_EMAIL = "finance_invoice_send_email";
	String FINANCE_INVOICE_SEND_EMAIL_ERROR = "finance_invoice_send_email_error";
	String FINANCE_INVOICE_SEND_EMAIL_FNINISH = "finance_invoice_send_email_finish";
	String FINANCE_CASH = "finance_cash";
	String FINANCE_PENDING = "finance_pending";
	String FINANCE_INVOICE_CHECKING_MODULE_NO_FINANCE = "finance_invoice_checking_module_no_finance";	
	String FINANCE_INVOICE_CHECKING_MODULE_WRONG_FINANCE = "finance_invoice_checking_module_wrong_finance";
	String FINANCE_INVOICE_INTEGRITY_NO_RESULT = "finance_integrity_no_result";
	String FINANCE_IMPORT_BANK_ACCOUNT_NOT_FOUND = "finance_import_bank_account_not_found_message";
	String FINANCE_CHECK_NO_LINE_SELECTED = "finance_check_no_line_selected_message";
	String FINANCE_UNRECORD_INVOICE_WARNING ="finance_unrecord_invoice_warning";
	String FINANCE_EXPENSE_INVOICE_QUOTA_WARNING = "finance_expense_invoice_quota_warning";
	String FINANCE_EXPENSE_INVOICE_CHECK_WARNING = "finance_expense_invoice_check_warning";
	String FINANCE_CUSTOMER_REQUIRED_ERROR = "finance_customer_required_error";
	String FINANCE_INVOICE_OFFER = "finance_invoice_offer";
	String FINANCE_INVOICE_SALES = "finance_invoice_sales";
	String FINANCE_INVOICE_DELIVERY = "finance_invoice_delivery";
	String FINANCE_POS_OPENED = "finance_pos_opened";


}

