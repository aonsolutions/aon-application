package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod115.Mod115Service;
import com.esferalia.aon.gwt.fiscal.server.util.AONMVELUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod115 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod115" })
public class Mod115ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod115Service {
	
	private static final long serialVersionUID = -2628693172915571116L;

	public static Mod115ServiceImpl getInstance() {
		return new Mod115ServiceImpl();
	}
	
	@Override
	public Mod115 getMod115(String domainName, String user, int domain,int id) throws AonCoreException {
		return FISCAL.getMod115(domainName, domain, user, id);
	}

	@Override
	public LinkedList<Mod115> getMod115s(String domainName, String user, int domain) throws AonCoreException {
		return FISCAL.getMod115s(domainName, domain, user);
	}

	@Override
	public Mod115 calculate(String domainName, String user, Mod115 mod115) {
		return FISCAL.calculate(domainName, user, mod115);
	}

	@Override
	public Mod115 save(String domainName, String user, Mod115 mod115) {
		return FISCAL.save(domainName, user, mod115);
	}

	@Override
	public Mod115 saveComments(String domainName, String user, Mod115 mod115) {
		return FISCAL.saveComments(domainName, user, mod115);
	}

	@Override
	public Mod115 initializeForFinish(String domainName, String user, Mod115 mod115) {
		return FISCAL.initializeForFinish(domainName, user, mod115);
	}

	@Override
	public Mod115 markAsFinished(String domainName, String user, Mod115 mod115) {
		return FISCAL.markAsFinished(domainName, user, mod115);
	}

	@Override
	public Mod115 markAsSent(String domainName, String user, Mod115 mod115) {
		return FISCAL.markAsSent(domainName, user, mod115);
	}

	@Override
	public Mod115 markAsPending(String domainName, String user, Mod115 mod115) {
		return FISCAL.markAsPending(domainName, user, mod115);
	}

	@Override
	public Mod115 initialize(String domainName, String user, int domain, Mod115 mod115) {
		return FISCAL.initializeMod115(domainName, domain, user, mod115);
	}

	@Override
	public Mod115 create(String domainName, String user, int domain, Mod115 mod115) {
		return FISCAL.createMod115(domainName, domain, user, mod115);
	}

	@Override
	public void delete(String domainName, String user, Mod115 mod115) {
		FISCAL.deleteMod115(domainName, user, mod115);
	}
	@Override
	public String getInfo(String domainName, String user, int domain, Mod115 mod115, IModelScript<Mod115Key> script, FiscalModelKeyInfo infoKey)
			throws AonCoreException {
		return FISCAL.getMod115Info(domainName, domain, user, mod115, script, infoKey);
		
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
			f.getSourceTypeProperty().eq(DataAttachSource.MOD115.value())
			.and(f.getSourceBatchProperty().eq(id))
			.and(f.getDescriptionProperty().eq("Validacion AEAT")), AttachType.DATA, false);
		return attach != null && attach.getId() != null ? attach.getId() :  -1;
	}
	
	@Override
	public Integer presentationFile(String domainName, Integer domainId, String user,Integer id) {
		Attach attach = AON.getAttach(domainName, domainId, user, f -> 
		f.getSourceTypeProperty().eq(DataAttachSource.MOD115.value())
		.and(f.getSourceBatchProperty().eq(id))
		.and(f.getDescriptionProperty().eq("Presentacion AEAT")), AttachType.DATA, false);
		return attach != null && attach.getId() != null ? attach.getId() :  -1;
	}
}
