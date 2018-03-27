package com.esferalia.aon.gwt.fiscal.client.matrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.common.JsDataResponse;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.client.CertificationPopup;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class NeoMatrix extends DockLayoutPanel {

	final FiscalServiceAsync impl = GWT.create(FiscalService.class);
	
	private API API;
	private HashMap<Integer, IFiscalModel> modelMap = new HashMap<>();
	private HashMap<Integer, JSONObject> errors = new HashMap<>();
	private Integer eastSelected = 0;

	private Boolean minimize = true;

	private SplitLayoutPanel split = new SplitLayoutPanel();
	private SimplePanel consolePanel = new SimplePanel();
	private VerticalPanel modelListPanel = new VerticalPanel();
	
	private Domain domain;
	private String user;
	
	private HashMap<Integer, IFiscalModel> getModelMap() {
		return modelMap;
	}
	
	private Boolean isMinimize() {
		return minimize;
	}
	
	private API getAPI() {
		return API;
	}

	public NeoMatrix(AonData aonData) {
		super(Unit.PX);
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
				aonData.getDomain().getName(), aonData.getDomain().getId(),
				aonData.getUser().getLogin());
		this.domain = aonData.getDomain();
		this.user = aonData.getUser().getLogin();
		
		addNorth(functionsPanel(), 30);
		
		split = new SplitLayoutPanel();
		consolePanel.addStyleName(AON.AON_CSS.aonSelector());
		split.addEast(consolePanel, 0);
		modelListPanel.setWidth("100%");
		ScrollPanel sp = new ScrollPanel();
		sp.add(modelListPanel);
		split.add(sp);
		add(split);
	}
	
	public NeoMatrix(API API) {
		super(Unit.PX);
		this.API = API;
		addNorth(functionsPanel(), 30);
		addEast(modelInfoPanel(), 0);
		add(modelListPanel);
	}
	
	protected Boolean hasModel(IFiscalModel model) {
		return modelMap.containsKey(model.getId());
	}
	
	protected void addModel(IFiscalModel model) {
		getModelMap().put(model.getId(), model);
		modelListPanel.add(item(model));
	}

	protected void removeModel(IFiscalModel model) {
		getModelMap().remove(model.getId());
		errors.remove(model.getId());
		if(model.getId().equals(eastSelected)) {
			split.setWidgetSize(consolePanel, 0);	
			split.animate(500);
			consolePanel.remove(consolePanel.getWidget());
		}
		for(Integer i = 0 ; i < modelListPanel.getWidgetCount(); i++) {
			if(modelListPanel.getWidget(i).getTitle().equals(model.getId().toString())) {
				modelListPanel.remove(i);
			}
		}
	}
	
	protected void clean() {
		modelMap = new HashMap<>();
		modelListPanel = new VerticalPanel();
		consolePanel = new SimplePanel();
		split.removeFromParent();

		split = new SplitLayoutPanel();
		consolePanel.addStyleName(AON.AON_CSS.aonSelector());
		split.addEast(consolePanel, 0);
		modelListPanel.setWidth("100%");
		ScrollPanel sp = new ScrollPanel();
		sp.add(modelListPanel);
		split.add(sp);
		add(split);
		
		split.setWidgetSize(consolePanel, 0);	
		split.animate(500);
		onClosePanel();
	}
	
	private FlowPanel functionsPanel() {
		FlowPanel fp = new FlowPanel();
		if(isMinimize()) {
			Button maximizeButton = new Button();
			maximizeButton.setStyleName(AON.AON_CSS.aonIconCommandButton());
			maximizeButton.addStyleName(AON.AON_CSS.aonIconMaximize()); 
			maximizeButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					onOpenPanel();
				}
			});
			fp.add(maximizeButton);
			
			Button minimizeButton = new Button();
			minimizeButton.setStyleName(AON.AON_CSS.aonIconCommandButton());
			minimizeButton.addStyleName(AON.AON_CSS.aonIconMinimize()); 
			minimizeButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					onClosePanel();
				}
			});
			fp.add(minimizeButton);
		}
		Button aeatButton = new Button();
		aeatButton.setTitle("Presentaci\u00f3n Telem\u00e1tica");
		aeatButton.setStyleName(AON.AON_CSS.aonIconCommandButton());
		aeatButton.addStyleName(AON.AON_CSS.aonIconAeat());
		aeatButton.getElement().getStyle().setFloat(Float.RIGHT);
		aeatButton.getElement().getStyle().setMarginRight(16, Unit.PX);
		aeatButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				CertificationPopup certPopup = new CertificationPopup(getAPI(), false) {
					
					@Override
					protected void onCancel() {
					
					}
							
					@Override
					protected void onAccept() {		
						send(0, getCert(), getPass());
					}
				};
				certPopup.center();			
			}
		});
		fp.add(aeatButton);
		
		Button finishButton = new Button();
		finishButton.setTitle("Finalizar Modelo");
		finishButton.setStyleName(AON.AON_CSS.aonIconCommandButton());
		finishButton.addStyleName(AON.AON_CSS.aonIconPointLightGreen());
		finishButton.getElement().getStyle().setFloat(Float.RIGHT);
		finishButton.getElement().getStyle().setMarginRight(16, Unit.PX);
		finishButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				markAsFinished(0);
			}
		});
		fp.add(finishButton);
		
		Button cleanButton = new Button();
		cleanButton.setTitle("Limpiar Selecci\u00f3n");
		cleanButton.setStyleName(AON.AON_CSS.aonIconCommandButton());
		cleanButton.addStyleName("aon-icon-rubber");
		cleanButton.getElement().getStyle().setFloat(Float.RIGHT);
		cleanButton.getElement().getStyle().setMarginRight(16, Unit.PX);
		cleanButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				clean();
			}
		});
		fp.add(cleanButton);
		
		return fp;
	}
	
	private Label modelInfoPanel() {
		return new Label("Model Info"); 
	}
	
	private FocusPanel item(IFiscalModel model) {
		FocusPanel wrapper = new FocusPanel();
		wrapper.setTitle(model.getId().toString());
		HorizontalPanel hp = new HorizontalPanel();
		hp.getElement().getStyle().setPaddingLeft(10, Unit.PX);
		hp.getElement().getStyle().setPaddingTop(2.5, Unit.PX);
		hp.getElement().getStyle().setPaddingBottom(2.5, Unit.PX);
		
		Label icon = new Label();
		icon.setStyleName(FiscalModelUtils.getAdministrationIcon(model.getAdministration()));
		icon.getElement().getStyle().setPaddingLeft(20, Unit.PX);
		
		Label l0 = new Label("Modelo " + model.getModel().getName()); 
		
		Label l1 = new Label(model.getPeriod().getDescription()); 
		l1.getElement().getStyle().setPaddingLeft(20, Unit.PX);
		
		Label l2 = new Label( model.getFullName()); 
		l2.getElement().getStyle().setPaddingLeft(20, Unit.PX);
		
		hp.add(icon);
		hp.add(l0);
		hp.add(l1);
		hp.add(l2);
		
		wrapper.add(hp);
		wrapper.addClickHandler(new ClickHandler() {
		  @Override
		  public void onClick(ClickEvent event) {
		    for(Integer i = 0 ; i < modelListPanel.getWidgetCount(); i ++) {
				modelListPanel.getWidget(i).getElement().getStyle().setBackgroundColor("#fff");
			}
			wrapper.getElement().getStyle().setBackgroundColor("#eee");
			east(model);
			Integer clientWidth = Window.getClientWidth();
			eastSelected = model.getId();
			split.setWidgetSize(consolePanel, clientWidth.doubleValue() / 2);	
			split.animate(500);
		  }
		});
	
		return wrapper;		
	}
	
	private void markAsFinished(Integer i) {
		if(i < modelListPanel.getWidgetCount()) {
			Integer id = Integer.parseInt(modelListPanel.getWidget(i).getTitle());
			IFiscalModel model = modelMap.get(id);
			JSONObject js = new JSONObject();
			if(hasFinishOption(model)) {
				impl.markAsFinished(domain.getName(), domain.getId(), user, model, new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void v) {
						// TODO
						resultFocus(i, "green");
						markAsFinished(i + 1);
					}

					@Override
					public void onFailure(Throwable caught) {
						resultFocus(i, "red");
						js.put("E00", new JSONString("Ha ocurrido un error inesperado."));
						errors.put(model.getId(), js);
						markAsFinished(i + 1);
					}
				});
			} else {
				resultFocus(i, "red");
				js.put("E00", new JSONString("La funcionalidad no est\u00e1 disponible para este modelo."));
				errors.put(model.getId(), js);
				markAsFinished(i + 1);
			}
		} else onParentLoad();
	}
	
	private void send(Integer i, String cert, String pass) {
		if(i < modelListPanel.getWidgetCount()) {
			Integer id = Integer.parseInt(modelListPanel.getWidget(i).getTitle());
			IFiscalModel model = modelMap.get(id);

			if(model.isAEAT() && model.isFinished() && hasSendOption(model)) {
				JSONObject json = new JSONObject();
				json.put("mod", new JSONNumber(model.getId()));
				json.put("domainId", new JSONNumber(domain.getId()));
				json.put("domainName", new JSONString(domain.getName()));
				json.put("user", new JSONString(user));
				json.put("cert", new JSONNumber(Integer.parseInt(cert)));
				json.put("pass", new JSONString(pass));
				json.put("name", new JSONString(model.getFullName()));
				json.put("document", new JSONString(model.getDocument()));
				String requestData = JsonUtils.stringify(json.getJavaScriptObject());
				getAPI().getFiscal().send2AEAT(GWT.getHostPageBaseURL() + getSendPath(model), 
					requestData, new AsyncCallback<JavaScriptObject>() {
			
					@Override
					public void onSuccess(JavaScriptObject result) {
						JSONObject js = new JSONObject(result);
						resultFocus(i, js.containsKey("CEL") ? "green" : "red");
						if(!js.containsKey("CEL")) {
							errors.put(model.getId(), js);
						} else errors.remove(model.getId());
						send(i+1, cert, pass);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						resultFocus(i, "red");
						send(i+1, cert, pass);
					}			
				});
			} else {
				resultFocus(i, "red");
				JSONObject js = new JSONObject();
				Integer error = 0;
				if(!model.isFinished()) {
					js.put("E0" + error, new JSONString("El modelo no est\u00e1 finalizado."));
					error++;
				}
				if(!model.isAEAT()) {
					js.put("E0" + error, new JSONString("La presentaci\u00f3n telem\u00e1tica solo es compatible para los modelos de Territorio Com\u00fan."));
					error++;
				}
				if(!hasSendOption(model)) {
					js.put("E0" + error, new JSONString("La funcionalidad no est\u00e1 disponible para este modelo."));	
					error++;
				}
				errors.put(model.getId(), js);
				send(i+1, cert, pass);
			}
		}
	}
	
	private void resultFocus(Integer i, String color){
		FocusPanel fp = (FocusPanel) modelListPanel.getWidget(i);
		HorizontalPanel hp = (HorizontalPanel) fp.getWidget();
		hp.getWidget(1).getElement().getStyle().setColor(color);
		hp.getWidget(1).getElement().getStyle().setFontWeight(FontWeight.BOLD);
		hp.getWidget(2).getElement().getStyle().setColor(color);
		hp.getWidget(2).getElement().getStyle().setFontWeight(FontWeight.BOLD);
		hp.getWidget(3).getElement().getStyle().setColor(color);
		hp.getWidget(3).getElement().getStyle().setFontWeight(FontWeight.BOLD);
	}
	
	private Boolean hasSendOption(IFiscalModel model) {
		return FiscalModelType.M111.equals(model.getModel())
			|| FiscalModelType.M115.equals(model.getModel())
			|| FiscalModelType.M123.equals(model.getModel())
			|| FiscalModelType.M303.equals(model.getModel())
			|| FiscalModelType.M303_RG.equals(model.getModel())
			|| FiscalModelType.M303_RS.equals(model.getModel())
			|| FiscalModelType.M130.equals(model.getModel())
			|| FiscalModelType.M131.equals(model.getModel())
			|| FiscalModelType.M390.equals(model.getModel());
	}
	
	private Boolean hasFinishOption(IFiscalModel model) {
		return FiscalModelType.M111.equals(model.getModel())
			|| FiscalModelType.M115.equals(model.getModel())
			|| FiscalModelType.M123.equals(model.getModel())
			|| FiscalModelType.M130.equals(model.getModel())
			|| FiscalModelType.M131.equals(model.getModel())
			|| FiscalModelType.M180.equals(model.getModel())
			|| FiscalModelType.M184.equals(model.getModel())
			|| FiscalModelType.M190.equals(model.getModel())
			|| FiscalModelType.M193.equals(model.getModel())
			|| FiscalModelType.M202.equals(model.getModel())
			|| FiscalModelType.M303.equals(model.getModel())
			|| FiscalModelType.M303_RG.equals(model.getModel())
			|| FiscalModelType.M303_RS.equals(model.getModel())
			|| FiscalModelType.M347.equals(model.getModel())
			|| FiscalModelType.M349.equals(model.getModel())
			|| FiscalModelType.M390.equals(model.getModel())
			|| FiscalModelType.M390_HF.equals(model.getModel());
	}
	
	private String getSendPath(IFiscalModel model){
		if(FiscalModelType.M111.equals(model.getModel()))
			return "/aon_gwt_fiscal/ms/Model111PrintAEAT";
		else if(FiscalModelType.M115.equals(model.getModel()))
			return "/aon_gwt_fiscal/ms/Model115PrintAEAT";
		else if(FiscalModelType.M123.equals(model.getModel()))
			return "/aon_gwt_fiscal/ms/Model123PrintAEAT";
		else if(FiscalModelType.M130.equals(model.getModel()))
			return "/aon_gwt_fiscal/ms/Model130PrintAEAT";
		else if(FiscalModelType.M131.equals(model.getModel()))
			return "/aon_gwt_fiscal/ms/Model131PrintAEAT";
		else if(FiscalModelType.M303.equals(model.getModel())
				|| FiscalModelType.M303_RG.equals(model.getModel())
				|| FiscalModelType.M303_RS.equals(model.getModel()))
			return "/aon_gwt_fiscal/ms/Model303PrintAEAT";
		else if(FiscalModelType.M390.equals(model.getModel()))
			return "/aon_gwt_fiscal/ms/Model390PrintAEAT";
		return "";
	}
	
	private void east(IFiscalModel model) {
		TabLayoutPanel tabPanel = new TabLayoutPanel(2.5, Unit.EM);
	    tabPanel.setAnimationDuration(1000);
	    tabPanel.getElement().getStyle().setMarginBottom(10.0, Unit.PX);

	    Boolean error = errors.containsKey(model.getId());
	
	    String[] withError = {"Errores","Historial"};
	    String[] withoutError = {"Historial"};

	    String[] tabTitles = error ? withError : withoutError;
	
	    // Add a Errors tab
	    if(error) {
	    	
	    	JSONObject js = errors.get(model.getId());
	    	Integer i = 0; 
	    	ArrayList<String> arr = new ArrayList<>();
	    	while(js.containsKey("E" + (i > 9 ? i : "0" + i))) {
	    		new Label(js.get("E" + (i > 9 ? i : "0" + i)).toString());
	    		arr.add(js.get("E" + (i > 9 ? i : "0" + i)).toString());
	    		i++;
	    	}		
	    	tabPanel.add(showErrorsPanel(arr), tabTitles[0]);
	    }

	    // Add a Errors tab
	    tabPanel.add(showHistoryPanel(model), error ? tabTitles[1] : tabTitles[0]);

	    // Return the content
	    tabPanel.selectTab(0);
	
		consolePanel.setWidget(tabPanel);
	}
	
	private Widget historyItem(IFiscalModel model, JsDataResponse dataResponse) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.getElement().getStyle().setPaddingLeft(10, Unit.PX);
		hp.getElement().getStyle().setPaddingTop(2.5, Unit.PX);
		hp.getElement().getStyle().setPaddingBottom(2.5, Unit.PX);

		Label icon = new InlineLabel("");		
		icon.setStyleName(dataResponse.getCode().contains("Fallida") ?
				"aon-icon-point-red" : AON.AON_CSS.aonIconPointGreen());
		icon.getElement().getStyle().setPaddingLeft(20, Unit.PX);
		
		Label l0 = new Label(AonDateUtils.formatDate(AonDateUtils.parseDateTime(dataResponse.getDate()))); 
		
		Label l1 = new Label(dataResponse.getCode()); 
		l1.getElement().getStyle().setPaddingLeft(20, Unit.PX);
		l1.getElement().getStyle().setPaddingRight(20, Unit.PX);
		
		hp.add(icon);
		hp.add(l0);
		hp.add(l1);
		
		impl.presentationFile(domain.getName(), domain.getId(), user, model.getModel(), model.getId(), new AsyncCallback<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				if(result > 0) {
					Button download = new Button();
					download.setStyleName("aon-icon-mail-save");
					download.addStyleName(AON.AON_CSS.aonIconCommandButton());
					download.getElement().getStyle().setPaddingTop(16, Unit.PX);
					
					download.addClickHandler(new ClickHandler() {
				
						@Override
						public void onClick(ClickEvent event) {
							getAPI().getFiscal().download(result +"");
						}
					});
					hp.add(download);
				}
			}
			
			@Override public void onFailure(Throwable caught) {}
		
		});
	
		return hp;		
	}
	
	private ScrollPanel showErrorsPanel(ArrayList<String> msg) {
		ScrollPanel panel = new ScrollPanel();
		FlexTable tab = new FlexTable();
		tab.setWidth("95%");
		tab.setStyleName(AON.AON_CSS.aonBlockCenter());
		tab.addStyleName(AON.AON_CSS.aonMarginBottom());
		tab.addStyleName(AON.AON_CSS.aonMarginTop());
		tab.getColumnFormatter().setWidth(0, "20px");
		tab.getColumnFormatter().setWidth(1, "auto");

		for(Integer i = 0 ; i < msg.size(); i++) {
			InlineLabel icon = new InlineLabel("");
			icon.setStyleName(AON.AON_CSS.aonIconPointRed());
			icon.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
			icon.getElement().getStyle().setBorderWidth(0, Unit.PX);
			tab.setWidget(i, 0, icon);
			tab.getCellFormatter().setStyleName(i, 0, AON.AON_CSS.aonPanelGridEven());
			
			InlineLabel label = new InlineLabel(msg.get(i));
			label.addStyleName(AON.AON_CSS.aonColorRed());
			label.addStyleName(AON.AON_CSS.aonBold());
			label.getElement().getStyle().setBorderWidth(0, Unit.PX);
			tab.setWidget(i, 1, label);
			tab.getCellFormatter().setStyleName(i, 1, AON.AON_CSS.aonPanelGridEven());
		}
		panel.add(tab);
		return panel;
	}
	

	private ScrollPanel showHistoryPanel(IFiscalModel model) {
		ScrollPanel panel = new ScrollPanel();
		HashMap<String, LinkedList<String>> map = new HashMap<>();
		LinkedList<String> list = new LinkedList<>();
		list.add("filter2");
		map.put("filter2",list);
		list = new LinkedList<>(); 
		list.add(getDataResponseSource(model.getModel()));
		map.put("source", list);
		list = new LinkedList<>(); 
		list.add(model.getId().toString()); 
		map.put("source_id", list);
		getAPI().getCommon().getDataResponse(map,new AsyncCallback<JSON<JsDataResponse>>() {
			
			@Override
			public void onSuccess(JSON<JsDataResponse> arg0) {
				VerticalPanel vp = new VerticalPanel();
				arg0.getData().stream().forEach(r -> {
					vp.add(historyItem(model,r));
				});
				panel.add(vp.getWidgetCount() > 0 ? vp : new Label("No hay datos relacionados con este modelo."));
			}
			
			@Override
			public void onFailure(Throwable arg0) {
						
			}
		});
		return panel;
	}
	
	private String getDataResponseSource(FiscalModelType type) {
		if(FiscalModelType.M111.equals(type)) {
			return DataResponseSource.MOD111.value() + "";
		} else if(FiscalModelType.M115.equals(type)) {
			return DataResponseSource.MOD115.value() + "";
		} else if(FiscalModelType.M123.equals(type)) {
			return DataResponseSource.MOD123.value() + "";
		} else if(FiscalModelType.M130.equals(type)) {
			return DataResponseSource.MOD130.value() + "";
		} else if(FiscalModelType.M131.equals(type)) {
			return DataResponseSource.MOD131.value() + "";
		} else if(FiscalModelType.M303.equals(type) ||
				FiscalModelType.M303_RG.equals(type) ||
				FiscalModelType.M303_RS.equals(type)) {
			return DataResponseSource.MOD303.value() + "";
		} else if(FiscalModelType.M390.equals(type)) {
			return DataResponseSource.MOD390.value() + "";
		} 
		return "";
	}
	
	protected abstract void onOpenPanel();
	protected abstract void onClosePanel();
	protected abstract void onParentLoad();
}
