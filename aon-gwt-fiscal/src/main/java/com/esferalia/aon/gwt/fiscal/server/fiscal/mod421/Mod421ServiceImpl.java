package com.esferalia.aon.gwt.fiscal.server.fiscal.mod421;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod421.Mod421Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.fiscal.MODEL421;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod421Key;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "Mod421 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod421", "/aon_gwt_mod200/ms/Mod421" })
public class Mod421ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod421Service {

	private static final long serialVersionUID = -8061057875397540297L;
	
	public static Mod421ServiceImpl getInstance() {
		return new Mod421ServiceImpl();
	}
	
	@Override
	public Mod421 getMod421(Occam occam, int id) throws AonCoreException {
		return MODEL421.get(occam, id);
	}

	@Override
	public LinkedList<Mod421> getMod421s(Occam occam) throws AonCoreException {
		return MODEL421.getMod421s(occam);
	}

	@Override
	public Mod421 calculate(Occam occam, Mod421 mod421) {
		return MODEL421.calculate(occam, mod421);
	}

	@Override
	public Mod421 save(Occam occam, Mod421 mod421) {
		return MODEL421.save(occam, mod421);
	}

	@Override
	public Mod421 saveComments(Occam occam, Mod421 mod421) {
		return MODEL421.saveComments(occam, mod421);
	}

	@Override
	public Mod421 initializeForFinish(Occam occam, Mod421 mod421) {
		return MODEL421.initializeForFinish(occam, mod421);
	}

	@Override
	public Mod421 markAsFinished(Occam occam, Mod421 mod421) {
		return MODEL421.markAsFinished(occam, mod421);
	}

	@Override
	public Mod421 markAsPending(Occam occam, Mod421 mod421) {
		return MODEL421.markAsPending(occam, mod421);
	}

	@Override
	public Mod421 initialize(Occam occam, Mod421 mod421) {
		return MODEL421.initialize(occam, mod421);
	}

	@Override
	public Mod421 create(Occam occam, Mod421 mod421) {
		return MODEL421.create(occam, mod421);
	}
	@Override
	public void delete(Occam occam, Mod421 mod421) {
		MODEL421.delete(occam, mod421);
	}
	@Override
	public String getInfo(Occam occam, Mod421 mod421, IModelScript<Mod421Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException {
		return MODEL421.getInfo(occam, mod421, script, infoKey);
	}

	@Override
	public Mod421 markAsSent(Occam occam, Mod421 mod421) throws AonCoreException {
		return MODEL421.markAsSent(occam, mod421);
	}

	@Override
	public Mod421 markAsCustomerCheck(Occam occam, Mod421 mod421) {
		return MODEL421.markAsCustomerCheck(occam, mod421);
	}

	@Override
	public Invoice getInvoice(Occam occam, int invoiceId) throws AonCoreException {
		return AON.getInvoice(occam, invoiceId);
	}
	
	@Override
	public Mod421 doRecord(Occam occam, Mod421 mod421) throws AonCoreException {
		return MODEL421.doRecord(occam, mod421);
	}
	@Override
	public Mod421 unrecord(Occam occam, Mod421 mod421) throws AonCoreException {
		return MODEL421.unrecord(occam, mod421);
	}
}
