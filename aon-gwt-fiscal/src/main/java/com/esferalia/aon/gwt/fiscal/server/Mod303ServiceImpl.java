package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod303.Mod303Service;
import com.esferalia.aon.gwt.fiscal.server.util.AONMVELUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod303 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod303" })
public class Mod303ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod303Service {

	private static final long serialVersionUID = -1101706717961420535L;

	// ---------------------------------------------------------------MODELO 303
	@Override
	public Mod303 getMod303(String domainName, String user, int domain,int id) throws AonCoreException {
		return FISCAL.getMod303(domainName, domain, user, id);
	}

	@Override
	public LinkedList<Mod303> getMod303s(String domainName, String user, int domain) throws AonCoreException {
		return FISCAL.getMod303s(domainName, domain, user);
	}

	@Override
	public Mod303 calculate(String domainName, String user, Mod303 mod303) {
		return FISCAL.calculate(domainName, user, mod303);
	}

	@Override
	public Mod303 save(String domainName, String user, Mod303 mod303) {
		return FISCAL.save(domainName, user, mod303);
	}

	@Override
	public Mod303 saveComments(String domainName, String user, Mod303 mod303) {
		return FISCAL.saveComments(domainName, user, mod303);
	}

	@Override
	public Mod303 initializeForFinish(String domainName, String user, Mod303 mod303) {
		return FISCAL.initializeForFinish(domainName, user, mod303);
	}

	@Override
	public Mod303 markAsFinished(String domainName, String user, Mod303 mod303) {
		return FISCAL.finish(domainName, user, mod303);
	}

	@Override
	public Mod303 markAsPending(String domainName, String user, Mod303 mod303) {
		return FISCAL.reopen(domainName, user, mod303);
	}

	@Override
	public Mod303 initialize(String domainName, String user, int domain, Mod303 mod303) {
		return FISCAL.initializeMod303(domainName, domain, user, mod303);
	}

	@Override
	public Mod303 create(String domainName, String user, int domain, Mod303 mod303) {
		return FISCAL.createMod303(domainName, domain, user, mod303);
	}
	@Override
	public Mod303 declarationChanged(String domainName, String user, int domain, Mod303 mod303) throws AonCoreException {
		return FISCAL.declarationChanged(domainName, domain, user, mod303);
	}
	@Override
	public void delete(String domainName, String user, Mod303 mod303) {
		FISCAL.deleteMod303(domainName, user, mod303);
	}
	@Override
	public String getInfo(String domainName, String user, int domain, Mod303 mod303, IModelScript<Mod303Key> script, FiscalModelKeyInfo infoKey)
			throws AonCoreException {
		return FISCAL.getMod303Info(domainName, domain, user, mod303, script, infoKey);
		
	}

	@Override
	public void importMod303(String domainName, String user, int domain) throws AonCoreException {
		FISCAL.importMod303(domainName, domain, user);
	}

	@Override
	public Mod303 markAsSent(String domainName, String user, Mod303 mod303) throws AonCoreException {
		return FISCAL.markAsSent(domainName, mod303, user);
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
			f.getSourceTypeProperty().eq(DataAttachSource.MOD303.value())
			.and(f.getSourceBatchProperty().eq(id))
			.and(f.getDescriptionProperty().eq("Validacion AEAT")), AttachType.DATA, false);
		return attach != null && attach.getId() != null ? attach.getId() :  -1;
	}
	
	@Override
	public Integer presentationFile(String domainName, Integer domainId, String user,Integer id) {
		Attach attach = AON.getAttach(domainName, domainId, user, f -> 
		f.getSourceTypeProperty().eq(DataAttachSource.MOD303.value())
		.and(f.getSourceBatchProperty().eq(id))
		.and(f.getDescriptionProperty().eq("Presentacion AEAT")), AttachType.DATA, false);
		return attach != null && attach.getId() != null ? attach.getId() :  -1;
	}
	
}
