package com.code.aon.file.tax.model.MOD347.check;

import java.util.ArrayList;

import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.tax.model.MOD347.data.Declared;

/**
 * Checks Declared object data
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public class CheckDeclared extends Check {

	/**
	 * Parses data
	 * 
	 * @param declared the object to parse
	 * @param exceptions errors founds
	 * @return true if no errors
	 */
	public static boolean parse(Declared declared,ArrayList<Exception> exceptions){
		boolean status = true;
		if (declared.getCode()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_DECLERED_1") ,declared.toString()) );
			status = false;
		}
		if (declared.getName()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_DECLERED_2") ,declared.toString()) );
			status = false;
		}
		if (declared.getProvince()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_DECLERED_3") ,declared.toString()) );
			status = false;
		}
		if (declared.getCountry()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_DECLERED_4") ,declared.toString()) );
			status = false;
		}
		if (declared.getKey()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_DECLERED_5") ,declared.toString()) );
			status = false;
		}else{
			if (!declared.getKey().equals("A") &&
				!declared.getKey().equals("B") &&
				!declared.getKey().equals("C") &&
				!declared.getKey().equals("D") &&
				!declared.getKey().equals("E") &&
				!declared.getKey().equals("F") &&
				!declared.getKey().equals("G") 
				){
				exceptions.add( new Fd0Exception( getMessage("ERROR_DECLERED_6") ,declared.toString()) );
				status = false;
			}
		}
		checkImport(declared, exceptions);
		return status;
	}

	private static boolean checkImport(Declared declared,ArrayList<Exception> exceptions){
		String clave = declared.getKey();
		double importe = declared.getQuantity()==null?0d:declared.getQuantity().doubleValue();
		double IMPORTE_A = 3005.06;
		double IMPORTE_B = 3005.06;
		double IMPORTE_C = 300.51;
		double IMPORTE_D = 3005.06;
		double IMPORTE_E = 3005.06;
		double limite = 0;
		boolean status = true;

		if ( clave != null && clave.equals("A")) {
			limite = IMPORTE_A;
		}
		if ( clave != null && clave.equals("B")) {
			limite = IMPORTE_B;
		}
		if ( clave != null && clave.equals("C")) {
			limite = IMPORTE_C;
		}
		if ( clave != null && clave.equals("D")) {
			limite = IMPORTE_D;
		}
		if ( clave != null && clave.equals("E")) {
			limite = IMPORTE_E;
		}
		status = !(importe <= limite);

		if ( !status ) {
			exceptions.add( new Fd0Exception( getMessage("ERROR_DECLERED_7") ,declared.toString()) );
			status = false;
		}
		return status;
	}
	
}
