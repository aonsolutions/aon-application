package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

public class AonAttachPanel extends SimplePanel {
	
	public static interface AonAttachCallback {
		void onAccept();
		void onCancel();
	}

	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// Address Info
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private static final String CREATEURL = GWT.getModuleBaseURL() + "attach/create/";
	private static final String UPDATEURL = GWT.getModuleBaseURL() + "attach/update/";
	
	private FocusPanel dropPanel; 	
	
	
	// Form
	FormPanel form;
	FileUpload fileUpload;
	Hidden userLoginHidden = new Hidden("login", "");
	Hidden currentDomainHidden = new Hidden("domain", "");
	Hidden attachIdHidden = new Hidden("attachId", "");
	Hidden attachTypeHidden = new Hidden("attachType", "");
	Hidden attachModuleHidden = new Hidden("attachModule", "");
	Hidden descriptionHidden = new Hidden("description", "");
	Hidden typeHidden = new Hidden("type", "");
	Hidden securityHidden = new Hidden("security", "");
	Hidden dateHidden = new Hidden("date", "");
	Hidden scopeHidden = new Hidden("scope", "");
	Hidden mimeTypeHidden = new Hidden("mimeType", "");
	Hidden categoryHidden = new Hidden("category", "");
	
	private AonCustomTextBox description = new AonCustomTextBox("Descripci\u00f3n");
	
	private AonCustomToogleButton visible = new AonCustomToogleButton("Privado");
	private AonCustomDateBox date = new AonCustomDateBox("Fecha");
	private AonCustomListBox scope = new AonCustomListBox("Ambito");
	
	private AonCustomSuggestBox category = new AonCustomSuggestBox("Categoria");
	private List<Category> categories = new ArrayList<>();
	
	private String domainName;
	private Integer domainId;
	private String user;
	
	private Integer registry;
	
	private LinkedList<Scope> aviableScopes;
	
	public AonAttachPanel(final String domainName,final int domain, final String user, final LinkedList<Scope> aviableScopes, final Integer registry, final AonAttachCallback callback) {
		initializeCommonService();
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.registry = registry;
		
		this.aviableScopes = aviableScopes;
		
		show(new Attach(), callback);
	}
	
	public AonAttachPanel(final String domainName,final int domain, final String user, final LinkedList<Scope> aviableScopes, final Attach attach, final AonAttachCallback callback) {
		initializeCommonService();
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.aviableScopes = aviableScopes;
		
		show(attach, callback);
	}
	
	public void show(Attach attach, AonAttachCallback callback) {
		getElement().getStyle().setProperty("padding", "1rem 0");
		
		FlowPanel rootPanel = new FlowPanel();
		rootPanel.setStyleName(AON.CSS.aonFlexColumnBetween());
		
		final AonErrorPanel errorPanel = new AonErrorPanel();
		errorPanel.addStyleName(AON.CSS.aonMarginTop());
		rootPanel.add(errorPanel);
		
		FlowPanel tablePanel = new FlowPanel();
		tablePanel.setStyleName(AON.CSS.aonScrollArea());
		
		KeyUpHandler keyUpHandler = new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					callback.onCancel();	
				}
			}
		};
		
		// Create Form Panel
		form = new FormPanel();
		form.setAction(null == attach.getId() ? CREATEURL : UPDATEURL);
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.addSubmitCompleteHandler(e -> callback.onAccept());

		FlexTable table1 = new FlexTable();
		table1.setStyleName(AON.CSS.aonTable());
		table1.setWidth("100%");
		
		description.getTextBox().setMaxLength(64);
		
		date.setValue(new Date());
		
		scope.clearItems();
		aviableScopes.forEach(as -> scope.addItem(as.getDescription(), as.getId().toString()));
		
		table1.setWidget(0, 0, description);
		table1.getFlexCellFormatter().setColSpan(0, 0, 3);
		
		table1.setWidget(1, 0, date);
		table1.setWidget(1, 1, scope);
		table1.setWidget(1, 2, visible);
		
		category.setAutoSelectEnabled(false);
		category.setPlaceHolder("Cuota: ctrl + espacio para ver sugerencias");
		category.getSuggestBox().addKeyUpHandler(e -> {
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				category.showSuggestionList();
			}
		});
		getAviableCategories(success -> {
			if(attach.getId() != null)
				category.setValue(null == attach.getFullCategory() ? null : ("[" + attach.getFullCategory().getId() + "] " + attach.getFullCategory().getName()));
		});
		table1.setWidget(2, 0, category);
		table1.getFlexCellFormatter().setColSpan(2, 0, 3);
		
		tablePanel.add( table1 );
		
		
		FlowPanel flowFormPanel = new FlowPanel();
		if(attach.getId() == null) {
			Panel dropZone = getDropFileZone();
			flowFormPanel.add(dropZone);
		}
		
		// Add all to FlowPanel to add to FormPanel
		flowFormPanel.add(userLoginHidden);
		flowFormPanel.add(currentDomainHidden);
		flowFormPanel.add(attachIdHidden);
		flowFormPanel.add(attachTypeHidden);
		flowFormPanel.add(attachModuleHidden);
		flowFormPanel.add(descriptionHidden);
		flowFormPanel.add(typeHidden);
		flowFormPanel.add(securityHidden);
		flowFormPanel.add(dateHidden);
		flowFormPanel.add(scopeHidden);
		flowFormPanel.add(mimeTypeHidden);
		flowFormPanel.add(categoryHidden);
		form.add(flowFormPanel);
		
		rootPanel.add(form);
		rootPanel.add( tablePanel );
		
		if(attach.getId() != null) {
			attachIdHidden.setValue(attach.getId().toString());
			mimeTypeHidden.setValue(attach.getMimeType().getExtension());
			
			description.setValue(attach.getDescription());
			date.setValue(attach.getDate());
			scope.setValue(null == attach.getScope() ? null : attach.getScope().toString());
			visible.setValue(attach.isConfidential());
		}
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	
    	final Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addKeyUpHandler( keyUpHandler);
    	
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				okButton.setEnabled(false);
				
				userLoginHidden.setValue(user);
				currentDomainHidden.setValue(domainName);
				attachTypeHidden.setValue("registry");
				attachModuleHidden.setValue(attach.getId() == null ? registry.toString() : attach.getAttachModule().toString());
				descriptionHidden.setValue(description.getValue());
				typeHidden.setValue("5");
				dateHidden.setValue(formatDate.format(date.getValue()));
				scopeHidden.setValue(scope.getValue());
				securityHidden.setValue(Boolean.toString(visible.getValue()));
				categoryHidden.setValue(AonStringUtils.isBlank(category.getValue()) ? "" : category.getValue().split("\\[")[1].split("\\]")[0]);
				
				form.submit();
			}
		});
    	
    	buttons.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addKeyUpHandler( keyUpHandler);
    	cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cancelButton.setEnabled(false);
				callback.onCancel();
			}
		});
    	buttons.add(cancelButton);
    	rootPanel.add(buttons);
		setWidget(rootPanel);	
		
	}
	
	private void getAviableCategories(Consumer<Void> success) {
		commonService.getAviableCategories(domainName, domainId, user, new AsyncCallback<List<Category>>() {
			
			@Override
			public void onSuccess(List<Category> newsSuggestion) {
				categories = newsSuggestion;
				
				List<String> suggestions = new ArrayList<String>();
				categories.forEach(typeIt -> suggestions.add("[" + typeIt.getId() + "] " + typeIt.getName()));
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) category.getSuggestBox().getSuggestOracle();
				orclSb.clear();
				orclSb.addAll(suggestions);
				orclSb.setDefaultSuggestionsFromText(suggestions);
				
				success.accept(null);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}
	
	private Panel getDropFileZone() {
		fileUpload = new FileUpload();
		fileUpload.setName("uploader");
		fileUpload.getElement().setPropertyString("multiple", "multiple");
        fileUpload.getElement().setAttribute("style", "display:none;");
        
        final FlowPanel dropPanel = new FlowPanel();
        dropPanel.getElement().setAttribute("style", "border: 2px dashed #ccc; padding: 20px; width: -moz-available; width: -webkit-fill-available; text-align: center; border-radius: 5px;");
        dropPanel.add(new Label("Arrastra un archivo aqui. O haga click para importarlo."));

        HTMLPanel mainPanel = new HTMLPanel("");
        mainPanel.addStyleName(AON.CSS.aonFlexColumn());
        mainPanel.getElement().getStyle().setProperty("align-items", "center");
        mainPanel.getElement().getStyle().setProperty("cursor", "pointer");
        mainPanel.getElement().getStyle().setProperty("padding", "0 1.5rem");
        mainPanel.addDomHandler(e -> fileUpload.click(), ClickEvent.getType());
        mainPanel.add(dropPanel);
        mainPanel.add(fileUpload);

        addDragAndDropHandlers(dropPanel.getElement(), fileUpload.getElement());

        fileUpload.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
            	String filename = getFileName(fileUpload.getFilename());
    			String fileExt = getFileExtension(fileUpload.getFilename());
            	
    			mimeTypeHidden.setValue(fileExt);
				descriptionHidden.setValue(filename);
				description.setValue(filename);
				
				mainPanel.setVisible(false);
            }
        });
        
        return mainPanel;
	}
	
	private String getFileName(String filename) {
		String[] splits = filename.split("\\\\");
		return splits[splits.length-1].contains("\\.") ? splits[splits.length-1].split("\\.")[0] : splits[splits.length-1];
	}
	
	private String getFileExtension(String filename) {
		String[] splits = filename.split("\\.");
		return splits[splits.length-1];
	}
	
	private native int getFileSize(final Element data) /*-{
		return data.files[0].size;
	}-*/;
	
	private native void addDragAndDropHandlers(Element dropElement, Element fileInputElement) /*-{
	    dropElement.addEventListener('dragover', function(event) {
	        event.preventDefault();
	        dropElement.style.border = '2px solid #ddd';
	    }, false);
	
	    dropElement.addEventListener('dragleave', function(event) {
	        dropElement.style.border = '2px dashed #ccc';
	    }, false);
	
	    dropElement.addEventListener('drop', function(event) {
	        event.preventDefault();
	        dropElement.style.backgroundColor = '';
	
	        var files = event.dataTransfer.files;
	        if (files.length > 0) {
	            fileInputElement.files = files;
	            var changeEvent = new Event('change', {
	                'bubbles': true,
	                'cancelable': true
	            });
	            fileInputElement.dispatchEvent(changeEvent);
	        }
	    }, false);
	}-*/;

}
