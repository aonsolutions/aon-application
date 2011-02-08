package com.code.aon.csb.fd0.model.CSB32.check;

import java.util.ArrayList;

import com.code.aon.csb.fd0.model.Fd0Exception;
import com.code.aon.csb.fd0.model.CSB32.data.Lot;


public class CheckLot extends Check{

	public static boolean parse(Lot lot,ArrayList exceptions){
		boolean status = true;
		if (lot.getFileDate()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_LOT_1") ,lot.toString()) );
			status = false;
		}
		if (lot.getFileNumber()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_LOT_2") ,lot.toString()) );
			status = false;
		}else{
			if (lot.getFileNumber().intValue()<0 ||
					lot.getFileNumber().intValue()>9999){
				exceptions.add( new Fd0Exception( getMessage("ERROR_LOT_3") ,lot.toString()) );
				status = false;
			}
		}
		if (lot.getEntity()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_LOT_4") ,lot.toString()) );
			status = false;
		}else{
			if (lot.getEntity().intValue()<0 ||
					lot.getEntity().intValue()>9999){
				exceptions.add( new Fd0Exception( getMessage("ERROR_LOT_5") ,lot.toString()) );
				status = false;
			}
		}
		if (lot.getOffice()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_LOT_6") ,lot.toString()) );
			status = false;
		}else{
			if (lot.getOffice().intValue()<0 ||
					lot.getOffice().intValue()>9999){
				exceptions.add( new Fd0Exception( getMessage("ERROR_LOT_7") ,lot.toString()) );
				status = false;
			}
		}
		return status;
	}
	
}
