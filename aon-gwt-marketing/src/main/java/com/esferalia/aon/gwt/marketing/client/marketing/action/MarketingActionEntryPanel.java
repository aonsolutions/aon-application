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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomNumberBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomSuggestBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMarketingActionTargetCreationPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMarketingActionTargetCreationPanel.AonMarketingActionTargetCreationPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMarketingActionTargetPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMarketingActionTargetPanel.AonMarketingActionTargetPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
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
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class MarketingActionEntryPanel extends AonCustomDockLayout{
	
	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}

	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");	
	
	// MarketingCampaign Info
	private AonCustomTextBox description = new AonCustomTextBox("Descripci\u00f3n");
	private AonCustomTextBox actionType = new AonCustomTextBox("Tipo");
	private AonCustomListBox typeListBox = new AonCustomListBox("Canal");
	private AonCustomSuggestBox tagSuggestBox = new AonCustomSuggestBox("Etiquetas");
	private List<Tag> tags = new ArrayList<>();
	private AonCustomNumberBox budget = new AonCustomNumberBox("Presupuesto");
	private AonCustomNumberBox expense = new AonCustomNumberBox("Gastos");
	private AonCustomDateBox startDate = new AonCustomDateBox("F. Inicio");
	private AonCustomDateBox endDate = new AonCustomDateBox("F. Fin");
	
	private AonCustomListBox workgroup = new AonCustomListBox("G. Trabajo");
	private AonCustomListBox sellerDistribution = new AonCustomListBox("T. Asignaci\u00f3n");
	private AonCustomListBox taskHolder = new AonCustomListBox("Asignado a");
	
	private AonCustomSuggestBox newsSuggestBox = new AonCustomSuggestBox("Noticia");
	private List<News> news = new ArrayList<>();
	
	private AonCustomSuggestBox surveySuggestBox = new AonCustomSuggestBox("Cuestionario");
	private List<Survey> surveis = new ArrayList<>();
	
	private AonCustomSuggestBox newsletterSuggestBox = new AonCustomSuggestBox("Boletin");
	private List<Newsletter> newsletter = new ArrayList<>();
	
	private AonCustomTextBox webForm = new AonCustomTextBox("Formulario Web");
	
	private Integer projectCommercialIndx = 0;
	
	// MarketingAction Search
	private AonCustomListBox targetStatus = new AonCustomListBox("Estado");
	
	private AonCustomListBox sort = new AonCustomListBox("Ordenar Por");
	private AonCustomListBox asc = new AonCustomListBox("Orden");
	
	private SimpleLayoutPanel centerPanel;
	
	private MarketingActionTargetPanel marketingActionPanel;
	
	private MarketingModuleOptions options;
	private MarketingAction marketingAction;
	
	public MarketingActionEntryPanel(MarketingModuleOptions options) {
		super("ACCI\u00f3N");
		
		this.options = options;
		initializeCommonService();
		
		addButtonsToolbar();
		
		hideToolbarFilterMessages();
		setSearchPlaceholder("Busque por descripci\u00f3n...");
		addKeyUpHandler(e -> {
			String value = getSearchTextBox().getValue();
			if(AonStringUtils.isNotBlank(value) && value.length() > 3) {
				onSearchActionTargets();
			} else if(AonStringUtils.isBlank(value)) {
				onSearchActionTargets();
			}
		});
	}
	
	private void addButtonsToolbar() {
		AonToolbarButton backButton = new AonToolbarButton("Volver a la campa\u00f1a", AON.CSS.aonIconBack());
		backButton.addClickHandler(e -> onActionBackClick());
		addToolbarButton(backButton);
		
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
		addToolbarButton(deleteButton);
		
		AonToolbarButton saveButton = new AonToolbarButton("Guardar acci\u00f3n", AON.CSS.aonIconSave());
		saveButton.addClickHandler(e -> saveMarketingAction());
		addToolbarButton(saveButton);
		
		AonToolbarButton newActionTargetButton = new AonToolbarButton("A\u00f1adir cliente potencial", AON.CSS.aonIconAdd());
		newActionTargetButton.addClickHandler(e -> showMarketingActionTargetDialog());
		addToolbarButton(newActionTargetButton);
	}

	private void importActionTarget() {
		if(AonStringUtils.isBlank(taskHolder.getValue()))
			importActionTarget(null);
		else {
			commonService.getSellerByTaskHolder(options.getDomainName(), options.getDomain(), options.getUser(), Integer.parseInt(taskHolder.getValue()), new AsyncCallback<Seller>() {
				
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
		marketingAction.setMediaType(MarketingActionMediaType.getMediaType(Integer.parseInt(typeListBox.getValue())));
		marketingAction.setStartDate(startDate.getValue());
		marketingAction.setEndDate(endDate.getValue());
		marketingAction.setBudget(budget.getValue());
		marketingAction.setExpense(expense.getValue());
		marketingAction.setWorkgroup(AonStringUtils.isBlank(workgroup.getValue()) ? null : new Workgroup().setId(Integer.parseInt(workgroup.getValue())));
		marketingAction.setSellerDistribution(MarketingSellerDistribution.getSellerDistribution(Integer.parseInt(sellerDistribution.getValue())));
		if(marketingAction.getSellerDistribution() == MarketingSellerDistribution.MANUAL)
			marketingAction.setTaskHolder(AonStringUtils.isBlank(taskHolder.getValue()) ? null : new TaskHolder().setRegistry(Integer.parseInt(taskHolder.getValue())));
		else
			marketingAction.setTaskHolder(null);
		
		switch (MarketingActionMediaType.getMediaType(Integer.parseInt(typeListBox.getValue()))) {
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
		
		if(null != container) remove(container);
		
		setToolbarTitle("Acci\u00f3n " + marketingAction.getDescription());
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(messagePanel);
		
		Widget otherDataContent = createOtherDataCardContent();
		
		// Cards Panel
		HTMLPanel cardsPanel = new HTMLPanel("");
		cardsPanel.setStyleName(AON.CSS.aonItemFlex());
		cardsPanel.getElement().getStyle().setProperty("align-items", "flex-start");
		cardsPanel.getElement().getStyle().setProperty("padding", "0 1rem");
		
		// Info Card
		AonCustomCard infoCard = new AonCustomCard("Informaci\u00f3n");
		infoCard.getElement().getStyle().setProperty("min-height", "16.5rem");
		infoCard.getElement().getStyle().setProperty("width", "100%");
		infoCard.add(createInfoCardContent(otherDataContent));
		cardsPanel.add(infoCard);
		
		// Expenses Card
		AonCustomCard accountingCard = new AonCustomCard("Otros datos");
		accountingCard.getElement().getStyle().setProperty("min-height", "16.5rem");
		accountingCard.getElement().getStyle().setProperty("width", "100%");
		accountingCard.add(otherDataContent);
		cardsPanel.add(accountingCard);
		
		// Status Card
		AonCustomCard statusCard = new AonCustomCard("Estados");
		statusCard.getElement().getStyle().setProperty("min-height", "16.5rem");
		statusCard.getElement().getStyle().setProperty("width", "7rem");
		statusCard.add(createStatusCardContent());
		cardsPanel.add(statusCard);
		
		container.add(cardsPanel);
		
		// Targets Card
		HTMLPanel actionsPanel = new HTMLPanel("");
		actionsPanel.setStyleName(AON.CSS.aonItemFlex());
		actionsPanel.getElement().getStyle().setProperty("align-items", "flex-start");
		actionsPanel.getElement().getStyle().setProperty("padding", "0 1rem");
		
		AonCustomCard actionsCard = new AonCustomCard("Clientes Potenciales");
		actionsCard.getElement().getStyle().setProperty("min-height", "13rem");
		actionsCard.getElement().getStyle().setProperty("width", "100%");
		
		centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight((Window.getClientHeight() - 560) + "px");
		actionsCard.add(centerPanel);
		
		actionsPanel.add(actionsCard);
		
		container.add(actionsPanel);
		
		add(container);
		
		createMarketingActionTargets();
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	description.setFocus(true);
	        }
	    });	
	}

	private Widget createInfoCardContent(Widget otherDataContent) {
		HTMLPanel content = new HTMLPanel("");
		content.addStyleName(AON.CSS.aonFlexColumn());
		
		HTMLPanel row1 = new HTMLPanel("");
		row1.setStyleName(AON.CSS.aonItemFlex());
		
		AonCustomTextBox campaing = new AonCustomTextBox("Campa\u00f1a");
		campaing.setValue(marketingAction.getMarketingCampaign().getDescription());
		campaing.setEnable(false);
		
		row1.add(campaing);
		content.add(row1);
		
		HTMLPanel row2 = new HTMLPanel("");
		row2.setStyleName(AON.CSS.aonItemFlex());
		
		description.getTextBox().setMaxLength(64);
		description.setValue(marketingAction.getDescription());
		
		row2.add(description);
		content.add(row2);
		
		HTMLPanel row3 = new HTMLPanel("");
		row3.setStyleName(AON.CSS.aonItemFlex());
		
		actionType.setEnable(false);
		actionType.setValue(getActivonType(marketingAction.getMediaType().getValue()));
		
		typeListBox.clearItems();
		for(int i=0; i<MarketingActionMediaType.values().length; i++) {
			MarketingActionMediaType marketingActionMediaType = MarketingActionMediaType.values()[i];
			typeListBox.addItem(marketingActionMediaType.getDescription(), marketingActionMediaType.getValue().toString());
		}
		typeListBox.setValue(marketingAction.getMediaType().getValue().toString());
		typeListBox.addChangeHandler(e -> {
			MarketingActionMediaType marketingActionMediaType = MarketingActionMediaType.getMediaType(Integer.parseInt(typeListBox.getValue()));
			showMarketingActionMediaOptions(otherDataContent, marketingActionMediaType);
		});
		
		tagSuggestBox.setAutoSelectEnabled(false);
		tagSuggestBox.getSuggestBox().getElement().setPropertyString("placeholder", "Ctrl + espacio para ver sugerencias");
		tagSuggestBox.getSuggestBox().addKeyUpHandler(e -> {
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
		
		row3.add(actionType);
		row3.add(typeListBox);
		row3.add(tagSuggestBox);
		content.add(row3);
		
		HTMLPanel row4 = new HTMLPanel("");
		row4.setStyleName(AON.CSS.aonItemFlex());
		
		startDate.setValue(marketingAction.getStartDate());
		endDate.setValue(marketingAction.getEndDate());
		
		row4.add(startDate);
		row4.add(endDate);
		content.add(row4);
		
		showMarketingActionMediaOptions(otherDataContent, marketingAction.getMediaType());
		
		return content;
	}

	private Widget createOtherDataCardContent() {
		HTMLPanel content = new HTMLPanel("");
		content.addStyleName(AON.CSS.aonFlexColumn());
		
		HTMLPanel row0 = new HTMLPanel("");
		row0.setStyleName(AON.CSS.aonItemFlex());
		
		budget.hideNearBy();
		budget.setValue(marketingAction.getBudget());
		expense.hideNearBy();
		expense.setValue(marketingAction.getExpense());
		
		row0.add(budget);
		row0.add(expense);
		content.add(row0);
		
		HTMLPanel row1 = new HTMLPanel("");
		row1.setStyleName(AON.CSS.aonItemFlex());
		
		// Accion Comercial
		HTMLPanel sellerDistributionPanel = new HTMLPanel("");
		sellerDistributionPanel.addStyleName(AON.CSS.aonItemFlex());
		
		AonTableButton projectCommercialBtn = new AonTableButton("Crear operaci\u00f3n comercial", AON.CSS.aonIconWorkAdd());
		projectCommercialBtn.addClickHandler(e -> createProjectCommercial());
		
		workgroup.clearItems();
		workgroup.addItem("-", "");
		workgroup.addChangeHandler(e -> {
			if(AonStringUtils.isBlank(workgroup.getValue())) {
				taskHolder.getListBox().setSelectedIndex(0);
				sellerDistributionPanel.setVisible(false);
				taskHolder.setVisible(false);
			} else {
				sellerDistributionPanel.setVisible(true);
				taskHolder.setVisible(true);	
				getAviableTaskHolders(Integer.parseInt(workgroup.getValue()), taskHolders -> { 
					taskHolder.clearItems();
					taskHolder.addItem("-", "");
					taskHolders.forEach(taskHolderIt -> taskHolder.addItem(taskHolderIt.getName(), taskHolderIt.getRegistry().toString()));
				});
			}
			
			marketingAction.setWorkgroup(AonStringUtils.isBlank(workgroup.getValue()) ? null : new Workgroup().setId(Integer.parseInt(workgroup.getValue())));
		});
		
		taskHolder.clearItems();
		taskHolder.addItem("-", "");
		taskHolder.addChangeHandler(e -> projectCommercialBtn.setEnabled(AonStringUtils.isNotBlank(taskHolder.getValue())));
		
		getAviableWorkgroups(workgroups -> {
			workgroups.forEach(workgroupIt -> workgroup.addItem(workgroupIt.getDescription(), workgroupIt.getId().toString()));
			workgroup.setValue(null != marketingAction.getWorkgroup() ? marketingAction.getWorkgroup().getId().toString() : null);
			
			if(null != marketingAction.getWorkgroup()) {	
				getAviableTaskHolders(marketingAction.getWorkgroup().getId(), taskHolders -> {
					taskHolder.clearItems();
					taskHolder.addItem("-", ""); 
					taskHolders.forEach(taskHolderIt -> taskHolder.addItem(taskHolderIt.getName(), taskHolderIt.getRegistry().toString()));
					taskHolder.setValue(null != marketingAction.getTaskHolder() ? marketingAction.getTaskHolder().getRegistry().toString() : null);
					
					if(null != marketingAction.getTaskHolder() && marketingAction.getSellerDistribution() == MarketingSellerDistribution.MANUAL) projectCommercialBtn.setEnabled(true);
					
					createMarketingActionTargets();
				});
			} else {
				sellerDistributionPanel.setVisible(false);
				taskHolder.setVisible(false);
				createMarketingActionTargets();
			}	
		});
		
		sellerDistribution.clearItems();
		for(int i=0; i < MarketingSellerDistribution.values().length; i++)
			sellerDistribution.addItem(MarketingSellerDistribution.values()[i].getDescription(), MarketingSellerDistribution.values()[i].getValue().toString());
		sellerDistribution.setValue(marketingAction.getSellerDistribution().getValue().toString());
		sellerDistribution.addChangeHandler(e -> checkSellerDistribution(projectCommercialBtn));
		checkSellerDistribution(projectCommercialBtn);
		
		sellerDistributionPanel.add(sellerDistribution);
		sellerDistributionPanel.add(taskHolder);
		sellerDistributionPanel.add(projectCommercialBtn);
		
		row1.add(workgroup);
		row1.add(sellerDistributionPanel);
		content.add(row1);
		
		HTMLPanel row2 = new HTMLPanel("");
		row2.setStyleName(AON.CSS.aonItemFlex());
		
		newsSuggestBox.setAutoSelectEnabled(false);
		newsSuggestBox.getSuggestBox().getElement().setPropertyString("placeholder", "Ctrl + espacio para ver sugerencias");
		newsSuggestBox.getSuggestBox().addKeyUpHandler(e -> {
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
		
		row2.add(newsSuggestBox);
		content.add(row2);
		
		HTMLPanel row3 = new HTMLPanel("");
		row3.setStyleName(AON.CSS.aonItemFlex());
		
		newsletterSuggestBox.setAutoSelectEnabled(false);
		newsletterSuggestBox.getSuggestBox().getElement().setPropertyString("placeholder", "Ctrl + espacio para ver sugerencias");
		newsletterSuggestBox.getSuggestBox().addKeyUpHandler(e -> {
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
		
		row3.add(newsletterSuggestBox);
		content.add(row3);
		
		HTMLPanel row4 = new HTMLPanel("");
		row4.setStyleName(AON.CSS.aonItemFlex());
		
		surveySuggestBox.setAutoSelectEnabled(false);
		surveySuggestBox.getSuggestBox().getElement().setPropertyString("placeholder", "Ctrl + espacio para ver sugerencias");
		surveySuggestBox.getSuggestBox().addKeyUpHandler(e -> {
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
		
		row4.add(surveySuggestBox);
		content.add(row4);
		
		HTMLPanel row5 = new HTMLPanel("");
		row5.setStyleName(AON.CSS.aonItemFlex());
		row5.getElement().getStyle().setProperty("cursor", "pointer");
		
		webForm.setValue("Ejemplo del formulario web (API)");
		
		row5.add(webForm);
		row5.addDomHandler(e -> importActionTarget(), ClickEvent.getType());
		
		content.add(row5);
		
		return content;
	}

	private Widget createStatusCardContent() {
		HTMLPanel content = new HTMLPanel("");
		content.addStyleName(AON.CSS.aonFlexColumn());
		
		HTMLPanel row1 = new HTMLPanel("");
		row1.setStyleName(AON.CSS.aonItemFlex());
		
		AonCustomTextBox pendiente = new AonCustomTextBox("Pendiente");
		pendiente.setEnable(false);
		pendiente.setValue(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)0).count() + "");
		
		row1.add(pendiente);
		content.add(row1);
		
		if(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)1).count() != 0) {
			HTMLPanel row = new HTMLPanel("");
			row.setStyleName(AON.CSS.aonItemFlex());
			
			AonCustomTextBox ausente = new AonCustomTextBox("Ausente");
			ausente.setEnable(false);
			ausente.setValue(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)1).count() + "");
			
			row.add(ausente);
			content.add(row);
		}
		
		if(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)2).count() != 0) {
			HTMLPanel row = new HTMLPanel("");
			row.setStyleName(AON.CSS.aonItemFlex());
			
			AonCustomTextBox incorrecto = new AonCustomTextBox("Incorrecto");
			incorrecto.setEnable(false);
			incorrecto.setValue(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)2).count() + "");
			
			row.add(incorrecto);
			content.add(row);
		}
		
		if(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)3).count() != 0) {
			HTMLPanel row = new HTMLPanel("");
			row.setStyleName(AON.CSS.aonItemFlex());
			
			AonCustomTextBox reintentar = new AonCustomTextBox("Reintentar");
			reintentar.setEnable(false);
			reintentar.setValue(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)3).count() + "");
			
			row.add(reintentar);
			content.add(row);
		}
		
		if(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)4).count() != 0) {
			HTMLPanel row = new HTMLPanel("");
			row.setStyleName(AON.CSS.aonItemFlex());
			
			AonCustomTextBox anular = new AonCustomTextBox("Anular");
			anular.setEnable(false);
			anular.setValue(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)4).count() + "");
			
			row.add(anular);
			content.add(row);
		}
		
		if(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)5).count() != 0) {
			HTMLPanel row = new HTMLPanel("");
			row.setStyleName(AON.CSS.aonItemFlex());
			
			AonCustomTextBox finalizado = new AonCustomTextBox("Finalizado");
			finalizado.setEnable(false);
			finalizado.setValue(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)5).count() + "");
			
			row.add(finalizado);
			content.add(row);
		}
		
		if(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)6).count() != 0) {
			HTMLPanel row = new HTMLPanel("");
			row.setStyleName(AON.CSS.aonItemFlex());
			
			AonCustomTextBox enviado = new AonCustomTextBox("Enviado");
			enviado.setEnable(false);
			enviado.setValue(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)6).count() + "");
			
			row.add(enviado);
			content.add(row);
		}
			
		return content;
	}

	private void checkSellerDistribution(AonTableButton button) {
		taskHolder.setVisible(sellerDistribution.getListBox().getSelectedIndex() == 0);
		button.setEnabled(sellerDistribution.getListBox().getSelectedIndex() != 0);	
		
		marketingAction.setSellerDistribution(MarketingSellerDistribution.getSellerDistribution(Integer.parseInt(sellerDistribution.getValue())));
	}

	private void showMarketingActionMediaOptions(Widget contentWidget, MarketingActionMediaType marketingActionMediaType) {
		HTMLPanel content = (HTMLPanel) contentWidget;
		
		switch (marketingActionMediaType) {
			case PHONE:
				actionType.setWidth("100%");
				typeListBox.setWidth("100%");
				
				content.getWidget(2).getElement().getStyle().clearDisplay();
				content.getWidget(3).getElement().getStyle().setDisplay(Display.NONE);
				content.getWidget(4).getElement().getStyle().clearDisplay();
				content.getWidget(5).getElement().getStyle().setDisplay(Display.NONE);
				tagSuggestBox.getElement().getStyle().setDisplay(Display.NONE);
				break;
			case EMAIL:
				actionType.setWidth("100%");
				typeListBox.setWidth("100%");
				
				content.getWidget(2).getElement().getStyle().clearDisplay();
				content.getWidget(3).getElement().getStyle().setDisplay(Display.NONE);
				content.getWidget(4).getElement().getStyle().setDisplay(Display.NONE);
				content.getWidget(5).getElement().getStyle().setDisplay(Display.NONE);
				tagSuggestBox.getElement().getStyle().setDisplay(Display.NONE);
				break;
			case MAIL:
				actionType.setWidth("100%");
				typeListBox.setWidth("100%");
				
				content.getWidget(2).getElement().getStyle().setDisplay(Display.NONE);
				content.getWidget(3).getElement().getStyle().setDisplay(Display.NONE);
				content.getWidget(4).getElement().getStyle().setDisplay(Display.NONE);
				content.getWidget(5).getElement().getStyle().setDisplay(Display.NONE);
				tagSuggestBox.getElement().getStyle().setDisplay(Display.NONE);
				break;
			case BULLETIN:
				actionType.setWidth("100%");
				typeListBox.setWidth("100%");
				
				content.getWidget(2).getElement().getStyle().setDisplay(Display.NONE);
				content.getWidget(3).getElement().getStyle().clearDisplay();
				content.getWidget(4).getElement().getStyle().setDisplay(Display.NONE);
				content.getWidget(5).getElement().getStyle().setDisplay(Display.NONE);
				tagSuggestBox.getElement().getStyle().setDisplay(Display.NONE);
				break;
			default:
				actionType.setWidth("5rem");
				typeListBox.setWidth("6rem");
				
				content.getWidget(2).getElement().getStyle().setDisplay(Display.NONE);
				content.getWidget(3).getElement().getStyle().setDisplay(Display.NONE);
				content.getWidget(4).getElement().getStyle().setDisplay(Display.NONE);
				content.getWidget(5).getElement().getStyle().clearDisplay();
				tagSuggestBox.getElement().getStyle().clearDisplay();
				break;
		}
		
		actionType.setValue(getActivonType(marketingActionMediaType.getValue()));
	}

	private String getActivonType(Integer type) {
		return type > 5 ? "Incoming" : "Outcoming";
	}
	
	private void createMarketingActionTargets() {
		targetStatus.clearItems();
		targetStatus.addItem("Todos", "");
		targetStatus.addItem("Pendiente", "0");
		targetStatus.addItem("Ausente", "1");
		targetStatus.addItem("Incorrecto", "2");
		targetStatus.addItem("Reintentar", "3");
		targetStatus.addItem("Anular", "4");
		targetStatus.addItem("Finalizado", "5");
		targetStatus.addItem("Enviado", "6");
		targetStatus.addChangeHandler(e -> onSearchActionTargets());
		
		addFilterWidget(targetStatus);
		
		sort.clearItems();
		sort.addItem("Cliente Potencial", "name");
		sort.addItem("Estado", "status");
		sort.getListBox().addChangeHandler(event -> onSearchActionTargets());
		
		asc.clearItems();
		asc.addItem("Ascendente", "true");
		asc.addItem("Descendete", "false");
		asc.getListBox().addChangeHandler(event -> onSearchActionTargets());
		
		addSortWidget(sort);
		addSortWidget(asc);
		
		onSearchActionTargets();
	}
	
	@Override
	protected void onClearFilter() {
		getSearchTextBox().setValue(null);
		targetStatus.setValue("");
		marketingActionPanel.resetSearchOffset();
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
			.setDescription(getSearchTextBox().getValue())
			.setMarketingAction(this.marketingAction)
			.setStatus(AonStringUtils.isBlank(targetStatus.getValue()) ? null : Byte.parseByte(targetStatus.getValue()))
			.setOrderBy(sort.getValue())
			.setAsc(Boolean.parseBoolean(asc.getValue()))
			;
	}
	
	// Project Comercial
	private void getTaskHolderSeller(Consumer<Seller> finish) {
		if(AonStringUtils.isBlank(taskHolder.getValue()))
			finish.accept(null);
		else {
			commonService.getSellerByTaskHolder(options.getDomainName(), options.getDomain(), options.getUser(), Integer.parseInt(taskHolder.getValue()), new AsyncCallback<Seller>() {
				
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
		if(sellerDistribution.getListBox().getSelectedIndex() == 1) {
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
		} else if(AonStringUtils.isNotBlank(taskHolder.getValue())) {
			commonService.getSellerByTaskHolder(options.getDomainName(), options.getDomain(), options.getUser(), Integer.parseInt(taskHolder.getValue()), new AsyncCallback<Seller>() {
				
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
			
			commonService.getNextLinealSellerByWorkgroup(options.getDomainName(), options.getDomain(), options.getUser(), Integer.parseInt(workgroup.getValue()), new AsyncCallback<Seller>() {
				
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
					target.setProject(projectCommercial.getId());
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
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) tagSuggestBox.getSuggestBox().getSuggestOracle();
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
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) newsSuggestBox.getSuggestBox().getSuggestOracle();
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
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) newsletterSuggestBox.getSuggestBox().getSuggestOracle();
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
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) surveySuggestBox.getSuggestBox().getSuggestOracle();
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
