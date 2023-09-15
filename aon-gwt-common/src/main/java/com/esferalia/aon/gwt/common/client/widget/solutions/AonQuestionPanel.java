package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.occam.api.model.Question;
import com.esferalia.aon.occam.api.model.QuestionValue;
import com.esferalia.aon.occam.api.model.registry.QuestionType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class AonQuestionPanel extends SimplePanel {
	
	public static interface AonQuestionPanelCallback {
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

	private TextBox alias = new TextBox();
	private TextBox questionText = new TextBox();
	private TextArea argumentText = new TextArea();
	private ListBox type = new ListBox();
	private Button active = new Button();
	
	private FlowPanel valuesPanel = new FlowPanel();
	private ScrollPanel valuesTableScroll = new ScrollPanel();
	private FlexTable valuesTable = new FlexTable();
	private AonTableButton addValue = new AonTableButton(AON.MSG.newAction(), AON.CSS.aonIconAdd());
	
	public AonQuestionPanel(final String domainName,final int domain, final String user,final AonQuestionPanelCallback callback) {
		initializeCommonService();
		show(domainName, domain, user, new Question(), callback);
	}
	
	public AonQuestionPanel(final String domainName,final int domain,final String user, Integer id, final AonQuestionPanelCallback callback) {
		initializeCommonService();
		commonService.getQuestion(domainName, domain, user, id, new AsyncCallback<Optional<Question>>() {
			
			@Override
			public void onSuccess(Optional<Question> result) {
				if (result.isEmpty()) result = Optional.of(new Question());
				show(domainName, domain, user, result.get(), callback);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				show(domainName, domain, user, new Question(), callback);
			}
		});
	}
	
	public void show(final String domainName,final int domain, final String user,final Question question, final AonQuestionPanelCallback callback) {
		setWidth("650px");
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
		table.addStyleName(AON.CSS.aonWidthAll());
		
		table.setWidget(0, 0, new InlineLabel("Alias"));
		table.getCellFormatter().setStyleName(0, 0, AON.CSS.aonTableLabel());
		table.getCellFormatter().getElement(0, 0).setPropertyString("min-width", "135px");
		alias.setValue(question.getAlias());
		alias.setMaxLength(128);
		alias.setStyleName(AON.CSS.aonInputText());
		alias.addStyleName(AON.CSS.aonWidthAll());
		alias.addValueChangeHandler(e -> {
			checkQuestionAlias(domainName, domain, user, e.getValue(), existAlias -> {
				if(existAlias) {
					errorPanel.showWarning("No puede existir dos preguntas con el mismo alias"); 
					alias.setValue(null);
				}
			});
		});
		table.setWidget(0,1,alias);
		table.getCellFormatter().setStyleName(0, 1, AON.CSS.aonWidthAll());
		table.getFlexCellFormatter().setColSpan(0, 1, 3);
		
		table.setWidget(1,0,new InlineLabel("Texto Pregunta"));
		table.getCellFormatter().setStyleName(1, 0, AON.CSS.aonTableLabel());
		table.getCellFormatter().getElement(1, 0).setPropertyString("min-width", "135px");
		questionText.setValue(question.getText());
		questionText.setMaxLength(128);
		questionText.setStyleName(AON.CSS.aonInputText());
		questionText.addStyleName(AON.CSS.aonWidthAll());
		table.setWidget(1,1,questionText);
		table.getCellFormatter().setStyleName(1, 1, AON.CSS.aonWidthAll());
		table.getFlexCellFormatter().setColSpan(1, 1, 3);
		
		table.setWidget(2,0,new InlineLabel("Argumentaci\u00f3n"));
		table.getCellFormatter().setStyleName(2, 0, AON.CSS.aonTableLabel());
		table.getCellFormatter().getElement(2, 0).setPropertyString("min-width", "135px");
		argumentText.setValue(question.getArgument());
		argumentText.setVisibleLines(3);
		argumentText.setStyleName(AON.CSS.aonInputText());
		argumentText.addStyleName(AON.CSS.aonWidthAll());
		table.setWidget(2,1,argumentText);
		table.getCellFormatter().setStyleName(2, 1, AON.CSS.aonWidthAll());
		table.getFlexCellFormatter().setColSpan(2, 1, 3);
		
		table.setWidget(3,0,new InlineLabel(AON.MSG.type()));
		table.getCellFormatter().setStyleName(3, 0, AON.CSS.aonTableLabel());
		table.getCellFormatter().getElement(3, 0).setPropertyString("min-width", "135px");
		Arrays.stream(QuestionType.values()).forEach(qt -> type.addItem(qt.description(), qt.ordinal() + ""));
		type.setStyleName(AON.CSS.aonInputText());
		setSelectedValueLB(type, null != question.getType() ? question.getType().ordinal() + "" : null);
		type.addChangeHandler(e -> changeQuestionType(domain, question));
		table.setWidget(3,1,type);
		
		table.setWidget(4,0,new InlineLabel("Activo"));
		table.getCellFormatter().setStyleName(4, 0, AON.CSS.aonTableLabel());
		table.getCellFormatter().getElement(4, 0).setPropertyString("min-width", "135px");
		getEnableDisableButton(active, question.isActive());
		active.addClickHandler(e -> getEnableDisableButton(active, !isActiveToggleButton(active)));
		table.setWidget(4,1,active);
		
		tablePanel.add( table );
		
		addValue.addClickHandler(e -> {
			question.addValue(new QuestionValue().setDomain(domain).setQuestion(question));
			createValuesPanel(domain, question);
		});
		
		tablePanel.add( createValuesPanel(domain, question) );
		
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
				
				question.setActive(isActiveToggleButton(active));
				question.setText(questionText.getValue());
				question.setType(QuestionType.safeValueOf(Integer.parseInt(type.getSelectedValue())));
				question.setArgument(argumentText.getValue());
				question.setAlias(alias.getValue());
				
				if (question.getId() == null) {
					question.setDomain(domain);
					question.setActive(isActiveToggleButton(active));
					question.setType(QuestionType.safeValueOf(Integer.parseInt(type.getSelectedValue())));
					question.setText(questionText.getValue());
				}
				
				commonService.saveQuestion(domainName, domain, user, question, new AsyncCallback<Question>() {

					@Override
					public void onSuccess(Question result) {
						callback.onAccept();
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
	        	alias.setFocus(true);
	        	onResize();
	        }
	    });		
		
	}
	
	private Widget createValuesPanel(int domain, Question question) {
		valuesPanel.clear();
		valuesPanel.addStyleName(AON.CSS.aonFlexColumn());
		
		InlineLabel valuesLabel = new InlineLabel("");
		valuesPanel.add(valuesLabel);
		
		valuesTableScroll.clear();
		valuesTableScroll.getElement().getStyle().setProperty("max-height", "180px");
		
		valuesTable = new FlexTable();
		valuesTable.setStyleName(AON.CSS.aonTable());
		valuesTable.addStyleName(AON.CSS.aonWidthAll());
		
		if(QuestionType.safeValueOf(Integer.parseInt(type.getSelectedValue())).equals(QuestionType.BOOLEAN)) {
			question.addValue(new QuestionValue().setDomain(domain).setQuestion(question).setValueNumber((double)0));
			question.addValue(new QuestionValue().setDomain(domain).setQuestion(question).setValueNumber((double)1));
		} else if(!QuestionType.safeValueOf(Integer.parseInt(type.getSelectedValue())).equals(QuestionType.INFO)) {
			
			InlineLabel line = new InlineLabel("");
			line.setStyleName(AON.CSS.aonLine());
			valuesPanel.add(line);
			
			List<QuestionValue> aviableQuestionValues = question.getValues().stream().filter(qtv -> !qtv.isDeleted()).collect(Collectors.toList());
			
			valuesTable.setWidget(0, 0, new InlineLabel("Valor"));
			valuesTable.getCellFormatter().setStyleName(0, 0, AON.CSS.aonTableLabel());
			valuesTable.getCellFormatter().getElement(0, 0).setPropertyString("width", "auto");
			
			valuesTable.setWidget(0, 1, new InlineLabel(""));
			valuesTable.getCellFormatter().getElement(0, 1).setPropertyString("width", "20px");
			
			int row = 1;
			
			for(QuestionValue questionValue : aviableQuestionValues) {
				switch (QuestionType.safeValueOf(Integer.parseInt(type.getSelectedValue()))) {
					case TEXT:
						TextBox valueText = new TextBox();
						valueText.addStyleName(AON.CSS.aonWidthAlmostAll());
						valueText.setValue(questionValue.getValueText());
						valueText.addValueChangeHandler(e -> questionValue.setValueText(e.getValue()));
						valuesTable.setWidget(row, 0, valueText);
						valuesTable.setWidget(row, 1, createDeleteButton(domain, question, questionValue));
						if(null == questionValue.getValueText()) {
							Scheduler.get().scheduleDeferred(new ScheduledCommand() {
								@Override public void execute() { valueText.setFocus(true); }
							});
						}
						break;
					case NUMBER:
						DoubleBox valueNumber = new DoubleBox();
						valueNumber.setValue(questionValue.getValueNumber());
						valueNumber.addValueChangeHandler(e -> questionValue.setValueNumber(e.getValue()));
						valuesTable.setWidget(row, 0, valueNumber);
						valuesTable.setWidget(row, 1, createDeleteButton(domain, question, questionValue));
						if(null == questionValue.getValueNumber()) {
							Scheduler.get().scheduleDeferred(new ScheduledCommand() {
								@Override public void execute() { valueNumber.setFocus(true); }
							});
						}
						break;
					case DATE:
						AonDateBox valueDate = new AonDateBox();
						valueDate.setValue(questionValue.getValueDate());
						valueDate.addValueChangeHandler(e -> questionValue.setValueDate(e.getValue()));
						valuesTable.setWidget(row, 0, valueDate);
						valuesTable.setWidget(row, 1, createDeleteButton(domain, question, questionValue));
						if(null == questionValue.getValueDate()) {
							Scheduler.get().scheduleDeferred(new ScheduledCommand() {
								@Override public void execute() { valueDate.setFocus(true); }
							});
						}
						break;
					default:
						break;
				}
				
				row++;
			}
			
			valuesTable.setWidget(row, 0, addValue);
			valuesTable.getFlexCellFormatter().setColSpan(row, 0, 2);
			
		}
		
		valuesTableScroll.add(valuesTable);
		valuesPanel.add(valuesTableScroll);
		
		return valuesPanel;
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
	
	private AonTableButton createDeleteButton(int domain, Question question, QuestionValue questionValue) {
		AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
		deleteButton.addClickHandler(e -> {
			deleteButton.setEnabled(false);
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n Valor",
					new HTML("Se va a proceder a eliminar el valor.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					deleteButton.setEnabled(true);
				}

				@Override
				public void onAccept() {
					questionValue.setDeleted(true);
					createValuesPanel(domain, question);
				}
			});
		});
		
		return deleteButton;
	}
	
	private void changeQuestionType(int domain, Question question) {
		List<QuestionValue> aviableQuestionValues = question.getValues().stream().filter(qtv -> !qtv.isDeleted()).collect(Collectors.toList());
		if(!aviableQuestionValues.isEmpty()) {
			AonDialog dialog = new AonDialog("Cambio Tipo",
					new HTML("Se va a proceder a eliminar los valores que existen para el tipo actual.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					setSelectedValueLB(type, null != question.getType() ? question.getType().ordinal() + "" : null);
				}

				@Override
				public void onAccept() {
					question.setType(QuestionType.safeValueOf(Integer.parseInt(type.getSelectedValue())));
					question.getValues().forEach(qtv -> qtv.setDeleted(true));
					createValuesPanel(domain, question);
				}
			});
		} else createValuesPanel(domain, question);
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
	
	private void checkQuestionAlias(String domainName, Integer domain, String user, String alias, Consumer<Boolean> success) {
		commonService.checkQuestionAlias(domainName, domain, user, alias, new AsyncCallback<Boolean>() {
			
			@Override
			public void onSuccess(Boolean existAlias) {
				success.accept(existAlias);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}

	protected abstract void onResize();

}
