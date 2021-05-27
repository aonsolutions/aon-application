package com.code.aon.file.tax.model.MOD340;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.aon.file.format.core.DiskRegisterLoader;
import com.code.aon.file.format.model.AbstractFileFiller;
import com.code.aon.file.tax.model.MOD340.data.Deponent;
import com.code.aon.file.tax.model.MOD340.data.Invoice;

public class MOD340  extends AbstractFileFiller{

	public static String DEPONENT = "DEPONENT";
	public static String ISSUED = "ISSUED";
	public static String RECEIVED = "RECEIVED";
	public static String INVESTMENT = "INVESTMENT";
	public static String INTRACOMMUNITARY = "INTRACOMMUNITARY";
	
	private Deponent deponent;
	private List<Invoice> invoices;
	
	public MOD340(MOD340Format format, PrintWriter writer, Deponent deponent, List<Invoice> invoices ) throws FileNotFoundException, UnsupportedEncodingException {
		super(writer);
		if (format == null)  {
			throw new IllegalArgumentException("Format can not be null!");
		}
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
		
		this.deponent = deponent; 	
		this.invoices = invoices;

	}

	private ArrayList<Exception> createDeponent( Deponent deponent) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(MOD340.DEPONENT, deponent );
		createLine(MOD340.DEPONENT, properties);
		return exceptions;
	}
	
	private ArrayList<Exception> createInvoice ( Invoice invoice ) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(invoice.getType(), invoice);
		createLine(invoice.getType(), properties);
		return exceptions;
	}

	@Override
	public ArrayList<Exception> create() {
		ArrayList<Exception> ex = createDeponent(this.deponent);
		for (Invoice invoice : invoices ) {
			ex.addAll( createInvoice(invoice));
		}
		output.flush();
		return ex;
	}

}
