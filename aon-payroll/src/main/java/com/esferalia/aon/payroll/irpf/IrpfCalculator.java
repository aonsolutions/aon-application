package com.esferalia.aon.payroll.irpf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.aeat.jaxb.AEATRetencionesEntrada2011;
import com.aeat.jaxb.TipoError;
import com.aeat.jaxb.TipoRetenedorEntrada2011;
import com.aeat.jaxb.TipoRetenedorError2011;
import com.aeat.jaxb.TipoRetenedorSalida2011;
import com.aeat.jaxb.TipoRetenidoEntrada2011;
import com.aeat.jaxb.TipoRetenidoError2011;
import com.aeat.jaxb.TipoRetenidoSalida2011;
import com.aeat.jaxb.TipoRetenidoSalida2011.MinimoPersonalFamiliar;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.irpf.sql.DefaultEntrada2011Handler;
import com.esferalia.aon.payroll.irpf.sql.SQLAEAT2011Factory;
import com.esferalia.aon.payroll.irpf.sql.SQLAEAT2011Factory.Entrada2011Handler;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfRegularizationColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;
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
	
	public static void registerCalculator(Administration administration, IrpfCalculator calculator) {
		IRPF_CALCULATORS_MAP.put(administration, calculator);
	}
	
	private static Map<Administration, IrpfCalculator> IRPF_CALCULATORS_MAP = 
		new HashMap<Administration, IrpfCalculator>() {
		{
			put(Administration.COMMON_TERRITORY, new AEATIrpfCalculator());
		}
	};
	
	
	public static void main(String[] args) 
		throws Exception{
		
		System.setProperty(AEATIrpfCalculator.TMP_DIR, "/tmp/aeat");
		
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
			
			//date = DateFormat.getDateInstance(DateFormat.SHORT).parse("09/01/2011");
			
			IrpfCalculator.registerCalculator(Administration.ALAVA, 
					new GeozoneIrpfCalculator(connection, Administration.ALAVA, date));
			IrpfCalculator.registerCalculator(Administration.GIPUZKOA, 
					new GeozoneIrpfCalculator(connection, Administration.GIPUZKOA, date));
			IrpfCalculator.registerCalculator(Administration.BIZKAIA, 
					new GeozoneIrpfCalculator(connection, Administration.BIZKAIA, date));
			IrpfCalculator.registerCalculator(Administration.NAVARRA, 
					new GeozoneIrpfCalculator(connection, Administration.NAVARRA, date));
			
			long start = System.currentTimeMillis();
			
			Criteria criteria = new Criteria();
			
			//criteria.addNotEqualExpression(SQLAEAT2011Factory.PERSON_REGISTRY + "." + RegistryColumns.DOCUMENT,"16060173D" );
			
			//criteria.addEqualExpression(SQLAEAT2011Factory.PERSON_REGISTRY + "." + RegistryColumns.DOCUMENT,"50165539D" );
			//criteria.addEqualExpression(SQLAEAT2011Factory.PERSON_REGISTRY + "." + RegistryColumns.DOCUMENT,"09046167Z" );
			//criteria.addEqualExpression(SQLAEAT2011Factory.PERSON_REGISTRY + "." + RegistryColumns.DOCUMENT,"02870858K" );
			//criteria.addEqualExpression(SQLAEAT2011Factory.PERSON_REGISTRY + "." + RegistryColumns.DOCUMENT,"44867183W" );
			//criteria.addEqualExpression(SQLAEAT2011Factory.PERSON_REGISTRY + "." + RegistryColumns.DOCUMENT,"00388921Z" );
			//criteria.addEqualExpression(SQLAEAT2011Factory.PERSON_REGISTRY + "." + RegistryColumns.DOCUMENT,"02260882M" );
			//criteria.addEqualExpression(SQLAEAT2011Factory.PERSON_REGISTRY + "." + RegistryColumns.DOCUMENT,"51321359X" );
			
			
			//criteria.addEqualExpression(SQLAEAT2011Factory.ENTERPRISE_REGISTRY + "." + RegistryColumns.DOCUMENT,"A28671600" );
			//criteria.addEqualExpression(SQLAEAT2011Factory.ENTERPRISE_REGISTRY + "." + RegistryColumns.DOCUMENT,"A82724089" );
			//criteria.addEqualExpression(SQLAEAT2011Factory.ENTERPRISE_REGISTRY + "." + RegistryColumns.DOCUMENT,"B78342615" );
			
			
			SQLAEAT2011Factory factory = 
				new SQLAEAT2011Factory(connection, date, criteria);
			//SQLAEAT2011Factory factory = new SQLAEAT2011Factory(connection, date);
			
			final IrpfTester irpfTester  = 
				new IrpfTester(connection, date);
			
			Entrada2011Handler entrada2011Handler = 
				new DefaultEntrada2011Handler(irpfTester) ;
			
			factory.forEachTipoRetenidoEntrada2011(entrada2011Handler);
			
			// old way. 
			// AEATRetencionesEntrada2011 aeatRetencionesEntrada2011 = 
			//	factory.getAeatRetencionesEntrada2011();
			//irpfCalculator.calculate(aeatRetencionesEntrada2011, irpfTester);
			
			long stop = System.currentTimeMillis();
			
			System.out.println("warnings : " + irpfTester.getWarnings()  );
			System.out.println("errors : " + ( irpfTester.getErrors() - irpfTester.getWarnings() ) );
			System.out.println("sucess : " + irpfTester.getSuccess()  );
			System.out.println("not found : " + irpfTester.getNotFound()  );
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
	
	private static class IrpfTester extends AbstractIrpfTester {
		
		public IrpfTester(Connection connection, Date date ) 
		throws SQLException {
			super(connection, date);
		}
		
		private int warnings = 0;
		
		public int getWarnings() {
			return warnings;
		}
		
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
			} catch (NotFoundException e) {
			} 
			catch ( AbstractIrpfTester.UnExpectedValue e ) {
				
				Double priorIrpf = e.getPriorIrpf();
				BigDecimal tipoRetencion = retenidoSalida2011.getTipoRetencion();
				
				if ( priorIrpf != null ) {
					double dbIrpf = e.getIrpf();
					double calcIrpf = tipoRetencion.doubleValue();
					if ( calcIrpf < priorIrpf && priorIrpf == dbIrpf ){
						warnings++;
						return;
					} // Min prior I.R.P.F ?
				}
				
				
				System.out.printf("[%s]: Contrato %d,  %s \r\n",
						retenidoSalida2011.getNif(), retenidoSalida2011.getSituacionLaboral().getTrabajadorActivo().getContrato(), e.getMessage());
				/*
				err("Retribución anual", 
						retenidoSalida2011.getRetribAnuales(), 
						e.getAnnualRemuneration());
				err("Gastos deduccibles", 
						retenidoSalida2011.getGastosAnuales(), 
						e.getDeducciblesExpenses());
				
				err("Base para la retención", 
						retenidoSalida2011.getBaseRetencion(), 
						e.getBaseIrpf());
				
				MinimoPersonalFamiliar minimoPersonalFamiliar = 
					retenidoSalida2011.getMinimoPersonalFamiliar();
				if ( minimoPersonalFamiliar != null ) {
					err("Mínimo personal, familiar ", 
							minimoPersonalFamiliar.getTotal(), 
							e.getMinimunPersonalFamily());
				}

				err("Retención anual", 
						retenidoSalida2011.getImpAnualRetencionesIngresosCuenta(), 
						e.getAnnualIrpf());
					*/	
			}
		}
		
	}
	
	private static void err(String msg, BigDecimal calc, double dbValue ) {
		double calcValue = calc == null ? -9999.00: calc.doubleValue();   
		if ( dbValue != calcValue ) {
			System.out.printf("\t %s diferentes  %2f = %2f \r\n",
					msg,
					dbValue,
					calcValue );
		}
		
	}
	

}
