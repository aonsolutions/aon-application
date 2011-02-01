package com.code.aon.file.tax.model.MOD340;

import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.tax.model.MOD340.data.Deponent;
import com.code.aon.file.tax.model.MOD340.data.IntracommunitaryInvoice;
import com.code.aon.file.tax.model.MOD340.data.InvestmentInvoice;
import com.code.aon.file.tax.model.MOD340.data.IssuedInvoice;
import com.code.aon.file.tax.model.MOD340.data.ReceivedInvoice;

public interface IMOD340Provider {
	
	MOD340Format getFormat();
	Deponent getDeponent() throws Fd0Exception;
	
	void initializeIssuedInvoices() throws Fd0Exception;
	boolean hasNextIssuedInvoice() throws Fd0Exception;
	IssuedInvoice getNextIssueInvoice() throws Fd0Exception;
	void finalizeIssuedInvoices() throws Fd0Exception;

	void initializeReceivedInvoices() throws Fd0Exception;
	boolean hasNextReceivedInvoice() throws Fd0Exception;
	ReceivedInvoice getNextReceivedInvoice() throws Fd0Exception;
	void finalizeReceivedInvoices() throws Fd0Exception;
	
	void initializeInvestmentInvoices() throws Fd0Exception;
	boolean hasNextInvestmentInvoice() throws Fd0Exception;
	InvestmentInvoice getNextInvestmentInvoice() throws Fd0Exception;
	void finalizeInvestmentInvoices() throws Fd0Exception;
	
	void initializeIntracommunitaryInvoices() throws Fd0Exception;
	boolean hasNextIntracommunitaryInvoice() throws Fd0Exception;
	IntracommunitaryInvoice getNextIntracommunitaryInvoice() throws Fd0Exception;
	void finalizeIntracommunitaryInvoices() throws Fd0Exception;
	
}
