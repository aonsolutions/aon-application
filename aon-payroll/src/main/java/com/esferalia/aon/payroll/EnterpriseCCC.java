package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.esferalia.aon.entity.master.EnterpriseCCCDB;

@Entity
@Table(name="enterprise_ccc")
public class EnterpriseCCC extends EnterpriseCCCDB {
	
	private static final long serialVersionUID = 1L;
	
	@Transient
	public String getFullCcc(){
		if(this.getActivity()!=null && this.getActivity().getQuoteRegimeCode()!=null){
			return this.getActivity().getQuoteRegimeCode() + this.getCcc();
		}
		return this.getCcc();
	}
	
	@Transient
	public boolean isValidSSNumber(){
		try {
			if(StringUtils.isEmpty(getCcc()) || !getValidSSNumber(getCcc()).isEmpty()){
				return false;
			}
		} catch (IllegalArgumentException e) {
			return false;
		}
		return true;
	}
	
	private String getValidSSNumber(String ssNumber) throws IllegalArgumentException {
		Integer iDC = null;
		Integer iDCTemp = null;
		Integer iPos = null;
		String sNumSegSocialTemp = null; 
		String sTempNumOriginal = null;
		
	    if (ssNumber.length() > 12 || ssNumber.length() == 0) {
	        throw new IllegalArgumentException("La longitud no es correcta.");
	    }
	    if (ssNumber.matches("[^0-9]")) {
	        throw new IllegalArgumentException("El valor debe ser numerico.");
	    }
	    iDCTemp = Integer.parseInt( ssNumber.substring(ssNumber.length()-2, ssNumber.length()) );
	    sTempNumOriginal = ssNumber.substring(0, ssNumber.length()-2);
	    switch(ssNumber.length()) {
	        case 11: // Número de Empresa
	        	sNumSegSocialTemp = ssNumber.substring(0, 2) + ssNumber.substring(2, 9);
	            break;                        
	        case 12: // Número de Trabajador
	            iPos = Integer.parseInt( (ssNumber.substring(2,3)) );
	            if (iPos == 0) {
	            	sNumSegSocialTemp = ssNumber.substring(0, 2) + ssNumber.substring(3, 10);
	            } else {
	                sNumSegSocialTemp = ssNumber.substring(0, 2) + ssNumber.substring(2, 10);
	            }
	            break;
	        default:
	        	ssNumber = null;
        }	        
	    iDC = (int) ( Long.parseLong(sNumSegSocialTemp) - ( (Long.parseLong(sNumSegSocialTemp) / 97) * 97 ) );
	    
	    if (iDC == iDCTemp) {
	        return "";
	    } else {
	    	return sTempNumOriginal + iDC.toString();
	    }
	}

}
