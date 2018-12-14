package com.esferalia.aon.gwt.fiscal.server;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2018.Mod3902018Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018;

@WebServlet(name = "Mod3902018 Servlet", urlPatterns = { "/aon_gwt_fiscal/Mod3902018" })
public class Mod3902018ServiceImpl extends AonRemoteServiceServlet implements Mod3902018Service {

	private static final long serialVersionUID = -2916020705631202792L;

	public static Mod3902018ServiceImpl getInstance() {
		return new Mod3902018ServiceImpl();
	}
	
	@Override
	public Mod3902018 getMod3902018(String domainName, Integer domain,Mod390 mod390) {
		return FISCAL.getMod3902018(domainName, domain, this.getUserLogin(), mod390);
	}

	@Override
	public Mod3902018 saveMod3902018(String domainName, Integer domain, Mod3902018 mod390) {
		return FISCAL.saveMod3902018(domainName, domain, this.getUserLogin(), mod390);
	}

	@Override
	public void deleteMod3902018(String domainName, Integer domain, Mod3902018 mod390) {
		FISCAL.deleteMod3902018(domainName, domain, this.getUserLogin(), mod390);
	}

	@Override
	public Mod3902018 changeStatus(String domainName, Mod3902018 mod390, FiscalStatus status) {
		return FISCAL.changeStatusMod3902018(domainName, this.getUserLogin(), mod390, status);
	}
	
	public Mod3902018 changeStatus(String domainName, String user, Mod3902018 mod390, FiscalStatus status) {
		return FISCAL.changeStatusMod3902018(domainName, user, mod390, status);
	}
	
	@Override
	public Integer presentationFile(String domainName, Integer domainId, String user,Integer id) {
		Attach attach = AON.getAttach(domainName, domainId, user, f -> 
		f.getSourceTypeProperty().eq(DataAttachSource.MOD131.value())
		.and(f.getSourceBatchProperty().eq(id))
		.and(f.getDescriptionProperty().eq("Presentacion AEAT")), AttachType.DATA, false);
		return attach != null && attach.getId() != null ? attach.getId() :  -1;
	}
	
}
