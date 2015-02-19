package com.code.aon.file.tax.model.MOD184;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.code.aon.file.format.core.DiskRegisterLoader;
import com.code.aon.file.format.model.AbstractFileFiller;

public class MOD184  extends AbstractFileFiller{

	private static String DEPONENT = "DEPONENT";
	private static String INCOME = "INCOME";
	private static String PARTNER = "PARTNER";

	private Deponent deponent;
	
	public MOD184(Deponent deponent, MOD184Format format, PrintWriter out) throws FileNotFoundException, UnsupportedEncodingException {
		super(out);
		if (deponent == null)  {
			throw new IllegalArgumentException("Deponent can not be null!");
		}
		if (format == null)  {
			throw new IllegalArgumentException("Format can not be null!");
		}
		
		this.deponent = deponent;
		
		InputStream input = MOD184.class.getResourceAsStream(format.getDeponentMetadataResource());
		DiskRegisterLoader.load(input, manager);

		input = MOD184.class.getResourceAsStream(format.getIncomeMetadataResource());
		DiskRegisterLoader.load(input, manager);

		input = MOD184.class.getResourceAsStream(format.getPartnerMetadataResource());
		DiskRegisterLoader.load(input, manager);

	}

	public ArrayList<Exception> create() {
		try{
			Map<String,Object> properties = new HashMap<String,Object>();
			properties.put(MOD184.DEPONENT, deponent);
			
			createLine(MOD184.DEPONENT,properties);
			for (Income income: deponent.getIncomes()){
				properties.put(MOD184.INCOME, income);
				try{
					createLine(MOD184.INCOME,properties);
				} catch (Exception ex) {
					exceptions.add (ex);
				}
			}
			for (Partner partner: deponent.getPartners()){
				properties.put(MOD184.PARTNER, partner);
				try{
					createLine(MOD184.PARTNER,properties);
				} catch (Exception ex) {
					exceptions.add (ex);
				}
			}

		} catch (Exception ex) {
			exceptions.add (ex);
		}
		output.flush();
		//writeErrorsFile();
		return exceptions;
	}

}
