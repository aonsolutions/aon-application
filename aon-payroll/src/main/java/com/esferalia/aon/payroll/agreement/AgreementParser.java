package com.esferalia.aon.payroll.agreement;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.esferalia.aon.payroll.agreement.Agreement.AgreementLevel;
import com.esferalia.aon.payroll.agreement.Agreement.AgreementLevelData;

public class AgreementParser {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private static Integer DOMAIN = 0;
	
	private static List<String> agreementCodes = new ArrayList<String>() {{
		add("c0000001");
		add("c0000002");
		add("c0000023");
		add("c0000047");
		add("c0000088");
		add("c0000139");
		add("c0000184");
		add("c0000233");
		add("c0000235");
		add("c0000332");
		add("c0000429");
		add("c0000474");
		add("c0000645");
		add("c0000649");
		add("c0000679");
		add("c0000905");
		add("c0001124");
		add("c0001285");
		add("c0001393");
		add("c0001672");
		add("c0001710");
		add("c0001940");
		add("c0002060");
	}};
	
	@SuppressWarnings("serial")
	private static Map<String, String> variablesNameMap = new HashMap<String, String>(){{
		put("SALARIO_CONVENIO_ANUAL", "SALARIO_ANUAL");
		put("SALARIO_CONVENIO_ANUAL_ANUAL", "SALARIO_ANUAL");
		put("SALARIO_BASE_ANUAL", "SALARIO_ANUAL");
		put("SALARIO_BASE_ANUAL_ANUAL", "SALARIO_ANUAL");
		put("SALARIO_CONVENIO_MENSUAL", "SALARIO_MENSUAL");
		put("SALARIO_BASE_MENSUAL", "SALARIO_MENSUAL");
		put("SALARIO_CONVENIO_HORAS", "SALARIO_DIARIO");
		put("SALARIO_HORA_HORAS", "SALARIO_DIARIO");
		put("SALARIO_BASE_HORAS", "SALARIO_DIARIO");
		put("SALARIO_CONVENIO_DIARIO", "SALARIO_DIARIO");
		put("SALARIO_BASE_DIARIO", "SALARIO_DIARIO");
		put("PLUS_CONVENIO_ANUAL", "PLUS_CONVENIO_ANUAL");
		put("PLUS_CONVENIO_MENSUAL", "PLUS_CONVENIO_MENSUAL");
		put("COMPLEMENTO_CONVENIO_MENSUAL", "PLUS_CONVENIO_MENSUAL");
		put("PLUS_CONVENIO_HORAS", "PLUS_CONVENIO_HORAS");
		put("COMPLEMENTO_CONVENIO_DIARIO", "PLUS_CONVENIO_DIARIO");
		put("PLUS_EXTRA_CATEGORIA_ANUAL", "PLUS_EXTRA_CATEGORIA_A");
		put("PLUS_ACTIVIDAD_MENSUAL", "PLUS_ACTIVIDAD_MENSUAL");
		put("PLUS_ACTIVIDAD_DIARIO", "PLUS_ACTIVIDAD_DIARIO");
		put("PLUS_EXTRASALARIAL_MENSUAL", "PLUS_EXTRA_SALARIAL");
		put("PLUS_EXTRASALARIAL_DIARIO", "PLUS_EXTRA_SALARIAL");
		put("PLUS_VESTUARIO_MENSUAL", "PLUS_VESTUARIO");
		put("PLUS_ROTACION_MENSUAL", "PLUS_ROTACION");
		put("PLUS_DISTANCIA_MENSUAL", "PLUS_DISTANCIA_M");
		put("PLUS_DISTANCIA_HORAS", "PLUS_DISTANCIA_H");
		put("PLUS_URGENCIA_MENSUAL", "PLUS_URGENCIA");
		put("PLUS_ASISTENCIA_MENSUAL", "PLUS_ASISTENCIA");
		put("PLUS_TRANSPORTE_MENSUAL", "PLUS_TRANSPORTE_MEN");
		put("PLUS_TRANSPORTE_DIARIO", "PLUS_TRANSPORTE_DIA");
		put("PLUS_CARENCIA_DE_INCENTIVOS_MENSUAL", "PLUS_CARENCIA_INCENT_M");
		put("PLUS_RIESGO_DIARIO", "PLUS_RIESGO");
		put("PLUS_DOMINGOS_Y_FESTIVOS_DIARIO", "PLUS_FINDES");
		put("PLUS_PENOSIDAD_1_CIRCUNSTANCIA_DIARIO", "PLUS_PENOSIDAD_1");
		put("PLUS_PENOSIDAD_2_CIRCUNSTANCIAS_DIARIO", "PLUS_PENOSIDAD_2");
		put("PLUS_TOXICIDAD_MENSUAL", "PLUS_TOXICIDAD");
		put("PLUS_ACERCAMIENTO_ANUAL", "PLUS_ACERCAMIENTO_ANUAL");
		put("COMPLEMENTO_ESPECIFICO_MENSUAL", "COMPL_ESPECIFICO_MEN");
		put("COMPLEMENTO_NO_SALARIAL_ANUAL", "COMPL_NO_SALARIAL_ANUAL");
		put("COMPLEMENTO_NO_SALARIAL_MENSUAL", "COMPL_NO_SALARIAL_MEN");
		put("COMPLEMENTO_NO_SALARIAL_DIARIO", "COMPL_NO_SALARIAL_DIA");
		put("PAGAS_EXTRA_VERANO_Y_NAVIDAD_ANUAL", "P_E_VERANO_Y_NAVIDAD_A");
		put("PAGAS_EXTRA_VERANO_Y_NAVIDAD_MENSUAL", "P_E_VERANO_Y_NAVIDAD_M");
		put("PAGA_EXTRA_VERANO_MENSUAL", "P_E_VERANO_M");
		put("PAGA_EXTRA_NAVIDAD_MENSUAL", "P_E_NAVIDAD_M");
		put("PAGA_EXTRA_SIN_ANTIGUEDAD_MENSUAL", "P_EXTRA_SIN_ANTIGUEDAD_M");
		put("PAGA_EXTRA_MARZO_MENSUAL", "PAGA_EXTRA_MARZO");
		put("HORA_ORDINARIA_HORAS", "HORA_ORDINARIA");
		put("HORA_EXTRA_HORAS", "HORA_EXTRA");
		put("HORA_EXTRA_DOMINGOS_Y_FESTIVOS_HORAS", "HORAS_EXTRAS_F");
		put("HORA_EXTRA_NOCTURNA_HORAS", "HORAS_EXTRAS_NOC");
		put("HORA_EXTRA_SIN_ANTIGUEDAD_HORAS", "P_EXTRA_SIN_ANTIGUEDAD_H");
		put("HORA_EXTRA_1_QUINQUENIO_HORAS", "H_E_1_QUINQUENIO");
		put("HORA_EXTRA_2_QUINQUENIO_HORAS", "H_E_2_QUINQUENIO");
		put("HORA_EXTRA_3_QUINQUENIO_HORAS", "H_E_3_QUINQUENIO");
		put("HORA_EXTRA_4_QUINQUENIO_HORAS", "H_E_4_QUINQUENIO");
		put("VACACIONES_MENSUAL", "VACACIONES_MENSUAL");
		put("DIETA_COMPLETA_DIARIO", "DIETA_COMPLETA");
		put("DIETA_COMPLETA_DESPL_MAS_100KMS_DIARIO", "DIETA_COMPLETA_G_100");
		put("DIETA_COMPLETA_DESPL_MENOS_100KMS_DIARIO", "DIETA_COMPLETA_L_100");
		put("DIETA_PERNOCTA_DIARIO", "DIETA_PERNOCTA");
		put("DIETAS_DIARIO", "DIETA");
		put("DIETA_COMIDA_DIARIO", "DIETA_COMIDA");
		put("MEDIA_DIETA_DIARIO", "MEDIA_DIETA");
		put("MEDIA_DIETA_DESPL_MAS_100KMS_DIARIO", "MEDIA_DIETA_G_100");
		put("MEDIA_DIETA_DESPL_MENOS_100KMS_DIARIO", "MEDIA_DIETA_L_100");
		put("INCENTIVOS_MENSUAL", "INCENTIVOS_MENSUAL");
		put("INCENTIVOS_DIARIO", "INCENTIVO_DIARIO");
		put("INCENTIVOS_MARMOLERIAS_DIARIO", "INCENTIVOS_MARMOL_DIA");
		put("GRATIFICACION_ANUAL", "GRATIFICACION_ANUAL");
		put("COMPENSACIONES_MARMOLERIAS_MENSUAL", "COMP_MARMOLERIAS");
		put("AYUDA_FAMILIA_NUMEROSA_MENSUAL", "AYUDA_FAMILIA_NUM");
		put("QUEBRANDO_DE_MONEDA_MENSUAL", "QUEBRANDO_MONEDA");
		put("COMPENSACIONES_MENSUAL", "COMPENSACIONES");
		put("PAGAS_EXTRA_MENSUAL", "PAGAS_EXTRA_MENSUAL");
		put("TURNICIDAD_MENSUAL", "TURNICIDAD_MENSUAL");
		put("TRIENIOS_MENSUAL", "TRIENIOS_MENSUAL");
		put("BIENIOS_MENSUAL", "BIENIOS_MENSUAL");
		put("P_C_I__MENSUAL", "PCI_MENUSAL");
		put("DIETA_KILOMETRAJE_HORAS", "DIETA_KILOMETRAJE_H");
	}};
	
	public static String getAgreement(DSLContext dslContext, String agreementCode, Integer domainId) {
		DOMAIN = domainId;
		String log = "";
		InputStream is = AgreementParser.class.getResourceAsStream(agreementCode + ".xml");
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder documentBuilder;
		try {
			
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(is);
			
			// Agreement general info
			Agreement agreement = getAgreementInfo(dslContext, document);
			
			// Agreement Concepts
			getAgreementConcepts(dslContext, document, agreement);
			
			// Agreement levels and categories
			getAgreementLevelAndCategory(dslContext, document, agreement);
			
			// Agreement levels data
			getAgreementLevelData(dslContext, document, agreement);
			
//			System.out.println(agreement.toString());
			
			// Insert Agreement to DataBase
			Map<String, String> varNotInsertMap = insertAgreementDB(dslContext, agreement);
			
//			System.out.println("serviAgreementsMap.put(\"" + agreement.getSSCode() + " - " + agreement.getAgreementDescription().toUpperCase() + "\",  \"" + agreement.getServiAgreementCode() + "\");"  );
			
//			PrintWriter out = new PrintWriter(new File("/Users/sergio/Desktop/ParseAgreement_NotValidVars.txt"));
//			out.write(agreement.toString());
//			out.close();
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return log;
	}

	private static Agreement getAgreementInfo(DSLContext dslContext, Document document) {
		NodeList list = document.getElementsByTagName("DATOS_GENERALES");
		Agreement agreement = null;
		
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
				} catch (DOMException e) {
					e.printStackTrace();
				} catch (ParseException e) {
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
	
	private static void getAgreementConcepts(DSLContext dslContext, Document document, Agreement agreement) {
		NodeList listCPR = document.getElementsByTagName("CATALOGO_CPTOS_RETRIB");
		
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
	    	            String type = elementCI.getElementsByTagName("TIPO_AMDH").item(0).getTextContent();
	    	            
	    	            String realName = getParseName(name, type);
	    	            
	    	            agreement.addAgreementConcept(realName);
	    	        }
	            }   
	        }
		}
	}

	
	
	private static void getAgreementLevelAndCategory(DSLContext dslContext, Document document, Agreement agreement) {
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
		    	            description.trim();
	    	            }
	    	            
	    	            String category = elementCatProfIt.getElementsByTagName("NOMBRE").item(0).getTextContent();
	    	            
//	    	            AgreementLevel agreementLevel = agreement.checkLevelExist(description);
//	    	            
//	    	            if(null != agreementLevel)
//	    	            	description = agreementLevel + "_" + j;
//	    	            
	    	            agreement.addAgreementLevel(code, description, category);
	    	            
	    	        }
	    		}
	        }
		}
	}
	
	private static void getAgreementLevelData(DSLContext dslContext, Document document, Agreement agreement) {
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
	    	            
	    	            // Calendar StartDate
	    	            Calendar startDate = Calendar.getInstance();
	    	            startDate.set(Calendar.YEAR, year);
	    	            startDate.set(Calendar.MONTH, 0);
	    	            startDate.set(Calendar.DAY_OF_MONTH, 1);
	    	            
	    	            // EndDate
	    	            Calendar endDate = Calendar.getInstance();
	    	            endDate.set(Calendar.YEAR, year-1);
	    	            endDate.set(Calendar.MONTH, 11);
	    	            endDate.set(Calendar.DAY_OF_MONTH, 31);
	    	            
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
		    		    	            String description = "";
		    		    	            
		    		    	            if(listdescriptions.getLength() == 0)
		    		    	            	description = "NIVEL " + (l+1);
		    		    	            else {
			    		    	            for(int b=0; b<listdescriptions.getLength(); b++)
			    		    	            	description += listdescriptions.item(b).getTextContent() + " ";
			    		    	            description.trim();
		    		    	            }
		    		    	            
			   	    	            	String category = elementCPI.getElementsByTagName("NOMBRE").item(0).getTextContent();
			   	    	            	
				   	    	            AgreementLevel agreementLevel = agreement.getAgreementLevel(description, category);
			   	    	            	
			   	    	            	Node nodeConcept = elementCPI.getElementsByTagName("CONCEPTOS").item(0);
			   	    	            	if (nodeConcept.getNodeType() == Node.ELEMENT_NODE) {
			   	    	            		Element elementConcept = (Element) nodeConcept;
			   	    	            		
			   	    	            		NodeList listCPTO = elementConcept.getElementsByTagName("CPTO_IT");
				   	    	            	
				   	    	            	for(int b=0; b<listCPTO.getLength(); b++) {
				    	     	            	Node nodeCPTO = listCPTO.item(b);
				    	     	            	
				    	     	            	if (nodeCPTO.getNodeType() == Node.ELEMENT_NODE) {
				    	     	            		Element elementCPTO = (Element) nodeCPTO;
				    	     	            		
				    	     	            		String name = elementCPTO.getElementsByTagName("NOMBRE").item(0).getTextContent();
						   	    	            	String value = elementCPTO.getElementsByTagName("IMPORTE").item(0).getTextContent();
						   	    	            	String type = elementCPTO.getElementsByTagName("TIPO_AMDH").item(0).getTextContent();
						   	    	            	
						   	    	            	String realName = getParseName(name, type);
						   	    	            	
						   	    	            	agreementLevel.addLevelData(realName, value, startDate.getTime());
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
	
	private static Map<String, String> insertAgreementDB(DSLContext dslContext, Agreement agreement) {
		// Variables not insert
		Map<String, String> mapVarNotInsert = new HashMap<String, String>();
		
		// Agreement
		
		AgreementRecord agreementRecord = dslContext.insertInto(AGREEMENT)
			.set(AGREEMENT.DOMAIN, DOMAIN)
			.set(AGREEMENT.DESCRIPTION, parseDescription(agreement.getAgreementDescription()))
			.set(AGREEMENT.SS_NUMBER, agreement.getSSCode())
			.returning(AGREEMENT.ID)
			.fetchOne();
		
		Integer agreementId = agreementRecord.getId();
		
		// Agreement Level / Agreement Level Category / Agreement Level Data
		
		String oldLevelDescription = null;
		Integer count = 1;
		
		for(AgreementLevel lvl : agreement.getAgreementLevels()) {
			
			// Agreement Level
			
			String levelDescription = parseLevelDescription(lvl.getDescription());
			
			if(null != oldLevelDescription && (oldLevelDescription ==  levelDescription|| oldLevelDescription.equals(levelDescription))) {
				levelDescription = levelDescription + "_" + count;
				count++;
			} else {
				oldLevelDescription = levelDescription;
				count = 1;
			}
			
			AgreementLevelRecord agreementLevelRecord = dslContext.insertInto(AGREEMENT_LEVEL)
				.set(AGREEMENT_LEVEL.DOMAIN, DOMAIN)
				.set(AGREEMENT_LEVEL.AGREEMENT, agreementId)
				.set(AGREEMENT_LEVEL.DESCRIPTION, levelDescription)
				.returning(AGREEMENT_LEVEL.ID)
				.fetchOne();
			
			Integer agreementLevelId = agreementLevelRecord.getId();
			
			// Agreement Level Category
			
			for(String lvlCategory : lvl.getLevelCategories()) {
				dslContext.insertInto(AGREEMENT_LEVEL_CATEGORY)
					.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, DOMAIN)
					.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevelId)
					.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, parseDescription(lvlCategory))
					.execute();
			}
			
			// Agreement Level Data
			
			for(AgreementLevelData lvlData : lvl.getLevelDatas()) {
				String realName = variablesNameMap.getOrDefault(lvlData.getName(), null);
				
				if(null != realName) {
					dslContext.insertInto(AGREEMENT_LEVEL_DATA)
						.set(AGREEMENT_LEVEL_DATA.DOMAIN, DOMAIN)
						.set(AGREEMENT_LEVEL_DATA.NAME, realName)
						.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, agreementLevelId)
						.set(AGREEMENT_LEVEL_DATA.EXPRESSION, lvlData.getValue())
						.set(AGREEMENT_LEVEL_DATA.START_DATE, parseDateToSql(lvlData.getStartDate()))
						.set(AGREEMENT_LEVEL_DATA.END_DATE, parseDateToSql(lvlData.getEndDate()))
						.execute();
				} else
					mapVarNotInsert.put(lvlData.getName(), lvlData.getName());
			}
			
		}
		
		// Agreement Data
		
		Date auxEndDate = null;
		
		dslContext.insertInto(AGREEMENT_DATA)
			.set(AGREEMENT_DATA.DOMAIN, DOMAIN)
			.set(AGREEMENT_DATA.NAME, "PAGAS")
			.set(AGREEMENT_DATA.AGREEMENT, agreementId)
			.set(AGREEMENT_DATA.EXPRESSION, "14")
			.set(AGREEMENT_DATA.START_DATE, parseDateToSql(agreement.getStartDate()))
			.set(AGREEMENT_DATA.END_DATE, parseDateToSql(auxEndDate))
			.execute();
		
		// Agreement Payment
		
		for(String agreementConceptName : agreement.getAgreementConcepts()) {
			AgreementPayment agreementPayment = AgreementPayment.safeValueOf(agreementConceptName);
			
			if(null != agreementPayment) {
				PaymentConceptRecord paymentConceptRecord = dslContext.insertInto(PAYMENT_CONCEPT)
						.set(PAYMENT_CONCEPT.DOMAIN, DOMAIN)
						.set(PAYMENT_CONCEPT.CODE, agreementPayment.getConceptCode())
						.set(PAYMENT_CONCEPT.DESCRIPTION, agreementPayment.getNormalizeName())
						.set(PAYMENT_CONCEPT.TYPE, agreementPayment.getType())
						.set(PAYMENT_CONCEPT.DESCRIPTION_DECORABLE, (byte)0)
						.set(PAYMENT_CONCEPT.EXPRESSION, agreementPayment.getExpression())
						.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
						.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "_P")
						.returning(PAYMENT_CONCEPT.ID)
						.fetchOne();
				
				Integer paymentConceptId = paymentConceptRecord.getId();
				
				dslContext.insertInto(AGREEMENT_PAYMENT)
						.set(AGREEMENT_PAYMENT.DOMAIN, DOMAIN)
						.set(AGREEMENT_PAYMENT.AGREEMENT, agreementId)
						.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, paymentConceptId)
						.set(AGREEMENT_PAYMENT.TYPE, agreementPayment.getType())
						.set(AGREEMENT_PAYMENT.EXPRESSION, agreementPayment.getExpression())
						.set(AGREEMENT_PAYMENT.DESCRIPTION, agreementPayment.getNormalizeName())
						.set(AGREEMENT_PAYMENT.START_DATE, parseDateToSql(agreement.getStartDate()))
						.set(AGREEMENT_PAYMENT.END_DATE, parseDateToSql(auxEndDate))
						.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 0)
						.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, "_P")
						.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, "_P")
						.execute();
			}
		}
		
		Integer paymentConceptId = insertOrGetPaymentConceptExtraPay(dslContext);
		
		AgreementPaymentRecord agreementPaymentRecord = dslContext.insertInto(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.DOMAIN, DOMAIN)
			.set(AGREEMENT_PAYMENT.AGREEMENT, agreementId)
			.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, paymentConceptId)
			.set(AGREEMENT_PAYMENT.TYPE, (byte)4)
			.set(AGREEMENT_PAYMENT.EXPRESSION, "SALARIO_BASE+PLUS_SALARIAL")
			.set(AGREEMENT_PAYMENT.DESCRIPTION, "[92] PAGA VERANO")
			.set(AGREEMENT_PAYMENT.START_DATE, parseDateToSql(agreement.getStartDate()))
			.set(AGREEMENT_PAYMENT.END_DATE, parseDateToSql(auxEndDate))
			.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 0)
			.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, "_P")
			.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, "_P")
			.returning(AGREEMENT_PAYMENT.ID)
			.fetchOne();
		
		Integer agreementPaymentId = agreementPaymentRecord.getId();
		
		dslContext.insertInto(AGREEMENT_EXTRA)
			.set(AGREEMENT_EXTRA.DOMAIN, DOMAIN)
			.set(AGREEMENT_EXTRA.AGREEMENT, agreementId)
			.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, agreementPaymentId)
			.set(AGREEMENT_EXTRA.START_DATE, "1/1")
			.set(AGREEMENT_EXTRA.END_DATE, "31/12")
			.set(AGREEMENT_EXTRA.ISSUE_DATE, "31/6")
			.execute();
		
		agreementPaymentRecord = dslContext.insertInto(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.DOMAIN, DOMAIN)
			.set(AGREEMENT_PAYMENT.AGREEMENT, agreementId)
			.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, paymentConceptId)
			.set(AGREEMENT_PAYMENT.TYPE, (byte)4)
			.set(AGREEMENT_PAYMENT.EXPRESSION, "SALARIO_BASE+PLUS_SALARIAL")
			.set(AGREEMENT_PAYMENT.DESCRIPTION, "[93] PAGA NAVIDAD")
			.set(AGREEMENT_PAYMENT.START_DATE, parseDateToSql(agreement.getStartDate()))
			.set(AGREEMENT_PAYMENT.END_DATE, parseDateToSql(auxEndDate))
			.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 0)
			.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, "_P")
			.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, "_P")
			.returning(AGREEMENT_PAYMENT.ID)
			.fetchOne();
		
		agreementPaymentId = agreementPaymentRecord.getId();
		
		dslContext.insertInto(AGREEMENT_EXTRA)
		.set(AGREEMENT_EXTRA.DOMAIN, DOMAIN)
		.set(AGREEMENT_EXTRA.AGREEMENT, agreementId)
		.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, agreementPaymentId)
		.set(AGREEMENT_EXTRA.START_DATE, "1/1")
		.set(AGREEMENT_EXTRA.END_DATE, "31/12")
		.set(AGREEMENT_EXTRA.ISSUE_DATE, "31/12")
		.execute();
		
		return mapVarNotInsert;
		
	}
	
	private static Integer insertOrGetPaymentConceptExtraPay(DSLContext dslContext) {
		
		Result<Record> paymentConceptRecords = dslContext.select().from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(DOMAIN))
			.and(PAYMENT_CONCEPT.CODE.eq("PAGA_EXTRA"))
			.and(PAYMENT_CONCEPT.DESCRIPTION.eq("PAGA EXTRAORDINARIA"))
			.and(PAYMENT_CONCEPT.EXPRESSION.eq("SALARIO_BASE+PLUS_SALARIAL"))
			.and(PAYMENT_CONCEPT.TYPE.eq((byte)4))
			.fetch();
		
		if(paymentConceptRecords.isNotEmpty())
			return paymentConceptRecords.get(0).get(PAYMENT_CONCEPT.ID);
			
		PaymentConceptRecord paymentConceptRecord = dslContext.insertInto(PAYMENT_CONCEPT)
				.set(PAYMENT_CONCEPT.DOMAIN, DOMAIN)
				.set(PAYMENT_CONCEPT.CODE, "PAGA_EXTRA")
				.set(PAYMENT_CONCEPT.DESCRIPTION, "PAGA EXTRAORDINARIA")
				.set(PAYMENT_CONCEPT.TYPE, (byte)4)
				.set(PAYMENT_CONCEPT.DESCRIPTION_DECORABLE, (byte)0)
				.set(PAYMENT_CONCEPT.EXPRESSION, "SALARIO_BASE+PLUS_SALARIAL")
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
			return description.substring(0, 61);
		return description;
	}
	
	private static java.sql.Date parseDateToSql(Date date) {
		if(null == date)
			return null;
		
		return new java.sql.Date(date.getTime());
	}
	
	private static String getParseName(String name, String type) {
		String realName = "";
		
		name = name.replaceAll(" ", "_");
		name = name.replaceAll("\\.", "_");
		
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

	public static void main(String[] args) {
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

		try {
			Class.forName(com.mysql.cj.jdbc.Driver.class.getName());
			
			// Parse the command line arguments
			CommandLine cmd = parser.parse(options, args);

			Connection connection = DriverManager.getConnection(
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
				DOMAIN = Integer.parseInt(domainIdStr);
			
			// Get dslContext for given connection
			AONContext ctx = new AONContext(connection);
			DSLContext dslContext = ctx.getDslContext();
			
//			getAgreement(dslContext, agreementCode);
			
			String log = "";
			for(String agreementCodeAux : agreementCodes)
				log += getAgreement(dslContext, agreementCodeAux, Integer.parseInt(domainIdStr));
			
			PrintWriter out = new PrintWriter(new File("/Users/sergio/Desktop/ParseAgreement_NotValidVars.txt"));
			out.write(log);
			out.close();

		} catch (ClassNotFoundException | SQLException | org.apache.commons.cli.ParseException | FileNotFoundException e) {
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("AGREEMENT", options);
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
