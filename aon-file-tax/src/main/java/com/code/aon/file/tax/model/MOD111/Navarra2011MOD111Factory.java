package com.code.aon.file.tax.model.MOD111;


import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.code.aon.file.format.core.DiskRegisterLoader;
import com.code.aon.file.format.model.AbstractFileFiller;
import com.code.aon.file.format.model.Fd0Exception;

public class Navarra2011MOD111Factory implements IMOD111Factory {

	@Override
	public List<Exception> createDocument(List<Declaration> declarations, Writer out) {
		PrintWriter pw = (out instanceof PrintWriter)?(PrintWriter) out: new PrintWriter(out);
		List<Exception> exceptions = new LinkedList<Exception>();
		try {
			Navarra2014MOD111 fileFiller = new Navarra2014MOD111(declarations,pw);
			exceptions.addAll(fileFiller.create());
			pw.flush();
		} catch (FileNotFoundException e) {
			exceptions.add(e);
		} catch (UnsupportedEncodingException e) {
			exceptions.add(e);
		}
		return exceptions;
	}
	
	private class Navarra2014MOD111 extends AbstractFileFiller {
		private static final String DECLARATION = "Declaration";
		private static final String DECLARATION1 = "Declaration1";
		private static final String DECLARATION1_METADATA = "/com/code/aon/file/tax/model/MOD111/2014_NAVARRA_Declaration1.xml";
		private static final String DECLARATION2 = "Declaration2";
		private static final String DECLARATION2_METADATA = "/com/code/aon/file/tax/model/MOD111/2014_NAVARRA_Declaration2.xml";
		private static final String DECLARATION3 = "Declaration3";
		private static final String DECLARATION3_METADATA = "/com/code/aon/file/tax/model/MOD111/2014_NAVARRA_Declaration3.xml";
		
		
		private List<Declaration> declarations;
		
		public Navarra2014MOD111(List<Declaration> declarations, PrintWriter out) throws FileNotFoundException, UnsupportedEncodingException {
			super(out);
			if (declarations == null || declarations.size() == 0)  {
				throw new IllegalArgumentException("Declaration can not be null!");
			}
			this.declarations = declarations;
			DiskRegisterLoader.load(MOD111.class.getResourceAsStream(DECLARATION1_METADATA), manager);
			DiskRegisterLoader.load(MOD111.class.getResourceAsStream(DECLARATION2_METADATA), manager);
			DiskRegisterLoader.load(MOD111.class.getResourceAsStream(DECLARATION3_METADATA), manager);
		}
		
		public ArrayList<Exception> create() {
			Map<String,Object> properties = new HashMap<String,Object>();
			try {
				for (Declaration declaration : declarations) {
					properties.put(DECLARATION, declaration);
					createLine(DECLARATION1,properties);
					createLine(DECLARATION2,properties);
					createLine(DECLARATION3,properties);
				}
				getOutput().flush();
			} catch (Exception ex) {
				if ( ex instanceof Fd0Exception ) {
					getExceptions().add (ex);
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
