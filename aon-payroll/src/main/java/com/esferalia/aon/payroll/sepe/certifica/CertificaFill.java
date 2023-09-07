package com.esferalia.aon.payroll.sepe.certifica;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

public class CertificaFill {
	
	private CertificaFill() {
		super();
	}
	
	public static byte[] exportCertEnterprisePDF(Map<String, String> fieldsMap) {
		InputStream is = CertificaFill.class.getResourceAsStream("certificados.pdf");
		return fillCertEnterprisePDF(is, fieldsMap);
	}
	
	public static byte[] exportCertEnterpriseAgrarianPDF(Map<String, String> fieldsMap) {
		InputStream is = CertificaFill.class.getResourceAsStream("certificadosAgrarios.pdf");
		return fillCertEnterprisePDF(is, fieldsMap);
	}
	
	private static byte[] fillCertEnterprisePDF(InputStream is, Map<String, String> fieldsMap) {
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
//					String valueStr = field.getValueAsString();
					String fieldName = field.getPartialName();
//					System.out.println(valueStr + "  --  " + fieldName);
					
					String value = fieldsMap.get(fieldName);
					setField(field, value);						
				}
			}
			
			pdfDocument.setAllSecurityToBeRemoved(true);
	        COSDictionary dictionary = pdfDocument.getDocumentCatalog().getCOSObject();
	        dictionary.removeItem(COSName.PERMS);
	        
	        pdfDocument.save(out);
			
			return out.toByteArray();
		
		} catch (IOException e) {
			e.printStackTrace();
			return new byte[0];
		}
	}
	
	private static void defaultCheckBox(PDField field) throws IOException {
	    if (field instanceof PDCheckBox) {
	        field.setValue("No");
	        ((PDCheckBox) field).setDefaultValue("No");
	        ((PDCheckBox) field).unCheck();
	    }
	}

	private static void setField(PDField field, String value) throws IOException {
		if (field instanceof PDTextField) {
	    	try{
	    		String fieldIdxStr = field.getPartialName();
		    	field.getCOSObject().removeItem(COSName.AP);
		    	((PDTextField) field).setDefaultAppearance(getOwnAppearance(Integer.parseInt(fieldIdxStr)));
		    	field.setValue(value);
		        ((PDTextField) field).setDefaultValue(value);
	    	} catch (Exception e) {
//				System.out.println("ERR : " + field.getValueAsString());
			}
	    } else {
	        System.out.println("Tipo no identificado");
	    }
	}

	private static String getOwnAppearance(int idx) {
		if(idx <= 72) return "/MinionPro-Regular 8 Tf 0 g";
		if(idx <= 1020) return "/MinionPro-Regular 6 Tf 0 g";
		if(idx <= 1025) return "/MinionPro-Regular 5 Tf 0 g";
		return "/MinionPro-Regular 8 Tf 0 g";
	}

}
