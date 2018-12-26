package net.aonsolutions.aon.gwt.document.client;

import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsLabel;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.vaadin.polymer.iron.widget.IronCollapse;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.iron.widget.IronSelector;
import com.vaadin.polymer.paper.widget.PaperIconButton;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperItem;

public class DocumentsConfigurationPanel extends Composite {

    interface Binder extends UiBinder<HTMLPanel , DocumentsConfigurationPanel> {
    	
    }
    
    @UiField HTMLPanel panel;
    
    @UiField IronSelector menuSelector;
//    @UiField PaperItem allFilesPaper;
//    @UiField PaperItem systemPaper;
//    @UiField PaperItem lotePaper;
    
    @UiField Button categoryButton;
    @UiField IronCollapse categoryCollapse;
    @UiField IronSelector categorySelector;
    
    @UiField Button tagButton;
    @UiField IronCollapse tagCollapse;
    @UiField IronSelector tagSelector;
        
    
    private static Binder binder = GWT.create(Binder.class);
	
    private Documental parent;
    
    public Documental getDocumental() {
		return parent;
	}
       
	public DocumentsConfigurationPanel(Documental parent) {
    	this.parent = parent;
    	initWidget(binder.createAndBindUi(this));
    	createMenu();
        createCategory();
        createTag();
    }
    
    
    private static final String ALL_FILES = "Todos los Archivos";
    private static final String SYSTEM_FILES = "Mensajes del Sistema";
    private static final String PARENT_FILES = "Archivos del Entorno";
    
    private void createMenu() {

		menuSelector.add(buildMenu(ALL_FILES, "attachment"));
		menuSelector.add(buildMenu(SYSTEM_FILES, "settings"));
		if(!getDocumental().getAonData().getDomain().isParent() && getDocumental().getAonData().getDomain().isEnableHeredity()) {
			menuSelector.add(buildMenu(PARENT_FILES, "folder"));
		}
    }
    
    private void createCategory() {
    	getDocumental().getAPI().getAttachment().getCategories(new AsyncCallback<JSON<JsLabel>>() {
			
			@Override
			public void onSuccess(JSON<JsLabel> result) {
				load(result.getData(), categorySelector, categoryCollapse, true);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
    	
    	categoryButton.addClickHandler(new ClickHandler() {
	   	
	    	   @Override
	    	   public void onClick(ClickEvent event) {
	    		   categoryCollapse.toggle();
	    	   }
	    });
    }
    
    private void createTag() {
    	getDocumental().getAPI().getAttachment().getTags(new AsyncCallback<JSON<JsLabel>>() {
			
			@Override
			public void onSuccess(JSON<JsLabel> result) {
				load(result.getData(), tagSelector, tagCollapse, false);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
    	
    	tagButton.addClickHandler(new ClickHandler() {
	   	
	    	   @Override
	    	   public void onClick(ClickEvent event) {
	    		   tagCollapse.toggle();
	    	   }
	    });
    }

    private ClickHandler addCategoryClickHandler(){
    	return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				PaperInput pi = new PaperInput();
				pi.setLabel("Categoria");
				AonDialog dialog =  new AonDialog("Nueva Categoria",pi){
					@Override protected void onCancel() {hide();}
					@Override protected void onAccept() {
						PaperInput pi = (PaperInput) getContentWidget().getWidget(0);
						if(!"".equals(pi.getValue())){
							getDocumental().getAPI().getAttachment().createCategory("{\"name\":\""+ pi.getValue() +"\","
								+ "\"type\":\""+ TagType.TASK_TYPE.ordinal() +"\"" +"}", new AsyncCallback<JsLabel>() {
							
								@Override
								public void onSuccess(JsLabel result) {
									categorySelector.removeFromParent();
									categorySelector = new IronSelector();
									categoryCollapse.add(categorySelector);
									createCategory();
								}
							
								@Override public void onFailure(Throwable caught) {}
							});		
							hide();
						}
					}
				};
				dialog.center();
			}
		};
    }
    
    private ClickHandler editCategoryClickHandler(JsLabel label){
    	return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				event.preventDefault();
				PaperInput pi = new PaperInput();
				pi.setLabel("Categoria");
				pi.setValue(label.getName());
				AonDialog dialog =  new AonDialog("Editar Categoria",pi){
					@Override protected void onCancel() {hide();}
					@Override protected void onAccept() {
						PaperInput pi = (PaperInput) getContentWidget().getWidget(0);
						if(!"".equals(pi.getValue())){
							
							getDocumental().getAPI().getAttachment().updateCategory(label, "{\"name\":\""+ pi.getValue() +"\"}", new AsyncCallback<JsLabel>() {
							
								@Override
								public void onSuccess(JsLabel result) {
									categorySelector.removeFromParent();
									categorySelector = new IronSelector();
									categoryCollapse.add(categorySelector);
									createCategory();			
								}
								
								@Override public void onFailure(Throwable caught) {}
							});
							hide();
						}
					}
				};
				dialog.center();
			}
		};
    }
    
    private ClickHandler deleteCategoryClickHandler(JsLabel label){
    	return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				event.preventDefault();
				getDocumental().getAPI().getAttachment().deleteCategory(label, new AsyncCallback<JsLabel>() {
					
					@Override public void onSuccess(JsLabel result) {
						categorySelector.removeFromParent();
						categorySelector = new IronSelector();
						categoryCollapse.add(categorySelector);
						createCategory();	
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		};
    }
    
    private ClickHandler addTagClickHandler(){
    	return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				PaperInput pi = new PaperInput();
				pi.setLabel("Etiqueta");
				AonDialog dialog =  new AonDialog("Nueva Etiqueta",pi){
					@Override protected void onCancel() {hide();}
					@Override protected void onAccept() {
						PaperInput pi = (PaperInput) getContentWidget().getWidget(0);
						if(!"".equals(pi.getValue())){
							getDocumental().getAPI().getAttachment().createTag("{\"name\":\""+ pi.getValue() +"\","
								+ "\"type\":\""+ TagType.TASK_TYPE.ordinal() +"\"" +"}", new AsyncCallback<JsLabel>() {
							
								@Override
								public void onSuccess(JsLabel result) {
									tagSelector.removeFromParent();
									tagSelector = new IronSelector();
									tagCollapse.add(tagSelector);
									createTag();	
								}
							
								@Override public void onFailure(Throwable caught) {}
							});		
							hide();
						}
					}
				};
				dialog.center();
			}
		};
    }
    
    private ClickHandler editTagClickHandler(JsLabel label){
    	return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				PaperInput pi = new PaperInput();
				pi.setLabel("Etiqueta");
				pi.setValue(label.getName());
				AonDialog dialog =  new AonDialog("Editar Etiqueta",pi){
					@Override protected void onCancel() {hide();}
					@Override protected void onAccept() {
						PaperInput pi = (PaperInput) getContentWidget().getWidget(0);
						if(!"".equals(pi.getValue())){
							
							getDocumental().getAPI().getAttachment().updateTag(label, "{\"name\":\""+ pi.getValue() +"\"}", new AsyncCallback<JsLabel>() {
							
								@Override
								public void onSuccess(JsLabel result) {
									tagSelector.removeFromParent();
									tagSelector = new IronSelector();
									tagCollapse.add(tagSelector);
									createTag();			
								}
								
								@Override public void onFailure(Throwable caught) {}
							});
							hide();
						}
					}
				};
				dialog.center();
			}
		};
    }
    
    private ClickHandler deleteTagClickHandler(JsLabel label){
    	return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				getDocumental().getAPI().getAttachment().deleteTag(label, new AsyncCallback<JsLabel>() {
					
					@Override public void onSuccess(JsLabel result) {
						tagSelector.removeFromParent();
						tagSelector = new IronSelector();
						tagCollapse.add(tagSelector);
						createTag();	
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		};
    }
    
    private PaperItem buildMenu(String title, String icon) {
    	PaperItem item = new PaperItem();
    	item.getElement().getStyle().setCursor(Cursor.POINTER);
		IronIcon ii = new IronIcon();
		ii.setIcon(icon);
			
		item.add(ii);
		item.add(new Label(title));
		item.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				LinkedList<String> list = new LinkedList<>();
				if(ALL_FILES.equals(title)) {
					list.add("all");
				}else if(SYSTEM_FILES.equals(title)) {
					list.add("system");
				} else if(PARENT_FILES.equals(title)) {
					list.add("parent");
				}
				getDocumental().getFilterMap().put("type", list);
				getDocumental().createAttachListPanel();
			}
		});
		return item;
    }
    
    private void load(AonJsArray<JsLabel> data, IronSelector selector, IronCollapse collapse, Boolean isCategory){
   		for(JsLabel label : data.toLinkedList()){
   			HorizontalPanel hp = new HorizontalPanel();
   			PaperItem item = new PaperItem();
			
   			Boolean selected = getDocumental().getFilterMap().containsKey(isCategory ? "category" : "tag") &&
   					getDocumental().getFilterMap().get(isCategory ? "category" : "tag").contains(label.getId() + "");
   			IronIcon ii = new IronIcon();
   			ii.setIcon(selected ? "check" : "label");
   			ii.getElement().getStyle().setColor("#"+label.getColor());
   			
   			Label l = new Label(label.getName());
   			if(selected) l.getElement().getStyle().setFontWeight(FontWeight.BOLD);
   			item.add(ii);
   			item.add(l);
   			item.setStyle("min-height: 30px;");	
   			
   			item.addDomHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					String key = isCategory ? "category" : "tag";
					if(getDocumental().getFilterMap().containsKey(key))
						if(getDocumental().getFilterMap().get(key).contains(label.getId() + "")) {
							getDocumental().getFilterMap().get(key).remove(label.getId() + "");
						} else getDocumental().getFilterMap().get(key).add(label.getId() + "");
					else {
						LinkedList<String> list = new LinkedList<>();
						list.add(label.getId() + "");
						getDocumental().getFilterMap().put(key, list);
					}
					if(isCategory) {
						categorySelector.removeFromParent();
						categorySelector = new IronSelector();
						categoryCollapse.add(categorySelector);
						createCategory();	
					} else {
						tagSelector.removeFromParent();
						tagSelector = new IronSelector();
						tagCollapse.add(tagSelector);
						createTag();		
					}
					getDocumental().createAttachListPanel();
				}
			}, ClickEvent.getType());
   			
			hp.add(item);
			hp.setWidth("100%");
   			
			
   			if(getDocumental().getAonData().getDomain().getId() == label.getDomain() &&
   					getDocumental().getAonData().getUser().hasDocumentManagerRole()) {
   				PaperIconButton edit = new PaperIconButton();
   				edit.setIcon("create");
   				//edit.setDisabled(admin);
   				edit.setStyle("min-height: 30px;padding-top:0px;");
   				edit.addClickHandler(isCategory ? editCategoryClickHandler(label) : editTagClickHandler(label));
   				edit.setVisible(false);
					
   				PaperIconButton del = new PaperIconButton();
   				del.setIcon("delete");
   				//del.setDisabled(admin);
   				del.setStyle("min-height: 30px;padding-top:0px;");
   				del.addClickHandler(isCategory ? deleteCategoryClickHandler(label) : deleteTagClickHandler(label));
   				del.setVisible(false);
   				
   				HorizontalPanel hoption = new HorizontalPanel();
   				hoption.getElement().getStyle().setPosition(Position.ABSOLUTE);
   				hoption.getElement().getStyle().setRight(10, Unit.PX);
   				hoption.add(edit);
   				hoption.add(del);

				hp.add(hoption);
		
				hp.addDomHandler(new MouseOverHandler() {
				
					@Override
					public void onMouseOver(MouseOverEvent event) {
						edit.setVisible(true);
						del.setVisible(true);
					}
				}, MouseOverEvent.getType());
			
				hp.addDomHandler(new MouseOutHandler() {
						
					@Override
					public void onMouseOut(MouseOutEvent event) {
						edit.setVisible(false);
						del.setVisible(false);
					}
				}, MouseOutEvent.getType());
			}
			selector.add(hp);
   		}			
   		PaperIconButton add = new PaperIconButton();
   		add.setIcon("add");
   		//pib.setDisabled(admin);
   		add.setStyle("min-height: 30px;");
   		add.getElement().getStyle().setLeft(290, Unit.PX);
   		add.addClickHandler(isCategory ? addCategoryClickHandler() : addTagClickHandler());
   		selector.add(add);
   		if(!collapse.getOpened())
   			collapse.toggle();				
    }
   
}
