package com.code.aon.file.tax.model.MOD111;


import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.tax.model.MOD111.data.Declaration;

public class MOD111 {
	
	public List<Exception> create(List<Declaration> declarations, MOD111Format format,PrintWriter writer) {
		if (declarations == null || declarations.size() == 0 )  {
			throw new IllegalArgumentException("Declarations can not be null!");
		}
		if (format == null)  {
			throw new IllegalArgumentException("Format can not be null!");
		}
		List<Exception> exceptions = new LinkedList<Exception>();
		try{
			MOD111FactoryManager factoryManger = MOD111FactoryManager.getInstance();
			IMOD111Factory factory = factoryManger.getFactory(format);
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
