package net.aonsolutions.aon.gwt.sii.server;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;

import jakarta.servlet.annotation.WebServlet;
import net.aonsolutions.aon.gwt.sii.client.ISii;

@SuppressWarnings("serial")
@WebServlet(name = "SiiGwtServlet", urlPatterns = { "/aon_gwt_aio/ms/gwt_sii" })
public class SiiImpl extends AonStatelessRemoteServiceServlet implements ISii{

	public SiiConfiguration getSiiConfiguration(Domain domain, String login) {
		return AON.getSiiConfiguration(domain, login);
	}

	
}
