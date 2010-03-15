package com.code.aon.file.tax.model.MOD347.check;

import java.util.ArrayList;

import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.tax.model.MOD347.data.Building;

/**
 * Checks the Building objects data
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public class CheckBuilding extends Check {

	/**
	 * Parses data
	 * 
	 * @param building the object to parse
	 * @param exceptions errors founds
	 * @return true if no errors
	 */
	public static boolean parse(Building building,ArrayList<Exception> exceptions){
		boolean status = true;
		if (building.getCode()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_BUILDING_1") ,building.toString()) );
			status = false;
		}
		if (building.getName()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_BUILDING_2") ,building.toString()) );
			status = false;
		}
		if (building.getCadastre()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_BUILDING_3") ,building.toString()) );
			status = false;
		}
		if (building.getProvince()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_BUILDING_4") ,building.toString()) );
			status = false;
		}
		if (building.getCounty()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_BUILDING_5") ,building.toString()) );
			status = false;
		}
		if (building.getStreetName()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_BUILDING_6") ,building.toString()) );
			status = false;
		}
		return status;
	}

}
