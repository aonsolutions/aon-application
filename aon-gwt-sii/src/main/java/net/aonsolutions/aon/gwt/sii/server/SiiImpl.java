package net.aonsolutions.aon.gwt.sii.server;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;

import net.aonsolutions.aon.gwt.sii.client.ISii;

@SuppressWarnings("serial")
@WebServlet(name = "SiiGwtServlet", urlPatterns = { "/aon_gwt_aio/gwt_sii" })
public class SiiImpl extends AonRemoteServiceServlet implements ISii{

	
	
}
