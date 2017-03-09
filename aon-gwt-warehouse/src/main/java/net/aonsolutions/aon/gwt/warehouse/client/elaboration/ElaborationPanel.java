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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ElaborationPanel extends Composite {

	interface Binder extends UiBinder<Widget, ElaborationPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	ListBox series;
	@UiField
	TextBox number;
	@UiField
	DateBoxEx date;
	@UiField
	ListBox item;
	@UiField
	ListBox warehouse;
	@UiField
	TextBox quantity;
	@UiField
	ListBox status;
	@UiField
	TextBox comments;
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
		API.getWarehouse().getCarrierPackingSeries(new AsyncCallback<JSON<JsObject>>() {

			@Override
			public void onSuccess(JSON<JsObject> result) {
				result.getData().stream().forEach(s -> series.addItem(s.getName()));
				if (jsElaboration != null && jsElaboration.getSeries() != null) {
					for (Integer i = 0; i < series.getItemCount(); i++) {
						if (jsElaboration.getSeries().equals(series.getItemText(i))) {
							series.setSelectedIndex(i);
						}
					}
				}
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
		series.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				updateElaboration();
			}
		});
		number.setText(jsElaboration != null && jsElaboration.getNumber() != null
				? (jsElaboration.getNumber() + "") : "");
		number.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				updateElaboration();
			}
		});

		API.getWarehouse().getCarrierPackingStatuses(new AsyncCallback<JSON<JsObject>>() {

			@Override
			public void onSuccess(JSON<JsObject> result) {
				result.getData().stream().forEach(s -> status.addItem(s.getName(), s.getId() + ""));

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

		status.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				updateElaboration();
			}
		});

		if (jsElaboration != null && jsElaboration.getDate() != null
				&& !"".equals(jsElaboration.getDate())) {
			Date issueDate = DateTimeFormat.getFormat("dd/MM/yyyy").parse(jsElaboration.getDate());
			date.setValue(issueDate);
		}
		date.addValueChangeHandler(new ValueChangeHandler<Date>() {

			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				updateElaboration();
			}
		});

		if (jsElaboration != null && jsElaboration.getComments() != null) {
			comments.setText(
					jsElaboration.getComments() != null ? jsElaboration.getComments() : "");
		}
		comments.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				updateElaboration();
			}
		});

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
