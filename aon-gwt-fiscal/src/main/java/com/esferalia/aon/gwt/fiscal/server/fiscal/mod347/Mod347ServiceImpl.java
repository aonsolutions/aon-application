package com.esferalia.aon.gwt.fiscal.server.fiscal.mod347;

import java.util.LinkedList;

import jakarta.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.fiscal.MODEL347;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod347 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod347", "/aon_gwt_mod200/ms/Mod347" })
public class Mod347ServiceImpl extends AonStatelessRemoteServiceServlet implements Model347Service {

	private static final long serialVersionUID = -6895893410550617969L;

	public static Mod347ServiceImpl getInstance() {
		return new Mod347ServiceImpl();
	}

	@Override
	public LinkedList<Mod347> getMod347s(Occam occam) {
		return MODEL347.getMod347s(occam);
	}

	@Override
	public Mod347 get(Occam occam, Integer id) {
		return MODEL347.get(occam, id);
	}

	@Override
	public Mod347 initialize(Occam occam, int year) {
		return MODEL347.initialize(occam, year);
	}
	@Override
	public Mod347 reset(Occam occam, Mod347 mod347) throws AonCoreException {
		return MODEL347.reset(occam, mod347);
	}

	@Override
	public Mod347 save(Occam occam,Mod347 mod347) {
		return MODEL347.save(occam, mod347);
	}

	@Override
	public void delete(Occam occam, Mod347 mod347){
		MODEL347.delete(occam, mod347);
	}

	@Override
	public Mod347 saveComments(Occam occam, Mod347 mod347) {
		return MODEL347.saveComments(occam, mod347);
	}

	@Override
	public Mod347 changeStatus(Occam occam, Mod347 mod347, FiscalStatus newStatus) throws AonCoreException {
		return MODEL347.changeStatus(occam, mod347, newStatus);
	}
	
	@Override
	public String getInfo(Occam occam, Mod347 mod347, Mod347Declared declared, FiscalModelKeyInfo infoKey) throws AonCoreException {
		return MODEL347.getInfo(occam, mod347, declared, infoKey);
	}
	
	@Override
	public Mod347 duplicate(Occam occam, Mod347 mod347) {
		return MODEL347.duplicate(occam, mod347);
	}
	
	@Override
	public Invoice getInvoice(Occam occam, int invoiceId) throws AonCoreException {
		return AON.getInvoice(occam, invoiceId);
	}

}
