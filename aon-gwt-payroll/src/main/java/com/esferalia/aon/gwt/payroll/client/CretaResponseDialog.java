package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.client.MainCreta.hasTrabajadoresYTramos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.shared.CretaService;
import com.esferalia.aon.gwt.payroll.shared.CretaService.File;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsBasesResult;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsFile;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsRespuesta;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsTrabajadoresYTramos;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.XMLParser;

import net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta;

public class CretaResponseDialog extends SelectDialog<CretaService.JsFile> {

	// ------------------------------------------------------------------------

	public static interface Handler {
		void onBases(CretaService.JsBasesResult result);
	}

	interface Binder extends UiBinder<Widget, CretaResponseDialog> {

	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	Button fileButton;

	@UiField
	FormPanel formPanel;
	@UiField
	CheckBox basesCheck;
	@UiField
	FileUpload fileUpload;
	@UiField
	InlineLabel fileLabel;

	private Handler handler;

	private CretaService.File inFile;
	private CretaService.File outFile;

	private Timer jsFileToolTipTimer;
	private Map<String, CretaService.JsRespuesta> respuestasMap;

	public CretaResponseDialog(CretaService.File outFile,
			CretaService.File inFile, Handler handler) {
		super();
		this.inFile = inFile;
		this.outFile = outFile;
		// exportSubmitComplete();
		this.handler = handler;
		setWidget(binder.createAndBindUi(this));

		setCaption("Sistema de Liquidaci\u00F3n Directa (Proyecto Cret@)");
		fileUpload.getElement().setPropertyString("multiple", "multiple");

		addColumn(new JsFileColumn() {

			@Override
			void onJsFileOut(JsFile jsFile, NativeEvent event) {
				CretaResponseDialog.this.onJsFileOut();
			}

			@Override
			void onJsFileOver(JsFile jsFile, NativeEvent event) {
				CretaResponseDialog.this.onJsFileOver(jsFile.getId(),
						event.getClientX(), event.getClientY());
			}

			@Override
			void onJsFileClick(JsFile jsFile, NativeEvent event) {
				CretaResponseDialog.this.onJsFileClick(jsFile.getId(),
						event.getClientX(), event.getClientY());
			}

			@Override
			void onJsFileDblClick(JsFile jsFile, NativeEvent event) {
			}

			@Override
			String getIconStyle(JsTrabajadoresYTramos jsTrabajadoresYTramos) {
				return CretaResponseDialog.this
						.getIconStyle(jsTrabajadoresYTramos);
			}

			@Override
			String getDescription(JsTrabajadoresYTramos jsTrabajadoresYTramos) {
				return CretaResponseDialog.this
						.getDescription(jsTrabajadoresYTramos.getCCC());
			}

		}, inFile.getFilename());

		setData(new ArrayList<CretaService.JsFile>(0));

		this.respuestasMap = new HashMap<String, CretaService.JsRespuesta>();
	}

	// ------------------------------------------------------------------------

	@UiHandler("fileButton")
	void onFileClicked(ClickEvent e) {
		fileUpload.click();
	}

	// UiHandler("acceptButton")
	@Override
	void onAcceptClick(ClickEvent e) {
		send(outFile);
	}

	@UiHandler("fileUpload")
	void onFileUploadChange(ChangeEvent event) {

		String fileName = fileUpload.getFilename();
		fileLabel.setTitle(fileName);
		fileLabel.setText(fileName.substring(
				Math.max(0, fileName.length() - 25), fileName.length()));

		submit(inFile);

	}

	// ------------------------------------------------------------------------

	public void onResultComplete() {
	}

	public boolean getPreviousBases() {
		return basesCheck.getValue();
	}

	public String getDescription(String ccc) {
		return "";
	}

	public boolean accept(JsFile f) {
		return true;
	}

	// ------------------------------------------------------------------------

	public void onBases(CretaService.JsBasesResult object) {
		handler.onBases(object);
	}

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

			List<JsFile> avail = new ArrayList<JsFile>();
			for (JsTrabajadoresYTramos trabajadorYTramo : trabajadoresYTramosMap
					.values())
				if (accept(trabajadorYTramo))
					avail.add(trabajadorYTramo);

			for (JsRespuesta respuesta : respuestas)
				if (!contains(avail, respuesta)
						&& hasTrabajadoresYTramos(respuesta)
						&& accept(respuesta))
					avail.add(respuesta);

			setData(avail);

		} catch (UnsupportedOperationException e) {
			List<JsFile> avail = new ArrayList<JsFile>();
			for (JsTrabajadoresYTramos trabajadorYTramo : trabajadoresYTramos)
				if (accept(trabajadorYTramo))
					avail.add(trabajadorYTramo);

			respuestasMap = new HashMap<String, JsRespuesta>();
			for (CretaService.JsRespuesta respuesta : respuestas) {
				respuestasMap.put(respuesta.getCCC() + respuesta.getFrom(),
						respuesta);
				if (!contains(avail, respuesta)
						&& MainCreta.hasTrabajadoresYTramos(respuesta)
						&& accept(respuesta))
					avail.add(respuesta);
			}

			setData(avail);
		}

	}
	// ------------------------------------------------------------------------

	@Override
	protected void onAttach() {
		exportSubmitComplete();
		super.onAttach();
	}
	// ------------------------------------------------------------------------

	public void onJsFileOver(final String key, final int x, final int y) {

		if (jsFileToolTipTimer != null)
			jsFileToolTipTimer.cancel();

		jsFileToolTipTimer = new Timer() {

			@Override
			public void run() {
				MainCreta.showjsRespuestaToolTip(respuestasMap.get(key), x, y);
			}
		};

		jsFileToolTipTimer.schedule(1000);
	}

	private void onJsFileClick(final String key, final int x, final int y) {

		if (jsFileToolTipTimer != null)
			jsFileToolTipTimer.cancel();

		MainCreta.showjsRespuestaToolTip(respuestasMap.get(key), x, y);
	}

	private void onJsFileOut() {
		if (jsFileToolTipTimer != null)
			jsFileToolTipTimer.cancel();
	}

	private String getIconStyle(JsTrabajadoresYTramos t) {
		return MainCreta.getIconStyle(respuestasMap.get(t.getId()));
	}

	private void submit(CretaService.File file) {
		formPanel.setAction(CretaService.CRETA_URL + "/" + file.name());
		formPanel.submit();
	}

	private void send(CretaService.File file) {
		MainCreta.submit(CretaService.CRETA_URL + "/" + file.name(),
				getDefaults(),
				getSelectedData(),

		new AsyncCallback<CretaService.JsBasesResult>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO:
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(JsBasesResult result) {
				onBases(result);
			}

		}

		);
	}

	private Map<String,Collection<String>> getDefaults(){
		return Collections.emptyMap();
	}

	// ------------------------------------------------------------------------

	private native void exportSubmitComplete() /*-{
		var that = this;
		$wnd.__onTrabajadoresYTramos = $entry(function(trabajadoresYTramos,
				respuestas) {
			that.@com.esferalia.aon.gwt.payroll.client.CretaResponseDialog::onTrabajadoresYTramos([Lcom/esferalia/aon/gwt/payroll/shared/CretaService$JsTrabajadoresYTramos;[Lcom/esferalia/aon/gwt/payroll/shared/CretaService$JsRespuesta;)(trabajadoresYTramos, respuestas);
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
