package com.code.aon.file.tax.model.MOD303;


import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.tax.model.MOD303.data.Declaration;

public class MOD303 {

	public List<Exception> create(List<Declaration> declarations, MOD303Format format,PrintWriter writer) {
		if (declarations == null || declarations.size() == 0 )  {
			throw new IllegalArgumentException("Declarations can not be null!");
		}
		if (format == null)  {
			throw new IllegalArgumentException("Format can not be null!");
		}
		List<Exception> exceptions = new LinkedList<Exception>();
		try{
			MOD303FactoryManager factoryManger = MOD303FactoryManager.getInstance();
			IMOD303Factory factory = factoryManger.getFactory(format);
			if (factory == null) {
				throw new IllegalArgumentException("No se encontró un formateador váalido para " + format);
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
