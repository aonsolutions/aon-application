package net.aonsolutions.aon.gwt.communication.server;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;

import net.aonsolutions.aon.gwt.communication.client.ICommunication;

@SuppressWarnings("serial")
@WebServlet(name = "CommunicationGwtServlet", urlPatterns = { "/aon_gwt_aio/gwt_communication" })
public class CommunicationImpl extends AonRemoteServiceServlet implements ICommunication{

}
