package com.esferalia.aon.gwt.fiscal.server.fiscal.mod115;

import java.util.LinkedList;

import jakarta.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod115.Mod115Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.fiscal.MODEL115;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod115 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod115" })
public class Mod115ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod115Service {
	
	private static final long serialVersionUID = -2628693172915571116L;

	public static Mod115ServiceImpl getInstance() {
		return new Mod115ServiceImpl();
	}
	
	@Override
	public Mod115 getMod115(Occam occam,int id) throws AonCoreException {
		return MODEL115.get(occam, id);
	}

	@Override
	public LinkedList<Mod115> getMod115s(Occam occam) throws AonCoreException {
		return MODEL115.getMod115s(occam);
	}

	@Override
	public Mod115 calculate(Occam occam, Mod115 mod115) {
		return MODEL115.calculate(occam, mod115);
	}

	@Override
	public Mod115 save(Occam occam, Mod115 mod115) {
		return MODEL115.save(occam, mod115);
	}

	@Override
	public Mod115 saveComments(Occam occam, Mod115 mod115) {
		return MODEL115.saveComments(occam, mod115);
	}

	@Override
	public Mod115 initializeForFinish(Occam occam, Mod115 mod115) {
		return MODEL115.initializeForFinish(occam, mod115);
	}

	@Override
	public Mod115 markAsFinished(Occam occam, Mod115 mod115) {
		return MODEL115.markAsFinished(occam, mod115);
	}

	@Override
	public Mod115 markAsSent(Occam occam, Mod115 mod115) {
		return MODEL115.markAsSent(occam, mod115);
	}

	@Override
	public Mod115 markAsCustomerCheck(Occam occam, Mod115 mod115) throws AonCoreException {
		return MODEL115.markAsCustomerCheck(occam, mod115);
	}
	
	@Override
	public Mod115 markAsPending(Occam occam, Mod115 mod115) {
		return MODEL115.markAsPending(occam, mod115);
	}

	@Override
	public Mod115 initialize(Occam occam, Mod115 mod115) {
		return MODEL115.initialize(occam, mod115);
	}

	@Override
	public Mod115 create(Occam occam, Mod115 mod115) {
		return MODEL115.create(occam, mod115);
	}

	@Override
	public void delete(Occam occam, Mod115 mod115) {
		MODEL115.delete(occam, mod115);
	}
	@Override
	public String getInfo(Occam occam, Mod115 mod115, IModelScript<Mod115Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException {
		return MODEL115.getInfo(occam, mod115, script, infoKey);
	}

	@Override
	public Invoice getInvoice(Occam occam, int invoiceId) throws AonCoreException {
		return AON.getInvoice(occam, invoiceId);
	}
}
