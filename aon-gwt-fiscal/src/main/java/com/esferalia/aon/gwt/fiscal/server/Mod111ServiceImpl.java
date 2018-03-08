package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod111.Mod111Service;
import com.esferalia.aon.gwt.fiscal.server.util.AONMVELUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod111 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod111" })
public class Mod111ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod111Service {

	private static final long serialVersionUID = 4871750281617969066L;

	@Override
	public Mod111 getMod111(String domainName, String user, int domain,int id) throws AonCoreException {
		return FISCAL.getMod111(domainName, domain, user, id);
	}

	@Override
	public LinkedList<Mod111> getMod111s(String domainName, String user,int domain) throws AonCoreException {
		return FISCAL.getMod111s(domainName, domain, user);
	}

	@Override
	public Mod111 calculate(String domainName, String user, Mod111 mod111) {
		return FISCAL.calculate(domainName, user, mod111);
	}

	@Override
	public Mod111 save(String domainName, String user, Mod111 mod111) {
		return FISCAL.save(domainName, user, mod111);
	}

	@Override
	public Mod111 saveComments(String domainName, String user, Mod111 mod111) {
		return FISCAL.saveComments(domainName, user, mod111);
	}

	@Override
	public Mod111 initializeForFinish(String domainName, String user, Mod111 mod111) {
		return FISCAL.initializeForFinish(domainName, user, mod111);
	}

	@Override
	public Mod111 markAsFinished(String domainName, String user, Mod111 mod111) {
		return FISCAL.markAsFinished(domainName, user, mod111);
	}

	@Override
	public Mod111 markAsPending(String domainName, String user, Mod111 mod111) {
		return FISCAL.markAsPending(domainName, user, mod111);
	}

	@Override
	public Mod111 markAsSent(String domainName, String user, Mod111 mod111) throws AonCoreException {
		return FISCAL.markAsSent(domainName, user, mod111);
	}

	@Override
	public Mod111 initialize(String domainName, String user, int domain, Mod111 mod111) {
		return FISCAL.initializeMod111(domainName, domain, user, mod111);
	}

	@Override
	public Mod111 create(String domainName, String user, int domain, Mod111 mod111) {
		return FISCAL.createMod111(domainName, domain, user, mod111);
	}

	@Override
	public void delete(String domainName, String user, Mod111 mod111) {
		FISCAL.deleteMod111(domainName, user, mod111);
	}
	@Override
	public String getInfo(String domainName, String user, int domain, Mod111 mod111, IModelScript<Mod111Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException {
		return FISCAL.getMod111Info(domainName, domain, user, mod111, script, infoKey);
	}

	@Override
	public Double mathExpression(String expression) throws AonCoreException {
		try {
			return AONMVELUtils.mathExpression(expression);
		} catch ( Throwable t) {
			throw new AonCoreException(t);
		}
	}
	
	@Override
	public Integer validationFile(String domainName, Integer domainId, String user,Integer id) {
		Attach attach = AON.getAttach(domainName, domainId, user, f -> 
			f.getSourceTypeProperty().eq(DataAttachSource.MOD111.value())
			.and(f.getSourceBatchProperty().eq(id))
			.and(f.getDescriptionProperty().eq("Validacion AEAT")), AttachType.DATA, false);
		return attach != null && attach.getId() != null ? attach.getId() :  -1;
	}
	
	@Override
	public Integer presentationFile(String domainName, Integer domainId, String user,Integer id) {
		Attach attach = AON.getAttach(domainName, domainId, user, f -> 
		f.getSourceTypeProperty().eq(DataAttachSource.MOD111.value())
		.and(f.getSourceBatchProperty().eq(id))
		.and(f.getDescriptionProperty().eq("Presentacion AEAT")), AttachType.DATA, false);
		return attach != null && attach.getId() != null ? attach.getId() :  -1;
	}
}
