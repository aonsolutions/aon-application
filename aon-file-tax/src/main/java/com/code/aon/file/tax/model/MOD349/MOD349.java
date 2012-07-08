package com.code.aon.file.tax.model.MOD349;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.code.aon.file.format.core.DiskRegisterLoader;
import com.code.aon.file.format.model.AbstractFileFiller;
import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.tax.model.MOD349.check.CheckDeponent;
import com.code.aon.file.tax.model.MOD349.check.CheckOperator;
import com.code.aon.file.tax.model.MOD349.check.CheckRectification;
import com.code.aon.file.tax.model.MOD349.data.Deponent;
import com.code.aon.file.tax.model.MOD349.data.Operator;
import com.code.aon.file.tax.model.MOD349.data.Rectification;

public class MOD349  extends AbstractFileFiller{

	private static String DEPONENT = "DEPONENT";
	private static String OPERATOR = "OPERATOR";
	private static String RECTIFICATION = "RECTIFICATION";

	private Deponent deponent;
	
	public MOD349(Deponent deponent, MOD349Format format,String filePath) throws FileNotFoundException, UnsupportedEncodingException {
		super(filePath);
		if (deponent == null)  {
			throw new IllegalArgumentException("Deponent can not be null!");
		}
		if (format == null)  {
			throw new IllegalArgumentException("Format can not be null!");
		}
		
		this.deponent = deponent;
		
		InputStream input = MOD349.class.getResourceAsStream(format.getDeponentMetadataResource());
		DiskRegisterLoader.load(input, manager);

		input = MOD349.class.getResourceAsStream(format.getOperatorMetadataResource());
		DiskRegisterLoader.load(input, manager);

		input = MOD349.class.getResourceAsStream(format.getRectificationMetadataResource());
		DiskRegisterLoader.load(input, manager);
	}

	public ArrayList<Exception> create() {
		try{
			Map<String,Object> properties = new HashMap<String,Object>();
			properties.put(MOD349.DEPONENT, deponent);
			
			if (CheckDeponent.parse(deponent,exceptions)==false) {
				throw new Fd0Exception( "ERROR: " + deponent.toString(), deponent.toString());
			}
			
			createLine("DEPONENT",properties);

			for (Operator operator: deponent.getOperators()){
				properties.put(MOD349.OPERATOR, operator);
				try{
					if (!CheckOperator.parse(operator,exceptions)) {
						throw new Fd0Exception( "ERROR: " + operator.toString(),operator.toString());
					}
					createLine("OPERATOR",properties);
				} catch (Exception ex) {
					if ( ex instanceof Fd0Exception ) {
						exceptions.add (ex);
					} 
					else {
						Fd0Exception e = new Fd0Exception( ex.getMessage(),operator.toString());
						exceptions.add (e);
					}
				}
			}

			for (Rectification rectification: deponent.getRectifications()){
				properties.put(MOD349.RECTIFICATION, rectification);
				try{
					if (CheckRectification.parse(rectification,exceptions)==false) {
						throw new Fd0Exception( "ERROR: ",rectification.toString());
					}
					createLine("Rectification",properties);
				} catch (Exception ex) {
					if ( ex instanceof Fd0Exception ) {
						exceptions.add (ex);
					} 
					else {
						Fd0Exception e = new Fd0Exception( ex.getMessage(),rectification.toString());
						exceptions.add (e);
					}
				}
			}

		} catch (Exception ex) {
			if ( ex instanceof Fd0Exception ) {
				exceptions.add (ex);
			} 
			else {
				Fd0Exception e = new Fd0Exception( ex.getMessage(),deponent.toString());
				exceptions.add (e);
			}
		}
		output.flush();
		writeErrorsFile();
		return exceptions;
	}
	
}
