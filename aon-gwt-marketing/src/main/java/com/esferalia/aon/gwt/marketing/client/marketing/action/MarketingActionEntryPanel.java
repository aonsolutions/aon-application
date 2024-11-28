package com.esferalia.aon.gwt.marketing.client.marketing.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonErrorPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMarketingActionTargetCreationPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMarketingActionTargetCreationPanel.AonMarketingActionTargetCreationPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMarketingActionTargetPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMarketingActionTargetPanel.AonMarketingActionTargetPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.marketing.client.marketing.MarketingModuleOptions;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.MarketingAction.MarketingActionMediaType;
import com.esferalia.aon.occam.api.model.MarketingAction.MarketingSellerDistribution;
import com.esferalia.aon.occam.api.model.MarketingActionTarget;
import com.esferalia.aon.occam.api.model.MarketingActionTargetParams;
import com.esferalia.aon.occam.api.model.Newsletter;
import com.esferalia.aon.occam.api.model.Survey;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.news.News;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.project.ProjectCommercial;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;

public abstract class MarketingActionEntryPanel extends DockLayoutPanel {
	
	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	private AonToolbar toolbar;

	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");	
	
	// MarketingCampaign Info
	private TextBox description = new TextBox();
	private Label actionType = new Label();
	private ListBox typeListBox = new ListBox();
	private SuggestBox tagSuggestBox = new SuggestBox();
	private List<Tag> tags = new ArrayList<>();
	private AonDoubleBox budget = new AonDoubleBox(15, 2);
	private AonDoubleBox expense = new AonDoubleBox(15, 2);
	private AonDateBox startDate = new AonDateBox();
	private AonDateBox endDate = new AonDateBox();
	private ListBox workgroup = new ListBox();
	private ListBox sellerDistribution = new ListBox();
	private InlineLabel taskHolderLabel = new InlineLabel("Asignado a");
	private ListBox taskHolder = new ListBox();
	
	private SuggestBox newsSuggestBox = new SuggestBox();
	private List<News> news = new ArrayList<>();
	
	private SuggestBox surveySuggestBox = new SuggestBox();
	private List<Survey> surveis = new ArrayList<>();
	
	private SuggestBox newsletterSuggestBox = new SuggestBox();
	private List<Newsletter> newsletter = new ArrayList<>();
	
	private Integer projectCommercialIndx = 0;
	
	
	// MarketingAction Search
	private FlowPanel searchPanel;
	private FlowPanel filterPanel;
	
	private TextBox targetDescription;
	private ListBox targetStatus;
	
	private SimpleLayoutPanel centerPanel;
	
	private MarketingActionTargetPanel marketingActionPanel;
	
	private MarketingModuleOptions options;
	private MarketingAction marketingAction;
	
	public MarketingActionEntryPanel(MarketingModuleOptions options) {
		super(Unit.PX);
		
		initializeCommonService();
		this.options = options;
		
		createToolbar();
	}
	
	private void createToolbar() {
		toolbar = new AonToolbar( "ACCI\u00f3N" );
		
		AonToolbarButton backButton = new AonToolbarButton("Volver a la campa\u00f1a", AON.CSS.aonIconBack());
		backButton.addClickHandler(e -> onActionBackClick());
		toolbar.add(backButton);
		
		AonToolbarButton deleteButton = new AonToolbarButton("Borrar acci\u00f3n", AON.CSS.aonIconDelete());
		deleteButton.addClickHandler(e -> {
			deleteButton.setEnabled(false);
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n Acci\u00f3n",
					new HTML("Se va a proceder a eliminar la acci\u00f3n <b>" + marketingAction.getDescription() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					deleteButton.setEnabled(true);
				}

				@Override
				public void onAccept() {
					onActionDeleteClick(marketingAction);
				}
			});
			
		});
		toolbar.add(deleteButton);
		
		AonToolbarButton saveButton = new AonToolbarButton("Guardar acci\u00f3n", AON.CSS.aonIconSave());
		saveButton.addClickHandler(e -> saveMarketingAction());
		toolbar.add(saveButton);
		
		AonToolbarButton newActionTargetButton = new AonToolbarButton("A\u00f1adir cliente potencial", AON.CSS.aonIconAdd());
		newActionTargetButton.addClickHandler(e -> showMarketingActionTargetDialog());
		toolbar.add(newActionTargetButton);
		
		addNorth(toolbar, 50);
	}

	private void importActionTarget() {
		if(AonStringUtils.isBlank(taskHolder.getSelectedValue()))
			importActionTarget(null);
		else {
			commonService.getSellerByTaskHolder(options.getDomainName(), options.getDomain(), options.getUser(), Integer.parseInt(taskHolder.getSelectedValue()), new AsyncCallback<Seller>() {
				
				@Override
				public void onSuccess(Seller seller) {
					importActionTarget(seller);
				}

				@Override
				public void onFailure(Throwable caught) {
					AonMessagePanel.showError(messagePanel, "Error getSellerByTaskHolder(): " + caught.getMessage());
				}
				
			});
		}
	}
	
	private void importActionTarget(Seller seller) {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "Ejemplo formulario web (API)" );
		dialog.showCloseButton(true);
		
		final AonMarketingActionTargetCreationPanel aonMarketingActionPanel = new AonMarketingActionTargetCreationPanel( options.getDomainName(), options.getDomain(), options.getUser(), options.getConfiguration().getAvailableScopes(),  options.getConfiguration().getGeozones(), marketingAction, seller, new AonMarketingActionTargetCreationPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept(String domainName, String login, Integer domainId, String sessionId, JSONObject json) {
				dialog.hide();
				String host = Window.Location.getHost();
				String endPoint = "/ms/api/action-target/";
				
				HashMap<String, String> headers = new HashMap<>();
				headers.put("domain_name", AonStringUtils.isBlank(domainName) ? options.getDomainName() : domainName);
				headers.put("domain_login", AonStringUtils.isBlank(login) ? options.getUser() : login);
				headers.put("domain_id", null == domainId ? String.valueOf(options.getDomain()) : String.valueOf(domainId));
				
				JSONObject body = new JSONObject();
				body.put("actionTarget", json);
				
				// Create a URL builder and add query parameters
				UrlBuilder urlBuilder = new UrlBuilder();
				urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
				urlBuilder.setHost(host); 
				urlBuilder.setPath(endPoint);
				
				// Create the request builder with the complete URL
				RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, urlBuilder.buildString());
				requestBuilder.setHeader("session_id", AonStringUtils.isBlank(sessionId) ? "AONd95770f269e711eb94390242ac130002" : sessionId);
				
				headers.entrySet().forEach(entry -> requestBuilder.setHeader(entry.getKey(), entry.getValue()));
				
				try {
				    // Send the request
				    requestBuilder.sendRequest(body.toString(), new RequestCallback() {
				        public void onResponseReceived(Request request, Response response) {
				        	JSONValue jsonValue = JSONParser.parseStrict(response.getText());
				        	 String message = "";
			        	    if (jsonValue != null && jsonValue.isObject() != null) {
			        	        JSONObject jsonObject = jsonValue.isObject();
			        	        
			        	        JSONValue messageValue = jsonObject.get("message");
			        	        message = null != messageValue ? messageValue.isString().stringValue() : "Error desconocido";
			        	    }
				        	
				        	if(response.getStatusCode() == 400) {
				        		AonMessagePanel.showError(messagePanel, message);
				        	} else {
				        		AonMessagePanel.showSuccess(messagePanel, message);
				        		
				        		Timer timer = new Timer() {
					       		     @Override
					       		     public void run() {
					       		    	marketingActionPanel.resetSearchOffset();
										setMarketingAction(marketingAction);
					       		     }
					       		};
					       		timer.schedule(2500);
					        	
				        	}
				        }

						public void onError(Request request, Throwable exception) {
							Window.alert(exception.getMessage());
				        }
				    });
				} catch (RequestException exception) {
					Window.alert("Catch : " + exception.getMessage());
				}
			}
		}) {

			@Override
			protected void onResize() {
				dialog.showLoaded();
			}};
		
		dialog.add( aonMarketingActionPanel );
		dialog.showLoaded();
	}

	private void saveMarketingAction() {
		AonMessagePanel.showLoading(messagePanel, "Guardando acci\u00f3n " + this.marketingAction.getDescription());
		
		marketingAction.setDescription(description.getValue());
		marketingAction.setMediaType(MarketingActionMediaType.getMediaType(Integer.parseInt(typeListBox.getSelectedValue())));
		marketingAction.setStartDate(startDate.getValue());
		marketingAction.setEndDate(endDate.getValue());
		marketingAction.setBudget(budget.getValue());
		marketingAction.setExpense(expense.getValue());
		marketingAction.setWorkgroup(0 == workgroup.getSelectedIndex() ? null : new Workgroup().setId(Integer.parseInt(workgroup.getSelectedValue())));
		marketingAction.setSellerDistribution(MarketingSellerDistribution.getSellerDistribution(Integer.parseInt(sellerDistribution.getSelectedValue())));
		if(marketingAction.getSellerDistribution() == MarketingSellerDistribution.MANUAL)
			marketingAction.setTaskHolder(0 == taskHolder.getSelectedIndex() ? null : new TaskHolder().setRegistry(Integer.parseInt(taskHolder.getSelectedValue())));
		else
			marketingAction.setTaskHolder(null);
		
		switch (MarketingActionMediaType.getMediaType(Integer.parseInt(typeListBox.getSelectedValue()))) {
			case PHONE:
				newsletterSuggestBox.setValue("");
				tagSuggestBox.setValue("");
				break;
			case EMAIL:
				newsletterSuggestBox.setValue("");
				surveySuggestBox.setValue("");
				tagSuggestBox.setValue("");
				break;
			case MAIL:
				newsSuggestBox.setValue("");
				newsletterSuggestBox.setValue("");
				surveySuggestBox.setValue("");
				tagSuggestBox.setValue("");
				break;
			case BULLETIN:
				newsSuggestBox.setValue("");
				surveySuggestBox.setValue("");
				tagSuggestBox.setValue("");
				break;
			default:
				newsSuggestBox.setValue("");
				newsletterSuggestBox.setValue("");
				surveySuggestBox.setValue("");
				break;
		}
		
		if(AonStringUtils.isNotBlank(newsSuggestBox.getValue())) {
			Integer newsId = Integer.parseInt(newsSuggestBox.getValue().split("\\[")[1].split("\\]")[0]);
			marketingAction.setNews(newsId);
		} else marketingAction.setNews(null);
		
		if(AonStringUtils.isNotBlank(newsletterSuggestBox.getValue())) {
			Integer newsletterId = Integer.parseInt(newsletterSuggestBox.getValue().split("\\[")[1].split("\\]")[0]);
			marketingAction.setNewsletter(newsletterId);
		} else marketingAction.setNewsletter(null);
		
		if(AonStringUtils.isNotBlank(surveySuggestBox.getValue())) {
			Integer surveyId = Integer.parseInt(surveySuggestBox.getValue().split("\\[")[1].split("\\]")[0]);
			marketingAction.setSurvey(new Survey().setId(surveyId));
		} else marketingAction.setSurvey(null);
		
		if(AonStringUtils.isNotBlank(tagSuggestBox.getValue())) {
			if(AonStringUtils.contains(tagSuggestBox.getValue(), '[')) {
				marketingAction.setTag(new Tag().setId(Integer.parseInt(tagSuggestBox.getValue().split("\\[")[1].split("\\]")[0])));
			} else {
				marketingAction.setTag(new Tag().setDomain(marketingAction.getDomain()).setTagType(TagType.MARKETING).setName(tagSuggestBox.getValue()));
			}
		} else marketingAction.setTag(null);
		
		commonService.saveMarketingAction(options.getDomainName(), options.getDomain(), options.getUser(), marketingAction, new AsyncCallback<MarketingAction>() {
			
			@Override
			public void onSuccess(MarketingAction result) {
				marketingAction = result;
				setMarketingAction(marketingAction);
				AonMessagePanel.showSuccess(messagePanel, "Acci\u00f3n " + marketingAction.getDescription()+ " guardada correctamente");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error guardado: " + caught.getMessage());
			}
		});
	}

	private void showMarketingActionTargetDialog() {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "Cliente Potencial" );
		final AonMarketingActionTargetPanel aonMarketingActionPanel = new AonMarketingActionTargetPanel( options.getDomainName(), options.getDomain(), options.getUser(), this.marketingAction, new AonMarketingActionTargetPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept() {
				dialog.hide();
				marketingActionPanel.resetSearchOffset();
				setMarketingAction(marketingAction);
			}
		}) {

			@Override
			protected void onResize() {
				dialog.showLoaded();
			}};
		
		dialog.add( aonMarketingActionPanel );
		dialog.showLoaded();
	}
	
	public void setMarketingAction(MarketingAction marketingAction) {
		commonService.getMarketingAction(options.getDomainName(), options.getDomain(), options.getUser(), marketingAction.getId(), new AsyncCallback<MarketingAction>() {
			
			@Override
			public void onSuccess(MarketingAction marketingActionDB) {
				marketingActionDB.setMarketingCampaign(marketingAction.getMarketingCampaign());
				initializeMarketingAction(marketingActionDB);
			}
			
			@Override
			public void onFailure(Throwable arg0) {
				
			}
		});
	}

	public void initializeMarketingAction(MarketingAction marketingAction) {
		this.marketingAction = marketingAction;
		
		clear();
		
		createToolbar();
		this.toolbar.setTitle("Acci\u00f3n " + marketingAction.getDescription());
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(messagePanel);
		
		FlowPanel rootPanel = new FlowPanel();
		rootPanel.setStyleName(AON.CSS.aonFlexColumnBetween());
		
		final AonErrorPanel errorPanel = new AonErrorPanel();
		errorPanel.addStyleName(AON.CSS.aonMarginTop());
		rootPanel.add(errorPanel);
		
		FlowPanel tablePanel = new FlowPanel();
		tablePanel.setStyleName(AON.CSS.aonScrollArea());
		tablePanel.getElement().getStyle().setProperty("align-items", "start");
		tablePanel.addStyleName(AON.CSS.aonItemFlex());
		tablePanel.addStyleName(AON.CSS.aonFlexBetween());

		FlexTable table = new FlexTable();
		table.setStyleName(AON.CSS.aonTable());
		table.getElement().getStyle().setProperty("width", "30rem");
		
		table.setWidget(0, 0, new InlineLabel("Campa\u00f1a"));
		table.getCellFormatter().setStyleName(0, 0, AON.CSS.aonTableLabel());
		Label campaignLabel = new Label(marketingAction.getMarketingCampaign().getDescription());
		campaignLabel.addStyleName(AON.CSS.aonItemFlex());
		addInputStyle(campaignLabel.getElement());
		table.setWidget(0, 1, campaignLabel);
		table.getFlexCellFormatter().setColSpan(0, 1, 3);
		
		table.setWidget(1, 0, new InlineLabel("Acci\u00f3n"));
		table.getCellFormatter().setStyleName(1, 0, AON.CSS.aonTableLabel());
		
		description.setValue(marketingAction.getDescription());
		description.setMaxLength(64);
		description.setStyleName(AON.CSS.aonInputText());
		addInputStyle(description.getElement());
		table.setWidget(1, 1, description);
		table.getFlexCellFormatter().setColSpan(1, 1, 3);
		
		table.setWidget(2, 0, new InlineLabel("Tipo"));
		table.getCellFormatter().setStyleName(2, 0, AON.CSS.aonTableLabel());
		actionType.setText(getActivonType(marketingAction.getMediaType().getValue()));
		table.setWidget(2, 1, actionType);
		
		table.setWidget(2, 2, new InlineLabel("Canal"));
		table.getCellFormatter().setStyleName(2, 2, AON.CSS.aonTableLabel());
		
		HTMLPanel typePanel = new HTMLPanel("");
		typePanel.addStyleName(AON.CSS.aonItemFlex());
		
		addSelectStyle(typeListBox.getElement());
		typeListBox.clear();
		for(int i=0; i<MarketingActionMediaType.values().length; i++) {
			MarketingActionMediaType marketingActionMediaType = MarketingActionMediaType.values()[i];
			typeListBox.addItem(marketingActionMediaType.getDescription(), marketingActionMediaType.getValue().toString());
		}
		typeListBox.setStyleName(AON.CSS.aonInputText());
		typeListBox.addChangeHandler(e -> {
			MarketingActionMediaType marketingActionMediaType = MarketingActionMediaType.getMediaType(Integer.parseInt(typeListBox.getSelectedValue()));
			showMarketingActionMediaOptions(table, marketingActionMediaType);
		});
		setSelectedValueLB(typeListBox, marketingAction.getMediaType().getValue().toString());
		typePanel.add(typeListBox);
		
		tagSuggestBox.setStyleName(AON.CSS.aonInputText());
		addInputStyle(tagSuggestBox.getElement());
		tagSuggestBox.setAutoSelectEnabled(false);
		tagSuggestBox.getElement().setPropertyString("placeholder", "Etiqueta");
		tagSuggestBox.addKeyUpHandler(e -> {
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				tagSuggestBox.showSuggestionList();
			}
		});
		getTagSuggestion(success -> {
			if(marketingAction.getTag() != null) {
				Optional<Tag> tagOpt = tags.stream().filter(tag -> tag.getId().equals(marketingAction.getTag().getId())).findFirst();
				tagSuggestBox.setValue(tagOpt.isPresent() ? "[" + tagOpt.get().getId() + "] " + tagOpt.get().getName() : "");
			} else tagSuggestBox.setValue(null);
		});
		typePanel.add(tagSuggestBox);
		table.setWidget(2, 3, typePanel);
		

		table.setWidget(3, 0, new InlineLabel("Presupuesto"));
		table.getCellFormatter().setStyleName(3, 0, AON.CSS.aonTableLabel());
		budget.setValue(marketingAction.getBudget());
		addInputStyle(budget.getElement());
		table.setWidget(3, 1, budget);
		
		table.setWidget(3, 2, new InlineLabel("Gastos"));
		table.getCellFormatter().setStyleName(3, 2, AON.CSS.aonTableLabel());
		expense.setValue(marketingAction.getExpense());
		addInputStyle(expense.getElement());
		table.setWidget(3, 3, expense);

		table.setWidget(4, 0, new InlineLabel("F. Inicio"));
		table.getCellFormatter().setStyleName(4, 0, AON.CSS.aonTableLabel());
		startDate.setStyleName(AON.CSS.aonInputText());
		startDate.addStyleName(AON.CSS.aonTextCenter());
		addInputStyle(startDate.getElement());
		startDate.setValue(marketingAction.getStartDate());
		table.setWidget(4, 1, startDate);

		table.setWidget(4, 2, new InlineLabel("F. Fin"));
		table.getCellFormatter().setStyleName(4, 2, AON.CSS.aonTableLabel());
		endDate.setStyleName(AON.CSS.aonInputText());
		endDate.addStyleName(AON.CSS.aonTextCenter());
		addInputStyle(endDate.getElement());
		endDate.setValue(marketingAction.getEndDate());
		table.setWidget(4, 3, endDate);
		
		// Accion Comercial
		HTMLPanel sellerDistributionPanel = new HTMLPanel("");
		sellerDistributionPanel.addStyleName(AON.CSS.aonItemFlex());
		
		AonTableButton projectCommercialBtn = new AonTableButton("Crear operaci\u00f3n comercial", AON.CSS.aonIconWorkAdd());
		projectCommercialBtn.addClickHandler(e -> createProjectCommercial());
		
		workgroup = new ListBox();
		addSelectStyle(workgroup.getElement());
		workgroup.addItem("-", "");
		workgroup.addChangeHandler(e -> {
			if(workgroup.getSelectedIndex() == 0) {
				taskHolder.setSelectedIndex(0);
				taskHolder.setVisible(false);
				taskHolderLabel.setVisible(false);
			} else {
				taskHolder.setVisible(true);
				taskHolderLabel.setVisible(true);	
				getAviableTaskHolders(Integer.parseInt(workgroup.getSelectedValue()), taskHolders -> { 
					taskHolder.clear();
					taskHolder.addItem("-", "");
					taskHolders.forEach(taskHolderIt -> taskHolder.addItem(taskHolderIt.getName(), taskHolderIt.getRegistry().toString()));
				});
			}
			
			marketingAction.setWorkgroup(0 == workgroup.getSelectedIndex() ? null : new Workgroup().setId(Integer.parseInt(workgroup.getSelectedValue())));
		});
		
		taskHolder = new ListBox();
		addSelectStyle(taskHolder.getElement());
		taskHolder.addItem("-", "");
		taskHolder.addChangeHandler(e -> {
			projectCommercialBtn.setEnabled(AonStringUtils.isNotBlank(taskHolder.getSelectedValue()));	
		});
		
		getAviableWorkgroups(workgroups -> {
			workgroups.forEach(workgroupIt -> workgroup.addItem(workgroupIt.getDescription(), workgroupIt.getId().toString()));
			setSelectedValueLB(workgroup, null != marketingAction.getWorkgroup() ? marketingAction.getWorkgroup().getId().toString() : null);
			
			if(null != marketingAction.getWorkgroup()) {	
				getAviableTaskHolders(marketingAction.getWorkgroup().getId(), taskHolders -> {
					taskHolder.clear();
					taskHolder.addItem("-", ""); 
					taskHolders.forEach(taskHolderIt -> taskHolder.addItem(taskHolderIt.getName(), taskHolderIt.getRegistry().toString()));
					setSelectedValueLB(taskHolder, null != marketingAction.getTaskHolder() ? marketingAction.getTaskHolder().getRegistry().toString() : null);
					
					if(null != marketingAction.getTaskHolder() && marketingAction.getSellerDistribution() == MarketingSellerDistribution.MANUAL) projectCommercialBtn.setEnabled(true);
					
					createMarketingActionTargets();
				});
			} else {
				taskHolder.setVisible(false);
				taskHolderLabel.setVisible(false);
				createMarketingActionTargets();
			}	
		});
		
		table.setWidget(5, 0, new InlineLabel("Grupo comercial"));
		table.getCellFormatter().setStyleName(5, 0, AON.CSS.aonTableLabel());
		table.setWidget(5, 1, workgroup);
		
		InlineLabel distributionType = new InlineLabel("T. Asignaci\u00f3n");
		distributionType.setStyleName(AON.CSS.aonTableLabel());
		
		sellerDistribution.clear();
		addSelectStyle(sellerDistribution.getElement());
		for(int i=0; i < MarketingSellerDistribution.values().length; i++)
			sellerDistribution.addItem(MarketingSellerDistribution.values()[i].getDescription(), MarketingSellerDistribution.values()[i].getValue().toString());
		setSelectedValueLB(sellerDistribution, marketingAction.getSellerDistribution().getValue().toString());
		sellerDistribution.addChangeHandler(e -> checkSellerDistribution(projectCommercialBtn));
		checkSellerDistribution(projectCommercialBtn);
		
		taskHolderLabel.setStyleName(AON.CSS.aonTableLabel());
		
		sellerDistributionPanel.add(sellerDistribution);
		sellerDistributionPanel.add(taskHolderLabel);
		sellerDistributionPanel.add(taskHolder);
		sellerDistributionPanel.add(projectCommercialBtn);
		
		table.setWidget(5, 2, distributionType);
		table.setWidget(5, 3, sellerDistributionPanel);
		table.getFlexCellFormatter().setColSpan(5, 3, 2);
		
		table.setWidget(6, 0,new InlineLabel("Noticia"));
		table.getCellFormatter().setStyleName(6, 0, AON.CSS.aonTableLabel());
		newsSuggestBox.setStyleName(AON.CSS.aonInputText());
		addInputStyle(newsSuggestBox.getElement());
		newsSuggestBox.setAutoSelectEnabled(false);
		newsSuggestBox.getElement().setPropertyString("placeholder", "Cuota: ctrl + espacio para ver sugerencias");
		newsSuggestBox.addKeyUpHandler(e -> {
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				newsSuggestBox.showSuggestionList();
			}
		});
		getNewsSuggestion(success -> {
			if(marketingAction.getNews() != null) {
				Optional<News> newOpt = news.stream().filter(newIt -> newIt.getId().equals(marketingAction.getNews())).findFirst();
				newsSuggestBox.setValue(newOpt.isPresent() ? "[" + newOpt.get().getId() + "] " + newOpt.get().getTitle() : "");
			} else newsSuggestBox.setValue(null);
		});
		table.setWidget(6,1,newsSuggestBox);
		table.getFlexCellFormatter().setColSpan(6, 1, 3);
		
		table.setWidget(7, 0, new InlineLabel("Boletin"));
		table.getCellFormatter().setStyleName(7, 0, AON.CSS.aonTableLabel());
		newsletterSuggestBox.setStyleName(AON.CSS.aonInputText());
		addInputStyle(newsletterSuggestBox.getElement());
		newsletterSuggestBox.setAutoSelectEnabled(false);
		newsletterSuggestBox.getElement().setPropertyString("placeholder", "Cuota: ctrl + espacio para ver sugerencias");
		newsletterSuggestBox.addKeyUpHandler(e -> {
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				newsletterSuggestBox.showSuggestionList();
			}
		});
		getNewsletterSuggestion(success -> {
			if(marketingAction.getNewsletter() != null) {
				Optional<Newsletter> newletterOpt = newsletter.stream().filter(newletter -> newletter.getId().equals(marketingAction.getNewsletter())).findFirst();
				newsletterSuggestBox.setValue(newletterOpt.isPresent() ? "[" + newletterOpt.get().getId() + "] " + newletterOpt.get().getName() : "");
			} else newsletterSuggestBox.setValue(null);
		});
		table.setWidget(7, 1, newsletterSuggestBox);
		table.getFlexCellFormatter().setColSpan(7, 1, 3);
		
		table.setWidget(8, 0, new InlineLabel("Cuestionario"));
		table.getCellFormatter().setStyleName(8, 0, AON.CSS.aonTableLabel());
		surveySuggestBox.setStyleName(AON.CSS.aonInputText());
		addInputStyle(surveySuggestBox.getElement());
		surveySuggestBox.setAutoSelectEnabled(false);
		surveySuggestBox.getElement().setPropertyString("placeholder", "Cuota: ctrl + espacio para ver sugerencias");
		surveySuggestBox.addKeyUpHandler(e -> {
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				surveySuggestBox.showSuggestionList();
			}
		});
		getSurveySuggestion(success -> {
			if(marketingAction.getSurvey() != null) {
				Optional<Survey> surveyOpt = surveis.stream().filter(survey -> survey.getId().equals(marketingAction.getSurvey().getId())).findFirst();
				surveySuggestBox.setValue(surveyOpt.isPresent() ? "[" + surveyOpt.get().getId() + "] " + surveyOpt.get().getDescription() : "");
			} else surveySuggestBox.setValue(null);
		});
		table.setWidget(8, 1, surveySuggestBox);
		table.getFlexCellFormatter().setColSpan(8, 1, 3);
		
		table.setWidget(9, 0, new InlineLabel("Formulario Web"));
		table.getCellFormatter().setStyleName(9, 0, AON.CSS.aonTableLabel());
		Label webForm = new Label("Ejemplo del formulario web (API)");
		webForm.addStyleName(AON.CSS.aonItemFlex());
		webForm.getElement().getStyle().setProperty("cursor", "pointer");
		addInputStyle(webForm.getElement());
		webForm.addClickHandler(e -> importActionTarget());
		table.setWidget(9, 1, webForm);
		table.getFlexCellFormatter().setColSpan(9, 1, 3);
		
		showMarketingActionMediaOptions(table, marketingAction.getMediaType());
		
		tablePanel.add( table );
		
		FlexTable tableActionTargetStatus = new FlexTable();
		tableActionTargetStatus.setStyleName(AON.CSS.aonTable());
		tableActionTargetStatus.getElement().getStyle().setProperty("border", "1px solid #ebebeb");
		tableActionTargetStatus.getElement().getStyle().setProperty("border-radius", "5px");
		tableActionTargetStatus.getElement().getStyle().setProperty("min-width", "140px");
		tableActionTargetStatus.getElement().getStyle().setProperty("margin-right", "4rem");
		
		InlineLabel status = new InlineLabel("ESTADOS");
		status.addStyleName(AON.CSS.aonToolbarSmallTitle());
		tableActionTargetStatus.setWidget(0, 0, status);
		tableActionTargetStatus.getRowFormatter().getElement(0).getStyle().setProperty("border-bottom", "1px solid #ebebeb");
		tableActionTargetStatus.getRowFormatter().getElement(0).getStyle().setProperty("height", "25px");
		
		tableActionTargetStatus.setWidget(1, 0, new InlineLabel(""));
		tableActionTargetStatus.setWidget(1, 1, new InlineLabel(""));
		tableActionTargetStatus.getRowFormatter().getElement(1).getStyle().setProperty("height", "10px");
		
		tableActionTargetStatus.setWidget(2, 0, new InlineLabel("Pendiente"));
		tableActionTargetStatus.getCellFormatter().setStyleName(2, 0, AON.CSS.aonTableLabel());
		tableActionTargetStatus.setWidget(2,1,new Label(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)0).count() + ""));

		tableActionTargetStatus.setWidget(3, 0, new InlineLabel("Ausente"));
		tableActionTargetStatus.getCellFormatter().setStyleName(3, 0, AON.CSS.aonTableLabel());
		tableActionTargetStatus.setWidget(3,1,new Label(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)1).count() + ""));
		if(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)1).count() == 0)
			tableActionTargetStatus.getRowFormatter().getElement(3).getStyle().setDisplay(Display.NONE);
		
		tableActionTargetStatus.setWidget(4, 0, new InlineLabel("Incorrecto"));
		tableActionTargetStatus.getCellFormatter().setStyleName(4, 0, AON.CSS.aonTableLabel());
		tableActionTargetStatus.setWidget(4,1,new Label(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)2).count() + ""));
		if(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)2).count() == 0)
			tableActionTargetStatus.getRowFormatter().getElement(4).getStyle().setDisplay(Display.NONE);
		
		tableActionTargetStatus.setWidget(5, 0, new InlineLabel("Reintentar"));
		tableActionTargetStatus.getCellFormatter().setStyleName(5, 0, AON.CSS.aonTableLabel());
		tableActionTargetStatus.setWidget(5,1,new Label(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)3).count() + ""));
		if(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)3).count() == 0)
			tableActionTargetStatus.getRowFormatter().getElement(5).getStyle().setDisplay(Display.NONE);
		
		tableActionTargetStatus.setWidget(6, 0, new InlineLabel("Anular"));
		tableActionTargetStatus.getCellFormatter().setStyleName(6, 0, AON.CSS.aonTableLabel());
		tableActionTargetStatus.setWidget(6,1,new Label(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)4).count() + ""));
		if(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)4).count() == 0)
			tableActionTargetStatus.getRowFormatter().getElement(6).getStyle().setDisplay(Display.NONE);
		
		tableActionTargetStatus.setWidget(7, 0, new InlineLabel("Finalizado"));
		tableActionTargetStatus.getCellFormatter().setStyleName(7, 0, AON.CSS.aonTableLabel());
		tableActionTargetStatus.setWidget(7,1,new Label(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)5).count() + ""));
		if(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)5).count() == 0)
			tableActionTargetStatus.getRowFormatter().getElement(7).getStyle().setDisplay(Display.NONE);
		
		tableActionTargetStatus.setWidget(8, 0, new InlineLabel("Enviado"));
		tableActionTargetStatus.getCellFormatter().setStyleName(8, 0, AON.CSS.aonTableLabel());
		tableActionTargetStatus.setWidget(8,1,new Label(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)6).count() + ""));
		if(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)6).count() == 0)
			tableActionTargetStatus.getRowFormatter().getElement(8).getStyle().setDisplay(Display.NONE);
		
		tableActionTargetStatus.setWidget(9, 0, new InlineLabel(""));
		tableActionTargetStatus.setWidget(9, 1, new InlineLabel(""));
		tableActionTargetStatus.getRowFormatter().getElement(9).getStyle().setProperty("height", "10px");
		
		tablePanel.add( tableActionTargetStatus );
		
		rootPanel.add( tablePanel );
		
		container.add(rootPanel);
		
		add(container);
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	description.setFocus(true);
	        }
	    });		
	}

	private void checkSellerDistribution(AonTableButton button) {
		taskHolderLabel.setVisible(sellerDistribution.getSelectedIndex() == 0);
		taskHolder.setVisible(sellerDistribution.getSelectedIndex() == 0);
		button.setEnabled(sellerDistribution.getSelectedIndex() != 0);	
		
		marketingAction.setSellerDistribution(MarketingSellerDistribution.getSellerDistribution(Integer.parseInt(sellerDistribution.getSelectedValue())));
	}

	private void showMarketingActionMediaOptions(FlexTable table, MarketingActionMediaType marketingActionMediaType) {
		switch (marketingActionMediaType) {
			case PHONE:
				table.getRowFormatter().getElement(6).getStyle().clearDisplay();
				table.getRowFormatter().getElement(7).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(8).getStyle().clearDisplay();
				table.getRowFormatter().getElement(9).getStyle().setDisplay(Display.NONE);
				tagSuggestBox.getElement().getStyle().setDisplay(Display.NONE);
				break;
			case EMAIL:
				table.getRowFormatter().getElement(6).getStyle().clearDisplay();
				table.getRowFormatter().getElement(7).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(8).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(9).getStyle().setDisplay(Display.NONE);
				tagSuggestBox.getElement().getStyle().setDisplay(Display.NONE);
				break;
			case MAIL:
				table.getRowFormatter().getElement(6).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(7).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(8).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(9).getStyle().setDisplay(Display.NONE);
				tagSuggestBox.getElement().getStyle().setDisplay(Display.NONE);
				break;
			case BULLETIN:
				table.getRowFormatter().getElement(6).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(7).getStyle().clearDisplay();
				table.getRowFormatter().getElement(8).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(9).getStyle().setDisplay(Display.NONE);
				tagSuggestBox.getElement().getStyle().setDisplay(Display.NONE);
				break;
			default:
				table.getRowFormatter().getElement(6).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(7).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(8).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(9).getStyle().clearDisplay();
				tagSuggestBox.getElement().getStyle().clearDisplay();
				break;
		}
		
		actionType.setText(getActivonType(marketingActionMediaType.getValue()));
	}

	private String getActivonType(Integer type) {
		return type > 5 ? "Incoming" : "Outcoming";
	}
	
	private void createMarketingActionTargets() {
		targetDescription = new TextBox();
		targetDescription.setVisibleLength(50);
		targetDescription.setStyleName(AON.CSS.aonInputText());
		targetDescription.addValueChangeHandler(event -> onSearchActionTargets());
		
		targetStatus = new ListBox();
		targetStatus.addItem("Todos", "");
		targetStatus.addItem("Pendiente", "0");
		targetStatus.addItem("Ausente", "1");
		targetStatus.addItem("Incorrecto", "2");
		targetStatus.addItem("Reintentar", "3");
		targetStatus.addItem("Anular", "4");
		targetStatus.addItem("Finalizado", "5");
		targetStatus.addItem("Enviado", "6");
		targetStatus.addChangeHandler(e -> onSearchActionTargets());
		
		searchPanel = new FlowPanel();
		searchPanel.setStyleName(AON.CSS.aonSearchPanel());
		searchPanel.addStyleName(AON.CSS.aonFlexBetween());
		searchPanel.getElement().getStyle().setProperty("margin", "0 1rem");
		
		filterPanel = new FlowPanel();
		filterPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label descriptionLabel = new Label("Cliente");
		descriptionLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		filterPanel.add(descriptionLabel);
		filterPanel.add(targetDescription);
		
		Label statusLabel = new Label("Estado");
		statusLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		filterPanel.add(statusLabel);
		filterPanel.add(targetStatus);
		
		searchPanel.add(filterPanel);
		
		AonSearchPanelButton cleanButton = new AonSearchPanelButton( AON.MSG.clean(), AON.CSS.aonIconClear() );
		cleanButton.addClickHandler(event -> {
			targetDescription.setValue(null,false);
			targetStatus.setSelectedIndex(0);
			
			marketingActionPanel.resetSearchOffset();
			
			onSearchActionTargets();
		});

		AonSearchPanelButton refreshButton = new AonSearchPanelButton( AON.MSG.refresh(), AON.CSS.aonIconRefresh() );
		refreshButton.addStyleName(AON.CSS.aonMarginLeft());
		refreshButton.addClickHandler(event -> onSearchActionTargets());

		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.add( cleanButton );
		buttonsPanel.add( refreshButton );
		searchPanel.add(buttonsPanel);
		
		centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight((Window.getClientHeight() - 240) + "px");
		
		container.add(searchPanel);
		container.add(centerPanel);
		
		onSearchActionTargets();
	}
	
	public void onSearchActionTargets() {
		MarketingActionTargetParams params = getWidgetParams( options );
		
		getTaskHolderSeller(seller -> {
			
			marketingActionPanel = new MarketingActionTargetPanel(params, seller, marketingAction.getWorkgroup()) {

				@Override
				protected void onShowErrorMessage(String errorMessage) {
					AonMessagePanel.showError(messagePanel, errorMessage);
				}

				@Override
				protected void reloadMarketingAction() {
					setMarketingAction(marketingAction);
				}

				@Override
				protected void onShowLoadingMessage(String loadingMessage) {
					AonMessagePanel.showLoading(messagePanel, loadingMessage);
				}

				@Override
				protected void onShowSuccessMessage(String successMessage) {
					AonMessagePanel.showSuccess(messagePanel, successMessage);
				}
			
			};
			
			centerPanel.setWidget(marketingActionPanel);
			
		});
	}

	public MarketingActionTargetParams getWidgetParams( MarketingModuleOptions options) {
		return new MarketingActionTargetParams()
			.setDomainName(options.getDomainName())
			.setDomain(options.getDomain())
			.setUser(options.getUser())
			.setDescription(targetDescription.getValue())
			.setMarketingAction(this.marketingAction)
			.setStatus(targetStatus.getSelectedIndex() == 0 ? null : Byte.parseByte(targetStatus.getSelectedValue()))
			;
	}
	
	private void addInputStyle(Element el) {
		el.getStyle().setProperty("width", "-moz-available");
		el.getStyle().setProperty("width", "-webkit-fill-available");
		el.getStyle().setProperty("height", "1.1rem");
	}
	
	private void addSelectStyle(Element el) {
		el.getStyle().setProperty("width", "-moz-available");
		el.getStyle().setProperty("width", "-webkit-fill-available");
		el.getStyle().setProperty("height", "1.3rem");
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
	
	// Project Comercial
	private void getTaskHolderSeller(Consumer<Seller> finish) {
		if(AonStringUtils.isBlank(taskHolder.getSelectedValue()))
			finish.accept(null);
		else {
			commonService.getSellerByTaskHolder(options.getDomainName(), options.getDomain(), options.getUser(), Integer.parseInt(taskHolder.getSelectedValue()), new AsyncCallback<Seller>() {
				
				@Override
				public void onSuccess(Seller seller) {
					finish.accept(seller);
				}

				@Override
				public void onFailure(Throwable caught) {
					finish.accept(null);
				}
				
			});
			
		}
	}
	
	private void createProjectCommercial() {
		if(sellerDistribution.getSelectedIndex() == 1) {
			AonDialog dialog = new AonDialog("Asignaci\u00f3n autom\u00e1tica agente comercial",
					new HTML("Se va a proceder a buscar el siguiente agente comercial disponible de forma  autom\u00e1tica para la acci\u00f3n <b>" + marketingAction.getDescription() + "</b>.<br>\u00bfEsta seguro que desea proceder con la asignaci\u00f3n autom\u00e1tica\u003f."));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {}

				@Override
				public void onAccept() {
					createProjectCommercialAutomatic();
				}
			});
		} else if(AonStringUtils.isNotBlank(taskHolder.getSelectedValue())) {
			commonService.getSellerByTaskHolder(options.getDomainName(), options.getDomain(), options.getUser(), Integer.parseInt(taskHolder.getSelectedValue()), new AsyncCallback<Seller>() {
				
				@Override
				public void onSuccess(Seller seller) {
					if(seller == null)
						AonMessagePanel.showError(messagePanel, "El task holder seleccionado no tiene un comercial asociado. Pruebe con otro task holder");
					else
						createProjectCommercial(seller);
				}

				@Override
				public void onFailure(Throwable caught) {
					AonMessagePanel.showError(messagePanel, "Error getSellerByTaskHolder(): " + caught.getMessage());
				}
				
			});
			
		}
	}
	
	private void createProjectCommercial(Seller seller) {
		AonMessagePanel.showLoading(messagePanel, "Creando operaciones comerciales para cada lead de la acción " + marketingAction.getDescription() + " ...");
		projectCommercialIndx = 0;
		createProjectCommercial(seller, projectCommercialIndx);
		
	}
	
	private void createProjectCommercialAutomatic() {
		AonMessagePanel.showLoading(messagePanel, "Creando operaciones comerciales para cada lead de la acción " + marketingAction.getDescription() + " ...");
		projectCommercialIndx = 0;
		createProjectCommercial(projectCommercialIndx);
		
	}
	
	private void createProjectCommercial(Integer indx) {
		MarketingActionTarget target = marketingAction.getTargets().get(indx);
		
		if(target.getActionTargetStatus() == (byte)0) { // Pendiente
			
			AonMessagePanel.showLoading(messagePanel, "Creando operaci\u00f3n comercial para el lead '" + target.getName() + "' ...");
			
			commonService.getNextLinealSellerByWorkgroup(options.getDomainName(), options.getDomain(), options.getUser(), Integer.parseInt(workgroup.getSelectedValue()), new AsyncCallback<Seller>() {
				
				@Override
				public void onSuccess(Seller seller) {
					ProjectCommercial projectCommercial = new ProjectCommercial()
							.copy(new Project()
								.setDomain(target.getDomain())
								.setRegistry(target.get())
								.setName(marketingAction.getDescription())
								.setDate(new Date())
								.setTas(false)
								.setCommercial(true)
								.setReservation(false)
								.setActive(true)
							)
							.setTarget(target.getId())
							.setSeller(seller.getId())
							.setComments(target.getComments())
							.setSource((byte)8) // Marketing
							.setStatus((byte)0)
							.setStatusDate(new Date())
							.setProbability(0);
					
					commonService.saveProjectCommercial(options.getDomainName(), options.getDomain(), options.getUser(), projectCommercial, new AsyncCallback<ProjectCommercial>() {
						
						@Override
						public void onSuccess(ProjectCommercial projectCommercial) {
							target.setActionTargetStatus((byte)6); // Enviado
							commonService.saveMarketingActionTarget(options.getDomainName(), options.getDomain(), options.getUser(), target, new AsyncCallback<MarketingActionTarget>() {
								
								@Override
								public void onSuccess(MarketingActionTarget marketingActionTarget) {
									if(indx == (marketingAction.getTargets().size() - 1)) {
										AonMessagePanel.showSuccess(messagePanel, "Operaciones comerciales creadas correctamente");
										marketingActionPanel.resetSearchOffset();
										setMarketingAction(marketingAction);
									} else {
										createProjectCommercial(projectCommercialIndx++);
									}
								}
								
								@Override
								public void onFailure(Throwable caught) {
									AonMessagePanel.showError(messagePanel, "Error saveMarketingActionTarget(): " + caught.getMessage());
								}
							});
						}
			
						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error saveProjectCommercial(): " + caught.getMessage());
						}
						
					});
				}

				@Override
				public void onFailure(Throwable caught) {
					AonMessagePanel.showError(messagePanel, "Error getNextLinealSellerByWorkgroup(): " + caught.getMessage());
				}
				
			});
		
		} else {
			if(indx == (marketingAction.getTargets().size() - 1)) {
				AonMessagePanel.showSuccess(messagePanel, "Operaciones comerciales creadas correctamente");
				marketingActionPanel.resetSearchOffset();
				setMarketingAction(marketingAction);
			} else createProjectCommercial(projectCommercialIndx++);
		}
		
	}
	
	private void createProjectCommercial(Seller seller, Integer indx) {
		MarketingActionTarget target = marketingAction.getTargets().get(indx);
		
		if(target.getActionTargetStatus() == (byte)0) { // Pendiente
		
			AonMessagePanel.showLoading(messagePanel, "Creando operaci\u00f3n comercial para el lead '" + target.getName() + "' ...");
			
			ProjectCommercial projectCommercial = new ProjectCommercial()
					.copy(new Project()
						.setDomain(target.getDomain())
						.setRegistry(target.get())
						.setName(marketingAction.getDescription())
						.setDate(new Date())
						.setTas(false)
						.setCommercial(true)
						.setReservation(false)
						.setActive(true)
					)
					.setTarget(target.getId())
					.setSeller(seller.getId())
					.setComments(target.getComments())
					.setSource((byte)8) // Marketing
					.setStatus((byte)0)
					.setStatusDate(new Date())
					.setProbability(0);
			
			commonService.saveProjectCommercial(options.getDomainName(), options.getDomain(), options.getUser(), projectCommercial, new AsyncCallback<ProjectCommercial>() {
				
				@Override
				public void onSuccess(ProjectCommercial projectCommercial) {
					target.setActionTargetStatus((byte)6); // Enviado
					commonService.saveMarketingActionTarget(options.getDomainName(), options.getDomain(), options.getUser(), target, new AsyncCallback<MarketingActionTarget>() {
						
						@Override
						public void onSuccess(MarketingActionTarget marketingActionTarget) {
							if(indx == (marketingAction.getTargets().size() - 1)) {
								AonMessagePanel.showSuccess(messagePanel, "Operaciones comerciales creadas correctamente");
								marketingActionPanel.resetSearchOffset();
								setMarketingAction(marketingAction);
							} else {
								createProjectCommercial(seller, projectCommercialIndx++);
							}
						}
						
						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error saveMarketingActionTarget(): " + caught.getMessage());
						}
					});
				}
	
				@Override
				public void onFailure(Throwable caught) {
					AonMessagePanel.showError(messagePanel, "Error saveProjectCommercial(): " + caught.getMessage());
				}
				
			} );
		
		} else {
			if(indx == (marketingAction.getTargets().size() - 1)) {
				AonMessagePanel.showSuccess(messagePanel, "Operaciones comerciales creadas correctamente");
				marketingActionPanel.resetSearchOffset();
				setMarketingAction(marketingAction);
			} else createProjectCommercial(seller, projectCommercialIndx++);
		}
		
	}
	
	private void getTagSuggestion(Consumer<Void> success) {
		commonService.getTagSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), TagType.MARKETING, new AsyncCallback<List<Tag>>() {
			
			@Override
			public void onSuccess(List<Tag> tagSuggestion) {
				tags = tagSuggestion;
				
				List<String> suggestions = new ArrayList<String>();
				tags.forEach(tag -> suggestions.add("[" + tag.getId() + "] " + tag.getName()));
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) tagSuggestBox.getSuggestOracle();
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
	
	private void getNewsSuggestion(Consumer<Void> success) {
		commonService.getNewsSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<News>>() {
			
			@Override
			public void onSuccess(List<News> newsSuggestion) {
				news = newsSuggestion;
				
				List<String> suggestions = new ArrayList<String>();
				news.forEach(newIt -> suggestions.add("[" + newIt.getId() + "] " + newIt.getTitle()));
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) newsSuggestBox.getSuggestOracle();
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
	
	private void getNewsletterSuggestion(Consumer<Void> success) {
		commonService.getNewsletterSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<Newsletter>>() {
			
			@Override
			public void onSuccess(List<Newsletter> newsletterSuggestion) {
				newsletter = newsletterSuggestion;
				
				List<String> suggestions = new ArrayList<String>();
				newsletter.forEach(newsletter -> suggestions.add("[" + newsletter.getId() + "] " + newsletter.getName()));
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) newsletterSuggestBox.getSuggestOracle();
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
	
	private void getSurveySuggestion(Consumer<Void> success) {
		commonService.getSurveySuggestion(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<Survey>>() {
			
			@Override
			public void onSuccess(List<Survey> surveySuggestion) {
				surveis = surveySuggestion;
				
				List<String> suggestions = new ArrayList<String>();
				surveis.forEach(survey -> suggestions.add("[" + survey.getId() + "] " + survey.getDescription()));
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) surveySuggestBox.getSuggestOracle();
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
	
	private void getAviableWorkgroups(Consumer<List<Workgroup>> success) {
		commonService.getAviableWorkgroups(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<Workgroup>>() {
			
			@Override
			public void onSuccess(List<Workgroup> workgroups) {
				success.accept(workgroups);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo workgroups: " + caught.getMessage());
			}
		});
	}
	
	private void getAviableTaskHolders(Integer workgroup, Consumer<List<TaskHolder>> success) {
		commonService.getAviableTaskHolders(options.getDomainName(), options.getDomain(), options.getUser(), workgroup, new AsyncCallback<List<TaskHolder>>() {
			
			@Override
			public void onSuccess(List<TaskHolder> taskHolders) {
				success.accept(taskHolders);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo workgroups: " + caught.getMessage());
			}
		});
	}
	
	protected abstract void onActionBackClick();
	protected abstract void onActionDeleteClick(MarketingAction marketingAction);

}
