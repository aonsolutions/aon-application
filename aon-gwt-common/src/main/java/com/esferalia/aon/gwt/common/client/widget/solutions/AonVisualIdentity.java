package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.registry.RegistrySource;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class AonVisualIdentity extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;

	private static final Logger LOGGER = Logger.getLogger(AonVisualIdentity.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}

	private static final String ATTACH_SERVLET_URL = "/ms/api/attach/";
	private static final String SESSION_API = "AONd95770f269e711eb94390242ac130002";

	// -------------------------------------------------------------------------
	// Paleta de colores y constantes de estilo
	// -------------------------------------------------------------------------
	private static final String COLOR_PRIMARY = "#0066cc";
	private static final String COLOR_PRIMARY_HOVER = "#0052a3";
	private static final String COLOR_TEXT_SECONDARY = "#6b7280";
	private static final String COLOR_BORDER = "#e5e7eb";
	private static final String COLOR_BORDER_DRAG = "#0066cc";
	private static final String COLOR_BG_CARD = "#ffffff";
	private static final String COLOR_BG_PLACEHOLDER = "#f3f4f6";
	private static final String COLOR_BG_DRAG = "#e8f0fb";
	private static final String BORDER_RADIUS_BTN = "4px";

	// -------------------------------------------------------------------------
	// Estado
	// -------------------------------------------------------------------------
	private String domainName;
	private Integer domain;
	private String user;
	private Integer registry;
	private RegistrySource registrySource;

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------

	public AonVisualIdentity(String domainName, int domain, String user, Integer registry,
			RegistrySource registrySource) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		this.domainName = domainName;
		this.domain = domain;
		this.user = user;
		this.registry = registry;
		this.registrySource = registrySource;

		HTMLPanel container = new HTMLPanel("");
		styleContainer(container);

		container.add(buildAttachCard("Logo", RegistryAttachmentType.LOGO, this::getCompanyLogo));
		if (!RegistrySource.ENVIROMENT.equals(this.registrySource)) {
			container.add(buildAttachCard("Firma", RegistryAttachmentType.SIGNATURE, this::getCompanySignature));
		}

		setWidget(container);
	}

	// -------------------------------------------------------------------------
	// Construcción de cards
	// -------------------------------------------------------------------------

	private AonCustomCard buildAttachCard(String title, RegistryAttachmentType attachType,
			Consumer<Consumer<Attach>> fetcher) {
		// Sin botón en la toolbar  el delete va en el hover del preview
		AonCustomCard card = new AonCustomCard(title);

		// Card con altura fija y flexbox para empujar los botones al fondo
		Style s = card.getElement().getStyle(); // ajusta según tu AonCustomCard
		s.setDisplay(Display.FLEX);
		s.setProperty("flexDirection", "column");
		s.setHeight(320, Unit.PX);

		FlowPanel contentPanel = new FlowPanel();
		// contentPanel ocupa todo el espacio disponible
		contentPanel.getElement().getStyle().setProperty("flex", "1");
		contentPanel.getElement().getStyle().setDisplay(Display.FLEX);
		contentPanel.getElement().getStyle().setProperty("flexDirection", "column");
		card.add(contentPanel);

		fetcher.accept(attach -> renderAttach(attach, attachType, fetcher, contentPanel));

		return card;
	}

	// -------------------------------------------------------------------------
	// Renderizado dinámico del contenido
	// -------------------------------------------------------------------------

	private void renderAttach(Attach attach, RegistryAttachmentType attachType, Consumer<Consumer<Attach>> fetcher,
			FlowPanel contentPanel) {
		contentPanel.clear();

		boolean hasAttach = attach != null && attach.getData() != null && attach.getData().length > 0;

		Image previewImg = new Image();
		stylePreviewImage(previewImg);

		// dropZone: ocupa el espacio central, hace flex-grow para igualar altura
		FlowPanel dropZone = new FlowPanel();
		styleDropZoneEmpty(dropZone);
		// Crece para ocupar el espacio disponible entre título y botones
		dropZone.getElement().getStyle().setProperty("flex", "1");
		dropZone.getElement().getStyle().setProperty("position", "relative");

		if (hasAttach) {
			String mime = attach.getMimeType() != null ? attach.getMimeType().getName() : "";
			if (mime.startsWith("image/")) {
				previewImg.setUrl("data:" + mime + ";base64," + base64Encode(attach.getData()));
				previewImg.setAltText(attach.getDescription());
				styleDropZoneWithImage(dropZone);
				dropZone.getElement().getStyle().setProperty("flex", "1");
				dropZone.getElement().getStyle().setProperty("position", "relative");
				dropZone.add(previewImg);
			} else {
				previewImg.getElement().getStyle().setDisplay(Display.NONE);
				dropZone.add(buildFileIcon(attach));
			}

			// Overlay de borrado que aparece en hover sobre el preview
			HTML deleteOverlay = buildDeleteOverlay();
			dropZone.add(deleteOverlay);
			registerDeleteHover(dropZone.getElement(), deleteOverlay.getElement());
			deleteOverlay.addClickHandler(e -> onDeleteAttach(attach, attachType, fetcher, contentPanel));

			contentPanel.add(dropZone);
			contentPanel.add(buildNameLabel(attach.getDescription()));
		} else {
			previewImg.getElement().getStyle().setDisplay(Display.NONE);
			dropZone.add(previewImg);
			dropZone.add(buildPlaceholderLabel());
			contentPanel.add(dropZone);
		}

		// uploadArea siempre al fondo
		contentPanel.add(buildUploadArea(attach, attachType, fetcher, contentPanel, previewImg, dropZone));
	}

	// Overlay semitransparente con texto "Eliminar" que aparece en hover
	private HTML buildDeleteOverlay() {
		HTML overlay = new HTML("Eliminar");
		Style s = overlay.getElement().getStyle();
		s.setProperty("position", "absolute");
		s.setProperty("top", "0");
		s.setProperty("left", "0");
		s.setWidth(100, Unit.PCT);
		s.setHeight(100, Unit.PCT);
		s.setDisplay(Display.FLEX);
		s.setProperty("alignItems", "center");
		s.setProperty("justifyContent", "center");
		s.setBackgroundColor("rgba(0,0,0,0.45)");
		s.setColor("#ffffff");
		s.setFontSize(14, Unit.PX);
		s.setProperty("fontWeight", "bold");
		s.setProperty("borderRadius", BORDER_RADIUS_BTN);
		s.setCursor(Cursor.POINTER);
		s.setProperty("opacity", "0");
		s.setProperty("transition", "opacity 0.2s ease");
		return overlay;
	}

	private native void registerDeleteHover(com.google.gwt.dom.client.Element zone,
			com.google.gwt.dom.client.Element overlay) /*-{
	    zone.addEventListener('mouseenter', function() { overlay.style.opacity = '1'; });
	    zone.addEventListener('mouseleave', function() { overlay.style.opacity = '0'; });
	}-*/;

	// -------------------------------------------------------------------------
	// Drop zone: zona de drag & drop
	// -------------------------------------------------------------------------

	/**
	 * Registra los eventos dragover, dragleave y drop sobre la dropZone mediante
	 * JSNI. Al soltar un fichero se llama al mismo callback que usa FileReader en
	 * el selector.
	 */
	private native void registerDropZone(com.google.gwt.dom.client.Element dropEl,
			com.google.gwt.dom.client.Element inputEl, FileReadCallback callback, String borderDrag,
			String borderNormal, String bgDrag, String bgNormal) /*-{
		dropEl.addEventListener('dragover', function(e) {
			e.preventDefault();
			e.stopPropagation();
			dropEl.style.borderColor = borderDrag;
			dropEl.style.backgroundColor = bgDrag;
		});
		dropEl.addEventListener('dragleave', function(e) {
			e.preventDefault();
			e.stopPropagation();
			dropEl.style.borderColor = borderNormal;
			dropEl.style.backgroundColor = bgNormal;
		});
		dropEl.addEventListener('drop', function(e) {
			e.preventDefault();
			e.stopPropagation();
			dropEl.style.borderColor = borderNormal;
			dropEl.style.backgroundColor = bgNormal;

			var file = e.dataTransfer.files[0];
			if (!file) return;

			// Sincronizamos el input[type=file] con el fichero soltado (no siempre posible
			// por seguridad del navegador, pero intentamos actualizar su valor visual)
			var reader = new FileReader();
			reader.onload = function(evt) {
				var base64   = evt.target.result.split(",")[1];
				var mimeType = file.type || "application/octet-stream";
				callback.@com.esferalia.aon.gwt.common.client.widget.solutions.AonVisualIdentity.FileReadCallback::onRead(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)(
					base64, mimeType, file.name, file.size
				);
			};
			reader.readAsDataURL(file);
		});
	}-*/;

	// -------------------------------------------------------------------------
	// buildUploadArea
	// -------------------------------------------------------------------------

	private FlowPanel buildUploadArea(Attach existing, RegistryAttachmentType attachType,
			Consumer<Consumer<Attach>> fetcher, FlowPanel contentPanel, Image previewImg, FlowPanel dropZone) {

		FileUpload fileUpload = new FileUpload();
		fileUpload.setName("file");
		fileUpload.getElement().setAttribute("accept", "image/*,application/pdf");
		fileUpload.getElement().getStyle().setDisplay(Display.NONE);

		Label fileNameLabel = new Label("Ning\u00fan archivo seleccionado");
		styleFileNameLabel(fileNameLabel);

		Button uploadBtn = new Button(existing != null && existing.getId() != null ? "Reemplazar" : "Subir");
		uploadBtn.getElement().getStyle().setDisplay(Display.NONE);

		final FlowPanel uploadPanel = new FlowPanel();
		Style s = uploadPanel.getElement().getStyle();
		s.setDisplay(Display.FLEX);
		s.setProperty("flexDirection", "column");
		s.setProperty("gap", "8px");
		s.setMarginTop(8, Unit.PX);

		FileReadCallback onFileRead = (base64, mimeType, fileName, fileSize) -> {
			fileNameLabel.setText(fileName);
			if (mimeType.startsWith("image/")) {
				previewImg.setUrl("data:" + mimeType + ";base64," + base64);
				previewImg.getElement().getStyle().setDisplay(Display.BLOCK);
				styleDropZoneWithImage(dropZone);
				dropZone.getElement().getStyle().setProperty("flex", "1");
				dropZone.getElement().getStyle().setProperty("position", "relative");
				for (int i = 0; i < dropZone.getWidgetCount(); i++) {
					com.google.gwt.user.client.ui.Widget w = dropZone.getWidget(i);
					if (w instanceof Label)
						w.getElement().getStyle().setDisplay(Display.NONE);
				}
			}
			uploadBtn.getElement().getStyle().clearDisplay();
			storeFileData(uploadPanel.getElement(), base64, mimeType, fileName, fileSize);
		};

		// Clic en la dropZone abre el selector de fichero
		registerDropZoneClick(dropZone.getElement(), fileUpload.getElement());

		registerDropZone(dropZone.getElement(), fileUpload.getElement(), onFileRead, COLOR_BORDER_DRAG, COLOR_BORDER,
				COLOR_BG_DRAG, COLOR_BG_PLACEHOLDER);

		fileUpload.addChangeHandler(e -> {
			InputElement input = fileUpload.getElement().cast();
			if (input.getValue() == null || input.getValue().isEmpty()) {
				fileNameLabel.setText("Ning\u00fan archivo seleccionado");
				uploadBtn.getElement().getStyle().setDisplay(Display.NONE);
				return;
			}
			readFileAsBase64(fileUpload.getElement(), onFileRead);
		});

		uploadBtn.addClickHandler((ClickEvent e) -> {
			String[] data = getFileData(uploadPanel.getElement());
			if (data != null) {
				doUpload(data[0], data[1], data[2], Integer.parseInt(data[3]), existing, attachType, fetcher,
						contentPanel);
			} else {
				readFileAsBase64(fileUpload.getElement(), (base64, mimeType, fileName, fileSize) -> doUpload(base64,
						mimeType, fileName, fileSize, existing, attachType, fetcher, contentPanel));
			}
		});

		uploadPanel.add(fileUpload);
		uploadPanel.add(fileNameLabel);
		uploadPanel.add(uploadBtn);

		return uploadPanel;
	}

	/** Clic en la dropZone abre el file picker (excepto si hay overlay encima) */
	private native void registerDropZoneClick(com.google.gwt.dom.client.Element dropEl,
			com.google.gwt.dom.client.Element inputEl) /*-{
	    dropEl.addEventListener('click', function(e) {
	        // Evitamos abrir el picker si el clic fue en el overlay de borrado
	        if (e.target !== dropEl && e.target.tagName !== 'IMG') return;
	        inputEl.click();
	    });
	}-*/;

	// -------------------------------------------------------------------------
	// Almacenamiento temporal del fichero en el elemento DOM (atributos data-*)
	// -------------------------------------------------------------------------

	private native void storeFileData(com.google.gwt.dom.client.Element el, String base64, String mimeType,
			String fileName, int fileSize) /*-{
		el.__fileBase64   = base64;
		el.__fileMimeType = mimeType;
		el.__fileName     = fileName;
		el.__fileSize     = fileSize;
	}-*/;

	private native String[] getFileData(com.google.gwt.dom.client.Element el) /*-{
		if (!el.__fileBase64) return null;
		return [el.__fileBase64, el.__fileMimeType, el.__fileName, String(el.__fileSize)];
	}-*/;

	// -------------------------------------------------------------------------
	// POST al servlet
	// -------------------------------------------------------------------------

	private void doUpload(String base64, String mimeType, String fileName, int fileSize, Attach existing,
			RegistryAttachmentType attachType, Consumer<Consumer<Attach>> fetcher, FlowPanel contentPanel) {

		String json = buildAttachJson(existing != null ? existing.getId() : null, base64, mimeType, fileName, fileSize,
				attachType.value());

		com.google.gwt.http.client.RequestBuilder rb = new com.google.gwt.http.client.RequestBuilder(
				com.google.gwt.http.client.RequestBuilder.POST, ATTACH_SERVLET_URL);
		rb.setHeader("Content-Type", "application/json; charset=UTF-8");
		rb.setHeader("session_id", SESSION_API);

		try {
			rb.sendRequest(json, new com.google.gwt.http.client.RequestCallback() {
				@Override
				public void onResponseReceived(com.google.gwt.http.client.Request req,
						com.google.gwt.http.client.Response resp) {
					if (resp.getStatusCode() >= 200 && resp.getStatusCode() < 300) {
						LOGGER.info("Attach subido. Tipo: " + attachType);
						fetcher.accept(a -> renderAttach(a, attachType, fetcher, contentPanel));
					} else {
						LOGGER.severe("Error HTTP: " + resp.getStatusCode());
						Window.alert("Error al subir: HTTP " + resp.getStatusCode());
					}
				}

				@Override
				public void onError(com.google.gwt.http.client.Request req, Throwable ex) {
					LOGGER.severe("Error de red: " + ex.getMessage());
					Window.alert("Error de red al subir el archivo.");
				}
			});
		} catch (com.google.gwt.http.client.RequestException ex) {
			LOGGER.severe("RequestException: " + ex.getMessage());
			Window.alert("Error al iniciar la subida.");
		}
	}

	// -------------------------------------------------------------------------
	// Widgets auxiliares
	// -------------------------------------------------------------------------

	private HTML buildFileIcon(Attach attach) {
		String ext = attach.getMimeType() != null ? attach.getMimeType().getExtension().toUpperCase() : "FILE";
		return new HTML("<div style=\"display:flex;flex-direction:column;align-items:center;"
				+ "justify-content:center;height:100px;color:" + COLOR_TEXT_SECONDARY + ";\">"
				+ "<div style=\"font-size:13px;font-weight:bold;padding:8px 12px;" + "border:2px solid " + COLOR_BORDER
				+ ";border-radius:" + BORDER_RADIUS_BTN + ";" + "background:" + COLOR_BG_PLACEHOLDER + ";\">" + ext
				+ "</div>" + "</div>");
	}

	private Label buildPlaceholderLabel() {
		Label lbl = new Label("Arrastra o suelta aqui");
		Style s = lbl.getElement().getStyle();
		s.setColor(COLOR_TEXT_SECONDARY);
		s.setFontSize(13, Unit.PX);
		return lbl;
	}

	private Label buildNameLabel(String description) {
		Label label = new Label(description);
		Style s = label.getElement().getStyle();
		s.setFontSize(13, Unit.PX);
		s.setColor(COLOR_TEXT_SECONDARY);
		s.setMarginTop(6, Unit.PX);
		s.setMarginBottom(4, Unit.PX);
		s.setProperty("textOverflow", "ellipsis");
		s.setOverflow(Style.Overflow.HIDDEN);
		s.setWhiteSpace(Style.WhiteSpace.NOWRAP);
		return label;
	}

	// -------------------------------------------------------------------------
	// JSNI utils
	// -------------------------------------------------------------------------

	private native void triggerFileInput(com.google.gwt.dom.client.Element inputElement) /*-{
		inputElement.click();
	}-*/;

	private native void readFileAsBase64(com.google.gwt.dom.client.Element inputElement, FileReadCallback callback) /*-{
		var file = inputElement.files[0];
		if (!file) return;
		var reader = new FileReader();
		reader.onload = function(evt) {
			var base64   = evt.target.result.split(",")[1];
			var mimeType = file.type || "application/octet-stream";
			callback.@com.esferalia.aon.gwt.common.client.widget.solutions.AonVisualIdentity.FileReadCallback::onRead(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)(
				base64, mimeType, file.name, file.size
			);
		};
		reader.readAsDataURL(file);
	}-*/;

	private native String base64Encode(byte[] data) /*-{
		var binary = "";
		var bytes  = new Uint8Array(data);
		for (var i = 0; i < bytes.byteLength; i++) {
			binary += String.fromCharCode(bytes[i]);
		}
		return btoa(binary);
	}-*/;

	// -------------------------------------------------------------------------
	// JSON
	// -------------------------------------------------------------------------

	@FunctionalInterface
	interface FileReadCallback {
		void onRead(String base64, String mimeType, String fileName, int fileSize);
	}

	private String buildAttachJson(Integer id, String base64, String mimeType, String fileName, int fileSize,
			byte type) {
		StringBuilder sb = new StringBuilder("{");
		if (id != null)
			sb.append("\"id\":").append(id).append(",");
		sb.append("\"domain\":{\"id\":").append(domain).append(",\"name\":\"").append(escapeJson(domainName))
				.append("\"},");
		sb.append("\"attachType\":\"").append(AttachType.REGISTRY.name()).append("\",");
		sb.append("\"attachModule\":").append(registry).append(",");
		sb.append("\"name\":\"").append(escapeJson(fileName)).append("\",");
		sb.append("\"contentName\":\"").append(escapeJson(fileName)).append("\",");
		sb.append("\"contentType\":\"").append(escapeJson(mimeType)).append("\",");
		sb.append("\"contentSize\":").append(fileSize).append(",");
		sb.append("\"type\":").append(type).append(",");
		sb.append("\"date\":\"").append(getCurrentDateISO()).append("\",");
		sb.append("\"content\":\"").append(base64).append("\"");
		sb.append("}");
		return sb.toString();
	}

	private String getCurrentDateISO() {
		com.google.gwt.i18n.client.DateTimeFormat fmt = com.google.gwt.i18n.client.DateTimeFormat
				.getFormat("yyyy-MM-dd'T'HH:mm:ss");
		return fmt.format(new java.util.Date());
	}

	private static String escapeJson(String value) {
		if (value == null)
			return "";
		return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	// -------------------------------------------------------------------------
	// Eliminar con confirmación
	// -------------------------------------------------------------------------

	private void onDeleteAttach(Attach attach, RegistryAttachmentType attachType, Consumer<Consumer<Attach>> fetcher,
			FlowPanel contentPanel) {
		AonDialog dialog = new AonDialog("Eliminaci\u00f3n",
				new HTML("Se va a proceder a eliminar el archivo <b>" + attach.getDescription() + "</b>.<br>"
						+ "\u00bfEst\u00e1 seguro que desea proceder con la eliminaci\u00f3n? "
						+ "Este proceso ser\u00e1 irreversible."));
		dialog.confirm(new AonAcceptDialogCallback() {
			@Override
			public void onCancel() {
			}

			@Override
			public void onAccept() {
				deleteAttach(attach.getId(),
						end -> fetcher.accept(a -> renderAttach(a, attachType, fetcher, contentPanel)));
			}
		});
	}

	// -------------------------------------------------------------------------
	// Llamadas al servicio
	// -------------------------------------------------------------------------

	private void getCompanyLogo(Consumer<Attach> success) {
		COMMON_SERVICE.getCompanyLogo(domainName, domain, user, registry, new AsyncCallback<Attach>() {
			@Override
			public void onSuccess(Attach a) {
				success.accept(a);
			}

			@Override
			public void onFailure(Throwable caught) {
				LOGGER.severe("Error logo: " + caught.getMessage());
			}
		});
	}

	private void getCompanySignature(Consumer<Attach> success) {
		COMMON_SERVICE.getCompanySignature(domainName, domain, user, registry, new AsyncCallback<Attach>() {
			@Override
			public void onSuccess(Attach a) {
				success.accept(a);
			}

			@Override
			public void onFailure(Throwable caught) {
				LOGGER.severe("Error firma: " + caught.getMessage());
			}
		});
	}

	private void deleteAttach(Integer attachId, Consumer<Void> success) {
		COMMON_SERVICE.deleteRegistryAttach(domainName, domain, user, attachId, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void end) {
				success.accept(end);
			}

			@Override
			public void onFailure(Throwable caught) {
				LOGGER.severe("Error delete: " + caught.getMessage());
			}
		});
	}

	// -------------------------------------------------------------------------
	// Métodos de estilo
	// -------------------------------------------------------------------------

	private void styleContainer(HTMLPanel panel) {
		Style s = panel.getElement().getStyle();
		s.setDisplay(Display.FLEX);
		s.setProperty("flexWrap", "wrap");
		s.setProperty("gap", "24px");
		s.setPadding(16, Unit.PX);
	}

	private void styleDropZoneEmpty(FlowPanel zone) {
		Style s = zone.getElement().getStyle();
		s.setDisplay(Display.FLEX);
		s.setProperty("alignItems", "center");
		s.setProperty("justifyContent", "center");
		s.setWidth(100, Unit.PCT);
		s.setHeight(140, Unit.PX);
		s.setBackgroundColor(COLOR_BG_PLACEHOLDER);
		s.setBorderStyle(BorderStyle.DASHED);
		s.setBorderWidth(2, Unit.PX);
		s.setBorderColor(COLOR_BORDER);
		s.setProperty("borderRadius", BORDER_RADIUS_BTN);
		s.setProperty("boxSizing", "border-box");
		s.setProperty("transition", "border-color 0.15s ease, background-color 0.15s ease");
		s.setCursor(Cursor.POINTER);
	}

	private void styleDropZoneWithImage(FlowPanel zone) {
		Style s = zone.getElement().getStyle();
		s.setDisplay(Display.BLOCK);
		s.setWidth(100, Unit.PCT);
		s.clearHeight();
		s.setBackgroundColor(COLOR_BG_PLACEHOLDER);
		s.setBorderStyle(BorderStyle.SOLID);
		s.setBorderWidth(1, Unit.PX);
		s.setBorderColor(COLOR_BORDER);
		s.setProperty("borderRadius", BORDER_RADIUS_BTN);
		s.setProperty("transition", "border-color 0.15s ease, background-color 0.15s ease");
	}

	private void stylePreviewImage(Image img) {
		Style s = img.getElement().getStyle();
		s.setWidth(100, Unit.PCT);
		s.setProperty("maxHeight", "140px");
		s.setProperty("objectFit", "contain");
		s.setProperty("borderRadius", BORDER_RADIUS_BTN);
		s.setDisplay(Display.BLOCK);
	}

	private void styleFileNameLabel(Label label) {
		Style s = label.getElement().getStyle();
		s.setFontSize(12, Unit.PX);
		s.setColor(COLOR_TEXT_SECONDARY);
		s.setProperty("flex", "1");
		s.setProperty("minWidth", "0");
		s.setProperty("textOverflow", "ellipsis");
		s.setOverflow(Style.Overflow.HIDDEN);
		s.setWhiteSpace(Style.WhiteSpace.NOWRAP);
	}
}