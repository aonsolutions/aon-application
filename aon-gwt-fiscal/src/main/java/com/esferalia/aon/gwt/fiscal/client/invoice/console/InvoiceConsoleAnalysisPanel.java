package com.esferalia.aon.gwt.fiscal.client.invoice.console;

import java.util.HashMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonGroupPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerLabel;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCollectionInfo;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceConsoleAnalysis;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class InvoiceConsoleAnalysisPanel extends SimpleLayoutPanel {

	private static final String SIN_ESTADO = "Sin estado";
	private static final String SIN_COMUNICACION = "Sin comunicaci\u00F3n";

	private final FlowPanel container = new FlowPanel();

	public InvoiceConsoleAnalysisPanel() {
		container.setStyleName(AON.CSS.aonPadding());
		ScrollPanel scroll = new ScrollPanel();
		scroll.setWidget(container);
		setWidget(scroll);
	}

	public void paint(InvoiceConsoleAnalysis ica) {
		container.clear();

		boolean something = paintTotals(ica);
		something = paintTypes(ica) || something;

		if (!something) {
			Label noInfo = new Label("No hay informaci\u00F3n disponible para la selecci\u00F3n de facturas");
			noInfo.setStyleName(AON.CSS.aonBlockCenter());
			noInfo.addStyleName(AON.CSS.aonMarginTop());
			noInfo.addStyleName(AON.CSS.aonBold());
			container.add(noInfo);
		}
	}

	// ----------------------------------------------------------- [RESUMEN]

	private boolean paintTotals(InvoiceConsoleAnalysis ica) {
		boolean facturas = has(ica.getInvoiceInfo());
		boolean proformas = has(ica.getProformaInfo());
		if (!facturas && !proformas) return false;

		AonDisplayGrid grid = newGrid("Concepto");
		if (facturas) addInfoRow(grid, "Facturas", ica.getInvoiceInfo());
		if (proformas) addInfoRow(grid, "Proformas", ica.getProformaInfo());
		container.add(AonGroupPanel.get("Resumen", grid));
		return true;
	}

	private boolean has(InvoiceCollectionInfo info) {
		return info != null && info.getTotalCount() > 0;
	}

	// ---------------------------------------------------------- [DESGLOSE]

	private boolean paintTypes(InvoiceConsoleAnalysis ica) {
		boolean something = false;
		// Orden estable: primero los tipos declarados, al final lo no comunicado.
		for (InvoiceCommunicationType type : InvoiceCommunicationType.values()) {
			something = paintType(ica, type) || something;
		}
		something = paintType(ica, null) || something;
		return something;
	}

	private boolean paintType(InvoiceConsoleAnalysis ica, InvoiceCommunicationType type) {
		HashMap<InvoiceCommunicationStatus, InvoiceCollectionInfo> statusMap = ica.getTypesInfo().get(type);
		if (statusMap == null || statusMap.isEmpty()) return false;

		AonDisplayGrid grid = newGrid("Estado");

		int totalCount = 0;
		double totalAmount = 0;
		// Orden estable: por el orden natural del estado, y lo desconocido al final.
		for (InvoiceCommunicationStatus status : InvoiceCommunicationStatus.values()) {
			InvoiceCollectionInfo info = statusMap.get(status);
			if (info == null) continue;
			addInfoRow(grid, status.getDescription(), info);
			totalCount += info.getTotalCount();
			totalAmount += info.getTotalAmount();
		}
		InvoiceCollectionInfo sinEstado = statusMap.get(null);
		if (sinEstado != null) {
			addInfoRow(grid, SIN_ESTADO, sinEstado);
			totalCount += sinEstado.getTotalCount();
			totalAmount += sinEstado.getTotalAmount();
		}

		if (statusMap.size() > 1) {
			grid.addFooterRow()
				.addCell(bold("Total"), AON.CSS.aonWidth200())
				.addCell(bold(new AonIntegerLabel(totalCount)), AON.CSS.aonWidth100(), AON.CSS.aonTextRight())
				.addCell(bold(new AonDoubleLabel(totalAmount)), AON.CSS.aonWidth150(), AON.CSS.aonTextRight())
			;
		}

		String titulo = type == null ? SIN_COMUNICACION : type.getDescription();
		container.add(AonGroupPanel.get(titulo, grid));
		return true;
	}

	// ------------------------------------------------------------ [COMUNES]

	private AonDisplayGrid newGrid(String primeraColumna) {
		AonDisplayGrid grid = new AonDisplayGrid();
		grid.setWidth("100%");
		grid.addHeaderRow()
			.addCell(new Label(primeraColumna), AON.CSS.aonWidth200())
			.addCell(new Label("Facturas"), AON.CSS.aonWidth100(), AON.CSS.aonTextRight())
			.addCell(new Label("Importe"), AON.CSS.aonWidth150(), AON.CSS.aonTextRight())
		;
		return grid;
	}

	private void addInfoRow(AonDisplayGrid grid, String etiqueta, InvoiceCollectionInfo info) {
		grid.addRow()
			.addCell(new Label(etiqueta), AON.CSS.aonWidth200())
			.addCell(new AonIntegerLabel(info.getTotalCount()), AON.CSS.aonWidth100(), AON.CSS.aonTextRight())
			.addCell(new AonDoubleLabel(info.getTotalAmount()), AON.CSS.aonWidth150(), AON.CSS.aonTextRight())
		;
	}

	private Widget bold(String texto) {
		InlineLabel label = new InlineLabel(texto);
		label.setStyleName(AON.CSS.aonBold());
		return label;
	}

	private Widget bold(Widget widget) {
		widget.addStyleName(AON.CSS.aonBold());
		return widget;
	}

}
