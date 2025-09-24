package net.aonsolutions.aon.gwt.sii.server;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;

import jakarta.servlet.annotation.WebServlet;
import net.aonsolutions.aon.gwt.sii.client.ISii;

@SuppressWarnings("serial")
@WebServlet(name = "SiiGwtServlet", urlPatterns = { "/aon_gwt_aio/ms/gwt_sii" })
public class SiiImpl extends AonStatelessRemoteServiceServlet implements ISii{

	public InvoiceCommunicationConfiguration getConfiguration(Occam occam) {
		return AON.getInvoiceCommunicationConfiguration(occam);
	}

	
}
