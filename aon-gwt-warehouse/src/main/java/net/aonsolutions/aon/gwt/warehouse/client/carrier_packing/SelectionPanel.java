package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.api.client.product.JsItem;
import com.esferalia.aon.gwt.api.client.registry.JsRmedia;
import com.esferalia.aon.gwt.api.client.warehouse.JsCarrierPacking;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrder;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrderDetail;
import com.esferalia.aon.gwt.api.client.warehouse.JsWarehouse;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperIconButton;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperItem;
import com.vaadin.polymer.paper.widget.PaperToggleButton;
import com.vaadin.polymer.paper.widget.event.ChangeEvent;
import com.vaadin.polymer.paper.widget.event.ChangeEventHandler;

import net.aonsolutions.aon.gwt.warehouse.client.Utils;
import net.aonsolutions.polymer.aon.widget.AonComboBox;
import net.aonsolutions.polymer.aon.widget.event.ValueChangedEvent;
import net.aonsolutions.polymer.aon.widget.event.ValueChangedEventHandler;


public class SelectionPanel extends ResizeComposite implements RequiresResize {
	
	static final String GRAY_COLOR = "#DDDDDD";
	static final String GRAY_SUAVE_COLOR = "#F5F5F5";

	private HashMap<String, LinkedList<String>> selectedFilter;
	private Boolean selectedMore = true;
	private VerticalPanel selectedVerticalPanel;
	private HashMap<String, LinkedList<String>> selectableFilter;
	private Boolean selectableMore = true;
	private VerticalPanel selectableVerticalPanel;
	private Integer selectableOrder;	
	
	private VerticalPanel receptionVerticalPanel;
	private Boolean receptionMore = true;
	private HashMap<String, LinkedList<String>> receptionFilter;
	private Integer receptionOrder;	




	private CarrierPackingDetail parent;
	
	private API getAPI(){
		return parent.getAPI();
	}

	private JsCarrierPacking getCarrierPacking(){
		return parent.getJsCarrierPacking();
	}
	
	private void setCarrierPacking(JsCarrierPacking js){
		parent.setJsCarrierPacking(js);
	}
	
	@Override
	public void onResize() {
		super.onResize();
		ScrollPanel selectedScrollPanel = (ScrollPanel) selectedVerticalPanel.getWidget(1);
		selectedScrollPanel.getElement().getStyle().setHeight(getOffsetHeight() > 25 ? getOffsetHeight() - 25 : 0.0, Unit.PX);
		if(isPending()){
			ScrollPanel selectableScrollPanel = (ScrollPanel) selectableVerticalPanel.getWidget(1);
			selectableScrollPanel.getElement().getStyle().setHeight(getOffsetHeight() > 65 ? getOffsetHeight() - 65 : 0.0, Unit.PX);
		} else if(isShipment()){
			ScrollPanel receptionScrollPanel = (ScrollPanel) receptionVerticalPanel.getWidget(1);
			receptionScrollPanel.getElement().getStyle().setHeight(getOffsetHeight() > 25 ? getOffsetHeight() - 25 : 0.0, Unit.PX);
		}
	}
	
	public void resize() {
		onResize();
	}
	
	public SelectionPanel(CarrierPackingDetail parent) {	
		this.parent = parent;
		initSelectedFilter();
		initSelectableFilter();
		initReceptionFilter();
		SplitLayoutPanel rootPanel = new SplitLayoutPanel(4);
		
		 Window.addResizeHandler(new ResizeHandler() {
				
			@Override
			public void onResize(ResizeEvent event) {
				resize();
			}
		});

		//  -------------------------- SELECTABLE PANEL ------------------------------
		if(isPending()){
			SimpleLayoutPanel selectable = new SimpleLayoutPanel();
			selectable.setStyleName(AON.AON_CSS.aonFlexContainer());
			selectable.addStyleName(AON.AON_CSS.aonPadding2Top());
			selectable.setStyleName(AON.AON_CSS.aonInvoicePanel());
			selectable.getElement().getStyle().setBackgroundColor(GRAY_COLOR);
			selectable.getElement().getStyle().setMarginLeft(0, Unit.PX);
			selectablePanel();
			selectable.setWidget(selectableVerticalPanel);
			rootPanel.addEast(selectable, Window.getClientWidth()/2);
		} else if(isShipment()){
			SimpleLayoutPanel reception = new SimpleLayoutPanel();
			reception.setStyleName(AON.AON_CSS.aonFlexContainer());
			reception.addStyleName(AON.AON_CSS.aonPadding2Top());
			reception.setStyleName(AON.AON_CSS.aonInvoicePanel());
			reception.getElement().getStyle().setBackgroundColor(GRAY_SUAVE_COLOR);
			reception.getElement().getStyle().setMarginLeft(0, Unit.PX);
			receptionPanel();
			reception.setWidget(receptionVerticalPanel);
			rootPanel.addEast(reception, Window.getClientWidth()/2);
		}
		//  -------------------------- SELECTED PANEL ------------------------------

		SimpleLayoutPanel selected = new SimpleLayoutPanel();
		selected.setStyleName(AON.AON_CSS.aonFlexContainer());
		selected.addStyleName(AON.AON_CSS.aonPadding2Top());
		selected.setStyleName(AON.AON_CSS.aonInvoicePanel());
		selected.getElement().getStyle().setBackgroundColor(isPending() ? GRAY_SUAVE_COLOR : GRAY_COLOR);
		selected.getElement().getStyle().setMarginLeft(0, Unit.PX);
		selectedPanel();
		selected.setWidget(selectedVerticalPanel);
		rootPanel.add(selected);
		
		initWidget(rootPanel);
		onResize();
	}
	
	// -------------------- RECEPTION PANEL
	private void receptionPanel(){
		receptionVerticalPanel = new VerticalPanel();
		receptionVerticalPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		Label label = new Label("Recepcion");
		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		label.getElement().getStyle().setPadding(3, Unit.PX);
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(label);
		if(isShipment() && isOnRoute()){
			PaperIconButton truck = new PaperIconButton();
			truck.setIcon("maps:local-shipping");
			truck.getElement().getStyle().setMargin(0, Unit.PX);
			truck.getElement().getStyle().setPadding(0, Unit.PX);
			truck.getElement().getStyle().setHeight(20, Unit.PX);
			truck.setNoink(true);
			if(getCarrierPacking().getReceptionStartDate() != null){
				truck.getElement().getStyle().setColor(
						getCarrierPacking().getReceptionEndDate() != null
						? "red" : "green");
			}
			truck.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if(getCarrierPacking().getReceptionEndDate() == null){
						truckHandler(truck);
					}
				}
			});
			hp.add(truck);
		}
		receptionVerticalPanel.add(hp);
		receptionVerticalPanel.add(receptionItems());
	}
	
	private void initReceptionFilter(){
		receptionFilter = new HashMap<>();
		
		LinkedList<String> list = new LinkedList<>();
		list.add(getCarrierPacking().getId() + "");
		receptionFilter.put("carrier_packing", list);
		
		// -------------------- per page
		list = new LinkedList<>();
		list.add("30");
		receptionFilter.put("per_page", list);
		
		// -------------------- page
		list = new LinkedList<>();
		list.add("1");
		receptionFilter.put("page", list);
	}
	
	private ScrollPanel receptionItems() {
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.addScrollHandler(new ScrollHandler() {
			
			@Override
			public void onScroll(ScrollEvent event) {
				Integer scrollTop = scrollPanel.getElement().getScrollTop();
				Integer offsetHeight = scrollPanel.getElement().getOffsetHeight();
				Integer physicalSize = scrollPanel.getElement().getScrollHeight();
				Integer maxScrollPosition = physicalSize - offsetHeight;
				if(scrollTop > maxScrollPosition && receptionMore){
					Integer page = Integer.parseInt(receptionFilter.get("page").get(0));
					LinkedList<String> list = new LinkedList<>();
					list.add(Integer.toString(page +1));
					receptionFilter.put("page", list);	
					getAPI().getWarehouse().getOrders(getOrderType(), receptionFilter, new AsyncCallback<JSON<JsOrder>>() {
						
						@Override
						public void onSuccess(JSON<JsOrder> result) {
							receptionMore = Integer.parseInt(receptionFilter.get("perPage").get(0)) > result.getData().length();
							addReception(result);
						}
						
						@Override public void onFailure(Throwable caught) {}
					});
				}
			}
		});
		
		HashMap<String, LinkedList<String>> map = new HashMap<>();
		LinkedList<String> l = new LinkedList<>();
		l.add(getCarrierPacking().getId() + "");
		map.put("carrier_packing", l);
		
		scrollPanel.add(new VerticalPanel());
		getAPI().getWarehouse().getOrders("income", receptionFilter, new AsyncCallback<JSON<JsOrder>>() {
			
			@Override
			public void onSuccess(JSON<JsOrder> result) {
				addReception(scrollPanel, result);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		return scrollPanel;
	}
	
	private void addReception(JSON<JsOrder> orders){
		ScrollPanel scroll = (ScrollPanel) selectedVerticalPanel.getWidget(1);
		addReception(scroll, orders);
	}
	
	private void addReception(ScrollPanel scroll, JSON<JsOrder> orders){
		VerticalPanel vp = (VerticalPanel) scroll.getWidget();
		orders.getData().stream().forEach(order -> buildOrderItem(vp, order, "reception"));
	}

	// -------------------- SELECTED PANEL

	private void initSelectedFilter(){
		selectedFilter = new HashMap<>();
		
		LinkedList<String> list = new LinkedList<>();
		list.add(getCarrierPacking().getId() + "");
		selectedFilter.put("carrier_packing", list);
		
		// -------------------- per page
		list = new LinkedList<>();
		list.add("30");
		selectedFilter.put("per_page", list);
		
		// -------------------- page
		list = new LinkedList<>();
		list.add("1");
		selectedFilter.put("page", list);
	}
	
	private void selectedPanel(){
		selectedVerticalPanel = new VerticalPanel();
		selectedVerticalPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		Label label = new Label(isShipment() ? "Solicitud de Carga" : "Hoja de Ruta");
		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		label.getElement().getStyle().setPadding(3, Unit.PX);
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(label);
	
		selectedVerticalPanel.add(hp);
		selectedVerticalPanel.add(selectedItems());
	}
	
	private ScrollPanel selectedItems() {
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.addScrollHandler(new ScrollHandler() {
			
			@Override
			public void onScroll(ScrollEvent event) {
				Integer scrollTop = scrollPanel.getElement().getScrollTop();
				Integer offsetHeight = scrollPanel.getElement().getOffsetHeight();
				Integer physicalSize = scrollPanel.getElement().getScrollHeight();
				Integer maxScrollPosition = physicalSize - offsetHeight;
				if(scrollTop > maxScrollPosition && selectedMore){
					Integer page = Integer.parseInt(selectedFilter.get("page").get(0));
					LinkedList<String> list = new LinkedList<>();
					list.add(Integer.toString(page +1));
					selectedFilter.put("page", list);	
					getAPI().getWarehouse().getOrders(getOrderType(), selectedFilter, new AsyncCallback<JSON<JsOrder>>() {
						
						@Override
						public void onSuccess(JSON<JsOrder> result) {
							selectedMore = Integer.parseInt(selectedFilter.get("perPage").get(0)) > result.getData().length();
							addSelected(result);
						}
						
						@Override public void onFailure(Throwable caught) {}
					});
				}
			}
		});
		scrollPanel.add(new VerticalPanel());
		getAPI().getWarehouse().getOrders(getOrderType(), selectedFilter, new AsyncCallback<JSON<JsOrder>>() {
			
			@Override
			public void onSuccess(JSON<JsOrder> result) {
				addSelected(scrollPanel, result);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		
		return scrollPanel;
	}
	
	private void addSelected(JSON<JsOrder> orders){
		ScrollPanel scroll = (ScrollPanel) selectedVerticalPanel.getWidget(1);
		addSelected(scroll, orders);
	}
	
	private void addSelected(ScrollPanel scroll, JSON<JsOrder> orders){
		VerticalPanel vp = (VerticalPanel) scroll.getWidget();
		orders.getData().stream().forEach(order -> buildOrderItem(vp, order, "selected"));
	}

	// -------------------- SELECTABLE PANEL
	
	private void initSelectableFilter(){
		selectableFilter = new HashMap<>();
		
		LinkedList<String> list = new LinkedList<>();
		list.add(getCarrierPacking().getCarrier().getId() + "");
		selectableFilter.put("carrier", list);
		
		list = new LinkedList<>();
		list.add(Long.toString(new Date().getTime()));
		selectableFilter.put("issue_date", list);	
		
		list = new LinkedList<>();
		list.add("");
		selectableFilter.put("not_carrier_packing", list);
		
		// -------------------- per page
		list = new LinkedList<>();
		list.add("30");
		selectableFilter.put("per_page", list);
		
		// -------------------- page
		list = new LinkedList<>();
		list.add("1");
		selectableFilter.put("page", list);
	}
	
	private void selectablePanel() {
		selectableVerticalPanel = new VerticalPanel();
		selectableVerticalPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		selectableVerticalPanel.add(selectableFilterPanel());
		selectableVerticalPanel.add(selectableItems());
	}

	private FlexTable selectableFilterPanel() {
		FlexTable table = new FlexTable();
		table.getElement().getStyle().setPaddingTop(5, Unit.PX);

		HorizontalPanel p = new HorizontalPanel();
		p.getElement().getStyle().setPaddingTop(5, Unit.PX);
		Label label = new Label("Fecha ");
		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		label.getElement().getStyle().setPadding(3, Unit.PX);
		p.add(label);
		table.setWidget(0, 0, label);
		DateBoxEx datebox = new DateBoxEx();
		datebox.setStyleName(AON.AON_CSS.aonTextBox());
		datebox.setValue(new Date());
		datebox.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(Long.toString(datebox.getValue().getTime()));
				selectableFilter.put("issue_date", list);
				selectableVerticalPanel.remove(1);
				selectableVerticalPanel.add(selectableItems());
				onResize();
			}
		});
		p.add(datebox);
		table.setWidget(0, 1, datebox);		
		Label registryLabel = new Label(isShipment() ? "Proveedor " : "Cliente ");
		registryLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		registryLabel.getElement().getStyle().setPadding(3, Unit.PX);
		p.add(registryLabel);
		table.setWidget(0, 2, registryLabel);		

		TextBox tb = new TextBox();
		tb.setStyleName(AON.AON_CSS.aonTextBox());
		tb.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(KeyCodes.KEY_TAB != event.getNativeEvent().getKeyCode()){
					if(selectableFilter.containsKey("registry") && "".equals(tb.getValue())){
						selectableFilter.remove("registry");
					} else {
						LinkedList<String> list = new LinkedList<>();
						list.add(tb.getValue());
						selectableFilter.put("registry", list);
					}
					selectableVerticalPanel.remove(1);
					selectableVerticalPanel.add(selectableItems());
					onResize();
				}
			}
		}); 
		p.add(tb);
		table.setWidget(0, 3, tb);		

		HorizontalPanel p2 = new HorizontalPanel();
		Label seriesLabel = new Label("Serie ");
		seriesLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		seriesLabel.getElement().getStyle().setPadding(3, Unit.PX);
		p2.add(seriesLabel);
		table.setWidget(1, 0, seriesLabel);		
		TextBox serie = new TextBox();
		serie.setStyleName(AON.AON_CSS.aonTextBox());
		serie.getElement().getStyle().setWidth(50, Unit.PX);
		serie.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(KeyCodes.KEY_TAB != event.getNativeEvent().getKeyCode()){
					if(selectableFilter.containsKey("series") && "".equals(serie.getValue())){
						selectableFilter.remove("series");
					} else {
						LinkedList<String> list = new LinkedList<>();
						list.add(serie.getValue());
						selectableFilter.put("series", list);
					}
					selectableVerticalPanel.remove(1);
					selectableVerticalPanel.add(selectableItems());
					onResize();
				}
			}
		}); 
		p2.add(serie);
		table.setWidget(1, 1, serie);
		
		Label numberLabel = new Label("N\u00famero ");
		numberLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		numberLabel.getElement().getStyle().setPadding(3, Unit.PX);
		p2.add(numberLabel);
		table.setWidget(1, 2, numberLabel);
		
		TextBox number = new TextBox();
		number.setStyleName(AON.AON_CSS.aonTextBox());
		number.getElement().getStyle().setWidth(50, Unit.PX);
		number.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(KeyCodes.KEY_TAB != event.getNativeEvent().getKeyCode()){
					if(selectableFilter.containsKey("number") && "".equals(number.getValue())){
						selectableFilter.remove("number");
					} else {
						LinkedList<String> list = new LinkedList<>();
						list.add(number.getValue());
						selectableFilter.put("number", list);
					}
					selectableVerticalPanel.remove(1);
					selectableVerticalPanel.add(selectableItems());
					onResize();
				}
			}
		}); 
		p2.add(number);
		table.setWidget(1, 3, number);
		return table;
	}
	
	private ScrollPanel selectableItems() {
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.addScrollHandler(new ScrollHandler() {
			
			@Override
			public void onScroll(ScrollEvent event) {
				Integer scrollTop = scrollPanel.getElement().getScrollTop();
				Integer offsetHeight = scrollPanel.getElement().getOffsetHeight();
				Integer physicalSize = scrollPanel.getElement().getScrollHeight();
				Integer maxScrollPosition = physicalSize - offsetHeight;
				if(scrollTop > maxScrollPosition && selectableMore){
					Integer page = Integer.parseInt(selectableFilter.get("page").get(0));
					LinkedList<String> list = new LinkedList<>();
					list.add(Integer.toString(page +1));
					selectableFilter.put("page", list);	
					getAPI().getWarehouse().getOrders(getOrderType(), selectableFilter, new AsyncCallback<JSON<JsOrder>>() {
						
						@Override
						public void onSuccess(JSON<JsOrder> result) {
							selectableMore = Integer.parseInt(selectableFilter.get("perPage").get(0)) > result.getData().length();
							addSelectable(result);
						}
						
						@Override public void onFailure(Throwable caught) {}
					});
				}
			}
		});
		scrollPanel.add(new VerticalPanel());
		getAPI().getWarehouse().getOrders(getOrderType(), selectableFilter, new AsyncCallback<JSON<JsOrder>>() {
			
			@Override
			public void onSuccess(JSON<JsOrder> result) {
				addSelectable(scrollPanel, result);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		
		return scrollPanel;
	}
	
	private void addSelectable(JSON<JsOrder> orders){
		ScrollPanel scroll = (ScrollPanel) selectableVerticalPanel.getWidget(1);
		addSelectable(scroll, orders);
	}
	
	private void addSelectable(ScrollPanel scroll, JSON<JsOrder> orders){
		VerticalPanel vp = (VerticalPanel) scroll.getWidget();
		orders.getData().stream().forEach(order -> buildOrderItem(vp, order, "selectable"));
	}
	
	private void buildOrderItem(VerticalPanel vp, JsOrder order, String panel){
		PaperItem pi = new PaperItem();
		HorizontalPanel hp = new HorizontalPanel();

		if(isPending()){
			if(isSelected(panel)){
				PaperIconButton sendMail = new PaperIconButton();
				sendMail.setIcon("mail");
				sendMail.setStyle("height:24px;font-size:12px;padding:0px;font-weight: bold;");
		    	sendMail.setNoink(true);
		    
		    	sendMail.addClickHandler(new ClickHandler() {
				
					@Override
					public void onClick(ClickEvent event) {
						send(order);
					}
				});
		    	hp.add(sendMail);
			}

			PaperIconButton pib = new PaperIconButton();
			pib.setIcon("swap-horiz");
	    	pib.setStyle("height:24px;font-size:12px;padding:0px;font-weight: bold;");
	    	pib.setNoink(true);
	    
			pib.addClickHandler(new ClickHandler() {
			
				@Override
				public void onClick(ClickEvent event) {
					if(isPending()){
						if(isSelected(panel)){
							removeAllOrder(order); 
						} else {
							addAllOrder(order);
						}
					} 
				}
			});

			hp.add(pib);
		}
		IronIcon ironIcon = new IronIcon();
	   	ironIcon.setIcon("arrow-drop-up");
	    pi.add(ironIcon);
	    String title = (order.getIssueDate() != null 
	    		? Utils.formatDate(Utils.parseDateTime(order.getIssueDate())) + " - "
	    		: "") + (isReception(panel) ? order.getReferenceCode() : order.getSeriesNumber()) + " - " + order.getRegistry().getName(); // + WORKPLACE!
	   
	    Label ot = new Label(title.length() > 70 ? title.substring(0,70) + "..." : title);
	    ot.setTitle(title);
	    pi.add(ot);
	    

	    pi.setStyle("min-height:24px;font-size:12px;padding:0px;font-weight: bold;");
	    
	    SimplePanel sp = new SimplePanel();
		sp.setVisible(false);

	    if(isSelected(panel) || (isSelectable(panel) && selectableOrder != null && selectableOrder == order.getId())
	    		|| (isReception(panel) && receptionOrder != null && receptionOrder == order.getId())){
	    	ironIcon.setIcon("arrow-drop-down");
	    	loadDetails(sp, panel, order);
	    }

		pi.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
		    	ironIcon.setIcon(sp.isVisible() ? "arrow-drop-up" : "arrow-drop-down" );
				loadDetails(sp, panel, order);
			}
		});

	    hp.add(pi);
	    vp.add(hp);
		vp.add(sp);
	}
	
	private void loadDetails(SimplePanel sp, String panel, JsOrder order){
		if(!sp.isVisible()){
			if(isReception(panel)){
				getAPI().getWarehouse().getDetails(order.getId(), "income", new AsyncCallback<JSON<JsOrderDetail>>() {
					
					@Override
					public void onSuccess(JSON<JsOrderDetail> result) {
						sp.setWidget(buildDetail(order, result, panel));
						sp.setVisible(!sp.isVisible());
					}
				
					@Override public void onFailure(Throwable caught) {}
				});
			} else {
				HashMap<String, LinkedList<String>> map = new HashMap<>();
				LinkedList<String> list = new LinkedList<>();
				list.add(isSelected(panel) ? (getCarrierPacking().getId() + "") : "null");
				map.put("carrier_packing", list);
				list = new LinkedList<>();
				list.add("" + order.getId());
				map.put("parent_id", list);
				getAPI().getWarehouse().getDetails(getOrderType(), map, new AsyncCallback<JSON<JsOrderDetail>>() {
				
					@Override
					public void onSuccess(JSON<JsOrderDetail> result) {
						sp.setWidget(buildDetail(order, result, panel));
						sp.setVisible(!sp.isVisible());
					}
				
					@Override public void onFailure(Throwable caught) {}
				});
			}
		} else sp.setVisible(!sp.isVisible());
	}
	
	private VerticalPanel buildDetail(JsOrder order, JSON<JsOrderDetail> details, String panl) {
		VerticalPanel vp = new VerticalPanel();
		if(!isShipment() && isSelected(panl)){
			vp.add(buildBultosPeso(order));
		}
		details.getData().stream().forEach(detail ->{
			HorizontalPanel panel = new HorizontalPanel();
			if(isSelected(panl) & isPending()){
				panel.getElement().getStyle().setPaddingLeft(80, Unit.PX);
			} else 	panel.getElement().getStyle().setPaddingLeft(40, Unit.PX);
			Label label = new Label(detail.getDescription());
		    label.getElement().getStyle().setPaddingTop(2, Unit.PX);
		    
		    DoubleBox db = new DoubleBox();
		    db.getElement().getStyle().setMarginRight(10, Unit.PX);
		    db.getElement().getStyle().setHeight(13, Unit.PX);
		    db.getElement().getStyle().setWidth(40, Unit.PX);
		    db.getElement().getStyle().setPadding(0, Unit.PX);
		    db.setEnabled(isShipment() && isSelectable(panl));
		    if(isSelected(panl) && isShipment()){
		    	db.setValue(detail.getQuantity() - detail.getDelivered());
		    }else db.setValue(detail.getQuantity());
		    if(isShipment() && !isFinished() &&
		    	((isSelected(panl) && (detail.getQuantity()- detail.getDelivered()) > 0)
	    		|| isReception(panl) || isSelectable(panl))){
				PaperIconButton pib = new PaperIconButton();
				pib.setIcon("swap-horiz");
				pib.setStyle("height:16px;font-size:12px;padding:0px;font-weight: bold;");
		    	pib.setNoink(false);
		    	panel.add(pib);
		    	pib.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						if(isPending()){
							if(detail.getCarrierPacking() != null){
								String text = "Est\u00e1s seguro Eliminar "+ detail.getDescription() + " del packing list " + getCarrierPacking().getSeriesNumber();
						      	AonDialog dialog = new AonDialog("Eliminar", new Label(text)) {
									
									@Override protected void onCancel() {hide();}
									
									@Override 
									protected void onAccept() {
										addOneDetail(detail, db.getValue());
										hide();
									}
						      	};
						      	dialog.setAutoHideEnabled(true);
								dialog.getElement().getStyle().setWidth(310, Unit.PX);
								dialog.center();
							} else {
								addOneDetail(detail, db.getValue());
							}
						} else {
							if(isReception(panl)){
								removeIncomeDetail(order, detail, detail.getQuantity(), false);
							} else {
								addToIncome(order, detail);
							}
						}
					}
				});
			
		    }
		    panel.add(db);
		    panel.add(new Label(detail.getDescription()));
		    vp.add(panel);
		});
		return vp;
	}
	
	private HorizontalPanel buildBultosPeso(JsOrder order){
		HorizontalPanel hp = new HorizontalPanel();
		if(isPending()){
			hp.getElement().getStyle().setPaddingLeft(80, Unit.PX);
		} else hp.getElement().getStyle().setPaddingLeft(40, Unit.PX);
		Label bultoslabel = new Label("Bultos");
		bultoslabel.getElement().getStyle().setPaddingTop(2, Unit.PX);
		bultoslabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		DoubleBox bultos = new DoubleBox();
		bultos.getElement().getStyle().setMarginRight(10, Unit.PX);
		bultos.getElement().getStyle().setHeight(13, Unit.PX);
		bultos.getElement().getStyle().setWidth(40, Unit.PX);
		bultos.getElement().getStyle().setPadding(0, Unit.PX);
		bultos.setValue(order.getTotalPackages());
		bultos.setEnabled(isPending());
		bultos.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				String requestData = "{\"total_packages\":\""+ bultos.getValue() +"\"}";
				getAPI().getWarehouse().updateDelivery(order.getId(), requestData);
			}
		});
		
		Label pesolabel = new Label("Peso");
		pesolabel.getElement().getStyle().setPaddingTop(2, Unit.PX);
		pesolabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		DoubleBox peso = new DoubleBox();
		peso.getElement().getStyle().setMarginRight(10, Unit.PX);
		peso.getElement().getStyle().setHeight(13, Unit.PX);
		peso.getElement().getStyle().setWidth(50, Unit.PX);
		peso.getElement().getStyle().setPadding(0, Unit.PX);
		peso.setValue(order.getTotalWeight());
		peso.setEnabled(isPending());
		peso.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				String requestData = "{\"total_weight\":\""+ peso.getValue() +"\"}";
				getAPI().getWarehouse().updateDelivery(order.getId(), requestData);
			}
		});
		
		hp.add(bultoslabel);
		hp.add(bultos);
		hp.add(pesolabel);
		hp.add(peso);
		return hp;
	}
	
	private void removeAllOrder(JsOrder order){
		String text = "Est\u00e1s seguro Eliminar "+ order.getSeriesNumber() + " del packing list " + getCarrierPacking().getSeriesNumber();
      	AonDialog dialog = new AonDialog("Eliminar", new Label(text)) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {
				String requestData = "{\"carrier_packing\":\"\"}";
				if(isShipment()){
					requestData = "{\"carrier_packing\":\""+ getCarrierPacking().getId() +"\","
							+ "\"purchase\":\""+ order.getId() +"\"," 
							+ "\"action\":\"delete\""+ "}";

					getAPI().getWarehouse().addAllCarrierPacking("purchase", requestData, new AsyncCallback<JSON<JsOrderDetail>>() {
						@Override public void onSuccess(JSON<JsOrderDetail> result) {
							refresh();
						}
						@Override public void onFailure(Throwable caught) {}
					});
				} else {
					getAPI().getWarehouse().updateDelivery(order.getId(), requestData, new AsyncCallback<JsOrder>() {
						@Override public void onFailure(Throwable caught) {}
						@Override public void onSuccess(JsOrder result) {
							refresh();
						}
					});		
				}
				hide();
			}
		};
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();
	}
	
	private void addAllOrder(JsOrder order){
		String requestData = "{\"carrier_packing\":\""+ getCarrierPacking().getId() +"\","
				+ "\"purchase\":\""+ order.getId() +"\","
				+ "\"action\":\"add\"" + "}";
		if(isShipment()){
			getAPI().getWarehouse().addAllCarrierPacking(getOrderType(), requestData, new AsyncCallback<JSON<JsOrderDetail>>() {
				
				@Override
				public void onSuccess(JSON<JsOrderDetail> result) {
					refresh();
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
		} else {
			getAPI().getWarehouse().updateDelivery(order.getId(), requestData, new AsyncCallback<JsOrder>() {
				
				@Override
				public void onSuccess(JsOrder result) {
					refresh();
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
		}
	}
	
	private void addOneDetail(JsOrderDetail detail, Double quantity){
		selectableOrder = detail.getPurchase();
		String requestData = "{\"id\":\""+ detail.getId() +"\","
				+"\"action\":\""+ (detail.getCarrierPacking() != null ? "delete" : "add") + "\","
				+"\"carrier_packing\":\""+ getCarrierPacking().getId() + "\","
				+"\"quantity\":\""+ quantity + "\""
				+ "}";
		
		getAPI().getWarehouse().addCarrierPacking(getOrderType(), requestData, new AsyncCallback<JSON<JsOrderDetail>>() {
			
			@Override
			public void onSuccess(JSON<JsOrderDetail> result) {
				refresh();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
		
	private void refresh(){
		selectableVerticalPanel.remove(1);
		selectableVerticalPanel.add(selectableItems());
		selectedVerticalPanel.remove(1);
		selectedVerticalPanel.add(selectedItems());
	}
	
	private Boolean isShipment(){
		return getCarrierPacking().getType().getName().equals(CarrierPackingType.SHIPMENT_REQUEST.getName());
	}
	
	private Boolean isPending(){
		return getCarrierPacking().getStatus().getName().equals(CarrierPackingStatus.PENDING.getName());
	}
	
	private Boolean isOnRoute(){
		return getCarrierPacking().getStatus().getName().equals(CarrierPackingStatus.ON_ROUTE.getName());
	}
	
	private Boolean isFinished(){
		return getCarrierPacking().getStatus().getName().equals(CarrierPackingStatus.FINISHED.getName());
	}
	
	private Boolean isSelected(String panel){
		return "selected".equals(panel);
	}
	
	private Boolean isSelectable(String panel){
		return "selectable".equals(panel);
	}
	
	private Boolean isReception(String panel){
		return "reception".equals(panel);
	}
	
	private String getOrderType(){
		if(isShipment()){
			return "purchase";
		} else {
			return "delivery";
		}
	}	
	
	private void addToIncome(JsOrder order, JsOrderDetail detail){
		String serie = Utils.format("yyMMdd", new Date());
		VerticalPanel panel = new VerticalPanel();
		panel.addStyleName(AON.AON_CSS.aonWidthAll());
		Label label = new Label(detail.getDescription());
		panel.add(label);
		
		AonComboBox income = new AonComboBox();
		income.setLabel("Albar\u00e1n");
		income.setItemLabelPath("reference_code");
		income.setItemValuePath("reference_code");
		
		HashMap<String, LinkedList<String>> map = new HashMap<>();
		LinkedList<String> l = new LinkedList<>();
		l.add(getCarrierPacking().getId() + "");
		map.put("carrier_packing", l);
		LinkedList<String> l2 = new LinkedList<>();
		l2.add(order.getRegistry().getId() + "");
		map.put("supplier", l2);
		getAPI().getWarehouse().getOrders("income", map, new AsyncCallback<JSON<JsOrder>>() {
			
			@Override
			public void onSuccess(JSON<JsOrder> result) {
				income.setVisible(result.getData().length() > 0);	
				income.setItems(result.getData());
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		panel.add(income);
		
		PaperInput ref = new PaperInput();
		ref.setLabel("N\u00famero");
		panel.add(ref);
		
		AonComboBox warehouse = new AonComboBox();
		warehouse.setLabel("Almac\u00e9n");
		warehouse.setItemLabelPath("name");
		warehouse.setItemValuePath("name");
		getAPI().getWarehouse().getWarehouses(new AsyncCallback<JSON<JsWarehouse>>() {
			
			@Override
			public void onSuccess(JSON<JsWarehouse> result) {
				warehouse.setItems(result.getData());
				if(result.getData().length() > 0){
					//warehouse.setInputElementValue(result.getData().get(0).getName());
					warehouse.setSelectedItem(warehouse.getItems().get(0));
				}
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		panel.add(warehouse);
		PaperInput param = new PaperInput();
		param.setLabel("Fecha");
		param.setValue(Utils.formatDate(new Date()));
		param.setMaxlength(10);

		panel.add(param);

		HorizontalPanel hp = new HorizontalPanel();
		PaperInput quantity = new PaperInput();
		quantity.setLabel("Cantidad");
		quantity.setValue((detail.getQuantity() - detail.getDelivered())+ "");
		quantity.setMaxlength(8);
		hp.add(quantity);
		
		PaperInput peso1 = new PaperInput();
		peso1.setLabel("Peso 1");
		peso1.setValue(detail.getQuantity() + "");
		peso1.setMaxlength(8);
		peso1.setWidth("50px");
		peso1.setVisible(false);
		hp.add(peso1);
		
		PaperInput peso2 = new PaperInput();
		peso2.getElement().getStyle().setPaddingLeft(5, Unit.PX);
		peso2.setLabel("Peso 2");
		peso2.setValue(detail.getQuantity() + "");
		peso2.setMaxlength(8);
		peso2.setWidth("50px");
		peso2.setVisible(false);
		hp.add(peso2);
		
		PaperInput quan = new PaperInput();
		quan.getElement().getStyle().setPaddingLeft(5, Unit.PX);
		quan.setLabel("Cantidad");
		Double p1 = Double.parseDouble(peso1.getValue());
		Double p2 = Double.parseDouble(peso2.getValue());
		quan.setValue((p2 - p1) + "");
		quan.setMaxlength(8);
		quan.setWidth("50px");
		quan.setVisible(false);
		quan.setDisabled(true);
		hp.add(quan);
		
		peso1.addChangeHandler(new ChangeEventHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				Double p1 = Double.parseDouble(peso1.getValue());
				Double p2 = Double.parseDouble(peso2.getValue());
				quan.setValue((p2 - p1) + "");
			}
		});
		
		peso2.addChangeHandler(new ChangeEventHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				Double p1 = Double.parseDouble(peso1.getValue());
				Double p2 = Double.parseDouble(peso2.getValue());
				quan.setValue((p2 - p1) + "");
			}
		});
	
		Label saldarLabel = new Label("Saldar");
		saldarLabel.getElement().getStyle().setMarginLeft(10, Unit.PX);
		saldarLabel.getElement().getStyle().setMarginTop(45, Unit.PX);
		saldarLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);		
		hp.add(saldarLabel);
				
		PaperToggleButton ptb = new PaperToggleButton();
		ptb.getElement().getStyle().setMarginLeft(10, Unit.PX);
		ptb.getElement().getStyle().setMarginTop(40, Unit.PX);	
		ptb.setChecked(false);
		hp.add(ptb);
		panel.add(hp);

		PaperInput lote = new PaperInput();
		lote.setLabel("Lote");
		lote.setVisible(detail.isLotable()); 
		getAPI().getWarehouse().getIncomeLastLote(serie, new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> result) {
				ref.setValue(result.getOneData().getName());
				lote.setValue(result.getOneData().getName() + "1");				
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		
		income.addValueChangedHandler(new ValueChangedEventHandler() {
			
			@Override
			public void onValueChanged(ValueChangedEvent event) {
				if(income.getValue() == null || income.getValue().equals("")){
					ref.setVisible(true);
					warehouse.setVisible(true);
					param.setVisible(true);
					lote.setValue(ref.getValue() + "1");
				}else {
					ref.setVisible(false);
					warehouse.setVisible(false);
					param.setVisible(false);
					JsOrder js = (JsOrder) income.getSelectedItem();
					lote.setValue(js.getReferenceCode() + js.getDetailCount());
				}
			}
		});
		panel.add(lote);
    	AonDialog dialog = new AonDialog("Albar\u00e1n", panel) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {
				Double q = Double.parseDouble(quantity.getValue()); 
				Boolean saldar = ptb.getChecked();
				String l = "";
				
				if(income.getValue() != null && !income.getValue().equals("")){
					JsOrder order = (JsOrder) income.getSelectedItem();
					if(detail.isLotable()){
						l = lote.getValue();
						String requestData = "{\"item_id\":\""+ detail.getItem() +"\","
								+ "\"lote\":\""+ l +"\"}";
						getAPI().getProduct().insertItem(requestData, new AsyncCallback<JsItem>() {
							
							@Override
							public void onSuccess(JsItem result) {
								addIncomeDetail(order, detail, q, result.getId(), saldar);
							}
							
							@Override public void onFailure(Throwable caught) {}
						});
					} else addIncomeDetail(order, detail, q, detail.getItem(), saldar);
				} else {
					JsWarehouse js = (JsWarehouse) warehouse.getSelectedItem();
					String number = ref.getValue();
					String date = Utils.formatDateTime(Utils.parseDate(param.getValue()));
					String requestData = "{\"reference_code\":\""+ number +"\","
							+ "\"issue_time\":\""+ date +"\","
							+ "\"supplier\":\""+ order.getRegistry().getId() +"\","
							+ "\"address\":\""+ order.getAddress() +"\","
							+ "\"carrier_packing\":\""+ getCarrierPacking().getId() +"\","
							+ "\"workplace\":\"" + js.getWorkplace() + "\"" + "}";
					getAPI().getWarehouse().insertIncome(requestData, new AsyncCallback<JsOrder>() {
						
						@Override
						public void onSuccess(JsOrder result) {
							if(detail.isLotable()){
								String l = lote.getValue();
								String requestData = "{\"item_id\":\""+ detail.getItem() +"\","
										+ "\"lote\":\""+ l +"\"}";
								getAPI().getProduct().insertItem(requestData, new AsyncCallback<JsItem>() {
									
									@Override
									public void onSuccess(JsItem result2) {
										addIncomeDetail(result, detail, q, result2.getId(), saldar);
									}
									
									@Override public void onFailure(Throwable caught) {}
								});
							}else addIncomeDetail(result, detail, q, detail.getItem(), saldar);
						}
						
						@Override
						public void onFailure(Throwable caught) {}
					});
					
				}
				hide();
			}
    	};
		dialog.addAutoHidePartner(income.getElementById("overlay"));
		dialog.addAutoHidePartner(warehouse.getElementById("overlay"));
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();
	}
	
	private void addIncomeDetail(JsOrder income, JsOrderDetail detail, Double quantity, Integer item, Boolean saldar){
		receptionOrder = income.getId();
		if(quantity > 0){
			String requestData = "{\"item\":\""+ item +"\","
				+ "\"description\":\""+ detail.getDescription() +"\","
				+ "\"workplace\":\""+ income.getWorkplace() +"\","
				+ "\"quantity\":\""+ quantity +"\","
				+ "\"income\":\""+ income.getId() +"\","
				+ "\"purchase_detail\":\"" + detail.getId() + "\"" + "}";
			getAPI().getWarehouse().insertDetail("income", requestData, new AsyncCallback<JsOrderDetail>() {
			
				@Override
				public void onSuccess(JsOrderDetail result) {
					updatePurchaseDetailDelivered(detail.getId(), quantity, saldar);
				}
			
				@Override public void onFailure(Throwable caught) {}
			});			
		}
	}
	
	
	private void removeIncomeDetail(JsOrder income, JsOrderDetail incomeDetail, Double quantity, Boolean saldar){
		String text = "Est\u00e1s seguro Eliminar "+ incomeDetail.getDescription() + " del Albar\u00e1n " + income.getReferenceCode();
      	AonDialog dialog = new AonDialog("Eliminar", new Label(text)) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {
				receptionOrder = income.getId();

				Double pq = incomeDetail.getQuantity();
				if(quantity > 0 && quantity <= pq){
					if(quantity == pq){
						String requestData = "{"
								+ "\"id\":\""+ incomeDetail.getId() +"\""
								+ "}";
						getAPI().getWarehouse().deleteDetail("income", requestData, new AsyncCallback<JsOrder>() {
							@Override
							public void onSuccess(JsOrder result) {
								updatePurchaseDetailDelivered(incomeDetail.getPurchaseDetail(), -quantity, saldar);
							}
							
							@Override
							public void onFailure(Throwable caught) {}
						});
					} else {
						String requestData = "{\"quantity\":\""+ (incomeDetail.getQuantity() - quantity) +"\","
								+ "\"id\":\""+ incomeDetail.getId() +"\""
								+ "}";
						getAPI().getWarehouse().updateDetail("income", requestData, new AsyncCallback<JsOrder>() {
							
							@Override
							public void onSuccess(JsOrder result) {
								updatePurchaseDetailDelivered(incomeDetail.getPurchaseDetail(), -quantity, saldar);						
							}
							
							@Override public void onFailure(Throwable caught) {}
						});
					}
				}
				hide();
			}
		};
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();
	}
	
	private void updatePurchaseDetailDelivered(Integer purchaseDetailId, Double quantity, Boolean saldar){
		String purchaseDetailRD = "{\"delivered\":\""+ quantity +"\","
				+ "\"id\":\""+ purchaseDetailId +"\","
				+ "\"saldar\":\""+ saldar +"\""
				+ "}";
		getAPI().getWarehouse().updateDetail("purchase", purchaseDetailRD, new AsyncCallback<JsOrder>() {
			
			@Override
			public void onSuccess(JsOrder result) {
				receptionVerticalPanel.remove(1);
				receptionVerticalPanel.add(receptionItems());
				

				selectedVerticalPanel.remove(1);
				selectedVerticalPanel.add(selectedItems());
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	private void send(JsOrder js) {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName(AON.AON_CSS.aonWidthAll());
		AonComboBox emailComboBox = new AonComboBox();
    	emailComboBox.setLabel("De");
    	emailComboBox.setItemLabelPath("name");
    	emailComboBox.setItemValuePath("name");
		getAPI().getCommon().getMailAccounts(new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> result) {
		    	emailComboBox.setItems(result.getData());
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		panel.add(emailComboBox);
		
		PaperInput toText = new PaperInput();
		toText.setLabel("Para");
		getAPI().getIncidence().getEnterpriseRmediaList(js.getRegistry().getId(), new AsyncCallback<JSON<JsRmedia>>() {
			
			@Override
			public void onSuccess(JSON<JsRmedia> result) {
				StringBuilder emails = new StringBuilder();
				result.getData().stream().forEach(rmedia -> {
					String media = rmedia.getMedia() + "";
					if(rmedia.isTechnical() && media.equals("4")){	
						emails.append(rmedia.getValue());
						emails.append(";");
					}
				});
				toText.setValue(emails.toString());
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		panel.add(toText);
		
		AonComboBox signComboBox = new AonComboBox();
    	signComboBox.setLabel("Firma de Correo");
    	signComboBox.setItemLabelPath("name");
    	signComboBox.setItemValuePath("name");
    	getAPI().getCommon().getSignatures(new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> result) {
				signComboBox.setItems(result.getData());
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
    	panel.add(signComboBox);
    	
      	AonDialog dialog = new AonDialog("Enviar Packing List", panel) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {
				JsObject jsEmail = (JsObject) emailComboBox.getSelectedItem();
				JsObject jsSign = (JsObject) signComboBox.getSelectedItem();
				String requestData = "{\"carrier_packing\":\""+ getCarrierPacking().getId() +"\","
						+ "\"mail_account\":\""+ jsEmail.getId() +"\","
						+ "\"signature\":\""+ ((jsSign != null) ? jsSign.getId() : "-1" )+"\"," 
						+ "\"to\":\""+ toText.getValue() + "\","
						+ "\"order\":\""+ js.getId() + "\","
						+ "\"type\":\"registry\"" + "}";

				getAPI().getWarehouse().sendPackingList(requestData);
				hide();
			}
		};
		dialog.addAutoHidePartner(emailComboBox.getElementById("overlay"));
		dialog.addAutoHidePartner(signComboBox.getElementById("overlay"));
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();
	}
	
	private void truckHandler(PaperIconButton truck) {
		Boolean b1 = getCarrierPacking().getReceptionStartDate() != null;
		
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName(AON.AON_CSS.aonWidthAll());
		
		if(b1){
			PaperInput pesoBruto = new PaperInput();
			pesoBruto.setLabel("Peso Bruto");
			pesoBruto.setValue(getCarrierPacking().getGross().toString());
			pesoBruto.setDisabled(true);
			panel.add(pesoBruto);
		}
		
		PaperInput quantity = new PaperInput();
		quantity.setLabel(b1 ? "Tara" : "Peso Bruto");
		panel.add(quantity);
    	
		if(b1){
			PaperInput pesoNeto = new PaperInput();
			pesoNeto.setLabel("Peso Neto");
			pesoNeto.setDisabled(true);
			panel.add(pesoNeto);		
			
			quantity.addChangeHandler(new ChangeEventHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					Double d = Double.parseDouble(quantity.getValue());
					Double net = getCarrierPacking().getGross() - d;
					pesoNeto.setValue(net.toString());
				}
			});
		}
		
      	AonDialog dialog = new AonDialog("Recepcion", panel) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {
				JSONObject json = new JSONObject();	
				String requestData = json.toString();
				if(getCarrierPacking().getReceptionStartDate() != null){
					Double tare = Double.parseDouble(quantity.getValue());
					json.put("tare", new JSONNumber(tare));
					json.put("net", new JSONNumber(getCarrierPacking().getGross() - tare));	
					json.put("reception_end_date", new JSONString(Utils.formatDateTime(new Date())));
					json.put("status", new JSONNumber(CarrierPackingStatus.FINISHED.ordinal()));
					requestData = JsonUtils.stringify(json.getJavaScriptObject());
				} else {
					Double gross = Double.parseDouble(quantity.getValue()); 
					json.put("gross", new JSONNumber(gross));
					json.put("reception_start_date", new JSONString(Utils.formatDateTime(new Date())));
					requestData = JsonUtils.stringify(json.getJavaScriptObject());
				}
				getAPI().getWarehouse().updateCarrierPacking(getCarrierPacking().getId(), requestData, new AsyncCallback<JsCarrierPacking>() {

					@Override public void onFailure(Throwable caught) {}

					@Override
					public void onSuccess(JsCarrierPacking result) {
						setCarrierPacking(result);
						if(getCarrierPacking().getReceptionEndDate() != null){
							parent.content();
							parent.status.setSelectedIndex(CarrierPackingStatus.FINISHED.ordinal());
						}
						truck.getElement().getStyle().setColor(result.getReceptionEndDate() != null ? "red" : "green");
						hide();
			
		}
				});
			}
		};
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();
	}
}
