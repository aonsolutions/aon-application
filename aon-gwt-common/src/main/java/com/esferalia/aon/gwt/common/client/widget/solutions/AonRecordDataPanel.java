package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.Date;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.registry.CommercialRegistryCode;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.RecordDataType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonRecordDataPanel extends HTMLPanel {

    public static interface AonRecordDataPanelCallback {
        void onAccept(RecordData recordData);
        void onCancel();
    }

    static CommonServiceAsync commonService;

    private static void initializeCommonService() {
        if (commonService == null) {
            CommonServiceAsync raw = GWT.create(CommonService.class);
            commonService = new CommonServiceAsyncDecorator(raw);
        }
    }

    private final static String EMPTY_STRING = "";

    private String domainName;
    private Integer domainId;
    private String user;

    private AonRecordDataPanelCallback callback;
    private RecordData recordData;
    private boolean recordDataExistedAtStart;

    private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);

    private AonCustomDateBox date = new AonCustomDateBox("F. Creaci\u00f3n");
    private AonCustomListBox type = new AonCustomListBox("Tipo");
    private AonCustomTextBox description = new AonCustomTextBox("Descripci\u00f3n");

    private AonCustomDateBox registryDate = new AonCustomDateBox("F. Registro");
    private AonCustomTextBox notary = new AonCustomTextBox("Notario");

    private AonCustomTextBox protocol = new AonCustomTextBox("N. Protocolo");
    private AonCustomTextBox tomo = new AonCustomTextBox("Tomo");
    private AonCustomTextBox section = new AonCustomTextBox("Secci\u00f3n");
    private AonCustomTextBox page = new AonCustomTextBox("Folio");

    private AonCustomTextBox sheet = new AonCustomTextBox("Hoja");
    private AonCustomTextBox inscription = new AonCustomTextBox("Inscripci\u00f3n");

    private AonCustomListBox commercialRegistryCode = new AonCustomListBox("C\u00f3digo Registro Mercantil");
    private AonCustomTextBox irus = new AonCustomTextBox("IRUS");

    // Nuevo input est\u00e9tico
    private AonCustomTextBox fileInput = new AonCustomTextBox("Archivo");

    private static final String ATTACH_RECORDDATA_URL = "/ms/api/attach/recordData";
    private static final String SESSION_API = "AONd95770f269e711eb94390242ac130002";

    public AonRecordDataPanel(String domainName, Integer domain, String user, Integer registry, AonRecordDataPanelCallback callback) {
        super(EMPTY_STRING);
        this.recordData = new RecordData().setDomain(domain).setRegistry(registry);
        this.recordDataExistedAtStart = false;
        aonRDirStaffPanel(domainName, domain, user, callback);
    }

    public AonRecordDataPanel(String domainName, Integer domain, String user, RecordData recordData, AonRecordDataPanelCallback callback) {
        super(EMPTY_STRING);
        this.recordData = recordData;
        this.recordDataExistedAtStart = (recordData.getId() != null);
        aonRDirStaffPanel(domainName, domain, user, callback);
    }

    private void aonRDirStaffPanel(String domainName, Integer domain, String user, AonRecordDataPanelCallback callback) {
        initializeCommonService();
        this.domainName = domainName;
        this.domainId = domain;
        this.user = user;
        this.callback = callback;
        show();
    }

    public void show() {

        clear();
        setStyleName(AON.CSS.aonFlexColumn2());
        getElement().getStyle().setProperty("padding", "1rem 0");
        add(messagePanel);

        HTMLPanel container = new HTMLPanel(EMPTY_STRING);
        container.setStyleName(AON.CSS.aonFlexColumn2());
        container.getElement().getStyle().setProperty("padding", "0 1rem");
        container.getElement().getStyle().setProperty("min-width", "25rem");

        HTMLPanel row = new HTMLPanel(EMPTY_STRING);
        row.setStyleName(AON.CSS.aonItemFlex());

        date.setValue(recordData.getCreationDate() != null ? recordData.getCreationDate() : new Date());

        type.clearItems();
        for (RecordDataType rdt : RecordDataType.values())
            type.addItem(rdt.getDescription(), rdt.name());

        row.add(date);
        row.add(type);
        row.add(description);
        container.add(row);

        HTMLPanel row2 = new HTMLPanel(EMPTY_STRING);
        row2.setStyleName(AON.CSS.aonItemFlex());
        row2.add(registryDate);
        row2.add(notary);
        container.add(row2);

        HTMLPanel row3 = new HTMLPanel(EMPTY_STRING);
        row3.setStyleName(AON.CSS.aonItemFlex());
        row3.add(protocol);
        row3.add(tomo);
        row3.add(section);
        row3.add(page);
        container.add(row3);

        HTMLPanel row4 = new HTMLPanel(EMPTY_STRING);
        row4.setStyleName(AON.CSS.aonItemFlex());
        row4.add(sheet);
        row4.add(inscription);
        container.add(row4);

        HTMLPanel row5 = new HTMLPanel(EMPTY_STRING);
        row5.setStyleName(AON.CSS.aonItemFlex());

        commercialRegistryCode.clearItems();
        commercialRegistryCode.addItem("-", "");
        for (CommercialRegistryCode crc : CommercialRegistryCode.values())
            commercialRegistryCode.addItem(crc.getDescription(), crc.ordinal() + "");

        row5.add(commercialRegistryCode);
        row5.add(irus);
        container.add(row5);

        // ---------------------------
        //   INPUT DE ARCHIVO NUEVO
        // ---------------------------
        if (recordData.getId() != null) {
        	
        	fileInput = new AonCustomTextBox("Archivo");

            HTMLPanel row6 = new HTMLPanel(EMPTY_STRING);
            row6.setStyleName(AON.CSS.aonItemFlex());

            fileInput.setValue(recordData.getAttach() != null ? recordData.getFullAttach().getDescription() : "Sin archivo");

            HorizontalPanel buttonsPanel = new HorizontalPanel();
            buttonsPanel.setSpacing(8);

            AonTableButton preview = new AonTableButton("Ver archivo", null != recordData.getAttach() && null != recordData.getFullAttach().getData() ? AON.CSS.aonIconPdf() : AON.CSS.aonIconPdf());
            AonTableButton upload = new AonTableButton("Subir archivo", AON.CSS.aonIconUpload());
            AonTableButton delete = new AonTableButton("Eliminar archivo", AON.CSS.aonIconDelete());

            preview.setVisible(recordData.getAttach() != null);
            delete.setVisible(recordData.getAttach() != null);
            upload.setVisible(recordData.getAttach() == null);

            buttonsPanel.add(preview);
            buttonsPanel.add(upload);
            buttonsPanel.add(delete);

            fileInput.addButton(buttonsPanel);

            bindAttachEvents(upload, delete, preview);

            row6.add(fileInput);
            container.add(row6);
        }

        if (recordData.getId() != null) {
            date.setValue(recordData.getCreationDate());
            type.setValue(recordData.getType().name());
            description.setValue(recordData.getDescription());
            registryDate.setValue(recordData.getRecordDate());
            notary.setValue(recordData.getNotary());
            protocol.setValue(recordData.getNumber());
            tomo.setValue(recordData.getVolume());
            section.setValue(recordData.getSection());
            page.setValue(recordData.getPage());
            sheet.setValue(recordData.getSheet());
            inscription.setValue(recordData.getRegistration());
            commercialRegistryCode.setValue(recordData.getCommercialRegistryCode() == null ? "" : recordData.getCommercialRegistryCode().ordinal() + "");
            irus.setValue(recordData.getIrus());
        }

        container.add(createButtonsPanel());
        add(container);
    }

    private void bindAttachEvents(AonTableButton upload, AonTableButton delete, AonTableButton preview) {

    	upload.addClickHandler(e -> {
    	    if (recordData.getId() == null || recordData.getAttach() != null)
    	        return;

    	    // Crear input file oculto
    	    FileUpload hiddenUpload = new FileUpload();
    	    hiddenUpload.getElement().setAttribute("type", "file");
    	    hiddenUpload.getElement().setAttribute("accept", "*/*");
    	    hiddenUpload.getElement().getStyle().setProperty("display", "none");

    	    RootPanel.get().add(hiddenUpload);

    	    // Cuando el usuario seleccione un archivo subir directamente
    	    hiddenUpload.addChangeHandler(ev -> {
    	        uploadRecordDataFile(hiddenUpload, recordData, () -> {
    	            getRecordData(rd -> {
    	            	show();
    	            });
    	        });
    	    });

    	    // Abrir selector de archivos
    	    hiddenUpload.getElement().<InputElement>cast().click();
    	});


        delete.addClickHandler(e -> {
        	AonDialog dialog = new AonDialog("Eliminaci\u00f3n Archivo Adjunto",
					new HTML("Se va a proceder a eliminar el archivo adjunto <b>" + recordData.getFullAttach().getDescription()
							+ "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));

			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {}

				@Override
				public void onAccept() {
					if (recordData.getId() == null || recordData.getAttach() == null)
		                return;

		            commonService.deleteRecordDataAttach(domainName, domainId, user, recordData.getId(),
		                new AsyncCallback<Void>() {

		                    @Override
		                    public void onSuccess(Void result) {
		                        getRecordData(rd -> show());
		                    }

		                    @Override
		                    public void onFailure(Throwable caught) {
		                        Window.alert("Error eliminando archivo: " + caught.getMessage());
		                    }
		                }
		            );
				}
			});
        	
            
        });

        preview.addClickHandler(e -> {
            stopEvent(e);

            if (recordData.getAttach() == null)
                return;

            // Si está en Drive descargar vía iframe
            if (recordData.getFullAttach().getDriveId() != null) {

                JSONObject json = new JSONObject();
                json.put("domain_id", new JSONNumber(domainId));
                json.put("domain_name", new JSONString(domainName));
                json.put("domain_login", new JSONString(user));
                json.put("id", new JSONNumber(recordData.getAttach()));
                json.put("attach_type", new JSONString("registry"));

                String base64 = base64Encode(json.toString());
                String url = "/ms/api/file/" + base64;

                fetchBinaryXHR(url, base64Data -> {

                    byte[] bytes = base64ToBytes(base64Data);

                    Attach temp = new Attach();
                    temp.setId(recordData.getAttach());
                    temp.setDescription(recordData.getFullAttach().getDescription());
                    temp.setMimeType(recordData.getFullAttach().getMimeType());
                    temp.setData(bytes);

                    showPreview(temp);
                });

            } else {
                // Si ya está en BD abrir directamente
                showPreview(recordData.getFullAttach());
            }
        });

    }
    
    private void stopEvent(ClickEvent e) {
        e.preventDefault();
        e.stopPropagation();
        e.getNativeEvent().stopPropagation();
        e.getNativeEvent().preventDefault();
    }
    
    private void showPreview(Attach attach) {
        AonAttachPreviewPanel popup = new AonAttachPreviewPanel(attach);
        popup.center();
        popup.show();
    }

	
	public static void downloadFile(String jsonParam, String sessionId) {
		String url = "/ms/api/download?json=" + URL.encodeQueryString(jsonParam);
		Window.open(url, "_blank", "");
	}

	private static native String base64Encode(String text) /*-{
	    return btoa(text);
	}-*/;
	
	private native void fetchBinaryXHR(String url, Consumer<String> callback) /*-{
	    var xhr = new XMLHttpRequest();
	    xhr.open("GET", url, true);
	    xhr.responseType = "arraybuffer";
	
	    xhr.onload = function() {
	        if (xhr.status >= 200 && xhr.status < 300) {
	            var bytes = new Uint8Array(xhr.response);
	            var binary = "";
	            for (var i = 0; i < bytes.byteLength; i++) {
	                binary += String.fromCharCode(bytes[i]);
	            }
	            var base64 = btoa(binary);
	            callback.@java.util.function.Consumer::accept(Ljava/lang/Object;)(base64);
	        }
	    };
	
	    xhr.send();
	}-*/;
	
	private native byte[] base64ToBytes(String base64) /*-{
	    var raw = atob(base64);
	    var len = raw.length;
	    var bytes = @com.google.gwt.core.client.JsArrayInteger::createArray()();
	    for (var i = 0; i < len; i++) {
	        bytes.push(raw.charCodeAt(i));
	    }
	    return bytes;
	}-*/;

    private void uploadRecordDataFile(FileUpload fileUpload, RecordData recordData, Runnable onSuccess) {

        getFileAsBase64(fileUpload.getElement(), (base64, mimeType, fileName, fileSize) -> {

            String json = buildAttachJson(
                    recordData.getAttach(),
                    base64,
                    mimeType,
                    fileName,
                    fileSize,
                    (byte) 1,
                    recordData.getId()
            );

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
            callback.@com.esferalia.aon.gwt.common.client.widget.solutions.AonRecordDataPanel.FileReadCallback::onRead(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)
                ("", "", "", 0);
            return;
        }

        var reader = new FileReader();
        reader.onload = function(e) {
            var base64 = e.target.result.split(",")[1];
            callback.@com.esferalia.aon.gwt.common.client.widget.solutions.AonRecordDataPanel.FileReadCallback::onRead(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)
                (base64, file.type, file.name, file.size);
        };
        reader.readAsDataURL(file);
    }-*/;

    public static interface FileReadCallback {
        void onRead(String base64, String mimeType, String fileName, int fileSize);
    }

    private String buildAttachJson(Integer id, String base64, String mimeType, String fileName,
                                   int fileSize, byte type, Integer recordDataId) {

        StringBuilder sb = new StringBuilder("{");

        if (id != null)
            sb.append("\"id\":").append(id).append(",");

        sb.append("\"domain\":{\"id\":").append(domainId)
                .append(",\"name\":\"").append(escapeJson(domainName)).append("\"},");

        sb.append("\"attachType\":\"REGISTRY\",");
        sb.append("\"attachModule\":").append(recordData.getRegistry()).append(",");

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
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private Widget createButtonsPanel() {
        HTMLPanel buttonsPanel = new HTMLPanel(EMPTY_STRING);
        buttonsPanel.setStyleName(AON.CSS.aonTextCenter());
        buttonsPanel.getElement().getStyle().setProperty("margin-top", "1rem");

        Button okButton = new Button();
        okButton.setStyleName(AON.CSS.aonOkButton());
        okButton.setText(AON.MSG.accept());
        okButton.addClickHandler(e -> {
            okButton.setEnabled(false);

            recordData
                    .setCreationDate(date.getValue())
                    .setType(RecordDataType.safeValueOf(type.getValue()))
                    .setDescription(description.getValue())
                    .setRecordDate(registryDate.getValue())
                    .setNotary(notary.getValue())
                    .setNumber(protocol.getValue())
                    .setVolume(tomo.getValue())
                    .setSection(section.getValue())
                    .setPage(page.getValue())
                    .setSheet(sheet.getValue())
                    .setRegistration(inscription.getValue())
                    .setCommercialRegistryCode(AonStringUtils.isBlank(commercialRegistryCode.getValue()) ? null :
                            CommercialRegistryCode.safeValueOf(Integer.parseInt(commercialRegistryCode.getValue())))
                    .setIrus(irus.getValue());

            commonService.saveRecordData(domainName, domainId, user, recordData, new AsyncCallback<RecordData>() {

                @Override
                public void onSuccess(RecordData result) {

                    if (!recordDataExistedAtStart) {
                        recordData = result;
                        recordDataExistedAtStart = true;
                        show();
                        return;
                    }

                    callback.onAccept(result);
                }

                @Override
                public void onFailure(Throwable error) {
                    AonMessagePanel.showError(messagePanel, error.getMessage());
                    okButton.setEnabled(true);
                }
            });
        });
        buttonsPanel.add(okButton);

        Button cancelButton = new Button();
        cancelButton.setStyleName(AON.CSS.aonCancelButton());
        cancelButton.addStyleName(AON.CSS.aonMarginLeft());
        cancelButton.setText(AON.MSG.close());
        cancelButton.addClickHandler(e -> {
            cancelButton.setEnabled(false);
            callback.onCancel();
        });
        buttonsPanel.add(cancelButton);

        return buttonsPanel;
    }

    private void getRecordData(Consumer<RecordData> success) {
        commonService.getRecordData(domainName, domainId, user, recordData.getId(), new AsyncCallback<RecordData>() {

            @Override
            public void onSuccess(RecordData recordDataDB) {
                recordData = recordDataDB;
                success.accept(recordDataDB);
            }

            @Override
            public void onFailure(Throwable caught) {
                AonMessagePanel.showError(messagePanel, "RecordData error : " + caught.getMessage());
            }

        });
    }
}
