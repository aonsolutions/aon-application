package com.code.aon.file.bank.model.CSB34.checks;

import java.util.ArrayList;

import com.code.aon.file.bank.model.CSB34.data.Master;
import com.code.aon.file.format.model.Fd0Exception;


/**
 * Checks Master object values
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public class CheckMaster extends Check{

	/**
	 * Parses data
	 * 
	 * @param master the object to parse
	 * @param exceptions errors founds
	 * @return true if no errors
	 */
	public static boolean parse(Master master,ArrayList<Exception> exceptions){
		boolean status = true;
		if (master.getOrderer()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_MASTER_1") ,master.toString()) );
			status = false;
		}else{
			if (master.getOrderer().getCode()==null || 
					master.getOrderer().getCode().trim().equals("")){
				exceptions.add( new Fd0Exception( getMessage("ERROR_MASTER_2") ,master.toString()) );
				status = false;
			}
			if (master.getOrderer().getName()==null || 
					master.getOrderer().getName().trim().equals("")){
				exceptions.add( new Fd0Exception( getMessage("ERROR_MASTER_3") ,master.toString()) );
				status = false;
			}
			if (master.getOrderer().getAddress()==null || 
					master.getOrderer().getAddress().trim().equals("")){
				exceptions.add( new Fd0Exception( getMessage("ERROR_MASTER_4") ,master.toString()) );
				status = false;
			}
			if (master.getOrderer().getCounty()==null || 
					master.getOrderer().getCounty().trim().equals("")){
				exceptions.add( new Fd0Exception( getMessage("ERROR_MASTER_5") ,master.toString()) );
				status = false;
			}
		}
		if (master.getAccount()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_MASTER_6") ,master.toString()) );
			status = false;
		}else{
			try{
				if (!master.getAccount().isCorrect()){
					exceptions.add( new Fd0Exception( getMessage("ERROR_MASTER_7") ,master.toString()) );
					status = false;
				}
			}catch (Exception e) {
				exceptions.add( new Fd0Exception( getMessage("ERROR_MASTER_8") ,master.toString()) );
				status = false;
			}
		}
		if (master.getSendDate()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_MASTER_9") ,master.toString()) );
			status = false;
		}
		if (master.getOrderDate()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_MASTER_10") ,master.toString()) );
			status = false;
		}
		if (master.getDetail()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_MASTER_11") ,master.toString()) );
			status = false;
		}else{
			if (!"0".equals(master.getDetail()) &&
				!"1".equals(master.getDetail())){
				exceptions.add( new Fd0Exception( getMessage("ERROR_MASTER_12") ,master.toString()) );
				status = false;
			}
		}
		if (master.getCosts()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_DETAIL_9") ,master.toString()) );
			status = false;
		}else{
			if (!"1".equals(master.getCosts()) &&
				!"2".equals(master.getCosts())){
				exceptions.add( new Fd0Exception( getMessage("ERROR_MASTER_13") ,master.toString()) );
				status = false;
			}
		}
		return status;
	}
	
}
