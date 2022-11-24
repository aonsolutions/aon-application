package com.esferalia.aon.gwt.fiscal.server.fiscal.mod390HF;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod390hf.Mod390HFService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.fiscal.MODEL390HF;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod390HF Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod390HF" })
public class Mod390HFServiceImpl extends AonStatelessRemoteServiceServlet implements Mod390HFService {

	private static final long serialVersionUID = 11373213973954273L;

	public static Mod390HFServiceImpl getInstance() {
		return new Mod390HFServiceImpl();
	}

	// ---------------------------------------------------------------MODELO 303
	@Override
	public Mod390HF get(Occam occam, int id) throws AonCoreException {
		return MODEL390HF.get(occam, id);
	}

	@Override
	public LinkedList<Mod390HF> getMod390HFs(Occam occam) throws AonCoreException {
		return MODEL390HF.getMod390HFs(occam);
	}

	@Override
	public Mod390HF calculate(Occam occam, Mod390HF mod303) {
		return MODEL390HF.calculate(occam, mod303);
	}

	@Override
	public Mod390HF calculateProrrate(Occam occam, Mod390HF model) {
		return MODEL390HF.calculateProrrate(occam, model);
	}
	
	@Override
	public Mod390HF save(Occam occam, Mod390HF mod303) {
		return MODEL390HF.save(occam, mod303);
	}

	@Override
	public Mod390HF saveComments(Occam occam, Mod390HF mod303) {
		return MODEL390HF.saveComments(occam, mod303);
	}

	@Override
	public Mod390HF initializeForFinish(Occam occam, Mod390HF mod303) {
		return MODEL390HF.initializeForFinish(occam, mod303);
	}

	@Override
	public Mod390HF initialize(Occam occam, Mod390HF mod303) {
		return MODEL390HF.initialize(occam, mod303);
	}

	@Override
	public Mod390HF create(Occam occam, Mod390HF mod303) {
		return MODEL390HF.create(occam, mod303);
	}

	@Override
	public Mod390HF reset(Occam occam, Mod390HF mod303) {
		return MODEL390HF.reset(occam, mod303);
	}
	
	@Override
	public void delete(Occam occam, Mod390HF mod303) {
		MODEL390HF.delete(occam, mod303);
	}

	@Override
	public String getInfo(Occam occam, Mod390HF mod303, IModelScript<Mod390Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException {
		return MODEL390HF.getInfo(occam, mod303, script, infoKey);

	}

	@Override
	public Mod390HF markAsFinished(Occam occam, Mod390HF mod303) {
		return MODEL390HF.markAsFinished(occam, mod303);
	}

	@Override
	public Mod390HF markAsPending(Occam occam, Mod390HF mod303) {
		return MODEL390HF.markAsPending(occam, mod303);
	}

	@Override
	public Mod390HF markAsSent(Occam occam, Mod390HF mod303) throws AonCoreException {
		return MODEL390HF.markAsSent(occam, mod303);
	}

	@Override
	public Mod390HF markAsCustomerCheck(Occam occam, Mod390HF mod303) throws AonCoreException {
		return MODEL390HF.markAsCustomerCheck(occam, mod303);
	}
	
	@Override
	public Mod390HF markAsCustomerAccepted(Occam occam, Mod390HF mod303) throws AonCoreException {
		return MODEL390HF.markAsCustomerAccepted(occam, mod303);
	}

	@Override
	public Mod390HF markAsCustomerRejected(Occam occam, Mod390HF mod303, String reason) throws AonCoreException {
		return MODEL390HF.markAsCustomerRejected(occam, mod303, reason);
	}
	
	@Override
	public Invoice getInvoice(Occam occam, int invoiceId) throws AonCoreException {
		return AON.getInvoice(occam, invoiceId);
	}

}
