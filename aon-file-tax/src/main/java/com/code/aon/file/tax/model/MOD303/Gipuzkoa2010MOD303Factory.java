package com.code.aon.file.tax.model.MOD303;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.code.aon.file.format.core.DiskRegisterLoader;
import com.code.aon.file.format.model.AbstractFileFiller;
import com.code.aon.file.format.model.Fd0Exception;

public class Gipuzkoa2010MOD303Factory implements IMOD303Factory {

	@Override
	public List<Exception> createDocument(List<Declaration> declarations, Writer out) {
		PrintWriter pw = (out instanceof PrintWriter)?(PrintWriter) out: new PrintWriter(out);
		List<Exception> exceptions = new LinkedList<Exception>();
		try {
			Gipuzkoa2010MOD303 fileFiller = new Gipuzkoa2010MOD303(declarations,pw);
			exceptions.addAll(fileFiller.create());
			pw.flush();
		} catch (Throwable e) {
			if (e instanceof Exception) {
				exceptions.add((Exception) e);	
			} else {
				exceptions.add(new UnexpectedException( e.getMessage() ));
			}
			
		}
		return exceptions;
	}
	
	private class Gipuzkoa2010MOD303 extends AbstractFileFiller {
		private static final String DECLARATION = "Declaration";
		private static final String DECLARATION_METADATA = "/com/code/aon/file/tax/model/MOD303/2010_GIPUZKOA_Declaration.xml";
		
		
		private List<Declaration> declarations;
		
		public Gipuzkoa2010MOD303(List<Declaration> declarations, PrintWriter out) throws FileNotFoundException, UnsupportedEncodingException {
			super(out);
			if (declarations == null || declarations.size() == 0)  {
				throw new IllegalArgumentException("Declaration can not be null!");
			}
			this.declarations = declarations;
			InputStream input = MOD303.class.getResourceAsStream(DECLARATION_METADATA);
			DiskRegisterLoader.load(input, manager);
		}
		
		public ArrayList<Exception> create() {
			Map<String,Object> properties = new HashMap<String,Object>();
			try {
				for (Declaration declaration : declarations) {
					if (declaration.getGeneralRegime() != null) {
						GeneralRegime gr = declaration.getGeneralRegime();
						// Nos aseguramos de que vayan las claves de IVA que se requieren en la presentacion
						String[] ensuredKeys = new String[]{"18.0","8.0","4.0","10.0","21.0"};
						for (String ensureKey : ensuredKeys) {
							if (!gr.getOutputVat().containsKey(ensureKey)) {;
							gr.getOutputVat().put(ensureKey, new Breakdown());
							}
						}
						ensuredKeys = new String[]{"4.0","1.0","0.5","5.2","1.4"};
						for (String ensureKey : ensuredKeys) {
							if (!gr.getSurcharge().containsKey(ensureKey)) {;
								gr.getSurcharge().put(ensureKey, new Breakdown());
							}
						}
					}
					// ------------------
					
					properties.put(DECLARATION, declaration);
					createLine(DECLARATION,properties);
				}
				getOutput().flush();
			} catch (Throwable ex) {
				if ( ex instanceof Fd0Exception ) {
					getExceptions().add ((Exception) ex);
				} 
				else {
					ex.printStackTrace();
					Fd0Exception e = new Fd0Exception( ex.getMessage(),"");
					getExceptions().add (e);
				}
			}
			return getExceptions();
		}
	}
}
