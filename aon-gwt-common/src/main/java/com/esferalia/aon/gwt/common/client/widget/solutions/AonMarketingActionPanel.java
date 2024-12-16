package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.MarketingAction.MarketingActionMediaType;
import com.esferalia.aon.occam.api.model.MarketingCampaign;
import com.esferalia.aon.occam.api.model.Newsletter;
import com.esferalia.aon.occam.api.model.Survey;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.news.News;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class AonMarketingActionPanel extends SimplePanel {
	
	public static interface AonMarketingActionPanelCallback {
		void onAccept(MarketingAction marketingAction);
		void onCancel();
	}

	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	private HTMLPanel messagePanel = new HTMLPanel(AonStringUtils.EMPTY);

	private AonCustomTextBox description = new AonCustomTextBox("Descripci\u00f3n");
	private AonCustomListBox typeListBox = new AonCustomListBox("Canal");
	private AonCustomTextBox actionType = new AonCustomTextBox("Tipo");
	
	private AonCustomNumberBox budget = new AonCustomNumberBox("Presupuesto");
	private AonCustomNumberBox expense = new AonCustomNumberBox("Gastos");
	
	private AonCustomDateBox startDate = new AonCustomDateBox("F. Inicio");
	private AonCustomDateBox endDate = new AonCustomDateBox("F .Fin");
	
	private AonCustomListBox workgroup = new AonCustomListBox("Grupo de trabajo");
	private AonCustomListBox taskHolder = new AonCustomListBox("Asignado a");
	
	private AonCustomSuggestBox newsSuggestBox = new AonCustomSuggestBox("Noticia");
	private List<News> news = new ArrayList<>();
	
	private AonCustomSuggestBox surveySuggestBox = new AonCustomSuggestBox("Boletin");
	private List<Survey> surveis = new ArrayList<>();
	
	private AonCustomSuggestBox newsletterSuggestBox = new AonCustomSuggestBox("Custionario");
	private List<Newsletter> newsletter = new ArrayList<>();
	
	private AonCustomTextBox webForm = new AonCustomTextBox("Formulario Web");
	
	private String domainName;
	private Integer domainId;
	private String user;
	
	public AonMarketingActionPanel(final String domainName,final int domain, final String user, final MarketingCampaign marketingCampaign, final AonMarketingActionPanelCallback aonMarketingActionPanelCallback) {
		initializeCommonService();
		
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		show(new MarketingAction().setMarketingCampaign(marketingCampaign), aonMarketingActionPanelCallback);
	}

	public void show(MarketingAction marketingAction, AonMarketingActionPanelCallback callback) {
		getElement().getStyle().setProperty("padding", "1rem");
		getElement().getStyle().setProperty("min-width", "25rem");
		
		FlowPanel rootPanel = new FlowPanel();
		rootPanel.setStyleName(AON.CSS.aonFlexColumnBetween());
		
		rootPanel.add(messagePanel);
		
		KeyUpHandler keyUpHandler = new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					callback.onCancel();	
				}
			}
		};
		
		AonCustomTextBox campaign = new AonCustomTextBox("Campa\u00f1a");
		campaign.setValue(marketingAction.getMarketingCampaign().getDescription());
		campaign.setEnable(false);
		rootPanel.add(campaign);
		
		description.setValue(marketingAction.getDescription());
		description.getTextBox().setMaxLength(64);
		rootPanel.add(description);

		HTMLPanel typePanel = new HTMLPanel(AonStringUtils.EMPTY);
		typePanel.addStyleName(AON.CSS.aonItemFlex());
		
		typeListBox.clearItems();
		for(int i=0; i<MarketingActionMediaType.values().length; i++) {
			MarketingActionMediaType marketingActionMediaType = MarketingActionMediaType.values()[i];
			typeListBox.addItem(marketingActionMediaType.getDescription(), marketingActionMediaType.getValue().toString());
		}
		typeListBox.addChangeHandler(e -> {
			MarketingActionMediaType marketingActionMediaType = MarketingActionMediaType.getMediaType(Integer.parseInt(typeListBox.getValue()));
			showMarketingActionMediaOptions(marketingActionMediaType);
		});
		typePanel.add(typeListBox);
		
		actionType.setEnable(false);
		typePanel.add(actionType);
		
		rootPanel.add(typePanel);
		
		HTMLPanel amountsPanel = new HTMLPanel(AonStringUtils.EMPTY);
		amountsPanel.addStyleName(AON.CSS.aonItemFlex());
		
		budget.hideNearBy();
		amountsPanel.add(budget);
		expense.hideNearBy();
		amountsPanel.add(expense);
		
		rootPanel.add(amountsPanel);
		
		HTMLPanel datesPanel = new HTMLPanel(AonStringUtils.EMPTY);
		datesPanel.addStyleName(AON.CSS.aonItemFlex());
		
		datesPanel.add(startDate);
		datesPanel.add(endDate);
		
		rootPanel.add(datesPanel);
		
		
		HTMLPanel holderPanel = new HTMLPanel(AonStringUtils.EMPTY);
		holderPanel.addStyleName(AON.CSS.aonItemFlex());
		
		workgroup.addItem("-", "");
		workgroup.addChangeHandler(e -> {
			if(AonStringUtils.isBlank(workgroup.getValue())) {
				taskHolder.getListBox().setSelectedIndex(0);
				taskHolder.setVisible(false);
			} else {
				taskHolder.setVisible(true);
				getAviableTaskHolders(Integer.parseInt(workgroup.getValue()), taskHolders -> { 
					taskHolder.clearItems();
					taskHolder.addItem("-", "");
					taskHolders.forEach(taskHolderIt -> taskHolder.addItem(taskHolderIt.getName(), taskHolderIt.getRegistry().toString()));
				});
			}
		});
		
		taskHolder.addItem("-", "");
		getAviableWorkgroups(workgroups -> {
			workgroups.forEach(workgroupIt -> workgroup.addItem(workgroupIt.getDescription(), workgroupIt.getId().toString()));
			taskHolder.setVisible(false);
		});
		
		holderPanel.add(workgroup);
		holderPanel.add(taskHolder);
		
		rootPanel.add(holderPanel);
		
		newsSuggestBox.setAutoSelectEnabled(false);
		newsSuggestBox.getSuggestBox().getElement().setPropertyString("placeholder", "Ctrl + espacio para ver sugerencias");
		newsSuggestBox.getSuggestBox().addKeyUpHandler(e -> {
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				newsSuggestBox.showSuggestionList();
			}
		});
		getNewsSuggestion(success -> {
			newsSuggestBox.setValue(null);
		});
		rootPanel.add(newsSuggestBox);
		
		newsletterSuggestBox.setAutoSelectEnabled(false);
		newsletterSuggestBox.getSuggestBox().getElement().setPropertyString("placeholder", "Ctrl + espacio para ver sugerencias");
		newsletterSuggestBox.getSuggestBox().addKeyUpHandler(e -> {
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				newsletterSuggestBox.showSuggestionList();
			}
		});
		getNewsletterSuggestion(success -> {
			newsletterSuggestBox.setValue(null);
		});
		rootPanel.add(newsletterSuggestBox);
		
		surveySuggestBox.setAutoSelectEnabled(false);
		surveySuggestBox.getSuggestBox().getElement().setPropertyString("placeholder", "Ctrl + espacio para ver sugerencias");
		surveySuggestBox.getSuggestBox().addKeyUpHandler(e -> {
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				surveySuggestBox.showSuggestionList();
			}
		});
		getSurveySuggestion(success -> {
			surveySuggestBox.setValue(null);
		});
		rootPanel.add(surveySuggestBox);
		
		webForm.setValue("Una vez cree la acci\u00f3n podr\u00e1 visualizar el formulario web (API)");
		webForm.setEnable(false);
		rootPanel.add(webForm);
		
		MarketingActionMediaType marketingActionMediaType = MarketingActionMediaType.getMediaType(Integer.parseInt(typeListBox.getValue()));
		showMarketingActionMediaOptions(marketingActionMediaType);
		
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
				
				marketingAction.setDomain(domainId);
				marketingAction.setDescription(description.getValue());
				marketingAction.setMediaType(MarketingActionMediaType.getMediaType(Integer.parseInt(typeListBox.getValue())));
				marketingAction.setStartDate(startDate.getValue());
				marketingAction.setEndDate(endDate.getValue());
				marketingAction.setBudget(budget.getValue());
				marketingAction.setExpense(expense.getValue());
				marketingAction.setWorkgroup(AonStringUtils.isBlank(workgroup.getValue()) ? null : new Workgroup().setId(Integer.parseInt(workgroup.getValue())));
				marketingAction.setTaskHolder(AonStringUtils.isBlank(taskHolder.getValue()) ? null : new TaskHolder().setRegistry(Integer.parseInt(taskHolder.getValue())));
				
				switch (MarketingActionMediaType.getMediaType(Integer.parseInt(typeListBox.getValue()))) {
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
				
				commonService.saveMarketingAction(domainName, domainId, user, marketingAction, new AsyncCallback<MarketingAction>() {

					@Override
					public void onSuccess(MarketingAction marketingAction) {
						callback.onAccept(marketingAction);
					}
					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, caught.getMessage());
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
	        	description.setFocus(true);
	        	onResize();
	        }
	    });		
		
	}
	
	private void showMarketingActionMediaOptions(MarketingActionMediaType marketingActionMediaType) {
		switch (marketingActionMediaType) {
			case PHONE:
				newsSuggestBox.setVisible(true);
				surveySuggestBox.setVisible(false);
				newsletterSuggestBox.setVisible(true);
				webForm.setVisible(false);
				break;
			case EMAIL:
				newsSuggestBox.setVisible(true);
				surveySuggestBox.setVisible(false);
				newsletterSuggestBox.setVisible(false);
				webForm.setVisible(false);
				break;
			case MAIL:
				newsSuggestBox.setVisible(false);
				surveySuggestBox.setVisible(false);
				newsletterSuggestBox.setVisible(false);
				webForm.setVisible(false);
				break;
			case BULLETIN:
				newsSuggestBox.setVisible(false);
				surveySuggestBox.setVisible(true);
				newsletterSuggestBox.setVisible(false);
				webForm.setVisible(false);
				break;
			default:
				newsSuggestBox.setVisible(false);
				surveySuggestBox.setVisible(false);
				newsletterSuggestBox.setVisible(false);
				webForm.setVisible(true);
				break;
		}
		
		actionType.setValue(getActivonType(marketingActionMediaType.getValue()));
	}

	private String getActivonType(Integer type) {
		return type > 5 ? "Incoming" : "Outcoming";
	}
	
	private void getNewsSuggestion(Consumer<Void> success) {
		commonService.getNewsSuggestion(domainName, domainId, user, new AsyncCallback<List<News>>() {
			
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
				AonMessagePanel.showError(messagePanel, "Error obteniendo news: " + caught.getMessage());
			}
			
		});
	}
	
	private void getNewsletterSuggestion(Consumer<Void> success) {
		commonService.getNewsletterSuggestion(domainName, domainId, user, new AsyncCallback<List<Newsletter>>() {
			
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
				AonMessagePanel.showError(messagePanel, "Error obteniendo newsletters: " + caught.getMessage());
			}
			
		});
	}
	
	private void getSurveySuggestion(Consumer<Void> success) {
		commonService.getSurveySuggestion(domainName, domainId, user, new AsyncCallback<List<Survey>>() {
			
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
				AonMessagePanel.showError(messagePanel, "Error obteniendo surveis: " + caught.getMessage());
			}
			
		});
	}
	
	private void getAviableWorkgroups(Consumer<List<Workgroup>> success) {
		commonService.getAviableWorkgroups(domainName, domainId, user, new AsyncCallback<List<Workgroup>>() {
			
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
		commonService.getAviableTaskHolders(domainName, domainId, user, workgroup, new AsyncCallback<List<TaskHolder>>() {
			
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
	
	protected abstract void onResize();

}
