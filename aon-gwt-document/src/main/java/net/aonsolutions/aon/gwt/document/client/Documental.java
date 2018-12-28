package net.aonsolutions.aon.gwt.document.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.documental.Attachment;
import com.esferalia.aon.gwt.api.client.documental.JsAttach;
import com.esferalia.aon.gwt.api.client.incidence.JsLabel;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.polymer.AonToolbar;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.issues.client.css.AonGwtIssuesResources;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronCollapseElement;
import com.vaadin.polymer.iron.IronIconsElement;
import com.vaadin.polymer.iron.IronListElement;
import com.vaadin.polymer.paper.PaperCheckboxElement;
import com.vaadin.polymer.paper.PaperDialogElement;
import com.vaadin.polymer.paper.PaperIconButtonElement;
import com.vaadin.polymer.paper.PaperInputElement;
import com.vaadin.polymer.paper.PaperSliderElement;
import com.vaadin.polymer.paper.PaperTextareaElement;
import com.vaadin.polymer.paper.PaperToggleButtonElement;
import com.vaadin.polymer.paper.widget.PaperIconButton;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperToggleButton;
import com.vaadin.polymer.vaadin.VaadinComboBoxElement;
import com.vaadin.polymer.vaadin.widget.VaadinUpload;

import net.aonsolutions.polymer.aon.AonComboBoxElement;
import net.aonsolutions.polymer.aon.AonIconsElement;
import net.aonsolutions.polymer.aon.widget.AonComboBox;
import net.aonsolutions.polymer.aon.widget.event.ValueChangedEvent;
import net.aonsolutions.polymer.aon.widget.event.ValueChangedEventHandler;

public class Documental implements EntryPoint {
	
	interface Binder extends UiBinder<Widget, Documental> {

	}
	private static final Binder binder = GWT.create(Binder.class);
	@UiField DockLayoutPanel dockLayoutPanel;
	
	@UiField HTMLPanel toolbar;
	@UiField HTMLPanel configurationPanel;
	@UiField DockLayoutPanel contentDockLayoutPanel;
	@UiField HTMLPanel searchContent;
	@UiField SimpleLayoutPanel content;
	
	final IDocumentalAsync idoc = GWT.create(IDocumental.class);

	private API API;
	private AonData aonData;
	private Attachment attachment;
	private Documental me = this;;
	HashMap<String, LinkedList<String>> filterMap;
	LinkedList<String> selectedAttach = new LinkedList<>(); 
	Boolean more = true;
	
	public API getAPI() {
		return API;
	}
	
	public HashMap<String, LinkedList<String>> getFilterMap() {
		return filterMap;
	}
	
	public void setFilterMap(HashMap<String, LinkedList<String>> filterMap) {
		this.filterMap = filterMap;
	}
	
	public LinkedList<String> getSelectedAttach() {
		return selectedAttach;
	}
	
	public IDocumentalAsync getIDoc() {
		return idoc;
	}
	
	public void setSelectedAttach(LinkedList<String> selectedAttach) {
		this.selectedAttach = selectedAttach;
	}

	public AonData getAonData() {
		
		return aonData;
	}
	
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	public Documental(AonData aonData) {
		this.aonData = aonData;
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
				aonData.getDomain().getName(), aonData.getDomain().getId(),
				aonData.getUser().getLogin());
	}
	
	/*
	 * Operaciones por ficehero
	 *  Editar - Ambito | Categoria | Etiquetas | Fecha | Confidencia? - icon = create
	 *  Borrar - preguntar para borrar definitivamente - icon = delete
	 *  Compartir - Lote | Drive 
	 *  Descargar  icon = file-download
	 *  Informacion
	 */
	
	@Override
	public void onModuleLoad() {
		Polymer.importHref(Arrays.asList(
				IronIconsElement.SRC,
				IronCollapseElement.SRC,
				PaperInputElement.SRC,
				PaperTextareaElement.SRC,
				PaperDialogElement.SRC,
				VaadinComboBoxElement.SRC,
				PaperIconButtonElement.SRC,
				IronListElement.SRC,
				PaperToggleButtonElement.SRC,
				PaperSliderElement.SRC,
				PaperCheckboxElement.SRC,
				AonComboBoxElement.SRC,
				AonIconsElement.SRC,
				"aon-icons/aon-documental-icons.html",
				"aon-icons/aon-icons.html",
				"iron-icons/maps-icons.html"
		));
		
		Polymer.whenReady(o -> {
			startApplication();
			return null;
		});
	}
	
	private void startApplication() {
		GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css().ensureInjected();
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
		initializeFilterMap();
		createAonToolbar();
		createSearchPanel();
		createAttachListPanel();
	}
	
	public void initializeFilterMap() {
		filterMap = new HashMap<>();
		LinkedList<String> list = new LinkedList<>();
		list.add("all");
		filterMap.put("type", list);
	}
	
	private void createAonToolbar(){
		toolbar.add(new AonToolbar("Documental") {

			@Override protected void onMenuButtonClick() {
				if(dockLayoutPanel.getWidgetSize(configurationPanel) == 0){
					configurationPanel.add(new ConfigurationPanel(me));
					dockLayoutPanel.setWidgetSize(configurationPanel, 350);
				}
				else {
					configurationPanel.remove(0);
					dockLayoutPanel.setWidgetSize(configurationPanel, 0);
				}	
			}
			
			@Override protected void onRefreshButtonClick() {}
			@Override protected void onMoreOptionButtonClick() {}
			@Override protected void onEditButtonClick() {
				editSelectedDocument();
			}
			@Override protected void onDeleteButtonClick() {
				deleteSelectedDocuments();
			}
			@Override protected void onAddButtonClick() {
				addFileClick();
			}
			@Override protected void onInfoButtonClick() {}
			@Override protected void onStatsButtonClick() {}
			@Override protected void onFastFilterButtonClick() {}
			@Override protected void onTitleClick() {}
			@Override protected void onDownloadButtonClick() {
				downloadSelectedDocument();
			}
			@Override protected void onSendButtonClick() {
				sendSelectedDocuments();
			}
			
		}
		.setVisibleSendButton(false)
		.setVisibleEditButton(false)
		.setVisibleDeleteButton(false)
		.setVisibleMoreOptionButton(false)
		.setVisibleInfoButton(false)
		.setVisibleStatsButton(false));
	}
	
	public void activeMultiselectionFunctions(Integer size) {
		AonToolbar t = (AonToolbar) toolbar.getWidget(0);
		t.setVisibleSendButton(size> 0);
		t.setVisibleDownloadButton(size == 1);
		if(getAonData().getUser().hasDocumentManagerRole()) {
			t.setVisibleEditButton(size == 1);
			t.setVisibleDeleteButton(size > 0);
		}
	}
	
	void addFileClick() {
		AonComboBox categoryBox = new AonComboBox();
		categoryBox.setLabel("Categor\u00eda");
		categoryBox.setWidth("100%");
		categoryBox.setItemLabelPath("name");
		categoryBox.setItemValuePath("name");
		getAPI().getAttachment().getCategories(new AsyncCallback<JSON<JsLabel>>() {
			
			@Override
			public void onSuccess(JSON<JsLabel> result) {
				categoryBox.setItems(result.getData());
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		
		AonComboBox tagBox = new AonComboBox();
		tagBox.setWidth("100%");
		tagBox.setLabel("Etiqueta");
		tagBox.setItemLabelPath("name");
		tagBox.setItemValuePath("name");
		getAPI().getAttachment().getTags(new AsyncCallback<JSON<JsLabel>>() {
			
			@Override
			public void onSuccess(JSON<JsLabel> result) {
				tagBox.setItems(result.getData());
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		
		HorizontalPanel tagPanel = new HorizontalPanel();
		
		tagBox.addValueChangedHandler(new ValueChangedEventHandler() {
			
			@Override
			public void onValueChanged(ValueChangedEvent event) {
				JsLabel tag = tagBox.getSelectedItem().cast();
				Boolean addTag = true;
				for(Integer i = 0 ; i < tagList.size(); i++) {
					if(tagList.get(i).getName().equals(tag.getName())) {
						addTag = false;
					}
				}
				if(addTag) {
					tagList.add(tag);
					tagPanel.add(buildTagPanel(tag));
				}
			}
		});
		
		AonComboBox scopeBox = new AonComboBox();
		scopeBox.setWidth("100%");
		scopeBox.setLabel("\u00c1mbito");
		scopeBox.setItemLabelPath("name");
		scopeBox.setItemValuePath("name");
		getAPI().getAttachment().getScopes(new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> result) {
				scopeBox.setItems(result.getData());
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		
		HorizontalPanel hp = new HorizontalPanel();
		Label confidentialLabel = new Label("Confidencial");
		confidentialLabel.getElement().getStyle().setPaddingTop(20, Unit.PX);
		confidentialLabel.getElement().getStyle().setPaddingRight(10, Unit.PX);
		confidentialLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);		
		hp.add(confidentialLabel);
				
		PaperToggleButton confidential = new PaperToggleButton();
		confidential.getElement().getStyle().setPaddingTop(13, Unit.PX);
		hp.add(confidential);
		
		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("100%");
		vp.add(categoryBox);
		vp.add(tagBox);
		vp.add(tagPanel);
		vp.add(scopeBox);
		vp.add(hp);
		AonDialog dialog = new AonDialog("Nuevo Archivo", vp) {
			
			@Override protected void onCancel() {hide();}
			
			@Override
			protected void onAccept() {
				JsObject category = (JsObject) categoryBox.getSelectedItem();
				ids = "";
				for(Integer i = 0 ; i< tagList.size(); i++) {
					if(!"".equals(ids)) ids = ids + ","; 
					ids = ids + tagList.get(i).getId();
				}
				JsObject scope = (JsObject) scopeBox.getSelectedItem();
				for(Integer i = vp.getWidgetCount() - 1 ; i >= 0; i--){
					vp.getWidget(i).removeFromParent();
				}
				VaadinUpload upload = new VaadinUpload();
				String dataRequest = "?domain_name="+ aonData.getDomain().getName() 
						+ "&domain_id="+ aonData.getDomain().getId()
						+ "&login="+ "system"
						+ "&category="+ (category != null ? category.getId() : "")
						+ "&tag=[" + ids +"]"
						+ "&scope=" + (scope != null ? scope.getId() : "")
						+ "&confidential=" + confidential.getChecked();
				upload.setTarget(GWT.getModuleBaseURL() + "uploadDocumentalx"+ dataRequest);
				ScrollPanel scroll = new ScrollPanel();
				scroll.setHeight("300px");
				scroll.add(upload);
				this.getAccept().setVisible(false);
				this.getCancel().setVisible(false);
				this.getClose().setVisible(true);
				this.getClose().addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						createAttachListPanel();
					}
				});
				vp.add(scroll);
			}
		};
		dialog.setAutoHideEnabled(true);
		dialog.addAutoHidePartner(categoryBox.getElementById("overlay"));
		dialog.addAutoHidePartner(tagBox.getElementById("overlay"));
		dialog.addAutoHidePartner(scopeBox.getElementById("overlay"));
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();
	}
	
	LinkedList<JsLabel> tagList = new LinkedList<>();

	private HorizontalPanel buildTagPanel(JsLabel tag) {
		HorizontalPanel labelPanel = new HorizontalPanel();
		labelPanel.getElement().getStyle().setPaddingLeft(5, Unit.PX);
		Label label = new Label(tag.getName());
		label.getElement().getStyle().setPadding(3, Unit.PX);
		label.getElement().getStyle().setBackgroundColor("#ddd");
		PaperIconButton icon = new PaperIconButton();
		icon.setIcon("close");
		icon.getElement().getStyle().setWidth(16, Unit.PX);
		icon.getElement().getStyle().setHeight(16, Unit.PX);
		icon.getElement().getStyle().setMargin(0, Unit.PX);
		icon.getElement().getStyle().setPadding(0, Unit.PX);
		icon.getElement().getStyle().setPaddingTop(4, Unit.PX);
		icon.setNoink(true);
		icon.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				tagList.remove(tag);
				labelPanel.removeFromParent();
			}
		});
		labelPanel.add(label);
		labelPanel.add(icon);
		return labelPanel;
	}
	
	private void createSearchPanel(){
		searchContent.add(new FilterPanel(me));
	}
	
	public void createAttachListPanel() {
		LinkedList<String> list = new LinkedList<>();
		list.add("1");
		getFilterMap().put("page", list);
		list = new LinkedList<>();
		list.add("30");
		getFilterMap().put("per_page", list);
		getAPI().getAttachment().getAttachList(getFilterMap(), new AsyncCallback<JSON<JsAttach>>() {
				
			@Override
			public void onSuccess(JSON<JsAttach> result) {
				more = result.getData().length() >= 30;
				content.setWidget(new AttachListPanel(me, result.getData()));
			}
				
			@Override public void onFailure(Throwable caught) {}
		});   
	}
	
	public void updateAttachListPanel() {
		getAPI().getAttachment().getAttachList(getFilterMap(), new AsyncCallback<JSON<JsAttach>>() {
				
			@Override
			public void onSuccess(JSON<JsAttach> result) {
				more = result.getData().length() >= 30;
				AttachListPanel attachList = (AttachListPanel) content.getWidget();
				attachList.updateItems(attachList.getItems().concat(result.getData()).cast());
			}
				
			@Override public void onFailure(Throwable caught) {}
		});   
	}
	
	public void remove() {
		dockLayoutPanel.removeFromParent();
	}
	
	String ids = "" ;
	String ids2 = "" ;

	private void deleteSelectedDocuments() {	
		AonDialog d = new AonDialog("Borrar Documentos",new Label("Est\u00e1s seguro de Borrar definitivamente los ficheros seleccionados") ) {
			
			@Override protected void onCancel() {hide();}
			
			@Override
			protected void onAccept() {
				ids = "" ;
				getSelectedAttach().stream().forEach(r -> {
					if(!ids.equals("")) {
						ids = ids + ",";
					}
					ids = ids + r;
				});
				String requestData = "{\"id\":[" + ids + "],"
						+ "\"attach_type\":\"registry\"}";
				getAPI().getAttachment().removeAttach(requestData, new AsyncCallback<JSON<JsAttach>>() {
					
					@Override
					public void onSuccess(JSON<JsAttach> result) {
						setSelectedAttach(new LinkedList<>());
						createAttachListPanel();
						hide();
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		};
		d.getElement().getStyle().setWidth(255, Unit.PX);
		d.center();
		
	}

	private void editSelectedDocument() {	
		getSelectedAttach().stream().forEach(r -> {
		getAPI().getAttachment().getAttach(r, new AsyncCallback<JSON<JsAttach>>() {
				
			@Override
			public void onSuccess(JSON<JsAttach> js) {
				JsAttach attach = js.getOneData();
				PaperInput nameBox = new PaperInput();
				nameBox.setLabel("Nombre");
				nameBox.setWidth("100%");
				nameBox.setList("as");
				nameBox.setValue(attach.getTitle());
				   
				AonComboBox categoryBox = new AonComboBox();
				categoryBox.setLabel("Categor\u00eda");
				categoryBox.setWidth("100%");
				categoryBox.setItemLabelPath("name");
				categoryBox.setItemValuePath("name");
				getAPI().getAttachment().getCategories(new AsyncCallback<JSON<JsLabel>>() {
					
					@Override
					public void onSuccess(JSON<JsLabel> result) {
						categoryBox.setItems(result.getData());
						categoryBox.setValue(attach.getCategory().getName());
					}
							
					@Override public void onFailure(Throwable caught) {}
				});
						
				AonComboBox tagBox = new AonComboBox();
				tagBox.setWidth("100%");
				tagBox.setLabel("Etiqueta");
				tagBox.setItemLabelPath("name");
				tagBox.setItemValuePath("name");
				getAPI().getAttachment().getTags(new AsyncCallback<JSON<JsLabel>>() {
						
					@Override
					public void onSuccess(JSON<JsLabel> result) {
						tagBox.setItems(result.getData());
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
				HorizontalPanel tagPanel = new HorizontalPanel();
				JsArray<JsLabel> tags = attach.getTags();
				for(Integer i = 0 ; i < tags.length(); i++) {
					tagList.add(tags.get(i));
					tagPanel.add(buildTagPanel(tags.get(i)));
				}
				
				tagBox.addValueChangedHandler(new ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(ValueChangedEvent event) {
						JsLabel tag = tagBox.getSelectedItem().cast();
						Boolean addTag = true;
						for(Integer i = 0 ; i < tagList.size(); i++) {
							if(tagList.get(i).getName().equals(tag.getName())) {
								addTag = false;
							}
						}
						if(addTag) {
							tagList.add(tag);
							tagPanel.add(buildTagPanel(tag));
						}
					}
				});
				AonComboBox scopeBox = new AonComboBox();
				scopeBox.setWidth("100%");
				scopeBox.setLabel("\u00c1mbito");
				scopeBox.setItemLabelPath("name");
				scopeBox.setItemValuePath("name");
				getAPI().getAttachment().getScopes(new AsyncCallback<JSON<JsObject>>() {
					
					@Override
					public void onSuccess(JSON<JsObject> result) {
						scopeBox.setItems(result.getData());
						scopeBox.setValue(attach.getScope().getName());
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
					
				HorizontalPanel hp = new HorizontalPanel();
				Label confidentialLabel = new Label("Confidencial");
				confidentialLabel.getElement().getStyle().setPaddingTop(20, Unit.PX);
				confidentialLabel.getElement().getStyle().setPaddingRight(10, Unit.PX);
				confidentialLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);		
				hp.add(confidentialLabel);
								
				PaperToggleButton confidential = new PaperToggleButton();
				confidential.setChecked(attach.isConfidential());
				confidential.getElement().getStyle().setPaddingTop(13, Unit.PX);
				hp.add(confidential);
				
				VerticalPanel vp = new VerticalPanel();
				vp.setWidth("100%");
				vp.add(nameBox);
				vp.add(categoryBox);
				vp.add(tagBox);
				vp.add(tagPanel);
				vp.add(scopeBox);
				vp.add(hp);
				AonDialog dialog = new AonDialog("Editar Archivo", vp) {
					
					@Override protected void onCancel() {hide();}
					
					@Override
					protected void onAccept() {
						JSONObject json = new JSONObject();
						json.put("name", new JSONString(nameBox.getValue()));
						JsLabel categoryItem = (JsLabel) categoryBox.getSelectedItem().cast();
						json.put("category", new JSONString(categoryItem != null ?  categoryItem.getId() + "" : ""));
						ids = "";
						for(Integer i = 0 ; i< tagList.size(); i++) {
							if(!"".equals(ids)) ids = ids + ","; 
							ids = ids + tagList.get(i).getId();
						}
						json.put("tag", new JSONString(ids));
						JsLabel scopeItem = (JsLabel) scopeBox.getSelectedItem().cast();
						json.put("scope", new JSONString(scopeItem != null ? scopeItem.getId() + "" : ""));
						json.put("confidential", new JSONString(Boolean.toString(confidential.getChecked())));
						String requestData = JsonUtils.stringify(json.getJavaScriptObject());
						getAPI().getAttachment().updateAttach(r, requestData, new AsyncCallback<JsAttach>() {
							
							@Override
							public void onSuccess(JsAttach result) {
								createAttachListPanel();
								hide();
							}
							
							@Override public void onFailure(Throwable caught) {}
						});
					}	
				};
							
				dialog.setAutoHideEnabled(true);
				dialog.addAutoHidePartner(categoryBox.getElementById("overlay"));
				dialog.addAutoHidePartner(tagBox.getElementById("overlay"));
				dialog.addAutoHidePartner(scopeBox.getElementById("overlay"));
				dialog.getElement().getStyle().setWidth(310, Unit.PX);
				dialog.center();			
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		});
	}
	
	private void downloadSelectedDocument() {
		getSelectedAttach().stream().forEach(r -> {
			getAPI().getAttachment().download(r); 
		});
	}
	
	private void sendSelectedDocuments() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName(AON.AON_CSS.aonWidthAll());
		AonComboBox emailComboBox = new AonComboBox();
    	emailComboBox.setLabel("De");
    	emailComboBox.setItemLabelPath("name");
    	emailComboBox.setItemValuePath("name");
		API.getCommon().getMailAccounts(new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> result) {
		    	emailComboBox.setItems(result.getData());
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		panel.add(emailComboBox);
		
		PaperInput toText = new PaperInput();
		toText.setLabel("Para");

		panel.add(toText);
		AonComboBox signComboBox = new AonComboBox();
    	signComboBox.setLabel("Firma de Correo");
    	signComboBox.setItemLabelPath("name");
    	signComboBox.setItemValuePath("name");
    	API.getCommon().getSignatures(new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> result) {
				signComboBox.setItems(result.getData());
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
    	panel.add(signComboBox);
    	
    	AonDialog dialog = new AonDialog("Enviar Documentos", panel) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {
				// TODO SEND MAIL!!!+
				JsObject jsEmail = (JsObject) emailComboBox.getSelectedItem();
				JsObject jsSign = (JsObject) signComboBox.getSelectedItem();
				ids2 = "" ;
				getSelectedAttach().stream().forEach(r -> {
					if(!ids2.equals("")) {
						ids2 = ids2 + ",";
					}
					ids2 = ids2 + r;
				});
				String requestData = "{\"id\":["+ ids2 + "],"
						+ "\"mail_account\":\""+ jsEmail.getId() +"\","
						+ "\"signature\":\""+ ((jsSign != null) ? jsSign.getId() : "-1" )+"\"," 
						+ "\"to\":\""+ toText.getValue() + "\""
						+ "}";

				API.getAttachment().sendDocuments(requestData);
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
