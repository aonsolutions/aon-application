package net.aonsolutions.dump;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Scope;

public class AonDump {

	/**
	 * Variables globales
	 */
	public Connection connection;
	private Settings settings;
	public DSLContext dslContext;
	public static PrintStream out;
	public static Map<Table<?>, Field<byte[]>> tablasAttach;

	public AonDump(String url, String usr, String password) throws SQLException {
		// Create a connection to our DataBase
		connection = DriverManager.getConnection(url, usr, password);

		// Establish settings
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		tablasAttach = new HashMap<>();

	}

	public AonDump(Connection connection) {
		this.connection = connection;
		// Establish settings
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		tablasAttach = new HashMap<>();
	}

	public void findDomainInTables(Connection connection, DSLContext dslContext, CallbackDump cb, String hostName,
			String dataBase, String domain) throws FileNotFoundException {

		IdsMap idsMap;
		Stack<Table<?>> tablesStack = new Stack<Table<?>>();
		// List<Table<?>> tablesCiclic = new ArrayList<Table<?>>();
		List<String> tablesNotToDownload = new ArrayList<String>();

		tablesWontDownload(tablesNotToDownload);

		// Get the Schema code and filter it to find our DataBase
		Optional<Schema> schema = DSL.using(connection, SQLDialect.MARIADB).meta().getSchemas().stream()
				.filter(s -> s.getName().equals(dataBase)).findFirst();

		List<Table<?>> tables = schema.get().getTables();

		int idDomain = dslContext.select(DOMAIN.ID).from(DOMAIN).where((DOMAIN.NAME).equal(domain)).fetchOne().value1();

		// We filter all the tables we have so we let only the ones we are going
		// to use
		Map<Table<?>, Integer> dumpTables = new HashMap<Table<?>, Integer>();
		dumpTables = getDumpTables(tables, idDomain);

		// Initialize the Ids map and add our domain to it
		idsMap = newIdsMap(dumpTables);
		idsMap.createTableName(DOMAIN.getName());
		idsMap.setTableName(DOMAIN.getName());
		idsMap.setOrder(DOMAIN.getName(), idDomain, idDomain, false);

		idsMap.createTableName(Scope.SCOPE.getName());
		idsMap.setTableName(Scope.SCOPE.getName());

		cb.header(schema.get(), hostName, dumpTables, dslContext, idDomain, idsMap);

		// Download tables domain and scope (base case)
		cb.accept("SET FOREIGN_KEY_CHECKS=0;");

		downloadTableReferenceDomain(DOMAIN, DOMAIN.getReferences(), idsMap, cb, Collections.emptyList(),
				(DSL.field("id")).equal(idDomain));

		downloadTableReferenceDomain(Scope.SCOPE, Scope.SCOPE.getReferences(), idsMap, cb, Collections.emptyList(),
				(DSL.field("domain")).equal(idDomain));

		cb.accept("SET FOREIGN_KEY_CHECKS=1;");

		// We go over our dumpTables so we can download each one
		dumpTables.forEach((t, k) -> {
			if (tablesNotToDownload.contains(idsMap.getTableInformation(t.getName())))
				return;
			if (idsMap.getTableInformation(t.getName()) == null)
				downloadTable(t, idsMap, cb, out, tablesStack, Collections.emptyList(),
						(DSL.field("domain")).equal(idDomain));
		});

		// Get attachs from DataBase and UPDATE them
		gestionAttachs(cb, dslContext, tablasAttach, idDomain, idsMap);

		cb.footer();

	}

	private void tablesWontDownload(List<String> tablesNotToDownload) {
		// TODO Choose which tables (names) we wont download, becasue we dont
		// need them

	}

	private Map<Table<?>, Integer> getDumpTables(List<Table<?>> tables, int id) {

		// Map <Table, Number of lines we are going to download>
		Map<Table<?>, Integer> dumpTables = new HashMap<Table<?>, Integer>();

		Map<Table<?>, Integer> domainTables = tables.stream().filter(t -> t.field("domain") != null)
				.collect(Collectors.toMap(t -> t, t -> dslContext.select(DSL.count()).from(t)
						.where((((Field<Integer>) t.field("domain")).eq(id))).fetchOne(DSL.count())));

		domainTables.forEach((t, k) -> {
			if (k != 0)
				dumpTables.put(t, k);
		});

		return dumpTables;
	}

	private void gestionAttachs(CallbackDump cb, DSLContext dslContext, Map<Table<?>, Field<byte[]>> tablasAttach,
			int idDomain, IdsMap idsMap) {

		// Go over throw the attachs of our DataBase and UPDATE them
		tablasAttach.forEach((t, f) -> {

			Field<Integer> fieldDomain = (Field<Integer>) t.field("domain");

			for (Record r : dslContext.select().from(t).where(fieldDomain.equal(idDomain)).fetchLazy()) {

				Field<Integer> fieldId = (Field<Integer>) t.field("id");
				Field<Integer> orderId = idsMap.getOrder(t.getName(), r.getValue(fieldId));

				if (f != null) {
					try {
						UpdateConditionStep<?> update = dslContext.update(t).set(f, r.getValue(f))
								.where((fieldId).eq(orderId));
						cb.onAttachInsert(update);
					} catch (Exception e) {
						e.getMessage();
					}
				}
			}
		});

	}

	protected void downloadTable(Table<?> t, IdsMap idsMap, CallbackDump cb, PrintStream out,
			Stack<Table<?>> tablesStack, List<Table<?>> tablesCiclic, Condition where) {

		List<?> references = t.getReferences();

		// Push the table in the Stack, in order to download it, and know if it
		// is ciclic
		tablesStack.push(t);

		List<Table<?>> myTablesCiclic = new ArrayList<Table<?>>(tablesCiclic);

		for (Object ref : references) {
			ForeignKey<?, ?> fk = (ForeignKey<?, ?>) ref;

			Table<?> tableReference = fk.getKey().getTable();

			if (tablesStack.contains(tableReference)) { // Ciclic
				myTablesCiclic.add(tableReference);
				continue;
			}

			if (!idsMap.containsTable(tableReference.getName()))
				continue;

			if (idsMap.getTableInformation(tableReference.getName()) == null) {
				downloadTable(tableReference, idsMap, cb, out, tablesStack, Collections.emptyList(), where);
			}
		}

		// Initialize the Ids map of every table we have to download
		idsMap.setTableName(t.getName());

		downloadTableReferenceDomain(t, references, idsMap, cb, myTablesCiclic, where);

		tablesStack.pop();

	}

	protected void downloadTableReferenceDomain(Table<?> t, List<?> references, IdsMap idsMap, CallbackDump cb,
			List<Table<?>> tablesCiclic, Condition where) {

		// Prepare the 'insert into' in the table we are at this moment
		InsertSetStep<?> insert = dslContext.insertInto(t);
		InsertSetMoreStep<?> insertMore = null;
		Integer numRows = 0;
		String varTableName = "";

		if (t.getName().equals("agreement_level_category"))
			System.out.println();

		try {

			for (Record r : dslContext.select().from(t).where(where).orderBy(t.field(0).desc()).fetchLazy()) {

				/**
				 * Creamos el campo que vamos a insertar para actualizar el
				 * valor antiguo siguiendo el siguiente esquema:
				 * (@(nombre_campo) +1)
				 */
				Field<Integer> varId = DSL.field("@" + t.getName().toUpperCase(), Integer.class);
				Field<Integer> fieldId = (Field<Integer>) t.getPrimaryKey().getFields().get(0);
				// Field<Integer> domainId = (Field<Integer>)
				// t.getReferencesTo(DOMAIN).get(0).getFields().get(0);

				// insertMap will contain all the information we want to upload
				// to our table
				Map<Field<Integer>, Field<Integer>> fkInsertMap = new HashMap<Field<Integer>, Field<Integer>>();
				Map<Field<?>, Object> insertMap = new HashMap<Field<?>, Object>();

				if (t.getName().equals("user") && r.getValue("login").equals("jgarcia"))
					System.out.println();
				// Check the correct relationship of the foreignkeys
				try {
					
					
					fkInsertMap = checksFK(r, references, idsMap, cb, tablesCiclic, where);

				} catch (FkErrorException e) {
					continue;
				}

				// If we have a Id field we will rename it like (@(table_name) +
				// x)
				if (fieldId != null) {
					Threes<Integer, Integer, Boolean> pair = idsMap.getIdInformation(t.getName(), r.getValue(fieldId));

					if (pair != null && pair.getThird())
						continue;

					if (pair == null) {
						idsMap.setOrder(t.getName(), r.getValue(fieldId), r.getValue("domain", Integer.class), true);

					} else if (pair != null) {

						pair.setThird(true);

					}

					Field<Integer> order = null;
					order = idsMap.getOrder(t.getName(), r.getValue(fieldId));
					insertMore = insert.set(fieldId, order);

					if (varTableName.equals(""))
						varTableName = idsMap.getVarTableName(t.getName(), r.getValue(fieldId));

				}

				if (insertMore != null)
					insertMore = insertMore.set(fkInsertMap);
				else
					insertMore = insert.set(fkInsertMap);

				// Fill the remaining fields
				for (Field<?> f : t.fields()) {

					if (f.getName().equals("id"))
						continue;

					if (fkInsertMap.containsKey(f))
						continue;

					if (f.getDataType().isBinary() && f.getDataType().nullable()) {
						tablasAttach.put(t, (Field<byte[]>) f);
						continue;
					}

					insertMap.put((Field<Object>) f, r.getValue(f));

				}

				cb.onNewRow(t, insertMap, r);

				insertMore = insertMore.set(insertMap);

				insert = insertMore.newRecord();
				numRows++;

			}

			if (insertMore != null)
				try {
					cb.accept(insertMore, t, tablesCiclic, numRows, varTableName);

				} catch (SkipInsertException e) {
					idsMap.clear(t.getName());
				}

		} finally {
		}

	}

	private Map<Field<Integer>, Field<Integer>> checksFK(Record r, List<?> references, IdsMap idsMap, CallbackDump cb,
			List<Table<?>> tablesCiclic, Condition where) throws FkErrorException {

		Map<Field<Integer>, Field<Integer>> fkMapInsert = new HashMap<Field<Integer>, Field<Integer>>();

		for (Object ref : references) {

			ForeignKey<?, ?> fk = (ForeignKey<?, ?>) ref;

			// Get the references table so we will know the table which one we
			// have to relation to
			Table<?> tableReference = fk.getKey().getTable();

			String fieldNameId = fk.getKey().getFields().get(0).getName();
			String tableReferenceName = fieldNameId.equals("id") ? tableReference.getName() : fieldNameId;

			if (!idsMap.containsTable(tableReferenceName))
				continue;

			Field<Integer> fieldNull = null;

			if (r.getValue(fk.getFields().get(0)) == null) {
				fieldNull = (Field<Integer>) fk.getFields().get(0);
				fkMapInsert.put(fieldNull, DSL.castNull(fieldNull));
			}

			else {
				try {

					Map<Integer, Threes<Integer, Integer, Boolean>> map = idsMap
							.getTableInformation(tableReferenceName);

					if (map == null)
						idsMap.setTableName(tableReferenceName);

					Threes<Integer, Integer, Boolean> pair = idsMap.getIdInformation(tableReferenceName,
							((Integer) r.getValue(fk.getFields().get(0))));

					if (pair == null) {
						Field<Integer> parentField = cb.onErrFk(dslContext, r, fk, this, idsMap, cb, tablesCiclic,
								references, where);
						if (parentField != null) {
							fkMapInsert.put((Field<Integer>) fk.getFields().get(0), parentField);
							continue;
						}

						if (tablesCiclic.contains(tableReference)) {
							idsMap.setOrder(tableReferenceName, ((Integer) r.getValue(fk.getFields().get(0))),
									((Integer) r.getValue("domain")), false);
						} else

							throw new FkErrorException(fk);

					}

					Field<Integer> fkOrder = idsMap.getOrder(tableReferenceName,
							(Integer) r.getValue(fk.getFields().get(0)));

					fkMapInsert.put((Field<Integer>) fk.getFields().get(0), fkOrder);

				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		}
		return fkMapInsert;
	}

	private IdsMap newIdsMap(Map<Table<?>, Integer> dumpTables) {

		IdsMap map = new IdsMap();

		dumpTables.forEach((t, k) -> map.createTableName(t.getName()));

		map.createTableName(DOMAIN.getName());

		return map;
	}

	private static class FkErrorException extends Exception {

		private ForeignKey<?, ?> fk;

		public FkErrorException(ForeignKey<?, ?> fk) {
			this.fk = fk;
		}
	}

}
