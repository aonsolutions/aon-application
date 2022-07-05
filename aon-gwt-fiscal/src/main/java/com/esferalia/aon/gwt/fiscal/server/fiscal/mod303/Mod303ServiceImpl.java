package com.esferalia.aon.gwt.fiscal.server.fiscal.mod303;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod303.Mod303Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod303 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod303" })
public class Mod303ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod303Service {

	private static final long serialVersionUID = -1101706717961420535L;

	public static Mod303ServiceImpl getInstance() {
		return new Mod303ServiceImpl();
	}
	
	@Override
	public Mod303 getMod303(Occam occam, int id) throws AonCoreException {
		return MODEL303.get(occam, id);
	}

	@Override
	public LinkedList<Mod303> getMod303s(Occam occam) throws AonCoreException {
		return MODEL303.getMod303s(occam);
	}

	@Override
	public Mod303 calculate(Occam occam, Mod303 mod303) {
		return MODEL303.calculate(occam, mod303);
	}

	@Override
	public Mod303 calculateProrrate(Occam occam, Mod303 mod303) {
		return MODEL303.calculateProrrate(occam, mod303);
	}

	@Override
	public Mod303 save(Occam occam, Mod303 mod303) {
		return MODEL303.save(occam, mod303);
	}

	@Override
	public Mod303 saveComments(Occam occam, Mod303 mod303) {
		return MODEL303.saveComments(occam, mod303);
	}

	@Override
	public Mod303 initializeForFinish(Occam occam, Mod303 mod303) {
		return MODEL303.initializeForFinish(occam, mod303);
	}

	@Override
	public Mod303 markAsFinished(Occam occam, Mod303 mod303) {
		return MODEL303.markAsFinished(occam, mod303);
	}

	@Override
	public Mod303 markAsPending(Occam occam, Mod303 mod303) {
		return MODEL303.markAsPending(occam, mod303);
	}

	@Override
	public Mod303 initialize(Occam occam, Mod303 mod303) {
		return MODEL303.initialize(occam, mod303);
	}

	@Override
	public Mod303 create(Occam occam, Mod303 mod303) {
		return MODEL303.create(occam, mod303);
	}
	@Override
	public Mod303 reset(Occam occam, Mod303 mod303) {
		return MODEL303.reset(occam, mod303);
	}
	@Override
	public void delete(Occam occam, Mod303 mod303) {
		MODEL303.delete(occam, mod303);
	}
	@Override
	public String getInfo(Occam occam, Mod303 mod303, IModelScript<Mod303Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException {
		return MODEL303.getInfo(occam, mod303, script, infoKey);
	}

	@Override
	public Mod303 markAsSent(Occam occam, Mod303 mod303) throws AonCoreException {
		return MODEL303.markAsSent(occam, mod303);
	}

	@Override
	public Mod303 markAsCustomerCheck(Occam occam, Mod303 mod303) {
		return MODEL303.markAsCustomerCheck(occam, mod303);
	}

	@Override
	public Invoice getInvoice(Occam occam, int invoiceId) throws AonCoreException {
		return AON.getInvoice(occam, invoiceId);
	}
	
	@Override
	public Mod303 doRecord(Occam occam, Mod303 mod303) throws AonCoreException {
		return MODEL303.doRecord(occam, mod303);
	}
	@Override
	public Mod303 unrecord(Occam occam, Mod303 mod303) throws AonCoreException {
		return MODEL303.unrecord(occam, mod303);
	}
}
