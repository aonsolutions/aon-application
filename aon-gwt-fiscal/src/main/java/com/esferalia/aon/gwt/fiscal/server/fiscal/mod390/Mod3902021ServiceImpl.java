package com.esferalia.aon.gwt.fiscal.server.fiscal.mod390;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2021.Mod3902021Service;
import com.esferalia.aon.occam.api.fiscal.MODEL3902021;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902021;

@WebServlet(name = "Mod3902021 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod3902021" })
public class Mod3902021ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod3902021Service {

	private static final long serialVersionUID = -2916020705631202792L;

	public static Mod3902021ServiceImpl getInstance() {
		return new Mod3902021ServiceImpl();
	}
	
	@Override
	public Mod3902021 get(Occam occam,Mod390 mod390) {
		return MODEL3902021.get(occam, mod390);
	}

	@Override
	public Mod3902021 save(Occam occam, Mod3902021 mod390) {
		return MODEL3902021.save(occam, mod390);
	}

	@Override
	public void delete(Occam occam, Mod3902021 mod390) {
		MODEL3902021.delete(occam, mod390);
	}

	@Override
	public Mod3902021 changeStatus(Occam occam, Mod3902021 mod390, FiscalStatus status) {
		return MODEL3902021.changeStatus(occam, mod390, status);
	}
	
}
