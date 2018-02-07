package net.aonsolutions.aon.gwt.warehouse.client.elaboration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.warehouse.JsElaboration;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;
import com.vaadin.polymer.paper.PaperButtonElement;
import com.vaadin.polymer.paper.PaperItemElement;
import com.vaadin.polymer.paper.PaperRadioButtonElement;

import net.aonsolutions.polymer.aon.AonComboBoxElement;

public class Elaboration extends AonTemplate2 {

	protected API API;
	private HashMap<String, LinkedList<String>> filterMap;
	private Elaboration me = this;

//	public Elaboration(AonData aonData, Boolean future) {
//		this(aonData);
//	}

	public Elaboration(AonData aonData) {
		filterMap = new HashMap<>();
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(), aonData.getDomain().getName(),
				aonData.getDomain().getId(), aonData.getUser().getLogin());
	}

	@Override
	public void onModuleLoad() {
		super.onModuleLoad();
		Polymer.importHref(Arrays.asList(IronIconsElement.SRC, AonComboBoxElement.SRC, PaperButtonElement.SRC,
				PaperRadioButtonElement.SRC, PaperItemElement.SRC)
		,o -> {
			load();
			return null;
		});
		Polymer.whenReady(o -> {
			load();
			return null;
		});
	}

	private void load() {
		loadToolbar();
		loadContent();
	}

	private void loadToolbar() {
		getDockLayoutPanel().setWidgetSize(getToolbar(), 23);
		Toolbar toolbar = new Toolbar() {
			
			@Override
			protected void accept() {
				this.download.setVisible(true);
				JsElaboration js = getJsElaboration();
				if(js.getId()!=null){
					updateElaboration(js);
				} else {
					insertElaboration(js);
				}
			}

			@Override
			protected void reset() {
				this.accept.setVisible(true);
				this.back.setVisible(true);
				this.remove.setVisible(false);
				this.reset.setVisible(false);
				this.download.setVisible(false);
				this.setBackwardVisible(false);
				this.setForwardVisible(false);
				this.subtitle.setText("Nuevo");
				resetElaboration();
			}

			@Override
			protected void remove() {
				deleteElaboration(getJsElaboration().getId());
			}

			@Override
			protected void back() {
				this.accept.setVisible(false);
				load();
			}

			@Override
			protected void download() {
				downloadElaboration(getJsElaboration().getId());
			}
			
			// TODO toolbar::backward() {
			@Override
			protected void backward() {
				Integer page = Integer.parseInt(filterMap.get("page").get(0));
				if(page > 1 ){
					page = page - 1;
					LinkedList<String> list = new LinkedList<>();
					list.add(page.toString());
					filterMap.put("page", list);
					list = new LinkedList<>();
					list.add("1");
					filterMap.put("per_page", list);
				
//					API.getWarehouse().getElaboration(filterMap, new AsyncCallback<JSON<JsElaboration>>() {
//					
//						@Override
//						public void onSuccess(JSON<JsElaboration> result) {
//							setContent(new CarrierPackingDetail(me, result.getData().get(0)));
//						}
//						
//						@Override public void onFailure(Throwable caught) {}
//					});
				}
			}

			// TODO toolbar::forward()
			@Override
			protected void forward() {
				Integer page = Integer.parseInt(filterMap.get("page").get(0));
				page = page + 1;
				LinkedList<String> list = new LinkedList<>();
				list.add(page.toString());
				filterMap.put("page", list);
				list = new LinkedList<>();
				list.add("1");
				filterMap.put("per_page", list);
				
//				API.getWarehouse().getElaboration(filterMap, new AsyncCallback<JSON<JsElaboration>>() {
//					
//					@Override
//					public void onSuccess(JSON<JsElaboration> result) {
//						if(result.getData().length() == 0){
//							Integer page = Integer.parseInt(filterMap.get("page").get(0));
//							page = page < 2 ? 1 : page - 1;
//							LinkedList<String> list = new LinkedList<>();
//							list.add(page.toString());
//							filterMap.put("page", list);
//						}
//						setContent(new CarrierPackingDetail(me, result.getData().get(0)));					}
//					
//					@Override public void onFailure(Throwable caught) {}
//				});
			}
		};
		
		toolbar.back.setVisible(false);
		toolbar.accept.setVisible(false);
		toolbar.remove.setVisible(false);
		toolbar.download.setVisible(false);
		toolbar.setBackwardVisible(false);
		toolbar.setForwardVisible(false);
		toolbar.title.setText("Elaboraciones");
		toolbar.subtitle.setText("Lista");
		setToolbar(toolbar);
	}

	public void loadContent() {
		setContent(new MainElaboration(this, filterMap));
	}


	
	public void onSelectElaboration(JsElaboration js) {
		Toolbar toolbar = (Toolbar) getToolbar().getWidget();
		toolbar.accept.setVisible(true);
		toolbar.back.setVisible(true);
		if(!js.getStatus().getName().equals("En progreso")
				&& !js.getStatus().getName().equals("Cerrado")
				) {
			toolbar.remove.setVisible(true);
		}
		toolbar.download.setVisible(true);
		toolbar.subtitle.setText("Edici\u00F3n");
		
		MainElaboration main = (MainElaboration) getContent().getWidget();
		main.contentDockLayoutPanel.setWidgetSize(main.northContent, 120);
		
		main.northContent.setWidget(new ElaborationPanel(me, js));
//		main.content.setWidget(new ElaborationSelect(me, js));
		main.content.setWidget(new SelectionPanel(API, js));
		main.southContent.setWidget(new FooterPanel(main, js));
		
		if(isElaborationCLosed()){
			toolbar.accept.setVisible(false);
			toolbar.remove.setVisible(false);
		}
	}
	
	private void resetElaboration() {
		Toolbar toolbar = (Toolbar) getToolbar().getWidget();
		toolbar.accept.setVisible(true);
		toolbar.back.setVisible(true);
		toolbar.remove.setVisible(false);
		toolbar.download.setVisible(false);
		
		MainElaboration main = (MainElaboration) getContent().getWidget();
		
		main.contentDockLayoutPanel.setWidgetSize(main.northContent, 120);
				
		API.getWarehouse().createElaboration(new AsyncCallback<JSON<JsElaboration>>() {
			
			@Override
			public void onSuccess(JSON<JsElaboration> result) {
				JsElaboration js = result.getOneData();
				ElaborationPanel panel = new ElaborationPanel(me, js);
				main.northContent.setWidget(panel);
				main.content.setWidget(new Label(""));
				main.loadSouthContent();
				main.southContent.setWidget(new FooterPanel(main, js));
				main.contentDockLayoutPanel.setWidgetSize(main.southContent, 30);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Ha ocurrido algun error resetear. \n"+caught.getMessage());
			}
		});
	}
	
	
	protected void closeElaboration(JsElaboration jsElaboration) {
		MainElaboration main = (MainElaboration) getContent().getWidget();
		
		API.getWarehouse().updateElaboration(jsElaboration.getId(), getCloseData(), new AsyncCallback<JsElaboration>() {
			
			@Override
			public void onSuccess(JsElaboration result) {
				ElaborationPanel panel = (ElaborationPanel) main.northContent.getWidget();
				panel.setJsElaboration(result);
				onSelectElaboration(panel.getJsElaboration());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Ha ocurrido algun error al guardar. \n"+caught.getMessage());
			}
		});
	}

	protected void reopenElaboration(JsElaboration jsElaboration) {
		MainElaboration main = (MainElaboration) getContent().getWidget();
		
		API.getWarehouse().updateElaboration(jsElaboration.getId(), getReopenData(), new AsyncCallback<JsElaboration>() {
			
			@Override
			public void onSuccess(JsElaboration result) {
				ElaborationPanel panel = (ElaborationPanel) main.northContent.getWidget();
				panel.setJsElaboration(result);
				onSelectElaboration(panel.getJsElaboration());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Ha ocurrido algun error al guardar. \n"+caught.getMessage());
			}
		});
	}
	
	private void updateElaboration(JsElaboration jsElaboration) {
		MainElaboration main = (MainElaboration) getContent().getWidget();
		
		API.getWarehouse().updateElaboration(jsElaboration.getId(), getData(), new AsyncCallback<JsElaboration>() {

			@Override
			public void onSuccess(JsElaboration result) {
				ElaborationPanel panel = (ElaborationPanel) main.northContent.getWidget();
				panel.setJsElaboration(result);
				onSelectElaboration(panel.getJsElaboration());
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Ha ocurrido algun error al guardar. \n"+caught.getMessage());
			}
		});
	}
	
	private void deleteElaboration(Integer elaboratinId) {
		AonDialog dialog = new AonDialog("Solicitud de confirmaci\u00f3n", new Label("\u00bfBorrar\u003f")) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {	
				API.getWarehouse().deleteElaboration(elaboratinId, getData(), new AsyncCallback<JsElaboration>() {
					
					@Override
					public void onSuccess(JsElaboration result) {
						hide();
						Toolbar toolbar = (Toolbar) getToolbar().getWidget();
						toolbar.back();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Ha ocurrido algun error al eliminar. \n"+caught.getMessage());
					}
				});
			}
		};
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();		
	}
	
	private void insertElaboration(JsElaboration jsElaboration) {
		MainElaboration main = (MainElaboration) getContent().getWidget();
		
		API.getWarehouse().insertElaboration(getData(), new AsyncCallback<JsElaboration>() {

			@Override
			public void onSuccess(JsElaboration result) {
				ElaborationPanel panel = (ElaborationPanel) main.northContent.getWidget();
				panel.setJsElaboration(result);
				onSelectElaboration(panel.getJsElaboration());
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Ha ocurrido algun error al guardar. \n"+caught.getMessage());
			}
		});
	}
	
	protected void downloadElaboration(Integer elaboratinId) {
		API.getWarehouse().downloadElaboration(getJsElaboration().getId());
	}
	
	private String getData() {
//		String data = JsonUtils.stringify(getJsElaboration());
		MainElaboration main = (MainElaboration) getContent().getWidget();
		ElaborationPanel panel = main.getElaborationPanel();
		FooterPanel footer = main.getFooterPanel();
		String description = panel.descriptionLabel.getText();
		description = (description!=null && !"".equals(description)
				? description : panel.itemBox.getDescription());
		String comments = footer!=null?footer.getCommentsValue():""; 
		String remarks = footer!=null?footer.getRemarksValue():""; 
		String data = "{\"series\":\"" + panel.series.getValue()+ "\""
				+ ",\"number\":\"" + panel.number.getValue()+ "\""
				+ ",\"date\":\"" + (panel.date.getValue() != null ? panel.date.getValue().getTime() : "")+ "\""
				+ ",\"quantity\":\"" + panel.quantity.getValue()+ "\""
				+ ",\"item\":\"" + panel.itemBox.getId()+ "\""
				+ ",\"description\":\"" + description + "\""
				+ ",\"warehouse\":\"" + panel.warehouse.getSelectedValue()+ "\""
				+ ",\"comments\":\"" + (comments!=null && !"".equals(comments.trim())?comments:"") + "\""
				+ ",\"remarks\":\"" + (remarks!=null && !"".equals(remarks.trim())?remarks:"") + "\""
				+ "}";
		return data;
	}
	
	private String getCloseData() {
		String data = "{\"status\":\"CLOSED\"}";
		return data;
	}

	private String getReopenData() {
		String data = "{\"status\":\"PENDING\"}";
		return data;
	}
	
	
	public HashMap<String, LinkedList<String>> getFilterMap() {
		return filterMap;
	}

	public void setFilterMap(HashMap<String, LinkedList<String>> filterMap) {
		this.filterMap = filterMap;
	}

	public void refreshSelect() {
		 ElaborationSelect cps = (ElaborationSelect)
		 getContent().getWidget();
		 cps.refresh();
	}

	public API getAPI() {
		return API;
	}
	
	public JsElaboration getJsElaboration(){ 
		MainElaboration main = (MainElaboration) getContent().getWidget();
		return main.getJsElaboration();
	}
	
	public void setJsElaboration(JsElaboration js){ 
		MainElaboration main = (MainElaboration) getContent().getWidget();
		main.setJsElaboration(js);
	}
	
	protected boolean isElaborationCLosed(){
		MainElaboration main = (MainElaboration) getContent().getWidget();
		return main.isElaborationCLosed();
	}
	
}
