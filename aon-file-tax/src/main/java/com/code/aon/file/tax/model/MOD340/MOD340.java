package com.code.aon.file.tax.model.MOD340;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.code.aon.file.format.core.DiskRegisterLoader;
import com.code.aon.file.format.model.AbstractFileFiller;

public class MOD340  extends AbstractFileFiller{

	private static String DEPONENT = "DEPONENT";
	private static String ISSUED = "ISSUED";
	private static String RECEIVED = "RECEIVED";
	private static String INVESTMENT = "INVESTMENT";
	private static String INTRACOMMUNITARY = "INTRACOMMUNITARY";
	
	private IMOD340Provider provider;
	
	public MOD340(IMOD340Provider provider, PrintWriter writer) throws FileNotFoundException, UnsupportedEncodingException {
		super(writer);
		if (provider == null)  {
			throw new IllegalArgumentException("provider can not be null!");
		}
		this.provider = provider;
		
		MOD340Format format = provider.getFormat();
		InputStream input = MOD340.class.getResourceAsStream(format.getDeponentMetadataResource());
		DiskRegisterLoader.load(input, manager);

		input = MOD340.class.getResourceAsStream(format.getIssuedMetadataResource());
		DiskRegisterLoader.load(input, manager);

		input = MOD340.class.getResourceAsStream(format.getReceivedMetadataResource());
		DiskRegisterLoader.load(input, manager);

		input = MOD340.class.getResourceAsStream(format.getInvestmentMetadataResource());
		DiskRegisterLoader.load(input, manager);

		input = MOD340.class.getResourceAsStream(format.getIntracommunitaryMetadataResource());
		DiskRegisterLoader.load(input, manager);

	}

	public ArrayList<Exception> create() {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(MOD340.DEPONENT, provider.getDeponent());
		createLine("Deponent", properties);
		fillIssuedInvoices(properties,provider);
		fillReceivedInvoices(properties,provider);
		fillInvestmentInvoices(properties,provider);
		fillIntracommunitaryInvoices(properties,provider);
		output.flush();
		return exceptions;
	}

	private void fillIssuedInvoices(Map<String, Object> properties,IMOD340Provider provider) {
		provider.initializeIssuedInvoices();
		while (provider.hasNextIssuedInvoice()) {
			properties.put(MOD340.ISSUED, provider.getNextIssueInvoice());
			createLine("IssuedInvoice", properties);
		}
		provider.finalizeIssuedInvoices();
	}

	private void fillReceivedInvoices(Map<String, Object> properties,IMOD340Provider provider) {
		provider.initializeReceivedInvoices();
		while (provider.hasNextReceivedInvoice()) {
			properties.put(MOD340.RECEIVED, provider.getNextReceivedInvoice());
			createLine("ReceivedInvoice", properties);
		}
		provider.finalizeReceivedInvoices();
	}

	private void fillInvestmentInvoices(Map<String, Object> properties,IMOD340Provider provider) {
		provider.initializeInvestmentInvoices();
		while (provider.hasNextInvestmentInvoice()) {
			properties.put(MOD340.INVESTMENT, provider.getNextInvestmentInvoice());
			createLine("InvestmentInvoice", properties);
		}
		provider.finalizeInvestmentInvoices();
	}

	private void fillIntracommunitaryInvoices(Map<String, Object> properties,IMOD340Provider provider) {
		provider.initializeIntracommunitaryInvoices();
		while (provider.hasNextIntracommunitaryInvoice()) {
			properties.put(MOD340.INTRACOMMUNITARY, provider.getNextIntracommunitaryInvoice());
			createLine("IntracommunitaryInvoice", properties);
		}
		provider.finalizeIntracommunitaryInvoices();
	}

}
