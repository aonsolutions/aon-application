package com.code.aon.csb.fd0.model.CSB19.check;

import java.util.ArrayList;

import com.code.aon.csb.fd0.model.Fd0Exception;
import com.code.aon.csb.fd0.model.CSB19.data.Presenter;


public class CheckPresenter extends Check{

	public static boolean parse(Presenter presenter,ArrayList exceptions){
		boolean status = true;
		if (presenter.getCode()==null || 
				presenter.getCode().trim().equals("")){
			exceptions.add( new Fd0Exception( getMessage("ERROR_PRESENTER_1") ,presenter.toString()) );
			status = false;
		}
		if (presenter.getSufix()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_PRESENTER_2") ,presenter.toString()) );
			status = false;
		}
		if (presenter.getMakeDate()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_PRESENTER_3") ,presenter.toString()) );
			status = false;
		}
		if (presenter.getName()==null|| 
				presenter.getName().trim().equals("")){
			exceptions.add( new Fd0Exception( getMessage("ERROR_PRESENTER_4") ,presenter.toString()) );
			status = false;
		}
		if (presenter.getEntity()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_PRESENTER_5") ,presenter.toString()) );
			status = false;
		}
		if (presenter.getOffice()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_PRESENTER_6") ,presenter.toString()) );
			status = false;
		}
		return status;
	}
	
}
