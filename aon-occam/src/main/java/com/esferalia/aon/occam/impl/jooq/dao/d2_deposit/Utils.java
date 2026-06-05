package com.esferalia.aon.occam.impl.jooq.dao.d2_deposit;

import static com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.D2DepositInitialization.INITIALIZE_EXPRESSION_MAP;
import static com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.D2DepositInitialization.INITIALIZE_EXPRESSION_MAP_D2;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.Municipalities;
import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositFooterKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.ID2DepositKey;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.type.CNAE2009;
import com.esferalia.aon.occam.api.model.type.CNAE2025;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema.Cabecera;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema.Claves;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema.Claves.Clave;
import com.esferalia.aon.occam.server.accounting.AccMiningMVELContext;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Utils {
	static final String ABREVIATE = "Abreviado";
	static final String PYMES = "Pymes";
	
	// ESTO REALMENTE ES PASAR EL ESQUEMA A XML Y DEVOLVERLO COMO BYTE[], AUNQUE LO QUE HACE ES
	// GRABAR UN ARCHIVO EN EL DIRECTORIO TMP SIEMPRE CON EL MISMO NOMBRE, LEER ESE ARCHIVO GRABADO Y DEVOLVER EL ARRAY DE BYTES
	// LO CAMBIO PARA QUE SE HAGA SIMPLEMENTE CON UN ByteArrayOutputStream SIN NECESIDAD DE GRABAR UN FICHERO FISICAMENTE EN TMP 
//	public static byte[] writeXml(Esquema schema) throws JAXBException, IOException{
//		JAXBContext ctx = JAXBContext.newInstance(Esquema.class);
//		
//		Marshaller marshaller = ctx.createMarshaller();
//		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//		
//		FileOutputStream fos = new FileOutputStream("/tmp/"+schema.getCabecera().getCIF()+".xml");
//		
//		marshaller.marshal(schema, fos);
//		fos.close();
//		FileInputStream fis = new FileInputStream("/tmp/"+schema.getCabecera().getCIF()+".xml");
//
//		return AonIOUtils.toByteArray(fis);
//	}
	
	public static byte[] writeXml(Esquema schema) throws JAXBException, IOException{
				
		JAXBContext ctx = JAXBContext.newInstance(Esquema.class);
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		marshaller.marshal(schema, baos);
		baos.close();

		return baos.toByteArray();
				
	}
	
	public static Esquema readXml(byte[] xmlFile){
		Esquema schema = new Esquema();
		try {
			JAXBContext ctx = JAXBContext.newInstance(Esquema.class);
			Unmarshaller unmarshaller = ctx.createUnmarshaller();
			InputStream input = new ByteArrayInputStream(xmlFile);
			schema = (Esquema) unmarshaller.unmarshal(input);
		} catch (JAXBException e) {
			e.printStackTrace();
			schema.setError("El archivo xml no es legible.\n" + e.getLocalizedMessage());
		}
		
		
		return schema;
	}
	
	
	public static Esquema readxml(File fXmlFile) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		
		doc.getDocumentElement().normalize();
	 	Esquema schema = new Esquema();
	 	Cabecera header = new Cabecera();
	 	Claves keys = new Claves();
		
		header.setCIF(doc.getElementById("CIF").getTextContent());
		header.setRazonSocial(doc.getElementById("RazonSocial").getTextContent());
		header.setEjercicio(BigInteger.valueOf(Integer.parseInt(doc.getElementById("Ejercicio").getTextContent())));
		header.setIdiomaCuestionario(doc.getElementById("IdiomaCuestionario").getTextContent());
		header.setMemoriaNormalizada(doc.getElementById("MemoriaNormalizada").getTextContent().equalsIgnoreCase("True"));
		header.setTipoCuestionario(doc.getElementById("TipoCuestionario").getTextContent());

		
		NodeList nList = doc.getElementsByTagName("Clave");
		
		for(Integer t =0; t< nList.getLength();t++){
			Clave key = new Clave();
			key.setCodigo(BigInteger.valueOf(Integer.parseInt(nList.item(t).getChildNodes().item(0).getTextContent())));
			key.setValor(nList.item(t).getChildNodes().item(1).getTextContent());
			keys.getClave().add(key);
		}
		schema.setCabecera(header);
		schema.setClaves(keys);
		return schema;
	}
	
	public static byte[] CreateXml(String document, String name) {
		Esquema schema = new Esquema();
		Cabecera header = new Cabecera();
		Claves keys = new Claves();
		
		header.setCIF(document);
		header.setEjercicio(BigInteger.valueOf(2014));
		header.setRazonSocial("");
		header.setTipoCuestionario("Abreviado");
		header.setIdiomaCuestionario("Castellano");
		header.setMemoriaNormalizada(true);
		schema.setCabecera(header);
		
		Clave c8080805 = new Clave();
		c8080805.setCodigo(BigInteger.valueOf(8080805));
		c8080805.setValor("1");
		keys.getClave().add(c8080805);
		
		Clave c8009020 = new Clave();
		c8009020.setCodigo(BigInteger.valueOf(8009020));
		c8009020.setValor("0");
		keys.getClave().add(c8009020);
		
		Clave c1010 = new Clave();
		c1010.setCodigo(BigInteger.valueOf(1010));
		c1010.setValor(document);
		keys.getClave().add(c1010);
		
		Clave c11021 = new Clave();
		c11021.setCodigo(BigInteger.valueOf(11021));
		c11021.setValor("2014");
		keys.getClave().add(c11021);
		   
		Clave c11022 = new Clave();
		c11022.setCodigo(BigInteger.valueOf(11022));
		c11022.setValor("1");
		keys.getClave().add(c11022);
		
		Clave c11023 = new Clave();
		c11023.setCodigo(BigInteger.valueOf(11023));
		c11023.setValor("1");
		keys.getClave().add(c11023);
		    
		Clave c11011 = new Clave();
		c11011.setCodigo(BigInteger.valueOf(11011));
		c11011.setValor("2014");
		keys.getClave().add(c11011);
		
		Clave c11012 = new Clave();
		c11012.setCodigo(BigInteger.valueOf(11012));
		c11012.setValor("12");
		keys.getClave().add(c11012);
		
		Clave c110139 = new Clave();
		c110139.setCodigo(BigInteger.valueOf(110139));
		c110139.setValor("31");
		keys.getClave().add(c110139);
		
		Clave c110219 = new Clave();
		c110219.setCodigo(BigInteger.valueOf(110219));
		c110219.setValor("2013");
		keys.getClave().add(c110219);
		
		Clave c110229 = new Clave();
		c110229.setCodigo(BigInteger.valueOf(110229));
		c110229.setValor("1");
		keys.getClave().add(c110229);
		
		Clave c110239 = new Clave();
		c110239.setCodigo(BigInteger.valueOf(110239));
		c110239.setValor("1");
		keys.getClave().add(c110239);
		   
		Clave c8009010 = new Clave();
		c8009010.setCodigo(BigInteger.valueOf(8009010));
		c8009010.setValor("0");
		keys.getClave().add(c8009010);

		Clave c8009040 = new Clave();
		c8009040.setCodigo(BigInteger.valueOf(8009040));
		c8009040.setValor("0");
		keys.getClave().add(c8009040);    

		Clave c8009030 = new Clave();
		c8009030.setCodigo(BigInteger.valueOf(8009030));
		c8009030.setValor("0");
		keys.getClave().add(c8009030);  
		
		Clave c8080854 = new Clave();
		c8080854.setCodigo(BigInteger.valueOf(8080854));
		c8080854.setValor("1");
		keys.getClave().add(c8080854);
		
		Clave c110119 = new Clave();
		c110119.setCodigo(BigInteger.valueOf(110119));
		c110119.setValor("2013");
		keys.getClave().add(c110119);
		
		Clave c11013 = new Clave();
		c11013.setCodigo(BigInteger.valueOf(11013));
		c11013.setValor("31");
		keys.getClave().add(c11013);
		
		Clave c8080801 = new Clave();
		c8080801.setCodigo(BigInteger.valueOf(8080801));
		c8080801.setValor("1");
		keys.getClave().add(c8080801);

		Clave c8080803 = new Clave();
		c8080803.setCodigo(BigInteger.valueOf(8080803));
		c8080803.setValor("1");
		keys.getClave().add(c8080803);
		
		Clave c8080811 = new Clave();
		c8080811.setCodigo(BigInteger.valueOf(8080811));
		c8080811.setValor("1");
		keys.getClave().add(c8080811);

		Clave c8080800 = new Clave();
		c8080800.setCodigo(BigInteger.valueOf(8080800));
		c8080800.setValor("1");
		keys.getClave().add(c8080800);
		
		Clave c8080819 = new Clave();
		c8080819.setCodigo(BigInteger.valueOf(8080819));
		c8080819.setValor("1");
		keys.getClave().add(c8080819);
		
		Clave c9000000 = new Clave();
		c9000000.setCodigo(BigInteger.valueOf(9000000));
		c9000000.setValor("1");
		keys.getClave().add(c9000000);
		
		schema.setClaves(keys);
		
		byte[] b = null;
		try {
			b = writeXml(schema);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	public static byte[] CreateXml(Map<D2DepositHeaderKey, Double> ctx, Map<ID2DepositKey, String> ctxText, Map<D2DepositKey, Double> ctxMem, Map<D2DepositKey, String> ctxFreeText, Enterprise enterprise, String name, String type , String domain, Integer year, String cnae25, RecordData recordData, boolean hasPreviousDeposit, String cnae09) {
		Esquema schema = createXml(enterprise, name, type, domain, year, cnae25, recordData, hasPreviousDeposit, cnae09);
		
		for (D2DepositHeaderKey key : ctx.keySet()) {
			Clave clave = new Clave();
			clave.setCodigo(BigInteger.valueOf(Integer.parseInt(key.getCode())));
			clave.setValor(double2String(ctx.get(key)));
			schema.getClaves().getClave().add(clave);
		}
		
		for (ID2DepositKey key : ctxText.keySet()) {
			Clave clave = new Clave();
			clave.setCodigo(BigInteger.valueOf(Integer.parseInt(key.getCode())));
			clave.setValor(ctxText.get(key));
			schema.getClaves().getClave().add(clave);
		}
		
		// Casillas dia, mes y año de la fecha de inicio anterior 
		if (AonStringUtils.isNotBlank(ctxText.get(D2DepositHeaderKey.IDA011029))) {
			String[] startDateSplit = AonStringUtils.split(ctxText.get(D2DepositHeaderKey.IDA011029), '.');
			if (startDateSplit.length == 3) {
				schema.getClaves().getClave().add(getNewClave(110219, startDateSplit[2])); // Año
				schema.getClaves().getClave().add(getNewClave(110229, startDateSplit[1])); // Mes
				schema.getClaves().getClave().add(getNewClave(110239, startDateSplit[0])); // Día
			}
		}
		
		// Casillas dia, mes y año de la fecha de fin anterior
		if (AonStringUtils.isNotBlank(ctxText.get(D2DepositHeaderKey.IDA011019))) {
			String[] endDateSplit = AonStringUtils.split(ctxText.get(D2DepositHeaderKey.IDA011019), '.');
			if (endDateSplit.length == 3) {
				schema.getClaves().getClave().add(getNewClave(110119, endDateSplit[2])); // Año
				schema.getClaves().getClave().add(getNewClave(110129, endDateSplit[1])); // Mes
				schema.getClaves().getClave().add(getNewClave(110139, endDateSplit[0])); // Dia
			}
		}
		
		for (D2DepositKey key : ctxMem.keySet()) {
			Clave clave = new Clave();
			clave.setCodigo(BigInteger.valueOf(Integer.parseInt(key.getCode())));
			clave.setValor(double2String(ctxMem.get(key)));
			schema.getClaves().getClave().add(clave);
		}
		
		for (D2DepositKey key : ctxFreeText.keySet()) {
			Clave clave = new Clave();
			clave.setCodigo(BigInteger.valueOf(Integer.parseInt(key.getCode())));
			clave.setValor(ctxFreeText.get(key));
			schema.getClaves().getClave().add(clave);
		}
		
		// Registro Mercantil: Si está vacio porque no se ha copiado del ejercicio anterior, entonces se copia de los datos registrales de la Empresa
		if (schema.getClaves().getClave().stream().noneMatch(c -> c.getCodigo().equals( new BigInteger(D2DepositFooterKey.PR8081001.getCode())))
				&& recordData != null && recordData.getCommercialRegistryCode() != null ) {
			// Hay que grabar el valor sin el cero de delante, es decir como si fuera un numero entero
			String value = String.valueOf(Integer.parseInt(recordData.getCommercialRegistryCode().getCode()));
			schema.getClaves().getClave().add(getNewClave(D2DepositFooterKey.PR8081001, value));
		}
				
		byte[] b = null;
		try {
			b = writeXml(schema);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
// NO SE USA	
//	public static byte[] CreateXml(Enterprise enterprise, String name, String type , String domain, Integer year) {
//		Esquema  schema = createXml(enterprise, name, type, domain, year);
//		byte[] b = null;
//		try {
//			b = writeXml(schema);
//		} catch (JAXBException | IOException e) {
//			e.printStackTrace();
//		}
//		return b;
//	}
	
	private static Clave getNewClave(long codigo, String valor) {
		Clave clave = new Clave();
		clave.setCodigo(BigInteger.valueOf(codigo));
		clave.setValor(valor);
		return clave;
	}
	
	private static Clave getNewClave(ID2DepositKey key, String value) {
		return getNewClave(Long.parseLong(key.getCode()), value);
	}

	public static Esquema changeType(Esquema schema, String type) {
		schema.getCabecera().setTipoCuestionario(type);
		Integer[] arr = {8080805, 8080852, 8080854, 8080855, 8080801, 8080803, 8080850, 8080851};
		
		schema.getClaves().getClave().stream().forEach(c ->{
			if(c.getCodigo().equals(BigInteger.valueOf(8080805))){
				c.setValor(ABREVIATE.equals(type) ? "1" : "0");
				arr[0] = 1;
			}
			if(c.getCodigo().equals(BigInteger.valueOf(8080852))){
				c.setValor(PYMES.equals(type) ? "1" : "0");
				arr[1] = 1;
			}
			if(c.getCodigo().equals(BigInteger.valueOf(8080854))){
				c.setValor(ABREVIATE.equals(type) ? "1" : "0");
				arr[2] = 1;
			}
			if(c.getCodigo().equals(BigInteger.valueOf(8080855))){
				c.setValor(PYMES.equals(type) ? "1" : "0");
				arr[3] = 1;
			}
			if(c.getCodigo().equals(BigInteger.valueOf(8080801))){
				c.setValor(ABREVIATE.equals(type) ? "1" : "0");
				arr[4] = 1;
			}
			if(c.getCodigo().equals(BigInteger.valueOf(8080803))){
				c.setValor(ABREVIATE.equals(type) ? "1" : "0");
				arr[5] = 1;
			}
			if(c.getCodigo().equals(BigInteger.valueOf(8080850))){
				c.setValor(PYMES.equals(type) ? "1" : "0");
				arr[6] = 1;
			}
			if(c.getCodigo().equals(BigInteger.valueOf(8080851))){
				c.setValor(PYMES.equals(type) ? "1" : "0");
				arr[7] = 1;
			}
		});
		
		for(Integer i : arr) {
			if(i != 1) {
				Clave clave = new Clave();
				clave.setCodigo(BigInteger.valueOf(i));
				if(clave.getCodigo().equals(BigInteger.valueOf(8080805))){
					clave.setValor(ABREVIATE.equals(type) ? "1" : "0");
				} else if(clave.getCodigo().equals(BigInteger.valueOf(8080852))){
					clave.setValor(PYMES.equals(type) ? "1" : "0");
				} else if(clave.getCodigo().equals(BigInteger.valueOf(8080854))){
					clave.setValor(ABREVIATE.equals(type) ? "1" : "0");
				} else if(clave.getCodigo().equals(BigInteger.valueOf(8080855))){
					clave.setValor(PYMES.equals(type) ? "1" : "0");
				} else if(clave.getCodigo().equals(BigInteger.valueOf(8080801))){
					clave.setValor(ABREVIATE.equals(type) ? "1" : "0");
				} else if(clave.getCodigo().equals(BigInteger.valueOf(8080803))){
					clave.setValor(ABREVIATE.equals(type) ? "1" : "0");
				} else if(clave.getCodigo().equals(BigInteger.valueOf(8080850))){
					clave.setValor(PYMES.equals(type) ? "1" : "0");
				} else if(clave.getCodigo().equals(BigInteger.valueOf(8080851))){
					clave.setValor(PYMES.equals(type) ? "1" : "0");
				}
				schema.getClaves().getClave().add(clave);
			}
		}
		return schema;
	}
	
	public static Esquema createXml(Enterprise enterprise, String name, String type , String domain, Integer year, String cnae25, RecordData recordData, boolean hasPreviousDeposit, String cnae09) {
		Esquema schema = new Esquema();
		Cabecera header = new Cabecera();
		Claves keys = new Claves();
		
		header.setCIF(enterprise.getDocument());
		header.setEjercicio(BigInteger.valueOf(year));
		header.setRazonSocial("");
		header.setTipoCuestionario(type);
		header.setIdiomaCuestionario("Castellano");
		header.setMemoriaNormalizada(true);
		schema.setCabecera(header);
		
		if(type.equals(ABREVIATE)){
			Clave c8080805 = new Clave();
			c8080805.setCodigo(BigInteger.valueOf(8080805));
			c8080805.setValor("1");
			keys.getClave().add(c8080805);
		}
		else if(type.equals(PYMES)){
			Clave c8080852 = new Clave();
			c8080852.setCodigo(BigInteger.valueOf(8080852));
			c8080852.setValor("1");
			keys.getClave().add(c8080852);
		}

		
		Clave c8009020 = new Clave();
		c8009020.setCodigo(BigInteger.valueOf(8009020));
		c8009020.setValor("0");
		keys.getClave().add(c8009020);
		
		//---------- Enterprise Information
		
		Clave c1010 = new Clave();
		c1010.setCodigo(BigInteger.valueOf(1010));
		c1010.setValor(enterprise.getDocument());
		keys.getClave().add(c1010);
	
		if(enterprise.getDocument() != null && enterprise.getDocument().contains("A")){
			Clave c1011 = new Clave();
			c1011.setCodigo(BigInteger.valueOf(1011));
			c1011.setValor("1");
			keys.getClave().add(c1011);
		}
		else if(enterprise.getDocument() != null && enterprise.getDocument().contains("B")){
			Clave c1012 = new Clave();
			c1012.setCodigo(BigInteger.valueOf(1012));
			c1012.setValor("1");
			keys.getClave().add(c1012);
		}
		
		Clave c1020 = new Clave();
		c1020.setCodigo(BigInteger.valueOf(1020));
		c1020.setValor(enterprise.getName());
		keys.getClave().add(c1020);
		
		Clave c1022 = new Clave();
		c1022.setCodigo(BigInteger.valueOf(1022));
		c1022.setValor(enterprise.getAddress()+ " " + enterprise.getAddress2()+ " " + enterprise.getAddress3());
		keys.getClave().add(c1022);
		
		// Municipio: Se coge el nombre del municipio a partir del código de municipio de la dirección de la empresa, si está vacio se coge la localidad
		Municipalities municipalities = new Municipalities();
		String townName = municipalities.getMunicipalityByZip(enterprise.getTown());
		if (AonStringUtils.isBlank(townName)) {
			townName = enterprise.getCity();
		}		
//		Clave c1023 = new Clave();
//		c1023.setCodigo(BigInteger.valueOf(1023));
//		c1023.setValor(enterprise.getCity() != null ? enterprise.getCity() : "");
//		keys.getClave().add(c1023);
		keys.getClave().add(getNewClave(D2DepositHeaderKey.IDA01023, AonStringUtils.trimToEmpty(townName)));
		
		Clave c1024 = new Clave();
		c1024.setCodigo(BigInteger.valueOf(1024));
		c1024.setValor(enterprise.getZip() != null ? enterprise.getZip() : "");
		keys.getClave().add(c1024);
		
		Clave c1025 = new Clave();
		c1025.setCodigo(BigInteger.valueOf(1025));
		Map<Province,String> ctx = new HashMap<>();
		ProvincetoProvinces.fill(ctx);
		c1025.setValor(ctx.get(enterprise.getProvince()));
		keys.getClave().add(c1025);
		
		Clave c1031 = new Clave();
		c1031.setCodigo(BigInteger.valueOf(1031));
		c1031.setValor(enterprise.getPhone() != null ? enterprise.getPhone() : "");
		keys.getClave().add(c1031);
		
		Clave c1037 = new Clave();
		c1037.setCodigo(BigInteger.valueOf(1037));
		c1037.setValor(enterprise.getEmail() != null ? enterprise.getEmail() : "");
		keys.getClave().add(c1037);

		// CNAE
		CNAE2025 cnae2025 = CNAE2025.valueOfCode(cnae25);
		if (year >= 2025) {
			if (cnae2025 != null) {
				Clave c2014 = new Clave();
				c2014.setCodigo(BigInteger.valueOf(2014));
				c2014.setValor(cnae2025.getCodeWithoutPoint());
				keys.getClave().add(c2014);
				
				Clave c2009 = new Clave();
				c2009.setCodigo(BigInteger.valueOf(2009));
				c2009.setValor(cnae2025.getDescription());
				keys.getClave().add(c2009);
			}
		} else {
			CNAE2009 cnae2009 = CNAE2009.valueOfCode(cnae09);
			if (cnae2009 != null) {
				Clave c2001 = new Clave();
				c2001.setCodigo(BigInteger.valueOf(2001));
				c2001.setValor(cnae2009.getCodeWithoutPoint());
				keys.getClave().add(c2001);
			}
			// Si el ejercicio es 2024 se graba tambien el CNAE2025
			if (year == 2024) {
				if (cnae2025 != null) {
					Clave c2014 = new Clave();
					c2014.setCodigo(BigInteger.valueOf(2014));
					c2014.setValor(cnae2025.getCodeWithoutPoint());
					keys.getClave().add(c2014);
				}
			}
			// Descripción de la actividad: Si el ejercicio es 2024 se graba la del CNAE2025, si no la del CNAE2009
			if (year == 2024 && cnae2025 != null) {
				keys.getClave().add(getNewClave(D2DepositHeaderKey.IDA02009, cnae2025.getDescription()));
			} else if (cnae2009 != null) {
				keys.getClave().add(getNewClave(D2DepositHeaderKey.IDA02009, cnae2009.getDescription()));
			}
		}
		
		// Datos Registrales (Tomo, Folio, Nº Hoja, IRUS)
		if (recordData != null) {
			Clave c8081002 = new Clave();
			c8081002.setCodigo(BigInteger.valueOf(8081002));
			c8081002.setValor(recordData.getVolume());
			keys.getClave().add(c8081002);
			
			Clave c8081003 = new Clave();
			c8081003.setCodigo(BigInteger.valueOf(8081003));
			c8081003.setValor(recordData.getPage());
			keys.getClave().add(c8081003);
			
			Clave c8081004 = new Clave();
			c8081004.setCodigo(BigInteger.valueOf(8081004));
			c8081004.setValor(recordData.getSheet());
			keys.getClave().add(c8081004);
			
			if (year >= 2024) {
				keys.getClave().add(getNewClave(D2DepositHeaderKey.IDA01008, recordData.getIrus())); // IRUS
			}
		}
		
		// Fecha Inicio Actual
		keys.getClave().add(getNewClave(1102, "1.1." + year.toString())); // Completa
		keys.getClave().add(getNewClave(11021, year.toString()));         // Año
		keys.getClave().add(getNewClave(11022, "1"));                     // Mes
		keys.getClave().add(getNewClave(11023, "1"));                     // Día
		
		// Fecha Fin Actual
		keys.getClave().add(getNewClave(1101, "31.12." + year.toString())); // Completa
		keys.getClave().add(getNewClave(11011, year.toString()));           // Año
		keys.getClave().add(getNewClave(11012, "12"));                      // Mes
		keys.getClave().add(getNewClave(11013, "31"));                      // Día
		
		// Las fechas de inicio y fin anterior, solo se inicializan si no se han copiado del ejercicio anterior
		if (!hasPreviousDeposit) {

			// Fecha Inicio Anterior
			Integer preYear = year - 1;
			keys.getClave().add(getNewClave(11029, "1.1." + preYear.toString())); // Completa
			keys.getClave().add(getNewClave(110219, preYear.toString()));         // Año
			keys.getClave().add(getNewClave(110229, "1"));                        // Mes
			keys.getClave().add(getNewClave(110239, "1"));                        // Día
			
			// Fecha Fin Anterior
			keys.getClave().add(getNewClave(11019, "31.12." + preYear.toString())); // Completa
			keys.getClave().add(getNewClave(110119, preYear.toString()));           // Año
			keys.getClave().add(getNewClave(110129, "12"));                         // Mes
			keys.getClave().add(getNewClave(110139, "31"));                         // Día
			
		}
		   
		Clave c8009010 = new Clave();
		c8009010.setCodigo(BigInteger.valueOf(8009010));
		c8009010.setValor("0");
		keys.getClave().add(c8009010);

		Clave c8009040 = new Clave();
		c8009040.setCodigo(BigInteger.valueOf(8009040));
		c8009040.setValor("0");
		keys.getClave().add(c8009040);    

		Clave c8009030 = new Clave();
		c8009030.setCodigo(BigInteger.valueOf(8009030));
		c8009030.setValor("0");
		keys.getClave().add(c8009030);  
		
		if(type.equals("Abreviado")){
			Clave c8080854 = new Clave();
			c8080854.setCodigo(BigInteger.valueOf(8080854));
			c8080854.setValor("1");
			keys.getClave().add(c8080854);
		}
		else if(type.equals("Pymes")){
			Clave c8080855 = new Clave();
			c8080855.setCodigo(BigInteger.valueOf(8080855));
			c8080855.setValor("1");
			keys.getClave().add(c8080855);
		}
		
		if(type.equals("Abreviado")){
			Clave c8080801 = new Clave();
			c8080801.setCodigo(BigInteger.valueOf(8080801));
			c8080801.setValor("1");
			keys.getClave().add(c8080801);
			
			Clave c8080803 = new Clave();
			c8080803.setCodigo(BigInteger.valueOf(8080803));
			c8080803.setValor("1");
			keys.getClave().add(c8080803);
		}
		else if(type.equals("Pymes")){
			Clave c8080850 = new Clave();
			c8080850.setCodigo(BigInteger.valueOf(8080850));
			c8080850.setValor("1");
			keys.getClave().add(c8080850);
			
			Clave c8080851 = new Clave();
			c8080851.setCodigo(BigInteger.valueOf(8080851));
			c8080851.setValor("1");
			keys.getClave().add(c8080851);
		}

		Clave c8080811 = new Clave();
		c8080811.setCodigo(BigInteger.valueOf(8080811));
		c8080811.setValor("1");
		keys.getClave().add(c8080811);
		if(year >= 2017) {
			Clave c8080827 = new Clave();
			c8080827.setCodigo(BigInteger.valueOf(8080827));
			c8080827.setValor("1");
			keys.getClave().add(c8080827);
		}
		Clave c8080800 = new Clave();
		c8080800.setCodigo(BigInteger.valueOf(8080800));
		c8080800.setValor("1");
		keys.getClave().add(c8080800);
		
		Clave c8080819 = new Clave();
		c8080819.setCodigo(BigInteger.valueOf(8080819));
		c8080819.setValor("1");
		keys.getClave().add(c8080819);
		
		Clave c9000000 = new Clave();
		c9000000.setCodigo(BigInteger.valueOf(9000000));
		c9000000.setValor("1");
		keys.getClave().add(c9000000);
		
		/* INICIALIZAR LOS VALORES DE LAS CUENTAS ANUALES DESDE CONTABILIDAD MEDIANTE SUS FORMULAS DE CÁLCULO. */

		CloseableAONContext ctx2 = null;
		try {
			ctx2 = AONContext.getAONContext( domain ,enterprise.getDomain());
			
			Map<String,String> computeMap = new HashMap<String, String>();
			
			//Valores de D2DepositHeaderKey
			AccMiningMVELContext acc = initializeHeaderKey(ctx2, year, type);
			for (String value : acc.keySet()) {
				if(!value.equalsIgnoreCase(ABREVIATE)){
					D2DepositHeaderKey k = D2DepositHeaderKey.valueOf(value);
					if(!computeMap.containsKey(k.getCode())){
						Clave clave = new Clave();
						Integer code = Integer.parseInt(k.getCode());
						clave.setCodigo(BigInteger.valueOf(code));
						clave.setValor(object2String(acc.get(value)));	
						keys.getClave().add(clave);
						computeMap.put(k.getCode(),acc.get(value).toString());
					} 
				}
			}
			
			//Valores de D2DepositKey
			AccMiningMVELContext acc2 = initializeKey(ctx2, year, type);
			for (String value : acc2.keySet()) {
				if(!value.equalsIgnoreCase(ABREVIATE)){
					D2DepositKey k = D2DepositKey.valueOf(value);
					if(!computeMap.containsKey(k.getCode())){
						Clave clave = new Clave();
						Integer code = Integer.parseInt(k.getCode());
						clave.setCodigo(BigInteger.valueOf(code));
						clave.setValor(object2String(acc2.get(value)));	
						keys.getClave().add(clave);
						computeMap.put(k.getCode(),acc2.get(value).toString());
					}
				}
			}

			// Añadir casillas calculadas
			Map<String, String> c = compute(computeMap, year, type);

			for (String key : c.keySet()) {
				// Casillas que son totales, pero que tambien se leen de la contabilidad, se ignoran, porque si no se duplicarían
				if (!key.equals("32580") && !key.equals("12380") && !key.equals("21300")) {
					Clave clave = new Clave();
					clave.setCodigo(BigInteger.valueOf(Integer.parseInt(key)));
					clave.setValor(c.get(key));
					keys.getClave().add(clave);
				}
			}
	
		}finally{
			if (ctx2 != null) ctx2.close();
		}
		
		schema.setClaves(keys);	
		return schema;
	}
	

	private static final IAccMiningKeyAccept ACCEPTER = new IAccMiningKeyAccept() {
		
		@Override
		public boolean acceptKey(Object key) {
			try {
				return (D2DepositKey.valueOf((String) key) != null);	
			} catch (IllegalArgumentException e) {
				return false;
			}
		}
	};
	
	private static AccMiningParameters getParams(AONContext ctx, Integer domainId, Integer year) throws AonCoreException {
		AccMiningParameters params = new AccMiningParameters();
		params.setDomain(domainId);
		params.setYear(year);
		 
		AccountPeriod period =  AccountPeriodDAO.getPeriodByYear(ctx, year);
		if (period == null) {
//			throw new AonCoreException("Ejercicio '"+mod200.getYear()+"' no encontrado.");
			return null;
		}
//		if (!period.isClosed()) {
//			throw new AonCoreException("Cierre el ejercicio contable '"+mod200.getYear()+"' para poder continuar.");
//		}
		params.setPeriodId(period.getId());
		params.setStartDate(period.getInitiationDate());
		params.setEndDate(period.getDeadline());
		params.setAccountLevel(5); // Ahora se debe indicar que queremos los saldos a 5 dígitos
		return params;
	}
	
	public static AccMiningMVELContext initializeHeaderKey(AONContext ctx, Integer year, String type) {
		AccMiningMVELContext mvlCtx = new AccMiningMVELContext(ACCEPTER);
		AccMiningParameters params =  getParams(ctx, ctx.getDomainId(), year); //getParams(ctx,mod200);
		if (params != null) {
			mvlCtx.setAccounts(ACCOUNTING.getAccountBalances(ctx, params) );
		} else {
			mvlCtx.setAccounts( new HashMap<String,AccountBalance>() );
		}
		mvlCtx.setExpressionMap(INITIALIZE_EXPRESSION_MAP);
		mvlCtx.put("ABREVIADO", type.equals(ABREVIATE));
		for (String stringKey : INITIALIZE_EXPRESSION_MAP.keySet()) {
			String expression = INITIALIZE_EXPRESSION_MAP.get(stringKey);
			mvlCtx.put(stringKey, 0.0 );
			Object ret = mvlCtx.evaluateExpression(stringKey,expression);
			mvlCtx.put(stringKey, ret );
		}	
		return mvlCtx;
	}
	
	public static AccMiningMVELContext initializeKey(AONContext ctx, Integer year, String type) {
		AccMiningMVELContext mvlCtx = new AccMiningMVELContext(ACCEPTER);
		AccMiningParameters params =  getParams(ctx, ctx.getDomainId(), year); //getParams(ctx,mod200);
		if (params != null) {
			mvlCtx.setAccounts(ACCOUNTING.getAccountBalances(ctx, params) );
		} else {
			mvlCtx.setAccounts( new HashMap<String,AccountBalance>() );
		}
		mvlCtx.setExpressionMap(INITIALIZE_EXPRESSION_MAP_D2);

		mvlCtx.put("ABREVIADO", type.equals(ABREVIATE));
		for(String stringKey : INITIALIZE_EXPRESSION_MAP_D2.keySet()){
			String expression = INITIALIZE_EXPRESSION_MAP_D2.get(stringKey);
			mvlCtx.put(stringKey, 0.0);
			Object ret = mvlCtx.evaluateExpression(stringKey,expression);
			mvlCtx.put(stringKey, ret );
		}
		return mvlCtx;
	}	

	public static Map<String, String> compute(Map<String, String> map, Integer year, String type) {
		AccMiningMVELContext ctx = new AccMiningMVELContext(new IAccMiningKeyAccept() {
			@Override
			public boolean acceptKey(Object key) {
				return AonStringUtils.isNotEmpty((String) key);
			}
		});
		ctx.setExpressionMap(D2Compute.COMPUTE_MAP_CURRENT);
		
		for(String key : map.keySet()){
			try {
				if (AonStringUtils.isNotEmpty( map.get(key) )) {
					Double d = Double.parseDouble(map.get(key));
					ctx.put("Q"+key, d);
				}
			} catch (NumberFormatException e) {
				// Ignore value
			}
		}
		Map<String, String> m = new HashMap<String, String>();
		ctx.put("PYMES", type.equals(PYMES));
		ctx.put("Y2014", year == 2014);
		for (String key : D2Compute.COMPUTE_MAP_CURRENT.keySet()) {
			String expression = D2Compute.COMPUTE_MAP_CURRENT.get(key);
			Object ret = ctx.evaluateExpression(key,expression);
			if (ret instanceof Double) {
				Double calculated = (Double) ret;
				ctx.put("Q"+key, calculated);
				if(map.containsKey(key)) map.remove(key);
				m.put(key, double2String(calculated));
			}
		}
		return m;
	}
	
	private static String double2String(Double d) {
		BigDecimal bd = new BigDecimal(d);
		return bd.setScale(2, RoundingMode.HALF_EVEN).toPlainString();
	}
	
	private static String object2String(Object o) {
		try {
			Double d = Double.parseDouble(o.toString());
			return double2String(d);
		} catch (Exception e) {
			return o.toString();
		}
	}
	
}
