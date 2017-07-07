package net.aonsolutions.aon.gwt.warehouse.client.elaboration;

import java.util.Date;
import java.util.HashMap;
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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextBox;
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
	InlineLabel categoryLabel;
	@UiField
	InlineLabel customerLabel;
	@UiField
	InlineLabel sellerLabel;
	@UiField
	InlineLabel workplaceLabel;
	@UiField
	InlineLabel periodLabel;
	@UiField
	PaperIconButton cleanFilter;

	Elaboration elaboration;
//	HashMap<String, LinkedList<String>> filterMap;
	API API;

	public FilterPanel(Elaboration elaboration, HashMap<String, LinkedList<String>> filterMap) {
		this.elaboration = elaboration;
//		this.filterMap = filterMap;
		API = elaboration.API;
		initWidget(binder.createAndBindUi(this));

		HorizontalPanel datePanel = new HorizontalPanel();
		datePanel.addStyleName(AON.AON_CSS.aonMarginTop());
		InlineLabel dateLabel = new InlineLabel(AON.MSG.date());
		dateLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		dateLabel.setWidth("20px");
		datePanel.add(dateLabel);

//		final DateBoxEx date = new DateBoxEx();
//		date.setWidth("70px");
//		date.addValueChangeHandler(new ValueChangeHandler<Date>() {
//			@Override
//			public void onValueChange(ValueChangeEvent<Date> event) {
//				LinkedList<String> list = new LinkedList<>();
//				list.add(Long.toString(date.getValue().getTime()));
//				elaboration.getFilterMap().put("date", list);
//				elaboration.loadContent();
//			}
//		});
//		datePanel.add(date);
		
		final DateBoxEx fromDate = new DateBoxEx();
		fromDate.setWidth("70px");
		fromDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(Long.toString(fromDate.getValue().getTime()));
				elaboration.getFilterMap().put("from", list);
				elaboration.loadContent();
			}
		});
		InlineLabel fromLabel = new InlineLabel(AON.MSG.from());
		fromLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		fromLabel.setWidth("20px");
		datePanel.add(fromLabel);
		datePanel.add(fromDate);
		
		final DateBoxEx toDate = new DateBoxEx();
		toDate.setWidth("70px");
		toDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(Long.toString(toDate.getValue().getTime()));
				elaboration.getFilterMap().put("to", list);
				elaboration.loadContent();
			}
		});
		InlineLabel toLabel = new InlineLabel(AON.MSG.to());
		toLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		toLabel.setWidth("20px");
		datePanel.add(toLabel);
		datePanel.add(toDate);

		panel.add(datePanel);

		// ------------------ FILTER BUTTONS
		FlowPanel fpanel = new FlowPanel();

		// ------------------ SERIES
//		PaperButton seriesButton = filterButton(AON.MSG.series());
//		seriesButton.addClickHandler(new ClickHandler() {
//
//			@Override
//			public void onClick(ClickEvent event) {
//				API.getWarehouse().getCarrierPackingSeries(new AsyncCallback<JSON<JsObject>>() {
//
//					@Override
//					public void onSuccess(JSON<JsObject> result) {
//						ButtonClick(seriesButton, result, AON.MSG.series());
//					}
//
//					@Override
//					public void onFailure(Throwable caught) {
//					}
//				});
//			}
//		});
//		fpanel.add(seriesButton);
		
		final TextBox seriesInput = new TextBox();
		seriesInput.setWidth("70px");
		seriesInput.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(seriesInput.getValue());
				elaboration.getFilterMap().put("series", list);
				elaboration.loadContent();
			}
		});
		InlineLabel seriesLabel = new InlineLabel(AON.MSG.series());
		seriesLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		seriesLabel.setWidth("20px");
		fpanel.add(seriesLabel);
		fpanel.add(seriesInput);
		
		final IntegerBox numberInput = new IntegerBox();
		numberInput.setWidth("70px");
		numberInput.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(Long.toString(numberInput.getValue()));
				elaboration.getFilterMap().put("number", list);
				elaboration.loadContent();
			}
		});
		InlineLabel numberLabel = new InlineLabel(AON.MSG.number());
		numberLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		numberLabel.setWidth("20px");
		fpanel.add(numberLabel);
		fpanel.add(numberInput);

		// ------------------ STATUS
		PaperButton statusButton = filterButton(AON.MSG.status());
		statusButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				API.getWarehouse().getElaborationStatuses(new AsyncCallback<JSON<JsObject>>() {

					@Override
					public void onSuccess(JSON<JsObject> result) {
						ButtonClick(statusButton, result, AON.MSG.status());
					}

					@Override
					public void onFailure(Throwable caught) {
					}
				});
			}
		});
		fpanel.add(statusButton);

		panel.add(fpanel);
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

	@UiHandler("cleanFilter")
	void cleanFilter(ClickEvent event) {
		categoryLabel.setText("");
		customerLabel.setText("");
		sellerLabel.setText("");
		workplaceLabel.setText("");
		periodLabel.setText("");
		
		
		HashMap<String, LinkedList<String>> map = new HashMap<>();
		LinkedList<String> to = new LinkedList<>();
		to.add(Long.toString(new Date().getTime()));
		map.put("to", to);
		elaboration.setFilterMap(map);
		onFilterChanged();
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
		LinkedList<String> filterList = elaboration.getFilterMap().containsKey(key)
				? elaboration.getFilterMap().get(key) : new LinkedList<>();
		AonFilterDialog sw = new AonFilterDialog(pb, label, "", filterList, result.getData().cast()) {

			@Override
			protected void onSelect(JavaScriptObject o, Boolean apply) {
				JsObject js = o.cast();
				if (apply) {
					if (elaboration.getFilterMap().containsKey(key)) {
						elaboration.getFilterMap().get(key).add(js.getId() + "");
					} else {
						LinkedList<String> list = new LinkedList<>();
						list.add(js.getId() + "");
						elaboration.getFilterMap().put(key, list);
					}
				} else {
					if (elaboration.getFilterMap().containsKey(key)) {
						elaboration.getFilterMap().get(key).remove(js.getId() + "");
					}
				}
//				elaboration.loadContent();
				onFilterChanged();
			}
		};
		sw.show();
	}
	
	private void onFilterChanged() {
		elaboration.loadContent();
	}
}
