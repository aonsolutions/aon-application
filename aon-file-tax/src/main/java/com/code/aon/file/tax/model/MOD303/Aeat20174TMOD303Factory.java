package com.code.aon.file.tax.model.MOD303;

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
import com.code.aon.file.tax.model.MOD303.Breakdown;
import com.code.aon.file.tax.model.MOD303.Declaration;

public class Aeat20174TMOD303Factory implements IMOD303Factory {
	
	@Override
	public List<Exception> createDocument(List<Declaration> declarations, Writer out) {
		PrintWriter pw = (out instanceof PrintWriter)?(PrintWriter) out: new PrintWriter(out);
		List<Exception> exceptions = new LinkedList<Exception>();
		try {
			Aeat2014MOD303 fileFiller = new Aeat2014MOD303(declarations,pw);
			exceptions.addAll(fileFiller.create());
			pw.flush();
		} catch (FileNotFoundException e) {
			exceptions.add(e);
		} catch (UnsupportedEncodingException e) {
			exceptions.add(e);
		}
		return exceptions;
	}
	
	private class Aeat2014MOD303 extends AbstractFileFiller {
		private static final String DECLARATION = "dec";
		private static final String GENERAL_REGIME = "gr";
		private static final String SIMPLIFIED_REGIME = "sr";
		
		private static final String LINE0 = "Line0";
		private static final String LINE1 = "Line1";
		private static final String LINE2 = "Line2";
		private static final String LINE3 = "Line3";
		private static final String LINE4 = "Line4";
		private static final String LINE0_METADATA = "/com/code/aon/file/tax/model/MOD303/2017_AEAT_LINE0.xml";
		private static final String LINE1_METADATA = "/com/code/aon/file/tax/model/MOD303/2017_AEAT_LINE1.xml";
		private static final String LINE2_METADATA = "/com/code/aon/file/tax/model/MOD303/2017_AEAT_LINE2.xml";
		private static final String LINE3_METADATA = "/com/code/aon/file/tax/model/MOD303/2017_AEAT_LINE3.xml";
		private static final String LINE4_METADATA = "/com/code/aon/file/tax/model/MOD303/2017_AEAT_LINE4.xml";
		
		private List<Declaration> declarations;
		
		public Aeat2014MOD303(List<Declaration> declarations, PrintWriter out) throws FileNotFoundException, UnsupportedEncodingException {
			super(out);
			if (declarations == null || declarations.size() == 0)  {
				throw new IllegalArgumentException("Declaration can not be null!");
			}
			this.declarations = declarations;
			InputStream input = MOD303.class.getResourceAsStream(LINE0_METADATA);
			DiskRegisterLoader.load(input, manager);
			
			input = MOD303.class.getResourceAsStream(LINE1_METADATA);
			DiskRegisterLoader.load(input, manager);
			
			input = MOD303.class.getResourceAsStream(LINE2_METADATA);
			DiskRegisterLoader.load(input, manager);
			
			input = MOD303.class.getResourceAsStream(LINE3_METADATA);
			DiskRegisterLoader.load(input, manager);


			input = MOD303.class.getResourceAsStream(LINE4_METADATA);
			DiskRegisterLoader.load(input, manager);

		}
		
		public ArrayList<Exception> create() {
			Map<String,Object> properties = new HashMap<String,Object>();
			try {
				
				for (Declaration declaration : declarations) {
					properties.put(DECLARATION, declaration);
					createLine(LINE0,properties);
					if (declaration.getGeneralRegime() != null) {
						GeneralRegime gr = declaration.getGeneralRegime();
						ensureGeneralRegime(gr);
						properties.put(GENERAL_REGIME, declaration.getGeneralRegime());
						createLine(LINE1,properties);
					} else {
						if (declaration.getSimplifiedRegime() != null) {
							declaration.setGeneralRegime(new GeneralRegime());
							ensureGeneralRegime(declaration.getGeneralRegime());
							properties.put(GENERAL_REGIME, declaration.getGeneralRegime());
							properties.put(SIMPLIFIED_REGIME, declaration.getSimplifiedRegime());
							createLine(LINE1,properties);
							createLine(LINE2,properties);
						}
					}
					createLine(LINE3,properties);
					createLine(LINE4,properties);

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

		private void ensureGeneralRegime(GeneralRegime gr) {
			// Nos aseguramos de que vayan las claves de IVA que se requieren en la presentacion
			String[] ensuredKeys = new String[]{"21.0","10.0","4.0"};
			for (String ensureKey : ensuredKeys) {
				if (!gr.getOutputVat().containsKey(ensureKey)) {;
				gr.getOutputVat().put(ensureKey, new Breakdown());
				}
			}
			for (String ensureKey : ensuredKeys) {
				if (!gr.getOutputVatInvPasive().containsKey(ensureKey)) {;
				gr.getOutputVatInvPasive().put(ensureKey, new Breakdown());
				}
			}
			ensuredKeys = new String[]{"5.2","1.4","0.5"};
			for (String ensureKey : ensuredKeys) {
				if (!gr.getSurcharge().containsKey(ensureKey)) {;
					gr.getSurcharge().put(ensureKey, new Breakdown());
				}
			}
		}
	}
}
	
	