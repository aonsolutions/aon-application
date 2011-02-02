package com.code.aon.file.tax.model.MOD303.check;


import java.util.ArrayList;

import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.tax.model.MOD303.data.Declaration;

public class CheckDeclaration extends Check {

	public static boolean parse(Declaration declaration,ArrayList<Exception> exceptions){
		boolean status = true;
		if (declaration.getDocument()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_DECLARATION_1") ,declaration.toString()) );
			status = false;
		}
		if (declaration.getName()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_DECLARATION_2") ,declaration.toString()) );
			status = false;
		}
		return status;
	}

}
