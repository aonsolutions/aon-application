package com.esferalia.aon.gwt.fiscal.server;

import static com.esferalia.aon.gwt.common.server.AonServletUtils.commit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.disableAutoCommit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.enableAutoCommit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.getConnection;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.rollback;

import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import com.code.aon.accounting.util.AccountingUtil;
import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.gwt.common.shared.FiscalParameters;
import com.esferalia.aon.gwt.common.sql.SQLAppParams;
import com.esferalia.aon.gwt.common.sql.SQLUtils;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.server.Activities.Type1Activities;
import com.esferalia.aon.gwt.fiscal.server.Activities.Type2Activities;
import com.esferalia.aon.gwt.fiscal.server.Activities.Type3Activities;
import com.esferalia.aon.gwt.fiscal.server.Activities.Type4Activities;
import com.esferalia.aon.gwt.fiscal.server.Activities.Type7Activities;
import com.esferalia.aon.gwt.fiscal.server.Activities.TypeActivity;
import com.esferalia.aon.gwt.fiscal.shared.Activity;
import com.esferalia.aon.gwt.fiscal.shared.Enterprise;
import com.esferalia.aon.gwt.fiscal.shared.Mod303Results;
import com.esferalia.aon.gwt.fiscal.shared.Mod311Results;
import com.esferalia.aon.gwt.fiscal.shared.Mod390;
import com.esferalia.aon.gwt.fiscal.shared.Mod390Detail;
import com.esferalia.aon.gwt.fiscal.sql.SQLEnterprise;
import com.esferalia.aon.gwt.fiscal.sql.SQLMod390;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Mod180;
import com.esferalia.aon.occam.api.model.Mod180Detail;
import com.esferalia.aon.occam.api.model.Mod190;
import com.esferalia.aon.occam.api.model.Mod190Detail;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
@WebServlet(name = "Fiscal Servlet", urlPatterns = { "/aon_gwt_fiscal/Fiscal" })
public class FiscalServiceImpl extends AonRemoteServiceServlet implements
		FiscalService {

	// ------------------------------------------------------- FISCAL PARAMETERS
	@Override
	public FiscalParameters getFiscalParameters(int domain) throws AonSQLException {
		Connection conn = null;
		FiscalParameters fiscalParams = null;
		try {
			conn = getConnection();
			DSLContext dsl = DSL.using(conn, AccountingUtil.getDefaultSettings());
			fiscalParams = SQLAppParams.getFiscalParameters(dsl,domain);
			return fiscalParams;
		} catch (Throwable e) {
			throw new AonSQLException(e);
		} finally {
			SQLUtils.closeQuietly(conn);
		}
	}

	// -------------------------------------------------------------- ENTERPRISE
	@Override
	public ArrayList<Enterprise> getEnterprises(int domain, String query)
			throws AonSQLException {
		Connection conn = null;
		ArrayList<Enterprise> list = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			list = SQLEnterprise.getEnterprises(domain, query, conn);
			commit(conn);
			return list;
		} catch (AonSQLException e) {
			rollback(conn);
			throw e;
		} catch (Throwable e) {
			rollback(conn);
			throw new AonSQLException(e);
		} finally {
			enableAutoCommit(conn);
			SQLUtils.closeQuietly(conn);
		}
	}

	// -------------------------------------------------------------- ACTIVITIES
	@Override
	public ArrayList<Activity> getActivities(int activityGroup) throws AonSQLException {
		ArrayList<Activity> list = new ArrayList<Activity>();
		TypeActivity[] types = null;
		if (activityGroup == 0) {
			types = Type1Activities.values();
		} if (activityGroup == 1) {
			types = Type2Activities.values();
		} if (activityGroup == 2) {
			types = Type3Activities.values();
		} if (activityGroup == 3) {
			types = Type4Activities.values();
		} if (activityGroup == 6) {
			types = Type7Activities.values();
		}
		if (types == null) {
			throw new AonSQLException("Grupo de actividad no soportado " + activityGroup );
		}
		Activity a;
		for (TypeActivity type : types) {
			a = new Activity();
			a.setEpigraph(type.getEpigraph());
			a.setDescription(type.getLiteral());
			list.add(a);
		}
		return list;
	}
	
	// ---------------------------------------------------------------MODELO 190
	@Override
	public ArrayList<Mod190> getMod190s(String domainName, int domain) {
		return AON.getMod190s(domainName, domain);
	}

	@Override
	public void deleteMod190(String domainName, int domain, Mod190 mod190) {
		AON.deleteMod190(domainName, domain, mod190);
	}

	@Override
	public Mod190 saveMod190(String domainName, int domain,Mod190 mod190) {
		return AON.saveMod190(domainName, domain, mod190);
	}


	@Override
	public Mod190 getMod190(String domainName, int domain, Integer id) {
		return AON.getMod190(domainName, domain, id);
	}

	@Override
	public Mod190Detail getMod190Detail(String domainName, int domain, Integer id) {
		return AON.getMod190Detail(domainName, domain, id);
	}

	// ---------------------------------------------------------------MODELO 180
	@Override
	public ArrayList<Mod180> getMod180s(String domainName, int domain) {
		return AON.getMod180s(domainName, domain);
	}

	@Override
	public void deleteMod180(String domainName, int domain, Mod180 mod180){
		AON.deleteMod180(domainName, domain, mod180);
	}

	@Override
	public Mod180 saveMod180(String domainName, int domain,Mod180 mod180) {
		return AON.saveMod180(domainName, domain, mod180);
	}


	@Override
	public Mod180 getMod180(String domainName, int domain, Integer id) {
		return AON.getMod180(domainName, domain, id);
	}

	@Override
	public Mod180Detail getMod180Detail(String domainName, int domain, Integer id) {
		return AON.getMod180Detail(domainName, domain, id);
	}

	// ---------------------------------------------------------------MODELO 390
	@Override
	public ArrayList<Mod390Detail> getMod390Details(int domain, Integer year)
			throws AonSQLException {
		Connection conn = null;
		ArrayList<Mod390Detail> list = new ArrayList<Mod390Detail>();
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			list = SQLMod390.getMod390Details(domain,year,conn);
			commit(conn);
			return list;
		} catch (AonSQLException e) {
			rollback(conn);
			throw e;
		} catch (Throwable e) {
			rollback(conn);
			throw new AonSQLException(e);
		} finally {
			enableAutoCommit(conn);
			SQLUtils.closeQuietly(conn);
		}
	}
	
	@Override
	public Mod303Results getMod303Results(int domain, int year)
			throws AonSQLException {
		Connection conn = null;
		Mod303Results result = new Mod303Results();
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			result = SQLMod390.getMod303Results(domain,year,conn);
			commit(conn);
			return result;
		} catch (AonSQLException e) {
			rollback(conn);
			throw e;
		} catch (Throwable e) {
			rollback(conn);
			throw new AonSQLException(e);
		} finally {
			enableAutoCommit(conn);
			SQLUtils.closeQuietly(conn);
		}
	}

	@Override
	public ArrayList<Mod311Results> getMod311Results(int domain, int year)
			throws AonSQLException {
		Connection conn = null;
		ArrayList<Mod311Results> result = new ArrayList<Mod311Results>();
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			result = SQLMod390.getMod311Results(domain,year,conn);
			commit(conn);
			return result;
		} catch (AonSQLException e) {
			rollback(conn);
			throw e;
		} catch (Throwable e) {
			rollback(conn);
			throw new AonSQLException(e);
		} finally {
			enableAutoCommit(conn);
			SQLUtils.closeQuietly(conn);
		}
	}

	@Override
	public Mod390 getMod390(Integer id) throws AonSQLException {
		Connection conn = null;
		Mod390 mod390 = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			mod390 = SQLMod390.getById(id, conn);
			commit(conn);
			return mod390;
		} catch (AonSQLException e) {
			rollback(conn);
			throw e;
		} catch (Throwable e) {
			rollback(conn);
			throw new AonSQLException(e);
		} finally {
			enableAutoCommit(conn);
			SQLUtils.closeQuietly(conn);
		}
	}

	@Override
	public ArrayList<Mod390> getMod390s(int domain)
			throws AonSQLException {
		Connection conn = null;
		ArrayList<Mod390> list = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			list = SQLMod390.getByDomain(domain, conn);
			commit(conn);
			return list;
		} catch (AonSQLException e) {
			rollback(conn);
			throw e;
		} catch (Throwable e) {
			rollback(conn);
			throw new AonSQLException(e);
		} finally {
			enableAutoCommit(conn);
			SQLUtils.closeQuietly(conn);
		}
	}
	
	@Override
	public Mod390 saveMod390(Mod390 mod390) throws AonSQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			Mod390 ret = SQLMod390.save(conn, mod390);
			commit(conn);
			return ret;
		} catch (AonSQLException e) {
			rollback(conn);
			throw e;
		} catch (Throwable e) {
			rollback(conn);
			throw new AonSQLException(e);
		} finally {
			enableAutoCommit(conn);
			SQLUtils.closeQuietly(conn);
		}
	}
	
	@Override
	public void deleteMod390(Mod390 mod390) throws AonSQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			SQLMod390.delete(conn, mod390);
			commit(conn);
		} catch (AonSQLException e) {
			rollback(conn);
			throw e;
		} catch (Throwable e) {
			rollback(conn);
			throw new AonSQLException(e);
		} finally {
			enableAutoCommit(conn);
			SQLUtils.closeQuietly(conn);
		}
	}

}
