package com.esferalia.aon.gwt.template.client.marketplace;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.template.client.ContextMenu;
import com.esferalia.aon.gwt.template.client.TemplatesDialog;
import com.esferalia.aon.gwt.template.client.marketplace.tree.TreeNode;
import com.esferalia.aon.gwt.template.client.marketplace.tree.TreeNodeTypes;
import com.esferalia.aon.gwt.template.shared.Dialog;
import com.esferalia.aon.gwt.template.shared.Ecommerce;
import com.esferalia.aon.gwt.template.shared.marketplace.DisclosureImages;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class Marketplace extends Composite {

	final IMarketplaceAsync impl = GWT.create(IMarketplace.class);

	interface Binder extends UiBinder<Widget, Marketplace> {

	}
	
	private static final Binder binder = GWT.create(Binder.class);

	@UiField SplitLayoutPanel splitLayoutPanel;
	@UiField ScrollPanel sidebar;
	@UiField Tree tree;
	@UiField(provided=true) DisclosurePanel epanel;
	@UiField Button tagButton;
	@UiField SimpleLayoutPanel content;
	
	AonData aonData;
	
	public Marketplace(AonData aonData){
		this.aonData = aonData;
		splitLayoutPanel = new SplitLayoutPanel();
		sidebar = new ScrollPanel();
		tree = new Tree();
		content = new SimpleLayoutPanel();
		DisclosureImages di = new DisclosureImages();
		epanel = new DisclosurePanel(di.getClosed(), di.getOpen(), "Etiquetas");
		tagButton = new Button();
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get(aonData.getRootPanel()).add(ui);
		load();
	}
	
	public AonData getAonData() {
		return aonData;
	}
	
	public Domain getDomain() {
		return getAonData().getDomain();
	}
	
	public User getUser() {
		return getAonData().getUser();
	}
	
	private void load() {
		TreeNode<Ecommerce> amazon = TreeNodeTypes.ECOMMERCE.getInstance().render(tree, Ecommerce.AMAZON);
		amazon.setState(true);
		TreeNodeTypes.PRODUCT_TEMPLATES.getInstance().render(tree, getDomain().getId());
		
		final TreeNode<Integer> productTemplateValues = TreeNodeTypes.PRODUCT_TEMPLATE_VALUES.getInstance().render(tree, null);
		productTemplateValues.setState(true);
		
		// Gestión de Etiquetas de tipo Marketplace
		loadTagPanel();
		
	}
	
	public void setContent(Widget widget) {
		this.content.setWidget(widget);
	}
	
	@UiHandler("tree")
	void onTreeSelecction(SelectionEvent<TreeItem> event) {
		TreeNode<?> node = (TreeNode<?>) event.getSelectedItem();
		node.select(this);
		sidebar.scrollToLeft();
	}

	public IMarketplaceAsync getImpl() {
		return impl;
	}
	
	//-------------------- TAG 
	
	private void loadTagPanel(){
		impl.getMarketplaceTagList(getDomain(), getUser(), new AsyncCallback<LinkedList<Tag>>() {
			
			@Override
			public void onSuccess(LinkedList<Tag> result) {
				VerticalPanel vp = new VerticalPanel();
				for (Tag tag : result) {
					Button button = new Button(tag.getName());
					button.setStyleName("aon-editDataTable-button aon-icon-tag");
					final Tag tagAux = tag;
					button.addDomHandler(new ContextMenuHandler() {
						@Override
						public void onContextMenu(ContextMenuEvent event) {
							event.preventDefault();
							event.stopPropagation();
							NativeEvent nativeEvent =  event.getNativeEvent();
							TagContextMenu tcm = new TagContextMenu(tagAux);
							tcm.setPopupPosition(nativeEvent.getClientX(),
										nativeEvent.getClientY());
							tcm.show();
						}
					}, ContextMenuEvent.getType());
					vp.add(button);
				}
				epanel.setContent(vp);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("tagButton")
	void tagb(ClickEvent event){
		epanel.setOpen(true);
		TextBox tb = new TextBox();
		tb.setStyleName("aon-inputText");
		tb.addBlurHandler(new BlurHandler() {
		
			@Override
			public void onBlur(BlurEvent event) {
				VerticalPanel v = (VerticalPanel) epanel.getContent();
				v.remove(v.getWidgetCount() - 1);						
			}
		});
		tb.addKeyPressHandler(new KeyPressHandler() {
				
			@Override
			public void onKeyPress(KeyPressEvent event) {
				
				if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER){
					final VerticalPanel vp = (VerticalPanel) epanel.getContent();
					TextBox tb = (TextBox) vp.getWidget(vp.getWidgetCount() - 1);
					impl.addMarketplaceTag(getDomain(), getUser(), tb.getText(), new AsyncCallback<Tag>() {
						@Override public void onSuccess(Tag result) {
							Button button = new Button(result.getName());
							button.setStyleName("aon-editDataTable-button aon-icon-tag");
							final Tag tag = result;
							button.addDomHandler(new ContextMenuHandler() {
								@Override
								public void onContextMenu(ContextMenuEvent event) {
									event.preventDefault();
									event.stopPropagation();
									NativeEvent nativeEvent =  event.getNativeEvent();
									TagContextMenu tcm = new TagContextMenu(tag);
									tcm.setPopupPosition(nativeEvent.getClientX(),
												nativeEvent.getClientY());
									tcm.show();
								}
							}, ContextMenuEvent.getType());
							vp.remove(vp.getWidgetCount() - 1);	
							vp.add(button);
						}
						@Override public void onFailure(Throwable caught) {}
					});
				}
			}
		 });
		 VerticalPanel vp = (VerticalPanel) epanel.getContent();
		 vp.add(tb);
		 tb.getElement().focus();
	}
	
	private void editTag(Tag tag){
		Dialog dialog = new Dialog("Editar Etiqueta","Editar",true,"Cancelar",true,"editTag")
				.setTag(tag);
		final Tag tagAux = tag;
		TemplatesDialog popup = new TemplatesDialog(getAonData(), dialog) {
			@Override protected void onCancel() {
				hide();
			}
				
			@Override protected void onAccept() {
				hide();
				TextBox tb = (TextBox) flex_table.getWidget(0, 1); 
				tagAux.setName(tb.getText());
				tagAux.setType(TagType.MARKETPLACE);
				impl.updateMarketplaceTag(getDomain(), getUser(), tagAux, new AsyncCallback<Tag>() {
					@Override public void onSuccess(Tag result) {
						loadTagPanel();
					}
						
					@Override public void onFailure(Throwable caught) {}
				});
			}
		};
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	private void removeTag(Tag tag){
		Dialog dialog = new Dialog("Eliminar Etiqueta","Borrar",true,"Cancelar",true,"deleteTag")
			.setTag(tag);
		final Tag tagAux = tag; 
		TemplatesDialog popup = new TemplatesDialog(getAonData(), dialog) {
			@Override protected void onCancel() {
				hide();
			}
			
			@Override protected void onAccept() {
				hide();
				impl.removeMarketplaceTag(getDomain(), getUser(), tagAux, new AsyncCallback<Void>() {
					@Override public void onSuccess(Void result) {
						loadTagPanel();
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		};
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	class TagContextMenu extends ContextMenu {
		ScheduledCommand editCommand = new ScheduledCommand() {
			public void execute() {
				editTag(tag);
			};
		};
		ScheduledCommand removeCommand = new ScheduledCommand() {
			public void execute() {
				removeTag(tag);
			};
		};

		private MenuItem editItem;
		private MenuItem removeItem;
		private Tag tag;
		
		public TagContextMenu(Tag tag){
			this.tag = tag; 
			editItem = addItem("Editar", editCommand,
					"aon-icon-edit", AON.AON_ICON_CMD_BUTTON);
			editItem.setEnabled(true);
			removeItem = addItem("Borrar", removeCommand,
						AON.AON_ICON_DELETE, AON.AON_ICON_CMD_BUTTON);
			removeItem.setEnabled(true);
		}	
		
		public TagContextMenu() {}
		
		@Override public void show() {super.show();}
	}
}
