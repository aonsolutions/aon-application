package com.esferalia.aon.dsi;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.sql.Connection;
import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.TransactionalRunnable;
import org.jooq.conf.Settings;
import org.jooq.exception.DetachedException;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

import com.code.aon.audit.enumeration.Module;
import net.aonsolutions.core.dbutils.AonSQLException;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.jooq.tables.records.PersonRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;

public class DSI2AON {

	public static interface Listener extends CconceLoader.Listener, Trabaj2Loader.Listener, EmpresLoader.Listener{

		void onCommited();

		void onRollbacked();

	}

	protected static class RollBackException extends Exception {

	}

	private static class Listeners implements Listener {
		private List<Listener> listeners = new LinkedList<Listener>();
		
		void add(Listener listener) {
			listeners.add(listener);
		}

		@Override
		public void onCommited() {
			for (Listener listener : listeners)
				listener.onCommited();
		}

		@Override
		public void onRollbacked() {
			for (Listener listener : listeners)
				listener.onRollbacked();
		}

		@Override
		public void onPaymentConceptUpdated(PaymentConceptRecord concept) {
			for (Listener listener : listeners)
				listener.onPaymentConceptUpdated(concept);
		}

		@Override
		public void onPaymentConceptIgnored(PaymentConceptRecord concept) {
			for (Listener listener : listeners)
				listener.onPaymentConceptIgnored(concept);
		}

		@Override
		public void onPaymentConceptInserted(PaymentConceptRecord concept) {
			for (Listener listener : listeners)
				listener.onPaymentConceptInserted(concept);
		}

		@Override
		public void onContractIgnored(ContractRecord contract, PersonRecord person) {
			for (Listener listener : listeners)
				listener.onContractIgnored(contract, person);
		}

		@Override
		public void onContractUpdated(ContractRecord contract, PersonRecord person) {
			for (Listener listener : listeners)
				listener.onContractUpdated(contract, person);
		}

		@Override
		public void onContractInserted(ContractRecord contract, PersonRecord person) {
			for (Listener listener : listeners)
				listener.onContractInserted(contract, person);
		}

		@Override
		public void onEnterpriseIgnored(RegistryRecord enterprise) {
			for (Listener listener : listeners)
				listener.onEnterpriseIgnored(enterprise);
		}

		@Override
		public void onEnterpriseUpdated(RegistryRecord enterprise) {
			for (Listener listener : listeners)
				listener.onEnterpriseUpdated(enterprise);
		}

		@Override
		public void onEnterpriseInserted(RegistryRecord enterprise) {
			for (Listener listener : listeners)
				listener.onEnterpriseInserted(enterprise);
		}

	}

	private Date from;
	private boolean commit;
	private boolean replace;

	private DSLContext dsiContext;
	private DSLContext aonContext;

	private Listeners listeners;
	
	public DSI2AON(Connection dsiConn, Connection aonConn) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		this.dsiContext = DSL.using(dsiConn, settings);

		//@formatter:off
		Configuration configuration = 
			new DefaultConfiguration()
			.set(aonConn)
			.set(settings)
			.set(SQLDialect.MYSQL)
			;
		
		this.aonContext = DSL.using(configuration);
		//@formatter:on

		// DSL.using(aonConn, SQLDialect.MYSQL, settings);
		listeners = new Listeners();

	}

	public DSI2AON(DSLContext dsiContext, DSLContext aonContext) {
		listeners = new Listeners();
		this.aonContext = aonContext;
		this.dsiContext = dsiContext;
	}

	public DSI2AON setFrom(Date from) {
		this.from = from;
		return this;
	}

	public DSI2AON setCommit(boolean commit) {
		this.commit = commit;
		return this;
	}

	public DSI2AON setReplace(boolean replace) {
		this.replace = replace;
		return this;
	}
	
	
	public DSI2AON addListener(Listener listener) {
		this.listeners.add(listener);
		return this;
	}
	
	
	public void run(final String parentDomain, final String owner,
			final Condition... conditions) throws AonSQLException {
		run(getDomain(parentDomain), getSuffix(parentDomain), owner, conditions);
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
					runImpl(parentDomain, domainSuffix, owner, conditions);
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

	// ------------------------------------------------------------------------

	protected void runImpl(final Integer parentDomain, final String domainSuffix, final String owner,
			final Condition... conditions) throws AonSQLException, RollBackException {
		//@formatter:off
		CconceLoader cconceLoader = 
			new CconceLoader(dsiContext,aonContext)
			.setReplace(replace)
			.setListener(listeners);
		//@formatter:on
		// TODO: conditions ?
		cconceLoader.load(parentDomain);

		ConvenLoader convenLoader = new ConvenLoader(dsiContext,
				aonContext);
		// TODO: conditions ?
		convenLoader.loadConven(parentDomain, cconceLoader);

		//@formatter:off
		EmpresLoader empresLoader = 
				new EmpresLoader(dsiContext,aonContext)
				.setReplace(replace)
				.setListener(listeners);
		//@formatter:on
		empresLoader.loadEmpres(parentDomain, domainSuffix, owner,
				convenLoader, conditions);
		//@formatter:off
		Trabaj2Loader trabaj2Loader = 
				new Trabaj2Loader(dsiContext,aonContext)
				.setFrom(from)
				.setReplace(replace)
				.setListener(listeners);
		//@formatter:on
		trabaj2Loader.loadTrabj2(empresLoader, conditions);

		NominaLoader nominaLoader = new NominaLoader(dsiContext,
				aonContext).setReplace(replace);
		nominaLoader.loadNominc(trabaj2Loader, conditions);

		UserLoader userLoader = new UserLoader(dsiContext,
				aonContext);
		int domainApplication = userLoader.loadModules(
				parentDomain, Module.CONFIGURATION, Module.PAYROLL);
		userLoader.loadUser(parentDomain, domainApplication,
				"admin", "Administrador");

		cconceLoader.execute();
		convenLoader.execute();
		//userLoader.execute();
		empresLoader.execute();
		trabaj2Loader.execute();
		nominaLoader.execute();

		// Rolls back the outer transaction
		if (!commit) {
			listeners.onRollbacked();
			throw new RollBackException();
		}

		// Implicit commit executed here
		listeners.onCommited();
	}

	// ------------------------------------------------------------------------

	private int getDomain(String name) {
		return aonContext.select(DOMAIN.ID)
				.from(DOMAIN)
				.where(DOMAIN.NAME.eq(name))
				.fetchOne(DOMAIN.ID);
	}

	private String getSuffix(String domain) {
		return String.format("-%s", domain);
	}


}
