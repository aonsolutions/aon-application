package net.aonsolutions.aon.gwt.udapa.server;

import javax.servlet.annotation.WebServlet;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import net.aonsolutions.aon.gwt.udapa.client.IUdapa;

@WebServlet(name = "UdapaGwtServlet", urlPatterns = { "/aon_gwt_aio/gwt_udapa" })
public class UdapaImpl extends RemoteServiceServlet implements IUdapa{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}