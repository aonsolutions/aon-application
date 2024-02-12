package com.esferalia.aon.gwt.fiscal.server.fiscal.aeat;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.aeat.AeatService;
import com.esferalia.aon.gwt.fiscal.server.fiscal.ModelAdmonUtils;
import com.esferalia.aon.occam.api.model.ddff.AeatFiscalData;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "Aon Aeat Com Center", urlPatterns = { "/aon_gwt_fiscal/ms/AeatComCenter" })
public class AeatServiceImpl extends AonStatelessRemoteServiceServlet implements AeatService {

	private static final long serialVersionUID = -1495086199296794184L;

	@Override
	public AeatFiscalData getAeatFiscalData(AEATParams params) throws AonCoreException {
		return  ModelAdmonUtils.fiscalData( null, params);
	}
}
