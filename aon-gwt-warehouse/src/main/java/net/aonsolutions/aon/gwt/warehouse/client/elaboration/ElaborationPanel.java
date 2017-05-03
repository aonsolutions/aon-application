package net.aonsolutions.aon.gwt.warehouse.client.elaboration;

import java.util.Date;

import net.aonsolutions.aon.gwt.warehouse.client.widget.ItemBox;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.api.client.product.JsItem;
import com.esferalia.aon.gwt.api.client.warehouse.JsElaboration;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.occam.api.model.type.ElaborationStatus;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
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
	TextBox series;
	@UiField
	TextBox number;
	@UiField
	Button closeButton;
	@UiField
	Button reopenButton;
	@UiField
	DateBoxEx date;
	@UiField
	HorizontalPanel itemPanel;
	@UiField
	ListBox warehouse;
	@UiField
	TextBox quantity;
	@UiField
	InlineLabel status;

	private Elaboration parent;
	private API API;
	private JsElaboration jsElaboration;
	ItemBox itemBox;
	InlineLabel descriptionLabel = new InlineLabel();

	public ElaborationPanel(Elaboration e) {
		initWidget(binder.createAndBindUi(this));
		this.API = e.getAPI();
		this.parent = e;
		this.jsElaboration = parent.getJsElaboration();
		load();
	}

	public ElaborationPanel(Elaboration e, JsElaboration jsElaboration) {
		initWidget(binder.createAndBindUi(this));
		this.API = e.getAPI();
		this.parent = e;
		this.jsElaboration = jsElaboration;
		load();
	}
	

	private void load() {

		series.setText(jsElaboration != null && jsElaboration.getSeries() != null
				? (jsElaboration.getSeries() + "") : "");
		if(jsElaboration != null && jsElaboration.getNumber() != null){
			number.setText(jsElaboration.getNumber() + "");
		} else {			
			number.setText("auto");
			number.setEnabled(false);
		}
		status.setText(jsElaboration != null && jsElaboration.getStatus() != null
				? (jsElaboration.getStatus().getName() + "") : "");
		
		if (jsElaboration != null && jsElaboration.getDate() != null
				&& !"".equals(jsElaboration.getDate())) {
			Date issueDate = DateTimeFormat.getFormat("yyyy/MM/dd").parse(jsElaboration.getDate());
			date.setValue(issueDate);
		}
		
		if(jsElaboration != null && jsElaboration.getItem() != null
			&& jsElaboration.getItem().getId() != null){
			API.getProduct().getItem(jsElaboration.getItem().getId(), new AsyncCallback<JSON<JsItem>>() {
				
				@Override
				public void onSuccess(JSON<JsItem> result) {
					if(jsElaboration.getDescription()==null)
						itemBox = new ItemBox(API);
					else
						itemBox = new ItemBox(API, false);
					if(result!=null){
						JsItem jsItem = result.getOneData();
						itemBox.set(jsItem);
						descriptionLabel.setText(jsItem.getName());
					}
					itemPanel.add(itemBox);
					if(jsElaboration.getDescription()!=null
						&& !"".equals(jsElaboration.getDescription())){
						descriptionLabel.addStyleName(AON.AON_CSS.aonMarginLeft() );
						descriptionLabel.addStyleName(AON.AON_CSS.aonBold());
						descriptionLabel.setText(jsElaboration.getDescription());
						itemPanel.add(descriptionLabel);
					}
				}
				
				@Override
				public void onFailure(Throwable caught) {
				}
			});
		} else {
			itemBox = new ItemBox(API);
			itemPanel.add(itemBox);
		}
		

		quantity.setText(jsElaboration != null && jsElaboration.getQuantity() != null
				? (jsElaboration.getQuantity() + "") : "");
		
		API.getWarehouse().getWarehouseList(new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> result) {
				result.getData().stream()
				.forEach(w -> warehouse.addItem(w.getName(), w.getId() + ""));
				
				if (jsElaboration != null && jsElaboration.getWarehouse() != null) {
					for (Integer i = 0; i < warehouse.getItemCount(); i++) {
						if (jsElaboration.getWarehouse() != null
								& jsElaboration.getWarehouse().getName().equals(warehouse.getItemText(i))) {
							warehouse.setSelectedIndex(i);
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
				// TODO series::onChange
			}
		});
		
		date.addValueChangeHandler(new ValueChangeHandler<Date>() {

			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				// TODO date::onValueChange
			}
		});
		
		closeButton.setTitle(AON.MSG.finish());
		closeButton.setStyleName(AON.AON_CSS.aonIconPointRed());
		closeButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
		closeButton.addStyleName(AON.AON_CSS.aonMarginLeft());
		closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				parent.closeElaboration(getJsElaboration());
			}
		});
		
		reopenButton.setTitle(AON.MSG.reopen());
		reopenButton.setStyleName(AON.AON_CSS.aonIconPointGreen());
		reopenButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
		reopenButton.addStyleName(AON.AON_CSS.aonMarginLeft());
		reopenButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				parent.reopenElaboration(getJsElaboration());
			}
		});
		
		
		if(isElaborationCLosed()){
			series.setEnabled(false);
			number.setEnabled(false);
			date.setEnabled(false);
//			itemBox.setEnabled(false);
			warehouse.setEnabled(false);
			quantity.setEnabled(false);
			closeButton.setVisible(false);
			reopenButton.setVisible(true);
		} else {
			closeButton.setVisible(true);
			reopenButton.setVisible(false);
		}
		
	}
	

	public JsElaboration getJsElaboration() {
		return jsElaboration;
	}
	public void setJsElaboration(JsElaboration jsElaboration) {
		this.jsElaboration = jsElaboration;
	}
	protected boolean isElaborationCLosed(){
		return getJsElaboration().getStatus().getId()==new Integer(ElaborationStatus.CLOSED.ordinal());
	}
	
}
