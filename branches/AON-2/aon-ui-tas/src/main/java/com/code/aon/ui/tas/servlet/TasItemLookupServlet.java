package com.code.aon.ui.tas.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.commercial.Target;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ILookupObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.tas.SupportOrder;
import com.code.aon.tas.dao.ITASAlias;
import com.code.aon.ui.common.lookup.LookupUtils;

/**
 * Completes the lookup info for tas item
 * recovering more data
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class TasItemLookupServlet extends HttpServlet {

	/**
	 * the logger
	 */
	private static final Logger LOGGER = Logger
			.getLogger(TasItemLookupServlet.class.getName());

    /**
     * Content type 
     */
    protected static final String CONTENT_TYPE = "text/xml";

    /**
     * Encoding
     */
    protected static final String CHARACTER_ENCODING = "ISO-8859-1";

    /**
     * Name parameter id
     */
    protected static final String NAME_PARAMETER = "name";

	/**
	 * value parameter id
	 */
	protected static final String VALUE_PARAMETER = "value";
	
	/**
	 * pojo parameter id
	 */
	protected static final String POJO_PARAMETER = "pojo";

	/**
	 * ids parameter id
	 */
	private static final String IDS_PARAMETER = "ids";	

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		Criteria criteria = new Criteria();
		String pojo = req.getParameter(POJO_PARAMETER);
		String name = getName( req.getParameter(NAME_PARAMETER) );
		String ids = req.getParameter(IDS_PARAMETER);
		try {
			IManagerBean ibmb = BeanManager.getManagerBean(pojo);
			criteria.addExpression(ibmb.getFieldName(name), req
					.getParameter(VALUE_PARAMETER));
			List<ITransferObject> list = ibmb.getList(criteria);
			if (list.size() > 0) {
				ILookupObject ito = (ILookupObject) list.get(0);
				renderResponse (res,ito,ids);
			}else{
				renderVoidResponse (res,ids);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage(), e);
		}
	}
	
	/**
	 * Converts the name string if needed
	 * 
	 * @param name
	 * @return
	 */
	private String getName( String name ) {
		int pos = name.indexOf("-");
		if ( pos != -1 ) {
			String result = name.substring( 0, pos );
			return result;
		}
		return name;
	}

	/**
	 * Full response
	 * 
	 * @param res servlet response
	 * @param ito ILookupObject to obtain the lookups
	 * @param ids ids parameter 
	 * @throws IOException
	 */
	protected void renderResponse(HttpServletResponse res, ILookupObject ito, String ids) throws IOException {
		res.setContentType(CONTENT_TYPE);
		res.setCharacterEncoding(CHARACTER_ENCODING);
		Map<String,Object> map = ito.getLookups();
        customizeLookupMap(ito, map);
        res.getOutputStream().print(LookupUtils.getResponseXML(map, ids));
		res.flushBuffer();
	}

	/**
	 * Void response
	 * 
	 * @param res servlet response
	 * @param ids ids parameter 
	 * @throws IOException
	 */
	protected void renderVoidResponse(HttpServletResponse res, String ids) throws IOException {
		res.setContentType(CONTENT_TYPE);
		res.setCharacterEncoding(CHARACTER_ENCODING);
		Map<String,Object> map = new HashMap<String,Object>();
        map.put(ITASAlias.TAS_ITEM_ID, null);
        res.getOutputStream().print(LookupUtils.getResponseXML(map, ids));
		res.flushBuffer();
	}
	
	/**
	 * Method used to add entries in the map which can't be added in the method <code>getLookups()</code>
	 * of the ILookupObject.
	 * 
	 * @param ito the ILookupObject
	 * @param map the map
     * @see com.code.aon.ui.common.lookup.LookupServlet#customizeLookupMap(com.code.aon.common.ILookupObject, java.util.Map)
     */
	@SuppressWarnings("unused")
	protected void customizeLookupMap(ILookupObject ito, Map<String, Object> map) {
		// BUSCAR EL TARGET
        try{
    		IManagerBean supportOrderBean = BeanManager.getManagerBean(SupportOrder.class);
    		Criteria socriteria = new Criteria();
    		socriteria.addEqualExpression(supportOrderBean.getFieldName(ITASAlias.SUPPORT_ORDER_TAS_ITEM_ID),map.get("TasItem_id"));
    		socriteria.addOrder(supportOrderBean.getFieldName(ITASAlias.SUPPORT_ORDER_START_DATE));
    		Iterator soiter = supportOrderBean.getList(socriteria).iterator();
    		if (soiter.hasNext()) {
    			SupportOrder so = (SupportOrder) soiter.next();
    			Target target = so.getTarget();
    			Map targetMap = target.getLookups();
    			Iterator targetIter = targetMap.keySet().iterator();
    			while (targetIter.hasNext()){
    				String key = (String)targetIter.next();
    				map.put(key, targetMap.get(key));
    			}
    			// BUSCAR LOS TELEFONOS
    			IManagerBean rMediaBean = BeanManager.getManagerBean(RegistryMedia.class);
    			Criteria criteria = new Criteria();
    			criteria.addEqualExpression(rMediaBean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_REGISTRY_ID),target.getId());
    			Iterator iter = rMediaBean.getList(criteria).iterator();

    			if (iter.hasNext()) {
    				while (iter.hasNext()){
    					RegistryMedia rmedia = (RegistryMedia)iter.next();
    					if (MediaType.FIXED_PHONE == rmedia.getMediaType()){
    						map.put(PHONE, rmedia.getValue());
    					}else if (MediaType.CELLULAR == rmedia.getMediaType()){
    						map.put(CELLULAR, rmedia.getValue());
    					}else if (MediaType.FAX == rmedia.getMediaType()){
    						map.put(FAX, rmedia.getValue());
    					}else if (MediaType.EMAIL == rmedia.getMediaType()){
    						map.put(EMAIL, rmedia.getValue());
    					}
    				}
    			} else {
    				map.put(PHONE, "");
    				map.put(CELLULAR, "");
    				map.put(FAX, "");
    				map.put(EMAIL, "");
    			}
    			// BUSCAR LAS DIRECCIONES
    			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
    			Criteria criteriaAddress = new Criteria();
    			criteriaAddress.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID),map.get("Target_registry_id"));
    			criteriaAddress.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_ADDRESS_TYPE),AddressType.MAIN);
    			Iterator iterAddress = rAddressBean.getList(criteria).iterator();

    			if (iterAddress.hasNext()) {
    				RegistryAddress raddress = (RegistryAddress)iterAddress.next();
    				map.put(ADDRESS, raddress.getAddress());
    				map.put(ADDRESS2, raddress.getAddress2());
    				map.put(ADDRESS3, raddress.getAddress3());
    				map.put(CITY, raddress.getCity());
    				map.put(ZIP, raddress.getZip());
    				map.put(GEOZONE, raddress.getGeozone().getId());
    			} else {
    				map.put(ADDRESS, "");
    				map.put(ADDRESS2, "");
    				map.put(ADDRESS3, "");
    				map.put(CITY, "");
    				map.put(ZIP, "");
    				map.put(GEOZONE, "");
    			}
    			
    		}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error customizing lookup map", e);
		}
	}
	
	/**
	 * Alias for the phone of the target
	 */
	private static final String PHONE = "Registry_phone";
	/**
	 * Alias for the cellular of the target
	 */
	private static final String CELLULAR = "Registry_cellular";
	/**
	 * Alias for the fax of the target
	 */
	private static final String FAX = "Registry_fax";
	/**
	 * Alias for the email of the target
	 */
	private static final String EMAIL = "Registry_email";
	/**
	 * Alias for the address of the target
	 */
	private static final String ADDRESS = "RegistryAddress_address";
	/**
	 * Alias for the address2 of the target
	 */
	private static final String ADDRESS2 = "RegistryAddress_address2";
	/**
	 * Alias for the address3 of the target
	 */
	private static final String ADDRESS3 = "RegistryAddress_address3";
	/**
	 * Alias for the city of the target
	 */
	private static final String CITY = "RegistryAddress_city";
	/**
	 * Alias for the zip of the target
	 */
	private static final String ZIP = "RegistryAddress_zip";
	/**
	 * Alias for the geozone of the target
	 */
	private static final String GEOZONE = "Geozone_id";
}