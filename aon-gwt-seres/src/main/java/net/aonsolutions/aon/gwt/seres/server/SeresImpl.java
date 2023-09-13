package net.aonsolutions.aon.gwt.seres.server;

import jakarta.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;

import net.aonsolutions.aon.gwt.seres.client.ISeres;

@SuppressWarnings("serial")
@WebServlet(name = "SeresGwtServlet", urlPatterns = { "/aon_gwt_aio/gwt_seres" })
public class SeresImpl extends AonRemoteServiceServlet implements ISeres{

}
