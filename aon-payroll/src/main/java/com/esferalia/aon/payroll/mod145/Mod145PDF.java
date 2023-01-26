package com.esferalia.aon.payroll.mod145;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.Loader;
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

import com.esferalia.aon.occam.api.model.mod145.IrpfDataAscendants;
import com.esferalia.aon.occam.api.model.mod145.IrpfDataDescendients;
import com.esferalia.aon.occam.api.model.mod145.Mod145;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod145PDF {
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private static SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
//	private static SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
//	private static SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
	
	private Mod145PDF() {
		super();
	}
	
	public static byte[] fillMod145(Mod145 mod145) {
		InputStream is = Mod145PDF.class.getResourceAsStream("mod145.pdf");
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		Map<String, String> mod145Context = createMod145Context(mod145);
		
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
					String newValue = mod145Context.get(fieldName);
					setField(field, newValue);	
					
//					System.out.println(fieldName + "  -->  " + newValue + "  -->  " + field.getValueAsString());
				}
			}
			
			pdfDocument.save(out);
			pdfDocument.close();
			
			return out.toByteArray();
		
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static Map<String, String> createMod145Context(Mod145 mod145) {
		Map<String, String> context = new HashMap<>();
		
		context.put("NIF", mod145.getNif());
		context.put("NOMBRE", mod145.getFullName());
		context.put("A\u00d1O NACIMIENTO", null == mod145.getBirthDate() ? "" : yearFormat.format(mod145.getBirthDate()));
		context.put("FECHA_TRASLADO", null != mod145.getMovingDate() ? dateFormat.format(mod145.getMovingDate()) : "");
		
		Byte familiarSituation = mod145.getFamilySituation();
		context.put("SITUACI\u00d3N 01", (byte) 0 == familiarSituation ? "SI" : "NO");
		context.put("SITUACI\u00d3N 02", (byte) 1 == familiarSituation ? "SI" : "NO");
		context.put("SITUACI\u00d3N 03", (byte) 2 == familiarSituation ? "SI" : "NO");
		
		context.put("NIF_CONYUGE", mod145.getSpouseDocument());
		
		Byte disabilityLevel = mod145.getDisabilityLevel();
		if(null != disabilityLevel) {
			context.put("DISCAPACIDAD A", (byte) 0 == disabilityLevel ? "SI" : "NO");
			context.put("DISCAPACIDAD B", (byte) 2 == disabilityLevel ? "SI" : "NO");
		}
		
		context.put("AYUDA", mod145.isDependence() ? "SI" : "NO");
		context.put("PLURIANUAL", mod145.isLabourProlongation() ? "SI" : "NO");
		
		int descendientIdx = 1;
		
		for(IrpfDataDescendients descendient : mod145.getDescendients()) {
			context.put("A\u00d1O NACIMIENTO 0" + descendientIdx, null == descendient.getBirthYear() ? "" : descendient.getBirthYear().toString());
			context.put("A\u00d1O ACOGIDA 0" + descendientIdx, null == descendient.getAdoptionYear() ? "" : descendient.getAdoptionYear().toString());
			context.put("DISCAPACIDAD A 0" + descendientIdx, null != descendient.getDisabilityLevel() && (byte) 1 == descendient.getDisabilityLevel() ? "SI" : "NO");
			context.put("DISCAPACIDAD B 0" + descendientIdx, null != descendient.getDisabilityLevel() && (byte) 2 == descendient.getDisabilityLevel() ? "SI" : "NO");
			context.put("DISCAPACIDAD C 0" + descendientIdx, descendient.isDependence() ? "SI" : "NO");
			context.put("ENTERO 0" + descendientIdx, descendient.isUniqueParent() ? "SI" : "NO");
			descendientIdx++;
		}
		
		int ascendantIdx = 5;
		
		for(IrpfDataAscendants ascendant : mod145.getAscendants()) {
			context.put("A\u00d1O NACIMIENTO 0" + ascendantIdx, null == ascendant.getBirthYear() ? "" : ascendant.getBirthYear().toString());
			context.put("DISCAPACIDAD A 0" + ascendantIdx, null != ascendant.getDisabilityLevel() && (byte) 1 == ascendant.getDisabilityLevel() ? "SI" : "NO");
			context.put("DISCAPACIDAD B 0" + ascendantIdx, null != ascendant.getDisabilityLevel() && (byte) 2 == ascendant.getDisabilityLevel() ? "SI" : "NO");
			context.put("DISCAPACIDAD C 0" + ascendantIdx, ascendant.isDependence() ? "SI" : "NO");
			context.put("CONVIVIENTE 0" + (ascendantIdx - 4), ascendant.isAnotherDescendient() ? "SI" : "NO");
			ascendantIdx++;
		}
		
		context.put("PENSI\u00d3N COMPENSATORIA", null == mod145.getSpousalSupport() ? "" : mod145.getSpousalSupport().toString());
		context.put("ANUALIDAD ALIMENTOS", null == mod145.getFoodAnnuity() ? "" : mod145.getFoodAnnuity().toString());
		context.put("VIVIENDA", mod145.isDeductionHomeLoan() ? "SI" : "NO");
		
		context.put("FIRMA2_ENTIDAD", mod145.getEnterpriseName());
		
		return context;
	}
	
//	private static Map<String, String> createMod145Context(Mod145 mod145) {
//		Map<String, String> context = new HashMap<>();
//		
//		context.put("dato.nif.0", mod145.getNif());
//		context.put("dato.0", mod145.getFullName());
//		context.put("dato.1", yearFormat.format(mod145.getBirthDate()));
//		context.put("dato.2", null == mod145.getMovingDate() ? "" : dateFormat.format(mod145.getMovingDate()));
//		
//		Byte familiarSituation = mod145.getFamilySituation();
//		context.put("dato.3", (byte) 0 == familiarSituation ? "SI" : "NO");
//		context.put("dato.4", (byte) 1 == familiarSituation ? "SI" : "NO");
//		context.put("dato.5", (byte) 2 == familiarSituation ? "SI" : "NO");
//		
//		context.put("dato.nif.1", mod145.getSpouseDocument());
//		
//		Byte disabilityLevel = mod145.getDisabilityLevel();
//		context.put("dato.6", (byte) 1 == disabilityLevel ? "SI" : "NO");
//		context.put("dato.7", (byte) 2 == disabilityLevel ? "SI" : "NO");
//		
//		context.put("dato.8", mod145.isDependence() ? "SI" : "NO");
//		context.put("dato.9", mod145.isLabourProlongation() ? "SI" : "NO");
//		
//		int descendientIdx = 10;
//		
//		for(IrpfDataDescendients descendient : mod145.getDescendients()) {
//			context.put("dato." + descendientIdx, null == descendient.getBirthYear() ? "" : descendient.getBirthYear().toString());
//			descendientIdx++;
//			context.put("dato." + descendientIdx, null == descendient.getAdoptionYear() ? "" : descendient.getAdoptionYear().toString());
//			descendientIdx++;
//			context.put("dato." + descendientIdx, null != descendient.getDisabilityLevel() && (byte) 1 == descendient.getDisabilityLevel() ? "SI" : "NO");
//			descendientIdx++;
//			context.put("dato." + descendientIdx, null != descendient.getDisabilityLevel() && (byte) 2 == descendient.getDisabilityLevel() ? "SI" : "NO");
//			descendientIdx++;
//			context.put("dato." + descendientIdx, descendient.isDependence() ? "SI" : "NO");
//			descendientIdx++;
//			context.put("dato." + descendientIdx, descendient.isUniqueParent() ? "SI" : "NO");
//			descendientIdx++;
//		}
//		
//		int ascendantIdx = 34;
//		
//		for(IrpfDataAscendants ascendant : mod145.getAscendants()) {
//			context.put("dato." + ascendantIdx, null == ascendant.getBirthYear() ? "" : ascendant.getBirthYear().toString());
//			ascendantIdx++;
//			context.put("dato." + ascendantIdx, null != ascendant.getDisabilityLevel() && (byte) 1 == ascendant.getDisabilityLevel() ? "SI" : "NO");
//			ascendantIdx++;
//			context.put("dato." + ascendantIdx, null != ascendant.getDisabilityLevel() && (byte) 2 == ascendant.getDisabilityLevel() ? "SI" : "NO");
//			ascendantIdx++;
//			context.put("dato." + ascendantIdx, ascendant.isDependence() ? "SI" : "NO");
//			ascendantIdx++;
//			context.put("dato." + ascendantIdx, ascendant.isAnotherDescendient() ? "SI" : "NO");
//			ascendantIdx++;
//		}
//		
//		context.put("dato.44", null == mod145.getSpousalSupport() ? "" : mod145.getSpousalSupport().toString());
//		context.put("dato.45", null == mod145.getFoodAnnuity() ? "" : mod145.getFoodAnnuity().toString());
//		context.put("dato.51", mod145.isDeductionHomeLoan() ? "SI" : "NO");
//		
//		context.put("dato.52", mod145.getEnterpriseName());
//		
//		Date date = new Date();
//		context.put("dato.47", dayFormat.format(date));
//		context.put("dato.48", monthFormat.format(date));
//		context.put("dato.49", yearFormat.format(date));
//		
//		context.put("dato.54", dayFormat.format(date));
//		context.put("dato.55", monthFormat.format(date));
//		context.put("dato.56", yearFormat.format(date));
//		
//		return context;
//	}
	
	private static void defaultCheckBox(PDField field) throws IOException {
	    if (field instanceof PDCheckBox) {
	    	try {
		        ((PDCheckBox) field).unCheck();
	    	} catch (Exception e) {
				System.out.println("Error default PDFCheckBox -> " + field.getPartialName());
			}
	    }
	}

	private static void setField(PDField field, String value) throws IOException {
		field.setReadOnly(true);

	    if (field instanceof PDTextField) {
	    	try{
		        field.setValue(value);
		        ((PDTextField) field).setDefaultValue(value);
		        ((PDTextField) field).setValue(value);
		        ((PDTextField) field).setDefaultAppearance("/Helv 8 Tf 0 g");
		        ((PDTextField) field).setDefaultStyleString("/Helv 8 Tf 0 g");
				field.getWidgets().get(0).setHidden(false);
	    	} catch (Exception e) {
//				System.out.println("ERR : " + field.getValueAsString());
			}
	    } else if (field instanceof PDCheckBox) {
	    	if(AonStringUtils.equalsIgnoreCase(value, "SI")) {
	    		((PDCheckBox) field).check();
	    		((PDCheckBox) field).setValue("S\u00ed");
	    	}
	    	else { 
	    		((PDCheckBox) field).unCheck();
	    		((PDCheckBox) field).setValue("Off");
	    	}
	    } else {
	        System.out.println("Tipo no identificado");
	    }
	}

}
