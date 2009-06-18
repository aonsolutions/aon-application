/*
 * Created on 28-jun-2005
 *
 */
package com.code.aon.ui.common.lookup;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ILookupObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.ql.Criteria;

/**
 * Servlet class invoked whenever a field form needs a Lookup.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 28-jun-2005
 * @since 1.0
 */
@Deprecated
public class LookupServlet extends HttpServlet {

	/** The Constant CONTENT_TYPE. */
	protected static final String CONTENT_TYPE = "text/xml";

	/** The Constant CHARACTER_ENCODING. */
	protected static final String CHARACTER_ENCODING = "ISO-8859-1";

	/** The Constant NAME_PARAMETER. */
	protected static final String NAME_PARAMETER = "name";

	/** The Constant VALUE_PARAMETER. */
	protected static final String VALUE_PARAMETER = "value";

	/** The Constant POJO_PARAMETER. */
	protected static final String POJO_PARAMETER = "pojo";

	/** The Constant IDS_PARAMETER. */
	private static final String IDS_PARAMETER = "ids";

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	/**
	 * Retrieves the values of the fields specified in the <code>IDS_PARAMETER</code> and renders the
	 * response 
	 * 
	 * @param req the HttpServletRequest
	 * @param res the HttpServletResponse
	 * 
	 * @throws IOException the IO exception
	 * @throws ServletException the servlet exception
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		Criteria criteria = new Criteria();
		String pojo = req.getParameter(POJO_PARAMETER);
		String name = getName(req.getParameter(NAME_PARAMETER));
		String ids = req.getParameter(IDS_PARAMETER);
		try {
			IManagerBean ibmb = BeanManager.getManagerBean(pojo);
			criteria.addExpression(ibmb.getFieldName(name), req.getParameter(VALUE_PARAMETER));
			List<ITransferObject> list = ibmb.getList(criteria);
			if (list.size() > 0) {
				ILookupObject ito = (ILookupObject) list.get(0);
				renderResponse (res,ito,ids);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage(), e);
		}
	}

    /**
     * Gets the name.
     * 
     * @param name the name
     * 
     * @return the name
     */
    private String getName(String name) {
        int pos = name.indexOf("-");
        if (pos != -1) {
            String result = name.substring(0, pos);
            return result;
        }
        return name;
    }

    /**
     * Set the cache controls on the response headers. Still in need of some
     * experimentation here.
     *
     * @param response
     */
    private void setCacheControls(HttpServletResponse httpResponse) {
        httpResponse.setHeader("Cache-Control", "no-cache"); //HTTP 1.1
        httpResponse.setHeader("Cache-Control", "no-store"); //HTTP 1.1
        httpResponse.setHeader("Cache-Control", "must-revalidate"); //HTTP 1.1
        httpResponse.setHeader("Pragma", "no-cache"); //HTTP 1.0
        httpResponse.setDateHeader("Expires", 0); //prevents proxy caching
    }
    
	/**
	 * Renders the response
	 * 
	 * @param ids the ids to render
	 * @param ito the ILookupObject from the values will be retrieved 
	 * @param res the HttpServletResponse
	 * 
	 * @throws IOException the IO exception
	 */
	protected void renderResponse(HttpServletResponse res, ILookupObject ito, String ids) throws IOException {
		setCacheControls( res );
		res.setContentType(CONTENT_TYPE);
		res.setCharacterEncoding(CHARACTER_ENCODING);
		Map<String,Object> map = ito.getLookups();
		customizeLookupMap(ito, map);
		res.getOutputStream().print(LookupUtils.getResponseXML(map, ids));
		res.flushBuffer();
	}

	// To redefine in childs
	/**
	 * Method used to add entries in the map which can't be added in the method <code>getLookups()</code>
	 * of the ILookupObject.
	 * 
	 * @param ito the ILookupObject
	 * @param map the map
	 */
	@SuppressWarnings("unused")
	protected void customizeLookupMap(ILookupObject ito, Map<String, Object> map) {
	}

}
