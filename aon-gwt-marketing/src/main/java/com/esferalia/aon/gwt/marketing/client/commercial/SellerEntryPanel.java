package com.esferalia.aon.gwt.marketing.client.commercial;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAddInfoPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAddInfoPanel.AonAddInfoPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAddressPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAddressPanel.AonAddressPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAttachPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAttachPanel.AonAttachCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomSuggestBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMediaPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMediaPanel.AonMediaPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DocumentValidator;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.commission.CommissionType;
import com.esferalia.aon.occam.api.model.registry.RegistryAddInfo;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public abstract class SellerEntryPanel extends DeckLayoutPanel {
	
	// ------------------------------------------------- CommonServiceAsync
	
	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// ------------------------------------------------- Variables
	
	private final String EMPTY_STRING = "";
	
	private DockLayoutPanel sellerEntryPanel;

	private AonCustomToolbar toolbar;
	private AonToolbarButton previusSeller;
	private Label sellerIteration;
	private AonToolbarButton nextSeller;
	
	private HTMLPanel container;
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private TabLayoutPanel tablayoutPanel;

	// Seller Info (Table 1)
	private AonCustomTextBox name = new AonCustomTextBox("Nombre");
	private SellerStatusSelect sellerStatus;
	
	private AonCustomTextBox alias = new AonCustomTextBox("Alias");
	private AonCustomListBox scope = new AonCustomListBox("Ambito");
	
	private AonCustomListBox type = new AonCustomListBox("Entidad");
	private AonCustomListBox nationality = new AonCustomListBox("Nacionalidad");
	
	private AonCustomListBox documentType = new AonCustomListBox("Tipo");
	private AonCustomListBox documentNationality = new AonCustomListBox("Pais");
	private AonCustomTextBox document = new AonCustomTextBox("Documento");
	
	// Seller Info (Table 2)
	private AonCustomSuggestBox commisionType = new AonCustomSuggestBox("Tipo Comisi\u00f3n");
	private List<CommissionType> commisionTypes = new ArrayList<>();
	
	private AonCustomSuggestBox taskHolder = new AonCustomSuggestBox("Operario");
	private List<TaskHolder> taskHolders = new ArrayList<>();
	
	// Other Info
	private AddressTable addressTable;
	private MediaTable mediaTable;
	
	// RAddInfoTable (Tab)
	private RAddInfoTable raddInfoTable;
	
	// RattachTable (Tab)
	private RattachTable rattachTable;
	
	private SellerModuleOptions options;
	private Seller seller;
	
	public SellerEntryPanel(SellerModuleOptions options) {
		
		this.options = options;
		initializeCommonService();
		
		sellerEntryPanel = new DockLayoutPanel(Unit.PX);
		add(sellerEntryPanel);
		showWidget(sellerEntryPanel);
		
		tablayoutPanel = new TabLayoutPanel(25.00, Unit.PX);
		tablayoutPanel.setHeight((Window.getClientHeight() - 180) + "px");
		tablayoutPanel.getElement().getStyle().setProperty("margin", "0 1rem");
	
		createToolbar();
	}
	
	private void createToolbar() {
		toolbar = new AonCustomToolbar( "Comercial" );
		toolbar.showBackButton();
		toolbar.getBackButton().addClickHandler(e -> onBackClick());
		
		AonToolbarButton deleteButton = new AonToolbarButton( "Borrar Agente Comercial", AON.CSS.aonIconDelete());
		deleteButton.addClickHandler(e -> {
			deleteButton.setEnabled(false);
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n Agente Comercial",
					new HTML("Se va a proceder a eliminar al agente comercial <b>" + seller.getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					deleteButton.setEnabled(true);
				}

				@Override
				public void onAccept() {
					onSellerDeleteClick(seller.getId());
				}
			});
			
		});
		toolbar.addToolbarButton(deleteButton);
		
		AonToolbarButton saveButton = new AonToolbarButton("Guardar Agente Comercial", AON.CSS.aonIconSave());
		saveButton.addClickHandler(e -> saveSeller());
		toolbar.addToolbarButton(saveButton);
		
		previusSeller = new AonToolbarButton("Anterior Agente Comercial", AON.CSS.aonIconLeft());
		previusSeller.addClickHandler(e -> {
			Seller prevSeller = null == seller ? null : getPreviusSeller(seller.getId());
			onSellerSelectionChange(prevSeller);
		});
		toolbar.addToolbarButton(previusSeller);
		
		sellerIteration = new Label((null == seller ? "ND" : getSellerListPosition(seller.getId())) + " / " + getSellerListCount());
		toolbar.addToolbarButton(sellerIteration);
		
		nextSeller = new AonToolbarButton("Siguiente Agente Comercial", AON.CSS.aonIconRight());
		nextSeller.addClickHandler(e -> {
			Seller nxtSeller = null == seller ? null : getNextSeller(seller.getId());
			onSellerSelectionChange(nxtSeller);
		});
		toolbar.addToolbarButton(nextSeller);
		
		sellerEntryPanel.addNorth(toolbar, 50);
	}
	

	private void hideNavegationOptions() {
		previusSeller.setVisible(false);
		sellerIteration.setVisible(false);
		nextSeller.setVisible(false);
	}

	private void showNavegationOptions() {
		previusSeller.setVisible(true);
		sellerIteration.setVisible(true);
		nextSeller.setVisible(true);
	}

	private void saveSeller() {
		AonMessagePanel.showLoading(messagePanel, "Guardando agente comercial " + this.seller.getName());
		
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
		
		seller.setStatus(sellerStatus.getValue());
		
		commonService.saveSeller(options.getDomainName(), options.getDomain(), options.getUser(), seller, new AsyncCallback<Seller>() {
			
			@Override
			public void onSuccess(Seller result) {
				seller = result;
				AonMessagePanel.showSuccess(messagePanel, "Agente Comercial " + seller.getName()+ " guardado correctamente");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error guardado: " + caught.getMessage());
			}
		});
	}



	public void setSeller(Seller seller, boolean showNavOpt) {
		setSeller(seller, finish -> {
			if(showNavOpt) showNavegationOptions();
			else hideNavegationOptions();
		});
	}
	
	public void setSeller(Seller seller, Consumer<Void> finish) {
		getSeller(seller.getId(), dbSeller -> {
			this.seller = seller;
			
			tablayoutPanel.clear();
			sellerEntryPanel.clear();
			
			createToolbar();
			toolbar.setToolbarTitle("Comercial / ", seller.getName());
			
			container = new HTMLPanel(EMPTY_STRING);
			container.addStyleName(AON.CSS.aonFlexColumn());
			container.add(messagePanel);
			
			HTMLPanel rootPanel = new HTMLPanel(EMPTY_STRING);
			rootPanel.addStyleName(AON.CSS.aonFlexColumn());
			
			FlowPanel tablesPanel = new FlowPanel();
			tablesPanel.setStyleName(AON.CSS.aonItemFlex());
			tablesPanel.getElement().getStyle().setProperty("flex-wrap", "wrap");
			tablesPanel.getElement().getStyle().setProperty("align-items", "flex-start");
			
			sellerStatus = new SellerStatusSelect(seller.getStatus());
			AonCustomCard infoCard = new AonCustomCard("Informaci\u00f3n", sellerStatus);
			infoCard.getElement().getStyle().setProperty("max-width", "550px");
			infoCard.getElement().getStyle().setProperty("min-height", "150px");
			infoCard.getElement().getStyle().setProperty("width", "100%");
			infoCard.setToolbarWidgetShown();
			
			FlexTable table1 = new FlexTable();
			table1.setStyleName(AON.CSS.aonTable());
			table1.setWidth("100%");
			
			name.setValue(seller.getName());
			table1.setWidget(0,0,name);
			table1.getFlexCellFormatter().setColSpan(0, 0, 2);
			
			alias.setValue(seller.getAlias());
			table1.setWidget(1,0,alias);
			
			scope.clearItems();
			options.getConfiguration().getAvailableScopes().forEach(as -> scope.addItem(as.getDescription(), as.getId().toString()));
			scope.setValue(null != seller.getScope() ? seller.getScope().getId().toString() : null);
			table1.setWidget(1,1,scope);
			
			nationality.clearItems();
			for(int i=0; i < Country.values().length; i++)
				nationality.addItem(Country.values()[i].getName(), Country.values()[i].getIso2());
			nationality.setValue(seller.getNationality().getIso2());
			table1.setWidget(2,0,nationality);
			
			type.clearItems();
			type.addItem("P. F\u00edsicas", "0");
			type.addItem("P. Jur\u00eddicas", "1");
			type.setValue(seller.isLegalPerson() ? "1" : "0");
			table1.setWidget(2,1,type);

			FlowPanel documentPanel = new FlowPanel();
			documentPanel.setStyleName(AON.CSS.aonItemFlex());
			documentPanel.getElement().getStyle().setProperty("align-items", "flex-end");
			
			documentNationality.clearItems();
			for(int i=0; i < Country.values().length; i++)
				documentNationality.addItem(Country.values()[i].getIso2(), Country.values()[i].getIso2());
			documentNationality.setValue(seller.getDocumentCountry().getIso2());
			documentNationality.getElement().getStyle().setProperty("max-width", "5rem");
			documentPanel.add(documentNationality);
			
			document.getTextBox().addValueChangeHandler(e -> {
				DocumentType documentTypeValidator = DocumentValidator.validateDocument(document.getValue());
				documentType.setValue(documentTypeValidator.toString());
			});
			document.setValue(seller.getDocument());
			documentPanel.add(document);
			
			table1.setWidget(3,0,documentPanel);
			
			documentType.clearItems();
			for(int i=0; i < DocumentType.values().length; i++)
				documentType.addItem(DocumentType.values()[i].getDescription(), DocumentType.values()[i].toString());
			documentType.setValue(seller.getDocumentType().toString());
			table1.setWidget(3,1,documentType);
			
			table1.getColumnFormatter().setWidth(0, "5rem");
			
			infoCard.add(table1);
			
			tablesPanel.add(infoCard);
			
			AonCustomCard agentCard = new AonCustomCard("Agente Comercial");
			agentCard.getElement().getStyle().setProperty("max-width", "550px");
			agentCard.getElement().getStyle().setProperty("min-height", "150px");
			agentCard.getElement().getStyle().setProperty("width", "100%");
			
			FlexTable table2 = new FlexTable();
			table2.setStyleName(AON.CSS.aonTable());
			table2.setWidth("100%");
			
			commisionType.setAutoSelectEnabled(false);
			commisionType.setPlaceHolder("Cuota: ctrl + espacio para ver sugerencias");
			commisionType.getSuggestBox().addKeyUpHandler(e -> {
				if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
					commisionType.showSuggestionList();
				}
			});
			getAviableCommisionTypes(success -> {
				if(seller.getCommissionType() != null) {
					Optional<CommissionType> newOpt = commisionTypes.stream().filter(newIt -> newIt.getId().equals(seller.getCommissionType().getId())).findFirst();
					commisionType.setValue(newOpt.isPresent() ? "[" + newOpt.get().getId() + "] " + newOpt.get().getName() : "");
				} else commisionType.setValue(null);
			});
			
			table2.setWidget(0, 0, commisionType);
			
			taskHolder.setAutoSelectEnabled(false);
			taskHolder.setPlaceHolder("Cuota: ctrl + espacio para ver sugerencias");
			taskHolder.getSuggestBox().addKeyUpHandler(e -> {
				if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
					taskHolder.showSuggestionList();
				}
			});
			getAviableSellerTaskHolders(success -> {
				if(null != seller.getTaskHolder().getRegistry()) {
					taskHolder.setValue("[" + seller.getTaskHolder().getRegistry() + "] " + seller.getTaskHolder().getName());
				} else taskHolder.setValue(null);
			});
			
			table2.setWidget(1, 0, taskHolder);
			
			AonCustomTextBox workgroup = new AonCustomTextBox("G. Trabajo");
			workgroup.setEnable(false);
			if(null == seller.getTaskHolder()) workgroup.setValue("Este agente comercial no tiene operario asociado");
			else {
				getTaskHolderWorkgroups(workgroups -> {
					if(null == workgroups || workgroups.isEmpty()) workgroup.setValue("No existen grupos de trabajo para este operario");
					else workgroup.setValue(String.join(", ", workgroups.stream().map(workgroupIt -> workgroupIt.getDescription()).collect(Collectors.toList())));
				});
			}
			table2.setWidget(2, 0, workgroup);
			
			AonCustomTextBox user = new AonCustomTextBox("Usuario");
			user.setEnable(false);
			if(null == seller.getTaskHolder()) user.setValue("Este agente comercial no tiene operario asociado");
			else {
				if(null == seller.getTaskHolder().getUserId()) user.setValue("No existen usuario para este operario");
				else
					getTaskHolderUser(userFind -> {
						if(null == userFind) user.setValue("No existen usuario para este operario");
						else user.setValue(userFind.getLogin() + (null != userFind.getAuth() ? (" / " + userFind.getAuth().getEmail()) : ""));
					});
			}
			table2.setWidget(3, 0, user);
			
			taskHolder.getSuggestBox().addSelectionHandler(e -> {
				Integer taskHolderId = Integer.parseInt(taskHolder.getValue().split("\\[")[1].split("\\]")[0]);
				Optional<TaskHolder> taskHolderOpt = taskHolders.stream().filter(taskHolder -> taskHolder.getId().equals(taskHolderId)).findFirst();
				seller.setTaskHolder(taskHolderOpt.get());
				
				getTaskHolderWorkgroups(workgroups -> {
					if(null == workgroups || workgroups.isEmpty()) workgroup.setValue("No existen grupos de trabajo para este operario");
					else workgroup.setValue(String.join(", ", workgroups.stream().map(workgroupIt -> workgroupIt.getDescription()).collect(Collectors.toList())));
				});
				
				getTaskHolderUser(userFind -> {
					if(null == userFind) user.setValue("No existen usuario para este operario");
					else user.setValue(userFind.getLogin() + (null != userFind.getAuth() ? (" / " + userFind.getAuth().getEmail()) : ""));
				});
			});
			
			taskHolder.getSuggestBox().addValueChangeHandler(e -> {
				if(AonStringUtils.isBlank(taskHolder.getSuggestBox().getValue())) {
					seller.setTaskHolder(null);
					workgroup.setValue("No existen grupos de trabajo para este operario");
					user.setValue("No existen usuario para este operario");
				}
			});
			
			
			agentCard.add(table2);
			
			tablesPanel.add( agentCard );
			
			AonToolbarButton addAddress = new AonToolbarButton("Nueva Direcci\u00f3n", AON.CSS.aonIconAdd());
			addAddress.addClickHandler(e -> createAddres());
			AonCustomCard addressCard = new AonCustomCard("Direcci\u00f3n", addAddress);
			addressCard.getElement().getStyle().setProperty("max-width", "550px");
			addressCard.getElement().getStyle().setProperty("min-height", "150px");
			addressCard.getElement().getStyle().setProperty("width", "100%");
			
			addressTable = new AddressTable(options.getDomainName(), options.getDomain(), options.getUser(), seller.getId()) {

				@Override
				protected void onShowErrorMessage(String errorMessage) {
					AonMessagePanel.showError(messagePanel, errorMessage);
				}

				@Override
				protected LinkedList<GeoZone> getAviableGeozones() {
					return options.getConfiguration().getGeozones();
				}
			
			};
			addressCard.add(addressTable);
			
			tablesPanel.add( addressCard );
			
			AonToolbarButton addMedia = new AonToolbarButton("Nuevo contacto", AON.CSS.aonIconAdd());
			addMedia.addClickHandler(e -> createMedia());
			AonCustomCard mediaCard = new AonCustomCard("Contacto", addMedia);
			mediaCard.getElement().getStyle().setProperty("max-width", "550px");
			mediaCard.getElement().getStyle().setProperty("min-height", "150px");
			mediaCard.getElement().getStyle().setProperty("width", "100%");
			
			mediaTable = new MediaTable(options.getDomainName(), options.getDomain(), options.getUser(), seller.getId()) {

				@Override
				protected void onShowErrorMessage(String errorMessage) {
					AonMessagePanel.showError(messagePanel, errorMessage);
				}
			
			};
			mediaCard.add(mediaTable);
			
			tablesPanel.add( mediaCard );
			
			rootPanel.add( tablesPanel );
			
			ScrollPanel rootScroll = new ScrollPanel(rootPanel);
			rootScroll.getElement().getStyle().setProperty("margin-top", "1rem");
			tablayoutPanel.add(rootScroll, "Datos Generales");
			
			//Other data
			AonToolbarButton addOtherData = new AonToolbarButton("Nuevo", AON.CSS.aonIconAdd());
			addOtherData.addClickHandler(e -> createOtherData());
			AonCustomCard otherDataCard = new AonCustomCard("Otros Datos", addOtherData);
			otherDataCard.getElement().getStyle().setProperty("margin-top", "1rem");
			raddInfoTable = new RAddInfoTable(options.getDomainName(), options.getDomain(), options.getUser(), seller.getId()) {

				@Override
				protected void onShowErrorMessage(String errorMessage) {
					AonMessagePanel.showError(messagePanel, errorMessage);
				}
			
			};
			otherDataCard.add(raddInfoTable);
			tablayoutPanel.add(otherDataCard, "Otros Datos");
			
			//Docuemnt
			AonToolbarButton addDocument = new AonToolbarButton("Nuevo Documento", AON.CSS.aonIconAdd());
			addDocument.addClickHandler(e -> createDocument());
			AonCustomCard documentCard = new AonCustomCard("Documentos", addDocument);
			documentCard.getElement().getStyle().setProperty("margin-top", "1rem");
			rattachTable = new RattachTable(options.getDomainName(), options.getDomain(), options.getUser(), options.getConfiguration().getAvailableScopes(), seller.getId()) {

				@Override
				protected void onShowErrorMessage(String errorMessage) {
					AonMessagePanel.showError(messagePanel, errorMessage);
				}
			
			};
			documentCard.add(rattachTable);
			tablayoutPanel.add(documentCard, "Documentos");
			
			container.add(tablayoutPanel);
			
			sellerEntryPanel.add(container);
			
			Scheduler.get().scheduleDeferred(new Command() {
		        public void execute() {
		        	name.setFocus(true);
		        }
		    });
			
			finish.accept(null);
		});
	}

	private void createAddres() {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "Nueva Direcci\u00f3n" );
		
		final AonAddressPanel marketingCampaignPanel = new AonAddressPanel( options.getDomainName(), options.getDomain(), options.getUser(), options.getConfiguration().getGeozones(), seller.getId(), new AonAddressPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept(RegistryAddress address) {
				dialog.hide();
				addressTable.onSearch();
			}
		});
		
		dialog.add( marketingCampaignPanel );
		dialog.showLoaded();
	}
	
	private void createMedia() {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "Nuevo Contacto" );
		
		final AonMediaPanel marketingCampaignPanel = new AonMediaPanel( options.getDomainName(), options.getDomain(), options.getUser(), seller.getId(), new AonMediaPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept(RegistryMedia media) {
				dialog.hide();
				mediaTable.onSearch();
			}
		});
		
		dialog.add( marketingCampaignPanel );
		dialog.showLoaded();
	}
	
	private void createOtherData() {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "Nuevo Otro Dato" );
		
		final AonAddInfoPanel marketingCampaignPanel = new AonAddInfoPanel( options.getDomainName(), options.getDomain(), options.getUser(), seller.getId(), new AonAddInfoPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept(RegistryAddInfo rAddInfo) {
				dialog.hide();
				raddInfoTable.onSearch();
			}
		});
		
		dialog.add( marketingCampaignPanel );
		dialog.showLoaded();
	}
	

	private void createDocument() {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "Nuevo Documento" );
		
		final AonAttachPanel marketingCampaignPanel = new AonAttachPanel( options.getDomainName(), options.getDomain(), options.getUser(), options.getConfiguration().getAvailableScopes(), seller.getId(), new AonAttachCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept() {
				dialog.hide();
				rattachTable.onSearch();
			}
		});
		
		dialog.add( marketingCampaignPanel );
		dialog.showLoaded();
	}

	private void getAviableCommisionTypes(Consumer<Void> success) {
		commonService.getAviableCommisionTypes(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<CommissionType>>() {
			
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
		commonService.getAviableSellerTaskHolders(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<TaskHolder>>() {
			
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
	
	private void getTaskHolderWorkgroups(Consumer<List<Workgroup>> success) {
		commonService.getTaskHolderWorkgroups(options.getDomainName(), options.getDomain(), options.getUser(), seller.getTaskHolder().getRegistry(), new AsyncCallback<List<Workgroup>>() {
			
			@Override
			public void onSuccess(List<Workgroup> workgroups) {
				success.accept(workgroups);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}
	
	private void getTaskHolderUser(Consumer<User> success) {
		commonService.getTaskHolderUser(options.getDomainName(), options.getDomain(), options.getUser(), seller.getTaskHolder().getUserId(), new AsyncCallback<User>() {
			
			@Override
			public void onSuccess(User user) {
				success.accept(user);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}
	
	private void getSeller(Integer sellerId, Consumer<Seller> success) {
		commonService.getSeller(options.getDomainName(), options.getDomain(), options.getUser(), sellerId, new AsyncCallback<Seller>() {
			
			@Override
			public void onSuccess(Seller seller) {
				success.accept(seller);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}
	
	protected abstract void onBackClick();
	protected abstract void onSellerDeleteClick(Integer sellerId);
	
	protected abstract Integer getSellerListPosition(Integer sellerId);
	protected abstract Integer getSellerListCount();
	
	protected abstract Seller getPreviusSeller(Integer sellerId);
	protected abstract Seller getNextSeller(Integer sellerId);
	protected abstract void onSellerSelectionChange(Seller seller);

}
