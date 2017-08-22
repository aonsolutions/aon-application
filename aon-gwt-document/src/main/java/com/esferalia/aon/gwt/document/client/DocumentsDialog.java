package com.esferalia.aon.gwt.document.client;

import java.util.List;
import java.util.TreeMap;

import com.esferalia.aon.gwt.common.client.widget.CustomDialogB;
import com.esferalia.aon.gwt.document.shared.Category;
import com.esferalia.aon.gwt.document.shared.Dialog;
import com.esferalia.aon.gwt.document.shared.FileInfo;
import com.esferalia.aon.gwt.document.shared.Lists;
import com.esferalia.aon.gwt.document.shared.PasswordGenerator;
import com.esferalia.aon.gwt.document.shared.Scope;
import com.esferalia.aon.gwt.document.shared.Tag;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnCancelUploaderHandler;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStartUploaderHandler;
import gwtupload.client.IUploader.OnStatusChangedHandler;
import gwtupload.client.MultiUploader;
import gwtupload.client.SingleUploader;

public abstract class DocumentsDialog extends CustomDialogB {

	interface Binder extends UiBinder<Widget, DocumentsDialog>{
		
	}
	private static final Binder binder = GWT.create(Binder.class);
	
	String email;

	Lists lists2;
	@UiField(provided = true) 
	FlexTable grid;
	FileInfo fileInfo;
	@UiField(provided = true)
	Label label;
	@UiField Button acceptButton;
	@UiField Button cancelButton;
	@UiField Button nextButton;
	Tag tag;
	Category cat;
	String n;
	Boolean son1;
	String domainSon;
	Integer num;
	Boolean confidentialUserDialog = null;
	public DocumentsDialog(Dialog dialog) {
		setWindowCode(PasswordGenerator.getPassword(10));
		
		if(dialog.getConfidentialUser()!= null) confidentialUserDialog = dialog.getConfidentialUser();
		num = 0;
		tree();
		if(dialog.getTag() != null){tag = dialog.getTag();n=tag.getName();}
		if(dialog.getCat() != null){cat = dialog.getCat();n=cat.getName();}
		lists2  = dialog.getLists();
		setFileInfo(dialog.getFileInfo());
		setCaption(dialog.getTitle());
		label = new Label();
		grid = new FlexTable();
		son1 = dialog.getSon();
		gridBuild(dialog);
		setWidget(binder.createAndBindUi(this));
		
		acceptButton.setFocus(true);
		acceptButton.setText(dialog.getAcceptButtonName());
		acceptButton.setVisible(dialog.getIsAcceptButton());

		acceptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onAccept();
			}
		});

		cancelButton.setText(dialog.getCancelButtonName());
		cancelButton.setVisible(dialog.getIsCancelButton());

		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCancel();
				
			}
		});

		if(dialog.getIsNextButton()) nextButton.setText(dialog.getNextButtonName());
		else nextButton.setText("");
		nextButton.setVisible(dialog.getIsNextButton());

		nextButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onNext();
				
			}
		});
	}
	
	@Override
	public void onClose() {
		onCancel();
	}
	
	protected abstract void onAccept();
	
	protected abstract void onCancel();

	protected abstract void onNext();
	
	private void gridBuild(Dialog dialog) {
		switch (dialog.getType()) {
			case "new": newFile(dialog);break;
			case "upload": uploadFile(dialog);break;
			case "edit": editFile(dialog);break;
			case "edit2": edit2();break;
			case "delete": deleteFile(dialog);break;
			case "delete2": delete2(dialog);break;
			case "search": searchFile(dialog);break;
			case "info": infoFile(dialog);break;
			case "share": shareFile();break;
			case "alert": alert(dialog);
			default:
				break;
		}
		
	}
	private void edit2() {
		grid.setStyleName("aon-panelGrid");
		grid.setWidth("400px");
		grid.setBorderWidth(1);
		grid.setCellSpacing(0);
		TextBox tb = new TextBox();
		tb.setStyleName("aon-inputText");
		tb.setText(n);
		grid.setWidget(0, 0, new Label("Descripci\u00f3n"));
		grid.setWidget(0, 1, tb);
		
		for (int i = 0; i < grid.getRowCount(); i++) {
			for (int j = 0; j < grid.getCellCount(i); j++) {
				if ((j % 2) == 0) {
					grid.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-odd");
				} else {
					grid.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-even");
				}
			}
		}	
	}
	
	HorizontalPanel h2;
	VerticalPanel vertical;
	HorizontalPanel categoryHP;
	VerticalPanel categoryVP;
	String url;
	
	
	private void newFile(Dialog dialog) {
		url = dialog.getBaseUrl();
		ListBox lb1 = new ListBox();
		lb1.addItem("-");
		ListBox lb2 = new ListBox();
		lb2.addItem("-");
		ListBox lb3 = new ListBox();
		lb3.addItem("-");

		for (Scope s : lists2.getScopeList().getList()) {
			if(s.getIsParent())
				lb3.addItem(Character.toString((char)9650)+s.getName());
			else if(s.getIsSon())
				lb3.addItem(Character.toString((char)9660)+s.getName());
			else lb3.addItem(s.getName());
		}
		/*if(dialog.getSon()){
			for(Scope s : lists2.getScopeListSon().getList()){
				lb3.addItem(Character.toString((char)9660)+s.getName());
			}
		}*/
		for (Tag t : lists2.getTagList().getList()) {
			if(t.getIsParent())
				lb2.addItem(Character.toString((char)9650)+t.getName());
			else if(t.getIsSon())
				lb2.addItem(Character.toString((char)9660)+t.getName());
			else lb2.addItem(t.getName());
		}
		for (Category c : lists2.getCategoryList()) {
			if(c.getIsParent())
				lb1.addItem(Character.toString((char)9650)+c.getName());
			else if(c.getIsSon())
				lb1.addItem(Character.toString((char)9660)+c.getName());
			else lb1.addItem(c.getName());
		}
		lb2.addChangeHandler(OneHandler2());

		/*final SingleUploader upload = newUploader(dialog.getUpload(),dialog.getBaseUrl());
        upload.addOnCancelUploadHandler(new OnCancelUploaderHandler() {
        	
			@Override
			public void onCancel(IUploader uploader) {
				final SingleUploader upload3 = newUploader(null,url) ;
					grid.setWidget(2, 1, upload3);

			}
		});*/
        
        MultiUploader mupload = new MultiUploader();
		mupload.setFileInputPrefix(getWindowCode());
		mupload.setAutoSubmit(true);
        mupload.setServletPath( dialog.getBaseUrl() + "/gwt_document_multiple_upload");
        mupload.setMaximumFiles(5);
        mupload.setTitle("multipleUploadFormElement");
		mupload.addOnFinishUploadHandler(new OnFinishUploaderHandler() {
			
			@Override
			public void onFinish(IUploader uploader) {
				num ++;
				if(num == 1){
					String s = uploader.getFileInput().getFilenames().get(0);
					Integer pos = s.contains(".") ? s.lastIndexOf(".") : s.length();
					TextBox tb = (TextBox) grid.getWidget(1, 1);
					if(tb.getText().equals("")){
						tb.setText(s.substring(0, pos));
					}
				} else if(num == 2) grid.removeRow(1);
			}
		});
		mupload.addOnCancelUploadHandler(new OnCancelUploaderHandler() {
			
			@Override
			public void onCancel(IUploader uploader) {
				num--;
				if(num == 1){
					Label l1 = (Label) grid.getWidget(1, 0);
					Widget w1 = grid.getWidget(1, 1);
				
					Label l3 = (Label) grid.getWidget(3, 0);
					Widget w3 = grid.getWidget(3, 1);					
					Label l4 = (Label) grid.getWidget(4, 0);
					Widget w4 = grid.getWidget(4, 1);
					Label l5 = (Label) grid.getWidget(5, 0);
					Widget w5 = grid.getWidget(5, 1);
					Label l6 = (Label) grid.getWidget(6, 0);
					Widget w6 = grid.getWidget(6, 1);
				
					
					if(confidentialUserDialog){
						Label l2 = (Label) grid.getWidget(2, 0);
    					Widget w2 = grid.getWidget(2, 1);
    					grid.setWidget(3, 0, l2);
    					grid.setWidget(3, 1, w2);
					}
					
					final TextBox tb1 = new TextBox();tb1.setStyleName("aon-inputText");
					tb1.setWidth("100%");
					tb1.addChangeHandler(new ChangeHandler() {
						@Override
						public void onChange(ChangeEvent event) {
							tb1.setStyleName("aon-inputText");
						}
					});
					grid.setWidget(1, 0, new Label("Descripci\u00f3n"));
					grid.setWidget(1, 1, tb1);
					
					grid.setWidget(2, 0, l1);
					grid.setWidget(2, 1, w1);
					
					
					grid.setWidget(4, 0, l3);
					grid.setWidget(4, 1, w3);
					
					grid.setWidget(5, 0, l4);
					grid.setWidget(5, 1, w4);
					
					grid.setWidget(6, 0, l5);
					grid.setWidget(6, 1, w5);
					
					grid.setWidget(7, 0, l6);
					grid.setWidget(7, 1, w6);
					
					grid.getCellFormatter().setStyleName(7, 0,
							"aon-panelGrid-odd");
					grid.getCellFormatter().setStyleName(7, 1,
							"aon-panelGrid-even");
				}
			}
		});
		
		grid.setStyleName("aon-panelGrid");
		grid.setWidth("400px");
		grid.setBorderWidth(1);
		grid.setCellSpacing(0);
		if(dialog.getSons().size() != 1){
		SuggestBox tb0 = new SuggestBox(Utils.createOracle(dialog.getSons()));
		tb0.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
			@Override
			public void onSelection(
					SelectionEvent<SuggestOracle.Suggestion> event) {
				String s = event.getSelectedItem().getDisplayString();
				String string2 = Utils.getOracleString(s);
				domainSon = string2;
				son1=true;
				ListBox auxlb = (ListBox) grid.getWidget(5, 1);
				removeFilter(auxlb);
				for (Category c : lists2.getCategoryListSon()) {
					if(c.getDomain().equals(string2)){
						auxlb.addItem(Character.toString((char)9660)+c.getName());
						grid.setWidget(5, 1, auxlb);
					}
				}
				VerticalPanel v = (VerticalPanel) grid.getWidget(6, 1);
				HorizontalPanel h = (HorizontalPanel) v.getWidget(0);
				ListBox auxlb2 = (ListBox) h.getWidget(0);
				removeFilter(auxlb2);
				for (Tag t : lists2.getTagListSon().getList()) {
					if(t.getDomain().equals(string2)){
						auxlb2.addItem(Character.toString((char)9660)+t.getName());
						
						h2 = new HorizontalPanel();
						vertical = new VerticalPanel();
						h2.add(auxlb2);
						vertical.add(h2);
						grid.setWidget(6, 1, vertical);	
					}
				}
				ListBox auxlb3 = (ListBox) grid.getWidget(7, 1);
				removeFilter(auxlb3);
				for (Scope scope : lists2.getScopeListSon().getList()) {
					if(scope.getDomain().equals(string2)){
						auxlb3.addItem(Character.toString((char)9660)+scope.getName());
						grid.setWidget(7, 1, auxlb3);
					}
				}
			}
			
		});
		
		tb0.setStyleName("aon-inputText");
		tb0.setWidth("100%");
		grid.setWidget(0, 0, new Label("Empresa"));
		grid.setWidget(0, 1, tb0);
		}
		final TextBox tb1 = new TextBox();tb1.setStyleName("aon-inputText");
		tb1.setWidth("100%");
		tb1.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				tb1.setStyleName("aon-inputText");
			}
		});
		grid.setWidget(1, 0, new Label("Descripci\u00f3n"));
		grid.setWidget(1, 1, tb1);

		grid.setWidget(2, 0, new Label("Archivo"));
		grid.setWidget(2, 1, mupload);
		if(confidentialUserDialog){
			CheckBox checkBox = new CheckBox();
			grid.setWidget(3, 0, new Label("Confidencial"));
			grid.setWidget(3, 1, checkBox);
		}
	    DateTimeFormat dateFormat = DateTimeFormat.getMediumDateFormat();
	    DateBox dateBox = new DateBox();
	    dateBox.setStyleName("aon-inputText");
	    dateBox.setFormat(new DateBox.DefaultFormat(dateFormat));
	    dateBox.getDatePicker().setYearArrowsVisible(true);
		grid.setWidget(4, 0, new Label("Fecha"));
		grid.setWidget(4, 1, dateBox);

		grid.setWidget(5, 0, new Label("Categor\u00eda"));
		grid.setWidget(5, 1, lb1);
		if(lb2.getItemCount() <= 2){
			VerticalPanel vp= new VerticalPanel();
			HorizontalPanel hp = new HorizontalPanel();
			hp.add(lb2);
			vp.add(hp);
			grid.setWidget(6, 0, new Label("Etiqueta"));
			grid.setWidget(6, 1, vp);	
		}
		else{
			h2 = new HorizontalPanel();
			vertical = new VerticalPanel();
			h2.add(lb2);
		
			vertical.add(h2);
			grid.setWidget(6, 0, new Label("Etiqueta"));
			grid.setWidget(6, 1, vertical);
			
		}

		grid.setWidget(7, 0, new Label("\u00c1mbito"));
		grid.setWidget(7, 1, lb3);

		for (int i = 0; i < grid.getRowCount(); i++) {
			for (int j = 0; j < grid.getCellCount(i); j++) {
				if ((j % 2) == 0) {
					grid.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-odd");
				} else {
					grid.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-even");
				}
			}
		}
	}
	
	private void uploadFile(Dialog dialog){
		final SingleUploader upload = newUploader(dialog.getUpload(),dialog.getBaseUrl());
        upload.addOnCancelUploadHandler(new OnCancelUploaderHandler() {
        	
			@Override
			public void onCancel(IUploader uploader) {
				final SingleUploader upload3 = newUploader(null,url) ;
				grid.setWidget(1, 1, upload3);
			}
		});

		grid.setStyleName("aon-panelGrid");
		grid.setWidth("400px");
		grid.setBorderWidth(1);
		grid.setCellSpacing(0);
		
		final TextBox tb1 = new TextBox();tb1.setStyleName("aon-inputText");
		tb1.setWidth("100%");
		tb1.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				tb1.setStyleName("aon-inputText");
			}
		});
		
		grid.setWidget(1, 0, new Label("Descripci\u00f3n"));
		grid.setWidget(1, 1, tb1);
		
		grid.setWidget(2, 0, new Label("Archivo"));
		grid.setWidget(2, 1, upload);
		
		ScrollPanel scroll = new ScrollPanel();
		scroll.setHeight("100px");
		scroll.add(sp);
		grid.setWidget(3, 0, new Label("Carpeta"));
		grid.setWidget(3, 1, scroll);
		

		for (int i = 0; i < grid.getRowCount(); i++) {
			for (int j = 0; j < grid.getCellCount(i); j++) {
				if ((j % 2) == 0) {
					grid.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-odd");
				} else {
					grid.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-even");
				}
			}
		}
	}
	
	private void editFile(Dialog dialog) {
		url = dialog.getBaseUrl();
		domainSon = dialog.getFileInfo().getDomain();
		FileInfo fi = dialog.getFileInfo();
		ListBox lb1 = new ListBox();
		lb1.addItem("-");
		ListBox lb2 = new ListBox();
		lb2.addItem("-");
		ListBox lb3 = new ListBox();
		lb3.addItem("-");
		for (Scope s : lists2.getScopeList().getList()) {
			if(s.getIsParent())
				lb3.addItem(Character.toString((char)9650)+s.getName());
			else if(s.getIsSon())
				lb3.addItem(Character.toString((char)9660)+s.getName());
			else lb3.addItem(s.getName());
		}
		
		if(dialog.getSon()){
			for(Scope s : lists2.getScopeListSon().getList()){
				if(s.getDomain().equals(dialog.getFileInfo().getDomain()))
					lb3.addItem(Character.toString((char)9660)+s.getName());
			}
		}
		for (Tag t : lists2.getTagList().getList()) {
			if(t.getIsParent())
				lb2.addItem(Character.toString((char)9650)+t.getName());
			else if(t.getIsSon())
				lb2.addItem(Character.toString((char)9660)+t.getName());
			else lb2.addItem(t.getName());
		}
		if(dialog.getSon()){
			for(Tag t : lists2.getTagListSon().getList()){
				if(t.getDomain().equals(dialog.getFileInfo().getDomain()))
					lb2.addItem(Character.toString((char)9660)+t.getName());
			}
		}
		for (Category c : lists2.getCategoryList()) {
			if(c.getIsParent())
				lb1.addItem(Character.toString((char)9650)+c.getName());
			else if(c.getIsSon())
				lb1.addItem(Character.toString((char)9660)+c.getName());
			else lb1.addItem(c.getName());
		}
		if(dialog.getSon()){
			for(Category c : lists2.getCategoryListSon()){
				if(c.getDomain().equals(dialog.getFileInfo().getDomain()))
					lb1.addItem(Character.toString((char)9660)+c.getName());
			}
		}
		lb2.addChangeHandler(OneHandler2());
		final SingleUploader upload = newUploader(dialog.getUpload(),dialog.getBaseUrl());
        upload.addOnCancelUploadHandler(new OnCancelUploaderHandler() {
        	
			@Override
			public void onCancel(IUploader uploader) {
				final SingleUploader upload3 = newUploader(null,url) ;
				grid.setWidget(1, 1, upload3);
			}
		});		
		grid.setStyleName("aon-panelGrid");
		grid.setWidth("400px");
		grid.setBorderWidth(1);
		grid.setCellSpacing(0);
		
		if(!dialog.getMultiple()){
			TextBox tb1 = new TextBox();tb1.setStyleName("aon-inputText");
			tb1.setWidth("100%");
			grid.setWidget(0, 0, new Label("Descripci\u00f3n"));
			tb1.setText(fi.getTitle());
			grid.setWidget(0, 1, tb1);

			grid.setWidget(1, 0, new Label("Archivo"));
			grid.setWidget(1, 1, upload);
		}
		
		if(confidentialUserDialog){
			CheckBox checkBox = new CheckBox();
			grid.setWidget(2, 0, new Label("Confidencial"));
			if(!dialog.getMultiple()) checkBox.setValue(fi.getConfidential());
			grid.setWidget(2, 1, checkBox);
		}
		
	    DateTimeFormat dateFormat = DateTimeFormat.getMediumDateFormat();
	    DateBox dateBox = new DateBox();
	    dateBox.setStyleName("aon-inputText");
	    dateBox.setFormat(new DateBox.DefaultFormat(dateFormat));
	    dateBox.getDatePicker().setYearArrowsVisible(true);
		grid.setWidget(3, 0, new Label("Fecha"));
		if(!dialog.getMultiple()) dateBox.setValue(fi.getDate());
		grid.setWidget(3, 1, dateBox);

		grid.setWidget(4, 0, new Label("Categor\u00eda"));
		if(!dialog.getMultiple()){
			for (int i = 0; i<lb1.getItemCount();i++) {
				String s1 = lb1.getItemText(i);
				if(s1.substring(0,1).equals(Character.toString((char)9650)) || s1.substring(0,1).equals(Character.toString((char)9660))){
					s1 = s1.substring(1);
				}
				if(s1.equals(fi.getCategoryStr())){
					lb1.setItemSelected(i, true);
				}
			}
		}
		grid.setWidget(4, 1, lb1);

		if(lb2.getItemCount() <= 2){
			VerticalPanel vp= new VerticalPanel();
			HorizontalPanel hp = new HorizontalPanel();
			
			grid.setWidget(5, 0, new Label("Etiqueta"));
			if(!dialog.getMultiple()){
				for (int i = 0; i<lb2.getItemCount();i++) {
					String s2 = lb2.getItemText(i);
					if(s2.substring(0,1).equals(Character.toString((char)9650)) || s2.substring(0,1).equals(Character.toString((char)9660))){
						s2 = s2.substring(1);
					}
					if(s2.equals(fi.getTagsStr())){
						lb2.setItemSelected(i, true);
					}
				}
			}
			hp.add(lb2);
			vp.add(hp);
			grid.setWidget(5, 1, vp);	
		}
		else{
			Integer size;
			if(!dialog.getMultiple())
				size = fi.getTags().size();
			else size = 1;
			if(size>1){
				vertical= new VerticalPanel();
				ListBox[] lbs = new ListBox[size];
				for(Integer k = 0; k< size ; k++){
					lbs[k] = new ListBox();
					lbs[k].addItem("-");
					for (Tag t : lists2.getTagList().getList()) {
						if(t.getIsParent())
							lbs[k].addItem(Character.toString((char)9650)+t.getName());
						else if(t.getIsSon())
							lbs[k].addItem(Character.toString((char)9660)+t.getName());
						else lbs[k].addItem(t.getName());
					}
					HorizontalPanel hp = new HorizontalPanel();
					for (int i = 0; i<lbs[k].getItemCount();i++) {
						String s2 = lbs[k].getItemText(i);
						if(s2.substring(0,1).equals(Character.toString((char)9650)) || s2.substring(0,1).equals(Character.toString((char)9660))){
							s2 = s2.substring(1);
						}
						if(s2.equals(fi.getTags().get(k).getName())){
							lbs[k].setItemSelected(i, true);
						}
					}
					
					
					if(k== size-1){
						hp.add(lbs[k]);
						Button bMenos = new Button();
						bMenos.setStyleName("aon-finding-toolbar-item aon-search-minus");
						bMenos.addClickHandler(menosHandler2());
						hp.add(bMenos);
						if(size < lists2.getTagList().getList().size()){
							Button mas = new Button("");
							mas.setStyleName("aon-finding-toolbar-item aon-search-add");
							mas.addClickHandler(masHandler2());
							hp.add(mas);
						}
					}
					else{
						lbs[k].setEnabled(false);
						hp.add(lbs[k]);
					}
					hp.addStyleName("aon-gwt-tags-popup");

					vertical.add(hp);
					
				}

			}
			else{
				vertical= new VerticalPanel();

				h2 = new HorizontalPanel();
				if(!dialog.getMultiple()){

					for (int i = 0; i<lb2.getItemCount();i++) {
						String s2 = lb2.getItemText(i);
						if(s2.substring(0,1).equals(Character.toString((char)9650))||s2.substring(0,1).equals(Character.toString((char)9660)))
							s2 = s2.substring(1);
						if(s2.equals(fi.getTagsStr())){
							lb2.setItemSelected(i, true);
						}
					}
				}
				h2.add(lb2);
				if(lb2.getSelectedIndex()!=0){
					Button mas = new Button("");
					mas.setStyleName("aon-finding-toolbar-item aon-search-add");
					mas.addClickHandler(masHandler2());
					h2.add(mas);
				}
				vertical.add(h2);
			}
			grid.setWidget(5, 0, new Label("Etiqueta"));
			grid.setWidget(5, 1, vertical);
		}
		
		grid.setWidget(6, 0, new Label("\u00c1mbito"));
		if(fi.getScope()!=null){
			if(!dialog.getMultiple()){

				for (int i = 0; i<lb3.getItemCount();i++) {
					String s3 = lb3.getItemText(i);
					if(s3.substring(0,1).equals(Character.toString((char)9650)) || s3.substring(0,1).equals(Character.toString((char)9660))){
						s3 = s3.substring(1);
					}
					if(s3.equals(fi.getScope().getName())){
						lb3.setItemSelected(i, true);
					}
				}
			}
		}
		grid.setWidget(6, 1, lb3);

		for (int i = 0; i < grid.getRowCount(); i++) {
			for (int j = 0; j < grid.getCellCount(i); j++) {
				if ((j % 2) == 0) {
					grid.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-odd");
				} else {
					grid.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-even");
				}
			}
		}
	}
	
	private void delete2(Dialog dialog) {
		String name = "";
		if(dialog.getTag() != null) name = "la etiqueta "+dialog.getTag().getName();
		else if(dialog.getCat() != null) name = "la categor\u00eda "+dialog.getCat().getName();
		label.setText("Est\u00e1s seguro de eliminar "+name);
	}
	
	private void deleteFile(Dialog dialog) {
		if(dialog.getMultiple())
			label.setText("Est\u00e1s seguro de eliminar los "+dialog.getNum().toString()+" archivos ");
		else label.setText("Est\u00e1s seguro de eliminar el archivo "+dialog.getFileInfo().getTitle());
	}
	
	private void alert(Dialog dialog){
		switch (dialog.getTitle()) {
			case "Editar Archivo": label.setText("No tiene permisos para editar el archivo "+dialog.getFileInfo().getTitle());break;
			case "Borrar Archivo": label.setText("No tiene permisos para borrar el archivo "+dialog.getFileInfo().getTitle());;break;
			case "Permisos":label.setText("No tiene permisos para modificar o borrar");break;
			case "Nuevo Archivo": label.setText("No tiene permisos para crear un nuevo archivo"); break;
			default:
				break;
		}
	}
	
	Boolean bool;
	private void searchFile(Dialog dialog) {
		domainSon = dialog.getSearchDomain();
		ListBox lb1 = new ListBox();
		lb1.addItem("-");
		ListBox lb2 = new ListBox();
		lb2.addItem("-");
		ListBox lb3 = new ListBox();
		lb3.addItem("-");

		for (Scope s : lists2.getScopeList().getList()) {
			if(s.getIsParent())
				lb3.addItem(Character.toString((char)9650)+s.getName());
			else if(s.getIsSon())
				lb3.addItem(Character.toString((char)9660)+s.getName());
			else lb3.addItem(s.getName());
		}
		if(dialog.getSon()){
			for(Scope s : lists2.getScopeListSon().getList()){
				if(s.getDomain().equals(dialog.getSearchDomain()))
					lb3.addItem(Character.toString((char)9660)+s.getName());
			}
		}

		for (Tag t : lists2.getTagList().getList()) {
			if(t.getIsParent())
				lb2.addItem(Character.toString((char)9650)+t.getName());
			else if(t.getIsSon())
				lb2.addItem(Character.toString((char)9660)+t.getName());
			else lb2.addItem(t.getName());
		}
		if(dialog.getSon()){
			for(Tag t : lists2.getTagListSon().getList()){
				if(t.getDomain().equals(dialog.getSearchDomain()))
					lb2.addItem(Character.toString((char)9660)+t.getName());
			}
		}
		for (Category c : lists2.getCategoryList()) {
			if(c.getIsParent())
				lb1.addItem(Character.toString((char)9650)+c.getName());
			else if(c.getIsSon())
				lb1.addItem(Character.toString((char)9660)+c.getName());
			else lb1.addItem(c.getName());
		}
		if(dialog.getSon()){
			for(Category c : lists2.getCategoryListSon()){
				if(c.getDomain().equals(dialog.getSearchDomain()))
					lb1.addItem(Character.toString((char)9660)+c.getName());
			}
		}
		
		lb2.addChangeHandler(OneHandler());
		
		/********************************/
		lb1.addChangeHandler(categoryOneHandler());

		
		grid.setStyleName("aon-panelGrid");
		grid.setWidth("400px");
		grid.setBorderWidth(1);
		grid.setCellSpacing(0);
		if(dialog.getSons().size() != 1){
			SuggestBox tb0 = new SuggestBox(Utils.createOracle(dialog.getSons()));
			tb0.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
				@Override
				public void onSelection(
					SelectionEvent<SuggestOracle.Suggestion> event) {
					String s = event.getSelectedItem().getDisplayString();
					/*Integer pos1 = s.indexOf('>');
					Integer pos2 = s.indexOf('/')-1;
					String s2 = s.substring(pos2);
					Integer pos3 = s2.indexOf('>');
					String string = s.substring(pos1+1,pos2)+s2.substring(pos3+1);*/
					String string2 = Utils.getOracleString(s);
					domainSon = string2;
					son1 = true;
					ListBox auxlb = (ListBox) grid.getWidget(4, 1);
					removeFilter(auxlb);
					for (Category c : lists2.getCategoryListSon()) {
						if(c.getDomain().equals(string2)){
							auxlb.addItem(Character.toString((char)9660)+c.getName());
							grid.setWidget(4, 1, auxlb);
						}
					}
					VerticalPanel v = (VerticalPanel) grid.getWidget(5, 1);
					HorizontalPanel h = (HorizontalPanel) v.getWidget(0);
					ListBox auxlb2 = (ListBox) h.getWidget(0);
					removeFilter(auxlb2);
					for (Tag t : lists2.getTagListSon().getList()) {
						if(t.getDomain().equals(string2)){
					
						/*if (bool) {
							ListBox lb = (ListBox) grid.getWidget(5, 1);
							lb.addItem(Character.toString((char)9660)+t.getName());
							grid.setWidget(5, 1, lb);
						} else {*/
							auxlb2.addItem(Character.toString((char)9660)+t.getName());
								
							h2 = new HorizontalPanel();
							vertical = new VerticalPanel();
							h2.add(auxlb2);
							vertical.add(h2);
							grid.setWidget(5, 1, vertical);
						}					
					//}
					}
					ListBox auxlb3 = (ListBox) grid.getWidget(6, 1);
					removeFilter(auxlb3);
					for (Scope scope : lists2.getScopeListSon().getList()) {
						if(scope.getDomain().equals(string2)){
							auxlb3.addItem(Character.toString((char)9660)+scope.getName());
							grid.setWidget(6, 1, auxlb3);
						}
					}
				}
			});
			tb0.setStyleName("aon-inputText");
			tb0.setWidth("100%");
			grid.setWidget(0, 0, new Label("Empresa"));
			grid.setWidget(0, 1, tb0);
		}
		TextBox tb1 = new TextBox();
		tb1.setStyleName("aon-inputText");
		tb1.setWidth("100%");
		tb1.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER){
					acceptButton.click();
				}
			}
		});
		grid.setWidget(1, 0, new Label("Descripci\u00f3n"));
		grid.setWidget(1, 1, tb1);
		if(confidentialUserDialog){
			ListBox lbc = new ListBox();
			lbc.addItem("-");
			lbc.addItem("Si");
			lbc.addItem("No");
			grid.setWidget(2, 0, new Label("Confidencial"));
			grid.setWidget(2, 1, lbc);
		}
		DateTimeFormat dateFormat = DateTimeFormat.getMediumDateFormat();
		DateBox dateBox = new DateBox();
		dateBox.setStyleName("aon-inputText");
		dateBox.setFormat(new DateBox.DefaultFormat(dateFormat));
		dateBox.getDatePicker().setYearArrowsVisible(true);
		grid.setWidget(3, 0, new Label("Fecha"));
		grid.setWidget(3, 1, dateBox);

		
		bool = lb1.getItemCount() <= 2;
		if (lb1.getItemCount() <= 2) {
			VerticalPanel vp= new VerticalPanel();
			HorizontalPanel hp = new HorizontalPanel();
			hp.add(lb1);
			vp.add(hp);
			grid.setWidget(4, 0, new Label("Categor\u00eda"));
			grid.setWidget(4, 1, vp);
		} else {
			categoryHP = new HorizontalPanel();
			categoryVP = new VerticalPanel();
			categoryHP.add(lb1);
			categoryVP.add(categoryHP);
			grid.setWidget(4, 0, new Label("Categor\u00eda"));
			grid.setWidget(4, 1, categoryVP);
		}
		
		
		bool = lb2.getItemCount() <= 2;
		if (lb2.getItemCount() <= 2) {
			VerticalPanel vp= new VerticalPanel();
			HorizontalPanel hp = new HorizontalPanel();
			hp.add(lb2);
			vp.add(hp);
			grid.setWidget(5, 0, new Label("Etiqueta"));
			grid.setWidget(5, 1, vp);
		} else {

			h2 = new HorizontalPanel();
			vertical = new VerticalPanel();
			h2.add(lb2);

			vertical.add(h2);
			grid.setWidget(5, 0, new Label("Etiqueta"));
			grid.setWidget(5, 1, vertical);
		}
		grid.setWidget(6, 0, new Label("\u00c1mbito"));
		grid.setWidget(6, 1, lb3);

		for (int i = 0; i < grid.getRowCount(); i++) {
			for (int j = 0; j < grid.getCellCount(i); j++) {
				if ((j % 2) == 0) {
					grid.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-odd");
				} else {
					grid.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-even");
				}
			}
		}
	
	}
	
	
	private void infoFile(Dialog dialog) {
		FileInfo object = dialog.getFileInfo();
		grid.setStyleName("aon-panelGrid");
		grid.setWidth("300px");
		grid.setBorderWidth(1);
		grid.setCellSpacing(0);
		if(object.getIsDrive()){
			grid.setWidget(0, 0, new Label("Descripci\u00f3n"));
			grid.setWidget(0, 1, new Label(object.getTitle()));

			grid.setWidget(1, 0, new Label("Fecha"));
			grid.setWidget(1, 1, new Label(object.getDateStr()));

			grid.setWidget(2, 0, new Label("Tama\u00f1o"));
			grid.setWidget(2, 1, new Label(object.getSizeStr()));
			
			grid.setWidget(3, 0, new Label("Mime Type"));
			//if(object.getMimetype() != null)grid.setWidget(3, 1, new Label(MimeType.values()[object.getMimetype()].getExtension().toString()));
			/*else*/ grid.setWidget(3, 1, new Label("-"));
			
			grid.setWidget(4, 0, new Label("Enlace"));
			grid.setWidget(4, 1, new Label(""));
			
			idoc.copyLink(object, dialog.getBaseUrl(), new AsyncCallback<String>() {
				
				@Override
				public void onSuccess(String result) {
					grid.setWidget(4, 1, new Label(result));
				}
				
				@Override
				public void onFailure(Throwable caught) {}
			});
		}
		else{

			grid.setWidget(0, 0, new Label("Descripci\u00f3n"));
			grid.setWidget(0, 1, new Label(object.getTitle()));
			if(confidentialUserDialog){
				grid.setWidget(1, 0, new Label("Confidencial"));
				grid.setWidget(1, 1, new Label(object.getConfidential() ? "Si" : "No"));
			}
			grid.setWidget(2, 0, new Label("Fecha"));
			grid.setWidget(2, 1, new Label(object.getDateStr()));

			grid.setWidget(3, 0, new Label("Categor\u00eda"));
			grid.setWidget(3, 1, new Label(object.getCategoryStr()));

			grid.setWidget(4, 0, new Label("Etiquetas"));
			grid.setWidget(4, 1, new Label(object.getTagsStr()));

			grid.setWidget(5, 0, new Label("\u00c1mbito"));
			if(object.getScope()!=null)grid.setWidget(5, 1, new Label(object.getScope().getName()));
			else grid.setWidget(5, 1, new Label("-"));
			
			grid.setWidget(6, 0, new Label("Tama\u00f1o"));
			grid.setWidget(6, 1, new Label(object.getSizeStr()));
			
			grid.setWidget(7, 0, new Label("Mime Type"));
			//if(object.getMimetype() != null)grid.setWidget(7, 1, new Label(MimeType.values()[object.getMimetype()].getExtension().toString()));
			/*else*/ grid.setWidget(7, 1, new Label("-"));
			
			grid.setWidget(8, 0, new Label("Enlace"));
			grid.setWidget(8, 1, new Label(""));

			grid.setWidget(9, 0, new Label("Creado por"));
			grid.setWidget(9, 1, new Label(object.getCreationUser()));
			
			grid.setWidget(10, 0, new Label("Fecha Creaci\u00f3n"));
			grid.setWidget(10, 1, new Label(object.getCreationDateStr()));
			
			grid.setWidget(11, 0, new Label("Modificado por"));
			grid.setWidget(11, 1, new Label(object.getModificationUser()));
			
			grid.setWidget(12, 0, new Label("Fecha Modificaci\u00f3n"));
			grid.setWidget(12, 1, new Label(object.getModificationDateStr()));
			idoc.copyLink(object, dialog.getBaseUrl(), new AsyncCallback<String>() {
				
				@Override
				public void onSuccess(String result) {
					grid.setWidget(8, 1, new Label(result));
				}
				
				@Override
				public void onFailure(Throwable caught) {}
			});
			
		}	
		for (int i = 0; i < grid.getRowCount(); i++) {
			for (int j = 0; j < grid.getCellCount(i); j++) {
				if ((j % 2) == 0) {
					grid.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-odd");
				} else {
					grid.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-even");
				}
			}
		}	
	}
	
	
	private void shareFile() {		
		grid.setStyleName("aon-panelGrid");
		grid.setWidth("300px");
		grid.setBorderWidth(1);
		grid.setCellSpacing(0);
		TextBox email = new TextBox();
		email.setStyleName("aon-inputText");
		email.setWidth("100%");
		grid.setWidget(0, 0, new Label("Email"));
		grid.setWidget(0, 1, email);
		
		for (int i = 0; i < grid.getRowCount(); i++) {
			for (int j = 0; j < grid.getCellCount(i); j++) {
				if ((j % 2) == 0) {
					grid.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-odd");
				} else {
					grid.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-even");
				}
			}
		}	
	}
	

	//--------------------Gets & Sets
	
	public FlexTable getGrid() {
		return grid;
	}

	
	public void setGrid(FlexTable grid) {
		this.grid = grid;
	}

	
	public FileInfo getFileInfo() {
		return fileInfo;
	}

	
	public void setFileInfo(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}
	
	Attach attach;
	public Attach getAttach() {
		return attach;
	}

	
	public void setFileInfo(Attach attacj) {
		this.attach = attach;
	}
	
	
	//-------------------- Utils
	MultiWordSuggestOracle oracleSons;
	long progress = 10;
	
	public ChangeHandler OneHandler(){
		ChangeHandler ch = new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ListBox lb = (ListBox) h2.getWidget(0);
				String s = null;
				for (int i = 0; i < lb.getItemCount(); i++) {
					if (lb.isItemSelected(i)) {
						s = lb.getValue(i);
					}
				}
				
				if(h2.getWidgetCount() == 1){
					Button mas = new Button("");
					mas.setStyleName("aon-finding-toolbar-item aon-search-add");
					mas.addClickHandler(masHandler());
					h2.add(mas);
				}
				if(s.equals("-")){
					h2.remove(1);
				}				
			}
		};
		return ch;

	}
	public ChangeHandler categoryOneHandler(){
		return new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				ListBox lb = (ListBox) categoryHP.getWidget(0);
				String s = null;
				for (int i = 0; i < lb.getItemCount(); i++) {
					if (lb.isItemSelected(i)) {
						s = lb.getValue(i);
					}
				}
				
				if(categoryHP.getWidgetCount() == 1){
					Button mas = new Button("");
					mas.setStyleName("aon-finding-toolbar-item aon-search-add");
					mas.addClickHandler(categoryMasHandler());
					categoryHP.add(mas);
				}
				if(s.equals("-")){
					categoryHP.remove(1);
				}				
			}
		};
	}
	
	public ChangeHandler OneHandler2(){
		ChangeHandler ch = new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ListBox lb = (ListBox) h2.getWidget(0);
				String s = null;
				for (int i = 0; i < lb.getItemCount(); i++) {
					if (lb.isItemSelected(i)) {
						s = lb.getValue(i);
					}
				}
				
				if(h2.getWidgetCount() == 1){
					Button mas = new Button("");
					mas.setStyleName("aon-finding-toolbar-item aon-search-add");
					mas.addClickHandler(masHandler2());
					h2.add(mas);
				}
				if(s.equals("-")){
					h2.remove(1);
				}				
			}
		};
		return ch;

	}
	
	
	public ChangeHandler TwoHandler(){
		ChangeHandler ch = new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ListBox lb = (ListBox) h2.getWidget(1);
				String s = null;
				for (int i = 0; i < lb.getItemCount(); i++) {
					if (lb.isItemSelected(i)) {
						s = lb.getValue(i);
					}
				}
				if(h2.getWidgetCount() == 3){
					Button bMas = new Button();
					bMas.setStyleName("aon-finding-toolbar-item aon-search-add");
					bMas.addClickHandler(masHandler());
					
					if(vertical.getWidgetCount()<lb.getItemCount()-1){//lists2.getTagList().getLength()){
						h2.add(bMas);
					}
				}
				if(s.equals("-")){
					h2.remove(3);
				}
			}
		};
		return ch;

	}
	
	public ChangeHandler categoryTwoHandler(){
		return new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				ListBox lb = (ListBox) categoryHP.getWidget(0);
				String s = null;
				for (int i = 0; i < lb.getItemCount(); i++) {
					if (lb.isItemSelected(i)) {
						s = lb.getValue(i);
					}
				}
				if(categoryHP.getWidgetCount() == 2){
					Button bMas = new Button();
					bMas.setStyleName("aon-finding-toolbar-item aon-search-add");
					bMas.addClickHandler(categoryMasHandler());
					
					if(categoryVP.getWidgetCount()<lb.getItemCount()-1){//lists2.getTagList().getLength()){
						categoryHP.add(bMas);
					}
				}
				if(s.equals("-")){
					categoryHP.remove(2);
				}
			}
		};
	}
	
	public ChangeHandler TwoHandler2(){
		ChangeHandler ch = new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ListBox lb = (ListBox) h2.getWidget(0);
				String s = null;
				for (int i = 0; i < lb.getItemCount(); i++) {
					if (lb.isItemSelected(i)) {
						s = lb.getValue(i);
					}
				}
				if(h2.getWidgetCount() == 2){
					Button bMas = new Button();
					bMas.setStyleName("aon-finding-toolbar-item aon-search-add");
					bMas.addClickHandler(masHandler2());
					
					if(vertical.getWidgetCount()<lb.getItemCount()-1){//lists2.getTagList().getLength()){
						h2.add(bMas);
					}
				}
				if(s.equals("-")){
					h2.remove(2);
				}
			}
		};
		return ch;

	}
	
	
	
	public ClickHandler masHandler() {
		ClickHandler ch = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ListBox lb2 = new ListBox();
				lb2.addItem("-");
				for (Tag t : lists2.getTagList().getList()) {
					if(t.getIsParent())
						lb2.addItem(Character.toString((char)9650)+t.getName());
					else if(t.getIsSon())
						lb2.addItem(Character.toString((char)9660)+t.getName());
					else lb2.addItem(t.getName());
				}
				
				
				//TODO				
				if(son1){
					for(Tag t : lists2.getTagListSon().getList()){
						if(t.getDomain().equals(domainSon)){
							lb2.addItem(Character.toString((char)9660)+t.getName());
						}
					}
				}
				lb2.addChangeHandler(TwoHandler());
				ListBox AndOr = new ListBox();
				AndOr.addItem("Y");
				AndOr.addItem("O");
				h2 = new HorizontalPanel();
				h2.add(AndOr);
				h2.add(lb2);
				
				Button bMenos = new Button();
				bMenos.setStyleName("aon-finding-toolbar-item aon-search-minus");
				bMenos.addClickHandler(menosHandler());
				h2.add(bMenos);
				
				vertical.add(h2);
				VerticalPanel p = (VerticalPanel)grid.getWidget(5,1);
				HorizontalPanel hp =(HorizontalPanel)p.getWidget(p.getWidgetCount()-2);
				if(p.getWidgetCount()==2){
					ListBox l = (ListBox)hp.getWidget(0);
					l.setEnabled(false);
					hp.remove(1);
				}
				else {
					ListBox l = (ListBox)hp.getWidget(1);
					l.setEnabled(false);
					hp.remove(3);
					hp.remove(2);
				}
				h2.addStyleName("aon-gwt-tags-popup");
				p.add(h2);
				
				grid.setWidget(5, 1, p);
			}
		};
		
		return ch;
	}
	
	public ClickHandler categoryMasHandler() {
		ClickHandler ch = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ListBox lb2 = new ListBox();
				lb2.addItem("-");
				
				for (Category category : lists2.getCategoryList()) {
					if(category.getIsParent())
						lb2.addItem(Character.toString((char)9650)+category.getName());
					else if(category.getIsSon())
						lb2.addItem(Character.toString((char)9660)+category.getName());
					else lb2.addItem(category.getName());
				}
				
				//TODO				
				if(son1){
					for(Category category : lists2.getCategoryListSon()){
						if(category.getDomain().equals(domainSon)){
							lb2.addItem(Character.toString((char)9660)+category.getName());
						}
					}
				}
				lb2.addChangeHandler(categoryTwoHandler());
				
				categoryHP = new HorizontalPanel();
				categoryHP.add(lb2);
				
				Button bMenos = new Button();
				bMenos.setStyleName("aon-finding-toolbar-item aon-search-minus");
				bMenos.addClickHandler(categoryMenosHandler());
				categoryHP.add(bMenos);
				
				categoryVP.add(categoryHP);
				VerticalPanel p = (VerticalPanel)grid.getWidget(4,1);
				HorizontalPanel hp =(HorizontalPanel)p.getWidget(p.getWidgetCount()-2);
				if(p.getWidgetCount()==2){
					ListBox l = (ListBox)hp.getWidget(0);
					l.setEnabled(false);
					hp.remove(1);
				}
				else {
					ListBox l = (ListBox)hp.getWidget(0);
					l.setEnabled(false);
					hp.remove(2);
					hp.remove(1);
				}
				categoryHP.addStyleName("aon-gwt-tags-popup");
				p.add(categoryHP);
				
				grid.setWidget(4, 1, p);
			}
		};
		
		return ch;
	}
	
	public ClickHandler masHandler2() {
		ClickHandler ch = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ListBox lb2 = new ListBox();
				
				lb2.addChangeHandler(TwoHandler2());
				
				lb2.addItem("-");
				
				for (Tag t : lists2.getTagList().getList()) {
					if(t.getIsParent())
						lb2.addItem(Character.toString((char)9650)+t.getName());
					else if(t.getIsSon())
						lb2.addItem(Character.toString((char)9660)+t.getName());
					else lb2.addItem(t.getName());
				}
				
				//TODO
				if(son1){
					for(Tag t : lists2.getTagListSon().getList()){
						if(t.getDomain().equals(domainSon))
							lb2.addItem(Character.toString((char)9660)+t.getName());
					}
				}
				lb2.addChangeHandler(TwoHandler2());
				h2 = new HorizontalPanel();
				h2.add(lb2);
				
				
				Button bMenos = new Button();
				bMenos.setStyleName("aon-finding-toolbar-item aon-search-minus");
				bMenos.addClickHandler(menosHandler2());
				h2.add(bMenos);
				
				vertical.add(h2);
				VerticalPanel p = (VerticalPanel)grid.getWidget(5,1);
				HorizontalPanel hp =(HorizontalPanel)p.getWidget(p.getWidgetCount()-2);
				
					ListBox l = (ListBox)hp.getWidget(0);
					l.setEnabled(false);
					if(p.getWidgetCount()==2){
						hp.remove(1);
					}
					else {
						hp.remove(2);
						hp.remove(1);
					}
				
				h2.addStyleName("aon-gwt-tags-popup");
				p.add(h2);
				
				grid.setWidget(5, 1, p);
			}
		};
		
		return ch;
	}
	
	
	public ClickHandler menosHandler() {
		ClickHandler ch = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
			
				Button bMas = new Button();
				bMas.setStyleName("aon-finding-toolbar-item aon-search-add");
				bMas.addClickHandler(masHandler());
				Button bMenos = new Button();
				bMenos.setStyleName("aon-finding-toolbar-item aon-search-minus");
				bMenos.addClickHandler(menosHandler());
				
				VerticalPanel p = (VerticalPanel)grid.getWidget(5,1);
				HorizontalPanel hp =(HorizontalPanel)p.getWidget(p.getWidgetCount()-2);
		
				if(p.getWidgetCount()==2){
						ListBox l = (ListBox)hp.getWidget(0);
						l.setEnabled(true);
						hp.add(bMas);
				}
				else {
					ListBox l = (ListBox)hp.getWidget(1);
					l.setEnabled(true);
					hp.add(bMenos);
					hp.add(bMas);		
				}
				p.remove(p.getWidgetCount()-1);
				//vertical.remove(vertical.getWidgetCount()-1);
				grid.setWidget(5, 1, p);
				
			}
		};
		
		return ch;
	}
	
	public ClickHandler categoryMenosHandler() {
		ClickHandler ch = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
			
				Button bMas = new Button();
				bMas.setStyleName("aon-finding-toolbar-item aon-search-add");
				bMas.addClickHandler(categoryMasHandler());
				Button bMenos = new Button();
				bMenos.setStyleName("aon-finding-toolbar-item aon-search-minus");
				bMenos.addClickHandler(categoryMenosHandler());
				
				VerticalPanel p = (VerticalPanel)grid.getWidget(4,1);
				HorizontalPanel hp =(HorizontalPanel)p.getWidget(p.getWidgetCount()-2);
		
				if(p.getWidgetCount()==2){
						ListBox l = (ListBox)hp.getWidget(0);
						l.setEnabled(true);
						hp.add(bMas);
				}
				else {
					ListBox l = (ListBox)hp.getWidget(0);
					l.setEnabled(true);
					hp.add(bMenos);
					hp.add(bMas);		
				}
				p.remove(p.getWidgetCount()-1);
				//vertical.remove(vertical.getWidgetCount()-1);
				grid.setWidget(4, 1, p);
				
			}
		};
		
		return ch;
	}


	
	public ClickHandler menosHandler2() {
		ClickHandler ch = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
			
				Button bMas = new Button();
				bMas.setStyleName("aon-finding-toolbar-item aon-search-add");
				bMas.addClickHandler(masHandler2());
				Button bMenos = new Button();
				bMenos.setStyleName("aon-finding-toolbar-item aon-search-minus");
				bMenos.addClickHandler(menosHandler2());
				
				VerticalPanel p = (VerticalPanel)grid.getWidget(5,1);
				HorizontalPanel hp =(HorizontalPanel)p.getWidget(p.getWidgetCount()-2);
		
				ListBox l = (ListBox)hp.getWidget(0);
				l.setEnabled(true);
				if(p.getWidgetCount()==2){
						hp.add(bMas);
				}
				else {
					hp.add(bMenos);
					hp.add(bMas);		
				}
				p.remove(p.getWidgetCount()-1);
				//vertical.remove(vertical.getWidgetCount()-1);
				grid.setWidget(5, 1, p);				
			}
		};
		
		return ch;
	}

	
	

	
	public  SingleUploader newUploader(SingleUploader up,String url){
		final SingleUploader upload;
       	if(up==null){
       		 upload=  new SingleUploader(FileInputType.BROWSER_INPUT.with(FileInputType.LABEL.getInstance()));
       	}
       	else{
       		 upload = up;
       	}
       	upload.setAutoSubmit(true);
        upload.setServletPath(url + "/gwt_document_upload");
        
        upload.getForm().getWidget().getElement().getChild(1).removeFromParent();
        upload.getForm().setAction(url + "/gwt_document_upload");
        upload.getForm().setEncoding(FormPanel.ENCODING_MULTIPART);
        upload.getForm().setMethod(FormPanel.METHOD_POST);
        upload.setTitle("uploadFormElement");
        upload.avoidEmptyFiles(true);
     
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
				//upload.addStatusBar(uploader.getStatusWidget());
			}
		});

        upload.addOnStartUploadHandler(new OnStartUploaderHandler() {
			
			@Override
			public void onStart(IUploader uploader) {
				upload.getStatusWidget().setVisible(true);
				//Window.alert("start");
			}
		});
        
        upload.addOnFinishUploadHandler(new OnFinishUploaderHandler() {
			
			@Override
			public void onFinish(IUploader uploader) {
				upload.getStatusWidget().setProgress(100, 100);
				//Window.alert("finish");
				upload.getStatusWidget().setStatus(Status.DONE);
				upload.getStatusWidget().setVisible(true);
				progress = 0;		
			}
		});
        upload.getForm().addFormHandler(new FormHandler() {
			
			@Override
			public void onSubmitComplete(FormSubmitCompleteEvent event) {
				upload.getForm().getWidget().getElement().getChild(0).removeFromParent();
			}
			
			@Override
			public void onSubmit(FormSubmitEvent event) {}
		});
        
        return upload;
	}   
	
	String driveId;
	String rootId;
	final IDocumentAsync idoc = GWT.create(IDocument.class);
	Tree t ;
	SimplePanel sp;
	public SimplePanel tree(){
		sp = new SimplePanel();
		t = new Tree();
		
		idoc.getRootId(new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				rootId = result;
			}
			@Override
			public void onFailure(Throwable caught) {}
		});
		idoc.drive(new TreeMap<String, List<FileInfo>>(),"",new AsyncCallback<TreeMap<String,List<FileInfo>>>() {
			@Override
			public void onSuccess(TreeMap<String, List<FileInfo>> result) {
				TreeItem ti = new TreeItem();
				Button button = new Button("Mi Unidad");
				button.setStyleName("aon-editDataTable-button aon-icon-google-drive-folder-root");
				button.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						Label l = new Label("Mi unidad");
						l.setStyleName("aon-editDataTable-button aon-icon-google-drive-folder-root");
						grid.setWidget(3, 1, l);
						driveId = rootId;
					}
				});
				ti = t.addItem(button);
				treeSons(ti,result,rootId);
				sp.add(t);
			}
			@Override
			public void onFailure(Throwable caught) {}
		});
		return sp;
	}
	FileInfo auxiliarf;
	TreeItem tiaux;
	FileInfo faux;
	public void treeSons(TreeItem t,TreeMap<String, List<FileInfo>> folders , String id){
		Integer i = 0;
		for (FileInfo f : folders.get(id)) {
			auxiliarf = f;
			TreeItem ti = new TreeItem();
			Button button = new Button(f.getTitle());
			button.setStyleName("aon-editDataTable-button aon-icon-google-drive-folder");
			button.addClickHandler(new ClickHandler() {
				FileInfo f = auxiliarf;
				@Override
				public void onClick(ClickEvent event) {
					Label l = new Label(f.getTitle());
					l.setStyleName("aon-editDataTable-button aon-icon-google-drive-folder");
					grid.setWidget(3, 1, l);
					driveId = f.getDriveId();
				}
			});
			ti = t.addItem(button);
			ti.setTitle(f.getDriveId());
			tiaux = ti;
			faux= f;
			idoc.drive(folders, f.getDriveId(), new AsyncCallback<TreeMap<String,List<FileInfo>>>() {
				TreeItem ti = tiaux;
				FileInfo f = faux;
				@Override
				public void onSuccess(TreeMap<String, List<FileInfo>> result) {
					treeSons(ti, result, f.getDriveId());
				}
				
				@Override
				public void onFailure(Throwable caught) {}
			});
			/*if(folders.containsKey(f.getDriveId())){
				treeSons(ti, folders, f.getDriveId());
			}*/
			i++;
		}
	}

	protected void removeFilter(ListBox lb){
		for (int i = lb.getItemCount()-1 ; i>=0 ; i--) {	
			if(lb.getItemText(i).substring(0, 1).equals(Character.toString((char)9660))){
				lb.removeItem(i);
			}
		}	
	}
	
	private String windowCode;
	
	
	public String getWindowCode() {
		return windowCode;
	}

	public void setWindowCode(String windowCode) {
		this.windowCode = windowCode;
	}
}

