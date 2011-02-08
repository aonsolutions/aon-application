package com.code.aon.csb.fd0.model.BE.checks;

import java.util.ArrayList;

import com.code.aon.csb.fd0.model.Fd0Exception;
import com.code.aon.csb.fd0.model.BE.data.Transfer;


/**
 * Checks Detail object values
 * 
 * @author Consulting & Development. Iñigo GAyarre - 20/02/2007
 * @since 1.0
 *
 */
public class CheckTransfer extends Check{

	/**
	 * Parses data
	 * 
	 * @param transfer the object to parse
	 * @param exceptions errors founds
	 * @return true if no errors
	 */
	public static boolean parse(Transfer transfer,ArrayList<Exception> exceptions){
		boolean status = true;
		if (transfer.getNumberCode()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_TRANSFER_1") ,transfer.toString()) );
			status = false;
		}		
		if (transfer.getCcc()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_TRANSFER_2") ,transfer.toString()) );
			status = false;
		}else{
			try{
				if (!transfer.getCcc().isCorrect()){
					exceptions.add( new Fd0Exception( getMessage("ERROR_TRANSFER_3") ,transfer.toString()) );
					status = false;
				}
			}catch (Exception e) {
				exceptions.add( new Fd0Exception( getMessage("ERROR_TRANSFER_3") ,transfer.toString()) );
				status = false;
			}
		}
		if (transfer.getRecipientNamePart1()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_TRANSFER_4") ,transfer.toString()) );
			status = false;
		}		
		if (transfer.getType()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_TRANSFER_5") ,transfer.toString()) );
			status = false;
		}else{
			if (!"1".equals(transfer.getType()) &&
					!"2".equals(transfer.getType()) &&
					!"3".equals(transfer.getType()) &&
					!"4".equals(transfer.getType()) &&
					!"5".equals(transfer.getType()) &&
					!"6".equals(transfer.getType()) &&
					!"7".equals(transfer.getType()) &&
					!"8".equals(transfer.getType()) &&
					!"9".equals(transfer.getType())){
					exceptions.add( new Fd0Exception( getMessage("ERROR_TRANSFER_6") ,transfer.toString()) );
					status = false;
			}
		}
		if (transfer.getAuthKey()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_TRANSFER_7") ,transfer.toString()) );
			status = false;
		}		
		return status;
	}
	
}
