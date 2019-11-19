package com.esferalia.aon.gwt.template.client;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.widget.CustomDialogB;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.template.client.i18n.TemplatesMessages;
import com.esferalia.aon.gwt.template.shared.Dialog;
import com.esferalia.aon.gwt.template.shared.Ecommerce;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.Series;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.WorkPlace;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.warehouse.Department;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnCancelUploaderHandler;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStartUploaderHandler;
import gwtupload.client.IUploader.OnStatusChangedHandler;
import gwtupload.client.SingleUploader;

public abstract class TemplatesDialog extends CustomDialogB {
	
	final ITemplateAsync item = GWT.create(ITemplate.class);
	private static final TemplatesMessages MSG = GWT.create(TemplatesMessages.class);
	interface Binder extends UiBinder<Widget, TemplatesDialog>{}
	private static final Binder binder = GWT.create(Binder.class);

	@UiField(provided = true) protected FlexTable flex_table;
	@UiField(provided = true) Label label;
	@UiField Button accept_button;
	@UiField Button cancel_button;
	@UiField(provided = true) ScrollPanel scroll;
	
	AonData aonData; 
	Integer column = 1;
	HandlerRegistration handler;
	TemplateInfo ti;
	Dialog d;
	LinkedList<TemplateInfo> tlist;
	LinkedList<Warehouse> w;
	LinkedList<Series> series;
	Boolean closed;
	
	public TemplatesDialog(AonData aonData, Dialog dialog) {
		this.aonData = aonData;
		if(dialog.isClosed() != null) closed = dialog.isClosed();
		setCaption(dialog.getTitle());
		if(dialog.getTemplateList()!= null) tlist = dialog.getTemplateList();
		if(dialog.getWarehouses() != null) w = dialog.getWarehouses();
		if(dialog.getSeries2() != null) series = dialog.getSeries2();
		label = new Label();
		flex_table = new FlexTable();
		scroll = new ScrollPanel();
		ti = dialog.getTemplateInfo();
		build(dialog);
		d = dialog;
		setWidget(binder.createAndBindUi(this));
		
		accept_button.setText(dialog.getAccept());
		accept_button.setVisible(dialog.getBoolAccept());
		if(dialog.getType().equals("editEcommerceTemplate") || dialog.getType().equals("importEcommerceTemplate")){
			accept_button.setEnabled(false);
		}
		accept_button.addClickHandler(new ClickHandler() {
			Dialog dialog = d;
			@Override
			public void onClick(ClickEvent event) {
			
				if(dialog.getType().equals("exportCatalogue")){
					ListBox lb1 = (ListBox) flex_table.getWidget(0, 1);
					ListBox lb2 = null;
					if(!lb1.getSelectedItemText().equals("-")) lb2 = (ListBox) flex_table.getWidget(1, 1);
					if(lb1.getSelectedItemText().equals("-") || lb2.getSelectedItemText().equals("-")){
						label.setText(MSG.error3());
						label.getElement().getStyle().setColor("red");
					}
					else{
						onAccept();
					}
				}
				else if(dialog.getType().contains("import") || dialog.getType().contains("export") 
						|| dialog.getType().equals("editEcommerceTemplate")){
					if("importResponse".equals(dialog.getType()) || "importDelivery".equals(dialog.getType())){
						onAccept();
					} else{
						ListBox lb1 = (ListBox) flex_table.getWidget(0, 1);
						Label label1 = (Label) flex_table.getWidget(0, 0);
						if(label1.getText().equals("Plantilla")){
							if(lb1.getSelectedItemText().equals("-")){
								label.setText(MSG.error2());
								label.getElement().getStyle().setColor("red");
							}
							else onAccept();
						}
						else onAccept();
					}
				}
				else{
					if(dialog.getType().equals("new")){
						onAccept();
					}
					else if(dialog.getType().equals("deleteTag") || dialog.getType().equals("editTag")){
						onAccept();
					}
					else if(FeeUtils.feeCheck(dialog,flex_table) || StockUtils.stockCheck(dialog,flex_table) || ProductUtils.productCheck(dialog,flex_table) 
							|| InventoryUtils.inventoryCheck(dialog, flex_table) 
							|| dialog.getType().equals("delete") ||  dialog.getType().equals("deleteEcommerce") || dialog.getType().contains("import") 
							|| dialog.getType().contains("export")){
						onAccept();
					}
					else {
						label.setText(MSG.error1());
						label.getElement().getStyle().setColor("red");					}
				}
			}
		});
		
		cancel_button.setText(dialog.getCancel());
		cancel_button.setVisible(dialog.getBoolCancel());
		cancel_button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCancel();
			}
		});
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
	
	private void build(Dialog dialog) {
		switch (dialog.getType()) {
		case "new": newTemplate();break;
		case "edit": editTemplate(dialog);break;
		case "editTag": editTag(dialog);break;
		case "import": importar(dialog);break;
		case "export": exportar(dialog);break;
		case "delete": deleteTemplate(dialog.getTemplateInfo().getName());break;
		case "deleteTag": deleteTag(dialog.getTag().getName());break;
		case "deleteEcommerce": deleteTemplate(dialog.getEcommerceProduct().getTemplate().getType());break;
		case "importProduct": importProduct(dialog.getUrl(),dialog.getTemplateList());break;
		case "importStock": importStock(dialog);break;
		case "importTransferStock": importTransferStock(dialog);break;
		case "importResponse": importResponse(dialog.getError());break;
		case "exportProduct": exportProduct(dialog.getTemplateList());break;
		case "exportStock": exportStock(dialog.getTemplateList());break;
		case "exportTransferStock": exportTransferStock(dialog.getTemplateList());break;
		case "importFee": importFee(dialog.getUrl(),dialog.getTemplateList());break;
		case "exportFee": exportFee(dialog.getTemplateList());break;
		case "exportCatalogue": exportCatalogue(dialog.getTemplateList());break;
		case "importProposal": importProposal(dialog.getUrl(),dialog.getTemplateList());break;
		case "exportProposal": exportProposal(dialog.getUrl(),dialog.getTemplateList());break;
		case "exportInventory": exportInventory(dialog);break;
		case "exportIncome": exportIncome(dialog);break;
		case "importEcommerceTemplate": importEcommerce(dialog);break;
		case "editEcommerceTemplate": editEcommerce(dialog);break;
		case "exportEcommerce":exportEcommerce(dialog);break;
		case "importDelivery":importDelivery(dialog.getUrl());break;
		default:
			break;
		}
	} 
	
	
	
	private void importar(Dialog dialog){
		dialogAux = dialog;
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		ListBox lb = new ListBox();
		lb.addItem("-");
		lb.addItem(MSG.product());
		lb.addItem(MSG.stock());
		lb.addItem(MSG.assignFees());
		
		lb.addChangeHandler(new ChangeHandler() {
			Dialog dialog = dialogAux;
			@Override
			public void onChange(ChangeEvent event) {
				ListBox lb = (ListBox) flex_table.getWidget(0, 1);
				if(lb.getSelectedItemText().equalsIgnoreCase(MSG.product()))
					 importProduct(dialog.getUrl(),dialog.getTemplateList());
				else if(lb.getSelectedItemText().equalsIgnoreCase(MSG.stock()))
					item.getWarehouses(getDomain(), getUser(), new AsyncCallback<LinkedList<Warehouse>>() {
						@Override
						public void onSuccess(LinkedList<Warehouse> result) {
							dialog.setWarehouses(result);
							importStock(dialog);
						}
						
						@Override
						public void onFailure(Throwable caught) {}
					});
				else if(lb.getSelectedItemText().equalsIgnoreCase(MSG.assignFees()))
					importFee(dialog.getUrl(),dialog.getTemplateList());
			}
		});
		
		flex_table.setWidget(0, 0, new Label(MSG.type()));
		flex_table.setWidget(0, 1, lb);
		flexTableCss();
	}
	
	private void importEcommerce(Dialog dialog) {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		ListBox ecommerceListBox = new ListBox();
		for(Ecommerce ecommerce : Ecommerce.values()){
			ecommerceListBox.addItem(ecommerce.getName(), ecommerce.getOrdinalStr());
		}
		Label label1 = new Label(MSG.ecommerce());
		label1.addStyleName("aon-input-required");
		flex_table.setWidget(0, 0, label1);
		flex_table.setWidget(0, 1, ecommerceListBox);
		
		ListBox sellerListBox = new ListBox();
		sellerListBox.addItem("-","-1");
		for(Seller seller : dialog.getSellerList())
			sellerListBox.addItem(seller.getRegistryName(), seller.getId().toString());
		Label label2 = new Label(MSG.seller());
		label2.addStyleName("aon-input-required");
		flex_table.setWidget(1, 0, label2);
		flex_table.setWidget(1, 1, sellerListBox);
		
		TextBox typeTextBox = new TextBox();
		typeTextBox.setStyleName("aon-inputText");
		Label label3 = new Label(MSG.type());
		label3.addStyleName("aon-input-required");
		flex_table.setWidget(2, 0, label3);
		flex_table.setWidget(2, 1, typeTextBox);
		
		ListBox tagListBox = new ListBox();
		tagListBox.addItem("-");
		for(Tag tag : dialog.getTagList())
			tagListBox.addItem(tag.getName(), tag.getId().toString());
		Label label4 = new Label(MSG.tag());
		label4.addStyleName("aon-input-required");
		flex_table.setWidget(3, 0, label4);
		flex_table.setWidget(3, 1, tagListBox);

		SingleUploader upload = newUploader(null, dialog.getUrl(), 4);
		Label label5 = new Label(MSG.file());
		label5.addStyleName("aon-input-required");
		flex_table.setWidget(4, 0, label5);
		flex_table.setWidget(4, 1, upload);
		
		flexTableCss();
	}
	
	private void exportEcommerce(Dialog dialog) {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		ListBox typeListBox = new ListBox();
		typeListBox.addItem("-","-1");
		for(String type : dialog.getTypeList())
			typeListBox.addItem(type, type);		
		flex_table.setWidget(0, 0, new Label(MSG.type()));
		flex_table.setWidget(0, 1, typeListBox);

		flexTableCss();
	}
	
	private void editEcommerce(Dialog dialog) {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);

		ListBox ecommerceListBox = new ListBox();

		String value = "";
		if(dialog.getEcommerceProduct().getTemplate().getEcommerce().equals(Ecommerce.AMAZON.getName()))
			value = Ecommerce.AMAZON.getOrdinalStr();
		else if(dialog.getEcommerceProduct().getTemplate().getEcommerce().equals(Ecommerce.EBAY.getName()))
			value = Ecommerce.EBAY.getOrdinalStr();
		else if(dialog.getEcommerceProduct().getTemplate().getEcommerce().equals(Ecommerce.GENERIC.getName()))
			value = Ecommerce.GENERIC.getOrdinalStr();
		
		ecommerceListBox.addItem(dialog.getEcommerceProduct().getTemplate().getEcommerce(), value);
		ecommerceListBox.setEnabled(false);
		Label label1 = new Label(MSG.ecommerce());
		label1.addStyleName("aon-input-required");
		flex_table.setWidget(0, 0, label1);
		flex_table.setWidget(0, 1, ecommerceListBox);
		
		ListBox sellerListBox = new ListBox();
		sellerListBox.addItem("-","-1");
		Integer index = 1;
		for(Seller seller : dialog.getSellerList()){
			sellerListBox.addItem(seller.getRegistryName(), seller.getId().toString());	
			if(dialog.getEcommerceProduct().getTemplate().getSeller().equals(seller.getRegistryName()))
				sellerListBox.setSelectedIndex(index);
			index++;
		}	
		Label label2 = new  Label(MSG.seller());
		label2.addStyleName("aon-input-required");
		flex_table.setWidget(1, 0, label2);
		flex_table.setWidget(1, 1, sellerListBox);
		
		TextBox typeTextBox = new TextBox();
		typeTextBox.setText(dialog.getEcommerceProduct().getTemplate().getType());
		typeTextBox.setEnabled(false);
		typeTextBox.setStyleName("aon-inputText");
		Label label3 = new Label(MSG.type());
		label3.addStyleName("aon-input-required");
		flex_table.setWidget(2, 0, label3);
		flex_table.setWidget(2, 1, typeTextBox);
		
		ListBox tagListBox = new ListBox();
		tagListBox.addItem("-");
		Integer index2 = 1;
		for(Tag tag : dialog.getTagList()){
			tagListBox.addItem(tag.getName(), tag.getId().toString());
			if(dialog.getEcommerceProduct().getTemplate().getTag().equals(tag.getName()))
				tagListBox.setSelectedIndex(index2);
			index2++;
		}
		Label label4 = new Label(MSG.tag());
		label4.addStyleName("aon-input-required");
		flex_table.setWidget(3, 0, label4);
		flex_table.setWidget(3, 1, tagListBox);
		
		SingleUploader upload = newUploader(null, dialog.getUrl(), 4);
		Label label5 = new Label(MSG.file());
		label5.addStyleName("aon-input-required");
		flex_table.setWidget(4, 0, label5);
		flex_table.setWidget(4, 1, upload);
		flexTableCss();
	}
	
	Dialog dialogAux;
	private void exportar(Dialog dialog){
		dialogAux = dialog;
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		ListBox lb = new ListBox();
		lb.addItem("-");
		lb.addItem(MSG.product());
		lb.addItem(MSG.stock());
		lb.addItem(MSG.catalogue());
		
		lb.addChangeHandler(new ChangeHandler() {
			Dialog dialog = dialogAux;
			@Override
			public void onChange(ChangeEvent event) {
				ListBox lb = (ListBox) flex_table.getWidget(0, 1);
				if(lb.getSelectedItemText().equalsIgnoreCase(MSG.product()))
					exportProduct(dialog.getTemplateList());
				else if(lb.getSelectedItemText().equalsIgnoreCase(MSG.stock()))
					exportStock(dialog.getTemplateList());
				else if(lb.getSelectedItemText().equalsIgnoreCase(MSG.catalogue()))
					exportCatalogue(dialog.getTemplateList());
			}
		});
		
		flex_table.setWidget(0, 0, new Label(MSG.type()));
		flex_table.setWidget(0, 1, lb);
	
		flexTableCss();
	}
	
	private void exportProposal(String url, LinkedList<TemplateInfo> templates){
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		ListBox lb = new ListBox();
		
		lb.addItem("-");
		
		for(TemplateInfo ti : templates){
			if(ti.getType().equals(MSG.stock()))
				lb.addItem(ti.getName());
		}
		flex_table.setWidget(0, 0, new Label(MSG.template()));
		flex_table.setWidget(0, 1, lb);
		
		flexTableCss();
	}
	
	private void exportIncome(Dialog dialog){
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		ListBox lb = new ListBox();
		
		lb.addItem("-");
		
		for(TemplateInfo ti : dialog.getTemplateList()){
			if(ti.getType().equals(MSG.stock()))
				lb.addItem(ti.getName());
		}
		flex_table.setWidget(0, 0, new Label(MSG.template()));
		flex_table.setWidget(0, 1, lb);
		
		flexTableCss();
	}

	private void exportInventory(Dialog dialog){
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		ListBox lb = new ListBox();
		
		lb.addItem("-");
		
		for(TemplateInfo ti : dialog.getTemplateList()){
			if(closed && ti.getType().equals(MSG.closedInventory()))
				lb.addItem(ti.getName());
			if(!closed && ti.getType().equals(MSG.valuedInventory()))
				lb.addItem(ti.getName());
		}
		flex_table.setWidget(0, 0, new Label(MSG.template()));
		flex_table.setWidget(0, 1, lb);
		
		flexTableCss();
	}

	
	private void importProposal(String url, LinkedList<TemplateInfo> templates) {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		ListBox lb = new ListBox();
		lb.addItem("-");
		for(TemplateInfo ti : templates){
			if(ti.getType().equals(MSG.stock()) )
				lb.addItem(ti.getName());
		}
		flex_table.setWidget(0, 0, new Label(MSG.template()));
		flex_table.setWidget(0, 1, lb);
		
		SingleUploader upload = newUploader(null, url, 1);
		flex_table.setWidget(1, 0, new Label(MSG.file()));
		flex_table.setWidget(1, 1, upload);
		
		flexTableCss();
	}
	
	private void exportCatalogue(LinkedList<TemplateInfo> templateList) {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		ListBox lb0 = new ListBox();
		lb0.addItem("-");
		
		for(TemplateInfo t : templateList){
			if(t.getType().equals(MSG.stock()))
				lb0.addItem(t.getName());
		}
		flex_table.setWidget(0, 0, new Label(MSG.template()));
		flex_table.setWidget(0, 1, lb0);
		
		item.getWorkplaces(getDomain(), getUser(), new AsyncCallback<LinkedList<WorkPlace>>() {
			
			@Override
			public void onSuccess(LinkedList<WorkPlace> result) {

				ListBox lb1 = new ListBox();
				lb1.addItem("-");
				for(WorkPlace w : result){
					lb1.addItem(w.getName());
				}
				lbaux = lb1;
				lb1.addChangeHandler(new ChangeHandler() {
					ListBox lb = lbaux;
					@Override
					public void onChange(ChangeEvent event) {
						item.getDepartments(getDomain(), getUser(), lb.getItemText(lb.getSelectedIndex()), new AsyncCallback<LinkedList<Department>>() {
							
							@Override
							public void onSuccess(LinkedList<Department> result) {
								ListBox lb2 = new ListBox();
								lb2.addItem("-");
								for(Department w : result){
									lb2.addItem(w.getName());
								}
								
								flex_table.setWidget(2, 0, new Label(MSG.department()));
								flex_table.setWidget(2, 1, lb2);
								
								flexTableCss();
							}
							
							@Override
							public void onFailure(Throwable caught) {}
						} );
						
					}
				});
				flex_table.setWidget(1, 0, new Label(MSG.workplace()));
				flex_table.setWidget(1, 1, lb1);
				
				flexTableCss();
			}
			@Override
			public void onFailure(Throwable caught) {}
		});
		
	}

	protected abstract void onAccept();
	
	protected abstract void onCancel();

	private void newTemplate() {
		flexTable();
	}
	
	private void editTemplate(Dialog dialog) {
		flexTableEdit();
	}
	
	private void editTag(Dialog dialog){
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		TextBox tb = new TextBox();
		tb.setStyleName("aon-inputText");
		tb.setText(dialog.getTag().getName());
		flex_table.setWidget(0, 0, new Label(MSG.name()));
		flex_table.setWidget(0, 1, tb);
		flexTableCss();
	}
	
	private void deleteTemplate(String name) {
		label.setText(MSG.deleteTemplateMessage(name));
	}
	
	private void deleteTag(String name) {
		label.setText(MSG.deleteTagMessage(name));
	}
	
	private void importResponse(Error error){
		VerticalPanel vp = new VerticalPanel();
		if(!error.getError()){
			Label l1 = new Label("No se ha importado correctamente");
			l1.getElement().getStyle().setColor("red");
			vp.add(l1);
			if(error.getTextError() != null) {
				error.getTextError().stream().forEach(e -> {
					Label l = new Label(e);
					l.getElement().getStyle().setColor("red");
					vp.add(l);
				});
			}
		} else {
			Label label = new Label(MSG.importOk());
			vp.add(label);
		}
		
		if(error.getTextWarning() != null) {
			error.getTextWarning().stream().forEach( w -> {
				Label l = new Label(w);
				l.getElement().getStyle().setColor("orange");
				vp.add(l);
			});
		}
		if(error.getTextError().size() > 0 || error.getTextWarning().size() > 0) {
			scroll.setHeight("200px");
		}
		scroll.add(vp);
	}
	
	private void exportProduct(LinkedList<TemplateInfo> templates){	
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		ListBox lb = new ListBox();
		lb.addItem("-");
		for(TemplateInfo ti : templates){
			if(ti.getType().equals(MSG.product()))
				lb.addItem(ti.getName());
		}
		flex_table.setWidget(0, 0, new Label(MSG.template()));
		flex_table.setWidget(0, 1, lb);
		flexTableCss();
	}
	
	private void exportFee(LinkedList<TemplateInfo> templates){
		//TODO 
	}
	
	private void exportTransferStock(LinkedList<TemplateInfo> templates){
		//TODO 
	}
	
	private void exportStock(LinkedList<TemplateInfo> templates){
		//TODO 
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		ListBox lb = new ListBox();
		lb.addItem("-");
		for(TemplateInfo ti : templates){
			if(ti.getType().equals(MSG.stock()))
				lb.addItem(ti.getName());
		}
		flex_table.setWidget(0, 0, new Label(MSG.template()));
		flex_table.setWidget(0, 1, lb);
		
		ListBox lb2 = new ListBox();
		if(w.size()>1){
			lb2.addItem("-");
			for(Warehouse wh : w){
				lb2.addItem(wh.getName());
			}
		}
		else {
			for(Warehouse wh : w){
				lb2.addItem(wh.getName());
			}
			lb2.setEnabled(false);
		}
		flex_table.setWidget(1, 0, new Label(MSG.warehouse()));
		flex_table.setWidget(1, 1, lb2);
		
		CheckBox cb = new CheckBox();
		cb.setValue(true);
		
		flex_table.setWidget(2, 0, cb);		
		flex_table.setWidget(2, 1, new Label(MSG.includeZeroQuantity()));
		
		CheckBox cb2 = new CheckBox();
		cb2.setValue(false);
		
		flex_table.setWidget(3, 0, cb2);		
		flex_table.setWidget(3, 1, new Label(MSG.addPackagedInfo()));
		
		flexTableCss();
	}
	
	private void importDelivery(String url) {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		SingleUploader upload = newUploader(null, url, 1);
		flex_table.setWidget(0, 0, new Label(MSG.file()));
		flex_table.setWidget(0, 1, upload);
			
		flexTableCss();
	}

	
	private void importProduct(String url,LinkedList<TemplateInfo> templates) {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		ListBox lb = new ListBox();
		lb.addItem("-");
		for(TemplateInfo ti : templates){
			if(ti.getType().equals(MSG.product()))
				lb.addItem(ti.getName());
		}
		flex_table.setWidget(0, 0, new Label(MSG.template()));
		flex_table.setWidget(0, 1, lb);
		
		SingleUploader upload = newUploader(null, url, 1);
		flex_table.setWidget(1, 0, new Label(MSG.file()));
		flex_table.setWidget(1, 1, upload);
		
		item.getProductRoles(getDomain(), getUser(), new AsyncCallback<LinkedList<String>>() {
			
			@Override
			public void onSuccess(LinkedList<String> result) {
				ListBox lb2 = new ListBox();
				lb2.addItem(MSG.purchaseSale(), "0");
				for (String role : result) {
					if(role.equals(MSG.purchase()))
						lb2.addItem(MSG.purchase() , "1");
					if(role.equals(MSG.sale()))
						lb2.addItem(MSG.sale() , "2");
				}
				flex_table.setWidget(2, 0, new Label(MSG.type()));
				flex_table.setWidget(2, 1, lb2);				
				flexTableCss();
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
		
		flexTableCss();
	}
	
	private void importFee(String url, LinkedList<TemplateInfo> templates) {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		ListBox lb = new ListBox();
		lb.addItem("-");
		for(TemplateInfo ti : templates){
			if(ti.getType().equals(MSG.fee()))
				lb.addItem(ti.getName());
		}
		flex_table.setWidget(0, 0, new Label(MSG.template()));
		flex_table.setWidget(0, 1, lb);
		
		SingleUploader upload = newUploader(null, url, 1);
		flex_table.setWidget(1, 0, new Label(MSG.file()));
		flex_table.setWidget(1, 1, upload);
		
		CheckBox cb = new CheckBox();
		cb.setValue(true);
		
		flex_table.setWidget(2, 0, cb);		
		flex_table.setWidget(2, 1, new Label(MSG.ignoreInactiveCustomer()));

		flexTableCss();
	}
	
	private void importStock(Dialog dialog) {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);

		ListBox lb = new ListBox();
		lb.addItem("-");
		for(TemplateInfo ti : dialog.getTemplateList()){
			if(ti.getType().equals(MSG.stock()))
				lb.addItem(ti.getName());
		}

		
		flex_table.setWidget(0, 0, new Label(MSG.template()));
		flex_table.setWidget(0, 1, lb);
		
		ListBox lb2 = new ListBox();
		lb2.addItem(dialog.getWarehouseName());
		lb2.setEnabled(false);
		
		flex_table.setWidget(1, 0, new Label(MSG.warehouse()));
		flex_table.setWidget(1, 1,lb2 );
		
		SingleUploader upload = newUploader(null, dialog.getUrl(), 4);
		flex_table.setWidget(4, 0, new Label(MSG.file()));
		flex_table.setWidget(4, 1, upload);
		
		flexTableCss();
	}
	private Boolean esta(Series series,LinkedList<Series> series2){
		for (Series series3 : series2) {
			if(series3.getName().equals(series.getName()))
				return true;
		}
		return false;
	}
	private void importTransferStock(Dialog dialog) {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);

		ListBox lb = new ListBox();
		lb.addItem("-");
		for(TemplateInfo ti : dialog.getTemplateList()){
			if(ti.getType().equals(MSG.stock()))
				lb.addItem(ti.getName());
		}

		
		flex_table.setWidget(0, 0, new Label(MSG.template()));
		flex_table.setWidget(0, 1, lb);
		
		ListBox lb2 = new ListBox();
		lb2.addItem("-");
		for(Warehouse wh : w){
			lb2.addItem(wh.getName());
		}
		lb2.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ListBox lb = (ListBox)flex_table.getWidget(1, 1);
				if(!lb.getSelectedItemText().equals("-")){
				item.getSeries(getDomain(), getUser(), lb.getSelectedItemText(), new AsyncCallback<LinkedList<Series>>() {
					
					@Override
					public void onSuccess(LinkedList<Series> result) {
						
						ListBox lb4 = (ListBox) flex_table.getWidget(2, 1);
						String w2 = lb4.getSelectedItemText();
						
						if(!w2.equals("-")){
							
							series = new LinkedList<Series>();
							series.addAll(result);
							item.getSeries(getDomain(), getUser(), w2, new AsyncCallback<LinkedList<Series>>() {
								LinkedList<Series> series2 = series;
								@Override
								public void onSuccess(LinkedList<Series> result) {
									
									for (Series series : result) {
										if(!esta(series, series2)){
											series2.add(series);
										}
									}
									ListBox lb3 = new ListBox();
									if(series2.size()>1)lb3.addItem("-");
									for(Series s : series2){
										lb3.addItem(s.getName());
									}
									flex_table.setWidget(3, 1,lb3 );
								}
								
								@Override
								public void onFailure(Throwable caught) {}
							});
						}
						else{
							ListBox lb3 = new ListBox();
							if(result.size()>1) lb3.addItem("-");
							for(Series s : result){
								lb3.addItem(s.getName());
							}
							flex_table.setWidget(3, 1,lb3 );
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
				}
			}
		});
		
		ListBox lb2Aux = new ListBox();
		lb2Aux.addItem(dialog.getWarehouses().get(0).getName());
		lb2Aux.setEnabled(false);
		flex_table.setWidget(1, 0, new Label(MSG.sourceWarehouse()));
		flex_table.setWidget(1, 1,lb2Aux );
		
		
		ListBox lb4 = new ListBox();
		lb4.addItem("-");
		for(Warehouse wh : w){
			lb4.addItem(wh.getName());
		}
		lb4.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ListBox lb = (ListBox)flex_table.getWidget(2, 1);
				item.getSeries(getDomain(), getUser(), lb.getSelectedItemText(), new AsyncCallback<LinkedList<Series>>() {
					
					@Override
					public void onSuccess(LinkedList<Series> result) {
						
						ListBox lb2 = (ListBox) flex_table.getWidget(1, 1);
						String w2 = lb2.getSelectedItemText();
						
						if(!w2.equals("-")){
							series = new LinkedList<Series>();
							series.addAll(result);
							item.getSeries(getDomain(), getUser(), w2, new AsyncCallback<LinkedList<Series>>() {
								LinkedList<Series> series2 = series;
								@Override
								public void onSuccess(LinkedList<Series> result) {
									for (Series series : result) {
										if(!esta(series, series2)){
											series2.add(series);
										}
									}
									ListBox lb3 = new ListBox();
									if(series2.size()>1)lb3.addItem("-");
									for(Series s : series2){
										lb3.addItem(s.getName());
									}
									flex_table.setWidget(3, 1,lb3 );
								}
								
								@Override
								public void onFailure(Throwable caught) {}
							});
						}
						else{
							ListBox lb3 = new ListBox();
							if(result.size()>1) lb3.addItem("-");
							for(Series s : result){
								lb3.addItem(s.getName());
							}
							flex_table.setWidget(3, 1,lb3 );
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
			}
		});
		
		
		ListBox lb4Aux = new ListBox();
		lb4Aux.addItem(dialog.getWarehouses().get(1).getName());
		lb4Aux.setEnabled(false);
		flex_table.setWidget(2, 0, new Label(MSG.targetWarehouse()));
		flex_table.setWidget(2, 1,lb4Aux );
		
		ListBox lb3 = new ListBox();
		lb3.addItem("-");
		/*for(String s : series){
			lb3.addItem(s);
		}*/
		ListBox lb3Aux = new ListBox();
		lb3Aux.addItem(dialog.getSeries2().get(0).getName());
		lb3Aux.setEnabled(false);
		flex_table.setWidget(3, 0, new Label(MSG.serie()));
		flex_table.setWidget(3, 1,lb3Aux );
		
		TextBox tb = new TextBox();
		tb.setStyleName("aon-inputText");
		flex_table.setWidget(4, 0, new Label(MSG.comments()));
		flex_table.setWidget(4, 1, tb);
		
		SingleUploader upload = newUploader(null, dialog.getUrl(), 5);
		flex_table.setWidget(5, 0, new Label(MSG.file()));
		flex_table.setWidget(5, 1, upload);
		
		flexTableCss();
	}
	
	PopupPanel popup;
	ListBox lbaux;
	public void flexTable() {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		TextBox tb = new TextBox();
		tb.setStyleName("aon-inputText");
		flex_table.setWidget(0, 0, new Label(MSG.name()));
		flex_table.setWidget(0, 1, tb);
		flex_table.setWidget(0, 2, new Label(""));
		
		ListBox lb = new ListBox();
		lb.addItem("-");
		lb.addItem(MSG.product());
		lb.addItem(MSG.stock());
		lb.addItem(MSG.fee());
		lb.addItem(MSG.closedInventory());
		lb.addItem(MSG.valuedInventory());

		lbaux = lb;
		lb.addChangeHandler(new ChangeHandler() {
			ListBox lb = lbaux;
			@Override
			public void onChange(ChangeEvent event) {
				column = 1;
				for(Integer i = flex_table.getRowCount(); i>2;i--)
					flex_table.removeRow(i-1);
				
				// Genera la lista de atributos del tipo de plantilla seleccionada.
				
				Integer index = mandatoryWidget(2, lb.getSelectedItemText());
				
				optionalListBox(index, lb.getSelectedItemText());	
				
				ListBox lb2 = new ListBox();
				for(Integer k = 0;k< list_box.getItemCount();k++)
					lb2.addItem(list_box.getItemText(k));
				
				handler = lb2.addChangeHandler(changeHandler());

				flex_table.setWidget(index, 0, new Label(MSG.column(column)));
				flex_table.setWidget(index, 1, lb2);
				flex_table.setWidget(index, 2, new Label(""));
				
				flex_table.getCellFormatter().setStyleName(index, 0,
						"aon-panelGrid-odd");
				flex_table.getCellFormatter().setStyleName(index, 1,
						"aon-panelGrid-even");
				flex_table.getCellFormatter().setStyleName(index, 2,
						"aon-panelGrid-aux");	
			}
		});
		flex_table.setWidget(1, 0, new Label(MSG.type()));
		flex_table.setWidget(1, 1, lb);
		Button info = new Button("");
		info.setStyleName("aon-finding-toolbar-item-template aon-icon-info");
		
		info.addMouseOverHandler(infoMouseOverHandler());
		info.addMouseOutHandler(infoMouseOutHandler());
		
		flex_table.setWidget(1, 2, info);
		
		flexTableCss();
		flex_table.getCellFormatter().setStyleName(0, 2, "aon-panelGrid-aux");	
		flex_table.getCellFormatter().setStyleName(1, 2, "aon-panelGrid-aux");	
	}
	
	public void flexTableEdit() {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		TextBox tb = new TextBox();
		tb.setStyleName("aon-inputText");
		tb.setText(ti.getName());
		flex_table.setWidget(0, 0, new Label(MSG.name()));
		flex_table.setWidget(0, 1, tb);
		flex_table.setWidget(0, 2, new Label(""));
		ListBox lb = new ListBox();
		lb.addItem("-");
		lb.addItem(MSG.product());
		lb.addItem(MSG.stock());
		lb.addItem(MSG.fee());
		lb.addItem(MSG.closedInventory());
		lb.addItem(MSG.valuedInventory());
		for(Integer i = 0;i< lb.getItemCount();i++){
			if(lb.getItemText(i).equals(ti.getType())){
				lb.setSelectedIndex(i);
			}
		}
		lb.setEnabled(false);
		flex_table.setWidget(1, 0, new Label(MSG.type()));
		flex_table.setWidget(1, 1, lb);
		Button info = new Button("");
		info.setStyleName("aon-finding-toolbar-item-template aon-icon-info");
		lbaux = lb;
		
		info.addMouseOverHandler(infoMouseOverHandler());
		info.addMouseOutHandler(infoMouseOutHandler());
		
		flex_table.setWidget(1, 2, info);
		listBox(ti.getType());
		listBoxEdit(ti.getType());
		Integer cont = 0;
		for(Integer j = 0; j< ti.getColumns().size();j++){
			column = j+1;
			ListBox lb2 = new ListBox();
			Integer num = null;
			for(Integer k = 0;k< list_box_edit.getItemCount();k++){
				lb2.addItem(list_box_edit.getItemText(k));
				if(lb2.getItemText(k).equals(ti.getColumns().get(j))){
					lb2.setSelectedIndex(k);
					cont++;
					if(cont < ti.getColumns().size())list_box.removeItem(k+1);
					
					num = k;
				}
			}
			
			if(num!=null) list_box_edit.removeItem(num);
			flex_table.setWidget(j+2, 0, new Label(MSG.column(j+1)));
			flex_table.setWidget(j+2, 1, lb2);
			if(j != ti.getColumns().size()-1){

				flex_table.setWidget(j+2, 2, new Label(""));
				lb2.setEnabled(false);
			}
			else{
				if(column <max-1){
					handler = lb2.addChangeHandler(changeHandler());
					HorizontalPanel hp = new HorizontalPanel();
					if(j!=0){
						Button b = new Button("");
						b.setStyleName("aon-finding-toolbar-item-template aon-search-minus");
						b.addClickHandler(removeClickHandler());
						hp.add(b);
					}
					Button b2 = new Button("");
					b2.setStyleName("aon-finding-toolbar-item-template aon-search-add");
					b2.addClickHandler(addClickHandler());
					hp.add(b2);
					flex_table.setWidget(j+2, 2,hp);
					
				}
				else{
					Button b = new Button("");
					b.setStyleName("aon-finding-toolbar-item-template aon-search-minus");
					b.addClickHandler(removeClickHandler());
					flex_table.setWidget(j+2, 2,b);
				}
			}
		}
		
		for (int i = 0; i < flex_table.getRowCount(); i++) {
			for (int j = 0; j < flex_table.getCellCount(i); j++) {
				if ((j % 2) == 0) {
					flex_table.getCellFormatter().setStyleName(i, j, "aon-panelGrid-odd");
				} else {
					flex_table.getCellFormatter().setStyleName(i, j, "aon-panelGrid-even");
				}
			}
			flex_table.getCellFormatter().setStyleName(i, 2, "aon-panelGrid-aux");
		}
	}
	
	private MouseOutHandler infoMouseOutHandler(){
		return new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				popup.hide();
			}
		};
	}
	
	private MouseOverHandler infoMouseOverHandler(){
		return new MouseOverHandler() {
			ListBox lb = lbaux;
			@Override
			public void onMouseOver(MouseOverEvent event) {
				Boolean bool = true;
				VerticalPanel vp = new VerticalPanel();
				Label title = new Label(MSG.requiredColumns());
				title.addStyleName("aon-info-title-template");
				vp.add(title);
				if(lb.getItemText(lb.getSelectedIndex()).equals(MSG.product())){
					for(String property : ProductUtils.requiredList()){
						Label label = new Label(property);
						label.addStyleName("aon-info-rest-template");
						vp.add(label);
					}
				}
				else if(lb.getItemText(lb.getSelectedIndex()).equals(MSG.stock())){
					for(String property : StockUtils.requiredList()){
						Label label = new Label(property);
						label.addStyleName("aon-info-rest-template");
						vp.add(label);
					}
				}
				else if(lb.getItemText(lb.getSelectedIndex()).equals(MSG.fee())){
					for(String property : FeeUtils.requiredList()){
						Label label = new Label(property);
						label.addStyleName("aon-info-rest-template");
						vp.add(label);
					}
				}
				else if(lb.getItemText(lb.getSelectedIndex()).contains(MSG.inventory())){
					for(String property : InventoryUtils.requiredList(closed)){
						Label label = new Label(property);
						label.addStyleName("aon-info-rest-template");
						vp.add(label);
					}
				} else bool = false;
				if(bool){
					popup = new PopupPanel();
					popup.setWidget(vp);
					popup.setStyleName("aon-popup-aux-template");
					popup.setPopupPosition(event.getNativeEvent().getClientX(),event.getNativeEvent().getClientY());
					popup.show();
				}
			}				
		};
	}

	public ChangeHandler changeHandler(){
		ChangeHandler ch = new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				handler();
			}
		};
		return ch;
	}

	
	private ClickHandler removeClickHandler() {
		ClickHandler ch = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				flex_table.removeRow(column+1);
				column--;
				ListBox l =(ListBox)flex_table.getWidget(column+1, 1);
				l.setEnabled(true);

				list_box = new ListBox();
				list_box.addItem("-");
				for (Integer j = 0; j<l.getItemCount();j++) {
					list_box.addItem(l.getItemText(j));	
				}
				
				handler = l.addChangeHandler(changeHandler());
				HorizontalPanel h = new HorizontalPanel();
				if(flex_table.getRowCount()>3){
					Button b = new Button("");
					b.setStyleName("aon-finding-toolbar-item-template aon-search-minus");
					b.addClickHandler(removeClickHandler());
					h.add(b);
				}
				Button b2 = new Button("");
				b2.setStyleName("aon-finding-toolbar-item-template aon-search-add");
				b2.addClickHandler(addClickHandler());
				h.add(b2);
				
				flex_table.setWidget(column+1,2, h);
				flex_table.getCellFormatter().setStyleName(column+1, 2, "aon-panelGrid-aux");	
			}
		};
		return ch;
	}
	
	private ClickHandler addClickHandler(){
		ClickHandler ch = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				handler();
			}
		};
		return ch;
	}
	
	private void handler(){
		ListBox lbc = (ListBox) flex_table.getWidget(column+1, 1);
		Integer i = lbc.getSelectedIndex();
		Boolean libre = lbc.getItemText(i).equals(MSG.freeText());
		if(lbc.getItemText(0).equals("-")){
			lbc.removeItem(0);
			if(!libre){
				list_box.removeItem(i);
			}
		}
		else{
			if(!libre)
				list_box.removeItem(i+1);
		}
		
		handler.removeHandler();
		
		ListBox lb = new ListBox();
		for(Integer k = 0;k< list_box.getItemCount();k++){
			lb.addItem(list_box.getItemText(k));
		}
		handler  = lb.addChangeHandler(changeHandler());
		if(column < max-1){
			lbc.setEnabled(false);
			flex_table.setWidget(column+1, 2, new Label(""));

			column++;
			Button b = new Button("");
			b.setStyleName("aon-finding-toolbar-item-template aon-search-minus");
			b.addClickHandler(removeClickHandler());
		
			flex_table.setWidget(column+1, 0, new Label(MSG.column(column)));
			flex_table.setWidget(column+1, 1, lb);
			flex_table.setWidget(column+1, 2, b);
		
			flex_table.getCellFormatter().setStyleName(column+1, 0, "aon-panelGrid-odd");
			flex_table.getCellFormatter().setStyleName(column+1, 1, "aon-panelGrid-even");
			flex_table.getCellFormatter().setStyleName(column+1, 2, "aon-panelGrid-aux");				
		}
	}
	
	
	ListBox list_box;
	Integer max;
	private void listBox(String type){
		LinkedList<String> v = new LinkedList<String>();
		if(type.equals(MSG.product()))
			v = ProductUtils.productList();
		else if(type.equals(MSG.stock()))
			v = StockUtils.stockList();
		else if(type.equals(MSG.fee()))
			v = FeeUtils.feeList();
		else if(type.equals(MSG.closedInventory()))
			v = InventoryUtils.inventoryCloseList();
		else if(type.equals(MSG.valuedInventory()))
			v = InventoryUtils.inventoryList();
		list_box = new ListBox();
		list_box.addItem("-");
		for(String s : v){
			list_box.addItem(s);
		}
		max= list_box.getItemCount();
	}
	
	ListBox list_box_edit;
	private void listBoxEdit(String type){
		LinkedList<String> v = new LinkedList<String>();
		if(type.equals(MSG.product()))
			v = ProductUtils.productList();
		else if(type.equals(MSG.stock()))
			v = StockUtils.stockList();
		else if(type.equals(MSG.fee()))
			v = FeeUtils.feeList();
		else if(type.equals(MSG.closedInventory()))
			v = InventoryUtils.inventoryList();
		else if(type.equals(MSG.valuedInventory()))
			v = InventoryUtils.inventoryCloseList();
		list_box_edit = new ListBox();
		for(String s : v){
			list_box_edit.addItem(s);
		}
	}
	
	private void optionalListBox(Integer index, String type){
		LinkedList<String> v = new LinkedList<String>();
		if(type.equals(MSG.product()))
			v = ProductUtils.productOptionalList();
		else if(type.equals(MSG.stock()))
			v = StockUtils.stockOptionalList();
		else if(type.equals(MSG.fee()))
			v = FeeUtils.feeOptionalList();
		else if(type.equals(MSG.closedInventory()))
			v = InventoryUtils.inventoryCloseOptionalList();
		else if(type.equals(MSG.valuedInventory()))
			v = InventoryUtils.inventoryOptionalList();
		list_box = new ListBox();
		list_box.addItem("-");
		for(String s : v){
			list_box.addItem(s);
		}
		max= list_box.getItemCount() + index -2;
	}
	
	long progress = 10;
	Integer rowAux;
	String urlAux;
	
	public  SingleUploader newUploader(SingleUploader up,String url, Integer row){
		rowAux = row;urlAux = url;
		final SingleUploader upload;
       	if(up==null){
       		 upload=  new SingleUploader(FileInputType.BROWSER_INPUT.with(FileInputType.LABEL.getInstance()));
       	}
       	else{
       		 upload = up;
       	}
       	upload.setAutoSubmit(true);
        upload.setServletPath(url + "/gwt_upload");
        
        upload.getForm().getWidget().getElement().getChild(1).removeFromParent();
        upload.getForm().setAction(url + "/gwt_upload");
        upload.getForm().setEncoding(FormPanel.ENCODING_MULTIPART);
        upload.getForm().setMethod(FormPanel.METHOD_POST);
        upload.setTitle("uploadFormElement");
        upload.avoidEmptyFiles(true);
       
        upload.addOnCancelUploadHandler(new OnCancelUploaderHandler() {
        	Integer row = rowAux;
        	String url = urlAux;
        	Dialog dialog = d;
        	@Override
			public void onCancel(IUploader uploader) {
        		SingleUploader upload = newUploader(null, url,row);
        		flex_table.setWidget(row, 1, upload);
        		if(dialog.getType().equals("editEcommerceTemplate") || dialog.getType().equals("importEcommerceTemplate")){
        			accept_button.setEnabled(false);
        		}
        		// reset out of TemplatesServlet!!!
			}
		});
        
        upload.addOnStatusChangedHandler(new OnStatusChangedHandler() {
		
			@Override
			public void onStatusChanged(IUploader uploader) {
				if(upload.getStatus() != Status.SUCCESS){
			
					upload.getStatusWidget().setProgress(progress, 100);
			
				}
				else{
					upload.getStatusWidget().setProgress(100, 100);
				}	
				progress=progress+20;
			}
		});

        upload.addOnStartUploadHandler(new OnStartUploaderHandler() {
			
			@Override
			public void onStart(IUploader uploader) {
				upload.getStatusWidget().setVisible(true);
			}
		});
        
        upload.addOnFinishUploadHandler(new OnFinishUploaderHandler() {
			
			@Override
			public void onFinish(IUploader uploader) {
				upload.getStatusWidget().setProgress(100, 100);
				upload.getStatusWidget().setStatus(Status.DONE);
				upload.getStatusWidget().setVisible(true);
				progress = 0;		
				accept_button.setEnabled(true);
			}
		});
        upload.getForm().addSubmitCompleteHandler(new SubmitCompleteHandler() {
			
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				upload.getForm().getWidget().getElement().getChild(0).removeFromParent();				
			}
		});
        
        return upload;
	}
	
	public void flexTableCss(){
		for (int i = 0; i < flex_table.getRowCount(); i++) {
			for (int j = 0; j < flex_table.getCellCount(i); j++) {
				if ((j % 2) == 0) {
					flex_table.getCellFormatter().setStyleName(i, j, "aon-panelGrid-odd");
				} else {
					flex_table.getCellFormatter().setStyleName(i, j, "aon-panelGrid-even");
				}
			}
		}
	}
	
	public Integer mandatoryWidget(Integer index, String type) {
		Integer i = index;
		if(type.equalsIgnoreCase(MSG.product())){
			for(String property : ProductUtils.requiredList())
				setLabel(i++, property);
			return i++;
		} else if(type.equalsIgnoreCase(MSG.stock())){
			for(String property : StockUtils.requiredList())
				setLabel(i++, property);
			return i++;
		} else if(type.equalsIgnoreCase(MSG.fee())){
			for(String property : FeeUtils.requiredList())
				setLabel(i++, property);
			return i++;
		} else if(type.equalsIgnoreCase(MSG.closedInventory())){
			for(String property : InventoryUtils.requiredList(true))
				setLabel(i++, property);
			return i++;
		} else if(type.equalsIgnoreCase(MSG.valuedInventory())){
			for(String property : InventoryUtils.requiredList(false))
				setLabel(i++, property);
			return i++;
		} else return index;
	}
	
	public void setLabel(Integer index, String name){
		flex_table.setWidget(index, 0, new Label(MSG.column(column)));
		flex_table.setWidget(index, 1, new Label(name));
		flex_table.setWidget(index, 2, new Label(""));
		
		flex_table.getCellFormatter().setStyleName(index, 0, "aon-panelGrid-odd");
		flex_table.getCellFormatter().setStyleName(index, 1, "aon-panelGrid-even");
		flex_table.getCellFormatter().setStyleName(index, 2, "aon-panelGrid-aux");	
		column++;
	}
}