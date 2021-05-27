package net.aonsolutions.aon.gwt.sii.server;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;

import net.aonsolutions.aon.gwt.sii.client.ISii;

@SuppressWarnings("serial")
@WebServlet(name = "SiiGwtServlet", urlPatterns = { "/aon_gwt_aio/ms/gwt_sii" })
public class SiiImpl extends AonStatelessRemoteServiceServlet implements ISii{

	public Administration getAdministration(Domain domain, String login) {
		ApplicationParameter param= AON.getApplicationParameter(domain.getName(), domain.getId(), login, AppParam.FS_DEFAULT_ADMINISTRATION);
		return param.getValue() != null ? Administration.values()[Integer.parseInt(param.getValue())] : Administration.COMMON_TERRITORY;
	}

	
}
