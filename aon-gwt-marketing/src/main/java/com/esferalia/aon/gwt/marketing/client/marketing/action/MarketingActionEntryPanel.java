package com.esferalia.aon.gwt.marketing.client.marketing.action;

import java.util.ArrayList;
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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonErrorPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMarketingActionTargetCreationPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMarketingActionTargetCreationPanel.AonMarketingActionTargetCreationPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMarketingActionTargetPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMarketingActionTargetPanel.AonMarketingActionTargetPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.marketing.client.marketing.MarketingModuleOptions;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.MarketingAction.MarketingActionMediaType;
import com.esferalia.aon.occam.api.model.MarketingActionTargetParams;
import com.esferalia.aon.occam.api.model.Newsletter;
import com.esferalia.aon.occam.api.model.Survey;
import com.esferalia.aon.occam.api.model.news.News;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Command;
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
	private AonDoubleBox budget = new AonDoubleBox(15, 2);
	private AonDateBox startDate = new AonDateBox();
	private AonDateBox endDate = new AonDateBox();
	
	private SuggestBox newsSuggestBox = new SuggestBox();
	private List<News> news = new ArrayList<>();
	
	private SuggestBox surveySuggestBox = new SuggestBox();
	private List<Survey> surveis = new ArrayList<>();
	
	private SuggestBox newsletterSuggestBox = new SuggestBox();
	private List<Newsletter> newsletter = new ArrayList<>();
	
	
	// MarketingAction Search
	private FlowPanel searchPanel;
	private FlowPanel filterPanel;
	
	private TextBox targetDescription;
	
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
		
		AonToolbarButton importActionTargetButton = new AonToolbarButton("Crear cliente potencial (Servlet)", AON.CSS.aonIconImport());
		importActionTargetButton.addClickHandler(e -> importActionTarget());
		toolbar.add(importActionTargetButton);
		
		addNorth(toolbar, 50);
	}

	private void importActionTarget() {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "Cliente Potencial" );
		final AonMarketingActionTargetCreationPanel aonMarketingActionPanel = new AonMarketingActionTargetCreationPanel( options.getDomainName(), options.getDomain(), options.getUser(), options.getConfiguration().getAvailableScopes(),  options.getConfiguration().getGeozones(), this.marketingAction, new AonMarketingActionTargetCreationPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept(JSONObject json) {
				dialog.hide();
				String host = Window.Location.getHost();
				String endPoint = "/ms/api/action-target/";
				
				HashMap<String, String> headers = new HashMap<>();
				headers.put("domain_name", options.getDomainName());
				headers.put("domain_login", options.getUser());
				headers.put("domain_id", String.valueOf(options.getDomain()));
				
				JSONObject body = new JSONObject();
				body.put("actionTarget", json);
				
				// Create a URL builder and add query parameters
				UrlBuilder urlBuilder = new UrlBuilder();
				urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
				urlBuilder.setHost(host); 
				urlBuilder.setPath(endPoint);
				
				// Create the request builder with the complete URL
				RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, urlBuilder.buildString());
				requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
				
				headers.entrySet().forEach(entry -> requestBuilder.setHeader(entry.getKey(), entry.getValue()));
				
				try {
				    // Send the request
				    requestBuilder.sendRequest(body.toString(), new RequestCallback() {
				        public void onResponseReceived(Request request, Response response) {
				        	marketingActionPanel.resetSearchOffset();
							setMarketingAction(marketingAction);
				        }

						public void onError(Request request, Throwable exception) {
							
				        }
				    });
				} catch (RequestException exception) {
					
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
		
		switch (MarketingActionMediaType.getMediaType(Integer.parseInt(typeListBox.getSelectedValue()))) {
			case PHONE:
				newsletterSuggestBox.setValue("");
				break;
			case EMAIL:
				newsletterSuggestBox.setValue("");
				surveySuggestBox.setValue("");
				break;
			case MAIL:
				newsSuggestBox.setValue("");
				newsletterSuggestBox.setValue("");
				surveySuggestBox.setValue("");
				break;
			case BULLETIN:
				newsSuggestBox.setValue("");
				surveySuggestBox.setValue("");
				break;
			default:
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
		
		commonService.saveMarketingAction(options.getDomainName(), options.getDomain(), options.getUser(), marketingAction, new AsyncCallback<MarketingAction>() {
			
			@Override
			public void onSuccess(MarketingAction result) {
				marketingAction = result;
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

		FlexTable table = new FlexTable();
		table.setStyleName(AON.CSS.aonTable());
		table.addStyleName(AON.CSS.aonWidthAll());
		
		table.getColumnFormatter().getElement(0).getStyle().setProperty("width", "135px");
		
		table.setWidget(0, 0, new InlineLabel("C\u00f3digo"));
		table.getCellFormatter().setStyleName(0, 0, AON.CSS.aonTableLabel());
		table.setWidget(0,1,new Label(marketingAction.getId().toString()));
		
		table.setWidget(1, 0, new InlineLabel("Descripci\u00f3n"));
		table.getCellFormatter().setStyleName(1, 0, AON.CSS.aonTableLabel());
		description.setValue(marketingAction.getDescription());
		description.setMaxLength(64);
		description.setStyleName(AON.CSS.aonInputText());
		description.addStyleName(AON.CSS.aonWidthAll());
		table.setWidget(1,1,description);
		table.getCellFormatter().setStyleName(1, 1, AON.CSS.aonWidthAll());

		table.setWidget(2, 0, new InlineLabel("Tipo"));
		table.getCellFormatter().setStyleName(2, 0, AON.CSS.aonTableLabel());
		actionType.setText(getActivonType(marketingAction.getMediaType().getValue()));
		table.setWidget(2,1,actionType);

		table.setWidget(3,0,new InlineLabel("Canal"));
		table.getCellFormatter().setStyleName(3, 0, AON.CSS.aonTableLabel());
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
		table.setWidget(3,1,typeListBox);
		
		table.setWidget(4,0,new InlineLabel("Presupuesto"));
		table.getCellFormatter().setStyleName(4, 0, AON.CSS.aonTableLabel());
		budget.setStyleName(AON.CSS.aonInputText());
		budget.setValue(marketingAction.getBudget());
		table.setWidget(4,1,budget);

		table.setWidget(5,0,new InlineLabel("F. Inicio"));
		table.getCellFormatter().setStyleName(5, 0, AON.CSS.aonTableLabel());
		startDate.setStyleName(AON.CSS.aonInputText());
		startDate.setValue(marketingAction.getStartDate());
		table.setWidget(5,1,startDate);

		table.setWidget(6,0,new InlineLabel("F. Fin"));
		table.getCellFormatter().setStyleName(6, 0, AON.CSS.aonTableLabel());
		endDate.setStyleName(AON.CSS.aonInputText());
		endDate.setValue(marketingAction.getEndDate());
		table.setWidget(6,1,endDate);

		table.setWidget(7,0,new InlineLabel("Noticia"));
		table.getCellFormatter().setStyleName(7, 0, AON.CSS.aonTableLabel());
		newsSuggestBox.setStyleName(AON.CSS.aonInputText());
		newsSuggestBox.addStyleName(AON.CSS.aonWidthAll());
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
		table.setWidget(7,1,newsSuggestBox);
		table.getCellFormatter().setStyleName(7, 1, AON.CSS.aonWidthAll());

		table.setWidget(8,0,new InlineLabel("Boletin"));
		table.getCellFormatter().setStyleName(8, 0, AON.CSS.aonTableLabel());
		newsletterSuggestBox.setStyleName(AON.CSS.aonInputText());
		newsletterSuggestBox.addStyleName(AON.CSS.aonWidthAll());
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
		table.setWidget(8,1,newsletterSuggestBox);
		table.getCellFormatter().setStyleName(8, 1, AON.CSS.aonWidthAll());
		
		table.setWidget(9,0,new InlineLabel("Cuestionario"));
		table.getCellFormatter().setStyleName(9, 0, AON.CSS.aonTableLabel());
		surveySuggestBox.setStyleName(AON.CSS.aonInputText());
		surveySuggestBox.addStyleName(AON.CSS.aonWidthAll());
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
		table.setWidget(9,1,surveySuggestBox);
		table.getCellFormatter().setStyleName(9, 1, AON.CSS.aonWidthAll());
		
		showMarketingActionMediaOptions(table, marketingAction.getMediaType());
		
		tablePanel.add( table );
		
		FlexTable tableActionTargetStatus = new FlexTable();
		tableActionTargetStatus.setStyleName(AON.CSS.aonTable());
		tableActionTargetStatus.addStyleName(AON.CSS.aonWidthAll());
		
		tableActionTargetStatus.getColumnFormatter().getElement(0).getStyle().setProperty("width", "135px");
		
		tableActionTargetStatus.setWidget(0, 0, new InlineLabel("Pendiente"));
		tableActionTargetStatus.getCellFormatter().setStyleName(0, 0, AON.CSS.aonTableLabel());
		tableActionTargetStatus.setWidget(0,1,new Label(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)0).count() + ""));

		tableActionTargetStatus.setWidget(1, 0, new InlineLabel("Ausente"));
		tableActionTargetStatus.getCellFormatter().setStyleName(1, 0, AON.CSS.aonTableLabel());
		tableActionTargetStatus.setWidget(1,1,new Label(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)1).count() + ""));

		tableActionTargetStatus.setWidget(2, 0, new InlineLabel("Incorrecto"));
		tableActionTargetStatus.getCellFormatter().setStyleName(2, 0, AON.CSS.aonTableLabel());
		tableActionTargetStatus.setWidget(2,1,new Label(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)2).count() + ""));

		tableActionTargetStatus.setWidget(3, 0, new InlineLabel("Reintentar"));
		tableActionTargetStatus.getCellFormatter().setStyleName(3, 0, AON.CSS.aonTableLabel());
		tableActionTargetStatus.setWidget(3,1,new Label(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)3).count() + ""));

		tableActionTargetStatus.setWidget(4, 0, new InlineLabel("Anular"));
		tableActionTargetStatus.getCellFormatter().setStyleName(4, 0, AON.CSS.aonTableLabel());
		tableActionTargetStatus.setWidget(4,1,new Label(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)4).count() + ""));

		tableActionTargetStatus.setWidget(5, 0, new InlineLabel("Finalizado"));
		tableActionTargetStatus.getCellFormatter().setStyleName(5, 0, AON.CSS.aonTableLabel());
		tableActionTargetStatus.setWidget(5,1,new Label(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)5).count() + ""));

		tableActionTargetStatus.setWidget(6, 0, new InlineLabel("Enviado"));
		tableActionTargetStatus.getCellFormatter().setStyleName(6, 0, AON.CSS.aonTableLabel());
		tableActionTargetStatus.setWidget(6,1,new Label(marketingAction.getTargets().stream().filter(actionTarget -> actionTarget.getActionTargetStatus().byteValue() == (byte)6).count() + ""));
		
		tablePanel.add( tableActionTargetStatus );
		
		rootPanel.add( tablePanel );
		
		container.add(rootPanel);
		
		add(container);
		
		createMarketingActionTargets();
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	description.setFocus(true);
	        }
	    });		
	}

	private void showMarketingActionMediaOptions(FlexTable table, MarketingActionMediaType marketingActionMediaType) {
		switch (marketingActionMediaType) {
			case PHONE:
				table.getRowFormatter().getElement(6).getStyle().clearDisplay();
				table.getRowFormatter().getElement(7).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(8).getStyle().clearDisplay();
				break;
			case EMAIL:
				table.getRowFormatter().getElement(6).getStyle().clearDisplay();
				table.getRowFormatter().getElement(7).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(8).getStyle().setDisplay(Display.NONE);
				break;
			case MAIL:
				table.getRowFormatter().getElement(6).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(7).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(8).getStyle().setDisplay(Display.NONE);
				break;
			case BULLETIN:
				table.getRowFormatter().getElement(6).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(7).getStyle().clearDisplay();
				table.getRowFormatter().getElement(8).getStyle().setDisplay(Display.NONE);
				break;
			default:
				table.getRowFormatter().getElement(6).getStyle().clearDisplay();
				table.getRowFormatter().getElement(7).getStyle().clearDisplay();
				table.getRowFormatter().getElement(8).getStyle().clearDisplay();
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
		
		searchPanel = new FlowPanel();
		searchPanel.setStyleName(AON.CSS.aonSearchPanel());
		searchPanel.addStyleName(AON.CSS.aonFlexBetween());
		searchPanel.getElement().getStyle().setProperty("margin", "0 1rem");
		
		filterPanel = new FlowPanel();
		filterPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label descriptionLabel = new Label(AON.MSG.description());
		descriptionLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		filterPanel.add(descriptionLabel);
		filterPanel.add(targetDescription);
		
		searchPanel.add(filterPanel);
		
		AonSearchPanelButton cleanButton = new AonSearchPanelButton( AON.MSG.clean(), AON.CSS.aonIconClear() );
		cleanButton.addClickHandler(event -> {
			targetDescription.setValue(null,false);
			
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
		marketingActionPanel = new MarketingActionTargetPanel(params) {

			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}

			@Override
			protected void reloadMarketingAction() {
				setMarketingAction(marketingAction);
			}
		
		};
		
		centerPanel.setWidget(marketingActionPanel);
	}

	public MarketingActionTargetParams getWidgetParams( MarketingModuleOptions options) {
		return new MarketingActionTargetParams()
			.setDomainName(options.getDomainName())
			.setDomain(options.getDomain())
			.setUser(options.getUser())
			.setDescription(targetDescription.getValue())
			.setMarketingAction(this.marketingAction)
			;
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
	
	protected abstract void onActionBackClick();
	protected abstract void onActionDeleteClick(MarketingAction marketingAction);

}
