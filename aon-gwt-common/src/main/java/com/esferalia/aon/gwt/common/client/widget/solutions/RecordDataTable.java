package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRecordDataPanel.AonRecordDataPanelCallback;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
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
		CAD("F. Creaci\u00f3n", "8rem", "max-width: 8rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		NAM("Nombre", "-moz-available", "min-width: 8rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		NOT("Notario", "-moz-available", "min-width: 8rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
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

		if (null != recordData.getAttach() && null != recordData.getFullAttach().getData()) {
			AonTableButton preview = new AonTableButton("Previsualizar", AON.CSS.aonIconPdf());
			preview.addStyleName(AON.CSS.aonCustomRowButtom());
			preview.addClickHandler(e -> {
				// Evitar que el click suba a la fila
			    e.preventDefault();
			    e.stopPropagation();
			    e.getNativeEvent().stopPropagation();
			    e.getNativeEvent().preventDefault();


				AonAttachPreviewPanel popup = new AonAttachPreviewPanel(recordData.getFullAttach());
				popup.center();
				popup.show();
			});
			buttonContainer.add(preview);
		} else if (recordData.getAttach() != null && recordData.getFullAttach().getDriveId() != null) {

			AonTableButton preview = new AonTableButton("Previsualizar", AON.CSS.aonIconPdf());
			preview.addStyleName(AON.CSS.aonCustomRowButtom());

			preview.addClickHandler(e -> {
				// Evitar que el click suba a la fila
			    e.preventDefault();
			    e.stopPropagation();
			    e.getNativeEvent().stopPropagation();
			    e.getNativeEvent().preventDefault();

			    JSONObject json = new JSONObject();
			    json.put("domain_id", new JSONNumber(domain));
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

			        AonAttachPreviewPanel popup = new AonAttachPreviewPanel(temp);
			        popup.center();
			        popup.show();
			    });
			});
		    buttonContainer.add(preview);
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
