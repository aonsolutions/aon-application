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

import com.code.aon.commercial.dao.ICommercialAlias;
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
import com.code.aon.ui.common.lookup.LookupUtils;

/**
 * Completes the lookup info for target
 * recovering more data
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class TargetLookupServlet extends HttpServlet {

	/**
	 * the logger
	 */
	private static final Logger LOGGER = Logger
			.getLogger(TargetLookupServlet.class.getName());

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
        map.put(ICommercialAlias.TARGET_REGISTRY_ID, null);
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
	@SuppressWarnings({"unused","unchecked"})
	protected void customizeLookupMap(ILookupObject ito, Map<String, Object> map) {
		try {
			// BUSCAR LOS TELEFONOS
			IManagerBean rMediaBean = BeanManager.getManagerBean(RegistryMedia.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rMediaBean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_REGISTRY_ID),map.get("Target_registry_id"));
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
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error customizing lookup map", e);
		}
	}
}