package com.code.aon.csb.fd0.model.CSB34.checks;

import java.util.ArrayList;

import com.code.aon.csb.fd0.model.Fd0Exception;
import com.code.aon.csb.fd0.model.CSB34.data.Detail;


/**
 * Checks Detail object values
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public class CheckDetail extends Check{

	/**
	 * Parses data
	 * 
	 * @param detail the object to parse
	 * @param exceptions errors founds
	 * @return true if no errors
	 */
	public static boolean parse(Detail detail,ArrayList<Exception> exceptions){
		boolean status = true;
		if (detail.getReceiver()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_DETAIL_1") ,detail.toString()) );
			status = false;
		}else{
			if (detail.getReceiver().getCode()==null || 
					detail.getReceiver().getCode().trim().equals("")){
				exceptions.add( new Fd0Exception( getMessage("ERROR_DETAIL_2") ,detail.toString()) );
				status = false;
			}
			if (detail.getReceiver().getName()==null || 
					detail.getReceiver().getName().trim().equals("")){
				exceptions.add( new Fd0Exception( getMessage("ERROR_DETAIL_3") ,detail.toString()) );
				status = false;
			}
			if (detail.getReceiver().getAddress()==null || 
					detail.getReceiver().getAddress().trim().equals("")){
				exceptions.add( new Fd0Exception( getMessage("ERROR_DETAIL_4") ,detail.toString()) );
				status = false;
			}
			if (detail.getReceiver().getCounty()==null || 
					detail.getReceiver().getCounty().trim().equals("")){
				exceptions.add( new Fd0Exception( getMessage("ERROR_DETAIL_5") ,detail.toString()) );
				status = false;
			}
		}
		if (detail.getAccount()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_DETAIL_6") ,detail.toString()) );
			status = false;
		}else{
			try{
				if (!detail.getAccount().isCorrect()){
					exceptions.add( new Fd0Exception( getMessage("ERROR_DETAIL_7") ,detail.toString()) );
					status = false;
				}
			}catch (Exception e) {
				exceptions.add( new Fd0Exception( getMessage("ERROR_DETAIL_8") ,detail.toString()) );
				status = false;
			}
		}
		if (detail.getConcept()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_DETAIL_11") ,detail.toString()) );
			status = false;
		}else{
			if (!"1".equals(detail.getConcept()) &&
				!"8".equals(detail.getConcept()) &&
				!"9".equals(detail.getConcept())){
				exceptions.add( new Fd0Exception( getMessage("ERROR_DETAIL_12") ,detail.toString()) );
				status = false;
			}
		}
		return status;
	}
	
}
