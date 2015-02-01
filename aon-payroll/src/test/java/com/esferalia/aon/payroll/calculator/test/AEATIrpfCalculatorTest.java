package com.esferalia.aon.payroll.calculator.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.irpf.AEATRetencionesEntradaFactory;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext;
import com.esferalia.aon.payroll.irpf.IrpfCalculateException;
import com.esferalia.aon.payroll.irpf.IrpfCalculator;
import com.esferalia.aon.payroll.irpf.sql.SQLIrpfCalculatorContext;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;

import es.aeat.pret.rw13.jaxb.AEATRetencionesEntrada2013;
import es.aeat.pret.rw13.jaxb.AEATRetencionesError2013;
import es.aeat.pret.rw13.jaxb.AEATRetencionesSalida2013;

public class AEATIrpfCalculatorTest {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Connection conn = getConnection();

		Calendar startCalendar = Calendar.getInstance();
		startCalendar.set(Calendar.DAY_OF_MONTH, 1);
		startCalendar.set(Calendar.MONTH, 4);
		Date startDate = startCalendar.getTime();
		
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.set(Calendar.DAY_OF_YEAR,
				endCalendar.getActualMaximum(Calendar.DAY_OF_YEAR));
		Date endDate = endCalendar.getTime();

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(SQLConstants.CONTRACT + "."
				+ ContractColumns.ID, 17);

		IIrpfCalculatorContext irpfCalculatorContext = new SQLIrpfCalculatorContext(
				conn, startDate, endDate, criteria);
		JAXBContext jaxbContext = JAXBContext
				.newInstance("es.aeat.pret.rw13.jaxb");
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty("jaxb.formatted.output", true);

		long start = System.currentTimeMillis();

		if (irpfCalculatorContext.next()) {
			long stop_next = System.currentTimeMillis();
			
			AEATRetencionesEntrada2013 aeatRetencionesEntrada2013 = AEATRetencionesEntradaFactory
					.create2013(irpfCalculatorContext);
			long stop_create = System.currentTimeMillis();
			


			
			/*
			File aeatRetencionesEntrada2013File = File.createTempFile(
					"AEATRetencionesEntrada2013", "xml");

			FileOutputStream os = new FileOutputStream(
					aeatRetencionesEntrada2013File);
			marshaller.marshal(aeatRetencionesEntrada2013, os);
			os.close();
			File aeatRetencionesSalida2013File = File.createTempFile(
					"AEATRetencionesSalida2013", "xml");
			File aeatRetencionesError2013File = File.createTempFile(
					"AEATRetencionesError2013", "xml");

			ModuloCalculo.procesarFicheroXml(
					aeatRetencionesEntrada2013File.getCanonicalPath(),
					aeatRetencionesError2013File.getCanonicalPath(), "",
					aeatRetencionesSalida2013File.getCanonicalPath());

			print(aeatRetencionesEntrada2013File);
			aeatRetencionesEntrada2013File.deleteOnExit();

			print(aeatRetencionesSalida2013File);
			aeatRetencionesSalida2013File.deleteOnExit();

			print(aeatRetencionesError2013File);
			aeatRetencionesError2013File.deleteOnExit();
			*/

			
			

		}

	}

	// ---------------------------------------------------------- Private
	// methods

	private static Connection getConnection() throws ClassNotFoundException,
			SQLException {
		String url = "jdbc:mysql://127.0.0.1:3306/tadldh-aonsolutions-net?autoReconnect=true";
		String usr = "dbuser";
		String psw = "serubd2000";
		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection(url, usr, psw);
	}

	private static void print(File file) throws IOException {
		if (!file.exists())
			return;

		FileInputStream is = null;
		try {
			is = new FileInputStream(file);
			byte bbuf[] = new byte[1024];
			for (int read = is.read(bbuf); read != -1; read = is.read(bbuf)) {
				System.out.write(bbuf, 0, read);
			}
		} finally {
			if (is != null)
				is.close();
		}
	}
}
