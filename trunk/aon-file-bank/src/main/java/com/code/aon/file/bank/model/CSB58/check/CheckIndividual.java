package com.code.aon.file.bank.model.CSB58.check;

import java.util.ArrayList;

import com.code.aon.file.bank.model.CSB58.data.Individual;
import com.code.aon.file.format.model.Fd0Exception;


public class CheckIndividual extends Check{

	public static boolean parse(Individual individual,ArrayList exceptions){
		boolean status = true;
		if (individual.getReferenceCode()==null || 
				individual.getReferenceCode().trim().equals("")){
			exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_1") ,individual.toString()) );
			status = false;
		}
		if (individual.getName()==null || 
				individual.getName().trim().equals("")){
			exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_2") ,individual.toString()) );
			status = false;
		}
		if (individual.getAccount()!=null){
			try{
				if (!individual.getAccount().isCorrect()){
					exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_4") ,individual.toString()) );
					status = false;
				}
			}catch (Exception e) {
				exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_5") ,individual.toString()) );
				status = false;
			}
		}
		if (individual.getReturnCode()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_6") ,individual.toString()) );
			status = false;
		}
		if (individual.getInternalCode()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_7") ,individual.toString()) );
			status = false;
		}
		if (individual.getExpiryDate()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_INDIVIDUAL_8") ,individual.toString()) );
			status = false;
		}
		return status;
	}
	
}
