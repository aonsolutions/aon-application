package com.code.aon.accounting.mvel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class BalanceTransformer {
	
	private static final String FACTORY_IMPL = "net.sf.saxon.TransformerFactoryImpl";
	private static final String RESOUCES_PACKAGE = "/com/code/aon/accounting/xbrl/transform/";
	private static final String XML2HTML_XSL_RESOURCE = "/com/code/aon/accounting/xbrl/transform/xml2html.xsl";
	private static final String XML2COMPONENTS_XSL_RESOURCE = "/com/code/aon/accounting/xbrl/transform/xml2aoncomponents.xsl";
	private static final String DEFAULT_ENCODING  = "ISO-8859-1";
	private static final String MODULE_PARAM = "module";
	private static final String ERROR_MSG = "No se pudo realizar la transformación del balance.";

	
	public void transformXML2AonComponents(InputStream input, OutputStream output,BalanceSheet sheet) throws  BalanceException {
		transformXML2AonComponents(input,output,sheet,DEFAULT_ENCODING);
	}
	public void transformXML2AonComponents(InputStream input, OutputStream output,BalanceSheet sheet,String encoding) throws  BalanceException {
		transformXML(input, output, sheet, encoding, XML2COMPONENTS_XSL_RESOURCE);	
	}
	
	public void transformXML2HTML(InputStream input, OutputStream output,BalanceSheet sheet) throws  BalanceException {
		transformXML2HTML(input,output,sheet,DEFAULT_ENCODING);
	}
	public void transformXML2HTML(InputStream input, OutputStream output,BalanceSheet sheet,String encoding) throws  BalanceException {
		transformXML(input, output, sheet, encoding, XML2HTML_XSL_RESOURCE);	
	}
	
	private void transformXML(InputStream input, OutputStream output,BalanceSheet sheet,final String encoding,String resource) throws  BalanceException {
		InputStream xslInput = null;
		try {
		    TransformerFactory  transformerFactory = TransformerFactory.newInstance(FACTORY_IMPL,this.getClass().getClassLoader());
		    transformerFactory.setURIResolver( new URIResolver() {
				@Override
				public Source resolve(String href, String base) throws TransformerException {
					InputStream input = BalanceTransformer.class.getResourceAsStream(RESOUCES_PACKAGE + href);
					try {
						InputStreamReader reader = new InputStreamReader(input, encoding);
						return new StreamSource( reader );
					} catch (UnsupportedEncodingException e) {
						return new StreamSource( input );
					}
					
				}
			} );
		    xslInput = BalanceTransformer.class.getResourceAsStream(resource);
			Source xslSource = new StreamSource( xslInput );
			Source inputSource = new StreamSource( input );
			Result outputResult = new StreamResult( output ); 
			Transformer transformer = transformerFactory.newTransformer( xslSource );
			
		    transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
		    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		    transformer.setParameter(MODULE_PARAM, sheet.getModule());
		    transformer.transform( inputSource, outputResult );
		} catch (Throwable e) {
			throw new BalanceException(ERROR_MSG,e);
		} finally {
			if (xslInput != null) {
				try {
					xslInput.close();
				} catch (IOException e) {
					// nothing
				}
			}
		}
	}
	
	public static void main(String[] args) throws Throwable {
		BalanceTransformer transformer = new BalanceTransformer();
		File file = new File("/tmp/pyg.xml");
		FileInputStream in = new FileInputStream(file);
		transformer.transformXML2AonComponents(in, System.out,BalanceSheet.BAL_ABR);
	}
	
}
