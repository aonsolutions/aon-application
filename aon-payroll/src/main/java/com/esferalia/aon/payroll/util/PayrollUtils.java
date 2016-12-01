package com.esferalia.aon.payroll.util;

import org.apache.commons.lang.StringUtils;

import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.enumeration.ss.T54;
import com.esferalia.aon.salary.enumeration.BonusType;


public class PayrollUtils {
	
	private static PayrollUtils instance;
	
	public static PayrollUtils getInstance(){
		if(instance == null){
			instance = new PayrollUtils();
		}
		return instance;
	}
	
	public BonusType getBonusTypeByCode(String code){
		
		if(T54.getEnumByValue(code)==T54.T54_3262){
			return BonusType.REDUCTION_FLAT_RATE_RDL03_2014; 
		} else if(T54.getEnumByValue(code)==T54.T54_3263){
			return BonusType.REDUCTION_FLAT_RATE_RDL03_2014;
		} else if(T54.getEnumByValue(code)==T54.T54_3266){
			return BonusType.REDUCTION_RATE_RDL01_2015; 
		} else if(T54.getEnumByValue(code)==T54.T54_3268){
			return BonusType.REDUCTION_RATE_RDL01_2015;
		}
		
		return null;
	}
	
	public String getRegimeCode(EnterpriseCCC ccc){
		if(ccc!=null){
			if(ccc.getType()==CCCType.AGRICULTURAL){
				return SSRegimeType.AGRICULTURAL.getCode();
			} else if(ccc.getType()==CCCType.HOME_EMPLOYEES){
				return SSRegimeType.DOMESTIC_EMPLOYEES.getCode();
			} else {
				return ccc.getActivity().getType().getCode();
			}
		}
		return null;
	}
	
	public boolean isValidSSNumber(EnterpriseCCC ccc){
		try {
			if(StringUtils.isEmpty(ccc.getCcc()) || !getValidSSNumber(ccc.getCcc()).isEmpty()){
				return false;
			}
		} catch (IllegalArgumentException e) {
			return false;
		}
		return true;
	}
	
	public String getValidSSNumber(String ssNumber) throws IllegalArgumentException {
		Integer iDC = null;
		Integer iDCTemp = null;
		String sNumSegSocialTemp = null; 
		String sTempNumOriginal = null;
		if (ssNumber.length() != 11) {
	        throw new IllegalArgumentException("La longitud no es correcta.");
	    }
	    if (ssNumber.matches("[^0-9]")) {
	        throw new IllegalArgumentException("El valor debe ser numerico.");
	    }
	    iDCTemp = Integer.parseInt( ssNumber.substring(ssNumber.length()-2, ssNumber.length()) );
	    sTempNumOriginal = ssNumber.substring(0, ssNumber.length()-2);
	    if (ssNumber.charAt(2)=='0') {
	    	sNumSegSocialTemp = ssNumber.substring(0, 2) + ssNumber.substring(3, 9);
	    } else {
	    	sNumSegSocialTemp = ssNumber.substring(0, 9);
	    }
	    iDC = Integer.parseInt(sNumSegSocialTemp) % 97;
	    if (iDC == iDCTemp) {
	        return "";
	    } else {
	    	return sTempNumOriginal + iDC.toString();
	    }
	}
	

}
