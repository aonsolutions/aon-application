package com.esferalia.aon.gwt.fiscal.server.fiscal.mod123;

import java.util.LinkedList;

import jakarta.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod123.Mod123Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.fiscal.MODEL123;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod123 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod123", "/aon_gwt_mod200/ms/Mod123" })
public class Mod123ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod123Service {

	private static final long serialVersionUID = 9086155747294281837L;

	public static Mod123ServiceImpl getInstance() {
		return new Mod123ServiceImpl();
	}
	
	@Override
	public Mod123 getMod123(Occam occam,int id) throws AonCoreException {
		return MODEL123.get(occam, id);	
	}

	@Override
	public LinkedList<Mod123> getMod123s(Occam occam) throws AonCoreException {
		try {
			return MODEL123.getMod123s(occam);
		} catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public Mod123 calculate(Occam occam, Mod123 mod123) {
		return MODEL123.calculate(occam, mod123);
	}

	@Override
	public Mod123 save(Occam occam, Mod123 mod123) {
		return MODEL123.save(occam, mod123);
	}

	@Override
	public Mod123 saveComments(Occam occam, Mod123 mod123) {
		return MODEL123.saveComments(occam, mod123);
	}

	@Override
	public Mod123 initializeForFinish(Occam occam, Mod123 mod123) {
		return MODEL123.initializeForFinish(occam, mod123);
	}

	@Override
	public Mod123 markAsFinished(Occam occam, Mod123 mod123) {
		return MODEL123.markAsFinished(occam, mod123);
	}

	@Override
	public Mod123 markAsSent(Occam occam, Mod123 mod123) {
		return MODEL123.markAsSent(occam, mod123);
	}
	@Override
	public Mod123 markAsCustomerCheck(Occam occam, Mod123 mod123) throws AonCoreException {
		return MODEL123.markAsCustomerCheck(occam, mod123);
	}

	@Override
	public Mod123 markAsPending(Occam occam, Mod123 mod123) {
		return MODEL123.markAsPending(occam, mod123);
	}

	@Override
	public Mod123 initialize(Occam occam, Mod123 mod123) {
		return MODEL123.initialize(occam, mod123);
	}

	@Override
	public Mod123 create(Occam occam, Mod123 mod123) {
		return MODEL123.create(occam, mod123);
	}

	@Override
	public void delete(Occam occam, Mod123 mod123) {
		MODEL123.delete(occam, mod123);
	}
	@Override
	public String getInfo(Occam occam, Mod123 mod123, IModelScript<Mod123Key> script, FiscalModelKeyInfo infoKey)
			throws AonCoreException {
		return MODEL123.getInfo(occam, mod123, script, infoKey);
		
	}

	@Override
	public Invoice getInvoice(Occam occam, int invoiceId) throws AonCoreException {
		return AON.getInvoice(occam, invoiceId);
	}
}
