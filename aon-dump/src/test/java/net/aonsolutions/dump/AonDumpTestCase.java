package net.aonsolutions.dump;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Proposal.PROPOSAL;
import static com.esferalia.aon.jooq.tables.ProposalDetail.PROPOSAL_DETAIL;
import static com.esferalia.aon.jooq.tables.Purchase.PURCHASE;
import static com.esferalia.aon.jooq.tables.PurchaseDetail.PURCHASE_DETAIL;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.SalesDetail.SALES_DETAIL;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.aonsolutions.core.dbutils.AonSQLException;
import com.code.aon.master.VersionManager;
import com.esferalia.aon.jooq.tables.Supplier;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.PurchaseRecord;
import com.esferalia.aon.jooq.tables.records.RaddressRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.ScopeRecord;
import com.esferalia.aon.jooq.tables.records.SupplierRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;

import junit.framework.Assert;

public class AonDumpTestCase {

	private static String dbHost;
	private static String dbPort;
	private static String dbName;
	private static String dbUser;
	private static String dbPasswd;
	private static String dbUseSSL;
	private static String dbTimezone;

	private DSLContext dslContext;

	private static Connection newConnection() throws ClassNotFoundException, SQLException, AonSQLException {
		Class.forName("com.mysql.jdbc.Driver");

		dbHost = System.getProperty("dbHost", "127.0.0.1");
		dbPort = System.getProperty("dbPort", "3306");
		dbName = System.getProperty("dbName", "aon-reveng");
		dbUser = System.getProperty("dbUser", "dbuser");
		dbPasswd = System.getProperty("dbPasswd", "serubd2000");
		dbUseSSL = System.getProperty("dbUseSSL", "false");
		dbTimezone = System.getProperty("dbTimeZone", TimeZone.getDefault().getID());

		Properties properties = new Properties();
		properties.setProperty("user", dbUser);
		properties.setProperty("password", dbPasswd);
		properties.setProperty("useSSL", dbUseSSL);
		properties.setProperty("serverTimezone", dbTimezone);
		String url = String.format("jdbc:mysql://%s:%s", dbHost, dbPort);
		Connection connection = DriverManager.getConnection(url, properties);

		ResultSet rs = connection.createStatement().executeQuery("SHOW DATABASES");
		while (rs.next()) {
			if (rs.getString(1).startsWith(dbName)) {
				connection.createStatement().execute("use `" + rs.getString(1)+"`");
				new VersionManager().uptodateDatabase(connection);
				System.out.println("use " + rs.getString(1));

				return connection;
			}
		}

		VersionManager versionManager = new VersionManager();
		versionManager.createDatabase(connection, dbName);
		versionManager.uptodateDatabase(connection);

		// Establish context
		return connection;
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
//		if (connection != null)
//			connection.close();
	}

	@Before
	public void setUp() throws Exception {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		Connection connection = newConnection();

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		dslContext.execute("SET FOREIGN_KEY_CHECKS=0");
		dslContext.deleteFrom(PURCHASE_DETAIL).where(PURCHASE_DETAIL.DESCRIPTION.like("Test%")).execute();
		dslContext.deleteFrom(SALES_DETAIL).where(SALES_DETAIL.DESCRIPTION.like("Test%")).execute();
		dslContext.deleteFrom(PROPOSAL_DETAIL).where(PROPOSAL_DETAIL.DESCRIPTION.like("Test%")).execute();
		dslContext.deleteFrom(PROPOSAL).where(PROPOSAL.CREATION_USER.like("Test%")).execute();
		dslContext.deleteFrom(ITEM).where(ITEM.DESCRIPTION.like("Test%")).execute();
		dslContext.deleteFrom(PRODUCT).where(PRODUCT.NAME.like("Test%")).execute();
		dslContext.deleteFrom(SCOPE).where(SCOPE.DESCRIPTION.like("Test%")).execute();
		dslContext.deleteFrom(DOMAIN).where(DOMAIN.NAME.like("Test%")).execute();
		dslContext.execute("SET FOREIGN_KEY_CHECKS=1");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBasic() throws ClassNotFoundException, SQLException, AonSQLException {

		DomainRecord domainRecord = dslContext.insertInto(DOMAIN).set(DOMAIN.NAME, "Test_domainPrueba")
									 .set(DOMAIN.DESCRIPTION, "descripcionPrueba")
									 .set(DOMAIN.OWNER, "emailCreadorPrueba").returning(DOMAIN.ID).fetchOne();

		Integer idDomain = domainRecord.getId();

		ScopeRecord scopes [] = new ScopeRecord [10];
		for ( int i = 0; i < scopes.length; i++ ) {
			scopes[i] = new ScopeRecord();
			scopes[i].set(SCOPE.DESCRIPTION, String.format("Test_DESC '%d'", i));
			dslContext.insertInto(SCOPE).set(SCOPE.DOMAIN, idDomain)
										.set(scopes[i])
										.execute();
		}


		Connection connection = newConnection();
		AonDump aonDump = new AonDump(connection);

		CallbackDump cb;
		cb = new CallbackDumpExecute(aonDump.dslContext);
		cb = new ErrorReferenceCallBackDump(cb);
		cb = new SiblingCallBackDump(cb);
		cb = new DomainZeroCallbackDump(cb);
		cb = new DomainSiblingCallBackDump(cb);
		cb = new IndexUniqueCallBackDump(cb);
		cb = new ModifyDataCallBack(cb, "Test_domainPrueba2", "domain", "name");
		cb = new ConsoleInformationCallBack(cb, System.out, aonDump, 0, 0);
		cb = new DownloadCallBackDump(cb, false);

		try {
			aonDump.findDomainInTables(connection, dslContext, cb, dbHost, dbName, "Test_domainPrueba");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Integer domainId = dslContext.select().from(DOMAIN).where(DOMAIN.NAME.eq("Test_domainPrueba2")).fetchOne(DOMAIN.ID);

		List<String> _scopes = dslContext.select().from(SCOPE).where(SCOPE.DOMAIN.eq(domainId)).orderBy(SCOPE.DESCRIPTION).fetch(SCOPE.DESCRIPTION);
		for ( int i = 0; i < scopes.length; i++ ) {
			Assert.assertEquals(scopes[i].get(SCOPE.DESCRIPTION), _scopes.get(i));
		}

	}

	//@Test
	public void testSpecialFK() throws ClassNotFoundException, SQLException, AonSQLException {

		DomainRecord domainRecord =
		dslContext.insertInto(DOMAIN)
		.set(DOMAIN.NAME, "Test_domainPrueba")
		.set(DOMAIN.DESCRIPTION, "descripcionPrueba")
		.set(DOMAIN.OWNER, "emailCreadorPrueba")
		.returning(DOMAIN.ID).
		fetchOne();


		ScopeRecord scopeRecord =
		dslContext.insertInto(SCOPE)
		.set(SCOPE.DOMAIN, domainRecord.getId())
		.set(SCOPE.DESCRIPTION, "TestSpecialFK")
		.returning(SCOPE.ID)
		.fetchOne();

		RegistryRecord registryRecord =
		dslContext.insertInto(REGISTRY)
		.set(REGISTRY.DOMAIN, domainRecord.getId())
		.set(REGISTRY.NAME, "TestSpecialFK")
		.returning(REGISTRY.ID)
		.fetchOne();

		RaddressRecord raddressRecord =
		dslContext.insertInto(RADDRESS)
		.set(RADDRESS.DOMAIN, domainRecord.getId())
		.set(RADDRESS.REGISTRY, registryRecord.getId())
		.set(RADDRESS.ALIAS, "TestSpecialFK")
		.returning(RADDRESS.ID)
		.fetchOne();

		dslContext.insertInto(ENTERPRISE)
		.set(ENTERPRISE.SCOPE, scopeRecord.getId())
		.set(ENTERPRISE.DOMAIN, domainRecord.getId())
		.set(ENTERPRISE.REGISTRY, registryRecord.getId())
		.execute();

		dslContext.insertInto(SUPPLIER)
		.set(SUPPLIER.SCOPE, scopeRecord.getId())
		.set(SUPPLIER.DOMAIN, domainRecord.getId())
		.set(SUPPLIER.REGISTRY, registryRecord.getId())
		.execute();

		WorkplaceRecord workplaceRecord =
		dslContext.insertInto(WORKPLACE)
		.set(WORKPLACE.SCOPE, scopeRecord.getId())
		.set(WORKPLACE.DOMAIN, domainRecord.getId())
		.set(WORKPLACE.ENTERPRISE, registryRecord.getId())
		.set(WORKPLACE.DESCRIPTION, "TestSpecialFK")
		.set(WORKPLACE.ADDRESS, raddressRecord.getId())
		.returning(WORKPLACE.ID)
		.fetchOne();

		PurchaseRecord purchaseRecord =
		dslContext.insertInto(PURCHASE)
		.set(PURCHASE.SCOPE, scopeRecord.getId())
		.set(PURCHASE.DOMAIN, domainRecord.getId())
		.set(PURCHASE.SUPPLIER, registryRecord.getId())
		.set(PURCHASE.WORKPLACE, workplaceRecord.getId())
		.returning(PURCHASE.ID)
		.fetchOne();


//		ItemRecord itemRecord = dslContext.insertInto(ITEM)
//		.set(ITEM.DOMAIN, idDomain)
//		.set(ITEM.DESCRIPTION, "TestSpecialFK Item")
//		.returning(ITEM.ID).fetchOne()
//		;


//		ProposalDetailRecord proposal_detailRecord = dslContext.insertInto(PROPOSAL_DETAIL)
//										  .set(PROPOSAL_DETAIL.DOMAIN, idDomain)
//										  .set(PROPOSAL_DETAIL.ITEM, 0)
//										  .set(PROPOSAL_DETAIL.PROPOSAL, 0)
//										  .set(PROPOSAL_DETAIL.DESCRIPTION, "Test_descripcionPrueba")
//										  .returning(PROPOSAL_DETAIL.ID).fetchOne();
		Connection connection = newConnection();
		AonDump aonDump = new AonDump(connection);

		CallbackDump cb;
		cb = new CallbackDumpExecute(aonDump.dslContext);
		cb = new ErrorReferenceCallBackDump(cb);
		cb = new SiblingCallBackDump(cb);
		cb = new DomainZeroCallbackDump(cb);
		cb = new DomainSiblingCallBackDump(cb);
		cb = new IndexUniqueCallBackDump(cb);
		cb = new ModifyDataCallBack(cb, "Test_domainPrueba2", "domain", "name");
		cb = new ConsoleInformationCallBack(cb, System.out, aonDump, 0, 0);
		cb = new DownloadCallBackDump(cb, false);

		try {
			aonDump.findDomainInTables(aonDump.connection, aonDump.dslContext, cb, dbHost, dbName, "Test_domainPrueba");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Integer domainId = dslContext.select().from(DOMAIN).where(DOMAIN.NAME.eq("Test_domainPrueba2")).fetchOne(DOMAIN.ID);

		Record prurchasedetail = dslContext.select().from(PURCHASE_DETAIL).where(PURCHASE_DETAIL.DOMAIN.eq(domainId)).and(PURCHASE_DETAIL.SOURCE.eq((byte)0)).fetchOne();
		Integer proposaldetailID = prurchasedetail.get(PURCHASE_DETAIL.SOURCE_ID);
		Record proposal_detail = dslContext.select().from(PROPOSAL_DETAIL).where(PROPOSAL_DETAIL.ID.eq(proposaldetailID)).fetchOne();
		Assert.assertEquals(proposal_detail.get(PROPOSAL_DETAIL.DESCRIPTION), "Test_descripcionPrueba");

		Record prurchasedetail2 = dslContext.select().from(PURCHASE_DETAIL).where(PURCHASE_DETAIL.DOMAIN.eq(domainId)).and(PURCHASE_DETAIL.SOURCE.eq((byte)2)).fetchOne();
		Integer salesdetailID = prurchasedetail2.get(PURCHASE_DETAIL.SOURCE_ID);
		Record sales_detail = dslContext.select().from(SALES_DETAIL).where(SALES_DETAIL.ID.eq(salesdetailID)).fetchOne();
		Assert.assertEquals(sales_detail.get(SALES_DETAIL.DESCRIPTION), "Test_descripcionPrueba");



	}

}
