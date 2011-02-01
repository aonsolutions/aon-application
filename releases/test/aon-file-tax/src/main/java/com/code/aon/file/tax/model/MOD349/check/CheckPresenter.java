package com.code.aon.file.tax.model.MOD349.check;

import java.util.ArrayList;

import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.tax.model.MOD349.data.Presenter;

/**
 * Checks the Receiver objects data
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public class CheckPresenter extends Check {

	/**
	 * Parses data
	 * 
	 * @param presenter the object to parse
	 * @param exceptions errors founds
	 * @return true if no errors
	 */
	public static boolean parse(Presenter presenter,ArrayList<Exception> exceptions){
		boolean status = true;
		if (presenter.getCode()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_PRESENTER_1") ,presenter.toString()) );
			status = false;
		}
		if (presenter.getName()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_PRESENTER_2") ,presenter.toString()) );
			status = false;
		}
		return status;
	}

}
