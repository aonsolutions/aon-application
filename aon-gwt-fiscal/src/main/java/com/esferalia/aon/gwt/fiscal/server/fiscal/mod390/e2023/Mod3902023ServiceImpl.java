package com.esferalia.aon.gwt.fiscal.server.fiscal.mod390.e2023;

import jakarta.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2023.Mod3902023Service;
import com.esferalia.aon.occam.api.fiscal.MODEL3902023;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902023;

@WebServlet(name = "Mod3902023 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod3902023", "/aon_gwt_mod200/ms/Mod3902023" })
public class Mod3902023ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod3902023Service {

	private static final long serialVersionUID = -2916020705631202792L;

	public static Mod3902023ServiceImpl getInstance() {
		return new Mod3902023ServiceImpl();
	}
	
	@Override
	public Mod3902023 get(Occam occam,Mod390 mod390) {
		return MODEL3902023.get(occam, mod390);
	}

	@Override
	public Mod3902023 save(Occam occam, Mod3902023 mod390) {
		return MODEL3902023.save(occam, mod390);
	}

	@Override
	public void delete(Occam occam, Mod3902023 mod390) {
		MODEL3902023.delete(occam, mod390);
	}

	@Override
	public Mod3902023 changeStatus(Occam occam, Mod3902023 mod390, FiscalStatus status) {
		return MODEL3902023.changeStatus(occam, mod390, status);
	}
	
}
