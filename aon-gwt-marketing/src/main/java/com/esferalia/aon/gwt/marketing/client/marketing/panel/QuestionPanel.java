package com.esferalia.aon.gwt.marketing.client.marketing.panel;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonQuestionPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonQuestionPanel.AonQuestionPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.Question;
import com.esferalia.aon.occam.api.model.QuestionParams;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public class QuestionPanel extends ScrollPanel implements HasSelectionHandlers<Question> {

	private static final int CHANGE_DISPLAY_MILLIS = 1000;
	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(QuestionPanel.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private final int limit = 100;
	private final MutableInt row = new MutableInt(0);
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	private SimplePanel container;
	private FlexTable tab;
	private int lastScrollPos = 0;
	
	private QuestionParams params;
	
	private static enum COLS {
		  NUM(AonStringUtils.EMPTY					,"20px"  ,AON.CSS.aonTextCenter())
		, SEL(AonStringUtils.EMPTY					,"20px"  ,AON.CSS.aonTextCenter())
		, DES(AON.MSG.alias()						,"auto"  ,null)
		, TYP(AON.MSG.type()						,"180px" ,null)
		, BUT(AonStringUtils.EMPTY					,"20px"  ,null)
		;

		String headerLabel;
		String colWidth;
		String cellStyleClass;

		private COLS(String headerLabel,String colWidth) {
			this(headerLabel, colWidth, null);
		}

		private COLS(String headerLabel,String colWidth,String cellStyleClass) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.cellStyleClass = cellStyleClass;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getCellStyleClass() {
			return cellStyleClass;
		}
	}

	public QuestionPanel(QuestionParams params) {
		
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonPaddingBottom());
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.params = params;

		container = new SimplePanel();
		setWidget(container);
		
		addScrollHandler(new ScrollHandler() {

			public void onScroll(ScrollEvent event) {
				// ------------------------------------ Ignore scroll up.
				int oldScrollPos = lastScrollPos;
				lastScrollPos = getVerticalScrollPosition();
				if (oldScrollPos >= lastScrollPos) {
					return;
				}
				// -----------------------------------------------------
				if (isSearchEnabled()) {
					int maxScrollTop = getWidget().getOffsetHeight() - getOffsetHeight();
					if (lastScrollPos >= maxScrollTop) {
						disableSearch();
						search(offset.getValue());
					}
				}
			}
		});
		
		onSearch();
		
	}

	public boolean isSearchEnabled() {
		return (searchEnabled.getValue() == 0 );
	}
	public void disableSearch() {
		searchEnabled.setValue(-1);
	}
	public void enableSearch() {
		searchEnabled.setValue(0);
	}
	public boolean isMoreData() {
		return (moreData.getValue() == 0 );
	}
	public void disableMoreData() {
		moreData.setValue(-1);
	}
	public void enableMoreData() {
		moreData.setValue(0);
	}
	
	private void onSearch() {
		enableMoreData();
		search();
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Question> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	private void search() {
		container.clear();
		tab = new FlexTable();
		tab.addStyleName(AON.CSS.aonGrid());
		
		paintHeader();
		container.setWidget(tab);
		row.setValue(1);
		offset.setValue(0);
		search(offset.getValue());
	}
	
	private void paintHeader() {
		for ( COLS col : COLS.values()) {
			tab.getColumnFormatter().setWidth(col.ordinal(), col.getColWidth());	
			tab.setWidget(0, col.ordinal(), new Label( col.getHeaderLabel() ));
			tab.getFlexCellFormatter().addStyleName(0, col.ordinal(),AON.CSS.aonGridHeader());
			if ( col.getCellStyleClass() != null) {
				tab.getFlexCellFormatter().addStyleName(0, col.ordinal(),col.getCellStyleClass());
				tab.getFlexCellFormatter().addStyleName(0, col.ordinal(),AON.CSS.aonNowrap());
			}
		}
	}
	
	private void search(final int ofs) {
		if (!isMoreData()) return;
		
		params.setOffset(ofs);
		params.setLimit(limit);
		
		getList(questions -> {
			boolean something = false;
			
			for(Question question : questions) {
				something = true;
				paintRow(question);
			}
			
			if (questions.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(ofs + questions.size() - 1);
				enableMoreData();
			}
			
			if (!something) {
				FlowPanel line = new FlowPanel();
				InlineLabel label = new InlineLabel(AON.MSG.noData());
				line.add(label);
				container.clear();
				container.add(line);
				disableMoreData();
			}
			enableSearch();
			
		});
	}
	
	private void paintRow(Question question) {
		final int r = row.getValue();
		paintRow(r, question); 
		row.increment();
	}
	
	private void paintRow(final int r, Question question) {
		int col = 0;
		boolean myInvestAsset =  question == null || AonNumberUtils.equals(question.getDomain() , params.getDomain()); 
		if (myInvestAsset) {
			paintActiveRow(r,col,question);
		} else {
			paintInactiveRow(r,col,question);
		}
	}

	private void paintInactiveRow(final int r, int col, Question question) {
		Label msg = new Label("");
		msg.setStyleName(AON.CSS.aonTabIcon());
		msg.addStyleName(AON.CSS.aonIconLevelTop());
		tab.setWidget(r, col, msg);
		col++;
		
		Label sel = new Label("");
		tab.setWidget(r, col, sel);
		col++;
		
		tab.setWidget(r, col, new Label(question.getAlias()));
		col++;
		
		tab.setWidget(r, col, new Label(question.getType().description()));
		col++;

	}

	private void paintActiveRow(final int r, int col, Question question) {
		Label msg = new Label("");
		Label sel = new Label("");
		sel.setStyleName(AON.CSS.aonTabIcon());
		sel.addStyleName(AON.CSS.aonIconRight());
		TextBox aliasBox = new TextBox();
		Label typeBox = new Label();
		
		ValueChangeHandler<String> valueChangeHandlerString = new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				question.setAlias(aliasBox.getValue());
				
				save(question, msg);
			}
		};
		
		sel.addClickHandler(e -> {
			final AonCustomDialog dialog = new AonCustomDialog();
			dialog.setCaption(AON.MSG.questionPanel());
			final AonQuestionPanel questionPanel = new AonQuestionPanel( params.getDomainName(), params.getDomain(), params.getUser(), question.getId(), new AonQuestionPanelCallback() {
				
				@Override
				public void onCancel() {
					dialog.hide();
				}
				
				@Override
				public void onAccept() {
					dialog.hide();
					onSearch();
				}
			}) {

				@Override
				protected void onResize() {
					dialog.showLoaded();
				}};
			
			dialog.add( questionPanel );
			dialog.showLoaded();
		});
		
		aliasBox.addValueChangeHandler(valueChangeHandlerString);
		
		msg.setStyleName(AON.CSS.aonTabIcon());
		tab.setWidget(r, col, msg);
		col++;
		
		tab.setWidget(r, col, sel);
		col++;
		
		aliasBox.setStyleName(AON.CSS.aonBorderNone());
		aliasBox.addStyleName(AON.CSS.aonWidthAll());
		aliasBox.setMaxLength(128);
		aliasBox.setValue(question.getAlias());
		tab.setWidget(r, col, aliasBox);
		col++;

		typeBox.setStyleName(AON.CSS.aonBorderNone());
		typeBox.addStyleName(AON.CSS.aonWidthAll());
		typeBox.setText(question.getType().description());
		tab.setWidget(r, col, typeBox);
		col++;
		
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		
		AonTableButton button;
		
		if(question.hasSurvey()) {
			button = new AonTableButton("", AON.CSS.aonIconInfo());
			button.addClickHandler(e -> {
				String message = "Esta pregunta pertenece a una o mas encuestas y no se puede borrar por que esta vinculada.<br> Estas son las encuestas donde aparece esta pregunta:<br>";
				for(String surveyDesc : question.getSurveyDescriptions()) message += "<b>" + surveyDesc + "</b><br>";
				
				AonDialog dialog = new AonDialog("Informaci\u00f3n Pregunta", new HTML(message));
				dialog.info();
			});
			
		} else {
			button = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
			button.addClickHandler( new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					button.setEnabled(false);
					AonDialog dialog = new AonDialog("Eliminaci\u00f3n Pregunta",
							new HTML("Se va a proceder a eliminar la pregunta <b>" + question.getAlias() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
					
					dialog.confirm(new AonAcceptDialogCallback() {

						@Override
						public void onCancel() {
							button.setEnabled(true);
						}

						@Override
						public void onAccept() {
							delete(question);
						}
					});
				}
			});
		}
		
		buttonContainer.add(button);
		tab.setWidget(r, col, buttonContainer);
		col++;
	}
	
	private void getList(Consumer<List<Question>> success) {
		COMMON_SERVICE.getQuestions(params, new AsyncCallback<List<Question>>() {
			
			@Override
			public void onSuccess(List<Question> questions) {
				success.accept(questions);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
	private void delete(Question question) {
		COMMON_SERVICE.deleteQuestion(params.getDomainName(), params.getDomain(), params.getUser(), question.getId(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				onSearch();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				error(caught.getMessage());
			}
		});
	}
	
	private void save(Question question, Label msg) {
		COMMON_SERVICE.saveQuestion(params.getDomainName(), params.getDomain(), params.getUser(), question, new AsyncCallback<Question>() {
			
			@Override
			public void onSuccess(Question result) {
				msg.addStyleName(AON.CSS.aonIconValid());
				new Timer() {
					@Override
					public void run() {
						msg.removeStyleName(AON.CSS.aonIconValid());
					}
				}.schedule(CHANGE_DISPLAY_MILLIS);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				error(caught.getMessage());
			}
		});
	}
	
	private void error(String message) {
		AonDialog dialog = new AonDialog("Error", new HTML(message));
		dialog.info();
	}
	
}

