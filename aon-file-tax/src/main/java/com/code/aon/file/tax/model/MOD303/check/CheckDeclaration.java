package com.code.aon.file.tax.model.MOD303.check;


import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.tax.model.MOD303.data.Declaration;

public class CheckDeclaration extends Check {
	
	private static final String ERR_1 = "ERROR_DECLARATION_1";
	private static final String ERR_2 = "ERROR_DECLARATION_2";
		

	
	public static boolean parse(Declaration declaration,ArrayList<Exception> exceptions){
		boolean status = true;
		if (StringUtils.isBlank(declaration.getDocument())){
			exceptions.add( new Fd0Exception( getMessage(ERR_1) ,declaration.toString()) );
			status = false;
		}
		if (StringUtils.isBlank(declaration.getName())){
			exceptions.add( new Fd0Exception( getMessage(ERR_2) ,declaration.toString()) );
			status = false;
		}
		return status;
	}

}
