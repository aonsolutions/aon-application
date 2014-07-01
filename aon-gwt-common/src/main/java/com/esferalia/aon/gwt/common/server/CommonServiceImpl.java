package com.esferalia.aon.gwt.common.server;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.client.CommonService;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
@WebServlet(name = "Common Servlet", urlPatterns = { "/aon_gwt_fiscal/Common " })
public class CommonServiceImpl extends AonRemoteServiceServlet implements CommonService {


}
