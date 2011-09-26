package com.esferalia.aon.payroll.irpf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.aeat.jaxb.AEATRetencionesEntrada2011;
import com.aeat.jaxb.AEATRetencionesError2011;
import com.aeat.jaxb.AEATRetencionesSalida2011;
import com.aeat.jaxb.ObjectFactory;
import com.aeat.jaxb.TipoError;
import com.aeat.jaxb.TipoRetenciones;
import com.aeat.jaxb.TipoRetenedorEntrada2011;
import com.aeat.jaxb.TipoRetenedorError2011;
import com.aeat.jaxb.TipoRetenedorSalida2011;
import com.aeat.jaxb.TipoRetenidoError2011;
import com.aeat.jaxb.TipoRetenidoSalida2011;
import com.aeat.modulo.retenciones.ModuloCalculo;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.irpf.sql.SQLAEAT2011Factory;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfRegularizationColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfResultColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;



public abstract class IrpfCalculator  {
	
	public interface CallbackHandler {
		
		public void onError( TipoRetenedorError2011 retenedorError2011, TipoRetenidoError2011 retenidoError2011);
		public void onSalida( TipoRetenedorSalida2011 retenedorSalida2011, TipoRetenidoSalida2011 retenidoSalida2011);
	
	}
	
	// TODO : AEATRetencionesError2011
	public abstract void calculate ( AEATRetencionesEntrada2011 aeatRetencionesEntrada2011, CallbackHandler cb) 
		throws IrpfException, ExpressionException ;
	
	public static IrpfCalculator getCalculator(Administration administration) 
	throws IrpfCalculatorNotFoundException {
		IrpfCalculator calculator = IRPF_CALCULATORS_MAP.get(administration);
		if ( calculator != null ) {
			return calculator;
		}
		else {
			throw new IrpfCalculatorNotFoundException(administration);
		}
	}
	private static final SchemaFactory SCHEMA_FACTORY = 
		SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
	private static final URL SCHEMA_URL = 
		IrpfCalculator.class.getClassLoader().getResource("com/aeat/xml/AEATRetenciones2011.xsd");

	private static Map<Administration, IrpfCalculator> IRPF_CALCULATORS_MAP = 
		new HashMap<Administration, IrpfCalculator>() {
		{
			put(Administration.COMMON_TERRITORY, new AEATIrpfCalculator());
		}
	};
	
	private static class AEATIrpfCalculator extends IrpfCalculator {
		@Override
		public void calculate(
				AEATRetencionesEntrada2011 aeatRetencionesEntrada2011, CallbackHandler cb)
				throws IrpfException, ExpressionException {
			
			File error ;
			File salida;
			File entrada;
			
			try { 
				
				entrada = File.createTempFile("AEATEntrada", ".xml");
				
				FileOutputStream outputStream = new FileOutputStream(entrada);
				
				IrpfCalculator.marshall(aeatRetencionesEntrada2011, outputStream);
				
				outputStream.close();
				
				error = File.createTempFile("AEATError", ".xml");
				salida = File.createTempFile("AEATSalida", ".xml");

				ModuloCalculo.procesarFicheroXml(
						entrada.getAbsolutePath(), 
						error.getAbsolutePath(), 
						null, 
						salida.getAbsolutePath());
				
				if ( error.exists() ) {
					InputStream errorInpuStream = 
						new FileInputStream(error.getAbsolutePath());
					AEATRetencionesError2011 aeatRetencionesError2011  = 
						unmarshal(errorInpuStream);
					List<TipoRetenedorError2011> retenedor = 
						aeatRetencionesError2011.getRetenedor();
					for (TipoRetenedorError2011 retenedorError2011 : retenedor) {
						List<TipoRetenidoError2011> retenido = 
							retenedorError2011.getRetenido();
						for (TipoRetenidoError2011 retenidoError2011 : retenido) {
							cb.onError(retenedorError2011, retenidoError2011);
						}
					}
				}
				if ( salida.exists()  ) {
					InputStream resultInpuStream = 
						new FileInputStream(salida.getAbsolutePath());
					AEATRetencionesSalida2011 aeatRetencionesSalida2011  = 
						unmarshal(resultInpuStream);
					
					List<TipoRetenedorSalida2011> retenedor = 
						aeatRetencionesSalida2011.getRetenedor();
	
					for (TipoRetenedorSalida2011 retenedorSalida2011 : retenedor ) {
						
						List<TipoRetenidoSalida2011> retenido = 
							retenedorSalida2011.getRetenido();
						
						for (TipoRetenidoSalida2011 retenidoSalida2011 : retenido) {
							cb.onSalida(retenedorSalida2011, retenidoSalida2011);
						}
					}
				}
			} catch (SAXException e) {
				throw new IrpfException(e);
			} catch (JAXBException e) {
				throw new IrpfException(e);
			} catch ( FileNotFoundException e ) {
				throw new IrpfException(e);
			} catch ( IOException e  ) {
				throw new IrpfException(e);
			}
		}
		
	}
	
	public static <T>  void marshall ( T object, OutputStream outputStream)
	throws JAXBException, SAXException {
		JAXBContext jc = JAXBContext.newInstance( "com.aeat.jaxb" );
		Schema schema = SCHEMA_FACTORY.newSchema(SCHEMA_URL);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setSchema(schema);
		marshaller.setProperty("jaxb.formatted.output", true);
		marshaller.marshal(object, outputStream);
	}

	public static <T> T unmarshal( InputStream inputStream )
	throws JAXBException, SAXException {
		JAXBContext jc = JAXBContext.newInstance( "com.aeat.jaxb" );
		Schema schema = SCHEMA_FACTORY.newSchema(SCHEMA_URL);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setSchema(schema);
		return ( T ) unmarshaller.unmarshal( inputStream );
	}		

	public static void main(String[] args) 
		throws Exception{
		
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://127.0.0.1:3306/payroll-esferalia-org?autoReconnect=true";
		String usr = "dbuser"; 
		String psw = "serubd2000";
		
		ResultSet	rs = null; 
		Statement  	stmt = null;
		Connection 	connection = null;
		try {
			connection = DriverManager.getConnection(url,usr ,psw );
	
			stmt = connection.createStatement();
			rs = 
				stmt.executeQuery("SELECT" +
								" effective_date"+
								", count(*)"+
								" FROM irpf_regularization"+
								" GROUP BY effective_date"+
								" ORDER BY 2 DESC LIMIT 1");
			rs.next();
			
			Date date = rs.getDate(IrpfRegularizationColumns.EFFECTIVE_DATE);
			
			rs.close();
			
			long start = System.currentTimeMillis();
			
			Criteria criteria = new Criteria();
			//criteria.addEqualExpression(SQLAEAT2011Factory.PERSON_REGISTRY + "." + RegistryColumns.DOCUMENT,"44867183W" );
			//criteria.addEqualExpression(SQLAEAT2011Factory.PERSON_REGISTRY + "." + RegistryColumns.DOCUMENT,"00388921Z" );
			criteria.addEqualExpression(SQLAEAT2011Factory.ENTERPRISE_REGISTRY + "." + RegistryColumns.DOCUMENT,"A28671600" );
			
			SQLAEAT2011Factory factory = new SQLAEAT2011Factory(connection, date, criteria);
			//SQLAEAT2011Factory factory = new SQLAEAT2011Factory(connection, date);
			
			AbstractIrpfTester irpfTester  = 
				new AbstractIrpfTester(connection, date){
				@Override
				public void onError(
						TipoRetenedorError2011 retenedorError2011,
						TipoRetenidoError2011 retenidoError2011) {
					for (TipoError error : retenidoError2011.getError()) {
						System.out.printf("[%s] : %s \r\n", 
								retenidoError2011.getNif(), 
								error.getDescripcion());
					}
				}
				
				@Override
				public void onSalida(
						TipoRetenedorSalida2011 retenedorSalida2011,
						TipoRetenidoSalida2011 retenidoSalida2011) {
					try {
						super.onSalida(retenedorSalida2011, retenidoSalida2011);
					} catch ( AbstractIrpfTester.UnExpectedValue e ) {
						System.out.printf("[%s]: %s \r\n",retenidoSalida2011.getNif(), e.getMessage());
						System.out.printf("\t %s %f = %f %tF \r\n",
								IrpfResultColumns.ANNUAL_REMUNERATION,
								( Double ) getIrpfResult(IrpfResultColumns.ANNUAL_REMUNERATION), 
								retenidoSalida2011.getRetribAnuales() , 
								( Date ) getContract(ContractColumns.END_DATE));
					}
				}
			};
			
			AEATRetencionesEntrada2011 aeatRetencionesEntrada2011 = 
				factory.getAeatRetencionesEntrada2011(Administration.COMMON_TERRITORY, irpfTester);
			
			IrpfCalculator irpfCalculator = 
				IrpfCalculator.getCalculator(Administration.COMMON_TERRITORY);
			
			irpfCalculator.calculate(aeatRetencionesEntrada2011, irpfTester);
			
			long stop = System.currentTimeMillis();
			
			System.out.println("time : " + (( stop -start ) / 1000 ) + " seconds"    );
			
		}
		finally {
			if ( rs != null ) {
				rs.close();
			}
			if ( stmt != null ) {
				stmt.close();
			}
			if ( connection != null ) {
				connection.close();
			}
		}
	}
	

}
