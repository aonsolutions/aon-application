package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonInvestAssetPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonInvestAssetPanel.AonInvestAssetPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.InvestAssetParams;
import com.esferalia.aon.occam.api.model.InvestAssetRegime;
import com.esferalia.aon.occam.api.model.InvestAssetType;
import com.esferalia.aon.occam.api.model.payroll.Activity;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public class InvestAssetPanel extends ScrollPanel implements HasSelectionHandlers<InvestAsset> {

	private static final int CHANGE_DISPLAY_MILLIS = 1000;
	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(InvestAssetPanel.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private final int limit = 100;
	private final MutableInt row = new MutableInt(0);
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	private SimplePanel container;
	private FlexTable tab;
	private int lastScrollPos = 0;
	
	private InvestAssetParams params;
	private List<Activity> activities;
	
	private static enum COLS {
		  NUM(AonStringUtils.EMPTY					,"20px"  ,AON.CSS.aonTextCenter())
		, SEL(AonStringUtils.EMPTY					,"20px"  ,AON.CSS.aonTextCenter())
		, DES(AON.MSG.description()					,"auto"  ,null)
		, ALI(AON.MSG.activity()					,"220px" ,null)
		, TYP(AON.MSG.type()						,"180px" ,null)
		, REG("Regimen"								,"150px" ,null)
		, IVA("% IVA"								,"90px" ,null)
		, RET("% Imp. Directa"						,"90px" ,null)
		, STD("F. Inicio"							,"90px" ,null)
		, END("F. Fin"								,"90px" ,null)
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

	public InvestAssetPanel(InvestAssetParams params) {
		
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonPaddingBottom());
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.params = params;

		container = new SimplePanel();
		setWidget(container);
		
		addScrollHandler(new ScrollHandler() {

			@Override
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
		
		getActivities(activities -> {
			this.activities = activities;
			onSearch();
		});
		
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
	public HandlerRegistration addSelectionHandler(SelectionHandler<InvestAsset> handler) {
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
		
		getList(investAssets -> {
			boolean something = false;
			
			for(InvestAsset investAsset : investAssets) {
				something = true;
				paintRow(investAsset);
			}
			
			if (investAssets.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(ofs + investAssets.size() - 1);
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
	
	private void paintRow(InvestAsset investAsset) {
		final int r = row.getValue();
		paintRow(r, investAsset); 
		row.increment();
	}
	
	private void paintRow(final int r, InvestAsset investAsset) {
		int col = 0;
		boolean myInvestAsset =  investAsset == null || AonNumberUtils.equals( investAsset.getDomain() , params.getDomain()); 
		if (myInvestAsset) {
			paintActiveRow(r,col,investAsset);
		} else {
			paintInactiveRow(r,col,investAsset);
		}
	}

	private void paintInactiveRow(final int r, int col, InvestAsset investAsset) {
		Label msg = new Label("");
		msg.setStyleName(AON.CSS.aonTabIcon());
		msg.addStyleName(AON.CSS.aonIconLevelTop());
		tab.setWidget(r, col, msg);
		col++;
		
		Label sel = new Label("");
		tab.setWidget(r, col, sel);
		col++;
		
		tab.setWidget(r, col, new Label(investAsset.getDescription()));
		col++;
		
		tab.setWidget(r, col, new Label(investAsset.getActivity().getDescription()));
		col++;

		tab.setWidget(r, col, new Label(investAsset.getType().name()));
		col++;

		tab.setWidget(r, col, new Label(investAsset.getRegime().name()));
		col++;
		
		tab.setWidget(r, col, new Label(investAsset.getVatPercent()+""));
		col++;
		
		tab.setWidget(r, col, new Label(investAsset.getRetentionPercent()+""));
		col++;
		
		tab.setWidget(r, col, new Label(investAsset.getStartDate()+""));
		col++;
		
		tab.setWidget(r, col, new Label(investAsset.getEndDate()+""));
		col++;
	}

	private void paintActiveRow(final int r, int col, InvestAsset investAsset) {
		Label msg = new Label("");
		Label sel = new Label("");
		sel.setStyleName(AON.CSS.aonTabIcon());
		sel.addStyleName(AON.CSS.aonIconRight());
		TextBox descriptionBox = new TextBox();
		ListBox activityBox = new ListBox();
		ListBox typeBox = new ListBox();
		ListBox regimeBox = new ListBox();
		DoubleBox ivaBox = new DoubleBox();
		DoubleBox retentionBox = new DoubleBox();
		AonDateBox startDateBox = new AonDateBox();
		AonDateBox endDateBox = new AonDateBox();

		ValueChangeHandler<String> valueChangeHandlerString = new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				investAsset.setDescription(descriptionBox.getValue());
				investAsset.setActivity(activityBox.getSelectedIndex() == 0 ? null : new EnterpriseActivity().setId(Integer.parseInt(activityBox.getSelectedValue())));
				investAsset.setType(InvestAssetType.safeValueOf(Integer.parseInt(typeBox.getSelectedValue())));
				investAsset.setRegime(InvestAssetRegime.safeValueOf(Integer.parseInt(regimeBox.getSelectedValue())));
				investAsset.setVatPercent(ivaBox.getValue());
				investAsset.setRetentionPercent(retentionBox.getValue());
				investAsset.setStartDate(startDateBox.getValue());
				investAsset.setEndDate(endDateBox.getValue());
				save(investAsset, msg);
			}
		};
		
		ValueChangeHandler<Date> valueChangeHandlerDate = new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				investAsset.setDescription(descriptionBox.getValue());
				investAsset.setActivity(activityBox.getSelectedIndex() == 0 ? null : new EnterpriseActivity().setId(Integer.parseInt(activityBox.getSelectedValue())));
				investAsset.setType(InvestAssetType.safeValueOf(Integer.parseInt(typeBox.getSelectedValue())));
				investAsset.setRegime(InvestAssetRegime.safeValueOf(Integer.parseInt(regimeBox.getSelectedValue())));
				investAsset.setVatPercent(ivaBox.getValue());
				investAsset.setRetentionPercent(retentionBox.getValue());
				investAsset.setStartDate(startDateBox.getValue());
				investAsset.setEndDate(endDateBox.getValue());
				save(investAsset, msg);
			}
		};
		
		ValueChangeHandler<Double> valueChangeHandlerDouble = new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				investAsset.setDescription(descriptionBox.getValue());
				investAsset.setActivity(activityBox.getSelectedIndex() == 0 ? null : new EnterpriseActivity().setId(Integer.parseInt(activityBox.getSelectedValue())));
				investAsset.setType(InvestAssetType.safeValueOf(Integer.parseInt(typeBox.getSelectedValue())));
				investAsset.setRegime(InvestAssetRegime.safeValueOf(Integer.parseInt(regimeBox.getSelectedValue())));
				investAsset.setVatPercent(ivaBox.getValue());
				investAsset.setRetentionPercent(retentionBox.getValue());
				investAsset.setStartDate(startDateBox.getValue());
				investAsset.setEndDate(endDateBox.getValue());
				save(investAsset, msg);
			}
		};
		
		ChangeHandler changeHandler = new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent arg0) {
				investAsset.setDescription(descriptionBox.getValue());
				investAsset.setActivity(activityBox.getSelectedIndex() == 0 ? null : new EnterpriseActivity().setId(Integer.parseInt(activityBox.getSelectedValue())));
				investAsset.setType(InvestAssetType.safeValueOf(Integer.parseInt(typeBox.getSelectedValue())));
				investAsset.setRegime(InvestAssetRegime.safeValueOf(Integer.parseInt(regimeBox.getSelectedValue())));
				investAsset.setVatPercent(ivaBox.getValue());
				investAsset.setRetentionPercent(retentionBox.getValue());
				investAsset.setStartDate(startDateBox.getValue());
				investAsset.setEndDate(endDateBox.getValue());
				save(investAsset, msg);
			}
		};
		
		sel.addClickHandler(e -> {
			final AonCustomDialog dialog = new AonCustomDialog();
			dialog.setCaption(AON.MSG.investAssetPanel());
			final AonInvestAssetPanel accountPanel = new AonInvestAssetPanel( params.getDomainName(), params.getDomain(), params.getUser(), investAsset.getId(), new AonInvestAssetPanelCallback() {
				
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
			
			dialog.add( accountPanel );
			dialog.showLoaded();
		});
		
		descriptionBox.addValueChangeHandler(valueChangeHandlerString);
		activityBox.addChangeHandler(changeHandler);
		typeBox.addChangeHandler(changeHandler);
		regimeBox.addChangeHandler(changeHandler);
		ivaBox.addValueChangeHandler(valueChangeHandlerDouble);
		retentionBox.addValueChangeHandler(valueChangeHandlerDouble);
		startDateBox.addValueChangeHandler(valueChangeHandlerDate);
		endDateBox.addValueChangeHandler(valueChangeHandlerDate);
		
		msg.setStyleName(AON.CSS.aonTabIcon());
		tab.setWidget(r, col, msg);
		col++;
		
		tab.setWidget(r, col, sel);
		col++;
		
		descriptionBox.setStyleName(AON.CSS.aonBorderNone());
		descriptionBox.addStyleName(AON.CSS.aonWidthAll());
		descriptionBox.setMaxLength(128);
		descriptionBox.setValue(investAsset.getDescription());
		tab.setWidget(r, col, descriptionBox);
		col++;

		activityBox.setStyleName(AON.CSS.aonBorderNone());
		activityBox.addStyleName(AON.CSS.aonWidthAll());
		activityBox.clear();
		activityBox.addItem("-", "");
		activities.forEach(activitiy -> activityBox.addItem(activitiy.getDescription(), activitiy.getId().toString()));
		setSelectedValueLB(activityBox, null != investAsset.getActivity() && null != investAsset.getActivity().getId() ? investAsset.getActivity().getId().toString() : null);
		tab.setWidget(r, col, activityBox);
		col++;
		
		typeBox.setStyleName(AON.CSS.aonBorderNone());
		typeBox.addStyleName(AON.CSS.aonWidthAll());
		typeBox.clear();
		for(int i=0; i < InvestAssetType.values().length; i++)
			typeBox.addItem(InvestAssetType.values()[i].description(), i + "");
		setSelectedValueLB(typeBox, investAsset.getType().ordinal() + "");
		tab.setWidget(r, col, typeBox);
		col++;
		
		regimeBox.setStyleName(AON.CSS.aonBorderNone());
		regimeBox.addStyleName(AON.CSS.aonWidthAll());
		regimeBox.clear();
		for(int i=0; i < InvestAssetRegime.values().length; i++)
			regimeBox.addItem(InvestAssetRegime.values()[i].description(), i + "");
		setSelectedValueLB(regimeBox, investAsset.getRegime().ordinal() + "");
		tab.setWidget(r, col, regimeBox);
		col++;

		ivaBox.setStyleName(AON.CSS.aonBorderNone());
		ivaBox.addStyleName(AON.CSS.aonWidthAll());
		ivaBox.setValue(investAsset.getVatPercent());
		tab.setWidget(r, col, ivaBox);
		col++;
		
		retentionBox.setStyleName(AON.CSS.aonBorderNone());
		retentionBox.addStyleName(AON.CSS.aonWidthAll());
		retentionBox.setValue(investAsset.getRetentionPercent());
		tab.setWidget(r, col, retentionBox);
		col++;
		
		startDateBox.setStyleName(AON.CSS.aonBorderNone());
		startDateBox.addStyleName(AON.CSS.aonWidthAll());
		startDateBox.setValue(investAsset.getStartDate());
		tab.setWidget(r, col, startDateBox);
		col++;
		
		endDateBox.setStyleName(AON.CSS.aonBorderNone());
		endDateBox.addStyleName(AON.CSS.aonWidthAll());
		endDateBox.setValue(investAsset.getEndDate());
		tab.setWidget(r, col, endDateBox);
		col++;
		
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		
		AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
		deleteButton.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				deleteButton.setEnabled(false);
				AonDialog dialog = new AonDialog("Eliminaci\u00f3n Bien Afecto o de Inversion",
						new HTML("Se va a proceder a eliminar el Bien Afecto o de Inversion <b>" + investAsset.getDescription() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
				
				dialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						deleteButton.setEnabled(true);
					}

					@Override
					public void onAccept() {
						delete(investAsset);
					}
				});
			}
		});
		
		buttonContainer.add(deleteButton);
		tab.setWidget(r, col, buttonContainer);
		col++;
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
	
	private void getList(Consumer<List<InvestAsset>> success) {
		COMMON_SERVICE.getInvestAssets(params, new AsyncCallback<List<InvestAsset>>() {
			
			@Override
			public void onSuccess(List<InvestAsset> investAssets) {
				success.accept(investAssets);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
	private void delete(InvestAsset investAsset) {
		COMMON_SERVICE.deleteInvestAsset(params.getDomainName(), params.getDomain(), params.getUser(), investAsset.getId(), new AsyncCallback<Void>() {
			
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
	
	private void save(InvestAsset investAsset, Label msg) {
		COMMON_SERVICE.saveInvestAsset(params.getDomainName(), params.getDomain(), params.getUser(), investAsset, new AsyncCallback<InvestAsset>() {
			
			@Override
			public void onSuccess(InvestAsset result) {
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
	
	private void getActivities(Consumer<List<Activity>> success) {
		COMMON_SERVICE.getActivities(params.getDomainName(), params.getDomain(), params.getUser(), new AsyncCallback<List<Activity>>() {
			
			@Override
			public void onSuccess(List<Activity> activities) {
				success.accept(activities);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
	private void error(String message) {
		AonDialog dialog = new AonDialog("Error", new HTML(message));
		dialog.info();
	}
	
}

