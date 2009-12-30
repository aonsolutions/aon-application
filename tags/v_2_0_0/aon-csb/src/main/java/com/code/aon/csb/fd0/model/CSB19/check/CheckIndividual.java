package com.code.aon.csb.fd0.model.CSB19.check;

import java.util.ArrayList;

import com.code.aon.csb.fd0.model.Fd0Exception;
import com.code.aon.csb.fd0.model.CSB19.data.Individual;


public class CheckIndividual extends Check{

	public static boolean parse(Individual individual,ArrayList exceptions){
		boolean status = true;
		if (individual.getReferenceCode()==null || 
				individual.getReferenceCode().trim().equals("")){
			exceptions.add( new Fd0Exception( individual.getName()+": "+getMessage("ERROR_INDIVIDUAL_1") ,individual.toString()) );
			status = false;
		}
		if (individual.getName()==null || 
				individual.getName().trim().equals("")){
			exceptions.add( new Fd0Exception( individual.getName()+": "+getMessage("ERROR_INDIVIDUAL_2") ,individual.toString()) );
			status = false;
		}
		if (individual.getAccount()==null){
			exceptions.add( new Fd0Exception( individual.getName()+": "+getMessage("ERROR_INDIVIDUAL_3") ,individual.toString()) );
			status = false;
		}else{
			try{
				if (!individual.getAccount().isCorrect()){
					exceptions.add( new Fd0Exception( individual.getName()+": "+getMessage("ERROR_INDIVIDUAL_4") ,individual.toString()) );
					status = false;
				}
			}catch (Exception e) {
				exceptions.add( new Fd0Exception( individual.getName()+": "+getMessage("ERROR_INDIVIDUAL_5") ,individual.toString()) );
				status = false;
			}
		}
		if (individual.getReturnCode()==null){
			exceptions.add( new Fd0Exception( individual.getName()+": "+getMessage("ERROR_INDIVIDUAL_6") ,individual.toString()) );
			status = false;
		}
		if (individual.getInternalCode()==null){
			exceptions.add( new Fd0Exception( individual.getName()+": "+getMessage("ERROR_INDIVIDUAL_7") ,individual.toString()) );
			status = false;
		}
		return status;
	}
	
}
