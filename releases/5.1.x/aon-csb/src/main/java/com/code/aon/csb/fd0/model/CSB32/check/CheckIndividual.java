package com.code.aon.csb.fd0.model.CSB32.check;

import java.util.ArrayList;

import com.code.aon.csb.fd0.model.Fd0Exception;
import com.code.aon.csb.fd0.model.CSB32.data.Individual;


public class CheckIndividual extends Check{

	public static boolean parse(Individual individual,ArrayList exceptions){
		boolean status = true;
		if (individual.getProvinceNumber()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_1") ,individual.toString()) );
			status = false;
		}else{
			if (individual.getProvinceNumber().intValue()<0 ||
					individual.getProvinceNumber().intValue()>99){
				exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_2") ,individual.toString()) );
				status = false;
			}
		}
		if (individual.getPaymentPost()==null){
			if (individual.getIneCode()==null){
				exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_3") ,individual.toString()) );
				status = false;
			}
		}
		if (individual.getAmount()!=null){
			if (individual.getAmount().intValue()<0 ||
				individual.getAmount().intValue()>999999999){
				exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_4") ,individual.toString()) );
				status = false;
			}
		}
		if (individual.getExpiryDate()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_5") ,individual.toString()) );
			status = false;
		}
		if (individual.getDocumentType()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_6") ,individual.toString()) );
			status = false;
		}else{
			if (individual.getDocumentType().intValue()!=1 &&
					individual.getDocumentType().intValue()!=2 &&
					individual.getDocumentType().intValue()!=3 ){
				exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_7") ,individual.toString()) );
				status = false;
			}
			if (individual.getDocumentType().intValue()==1 ||
					individual.getDocumentType().intValue()==3 ){
				if (individual.getPaymentDate()==null){
					exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_8") ,individual.toString()) );
					status = false;
				}
			}
		}
		if (individual.getAceptedCode()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_9") ,individual.toString()) );
			status = false;
		}else{
			if (individual.getAceptedCode().intValue()!=1 &&
					individual.getAceptedCode().intValue()!=2){
				exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_10") ,individual.toString()) );
				status = false;
			}
		}
		if (individual.getExpenseClause()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_11") ,individual.toString()) );
			status = false;
		}else{
			if (individual.getExpenseClause().intValue()!=0 &&
					individual.getExpenseClause().intValue()!=1 &&
					individual.getExpenseClause().intValue()!=9){
				exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_12") ,individual.toString()) );
				status = false;
			}
		}
		if (individual.getAccount()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_13") ,individual.toString()) );
			status = false;
		}else{
			try{
				if (!individual.getAccount().isCorrect()){
					exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_14") ,individual.toString()) );
					status = false;
				}
			}catch (Exception e) {
				exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_14") ,individual.toString()) );
				status = false;
			}
		}
		if (individual.getPayedPostPostalCode()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_15") ,individual.toString()) );
			status = false;
		}else{
			if (individual.getPayedPostPostalCode().intValue()<0 ||
					individual.getPayedPostPostalCode().intValue()>99999){
				exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_16") ,individual.toString()) );
				status = false;
			}
		}
		if (individual.getPayedPostProvince()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_17") ,individual.toString()) );
			status = false;
		}else{
			if (individual.getPayedPostProvince().intValue()<0 ||
					individual.getPayedPostProvince().intValue()>99){
				exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_18") ,individual.toString()) );
				status = false;
			}
		}
		return status;
	}
	
}
