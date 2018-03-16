package net.aonsolutions.aon.gwt.warehouse.client.movementList;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.api.client.warehouse.JsStockStat;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
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
	HorizontalPanel filterHorizontal;
	@UiField
	HorizontalPanel panel;
	@UiField
	PaperIconButton searchButton;
	@UiField
	PaperIconButton cleanButton;

	Main parent;
	
	FlowPanel breadcrumbs;
	
	DateBoxEx fromDate;
	DateBoxEx toDate;
	TextBox serialNumberInput;
	PaperButton categoryButton;
	PaperButton productButton;
	
	JsStockStat selectedProduct;
	JsStockStat selectedItem;
	
	public API getAPI() {
		return parent.getAPI();
	}

	public FilterPanel(Main parent) {
		this.parent = parent;
		initWidget(binder.createAndBindUi(this));
		
		breadcrumbs = new FlowPanel();
		filterHorizontal.add(breadcrumbs);
		
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
		InlineLabel serialNumberLabel = new InlineLabel("Lote/n\u00B0 Serie");
		serialNumberLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		serialNumberLabel.setWidth("20px");

		
		
		fromDate = createDateBox("from");
		toDate = createDateBox("to");
		serialNumberInput = createTextBox("serial_number");
		categoryButton = filterButton(AON.MSG.category());
		productButton = filterButton(AON.MSG.product());
		

		// ------------------ CATEGORY LIST
		categoryButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				parent.getAPI().getProduct().getProductCategories(new AsyncCallback<JSON<JsObject>>() {

					@Override
					public void onSuccess(JSON<JsObject> result) {
						ButtonClick(categoryButton, result, AON.MSG.category());
					}

					@Override
					public void onFailure(Throwable caught) {
					}
				});
			}
		});
		
		// ------------------ PRODUCT LIST
		productButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				parent.getAPI().getProduct().getProductList(new AsyncCallback<JSON<JsObject>>() {

					@Override
					public void onSuccess(JSON<JsObject> result) {
						ButtonClick(productButton, result, AON.MSG.product());
					}

					@Override
					public void onFailure(Throwable caught) {
					}
				});
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
		fpanel.add(categoryButton);
		fpanel.add(productButton);
		fpanel.add(serialNumberLabel);
		fpanel.add(serialNumberInput);
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
	
	private TextBox createTextBox(String key) {
		final TextBox input = new TextBox();
		input.setWidth("100px");
		input.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				LinkedList<String> list = new LinkedList<>();
				if(input.getValue()!=null)
					list.add(input.getValue());
				onChangeFilter(key, list);
			}
		});
		return input;
	}
	
	private void onChangeFilter(String key, LinkedList<String> value) {
    	parent.getFilterMap().put(key, value);
    	reloadBreadcrumbs();
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
		parent.getFilterMap().clear();
		fromDate.setValue(null);
		toDate.setValue(null);
		serialNumberInput.setValue(null);
		reloadContent();
	}
	
	@UiHandler("searchButton")
	void searchButton(ClickEvent event) {
		Date from = fromDate.getValue(), to = toDate.getValue();
		selectedProduct = selectedItem = null;
		parent.getFilterMap().remove("item");
		if(from==null || to==null)
			Window.alert("Se deben indicar, al menos, las fechas DESDE y HASTA");
		else if(from.after(to))
			Window.alert("La fecha DESDE no puede ser posterior a la fecha HASTA");
		else
			reloadContent();
	}
	
	void selectProduct(JsStockStat product) {
		this.selectedProduct = product;
		reloadBreadcrumbs();
	}
	void selectItem(JsStockStat item) {
		this.selectedItem = item;
		reloadBreadcrumbs();
	}
	
	
	private String key;

	private void ButtonClick(PaperButton pb, JSON<JsObject> result, String label) {
		if (AON.MSG.category().equals(label)) {
			key = "category";
		} else if (AON.MSG.product().equals(label)) {
			key = "product";
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
				reloadBreadcrumbs();
			}
		};
		sw.show();
	}
	private void reloadBreadcrumbs() {
		breadcrumbs.clear();
		if(parent.getFilterMap().containsKey("from")) {
			String text = AonDateUtils.formatDate(new Date(Long.parseLong(parent.getFilterMap().get("from").getFirst())));
			breadcrumbs.add(createFilterLabel(" Desde:"));
			breadcrumbs.add(createFilterValue(text, null));
		}
		if(parent.getFilterMap().containsKey("to")) {
			String text = AonDateUtils.formatDate(new Date(Long.parseLong(parent.getFilterMap().get("to").getFirst())));
			breadcrumbs.add(new InlineLabel(" | "));
			breadcrumbs.add(createFilterLabel("Hasta:"));
			breadcrumbs.add(createFilterValue(text, null));
		}
		if(parent.getFilterMap().containsKey("category") && parent.getFilterMap().get("category").size()>0) {
			String text = parent.getFilterMap().get("category").size()+"";
			String title = parent.getFilterMap().get("category").toString();
			breadcrumbs.add(new InlineLabel(" | "));
			breadcrumbs.add(createFilterLabel("Cat.:"));
			breadcrumbs.add(createFilterValue(text, title));
		}
		if(parent.getFilterMap().containsKey("product") && parent.getFilterMap().get("product").size()>0) {
			String text = parent.getFilterMap().get("product").size()+"";
			String title = parent.getFilterMap().get("product").toString();
			breadcrumbs.add(new InlineLabel(" | "));
			breadcrumbs.add(createFilterLabel("Prod.:"));
			breadcrumbs.add(createFilterValue(text, title));
		}
		if(parent.getFilterMap().containsKey("serial_number") && parent.getFilterMap().get("serial_number").size()>0) {
			String text = parent.getFilterMap().get("serial_number").getFirst();
			breadcrumbs.add(new InlineLabel(" | "));
			breadcrumbs.add(createFilterLabel("Lote/n\u00B0 Serie:"));
			breadcrumbs.add(createFilterValue(text, null));
		}
//		if(parent.getFilterMap().containsKey("item") && parent.getFilterMap().get("item").size()>0) {
//			String text = parent.getFilterMap().get("item").size()+"";
//			String title = parent.getFilterMap().get("item").toString();
//			breadcrumbs.add(new InlineLabel(" | "));
//			breadcrumbs.add(createFilterLabel("Det.:"));
//			breadcrumbs.add(createFilterValue(text, title));
//		}
		if(selectedItem!=null) {
			breadcrumbs.add(new InlineLabel(" | "));
			breadcrumbs.add(createFilterLabel("Seleccionado:"));
			breadcrumbs.add(createFilterValue(selectedItem.getProductName(), selectedItem.getProductName()));
		} else if(selectedProduct!=null) {
			breadcrumbs.add(new InlineLabel(" | "));
			breadcrumbs.add(createFilterLabel("Seleccionado:"));
			breadcrumbs.add(createFilterValue(selectedProduct.getProductName(), selectedProduct.getProductName()));
		}
	}
	private InlineLabel createFilterLabel(String text) {
		InlineLabel label = new InlineLabel(text);
		label.setStyleName(AON.AON_CSS.aonItalic());
		return label;
	}
	private InlineLabel createFilterValue(String text, String title) {
		InlineLabel label = new InlineLabel(text);
		label.setTitle(title);
		label.setStyleName(AON.AON_CSS.aonBold());
		return label;
	}
	
	private void reloadContent() {
		parent.loadContent();
	}
}
