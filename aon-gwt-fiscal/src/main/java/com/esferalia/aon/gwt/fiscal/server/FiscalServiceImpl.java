package com.esferalia.aon.gwt.fiscal.server;

import static com.esferalia.aon.gwt.fiscal.server.AonServletUtils.commit;
import static com.esferalia.aon.gwt.fiscal.server.AonServletUtils.disableAutoCommit;
import static com.esferalia.aon.gwt.fiscal.server.AonServletUtils.enableAutoCommit;
import static com.esferalia.aon.gwt.fiscal.server.AonServletUtils.getConnection;
import static com.esferalia.aon.gwt.fiscal.server.AonServletUtils.rollback;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.annotation.WebServlet;

import com.code.aon.file.format.output.FileOutput;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.server.file.MOD180Writer;
import com.esferalia.aon.gwt.fiscal.server.file.MOD190Writer;
import com.esferalia.aon.gwt.fiscal.shared.AonSQLException;
import com.esferalia.aon.gwt.fiscal.shared.Enterprise;
import com.esferalia.aon.gwt.fiscal.shared.FiscalParameters;
import com.esferalia.aon.gwt.fiscal.shared.Mod180;
import com.esferalia.aon.gwt.fiscal.shared.Mod180Detail;
import com.esferalia.aon.gwt.fiscal.shared.Mod180Receiver;
import com.esferalia.aon.gwt.fiscal.shared.Mod190;
import com.esferalia.aon.gwt.fiscal.shared.Mod190Detail;
import com.esferalia.aon.gwt.fiscal.shared.Mod190Receiver;
import com.esferalia.aon.gwt.fiscal.sql.SQLEnterprise;
import com.esferalia.aon.gwt.fiscal.sql.SQLMod180;
import com.esferalia.aon.gwt.fiscal.sql.SQLMod190;
import com.esferalia.aon.gwt.fiscal.sql.SQLParams;
import com.esferalia.aon.gwt.fiscal.sql.SQLUtils;
import com.google.gwt.user.server.Base64Utils;

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

	@Override
	public String generateMod190File(Integer id, int year, int administration)
			throws AonSQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			MOD190Writer writer = new MOD190Writer();
			FileOutput fileoutput = writer.createMOD190(conn, id, year, administration);
			commit(conn);
			
			FileInputStream in = new FileInputStream(fileoutput.getFile());
			StringWriter out = new StringWriter();
			encodeURIComponent("application/octet-stream", in, out);
			return out.toString();
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

	@Override
	public String generateMod180File(Integer id, int year, int administration)
			throws AonSQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			MOD180Writer writer = new MOD180Writer();
			FileOutput fileoutput = writer.createMOD180(conn, id, year, administration);
			commit(conn);
			
			FileInputStream in = new FileInputStream(fileoutput.getFile());
			StringWriter out = new StringWriter();
			encodeURIComponent("application/octet-stream", in, out);
			return out.toString();
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
	static void encodeURIComponent(String mime, InputStream is, Writer writer ) 
	throws IOException {
		// data:[<MIME-type>][;charset=<encoding>][;base64],<data>
		writer.write("data:");
		writer.write(mime);
		writer.write(";base64,");
		int read = 0; 
		byte buffer [] = new byte [3 * 50];
		while ( ( read = is.read(buffer) ) > 0 ) {
			byte data [] = Arrays.copyOfRange(buffer, 0, read);
			
			String safe = Base64Utils.toBase64(data);
			String base64 = safe.replace('$', '+');
			base64 = base64.replace('_', '/');
			
			writer.write(base64);
		}
		
		
	}
}
