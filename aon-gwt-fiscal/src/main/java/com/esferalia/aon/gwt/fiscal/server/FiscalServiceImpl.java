package com.esferalia.aon.gwt.fiscal.server;

import static com.esferalia.aon.gwt.fiscal.server.AonServletUtils.commit;
import static com.esferalia.aon.gwt.fiscal.server.AonServletUtils.disableAutoCommit;
import static com.esferalia.aon.gwt.fiscal.server.AonServletUtils.enableAutoCommit;
import static com.esferalia.aon.gwt.fiscal.server.AonServletUtils.getConnection;
import static com.esferalia.aon.gwt.fiscal.server.AonServletUtils.rollback;

import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.server.Activities.Type1Activities;
import com.esferalia.aon.gwt.fiscal.server.Activities.Type2Activities;
import com.esferalia.aon.gwt.fiscal.server.Activities.Type3Activities;
import com.esferalia.aon.gwt.fiscal.server.Activities.Type4Activities;
import com.esferalia.aon.gwt.fiscal.server.Activities.Type7Activities;
import com.esferalia.aon.gwt.fiscal.server.Activities.TypeActivity;
import com.esferalia.aon.gwt.fiscal.shared.Activity;
import com.esferalia.aon.gwt.fiscal.shared.AonSQLException;
import com.esferalia.aon.gwt.fiscal.shared.Enterprise;
import com.esferalia.aon.gwt.fiscal.shared.FiscalParameters;
import com.esferalia.aon.gwt.fiscal.shared.Mod180;
import com.esferalia.aon.gwt.fiscal.shared.Mod180Detail;
import com.esferalia.aon.gwt.fiscal.shared.Mod180Receiver;
import com.esferalia.aon.gwt.fiscal.shared.Mod190;
import com.esferalia.aon.gwt.fiscal.shared.Mod190Detail;
import com.esferalia.aon.gwt.fiscal.shared.Mod190Receiver;
import com.esferalia.aon.gwt.fiscal.shared.Mod303Results;
import com.esferalia.aon.gwt.fiscal.shared.Mod311Results;
import com.esferalia.aon.gwt.fiscal.shared.Mod390;
import com.esferalia.aon.gwt.fiscal.shared.Mod390Detail;
import com.esferalia.aon.gwt.fiscal.sql.SQLEnterprise;
import com.esferalia.aon.gwt.fiscal.sql.SQLMod180;
import com.esferalia.aon.gwt.fiscal.sql.SQLMod190;
import com.esferalia.aon.gwt.fiscal.sql.SQLMod390;
import com.esferalia.aon.gwt.fiscal.sql.SQLParams;
import com.esferalia.aon.gwt.fiscal.sql.SQLUtils;

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
			disableAutoCommit(conn);
			fiscalParams = SQLParams.getFiscalParameters(domain, conn);
			commit(conn);
			return fiscalParams;
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
	public void deleteMod190(Mod190 mod190) throws AonSQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			SQLMod190.delete(conn, mod190);
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

	@Override
	public Mod190 saveMod190(Mod190 mod190) throws AonSQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			Mod190 ret = SQLMod190.save(conn, mod190);
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
	public Mod190 saveMod190(Mod190 mod190, ArrayList<Mod190Receiver> perceptors)
			throws AonSQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			Mod190 ret = SQLMod190.save(conn, mod190, perceptors);
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
	public ArrayList<Mod190> getMod190s(int domain)
			throws AonSQLException {
		Connection conn = null;
		ArrayList<Mod190> list = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			list = SQLMod190.getByDomain(domain, conn);
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
	public Mod190 getMod190(Integer id) throws AonSQLException {
		Connection conn = null;
		Mod190 mod190 = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			mod190 = SQLMod190.getById(id, conn);
			commit(conn);
			return mod190;
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
	public ArrayList<Mod190Detail> getMod190DetailByMod190(int mod190,
			int offset, int limit) throws AonSQLException {
		Connection conn = null;
		ArrayList<Mod190Detail> list = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			list = SQLMod190.getDetailsByMod190(mod190, offset, limit, conn);
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
	public Mod190Receiver getMod190Detail(Integer id)
			throws AonSQLException {
		Connection conn = null;
		Mod190Receiver perceptor = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			perceptor = SQLMod190.getDetailById(id, conn);
			commit(conn);
			return perceptor;
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


	// ---------------------------------------------------------------MODELO 180
	@Override
	public void deleteMod180(Mod180 mod180) throws AonSQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			SQLMod180.delete(conn, mod180);
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

	@Override
	public Mod180 saveMod180(Mod180 mod180) throws AonSQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			Mod180 ret = SQLMod180.save(conn, mod180);
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
	public Mod180 saveMod180(Mod180 mod180, ArrayList<Mod180Receiver> perceptors)
			throws AonSQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			Mod180 ret = SQLMod180.save(conn, mod180, perceptors);
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
	public ArrayList<Mod180> getMod180s(int domain)
			throws AonSQLException {
		Connection conn = null;
		ArrayList<Mod180> list = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			list = SQLMod180.getByDomain(domain, conn);
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
	public Mod180 getMod180(Integer id) throws AonSQLException {
		Connection conn = null;
		Mod180 mod180 = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			mod180 = SQLMod180.getById(id, conn);
			commit(conn);
			return mod180;
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
	public ArrayList<Mod180Detail> getMod180DetailByMod180(int mod180,
			int offset, int limit) throws AonSQLException {
		Connection conn = null;
		ArrayList<Mod180Detail> list = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			list = SQLMod180.getDetailsByMod180(mod180, offset, limit, conn);
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
	public Mod180Receiver getMod180Detail(Integer id)
			throws AonSQLException {
		Connection conn = null;
		Mod180Receiver perceptor = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			perceptor = SQLMod180.getDetailById(id, conn);
			commit(conn);
			return perceptor;
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
