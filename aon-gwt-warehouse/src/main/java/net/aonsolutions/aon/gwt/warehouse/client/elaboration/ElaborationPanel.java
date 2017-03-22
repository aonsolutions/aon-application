package net.aonsolutions.aon.gwt.warehouse.client.elaboration;

import java.util.Date;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.api.client.warehouse.JsElaboration;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ElaborationPanel extends Composite {

	interface Binder extends UiBinder<Widget, ElaborationPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
//	ListBox series;
	TextBox series;
	@UiField
	TextBox number;
	@UiField
	DateBoxEx date;
	@UiField
//	ListBox item;
	TextBox item;
	@UiField
	ListBox warehouse;
	@UiField
	TextBox quantity;
	@UiField
	ListBox status;
	@UiField
	TextArea comments;
//	source
//	source_id

	private Elaboration parent;
	private API API;
	private JsElaboration jsElaboration;

	public ElaborationPanel(Elaboration elaboration) {
		initWidget(binder.createAndBindUi(this));
		this.API = elaboration.API;
		this.parent = elaboration;
		load();
	}

	public ElaborationPanel(Elaboration elaboration, JsElaboration js) {
		initWidget(binder.createAndBindUi(this));
		this.API = elaboration.API;
		this.parent = elaboration;
		this.jsElaboration = js;
		load();
	}

	private void load() {
		// TODO elaboration series
//		API.getWarehouse().getCarrierPackingSeries(new AsyncCallback<JSON<JsObject>>() {
//
//			@Override
//			public void onSuccess(JSON<JsObject> result) {
//				result.getData().stream().forEach(s -> series.addItem(s.getName()));
//				if (jsElaboration != null && jsElaboration.getSeries() != null) {
//					for (Integer i = 0; i < series.getItemCount(); i++) {
//						if (jsElaboration.getSeries().equals(series.getItemText(i))) {
//							series.setSelectedIndex(i);
//						}
//					}
//				}
//			}
//
//			@Override
//			public void onFailure(Throwable caught) {
//			}
//		});
		series.setText(jsElaboration != null && jsElaboration.getSeries() != null
				? (jsElaboration.getSeries() + "") : "");
		number.setText(jsElaboration != null && jsElaboration.getNumber() != null
				? (jsElaboration.getNumber() + "") : "");
		
		API.getWarehouse().getElaborationStatuses(new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> result) {
				result.getData().stream()
				.filter(s -> !s.getName().equalsIgnoreCase("fallido")
						&& !s.getName().equalsIgnoreCase("reabierto")
						&& !s.getName().equalsIgnoreCase("en progreso"))
						.forEach(s -> status.addItem(s.getName(), s.getId() + ""));
				
				if (jsElaboration != null && jsElaboration.getStatus() != null) {
					for (Integer i = 0; i < status.getItemCount(); i++) {
						if (jsElaboration.getStatus() != null
								& jsElaboration.getStatus().getName().equals(status.getItemText(i))) {
							status.setSelectedIndex(i);
						}
					}
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
			}
		});
		
		if (jsElaboration != null && jsElaboration.getDate() != null
				&& !"".equals(jsElaboration.getDate())) {
			// TODO
//			Date issueDate = DateTimeFormat.getFormat("dd/MM/yyyy").parse(jsElaboration.getDate());
			Date issueDate = DateTimeFormat.getFormat("yyyy-MM-dd").parse(jsElaboration.getDate());
			date.setValue(issueDate);
		}
		
		comments.setText(jsElaboration != null && 
				jsElaboration.getComments() != null ? jsElaboration.getComments() : "");
		comments.setCharacterWidth(20);
		comments.setVisibleLines(3);

		// TODO item: suggestBox? listBox? lookup?
		item.setText(jsElaboration != null && jsElaboration.getItem() != null
				? (jsElaboration.getItem().getName() + "") : "");
		item.setReadOnly(true);

		quantity.setText(jsElaboration != null && jsElaboration.getQuantity() != null
				? (jsElaboration.getQuantity() + "") : "");
		
		API.getWarehouse().getWarehouseList(new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> result) {
				result.getData().stream()
				.forEach(w -> warehouse.addItem(w.getName(), w.getId() + ""));
				
				if (jsElaboration != null && jsElaboration.getStatus() != null) {
					for (Integer i = 0; i < warehouse.getItemCount(); i++) {
						if (jsElaboration.getStatus() != null
								& jsElaboration.getStatus().getName().equals(warehouse.getItemText(i))) {
							warehouse.setSelectedIndex(i);
						}
					}
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
			}
		});
		
		series.addChangeHandler(getUpdateChangeListener());
		number.addChangeHandler(getUpdateChangeListener());
		status.addChangeHandler(getUpdateChangeListener());
		date.addValueChangeHandler(getUpdateChangeHandler());
		comments.addChangeHandler(getUpdateChangeListener());
		item.addChangeHandler(getUpdateChangeListener());
		quantity.addChangeHandler(getUpdateChangeListener());
		warehouse.addChangeHandler(getUpdateChangeListener());
		
	}

	private ChangeHandler getUpdateChangeListener() {
		return new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateElaboration();
			}
		};
	}
	
	private <T> ValueChangeHandler<T> getUpdateChangeHandler() {
		return new ValueChangeHandler<T>() {

			@Override
			public void onValueChange(ValueChangeEvent<T> event) {
				updateElaboration();
			}
		};
	}
	
	
	private void updateElaboration() {
//		if (jsElaboration != null) {
//			API.getWarehouse().updateElaboration(jsElaboration.getId(), getData(),
//					new AsyncCallback<JsElaboration>() {
//
//						@Override
//						public void onSuccess(JsElaboration result) {
//							jsElaboration = result;
//							number.setValue(result.getNumber() + "");
//							parent.setSelectContent(result);
//						}
//
//						@Override
//						public void onFailure(Throwable caught) {
//						}
//					});
//		} else {
//			this.API.getWarehouse().insertElaboration(getData(), new AsyncCallback<JsElaboration>() {
//
//				@Override
//				public void onSuccess(JsElaboration result) {
//					jsElaboration = result;
//					number.setValue(result.getNumber() + "");
//					parent.setSelectContent(result);
//				}
//
//				@Override
//				public void onFailure(Throwable caught) {
//				}
//			});
//		}
	}

	private String getData() {
//		return "{\"series\":\"" + series.getSelectedValue() + "\"," + "\"number\":\"" + number.getValue() + "\","
//				+ "\"type\":\"" + type.getSelectedValue() + "\"," + "\"status\":\"" + status.getSelectedValue() + "\","
//				+ "\"issue_date\":\"" + (issueDate.getValue() != null ? issueDate.getValue().getTime() : "") + "\","
//				+ "\"delivery_date\":\"" + (deliveryDate.getValue() != null ? deliveryDate.getValue().getTime() : "")
//				+ "\"," + "\"carrier\":\"" + carrier.getSelectedValue() + "\"," + "\"carrier_reference\":\""
//				+ reference.getValue() + "\"," + "\"number_plate\":\"" + numberPlate.getValue() + "\","
//				+ "\"driver_document\":\"" + driverDocument.getValue() + "\"," + "\"driver_name\":\""
//				+ driverName.getValue() + "\"" + "}";
		return null;
	}

	public JsElaboration getJsElaboration() {
		return jsElaboration;
	}
}
