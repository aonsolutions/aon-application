package com.esferalia.aon.gwt.common.client;

import com.esferalia.aon.gwt.common.client.css.AonCSS;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.NumberFormat;

public class AON {

	private static final int CONNECTION_STATUS_STOP = 0;
	private static final int CONNECTION_STATUS_START = 1;
	private static final int CONNECTION_STATUS_FAILED = 2;

	private static final String CONNECTION_STATUS_ELEMENTS[] = {
			"_viewRoot:status.stop", "_viewRoot:status.start", "status_error" };

	// ------------------------------------------------------------------------
	public static AonCSS AON_CSS = GWT.<AonResources> create(
			AonResources.class).css();
	
	public static void start() {
		show(CONNECTION_STATUS_START);
	}

	public static void stop() {
		show(CONNECTION_STATUS_STOP);
	}

	public static void fail() {
		show(CONNECTION_STATUS_FAILED);
	}

	private static void show(int el) {

		for (int i = 0; i < el; i++) {
			Document.get().getElementById(CONNECTION_STATUS_ELEMENTS[i])
					.getStyle().setDisplay(Display.NONE);
		}

		Document.get().getElementById(CONNECTION_STATUS_ELEMENTS[el])
				.getStyle().clearDisplay();

		for (int i = el + 1; i < CONNECTION_STATUS_ELEMENTS.length; i++) {
			Document.get().getElementById(CONNECTION_STATUS_ELEMENTS[i])
					.getStyle().setDisplay(Display.NONE);
		}
	}

	public static String format(Double d) {
		return d == null ? null : CURRENCY_FORMAT.format(d);
	}

	public static final String AON_DATA_TABLE_ROW_EVEN = "aon-dataTable-row-even";
	public static final String AON_DATA_TABLE_ROW_ODD = "aon-dataTable-row-odd";
	public static final String AON_DATA_TABLE_ROW_HIGHLIGHT = "aon-dataTable-row-highlight";
	public static final String AON_DATA_TABLE_ROW_HIGHLIGHT_TOP = "aon-dataTable-row-highlight-top";
	public static final String AON_DATA_TABLE_CELL_HIGHLIGHT = "aon-dataTable-cell-highlight";
	public static final String AON_DATA_TABLE_CELL_HIGHLIGHT_TOP = "aon-dataTable-cell-highlight-top";
	public static final String AON_BOLD = "aon-bold";
	public static final String AON_BLACK = "aon-black";
	public static final String AON_WIDTH_ALL = "aon-width-all";
	public static final String AON_WIDTH_HALF = "aon-width-half";
	public static final String AON_TEXT_RIGHT = "aon-text-right";
	public static final String AON_TEXT_CENTER = "aon-text-center";
	public static final String AON_ICON_RESET = "aon-icon-reset";
	public static final String AON_EDIT_DATA_TABLE_BUTTON = "aon-editDataTable-button";
	public static final String AON_ICON_COPY = "aon-icon-copy";
	public static final String AON_ICON_COMPANY = "aon-icon-company";
	public static final String AON_ICON_CHANGED = AON_CSS.aonIconChanged();// "aon-icon-changed";
	public static final String AON_ICON_CLIPBOARD = "aon-icon-clipboard";
	public static final String AON_ICON_DELETE = AON_CSS.aonIconDelete(); // "aon-icon-delete";
	public static final String AON_ICON_CANCEL = "aon-icon-cancel";
	public static final String AON_ICON_ACCEPT = "aon-icon-accept";
	public static final String AON_ICON_DUPLICATE = "aon-icon-duplicate";
	public static final String AON_ICON_PASTE = "aon-icon-paste";
	public static final String AON_ICON_INE = "aon-icon-ine";
	public static final String AON_ICON_AET = AON_CSS.aonIconAeat();// "aon-icon-aet";
	public static final String AON_ICON_BLANK = "aon-icon-blank";
	public static final String AON_ICON_EDIT_ADD = "aon-icon-edit-add";
	public static final String AON_ICON_EDIT_END = "aon-icon-edit-end";
	
	public static final String AON_ICON_ARABA = "aon-icon-araba";
	public static final String AON_ICON_BIZKAIA = "aon-icon-bizkaia";
	public static final String AON_ICON_NAVARRA = "aon-icon-navarra";
	public static final String AON_ICON_GIPUZKOA = "aon-icon-gipuzkoa";
	
	public static final String AON_ICON_WORKPLACE = "aon-icon-workplace";
	public static final String AON_ICON_EMPLOYEE = "aon-icon-employee";
	public static final String AON_ICON_ROW_SELECTOR = "aon-icon-rowSelector";
	public static final String AON_ICON_ROW_SELECTOR_S = AON_CSS.aonIconRowSelectorSystem();// "aon-icon-rowSelector-S";
	public static final String AON_ICON_ROW_SELECTOR_C = AON_CSS.aonIconRowSelectorAgreement(); // "aon-icon-rowSelector-C";
	public static final String AON_ICON_ROW_SELECTOR_PARENT = AON_CSS.aonIconRowSelectorParent(); // "aon-icon-rowSelector-Parent";
	public static final String AON_ICON_ROW_SELECTOR_CHANGED = AON_CSS.aonIconRowSelectorChanged(); // "aon-icon-rowSelector-Changed";
	public static final String AON_ICON_EXCEPTION = "aon-icon-exception";
	public static final String AON_ICON_WARN = AON_CSS.aonIconWarn(); // "aon-icon-warn";
	public static final String AON_ICON_ERRORWARNING = "aon-icon-errorwarning";
	public static final String GWT_HORIZONTAL_PANEL = "gwt-HorizontalPanel";
	public static final String AON_ICON = "aon-icon";
	public static final String AON_ICON_TASK_START = "aon-icon-task-start";
	public static final String AON_ICON_CMD_BUTTON = "aon-icon-commandButton";
	public static final String AON_ICON_TIME = "aon-icon-time";
	public static final String AON_ICON_EXCEL = "aon-icon-excel";
	public static final String AON_ICON_AGREEMENT = "aon-icon-agreement";
	public static final String AON_ICON_PAYMENT = "aon-icon-payment";
	public static final String AON_ICON_CHARGE = "aon-icon-charge";
	public static final String AON_ICON_PAYNNENT = "aon-icon-paynnent";
	public static final String AON_ICON_DEDUCTION = "aon-icon-deduction";
	public static final String AON_ICON_BONUS = "aon-icon-segsocial";
	public static final String AON_ICON_BONUS_SMALL = "aon-icon-segsocial-small";
	public static final String AON_ICON_CONFIG = "aon-icon-config";
	public static final String AON_ICON_CLEAN = "aon-lookupButton-clear";
	public static final String AON_NO_MARGIN = "aon-no-margin";
	public static final String AON_INPUT_REQUIRED = "aon-input-required";
	public static final String AON_PADDING_LEFT = "aon-padding-left";
	public static final String AON_LABEL_ERROR =  "aon-label-error";
	public static final String AON_LABEL_WARN = "aon-label-warn";
	public static final String AON_ICON_OK = "aon-icon-predetermine";
	public static final String AON_ICON_X = "aon-icon-x";
	public static final String AON_ICON_F =  "aon-icon-f";
	public static final String AON_ICON_FX = AON_CSS.aonIconFx(); //"aon-icon-fx";
	public static final String AON_ICON_LAMP =  "aon-icon-lamp";
	public static final String AON_ICON_COLLAPSE = "aon-icon-collapse";
	public static final String AON_ICON_EXPAND = "aon-icon-expand";
	public static final String AON_TOOLTIP = "aon-tooltip";
	public static final String AON_ICON_CARD = "aon-icon-card";
	public static final String AON_ICON_ERROR = "aon-icon-error";
	public static final String AON_ICON_IRPF_PREVIEW = AON_CSS.aonIconIrpfPreview(); // "aon-icon-irpfPreview";
	public static final String AON_ICON_EXPANDALL = AON_CSS.aonIconExpandAll(); // "aon-icon-expandAll";
	public static final String AON_ICON_COLLAPSEALL = AON_CSS.aonIconCollapseAll(); //"aon-icon-collapseAll";
	public static final String AON_ICON_PDF_PREVIEW = AON_CSS.aonIconPdfPreview();//"aon-icon-pdfPreview";
	public static final String AON_ICON_REDO = AON_CSS.aonIconRedo(); //"aon-icon-redo";
	public static final String AON_ICON_UNDO = AON_CSS.aonIconUndo(); //"aon-icon-undo";
	public static final String AON_ICON_VIEW = AON_CSS.aonIconView(); //"aon-icon-view";
	

	
	public static final String AON_ICON_CALENDAR = "aon-icon-calendar";

	public static final NumberFormat CURRENCY_FORMAT = NumberFormat
			.getFormat("#,##0.00");
	public static final DateTimeFormat MONTH_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.YEAR_MONTH_NUM);
	public static final DateTimeFormat DATE_FORMAT = DateTimeFormat
			.getFormat("dd/MM/yyyy");
	public static final String AON_NOWRAP = "aon-nowrap";

	public static double round(Double number) {
		return (double) Math.round(number * 1000.00) / 1000.00;
	}


}
