package com.esferalia.aon.gwt.fiscal.server.fiscal.mod130;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod130.Mod130Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.fiscal.MODEL130;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod130 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod130" })
public class Mod130ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod130Service {

	private static final long serialVersionUID = 1L;
	
	public static Mod130ServiceImpl getInstance() {
		return new Mod130ServiceImpl();
	}
	
	@Override
	public Mod130 getMod130(Occam occam,int id) throws AonCoreException {
		return MODEL130.get(occam , id);
	}

	@Override
	public LinkedList<Mod130> getMod130s(Occam occam) throws AonCoreException {
		return MODEL130.getMod130s(occam);
	}

	@Override
	public Mod130 calculate(Occam occam, Mod130 mod130) {
		return MODEL130.calculate(occam, mod130);
	}

	@Override
	public Mod130 save(Occam occam, Mod130 mod130) {
		return MODEL130.save(occam, mod130);
	}

	@Override
	public Mod130 saveComments(Occam occam, Mod130 mod130) {
		return MODEL130.saveComments(occam, mod130);
	}

	@Override
	public Mod130 initializeForFinish(Occam occam, Mod130 mod130) {
		return MODEL130.initializeForFinish(occam, mod130);
	}

	@Override
	public Mod130 markAsFinished(Occam occam, Mod130 mod130) {
		return MODEL130.markAsFinished(occam, mod130);
	}

	@Override
	public Mod130 markAsPending(Occam occam, Mod130 mod130) {
		return MODEL130.markAsPending(occam, mod130);
	}

	@Override
	public Mod130 markAsSent(Occam occam, Mod130 mod130) {
		return MODEL130.markAsSent(occam, mod130);
	}

	@Override
	public Mod130 markAsCustomerCheck(Occam occam, Mod130 mod130) {
		return MODEL130.markAsCustomerCheck(occam, mod130);
	}

	@Override
	public Mod130 markAsCustomerAccepted(Occam occam, Mod130 mod130) {
		return MODEL130.markAsCustomerAccepted(occam, mod130);
	}

	@Override
	public Mod130 markAsCustomerRejected(Occam occam, Mod130 mod130, String reason) {
		return MODEL130.markAsCustomerRejected(occam, mod130, reason);
	}

	@Override
	public Mod130 initialize(Occam occam, Mod130 mod130) {
		return MODEL130.initialize(occam, mod130);
	}

	@Override
	public Mod130 create(Occam occam, Mod130 mod130) {
		return MODEL130.create(occam, mod130);
	}

	@Override
	public void delete(Occam occam, Mod130 mod130) {
		MODEL130.delete(occam, mod130);
	}
	@Override
	public String getInfo(Occam occam, Mod130 mod130 , IModelScript<Mod130Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException {
		return MODEL130.getInfo(occam, mod130, script, infoKey);
		
	}
	
	@Override
	public Invoice getInvoice(Occam occam, int invoiceId) throws AonCoreException {
		return AON.getInvoice(occam, invoiceId);
	}	
}
