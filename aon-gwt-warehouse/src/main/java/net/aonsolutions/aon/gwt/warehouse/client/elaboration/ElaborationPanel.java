package net.aonsolutions.aon.gwt.warehouse.client.elaboration;

import java.util.Date;

import net.aonsolutions.aon.gwt.warehouse.client.widget.ItemBox;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.api.client.product.JsItem;
import com.esferalia.aon.gwt.api.client.product.JsProduct;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
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
	HorizontalPanel itemPanel;
	@UiField
	ListBox warehouse;
	@UiField
	TextBox quantity;
	@UiField
//	ListBox status;
	InlineLabel status;
//	@UiField
//	TextArea comments;

	private MainElaboration parent;
	private API API;
	private JsElaboration jsElaboration;

	public ElaborationPanel(MainElaboration me) {
		initWidget(binder.createAndBindUi(this));
		this.API = me.API;
		this.parent = me;
		this.jsElaboration = parent.getJsElaboration();
		load();
	}

	public ElaborationPanel(MainElaboration me, JsElaboration jsElaboration) {
		initWidget(binder.createAndBindUi(this));
		this.API = me.API;
		this.parent = me;
		this.jsElaboration = jsElaboration;
		load();
	}

	private void load() {

		series.setText(jsElaboration != null && jsElaboration.getSeries() != null
				? (jsElaboration.getSeries() + "") : "");
		number.setText(jsElaboration != null && jsElaboration.getNumber() != null
				? (jsElaboration.getNumber() + "") : "");
		status.setText(jsElaboration != null && jsElaboration.getStatus() != null
				? (jsElaboration.getStatus().getName() + "") : "");
		
//		API.getWarehouse().getElaborationStatuses(new AsyncCallback<JSON<JsObject>>() {
//			
//			@Override
//			public void onSuccess(JSON<JsObject> result) {
//				result.getData().stream()
//				.filter(s -> !s.getName().equalsIgnoreCase("fallido")
//						&& !s.getName().equalsIgnoreCase("reabierto")
//						&& !s.getName().equalsIgnoreCase("en progreso"))
//						.forEach(s -> status.addItem(s.getName(), s.getId() + ""));
//				
//				if (jsElaboration != null && jsElaboration.getStatus() != null) {
//					for (Integer i = 0; i < status.getItemCount(); i++) {
//						if (jsElaboration.getStatus() != null
//								& jsElaboration.getStatus().getName().equals(status.getItemText(i))) {
//							status.setSelectedIndex(i);
//						}
//					}
//				}
//			}
//			
//			@Override
//			public void onFailure(Throwable caught) {
//			}
//		});
		
		if (jsElaboration != null && jsElaboration.getDate() != null
				&& !"".equals(jsElaboration.getDate())) {
			// TODO
//			Date issueDate = DateTimeFormat.getFormat("dd/MM/yyyy").parse(jsElaboration.getDate());
			Date issueDate = DateTimeFormat.getFormat("yyyy-MM-dd").parse(jsElaboration.getDate());
			date.setValue(issueDate);
		}
		
//		comments.setText(jsElaboration != null && 
//				jsElaboration.getComments() != null ? jsElaboration.getComments() : "");
//		comments.setCharacterWidth(20);
//		comments.setVisibleLines(3);

		if(jsElaboration != null && jsElaboration.getItem() != null){
//			API.getProduct().getProduct(jsElaboration.getItem().getProductId(), new AsyncCallback<JSON<JsProduct>>() {
//				
//				@Override
//				public void onSuccess(JSON<JsProduct> result) {
//					ProductBox pBox = new ProductBox(API);
//					if(result!=null){
//						pBox.set(result.getOneData());
//					}
//					itemPanel.add(pBox);
//				}
//				
//				@Override
//				public void onFailure(Throwable caught) {
//				}
//			});
			API.getProduct().getItem(jsElaboration.getItem().getId(), new AsyncCallback<JSON<JsItem>>() {
				
				@Override
				public void onSuccess(JSON<JsItem> result) {
					ItemBox pBox = new ItemBox(API);
					if(result!=null){
						pBox.set(result.getOneData());
					}
					itemPanel.add(pBox);
				}
				
				@Override
				public void onFailure(Throwable caught) {
				}
			});
		}
		

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
		
//		series.addChangeHandler(getUpdateChangeListener());
//		number.addChangeHandler(getUpdateChangeListener());
////		status.addChangeHandler(getUpdateChangeListener());
//		date.addValueChangeHandler(getUpdateChangeHandler());
////		comments.addChangeHandler(getUpdateChangeListener());
//		item.addChangeHandler(getUpdateChangeListener());
//		quantity.addChangeHandler(getUpdateChangeListener());
//		warehouse.addChangeHandler(getUpdateChangeListener());
		
	}

	private ChangeHandler getUpdateChangeListener() {
		return new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				parent.updateElaboration(getJsElaboration());
			}
		};
	}
	
	private <T> ValueChangeHandler<T> getUpdateChangeHandler() {
		return new ValueChangeHandler<T>() {

			@Override
			public void onValueChange(ValueChangeEvent<T> event) {
				parent.updateElaboration(getJsElaboration());
			}
		};
	}

	public JsElaboration getJsElaboration() {
		return jsElaboration;
	}
	public void setJsElaboration(JsElaboration jsElaboration) {
		this.jsElaboration = jsElaboration;
	}
}
