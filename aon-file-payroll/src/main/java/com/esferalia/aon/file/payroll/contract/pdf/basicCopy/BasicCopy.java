package com.esferalia.aon.file.payroll.contract.pdf.basicCopy;

import java.io.IOException;
import java.util.Date;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.ContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.contrata.enumeration.TEQPTIEM;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public class BasicCopy extends AbstractContractBasicCopy {
	
	public final static String BASIC_COPY_NAME = "ContractBasicCopy";
	
	public BasicCopy(){
		super.documentName = BASIC_COPY_NAME;
	}
	
	@Override
	public void loadPdfFields(ContractCode code, Contract contract, ContrataParams contrataParams) throws UnsupportedContractDocumentException{
		// TODO
		
		try {
			PdfReader reader = new PdfReader(getContractBasicCopyUrl(documentName+".pdf"));
			
			readPdfFields(reader);
			
			super.loadPdfCommonFields(contract);
			
			if(contrataParams!=null){
				Integer horasJornada = Integer.parseInt(contrataParams.getHorasJornada());
				if(horasJornada!=null){
					getPdfFieldsMap().get(CONTRACT_JOURNAL_HOURS_1).setValue(String.valueOf(horasJornada));;				
//					getPdfFieldsMap().get(CONTRACT_JOURNAL_HOURS_2).setValue(String.valueOf(minutosJornada));;
					
					if(contrataParams.getTipoJornada()==TEQPTIEM.TEQPTIEM_A){
						getPdfFieldsMap().get(CONTRACT_JOURNAL).setValue("HORAS ANUALES");;
					} else if (contrataParams.getTipoJornada()==TEQPTIEM.TEQPTIEM_D){
						getPdfFieldsMap().get(CONTRACT_JOURNAL).setValue("HORAS DIARIAS");;
					} else if (contrataParams.getTipoJornada()==TEQPTIEM.TEQPTIEM_M){
						getPdfFieldsMap().get(CONTRACT_JOURNAL).setValue("HORAS MENSUALES");;
					} else if (contrataParams.getTipoJornada()==TEQPTIEM.TEQPTIEM_S){
						getPdfFieldsMap().get(CONTRACT_JOURNAL).setValue("HORAS SEMANALES");;
					}
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
	
	