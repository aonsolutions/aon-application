package com.code.aon.csb.fd0.model.BE.checks;

import java.util.ArrayList;

import com.code.aon.csb.fd0.model.Fd0Exception;
import com.code.aon.csb.fd0.model.BE.data.Relationship;


/**
 * Checks Detail object values
 * 
 * @author Consulting & Development. Iñigo GAyarre - 20/02/2007
 * @since 1.0
 *
 */
public class CheckRelationship extends Check{

	/**
	 * Parses data
	 * 
	 * @param transfer the object to parse
	 * @param exceptions errors founds
	 * @return true if no errors
	 */
	public static boolean parse(Relationship relationship,ArrayList<Exception> exceptions){
		boolean status = true;
		if (relationship.getEmitterCode()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_RELATIONSHIP_1") ,relationship.toString()) );
			status = false;
		}
		if (relationship.getOrderNumber()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_RELATIONSHIP_2") ,relationship.toString()) );
			status = false;
		}
		if (relationship.getExecutionDate()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_RELATIONSHIP_3") ,relationship.toString()) );
			status = false;
		}
		return status;
	}
	
}
