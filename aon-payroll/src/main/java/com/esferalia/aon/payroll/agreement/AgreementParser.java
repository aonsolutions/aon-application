package com.esferalia.aon.payroll.agreement;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.payroll.tgss.creta.Bases.getDatabaseOption;
import static com.esferalia.aon.payroll.tgss.creta.Bases.getDbPasswordOption;
import static com.esferalia.aon.payroll.tgss.creta.Bases.getDbUserOption;
import static com.esferalia.aon.payroll.tgss.creta.Bases.getHostNameOption;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Scanner;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.esferalia.aon.jooq.tables.records.AgreementLevelRecord;
import com.esferalia.aon.jooq.tables.records.AgreementPaymentRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Pair;
import com.esferalia.aon.payroll.agreement.Agreement.AgreementLevel;
import com.esferalia.aon.payroll.agreement.Agreement.AgreementLevelData;
import com.esferalia.aon.payroll.agreement.ServiAgreement.Extension;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AgreementParser {
	
	// ---------------------------------------------------------- Variables
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private static Integer domainId = 0;
	private static List<AgreementPayment> agreementPayments;
	
	// ---------------------------------------------------------- Get Agreement Years
	
	public static List<Integer> getAgreementYears(String agreementCode) throws IllegalArgumentException {
		
		InputStream is = null;
		
		if(AonStringUtils.contains(agreementCode, 'a'))
			is = AgreementParser.class.getResourceAsStream(agreementCode + ".xml");
		else
			try {
				is = ServiAgreement.get_online_file(agreementCode, Extension.XML);
			} catch (Exception e) {
				throw new IllegalArgumentException("El convenio con c\u00F3digo " + agreementCode + " no es accesible en este momento. Por favor p\u00F3ngase en contacto con el departamento de soporte para poder ayudarle (no existe XML).");
			}
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder documentBuilder;
		
	    try {
			
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(is);
			
			return getAgreementYears(document);
			
		} catch (IllegalArgumentException | ParserConfigurationException | IOException | SAXException e) {
			if(e instanceof IllegalArgumentException)
				throw new IllegalArgumentException("El convenio con c\u00F3digo " + agreementCode + " no es accesible en este momento. Por favor p\u00F3ngase en contacto con el departamento de soporte para poder ayudarle.");
			
			e.printStackTrace();
			
			return Collections.emptyList();
		}
	}

	private static List<Integer> getAgreementYears(Document document) {
		List<Integer> agreementDates = new ArrayList<>();
		
		NodeList list = document.getElementsByTagName("DATOS_GENERALES");
		
		for(int i=0; i<list.getLength(); i++) {
			Node node = list.item(i);

	        if (node.getNodeType() == Node.ELEMENT_NODE) {

	            Element element = (Element) node;
	            
	            // From year
				String startDateYear = element.getElementsByTagName("AÑO_APLIC_DESDE").item(0).getTextContent();
				Integer startYear = Integer.parseInt(startDateYear);
				
				 // To year
				String endDateYear = element.getElementsByTagName("AÑO_APLIC_HASTA").item(0).getTextContent();
				Integer endYear = Integer.parseInt(endDateYear);
				
				Integer iteratorYear = startYear;
				while (iteratorYear <= endYear) {
					agreementDates.add(iteratorYear);
					iteratorYear++;
				}
				
	        }
		}
		
		return agreementDates;
	}
	
	// ---------------------------------------------------------- AgreementPayment
	
	private static void getAgreementPayments() throws IOException {
		agreementPayments = new ArrayList<>();
		
		InputStream is = AgreementParser.class.getResourceAsStream("AgreementPayment.txt");
		Scanner scaner = new Scanner(is);
		
		while(scaner.hasNextLine()) {
			String line = scaner.nextLine();
			String[] lineSplit = line.split(" :: ");
			
//			System.out.println(line);
			
			AgreementPayment agreementPayment = new AgreementPayment()
					.setName(lineSplit[0])
					.setDescription(lineSplit[1])
					.setConceptCode(lineSplit[2])
					.setExpression(lineSplit[3])
					.setType(Byte.parseByte(lineSplit[4]))
					.setPeriodicity(lineSplit[5]);
			
			agreementPayments.add(agreementPayment);
		}
		
		scaner.close();
		is.close();
	}
	
	private static AgreementPayment getAgreementPayment(String name) {
		Optional<AgreementPayment> agreementPayment = agreementPayments.stream().filter(agreementPaymentIt -> agreementPaymentIt.getName().equals(name)).findFirst();
		return agreementPayment.isPresent() ? agreementPayment.get() : null;
	}

	// ---------------------------------------------------------- Get Agreement

	public static Pair<Integer,String> getAgreement(DSLContext dslContext, String agreementCode, List<Integer> selectedDates, Integer domainIdIn) throws IllegalArgumentException, IOException {
		domainId = domainIdIn;
		String log = "";
		
		getAgreementPayments();
		
		Pair<Integer,String> agreementLog = new Pair<>(-1, "");
		Pair<Integer,Map<String, String>> insertResult = new Pair<>(-1, new HashMap<>());
		
		InputStream is = null;
		String userLogin = null;
		
		if(AonStringUtils.contains(agreementCode, 'a')) {
			is = AgreementParser.class.getResourceAsStream(agreementCode + ".xml");
			userLogin = "AonSolutions";
		}else {
			is = ServiAgreement.get_online_file(agreementCode, Extension.XML);
			userLogin = "ServiConvenios";
		}
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder documentBuilder;
		
	    try {
			
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(is);
			
			// Agreement general info
			Agreement agreement = getAgreementInfo(document);
			
			// Agreement Concepts
			getAgreementConcepts(document, agreement);
			
			// Agreement levels and categories
			getAgreementLevelAndCategory(document, agreement);
			
			// Agreement levels data
			getAgreementLevelData(dslContext, document, agreement, selectedDates);
			
			// Parse agreement to group leves
			agreement = parseAgreement(agreement);

			// Insert Agreement to DataBase
			insertResult = insertAgreementDB(dslContext, agreement, agreementCode, selectedDates, userLogin);

			Map<String, String> varNotInsertMap = insertResult.getSecond();
			
			if(!varNotInsertMap.isEmpty())
				log = getAgreementLog(dslContext, domainId, agreement, varNotInsertMap);
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("El convenio con c\u00F3digo " + agreementCode + " no es accesible en este momento. Por favor p\u00F3ngase en contacto con el departamento de soporte para poder ayudarle.");
		} catch (ParserConfigurationException | IOException | SAXException e) {
			e.printStackTrace();
		}
		
		agreementLog.setFirst(insertResult.getFirst());
		agreementLog.setSecond(log);
		
		return agreementLog;
	}
	
	// ---------------------------------------------------------- Get Agreement Log

	private static String getAgreementLog(DSLContext dslContext, Integer domainId, Agreement agreement, Map<String, String> varNotInsertMap) {
		// Domain Record
		Record domainRecord = dslContext.select().from(DOMAIN).where(DOMAIN.ID.eq(domainId)).fetchOne();
		
		StringBuilder htmlBuilder = new StringBuilder();
		
		htmlBuilder.append("<html>");
		
		htmlBuilder.append("<head>");
		htmlBuilder.append("<style>");
		htmlBuilder.append("#serviAgrement {");
			htmlBuilder.append("font-family: Arial, Helvetica, sans-serif;");
			htmlBuilder.append("border-collapse: collapse;");
			htmlBuilder.append("width: 100%;");
			htmlBuilder.append("}");

		htmlBuilder.append("#serviAgrement td, #serviAgrement th {");
			htmlBuilder.append("border: 1px solid #ddd;");
			htmlBuilder.append("padding: 8px;");
			htmlBuilder.append("}");

		htmlBuilder.append("#serviAgrement tr:nth-child(even){background-color: #f2f2f2;}");

		htmlBuilder.append("#serviAgrement tr:hover {background-color: #ddd;}");

		htmlBuilder.append("#serviAgrement th {");
			htmlBuilder.append("padding-top: 12px;");
			htmlBuilder.append("padding-bottom: 12px;");
			htmlBuilder.append("text-align: left;");
			htmlBuilder.append("background-color: #0065a8;");
			htmlBuilder.append("color: white;");
			htmlBuilder.append("}");
		htmlBuilder.append("</style>");
		htmlBuilder.append("</head>");
		
		htmlBuilder.append("<body>");
		
		htmlBuilder.append("<div style=\"font-family: \"Lucida Sans Unicode\", \"Lucida Grande\", sans-serif;font-size: 12px;letter-spacing: 2px;word-spacing: 0px;color: #000000;font-weight: normal;text-decoration: none;font-style: normal;font-variant: normal;text-transform: none;\">");
		htmlBuilder.append("<p>Estimado desarrollador:</p>");
		htmlBuilder.append("<p>Le adjuntamos el log generado a la hora de intentar importar un convenio desde la plataforma de ServiConvenios.</p>");
		
		// Domain Data
		
		htmlBuilder.append("<ul>");
		htmlBuilder.append("<li>");
		htmlBuilder.append("<a style=\"font-weight: bold; color: black;\"> Domain Id : </a>" + domainId);
		htmlBuilder.append("</li>");
		htmlBuilder.append("<li>");
		htmlBuilder.append("<a style=\"font-weight: bold; color: black;\"> Domain URL : </a>" + domainRecord.get(DOMAIN.NAME));
		htmlBuilder.append("</li>");
		htmlBuilder.append("<li>");
		htmlBuilder.append("<a style=\"font-weight: bold; color: black;\"> Domain Description : </a>" + domainRecord.get(DOMAIN.DESCRIPTION));
		htmlBuilder.append("</li>");
		htmlBuilder.append("</ul>");
		
		// Domain Data
		
		htmlBuilder.append("<ul>");
		htmlBuilder.append("<li>");
		htmlBuilder.append("<a style=\"font-weight: bold; color: black;\"> Agreement SS Code : </a>" + agreement.getSSCode());
		htmlBuilder.append("</li>");
		htmlBuilder.append("<li>");
		htmlBuilder.append("<a style=\"font-weight: bold; color: black;\"> Agreement Description : </a>" + agreement.getAgreementDescription().toUpperCase());
		htmlBuilder.append("</li>");
		htmlBuilder.append("<li>");
		htmlBuilder.append("<a style=\"font-weight: bold; color: black;\"> ServiAgreement Code : </a>" + agreement.getServiAgreementCode());
		htmlBuilder.append("</li>");
		htmlBuilder.append("</ul>");
		
		htmlBuilder.append("<br>");
		
		htmlBuilder.append("<p>Variables que no se han podido insertar : </p>");
		
		htmlBuilder.append("<br>");
		
		htmlBuilder.append("<table id=\"serviAgrement\">");
		htmlBuilder.append("<tr>");
		htmlBuilder.append("<th>Nombre ServiConvenios</th>");
		htmlBuilder.append("<th>Nombre AON (Revisar)</th>");
		htmlBuilder.append("</tr>");

		for(Entry<String, String> entry: varNotInsertMap.entrySet()) {
			htmlBuilder.append("<tr>");
			htmlBuilder.append("<td>" + entry.getKey() + "</td>");
			htmlBuilder.append("<td>" + entry.getValue() + "</td>");
			htmlBuilder.append("</tr>");
		}
		
		htmlBuilder.append("</table>");
		
		htmlBuilder.append("</div>");
		htmlBuilder.append("</body>");
		htmlBuilder.append("</html>");
		
		return htmlBuilder.toString();
	}
	
	// ------------------------------------------------------------ AGREEMENT INFO

	private static Agreement getAgreementInfo(Document document) {
		Agreement agreement = null;
		NodeList list = document.getElementsByTagName("DATOS_GENERALES");
		
		for(int i=0; i<list.getLength(); i++) {
			Node node = list.item(i);

	        if (node.getNodeType() == Node.ELEMENT_NODE) {

	            Element element = (Element) node;
	            
	            String description = element.getElementsByTagName("NOMBRE").item(0).getTextContent();
	            String ssCode = element.getElementsByTagName("CODIGO_SS").item(0).getTextContent();
	            String serviAgreementCode = element.getElementsByTagName("FICHEROS").item(0).getTextContent();
	            
	            Date lastUpdate = null;
				try {
					lastUpdate = dateFormat.parse(element.getElementsByTagName("FECHA_ULT_ACT").item(0).getTextContent());
				} catch (DOMException | ParseException e) {
					e.printStackTrace();
				}
				
				String startDateYear = element.getElementsByTagName("AÑO_APLIC_DESDE").item(0).getTextContent();
				Calendar startDateCal = Calendar.getInstance();
				startDateCal.set(Calendar.YEAR, Integer.parseInt(startDateYear));
				startDateCal.set(Calendar.MONTH, 0);
				startDateCal.set(Calendar.DAY_OF_MONTH, 1);
				Date startDate = startDateCal.getTime();
				
	            agreement = new Agreement(description, ssCode, serviAgreementCode, lastUpdate, startDate);
	     
	        }
		}
		
		return agreement;
	}
	
	private static void getAgreementConcepts(Document document, Agreement agreement) {
		NodeList listCPR = document.getElementsByTagName("CATALOGO_CPTOS_RETRIB");
//		System.out.println("----- CONCEPTS -----");
		for(int i=0; i<listCPR.getLength(); i++) {
			Node nodeCPR = listCPR.item(i);

	        if (nodeCPR.getNodeType() == Node.ELEMENT_NODE) {

	            Element elementCPR = (Element) nodeCPR;
	            
	            NodeList listCI = elementCPR.getElementsByTagName("CPTO_IT");
	            
	            for(int j=0; j<listCI.getLength(); j++) {
	    			Node nodeCI = listCI.item(j);

	    	        if (nodeCI.getNodeType() == Node.ELEMENT_NODE) {
	    	        	Element elementCI = (Element) nodeCI;
	    	        	
	    	        	String name = elementCI.getElementsByTagName("NOMBRE").item(0).getTextContent();
	    	        	 String type = (null == elementCI.getElementsByTagName("TIPO_AMDH") || null == elementCI.getElementsByTagName("TIPO_AMDH").item(0))
		    	            		? null 
		    	            		: elementCI.getElementsByTagName("TIPO_AMDH").item(0).getTextContent();
 	            
	    	            String realName = getParseName(name, type);
	    	            
//	    	            System.out.println(realName);
	    	            
	    	            agreement.addAgreementConcept(realName);
	    	        }
	            }   
	        }
		}
		System.out.println("----------");
	}

	private static void getAgreementLevelAndCategory(Document document, Agreement agreement) {
//		System.out.println("--------- AGREMENT LEVELS -----------");
		NodeList list = document.getElementsByTagName("CATALOGO_CAT_PROF");
		for(int i=0; i<list.getLength(); i++) {
			Node node = list.item(i);

	        if (node.getNodeType() == Node.ELEMENT_NODE) {

	            Element element = (Element) node;
	            
	            NodeList listCatProfIt = element.getElementsByTagName("CAT_PROF_IT");
	            
	            for(int j=0; j<listCatProfIt.getLength(); j++) {
	    			Node nodeCatProfIt = listCatProfIt.item(j);

	    	        if (nodeCatProfIt.getNodeType() == Node.ELEMENT_NODE) {

	    	            Element elementCatProfIt = (Element) nodeCatProfIt;
	    	            
	    	            String code = elementCatProfIt.getElementsByTagName("CODIGO").item(0).getTextContent();
	    	            
	    	            NodeList listdescriptions = elementCatProfIt.getElementsByTagName("GRUPO");
	    	            String description = "";
	    	            
	    	            if(listdescriptions.getLength() == 0) {
	    	            	description = "NIVEL " + (j+1);
	    	            } else {
		    	            for(int b=0; b<listdescriptions.getLength(); b++)
		    	            	description += listdescriptions.item(b).getTextContent() + " ";
		    	            description = description.trim();
	    	            }
	    	            
	    	            String category = elementCatProfIt.getElementsByTagName("NOMBRE").item(0).getTextContent();
	    	            
	    	            agreement.addAgreementLevel(code, description, category);
	    	            
//	    	            System.out.println("AL -> code : " + code + ", description : " + description + ", category : " + category);
	    	            
	    	        }
	    		}
	        }
		}
	}
	
	private static void getAgreementLevelData(DSLContext dslContext, Document document, Agreement agreement, List<Integer> selectedDates) {
//		System.out.println("\n\n------------- TABLAS SALARIALES ---------------");
		NodeList listTS = document.getElementsByTagName("TABLAS_SALARIALES");
		for(int i=0; i<listTS.getLength(); i++) {
			Node nodeTS = listTS.item(i);

	        if (nodeTS.getNodeType() == Node.ELEMENT_NODE) {

	            Element elementTS = (Element) nodeTS;
	            
	            NodeList listTSI = elementTS.getElementsByTagName("TAB_SAL_IT");
	            
	            for(int j=0; j<listTSI.getLength(); j++) {
	    			Node nodeTSI = listTSI.item(j);

	    	        if (nodeTSI.getNodeType() == Node.ELEMENT_NODE) {

	    	            Element elementTSI = (Element) nodeTSI;
	    	            
	    	            String yearStr = elementTSI.getElementsByTagName("AÑO").item(0).getTextContent();
	    	            Integer year = Integer.parseInt(yearStr);
	    	            
	    	            if(!selectedDates.contains(year))
	    	            	continue;
	    	            
	    	            // Calendar StartDate
	    	            Calendar startDate = Calendar.getInstance();
	    	            startDate.set(Calendar.YEAR, year);
	    	            startDate.set(Calendar.MONTH, 0);
	    	            startDate.set(Calendar.DAY_OF_MONTH, 1);
	    	            
	    	            // EndDate
	    	            Calendar endDate = Calendar.getInstance();
	    	            endDate.set(Calendar.YEAR, year);
	    	            endDate.set(Calendar.MONTH, 11);
	    	            endDate.set(Calendar.DAY_OF_MONTH, 31);
	    	            
	    	            // Check if has explicit period
	    	            if(null != elementTSI.getElementsByTagName("PERIODO")) {
	    	            	NodeList listPeriod =  elementTSI.getElementsByTagName("PERIODO");
	    	            	
	    	            	for(int n=0; n<listPeriod.getLength(); n++) {
	    	            		Node nodePeriod = listPeriod.item(n);
	    	            		
	    	            		 if (nodePeriod.getNodeType() == Node.ELEMENT_NODE) {
	    	            			 Element elementPeriod = (Element) nodePeriod;
	    	            			 
	    	            			 String tillPeriod = elementPeriod.getElementsByTagName("DESDE_DD_MM").item(0).getTextContent();
    		    	            	 String toPeriod = elementPeriod.getElementsByTagName("HASTA_DD_MM").item(0).getTextContent();
    		    	            	
    		    	            	 try {
    		    	            		 
    		    	            		endDate.set(Calendar.YEAR, year);
    		    	            	
    			    	             	startDate.set(Calendar.MONTH, Integer.parseInt(tillPeriod.split("-")[1]) - 1);
    			 	    	            startDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tillPeriod.split("-")[0]));
    			 	    	             
    			 	    	            endDate.set(Calendar.MONTH,  Integer.parseInt(toPeriod.split("-")[1]) - 1);
    			 	    	            endDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(toPeriod.split("-")[0]));
    		 	    	            
    		    	            	 } catch (Exception e) {
    									System.err.println("Algo ha ido mal con los periodos");
    								 }
	    	            		 }
	    	            	}
	    	            	 
	    	            } else
	    	            	agreement.setEndDateToExistingLevelData(endDate.getTime());
	    	            
	    	            NodeList listCP = elementTSI.getElementsByTagName("CATEGORIAS_PROFESIONALES");
	    	            
	    	            for(int k=0; k<listCP.getLength(); k++) {
	    	            	Node nodeCP = listCP.item(k);
	    	            	
	    	            	if (nodeCP.getNodeType() == Node.ELEMENT_NODE) {
	    	            		Element elementCP = (Element) nodeCP;
	    	     	            
	    	     	            NodeList listCPI = elementCP.getElementsByTagName("CAT_PROF_IT");
	    	     	            
	    	     	            for(int l=0; l<listCPI.getLength(); l++) {
	    	     	            	Node nodeCPI = listCPI.item(l);
	    	     	            	
	    	     	            	if (nodeCPI.getNodeType() == Node.ELEMENT_NODE) {
	    	    	            		Element elementCPI = (Element) nodeCPI;

		    	     	            	NodeList listdescriptions = elementCPI.getElementsByTagName("GRUPO");
		    	     	            	NodeList listcodes = elementCPI.getElementsByTagName("CODIGO");
		    		    	            String description = "";
		    		    	            
		    		    	            if(listdescriptions.getLength() == 0)
		    		    	            	description = "NIVEL " + (listcodes.getLength() == 0 ? (l+1) : listcodes.item(0).getTextContent());
		    		    	            else {
			    		    	            for(int b=0; b<listdescriptions.getLength(); b++)
			    		    	            	description += listdescriptions.item(b).getTextContent() + " ";
			    		    	            description = description.trim();
		    		    	            }
		    		    	            
			   	    	            	String category = elementCPI.getElementsByTagName("NOMBRE").item(0).getTextContent();
			   	    	            	
				   	    	            AgreementLevel agreementLevel = agreement.getAgreementLevel(description, category);
				   	    	            
//				   	    	            System.out.println("description : " + description + ", category : " + category);
					    	           
			   	    	            	Node nodeConcept = elementCPI.getElementsByTagName("CONCEPTOS").item(0);
			   	    	            	if (null != nodeConcept && nodeConcept.getNodeType() == Node.ELEMENT_NODE) {
			   	    	            		Element elementConcept = (Element) nodeConcept;
			   	    	            		
			   	    	            		NodeList listCPTO = elementConcept.getElementsByTagName("CPTO_IT");
				   	    	            	
				   	    	            	for(int b=0; b<listCPTO.getLength(); b++) {
				    	     	            	Node nodeCPTO = listCPTO.item(b);
				    	     	            	
				    	     	            	if (nodeCPTO.getNodeType() == Node.ELEMENT_NODE) {
				    	     	            		Element elementCPTO = (Element) nodeCPTO;
				    	     	            		
				    	     	            		if(null != elementCPTO.getElementsByTagName("IMPORTE") && null != elementCPTO.getElementsByTagName("IMPORTE").item(0)) {
				    	     	            		
					    	     	            		String name = elementCPTO.getElementsByTagName("NOMBRE").item(0).getTextContent();
					    	     	            		String type = (null == elementCPTO.getElementsByTagName("TIPO_AMDH") || null == elementCPTO.getElementsByTagName("TIPO_AMDH").item(0))
									    	            		? null 
									    	            		: elementCPTO.getElementsByTagName("TIPO_AMDH").item(0).getTextContent();
							    	           
							   	    	            	String realName = getParseName(name, type);
							   	    	            	
							   	    	            	try {
							   	    	            		String value = elementCPTO.getElementsByTagName("IMPORTE").item(0).getTextContent();
							   	    	            		
							   	    	            		if(null != elementTSI.getElementsByTagName("PERIODO"))
								   	    	            		agreementLevel.addLevelData(realName, value, startDate.getTime(), endDate.getTime());
								   	    	            	else
								   	    	            		agreementLevel.addLevelData(realName, value, startDate.getTime());
								   	    	            	
							   	    	            	} catch (NullPointerException e) {
															System.err.println("------- ERROR ------\nName : " + name + "\nType : " + type + "\nRealName : " + realName + "\nDate : " + startDate.getTime());
														}
							   	    	            	
				    	     	            		}
				    	     	            	}
				   	    	            	}
			   	    	            	}
	    	     	            	}
	    	     	            } 
	    	            	}
	    	            }
	    	        }
	    		}
	        }
		}
	}
	
	// ------------------------------------------------------------ PARSE AGREEMENT
	
	private static Agreement parseAgreement(Agreement agreement) {
		Agreement parsedAgreement = new Agreement(
				agreement.getAgreementDescription(), 
				agreement.getSSCode(), 
				agreement.getServiAgreementCode(), 
				agreement.getLastUpdate(), 
				agreement.getStartDate());
		
		parsedAgreement.setAgreementConcepts(agreement.getAgreementConcepts());
		
		List<AgreementLevel> analizedAgreementLevels = new ArrayList<>();
		
		// Sort levels
		Collections.sort(agreement.getAgreementLevels(), new Comparator<AgreementLevel>() {
			@Override
			public int compare(AgreementLevel al1, AgreementLevel al2) {
				return al1.getDescription().compareTo(al2.getDescription());
			}
		});
		
		for(AgreementLevel agreementLevel : agreement.getAgreementLevels()) {
			
			List<AgreementLevel> duplicateAgreementLevels = getDuplicateAgreementLevels(agreement, agreementLevel, analizedAgreementLevels);
			analizedAgreementLevels.addAll(duplicateAgreementLevels);
			
			AgreementLevel newAgreementLevel = parsedAgreement.createAgreementLevel(agreementLevel.getCode(), agreementLevel.getDescription());
			newAgreementLevel.setLevelDatas(agreementLevel.getLevelDatas());
			
			List<String> categories = new ArrayList<>();
			
			for(AgreementLevel duplicateAgreementLevel : duplicateAgreementLevels)
				categories.addAll(duplicateAgreementLevel.getCategories());
				
			newAgreementLevel.setCategories(categories);
			
			if(!categories.isEmpty())
				parsedAgreement.addAgreementLevel(newAgreementLevel);
		}
		
		return parsedAgreement;
	}
	
	private static List<AgreementLevel> getDuplicateAgreementLevels(Agreement agreement, AgreementLevel checkedAgreementLevel, List<AgreementLevel> analizedAgreementLevels) {
		List<AgreementLevel> duplicateAgreementLevels = new ArrayList<>();
		
		for(AgreementLevel agreementLevel : agreement.getAgreementLevels()) {
			if(!analizedAgreementLevels.contains(agreementLevel) && isSameLevelAndValues(agreementLevel, checkedAgreementLevel))
				duplicateAgreementLevels.add(agreementLevel);
		}
		
		return duplicateAgreementLevels;
	}

	private static boolean isSameLevelAndValues(AgreementLevel agreementLevel, AgreementLevel checkedAgreementLevel) {
		return AonStringUtils.equalsIgnoreCase(checkedAgreementLevel.getDescription(), agreementLevel.getDescription()) && haveSameValues(agreementLevel, checkedAgreementLevel);
	}

	private static boolean haveSameValues(AgreementLevel agreementLevel, AgreementLevel checkedAgreementLevel) {
		// Sort levels
		Collections.sort(agreementLevel.getLevelDatas(), new Comparator<AgreementLevelData>() {
			@Override
			public int compare(AgreementLevelData ald1, AgreementLevelData ald2) {
				return ald1.getName().compareTo(ald2.getName());
			}
		});
		
		Collections.sort(checkedAgreementLevel.getLevelDatas(), new Comparator<AgreementLevelData>() {
			@Override
			public int compare(AgreementLevelData ald1, AgreementLevelData ald2) {
				return ald1.getName().compareTo(ald2.getName());
			}
		});
		
		if (agreementLevel.getLevelDatas().size() == checkedAgreementLevel.getLevelDatas().size()) {
			
			for(int i=0; i<agreementLevel.getLevelDatas().size(); i++) {
				
				AgreementLevelData levelData = agreementLevel.getLevelDatas().get(i);
				AgreementLevelData checkedLevelData = checkedAgreementLevel.getLevelDatas().get(i);
				
				if(	!AonStringUtils.equalsIgnoreCase(checkedLevelData.getName(), levelData.getName()) || 
					!AonStringUtils.equalsIgnoreCase(checkedLevelData.getValue(), levelData.getValue()) || 
					!checkedLevelData.getStartDate().equals(levelData.getStartDate()))
					return false;
			}
			
		} else return false;
		
		return true;
	}
	
	// ------------------------------------------------------------ INSERT AGREEMENT 

	private static Pair<Integer,Map<String, String>> insertAgreementDB(DSLContext dslContext, Agreement agreement, String agreementCode, List<Integer> selectedDates, String userLogin) {
		Pair<Integer,Map<String, String>> result = new Pair<>(-1, new HashMap<>());
		java.sql.Date creationDate = new java.sql.Date(new Date().getTime());
		
		dslContext.transaction(t -> {
		
			boolean hasWinterPay = false;
			boolean hasSummerPay = false;
			
			selectedDates.sort((o1, o2) -> o1.compareTo(o2));
			Integer startYear = selectedDates.get(0);
			Calendar startDateCal = Calendar.getInstance();
			startDateCal.set(Calendar.DAY_OF_MONTH, 1);
			startDateCal.set(Calendar.MONTH, 0);
			startDateCal.set(Calendar.YEAR, startYear);
			
			VariablesMap variablesMap = new VariablesMap();
			
			// Variables not insert
			Map<String, String> mapVarNotInsert = new TreeMap<>();
			
			// Agreement
			AgreementRecord agreementRecord = dslContext.insertInto(AGREEMENT)
				.set(AGREEMENT.DOMAIN, domainId)
				.set(AGREEMENT.DESCRIPTION, parseDescription(agreement.getAgreementDescription()))
				.set(AGREEMENT.SS_NUMBER, agreement.getSSCode())
				.set(AGREEMENT.CREATION_USER, userLogin)
				.set(AGREEMENT.CREATION_DATE, creationDate)
				.returning(AGREEMENT.ID)
				.fetchOne();
			
			Integer agreementId = agreementRecord.getId();
			
			// Agreement Level / Agreement Level Category / Agreement Level Data
			
			String oldLevelDescription = null;
			Integer count = 1;
			
			for(AgreementLevel lvl : agreement.getAgreementLevels()) {
				
				// Agreement Level
				
				String levelDescription = parseLevelDescription(lvl.getDescription());
				
				if(null != oldLevelDescription && AonStringUtils.equalsIgnoreCase(oldLevelDescription, levelDescription)) {
					if(count > 99 && levelDescription.length() >= 60)
						levelDescription = levelDescription.substring(0, 60);
					else if(count >= 10 && levelDescription.length() >= 61)
						levelDescription = levelDescription.substring(0, 61);
					else if(levelDescription.length() >= 62)
						levelDescription = levelDescription.substring(0, 61);
					levelDescription = levelDescription + "_" + count;
					count++;
				} else {
					oldLevelDescription = levelDescription;
					count = 1;
				}
				
				AgreementLevelRecord agreementLevelRecord = dslContext.insertInto(AGREEMENT_LEVEL)
					.set(AGREEMENT_LEVEL.DOMAIN, domainId)
					.set(AGREEMENT_LEVEL.AGREEMENT, agreementId)
					.set(AGREEMENT_LEVEL.DESCRIPTION, levelDescription)
					.returning(AGREEMENT_LEVEL.ID)
					.fetchOne();
				
				Integer agreementLevelId = agreementLevelRecord.getId();
				
				// Agreement Level Category
				
				for(String lvlCategory : lvl.getLevelCategories()) {
					dslContext.insertInto(AGREEMENT_LEVEL_CATEGORY)
						.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, domainId)
						.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevelId)
						.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, parseDescription(lvlCategory))
						.execute();
				}
				
				// Agreement Level Data
				
				for(AgreementLevelData lvlData : lvl.getLevelDatas()) {
					String realName = variablesMap.getVariablesMap().getOrDefault(lvlData.getName(), null);
					
					if(null != realName) {
						if((AonStringUtils.containsIgnoreCase(realName, "PAGA") || AonStringUtils.containsIgnoreCase(realName, "P_E_") || AonStringUtils.containsIgnoreCase(realName, "EXTRA")) && (AonStringUtils.containsIgnoreCase(realName, "VERANO") || AonStringUtils.containsIgnoreCase(realName, "JUNIO")))
							hasSummerPay = true;
						
						if((AonStringUtils.containsIgnoreCase(realName, "PAGA") || AonStringUtils.containsIgnoreCase(realName, "P_E_") || AonStringUtils.containsIgnoreCase(realName, "GRATIFICACION")) && (AonStringUtils.containsIgnoreCase(realName, "NAVIDAD") || AonStringUtils.containsIgnoreCase(realName, "DICIEMBRE")))
							hasWinterPay = true;
						
						if(AonStringUtils.equalsIgnoreCase(realName, "PAGA_EXTRA_MENSUAL") || AonStringUtils.equalsIgnoreCase(realName, "VACACIONES")) {
							hasSummerPay = true;
							hasWinterPay = true;
						}
						
//						System.out.println("realName : " + realName);
						
						dslContext.insertInto(AGREEMENT_LEVEL_DATA)
							.set(AGREEMENT_LEVEL_DATA.DOMAIN, domainId)
							.set(AGREEMENT_LEVEL_DATA.NAME, realName)
							.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, agreementLevelId)
							.set(AGREEMENT_LEVEL_DATA.EXPRESSION, "/*inherit*/" + lvlData.getValue() + "/**/")
							.set(AGREEMENT_LEVEL_DATA.START_DATE, parseDateToSql(lvlData.getStartDate()))
							.set(AGREEMENT_LEVEL_DATA.END_DATE, parseDateToSql(lvlData.getEndDate()))
							.set(AGREEMENT_LEVEL_DATA.CREATION_USER, userLogin)
							.set(AGREEMENT_LEVEL_DATA.CREATION_DATE, creationDate)
							.execute();
					} else {
						if( !AonStringUtils.containsIgnoreCase(lvlData.getName(), "TOTAL") &&
							!AonStringUtils.containsIgnoreCase(lvlData.getName(), "PAGA_EXTRA") &&
							!AonStringUtils.containsIgnoreCase(lvlData.getName(), "PAGAS_EXTRA") &&
							!AonStringUtils.containsIgnoreCase(lvlData.getName(), "+") &&
							!AonStringUtils.containsIgnoreCase(lvlData.getName(), "%"))
								
								mapVarNotInsert.put(lvlData.getName(), lvlData.getName());
					}
				}
				
			}
			
			// Agreement Data
			
			Date auxEndDate = null;

			Calendar defaultPaymentStartDate = Calendar.getInstance();
			defaultPaymentStartDate.set(Calendar.DAY_OF_MONTH, 1);
			defaultPaymentStartDate.set(Calendar.MONTH, 0);
			defaultPaymentStartDate.set(Calendar.YEAR, 1970);
			
			dslContext.insertInto(AGREEMENT_DATA)
				.set(AGREEMENT_DATA.DOMAIN, domainId)
				.set(AGREEMENT_DATA.NAME, "PAGAS")
				.set(AGREEMENT_DATA.AGREEMENT, agreementId)
				.set(AGREEMENT_DATA.EXPRESSION, "14")
				.set(AGREEMENT_DATA.START_DATE, parseDateToSql(startDateCal.getTime()))
				.set(AGREEMENT_DATA.END_DATE, parseDateToSql(auxEndDate))
				.execute();
			
			// Agreement Payment
			
			for(String agreementConceptName : agreement.getAgreementConcepts()) {
				AgreementPayment agreementPayment = getAgreementPayment(agreementConceptName);
				
				if(null != agreementPayment) {
					
//					System.out.println(agreementPayment.getConceptCode());
					
					String irpfExpression = "_P";
					String quoteExpression = "_P";
					
					if(AonStringUtils.containsIgnoreCase(agreementPayment.getConceptCode(), "MANUTENCION")) {
						irpfExpression = "EXCESO(26.67 * DIAS_MANUTENCION)";
						quoteExpression = "EXCESO(26.67 * DIAS_MANUTENCION)";
					} else if(AonStringUtils.containsIgnoreCase(agreementPayment.getConceptCode(), "PERNOCTA")) {
						irpfExpression = "EXCESO(53.34 * DIAS_PERNOCTA)";
						quoteExpression = "EXCESO(53.34 * DIAS_PERNOCTA)";
					} else if(AonStringUtils.containsIgnoreCase(agreementPayment.getConceptCode(), "LOCOMOCI") ||
							AonStringUtils.containsIgnoreCase(agreementPayment.getConceptCode(), "IMPORTE_KM") ||
							AonStringUtils.containsIgnoreCase(agreementPayment.getConceptCode(), "KM")) {
						irpfExpression = "EXCESO(0.26 * KMS)";
						quoteExpression = "EXCESO(0.26 * KMS)";
					}
					
					Result<PaymentConceptRecord> paymentConcepts = dslContext.selectFrom(PAYMENT_CONCEPT)
						.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
						.and(PAYMENT_CONCEPT.CODE.eq(agreementPayment.getConceptCode()))
						.and(PAYMENT_CONCEPT.DESCRIPTION.contains(agreementPayment.getPeriodicity()))
						.fetch();
					
					Integer paymentConceptId = null;
					
					if(paymentConcepts.isEmpty()) {
						PaymentConceptRecord paymentConceptRecord = dslContext.insertInto(PAYMENT_CONCEPT)
								.set(PAYMENT_CONCEPT.DOMAIN, domainId)
								.set(PAYMENT_CONCEPT.CODE, agreementPayment.getConceptCode())
								.set(PAYMENT_CONCEPT.DESCRIPTION, agreementPayment.getDescription())
								.set(PAYMENT_CONCEPT.TYPE, agreementPayment.getType())
								.set(PAYMENT_CONCEPT.DESCRIPTION_DECORABLE, (byte)0)
								.set(PAYMENT_CONCEPT.EXPRESSION, agreementPayment.getExpression())
								.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, irpfExpression)
								.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, quoteExpression)
								.returning(PAYMENT_CONCEPT.ID)
								.fetchOne();
						
						paymentConceptId = paymentConceptRecord.getId();
					} else
						paymentConceptId = paymentConcepts.get(0).getId();
					
					AgreementPaymentRecord agreementPaymentRecord = dslContext.insertInto(AGREEMENT_PAYMENT)
							.set(AGREEMENT_PAYMENT.DOMAIN, domainId)
							.set(AGREEMENT_PAYMENT.AGREEMENT, agreementId)
							.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, paymentConceptId)
							.set(AGREEMENT_PAYMENT.TYPE, agreementPayment.getType())
							.set(AGREEMENT_PAYMENT.EXPRESSION, "/*inherit*/" + agreementPayment.getExpression() + "/**/")
							.set(AGREEMENT_PAYMENT.DESCRIPTION, agreementPayment.getDescription())
							.set(AGREEMENT_PAYMENT.START_DATE, parseDateToSql(defaultPaymentStartDate.getTime()))
							.set(AGREEMENT_PAYMENT.END_DATE, parseDateToSql(auxEndDate))
							.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 0)
							.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, irpfExpression)
							.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, quoteExpression)
							.set(AGREEMENT_PAYMENT.CREATION_USER, userLogin)
							.set(AGREEMENT_PAYMENT.CREATION_DATE, creationDate)
							.returning(AGREEMENT_PAYMENT.ID)
							.fetchOne();
					
					Integer agreementPaymentId = agreementPaymentRecord.getId();
					
					// Summer agreement extra
					if(!(AonStringUtils.containsIgnoreCase(agreementPayment.getDescription(), "PAGA") && AonStringUtils.containsIgnoreCase(agreementPayment.getDescription(), "VERANO") && AonStringUtils.containsIgnoreCase(agreementPayment.getDescription(), "NAVIDAD"))) {
						
						if(!hasSummerPay && AonStringUtils.containsIgnoreCase(agreementPayment.getDescription(), "PAGA") && (AonStringUtils.containsIgnoreCase(agreementPayment.getDescription(), "VERANO") || AonStringUtils.containsIgnoreCase(agreementPayment.getDescription(), "JUNIO"))) {
							dslContext.insertInto(AGREEMENT_EXTRA)
								.set(AGREEMENT_EXTRA.DOMAIN, domainId)
								.set(AGREEMENT_EXTRA.AGREEMENT, agreementId)
								.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, agreementPaymentId)
								.set(AGREEMENT_EXTRA.START_DATE, "01/01")
								.set(AGREEMENT_EXTRA.END_DATE, "30/06")
								.set(AGREEMENT_EXTRA.ISSUE_DATE, "31/7")
								.execute();
							
							dslContext.update(AGREEMENT_PAYMENT)
								.set(AGREEMENT_PAYMENT.MONTH, (byte)6)
								.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte)1)
								.where(AGREEMENT_PAYMENT.ID.eq(agreementPaymentId))
								.execute();
						}
						
						// Winter agreement extra
						if(!hasWinterPay && AonStringUtils.containsIgnoreCase(agreementPayment.getDescription(), "PAGA") && AonStringUtils.containsIgnoreCase(agreementPayment.getDescription(), "NAVIDAD")) {
							dslContext.insertInto(AGREEMENT_EXTRA)
								.set(AGREEMENT_EXTRA.DOMAIN, domainId)
								.set(AGREEMENT_EXTRA.AGREEMENT, agreementId)
								.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, agreementPaymentId)
								.set(AGREEMENT_EXTRA.START_DATE, "01/07")
								.set(AGREEMENT_EXTRA.END_DATE, "31/12")
								.set(AGREEMENT_EXTRA.ISSUE_DATE, "31/12")
								.execute();
							
							dslContext.update(AGREEMENT_PAYMENT)
								.set(AGREEMENT_PAYMENT.MONTH, (byte)11)
								.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte)1)
								.where(AGREEMENT_PAYMENT.ID.eq(agreementPaymentId))
								.execute();
						}
					}
					
					// Benefits PLUS_FIESTAS_PATRONALES_ANUAL agreement extra
					if(AonStringUtils.containsIgnoreCase(agreementPayment.getDescription(), "PAGA") && AonStringUtils.containsIgnoreCase(agreementPayment.getDescription(), "BENEFICIOS")) {
						dslContext.insertInto(AGREEMENT_EXTRA)
							.set(AGREEMENT_EXTRA.DOMAIN, domainId)
							.set(AGREEMENT_EXTRA.AGREEMENT, agreementId)
							.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, agreementPaymentId)
							.set(AGREEMENT_EXTRA.START_DATE, "01/01 -1")
							.set(AGREEMENT_EXTRA.END_DATE, "31/12 -1")
							.set(AGREEMENT_EXTRA.ISSUE_DATE, "31/3")
							.execute();
						
						dslContext.update(AGREEMENT_PAYMENT)
							.set(AGREEMENT_PAYMENT.MONTH, (byte)2)
							.where(AGREEMENT_PAYMENT.ID.eq(agreementPaymentId))
							.execute();
					}
					
					// Benefits PLUS_PAGA_OCTUBRE_ANUAL agreement extra
					if(AonStringUtils.containsIgnoreCase(agreementPayment.getDescription(), "PAGA") && AonStringUtils.containsIgnoreCase(agreementPayment.getDescription(), "OCTUBRE")) {
						dslContext.insertInto(AGREEMENT_EXTRA)
							.set(AGREEMENT_EXTRA.DOMAIN, domainId)
							.set(AGREEMENT_EXTRA.AGREEMENT, agreementId)
							.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, agreementPaymentId)
							.set(AGREEMENT_EXTRA.START_DATE, "01/11 -1")
							.set(AGREEMENT_EXTRA.END_DATE, "31/10")
							.set(AGREEMENT_EXTRA.ISSUE_DATE, "15/10")
							.execute();
						
						dslContext.update(AGREEMENT_PAYMENT)
							.set(AGREEMENT_PAYMENT.MONTH, (byte)2)
							.where(AGREEMENT_PAYMENT.ID.eq(agreementPaymentId))
							.execute();
					}
				}
			}
			
			Integer paymentConceptId = insertOrGetPaymentConceptExtraPay(dslContext);
			AgreementPaymentRecord agreementPaymentRecord = null;
			Integer agreementPaymentId = null;
			
			if(!hasSummerPay) {
			
				agreementPaymentRecord = dslContext.insertInto(AGREEMENT_PAYMENT)
					.set(AGREEMENT_PAYMENT.DOMAIN, domainId)
					.set(AGREEMENT_PAYMENT.AGREEMENT, agreementId)
					.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, paymentConceptId)
					.set(AGREEMENT_PAYMENT.TYPE, (byte)4)
					.set(AGREEMENT_PAYMENT.EXPRESSION, "/*inherit*/" + "SALARIO_BASE" + "/**/")
					.set(AGREEMENT_PAYMENT.DESCRIPTION, "[90] PAGA VERANO")
					.set(AGREEMENT_PAYMENT.START_DATE, parseDateToSql(defaultPaymentStartDate.getTime()))
					.set(AGREEMENT_PAYMENT.END_DATE, parseDateToSql(auxEndDate))
					.set(AGREEMENT_PAYMENT.MONTH, (byte)6)
					.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 1)
					.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, "_P")
					.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, "_P")
					.set(AGREEMENT_PAYMENT.CREATION_USER, userLogin)
					.set(AGREEMENT_PAYMENT.CREATION_DATE, creationDate)
					.returning(AGREEMENT_PAYMENT.ID)
					.fetchOne();
				
				agreementPaymentId = agreementPaymentRecord.getId();
				
				dslContext.insertInto(AGREEMENT_EXTRA)
					.set(AGREEMENT_EXTRA.DOMAIN, domainId)
					.set(AGREEMENT_EXTRA.AGREEMENT, agreementId)
					.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, agreementPaymentId)
					.set(AGREEMENT_EXTRA.START_DATE, "01/01")
					.set(AGREEMENT_EXTRA.END_DATE, "30/06")
					.set(AGREEMENT_EXTRA.ISSUE_DATE, "31/7")
					.execute();
				
			}
			
			if(!hasWinterPay) {
			
				agreementPaymentRecord = dslContext.insertInto(AGREEMENT_PAYMENT)
					.set(AGREEMENT_PAYMENT.DOMAIN, domainId)
					.set(AGREEMENT_PAYMENT.AGREEMENT, agreementId)
					.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, paymentConceptId)
					.set(AGREEMENT_PAYMENT.TYPE, (byte)4)
					.set(AGREEMENT_PAYMENT.EXPRESSION, "/*inherit*/" + "SALARIO_BASE" + "/**/")
					.set(AGREEMENT_PAYMENT.DESCRIPTION, "[91] PAGA NAVIDAD")
					.set(AGREEMENT_PAYMENT.START_DATE, parseDateToSql(defaultPaymentStartDate.getTime()))
					.set(AGREEMENT_PAYMENT.END_DATE, parseDateToSql(auxEndDate))
					.set(AGREEMENT_PAYMENT.MONTH, (byte)11)
					.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 1)
					.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, "_P")
					.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, "_P")
					.set(AGREEMENT_PAYMENT.CREATION_USER, userLogin)
					.set(AGREEMENT_PAYMENT.CREATION_DATE, creationDate)
					.returning(AGREEMENT_PAYMENT.ID)
					.fetchOne();
				
				agreementPaymentId = agreementPaymentRecord.getId();
				
				dslContext.insertInto(AGREEMENT_EXTRA)
				.set(AGREEMENT_EXTRA.DOMAIN, domainId)
				.set(AGREEMENT_EXTRA.AGREEMENT, agreementId)
				.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, agreementPaymentId)
				.set(AGREEMENT_EXTRA.START_DATE, "01/07")
				.set(AGREEMENT_EXTRA.END_DATE, "31/12")
				.set(AGREEMENT_EXTRA.ISSUE_DATE, "31/12")
				.execute();
				
			}
			
			// Set agreement_data is ServiAgreement
			
			if(!AonStringUtils.contains(agreementCode, 'a'))
				dslContext.update(AGREEMENT)
					.set(AGREEMENT.OWNER, (byte)1) // 0 = AonSolutions, 1 = ServiConvenios
					.where(AGREEMENT.ID.eq(agreementId))
					.execute();
			
			result.setFirst(agreementId);
			result.setSecond(mapVarNotInsert);
			
		});
		
		return result;
		
	}
	
	private static Integer insertOrGetPaymentConceptExtraPay(DSLContext dslContext) {
		
		Result<Record> paymentConceptRecords = dslContext.select().from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(domainId).or(PAYMENT_CONCEPT.DOMAIN.eq(0)))
			.and(PAYMENT_CONCEPT.CODE.eq("PAGA_EXTRA"))
			.and(PAYMENT_CONCEPT.DESCRIPTION.eq("PAGA EXTRAORDINARIA"))
			.fetch();
		
		if(paymentConceptRecords.isNotEmpty())
			return paymentConceptRecords.get(0).get(PAYMENT_CONCEPT.ID);
			
		PaymentConceptRecord paymentConceptRecord = dslContext.insertInto(PAYMENT_CONCEPT)
				.set(PAYMENT_CONCEPT.DOMAIN, domainId)
				.set(PAYMENT_CONCEPT.CODE, "PAGA_EXTRA")
				.set(PAYMENT_CONCEPT.DESCRIPTION, "PAGA EXTRAORDINARIA")
				.set(PAYMENT_CONCEPT.TYPE, (byte)4)
				.set(PAYMENT_CONCEPT.DESCRIPTION_DECORABLE, (byte)0)
				.set(PAYMENT_CONCEPT.EXPRESSION, "INPUT(\"/*user*/MENSUALIDAD/**/\",PAGA_EXTRA_HELP)")
				.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
				.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "_P")
				.returning(PAYMENT_CONCEPT.ID)
				.fetchOne();
		
		return paymentConceptRecord.getId();
	}

	private static String parseDescription(String description) {
		if(description.contains("CONVENIO COLECTIVO")){
			description = "CC" + description.split("CONVENIO COLECTIVO")[1];
		}
		if(description.length() > 64)
			return description.substring(0, 63);
		return description;
	}
	
	private static String parseLevelDescription(String description) {
		if(description.length() > 64)
			return description.substring(0, 61).trim();
		return description.trim();
	}
	
	private static java.sql.Date parseDateToSql(Date date) {
		if(null == date)
			return null;
		
		return new java.sql.Date(date.getTime());
	}
	
	private static String getParseName(String name, String type) {
		String realName = "";
		
		name = name.replaceAll(" ", "_");
		name = name.replaceAll(",", "");
		name = name.replaceAll("\\.", "_");
		name = name.replaceAll("\\*", "");
		name = name.replaceAll("/", "_");
		name = name.replaceAll(":", "_");
		name = name.replaceAll("º", "");
		name = name.replaceAll("%", "");
		name = name.replaceAll("-", "_");
		name = name.replaceAll("\\+", "");
		name = name.replaceAll("<", "");
		name = name.replaceAll(">", "");
		name = name.replaceAll("=", "");
		
		if(null != type)
			switch (type) {
				case "A":
					realName = name + "_" + "ANUAL";
					break;
				case "M":
					realName = name + "_" + "MENSUAL";
					break;
				case "D":
					realName = name + "_" + "DIARIO";
					break;
				case "H":
					realName = name + "_" + "HORAS";
					break;
				default:
					realName = name + "_" + "ANUAL";
					break;
			}
		
		return realName;
	}

	public static void main(String[] args) throws NumberFormatException, IllegalArgumentException, IOException {
		//@formatter:off
		Option hostName = getHostNameOption();
		Option user = getDbUserOption();
		Option password = getDbPasswordOption();
		Option database = getDatabaseOption();
		Option agreementCodeOpt = getAgreementCodeOption();
		Option domainOpt = getAgreementDomainOption();

		Options options = new Options()
				.addOption(hostName)
				.addOption(user)
				.addOption(password)
				.addOption(database)
				.addOption(agreementCodeOpt)
				.addOption(domainOpt)
				;	
		//@formatter:on

		// Create the parser
		CommandLineParser parser = new DefaultParser();
		Connection connection = null;

		try {
			Class.forName(com.mysql.cj.jdbc.Driver.class.getName());
			
			// Parse the command line arguments
			CommandLine cmd = parser.parse(options, args);

			connection = DriverManager.getConnection(
					String.format(
							"jdbc:mysql://%s:%d/%s",
							cmd.getOptionValue(hostName.getLongOpt(), "127.0.0.1"),
							3306, 
							cmd.getOptionValue(database.getLongOpt())
					),
					cmd.getOptionValue(user.getLongOpt()),
					cmd.getOptionValue(password.getLongOpt()));
			
			
			String agreementCode = cmd.getOptionValue(agreementCodeOpt.getLongOpt());
			String domainIdStr = cmd.getOptionValue(domainOpt.getLongOpt(), null);
			
			if(null != domainIdStr)
				domainId = Integer.parseInt(domainIdStr);
			
			// Get dslContext for given connection
			AONContext ctx = new AONContext(connection);
			DSLContext dslContext = ctx.getDslContext();
			
			Pair<Integer,String> agreementResult = new Pair<>(-1, "");
			
			String log = "";
			agreementResult = getAgreement(dslContext, agreementCode, Collections.emptyList(), Integer.parseInt(domainIdStr));
			log = agreementResult.getSecond();
			
			if(AonStringUtils.isNotBlank(log)) {
				PrintWriter out = new PrintWriter(new File("/Users/sergio/Desktop/ParseAgreement_NotValidVars.txt"));
				out.write(log);
				out.close();
			}
			

		} catch (ClassNotFoundException | SQLException | org.apache.commons.cli.ParseException | FileNotFoundException e) {
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("AGREEMENT", options);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static Option getAgreementCodeOption() {
		return Option.builder("a")
				.longOpt("agreementCode")
				.desc("Servi Agreement Code : c00000xx")
				.required()
				.argName("name")
				.hasArg()
				.build();
	}
	
	private static Option getAgreementDomainOption() {
		return Option.builder("d")
				.longOpt("domain")
				.desc("Domain to save Agreement")
				.argName("name")
				.hasArg()
				.build();
	}
}
