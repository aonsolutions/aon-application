package com.esferalia.aon.gwt.fiscal.client.matrix;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

public class ModelMatrix extends MainEntryPoint {

	@FunctionalInterface
	private interface IPeriodTypeAccepter {
		boolean accept(Period mod);
	}
	
	private enum PeriodType {
		 MONTHLY	("Mensual",12,period -> period.isMonthPeriod())
		,QUARTERLY	("Trimes.", 4,period -> period.isQuarterPeriod())
		,YEARLY		("Anual", 1,period -> period == Period.YEAR)
		;
		private String value;
		private int arraySize;
		private IPeriodTypeAccepter accepter;
		private PeriodType( String value, int arraySize, IPeriodTypeAccepter accepter) {
			this.value = value;
			this.arraySize = arraySize;
			this.accepter = accepter;
		}
		private String getValue() {
			return value;
		}
		private int getArraySize() {
			return arraySize;
		}
		private int getIndex(Period period) {
			if (period == Period.YEAR) return 0;
			if (period.isMonthPeriod()) return period.ordinal();
			return (period.ordinal() - 12);
		}
		private static PeriodType getPeriodType(Period period) {
			for (PeriodType type : PeriodType.values()) {
				if (type.accepter.accept(period)) return type;
			}
			return null;
		}
	}

	private class FilterPanel extends FlowPanel implements HasValueChangeHandlers<FiscalMatrixParams>, Focusable {
		private ListBox year;
		private ListBox model;
		private ListBox admon;
		private CheckBox showConfigurated;
		private CheckBox showMadeModels;
		private CheckBox testMode;
		
		private FilterPanel(AonData aonData) {
			setStyleName(AON.AON_CSS.aonSimpleBorder());
			addStyleName(AON.AON_CSS.aonWidth98Percent());
			addStyleName(AON.AON_CSS.aonBlockCenter());
			addStyleName(AON.AON_CSS.aonPadding());

			InlineLabel yearLabel = new InlineLabel(AON.MSG.fiscalYear());
			yearLabel.setStyleName(AON.AON_CSS.aonMarginRight());
			add(yearLabel);
			year = new ListBox();
			year.setStyleName(AON.AON_CSS.aonMarginRight());
			for (int i = 2012; i < 2025; i++) {
				String y = AonNumberUtils.toString(i);
				year.addItem(y, y);
				if (i == AonDateUtils.getCurrentYear()) {
					year.setSelectedIndex(year.getItemCount() - 1);	
				}
			}
			add(year);
			year.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					fireValueChangeEvent();
				}
			});
			
			InlineLabel modelLabel = new InlineLabel(AON.MSG.fiscalModels());
			modelLabel.setStyleName(AON.AON_CSS.aonMarginRight());
			add(modelLabel);
			model = new ListBox();
			model.setStyleName(AON.AON_CSS.aonMarginRight());
			model.addItem(" TODOS ", "");
			for (FiscalModelType m : FiscalModelType.values()) {
				model.addItem(m.getName(), m.getValue());
			}
			add(model);
			model.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					fireValueChangeEvent();
				}
			});

			InlineLabel admonLabel = new InlineLabel(AON.MSG.administration());
			admonLabel.setStyleName(AON.AON_CSS.aonMarginRight());
			add(admonLabel);
			admon = new ListBox();
			admon.setStyleName(AON.AON_CSS.aonMarginRight());
			admon.addItem(" TODAS ", "");
			for (Administration a : Administration.values()) {
				admon.addItem(a.getDescription());
			}
			add(admon);
			
			admon.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					fireValueChangeEvent();
				}
			});
			
			showConfigurated = new CheckBox();
			showConfigurated.setValue(false);
			showConfigurated.setStyleName(AON.AON_CSS.aonMarginRight());
			showConfigurated.setText("Mostrar los configurados en par\u00E1metros fiscales");
			showConfigurated.addClickHandler( new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					fireValueChangeEvent();
				}
			});
			add(showConfigurated);
			
			showMadeModels = new CheckBox();
			showMadeModels.setValue(true);
			showMadeModels.setStyleName(AON.AON_CSS.aonMarginRight());
			showMadeModels.setText("Mostrar los realizados");
			showMadeModels.addClickHandler( new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					fireValueChangeEvent();
				}
			});
			add(showMadeModels);
			
			if(aonData.isBetaEnabled()) {
				testMode = new CheckBox();
				testMode.setValue(false);
				testMode.setStyleName(AON.AON_CSS.aonMarginRight());
				testMode.setText("Modo Pruebas");
				testMode.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						getNeo().setTest(event.getValue());
					}
				});
				
				add(testMode);
			}

		}

		protected void fireValueChangeEvent() {
			int y = AonNumberUtils.toint(year.getSelectedValue());
			FiscalMatrixParams params = new FiscalMatrixParams();
			Administration administration = null;
			if ( admon.getSelectedIndex() > 0) {
				administration = Administration.values()[admon.getSelectedIndex() - 1];
			}
			params.setYear(y)
				.setModel(model.getSelectedValue())
				.setAdministration(administration)
				.setConfiguredVisible(showConfigurated.getValue())
				.setMadeModelsVisible(showMadeModels.getValue())
				;
			
			ValueChangeEvent.fire(FilterPanel.this, params);
		}

		@Override
		public int getTabIndex() {
			return year.getTabIndex();
		}

		@Override
		public void setAccessKey(char key) {
		}

		@Override
		public void setFocus(boolean focused) {
			year.setFocus(focused);
		}

		@Override
		public void setTabIndex(int index) {
			year.setTabIndex(index);
		}

		@Override
		public HandlerRegistration addValueChangeHandler(ValueChangeHandler<FiscalMatrixParams> handler) {
			return super.addHandler(handler, ValueChangeEvent.getType());
		}
	}

	protected static FiscalModelServiceAsync SERVICE;
	final FiscalServiceAsync impl = GWT.create(FiscalService.class);

	private NeoMatrix neo;
	private AonData aonData;
	
	private NeoMatrix getNeo() {
		return neo;
	}
	
	private AonData getAonData() {
		return aonData;
	}

	@Override
	public void onModuleLoad() {
		impl.getAonData(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonData>() {

			@Override public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(AonData aonData) {
				onModuleLoad(aonData);
			}
		});
	}
	
	public void onModuleLoad(AonData aonData) {
		AON.ensureInjected();
		
		FiscalModelServiceAsync serviceRaw = GWT.create(FiscalModelService.class);
		SERVICE = new FiscalModelServiceAsyncDecorator(serviceRaw);

		DockLayoutPanel dockLayout = new DockLayoutPanel(Unit.PX);
		FilterPanel filterPanel = new FilterPanel(aonData);
		dockLayout.addNorth(filterPanel, 50);
		SplitLayoutPanel splitLayout = new SplitLayoutPanel();

		// TODO Aqui añadir la nueva pantalla;
		//************************************************//
		this.neo = new NeoMatrix(aonData) {
					
			@Override
			protected void onOpenPanel() {
				Integer clientHeight = Window.getClientHeight();
				splitLayout.setWidgetSize(getNeo(), clientHeight.doubleValue() / 3);	
				splitLayout.animate(500);
			}
					
			@Override
			protected void onClosePanel() {
				splitLayout.setWidgetSize(getNeo(), 30);	
				splitLayout.animate(500);				
			}

			@Override
			protected void onParentLoad() {
				filterPanel.fireValueChangeEvent();				
			}
		};

		splitLayout.addSouth(neo, 30);

		ScrollPanel scrollPanel = new ScrollPanel();
		splitLayout.add(scrollPanel);
		//************************************************//
		
		dockLayout.add(splitLayout);
		
		filterPanel.addValueChangeHandler( new ValueChangeHandler<FiscalMatrixParams>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<FiscalMatrixParams> event) {
				SERVICE.getFiscalPanel(getCurrentDomainName(), getCurrentUser(), getCurrentDomain(), event.getValue(), 
						new AsyncCallback<LinkedList<IFiscalModel>>() {
							
							@Override
							public void onSuccess(LinkedList<IFiscalModel> matrix) {
								scrollPanel.setWidget( paint(matrix,event.getValue()));
							}
							
							@Override
							public void onFailure(Throwable caught) {
								
							}
						});
			}
		});
		
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(dockLayout);
		
		filterPanel.fireValueChangeEvent();
	}

	private FlowPanel paint(LinkedList<IFiscalModel> matrix, FiscalMatrixParams fiscalMatrixParams) {
		FlowPanel content = new FlowPanel();
		content.setStyleName(AON.AON_CSS.aonWidth98Percent());
		content.addStyleName(AON.AON_CSS.aonBlockCenter());

		if (matrix == null || matrix.isEmpty()) {
			Label noData = new Label(AON.MSG.noData());
			noData.setStyleName(AON.AON_CSS.aonTextCenter());
			noData.addStyleName(AON.AON_CSS.aonMarginTop());
			noData.addStyleName(AON.AON_CSS.aonPadding());
			noData.addStyleName(AON.AON_CSS.aonTextBoxError());
			noData.addStyleName(AON.AON_CSS.aonColorRed());
			noData.addStyleName(AON.AON_CSS.aonBold());
			content.add(noData);
			return content;
		}
		
		
		FlexTable tab = new FlexTable();
		tab.setCellSpacing(1);
		tab.addStyleName(AON.AON_CSS.aonMarginTop());
		tab.addStyleName(AON.AON_CSS.aonBlockCenter());
		
		int col = 0;
		
		tab.getColumnFormatter().setWidth(col++, "450px");
		tab.getColumnFormatter().setWidth(col++, "50px");
		tab.getColumnFormatter().setWidth(col++, "25px");
		tab.getColumnFormatter().setWidth(col++, "70px");
		
		int modelOffset = col;
		tab.getColumnFormatter().setWidth(col++, "30px");
		tab.getColumnFormatter().setWidth(col++, "30px");
		tab.getColumnFormatter().setWidth(col++, "30px");
		tab.getColumnFormatter().setWidth(col++, "30px");
		tab.getColumnFormatter().setWidth(col++, "30px");
		tab.getColumnFormatter().setWidth(col++, "30px");
		tab.getColumnFormatter().setWidth(col++, "30px");
		tab.getColumnFormatter().setWidth(col++, "30px");
		tab.getColumnFormatter().setWidth(col++, "30px");
		tab.getColumnFormatter().setWidth(col++, "30px");
		tab.getColumnFormatter().setWidth(col++, "30px");
		tab.getColumnFormatter().setWidth(col++, "30px");

		int colspan = col;
		int documentCol = 0;
		int modelCol = 1;
		int admonCol = 2;
		int periodCol = 3;
		
		int row = 0;
		col = modelOffset;
		String[] months = new String[]{"E","F","M","A","M","J","X","A","S","O","N","D"};
		for (String month : months) {
			tab.setWidget(row, col, new Label( month ));
			tab.getCellFormatter().setStyleName(row, col  , AON.AON_CSS.aonSimpleBorder());
			tab.getCellFormatter().addStyleName(row, col  , AON.AON_CSS.aonTextCenter());
			tab.getCellFormatter().addStyleName(row, col++, AON.AON_CSS.aonBold());
		}
		
		row++;
		
		LinkedHashMap<String, LinkedHashMap<Administration, LinkedHashMap<PeriodType, LinkedHashMap<String, LinkedHashMap<String, LinkedList<IFiscalModel>>>>>> domainMap = sortInfo(matrix);
		for (String domainName : domainMap.keySet()) {
			 
			Label emptyLabel = new Label();
			emptyLabel.setStyleName(AON.AON_CSS.aonMarginTop());
			tab.setWidget(row, 0, emptyLabel);
			tab.getFlexCellFormatter().setColSpan(row, 0, colspan);
			row++;

			Label domainLabel = new Label(domainName);
			tab.getCellFormatter().setStyleName(row, 0 , AON.AON_CSS.aonBold());
			tab.getCellFormatter().addStyleName(row, 0 , AON.AON_CSS.aonSimpleBorder());
			tab.getCellFormatter().addStyleName(row, 0 , AON.AON_CSS.aonTextCenter());
			tab.setWidget(row, 0, domainLabel);
			tab.getFlexCellFormatter().setColSpan(row, 0, colspan);
			row++;

			LinkedHashMap<Administration, LinkedHashMap<PeriodType, LinkedHashMap<String, LinkedHashMap<String, LinkedList<IFiscalModel>>>>> admonMap = domainMap.get(domainName);
			for (Administration admon : admonMap.keySet()) {
				LinkedHashMap<PeriodType, LinkedHashMap<String, LinkedHashMap<String, LinkedList<IFiscalModel>>>> periodMap = admonMap.get(admon);
				for (PeriodType type : periodMap.keySet()) {
					LinkedHashMap<String, LinkedHashMap<String, LinkedList<IFiscalModel>>> modelMap = periodMap.get(type);
					for (String modelName : modelMap.keySet()) {
						LinkedHashMap<String, LinkedList<IFiscalModel>> documentMap = modelMap.get(modelName);
						for (String document : documentMap.keySet()) {
							Label admonLabel = new Label();
							if (admon == Administration.UNKNOWN) {
								admonLabel.setText("\u2022");
							} else {
								admonLabel.setStyleName(FiscalModelUtils.getAdministrationIcon(admon));	
							}
							tab.getCellFormatter().setStyleName(row, admonCol, AON.AON_CSS.aonSimpleBorder());
							tab.getCellFormatter().addStyleName(row, admonCol, AON.AON_CSS.aonTextCenter());
							tab.getCellFormatter().addStyleName(row, admonCol, AON.AON_CSS.aonPadding2Left());
							tab.setWidget(row, admonCol, admonLabel);
							
							Label periodLabel = new Label(type.getValue());
							tab.getCellFormatter().setStyleName(row, periodCol, AON.AON_CSS.aonSimpleBorder());
							tab.getCellFormatter().addStyleName(row, periodCol, AON.AON_CSS.aonTextCenter());
							tab.setWidget(row, periodCol, periodLabel);
							
							tab.getCellFormatter().setStyleName(row, modelCol, AON.AON_CSS.aonBold());
							tab.getCellFormatter().addStyleName(row, modelCol, AON.AON_CSS.aonSimpleBorder());
							tab.getCellFormatter().addStyleName(row, modelCol, AON.AON_CSS.aonTextCenter());
							tab.setWidget(row, modelCol, new Label( modelName ));
							
							tab.getCellFormatter().setStyleName(row, documentCol, AON.AON_CSS.aonSimpleBorder());
							tab.getCellFormatter().addStyleName(row, documentCol, AON.AON_CSS.aonFontMedium());
							tab.setWidget(row, documentCol, new Label( document ));

							int colspan2 = 12 / (type.getArraySize());
							for (int x = 0; x < type.getArraySize(); x++) {
								int c = x + modelOffset;
								tab.getFlexCellFormatter().setColSpan(row, c, colspan2);
								tab.getCellFormatter().setStyleName(row, c  , AON.AON_CSS.aonSimpleBorder());
								tab.getCellFormatter().addStyleName(row, c, AON.AON_CSS.aonTextCenter());
								tab.getCellFormatter().addStyleName(row, c, FiscalModelUtils.gettStatusBckColor( FiscalStatus.MISSING));
								tab.setWidget(row, c, new InlineLabel());
							}
							
							LinkedList<IFiscalModel> models = documentMap.get(document);
							for (IFiscalModel model : models) {
								if (model != null) {
									col = (model.getPeriod().isQuarterPeriod()?(model.getPeriod().getStartMonth()/3):model.getPeriod().getStartMonth()) + modelOffset;
									FocusPanel focusPanel = new FocusPanel();
									Label mod = new Label();
									mod.setText("\u2022");
									if (model.getStatus() != FiscalStatus.MISSING) {
//										mod.setTitle(model.getStatus().getName() + ". Click para detalles.");
										mod.setStyleName(AON.AON_CSS.aonClickable());
										mod.addStyleName(AON.AON_CSS.aonTextCenter());
										
										// TODO AQUI ESTA LA SELECCION DEL MODELO!
										//************************************************//
										focusPanel.addClickHandler( new ClickHandler() {
											
											@Override
											public void onClick(ClickEvent event) {
												getNeo().onOpenPanel();
												if(getNeo().hasModel(model)) 
													getNeo().removeModel(model);
												else getNeo().addModel(model);
											}
											
										});
										//************************************************//
									}
									focusPanel.add(mod);
									tab.getCellFormatter().addStyleName(row, col, FiscalModelUtils.gettStatusBckColor(model.getStatus()));
									tab.setWidget(row, col, focusPanel);
								}
							}
						}
						row++;
					}
					
				}
			}
		}
		content.add(tab);
		return content;
	}
	
	private LinkedHashMap<String, LinkedHashMap<Administration, LinkedHashMap<PeriodType, LinkedHashMap<String, LinkedHashMap<String, LinkedList<IFiscalModel>>>>>>
		sortInfo(LinkedList<IFiscalModel> matrix) {

		
		LinkedHashMap<String										// domainMap
			, LinkedHashMap<Administration							// admonMap
				, LinkedHashMap<PeriodType							// periodMap
					, LinkedHashMap<String							// modelMap
						, LinkedHashMap<String						// documentMap
							, LinkedList<IFiscalModel>>>>>>  	// models
		
		domainMap = new LinkedHashMap<String, LinkedHashMap<Administration, LinkedHashMap<PeriodType, LinkedHashMap<String, LinkedHashMap<String, LinkedList<IFiscalModel>>>>>>();
		
		for (IFiscalModel mod : matrix ) {
			String domainName = mod.getDomainName();
			
			String d = mod.getDocument();
			String n = mod.getName();
			String s = mod.getSurname();
			String name = AonStringUtils.join(new String[]{s,n}, AonStringUtils.isBlank(s)?"":", ");
			name = AonStringUtils.abbreviate(AonStringUtils.join(new String[]{d,name}, " "), 50);
			
			String modelName = FiscalModelUtils.getModelName(mod);
			
			Administration admon = mod.getAdministration();
			if (admon == null) admon = Administration.UNKNOWN;
			PeriodType type = PeriodType.getPeriodType(mod.getPeriod());
			
			LinkedHashMap<Administration, LinkedHashMap<PeriodType, LinkedHashMap<String, LinkedHashMap<String, LinkedList<IFiscalModel>>>>> admonMap = domainMap.get(domainName);
			if (admonMap == null) {
				admonMap = new LinkedHashMap<Administration, LinkedHashMap<PeriodType, LinkedHashMap<String, LinkedHashMap<String, LinkedList<IFiscalModel>>>>>();
				domainMap.put(domainName,admonMap);	
			}
			
			LinkedHashMap<PeriodType, LinkedHashMap<String, LinkedHashMap<String, LinkedList<IFiscalModel>>>> periodMap = admonMap.get(admon);
			if (periodMap == null) {
				periodMap = new LinkedHashMap<PeriodType, LinkedHashMap<String, LinkedHashMap<String, LinkedList<IFiscalModel>>>>();
				admonMap.put(admon,periodMap);	
			}
			
			LinkedHashMap<String, LinkedHashMap<String, LinkedList<IFiscalModel>>> modelMap = periodMap.get(type);
			if (modelMap == null) {
				modelMap = new LinkedHashMap<String, LinkedHashMap<String, LinkedList<IFiscalModel>>>();
				periodMap.put(type,modelMap);	
			}
			
			LinkedHashMap<String, LinkedList<IFiscalModel>> documentMap = modelMap.get(modelName);
			if (documentMap == null) {
				documentMap = new LinkedHashMap<String, LinkedList<IFiscalModel>>();
				modelMap.put(modelName,documentMap);	
			}

			LinkedList<IFiscalModel> models = documentMap.get(name);
			if (models == null) {
				models = new LinkedList<IFiscalModel>();
				for (int i = 0; i < type.getArraySize(); i++) {
					models.add(null);
				}
				documentMap.put(name, models);				
			}
			models.set(type.getIndex(mod.getPeriod()), mod);
		}
		return domainMap;
	}
}
