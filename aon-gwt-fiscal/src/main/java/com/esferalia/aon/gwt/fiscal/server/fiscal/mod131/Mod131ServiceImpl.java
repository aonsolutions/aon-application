package com.esferalia.aon.gwt.fiscal.server.fiscal.mod131;

import java.util.LinkedList;

import jakarta.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod131.Mod131Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.fiscal.MODEL131;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod131 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod131", "/aon_gwt_mod200/ms/Mod131" })
public class Mod131ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod131Service {

	private static final long serialVersionUID = -19020429855497195L;

	public static Mod131ServiceImpl getInstance() {
		return new Mod131ServiceImpl();
	}
	
	@Override
	public Mod131 get(Occam occam,int id) throws AonCoreException {
		return MODEL131.get(occam, id);
	}

	@Override
	public LinkedList<Mod131> getMod131s(Occam occam) throws AonCoreException {
		return MODEL131.getMod131s(occam);
	}

	@Override
	public Mod131 calculate(Occam occam, Mod131 mod131) {
		return MODEL131.calculate(occam, mod131);
	}
	@Override
	public Mod131Activity calculateActivity(Occam occam, Mod131 mod131, Mod131Activity activity) throws AonCoreException {
		return MODEL131.calculate(occam, mod131, activity);
	}

	@Override
	public Mod131 save(Occam occam, Mod131 mod131) {
		return MODEL131.save(occam, mod131);
	}

	@Override
	public Mod131 saveComments(Occam occam, Mod131 mod131) {
		return MODEL131.saveComments(occam, mod131);
	}

	@Override
	public Mod131 initializeForFinish(Occam occam, Mod131 mod131) {
		return MODEL131.initializeForFinish(occam, mod131);
	}

	@Override
	public Mod131 markAsFinished(Occam occam, Mod131 mod131) {
		return MODEL131.finish(occam, mod131);
	}
	@Override
	public Mod131 markAsSent(Occam occam, Mod131 mod131) {
		return MODEL131.markAsSent(occam, mod131);
	}
	@Override
	public Mod131 markAsCustomerCheck(Occam occam, Mod131 mod131) throws AonCoreException {
		return MODEL131.markAsCustomerCheck(occam, mod131);
	}
	@Override
	public Mod131 markAsPending(Occam occam, Mod131 mod131) {
		return MODEL131.reopen(occam, mod131);
	}

	@Override
	public Mod131 initialize(Occam occam, Mod131 mod131) {
		return MODEL131.initialize(occam, mod131);
	}

	@Override
	public Mod131 create(Occam occam, Mod131 mod131) {
		return MODEL131.create(occam, mod131);
	}

	@Override
	public void delete(Occam occam, Mod131 mod131) {
		MODEL131.delete(occam, mod131);
	}
	@Override
	public String getInfo(Occam occam, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException {
		return MODEL131.getInfo(occam, mod131, script, infoKey);
	}
	
	@Override
	public Invoice getInvoice(Occam occam, int invoiceId) throws AonCoreException {
		return AON.getInvoice(occam, invoiceId);
	}	
}
