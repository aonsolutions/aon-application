package net.aonsolutions.aon.gwt.warehouse.client.elaboration;

import java.util.HashMap;
import java.util.LinkedList;

import net.aonsolutions.aon.gwt.warehouse.client.widget.ItemBox;
import net.aonsolutions.polymer.aon.widget.AonComboBox;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.warehouse.JsElaboration;
import com.esferalia.aon.gwt.api.client.warehouse.JsElaborationDetail;
import com.esferalia.aon.gwt.api.client.warehouse.JsElaborationDetailComposition;
import com.esferalia.aon.gwt.api.client.warehouse.JsWarehouse;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.occam.api.model.type.ElaborationStatus;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperIconButton;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperItem;
import com.vaadin.polymer.paper.widget.event.ChangeEvent;
import com.vaadin.polymer.paper.widget.event.ChangeEventHandler;


public class SelectionPanel extends ResizeComposite implements RequiresResize {
	
	static final String GRAY_COLOR = "#DDDDDD";
	static final String GRAY_SUAVE_COLOR = "#F5F5F5";

	private HashMap<String, LinkedList<String>> selectedFilter;
	private Boolean selectedMore = true;
	private VerticalPanel detailVerticalPanel;
	private HashMap<String, LinkedList<String>> selectableFilter;
	private Boolean selectableMore = true;
	private VerticalPanel compositionVerticalPanel;
	private Integer selectableOrder;	
	

	
	private API API;
	private JsElaboration jsElaboration;
	private JsElaborationDetail selectedDetail;


	private JsElaboration getElaboration(){
		return jsElaboration;
	}

	
	@Override
	public void onResize() {
		super.onResize();
		ScrollPanel selectedScrollPanel = (ScrollPanel) detailVerticalPanel.getWidget(1);
		selectedScrollPanel.getElement().getStyle().setHeight(getOffsetHeight() > 25 ? getOffsetHeight() - 25 : 0.0, Unit.PX);
		if(isPending()){
			ScrollPanel selectableScrollPanel = (ScrollPanel) compositionVerticalPanel.getWidget(1);
			selectableScrollPanel.getElement().getStyle().setHeight(getOffsetHeight() > 65 ? getOffsetHeight() - 65 : 0.0, Unit.PX);
		}
	}
	
	public void resize() {
		onResize();
	}
	
	public SelectionPanel(API API, JsElaboration jsElaboration) {	
		this.API = API;
		this.jsElaboration = jsElaboration;

		SplitLayoutPanel rootPanel = new SplitLayoutPanel(4);
		
		Window.addResizeHandler(new ResizeHandler() {
				
			@Override
			public void onResize(ResizeEvent event) {
				resize();
			}
		});

		SimpleLayoutPanel selected = new SimpleLayoutPanel();
		selected.setStyleName(AON.AON_CSS.aonFlexContainer());
		selected.addStyleName(AON.AON_CSS.aonPadding2Top());
		selected.setStyleName(AON.AON_CSS.aonInvoicePanel());
		selected.getElement().getStyle().setBackgroundColor(isPending() ? GRAY_SUAVE_COLOR : GRAY_COLOR);
		selected.getElement().getStyle().setMarginLeft(0, Unit.PX);
		buildDetailPanel();
		selected.setWidget(detailVerticalPanel);
		rootPanel.add(selected);
		
		// TODO: select composition list if only one detail
//		if(){
			SimpleLayoutPanel selectable = new SimpleLayoutPanel();
			selectable.setStyleName(AON.AON_CSS.aonFlexContainer());
			selectable.addStyleName(AON.AON_CSS.aonPadding2Top());
			selectable.setStyleName(AON.AON_CSS.aonInvoicePanel());
			selectable.getElement().getStyle().setBackgroundColor(GRAY_COLOR);
			selectable.getElement().getStyle().setMarginLeft(0, Unit.PX);
			buildCompositionPanel(null);
			selectable.setWidget(compositionVerticalPanel);
			rootPanel.addEast(selectable, Window.getClientWidth()/2);
//		}
		
		initWidget(rootPanel);
		onResize();
	}
	
	

	// -------------------- DETAIL PANEL
	
	private void buildDetailPanel() {
		if(detailVerticalPanel==null){
			detailVerticalPanel = new VerticalPanel();
		} else {
			detailVerticalPanel.clear();
		}
		detailVerticalPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		Label label = new Label("Finalizados");
		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		label.getElement().getStyle().setPadding(3, Unit.PX);
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(label);
		
		if(!isClosed()){
			Button newDetailButton = new Button();
			newDetailButton.setTitle("Nuevo elaborado");
			newDetailButton.setStyleName(AON.AON_CSS.aonIconReset());
			newDetailButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
			newDetailButton.addStyleName(AON.AON_CSS.aonMarginLeft());
			newDetailButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					clickAddDetail();
				}
			});
			hp.add(newDetailButton);
		}
		
		detailVerticalPanel.add(hp);
		detailVerticalPanel.add(createDetailItems());
	}
	
	private ScrollPanel createDetailItems() {
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
					API.getWarehouse().getElaborationDetail(jsElaboration.getId(),
							new AsyncCallback<JSON<JsElaborationDetail>>() {

								@Override
								public void onSuccess(JSON<JsElaborationDetail> result) {
									addDetail(result);
								}

								@Override
								public void onFailure(Throwable caught) {
								}
							});
				}
			}
		});
		scrollPanel.add(new VerticalPanel());
		API.getWarehouse().getElaborationDetail(jsElaboration.getId(),
				new AsyncCallback<JSON<JsElaborationDetail>>() {

					@Override
					public void onSuccess(JSON<JsElaborationDetail> result) {
						addDetail(scrollPanel, result);
					}

					@Override
					public void onFailure(Throwable caught) {
					}
				});
		
		return scrollPanel;
	}
	
	private void addDetail(JSON<JsElaborationDetail> orders){
		ScrollPanel scroll = (ScrollPanel) detailVerticalPanel.getWidget(1);
		addDetail(scroll, orders);
	}
	
	private void addDetail(ScrollPanel scroll, JSON<JsElaborationDetail> orders){
		VerticalPanel vp = (VerticalPanel) scroll.getWidget();
		orders.getData().stream().forEach(js -> {
			buildDetailItem(vp, js, "selected");
		});
	}
	

	
	private void buildDetailItem(VerticalPanel vp, JsElaborationDetail detail, String panel){
		this.selectedDetail = detail;
		
		PaperItem pi = new PaperItem();
		HorizontalPanel hp = new HorizontalPanel();

		IronIcon ironIcon = new IronIcon();
	   	ironIcon.setIcon("arrow-drop-up");
	    pi.add(ironIcon);
	   
		String serial = detail.getItem().getSerialNumber();
		
		String label = detail.getQuantity()
				+ " uds. "
				+ (serial != null ? "(#" + serial + ") " : "")
				+ detail.getDate();
		String title = (label);
	    
	    Label ot = new Label(title.length() > 70 ? title.substring(0,70) + "..." : title);
	    ot.setTitle(title);
	    pi.add(ot);
	    

	    pi.setStyle("min-height:24px;font-size:12px;padding:0px;font-weight: bold;");
	    
	    SimplePanel sp = new SimplePanel();
		sp.setVisible(false);

	    if( isSelected(panel) || (isSelectable(panel) && selectableOrder != null && selectableOrder == detail.getId()) ){
	    	ironIcon.setIcon("arrow-drop-down");
	    	loadDetailDropDown(sp, panel, detail);
	    }

		pi.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				ironIcon.setIcon(sp.isVisible() ? "arrow-drop-up" : "arrow-drop-down" );
		    	buildCompositionPanel(detail);
//		    	loadDetailDropDown(sp, panel, order);
			}
		});

	    hp.add(pi);
	    
	    if(!isClosed()){
	    	PaperIconButton pib = new PaperIconButton();
	    	pib.setTitle("Borrar seleccionado");
	    	pib.setStyleName(AON.AON_CSS.aonIconDelete());
	    	pib.addStyleName(AON.AON_CSS.aonIconCommandButton());
	    	pib.addStyleName(AON.AON_CSS.aonMarginLeft());
	    	pib.setNoink(true);
	    	
	    	pib.addClickHandler(new ClickHandler() {
	    		
	    		@Override
	    		public void onClick(ClickEvent event) {
	    			clickRemoveDetail(detail);
	    		}
	    	});
	    	
	    	hp.add(pib);
	    }
	    
	    
	    vp.add(hp);
		vp.add(sp);
	}
	
	private void loadDetailDropDown(SimplePanel sp, String panel, JsElaborationDetail detail){
		if(!sp.isVisible()){
//			if(isReception(panel)){
//				getAPI().getWarehouse().getDetails(order.getId(), "income", new AsyncCallback<JSON<JsOrderDetail>>() {
//					
//					@Override
//					public void onSuccess(JSON<JsOrderDetail> result) {
//						sp.setWidget(buildDetail(order, result, panel));
//						sp.setVisible(!sp.isVisible());
//					}
//				
//					@Override public void onFailure(Throwable caught) {}
//				});
//			} else {
//				HashMap<String, LinkedList<String>> map = new HashMap<>();
//				LinkedList<String> list = new LinkedList<>();
//				list.add(isSelected(panel) ? (getCarrierPacking().getId() + "") : "null");
//				map.put("carrier_packing", list);
//				list = new LinkedList<>();
//				list.add("" + order.getId());
//				map.put("parent_id", list);
//				getAPI().getWarehouse().getDetails(getOrderType(), map, new AsyncCallback<JSON<JsOrderDetail>>() {
//				
//					@Override
//					public void onSuccess(JSON<JsOrderDetail> result) {
//						sp.setWidget(buildDetail(order, result, panel));
//						sp.setVisible(!sp.isVisible());
//					}
//				
//					@Override public void onFailure(Throwable caught) {}
//				});
//			}
			
//			sp.setWidget(new Label("composition"));
//			sp.setVisible(!sp.isVisible());
		} else {
			sp.setVisible(!sp.isVisible());
		}
		
	}

	// -------------------- COMPOSITION PANEL
	
	private void buildCompositionPanel(JsElaborationDetail detail) {
		
		String description = "";
		if(compositionVerticalPanel==null){
			// TODO load base item composition in readOnly mode
			compositionVerticalPanel = new VerticalPanel();
			description = ("("+getElaboration().getDescription()+")");
		} else {
			compositionVerticalPanel.clear();
			if(detail!=null && detail.getItem()!=null){
				description = ("(#" + detail.getItem().getSerialNumber() + ")");
			}
		}
		compositionVerticalPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		Label label = new Label("Composici\u00F3n " + description);
		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		label.getElement().getStyle().setPadding(3, Unit.PX);
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(label);
		
		if(isDetailSelected() && !isClosed()){
			Button newCompositionButton = new Button();
			newCompositionButton.setTitle("Nueva Composici\u00F3n");
			newCompositionButton.setStyleName(AON.AON_CSS.aonIconReset());
			newCompositionButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
			newCompositionButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					clickAddComposition();
				}
			});
			hp.add(newCompositionButton);
		}
	
		compositionVerticalPanel.add(hp);
		compositionVerticalPanel.add(compositionItems(detail));	
	}

	
	private ScrollPanel compositionItems(JsElaborationDetail detail) {
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
					API.getWarehouse().getElaborationDetailComposition(detail.getId(), new AsyncCallback<JSON<JsElaborationDetailComposition>>() {
						
						@Override
						public void onSuccess(JSON<JsElaborationDetailComposition> result) {
							selectableMore = Integer.parseInt(selectableFilter.get("perPage").get(0)) > result.getData().length();
							addComposition(result);
						}
						
						@Override
						public void onFailure(Throwable caught) {}
					});
				}
			}
		});
		scrollPanel.add(new VerticalPanel());
		if(detail!=null && detail.getId()!=null){
			API.getWarehouse().getElaborationDetailComposition(detail.getId(), new AsyncCallback<JSON<JsElaborationDetailComposition>>() {
				
				@Override
				public void onSuccess(JSON<JsElaborationDetailComposition> result) {
					addComposition(scrollPanel, result);
				}
				
				@Override
				public void onFailure(Throwable caught) {}
			});
		}
		
		return scrollPanel;
	}
	
	private void addComposition(JSON<JsElaborationDetailComposition> orders){
		ScrollPanel scroll = (ScrollPanel) compositionVerticalPanel.getWidget(1);
		addComposition(scroll, orders);
	}
	
	private void addComposition(ScrollPanel scroll, JSON<JsElaborationDetailComposition> orders){
		VerticalPanel vp = (VerticalPanel) scroll.getWidget();
		orders.getData().stream().forEach(order -> buildCompositionItem(vp, order, "selectable"));
	}
	
	private void buildCompositionItem(VerticalPanel vp, JsElaborationDetailComposition composition, String panel){
		PaperItem pi = new PaperItem();
		HorizontalPanel hp = new HorizontalPanel();

	    String title = "(" + composition.getQuantity() + " uds.) "
	    		+ composition.getItem().getCode() + " - " + composition.getItem().getName(); 
	   
	    Label ot = new Label(title.length() > 70 ? title.substring(0,70) + "..." : title);
	    ot.setTitle(title);
	    pi.add(ot);

	    pi.setStyle("min-height:24px;font-size:12px;padding:0px;font-weight: bold;");
	    
	    SimplePanel sp = new SimplePanel();
		sp.setVisible(false);

		// TODO edit composition
//		pi.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent arg0) {
//		    	ironIcon.setIcon(sp.isVisible() ? "arrow-drop-up" : "arrow-drop-down" );
//				loadDetails(sp, panel, order);
//			}
//		});

	    hp.add(pi);
	    
	    if(!isClosed()){
	    	PaperIconButton pib = new PaperIconButton();
	    	pib.setTitle("Borrar seleccionado");
	    	pib.setStyleName(AON.AON_CSS.aonIconDelete());
	    	pib.addStyleName(AON.AON_CSS.aonIconCommandButton());
	    	pib.addStyleName(AON.AON_CSS.aonMarginLeft());
	    	pib.setNoink(true);
	    	
	    	pib.addClickHandler(new ClickHandler() {
	    		
	    		@Override
	    		public void onClick(ClickEvent event) {
	    			clickRemoveComposition(composition);
	    		}
	    	});
	    	
	    	hp.add(pib);
	    }
	    
	    
	    vp.add(hp);
		vp.add(sp);
	}
	
	
	private Boolean isPending(){
		return getElaboration().getStatus().getId()==new Integer(ElaborationStatus.PENDING.ordinal());
	}
	
	private Boolean isClosed(){
		return getElaboration().getStatus().getId()==new Integer(ElaborationStatus.CLOSED.ordinal());
	}
	
	private Boolean isDetailSelected(){
		return selectedDetail!=null && selectedDetail.getId()!=null;
	}
	
	private Boolean isSelected(String panel){
		return "selected".equals(panel);
	}
	
	private Boolean isSelectable(String panel){
		return "selectable".equals(panel);
	}
	
	
	private void clickAddDetail(){
		VerticalPanel panel = new VerticalPanel();
	
		PaperInput inputNumber = new PaperInput();
		PaperInput inputDate = new PaperInput();
		PaperInput inputQuantity = new PaperInput();
		AonComboBox inputWarehouse = new AonComboBox();
		
		inputNumber.setLabel("N\u00FAmero de lote");
		inputNumber.addChangeHandler(new ChangeEventHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				if(inputNumber.getValue()!=null 
						&& !"".equals(inputNumber.getValue())){
					inputDate.setVisible(true);
					inputDate.setFocused(true);
				} else {
					inputDate.setVisible(false);
					inputQuantity.setFocused(true);
				}
			}
		});
		panel.add(inputNumber);
		
		inputDate.setLabel("Fecha de lote");
		inputDate.setValue(jsElaboration.getDate());
		inputDate.setMaxlength(10);
		inputDate.setVisible(false);
		panel.add(inputDate);

		inputQuantity.setLabel("Cantidad");
		inputQuantity.setValue(jsElaboration.getQuantity()+"");
		inputQuantity.setMaxlength(10);
		inputQuantity.setRequired(true);
		inputQuantity.setErrorMessage("Valor requerido");;
		panel.add(inputQuantity);
	
		inputWarehouse.setLabel("Almac\u00E9n");
		inputWarehouse.setItemLabelPath("name");
		inputWarehouse.setItemValuePath("name");
		API.getWarehouse().getWarehouses(new AsyncCallback<JSON<JsWarehouse>>() {
			
			@Override
			public void onSuccess(JSON<JsWarehouse> result) {
				inputWarehouse.setItems(result.getData());
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		panel.add(inputWarehouse);
		
		AonDialog dialog = new AonDialog("Nuevo elaborado", panel) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {
				DateTimeFormat format = DateTimeFormat.getFormat("yyyy/MM/dd");
				String number = inputNumber.getValue();
//				String date = Utils.formatDateTime(Utils.parseDate(inputDate.getValue()));
				String date = format.parse(inputDate.getValue()).getTime()+"";
				String quantity = inputQuantity.getValue();
				JsWarehouse js = (JsWarehouse) inputWarehouse.getSelectedItem();
				
				String requestData = "{"
						+ (number!=null && !"".equals(number)?"\"number\":\""+ number +"\",":"")
						+ "\"elaboration\":\""+ jsElaboration.getId() +"\","
						+ "\"date\":\""+ date +"\","
						+ "\"quantity\":\""+ quantity +"\","
						+ "\"warehouse\":\"" + js.getId()+ "\"" 
						+ "}";
				
				API.getWarehouse().insertElaborationDetail(requestData, new AsyncCallback<JsElaborationDetail>() {
					
					@Override
					public void onSuccess(JsElaborationDetail result) {
						buildDetailPanel();
						buildCompositionPanel(result);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Ha ocurrido algun error al guardar. \n"+caught.getMessage());
					}
				});
				
				if(number!=null && number.equals(jsElaboration.getQuantity()+"")){
					// TODO closeElaboration()
//					parent.closeElaboration(jsElaboration);
				}
				
				hide();
			}

		};
		dialog.addAutoHidePartner(inputWarehouse.getElementById("overlay"));
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();
	}
	
	private void clickRemoveDetail(JsElaborationDetail detail){		
		if(detail!=null && detail.getId()!=null){
			AonDialog dialog = new AonDialog("Solicitud de confirmaci\u00f3n", new Label("\u00bfBorrar\u003f")) {
				
				@Override protected void onCancel() {hide();}
				
				@Override 
				protected void onAccept() {	
					Integer id = detail.getId();
					API.getWarehouse().deleteElaborationDetail(id, null, new AsyncCallback<JsElaborationDetail>() {
						
						@Override
						public void onSuccess(JsElaborationDetail result) {
							hide();
							selectedDetail = null;
							buildDetailPanel();
							buildCompositionPanel(null);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Ha ocurrido algun error al guardar. \n"+caught.getMessage());
						}
					});
				}
			};
			dialog.setAutoHideEnabled(true);
			dialog.getElement().getStyle().setWidth(310, Unit.PX);
			dialog.center();
		}
	}
	
	private void clickAddComposition(){
		VerticalPanel panel = new VerticalPanel();
	
		AonComboBox inputWarehouse = new AonComboBox();
		ItemBox itemBox = new ItemBox(API);
		PaperInput inputQuantity = new PaperInput();
		
		inputQuantity.setLabel("Cantidad");
		inputQuantity.setValue(selectedDetail.getQuantity()+"");
		inputQuantity.setMaxlength(10);
		inputQuantity.setRequired(true);
		inputQuantity.setErrorMessage("Valor requerido");;
		panel.add(inputQuantity);
		
		panel.add(new Label("Producto"));
		itemBox.setTitle("no seleccionado");
		panel.add(itemBox);
	
		inputWarehouse.setLabel("Almac\u00E9n");
		inputWarehouse.setItemLabelPath("name");
		inputWarehouse.setItemValuePath("name");
		API.getWarehouse().getWarehouses(new AsyncCallback<JSON<JsWarehouse>>() {
			
			@Override
			public void onSuccess(JSON<JsWarehouse> result) {
				inputWarehouse.setItems(result.getData());
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		panel.add(inputWarehouse);
		
		AonDialog dialog = new AonDialog("Nueva composici\u00F3n", panel) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {
				String quantity = inputQuantity.getValue();
				JsWarehouse js = (JsWarehouse) inputWarehouse.getSelectedItem();
				
				Integer detailId = selectedDetail.getId();
				
				String requestData = "{"
						+ "\"elaboration_detail\":\""+ detailId +"\","
						+ "\"quantity\":\""+ quantity +"\","
						+ "\"item\":\""+ itemBox.getId() +"\","
						+ "\"warehouse\":\"" + js.getId()+ "\"" 
						+ "}";
				
				API.getWarehouse().insertElaborationDetailComposition(requestData, new AsyncCallback<JsElaborationDetailComposition>() {
					
					@Override
					public void onSuccess(JsElaborationDetailComposition result) {
						buildCompositionPanel(selectedDetail);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Ha ocurrido algun error al guardar. \n"+caught.getMessage());
					}
				});
				hide();
			}

		};
		dialog.addAutoHidePartner(inputWarehouse.getElementById("overlay"));
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(430, Unit.PX);
		dialog.center();
	}

	private void clickRemoveComposition(JsElaborationDetailComposition composition){		
		if(composition!=null && composition.getId()!=null){
			AonDialog dialog = new AonDialog("Solicitud de confirmaci\u00f3n", new Label("\u00bfBorrar\u003f")) {
				
				@Override protected void onCancel() {hide();}
				
				@Override 
				protected void onAccept() {	
					Integer id = composition.getId();
					API.getWarehouse().deleteElaborationDetailComposition(id, null, new AsyncCallback<JsElaborationDetailComposition>() {
						
						@Override
						public void onSuccess(JsElaborationDetailComposition result) {
							hide();
							buildCompositionPanel(selectedDetail);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Ha ocurrido algun error al guardar. \n"+caught.getMessage());
						}
					});
				}
			};
			dialog.setAutoHideEnabled(true);
			dialog.getElement().getStyle().setWidth(310, Unit.PX);
			dialog.center();
		}		
	}
	
	
}
