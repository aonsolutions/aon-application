package com.code.aon.csb.fd0.model.MOD349.check;

import java.util.ArrayList;

import com.code.aon.csb.fd0.model.Fd0Exception;
import com.code.aon.csb.fd0.model.MOD349.data.Correction;

/**
 * Checks the Correction object data
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public class CheckCorrection extends Check {

	/**
	 * Parses data
	 * 
	 * @param correction the object to parse
	 * @param exceptions errors founds
	 * @return true if no errors
	 */
	public static boolean parse(Correction correction,ArrayList<Exception> exceptions){
		boolean status = true;
		if (correction.getCountry()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_CORRECTION_1") ,correction.toString()) );
			status = false;
		}
		if (correction.getCode()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_CORRECTION_2") ,correction.toString()) );
			status = false;
		}
		if (correction.getName()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_CORRECTION_3") ,correction.toString()) );
			status = false;
		}
		if (correction.getKey()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_CORRECTION_4") ,correction.toString()) );
			status = false;
		}else{
			if (!correction.getKey().equals("A") &&
					!correction.getKey().equals("E") &&
					!correction.getKey().equals("T")){
				exceptions.add( new Fd0Exception( getMessage("ERROR_CORRECTION_5") ,correction.toString()) );
				status = false;
			}			
		}
		if (correction.getYear()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_CORRECTION_6") ,correction.toString()) );
			status = false;
		}
		if (correction.getPeriod()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_DEPONENT_7") ,correction.toString()) );
			status = false;
		}else{
			if (!correction.getPeriod().equals("1T") &&
					!correction.getPeriod().equals("2T") &&
					!correction.getPeriod().equals("3T") &&
					!correction.getPeriod().equals("4T") &&
					!correction.getPeriod().equals("0A")){
				exceptions.add( new Fd0Exception( getMessage("ERROR_DEPONENT_8") ,correction.toString()) );
				status = false;
			}			
		}
		return status;
	}

}
