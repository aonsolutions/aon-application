package com.esferalia.aon.dsi;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.sql.Connection;

import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.TransactionalRunnable;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.code.aon.audit.enumeration.Module;
import com.code.aon.dbutils.AonSQLException;
import com.esferalia.aon.jooq.tables.Domain;

public class DSI2AON {

	private static class RollBackException extends Exception {

	}

	private boolean commit;

	private Connection aonConn;

	private DSLContext dsiContext;
	private DSLContext aonContext;

	public DSI2AON(Connection dsiConn, Connection aonConn) {
		this.aonConn = aonConn;
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		this.dsiContext = DSL.using(dsiConn, settings);
		this.aonContext = DSL.using(aonConn, SQLDialect.MYSQL, settings);

	}

	public DSI2AON setCommit(boolean commit) {
		this.commit = commit;
		return this;
	}

	public void run(final String parentDomain, final String domainSuffix,
			final String owner, final Condition... conditions)
			throws AonSQLException {
		run(getDomain(parentDomain), domainSuffix, owner, conditions);
	}

	public void run(final Integer parentDomain, final String domainSuffix,
			final String owner, final Condition... conditions)
			throws AonSQLException {

		try {

			aonContext.transaction(new TransactionalRunnable() {

				@Override
				public void run(Configuration configuration) throws Exception {

					CconceLoader cconceLoader = new CconceLoader(dsiContext,
							aonContext);
					cconceLoader.load(parentDomain, conditions);

					ConvenLoader convenLoader = new ConvenLoader(dsiContext,
							aonContext);
					convenLoader.loadConven(parentDomain, cconceLoader,
							conditions);

					EmpresLoader empresLoader = new EmpresLoader(dsiContext,
							aonContext);
					empresLoader.loadEmpres(parentDomain, domainSuffix, owner,
							convenLoader, conditions);
					Trabaj2Loader trabaj2Loader = new Trabaj2Loader(dsiContext,
							aonContext);
					trabaj2Loader.loadTrabj2(empresLoader, conditions);

					NominaLoader nominaLoader = new NominaLoader(dsiContext,
							aonContext);
					nominaLoader.loadNominc(trabaj2Loader, conditions);

					UserLoader userLoader = new UserLoader(dsiContext,
							aonContext);
					int domainApplication = userLoader.loadModules(
							parentDomain, Module.CONFIGURATION, Module.PAYROLL);
					userLoader.loadUser(parentDomain, domainApplication,
							"admin", "Administrador");

					cconceLoader.execute();
					convenLoader.execute();
					userLoader.execute();
					empresLoader.execute();
					trabaj2Loader.execute();
					nominaLoader.execute();

					// Rolls back the outer transaction
					if (!commit)
						throw new RollBackException();

					// Implicit commit executed here
				}
			});
		} catch (RuntimeException e) {
			try {
				throw e.getCause();
			} catch (RollBackException throwable) {
			} catch (Throwable throwable) {
				throw e;
			}

		}

	}

	private int getDomain(String name) {
		return aonContext.select(DOMAIN.ID).from(DOMAIN)
				.where(DOMAIN.NAME.eq(name)).fetchOne(DOMAIN.ID);
	}

	// ------------------------------------------------------------------------
	public static void main(String[] args) throws ClassNotFoundException {

	}
}
