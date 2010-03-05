package com.code.aon.ui.tas.servlet;

import java.util.Map;

import com.code.aon.common.ILookupObject;
import com.code.aon.tas.SupportOrder;
import com.code.aon.ui.common.lookup.LookupServlet;

/**
 * Completes the lookup info for support order
 * recovering more data
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class SupportOrderLookupServlet extends LookupServlet {
	
	/**
	 * Alias for the name of the target in support order
	 */
	private static final String SUPPORT_ORDER_TARGET_NAME = "SupportOrder_target_name";
    /**
	 * Alias for the surname of the target in support order
     */
    private static final String SUPPORT_ORDER_TARGET_SURNAME = "SupportOrder_target_surname";
    /**
	 * Alias for the document of the target in support order
     */
    private static final String SUPPORT_ORDER_TARGET_DOCUMENT = "SupportOrder_target_document";

	/**
	 * Method used to add entries in the map which can't be added in the method <code>getLookups()</code>
	 * of the ILookupObject.
	 * 
	 * @param ito the ILookupObject
	 * @param map the map
     * @see com.code.aon.ui.common.lookup.LookupServlet#customizeLookupMap(com.code.aon.common.ILookupObject, java.util.Map)
     */
    protected void customizeLookupMap(ILookupObject ito, Map<String,Object> map) {
    	map.put(SUPPORT_ORDER_TARGET_NAME, ((SupportOrder)ito).getTarget().getRegistry().getName());
    	map.put(SUPPORT_ORDER_TARGET_SURNAME, ((SupportOrder)ito).getTarget().getRegistry().getSurname());
    	map.put(SUPPORT_ORDER_TARGET_DOCUMENT, ((SupportOrder)ito).getTarget().getRegistry().getDocument());
    }
}