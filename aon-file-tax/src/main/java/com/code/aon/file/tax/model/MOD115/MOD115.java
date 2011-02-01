package com.code.aon.file.tax.model.MOD115;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.code.aon.file.format.core.DiskRegisterLoader;
import com.code.aon.file.format.model.AbstractFileFiller;
import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.tax.model.MOD115.check.CheckDeclaration;
import com.code.aon.file.tax.model.MOD115.data.Declaration;

public class MOD115 extends AbstractFileFiller{

	private static String DECLARATION = "DECLARATION";
	
	private Declaration declaration;
	
	public MOD115(Declaration declaration, MOD115Format format,String filePath) throws FileNotFoundException, UnsupportedEncodingException {
		super(filePath);
		if (declaration == null)  {
			throw new IllegalArgumentException("Declaration can not be null!");
		}
		if (format == null)  {
			throw new IllegalArgumentException("Format can not be null!");
		}
		
		this.declaration = declaration;
		
		InputStream input = MOD115.class.getResourceAsStream(format.getDeclarationMetadataResource());
		DiskRegisterLoader.load(input, manager);
	}

	public ArrayList<Exception> create() {
		try{
			Map<String,Object> properties = new HashMap<String,Object>();
			properties.put(MOD115.DECLARATION, declaration);
			
			if (CheckDeclaration.parse(declaration,exceptions)==false) {
				throw new Fd0Exception( "ABORTED: ",declaration.toString());
			}
			
			createLine("Declaration",properties);

		} catch (Exception ex) {
			if ( ex instanceof Fd0Exception ) {
				exceptions.add (ex);
			} 
			else {
				Fd0Exception e = new Fd0Exception( ex.getMessage(),declaration.toString());
				exceptions.add (e);
			}
		}
		output.flush();
		writeErrorsFile();
		return exceptions;
	}
}
