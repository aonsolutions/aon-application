package com.esferalia.aon.gwt.fiscal.server.fiscal.mod390;

import jakarta.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2018.Mod3902018Service;
import com.esferalia.aon.occam.api.fiscal.MODEL3902018;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902018;

@WebServlet(name = "Mod3902018 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod3902018", "/aon_gwt_mod200/ms/Mod3902018" })
public class Mod3902018ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod3902018Service {

	private static final long serialVersionUID = -2916020705631202792L;

	public static Mod3902018ServiceImpl getInstance() {
		return new Mod3902018ServiceImpl();
	}
	
	@Override
	public Mod3902018 get(Occam occam,Mod390 mod390) {
		return MODEL3902018.get(occam, mod390);
	}

	@Override
	public Mod3902018 save(Occam occam, Mod3902018 mod390) {
		return MODEL3902018.save(occam, mod390);
	}

	@Override
	public void delete(Occam occam, Mod3902018 mod390) {
		MODEL3902018.delete(occam, mod390);
	}

	@Override
	public Mod3902018 changeStatus(Occam occam, Mod3902018 mod390, FiscalStatus status) {
		return MODEL3902018.changeStatus(occam, mod390, status);
	}
	
}
