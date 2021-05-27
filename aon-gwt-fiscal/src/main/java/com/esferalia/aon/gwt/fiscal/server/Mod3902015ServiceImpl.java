package com.esferalia.aon.gwt.fiscal.server;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Mod3902015Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;

@WebServlet(name = "Mod3902015 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod3902015" })
public class Mod3902015ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod3902015Service {

	private static final long serialVersionUID = -2916020705631202792L;

	public static Mod3902015ServiceImpl getInstance() {
		return new Mod3902015ServiceImpl();
	}
	
	@Override
	public Mod3902015 getMod3902015(String domainName, Integer domain,String user,Mod390 mod390) {
		return FISCAL.getMod3902015(domainName, domain, user, mod390);
	}

	@Override
	public Mod3902015 saveMod3902015(String domainName, Integer domain,String user, Mod3902015 mod390) {
		return FISCAL.saveMod3902015(domainName, domain, user, mod390);
	}

	@Override
	public void deleteMod3902015(String domainName, Integer domain, String user, Mod3902015 mod390) {
		FISCAL.deleteMod3902015(domainName, domain, user, mod390);
	}

	@Override
	public Mod3902015 changeStatus(String domainName, String user, Mod3902015 mod390, FiscalStatus status) {
		return FISCAL.changeStatusMod3902015(domainName, user, mod390, status);
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
