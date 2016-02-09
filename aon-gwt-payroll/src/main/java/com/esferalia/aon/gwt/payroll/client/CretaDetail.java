package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.client.MainCreta.hasTrabajadoresYTramos;
import static com.esferalia.aon.gwt.payroll.shared.CretaService.CRETA_URL;
import static com.esferalia.aon.gwt.payroll.shared.CretaService.File.DOCUMENTO_CALCULO_LIQUIDACION;
import static com.esferalia.aon.gwt.payroll.shared.CretaService.File.TRABAJADORES_TRAMOS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.payroll.client.MainCreta.JsFileComparator;
import com.esferalia.aon.gwt.payroll.shared.CretaService;
import com.esferalia.aon.gwt.payroll.shared.CretaService.File;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsBasesResult;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsDCLResult;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsEvent;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsFile;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsRespuesta;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsTrabajadoresYTramos;
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
	Button dclButton;
	@UiField
	MenuItem dclMenuItem;

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
				if (CretaDetail.this.jsFileToolTipTimer != null)
					CretaDetail.this.jsFileToolTipTimer.cancel();
				
				CretaDetail.this.onJsFileOver(jsFile.getId(),
						event.getClientX(), event.getClientY());
			}

			@Override
			void onJsFileClick(JsFile jsFile, NativeEvent event) {
				if (CretaDetail.this.jsFileToolTipTimer != null)
					CretaDetail.this.jsFileToolTipTimer.cancel();
				CretaDetail.this.onJsFileClick(jsFile.getId(),
						event.getClientX(), event.getClientY());

			}

			@Override
			void onJsFileDblClick(JsFile jsFile, NativeEvent event) {
				if (CretaDetail.this.jsFileToolTipTimer != null)
					CretaDetail.this.jsFileToolTipTimer.cancel();
				if (CretaDetail.this.popupTooltip != null)
					CretaDetail.this.popupTooltip.hide();
				
				List<JsFile> jsFiles = new ArrayList<JsFile>(2);
				jsFiles.add(jsFile);
				JsRespuesta jsRespuesta = CretaDetail.this.respuestasMap.get(jsFile.getId());
				if ( jsRespuesta != null )
					jsFiles.add(jsRespuesta);

				CretaDetail.this.onJsFileDblClick(event.getClientX(),
						event.getClientY(), jsFiles.toArray(new JsFile[jsFiles.size()]));

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
		fileUpload.setName(TRABAJADORES_TRAMOS.name());
		formPanel.setAction(CRETA_URL + '/' +TRABAJADORES_TRAMOS.name());
		fileUpload.click();
	}

	@UiHandler("dclButton")
	void onClickLiquidacionButton(ClickEvent e) {
		fileUpload.setName(DOCUMENTO_CALCULO_LIQUIDACION.name());
		formPanel.setAction(CRETA_URL + '/' +DOCUMENTO_CALCULO_LIQUIDACION.name());
		fileUpload.click();
	}

	@UiHandler("dbaButton")
	void onClickDBAButton(ClickEvent e) {
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

			Map<String, JsTrabajadoresYTramos> trabajadoresYTramosMap = MainCreta
					.add(File.TRABAJADORES_TRAMOS, trabajadoresYTramos);

			List<JsFile> filtered = new ArrayList<JsFile>();

			filtered.addAll(filter(trabajadoresYTramosMap.values()));

			respuestasMap = MainCreta.add(File.RESPUESTA, respuestas);

			for (JsRespuesta jsRespuesta : respuestasMap.values()) {
				if (!contains(filtered, jsRespuesta)
						&& hasTrabajadoresYTramos(jsRespuesta))
					filtered.addAll(filter(Collections.singleton(jsRespuesta)));
			}
			
			Collections.sort(filtered, JsFileComparator.newInstace());
			
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

	public void onDocumentoCalculoLiquidacion(
			CretaService.JsDCLResult success[],
			CretaService.JsDCLResult errors[]) {
		
		onDCLResults(success, errors);

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

	protected abstract void onDCLResults(JsEvent success [], JsEvent errors []);

	protected void onJsFileClick(final String key, final int x, final int y) {
		// NOOP
	}

	protected void onJsFileDblClick(final int x, final int y, JsFile ...jsFile ) {
		// NOOP
	}

	// ------------------------------------------------------------------------

	private void onJsFileOut() {
		if (jsFileToolTipTimer != null)
			jsFileToolTipTimer.cancel();
	}

	private void onJsFileOver(final String key, final int x, final int y) {

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

	private String getIconStyle(JsTrabajadoresYTramos t) {
		return MainCreta.getIconStyle(respuestasMap.get(t.getId()));
	}

	private void submitBases() {
		MainCreta.submit(CretaService.CRETA_URL + "/" + CretaService.File.BASES,
				getDefaults(),
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
				});
	}
	
	private Map<String,Collection<String>> getDefaults(){
		return Collections.emptyMap();
	}
	

	// ------------------------------------------------------------------------

	private native void exportSubmitComplete() /*-{
		var that = this;
		$wnd.__onTrabajadoresYTramos = $entry(function(trabajadoresYTramos,
				respuestas) {
			that.@com.esferalia.aon.gwt.payroll.client.CretaDetail::onTrabajadoresYTramos([Lcom/esferalia/aon/gwt/payroll/shared/CretaService$JsTrabajadoresYTramos;[Lcom/esferalia/aon/gwt/payroll/shared/CretaService$JsRespuesta;)(trabajadoresYTramos, respuestas);
		});
		$wnd.__onDocumentoCalculoLiquidacion = $entry(function(success,
				errors) {
			that.@com.esferalia.aon.gwt.payroll.client.CretaDetail::onDocumentoCalculoLiquidacion([Lcom/esferalia/aon/gwt/payroll/shared/CretaService$JsDCLResult;[Lcom/esferalia/aon/gwt/payroll/shared/CretaService$JsDCLResult;)(success, errors);
		});
	}-*/;

	// ------------------------------------------------------------------------

	private static boolean contains(List<JsFile> jsFiles, String id) {
		for (JsFile jsF : jsFiles)
			if (jsF.getId().equals(id))
				return true;
		return false;
	}

	private static boolean contains(List<JsFile> jsFiles, JsFile jsFile) {
		return contains(jsFiles, jsFile.getId());
	}

}
