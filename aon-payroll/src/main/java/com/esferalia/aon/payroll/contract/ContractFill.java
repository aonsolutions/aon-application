package com.esferalia.aon.payroll.contract;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
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
			HashMap<String, String> contractClauses) throws IllegalArgumentException {
		
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
	
	public static byte[] fillCopyBasic(
			Integer contractType, 
			String sepeIde, 
			Date comunicationDate,
			Map<String, String> contractOtherInfo, 
			Map<String, String> contractFillInfo, 
			HashMap<String, String> contractClauses) throws IllegalArgumentException {
		
		if(null == contractType)
			throw new IllegalArgumentException("El tipo de contrato no esta definido.");
		
		initializeFieldNames();
		checkContractOtherInfo(contractOtherInfo);
		
		if(contractType >= 100 && contractType <= 400) 
			return fillIndefiniteCopyBasic(contractType, sepeIde, comunicationDate, contractOtherInfo, contractFillInfo, contractClauses);
		else if (contractType == 421) 
			return fillFormationCopyBasic(sepeIde, comunicationDate,contractOtherInfo, contractFillInfo, contractClauses);
		else if (contractType == 420 || contractType == 520) 
			return fillPracticeCopyBasic(sepeIde, comunicationDate,contractOtherInfo, contractFillInfo, contractClauses);
		else 
			return fillTemporalCopyBasic(contractType, sepeIde, comunicationDate, contractOtherInfo, contractFillInfo, contractClauses);
		
	}
	
	public static byte[] fillContractExtension(Map<String, String> contractExtensionFillInfo) throws IllegalArgumentException {
		return fillExtensionContract(contractExtensionFillInfo);
	}
	
	public static byte[] fillContractRelocation(Map<String, String> contractRelocationFillInfo) throws IllegalArgumentException {
		return fillRelocationContract(contractRelocationFillInfo);
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
		
		FIELDNAMESTOMAP.put("AA0103-DNI", "T_ENTERPRISE_DIR_STAFF_NIF");
		FIELDNAMESTOMAP.put("AA0502_DNI", "T_LEGAL_REPRESENTATIVE_NIF");
//		FIELDNAMESTOMAP.put("FX_NAC_TRA", "E_BDAT");
//		FIELDNAMESTOMAP.put("FX_INICIO", "C_START");
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
	
	private static byte[] fillExtensionContract(Map<String, String> contractExtensionFillInfo) {
		InputStream is = ContractFill.class.getResourceAsStream("prorroga.pdf");
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		try (PDDocument pdfDocument = Loader.loadPDF(is.readAllBytes())){
			pdfDocument.setAllSecurityToBeRemoved(true);
			PDDocumentCatalog doc = pdfDocument.getDocumentCatalog();
			PDAcroForm acroForm = doc.getAcroForm();
			
			if(null != acroForm) {
				for(PDField field : acroForm.getFields()) {
					defaultCheckBox(field);
					
					String fieldName = field.getPartialName();
					String newValue = contractExtensionFillInfo.getOrDefault(fieldName, "");
					newValue = AonStringUtils.isBlank(newValue) ? "" : newValue.toUpperCase();
					setField(field, newValue);	
				}
			}
			
			// TODO: if we want to remove form and export as a plain text, uncomment the line below
//			acroForm.flatten();
	        
			pdfDocument.save(out);
			pdfDocument.close();
			
			return out.toByteArray();
		
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static byte[] fillRelocationContract(Map<String, String> contractRelocationFillInfo) {
		InputStream is = ContractFill.class.getResourceAsStream("propuestaRecolocacion.pdf");
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		try (PDDocument pdfDocument = Loader.loadPDF(is.readAllBytes())){
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
					
					String fieldName = field.getPartialName();
					String newValue = contractRelocationFillInfo.getOrDefault(fieldName, "");
					newValue = AonStringUtils.isBlank(newValue) ? "" : newValue.toUpperCase();
					setField(field, newValue);	
				}
			}
			
	        pdfDocument.setAllSecurityToBeRemoved(true);
	        
			pdfDocument.save(out);
			pdfDocument.close();
			
			return out.toByteArray();
		
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static byte[] fillIndefiniteContract(Integer contractType, String sepeIde, Date comunicationDate, Map<String, String> contractOtherInfo, Map<String, String> contractFillInfo, HashMap<String, String> contractClauses) {
		InputStream is = ContractFill.class.getResourceAsStream("indefinido.pdf");
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		try (PDDocument pdfDocument = Loader.loadPDF(is.readAllBytes())){
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
								setAditionalClauses(pdfDocument, field, contractClauses);
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
	
	private static byte[] fillIndefiniteCopyBasic(Integer contractType, String sepeIde, Date comunicationDate, Map<String, String> contractOtherInfo, Map<String, String> contractFillInfo, HashMap<String, String> contractClauses) {
		InputStream is = ContractFill.class.getResourceAsStream("indefinido.pdf");
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		try (PDDocument pdfDocument = Loader.loadPDF(is.readAllBytes())){
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
								setAditionalClauses(pdfDocument, field, contractClauses);
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
			
	        // Add Copy Basic info if exists
	        addCopyBasicInfo(pdfDocument, contractOtherInfo.get("LEGAL_REPRESENTATIVE"));
	        
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

	private static byte[] fillFormationContract(String sepeIde, Date comunicationDate, Map<String, String> contractOtherInfo, Map<String, String> contractFillInfo, HashMap<String, String> contractClauses) {
		InputStream is = ContractFill.class.getResourceAsStream("formacion.pdf");
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		try (PDDocument pdfDocument = Loader.loadPDF(is.readAllBytes())){
			
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
								setAditionalClauses(pdfDocument, field, contractClauses);
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
	
	private static byte[] fillFormationCopyBasic(String sepeIde, Date comunicationDate, Map<String, String> contractOtherInfo, Map<String, String> contractFillInfo, HashMap<String, String> contractClauses) {
		InputStream is = ContractFill.class.getResourceAsStream("formacion.pdf");
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		try (PDDocument pdfDocument = Loader.loadPDF(is.readAllBytes())){
			
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
								setAditionalClauses(pdfDocument, field, contractClauses);
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

			// Add Copy Basic info if exists
	        addCopyBasicInfo(pdfDocument, contractOtherInfo.get("LEGAL_REPRESENTATIVE"));
	        
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

	private static byte[] fillPracticeContract(String sepeIde, Date comunicationDate, Map<String, String> contractOtherInfo, Map<String, String> contractFillInfo, HashMap<String, String> contractClauses) {
		InputStream is = ContractFill.class.getResourceAsStream("practicas.pdf");
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		try (PDDocument pdfDocument = Loader.loadPDF(is.readAllBytes())){
			
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
								setAditionalClauses(pdfDocument, field, contractClauses);
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
	
	private static byte[] fillPracticeCopyBasic(String sepeIde, Date comunicationDate, Map<String, String> contractOtherInfo, Map<String, String> contractFillInfo, HashMap<String, String> contractClauses) {
		InputStream is = ContractFill.class.getResourceAsStream("practicas.pdf");
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		try (PDDocument pdfDocument = Loader.loadPDF(is.readAllBytes())){
			
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
								setAditionalClauses(pdfDocument, field, contractClauses);
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

			// Add Copy Basic info if exists
	        addCopyBasicInfo(pdfDocument, contractOtherInfo.get("LEGAL_REPRESENTATIVE"));
	        
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

	private static byte[] fillTemporalContract(Integer contractType, String sepeIde, Date comunicationDate, Map<String, String> contractOtherInfo, Map<String, String> contractFillInfo, HashMap<String, String> contractClauses) {
		InputStream is = ContractFill.class.getResourceAsStream("temporal.pdf");
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		try (PDDocument pdfDocument = Loader.loadPDF(is.readAllBytes())){
			
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
						String newValue = contractFillInfo.getOrDefault(renderFieldName, null);
						if(null == newValue) newValue = contractOtherInfo.getOrDefault(renderFieldName, "");
						setField(field, newValue);
					} else {
						if(AonStringUtils.equalsIgnoreCase(fieldName, "TEXTOCasilla de verificaci\u00f3n25") && null != contractOtherInfo.get("T_EMPLOYEE_CONTRACT_DIST_ADDR")) {
							 ((PDCheckBox) field).check();
						}
						
						if(!StringUtils.isBlank(valueStr) && StringUtils.containsIgnoreCase(valueStr, "$aon:")) {
							valueStr = valueStr.replace("$aon:", "");
							
							if(StringUtils.equals(valueStr, "ADITIONAL_CLAUSES")) {
								setAditionalClauses(pdfDocument, field, contractClauses);
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
	
	private static byte[] fillTemporalCopyBasic(Integer contractType, String sepeIde, Date comunicationDate, Map<String, String> contractOtherInfo, Map<String, String> contractFillInfo, HashMap<String, String> contractClauses) {
		InputStream is = ContractFill.class.getResourceAsStream("temporal.pdf");
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		try (PDDocument pdfDocument = Loader.loadPDF(is.readAllBytes())){
			
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
						if(AonStringUtils.equalsIgnoreCase(fieldName, "TEXTOCasilla de verificaci\u00f3n25") && null != contractOtherInfo.get("T_EMPLOYEE_CONTRACT_DIST_ADDR")) {
							 ((PDCheckBox) field).check();
						}
						
						if(!StringUtils.isBlank(valueStr) && StringUtils.containsIgnoreCase(valueStr, "$aon:")) {
							valueStr = valueStr.replace("$aon:", "");
							
							if(StringUtils.equals(valueStr, "ADITIONAL_CLAUSES")) {
								setAditionalClauses(pdfDocument, field, contractClauses);
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
			
			// Add Copy Basic info if exists
	        addTemporalCopyBasicInfo(pdfDocument, contractOtherInfo.get("LEGAL_REPRESENTATIVE"));
	        
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
			pdfDocument.removePage(3); // 4
			pdfDocument.removePage(3); // 5
			pdfDocument.removePage(3); // 6
			pdfDocument.removePage(3); // 7
			pdfDocument.removePage(3); // 8
			pdfDocument.removePage(3); // 9
			pdfDocument.removePage(3); // 10
			pdfDocument.removePage(3); // 11
			pdfDocument.removePage(3); // 12
			pdfDocument.removePage(3); // 13
			pdfDocument.removePage(4); // 15
			pdfDocument.removePage(4); // 16
//			pdfDocument.removePage(4); // 17
			pdfDocument.removePage(5); // 18
			pdfDocument.removePage(5); // 19
		}
	}
	
	public static void defaultCheckBox(PDField field) throws IOException {
	    if (field instanceof PDCheckBox) {
	    	try {
	    		((PDCheckBox) field).setActions(null);
		        ((PDCheckBox) field).unCheck();
	    	} catch (Exception e) {
				System.out.println("Error default PDFCheckBox -> " + field.getPartialName());
			}
	    }
	}

	public static void setField(PDField field, String value) throws IOException {
	    if (field instanceof PDTextField) {
	    	try{
	    		((PDTextField) field).setActions(null);
	    		field.setValue(value);
//		        ((PDTextField) field).setDefaultValue(value);
		        ((PDTextField) field).setValue(value);
//		        ((PDTextField) field).setDefaultAppearance("/Helv 8 Tf 0 g");
//		        ((PDTextField) field).setDefaultStyleString("/Helv 8 Tf 0 g");
//				field.getWidgets().get(0).setHidden(false);
	    	} catch (Exception e) {
				System.out.println("ERR TextField : " + field.getValueAsString() + " --> " + e.getMessage());
			}
	    } else if (field instanceof PDCheckBox) {
	    	try{
	    		((PDCheckBox) field).setActions(null);
		        if(AonStringUtils.isBlank(value) || AonStringUtils.equals(value, "N")) {
		        	((PDCheckBox) field).unCheck();
		    		((PDCheckBox) field).setValue("Off");
		        } else {
		        	((PDCheckBox) field).check();
		    		((PDCheckBox) field).setValue("S\u00ed");
		        }
	    	} catch (Exception e) {
				System.out.println("ERR CheckBoxField : " + field.getValueAsString() + " --> " + e.getMessage());
			}
	    } else {
	        System.out.println("Tipo no identificado");
	    }
	}
	
	public static void setAditionalClauses(
	        PDDocument pdfDocument,
	        PDField field,
	        HashMap<String, String> contractClauses
	) throws IOException {

	    // 1) Texto completo
	    StringBuilder fullText = new StringBuilder();
	    for (Entry<String, String> entry : contractClauses.entrySet()) {
	        fullText.append(entry.getKey()).append(":\n")
	                .append(entry.getValue()).append("\n\n");
	    }
	    String allClauses = fullText.toString();

	    PDTextField textField = (PDTextField) field;
	    PDAnnotationWidget widget = textField.getWidgets().get(0);
	    PDRectangle rect = widget.getRectangle();

	    float fontSize = 8f;
	    float leading  = fontSize * 1.2f;   // el MISMO que usa buildFieldAppearance
	    float padding  = 3f;
	    PDFont font = new PDType1Font(FontName.HELVETICA);

	    float usableWidth  = rect.getWidth()  - 2 * padding;
	    float usableHeight = rect.getHeight() - 2 * padding;

	    // 2) Cuantas lineas ocupa el texto y cuantas caben en el campo
	    List<String> lines = wrapTextByWidth(allClauses, font, fontSize, usableWidth);
	    int maxLinesInField = (int) Math.floor(usableHeight / leading);

//	    System.out.println("[CLAUSULAS] lineas=" + lines.size()
//	            + " maxCampo=" + maxLinesInField
//	            + " -> " + (lines.size() <= maxLinesInField ? "CAMPO" : "ANEXO"));

	    // 3) Decision: cabe todo -> campo ; no cabe -> anexo completo
	    if (lines.size() <= maxLinesInField) {
	        // Cabe entero: lo metemos en el campo y pintamos NOSOTROS la apariencia
	        try {
	            textField.setValue(String.join("\n", lines));
	        } catch (Exception e) {
	            System.out.println("[CLAUSULAS] setValue fallo (usamos appearance propia): " + e.getMessage());
	        }
	        buildFieldAppearance(pdfDocument, widget, font, fontSize, leading, padding, lines);
	        pdfDocument.getDocumentCatalog().getAcroForm().setNeedAppearances(false);
	    } else {
	        // No cabe: dejamos referencia y mandamos TODO al anexo
	        try {
	            textField.setValue("VER ANEXO DE CLAUSULAS ADICIONALES");
	        } catch (Exception e) { /* ignorable */ }

	        List<String> anexoLines = new ArrayList<>(java.util.Arrays.asList(allClauses.split("\n")));
	        writeOverflowPages(pdfDocument, anexoLines);
	    }
	}
	
	private static void buildFieldAppearance(
	        PDDocument doc,
	        PDAnnotationWidget widget,
	        PDFont font,
	        float fontSize,
	        float leading,
	        float padding,
	        List<String> linesToDraw
	) throws IOException {

	    PDRectangle rect = widget.getRectangle();

	    PDAppearanceStream aps = new PDAppearanceStream(doc);
	    aps.setBBox(new PDRectangle(rect.getWidth(), rect.getHeight()));
	    PDResources res = new PDResources();
	    res.put(COSName.getPDFName("Helv"), font);
	    aps.setResources(res);

	    try (PDPageContentStream cs = new PDPageContentStream(doc, aps)) {
	        cs.beginText();
	        cs.setFont(font, fontSize);
	        cs.setLeading(leading);
	        // arrancamos arriba del todo, bajando una linea
	        cs.newLineAtOffset(padding, rect.getHeight() - padding - fontSize);
	        boolean first = true;
	        for (String line : linesToDraw) {
	            if (!first) cs.newLine();       // usa el leading fijado
	            cs.showText(line);
	            first = false;
	        }
	        cs.endText();
	    }

	    PDAppearanceDictionary apDict = new PDAppearanceDictionary();
	    apDict.setNormalAppearance(aps);
	    widget.setAppearance(apDict);
	}

	private static void writeOverflowPages(
	        PDDocument document,
	        List<String> originalLines
	) throws IOException {

	    // ===== CONFIGURACIÓN =====
	    float margin = 50;
	    float yStart = 750;
	    float titleFontSize = 10;
	    float textFontSize = 8;
	    float leading = textFontSize * 1.5f;
	    int maxLinesPerPage = 45;

	    PDRectangle pageSize = PDRectangle.A4;
	    float pageWidth = pageSize.getWidth();
	    float usableWidth = pageWidth - (margin * 2);

	    PDFont titleFont = new PDType1Font(FontName.HELVETICA_BOLD);
	    PDFont textFont = new PDType1Font(FontName.HELVETICA);

	    String title = "CLÁUSULAS ADICIONALES";

	    // ===== WRAP REAL POR ANCHO =====
	    String joinedText = String.join("\n", originalLines);
	    List<String> lines = wrapTextByWidth(
	            joinedText,
	            textFont,
	            textFontSize,
	            usableWidth
	    );

	    PDPage page = new PDPage(pageSize);
	    document.addPage(page);

	    PDPageContentStream cs = new PDPageContentStream(document, page);

	    // ===== TÍTULO =====
	    float titleWidth = titleFont.getStringWidth(title) / 1000 * titleFontSize;
	    float titleX = (pageWidth - titleWidth) / 2;

	    cs.beginText();
	    cs.setFont(titleFont, titleFontSize);
	    cs.newLineAtOffset(titleX, yStart);
	    cs.showText(title);
	    cs.endText();

	    // ===== TEXTO =====
	    cs.beginText();
	    cs.setFont(textFont, textFontSize);
	    cs.newLineAtOffset(margin, yStart - leading * 2);

	    int lineCount = 0;

	    for (String line : lines) {

	        if (lineCount == maxLinesPerPage) {
	            cs.endText();
	            cs.close();

	            page = new PDPage(pageSize);
	            document.addPage(page);
	            cs = new PDPageContentStream(document, page);

	            // --- Título nueva página ---
	            float w = titleFont.getStringWidth(title) / 1000 * titleFontSize;
	            float x = (pageWidth - w) / 2;

	            cs.beginText();
	            cs.setFont(titleFont, titleFontSize);
	            cs.newLineAtOffset(x, yStart);
	            cs.showText(title);
	            cs.endText();

	            cs.beginText();
	            cs.setFont(textFont, textFontSize);
	            cs.newLineAtOffset(margin, yStart - leading * 2);

	            lineCount = 0;
	        }

	        if (line.isEmpty()) {
	            cs.newLineAtOffset(0, -leading);
	            lineCount++;
	            continue;
	        }

	        line = sanitize(line);
	        cs.showText(line);
	        cs.newLineAtOffset(0, -leading);
	        lineCount++;
	    }

	    cs.endText();
	    cs.close();
	}
	
	private static String sanitize(String s) {
	    if (s == null) return "";
	    return s.replace('\u2018', '\'').replace('\u2019', '\'')
	            .replace('\u201C', '"').replace('\u201D', '"')
	            .replace('\u2013', '-').replace('\u2014', '-')
	            .replace('\u00A0', ' ');
	}

	private static List<String> wrapTextByWidth(
	        String text,
	        PDFont font,
	        float fontSize,
	        float maxWidth
	) throws IOException {

	    List<String> result = new ArrayList<>();

	    for (String paragraph : text.split("\n")) {

	        if (paragraph.trim().isEmpty()) {
	            result.add("");
	            continue;
	        }

	        String[] words = paragraph.split("\\s+");
	        StringBuilder line = new StringBuilder();

	        for (String word : words) {
	            String testLine = line.length() == 0 ? word : line + " " + word;
	            float size = font.getStringWidth(testLine) / 1000 * fontSize;

	            if (size > maxWidth) {
	                result.add(line.toString());
	                line = new StringBuilder(word);
	            } else {
	                if (line.length() > 0) line.append(" ");
	                line.append(word);
	            }
	        }

	        if (line.length() > 0) {
	            result.add(line.toString());
	        }
	    }
	    return result;
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
			contentStream.setFont( font, 10 );
			contentStream.newLineAtOffset( 300, 810 );
			contentStream.showText("Registro SEPE");
			contentStream.endText();
			
			contentStream.beginText();
			contentStream.setStrokingColor(Color.RED);
			contentStream.setNonStrokingColor(Color.RED);
			contentStream.setFont( font, 9 );
			contentStream.newLineAtOffset( 270, 790 );
			contentStream.showText("IDE : " + sepeIde);
			contentStream.endText();
			
			contentStream.beginText();
			contentStream.setStrokingColor(Color.RED);
			contentStream.setNonStrokingColor(Color.RED);
			contentStream.setFont( font, 9 );
			contentStream.newLineAtOffset( 270, 775 );
			contentStream.showText("F. Comunicaci\u00f3n : " + (null == comunicationDate ? "" : dateFormat.format(comunicationDate)));
			contentStream.endText();
			
			contentStream.setNonStrokingColor(Color.RED);
			
			contentStream.moveTo(260, 830);
			contentStream.lineTo(420, 830);
			contentStream.stroke();
			
			contentStream.moveTo(260, 830);
			contentStream.lineTo(260, 765);
			contentStream.stroke();
			
			contentStream.moveTo(420, 830);
			contentStream.lineTo(420, 765);
			contentStream.stroke();
			
			contentStream.moveTo(260, 765);
			contentStream.lineTo(420, 765);
			contentStream.stroke();

			// Make sure that the content stream is closed:
			contentStream.close();
		} catch (IOException e) {
			System.err.println("ERROR SEPE PDF");
			e.printStackTrace();
		}
	}
	
	private static void addCopyBasicInfo(PDDocument document, String legalRepresentative) {
		// Create a document and add a page to it
		PDPage firstPage = document.getPage(0);

		// Create a new font object selecting one of the PDF base fonts
		PDFont font = new PDType1Font(FontName.HELVETICA_BOLD);
		PDFont fontLight = new PDType1Font(FontName.HELVETICA);

		// Start a new content stream which will "hold" the to be created content
		try {
			PDPageContentStream contentStream = new PDPageContentStream(document, firstPage, PDPageContentStream.AppendMode.APPEND, true, true);
			
			// Se imprime en orden inverso, ¿por que?, no lo se, creo que por el APPEND
			
			// Define a text content stream using the selected font, moving the cursor and drawing the text "Hello World"
			contentStream.beginText();
			contentStream.setStrokingColor(Color.BLACK);
			contentStream.setNonStrokingColor(Color.BLACK);
			contentStream.setFont( font, 10 );
			contentStream.newLineAtOffset( 130, 750 );
			contentStream.showText("En cumplimiento del art. 8, punto 4 del Real Decreto Legislativo 2/2015");
			contentStream.endText();
			
			contentStream.beginText();
			contentStream.setStrokingColor(Color.BLACK);
			contentStream.setNonStrokingColor(Color.BLACK);
			contentStream.setFont( fontLight, 10 );
			contentStream.newLineAtOffset( 150, 735 );
			contentStream.showText("FIRMA DE LOS REPRESENTANTES DE LOS TRABAJADORES");
			contentStream.endText();
			
			// Make sure that the content stream is closed:
			contentStream.close();
		} catch (IOException e) {
			System.err.println("ERROR SEPE COPY BASIC PDF");
			e.printStackTrace();
		}
		
		// Create a document and add a page to it
		PDPage lastPage = document.getPage(document.getPages().getCount() - 1);

		// Start a new content stream which will "hold" the to be created content
		try {
			PDPageContentStream contentStream = new PDPageContentStream(document, lastPage, PDPageContentStream.AppendMode.APPEND, true, true);
			
			// Se imprime en orden inverso, ¿por que?, no lo se, creo que por el APPEND
			
			// Define a text content stream using the selected font, moving the cursor and drawing the text "Hello World"
			contentStream.beginText();
			contentStream.setStrokingColor(Color.BLACK);
			contentStream.setNonStrokingColor(Color.BLACK);
			contentStream.setFont( fontLight, 10 );
			contentStream.newLineAtOffset( 150, 120 );
			contentStream.showText("FIRMA DE LOS REPRESENTANTES DE LOS TRABAJADORES");
			contentStream.endText();
			
			contentStream.beginText();
			contentStream.setStrokingColor(Color.BLACK);
			contentStream.setNonStrokingColor(Color.BLACK);
			contentStream.setFont( font, 10 );
			contentStream.newLineAtOffset( 220, 80 );
			contentStream.showText(null == legalRepresentative ? "" : legalRepresentative);
			contentStream.endText();
			
			// Make sure that the content stream is closed:
			contentStream.close();
		} catch (IOException e) {
			System.err.println("ERROR SEPE COPY BASIC PDF");
			e.printStackTrace();
		}
	}
	
	private static void addTemporalCopyBasicInfo(PDDocument document, String legalRepresentative) {
		// Create a document and add a page to it
		PDPage firstPage = document.getPage(0);

		// Create a new font object selecting one of the PDF base fonts
		PDFont font = new PDType1Font(FontName.HELVETICA_BOLD);
		PDFont fontLight = new PDType1Font(FontName.HELVETICA);

		// Start a new content stream which will "hold" the to be created content
		try {
			PDPageContentStream contentStream = new PDPageContentStream(document, firstPage, PDPageContentStream.AppendMode.APPEND, true, true);
			
			// Se imprime en orden inverso, ¿por que?, no lo se, creo que por el APPEND
			
			// Define a text content stream using the selected font, moving the cursor and drawing the text "Hello World"
			contentStream.beginText();
			contentStream.setStrokingColor(Color.BLACK);
			contentStream.setNonStrokingColor(Color.BLACK);
			contentStream.setFont( font, 10 );
			contentStream.newLineAtOffset( 130, 715 );
			contentStream.showText("En cumplimiento del art. 8, punto 4 del Real Decreto Legislativo 2/2015");
			contentStream.endText();
			
			contentStream.beginText();
			contentStream.setStrokingColor(Color.BLACK);
			contentStream.setNonStrokingColor(Color.BLACK);
			contentStream.setFont( fontLight, 10 );
			contentStream.newLineAtOffset( 150, 700 );
			contentStream.showText("FIRMA DE LOS REPRESENTANTES DE LOS TRABAJADORES");
			contentStream.endText();
			
			// Make sure that the content stream is closed:
			contentStream.close();
		} catch (IOException e) {
			System.err.println("ERROR SEPE COPY BASIC PDF");
			e.printStackTrace();
		}
		
		// Create a document and add a page to it
		PDPage lastPage = document.getPage(document.getPages().getCount() - 1);

		// Start a new content stream which will "hold" the to be created content
		try {
			PDPageContentStream contentStream = new PDPageContentStream(document, lastPage, PDPageContentStream.AppendMode.APPEND, true, true);
			
			// Se imprime en orden inverso, ¿por que?, no lo se, creo que por el APPEND
			
			// Define a text content stream using the selected font, moving the cursor and drawing the text "Hello World"
			contentStream.beginText();
			contentStream.setStrokingColor(Color.BLACK);
			contentStream.setNonStrokingColor(Color.BLACK);
			contentStream.setFont( fontLight, 10 );
			contentStream.newLineAtOffset( 150, 120 );
			contentStream.showText("FIRMA DE LOS REPRESENTANTES DE LOS TRABAJADORES");
			contentStream.endText();
			
			contentStream.beginText();
			contentStream.setStrokingColor(Color.BLACK);
			contentStream.setNonStrokingColor(Color.BLACK);
			contentStream.setFont( font, 10 );
			contentStream.newLineAtOffset( 220, 80 );
			contentStream.showText(null == legalRepresentative ? "" : legalRepresentative);
			contentStream.endText();
			
			// Make sure that the content stream is closed:
			contentStream.close();
		} catch (IOException e) {
			System.err.println("ERROR SEPE COPY BASIC PDF");
			e.printStackTrace();
		}
	}

}
