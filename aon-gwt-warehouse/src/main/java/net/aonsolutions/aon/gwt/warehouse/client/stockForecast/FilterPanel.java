package net.aonsolutions.aon.gwt.warehouse.client.stockForecast;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesCSS;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesResources;
import com.esferalia.aon.gwt.common.client.polymer.AonFilterDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperButton;
import com.vaadin.polymer.paper.widget.PaperIconButton;

public class FilterPanel extends Composite {

	interface Binder extends UiBinder<HTMLPanel, FilterPanel> {

	}

	private static Binder binder = GWT.create(Binder.class);

	public static final AonGwtIssuesCSS ICSS = GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css();

	@UiField
	HorizontalPanel panel;
	@UiField
	PaperIconButton searchButton;
	@UiField
	PaperIconButton cleanButton;

	Main parent;
	
	DateBoxEx fromDate;
	DateBoxEx toDate;
	IntegerBox accumulationDaysInput;
	PaperButton categoryButton;
	PaperButton productButton;
	PaperButton customerButton;
	
	public API getAPI() {
		return parent.getAPI();
	}

	public FilterPanel(Main parent) {
		this.parent = parent;
		initWidget(binder.createAndBindUi(this));
		
		searchButton.setTitle(AON.MSG.searchAction());
		cleanButton.setTitle(AON.MSG.clean());

		// ------------------ DATE PANEL
		InlineLabel dateLabel = new InlineLabel(AON.MSG.date());
		dateLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		dateLabel.setWidth("20px");
		
		// ------------------ FROM DATE
		InlineLabel startLabel = new InlineLabel(AON.MSG.from());
		startLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		startLabel.setWidth("20px");
		
		// ------------------ TO DATE
		InlineLabel endLabel = new InlineLabel(AON.MSG.until());
		endLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		endLabel.setWidth("20px");
		
		// ------------------ STOCK DAYS
		InlineLabel stockDaysLabel = new InlineLabel("Dias acopio");
		stockDaysLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		stockDaysLabel.setWidth("20px");

		
		
		fromDate = createDateBox("from");
		toDate = createDateBox("to");
		accumulationDaysInput = createIntegerBox("accumulation_days");
		categoryButton = filterButton(AON.MSG.category());
		productButton = filterButton(AON.MSG.product());
		customerButton = filterButton(AON.MSG.customer());
		
		// ------------------ DISABLED FIELDS
		categoryButton.setDisabled(true);
		productButton.setDisabled(true);
		customerButton.setDisabled(true);
		

		// ------------------ CATEGORY LIST
		categoryButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO categoryButton.ClickHandler:onClick
//				parent.getAPI().getWarehouse().getElaborationStatuses(new AsyncCallback<JSON<JsObject>>() {
//
//					@Override
//					public void onSuccess(JSON<JsObject> result) {
//						ButtonClick(statusButton, result, AON.MSG.status());
//					}
//
//					@Override
//					public void onFailure(Throwable caught) {
//					}
//				});
			}
		});
		
		// ------------------ PRODUCT LIST
		productButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO productButton.ClickHandler:onClick
			}
		});
		
		// ------------------ CUSTOMER LIST
		customerButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO customerButton.ClickHandler:onClick
			}
		});
		
		HorizontalPanel datePanel = new HorizontalPanel();
		datePanel.addStyleName(AON.AON_CSS.aonMarginTop());
		datePanel.add(dateLabel);
		datePanel.add(startLabel);
		datePanel.add(fromDate);
		datePanel.add(endLabel);
		datePanel.add(toDate);
		panel.add(datePanel);
		
		FlowPanel fpanel = new FlowPanel();
		fpanel.add(stockDaysLabel);
		fpanel.add(accumulationDaysInput);
		fpanel.add(categoryButton);
		fpanel.add(productButton);
		fpanel.add(customerButton);
		panel.add(fpanel);
	}

	private DateBoxEx createDateBox(String key) {
		final DateBoxEx dateBox = new DateBoxEx();
		dateBox.getElement().getStyle().setBorderColor("#dedede");
		dateBox.getElement().getStyle().setHeight(16, Unit.PX);;
		dateBox.setWidth("70px");
		dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				LinkedList<String> list = new LinkedList<>();
				if(dateBox.getValue()!=null)
					list.add(Long.toString(dateBox.getValue().getTime()));
				onChangeFilter(key, list);
			}
		});
		return dateBox;
	}
	
	private IntegerBox createIntegerBox(String key) {
		final IntegerBox input = new IntegerBox();
		input.setWidth("20px");
		input.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				LinkedList<String> list = new LinkedList<>();
				if(input.getValue()!=null)
					list.add(Long.toString(input.getValue()));
				onChangeFilter(key, list);
			}
		});
		return input;
	}
	
	private void onChangeFilter(String key, LinkedList<String> value) {
    	parent.getFilterMap().put(key, value);
//		parent.loadContent();
	}

	private PaperButton filterButton(String title) {
		PaperButton button = new PaperButton();
		button.setNoink(true);
		button.setStyleName(ICSS.aonPaperButtonFilterIssues());

		InlineLabel label = new InlineLabel(title);
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		button.add(label);

		IronIcon icon = new IronIcon();
		icon.setStyleName(ICSS.aonIronIconFilterIssues());
		icon.setIcon("arrow-drop-down");
		button.add(icon);
		return button;
	}

	@UiHandler("cleanButton")
	void cleanButton(ClickEvent event) {
//		HashMap<String, LinkedList<String>> map = new HashMap<>();
		LinkedList<String> to = new LinkedList<>();
		to.add(Long.toString(new Date().getTime()));
//		map.put("to", to);
//		parent.setFilterMap(map);
		parent.getFilterMap().clear();
		parent.getFilterMap().put("to", to);
		reloadContent();
	}
	
	@UiHandler("searchButton")
	void searchButton(ClickEvent event) {
		if(fromDate.getValue()==null || toDate.getValue()==null) {
			Window.alert("Se deben indicar, al menos, las fechas DESDE y HASTA");
		} else
			reloadContent();
	}

	private String key;

	private void ButtonClick(PaperButton pb, JSON<JsObject> result, String label) {
		if (AON.MSG.series().equals(label)) {
			key = "series";
		} else if (AON.MSG.carrier().equals(label)) {
			key = "carrier";
		} else if (AON.MSG.type().equals(label)) {
			key = "type";
		} else if (AON.MSG.status().equals(label)) {
			key = "status";
		}
		LinkedList<String> filterList = parent.getFilterMap().containsKey(key)
				? parent.getFilterMap().get(key) : new LinkedList<>();
		AonFilterDialog sw = new AonFilterDialog(pb, label, "", filterList, result.getData().cast()) {

			@Override
			protected void onSelect(JavaScriptObject o, Boolean apply) {
				JsObject js = o.cast();
				if (apply) {
					if (parent.getFilterMap().containsKey(key)) {
						parent.getFilterMap().get(key).add(js.getId() + "");
					} else {
						LinkedList<String> list = new LinkedList<>();
						list.add(js.getId() + "");
						parent.getFilterMap().put(key, list);
					}
				} else {
					if (parent.getFilterMap().containsKey(key)) {
						parent.getFilterMap().get(key).remove(js.getId() + "");
					}
				}
				reloadContent();
			}
		};
		sw.show();
	}
	
	private void reloadContent() {
		parent.loadContent();
	}
}
