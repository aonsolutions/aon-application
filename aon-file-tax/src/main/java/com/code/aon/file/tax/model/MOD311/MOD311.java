package com.code.aon.file.tax.model.MOD311;


import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.file.format.model.Fd0Exception;

public class MOD311 {
	
	public List<Exception> create(List<Declaration> declarations, MOD311Format format,PrintWriter writer) {
		if (declarations == null || declarations.size() == 0 )  {
			throw new IllegalArgumentException("Declarations can not be null!");
		}
		if (format == null)  {
			throw new IllegalArgumentException("Format can not be null!");
		}
		List<Exception> exceptions = new LinkedList<Exception>();
		try{
			MOD311FactoryManager factoryManger = MOD311FactoryManager.getInstance();
			IMOD311Factory factory = factoryManger.getFactory(format);
			if (factory == null) {
				throw new IllegalArgumentException("No se encontró un formateador válido para " + format);
			}
			exceptions.addAll( factory.createDocument(declarations, writer ) );
		} catch (Exception ex) {
			if ( ex instanceof Fd0Exception ) {
				exceptions.add (ex);
			} 
			else {
				ex.printStackTrace();
				Fd0Exception e = new Fd0Exception( ex.getMessage()," ");
				exceptions.add (e);
			}
		}
		writer.flush();
		return exceptions;
	}
}
