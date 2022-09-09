package com.esferalia.aon.occam.impl.jooq.console;

import static com.esferalia.aon.jooq.tables.Alarm.ALARM;
import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.BankStatementLink.BANK_STATEMENT_LINK;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.PurchaseDetail.PURCHASE_DETAIL;
import static com.esferalia.aon.jooq.tables.WarehouseTransfer.WAREHOUSE_TRANSFER;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Objects;

import org.jooq.ForeignKey;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;

import com.esferalia.aon.watson.util.AonStringUtils;


class ConsoleUtils {
	
	private ConsoleUtils() {
	}

	static void disableForeignKeys(ConsoleParams params) {
		String cmd = "SET FOREIGN_KEY_CHECKS=0";
		params.getToDslContext().execute(cmd);
		ConsoleUtils.log(params,"** Foreign keys disabled");
	}
	
	static void enableForeignKeys(ConsoleParams params) {
		String cmd = "SET FOREIGN_KEY_CHECKS=1";
		params.getToDslContext().execute(cmd);
		ConsoleUtils.log(params,"** Foreign keys enabled");
	}

	
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
	static void log(ConsoleParams params,String msg) {
		params.setPartialCount(0); 
		getPrinter(params).println(msg);
		getPrinter(params).flush();
	}
	static void logf(ConsoleParams params,String msg) {
		if (params.getPartialProgress() == 80) {
			params.setPartialProgress(0); 
			getPrinter(params).println();
			getPrinter(params).flush();
		}
		getPrinter(params).print(msg);
		getPrinter(params).flush();
		params.addPartialProgress();
	}
	static void printInfo(ConsoleParams params, ScriptTable scriptTable) {
		scriptTable.setCurrentRow((scriptTable.getCurrentRow() + 1));
		int percent = (scriptTable.getCurrentRow() * 100 / scriptTable.getRows());
		if ( percent != scriptTable.getPercent() && percent % 2 == 0) {
			scriptTable.setPercent( percent );
			log(params,MessageFormat.format(("\t [" + AonStringUtils.repeat('*', percent/2) + AonStringUtils.repeat(' ', 50 - percent/2) + "] {0}%  ( {1} / {2} )")
					, percent
					, Integer.toString(scriptTable.getCurrentRow())
					, Integer.toString(scriptTable.getRows())));
		}
		getPrinter(params).flush();
	}
	
}
