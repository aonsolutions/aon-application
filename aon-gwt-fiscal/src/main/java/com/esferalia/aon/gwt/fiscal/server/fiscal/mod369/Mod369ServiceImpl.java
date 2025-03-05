package com.esferalia.aon.gwt.fiscal.server.fiscal.mod369;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod369.Model369Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.fiscal.MODEL369;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod369;
import com.esferalia.aon.occam.api.model.fiscal.Mod369Detail;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "Mod369 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod369", "/aon_gwt_mod200/ms/Mod369" })
public class Mod369ServiceImpl extends AonStatelessRemoteServiceServlet implements Model369Service {

	private static final long serialVersionUID = 1013961629055185821L;

	public static Mod369ServiceImpl getInstance() {
		return new Mod369ServiceImpl();
	}
	
	@Override
	public Mod369 initialize(Occam occam, Integer year, Period period) {
		return MODEL369.initialize(occam, year, period);
	}
	
	@Override
	public LinkedList<Mod369> getMod369s(Occam occam) {
		return MODEL369.getMod369s(occam);
	}

	@Override
	public void delete(Occam occam, Mod369 mod369) {
		MODEL369.delete(occam, mod369);
	}

	@Override
	public Mod369 save(Occam occam,Mod369 mod369) {
		return MODEL369.save(occam, mod369);
	}


	@Override
	public Mod369 get(Occam occam, Integer id) {
		return MODEL369.get(occam, id);
	}

	@Override
	public Mod369 saveComments(Occam occam, Mod369 mod369) {
		return MODEL369.saveComments(occam, mod369);
	}

	@Override
	public Mod369 changeStatus(Occam occam, Mod369 mod369, FiscalStatus newStatus) throws AonCoreException {
		return MODEL369.changeStatus(occam, mod369, newStatus);
	}
	
	@Override
	public String getInfo(Occam occam, Mod369 mod369, Mod369Detail mod369Detail, byte detailType) throws AonCoreException {
		return MODEL369.getInfo(occam, mod369, mod369Detail, detailType);
	}

	@Override
	public Invoice getInvoice(Occam occam, int invoiceId) throws AonCoreException {
		return AON.getInvoice(occam, invoiceId);
	}

}
