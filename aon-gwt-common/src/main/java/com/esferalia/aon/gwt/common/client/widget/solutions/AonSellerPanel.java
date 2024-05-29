package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.shared.DocumentValidator;
import com.esferalia.aon.occam.api.model.commission.CommissionType;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class AonSellerPanel extends SimplePanel {
	
	public static interface AonSellerPanelCallback {
		void onAccept(Seller seller);
		void onCancel();
	}

	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// Seller Info
	private AonCustomTextBox name = new AonCustomTextBox("Nombre");
	
	private AonCustomTextBox alias = new AonCustomTextBox("Alias");
	private AonCustomListBox scope = new AonCustomListBox("Ambito");
	
	private AonCustomListBox type = new AonCustomListBox("Entidad");
	private AonCustomListBox nationality = new AonCustomListBox("Nacionalidad");
	
	private AonCustomListBox documentType = new AonCustomListBox("Documento");
	private AonCustomListBox documentNationality = new AonCustomListBox("Pais Emision");
	private AonCustomTextBox document = new AonCustomTextBox(null);
	
	private AonCustomSuggestBox commisionType = new AonCustomSuggestBox("Tipo Comisi\u00f3n");
	private List<CommissionType> commisionTypes = new ArrayList<>();
	
	private AonCustomSuggestBox taskHolder = new AonCustomSuggestBox("Operario");
	private List<TaskHolder> taskHolders = new ArrayList<>();
	
	private String domainName;
	private Integer domainId;
	private String user;
	
	public AonSellerPanel(final String domainName,final int domain, final String user, final LinkedList<Scope> aviableScopes, final AonSellerPanelCallback callback) {
		initializeCommonService();
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		show(aviableScopes, new Seller(), callback);
	}
	
	public void show(LinkedList<Scope> aviableScopes, Seller seller, AonSellerPanelCallback callback) {
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

		FlexTable table1 = new FlexTable();
		table1.setStyleName(AON.CSS.aonTable());
		table1.setWidth("100%");
		
		table1.setWidget(0,0,name);
		table1.getFlexCellFormatter().setColSpan(0, 0, 2);
		
		table1.setWidget(1,0,alias);
		
		scope.clearItems();
		aviableScopes.forEach(as -> scope.addItem(as.getDescription(), as.getId().toString()));
		table1.setWidget(1,1,scope);
		
		nationality.clearItems();
		for(int i=0; i < Country.values().length; i++)
			nationality.addItem(Country.values()[i].getName(), Country.values()[i].getIso2());
		nationality.setValue("ES");
		table1.setWidget(2,0,nationality);
		
		type.clearItems();
		type.addItem("P. F\u00edsicas", "0");
		type.addItem("P. Jur\u00eddicas", "1");
		table1.setWidget(2,1,type);
		
		FlowPanel documentPanel = new FlowPanel();
		documentPanel.setStyleName(AON.CSS.aonItemFlex());
		documentPanel.getElement().getStyle().setProperty("align-items", "flex-end");
		
		documentNationality.clearItems();
		for(int i=0; i < Country.values().length; i++)
			documentNationality.addItem(Country.values()[i].getIso2(), Country.values()[i].getIso2());
		documentNationality.getElement().getStyle().setProperty("max-width", "5rem");
		documentNationality.setValue("ES");
		documentPanel.add(documentNationality);
		
		document.getTextBox().addValueChangeHandler(e -> {
			DocumentType documentTypeValidator = DocumentValidator.validateDocument(document.getValue());
			documentType.setValue(documentTypeValidator.toString());
		});
		documentPanel.add(document);
		
		table1.setWidget(3,0,documentPanel);
		
		documentType.clearItems();
		for(int i=0; i < DocumentType.values().length; i++)
			documentType.addItem(DocumentType.values()[i].getDescription(), DocumentType.values()[i].toString());
		table1.setWidget(3,1,documentType);
		
		commisionType.setAutoSelectEnabled(false);
		commisionType.setPlaceHolder("Cuota: ctrl + espacio para ver sugerencias");
		commisionType.getSuggestBox().addKeyUpHandler(e -> {
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				commisionType.showSuggestionList();
			}
		});
		getAviableCommisionTypes(success -> {
			commisionType.setValue(null);
		});
		
		table1.setWidget(4, 0, commisionType);
		table1.getFlexCellFormatter().setColSpan(4, 0, 2);
		
		taskHolder.setAutoSelectEnabled(false);
		taskHolder.setPlaceHolder("Cuota: ctrl + espacio para ver sugerencias");
		taskHolder.getSuggestBox().addKeyUpHandler(e -> {
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				taskHolder.showSuggestionList();
			}
		});
		getAviableSellerTaskHolders(success -> {
			taskHolder.setValue(null);
		});
		
		table1.setWidget(5, 0, taskHolder);
		table1.getFlexCellFormatter().setColSpan(5, 0, 2);

		table1.getColumnFormatter().setWidth(0, "5rem");
		
		tablePanel.add( table1 );
		
		rootPanel.add( tablePanel );
		
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
				
				seller.setDomain(domainId);
				
				seller.setLegalPerson(type.getValue() == "1");
				seller.setDocumentType(DocumentType.valueOf(documentType.getValue()));
				seller.setDocumentCountry(Country.valueOf(documentNationality.getValue()));
				seller.setDocument(document.getValue());
				seller.setNationality(Country.valueOf(nationality.getValue()));
				
				seller.setName(name.getValue());
				seller.setAlias(alias.getValue());
				
				seller.setScope(new Scope().setId(Integer.parseInt(scope.getValue())));
				
				if(AonStringUtils.isNotBlank(commisionType.getValue())) {
					Integer commisionTypeId = Integer.parseInt(commisionType.getValue().split("\\[")[1].split("\\]")[0]);
					seller.setCommissionType(new CommissionType().setId(commisionTypeId));
				} else seller.setCommissionType(null);
			
				if(AonStringUtils.isNotBlank(taskHolder.getValue())) {
					Integer taskHolderId = Integer.parseInt(taskHolder.getValue().split("\\[")[1].split("\\]")[0]);
					seller.setTaskHolder(new TaskHolder().setRegistry(taskHolderId));
				} else seller.setTaskHolder(null);
				
				seller.setActive(true);
				
				commonService.saveSeller(domainName, domainId, user, seller, new AsyncCallback<Seller>() {

					@Override
					public void onSuccess(Seller seller) {
						commonService.getSeller(domainName, domainId, user, seller.getId(), new AsyncCallback<Seller>() {

							@Override
							public void onFailure(Throwable caught) {}

							@Override
							public void onSuccess(Seller sellerDB) {
								callback.onAccept(sellerDB);
							}
						
						});
					}
					@Override
					public void onFailure(Throwable caught) {
						errorPanel.showError(caught.getMessage());
						okButton.setEnabled(true);
					}
				});
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
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	name.setFocus(true);
	        	onResize();
	        }
	    });		
		
	}
	
	private void getAviableCommisionTypes(Consumer<Void> success) {
		commonService.getAviableCommisionTypes(domainName, domainId, user, new AsyncCallback<List<CommissionType>>() {
			
			@Override
			public void onSuccess(List<CommissionType> newsSuggestion) {
				commisionTypes = newsSuggestion;
				
				List<String> suggestions = new ArrayList<String>();
				commisionTypes.forEach(newIt -> suggestions.add("[" + newIt.getId() + "] " + newIt.getName()));
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) commisionType.getSuggestBox().getSuggestOracle();
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
	
	private void getAviableSellerTaskHolders(Consumer<Void> success) {
		commonService.getAviableSellerTaskHolders(domainName, domainId, user, new AsyncCallback<List<TaskHolder>>() {
			
			@Override
			public void onSuccess(List<TaskHolder> newsSuggestion) {
				taskHolders = newsSuggestion;
				
				List<String> suggestions = new ArrayList<String>();
				taskHolders.forEach(newIt -> suggestions.add("[" + newIt.getId() + "] " + newIt.getName()));
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) taskHolder.getSuggestBox().getSuggestOracle();
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

	protected abstract void onResize();

}
