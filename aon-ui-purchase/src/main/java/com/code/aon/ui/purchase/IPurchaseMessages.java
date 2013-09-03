package com.code.aon.ui.purchase;


public interface IPurchaseMessages {

	/** ResourceBundle name. */
	String BUNDLE_KEY = "purchaseBundle";

	String PURCHASE_EMAIL_SUBJECT = "purchase_email_subject";
	String PURCHASE_EMAIL_BODY_HEADER = "purchase_email_body_header";
	String PURCHASE_EMAIL_BODY = "purchase_email_body";
	String PURCHASE_SOURCE = "purchase_source";
	
	String PURCHASE_RETURN_OVER_MSG = "purchase_return_over";
	String PURCHASE_RETURNED_IN_MSG = "purchase_returned_in";	
	
	String PURCHASE_WITHOUT_EMAIL = "purchase_without_email";
	String PURCHASE_SEND_EMAIL = "purchase_send_email";
	String PURCHASE_SEND_EMAIL_ERROR = "purchase_send_email_error";
	String PURCHASE_SEND_EMAIL_FNINISH = "purchase_send_email_finish";
	String PURCHASE_SEND_EMAIL_NUMBER = "purchase_send_email_number";
	String PURCHASE_SEND_EMAIL_SENDED_COUNT = "purchase_send_email_sendedCount"; 
	String PURCHASE_SEND_EMAIL_ERROR_COUNT = "purchase_send_email_errorCount";
	
	String PURCHASE_TRANSFERED_TO = "purchase_transfered_to";
	String PURCHASE_TO_INCOME = "purchase_to_income";
	String PURCHASE_DETAIL_LINE = "purchase_detail_line";
	String PURCHASE_DETAIL_UNITS = "purchase_detail_units";

	String PURCHASE_PROPOSAL_ORDER = "purchase_proposal_order";
	String PURCHASE_PROPOSAL_TRANSFERENCE = "purchase_proposal_transference";
	String PURCHASE_PROPOSAL_ITEM_RETURN = "purchase_proposal_item_return";
	
}

