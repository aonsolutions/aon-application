package com.code.aon.file.tax.model.MOD349.check;

import java.util.ArrayList;

import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.tax.model.MOD349.data.Rectification;

public class CheckRectification extends Check {

	public static boolean parse(Rectification rectification,ArrayList<Exception> exceptions){
		boolean status = true;
		if (rectification.getCountry()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_CORRECTION_1") ,rectification.toString()) );
			status = false;
		}
		if (rectification.getKey()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_CORRECTION_2") ,rectification.toString()) );
			status = false;
		}
		if (rectification.getName()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_CORRECTION_3") ,rectification.toString()) );
			status = false;
		}
		if (rectification.getKey()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_CORRECTION_4") ,rectification.toString()) );
			status = false;
		}else{
			if (!rectification.getKey().equals("A") &&
					!rectification.getKey().equals("E") &&
					!rectification.getKey().equals("T")){
				exceptions.add( new Fd0Exception( getMessage("ERROR_CORRECTION_5") ,rectification.toString()) );
				status = false;
			}			
		}
		if (rectification.getRectifiedYear()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_CORRECTION_6") ,rectification.toString()) );
			status = false;
		}
		if (rectification.getRectifiedPeriod()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_DEPONENT_7") ,rectification.toString()) );
			status = false;
		}else{
			if (!rectification.getRectifiedPeriod().equals("1T") &&
				!rectification.getRectifiedPeriod().equals("2T") &&
				!rectification.getRectifiedPeriod().equals("3T") &&
				!rectification.getRectifiedPeriod().equals("4T") &&
				!rectification.getRectifiedPeriod().equals("01") &&
				!rectification.getRectifiedPeriod().equals("02") &&
				!rectification.getRectifiedPeriod().equals("03") &&
				!rectification.getRectifiedPeriod().equals("04") &&
				!rectification.getRectifiedPeriod().equals("05") &&
				!rectification.getRectifiedPeriod().equals("06") &&
				!rectification.getRectifiedPeriod().equals("07") &&
				!rectification.getRectifiedPeriod().equals("08") &&
				!rectification.getRectifiedPeriod().equals("09") &&
				!rectification.getRectifiedPeriod().equals("10") &&
				!rectification.getRectifiedPeriod().equals("11") &&
				!rectification.getRectifiedPeriod().equals("12") &&
				!rectification.getRectifiedPeriod().equals("0A")){
				exceptions.add( new Fd0Exception( getMessage("ERROR_DEPONENT_8") ,rectification.toString()) );
				status = false;
			}			
		}
		return status;
	}

}
