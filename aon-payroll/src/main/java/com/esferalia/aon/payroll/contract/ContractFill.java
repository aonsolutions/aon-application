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
	
	public static byte[] fillCopyBasic(
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
		
		try (PDDocument pdfDocument = Loader.loadPDF(is)){
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
	
	private static byte[] fillIndefiniteCopyBasic(Integer contractType, String sepeIde, Date comunicationDate, Map<String, String> contractOtherInfo, Map<String, String> contractFillInfo, TreeMap<String, String> contractClauses) {
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
	
	private static byte[] fillFormationCopyBasic(String sepeIde, Date comunicationDate, Map<String, String> contractOtherInfo, Map<String, String> contractFillInfo, TreeMap<String, String> contractClauses) {
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
	
	private static byte[] fillPracticeCopyBasic(String sepeIde, Date comunicationDate, Map<String, String> contractOtherInfo, Map<String, String> contractFillInfo, TreeMap<String, String> contractClauses) {
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
						if(AonStringUtils.equalsIgnoreCase(fieldName, "TEXTOCasilla de verificaci\u00f3n25") && null != contractOtherInfo.get("T_EMPLOYEE_CONTRACT_DIST_ADDR")) {
							 ((PDCheckBox) field).check();
						}
						
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
	
	private static byte[] fillTemporalCopyBasic(Integer contractType, String sepeIde, Date comunicationDate, Map<String, String> contractOtherInfo, Map<String, String> contractFillInfo, TreeMap<String, String> contractClauses) {
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
						if(AonStringUtils.equalsIgnoreCase(fieldName, "TEXTOCasilla de verificaci\u00f3n25") && null != contractOtherInfo.get("T_EMPLOYEE_CONTRACT_DIST_ADDR")) {
							 ((PDCheckBox) field).check();
						}
						
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
			
			// Add Copy Basic info if exists
	        addCopyBasicInfo(pdfDocument, contractOtherInfo.get("LEGAL_REPRESENTATIVE"));
	        
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
			contentStream.newLineAtOffset( 150, 100 );
			contentStream.showText("FIRMA DE LOS REPRESENTANTES DE LOS TRABAJADORES");
			contentStream.endText();
			
			contentStream.beginText();
			contentStream.setStrokingColor(Color.BLACK);
			contentStream.setNonStrokingColor(Color.BLACK);
			contentStream.setFont( font, 10 );
			contentStream.newLineAtOffset( 220, 60 );
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
