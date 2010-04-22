package com.code.aon.file.tax.model.MOD190.checks;

import java.util.ArrayList;

import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.tax.model.MOD190.data.WithHolder;


/**
 * Checks the WithHolder objects data
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public class CheckWithHolder extends Check{

	/**
	 * Parses data
	 * 
	 * @param withHolder the object to parse
	 * @param exceptions errors founds
	 * @return true if no errors
	 */
	public static boolean parse(WithHolder withHolder,ArrayList<Exception> exceptions){
		boolean status = true;
		if (withHolder.getYear()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_WITHHOLDER_1") ,withHolder.toString()) );
			status = false;
		}
		if (withHolder.getCode()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_WITHHOLDER_2") ,withHolder.toString()) );
			status = false;
		}
		if (withHolder.getName()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_WITHHOLDER_3") ,withHolder.toString()) );
			status = false;
		}
		if (withHolder.getJustify()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_WITHHOLDER_4") ,withHolder.toString()) );
			status = false;
		}
		if (withHolder.getReplaces()!=null){
			if (withHolder.getReplacedJustify()==null){
				exceptions.add( new Fd0Exception( getMessage("ERROR_WITHHOLDER_5") ,withHolder.toString()) );
				status = false;
			}
		}else{
			if (withHolder.getReplacedJustify()!=null){
				exceptions.add( new Fd0Exception( getMessage("ERROR_WITHHOLDER_6") ,withHolder.toString()) );
				status = false;
			}
		}
		return status;
	}
	
}
