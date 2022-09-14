package com.esferalia.aon.payroll.contract;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import com.esferalia.aon.watson.util.AonStringUtils;

public class ContractFill {
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private static final Map<String, String> FIELDNAMESTOMAP = new HashMap<>();
	
	private ContractFill() {
		super();
	}
	
	public static byte[] fillContract(
			Integer contractType, 
			String sepeIde, 
			Date comunicationDate, 
			Map<String, String> contractOtherInfo, 
			Map<String, String> contractFillInfo, 
			TreeMap<String, String> contractClauses) throws IllegalArgumentException {
		
		if(null == contractType)
			throw new IllegalArgumentException("El tipo de contrato no esta definido.");
		
		initializeFieldNames();
		checkContractOtherInfo(contractOtherInfo);
		
		if(contractType >= 100 && contractType <= 400) 
			return fillIndefiniteContract(contractType, sepeIde, comunicationDate, contractOtherInfo, contractFillInfo, contractClauses);
		else if (contractType == 421) 
			return fillFormationContract(sepeIde, comunicationDate, contractOtherInfo, contractFillInfo, contractClauses);
		else if (contractType == 420 || contractType == 520) 
			return fillPracticeContract(sepeIde, comunicationDate, contractOtherInfo, contractFillInfo, contractClauses);
		else 
			return fillTemporalContract(contractType, sepeIde, comunicationDate, contractOtherInfo, contractFillInfo, contractClauses);
		
	}
	
	private static void initializeFieldNames() {
		FIELDNAMESTOMAP.clear();
		FIELDNAMESTOMAP.put("Texto10", "ENTERPRISE_COUNTRY_CODE");
		FIELDNAMESTOMAP.put("Texto14", "ENTERPRISE_MUNICIPALITY_CODE");
		FIELDNAMESTOMAP.put("Texto19", "ENTERPRISE_ZIP");
		FIELDNAMESTOMAP.put("REG_CCC", "ENTERPRISE_CCC_REG");
		FIELDNAMESTOMAP.put("PRV_CCC", "ENTERPRISE_CCC_PRV");
		FIELDNAMESTOMAP.put("NUM_CCC", "ENTERPRISE_CCC_NUM");
		FIELDNAMESTOMAP.put("DC_CCC", "ENTERPRISE_CCC_DC");
		FIELDNAMESTOMAP.put("Texto3441", "ENTERPRISE_ACTIVITY_CODE");
		FIELDNAMESTOMAP.put("COD_PAISCT", "WORKPLC_COUNTRY_CODE");
		FIELDNAMESTOMAP.put("COD_MUNCT", "WORKPLC_MUNICIPALITY_CODE");
		FIELDNAMESTOMAP.put("COD_NACTRA", "E_NATIONALITY_CODE");
		FIELDNAMESTOMAP.put("COD_MUNDO", "E_MUNICIPALITY_ADDR_CODE");
		FIELDNAMESTOMAP.put("COD_PAISDO", "E_COUNTRY_ADDR_CODE");
		FIELDNAMESTOMAP.put("PRV_NASS", "E_SS1");
		FIELDNAMESTOMAP.put("NUM_NASS", "E_SS2");
		FIELDNAMESTOMAP.put("DC_NASS", "E_SS3");
		FIELDNAMESTOMAP.put("DEN_NVFOR", "E_FORMATIVE_LVL");
		FIELDNAMESTOMAP.put("COD_NVFOR", "E_FORMATIVE_LVL_CODE");
		FIELDNAMESTOMAP.put("HOR_JOR_HH", "I_PARTIALLY_TIME_HOURS");
	}
	
	private static void checkContractOtherInfo(Map<String, String> contractOtherInfo) {
		if(contractOtherInfo.get("I_TRIAL_DURATION") == null) contractOtherInfo.put("I_TRIAL_DURATION", "SEGUN CONVENIO COLECTIVO");
		if(contractOtherInfo.get("P_TRIAL_DURATION") == null) contractOtherInfo.put("P_TRIAL_DURATION", "SEGUN CONVENIO COLECTIVO");
		if(contractOtherInfo.get("T_TRIAL_DURATION") == null) contractOtherInfo.put("T_TRIAL_DURATION", "SEGUN CONVENIO COLECTIVO");
		
		if(contractOtherInfo.get("I_SALARY_AMOUNT") == null) contractOtherInfo.put("I_SALARY_AMOUNT", "SEGUN CONVENIO COLECTIVO");
		if(contractOtherInfo.get("P_SALARY_AMOUNT") == null) contractOtherInfo.put("P_SALARY_AMOUNT", "SEGUN CONVENIO COLECTIVO");
		if(contractOtherInfo.get("T_SALARY_AMOUNT") == null) contractOtherInfo.put("T_SALARY_AMOUNT", "SEGUN CONVENIO COLECTIVO");
		
		if(contractOtherInfo.get("I_SALARY_PERIOD") == null) contractOtherInfo.put("I_SALARY_PERIOD", "MENSUALES");
		if(contractOtherInfo.get("P_SALARY_PERIOD") == null) contractOtherInfo.put("P_SALARY_PERIOD", "MENSUALES");
		if(contractOtherInfo.get("T_SALARY_PERIOD") == null) contractOtherInfo.put("T_SALARY_PERIOD", "MENSUALES");
		
		if(contractOtherInfo.get("I_SALARY_CONCEPT") == null) contractOtherInfo.put("I_SALARY_CONCEPT", "SEGUN CONVENIO COLECTIVO");
		if(contractOtherInfo.get("P_SALARY_CONCEPT") == null) contractOtherInfo.put("P_SALARY_CONCEPT", "SEGUN CONVENIO COLECTIVO");
		if(contractOtherInfo.get("T_SALARY_CONCEPT") == null) contractOtherInfo.put("T_SALARY_CONCEPT", "SEGUN CONVENIO COLECTIVO");
		
		if(contractOtherInfo.get("I_HOLIDAYS") == null) contractOtherInfo.put("I_HOLIDAYS", "SEGUN CONVENIO COLECTIVO");
		if(contractOtherInfo.get("P_HOLIDAYS") == null) contractOtherInfo.put("P_HOLIDAYS", "SEGUN CONVENIO COLECTIVO");
		if(contractOtherInfo.get("T_HOLIDAYS") == null) contractOtherInfo.put("T_HOLIDAYS", "SEGUN CONVENIO COLECTIVO");
		
		if(contractOtherInfo.get("I_SEPE_MUNICIPALITY") == null) contractOtherInfo.put("I_SEPE_MUNICIPALITY", "A TRAVES DE CONTRATA");
		if(contractOtherInfo.get("P_SEPE_MUNICIPALITY") == null) contractOtherInfo.put("P_SEPE_MUNICIPALITY", "A TRAVES DE CONTRATA");
		if(contractOtherInfo.get("T_SEPE_MUNICIPALITY") == null) contractOtherInfo.put("T_SEPE_MUNICIPALITY", "A TRAVES DE CONTRATA");
		
	}

	private static byte[] fillIndefiniteContract(Integer contractType, String sepeIde, Date comunicationDate, Map<String, String> contractOtherInfo, Map<String, String> contractFillInfo, TreeMap<String, String> contractClauses) {
		InputStream is = ContractFill.class.getResourceAsStream("indefinido.pdf");
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		try (PDDocument pdfDocument = Loader.loadPDF(is)){
			pdfDocument.setAllSecurityToBeRemoved(true);
			
			PDDocumentCatalog doc = pdfDocument.getDocumentCatalog();
			PDAcroForm acroForm = doc.getAcroForm();
			
			if(null != acroForm) {
				PDResources resources = new PDResources();
				PDFont font = new PDType1Font(FontName.HELVETICA);
				resources.add(font);
				acroForm.setDefaultResources(resources);
				
				for(PDField field : acroForm.getFields()) {
					defaultCheckBox(field);
					
					String valueStr = field.getValueAsString();
					String fieldName = field.getPartialName();
					String renderFieldName = FIELDNAMESTOMAP.getOrDefault(fieldName, null);
					
					if(null != renderFieldName) {
						String newValue = contractFillInfo.getOrDefault(renderFieldName, "");
						newValue = AonStringUtils.isBlank(newValue) ? "" : newValue.toUpperCase();
						setField(field, newValue);
					} else {
					
						if(!StringUtils.isBlank(valueStr) && StringUtils.containsIgnoreCase(valueStr, "$aon:")) {
							valueStr = valueStr.replace("$aon:", "");
							
							if(StringUtils.equals(valueStr, "ADITIONAL_CLAUSES")) {
								setAditionalClauses(field, contractClauses);
							} else {
								if(!StringUtils.contains(valueStr, " ")){
									String newValue = contractOtherInfo.getOrDefault(valueStr, "");
									newValue = AonStringUtils.isBlank(newValue) ? "" : newValue.toUpperCase();
									setField(field, newValue);
								} else {
									String newValue = "";
									String[] splits = StringUtils.split(valueStr, " ");
									for(int i=0; i<splits.length; i++) {
										if(splits[i].contains("_"))
											newValue += contractOtherInfo.getOrDefault(splits[i], "") + " ";
									}
									newValue = AonStringUtils.isBlank(newValue) ? "" : newValue.toUpperCase();
									setField(field, newValue);
								}
							}
							
						} else if(!StringUtils.isBlank(valueStr) && StringUtils.containsIgnoreCase(valueStr, "${")) {
							valueStr = valueStr.replace("${", "");
							valueStr = valueStr.replace("}", "");
							if(!StringUtils.contains(valueStr, " ")){
								String newValue = contractFillInfo.getOrDefault(valueStr, "");
								newValue = AonStringUtils.isBlank(newValue) ? newValue : newValue.toUpperCase();
								setField(field, newValue);
							}
						}
					
					}
						
				}
			}
			
	        // Add Sepe info if exists
	        if(AonStringUtils.isNotBlank(sepeIde))
	        	addSepeInfo(pdfDocument, sepeIde, comunicationDate);
	        
	        // Remove unsed pages
	        removeIndefiniteNotUsingPage(contractType, pdfDocument);
	        
	        pdfDocument.setAllSecurityToBeRemoved(true);
	        
			pdfDocument.save(out);
			pdfDocument.close();
			
			return out.toByteArray();
		
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void removeIndefiniteNotUsingPage(Integer contractType, PDDocument pdfDocument) {
		if(contractType.equals(100) || contractType.equals(200) || contractType.equals(300)) {
//			pdfDocument.removePage(4);
//			pdfDocument.removePage(5);
//			pdfDocument.removePage(6);
//			pdfDocument.removePage(7);
//			pdfDocument.removePage(8);
//			pdfDocument.removePage(9);
//			pdfDocument.removePage(10);
//			pdfDocument.removePage(11);
//			pdfDocument.removePage(12);
//			pdfDocument.removePage(13);
//			pdfDocument.removePage(14);
//			pdfDocument.removePage(15);
//			pdfDocument.removePage(16);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
		} else if(contractType.equals(130) || contractType.equals(230) || contractType.equals(330)) {
			pdfDocument.removePage(3);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
		} else if(contractType.equals(109) || contractType.equals(139) || contractType.equals(189) || 
				contractType.equals(209) || contractType.equals(239) || contractType.equals(289) || 
				contractType.equals(309) || contractType.equals(339) || contractType.equals(389)) {
			pdfDocument.removePage(3);
			pdfDocument.removePage(3);
			pdfDocument.removePage(3);
			pdfDocument.removePage(3);
			pdfDocument.removePage(3);
			pdfDocument.removePage(3);
			pdfDocument.removePage(3);
			pdfDocument.removePage(3);
			pdfDocument.removePage(3);
			pdfDocument.removePage(3);
			pdfDocument.removePage(3);
			pdfDocument.removePage(3);
			pdfDocument.removePage(3);
			pdfDocument.removePage(3);
		}
	}

	private static byte[] fillFormationContract(String sepeIde, Date comunicationDate, Map<String, String> contractOtherInfo, Map<String, String> contractFillInfo, TreeMap<String, String> contractClauses) {
		InputStream is = ContractFill.class.getResourceAsStream("formacion.pdf");
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		try (PDDocument pdfDocument = Loader.loadPDF(is)){
			
			pdfDocument.setAllSecurityToBeRemoved(true);
			
			PDDocumentCatalog doc = pdfDocument.getDocumentCatalog();
			PDAcroForm acroForm = doc.getAcroForm();
			
			if(null != acroForm) {
				PDResources resources = new PDResources();
				PDFont font = new PDType1Font(FontName.HELVETICA);
				resources.add(font);
				acroForm.setDefaultResources(resources);
				
				for(PDField field : acroForm.getFields()) {
					defaultCheckBox(field);
					
					String valueStr = field.getValueAsString();
					String fieldName = field.getPartialName();
					String renderFieldName = FIELDNAMESTOMAP.getOrDefault(fieldName, null);
					
					if(null != renderFieldName) {
						String newValue = contractFillInfo.getOrDefault(renderFieldName, "");
						setField(field, newValue);
					} else {
					
						if(!StringUtils.isBlank(valueStr) && StringUtils.containsIgnoreCase(valueStr, "$aon:")) {
							valueStr = valueStr.replace("$aon:", "");
							
							if(StringUtils.equals(valueStr, "ADITIONAL_CLAUSES")) {
								setAditionalClauses(field, contractClauses);
							} else {
								if(!StringUtils.contains(valueStr, " ")){
									String newValue = contractOtherInfo.getOrDefault(valueStr, "");
									setField(field, newValue);
								} else {
									String newValue = "";
									String[] splits = StringUtils.split(valueStr, " ");
									for(int i=0; i<splits.length; i++) {
										if(splits[i].contains("_"))
											newValue += contractOtherInfo.getOrDefault(splits[i], "") + " ";
									}
									setField(field, newValue);
								}
							}
						} else if(!StringUtils.isBlank(valueStr) && StringUtils.containsIgnoreCase(valueStr, "${")) {
							valueStr = valueStr.replace("${", "");
							valueStr = valueStr.replace("}", "");
							
							if(!StringUtils.contains(valueStr, " ")){
								String newValue = contractFillInfo.getOrDefault(valueStr, "");
								setField(field, newValue);
							}
						}
					
					}
						
				}
			}

	        // Add Sepe info if exists
	        if(AonStringUtils.isNotBlank(sepeIde))
	        	addSepeInfo(pdfDocument, sepeIde, comunicationDate);
	        
	        // Remove unsed pages
	        pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			
			pdfDocument.setAllSecurityToBeRemoved(true);
	        
	        pdfDocument.save(out);
			pdfDocument.close();
			
			return out.toByteArray();
		
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static byte[] fillPracticeContract(String sepeIde, Date comunicationDate, Map<String, String> contractOtherInfo, Map<String, String> contractFillInfo, TreeMap<String, String> contractClauses) {
		InputStream is = ContractFill.class.getResourceAsStream("practicas.pdf");
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		try (PDDocument pdfDocument = Loader.loadPDF(is)){
			
			pdfDocument.setAllSecurityToBeRemoved(true);
			
			PDDocumentCatalog doc = pdfDocument.getDocumentCatalog();
			PDAcroForm acroForm = doc.getAcroForm();
			
			if(null != acroForm) {
				PDResources resources = new PDResources();
				PDFont font = new PDType1Font(FontName.HELVETICA);
				resources.add(font);
				acroForm.setDefaultResources(resources);
				
				for(PDField field : acroForm.getFields()) {
					defaultCheckBox(field);
					
					String valueStr = field.getValueAsString();
					String fieldName = field.getPartialName();
					String renderFieldName = FIELDNAMESTOMAP.getOrDefault(fieldName, null);
					
					if(null != renderFieldName) {
						String newValue = contractFillInfo.getOrDefault(renderFieldName, "");
						setField(field, newValue);
					} else {
					
						if(!StringUtils.isBlank(valueStr) && StringUtils.containsIgnoreCase(valueStr, "$aon:")) {
							valueStr = valueStr.replace("$aon:", "");
							
							if(StringUtils.equals(valueStr, "ADITIONAL_CLAUSES")) {
								setAditionalClauses(field, contractClauses);
							} else {
								if(!StringUtils.contains(valueStr, " ")){
									String newValue = contractOtherInfo.getOrDefault(valueStr, "");
									setField(field, newValue);
								} else {
									String newValue = "";
									String[] splits = StringUtils.split(valueStr, " ");
									for(int i=0; i<splits.length; i++) {
										if(splits[i].contains("_"))
											newValue += contractOtherInfo.getOrDefault(splits[i], "") + " ";
									}
									setField(field, newValue);
								}
							}
						} else if(!StringUtils.isBlank(valueStr) && StringUtils.containsIgnoreCase(valueStr, "${")) {
							valueStr = valueStr.replace("${", "");
							valueStr = valueStr.replace("}", "");
							
							if(!StringUtils.contains(valueStr, " ")){
								String newValue = contractFillInfo.getOrDefault(valueStr, "");
								setField(field, newValue);
							}
						}
					
					}
						
				}
			}
			
	        // Add Sepe info if exists
	        if(AonStringUtils.isNotBlank(sepeIde))
	        	addSepeInfo(pdfDocument, sepeIde, comunicationDate);
	        
	        // Remove unsed pages
	        pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			
			pdfDocument.setAllSecurityToBeRemoved(true);
	        
	        pdfDocument.save(out);
			pdfDocument.close();
			
			return out.toByteArray();
		
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static byte[] fillTemporalContract(Integer contractType, String sepeIde, Date comunicationDate, Map<String, String> contractOtherInfo, Map<String, String> contractFillInfo, TreeMap<String, String> contractClauses) {
		InputStream is = ContractFill.class.getResourceAsStream("temporal.pdf");
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		try (PDDocument pdfDocument = Loader.loadPDF(is)){
			
			PDDocumentCatalog doc = pdfDocument.getDocumentCatalog();
			PDAcroForm acroForm = doc.getAcroForm();
			
			if(null != acroForm) {
				PDResources resources = new PDResources();
				PDFont font = new PDType1Font(FontName.HELVETICA);
				resources.add(font);
				acroForm.setDefaultResources(resources);
				
				for(PDField field : acroForm.getFields()) {
					defaultCheckBox(field);
					
					String valueStr = field.getValueAsString();
					String fieldName = field.getPartialName();
					String renderFieldName = FIELDNAMESTOMAP.getOrDefault(fieldName, null);
					
					if(null != renderFieldName) {
						String newValue = contractFillInfo.getOrDefault(renderFieldName, "");
						setField(field, newValue);
					} else {
					
						if(!StringUtils.isBlank(valueStr) && StringUtils.containsIgnoreCase(valueStr, "$aon:")) {
							valueStr = valueStr.replace("$aon:", "");
							
							if(StringUtils.equals(valueStr, "ADITIONAL_CLAUSES")) {
								setAditionalClauses(field, contractClauses);
							} else {
								if(!StringUtils.contains(valueStr, " ")){
									String newValue = contractOtherInfo.getOrDefault(valueStr, "");
									setField(field, newValue);
								} else {
									String newValue = "";
									String[] splits = StringUtils.split(valueStr, " ");
									for(int i=0; i<splits.length; i++) {
										if(splits[i].contains("_"))
											newValue += contractOtherInfo.getOrDefault(splits[i], "") + " ";
									}
									setField(field, newValue);
								}
							}
						} else if(!StringUtils.isBlank(valueStr) && StringUtils.containsIgnoreCase(valueStr, "${")) {
							valueStr = valueStr.replace("${", "");
							valueStr = valueStr.replace("}", "");
							
							if(!StringUtils.contains(valueStr, " ")){
								String newValue = contractFillInfo.getOrDefault(valueStr, "");
								setField(field, newValue);
							}
						}
					
					}
						
				}
			}
			
	        // Add Sepe info if exists
	        if(AonStringUtils.isNotBlank(sepeIde))
	        	addSepeInfo(pdfDocument, sepeIde, comunicationDate);
	        
	        // Remove unsed pages
	        removeTemporalPages(contractType, pdfDocument);
	        
	        pdfDocument.setAllSecurityToBeRemoved(true);
	        
	        pdfDocument.save(out);
			pdfDocument.close();
			
			return out.toByteArray();
		
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static void removeTemporalPages(Integer contractType, PDDocument pdfDocument) {
		if(contractType.equals(401) || contractType.equals(501) || contractType.equals(402) || contractType.equals(502)) {
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
		} else if(contractType.equals(410) || contractType.equals(510)) {
			pdfDocument.removePage(7);
			pdfDocument.removePage(7);
			pdfDocument.removePage(7);
			pdfDocument.removePage(7);
			pdfDocument.removePage(7);
			pdfDocument.removePage(7);
			pdfDocument.removePage(7);
			pdfDocument.removePage(7);
			pdfDocument.removePage(7);
			pdfDocument.removePage(7);
			pdfDocument.removePage(7);
			pdfDocument.removePage(7);
			pdfDocument.removePage(7);
		} else if(contractType.equals(430) || contractType.equals(530)) {
			pdfDocument.removePage(3);
			pdfDocument.removePage(3);
			pdfDocument.removePage(3);
			pdfDocument.removePage(3);
			pdfDocument.removePage(3);
			pdfDocument.removePage(3);
			pdfDocument.removePage(3);
			pdfDocument.removePage(3);
			pdfDocument.removePage(3);
			pdfDocument.removePage(3);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
			pdfDocument.removePage(4);
		}
	}
	
	public static void defaultCheckBox(PDField field) throws IOException {
	    if (field instanceof PDCheckBox) {
	    	try {
		        ((PDCheckBox) field).unCheck();
	    	} catch (Exception e) {
				System.out.println("Error default PDFCheckBox -> " + field.getPartialName());
			}
	    }
	}

	public static void setField(PDField field, String value) throws IOException {
	    if (field instanceof PDTextField) {
	    	try{
		        field.setValue(value);
		        ((PDTextField) field).setDefaultValue(value);
	    	} catch (Exception e) {
				System.out.println("ERR : " + field.getValueAsString());
			}
	    } else {
	        System.out.println("Tipo no identificado");
	    }
	}
	
	public static void setAditionalClauses(PDField field, TreeMap<String, String> contractClauses) throws IOException {
	    String clauses = "\n";
		for(Entry<String, String> entry : contractClauses.entrySet()) {
			System.out.println("setAditionalClauses  --> " + entry.getKey());
			clauses += "\t" + entry.getKey() + " :  \t\t" + entry.getValue() + "\n\n";
		}
		try {
			field.setValue(clauses);
		} catch (Exception e) {}
		
		((PDTextField) field).setDefaultValue(clauses);
	}
	
	private static void addSepeInfo(PDDocument document, String sepeIde, Date comunicationDate) {
		// Create a document and add a page to it
		PDPage page = document.getPage(0);

		// Create a new font object selecting one of the PDF base fonts
		PDFont font = new PDType1Font(FontName.HELVETICA_BOLD);

		// Start a new content stream which will "hold" the to be created content
		try {
			PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
			
			// Se imprime en orden inverso, ¿por que?, no lo se, creo que por el APPEND
			
			// Define a text content stream using the selected font, moving the cursor and drawing the text "Hello World"
			contentStream.beginText();
			contentStream.setStrokingColor(Color.RED);
			contentStream.setNonStrokingColor(Color.RED);
			contentStream.setFont( font, 12 );
			contentStream.newLineAtOffset( 300, 785 );
			contentStream.showText("Registro SEPE");
			contentStream.endText();
			
			contentStream.beginText();
			contentStream.setStrokingColor(Color.RED);
			contentStream.setNonStrokingColor(Color.RED);
			contentStream.setFont( font, 10 );
			contentStream.newLineAtOffset( 270, 765 );
			contentStream.showText("IDE : " + sepeIde);
			contentStream.endText();
			
			contentStream.beginText();
			contentStream.setStrokingColor(Color.RED);
			contentStream.setNonStrokingColor(Color.RED);
			contentStream.setFont( font, 10 );
			contentStream.newLineAtOffset( 270, 750 );
			contentStream.showText("F. Comunicaci\u00f3n : " + (null == comunicationDate ? "" : dateFormat.format(comunicationDate)));
			contentStream.endText();
			
			contentStream.setNonStrokingColor(Color.RED);
			
			contentStream.moveTo(260, 805);
			contentStream.lineTo(420, 805);
			contentStream.stroke();
			
			contentStream.moveTo(260, 805);
			contentStream.lineTo(260, 740);
			contentStream.stroke();
			
			contentStream.moveTo(420, 805);
			contentStream.lineTo(420, 740);
			contentStream.stroke();
			
			contentStream.moveTo(260, 740);
			contentStream.lineTo(420, 740);
			contentStream.stroke();

			// Make sure that the content stream is closed:
			contentStream.close();
		} catch (IOException e) {
			System.err.println("ERROR SEPE PDF");
			e.printStackTrace();
		}
	}

}
