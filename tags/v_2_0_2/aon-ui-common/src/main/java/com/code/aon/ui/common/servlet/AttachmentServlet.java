package com.code.aon.ui.common.servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ql.Criteria;

/**
 * Servlet class invoked whenever a field form needs a Registry Attachment.
 * 
 * @author Consulting & Development. Aimar Tellitu - 29-jul-2005
 * @since 1.0
 */

public class AttachmentServlet extends HttpServlet {

	/** The Constant POJO_INIT_PARAMETER. */
	private static final String POJO_INIT_PARAMETER = "pojo";
	
	/** The manager bean. */
	private IManagerBean managerBean;
	
    /**
     * Initializes the servlet
     * 
     * @param config The <code>ServletConfig</code> object that contains the information
     * to configure this servlet.
     * 
     * @throws ServletException If occurs any exception which interrupts the execution of
     * the servlet.
     * 
     * @see javax.servlet.Servlet#init(ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
       	String pojo = config.getInitParameter(POJO_INIT_PARAMETER); 
		try {
			this.managerBean = BeanManager.getManagerBean(pojo);
		} catch (ManagerBeanException e) {
			throw new ServletException(e.getMessage(), e);			
		}
    }
	
    /**
     * Retrieves the registryAttachment data.
     * 
     * @param req the req
     * @param res the res
     * 
     * @throws IOException the IO exception
     * @throws ServletException the servlet exception
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
	    	if ( req.getParameter("file") != null ) {
	    		FileInputStream in = new FileInputStream( req.getParameter("file") );
	    		byte[] buffer = new byte[1024];
	    		int bytes = in.read(buffer);
	    		while ( bytes != -1 ) {
	    			res.getOutputStream().write( buffer, 0, bytes );
	    			bytes = in.read(buffer);
	    		}
	    		in.close();
	    		byte type = Integer.valueOf( req.getParameter("type") ).byteValue();
	    		res.setContentType( MimeType.values()[type].getName() );
	    		res.setCharacterEncoding("ISO-8859-1"); //$NON-NLS-1$
	    		res.flushBuffer();
	    	} else {
	        	Criteria criteria = new Criteria();
	    		Enumeration e = req.getParameterNames();
	    		while ( e.hasMoreElements() ) {
		    		String parameter = (String) e.nextElement();
		    		String field = this.managerBean.getFieldName(parameter);
		    		String value = req.getParameter(parameter);
		    		criteria.addExpression( field, value );
		    	}
	            List list = this.managerBean.getList(criteria);
	            if ( list.size() > 0 ) {
	            	IAttachment ia = (IAttachment) list.get(0);
	                res.setContentType( ia.getMimeType().getName() );
	                res.setCharacterEncoding("ISO-8859-1"); //$NON-NLS-1$
	                res.getOutputStream().write( ia.getData() );
	                res.flushBuffer();
	            }
	    	}
        } catch (Throwable th) {
            th.printStackTrace();
            throw new ServletException(th.getMessage(), th);
        }
    }

}
