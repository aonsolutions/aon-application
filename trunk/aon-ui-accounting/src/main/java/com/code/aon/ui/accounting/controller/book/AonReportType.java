package com.code.aon.ui.accounting.controller.book;

public enum AonReportType {
	COVER("cover"),
	JOURNAL("journalBook"),
	LEDGER("ledgerBook"),
	TRIAL("officialTrialBalance"),
	VAT("vatBook"),
	BALANCE("officialBalance"),
	OTHER(null);

	private String reportKey;
	
	private AonReportType( String reportKey) {
		this.reportKey = reportKey;
	}
	
	public String getReportKey() {
		return reportKey;
	}
}
