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
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;

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

	private TextBox description = new TextBox();
	private Label actionType = new Label();
	private ListBox typeListBox = new ListBox();
	private AonDoubleBox budget = new AonDoubleBox(15, 2);
	private AonDoubleBox expense = new AonDoubleBox(15, 2);
	private AonDateBox startDate = new AonDateBox();
	private AonDateBox endDate = new AonDateBox();
	private ListBox workgroup = new ListBox();
	private InlineLabel taskHolderLabel = new InlineLabel("Asignado a");
	private ListBox taskHolder = new ListBox();
	
	private SuggestBox newsSuggestBox = new SuggestBox();
	private List<News> news = new ArrayList<>();
	
	private SuggestBox surveySuggestBox = new SuggestBox();
	private List<Survey> surveis = new ArrayList<>();
	
	private SuggestBox newsletterSuggestBox = new SuggestBox();
	private List<Newsletter> newsletter = new ArrayList<>();
	
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
		
		table.setWidget(2, 0, new InlineLabel("Canal"));
		table.getCellFormatter().setStyleName(2, 0, AON.CSS.aonTableLabel());
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
		addSelectStyle(workgroup.getElement());
		table.setWidget(2, 1, typeListBox);

		table.setWidget(2, 2, new InlineLabel("Tipo"));
		table.getCellFormatter().setStyleName(1, 2, AON.CSS.aonTableLabel());
		table.setWidget(2, 3, actionType);

		table.setWidget(3, 0, new InlineLabel("Presupuesto"));
		table.getCellFormatter().setStyleName(3, 0, AON.CSS.aonTableLabel());
		addInputStyle(budget.getElement());
		table.setWidget(3, 1, budget);
		
		table.setWidget(3, 2, new InlineLabel("Gastos"));
		table.getCellFormatter().setStyleName(3, 2, AON.CSS.aonTableLabel());
		addInputStyle(expense.getElement());
		table.setWidget(3, 3, expense);

		table.setWidget(4, 0, new InlineLabel("F. Inicio"));
		table.getCellFormatter().setStyleName(4, 0, AON.CSS.aonTableLabel());
		startDate.setStyleName(AON.CSS.aonInputText());
		addInputStyle(startDate.getElement());
		table.setWidget(4, 1, startDate);

		table.setWidget(4, 2, new InlineLabel("F. Fin"));
		table.getCellFormatter().setStyleName(4, 2, AON.CSS.aonTableLabel());
		endDate.setStyleName(AON.CSS.aonInputText());
		addInputStyle(endDate.getElement());
		table.setWidget(4, 3, endDate);
		
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
		});
		
		taskHolder = new ListBox();
		addSelectStyle(taskHolder.getElement());
		taskHolder.addItem("-", "");
		
		getAviableWorkgroups(workgroups -> {
			workgroups.forEach(workgroupIt -> workgroup.addItem(workgroupIt.getDescription(), workgroupIt.getId().toString()));
			taskHolder.setVisible(false);
			taskHolderLabel.setVisible(false);
		});
		
		table.setWidget(5, 0, new InlineLabel("Grupo trabajo"));
		table.getCellFormatter().setStyleName(5, 0, AON.CSS.aonTableLabel());
		table.setWidget(5, 1, workgroup);
		
		table.setWidget(5, 2, taskHolderLabel);
		table.getCellFormatter().setStyleName(5, 2, AON.CSS.aonTableLabel());
		table.setWidget(5, 3, taskHolder);

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
			newsSuggestBox.setValue(null);
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
			newsletterSuggestBox.setValue(null);
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
			surveySuggestBox.setValue(null);
		});
		table.setWidget(8, 1, surveySuggestBox);
		table.getFlexCellFormatter().setColSpan(8, 1, 3);
		
		table.setWidget(9, 0, new InlineLabel("Form. Web"));
		table.getCellFormatter().setStyleName(9, 0, AON.CSS.aonTableLabel());
		Label webForm = new Label("Una vez cree la acci\u00f3n podr\u00e1 visualizar el formulario web (API)");
		webForm.addStyleName(AON.CSS.aonItemFlex());
		addInputStyle(webForm.getElement());
		table.setWidget(9, 1, webForm);
		table.getFlexCellFormatter().setColSpan(9, 1, 3);
		
		MarketingActionMediaType marketingActionMediaType = MarketingActionMediaType.getMediaType(Integer.parseInt(typeListBox.getSelectedValue()));
		showMarketingActionMediaOptions(table, marketingActionMediaType);
		
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
				
				marketingAction.setDomain(domainId);
				marketingAction.setDescription(description.getValue());
				marketingAction.setMediaType(MarketingActionMediaType.getMediaType(Integer.parseInt(typeListBox.getSelectedValue())));
				marketingAction.setStartDate(startDate.getValue());
				marketingAction.setEndDate(endDate.getValue());
				marketingAction.setBudget(budget.getValue());
				marketingAction.setExpense(expense.getValue());
				marketingAction.setWorkgroup(0 == workgroup.getSelectedIndex() ? null : new Workgroup().setId(Integer.parseInt(workgroup.getSelectedValue())));
				marketingAction.setTaskHolder(0 == taskHolder.getSelectedIndex() ? null : new TaskHolder().setRegistry(Integer.parseInt(taskHolder.getSelectedValue())));
				
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
	        	description.setFocus(true);
	        	onResize();
	        }
	    });		
		
	}
	
	private void showMarketingActionMediaOptions(FlexTable table, MarketingActionMediaType marketingActionMediaType) {
		switch (marketingActionMediaType) {
			case PHONE:
				table.getRowFormatter().getElement(6).getStyle().clearDisplay();
				table.getRowFormatter().getElement(7).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(8).getStyle().clearDisplay();
				table.getRowFormatter().getElement(9).getStyle().setDisplay(Display.NONE);
				break;
			case EMAIL:
				table.getRowFormatter().getElement(6).getStyle().clearDisplay();
				table.getRowFormatter().getElement(7).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(8).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(9).getStyle().setDisplay(Display.NONE);
				break;
			case MAIL:
				table.getRowFormatter().getElement(6).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(7).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(8).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(9).getStyle().setDisplay(Display.NONE);
				break;
			case BULLETIN:
				table.getRowFormatter().getElement(6).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(7).getStyle().clearDisplay();
				table.getRowFormatter().getElement(8).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(9).getStyle().setDisplay(Display.NONE);
				break;
			default:
				table.getRowFormatter().getElement(6).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(7).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(8).getStyle().setDisplay(Display.NONE);
				table.getRowFormatter().getElement(9).getStyle().clearDisplay();
				break;
		}
		
		actionType.setText(getActivonType(marketingActionMediaType.getValue()));
	}

	private String getActivonType(Integer type) {
		return type > 5 ? "Incoming" : "Outcoming";
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
	
	private void getNewsSuggestion(Consumer<Void> success) {
		commonService.getNewsSuggestion(domainName, domainId, user, new AsyncCallback<List<News>>() {
			
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
		commonService.getNewsletterSuggestion(domainName, domainId, user, new AsyncCallback<List<Newsletter>>() {
			
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
		commonService.getSurveySuggestion(domainName, domainId, user, new AsyncCallback<List<Survey>>() {
			
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
		commonService.getAviableWorkgroups(domainName, domainId, user, new AsyncCallback<List<Workgroup>>() {
			
			@Override
			public void onSuccess(List<Workgroup> workgroups) {
				success.accept(workgroups);
			}
			
			@Override
			public void onFailure(Throwable caught) {
//				AonMessagePanel.showError(messagePanel, "Error obteniendo workgroups: " + caught.getMessage());
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
//				AonMessagePanel.showError(messagePanel, "Error obteniendo workgroups: " + caught.getMessage());
			}
		});
	}
	
	protected abstract void onResize();

}
