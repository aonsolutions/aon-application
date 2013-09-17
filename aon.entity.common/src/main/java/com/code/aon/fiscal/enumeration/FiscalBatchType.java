package com.code.aon.fiscal.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum FiscalBatchType implements IResourceable {

	MOD303( true )
	,MOD111( true )
	,MOD115( true )
	,MOD123( true )
	,MOD130( true )
	,MOD131( true )
	,MOD310( true )
//	,MOD390( false )
//	,MOD347( false )
//	,MOD349( true )
	;

    private static final String MSG_KEY_PREFIX = "aon_enum_fiscal_batch_type_";

    private boolean annual;
    
    private FiscalBatchType(boolean annual) {
    	this.annual = annual;
	}
    
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
    public boolean isAnnual() {
    	return annual;
    }
    
}