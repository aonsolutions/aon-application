package com.esferalia.aon.occam.impl.jooq.dao.d2_deposit;

import static com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.D2DepositInitialization.INITIALIZE_EXPRESSION_MAP;
import static com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.D2DepositInitialization.INITIALIZE_EXPRESSION_MAP_D2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema.Cabecera;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema.Claves;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema.Claves.Clave;
import com.esferalia.aon.occam.server.accounting.AccMiningMVELContext;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonStringUtils;



public class Utils {
	final static String ABREVIATE = "Abreviado";
	final static String PYMES = "Pymes";
	
	public static byte[] writeXml(Esquema schema) throws JAXBException, IOException{
		JAXBContext ctx = JAXBContext.newInstance(Esquema.class);
		
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		FileOutputStream fos = new FileOutputStream("/tmp/"+schema.getCabecera().getCIF()+".xml");
		
		marshaller.marshal(schema, fos);
		fos.close();
		FileInputStream fis = new FileInputStream("/tmp/"+schema.getCabecera().getCIF()+".xml");

		return AonIOUtils.toByteArray(fis);
	}
	
	public static Esquema readXml(byte[] xmlFile) throws JAXBException{
		JAXBContext ctx = JAXBContext.newInstance(Esquema.class);
		
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
	
		InputStream input = new ByteArrayInputStream(xmlFile);
		Esquema schema = (Esquema) unmarshaller.unmarshal(input);
		
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
		header.setMemoriaNormalizada(doc.getElementById("MemoriaNormalizada").getTextContent().equals("True"));
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
	
	public static byte[] writeXmlFile(Esquema schema){
		/*<?xml version="1.0" encoding="UTF-8"?>
		<Esquema xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
			<Cabecera>
				....
			</Cabecera>
			<Claves>
				<Clave>
					<Codigo></Codigo>
					<Valor></Valor>
				</Clave>
				.....
				<Clave>
					<Codigo></Codigo>
					<Valor></Valor>
				</Clave>
			</Claves>
		</Esquema>*/
		
		byte[] b = null;
		try {
	 
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Esquema");
			doc.appendChild(rootElement);
	 
			// firstname elements
			Element cabecera = doc.createElement("Cabecera");
			rootElement.appendChild(cabecera);
			
			Element cif = doc.createElement("CIF");
			cif.setTextContent(schema.getCabecera().getCIF());
			cabecera.appendChild(cif);
			
			Element razonSocial = doc.createElement("RazonSocial"); 
			razonSocial.setTextContent(schema.getCabecera().getRazonSocial());
			cabecera.appendChild(razonSocial);
			
			Element descripcion = doc.createElement("Descripcion");
			descripcion.setTextContent("registry.name");
			cabecera.appendChild(descripcion);
			
			Element tipoCuestionario = doc.createElement("TipoCuestionario");
			tipoCuestionario.setTextContent(schema.getCabecera().getTipoCuestionario());
			cabecera.appendChild(tipoCuestionario);
			
			Element idiomaCuestionario = doc.createElement("IdiomaCuestionario");
			idiomaCuestionario.setTextContent(schema.getCabecera().getIdiomaCuestionario());
			cabecera.appendChild(idiomaCuestionario);
			
			Element memoriaNormalizada = doc.createElement("MemoriaNormalizada");
			memoriaNormalizada.setTextContent("True");
			cabecera.appendChild(memoriaNormalizada);
			
			Element ejercicio = doc.createElement("Ejercicio");
			ejercicio.setTextContent(schema.getCabecera().getEjercicio().toString());
			cabecera.appendChild(ejercicio);
			
			Element claves = doc.createElement("Claves");
			rootElement.appendChild(claves);
			
			for(Clave c : schema.getClaves().getClave()){
				Element clave = doc.createElement("Clave");
				claves.appendChild(clave);
				Element codigo = doc.createElement("Codigo");
				codigo.setTextContent(c.getCodigo().toString());
				clave.appendChild(codigo);
				Element valor = doc.createElement("Valor");
				valor.setTextContent(c.getValor());
				clave.appendChild(valor);
			}
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
						
							
						
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			StreamResult result = new StreamResult(bos);
				 
			transformer.transform(source, result);
						
			b = bos.toByteArray();
						
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
		return b;
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
	
	
	public static byte[] CreateXml(Map<D2DepositHeaderKey, Double> ctx, Map<D2DepositKey, Double> ctxMem, Enterprise enterprise, String name, String type , String domain, Integer year) {
		Esquema  schema = createXml(enterprise, name, type, domain, year);
		
		for (D2DepositHeaderKey key : ctx.keySet()) {
			Clave clave = new Clave();
			clave.setCodigo(BigInteger.valueOf(Integer.parseInt(key.getCode())));
			clave.setValor(ctx.get(key).toString());
			schema.getClaves().getClave().add(clave);
		}
		
		for (D2DepositKey key : ctxMem.keySet()) {
			Clave clave = new Clave();
			clave.setCodigo(BigInteger.valueOf(Integer.parseInt(key.getCode())));
			clave.setValor(ctxMem.get(key).toString());
			schema.getClaves().getClave().add(clave);
		}
		
		byte[] b = null;
		try {
			b = writeXml(schema);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	public static byte[] CreateXml(Enterprise enterprise, String name, String type , String domain, Integer year) {
		Esquema  schema = createXml(enterprise, name, type, domain, year);
		byte[] b = null;
		try {
			b = writeXml(schema);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	public static Esquema createXml(Enterprise enterprise, String name, String type , String domain, Integer year){
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
	
		if(enterprise.getDocument().contains("A")){
			Clave c1011 = new Clave();
			c1011.setCodigo(BigInteger.valueOf(1011));
			c1011.setValor("True");
			keys.getClave().add(c1011);
		}
		else if(enterprise.getDocument().contains("B")){
			Clave c1012 = new Clave();
			c1012.setCodigo(BigInteger.valueOf(1012));
			c1012.setValor("True");
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
		
		Clave c1023 = new Clave();
		c1023.setCodigo(BigInteger.valueOf(1023));
		c1023.setValor(enterprise.getCity());
		keys.getClave().add(c1023);
		
		Clave c1024 = new Clave();
		c1024.setCodigo(BigInteger.valueOf(1024));
		c1024.setValor(enterprise.getZip());
		keys.getClave().add(c1024);
		
		
		Clave c1025 = new Clave();
		c1025.setCodigo(BigInteger.valueOf(1025));
		Map<Province,String> ctx = new HashMap<>();
		ProvincetoProvinces.fill(ctx);
		c1025.setValor(ctx.get(enterprise.getProvince()));
		keys.getClave().add(c1025);
		
		Clave c1031 = new Clave();
		c1031.setCodigo(BigInteger.valueOf(1031));
		c1031.setValor(enterprise.getPhone());
		keys.getClave().add(c1031);
		
		Clave c1037 = new Clave();
		c1037.setCodigo(BigInteger.valueOf(1037));
		c1037.setValor(enterprise.getEmail());
		keys.getClave().add(c1037);
		
		//---------- //
		
		Clave c11021 = new Clave();
		c11021.setCodigo(BigInteger.valueOf(11021));
		c11021.setValor(year.toString());
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
		c11011.setValor(year.toString());
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
		Integer yearAux = year - 1; 
		c110219.setValor(yearAux.toString());
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
		
		Clave c110119 = new Clave();
		c110119.setCodigo(BigInteger.valueOf(110119));
		c110119.setValor(yearAux.toString());
		keys.getClave().add(c110119);
		
		Clave c11013 = new Clave();
		c11013.setCodigo(BigInteger.valueOf(11013));
		c11013.setValor("31");
		keys.getClave().add(c11013);
		
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
		
		/* INICIALIZAR LOS VALORES DE LAS CUENTAS ANUALES MEDIANTE SUS FORMULAS DE CÁLCULO. */

		AONContext ctx2 = null;
		try {
			ctx2 = AONContext.getAONContext( domain ,enterprise.getDomain());
			
			Map<String,String> computeMap = new HashMap<String, String>();
			
			//Valores de D2DepositHeaderKey
			AccMiningMVELContext acc = initializeHeaderKey(ctx2, year, type);
			for (String value : acc.keySet()) {
				if(!value.equalsIgnoreCase(ABREVIATE)){
					D2DepositHeaderKey k = D2DepositHeaderKey.valueOf(value);
					Clave clave = new Clave();
					Integer code = Integer.parseInt(k.getCode());
					clave.setCodigo(BigInteger.valueOf(code));
					clave.setValor(acc.get(value).toString());	
					keys.getClave().add(clave);
					computeMap.put(k.getCode(),acc.get(value).toString());
				}
			}
			
			//Valores de D2DepositKey
			AccMiningMVELContext acc2 = initializeKey(ctx2, year, type);
			for (String value : acc2.keySet()) {
				if(!value.equalsIgnoreCase(ABREVIATE)){
					D2DepositKey k = D2DepositKey.valueOf(value);
					Clave clave = new Clave();
					Integer code = Integer.parseInt(k.getCode());
					clave.setCodigo(BigInteger.valueOf(code));
					clave.setValor(acc2.get(value).toString());	
					keys.getClave().add(clave);
					computeMap.put(k.getCode(),acc2.get(value).toString());
				}
			}
			
			Map<String, String> c = compute(computeMap, type);
			for (String key : c.keySet()) {
				Clave clave = new Clave();
				clave.setCodigo(BigInteger.valueOf(Integer.parseInt(key)));
				clave.setValor(c.get(key));
				keys.getClave().add(clave);
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
		 
		AccountPeriod period =  AccountPeriodDAO.fetchOneByYear(ctx, year);
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
		return params;
	}
	
	public static AccMiningMVELContext initializeHeaderKey(AONContext ctx, Integer year, String type) {
		AccMiningMVELContext mvlCtx = new AccMiningMVELContext(ACCEPTER);
		AccMiningParameters params =  getParams(ctx, ctx.getDomainId(), year); //getParams(ctx,mod200);
		if (params != null) {
			mvlCtx.setAccounts(AON.getAccountBalances(ctx, params) );
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
			mvlCtx.setAccounts(AON.getAccountBalances(ctx, params) );
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

	public static Map<String, String> compute(Map<String, String> map, String type) {
		D2MVELContext ctx = new D2MVELContext(map, new IAccMiningKeyAccept() {
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
		for (String key : D2Compute.COMPUTE_MAP_CURRENT.keySet()) {
			String expression = D2Compute.COMPUTE_MAP_CURRENT.get(key);
			Object ret = ctx.evaluateExpression(key,expression);
			if (ret instanceof Double) {
				Double calculated = (Double) ret;
				ctx.put("Q"+key, calculated);
				
				if(map.containsKey(key)) map.remove(key);
				m.put(key, calculated.toString());
			}
		}
		return m;
	}
}
