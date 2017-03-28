package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing.nuevo;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.api.client.registry.JsRmedia;
import com.esferalia.aon.gwt.api.client.warehouse.JsCarrierPacking;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrder;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrderDetail;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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

import net.aonsolutions.aon.gwt.warehouse.client.Utils;
import net.aonsolutions.polymer.aon.widget.AonComboBox;


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

	private CarrierPackingDetail parent;
	
	private API getAPI(){
		return parent.getAPI();
	}

	private JsCarrierPacking getCarrierPacking(){
		return parent.getJsCarrierPacking();
	}
	
	@Override
	public void onResize() {
		super.onResize();
		ScrollPanel selectedScrollPanel = (ScrollPanel) selectedVerticalPanel.getWidget(1);
		selectedScrollPanel.getElement().getStyle().setHeight(getOffsetHeight() > 25 ? getOffsetHeight() - 25 : 0.0, Unit.PX);
		if(isPending()){
			ScrollPanel selectableScrollPanel = (ScrollPanel) selectableVerticalPanel.getWidget(1);
			selectableScrollPanel.getElement().getStyle().setHeight(getOffsetHeight() > 50 ? getOffsetHeight() - 50 : 0.0, Unit.PX);
		} else if(isShipment()){
			ScrollPanel receptionScrollPanel = (ScrollPanel) receptionVerticalPanel.getWidget(1);
			receptionScrollPanel.getElement().getStyle().setHeight(getOffsetHeight() > 25 ? getOffsetHeight() - 25 : 0.0, Unit.PX);
		}
	}
	
	public SelectionPanel(CarrierPackingDetail parent) {	
		this.parent = parent;
		initSelectedFilter();
		initSelectableFilter();
		SplitLayoutPanel rootPanel = new SplitLayoutPanel(4);

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
	}
	
	// -------------------- RECEPTION PANEL
	private void receptionPanel(){
		receptionVerticalPanel = new VerticalPanel();
		receptionVerticalPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		Label label = new Label("Recepcion");
		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		label.getElement().getStyle().setPadding(3, Unit.PX);
		receptionVerticalPanel.add(label);
		receptionVerticalPanel.add(receptionItems());
	}
	
	private ScrollPanel receptionItems() {
		ScrollPanel scroll = new ScrollPanel();
		
		return scroll;
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
		selectedVerticalPanel.add(label);
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
		addSelectable(scroll, orders);
	}
	
	private void addSelected(ScrollPanel scroll, JSON<JsOrder> orders){
		VerticalPanel vp = (VerticalPanel) scroll.getWidget();
		orders.getData().stream().forEach(order -> buildOrderItem(vp, order, true));
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
				}
			}
		}); 
		p2.add(serie);
		table.setWidget(1, 1, serie);
		
		Label numberLabel = new Label("Numero ");
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
		orders.getData().stream().forEach(order -> buildOrderItem(vp, order, false));
	}
	
	private void buildOrderItem(VerticalPanel vp, JsOrder order, Boolean selected){
		PaperItem pi = new PaperItem();
		HorizontalPanel hp = new HorizontalPanel();
		if(!isFinished()){
			if(selected){
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
			//pib.setIcon(selected ? "remove" : "add");
			pib.setIcon("swap-horiz");
	    	pib.setStyle("height:24px;font-size:12px;padding:0px;font-weight: bold;");
	    	pib.setNoink(true);
	    
			pib.addClickHandler(new ClickHandler() {
			
				@Override
				public void onClick(ClickEvent event) {
					if(isPending()){
						if(selected){
							removeAllOrder(order); 
						} else {
							addAllOrder(order);
						}
					} else{
						// TODO RECEPTION OPTIONS!!! 
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
	    		: "") + order.getSeriesNumber() + " - " + order.getRegistry().getName(); // + WORKPLACE!
	    pi.add(new Label(title));
	    pi.setStyle("min-height:24px;font-size:12px;padding:0px;font-weight: bold;");
	    
	    SimplePanel sp = new SimplePanel();
		sp.setVisible(false);
		
	    if(selected || (!selected && selectableOrder != null && selectableOrder == order.getId())){
	    	ironIcon.setIcon("arrow-drop-down");
	    	loadDetails(sp, selected, order);
	    }
	
		pi.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
		    	ironIcon.setIcon(sp.isVisible() ? "arrow-drop-up" : "arrow-drop-down" );
				loadDetails(sp, selected, order);
			}
		});
	    hp.add(pi);
	    vp.add(hp);
		vp.add(sp);
	}
	
	private void loadDetails(SimplePanel sp, Boolean selected, JsOrder order){
		if(!sp.isVisible()){
			HashMap<String, LinkedList<String>> map = new HashMap<>();
			LinkedList<String> list = new LinkedList<>();
			list.add(selected ? (getCarrierPacking().getId() + "") : "null");
			map.put("carrier_packing", list);
			list = new LinkedList<>();
			list.add("" + order.getId());
			map.put("parent_id", list);
			getAPI().getWarehouse().getDetails(getOrderType(), map, new AsyncCallback<JSON<JsOrderDetail>>() {
				
				@Override
				public void onSuccess(JSON<JsOrderDetail> result) {
					sp.setWidget(buildDetail(result, selected));
					sp.setVisible(!sp.isVisible());
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
		} else sp.setVisible(!sp.isVisible());
	}
	
	private VerticalPanel buildDetail(JSON<JsOrderDetail> details, Boolean selected) {
		VerticalPanel vp = new VerticalPanel();
		details.getData().stream().forEach(detail ->{
			HorizontalPanel panel = new HorizontalPanel();
			panel.getElement().getStyle().setPaddingLeft(40, Unit.PX);

		    Label label = new Label(detail.getDescription());
		    label.getElement().getStyle().setPaddingTop(2, Unit.PX);
		   
		    
		    DoubleBox db = new DoubleBox();
		    db.getElement().getStyle().setMarginRight(10, Unit.PX);
		    db.getElement().getStyle().setHeight(13, Unit.PX);
		    db.getElement().getStyle().setWidth(40, Unit.PX);
		    db.getElement().getStyle().setPadding(0, Unit.PX);
		    db.setEnabled(isShipment() && !selected);
		    db.setValue(detail.getQuantity());
		    
		    if(isShipment() && !isFinished()){
				PaperIconButton pib = new PaperIconButton();
				//pib.setIcon(selected ? "remove" : "add");
				pib.setIcon("swap-horiz");
				pib.setStyle("height:16px;font-size:12px;padding:0px;font-weight: bold;");
		    	pib.setNoink(false);
		    	panel.add(pib);
			    pib.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						if(isPending()){
							addOneDetail(detail, db.getValue());
						} else {
							// TODO RECEPTION OPTIONS.
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
	
	
	private void removeAllOrder(JsOrder order){
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
	
	private String getOrderType(){
		if(isShipment()){
			return "purchase";
		} else {
			return "delivery";
		}
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
}
