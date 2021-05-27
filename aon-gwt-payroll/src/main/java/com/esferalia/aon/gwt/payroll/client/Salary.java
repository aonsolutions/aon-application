package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.Salary.TypeVisitor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.gwt.pdfjs.client.Viewer;

public class Salary extends ResizeComposite {

	private static final int ZOOM_STEP = 20;
	private static final int MIN_ZOOM = 25;
	private static final int MAX_ZOOM = 500;

	private static final int DEFAULT_ZOOM = 135;

	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.YEAR_MONTH_DAY);

	interface Binder extends UiBinder<Widget, Salary> {
	}

	static interface Listener {
		void onPublis(SalaryDocuments documents, String type);
	}

	private static final Binder binder = GWT.create(Binder.class);
	
	private static String BLANK_PDF_URL = "data:application/pdf;base64,"
	+"JVBERi0xLjYNJeLjz9MNCjI0IDAgb2JqDTw8L0ZpbHRlci9GbGF0ZURlY29kZS9GaXJzdCA0L0xl"
	+"bmd0aCAyMTYvTiAxL1R5cGUvT2JqU3RtPj5zdHJlYW0NCmjePI9RS8MwFIX/yn1bi9jepCQ6GYNp"
	+"FBTEMsW97CVLbjWYNpImmz/fVsXXcw/f/c4SEFarepPTe4iFok8dU09DgtDBQx6TMwT74vaLTE7u"
	+"SPDUdXM0Xe/73r1FnVwYYEtHR6d9WdY3kX4ipRMV6oojSmxQMoGyac5RLBAXf63p38aGA7XPorLe"
	+"wyvFcYaJile8rB+D/YcwiRdMMGScszO8/IW0MdhsaKKYGA46gXKTr/cUQVY4We/cYMNpnLVeXPJU"
	+"XHs9fECr7kAFk+eZ5Xr9LcAAfKpQrA0KZW5kc3RyZWFtDWVuZG9iag0yNSAwIG9iag08PC9GaWx0"
	+"ZXIvRmxhdGVEZWNvZGUvRmlyc3QgNC9MZW5ndGggNDkvTiAxL1R5cGUvT2JqU3RtPj5zdHJlYW0N"
	+"CmjeslAwULCx0XfOL80rUTDU985MKY42NAIKBsXqh1QWpOoHJKanFtvZAQQYAN/6C60NCmVuZHN0"
	+"cmVhbQ1lbmRvYmoNMjYgMCBvYmoNPDwvRmlsdGVyL0ZsYXRlRGVjb2RlL0ZpcnN0IDkvTGVuZ3Ro"
	+"IDQyL04gMi9UeXBlL09ialN0bT4+c3RyZWFtDQpo3jJTMFAwVzC0ULCx0fcrzS2OBnENFIJi7eyA"
	+"IsH6LnZ2AAEGAI2FCDcNCmVuZHN0cmVhbQ1lbmRvYmoNMjcgMCBvYmoNPDwvRmlsdGVyL0ZsYXRl"
	+"RGVjb2RlL0ZpcnN0IDUvTGVuZ3RoIDEyMC9OIDEvVHlwZS9PYmpTdG0+PnN0cmVhbQ0KaN4yNFIw"
	+"ULCx0XfOzytJzSspVjAyBgoE6TsX5Rc45VdEGwB5ZoZGCuaWRrH6vqkpmYkYogGJRUCdChZgfUGp"
	+"xfmlRcmpxUAzA4ryk4NTS6L1A1zc9ENSK0pi7ez0g/JLEktSFQz0QyoLUoF601Pt7AACDADYoCeW"
	+"DQplbmRzdHJlYW0NZW5kb2JqDTIgMCBvYmoNPDwvTGVuZ3RoIDM1MjUvU3VidHlwZS9YTUwvVHlw"
	+"ZS9NZXRhZGF0YT4+c3RyZWFtDQo8P3hwYWNrZXQgYmVnaW49Iu+7vyIgaWQ9Ilc1TTBNcENlaGlI"
	+"enJlU3pOVGN6a2M5ZCI/Pgo8eDp4bXBtZXRhIHhtbG5zOng9ImFkb2JlOm5zOm1ldGEvIiB4Onht"
	+"cHRrPSJBZG9iZSBYTVAgQ29yZSA1LjQtYzAwNSA3OC4xNDczMjYsIDIwMTIvMDgvMjMtMTM6MDM6"
	+"MDMgICAgICAgICI+CiAgIDxyZGY6UkRGIHhtbG5zOnJkZj0iaHR0cDovL3d3dy53My5vcmcvMTk5"
	+"OS8wMi8yMi1yZGYtc3ludGF4LW5zIyI+CiAgICAgIDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0"
	+"PSIiCiAgICAgICAgICAgIHhtbG5zOnBkZj0iaHR0cDovL25zLmFkb2JlLmNvbS9wZGYvMS4zLyIK"
	+"ICAgICAgICAgICAgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIgogICAg"
	+"ICAgICAgICB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIKICAg"
	+"ICAgICAgICAgeG1sbnM6ZGM9Imh0dHA6Ly9wdXJsLm9yZy9kYy9lbGVtZW50cy8xLjEvIj4KICAg"
	+"ICAgICAgPHBkZjpQcm9kdWNlcj5BY3JvYmF0IERpc3RpbGxlciA2LjAgKFdpbmRvd3MpPC9wZGY6"
	+"UHJvZHVjZXI+CiAgICAgICAgIDx4bXA6Q3JlYXRlRGF0ZT4yMDA2LTAzLTA2VDE1OjA2OjMzLTA1"
	+"OjAwPC94bXA6Q3JlYXRlRGF0ZT4KICAgICAgICAgPHhtcDpDcmVhdG9yVG9vbD5BZG9iZVBTNS5k2"
	+"bGwgVmVyc2lvbiA1LjIuMjwveG1wOkNyZWF0b3JUb29sPgogICAgICAgICA8eG1wOk1vZGlmeURh"
	+"dGU+MjAxNi0wNy0xNVQxMDoxMjoyMSswODowMDwveG1wOk1vZGlmeURhdGU+CiAgICAgICAgIDx4"
	+"bXA6TWV0YWRhdGFEYXRlPjIwMTYtMDctMTVUMTA6MTI6MjErMDg6MDA8L3htcDpNZXRhZGF0YURh"
	+"dGU+CiAgICAgICAgIDx4bXBNTTpEb2N1bWVudElEPnV1aWQ6ZmYzZGNmZDEtMjNmYS00NzZmLTgz"
	+"OWEtM2U1Y2FlMmRhMmViPC94bXBNTTpEb2N1bWVudElEPgogICAgICAgICA8eG1wTU06SW5zdGFu"
	+"Y2VJRD51dWlkOjM1OTM1MGIzLWFmNDAtNGQ4YS05ZDZjLTAzMTg2YjRmZmIzNjwveG1wTU06SW5z"
	+"dGFuY2VJRD4KICAgICAgICAgPGRjOmZvcm1hdD5hcHBsaWNhdGlvbi9wZGY8L2RjOmZvcm1hdD4K"
	+"ICAgICAgICAgPGRjOnRpdGxlPgogICAgICAgICAgICA8cmRmOkFsdD4KICAgICAgICAgICAgICAg"
	+"PHJkZjpsaSB4bWw6bGFuZz0ieC1kZWZhdWx0Ij5CbGFuayBQREYgRG9jdW1lbnQ8L3JkZjpsaT4K"
	+"ICAgICAgICAgICAgPC9yZGY6QWx0PgogICAgICAgICA8L2RjOnRpdGxlPgogICAgICAgICA8ZGM6"
	+"Y3JlYXRvcj4KICAgICAgICAgICAgPHJkZjpTZXE+CiAgICAgICAgICAgICAgIDxyZGY6bGk+RGVw"
	+"YXJ0bWVudCBvZiBKdXN0aWNlIChFeGVjdXRpdmUgT2ZmaWNlIG9mIEltbWlncmF0aW9uIFJldmll"
	+"dyk8L3JkZjpsaT4KICAgICAgICAgICAgPC9yZGY6U2VxPgogICAgICAgICA8L2RjOmNyZWF0b3I+"
	+"CiAgICAgIDwvcmRmOkRlc2NyaXB0aW9uPgogICA8L3JkZjpSREY+CjwveDp4bXBtZXRhPgogICAg"
	+"ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg"
	+"ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAg"
	+"ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg"
	+"ICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg"
	+"ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg"
	+"ICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg"
	+"ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg"
	+"CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg"
	+"ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAg"
	+"ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg"
	+"ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAg"
	+"ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg"
	+"ICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg"
	+"ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg"
	+"ICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg"
	+"ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAg"
	+"ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg"
	+"ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgCjw/eHBhY2tldCBlbmQ9InciPz4NCmVuZHN0cmVhbQ1lbmRvYmoNMTEgMCBvYmoNPDwvTWV0YWRhdGEgMiAwIFIvUGFnZUxhYmVscyA2IDAgUi9QYWdlcyA4IDAgUi9UeXBlL0NhdGFsb2c+Pg1lbmRvYmoNMjMgMCBvYmoNPDwvRmlsdGVyL0ZsYXRlRGVjb2RlL0xlbmd0aCAxMD4+c3RyZWFtDQpIiQIIMAAAAAABDQplbmRzdHJlYW0NZW5kb2JqDTI4IDAgb2JqDTw8L0RlY29kZVBhcm1zPDwvQ29sdW1ucyA0L1ByZWRpY3RvciAxMj4+L0ZpbHRlci9GbGF0ZURlY29kZS9JRFs8REI3Nzc1Q0NFMjI3RjZCMzBDNDQwREY0MjIxREMzOTA+PEJGQ0NDRjNGNTdGNjEzNEFCRDNDMDRBOUU0Q0ExMDZFPl0vSW5mbyA5IDAgUi9MZW5ndGggODAvUm9vdCAxMSAwIFIvU2l6ZSAyOS9UeXBlL1hSZWYvV1sxIDIgMV0+PnN0cmVhbQ0KaN5iYgACJjDByGzIwPT/73koF0wwMUiBWYxA4v9/EMHA9I/hBVCxoDOQeH8DxH2KrIMIglFwIpD1vh5IMJqBxPpArHYgwd/KABBgAP8bEC0NCmVuZHN0cmVhbQ1lbmRvYmoNc3RhcnR4cmVmDQo0NTc2DQolJUVPRg0K";

	@UiField
	Viewer pdfViewer;

	@UiField
	Button saveButton;
	@UiField
	Button deleteButton;

	@UiField
	MenuItem printMenuItem;
	@UiField
	MenuItem publishMenuItem;
	@UiField
	MenuItem downloadMenuItem;

	@UiField
	MenuItem reduceMenuItem;
	@UiField
	MenuItem enlargeMenuItem;

	@UiField
	ListBox dateListBox;
	@UiField
	ListBox salaryTypeListBox;
	@UiField
	ListBox reportTypeListBox;
	
	@UiField
	Button publishButton;

	@UiField
	Button bidoqPublishButton;

	private int zoom = DEFAULT_ZOOM;

	private SalaryDocuments salaryDocuments;

	private List<Listener> listeners;

	private List<com.esferalia.aon.gwt.payroll.shared.Salary> salaries;
	private List<com.esferalia.aon.gwt.payroll.shared.Salary.Type> types;

	public Salary() {
		initWidget(binder.createAndBindUi(this));

		types = new ArrayList<com.esferalia.aon.gwt.payroll.shared.Salary.Type>(
				com.esferalia.aon.gwt.payroll.shared.Salary.Type.values().length);

		printMenuItem.setScheduledCommand(new Command() {

			@Override
			public void execute() {
				print();
			}
		});

		downloadMenuItem.setScheduledCommand(new Command() {

			@Override
			public void execute() {
				download();
			}
		});

		reduceMenuItem.setScheduledCommand(new Command() {

			@Override
			public void execute() {
				zoom = Math.max(MIN_ZOOM, zoom - ZOOM_STEP);
				viewPDF();
			}
		});

		enlargeMenuItem.setScheduledCommand(new Command() {

			@Override
			public void execute() {
				zoom = Math.min(MAX_ZOOM, zoom + ZOOM_STEP);
				viewPDF();
			}
		});

		publishMenuItem.setScheduledCommand(new ScheduledCommand() {

			@Override
			public void execute() {
				onPublish(salaryDocuments, "drive");
			}
		});
		
		listeners = new LinkedList<Listener>();

		publishButton.setVisible(!Wnd.getCurrentDomainNameURL().contains("ayudat"));
		bidoqPublishButton.setVisible(Wnd.getCurrentDomainNameURL().contains("ayudat"));

	}

	public void hideDeleteButton() {
		deleteButton.setVisible(false);
	}

	public void setSalaryDocuments(SalaryDocuments salaryDocuments) {
		this.salaryDocuments = salaryDocuments;
		onSalaryDocumentsChanged();
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	// ------------------------------------------------------------------------

	@UiHandler("deleteButton")
	void onDeleteButton(ClickEvent e) {
		delete();
	}

	@UiHandler("saveButton")
	void onSaveButton(ClickEvent e) {
		String fileName = getFileName();
		pdfViewer.download(fileName);
	}


	@UiHandler("dateListBox")
	void onDateListBox(ChangeEvent e) {
		onSalaryDateChanged();
		;
	}

	@UiHandler("salaryTypeListBox")
	void onSalaryTypeListBox(ChangeEvent e) {
		onSalaryTypeChanged();
	}

	@UiHandler("reportTypeListBox")
	void onReportTypeListBox(ChangeEvent e) {
		onReportTypeChanged();
	}

	@UiHandler("publishButton")
	void onPublishButtonClicked(ClickEvent e) {
		onPublish(salaryDocuments, "drive");
	}
	
	@UiHandler("bidoqPublishButton")
	void onBidoqPublishButtonClicked(ClickEvent e) {
		onPublish(salaryDocuments, "bidoq");
	}

	// ------------------------------------------------------------------------
	void onPublish(SalaryDocuments documents, String type) {
		for (Listener listener : listeners)
			listener.onPublis(salaryDocuments, type);
	}

	// ------------------------------------------------------------------------
	private void print() {
		salaryDocuments.print();
	}

	private void delete() {
		salaryDocuments.delete(new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void v) {
				onSalaryDocumentsChanged();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
		});

	}

	private void download() {
		salaryDocuments.download();
	}

	private void viewPDF() {
		String url = salaryDocuments.getDownloadURL("pdf");
		pdfViewer.setDocument(url, zoom/100.00);
		
	}

	private void onSalaryDocumentsChanged() {
		if (salaryDocuments.size() > 0) {
			viewPDF();
			syncSalaryTypeListBox();
			syncReportTypeListBox();
			saveButton.setEnabled(true);
			deleteButton.setEnabled(true);
		} else {
			pdfViewer.setDocument(BLANK_PDF_URL, zoom/100.00);
			dateListBox.clear();
			salaryTypeListBox.clear();
			saveButton.setEnabled(false);
			deleteButton.setEnabled(false);
		} // end : No salaries

	}

	private void onSalaryTypeChanged() {
		syncReportTypeListBox();
		selectNewestSalary(getSelectedType());
		syncSalaryDateListBox();
		onSalaryDateChanged();
	}

	private void onReportTypeChanged() {
		salaryDocuments.getCurrent().setType(getReportType());
		viewPDF();
	}

	private void onSalaryDateChanged() {
		com.esferalia.aon.gwt.payroll.shared.Salary currentSalary = salaries
				.get(dateListBox.getSelectedIndex());
		salaryDocuments.setCurrent(currentSalary);
		viewPDF();
	}

	private void syncSalaryTypeListBox() {
		types.clear();
		salaryTypeListBox.clear();

		com.esferalia.aon.gwt.payroll.shared.Salary currentSalary = getCurrentSalary();

		com.esferalia.aon.gwt.payroll.shared.Salary.Type currentType = currentSalary
				.getType();

		for (com.esferalia.aon.gwt.payroll.shared.Salary salary : getSalaries()) {
			com.esferalia.aon.gwt.payroll.shared.Salary.Type type = salary
					.getType();

			if (types.contains(type))
				continue;

			types.add(type);
			salaryTypeListBox.addItem(type.getDescription());
		}

		salaryTypeListBox.setSelectedIndex(types.indexOf(currentType));

		syncSalaryDateListBox();
	}

	private void syncReportTypeListBox() {
		reportTypeListBox.setVisible(false);
		salaryDocuments.getCurrent().setType(com.esferalia.aon.gwt.payroll.shared.Salary.Type.SALARY);
		getSelectedType().accept( new TypeVisitor<Void>() {

			@Override
			public Void visitSalary(Type type) {
				return null;
			}

			@Override
			public Void visitExtra(Type type) {
				return null;
			}

			@Override
			public Void visitSettle(Type type) {
				reportTypeListBox.clear();
				reportTypeListBox.addItem("CARTA", com.esferalia.aon.gwt.payroll.shared.Salary.Type.SETTLE.name());
				reportTypeListBox.addItem("N\u00D3MINA", com.esferalia.aon.gwt.payroll.shared.Salary.Type.SALARY.name());
				reportTypeListBox.setSelectedIndex(0);
				reportTypeListBox.setVisible(true);
				return null;
			}

			@Override
			public Void visitDelay(Type type) {
				return null;
			}

		});
	}

	private void syncSalaryDateListBox() {
		dateListBox.clear();

		com.esferalia.aon.gwt.payroll.shared.Salary currentSalary = getCurrentSalary();

		com.esferalia.aon.gwt.payroll.shared.Salary.Type type = getSelectedType();

		salaries = getSalaries(type);

		int index = 0;
		for (com.esferalia.aon.gwt.payroll.shared.Salary salary : salaries) {
			dateListBox.addItem(DATE_FORMAT.format(salary.getChargeDate()));
			if (salary.getEndDate().before(currentSalary.getChargeDate())) {
				index++;
			}
		}

		dateListBox.setSelectedIndex(index);
	}

	private com.esferalia.aon.gwt.payroll.shared.Salary.Type getReportType() {
		return com.esferalia.aon.gwt.payroll.shared.Salary.Type
		.valueOf(reportTypeListBox.getSelectedValue());
	}

	private com.esferalia.aon.gwt.payroll.shared.Salary.Type getSelectedType() {
		return types.get(salaryTypeListBox.getSelectedIndex());
	}

	private void selectNewestSalary(
			com.esferalia.aon.gwt.payroll.shared.Salary.Type type) {
		List<com.esferalia.aon.gwt.payroll.shared.Salary> salaries = getSalaries();
		for (int i = salaries.size() - 1; i >= 0; i--)
			if (salaries.get(i).getType() == type)
				salaryDocuments.setCurrentIndex(i);

	}

	private com.esferalia.aon.gwt.payroll.shared.Salary getCurrentSalary() {
		return salaryDocuments.getSalaries().get(
				salaryDocuments.getCurrentIndex());
	}

	private List<com.esferalia.aon.gwt.payroll.shared.Salary> getSalaries() {
		return salaryDocuments.getSalaries();
	}

	private List<com.esferalia.aon.gwt.payroll.shared.Salary> getSalaries(
			com.esferalia.aon.gwt.payroll.shared.Salary.Type type) {
		List<com.esferalia.aon.gwt.payroll.shared.Salary> salaries = new ArrayList<com.esferalia.aon.gwt.payroll.shared.Salary>();

		for (com.esferalia.aon.gwt.payroll.shared.Salary salary : getSalaries()) {
			if (salary.getType() == type) {
				salaries.add(salary);
			}
		}

		return salaries;
	}
	
	protected String getFileName() {
		com.esferalia.aon.gwt.payroll.shared.Salary salary = salaryDocuments.getCurrent();
		String fileName = 
				salary.getType().getDescription() + " " 
				+ DateTimeFormat.getFormat(PredefinedFormat.MONTH).format(salary.getChargeDate())
				+".pdf";
		return fileName;
	}
	

}
