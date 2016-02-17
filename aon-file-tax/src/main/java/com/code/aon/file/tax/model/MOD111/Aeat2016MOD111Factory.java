package com.code.aon.file.tax.model.MOD111;


import java.io.FileNotFoundException;
import java.io.InputStream;
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

public class Aeat2016MOD111Factory implements IMOD111Factory {

	@Override
	public List<Exception> createDocument(List<Declaration> declarations, Writer out) {
		PrintWriter pw = (out instanceof PrintWriter)?(PrintWriter) out: new PrintWriter(out);
		List<Exception> exceptions = new LinkedList<Exception>();
		try {
			Aeat2016MOD111 fileFiller = new Aeat2016MOD111(declarations,pw);
			exceptions.addAll(fileFiller.create());
			pw.flush();
		} catch (FileNotFoundException e) {
			exceptions.add(e);
		} catch (UnsupportedEncodingException e) {
			exceptions.add(e);
		}
		return exceptions;
	}
	
	private class Aeat2016MOD111 extends AbstractFileFiller {
		private static final String DECLARATION = "Declaration";
		private static final String DECLARATION_METADATA = "/com/code/aon/file/tax/model/MOD111/2016_AEAT_Declaration.xml";
		
		
		private List<Declaration> declarations;
		
		public Aeat2016MOD111(List<Declaration> declarations, PrintWriter out) throws FileNotFoundException, UnsupportedEncodingException {
			super(out);
			if (declarations == null || declarations.size() == 0)  {
				throw new IllegalArgumentException("Declaration can not be null!");
			}
			this.declarations = declarations;
			InputStream input = MOD111.class.getResourceAsStream(DECLARATION_METADATA);
			DiskRegisterLoader.load(input, manager);
		}
		
		public ArrayList<Exception> create() {
			Map<String,Object> properties = new HashMap<String,Object>();
			try {
				for (Declaration declaration : declarations) {
					properties.put(DECLARATION, declaration);
					createLine(DECLARATION,properties);
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
