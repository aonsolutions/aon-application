package com.esferalia.aon.gwt.document.client;

import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnCancelUploaderHandler;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStartUploaderHandler;
import gwtupload.client.IUploader.OnStatusChangedHandler;
import gwtupload.client.SingleUploader;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.document.shared.Category;
import com.esferalia.aon.gwt.document.shared.Dialog;
import com.esferalia.aon.gwt.document.shared.FileInfo;
import com.esferalia.aon.gwt.document.shared.Lists;
import com.esferalia.aon.gwt.document.shared.Scope;
import com.esferalia.aon.gwt.document.shared.Tag;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
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
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public abstract class DocumentsDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, DocumentsDialog>{
		
	}
	private static final Binder binder = GWT.create(Binder.class);
	
	String email;

	Lists lists;
	@UiField(provided = true) 
	FlexTable grid;
	FileInfo fileInfo;
	@UiField(provided = true)
	Label label;
	@UiField Button acceptButton;
	@UiField Button cancelButton;
	
	
	public DocumentsDialog(Dialog dialog) {
		lists  = dialog.getLists();
		setFileInfo(dialog.getFileInfo());
		setCaption(dialog.getTitle());
		label = new Label();
		grid = new FlexTable();
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
	}
	
	protected abstract void onAccept();
	
	protected abstract void onCancel();

	private void gridBuild(Dialog dialog) {
		switch (dialog.getType()) {
			case "new": newFile(dialog);break;
			case "upload": uploadFile(dialog);break;
			case "edit": editFile(dialog);break;
			case "delete": deleteFile(dialog.getFileInfo().getTitle());break;
			case "search": searchFile(dialog);break;
			case "info": infoFile(dialog.getFileInfo());break;
			case "share": shareFile();break;
			case "alert": alert(dialog);
			default:
				break;
		}
		
	}
	HorizontalPanel h2;
	VerticalPanel vertical;
	String url;
	
	private void newFile(Dialog dialog) {
		url = dialog.getBaseUrl();
		ListBox lb1 = new ListBox();
		lb1.addItem("-");
		ListBox lb2 = new ListBox();
		lb2.addItem("-");
		ListBox lb3 = new ListBox();
		lb3.addItem("-");

		for (Scope s : lists.getScopeList().getList()) {
			if(s.getIsParent())
				lb3.addItem(Character.toString((char)9650)+s.getName());
			else if(s.getIsSon())
				lb3.addItem(Character.toString((char)9660)+s.getName());
			else lb3.addItem(s.getName());
		}
		if(dialog.getSon()){
			for(Scope s : lists.getScopeListSon().getList()){
				lb3.addItem(Character.toString((char)9660)+s.getName());
			}
		}
		for (Tag t : lists.getTagList().getList()) {
			if(t.getIsParent())
				lb2.addItem(Character.toString((char)9650)+t.getName());
			else if(t.getIsSon())
				lb2.addItem(Character.toString((char)9660)+t.getName());
			else lb2.addItem(t.getName());
		}
		for (Category c : lists.getCategoryList().getList()) {
			if(c.getIsParent())
				lb1.addItem(Character.toString((char)9650)+c.getName());
			else if(c.getIsSon())
				lb1.addItem(Character.toString((char)9660)+c.getName());
			else lb1.addItem(c.getName());
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
		if(dialog.getSons().size() != 1){
		SuggestBox tb0 = new SuggestBox(Utils.createOracle(dialog.getSons()));
		tb0.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
			@Override
			public void onSelection(
					SelectionEvent<SuggestOracle.Suggestion> event) {
				String s = event.getSelectedItem().getDisplayString();
				Integer pos1 = s.indexOf('>');
				Integer pos2 = s.indexOf('/')-1;
				String s2 = s.substring(pos2);
				Integer pos3 = s2.indexOf('>');
				String string = s.substring(pos1+1,pos2)+s2.substring(pos3+1);
				for (Category c : lists.getCategoryListSon().getList()) {
					if(c.getDomain().equals(string)){
						ListBox lb = (ListBox) grid.getWidget(5, 1);
						lb.addItem(Character.toString((char)9660)+c.getName());
						grid.setWidget(5, 1, lb);
					}
				}
				for (Tag t : lists.getTagListSon().getList()) {
					if(t.getDomain().equals(string)){
					
						if (bool) {
							ListBox lb = (ListBox) grid.getWidget(6, 1);
							lb.addItem(Character.toString((char)9660)+t.getName());
							grid.setWidget(6, 1, lb);
						} else {
							VerticalPanel v = (VerticalPanel) grid.getWidget(6, 1);
							HorizontalPanel h = (HorizontalPanel) v.getWidget(0);
							ListBox lb = (ListBox) h.getWidget(0);
							lb.addItem(Character.toString((char)9660)+t.getName());
								
							h2 = new HorizontalPanel();
							vertical = new VerticalPanel();
							h2.add(lb);
							vertical.add(h2);
							grid.setWidget(6, 1, vertical);
						}					
					}
				}
				for (Scope scope : lists.getScopeListSon().getList()) {
					Window.alert(scope.getDomain());

					if(scope.getDomain().equals(string)){
						ListBox lb = (ListBox) grid.getWidget(7, 1);
						lb.addItem(Character.toString((char)9660)+scope.getName());
						grid.setWidget(7, 1, lb);
					}
				}
			}
			
		});
		tb0.setStyleName("aon-inputText");
		grid.setWidget(0, 0, new Label("Empresa"));
		grid.setWidget(0, 1, tb0);
		}
		final TextBox tb1 = new TextBox();tb1.setStyleName("aon-inputText");
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

		CheckBox checkBox = new CheckBox();
		grid.setWidget(3, 0, new Label("Confidencial"));
		grid.setWidget(3, 1, checkBox);

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
			grid.setWidget(6, 0, new Label("Etiqueta"));
			grid.setWidget(6, 1, lb2);	
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
		FileInfo fi = dialog.getFileInfo();
		ListBox lb1 = new ListBox();
		lb1.addItem("-");
		ListBox lb2 = new ListBox();
		lb2.addItem("-");
		ListBox lb3 = new ListBox();
		lb3.addItem("-");
		for (Scope s : lists.getScopeList().getList()) {
			if(s.getIsParent())
				lb3.addItem(Character.toString((char)9650)+s.getName());
			else if(s.getIsSon())
				lb3.addItem(Character.toString((char)9660)+s.getName());
			else lb3.addItem(s.getName());
		}
		if(dialog.getSon()){
			for(Scope s : lists.getScopeListSon().getList()){
				lb3.addItem(Character.toString((char)9660)+s.getName());
			}
		}
		for (Tag t : lists.getTagList().getList()) {
			if(t.getIsParent())
				lb2.addItem(Character.toString((char)9650)+t.getName());
			else if(t.getIsSon())
				lb2.addItem(Character.toString((char)9660)+t.getName());
			else lb2.addItem(t.getName());
		}
		for (Category c : lists.getCategoryList().getList()) {
			if(c.getIsParent())
				lb1.addItem(Character.toString((char)9650)+c.getName());
			else if(c.getIsSon())
				lb1.addItem(Character.toString((char)9660)+c.getName());
			else lb1.addItem(c.getName());
		}
		lb2.addChangeHandler(OneHandler2());
		final SingleUploader upload = newUploader(dialog.getUpload(),url);
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

		TextBox tb1 = new TextBox();tb1.setStyleName("aon-inputText");
		grid.setWidget(0, 0, new Label("Descripci\u00f3n"));
		tb1.setText(fi.getTitle());
		grid.setWidget(0, 1, tb1);

		grid.setWidget(1, 0, new Label("Archivo"));
		grid.setWidget(1, 1, upload);
		
		CheckBox checkBox = new CheckBox();
		grid.setWidget(2, 0, new Label("Confidencial"));
		checkBox.setValue(fi.getConfidential());
		grid.setWidget(2, 1, checkBox);
		
	    DateTimeFormat dateFormat = DateTimeFormat.getMediumDateFormat();
	    DateBox dateBox = new DateBox();
	    dateBox.setStyleName("aon-inputText");
	    dateBox.setFormat(new DateBox.DefaultFormat(dateFormat));
	    dateBox.getDatePicker().setYearArrowsVisible(true);
		grid.setWidget(3, 0, new Label("Fecha"));
		dateBox.setValue(fi.getDate());
		grid.setWidget(3, 1, dateBox);

		grid.setWidget(4, 0, new Label("Categor\u00eda"));
		for (int i = 0; i<lb1.getItemCount();i++) {
			String s1 = lb1.getItemText(i);
			if(s1.substring(0,1).equals(Character.toString((char)9650)) || s1.substring(0,1).equals(Character.toString((char)9660))){
				s1 = s1.substring(1);
			}
			if(s1.equals(fi.getCategoryStr())){
				lb1.setItemSelected(i, true);
			}
		}
		grid.setWidget(4, 1, lb1);

		if(lb2.getItemCount() <= 2){
			grid.setWidget(5, 0, new Label("Etiqueta"));
			for (int i = 0; i<lb2.getItemCount();i++) {
				String s2 = lb2.getItemText(i);
				if(s2.substring(0,1).equals(Character.toString((char)9650)) || s2.substring(0,1).equals(Character.toString((char)9660))){
					s2 = s2.substring(1);
				}
				if(s2.equals(fi.getTagsStr())){
					lb2.setItemSelected(i, true);
				}
			}
			grid.setWidget(5, 1, lb2);	
		}
		else{
			Integer size = fi.getTags().size();
			
			if(size>1){
				vertical= new VerticalPanel();
				ListBox[] lbs = new ListBox[size];
				for(Integer k = 0; k< size ; k++){
					lbs[k] = new ListBox();
					lbs[k].addItem("-");
					for (Tag t : lists.getTagList().getList()) {
						lbs[k].addItem(t.getName());
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
						if(size < lists.getTagList().getList().size()){
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
				
				for (int i = 0; i<lb2.getItemCount();i++) {
					String s2 = lb2.getItemText(i);
					if(s2.substring(0,1).equals(Character.toString((char)9650))||s2.substring(0,1).equals(Character.toString((char)9660)))
						s2 = s2.substring(1);
					if(s2.equals(fi.getTagsStr())){
						lb2.setItemSelected(i, true);
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

	
	private void deleteFile(String name) {
		label.setText("Estas seguro de eliminar el archivo "+name);
	}
	
	private void alert(Dialog dialog){
		String type="";
		if(dialog.getTitle().equals("Editar Archivo"))type = "editar";
		else if(dialog.getTitle().equals("Borrar Archivo"))type = "borrar";
		label.setText("No tiene permisos para "+type+" el archivo "+dialog.getFileInfo().getTitle());
	}
	
	Boolean bool;
	private void searchFile(Dialog dialog) {
		ListBox lb1 = new ListBox();
		lb1.addItem("-");
		ListBox lb2 = new ListBox();
		lb2.addItem("-");
		ListBox lb3 = new ListBox();
		lb3.addItem("-");

		for (Scope s : lists.getScopeList().getList()) {
			if(s.getIsParent())
				lb3.addItem(Character.toString((char)9650)+s.getName());
			else if(s.getIsSon())
				lb3.addItem(Character.toString((char)9660)+s.getName());
			else lb3.addItem(s.getName());
		}
		if(dialog.getSon()){
			for(Scope s : lists.getScopeListSon().getList()){
				lb3.addItem(Character.toString((char)9660)+s.getName());
			}
		}
		for (Tag t : lists.getTagList().getList()) {
			if(t.getIsParent())
				lb2.addItem(Character.toString((char)9650)+t.getName());
			else if(t.getIsSon())
				lb2.addItem(Character.toString((char)9660)+t.getName());
			else lb2.addItem(t.getName());
		}
		for (Category c : lists.getCategoryList().getList()) {
			if(c.getIsParent())
				lb1.addItem(Character.toString((char)9650)+c.getName());
			else if(c.getIsSon())
				lb1.addItem(Character.toString((char)9660)+c.getName());
			else lb1.addItem(c.getName());
		}
		lb2.addChangeHandler(OneHandler());
		
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
				Integer pos1 = s.indexOf('>');
				Integer pos2 = s.indexOf('/')-1;
				String s2 = s.substring(pos2);
				Integer pos3 = s2.indexOf('>');
				String string = s.substring(pos1+1,pos2)+s2.substring(pos3+1);
				for (Category c : lists.getCategoryListSon().getList()) {
					if(c.getDomain().equals(string)){
						ListBox lb = (ListBox) grid.getWidget(4, 1);
						lb.addItem(Character.toString((char)9660)+c.getName());
						grid.setWidget(4, 1, lb);
					}
				}
				for (Tag t : lists.getTagListSon().getList()) {
					if(t.getDomain().equals(string)){
					
						if (bool) {
							ListBox lb = (ListBox) grid.getWidget(5, 1);
							lb.addItem(Character.toString((char)9660)+t.getName());
							grid.setWidget(5, 1, lb);
						} else {
							VerticalPanel v = (VerticalPanel) grid.getWidget(5, 1);
							HorizontalPanel h = (HorizontalPanel) v.getWidget(0);
							ListBox lb = (ListBox) h.getWidget(0);
							lb.addItem(Character.toString((char)9660)+t.getName());
								
							h2 = new HorizontalPanel();
							vertical = new VerticalPanel();
							h2.add(lb);
							vertical.add(h2);
							grid.setWidget(5, 1, vertical);
						}					
					}
				}
				for (Scope scope : lists.getScopeListSon().getList()) {
					Window.alert(scope.getDomain());

					if(scope.getDomain().equals(string)){
						ListBox lb = (ListBox) grid.getWidget(6, 1);
						lb.addItem(Character.toString((char)9660)+scope.getName());
						grid.setWidget(6, 1, lb);
					}
				}
			}
			
		});
		tb0.setStyleName("aon-inputText");
		grid.setWidget(0, 0, new Label("Empresa"));
		grid.setWidget(0, 1, tb0);
		}
		TextBox tb1 = new TextBox();
		tb1.setStyleName("aon-inputText");
		grid.setWidget(1, 0, new Label("Descripci\u00f3n"));
		grid.setWidget(1, 1, tb1);

		grid.setWidget(2, 0, new Label("Confidencial"));
		grid.setWidget(2, 1, new CheckBox());

		DateTimeFormat dateFormat = DateTimeFormat.getMediumDateFormat();
		DateBox dateBox = new DateBox();
		dateBox.setStyleName("aon-inputText");
		dateBox.setFormat(new DateBox.DefaultFormat(dateFormat));
		dateBox.getDatePicker().setYearArrowsVisible(true);
		grid.setWidget(3, 0, new Label("Fecha"));
		grid.setWidget(3, 1, dateBox);

		grid.setWidget(4, 0, new Label("Categor\u00eda"));
		grid.setWidget(4, 1, lb1);
		bool = lb2.getItemCount() <= 2;
		if (lb2.getItemCount() <= 2) {
			grid.setWidget(5, 0, new Label("Etiqueta"));
			grid.setWidget(5, 1, lb2);
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
	
	
	private void infoFile(FileInfo object) {
		grid.setStyleName("aon-panelGrid");
		grid.setWidth("300px");
		grid.setBorderWidth(1);
		grid.setCellSpacing(0);
		if(object.getIsDrive()){
			grid.setWidget(0, 0, new Label("Descripci\u00f3n"));
			grid.setWidget(0, 1, new Label(object.getTitle()));
		
			grid.setWidget(1, 0, new Label("Fecha"));
			grid.setWidget(1, 1, new Label(object.getDateStr()));
		
			grid.setWidget(2, 0, new Label("Tamaño"));
			grid.setWidget(2, 1, new Label(object.getSizeStr()));
		
			grid.setWidget(3, 0, new Label("Mime Type"));
			grid.setWidget(3, 1, new Label(""));
		}
		else{
			grid.setWidget(0, 0, new Label("Descripci\u00f3n"));
			grid.setWidget(0, 1, new Label(object.getTitle()));
		
			grid.setWidget(1, 0, new Label("Confidencial"));
			grid.setWidget(1, 1, new Label(object.getConfidential() ? "Si" : "No"));
		
			grid.setWidget(2, 0, new Label("Fecha"));
			grid.setWidget(2, 1, new Label(object.getDateStr()));
		
			grid.setWidget(3, 0, new Label("Categoria"));
			grid.setWidget(3, 1, new Label(object.getCategoryStr()));
		
			grid.setWidget(4, 0, new Label("Etiquetas"));
			grid.setWidget(4, 1, new Label(object.getTagsStr()));
	
			grid.setWidget(5, 0, new Label("Ambito"));
			grid.setWidget(5, 1, new Label(object.getScope().getName()));
		
			grid.setWidget(6, 0, new Label("Tamaño"));
			grid.setWidget(6, 1, new Label(object.getSizeStr()));
		
			grid.setWidget(7, 0, new Label("Mime Type"));
			grid.setWidget(7, 1, new Label(""));//MimeType.values()[object.getMimetype()].getName()));
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
	
	
	
	
	//-------------------- Gets & Sets
	
	
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
					
					if(vertical.getWidgetCount()<lists.getTagList().getLength()){
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
					
					if(vertical.getWidgetCount()<lists.getTagList().getLength()){
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
				
				lb2.addChangeHandler(TwoHandler());
				
				lb2.addItem("-");
				
				for (Tag t : lists.getTagList().getList()) {
					lb2.addItem(t.getName());
				}
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
	
	
	public ClickHandler masHandler2() {
		ClickHandler ch = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ListBox lb2 = new ListBox();
				
				lb2.addChangeHandler(TwoHandler2());
				
				lb2.addItem("-");
				
				for (Tag t : lists.getTagList().getList()) {
					lb2.addItem(t.getName());
				}
				
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
        upload.setServletPath(url + "/gwt_upload");
        
        upload.getForm().getWidget().getElement().getChild(1).removeFromParent();
        upload.getForm().setAction(url + "/gwt_upload");
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
}

