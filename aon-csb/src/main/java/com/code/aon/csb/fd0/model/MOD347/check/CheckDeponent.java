package com.code.aon.csb.fd0.model.MOD347.check;

import java.util.ArrayList;

import com.code.aon.csb.fd0.model.Fd0Exception;
import com.code.aon.csb.fd0.model.MOD347.data.Deponent;

/**
 * Check the Deponent object data
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public class CheckDeponent extends Check {

	/**
	 * Parses data
	 * 
	 * @param deponent the object to parse
	 * @param exceptions errors founds
	 * @return true if no errors
	 */
	public static boolean parse(Deponent deponent,ArrayList<Exception> exceptions){
		boolean status = true;
		if (deponent.getYear()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_DEPONENT_1") ,deponent.toString()) );
			status = false;
		}
		if (deponent.getCode()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_DEPONENT_2") ,deponent.toString()) );
			status = false;
		}
		if (deponent.getName()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_DEPONENT_3") ,deponent.toString()) );
			status = false;
		}
		if (deponent.getType()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_DEPONENT_4") ,deponent.toString()) );
			status = false;
		}else{
			if (!deponent.getType().equals("C") &&
					!deponent.getType().equals("T")){
				exceptions.add( new Fd0Exception( getMessage("ERROR_DEPONENT_5") ,deponent.toString()) );
				status = false;
			}			
		}
		if (deponent.getJustify()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_DEPONENT_6") ,deponent.toString()) );
			status = false;
		}
		if (deponent.getReplaces()!=null){
			if (deponent.getReplacedJustify()==null){
				exceptions.add( new Fd0Exception( getMessage("ERROR_DEPONENT_7") ,deponent.toString()) );
				status = false;
			}
		}else{
			if (deponent.getReplacedJustify()!=null){
				exceptions.add( new Fd0Exception( getMessage("ERROR_DEPONENT_8") ,deponent.toString()) );
				status = false;
			}
		}
		return status;
	}

}
