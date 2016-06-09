package com.code.aon.warehouse.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum WarehouseTransferSource implements IResourceable {

	DIRECT_TRANSFER,
	
	INVENTORY,
	
	MANUFACTURING_ORDER,
	
	INVENTORY_INIT_STOCK;
	
    private static final String MSG_KEY_PREFIX = "aon_enum_warehouseTransfer_source_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
}
