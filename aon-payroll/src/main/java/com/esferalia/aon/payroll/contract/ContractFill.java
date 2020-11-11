package com.esferalia.aon.payroll.contract;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

public class ContractFill {
	
	public static byte[] fillContract(Integer contractType, Map<String, String> contractOtherInfo) {
		if(null == contractType)
			return null;
		
		if(contractType >= 100 && contractType <= 400) 
			return fillIndefiniteContract(contractOtherInfo);
		else if (contractType == 421) 
			return fillFormationContract(contractOtherInfo);
		else if (contractType == 420 || contractType == 520) 
			return fillPracticeContract(contractOtherInfo);
		else 
			return fillTemporalContract(contractOtherInfo);
		
	}
	
	private static byte[] fillIndefiniteContract(Map<String, String> contractOtherInfo) {
		InputStream is = ContractFill.class.getResourceAsStream("indefinido.pdf");
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		try (PDDocument pdfDocument = PDDocument.load(is)){
			
			pdfDocument.setAllSecurityToBeRemoved(true);
			
			PDDocumentCatalog doc = pdfDocument.getDocumentCatalog();
			PDAcroForm acroForm = doc.getAcroForm();
			
			if(null != acroForm) {
				for(PDField field : acroForm.getFields()) {
					String valueStr = field.getValueAsString();
					
					if(!StringUtils.isBlank(valueStr) && StringUtils.containsIgnoreCase(valueStr, "$aon:")) {
						valueStr = valueStr.replace("$aon:", "");
						
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
						
				}
			}
			
			// vvv--- new 
			COSDictionary dictionary = pdfDocument.getDocumentCatalog().getCOSObject();
			dictionary.setNeedToBeUpdated(true);
			dictionary = (COSDictionary) dictionary.getDictionaryObject(COSName.ACRO_FORM);
			dictionary.setNeedToBeUpdated(true);
			COSArray array = (COSArray) dictionary.getDictionaryObject(COSName.FIELDS);
			array.setNeedToBeUpdated(true);
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

	private static byte[] fillFormationContract(Map<String, String> contractOtherInfo) {
		InputStream is = ContractFill.class.getResourceAsStream("formacion.pdf");
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		try (PDDocument pdfDocument = PDDocument.load(is)){
			
			pdfDocument.setAllSecurityToBeRemoved(true);
			
			PDDocumentCatalog doc = pdfDocument.getDocumentCatalog();
			PDAcroForm acroForm = doc.getAcroForm();
			
			if(null != acroForm) {
				for(PDField field : acroForm.getFields()) {
					String valueStr = field.getValueAsString();
					
					if(!StringUtils.isBlank(valueStr) && StringUtils.containsIgnoreCase(valueStr, "$aon:")) {
						valueStr = valueStr.replace("$aon:", "");
						
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
						
				}
			}
			
			// vvv--- new 
			COSDictionary dictionary = pdfDocument.getDocumentCatalog().getCOSObject();
			dictionary.setNeedToBeUpdated(true);
			dictionary = (COSDictionary) dictionary.getDictionaryObject(COSName.ACRO_FORM);
			dictionary.setNeedToBeUpdated(true);
			COSArray array = (COSArray) dictionary.getDictionaryObject(COSName.FIELDS);
			array.setNeedToBeUpdated(true);
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

	private static byte[] fillPracticeContract(Map<String, String> contractOtherInfo) {
		InputStream is = ContractFill.class.getResourceAsStream("practicas.pdf");
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		try (PDDocument pdfDocument = PDDocument.load(is)){
			
			pdfDocument.setAllSecurityToBeRemoved(true);
			
			PDDocumentCatalog doc = pdfDocument.getDocumentCatalog();
			PDAcroForm acroForm = doc.getAcroForm();
			
			if(null != acroForm) {
				for(PDField field : acroForm.getFields()) {
					String valueStr = field.getValueAsString();
					
					if(!StringUtils.isBlank(valueStr) && StringUtils.containsIgnoreCase(valueStr, "$aon:")) {
						valueStr = valueStr.replace("$aon:", "");
						
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
						
				}
			}
			
			// vvv--- new 
			COSDictionary dictionary = pdfDocument.getDocumentCatalog().getCOSObject();
			dictionary.setNeedToBeUpdated(true);
			dictionary = (COSDictionary) dictionary.getDictionaryObject(COSName.ACRO_FORM);
			dictionary.setNeedToBeUpdated(true);
			COSArray array = (COSArray) dictionary.getDictionaryObject(COSName.FIELDS);
			array.setNeedToBeUpdated(true);
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

	private static byte[] fillTemporalContract(Map<String, String> contractOtherInfo) {
		InputStream is = ContractFill.class.getResourceAsStream("temporal.pdf");
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		try (PDDocument pdfDocument = PDDocument.load(is)){
			
			pdfDocument.setAllSecurityToBeRemoved(true);
			
			PDDocumentCatalog doc = pdfDocument.getDocumentCatalog();
			PDAcroForm acroForm = doc.getAcroForm();
			
			if(null != acroForm) {
				for(PDField field : acroForm.getFields()) {
					String valueStr = field.getValueAsString();
					
					if(!StringUtils.isBlank(valueStr) && StringUtils.containsIgnoreCase(valueStr, "$aon:")) {
						valueStr = valueStr.replace("$aon:", "");
						
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
						
				}
			}
			
			// vvv--- new 
			COSDictionary dictionary = pdfDocument.getDocumentCatalog().getCOSObject();
			dictionary.setNeedToBeUpdated(true);
			dictionary = (COSDictionary) dictionary.getDictionaryObject(COSName.ACRO_FORM);
			dictionary.setNeedToBeUpdated(true);
			COSArray array = (COSArray) dictionary.getDictionaryObject(COSName.FIELDS);
			array.setNeedToBeUpdated(true);
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
	        System.out.println("Original value: " + field.getValueAsString());
	        field.setValue(value);
	        System.out.println("New value: " + field.getValueAsString());
	    } else {
	        System.out.println("Tipo no identificado");
	    }

	    COSDictionary fieldDictionary = field.getCOSObject();
	    COSDictionary dictionary = (COSDictionary) fieldDictionary.getDictionaryObject(COSName.AP);
	    dictionary.setNeedToBeUpdated(true);
	    COSStream stream = (COSStream) dictionary.getDictionaryObject(COSName.N);
	    stream.setNeedToBeUpdated(true);
	    while (fieldDictionary != null) {
	        fieldDictionary.setNeedToBeUpdated(true);
	        fieldDictionary = (COSDictionary) fieldDictionary.getDictionaryObject(COSName.PARENT);
	    }
	}

}
