package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRecordDataPanel.AonRecordDataPanelCallback;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class RecordDataTable extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;

	private static final Logger LOGGER = Logger.getLogger(RecordDataTable.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}

	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt(0);

	private SimplePanel container;
	private ScrollPanel scrollPanel;
	private AonCustomTable tab;
	private int lastScrollPos = 0;

	private String domainName;
	private Integer domain;
	private String user;
	private Integer registry;

	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private static final String ATTACH_RECORDDATA_URL = "/ms/api/attach/recordData";
	private static final String SESSION_API = "AONd95770f269e711eb94390242ac130002";

	private static enum COLS {
		CAD("F. Creaci\u00f3n", "8rem",
				"max-width: 8rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		NAM("Nombre", "-moz-available",
				"min-width: 8rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		NOT("Notario", "-moz-available",
				"min-width: 8rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		PRO("N. Protocolo", "6rem", "max-width: 6rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		BUT(AonStringUtils.EMPTY, "4rem", "");

		String headerLabel;
		String colWidth;
		String styles;

		private COLS(String headerLabel, String colWidth, String styles) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.styles = styles;
		}

		public String getColWidth() {
			return colWidth;
		}

		public String getHeaderLabel() {
			return headerLabel;
		}

		public String getStyles() {
			return styles;
		}
	}

	public RecordDataTable(String domainName, int domain, String user, Integer registry) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		this.domainName = domainName;
		this.domain = domain;
		this.user = user;
		this.registry = registry;

		container = new SimplePanel();
		container.getElement().getStyle().setProperty("max-height", "200px");
		container.getElement().getStyle().setProperty("padding-left", "1px");
		setWidget(container);

		addScrollHandler(new ScrollHandler() {

			public void onScroll(ScrollEvent event) {
				// ------------------------------------ Ignore scroll up.
				int oldScrollPos = lastScrollPos;
				lastScrollPos = getVerticalScrollPosition();
				if (oldScrollPos >= lastScrollPos) {
					return;
				}
				// -----------------------------------------------------
				if (isSearchEnabled()) {
					int maxScrollTop = getWidget().getOffsetHeight() - getOffsetHeight();
					if (lastScrollPos >= maxScrollTop) {
						disableSearch();
						searchData();
					}
				}
			}
		});

		onSearch();

	}

	public boolean isSearchEnabled() {
		return (searchEnabled.getValue() == 0);
	}

	public void disableSearch() {
		searchEnabled.setValue(-1);
	}

	public void enableSearch() {
		searchEnabled.setValue(0);
	}

	public boolean isMoreData() {
		return (moreData.getValue() == 0);
	}

	public void disableMoreData() {
		moreData.setValue(-1);
	}

	public void enableMoreData() {
		moreData.setValue(0);
	}

	public void onSearch() {
		enableMoreData();
		search();
	}

	private void search() {
		container.clear();
		tab = new AonCustomTable();
		tab.setMaxHeight("160x");
		scrollPanel = new ScrollPanel(tab);

		paintHeader();
		container.setWidget(scrollPanel);
		searchData();
	}

	private void paintHeader() {
		tab.createHeader();
		for (COLS col : COLS.values()) {
			if (col.equals(COLS.BUT)) {
				FlowPanel buttonContainer = new FlowPanel();
				buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);

				AonTableButton button = new AonTableButton("Nuevo Dato Registral", AON.CSS.aonIconAdd());
				button.addStyleName(AON.CSS.aonCustomRowButtom());
				button.addClickHandler(e -> createRecordData());
				buttonContainer.add(button);

				tab.addHeader(buttonContainer, col.getColWidth(), col.getStyles());
			} else
				tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());

		}
	}

	private void searchData() {
		if (!isMoreData())
			return;

		getList(recordDatas -> {
			boolean something = false;

			for (RecordData recordData : recordDatas) {
				something = true;
				paintRow(recordData);
			}

			if (recordDatas.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + recordDatas.size() - 1);
				enableMoreData();
			}

			if (!something) {
				FlowPanel line = new FlowPanel();
				InlineLabel label = new InlineLabel(AON.MSG.noData());
				line.add(label);
				container.clear();
				container.add(line);
				disableMoreData();
			}
			enableSearch();

		});
	}

	private void paintRow(RecordData recordData) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		buttonContainer.addStyleName(AON.CSS.aonItemFlex());
		buttonContainer.getElement().getStyle().setProperty("justify-content", "end");

		if (null == recordData.getAttach()) {

			AonTableButton upload = new AonTableButton("Subir archivo", AON.CSS.aonIconUploadFile());
			upload.addStyleName(AON.CSS.aonCustomRowButtom());

			upload.addClickHandler(e -> {
				e.stopPropagation();

				// Crear input file oculto
				FileUpload hiddenUpload = new FileUpload();
				hiddenUpload.getElement().setAttribute("type", "file");
				hiddenUpload.getElement().setAttribute("accept", "*/*");
				hiddenUpload.getElement().getStyle().setProperty("display", "none");

				RootPanel.get().add(hiddenUpload);

				// Cuando el usuario seleccione un archivo subir directamente
				hiddenUpload.addChangeHandler(ev -> {

					uploadRecordDataFile(hiddenUpload, recordData, () -> {
						onSearch();
					});
				});

				// Abrir selector
				hiddenUpload.getElement().<InputElement>cast().click();
			});
			buttonContainer.add(upload);

		} else {

			if (null != recordData.getAttach() && null != recordData.getFullAttach().getData()) {
				AonTableButton preview = new AonTableButton("Previsualizar", AON.CSS.aonIconPdf());
				preview.addStyleName(AON.CSS.aonCustomRowButtom());
				preview.addClickHandler(e -> {
					e.stopPropagation();

					AonAttachPreviewPanel popup = new AonAttachPreviewPanel(recordData.getFullAttach());
					popup.center();
					popup.show();
				});
				buttonContainer.add(preview);
			} else if (null != recordData.getAttach() && null != recordData.getFullAttach().getDriveId()) {
				AonTableButton download = new AonTableButton("Descargar", AON.CSS.aonIconPdf());
				download.addStyleName(AON.CSS.aonCustomRowButtom());
				download.addClickHandler(e -> {
					e.stopPropagation();

					JSONObject json = new JSONObject();
					json.put("domainId", new JSONNumber(domain));
					json.put("domainName", new JSONString(domainName));
					json.put("domainLogin", new JSONString(user));
					json.put("rattach", new JSONNumber(recordData.getAttach()));
					json.put("type", new JSONString("registry"));

					String jsonBase64 = base64Encode(json.toString());

					downloadFile(jsonBase64, SESSION_API);
				});
				buttonContainer.add(download);
			}

			AonTableButton deleteFile = new AonTableButton("Eliminar archivo adjunto", AON.CSS.aonIconDeleteFile());
			deleteFile.addStyleName(AON.CSS.aonCustomRowButtom());
			deleteFile.addClickHandler(e -> {
				e.stopPropagation();

				AonDialog dialog = new AonDialog("Eliminaci\u00f3n Archivo Adjunto",
						new HTML("Se va a proceder a eliminar el archivo adjunto <b>"
								+ recordData.getFullAttach().getDescription()
								+ "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));

				dialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
					}

					@Override
					public void onAccept() {
						COMMON_SERVICE.deleteRecordDataAttach(domainName, domain, user, recordData.getId(),
								new AsyncCallback<Void>() {

									@Override
									public void onSuccess(Void result) {
										onSearch();
									}

									@Override
									public void onFailure(Throwable caught) {
										Window.alert("Error eliminando archivo: " + caught.getMessage());
									}
								});
					}
				});
			});
			buttonContainer.add(deleteFile);
		}

		AonTableButton button;
		button = new AonTableButton("Borrar Dato Registral", AON.CSS.aonIconDelete());
		button.addStyleName(AON.CSS.aonCustomRowButtom());
		button.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				button.setEnabled(false);
				AonDialog dialog = new AonDialog("Eliminaci\u00f3n Dato Regisral",
						new HTML("Se va a proceder a eliminar el dato registral <b>" + recordData.getDescription()
								+ "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));

				dialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						button.setEnabled(true);
					}

					@Override
					public void onAccept() {
						delete(recordData);
					}
				});
			}
		});
		buttonContainer.add(button);

		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onUpdateRecordData(recordData), ClickEvent.getType());

		Label date = new Label(
				null != recordData.getCreationDate() ? formatDate.format(recordData.getCreationDate()) : "");
		date.setTitle(null != recordData.getCreationDate() ? formatDate.format(recordData.getCreationDate()) : "");
		tab.addInlineStyle(date, COLS.CAD.getStyles());
		tab.addRow(row, date, COLS.CAD.getColWidth());

		Label description = new Label(recordData.getDescription());
		description.setTitle(recordData.getDescription());
		tab.addInlineStyle(description, COLS.NAM.getStyles());
		tab.addRow(row, description, COLS.NAM.getColWidth());

		Label notario = new Label(recordData.getNotary());
		notario.setTitle(recordData.getNotary());
		tab.addInlineStyle(notario, COLS.NOT.getStyles());
		tab.addRow(row, notario, COLS.NOT.getColWidth());

		Label protocol = new Label(recordData.getNumber());
		protocol.setTitle(recordData.getNumber());
		tab.addInlineStyle(protocol, COLS.PRO.getStyles());
		tab.addRow(row, protocol, COLS.PRO.getColWidth());

		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}

	public static void downloadFile(String jsonParam, String sessionId) {
		String url = "/ms/api/download?json=" + URL.encodeQueryString(jsonParam);
		Window.open(url, "_blank", "");
	}

	private static native String base64Encode(String text) /*-{
	    return btoa(text);
	}-*/;

	private void uploadRecordDataFile(FileUpload fileUpload, RecordData recordData, Runnable onSuccess) {

		getFileAsBase64(fileUpload.getElement(), (base64, mimeType, fileName, fileSize) -> {

			String json = buildAttachJson(recordData.getAttach(), base64, mimeType, fileName, fileSize, (byte) 1,
					recordData.getId());

			RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, ATTACH_RECORDDATA_URL);
			rb.setHeader("Content-Type", "application/json; charset=UTF-8");
			rb.setHeader("session_id", SESSION_API);

			try {
				rb.sendRequest(json, new RequestCallback() {

					@Override
					public void onResponseReceived(Request req, Response resp) {
						if (resp.getStatusCode() >= 200 && resp.getStatusCode() < 300) {
							onSuccess.run();
						} else {
							Window.alert("Error al subir: HTTP " + resp.getStatusCode());
						}
					}

					@Override
					public void onError(Request req, Throwable ex) {
						Window.alert("Error de red al subir el archivo.");
					}
				});

			} catch (RequestException ex) {
				Window.alert("Error al iniciar la subida.");
			}
		});
	}

	private native void getFileAsBase64(Element fileInput, FileReadCallback callback) /*-{
	    var file = fileInput.files[0];
	    if (!file) {
	        callback.@com.esferalia.aon.gwt.common.client.widget.solutions.RecordDataTable.FileReadCallback::onRead(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)
	            ("", "", "", 0);
	        return;
	    }
	
	    var reader = new FileReader();
	    reader.onload = function(e) {
	        var base64 = e.target.result.split(",")[1];
	        callback.@com.esferalia.aon.gwt.common.client.widget.solutions.RecordDataTable.FileReadCallback::onRead(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)
	            (base64, file.type, file.name, file.size);
	    };
	    reader.readAsDataURL(file);
	}-*/;

	public static interface FileReadCallback {
		void onRead(String base64, String mimeType, String fileName, int fileSize);
	}

	private String buildAttachJson(Integer id, String base64, String mimeType, String fileName, int fileSize, byte type,
			Integer recordDataId) {

		StringBuilder sb = new StringBuilder("{");

		if (id != null)
			sb.append("\"id\":").append(id).append(",");

		sb.append("\"domain\":{\"id\":").append(domain).append(",\"name\":\"").append(escapeJson(domainName))
				.append("\"},");

		sb.append("\"attachType\":\"REGISTRY\",");
		sb.append("\"attachModule\":").append(registry).append(",");

		sb.append("\"name\":\"").append(escapeJson(fileName)).append("\",");
		sb.append("\"contentName\":\"").append(escapeJson(fileName)).append("\",");
		sb.append("\"contentType\":\"").append(escapeJson(mimeType)).append("\",");
		sb.append("\"contentSize\":").append(fileSize).append(",");

		sb.append("\"type\":").append(type).append(",");

		sb.append("\"date\":\"").append(getCurrentDateISO()).append("\",");

		sb.append("\"content\":\"").append(base64).append("\",");

		sb.append("\"recordDataId\":").append(recordDataId);

		sb.append("}");
		return sb.toString();
	}

	private String getCurrentDateISO() {
		DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss");
		return fmt.format(new Date());
	}

	private static String escapeJson(String value) {
		if (value == null)
			return "";
		return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	private void getList(Consumer<List<RecordData>> success) {
		COMMON_SERVICE.getRecordDatas(domainName, domain, user, registry, true, new AsyncCallback<List<RecordData>>() {

			@Override
			public void onSuccess(List<RecordData> recordDatas) {
				success.accept(recordDatas);
			}

			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}

	private void delete(RecordData recordData) {
		COMMON_SERVICE.deleteRecordData(domainName, domain, user, recordData.getId(), true, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				onSearch();
			}

			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error borrado: " + caught.getMessage());
			}
		});
	}

	private void onUpdateRecordData(RecordData recordData) {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption("Editar Dato Registral");

		final AonRecordDataPanel aonRecordDataPanel = new AonRecordDataPanel(domainName, domain, user, recordData,
				new AonRecordDataPanelCallback() {

					@Override
					public void onCancel() {
						dialog.hide();
					}

					@Override
					public void onAccept(RecordData recordData) {
						dialog.hide();
						onSearch();
					}
				});

		dialog.add(aonRecordDataPanel);
		dialog.showLoaded();
	}

	private void createRecordData() {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption("Nuevo Dato Registral");

		final AonRecordDataPanel aonRecordDataPanel = new AonRecordDataPanel(domainName, domain, user, registry,
				new AonRecordDataPanelCallback() {

					@Override
					public void onCancel() {
						dialog.hide();
					}

					@Override
					public void onAccept(RecordData recordData) {
						dialog.hide();
						onSearch();
					}
				});

		dialog.add(aonRecordDataPanel);
		dialog.showLoaded();

	}

	protected abstract void onShowErrorMessage(String errorMessage);

}
