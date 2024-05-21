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
import com.google.gwt.dom.client.Element;
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
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;

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
	private ListBox type = new ListBox();
	private ListBox nationality = new ListBox();
	
	private ListBox documentType = new ListBox();
	private ListBox documentNationality = new ListBox();
	private TextBox document = new TextBox();
	
	private TextBox name = new TextBox();
	private TextBox alias = new TextBox();
	
	private ListBox scope = new ListBox();
	private Button active = new Button();
	
	private SuggestBox commisionType = new SuggestBox();
	private List<CommissionType> commisionTypes = new ArrayList<>();
	
	private SuggestBox taskHolder = new SuggestBox();
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

		FlexTable table = new FlexTable();
		table.setStyleName(AON.CSS.aonTable());
		
		table.setWidget(0, 0, new InlineLabel("Entidad"));
		table.getCellFormatter().setStyleName(0, 0, AON.CSS.aonTableLabel());
		type = new ListBox();
		type.addItem("P. F\u00edsicas", "0");
		type.addItem("P. Jur\u00edicas", "1");
		addSelectStyle(type.getElement());
		table.setWidget(0,1,type);
		
		table.setWidget(0, 2, new InlineLabel("Nacionalidad"));
		table.getCellFormatter().setStyleName(0, 2, AON.CSS.aonTableLabel());
		nationality = new ListBox();
		for(int i=0; i < Country.values().length; i++)
			nationality.addItem(Country.values()[i].getName(), Country.values()[i].getIso2());
		setSelectedValueLB(nationality, "ES");
		addSelectStyle(nationality.getElement());
		nationality.getElement().getStyle().setProperty("max-width", "10rem");
		table.setWidget(0,3,nationality);
		
		table.setWidget(1, 0, new InlineLabel("Documento"));
		table.getCellFormatter().setStyleName(1, 0, AON.CSS.aonTableLabel());
		documentType = new ListBox();
		for(int i=0; i < DocumentType.values().length; i++)
			documentType.addItem(DocumentType.values()[i].getDescription(), DocumentType.values()[i].toString());
		addSelectStyle(documentType.getElement());
		table.setWidget(1,1,documentType);
		
		table.setWidget(1, 2, new InlineLabel("Pa\u00eds Emisi\u00f3n"));
		table.getCellFormatter().setStyleName(1, 2, AON.CSS.aonTableLabel());
		documentNationality = new ListBox();
		for(int i=0; i < Country.values().length; i++)
			documentNationality.addItem(Country.values()[i].getIso2(), Country.values()[i].getIso2());
		setSelectedValueLB(documentNationality, "ES");
		addSelectStyle(documentNationality.getElement());
		table.setWidget(1,3,documentNationality);
		
		document.addValueChangeHandler(e -> {
			DocumentType documentTypeValidator = DocumentValidator.validateDocument(document.getValue());
			setSelectedValueLB(documentType, documentTypeValidator.toString());
		});
		addInputStyle(document.getElement());
		table.setWidget(1,4,document);
		
		table.setWidget(2, 0, new InlineLabel("Nombre / Raz\u00f3n Social"));
		table.getCellFormatter().setStyleName(2, 0, AON.CSS.aonTableLabel());
		addInputStyle(name.getElement());
		table.setWidget(2,1,name);
		table.getFlexCellFormatter().setColSpan(2, 1, 4);
		
		table.setWidget(3, 0, new InlineLabel("Nombre Comercial / Alias"));
		table.getCellFormatter().setStyleName(3, 0, AON.CSS.aonTableLabel());
		addInputStyle(alias.getElement());
		table.setWidget(3,1,alias);
		table.getFlexCellFormatter().setColSpan(3, 1, 4);
		
		table.setWidget(4,0,new InlineLabel(AON.MSG.scope()));
		table.getCellFormatter().setStyleName(4, 0, AON.CSS.aonTableLabel());
		scope.clear();
		addSelectStyle(scope.getElement());
		aviableScopes.forEach(as -> scope.addItem(as.getDescription(), as.getId().toString()));
		scope.setStyleName(AON.CSS.aonInputText());
		table.setWidget(4,1,scope);
		
		active = new Button();
		table.setWidget(4,2,new InlineLabel("Activo"));
		table.getCellFormatter().setStyleName(4, 2, AON.CSS.aonTableLabel());
		getEnableDisableButton(active, true);
		active.addClickHandler(e -> getEnableDisableButton(active, !isActiveToggleButton(active)));
		table.setWidget(4,3,active);
		
		addInputStyle(commisionType.getElement());
		commisionType.setAutoSelectEnabled(false);
		commisionType.getElement().setPropertyString("placeholder", "Cuota: ctrl + espacio para ver sugerencias");
		commisionType.addKeyUpHandler(e -> {
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				commisionType.showSuggestionList();
			}
		});
		getAviableCommisionTypes(success -> {
			commisionType.setValue(null);
		});
		
		table.setWidget(5,0,new InlineLabel("Tipo Comisi\u00f3n"));
		table.getCellFormatter().setStyleName(5, 0, AON.CSS.aonTableLabel());
		table.setWidget(5,1,commisionType);
		table.getFlexCellFormatter().setColSpan(5, 1, 4);
		

		addInputStyle(taskHolder.getElement());
		taskHolder.setAutoSelectEnabled(false);
		taskHolder.getElement().setPropertyString("placeholder", "Cuota: ctrl + espacio para ver sugerencias");
		taskHolder.addKeyUpHandler(e -> {
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				taskHolder.showSuggestionList();
			}
		});
		getAviableSellerTaskHolders(success -> {
			taskHolder.setValue(null);
		});
		
		table.setWidget(6,0,new InlineLabel("Operario"));
		table.getCellFormatter().setStyleName(6, 0, AON.CSS.aonTableLabel());
		table.setWidget(6,1,taskHolder);
		table.getFlexCellFormatter().setColSpan(6, 1, 4);
		
		tablePanel.add( table );
		
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
				
				seller.setLegalPerson(type.getSelectedValue() == "1");
				seller.setDocumentType(DocumentType.valueOf(documentType.getSelectedValue()));
				seller.setDocumentCountry(Country.valueOf(documentNationality.getSelectedValue()));
				seller.setDocument(document.getValue());
				seller.setNationality(Country.valueOf(documentNationality.getSelectedValue()));
				
				seller.setName(name.getValue());
				seller.setAlias(alias.getValue());
				
				seller.setScope(new Scope().setId(Integer.parseInt(scope.getSelectedValue())));
				
				if(AonStringUtils.isNotBlank(commisionType.getValue())) {
					Integer commisionTypeId = Integer.parseInt(commisionType.getValue().split("\\[")[1].split("\\]")[0]);
					seller.setCommissionType(new CommissionType().setId(commisionTypeId));
				} else seller.setCommissionType(null);
				
				if(AonStringUtils.isNotBlank(taskHolder.getValue())) {
					Integer taskHolderId = Integer.parseInt(taskHolder.getValue().split("\\[")[1].split("\\]")[0]);
					seller.setTaskHolder(new TaskHolder().setRegistry(taskHolderId));
				} else seller.setTaskHolder(null);
				
				seller.setActive(isActiveToggleButton(active));
				
				commonService.saveSeller(domainName, domainId, user, seller, new AsyncCallback<Seller>() {

					@Override
					public void onSuccess(Seller seller) {
						callback.onAccept(seller);
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
	
	private void getEnableDisableButton(Button button, boolean disabled) {
		button.removeStyleName(disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
		button.removeStyleName(AON.AON_NO_MARGIN);
		button.removeStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);
		
		button.setStyleName(!disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE );
		button.setStyleName(AON.AON_NO_MARGIN, true);
		button.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
	}
	
	private boolean isActiveToggleButton(Button button) {
		return AonStringUtils.containsIgnoreCase(button.getStyleName(), AON.AON_ICON_ENABLE);
	}
	
	private void addInputStyle(Element el) {
		el.getStyle().setProperty("width", "-moz-available");
		el.getStyle().setProperty("width", "-webkit-fill-available");
		el.getStyle().setProperty("height", "1.1rem");
	}
	
	private void addSelectStyle(Element el) {
		el.getStyle().setProperty("width", "-moz-available");
		el.getStyle().setProperty("width", "-webkit-fill-available");
		el.getStyle().setProperty("height", "1.2rem");
	}

	private void setSelectedValueLB(ListBox lBox, String str) {
	    String text = str;
	    int indexToFind = 0;
	    for (int i = 0; i < lBox.getItemCount(); i++) {
	        if (lBox.getValue(i).equals(text)) {
	            indexToFind = i;
	            break;
	        }
	    }
	    lBox.setSelectedIndex(indexToFind);
	}
	
	private void getAviableCommisionTypes(Consumer<Void> success) {
		commonService.getAviableCommisionTypes(domainName, domainId, user, new AsyncCallback<List<CommissionType>>() {
			
			@Override
			public void onSuccess(List<CommissionType> newsSuggestion) {
				commisionTypes = newsSuggestion;
				
				List<String> suggestions = new ArrayList<String>();
				commisionTypes.forEach(newIt -> suggestions.add("[" + newIt.getId() + "] " + newIt.getName()));
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) commisionType.getSuggestOracle();
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
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) taskHolder.getSuggestOracle();
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
