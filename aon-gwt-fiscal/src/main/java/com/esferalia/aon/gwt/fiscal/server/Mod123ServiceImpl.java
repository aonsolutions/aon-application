package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod123.Mod123Service;
import com.esferalia.aon.gwt.fiscal.server.util.AONMVELUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod123 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod123" })
public class Mod123ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod123Service {

	private static final long serialVersionUID = 9086155747294281837L;

	public static Mod123ServiceImpl getInstance() {
		return new Mod123ServiceImpl();
	}
	
	@Override
	public Mod123 getMod123(String domainName,String userLogin, int domain,int id) throws AonCoreException {
		return FISCAL.getMod123(domainName, domain, userLogin, id);	
	}

	@Override
	public LinkedList<Mod123> getMod123s(String domainName,String userLogin, int domain) throws AonCoreException {
		try {
			return FISCAL.getMod123s(domainName, domain, userLogin);
		} catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public Mod123 calculate(String domainName, String userLogin, Mod123 mod123) {
		return FISCAL.calculate(domainName, userLogin, mod123);
	}

	@Override
	public Mod123 save(String domainName, String userLogin, Mod123 mod123) {
		return FISCAL.save(domainName, userLogin, mod123);
	}

	@Override
	public Mod123 saveComments(String domainName, String userLogin, Mod123 mod123) {
		return FISCAL.saveComments(domainName, userLogin, mod123);
	}

	@Override
	public Mod123 initializeForFinish(String domainName, String userLogin, Mod123 mod123) {
		return FISCAL.initializeForFinish(domainName, userLogin, mod123);
	}

	@Override
	public Mod123 markAsFinished(String domainName, String userLogin, Mod123 mod123) {
		return FISCAL.markAsFinished(domainName, userLogin, mod123);
	}

	@Override
	public Mod123 markAsSent(String domainName, String userLogin, Mod123 mod123) {
		return FISCAL.markAsSent(domainName, userLogin, mod123);
	}
	@Override
	public Mod123 markAsCustomerCheck(String domainName, String userLogin, Mod123 mod123) throws AonCoreException {
		return FISCAL.markAsCustomerCheck(domainName, userLogin, mod123);
	}

	@Override
	public Mod123 markAsPending(String domainName, String userLogin, Mod123 mod123) {
		return FISCAL.markAsPending(domainName, userLogin, mod123);
	}

	@Override
	public Mod123 initialize(String domainName, String userLogin, int domain, Mod123 mod123) {
		return FISCAL.initializeMod123(domainName, domain, userLogin, mod123);
	}

	@Override
	public Mod123 create(String domainName, String userLogin, int domain, Mod123 mod123) {
		return FISCAL.createMod123(domainName, domain, userLogin, mod123);
	}

	@Override
	public void delete(String domainName, String userLogin, Mod123 mod123) {
		FISCAL.deleteMod123(domainName, userLogin, mod123);
	}
	@Override
	public String getInfo(String domainName, String userLogin, int domain, Mod123 mod123, IModelScript<Mod123Key> script, FiscalModelKeyInfo infoKey)
			throws AonCoreException {
		return FISCAL.getMod123Info(domainName, domain, userLogin, mod123, script, infoKey);
		
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
			f.getSourceTypeProperty().eq(DataAttachSource.MOD123.value())
			.and(f.getSourceBatchProperty().eq(id))
			.and(f.getDescriptionProperty().eq("Validacion AEAT")), AttachType.DATA, false);
		return attach != null && attach.getId() != null ? attach.getId() :  -1;
	}
	
	@Override
	public Integer presentationFile(String domainName, Integer domainId, String user,Integer id) {
		Attach attach = AON.getAttach(domainName, domainId, user, f -> 
		f.getSourceTypeProperty().eq(DataAttachSource.MOD123.value())
		.and(f.getSourceBatchProperty().eq(id))
		.and(f.getDescriptionProperty().eq("Presentacion AEAT")), AttachType.DATA, false);
		return attach != null && attach.getId() != null ? attach.getId() :  -1;
	}
}
