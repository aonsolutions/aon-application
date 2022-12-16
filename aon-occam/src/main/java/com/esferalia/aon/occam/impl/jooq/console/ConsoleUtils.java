package com.esferalia.aon.occam.impl.jooq.console;


import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Alarm.ALARM;
import static com.esferalia.aon.jooq.tables.AmortizationType.AMORTIZATION_TYPE;
import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.AutoConcept.AUTO_CONCEPT;
import static com.esferalia.aon.jooq.tables.BankConcept.BANK_CONCEPT;
import static com.esferalia.aon.jooq.tables.BankStatementLink.BANK_STATEMENT_LINK;
import static com.esferalia.aon.jooq.tables.Brand.BRAND;
import static com.esferalia.aon.jooq.tables.Calendar.CALENDAR;
import static com.esferalia.aon.jooq.tables.CalendarHoliday.CALENDAR_HOLIDAY;
import static com.esferalia.aon.jooq.tables.CalendarPeriod.CALENDAR_PERIOD;
import static com.esferalia.aon.jooq.tables.Catalogue.CATALOGUE;
import static com.esferalia.aon.jooq.tables.CatalogueCategory.CATALOGUE_CATEGORY;
import static com.esferalia.aon.jooq.tables.CatalogueItem.CATALOGUE_ITEM;
import static com.esferalia.aon.jooq.tables.Category.CATEGORY;
import static com.esferalia.aon.jooq.tables.Cnae.CNAE;
import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;
import static com.esferalia.aon.jooq.tables.Cnae2009Rate.CNAE2009_RATE;
import static com.esferalia.aon.jooq.tables.Cno.CNO;
import static com.esferalia.aon.jooq.tables.Feature.FEATURE;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.Geotree.GEOTREE;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.ItemAddinfo.ITEM_ADDINFO;
import static com.esferalia.aon.jooq.tables.ItemAlternative.ITEM_ALTERNATIVE;
import static com.esferalia.aon.jooq.tables.ItemComposition.ITEM_COMPOSITION;
import static com.esferalia.aon.jooq.tables.ItemTariff.ITEM_TARIFF;
import static com.esferalia.aon.jooq.tables.JobType.JOB_TYPE;
import static com.esferalia.aon.jooq.tables.Location.LOCATION;
import static com.esferalia.aon.jooq.tables.Make.MAKE;
import static com.esferalia.aon.jooq.tables.Mark.MARK;
import static com.esferalia.aon.jooq.tables.Model.MODEL;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.ProductTag.PRODUCT_TAG;
import static com.esferalia.aon.jooq.tables.Profile.PROFILE;
import static com.esferalia.aon.jooq.tables.PurchaseDetail.PURCHASE_DETAIL;
import static com.esferalia.aon.jooq.tables.Relationship.RELATIONSHIP;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Segment.SEGMENT;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.Tariff.TARIFF;
import static com.esferalia.aon.jooq.tables.TariffAddinfo.TARIFF_ADDINFO;
import static com.esferalia.aon.jooq.tables.TariffCatalogue.TARIFF_CATALOGUE;
import static com.esferalia.aon.jooq.tables.TasItem.TAS_ITEM;
import static com.esferalia.aon.jooq.tables.Tax.TAX;
import static com.esferalia.aon.jooq.tables.TaxDetail.TAX_DETAIL;
import static com.esferalia.aon.jooq.tables.WarehouseTransfer.WAREHOUSE_TRANSFER;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.jooq.ForeignKey;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.watson.error.AonCoreException;


class ConsoleUtils {
	
	private ConsoleUtils() {
	}
	
	static void disableForeignKeys(ConsoleParams params) {
		String cmd = "SET FOREIGN_KEY_CHECKS=0";
		params.getToDslContext().execute(cmd);
	}

//	static void disableForeignKeysAndPrint(ConsoleParams params) {
//		disableForeignKeys(params);
//		ConsoleUtils.log(params,"** Foreign keys disabled");
//	}
	
	static void enableForeignKeys(ConsoleParams params) {
		String cmd = "SET FOREIGN_KEY_CHECKS=1";
		params.getToDslContext().execute(cmd);
	}

//	static void enableForeignKeysAndPrint(ConsoleParams params) {
//		enableForeignKeys(params);
//		ConsoleUtils._log(params,"** Foreign keys enabled");
//	}

	
    static <R extends Record, U extends Record> ForeignKey<R, U> createForeignKey(UniqueKey<U> key,
            Table<R> table, String name, TableField<R, ?> field) {
        return new WeakForeignKey<>(key, table, name, field);
    }
	static final HashMap<String, CustomForeignKey[]> CUSTOM_FOREIGN_MAP = new HashMap<>();
	static {
		CUSTOM_FOREIGN_MAP.put(INVOICE_DETAIL.getName(), new CustomForeignKey[] {
			CustomForeignKey.INVOICE_DETAIL_PURCHASE_DETAIL,
			CustomForeignKey.INVOICE_DETAIL_SALES_DETAIL,
			CustomForeignKey.INVOICE_DETAIL_DELIVERY_DETAIL,
			CustomForeignKey.INVOICE_DETAIL_INCOME_DETAIL,
			CustomForeignKey.INVOICE_DETAIL_OFFER_DETAIL,
			CustomForeignKey.INVOICE_DETAIL_PROJECT_RESERVATION});
		CUSTOM_FOREIGN_MAP.put(BANK_STATEMENT_LINK.getName(), new CustomForeignKey[] {
			CustomForeignKey.BANK_STATEMENT_LINK_FINANCE_TRACKING,
			CustomForeignKey.BANK_STATEMENT_LINK_FBATCH,
			CustomForeignKey.BANK_STATEMENT_LINK_BANK_CONCEPT,
			CustomForeignKey.BANK_STATEMENT_LINK_ACCOUNT
		});
		CUSTOM_FOREIGN_MAP.put(FINANCE.getName(), new CustomForeignKey[] {
			CustomForeignKey.FINANCE_SALARY,
			CustomForeignKey.FINANCE_PREPAYMENT
		});
		CUSTOM_FOREIGN_MAP.put(ALARM.getName(), new CustomForeignKey[] {
			CustomForeignKey.ALARM_NOTICE,
			CustomForeignKey.ALARM_TASK,
			CustomForeignKey.ALARM_COMMERCIAL_TRACKING,
			CustomForeignKey.ALARM_MK_ACTION_TARGET
		});
		CUSTOM_FOREIGN_MAP.put(PURCHASE_DETAIL.getName(), new CustomForeignKey[] {
			CustomForeignKey.PURCHASE_DETAIL_PROPOSAL_DETAIL,
			CustomForeignKey.PURCHASE_DETAIL_PURCHASE_DETAIL,
			CustomForeignKey.PURCHASE_DETAIL_SALES_DETAIL
		});
		CUSTOM_FOREIGN_MAP.put(WAREHOUSE_TRANSFER.getName(), new CustomForeignKey[] {
			CustomForeignKey.WAREHOUSE_TRANSFER_INVENTORY_DETAIL,
			CustomForeignKey.WAREHOUSE_TRANSFER_PURCHASE_DETAIL,
			CustomForeignKey.WAREHOUSE_TRANSFER_INVENTORY_DETAIL
		});
		CUSTOM_FOREIGN_MAP.put(APP_PARAM.getName(), new CustomForeignKey[] {
			CustomForeignKey.ACC_APP_PARAM
		});
	}

	
	// ********************************
	// ************* [LOG] ************
	// ********************************

	static PrintStream getPrinter(ConsoleParams params) {
		return Objects.requireNonNullElse(params.getPrinter(), new PrintStream(System.out));
	}
//	static void _log(ConsoleParams params,String msg) {
//		params.setPartialCount(0); 
//		getPrinter(params).println(msg);
//		getPrinter(params).flush();
//	}
//	static void _logf(ConsoleParams params,String msg) {
//		if (params.getPartialProgress() == 80) {
//			params.setPartialProgress(0); 
//			getPrinter(params).println();
//			getPrinter(params).flush();
//		}
//		getPrinter(params).print(msg);
//		getPrinter(params).flush();
//		params.addPartialProgress();
//	}
//	
//	static void _printInfo(ConsoleParams params, ScriptTable scriptTable) {
//		scriptTable.setCurrentRow((scriptTable.getCurrentRow() + 1));
//		int percent = (scriptTable.getCurrentRow() * 100 / scriptTable.getRows());
//		if ( percent != scriptTable.getPercent() && percent % 2 == 0) {
//			scriptTable.setPercent( percent );
//			_log(params,MessageFormat.format(("\t [" + AonStringUtils.repeat('*', percent/2) + AonStringUtils.repeat(' ', 50 - percent/2) + "] {0}%  ( {1} / {2} )")
//					, percent
//					, Integer.toString(scriptTable.getCurrentRow())
//					, Integer.toString(scriptTable.getRows())));
//		}
//		getPrinter(params).flush();
//	}

	static ConsoleIDsTableInfo initializeConsoleIDsTableInfo( ConsoleIDsTableInfo info, ConsoleParams params ) {
		String schema = params.getFromConnection().getSchemaName();
		try (CloseableAONContext metaCtx = AONContext.getAONContext(schema)) {
			Schema idsSchema = metaCtx.getDslContext().meta()
				.getSchemas(ConsoleIDsTableInfo.SCHEMA)
				.stream()
				.findFirst()
				.orElse(null);
			if ( idsSchema == null) {
				throw new AonCoreException("No es posible encontrar el esquema \""+ConsoleIDsTableInfo.SCHEMA+"\"");
			}
			info.initialize(params);
			info.setCtx(AONContext.getUnpooledAONContext(ConsoleIDsTableInfo.SCHEMA));
			return info;
		}
	}
	
	static final Set<String> PARENT_INCLUDED_TABLES = new HashSet<>();
	static {
		PARENT_INCLUDED_TABLES.add(ACCOUNT.getName());
		PARENT_INCLUDED_TABLES.add(AMORTIZATION_TYPE.getName());
		PARENT_INCLUDED_TABLES.add(AUTO_CONCEPT.getName());
		PARENT_INCLUDED_TABLES.add(BANK_CONCEPT.getName());
		PARENT_INCLUDED_TABLES.add(BRAND.getName());
		PARENT_INCLUDED_TABLES.add(CALENDAR.getName());
		PARENT_INCLUDED_TABLES.add(CALENDAR_HOLIDAY.getName());
		PARENT_INCLUDED_TABLES.add(CALENDAR_PERIOD.getName());
		PARENT_INCLUDED_TABLES.add(CATALOGUE.getName());
		PARENT_INCLUDED_TABLES.add(CATALOGUE_CATEGORY.getName());
		PARENT_INCLUDED_TABLES.add(CATALOGUE_ITEM.getName());
		PARENT_INCLUDED_TABLES.add(CATEGORY.getName());
		PARENT_INCLUDED_TABLES.add(CNAE.getName());
		PARENT_INCLUDED_TABLES.add(CNAE2009.getName());
		PARENT_INCLUDED_TABLES.add(CNAE2009_RATE.getName());
		PARENT_INCLUDED_TABLES.add(CNO.getName());
		PARENT_INCLUDED_TABLES.add(FEATURE.getName());
		PARENT_INCLUDED_TABLES.add(GEOTREE.getName());
		PARENT_INCLUDED_TABLES.add(GEOZONE.getName());
		PARENT_INCLUDED_TABLES.add(IAE.getName());
		PARENT_INCLUDED_TABLES.add(ITEM.getName());
		PARENT_INCLUDED_TABLES.add(ITEM_ADDINFO.getName());
		PARENT_INCLUDED_TABLES.add(ITEM_ALTERNATIVE.getName());
		PARENT_INCLUDED_TABLES.add(ITEM_COMPOSITION.getName());
		PARENT_INCLUDED_TABLES.add(ITEM_TARIFF.getName());
		PARENT_INCLUDED_TABLES.add(JOB_TYPE.getName());
		PARENT_INCLUDED_TABLES.add(LOCATION.getName());
		PARENT_INCLUDED_TABLES.add(MAKE.getName());
		PARENT_INCLUDED_TABLES.add(MARK.getName());
		PARENT_INCLUDED_TABLES.add(MODEL.getName());
		PARENT_INCLUDED_TABLES.add(PAY_METHOD.getName());
		PARENT_INCLUDED_TABLES.add(PAYMENT_CONCEPT.getName());
		PARENT_INCLUDED_TABLES.add(PCATEGORY.getName());
		PARENT_INCLUDED_TABLES.add(PRODUCT.getName());
		PARENT_INCLUDED_TABLES.add(PRODUCT_TAG.getName());
		PARENT_INCLUDED_TABLES.add(PROFILE.getName());
		PARENT_INCLUDED_TABLES.add(RELATIONSHIP.getName());
		PARENT_INCLUDED_TABLES.add(SCOPE.getName());
		PARENT_INCLUDED_TABLES.add(SEGMENT.getName());
		PARENT_INCLUDED_TABLES.add(TAG.getName());
		PARENT_INCLUDED_TABLES.add(TARIFF.getName());
		PARENT_INCLUDED_TABLES.add(TARIFF_ADDINFO.getName());
		PARENT_INCLUDED_TABLES.add(TARIFF_CATALOGUE.getName());
		PARENT_INCLUDED_TABLES.add(TAS_ITEM.getName());
		PARENT_INCLUDED_TABLES.add(TAX.getName());
		PARENT_INCLUDED_TABLES.add(TAX_DETAIL.getName());
	}
}
