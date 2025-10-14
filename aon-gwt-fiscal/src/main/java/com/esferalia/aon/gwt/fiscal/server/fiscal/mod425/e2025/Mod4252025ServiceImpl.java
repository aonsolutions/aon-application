package com.esferalia.aon.gwt.fiscal.server.fiscal.mod425.e2025;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod425.e2025.Mod4252025Service;
import com.esferalia.aon.occam.api.fiscal.MODEL4252025;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025;

import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "Mod4252025 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod4252025", "/aon_gwt_mod200/ms/Mod4252025" })
public class Mod4252025ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod4252025Service {

	private static final long serialVersionUID = -1230677351278210083L;

	public static Mod4252025ServiceImpl getInstance() {
		return new Mod4252025ServiceImpl();
	}
	
	@Override
	public Mod4252025 get(Occam occam, Mod390 mod425) {
		return MODEL4252025.get(occam, mod425);
	}

	@Override
	public Mod4252025 save(Occam occam, Mod4252025 mod425) {
		return MODEL4252025.save(occam, mod425);
	}

	@Override
	public void delete(Occam occam, Mod4252025 mod425) {
		MODEL4252025.delete(occam, mod425);
	}

	@Override
	public Mod4252025 changeStatus(Occam occam, Mod4252025 mod425, FiscalStatus status) {
		return MODEL4252025.changeStatus(occam, mod425, status);
	}
	
}
