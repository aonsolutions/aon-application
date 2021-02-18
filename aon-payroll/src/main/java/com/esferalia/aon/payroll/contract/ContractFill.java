package com.esferalia.aon.payroll.contract;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

public class ContractFill {
	
	@SuppressWarnings("serial")
	private static final Map<String, String> FIELDNAMESTOMAP = new HashMap<String,String>(){
		{
			put("Texto10", "ENTERPRISE_COUNTRY_CODE");
			put("Texto14", "ENTERPRISE_MUNICIPALITY_CODE");
			put("Texto19", "ENTERPRISE_ZIP");
			put("REG_CCC", "ENTERPRISE_CCC_REG");
			put("PRV_CCC", "ENTERPRISE_CCC_PRV");
			put("NUM_CCC", "ENTERPRISE_CCC_NUM");
			put("DC_CCC", "ENTERPRISE_CCC_DC");
			put("Texto3441", "ENTERPRISE_ACTIVITY_CODE");
			put("COD_PAISCT", "WORKPLC_COUNTRY_CODE");
			put("COD_MUNCT", "WORKPLC_MUNICIPALITY_CODE");
			put("COD_NACTRA", "E_NATIONALITY_CODE");
			put("COD_MUNDO", "E_MUNICIPALITY_ADDR_CODE");
			put("COD_PAISDO", "E_COUNTRY_ADDR_CODE");
			put("PRV_NASS", "E_SS1");
			put("NUM_NASS", "E_SS2");
			put("DC_NASS", "E_SS3");
			put("DEN_NVFOR", "E_FORMATIVE_LVL");
			put("COD_NVFOR", "E_FORMATIVE_LVL_CODE");
		}
	};
	
	public static byte[] fillContract(Integer contractType, Map<String, String> contractOtherInfo, Map<String, String> contractFillInfo, Map<String, String> contractClauses) {
		if(null == contractType)
			return null;
		
		if(contractType >= 100 && contractType <= 400) 
			return fillIndefiniteContract(contractOtherInfo, contractFillInfo, contractClauses);
		else if (contractType == 421) 
			return fillFormationContract(contractOtherInfo, contractFillInfo, contractClauses);
		else if (contractType == 420 || contractType == 520) 
			return fillPracticeContract(contractOtherInfo, contractFillInfo, contractClauses);
		else 
			return fillTemporalContract(contractOtherInfo, contractFillInfo, contractClauses);
		
	}
	
	private static byte[] fillIndefiniteContract(Map<String, String> contractOtherInfo, Map<String, String> contractFillInfo, Map<String, String> contractClauses) {
		InputStream is = ContractFill.class.getResourceAsStream("indefinido.pdf");
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		try (PDDocument pdfDocument = PDDocument.load(is)){
			
			pdfDocument.setAllSecurityToBeRemoved(true);
			
			PDDocumentCatalog doc = pdfDocument.getDocumentCatalog();
			PDAcroForm acroForm = doc.getAcroForm();
			
			if(null != acroForm) {
				for(PDField field : acroForm.getFields()) {
					String valueStr = field.getValueAsString();
					String fieldName = field.getPartialName();
					String renderFieldName = FIELDNAMESTOMAP.getOrDefault(fieldName, null);
					
					if(null != renderFieldName) {
						String newValue = contractFillInfo.getOrDefault(renderFieldName, "");
						newValue = newValue.toUpperCase();
						setField(field, newValue);
					} else {
					
						if(!StringUtils.isBlank(valueStr) && StringUtils.containsIgnoreCase(valueStr, "$aon:")) {
							valueStr = valueStr.replace("$aon:", "");
							
							if(StringUtils.equals(valueStr, "ADITIONAL_CLAUSES")) {
								setAditionalClauses(field, contractClauses);
							} else {
								if(!StringUtils.contains(valueStr, " ")){
									String newValue = contractOtherInfo.getOrDefault(valueStr, "");
									newValue = newValue.toUpperCase();
									setField(field, newValue);
								} else {
									String newValue = "";
									String[] splits = StringUtils.split(valueStr, " ");
									for(int i=0; i<splits.length; i++) {
										if(splits[i].contains("_"))
											newValue += contractOtherInfo.getOrDefault(splits[i], "") + " ";
									}
									newValue = newValue.toUpperCase();
									setField(field, newValue);
								}
							}
							
						} else if(!StringUtils.isBlank(valueStr) && StringUtils.containsIgnoreCase(valueStr, "${")) {
							valueStr = valueStr.replace("${", "");
							valueStr = valueStr.replace("}", "");
							
							if(!StringUtils.contains(valueStr, " ")){
								String newValue = contractFillInfo.getOrDefault(valueStr, "");
								newValue = newValue.toUpperCase();
								setField(field, newValue);
							}
						}
					
					}
						
				}
			}
			
			pdfDocument.setAllSecurityToBeRemoved(true);
	        COSDictionary dictionary = pdfDocument.getDocumentCatalog().getCOSObject();
	        dictionary.removeItem(COSName.PERMS);
			
			// vvv--- new 
//			COSDictionary dictionary = pdfDocument.getDocumentCatalog().getCOSObject();
//			dictionary.setNeedToBeUpdated(true);
//			dictionary = (COSDictionary) dictionary.getDictionaryObject(COSName.ACRO_FORM);
//			dictionary.setNeedToBeUpdated(true);
//			COSArray array = (COSArray) dictionary.getDictionaryObject(COSName.FIELDS);
//			array.setNeedToBeUpdated(true);
			// ^^^--- new 
			
//			pdfDocument.save("/Users/sergio/Desktop/contrato.pdf");
			pdfDocument.save(out);
			pdfDocument.close();
			
			return out.toByteArray();
		
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static byte[] fillFormationContract(Map<String, String> contractOtherInfo, Map<String, String> contractFillInfo, Map<String, String> contractClauses) {
		InputStream is = ContractFill.class.getResourceAsStream("formacion.pdf");
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		try (PDDocument pdfDocument = PDDocument.load(is)){
			
			pdfDocument.setAllSecurityToBeRemoved(true);
			
			PDDocumentCatalog doc = pdfDocument.getDocumentCatalog();
			PDAcroForm acroForm = doc.getAcroForm();
			
			if(null != acroForm) {
				for(PDField field : acroForm.getFields()) {
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
			
			pdfDocument.setAllSecurityToBeRemoved(true);
	        COSDictionary dictionary = pdfDocument.getDocumentCatalog().getCOSObject();
	        dictionary.removeItem(COSName.PERMS);
			
			// vvv--- new 
//			COSDictionary dictionary = pdfDocument.getDocumentCatalog().getCOSObject();
//			dictionary.setNeedToBeUpdated(true);
//			dictionary = (COSDictionary) dictionary.getDictionaryObject(COSName.ACRO_FORM);
//			dictionary.setNeedToBeUpdated(true);
//			COSArray array = (COSArray) dictionary.getDictionaryObject(COSName.FIELDS);
//			array.setNeedToBeUpdated(true);
			// ^^^--- new 

//			pdfDocument.save("/Users/sergio/Desktop/contrato.pdf");
			pdfDocument.save(out);
			pdfDocument.close();
			
			return out.toByteArray();
		
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static byte[] fillPracticeContract(Map<String, String> contractOtherInfo, Map<String, String> contractFillInfo, Map<String, String> contractClauses) {
		InputStream is = ContractFill.class.getResourceAsStream("practicas.pdf");
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		try (PDDocument pdfDocument = PDDocument.load(is)){
			
			pdfDocument.setAllSecurityToBeRemoved(true);
			
			PDDocumentCatalog doc = pdfDocument.getDocumentCatalog();
			PDAcroForm acroForm = doc.getAcroForm();
			
			if(null != acroForm) {
				for(PDField field : acroForm.getFields()) {
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
			
			pdfDocument.setAllSecurityToBeRemoved(true);
	        COSDictionary dictionary = pdfDocument.getDocumentCatalog().getCOSObject();
	        dictionary.removeItem(COSName.PERMS);
			
			// vvv--- new 
//			COSDictionary dictionary = pdfDocument.getDocumentCatalog().getCOSObject();
//			dictionary.setNeedToBeUpdated(true);
//			dictionary = (COSDictionary) dictionary.getDictionaryObject(COSName.ACRO_FORM);
//			dictionary.setNeedToBeUpdated(true);
//			COSArray array = (COSArray) dictionary.getDictionaryObject(COSName.FIELDS);
//			array.setNeedToBeUpdated(true);
			// ^^^--- new 

//			pdfDocument.save("/Users/sergio/Desktop/contrato.pdf");
			pdfDocument.save(out);
			pdfDocument.close();
			
			return out.toByteArray();
		
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static byte[] fillTemporalContract(Map<String, String> contractOtherInfo, Map<String, String> contractFillInfo, Map<String, String> contractClauses) {
		InputStream is = ContractFill.class.getResourceAsStream("temporal.pdf");
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		try (PDDocument pdfDocument = PDDocument.load(is)){
			
			pdfDocument.setAllSecurityToBeRemoved(true);
			
			PDDocumentCatalog doc = pdfDocument.getDocumentCatalog();
			PDAcroForm acroForm = doc.getAcroForm();
			
			if(null != acroForm) {
				for(PDField field : acroForm.getFields()) {
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
			
			pdfDocument.setAllSecurityToBeRemoved(true);
	        COSDictionary dictionary = pdfDocument.getDocumentCatalog().getCOSObject();
	        dictionary.removeItem(COSName.PERMS);
			
			// vvv--- new 
//			COSDictionary dictionary = pdfDocument.getDocumentCatalog().getCOSObject();
//			dictionary.setNeedToBeUpdated(true);
//			dictionary = (COSDictionary) dictionary.getDictionaryObject(COSName.ACRO_FORM);
//			dictionary.setNeedToBeUpdated(true);
//			COSArray array = (COSArray) dictionary.getDictionaryObject(COSName.FIELDS);
//			array.setNeedToBeUpdated(true);
			// ^^^--- new 

//			pdfDocument.save("/Users/sergio/Desktop/contrato.pdf");
			pdfDocument.save(out);
			pdfDocument.close();
			
			return out.toByteArray();
		
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void setField(PDField field, String value) throws IOException {
	    if (field instanceof PDCheckBox) {
	        field.setValue("No");
	    } else if (field instanceof PDTextField) {
	    	field.getCOSObject().removeItem(COSName.AP);
	        System.out.println("Original value: " + field.getValueAsString());
	        field.setValue(value);
	        ((PDTextField) field).setDefaultValue(value);
//	        ((PDTextField) field).setDefaultAppearance(value);
	        System.out.println("New value: " + field.getValueAsString());
	    } else {
	        System.out.println("Tipo no identificado");
	    }

//	    COSDictionary fieldDictionary = field.getCOSObject();
//	    COSDictionary dictionary = (COSDictionary) fieldDictionary.getDictionaryObject(COSName.AP);
//	    dictionary.setNeedToBeUpdated(true);
//	    COSStream stream = (COSStream) dictionary.getDictionaryObject(COSName.N);
//	    stream.setNeedToBeUpdated(true);
//	    while (fieldDictionary != null) {
//	        fieldDictionary.setNeedToBeUpdated(true);
//	        fieldDictionary = (COSDictionary) fieldDictionary.getDictionaryObject(COSName.PARENT);
//	    }
	}
	
	public static void setAditionalClauses(PDField field, Map<String, String> contractClauses) throws IOException {
	    
		System.out.println("Original value: " + field.getValueAsString());
		
		field.getCOSObject().removeItem(COSName.AP);
		
		String clauses = "\n";
		Integer line = 1;
		for(Entry<String, String> entry : contractClauses.entrySet()) {
			clauses += "\t" + line + "  -  " + entry.getKey() + " :  \t\t" + entry.getValue() + "\n\n";
			line++;
		}
		
		field.setValue(clauses);
		((PDTextField) field).setDefaultValue(clauses);
		
		System.out.println("New value: " + field.getValueAsString());
		
	}

}
