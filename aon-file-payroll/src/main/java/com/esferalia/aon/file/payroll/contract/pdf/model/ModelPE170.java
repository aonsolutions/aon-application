package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;



public class ModelPE170 extends AbstractContractModel {
	
	/*
	 * Contract page 1
	 */
	final static String PE170_TC2_100 = "tipocontrato_100";
	final static String PE170_TC2_200 = "tipocontrato_200";
//	nomreptra
//	dnireptra
//	calireptra
//	profetraba
//	catetraba
//	ubicact
//	calletrab
//	sel_jorn1
//	horasjorna1
//	horainicio
//	horafin
//	horasjorna2
//	sel_jorn2
//	horasjorna2
//	tipojorn
//	tipojorntp1
//	tipojorntp2
//	tipojorntp3
//	tipojorntp4
//	sel_jorn21
//	sel_jorn22
//	sel_jorn23
//	horas4

	/*
	 * Contract page 2
	 */
//	horatraba1
//	horatraba2
//	sel_adj1
//	sel_adj2
//	sel_adjhhcc1
//	sel_adjhhcc2
//	fechaini
//	peridoprue
//	sel_ctorel1
//	sel_ctorel2
//	retribu
//	perioretri
//	concepsala
//	vacaciones
//	convcole
//	oecomu
//	clausadici
//	munifirma
//	diafirma
//	mesfirma
//	añofirma
	
	public final static String MODEL_NAME = "PE170";
	
	
	public ModelPE170(){
		super.modelName = MODEL_NAME;
	}
	
	
//	@Override
//	public byte[] buildPdf() {
//		try {
//			PdfReader reader = new PdfReader(getContractModelUrl(modelName+".pdf"));
//			setContractWidth((double)reader.getPageSize(1).getWidth());
//			setContractHeight((double)reader.getPageSize(1).getHeight());
//			ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
//			PdfStamper stamp = new PdfStamper(reader, baos);
//			AcroFields form = stamp.getAcroFields();
//			
//			
//			String checkValue = null;
//			for (Iterator<?> it = getPdfFields().iterator(); it.hasNext();) {
////				key = (String) it.next();
////				ContractPdfField field = getPdfFieldsMap().get(key);
//				ContractPdfField field = (ContractPdfField) it.next();
//				if(field.getType()==AcroFields.FIELD_TYPE_CHECKBOX){
//					if(checkValue==null){
//						checkValue = form.getAppearanceStates(field.getLabel())[0];
//					}
//					form.setField(field.getLabel(), field.getValue().equals("true")?checkValue:"");
//				} else {
//					form.setField(field.getLabel(), field.getValue());
//				}
//			}
////    		stamp.setFormFlattening(true);
//			stamp.setFormFlattening(false);
//			stamp.close();
//			reader.close();
//			return baos.toByteArray();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (DocumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}

	@Override
	public void loadPdfFields(ContractCode code, Contract contract) throws UnsupportedContractModelException{
		// TODO 
		
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(modelName+".pdf"));
			setContractWidth((double)reader.getPageSize(1).getWidth());
			setContractHeight((double)reader.getPageSize(1).getHeight());
//			ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
//			PdfStamper stamp = new PdfStamper(reader, baos);
//			AcroFields form = stamp.getAcroFields();
			
			
			readPdfFields(reader);

			
			if(code == ContractCode.C100){
				getPdfFieldsMap().get(PE170_TC2_100).setValue("true");
			} else if(code == ContractCode.C200){
				getPdfFieldsMap().get(PE170_TC2_200).setValue("true");
			} else {
				throw new UnsupportedContractModelException("El modelo de contrato seleccionado es incorrecto");
			}
			
			super.loadPdfCommonFields(contract);
			
			
//			String checkValue = null;
//			String key = null;
//			for (Iterator<?> it = getPdfFieldsMap().keySet().iterator(); it.hasNext();) {
//				key = (String) it.next();
//				ContractPdfField field = getPdfFieldsMap().get(key);
//				if(field.getType()==AcroFields.FIELD_TYPE_CHECKBOX){
//					if(checkValue==null){
//						checkValue = form.getAppearanceStates(field.getLabel())[0];
//					}
//					form.setField(field.getLabel(), field.getValue().equals("true")?checkValue:"");
//				} else {
//					form.setField(field.getLabel(), field.getValue());
//				}
//			}
////	    	stamp.setFormFlattening(true);
//			stamp.setFormFlattening(false);
//			stamp.close();
//			reader.close();
//	    	return baos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//		} catch (DocumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		return null;
		
		
	}
	
}
	
	