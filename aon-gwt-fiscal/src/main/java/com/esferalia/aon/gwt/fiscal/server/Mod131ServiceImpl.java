package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod131.Mod131Service;
import com.esferalia.aon.gwt.fiscal.server.util.AONMVELUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod131 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod131" })
public class Mod131ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod131Service {

	private static final long serialVersionUID = -19020429855497195L;

	public static Mod131ServiceImpl getInstance() {
		return new Mod131ServiceImpl();
	}
	
	@Override
	public Mod131 getMod131(String domainName, String user, int domain,int id) throws AonCoreException {
		return FISCAL.getMod131(domainName, domain, user, id);
	}

	@Override
	public LinkedList<Mod131> getMod131s(String domainName, String user, int domain) throws AonCoreException {
		return FISCAL.getMod131s(domainName, domain, user);
	}

	@Override
	public Mod131 calculate(String domainName, String user, Mod131 mod131) {
		return FISCAL.calculate(domainName, user, mod131);
	}
	@Override
	public Mod131Activity calculateActivity(String domainName, String user, int domain, Mod131 mod131, Mod131Activity activity) throws AonCoreException {
		return FISCAL.calculate(domainName, domain, user, mod131, activity);
	}

	@Override
	public Mod131 save(String domainName, String user, Mod131 mod131) {
		return FISCAL.save(domainName, user, mod131);
	}

	@Override
	public Mod131 saveComments(String domainName, String user, Mod131 mod131) {
		return FISCAL.saveComments(domainName, user, mod131);
	}

	@Override
	public Mod131 initializeForFinish(String domainName, String user, Mod131 mod131) {
		return FISCAL.initializeForFinish(domainName, user, mod131);
	}

	@Override
	public Mod131 markAsFinished(String domainName, String user, Mod131 mod131) {
		return FISCAL.finish(domainName, user, mod131);
	}
	@Override
	public Mod131 markAsSent(String domainName, String user, Mod131 mod131) {
		return FISCAL.markAsSent(domainName, user, mod131);
	}

	@Override
	public Mod131 markAsPending(String domainName, String user, Mod131 mod131) {
		return FISCAL.reopen(domainName, user, mod131);
	}

	@Override
	public Mod131 initialize(String domainName, String user, int domain, Mod131 mod131) {
		return FISCAL.initializeMod131(domainName, domain, user, mod131);
	}

	@Override
	public Mod131 create(String domainName, String user, int domain, Mod131 mod131) {
		return FISCAL.createMod131(domainName, domain, user, mod131);
	}

	@Override
	public void delete(String domainName, String user, Mod131 mod131) {
		FISCAL.deleteMod131(domainName, user, mod131);
	}
	@Override
	public String getInfo(String domainName, String user, int domain, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException {
		return FISCAL.getMod131Info(domainName, domain, user, mod131, script, infoKey);
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
	public Integer presentationFile(String domainName, Integer domainId, String user,Integer id) {
		Attach attach = AON.getAttach(domainName, domainId, user, f -> 
		f.getSourceTypeProperty().eq(DataAttachSource.MOD131.value())
		.and(f.getSourceBatchProperty().eq(id))
		.and(f.getDescriptionProperty().eq("Presentacion AEAT")), AttachType.DATA, false);
		return attach != null && attach.getId() != null ? attach.getId() :  -1;
	}
}
