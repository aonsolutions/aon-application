package com.esferalia.aon.gwt.fiscal.client.customer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomMultiSelectBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.customer.CustomersLinkedParams;
import com.esferalia.aon.occam.api.model.registry.DomainSigAddInfo;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Pestania "Integridad Estados": clientes SIG con dominio vinculado por
 * raddinfo, y si su estado esta alineado con el del dominio.
 *
 * A diferencia de "Vincular" no hay scroll infinito: para poder filtrar por
 * "solo desalineados" hay que diagnosticar todo el despacho. Se hace en una
 * sola peticion batch y se filtra en cliente.
 */
public class SigIntegrityTab extends ResizeComposite {
	
	/** Resultado por fila, para el informe final. */
	private static class AlignResult {
		Row row;
		String error;   // null = correcto
	}

	
	private static final String HOST_API_LOCAL = "localhost:8080";
	private static final String HOST_API = "aon.solutions";
	private static final String ENDPOINT_INTEGRITY = "/ms/api/domain/sig-integrity/";
	private static final String ENDPOINT_DOMAIN_STATUS = "/ms/api/customers/domainStatus/";

	/** Tope de filas pintadas. Con 1672 clientes el DOM se arrastra. */
	private static final int MAX_PAINTED_ROWS = 300;

	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("dd/MM/yyyy");

	private final CustomersLinkedParams params;
	private final CommonServiceAsync commonService;
	private final CustomerApi customerApi;

	private AonCustomDockLayout docklayoutPanel;
	private final HTMLPanel messagePanel = new HTMLPanel("");
	private final ScrollPanel tableScroll = new ScrollPanel();
	private AonCustomTable tab;

	private final AonCustomMultiSelectBox customerStatus = new AonCustomMultiSelectBox("Estado Cliente");
	private final AonCustomListBox integrity = new AonCustomListBox("Integridad");

	/** Todas las filas diagnosticadas, sin filtrar. */
	private final List<Row> rows = new ArrayList<Row>();

	private Byte[] loadedStatus;
	private String loadedQuery;
	
	private boolean loaded = false;
	private boolean isLocal = false;
	
	/** Filas marcadas para alinear. Se mantiene entre repintados. */
	private final Set<Row> selected = new HashSet<Row>();

	private CheckBox selectAllBox;
	private AonTableButton alignSelectedButton;
	private Label selectionLabel;

	/** Fila de pantalla: cliente + una entrada de raddinfo + su diagnostico. */
	static class Row {
		Customer customer;
		DomainSigAddInfo raddInfo;
		SigIntegrityRow diagnosis;
	}

	public SigIntegrityTab(CustomersLinkedParams params, String sessionApi) {
		this.params = params;

		CommonServiceAsync raw = GWT.create(CommonService.class);
		this.commonService = new CommonServiceAsyncDecorator(raw);
		this.customerApi = new CustomerApi(sessionApi);

		build();
		initWidget(docklayoutPanel);
	}

	private void build() {
		docklayoutPanel = new AonCustomDockLayout("Integridad de estados (SIG)") {
			@Override
			protected void onClearFilter() {
				getSearchTextBox().setValue(null, false);
				selectAllStatus();
				integrity.setValue("1");
				reload();
			}
		};

		docklayoutPanel.setSearchPlaceholder("Busqueda por documento/nombre...");

		Set<String> statusOptions = new LinkedHashSet<String>();
		statusOptions.add("Activo");
		statusOptions.add("Inactivo");
		statusOptions.add("Bloqueado");
		customerStatus.setOptions(statusOptions);
		selectAllStatus();

		integrity.clearItems();
		integrity.addItem("Solo desalineados", "1");
		integrity.addItem("Solo alineados", "2");
		integrity.addItem("Todos", "");
		integrity.setValue("1");

		docklayoutPanel.addFilterWidget(customerStatus);
		docklayoutPanel.addFilterWidget(integrity);

		// Igual que en Vincular: se busca al cerrar el panel, no en cada cambio
		docklayoutPanel.addOnSearchHandler(e -> {
		    // el filtro de integridad se resuelve en cliente; estado y texto exigen
		    // volver a pedir los clientes al servidor
		    if (filtersChanged()) reload();
		    else paintTable();
		});
		
		selectionLabel = new Label();
		docklayoutPanel.addToolbarButtonStart(selectionLabel);

		alignSelectedButton = new AonTableButton("Alinear seleccionados", AON.CSS.aonIconSync());
		alignSelectedButton.addClickHandler(e -> confirmAlignSelected());
		docklayoutPanel.addToolbarButton(alignSelectedButton);

		updateSelectionUi();

		HTMLPanel container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		container.add(messagePanel);

		tableScroll.setHeight("100%");
		container.add(tableScroll);

		docklayoutPanel.add(container);
	}
	
	private void updateSelectionUi() {
		alignSelectedButton.setVisible(!selected.isEmpty());
		selectionLabel.setText(selected.isEmpty()
				? AonStringUtils.EMPTY
				: selected.size() + " seleccionada(s)");
	}
	
	private boolean filtersChanged() {
	    Byte[] status = mapStatus(customerStatus.getSelectedOptions());
	    String query = docklayoutPanel.getSearchTextBox().getValue();

	    boolean changed = !Arrays.equals(status, loadedStatus)
	            || !AonStringUtils.equals(AonStringUtils.trimToEmpty(query),
	                                      AonStringUtils.trimToEmpty(loadedQuery));

	    loadedStatus = status;
	    loadedQuery = query;
	    return changed;
	}

	private void selectAllStatus() {
		Set<String> selected = new LinkedHashSet<String>();
		selected.add("Activo");
		selected.add("Inactivo");
		selected.add("Bloqueado");
		customerStatus.setSelectedOptions(selected);
	}

	public void ensureLoaded() {
		if (loaded)
			return;
		loaded = true;
		reload();
	}

	// ------------------------------------------------------------------ carga

	/** Recarga completa: clientes -> raddinfo -> diagnostico. */
	private void reload() {
		rows.clear();
		paintTable();

		AonMessagePanel.showLoading(messagePanel, "Obteniendo clientes...");

		params.setSig(true);
		params.setOffset(0);
		params.setLimit(Integer.MAX_VALUE);
		params.setQuery(docklayoutPanel.getSearchTextBox().getValue());
		params.setCustomerStatus(mapStatus(customerStatus.getSelectedOptions()));
		// esta pestania solo mira clientes CON raddinfo
		params.setNoRaddInfo(false);
		params.setNoAonCustomer(false);

		commonService.getCustomersLinked(params, new AsyncCallback<List<Customer>>() {

			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo clientes : " + caught.getMessage());
			}

			@Override
			public void onSuccess(List<Customer> customers) {
				if (customers.isEmpty()) {
					AonMessagePanel.hideMessage(messagePanel);
					paintTable();
					return;
				}
				loadRaddInfo(customers);
			}
		});
	}

	private void loadRaddInfo(List<Customer> customers) {
		AonMessagePanel.showLoading(messagePanel,
				"Obteniendo la vinculaci\u00f3n de " + customers.size() + " clientes...");

		ArrayList<Integer> customerIds = new ArrayList<Integer>();
		for (Customer c : customers)
			customerIds.add(c.getId());

		commonService.getDomainSigAddInfo(params.getDomainName(), params.getDomainId(), params.getUser(), customerIds,
				new AsyncCallback<HashMap<Integer, List<DomainSigAddInfo>>>() {

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel,
								"Error obteniendo la vinculaci\u00f3n (raddinfo) : " + caught.getMessage());
					}

					@Override
					public void onSuccess(HashMap<Integer, List<DomainSigAddInfo>> raddInfos) {
						List<Row> pending = new ArrayList<Row>();

						for (Customer customer : customers) {
							List<DomainSigAddInfo> list = raddInfos.get(customer.getId());
							if (null == list)
								continue;

							for (DomainSigAddInfo raddInfo : list) {
								// sin nombre no se puede alcanzar el dominio destino
								if (AonStringUtils.isBlank(raddInfo.getDomainName())
										|| AonStringUtils.equals("null", raddInfo.getDomainName()))
									continue;

								Row row = new Row();
								row.customer = customer;
								row.raddInfo = raddInfo;
								pending.add(row);
							}
						}

						if (pending.isEmpty()) {
							AonMessagePanel.hideMessage(messagePanel);
							paintTable();
							return;
						}

						diagnose(pending);
					}
				});
	}

	private void diagnose(List<Row> pending) {
		AonMessagePanel.showLoading(messagePanel,
				"Comprobando el estado de " + pending.size() + " dominios. Este proceso puede llevar unos segundos...");

		JSONArray entries = new JSONArray();
		for (int i = 0; i < pending.size(); i++)
			entries.set(i, toJSON(pending.get(i)));

		JSONObject body = new JSONObject();
		body.put("rows", entries);

		customerApi.getSigIntegrity(isLocal ? HOST_API_LOCAL : HOST_API, ENDPOINT_INTEGRITY, body, new AsyncCallback<List<SigIntegrityRow>>() {

			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel,
						"Error comprobando la integridad de estados : " + caught.getMessage());
			}

			@Override
			public void onSuccess(List<SigIntegrityRow> diagnosed) {
				// el servidor responde en el mismo orden que se envio
				int size = Math.min(pending.size(), diagnosed.size());
				for (int i = 0; i < size; i++) {
					Row row = pending.get(i);
					row.diagnosis = diagnosed.get(i);
					rows.add(row);
				}

				AonMessagePanel.hideMessage(messagePanel);
				paintTable();
			}
		});
	}

	private JSONObject toJSON(Row row) {
		JSONObject entry = new JSONObject();

		entry.put("customer", new JSONNumber(row.customer.getId()));
		entry.put("domainName", new JSONString(row.raddInfo.getDomainName()));

		// domainId puede venir vacio o como el literal "null": el servidor
		// resuelve entonces por domainName
		Integer domainId = toInteger(row.raddInfo.getDomainId());
		entry.put("domainId", null == domainId ? JSONNull.getInstance() : new JSONNumber(domainId));

		entry.put("schema", AonStringUtils.isBlank(row.raddInfo.getDomainSchema()) ? JSONNull.getInstance()
				: new JSONString(row.raddInfo.getDomainSchema()));

		entry.put("status", new JSONString(row.customer.getStatus().name()));

		Date expiration = row.customer.getExpirationDate();
		entry.put("expirationDate", null == expiration ? JSONNull.getInstance() : new JSONNumber(expiration.getTime()));

		return entry;
	}

	private static Integer toInteger(String value) {
		if (AonStringUtils.isBlank(value) || AonStringUtils.equals("null", value))
			return null;
		try {
			return Integer.valueOf(value.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private static Byte[] mapStatus(Set<String> selectedOptions) {
		ArrayList<Byte> result = new ArrayList<Byte>();

		if (null == selectedOptions || selectedOptions.isEmpty())
			return new Byte[] { 0, 1, 2 };

		if (selectedOptions.contains("Activo"))
			result.add((byte) 0);
		if (selectedOptions.contains("Inactivo"))
			result.add((byte) 1);
		if (selectedOptions.contains("Bloqueado"))
			result.add((byte) 2);

		return result.toArray(new Byte[0]);
	}

	// ---------------------------------------------------------------- pintado

	private enum COLS {

		SEL(AonStringUtils.EMPTY,	"3rem",				"min-width: 3rem !important; justify-content: center;"),
		IDC("Cliente", 				"5rem",				"min-width: 3rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		DOC("Documento", 			"6rem",				"min-width: 4rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		DES("Nombre", 				"-moz-available",	"min-width: 8rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		CST("Estado cliente", 		"7rem",				"min-width: 4rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		CFE("F. Cliente", 			"6rem",				"min-width: 4.5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		DOM("Dominio", 				"14rem",			"min-width: 6rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		DST("Estado dominio", 		"12rem",			"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		DIA("Diagn\u00f3stico", 	"18rem",			"min-width: 8rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		BTN(AonStringUtils.EMPTY, 	"5rem",				"min-width: 5rem; justify-content: center;")
		;

		final String headerLabel;
		final String colWidth;
		final String styles;

		COLS(String headerLabel, String colWidth, String styles) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.styles = styles;
		}
	}

	private void paintTable() {
		tab = new AonCustomTable();
		tableScroll.setWidget(tab);
		
		tab.createHeader();
		for (COLS col : COLS.values()) {
			if (COLS.SEL == col) {
				selectAllBox = new CheckBox();
				selectAllBox.addValueChangeHandler(e -> onSelectAll(Boolean.TRUE.equals(e.getValue())));
				tab.addHeader(selectAllBox, col.colWidth, col.styles);
			} else {
				Label header = new Label(col.headerLabel);
		        header.setTitle(col.headerLabel);
				tab.addHeader(header, col.colWidth, col.styles);
			}
		}

		List<Row> visible = filter();

		if (visible.isEmpty()) {
			paintMessageRow("No existen clientes para la busqueda");
			return;
		}

		int painted = Math.min(visible.size(), MAX_PAINTED_ROWS);
		for (int i = 0; i < painted; i++)
			paintRow(visible.get(i));

		if (visible.size() > painted)
			paintMessageRow("Se muestran " + painted + " de " + visible.size()
					+ " filas. Afina la busqueda para ver el resto.");
		
		selected.retainAll(rows);
		if (null != selectAllBox) selectAllBox.setValue(false, false);
		updateSelectionUi();
	}
	
	/** Marca o desmarca solo lo visible y pintado, no las filas ocultas por el tope. */
	private void onSelectAll(boolean value) {
		List<Row> visible = filter();
		int painted = Math.min(visible.size(), MAX_PAINTED_ROWS);

		for (int i = 0; i < painted; i++) {
			Row row = visible.get(i);
			if (!isAlignable(row)) continue;
			if (value) selected.add(row);
			else selected.remove(row);
		}

		paintTable();
		updateSelectionUi();
	}

	/** Filtra en cliente sobre lo ya diagnosticado; no vuelve a pedir nada. */
	private List<Row> filter() {
		String mode = integrity.getValue();
		List<Row> result = new ArrayList<Row>();

		for (Row row : rows) {
			if (null == row.diagnosis)
				continue;

			boolean aligned = row.diagnosis.isAligned();

			if (AonStringUtils.equalsIgnoreCase(mode, "1") && aligned)
				continue;
			if (AonStringUtils.equalsIgnoreCase(mode, "2") && !aligned)
				continue;

			result.add(row);
		}
		return result;
	}

	private void paintMessageRow(String message) {
		HTMLPanel row = tab.createRow();
		Label label = new Label(message);
		label.setTitle(message);
		tab.addRow(row, label, COLS.DES.colWidth, COLS.DES.styles);
	}

	private void paintRow(Row row) {
		Customer customer = row.customer;
		SigIntegrityRow diagnosis = row.diagnosis;

		HTMLPanel tableRow = tab.createRow();

		if (isAlignable(row)) {
			CheckBox check = new CheckBox();
			check.setValue(selected.contains(row));
			check.addValueChangeHandler(e -> {
				if (Boolean.TRUE.equals(e.getValue())) selected.add(row);
				else selected.remove(row);
				updateSelectionUi();
			});
			addCell(tableRow, check, COLS.SEL);
		} else {
			addCell(tableRow, new Label(), COLS.SEL);
		}

		addCell(tableRow, new Label(customer.getId().toString()), COLS.IDC);
		addCell(tableRow, new Label(customer.getDocument()), COLS.DOC);
		addCell(tableRow, titled(customer.getName()), COLS.DES);
		addCell(tableRow, new Label(customer.getStatus().getDescription()), COLS.CST);
		addCell(tableRow, new Label(format(customer.getExpirationDate())), COLS.CFE);

		String domainText = row.raddInfo.getDomainName()
				+ (null == diagnosis.getDomainId() ? "" : " [" + diagnosis.getDomainId() + "]");
		Label domain = new Label(domainText);
		domain.setTitle(
				domainText + (AonStringUtils.isBlank(row.raddInfo.getDomainSchema()) ? " (sin schema en raddinfo)"
						: " -- " + row.raddInfo.getDomainSchema()));
		addCell(tableRow, domain, COLS.DOM);

		addCell(tableRow, titled(domainStatusText(diagnosis)), COLS.DST);
		addCell(tableRow, titled(diagnosis.getDiagnosis()), COLS.DIA);

		HTMLPanel options = new HTMLPanel(AonStringUtils.EMPTY);
		options.addStyleName(AON.CSS.aonItemFlex());

		if (diagnosis.isFound() && !diagnosis.isAligned()) {
			AonTableButton align = new AonTableButton("Alinear", AON.CSS.aonIconSync());
			align.addClickHandler(e -> {
				e.stopPropagation();
				confirmAlign(java.util.Collections.singletonList(row), true);
			});
			options.add(align);
		}

		// El desajuste de aonCustomer pertenece a "Vincular": solo se avisa
		if (diagnosis.isAonCustomerMismatch()) {
			AonTableButton warning = new AonTableButton("El cliente del dominio no coincide con el seleccionado"
					+ (null == diagnosis.getDomainAonCustomer() ? " (el dominio no tiene cliente)"
							: " (dominio: " + diagnosis.getDomainAonCustomer() + ")"),
					AON.CSS.aonIconInfo());
			warning.getElement().getStyle().setProperty("background-size", "22px");
			options.add(warning);
		}

		addCell(tableRow, options, COLS.BTN);
	}
	
	private void confirmAlignSelected() {
		if (selected.isEmpty()) return;
		confirmAlign(new ArrayList<Row>(selected), false);
	}

	private void confirmAlign(List<Row> target, boolean single) {
		int turningOff = 0;
		int turningOn = 0;
		int losingAccess = 0;

		for (Row row : target) {
			SigIntegrityRow d = row.diagnosis;

			if (d.isDomainActive() && !d.isTargetDomainActive()) turningOff++;
			if (!d.isDomainActive() && d.isTargetDomainActive()) turningOn++;

			// accesible hoy y con fecha nueva ya vencida, o apagandose
			if (d.isDomainAccessible() && !d.isTargetDomainActive()) losingAccess++;
		}

		HTMLPanel content = new HTMLPanel("");
		content.addStyleName(AON.CSS.aonFlexColumn());

		content.add(new Label(single
				? "Se va a alinear el estado del dominio " + target.get(0).raddInfo.getDomainName() + "."
				: "Se van a alinear " + target.size() + " dominios."));

		if (turningOff > 0)
			content.add(new Label("Se apagaran " + turningOff + " dominio(s)."));
		if (turningOn > 0)
			content.add(new Label("Se encenderan " + turningOn + " dominio(s)."));
		if (losingAccess > 0)
			content.add(new Label("Los usuarios de " + losingAccess
					+ " dominio(s) perderan el acceso."));

		AonDialog dialog = new AonDialog("Alinear estados", content);
		dialog.confirm(new AonAcceptDialogCallback() {
			@Override public void onCancel() {}
			@Override public void onAccept() {
				dialog.hide();
				runAlign(target);
			}
		});
	}
	
	/** Solo se puede alinear lo que se ha encontrado y esta desalineado. */
	private boolean isAlignable(Row row) {
		return null != row.diagnosis && row.diagnosis.isFound() && !row.diagnosis.isAligned();
	}

	private String domainStatusText(SigIntegrityRow diagnosis) {
		if (!diagnosis.isFound())
			return "No encontrado";

		String text = diagnosis.isDomainActive() ? "Activo" : "Apagado";

		if (null != diagnosis.getDomainExpirationDate())
			text += " hasta " + format(diagnosis.getDomainExpirationDate());

		return text + (diagnosis.isDomainAccessible() ? "" : " (sin acceso)");
	}

	private Label titled(String text) {
		Label label = new Label(null == text ? AonStringUtils.EMPTY : text);
		if (AonStringUtils.isNotBlank(text))
			label.setTitle(text);
		return label;
	}

	private void addCell(HTMLPanel tableRow, com.google.gwt.user.client.ui.Widget widget, COLS col) {
		tab.addRow(tableRow, widget, col.colWidth, col.styles);
	}

	private static String format(Date date) {
		return null == date ? AonStringUtils.EMPTY : DATE_FORMAT.format(date);
	}
	


	private void runAlign(List<Row> target) {
		List<AlignResult> results = new ArrayList<AlignResult>();
		alignNext(target, 0, results);
	}

	private void alignNext(List<Row> target, int index, List<AlignResult> results) {
		if (index >= target.size()) {
			AonMessagePanel.hideMessage(messagePanel);
			selected.clear();
			showReport(results);
			reload();
			return;
		}

		Row row = target.get(index);

		AonMessagePanel.showLoading(messagePanel, "Alineando " + (index + 1) + " de " + target.size()
				+ " (" + row.raddInfo.getDomainName() + ")...");

		alignRow(row, error -> {
			AlignResult result = new AlignResult();
			result.row = row;
			result.error = error;
			results.add(result);

			// una fila que falla no aborta el lote
			alignNext(target, index + 1, results);
		});
	}

	/** Primero el dominio, y solo si va bien, la fecha del cliente. */
	private void alignRow(Row row, Consumer<String> done) {
		SigIntegrityRow diagnosis = row.diagnosis;

		if (!diagnosis.isDomainNeedsUpdate()) {
			alignCustomer(row, done);
			return;
		}

		JSONObject body = new JSONObject();
		body.put("domainId", new JSONNumber(diagnosis.getDomainId()));
		body.put("domainName", new JSONString(row.raddInfo.getDomainName()));
		body.put("status", new JSONString(row.customer.getStatus().name()));
		body.put("expirationDate", null == diagnosis.getTargetDate()
				? JSONNull.getInstance()
				: new JSONString(DATE_FORMAT.format(diagnosis.getTargetDate())));

		// el dominio destino vive en otro schema: se ataca por su propio host
		customerApi.saveCustomerDomainStatus(isLocal ? row.raddInfo.getDomainName() + ":8080" : row.raddInfo.getDomainName(), ENDPOINT_DOMAIN_STATUS, body,
				new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				done.accept("dominio: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				alignCustomer(row, done);
			}
		});
	}

	private void alignCustomer(Row row, Consumer<String> done) {
		if (!row.diagnosis.isCustomerNeedsUpdate()) {
			done.accept(null);
			return;
		}

		commonService.alignSigCustomerDate(params.getDomainName(), params.getDomainId(), params.getUser(),
				row.customer.getId(), row.diagnosis.getTargetDate(), new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// el dominio ya se actualizo: queda desalineado al reves
				done.accept("cliente (el dominio SI se actualizo): " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				done.accept(null);
			}
		});
	}

	private void showReport(List<AlignResult> results) {
		int ok = 0;
		List<AlignResult> failed = new ArrayList<AlignResult>();

		for (AlignResult r : results) {
			if (null == r.error) ok++;
			else failed.add(r);
		}

		HTMLPanel content = new HTMLPanel("");
		content.addStyleName(AON.CSS.aonFlexColumn());
		content.add(new Label("Alineados correctamente: " + ok));

		if (!failed.isEmpty()) {
			content.add(new Label("Con errores: " + failed.size()));

			ScrollPanel scroll = new ScrollPanel();
			scroll.setHeight("12rem");

			HTMLPanel list = new HTMLPanel("");
			list.addStyleName(AON.CSS.aonFlexColumn());

			for (AlignResult r : failed) {
				String text = r.row.customer.getName() + " (" + r.row.customer.getId() + ") -- "
						+ r.row.raddInfo.getDomainName() + " : " + r.error;
				Label label = new Label(text);
				label.setTitle(text);
				list.add(label);
			}

			scroll.setWidget(list);
			content.add(scroll);
		}

		AonDialog dialog = new AonDialog("Resultado de la alineaci\u00f3n", content);
		dialog.showCloseButton(true);
		dialog.info();
	}
	
}
