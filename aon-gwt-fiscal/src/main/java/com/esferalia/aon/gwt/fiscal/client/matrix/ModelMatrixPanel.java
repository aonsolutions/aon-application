package com.esferalia.aon.gwt.fiscal.client.matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.fiscal.JsFiscalMenuItem;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableCell;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class ModelMatrixPanel extends FlowPanel {

	private static final Logger LOGGER = Logger.getLogger(ModelMatrixPanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	private static final String LEYENDA = "Leyenda";
	private static final String TABLE_LAYOUT = "table-layout";
	private static final String FIXED = "fixed";
	
	private static final String[] MONTHS = new String[]{"ENE","FEB","MAR","ABR","MAY","JUN","JUL","AGO","SEP","OCT","NOV","DIC"};
	private static final String[] QUARS = new String[]{"1\u00BA TRIM","2\u00BA TRIM","3\u00BA TRIM","4\u00BA TRIM"};
	
	private FiscalMatrixParams params;
	private AonSearchPanelButton refreshButton;
	private boolean isBetaEnabled; // FALTA - POR AHORA PARA PODER MOSTRAR DETERMINADOS DATOS SI EL DOMINIO ES BETA
	
	private ArrayList<String> selected = new ArrayList<>();
	private ArrayList<CheckBox> selectedCheckBox = new ArrayList<>();
	private CheckBox markAllForSend;
	
	public ModelMatrixPanel(MatrixModuleOptions options, FiscalMatrixParams params, AonSearchPanelButton refreshButton) {
		
		this.params = params;
		this.refreshButton = refreshButton;
		this.isBetaEnabled = options.getConfiguration().isBetaEnabled();
		
		final PopupPanel pop = new PopupPanel(false, true);
		if (!options.isCompactMode()) {
			pop.add(new AonSplash());
			pop.setGlassEnabled(true);
			pop.setAnimationEnabled(true);
			pop.center();
		}
		
		API api = new API(GWT.getHostPageBaseURL(), 
				options.getConfiguration().getMd5(),
				options.getConfiguration().getDomain().getName(), 
				options.getConfiguration().getDomain().getId(),
				options.getConfiguration().getUser().getLogin());
		HashMap<String, LinkedList<String>> filterMap = new HashMap<>();
		LinkedList<String> yearList = new LinkedList<>();
		yearList.add(AonNumberUtils.toString(params.getYear()) );
		filterMap.put(IJsonNames.YEAR, yearList);
		LinkedList<String> modelList = new LinkedList<>();
		modelList.add(params.getModel() == null ? "" : params.getModel().toString());
		filterMap.put(IJsonNames.MODEL, modelList);
		LinkedList<String> admonList = new LinkedList<>();
		admonList.add(params.getAdministration()==null?"":params.getAdministration().toString());
		filterMap.put(IJsonNames.ADMINISTRATION, admonList);
		LinkedList<String> scopeList = new LinkedList<>();
		scopeList.add(AonNumberUtils.toString(params.getScope()) );
		filterMap.put(IJsonNames.SCOPE, scopeList);
		LinkedList<String> configuredVisibleList = new LinkedList<>();
		configuredVisibleList.add( params.isConfiguredVisible()?Boolean.TRUE.toString() : Boolean.FALSE.toString() );
		filterMap.put(IJsonNames.CONFIGURED_VISIBLE, configuredVisibleList);
		LinkedList<String> madeModelsVisibleList = new LinkedList<>();
		madeModelsVisibleList.add( params.isMadeModelsVisible()?Boolean.TRUE.toString() : Boolean.FALSE.toString() );
		filterMap.put(IJsonNames.MADE_MODELS_VISIBLE, madeModelsVisibleList);
		LinkedList<String> nameList = new LinkedList<>();
		nameList.add(AonStringUtils.defaultIfBlank(params.getDeclared()));
		filterMap.put(IJsonNames.NAME, nameList);
		LinkedList<String> statusList = new LinkedList<>();
		statusList.add(params.getStatus()==null?"":params.getStatus().toString());
		filterMap.put(IJsonNames.STATUS, statusList);
		LinkedList<String> periodList = new LinkedList<>();
		periodList.add(params.getPeriod()==null?"":params.getPeriod().getName());
		filterMap.put(IJsonNames.PERIOD, periodList);

		api.getFiscal().getFiscalModels( filterMap, new AsyncCallback<JSON<JsFiscalMenuItem>>() {
			
			@Override
			public void onSuccess(JSON<JsFiscalMenuItem> result) {
				AonJsArray<JsFiscalMenuItem> aonJsArray = result.getData();
				if (aonJsArray == null || aonJsArray.length() == 0) {
					FlowPanel content = new FlowPanel();
					content.setStyleName(AON.CSS.aonMarginRight());
					content.addStyleName(AON.CSS.aonMarginLeft());
					content.addStyleName(AON.CSS.aonBlockCenter());
					
					Label noData = new Label(AON.MSG.noData());
					noData.setStyleName(AON.CSS.aonTextCenter());
					noData.addStyleName(AON.CSS.aonMarginTop());
					noData.addStyleName(AON.CSS.aonPadding());
					noData.addStyleName(AON.CSS.aonColorRed());
					noData.addStyleName(AON.CSS.aonBold());
					content.add(noData);
					ModelMatrixPanel.this.add( content );
				} else {
					MatrixData matrixData = sortInfo(aonJsArray);
					ModelMatrixPanel.this.paint( options, matrixData, params);
				}
				if (!options.isCompactMode()) {
					pop.hide();
				}
				// Dejar como seleccionados solo los que están visibles en este momento en pantalla
				options.getSelected().clear();
				selected.forEach(s -> options.getSelected().add(s));
				selected.clear();		
			}
			
			@Override
			public void onFailure(Throwable caught) {
				if (!options.isCompactMode()) {
					pop.hide();
				}
			}
		});
	}

//	public ModelMatrixPanel(MatrixModuleOptions options, MatrixData matrixData, FiscalMatrixParams params) {
//		super();
//		paint( options, matrixData, params);
//	}
	
	private void paint(MatrixModuleOptions options, MatrixData matrixData, FiscalMatrixParams params) {
		setStyleName(AON.CSS.aonMarginRight());
		addStyleName(AON.CSS.aonMarginTop());
		addStyleName(AON.CSS.aonMarginLeft());
		addStyleName(AON.CSS.aonBlockCenter());
		
		AonDisplayTable table = new AonDisplayTable();
		table.addStyleName(AON.CSS.aonBlockCenter());
		table.addStyleName(AON.CSS.aonWidthAlmostAll());
		
		AonDisplayTableRow row = table.addRow( );
		if (!options.isCompactMode()) {
			row.addCell( getHelpButton(), AON.CSS.aonFlexGrow1());
		}
		row.addCell( getEmptyLabel( "50px"))
			.addCell( getEmptyLabel( "25px"))
			.addCell( getEmptyLabel( "70px"))
			.addCell( getMonthsTable(), AON. CSS.aonWidth400());
		
		row = table.addRow( );
		if (!options.isCompactMode()) {
			row.addCell( getEmptyLabel());
		}
		row.addCell( getEmptyLabel())
			.addCell( getEmptyLabel())
			.addCell( getEmptyLabel());
		
		if (params.getPeriod() == null || params.getPeriod().isQuarterPeriod()) {
			row.addCell(getQuarsTable());
		}
		else if (params.getPeriod() == Period.YEAR) {
			row.addCell(getYearlyTable());
		}
			
		matrixData.getDomains().stream().forEach( domKey -> {
			String domainId = AonStringUtils.substringBefore(domKey, AonStringUtils.PIPE);
			String domainName = AonStringUtils.substringAfter(domKey, AonStringUtils.PIPE);
			if (!options.isCompactMode()) {
				paintDomainRowIfNeeded( params.isFiscalModelTypePresent(),table,domainName );
			}
			for (Administration admKey : matrixData.getAdministrations(domKey)) {
				for (MatrixPeriodType perKey : matrixData.getPeriodTypes(domKey, admKey)) {
					for (String modKey : matrixData.getModels(domKey, admKey, perKey)) {
						for (String docKey : matrixData.getDocs(domKey, admKey, perKey, modKey)) {
							FiscalModel fm = new FiscalModel();
							fm.setYear(params.getYear());
							fm.setAdministration(admKey);
							fm.setModel(FiscalModelType.valueOf(modKey));
							fm.setPeriod( perKey.getInitialPeriod() );
							fm.setDomain(AonNumberUtils.toint(domainId));
							fm.setDomainName(domainName);
							fm.setDocument(domKey);
							List<JsFiscalMenuItem> items = matrixData.getItems(domKey, admKey, perKey, modKey, docKey);
							if ( items.isEmpty() ) {
								fm.setName(domainName);	
							} else {
								JsFiscalMenuItem firstItem = null;
								for (JsFiscalMenuItem it : items) {
									if (it != null) {
										//LOGGER.info("it = " + it.getModel() + " - " + it.getDeclarationResultType() + " - " +  it.getIban() + " - " +  it.getNrc());
										firstItem = it;
										break;
									}
								}
								String n = firstItem.getName();
								String s = firstItem.getSurname();
								String name = AonStringUtils.join(new String[]{s,n}, AonStringUtils.isBlank(s)?"":", ");
								fm.setName((firstItem == null)?domainName:name);	
							}
							
							AonDisplayTable periodTable = paintFiscalModelRow(options,fm,admKey,perKey,docKey,table,params,domainName);
							fillPeriodTable(options, fm, periodTable, items );
						}
					}
				}
			}
		});
		add(table);
		
	}
	
	private MatrixData sortInfo( AonJsArray<JsFiscalMenuItem> aonJsArray) {
		MatrixData matrixData = new MatrixData();
		aonJsArray.stream().forEach( matrixData::add );
		return matrixData;
	}

	private void fillPeriodTable(MatrixModuleOptions options, FiscalModel fm, AonDisplayTable periodTable, List<JsFiscalMenuItem> items) {
		for (JsFiscalMenuItem model : items) {
			if (model != null ) {
//				LOGGER.info("model = " + model.getModel() + " - " + model.getDeclarationResultType() + " - " +  model.getIban() + " - " +  model.getNrc());
				FiscalStatus status = FiscalStatus.safeValueOf( model.getStatus() );
				if (status != FiscalStatus.MISSING) {
					Period period = Period.valueOf(model.getPeriod());					
					int col = params.getPeriod() == null ? (period.isQuarterPeriod()?(period.getStartMonth()/3):period.getStartMonth()) : 0;
					AonDisplayTableRow row = (AonDisplayTableRow) periodTable.getWidget(0);
					AonDisplayTableCell cell = (AonDisplayTableCell) row.getWidget(col);
					FiscalModel cloned = cloneModel(fm);
					if (model.getId() != null) {
						cloned.setId(Integer.valueOf(model.getId() + ""));
					} else {
						cloned.setId(null);
					}
					cloned.setStatus(status);
					cloned.setPeriod(period);					
					cloned.setDeclarationResult(model.getResult());
					cloned.setDeclarationResultType(FiscalModelDeclarationType.safeNameOf(model.getDeclarationResultType()));
					cloned.setIban(model.getIban());
					cloned.setNrc(model.getNrc());
					
					paintViewModelCell(options, cell, cloned );
					
					// Si se está filtrando por solo un periodo, añadir celdas con Resultado, Tipo Declaración, IBAN/NRC y check para marcar (si está habilitada la presentación).
					// FALTA - POR AHORA SOLO PARA DOMINIOS BETA
					if (params.getPeriod() != null && options.getConfiguration().isBetaEnabled()) {
						boolean checkBoxEnabled = true;
						// Resultado, Tipo, IBAN/NRC, solo si no son anuales
						if (params.getPeriod() != Period.YEAR) {
							Label ibanNrcLabel = new Label();
							if (cloned.getDeclarationResultType() != null) {
								if (cloned.getDeclarationResultType() == FiscalModelDeclarationType.DEPOSIT) {
									ibanNrcLabel.setText(cloned.getNrc());
								} else {									
									ibanNrcLabel.setText(cloned.getIban());
								}								
								// Si está habilitada la presentación múltiple, comprobar si tiene IBAN o NRC en aquellos modelos que deberían tenerlo
								if (params.isMultiplePresentation()) {
									if (AonStringUtils.isBlank(ibanNrcLabel.getText())) {
										if (cloned.getDeclarationResultType() == FiscalModelDeclarationType.DEPOSIT) {
											ibanNrcLabel.setText("FALTA NRC");
											ibanNrcLabel.addStyleName(AON.CSS.aonColorRed());
											checkBoxEnabled = false;
										} else if (cloned.getDeclarationResultType() == FiscalModelDeclarationType.BANK || cloned.getDeclarationResultType() == FiscalModelDeclarationType.PAYBACK) {
											ibanNrcLabel.setText("FALTA IBAN");
											ibanNrcLabel.addStyleName(AON.CSS.aonColorRed());
											checkBoxEnabled = false;
										}
									}									 
								}
							}	
							row.addCell( new Label( cloned.getDeclarationResult() == null ? "" : AON.FMT.format(cloned.getDeclarationResult()) ), AON.CSS.aonBorderBottom(), AON.CSS.aonTextRight(), AON.CSS.aonWidth80(), AON.CSS.aonPaddingRight() )
							   .addCell( new Label( cloned.getDeclarationResultType() == null ? "" : cloned.getDeclarationResultType().getDescription() ), AON.CSS.aonBorderBottom(), AON.CSS.aonTextLeft(), AON.CSS.aonWidth170())
							   .addCell( ibanNrcLabel, AON.CSS.aonBorderBottom(), AON.CSS.aonTextLeft(), AON.CSS.aonWidth170() );
						}
						
						// Si está habilitada la presentacion múltiple, se añade un checkbox para poder seleccionar la fila
						if (params.isMultiplePresentation()) {
							CheckBox markForSend = new CheckBox();
							markForSend.setValue(options.isSelected(cloned));
							markForSend.setEnabled(checkBoxEnabled);
							if (options.isSelected(cloned) && markForSend.isEnabled()) {								
								selected.add(options.getSelectedKey(cloned));
							} else {
								markAllForSend.setValue(false,false);
							}
							markForSend.addValueChangeHandler( event -> {
								if (markForSend.getValue()) {
									options.addSelected(cloned);
									
									int totalEnabled = 0;									
									for (CheckBox cb : selectedCheckBox) {
										if (cb.isEnabled()) 
											totalEnabled++;										
									}									
									if (options.getSelected().size() == totalEnabled)
										markAllForSend.setValue(true,false);										
								} else {
									options.removeSelected(cloned);
									markAllForSend.setValue(false,false);
								}
							});							
							row.addCell(markForSend, AON.CSS.aonBorderBottom(), AON.CSS.aonTextCenter(), AON.CSS.aonWidth60());
							selectedCheckBox.add(markForSend);
						}						
					}
					
				}
			}
		}
	}

	private void paintNewModelCell(MatrixModuleOptions options, AonDisplayTableCell cell, IFiscalModel model) {
		cell.clear();
		cell.addStyleName( AON.CSS.aonBorderBottom() );
		cell.addStyleName( AON.CSS.aonTextCenter() );
		cell.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB( FiscalStatus.MISSING ));
		if (params.getStatus() == null && params.getPeriod() == null) {
			AonTableButton addButton = new AonTableButton(AON.MSG.newAction(), AON.CSS.aonIconAdd());
			cell.add(addButton);		
			addButton.addClickHandler(event -> model.getModel().visit(new MatrixNewModelVisitor(options.getConfiguration(),model
					, new AonModuleCallback<IFiscalModel>() {
	
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onChange(IFiscalModel changed) {
					LOGGER.info("Change " + changed.getStatus().getName());
					refresh( changed );
				}
		
				@Override
				public void onRemove(IFiscalModel removed) {
					LOGGER.info("Remove " + removed.getStatus().getName());
					refresh( removed );
				}
	
				@Override
				public void onExit(IFiscalModel edited) {
					LOGGER.info("Exit " + edited.getStatus().getName());
					if (edited.getId() != null) {
						refresh( edited );
					}
				}
	
				@Override
				public void onFailure(Throwable caught) {
					LOGGER.info("Failure");
					showError(caught.getMessage());
				}
				
				private void refresh(IFiscalModel model) {
					ModelMatrixPanel.this.paintViewModelCell( options, cell, model);
				}
				
			})));
		}
	}
	
	private void paintViewModelCell(MatrixModuleOptions options, AonDisplayTableCell cell, IFiscalModel model) {
		FocusPanel focusPanel = new FocusPanel();
		Label mod = new Label();
		mod.setTitle(AON.MSG.viewDeclaration(FiscalModelUtils.getModelName( model ), model.getPeriod().getDescription()));
		mod.setStyleName(AON.CSS.aonClickable());
		mod.addStyleName(AON.CSS.aonIconLabel());
		mod.addStyleName(AON.CSS.aonIconBullet());
		mod.addStyleName(AON.CSS.aonTextCenter());
		focusPanel.add(mod);
		cell.clear();
		cell.add(focusPanel);
		cell.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB( model.getStatus() ));
		if (params.getPeriod() != null) {
			if (params.getPeriod().isMonthPeriod()) 			
				cell.getElement().getStyle().setWidth(40, Unit.PX);
			else 
				cell.getElement().getStyle().setWidth(60, Unit.PX);
		}
		focusPanel.addClickHandler( event -> model.getModel().visit(
			new MatrixViewVisitor(options,model, new AonModuleCallback<IFiscalModel>() {

			private static final long serialVersionUID = 1L;

			@Override
			public void onChange(IFiscalModel changed) {
				LOGGER.info("Change " + changed.getStatus().getName());
				refresh( changed );
			}

			@Override
			public void onRemove(IFiscalModel removed) {
				ModelMatrixPanel.this.paintNewModelCell(options,cell,cloneModel( removed ));
			}

			@Override
			public void onExit(IFiscalModel edited) {
				LOGGER.info("Exit " + edited.getStatus().getName());
				refresh( edited );
			}

			@Override
			public void onFailure(Throwable caught) {
				LOGGER.info("Failure ---> " + (model.getModel()==null?"NULL":model.getModel().toString()));
				showError(caught.getMessage());
			}
			
			private void refresh(IFiscalModel model) {
				if (params.getPeriod() != null) {
					refreshButton.click();
				}
				else if (params.getStatus() == null) {
					cell.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB( model.getStatus() ));
				} else if (model.getStatus() != params.getStatus()) {
					refreshButton.click();
				}
			}
			
		})));
	}

	private AonDisplayTable paintFiscalModelRow(MatrixModuleOptions options, FiscalModel fm, Administration admKey, MatrixPeriodType perKey, String docKey, AonDisplayTable table, FiscalMatrixParams params, String domainName) {
		Label admonLabel = getAdmonLabel( admKey );
		AonDisplayTableRow periodRow = table.addRow();
		if (!options.isCompactMode()) {
			if (!params.isFiscalModelTypePresent()) {
				if (!AonStringUtils.equals(domainName,fm.getName())) {
					periodRow.addCell( new Label( docKey + " " + fm.getName() ), AON.CSS.aonBorderBottom());
				} else {
					periodRow.addCell( getEmptyLabel());
				}
			} else {
				periodRow.addCell( new Label( docKey + " " + fm.getName() ), AON.CSS.aonBorderBottom());
			}
		}
		periodRow
			.addCell( new Label( FiscalModelUtils.getModelName( fm) ), AON.CSS.aonBold(), AON.CSS.aonBorderBottom(), AON.CSS.aonTextCenter() )
			.addCell( admonLabel, AON.CSS.aonBorderBottom(), AON.CSS.aonTextCenter() )
			.addCell( new Label(perKey.getValue()), AON.CSS.aonBorderBottom(), AON.CSS.aonTextCenter() )
			;
		AonDisplayTableCell periodCell = periodRow.addCell();
		periodCell.getElement().getStyle().setVerticalAlign(VerticalAlign.BOTTOM);
		periodCell.getElement().getStyle().setPadding(0.0, Unit.PX);
		AonDisplayTable periodTable = getPeriodTable( options, fm ); 
		periodCell.add( periodTable );
		return periodTable;
	}

	private Label getAdmonLabel(Administration adm) {
		Label admonLabel = new Label();
		if (adm == Administration.UNKNOWN) {
			admonLabel.setText("\u2022");
		} else {
			admonLabel.setStyleName(AON.CSS.aonIconLabel());
			admonLabel.addStyleName(FiscalModelUtils.getAdministrationIconStyle(adm));	
		}
		return admonLabel;
	}

	private void paintDomainRowIfNeeded(boolean modelPresent, AonDisplayTable table, String domainName) {
		if (!modelPresent) {
			table.addRow( )
				.addCell( new Label(domainName), AON.CSS.aonBold(), AON.CSS.aonPaddingLeft(),AON.CSS.aonTextUppercase())
				.addCell( getEmptyLabel())
				.addCell( getEmptyLabel())
				.addCell( getEmptyLabel())
				.addCell( getEmptyLabel())
				;
		}
	}
	
	private AonDisplayTable getPeriodTable(MatrixModuleOptions options,FiscalModel fm) {
		AonDisplayTable periodTable = new AonDisplayTable();
		periodTable.addStyleName(AON.CSS.aonWidthAll());
		periodTable.getElement().getStyle().setProperty( TABLE_LAYOUT, FIXED );
		int times = 1;
		if (params.getPeriod() == null) {
			if (fm.isQuarterPeriod()) times = 4;
			if (fm.isMonthPeriod()) times = 12;
		}
		AonDisplayTableRow row = periodTable.addRow();
		for (int x = 0; x < times; x++) {
			FiscalModel model = new FiscalModel();
			model.setAdministration(fm.getAdministration());
			model.setModel(fm.getModel());
			model.setYear(fm.getYear());
			model.setDomain(fm.getDomain());
			model.setPeriod(params.getPeriod() == null ? Period.values()[ ((times == 4)?12:0) + x] : params.getPeriod());
			AonDisplayTableCell cell = row.addCell();
			paintNewModelCell(options,cell,model);
		}
		return periodTable;
	}

	private AonDisplayTable getMonthsTable() {
		AonDisplayTable monthsTable = new AonDisplayTable();
		if (params.getPeriod() == null || params.getPeriod().isMonthPeriod()) {
			monthsTable.addStyleName(AON.CSS.aonWidthAll());
			monthsTable.getElement().getStyle().setProperty(TABLE_LAYOUT, FIXED);
			AonDisplayTableRow monthsRow = monthsTable.addRow();
			monthsRow.addStyleName(AON.CSS.aonFontSmaller());
			monthsRow.addStyleName(AON.CSS.aonBold());			
			if (params.getPeriod() == null) {
				Arrays.stream(MONTHS).forEach( m -> monthsRow.addCell(new InlineLabel( m ), AON.CSS.aonBorderBottom(),AON.CSS.aonTextCenter()) );				
			} else {
				// Si filtrando por solo un mes, se muestra solo el mes por el que se filtra 
				monthsRow.addCell(new InlineLabel( MONTHS[params.getPeriod().value()] ), AON.CSS.aonBorderBottom(), AON.CSS.aonTextCenter(), AON.CSS.aonWidth40());
				addOnePeriodCells(monthsRow);
			}
		}
		return monthsTable;
	}

	private AonDisplayTable getQuarsTable() {
		AonDisplayTable quarsTable = new AonDisplayTable();
		if (params.getPeriod() == null || params.getPeriod().isQuarterPeriod()) {
			quarsTable.addStyleName(AON.CSS.aonWidthAll());
			quarsTable.getElement().getStyle().setProperty(TABLE_LAYOUT, FIXED);
			AonDisplayTableRow quarsRow = quarsTable.addRow();
			quarsRow.addStyleName(AON.CSS.aonFontSmaller());
			quarsRow.addStyleName(AON.CSS.aonBold());
			if (params.getPeriod() == null) {
				Arrays.stream(QUARS).forEach( q -> quarsRow.addCell(new InlineLabel( q ), AON.CSS.aonBorderBottom(),AON.CSS.aonTextCenter(),AON.CSS.aonBold()) );
			} else {
				// Si filtrando por solo un trimestre, se muestra solo el trimestre por el que se filtra
				quarsRow.addCell(new InlineLabel( QUARS[params.getPeriod().value()-Period.T1.value()] ), AON.CSS.aonBorderBottom(),AON.CSS.aonTextCenter(), AON.CSS.aonWidth60());
				addOnePeriodCells(quarsRow);
			}
		}
		return quarsTable;
	}
	
	private AonDisplayTable getYearlyTable() {
		AonDisplayTable table = new AonDisplayTable();
		table.addStyleName(AON.CSS.aonWidthAll());
		table.getElement().getStyle().setProperty(TABLE_LAYOUT, FIXED);
		AonDisplayTableRow row = table.addRow();
		row.addStyleName(AON.CSS.aonFontSmaller());
		row.addStyleName(AON.CSS.aonBold());
		row.addCell(new InlineLabel("ANUAL"), AON.CSS.aonBorderBottom(), AON.CSS.aonTextCenter(), AON.CSS.aonWidth60());
		addOnePeriodCells(row);
		return table;
	}
	
	private void addOnePeriodCells(AonDisplayTableRow row) {
		// FALTA - POR AHORA SOLO PARA DOMINIOS BETA
		if (isBetaEnabled) {
			if (params.getPeriod() != Period.YEAR) {
				row.addCell(new InlineLabel( AON.MSG.result() ), AON.CSS.aonBorderBottom(), AON.CSS.aonTextCenter(), AON.CSS.aonWidth80() )
				   .addCell(new InlineLabel( AON.MSG.declarationType() ), AON.CSS.aonBorderBottom(), AON.CSS.aonTextCenter(), AON.CSS.aonWidth170())
	               .addCell(new InlineLabel( "IBAN / NRC" ), AON.CSS.aonBorderBottom(), AON.CSS.aonTextCenter(), AON.CSS.aonWidth170());
			}
			if (params.isMultiplePresentation()) {					
				markAllForSend = new CheckBox();			 
				markAllForSend.setValue(true);
				markAllForSend.setTitle("Pulse para marcar o desmarcar todos");					
				markAllForSend.addClickHandler( event -> 
					selectedCheckBox.forEach( cb -> {
						if (cb.isEnabled()) 
							cb.setValue(markAllForSend.getValue(), true);	
					}) 
				);
				AonDisplayTable table = new AonDisplayTable(AON.CSS.aonWidthAll());
				table.addRow().addCell(new InlineLabel("Presentar"), AON.CSS.aonTextCenter());
				table.addRow().addCell(markAllForSend, AON.CSS.aonTextCenter());						
				row.addCell(table, AON.CSS.aonBorderBottom(), AON.CSS.aonTextCenter(), AON.CSS.aonWidth60());
			}	
		}
	}

	private Label getEmptyLabel() {
		return new Label( AonStringUtils.EMPTY );
	}
	
	private Label getEmptyLabel(String width) {
		Label label = getEmptyLabel();
		label.setWidth( width );
		return label;
	}

	private AonTableButton getHelpButton() {
		AonTableButton helpButton  = new AonTableButton(LEYENDA,AON.CSS.aonIconHelp());
		helpButton.setText(LEYENDA);
		helpButton.addStyleName(AON.CSS.aonMarginRight());
		helpButton.addStyleName(AON.CSS.aonMarginLeft());
		helpButton.getElement().getStyle().setPaddingLeft(3, Unit.EM);
		helpButton.addClickHandler(event -> showLegend());
		return helpButton;
	}

	private void showLegend() {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption(LEYENDA);
		FlowPanel tabContainer = new FlowPanel();
		tabContainer.addStyleName(AON.CSS.aonPadding());
	
		AonDisplayTable statusLegendTab = new AonDisplayTable();
		statusLegendTab.getElement().getStyle().setProperty("border-collapse", "separate");
		statusLegendTab.getElement().getStyle().setProperty("border-spacing", "2px");
		statusLegendTab.addStyleName(AON.CSS.aonMarginTop());
		statusLegendTab.addStyleName(AON.CSS.aonMarginBottom());
		statusLegendTab.addStyleName(AON.CSS.aonWidth300());
		statusLegendTab.addStyleName(AON.CSS.aonBlockCenter());
		statusLegendTab.addStyleName(AON.CSS.aonTextCenter());
		FiscalStatus[] statuses = new FiscalStatus[] {FiscalStatus.MISSING
				,FiscalStatus.PENDING
				,FiscalStatus.CUSTOMER_CHECK
				,FiscalStatus.CUSTOMER_ACCEPTED
				,FiscalStatus.CUSTOMER_REJECTED
				,FiscalStatus.FINISHED
				,FiscalStatus.SENT};
		for (FiscalStatus st : statuses) {
			InlineLabel statusLabel = new InlineLabel(st == FiscalStatus.MISSING? "No realizado" : st.getName());
			statusLabel.setStyleName(AON.CSS.aonMargin());
			statusLabel.getElement().getStyle().setPadding(3, Unit.PX);
			AonDisplayTableCell cell = statusLegendTab.addRow().addCell();
			cell.addStyleName(AON.CSS.aonBorder());
			cell.getElement().getStyle().setColor(FiscalModelUtils.getStatusFrgColorRGB( st ));
			cell.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB( st ));
			cell.add(statusLabel);
		}
		tabContainer.add( statusLegendTab );
		FlowPanel buttons = new FlowPanel();
		buttons.setStyleName(AON.CSS.aonTextCenter());
		buttons.addStyleName(AON.CSS.aonMarginTop());
		buttons.addStyleName(AON.CSS.aonMarginBottom());
		final Button okButton = new Button();
		okButton.setStyleName(AON.CSS.aonOkButton());
		okButton.setText( AON.MSG.accept());
		okButton.addClickHandler(event1 -> dialog.hide());
		buttons.add(okButton);
		
		tabContainer.add( buttons );
		dialog.add( tabContainer );
		dialog.center();
		dialog.show();
	}
	
	protected void showError(String message) {
		Window.alert("ERROR:" + message);
	}

	private FiscalModel cloneModel(IFiscalModel fm) {
		return new FiscalModel()
			.setYear(fm.getYear())
			.setAdministration(fm.getAdministration())
			.setModel( fm.getModel() )
			.setPeriod( fm.getPeriod() )
			.setStatus( fm.getStatus() )
			.setDomain(fm.getDomain())
			.setDomainName(fm.getDomainName())
			;
	}

}
