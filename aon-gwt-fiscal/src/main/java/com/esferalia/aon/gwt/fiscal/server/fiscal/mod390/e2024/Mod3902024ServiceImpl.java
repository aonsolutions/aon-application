package com.esferalia.aon.gwt.fiscal.server.fiscal.mod390.e2024;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2024.Mod3902024Service;
import com.esferalia.aon.occam.api.fiscal.MODEL3902024;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902024;

import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "Mod3902024 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod3902024", "/aon_gwt_mod200/ms/Mod3902024" })
public class Mod3902024ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod3902024Service {

	private static final long serialVersionUID = -1082572246027803070L;

	public static Mod3902024ServiceImpl getInstance() {
		return new Mod3902024ServiceImpl();
	}
	
	@Override
	public Mod3902024 get(Occam occam,Mod390 mod390) {
		return MODEL3902024.get(occam, mod390);
	}

	@Override
	public Mod3902024 save(Occam occam, Mod3902024 mod390) {
		return MODEL3902024.save(occam, mod390);
	}

	@Override
	public void delete(Occam occam, Mod3902024 mod390) {
		MODEL3902024.delete(occam, mod390);
	}

	@Override
	public Mod3902024 changeStatus(Occam occam, Mod3902024 mod390, FiscalStatus status) {
		return MODEL3902024.changeStatus(occam, mod390, status);
	}
	
}
