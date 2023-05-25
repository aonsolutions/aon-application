package com.esferalia.aon.gwt.fiscal.server.fiscal.mod111;

import java.util.LinkedList;

import jakarta.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod111.Mod111Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.fiscal.MODEL111;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod111 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod111" })
public class Mod111ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod111Service {

	private static final long serialVersionUID = 4871750281617969066L;

	public static Mod111ServiceImpl getInstance() {
		return new Mod111ServiceImpl();
	}
	@Override
	public Mod111 getMod111(Occam occam, int id) throws AonCoreException {
		return MODEL111.get(occam, id);
	}

	@Override
	public LinkedList<Mod111> getMod111s(Occam occam) throws AonCoreException {
		return MODEL111.getMod111s(occam);
	}

	@Override
	public Mod111 calculate(Occam occam, Mod111 mod111) {
		return MODEL111.calculate(occam, mod111);
	}

	@Override
	public Mod111 save(Occam occam, Mod111 mod111) {
		return MODEL111.save(occam, mod111);
	}

	@Override
	public Mod111 saveComments(Occam occam, Mod111 mod111) {
		return MODEL111.saveComments(occam, mod111);
	}

	@Override
	public Mod111 initializeForFinish(Occam occam, Mod111 mod111) {
		return MODEL111.initializeForFinish(occam, mod111);
	}

	@Override
	public Mod111 markAsFinished(Occam occam, Mod111 mod111) {
		return MODEL111.markAsFinished(occam, mod111);
	}
	@Override
	public Mod111 markAsCustomerCheck(Occam occam, Mod111 mod111) throws AonCoreException {
		return MODEL111.markAsCustomerCheck(occam, mod111);
	}

	@Override
	public Mod111 markAsPending(Occam occam, Mod111 mod111) {
		return MODEL111.markAsPending(occam, mod111);
	}

	@Override
	public Mod111 markAsSent(Occam occam, Mod111 mod111) throws AonCoreException {
		return MODEL111.markAsSent(occam, mod111);
	}

	@Override
	public Mod111 initialize(Occam occam, Mod111 mod111) {
		return MODEL111.initialize(occam, mod111);
	}

	@Override
	public Mod111 create(Occam occam, Mod111 mod111) {
		return MODEL111.create(occam, mod111);
	}

	@Override
	public void delete(Occam occam, Mod111 mod111) {
		MODEL111.delete(occam, mod111);
	}
	@Override
	public String getInfo(Occam occam, Mod111 mod111, IModelScript<Mod111Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException {
		return MODEL111.getInfo(occam, mod111, script, infoKey);
	}
	
	@Override
	public Invoice getInvoice(Occam occam, int invoiceId) throws AonCoreException {
		return AON.getInvoice(occam, invoiceId);
	}
}
