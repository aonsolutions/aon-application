package com.code.aon.csb.fd0.model.CSB58.check;

import java.util.ArrayList;

import com.code.aon.csb.fd0.model.Fd0Exception;
import com.code.aon.csb.fd0.model.CSB58.data.Orderer;


public class CheckOrderer extends Check{

	public static boolean parse(Orderer orderer,ArrayList exceptions){
		boolean status = true;
		if (orderer.getCode()==null || 
				orderer.getCode().trim().equals("")){
			exceptions.add( new Fd0Exception( getMessage("ERROR_ORDERER_1") ,orderer.toString()) );
			status = false;
		}
		if (orderer.getSufix()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_ORDERER_2") ,orderer.toString()) );
			status = false;
		}
		if (orderer.getName()==null || 
				orderer.getName().trim().equals("")){
			exceptions.add( new Fd0Exception( getMessage("ERROR_ORDERER_3") ,orderer.toString()) );
			status = false;
		}
		if (orderer.getAccount()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_ORDERER_4") ,orderer.toString()) );
			status = false;
		}else{
			try{
				if (!orderer.getAccount().isCorrect()){
					exceptions.add( new Fd0Exception( getMessage("ERROR_ORDERER_5") ,orderer.toString()) );
					status = false;
				}
			}catch (Exception e) {
				exceptions.add( new Fd0Exception( getMessage("ERROR_ORDERER_6") ,orderer.toString()) );
				status = false;
			}
		}
		return status;
	}
	
}
