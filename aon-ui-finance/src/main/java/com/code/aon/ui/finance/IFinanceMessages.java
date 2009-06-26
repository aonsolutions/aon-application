package com.code.aon.ui.finance;

public interface IFinanceMessages {

	String BUNDLE_KEY = "financeBundle";
	
	String VALIDATE_FINANCES_GENERATION_ERROR_KEY = "finance_validate_finances_generation_error";
	String GENERATE_FINANCES_ERROR_KEY = "finance_generate_finances_error";
	String EMPTY_INVOICE_LIST_ERROR_KEY = "finance_empty_invoice_list_error";
	String INVALID_INVOICING_GROUP_PARENT_KEY = "finance_invalid_invoicing_group_parent";
	String INVALID_INVOICING_GROUP_CHILD_KEY = "finance_invalid_invoicing_group_child";
	String INVALID_INVOICING_GROUP_DETAIL_PARENT_KEY = "finance_invalid_invoicing_group_detail_parent";
	String INVALID_INVOICING_GROUP_DETAIL_CHILD_KEY = "finance_invalid_invoicing_group_detail_child";
	String UNABLE_RECORD_INACCURACY_ERROR_KEY = "finance_unable_record_inaccuracy_error";
	String PAYMENT_INVALID_AMOUNT_ERROR = "finance_payment_invalid_amount_error";
	String FINANCE_TRACKING_FRACTIONED = "finance_tracking_fractioned";
	String FINANCE_TRACKING_SETTLED = "finance_tracking_settled";
	String FINANCE_TRACKING_BATCHED = "finance_tracking_batched";
	String FINANCE_TRACKING_RECORDED = "finance_tracking_recorded";
	String FINANCE_BATCH_DATE_ERROR = "finance_batch_date_error";
	String FINANCE_BATCH_DISK_ERROR = "finance_batch_disk_error";
	String FINANCE_BATCH_UNRECORD_ERROR = "finance_batch_unrecord_error";
}
