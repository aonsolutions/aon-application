/*
 * Created on 28-jun-2005
 *
 */
package com.code.aon.ui.form.lookup;

import java.io.IOException;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.ui.common.lookup.LookupUtils;
import com.code.aon.ui.common.lookup.ServleJSFtUtil;

/**
 * Servlet class invoked whenever a field form needs a LookupBean.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 28-jun-2005
 * @since 1.0
 */
public class LookupBeanServlet extends HttpServlet {

	/** The Constant CONTENT_TYPE. */
	private static final String CONTENT_TYPE = "text/xml";

	/** The Constant CHARACTER_ENCODING. */
	private static final String CHARACTER_ENCODING = "ISO-8859-1";

	/** The Constant BEAN_PARAMETER. */
	private static final String BEAN_PARAMETER = "bean";

	/** The Constant VALUE_PARAMETER. */
	private static final String VALUE_PARAMETER = "value";
	
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
	 * @param req the req
	 * @param res the res
	 * 
	 * @throws IOException the IO exception
	 * @throws ServletException the servlet exception
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		String bean = req.getParameter(BEAN_PARAMETER);		
		ILookupBean lookupBean = (ILookupBean) ServleJSFtUtil.getManagedBean( req, res, bean );
		String[] values = getValues(req.getParameter(VALUE_PARAMETER));
		String ids =  req.getParameter(IDS_PARAMETER);
		try {
			Map<String,Object> map = lookupBean.getLookups(values);		
			lookupBean.setIds( ids );
			renderResponse (res, map, lookupBean.getIdsMap());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage(), e);
		}
	}
	
	/**
	 * Return <code>value</code> as an array.
	 * 
	 * @param value the value
	 * 
	 * @return the values
	 */
	private String[] getValues(String value) {
		StringTokenizer st = new StringTokenizer( value, " ," );
		String[] result = new String[st.countTokens()];
		for( int i = 0; st.hasMoreTokens(); i++ ) {
			result[i] = st.nextToken();
		}
		return result;
	}
	
    /**
     * Set the cache controls on the response headers. Still in need of some
     * experimentation here.
     *
     * @param response
     */
    private void setCacheControls(HttpServletResponse httpResponse) {
        // Not caching AJAX request
        httpResponse.setHeader("Cache-Control", "no-cache, must-revalidate, max-age=0, no-store");
        httpResponse.setHeader("Pragma", "no-cache"); //HTTP 1.0
        httpResponse.setDateHeader("Expires", 0); //prevents proxy caching
    }

	/**
	 * Renders the response.
	 * 
	 * @param idMap the ids map
	 * @param map the map
	 * @param res the res
	 * 
	 * @throws IOException the IO exception
	 */
	protected void renderResponse(HttpServletResponse res, Map<String,Object> map, Map<String,String> idMap) throws IOException {
		setCacheControls( res );
		res.setContentType(CONTENT_TYPE);
		res.setCharacterEncoding(CHARACTER_ENCODING);
		res.getOutputStream().print(LookupUtils.getResponseXML(map, idMap));
		res.flushBuffer();
	}

}
