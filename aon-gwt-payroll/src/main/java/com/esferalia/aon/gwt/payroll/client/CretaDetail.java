package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.payroll.shared.CretaService;
import com.esferalia.aon.gwt.payroll.shared.CretaService.File;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsBasesResult;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsFile;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsRespuesta;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsTrabajadoresYTramos;
import com.google.api.client.util.Sleeper;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public abstract class CretaDetail extends Composite {

	private static CretaDetailUiBinder uiBinder = GWT
			.create(CretaDetailUiBinder.class);

	interface CretaDetailUiBinder extends UiBinder<Widget, CretaDetail> {
	}


	@UiField
	FormPanel formPanel;
	@UiField
	FileUpload fileUpload;

	@UiField
	Button msjRecButton;
	@UiField
	MenuItem msjRecMenuItem;

	@UiField
	Button basesButton;
	@UiField
	MenuItem basesMenuItem;

	@UiField
	Button trabajadoresYTramosButton;
	@UiField
	MenuItem trabajadoresYTramosMenuItem;

	@UiField
	Button borradorButton;
	@UiField
	MenuItem borradorMenuItem;

	@UiField
	Button confirmacionButton;
	@UiField
	MenuItem confirmacionMenuItem;

	@UiField(provided = true)
	DataGrid<JsFile> dataGrid;

	
	private PopupPanel popupTooltip;
	private Timer jsFileToolTipTimer;
	private MultiSelectionModel<JsFile> selectionModel;
	private Map<String, CretaService.JsRespuesta> respuestasMap;

	public CretaDetail() {
		ProvidesKey<JsFile> keyProvider = new HasIdKeyProvider<JsFile>();
		dataGrid = new CustomDataGrid<JsFile>(keyProvider);

		initWidget(uiBinder.createAndBindUi(this));

		selectionModel = new MultiSelectionModel<JsFile>(keyProvider);

		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<JsFile> createCheckboxManager(0));

		// init MSJREC Command
		ScheduledCommand msjRecCommand = new ScheduledCommand() {
			@Override
			public void execute() {
				fileUpload.click();
			}
		};
		msjRecMenuItem.setScheduledCommand(msjRecCommand);

		dataGrid.addColumn(new Column<JsFile, Boolean>(new CheckboxCell()) {
			@Override
			public Boolean getValue(JsFile jsFile) {
				return CretaDetail.this.selectionModel.isSelected(jsFile);
			}
		}, new SelectAllHeader<JsFile>(selectionModel, dataGrid));

		dataGrid.setColumnWidth(0, "40px");

		dataGrid.addColumn(new JsFileColumn() {

			@Override
			void onJsFileOut(JsFile jsFile, NativeEvent event) {
				CretaDetail.this.onJsFileOut();
			}

			@Override
			void onJsFileOver(JsFile jsFile, NativeEvent event) {
				CretaDetail.this.onJsFileOver(jsFile.getId(),
						event.getClientX(), event.getClientY());
			}

			@Override
			void onJsFileClick(JsFile jsFile, NativeEvent event) {
				CretaDetail.this.onJsFileClick(jsFile.getId(),
						event.getClientX(), event.getClientY());

			}

			@Override
			String getIconStyle(JsTrabajadoresYTramos jsTrabajadoresYTramos) {
				return CretaDetail.this.getIconStyle(jsTrabajadoresYTramos);
			}

			@Override
			String getDescription(JsTrabajadoresYTramos jsTrabajadoresYTramos) {
				return CretaDetail.this
						.getDescription(jsTrabajadoresYTramos.getCCC());
			}

		});

		selectionModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				boolean selected = CretaDetail.this.selectionModel
						.getSelectedSet().size() > 0;
				CretaDetail.this.basesButton.setEnabled(selected);
				
			}
		});

		dataGrid.setRowData(new ArrayList<CretaService.JsFile>(0));

		respuestasMap = new HashMap<String, CretaService.JsRespuesta>();

		fileUpload.getElement().setPropertyString("multiple", "multiple");

	}
	
	Set<JsFile> getSelected() {
		return selectionModel.getSelectedSet();
	}

	// ------------------------------------------------------------- UIHandlers

	@UiHandler("fileUpload")
	void onFileUploadChange(ChangeEvent event) {
		formPanel.submit();
	}

	@UiHandler("msjRecButton")
	void onClickMsjRecButton(ClickEvent e) {
		fileUpload.click();
	}

	@UiHandler("basesButton")
	void onClickBasesButton(ClickEvent e) {
		submitBases();
	}

	@UiHandler("borradorButton")
	void onClickBorradorButton(ClickEvent e) {
	}

	@UiHandler("confirmacionButton")
	void onClickConfirmacionButton(ClickEvent e) {
	}

	@UiHandler("trabajadoresYTramosButton")
	void onClickTrabajadoresYTramosButton(ClickEvent e) {
	}

	// ------------------------------------------------------------------------

	public void onTrabajadoresYTramos() {
		onTrabajadoresYTramos(new JsTrabajadoresYTramos[0], new JsRespuesta[0]);
	}

	public void onTrabajadoresYTramos(
			CretaService.JsTrabajadoresYTramos trabajadoresYTramos[],
			CretaService.JsRespuesta respuestas[]) {

		try {
			respuestasMap = MainCreta.add(File.RESPUESTA, respuestas);

			Map<String, JsTrabajadoresYTramos> trabajadoresYTramosMap = MainCreta
					.add(File.TRABAJADORES_TRAMOS, trabajadoresYTramos);

			List<JsTrabajadoresYTramos> filtered = filter(
					trabajadoresYTramosMap.values());
			dataGrid.setRowData(filtered);

		} catch (UnsupportedOperationException e) {

			respuestasMap = new HashMap<String, JsRespuesta>();
			for (CretaService.JsRespuesta respuesta : respuestas)
				respuestasMap.put(respuesta.getCCC() + respuesta.getFrom(),
						respuesta);

			List<JsTrabajadoresYTramos> filtered = filter(
					Arrays.asList(trabajadoresYTramos));
			dataGrid.setRowData(filtered);
		}

	}

	// ------------------------------------------------------------------------

	@Override
	protected void onAttach() {
		exportSubmitComplete();
		super.onAttach();
	}
	
	protected abstract void onBases(JsBasesResult result);
	
	protected abstract String getDescription(String ccc);

	protected abstract <T extends JsFile> List<T> filter(Collection<T> jsFiles);
	// ------------------------------------------------------------------------

	private void onJsFileOut() {
		if (jsFileToolTipTimer != null)
			jsFileToolTipTimer.cancel();
	}

	private void onJsFileOver(final String key, final int x, final int y) {
		if (jsFileToolTipTimer != null)
			jsFileToolTipTimer.cancel();

		jsFileToolTipTimer = new Timer() {

			@Override
			public void run() {

				if (CretaDetail.this.popupTooltip != null)
					CretaDetail.this.popupTooltip.hide();

				CretaDetail.this.popupTooltip = MainCreta
						.showjsRespuestaToolTip(respuestasMap.get(key), x, y);
			}
		};

		jsFileToolTipTimer.schedule(1000);
	}

	private void onJsFileClick(final String key, final int x, final int y) {
		// NOOP
	}

	private String getIconStyle(JsTrabajadoresYTramos t) {
		return MainCreta.getIconStyle(respuestasMap.get(t.getId()));
	}

	private void submitBases() {
		MainCreta.submit(CretaService.CRETA_URL + "/" + CretaService.File.BASES,
				selectionModel.getSelectedSet(), 
				new AsyncCallback<CretaService.JsBasesResult>() {
					
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(JsBasesResult result) {
						onBases(result);
					}
				}
		);
	}

	// ------------------------------------------------------------------------

	private native void exportSubmitComplete() /*-{
		var that = this;
		$wnd.__onTrabajadoresYTramos = $entry(function(trabajadoresYTramos,
				respuestas) {
			that.@com.esferalia.aon.gwt.payroll.client.CretaDetail::onTrabajadoresYTramos([Lcom/esferalia/aon/gwt/payroll/shared/CretaService$JsTrabajadoresYTramos;[Lcom/esferalia/aon/gwt/payroll/shared/CretaService$JsRespuesta;)(trabajadoresYTramos, respuestas);
		});
	}-*/;

}
