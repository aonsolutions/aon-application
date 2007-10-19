package com.code.aon.csb.fd0.model.BE.checks;

import java.util.ArrayList;

import com.code.aon.csb.fd0.model.Fd0Exception;
import com.code.aon.csb.fd0.model.BE.data.Lot;


/**
 * Checks Detail object values
 * 
 * @author Consulting & Development. Iñigo GAyarre - 20/02/2007
 * @since 1.0
 *
 */
public class CheckLot extends Check{

	/**
	 * Parses data
	 * 
	 * @param transfer the object to parse
	 * @param exceptions errors founds
	 * @return true if no errors
	 */
	public static boolean parse(Lot lot,ArrayList<Exception> exceptions){
		boolean status = true;
		if (lot.getPresenterCode()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_LOT_1") ,lot.toString()) );
			status = false;
		}
		if (lot.getAplicationCode()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_LOT_2") ,lot.toString()) );
			status = false;
		}else{
			int value = lot.getAplicationCode().intValue();
			if (value != 51 && 
					value != 52){
				exceptions.add( new Fd0Exception( getMessage("ERROR_LOT_3") ,lot.toString()) );
				status = false;
			}
		}
		if (lot.getPresenterName()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_LOT_4") ,lot.toString()) );
			status = false;
		}
		if (lot.getResponsibleName()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_LOT_5") ,lot.toString()) );
			status = false;
		}
		if (lot.getResponsiblePhone()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_LOT_6") ,lot.toString()) );
			status = false;
		}
		if (lot.getGenerationDate()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_LOT_7") ,lot.toString()) );
			status = false;
		}
		if (lot.getNumber()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_LOT_8") ,lot.toString()) );
			status = false;
		}
		return status;
	}
	
}
