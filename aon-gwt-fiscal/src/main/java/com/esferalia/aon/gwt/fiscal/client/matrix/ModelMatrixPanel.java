package com.esferalia.aon.gwt.fiscal.client.matrix;


import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.api.client.fiscal.JsFiscalMenuItem;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableCell;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

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
	

	public ModelMatrixPanel(MatrixModuleOptions options, MatrixData matrixData, FiscalMatrixParams fiscalMatrixParams) {
		super();
		setStyleName(AON.CSS.aonMarginRight());
		addStyleName(AON.CSS.aonMarginTop());
		addStyleName(AON.CSS.aonMarginLeft());
		addStyleName(AON.CSS.aonBlockCenter());
		
		AonDisplayTable table = new AonDisplayTable();
		table.addStyleName(AON.CSS.aonBlockCenter());
		table.addStyleName(AON.CSS.aonWidthAlmostAll());
		
		table.addRow( )
			.addCell( getHelpButton(), AON.CSS.aonFlexGrow1())
			.addCell( getEmptyLabel( "50px"))
			.addCell( getEmptyLabel( "25px"))
			.addCell( getEmptyLabel( "70px"))
			.addCell( getMonthsTable(), AON. CSS.aonWidth400());
		
		table.addRow( )
			.addCell( getEmptyLabel())
			.addCell( getEmptyLabel())
			.addCell( getEmptyLabel())
			.addCell( getEmptyLabel())
			.addCell( getQuarsTable())
			;
		matrixData.getDomains().stream().forEach( domKey -> {
			String domainId = AonStringUtils.substringBefore(domKey, AonStringUtils.PIPE);
			String domainName = AonStringUtils.substringAfter(domKey, AonStringUtils.PIPE);
			paintDomainRowIfNeeded( fiscalMatrixParams.isFiscalModelTypePresent(),table,domainName );
			for (Administration admKey : matrixData.getAdministrations(domKey)) {
				for (MatrixPeriodType perKey : matrixData.getPeriodTypes(domKey, admKey)) {
					for (String modKey : matrixData.getModels(domKey, admKey, perKey)) {
						for (String docKey : matrixData.getDocs(domKey, admKey, perKey, modKey)) {
							FiscalModel fm = new FiscalModel();
							fm.setYear(fiscalMatrixParams.getYear());
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
										firstItem = it;
										break;
									}
								}
								String n = firstItem.getName();
								String s = firstItem.getSurname();
								String name = AonStringUtils.join(new String[]{s,n}, AonStringUtils.isBlank(s)?"":", ");
								fm.setName((firstItem == null)?domainName:name);	
							}
							
							AonDisplayTable periodTable = paintFiscalModelRow(options,fm,admKey,perKey,docKey,table);
							fillPeriodTable(options, fm, periodTable, items );
						}
					}
				}
			}
		});
		add(table);
	}
	
	private void fillPeriodTable(MatrixModuleOptions options, FiscalModel fm, AonDisplayTable periodTable, List<JsFiscalMenuItem> items) {
		for (JsFiscalMenuItem model : items) {
			if (model != null ) {
				FiscalStatus status = FiscalStatus.safeValueOf( model.getStatus() );
				if (status != FiscalStatus.MISSING) {
					Period period = Period.valueOf(model.getPeriod());
					int col = (period.isQuarterPeriod()?(period.getStartMonth()/3):period.getStartMonth());
					AonDisplayTableRow row = (AonDisplayTableRow) periodTable.getWidget(0);
					AonDisplayTableCell cell = (AonDisplayTableCell) row.getWidget(col);
					FiscalModel cloned = cloneModel(fm);
					if (model.getId() != null) {
						cloned.setId(Integer.valueOf(model.getId() + ""));
					} else {
						cloned.setId(null);
					}
					cloned.setStatus( status );
					paintViewModelCell(options, cell, cloned );
				}
			}
		}
	}

	private void paintNewModelCell(MatrixModuleOptions options, AonDisplayTableCell cell, IFiscalModel model) {
		cell.clear();
		AonTableButton addButton = new AonTableButton(AON.MSG.newAction(), AON.CSS.aonIconAdd());
		cell.add(addButton);
		cell.addStyleName( AON.CSS.aonBorderBottom() );
		cell.addStyleName( AON.CSS.aonTextCenter() );
		cell.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB( FiscalStatus.MISSING ));									
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
				cell.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB( model.getStatus() ));
			}
			
		})));
	}

	private AonDisplayTable paintFiscalModelRow(MatrixModuleOptions options, FiscalModel fm, Administration admKey, MatrixPeriodType perKey, String docKey, AonDisplayTable table) {
		Label admonLabel = getAdmonLabel( admKey );
		AonDisplayTableRow periodRow = table.addRow();
		periodRow
			.addCell( new Label( docKey + " " + fm.getName() ), AON.CSS.aonBorderBottom())
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
		if (fm.isQuarterPeriod()) times = 4;
		if (fm.isMonthPeriod()) times = 12;
		AonDisplayTableRow row = periodTable.addRow();
		for (int x = 0; x < times; x++) {
			FiscalModel model = new FiscalModel();
			model.setAdministration(fm.getAdministration());
			model.setModel(fm.getModel());
			model.setYear(fm.getYear());
			model.setDomain(fm.getDomain());
			model.setPeriod(Period.values()[ ((times == 4)?12:0) + x]);
			AonDisplayTableCell cell = row.addCell();
			paintNewModelCell(options,cell,model);
		}
		return periodTable;
	}

	private AonDisplayTable getMonthsTable() {
		AonDisplayTable monthsTable = new AonDisplayTable();
		monthsTable.addStyleName(AON.CSS.aonWidthAll());
		monthsTable.getElement().getStyle().setProperty(TABLE_LAYOUT, FIXED);
		AonDisplayTableRow monthsRow = monthsTable.addRow();
		monthsRow.addStyleName(AON.CSS.aonFontSmaller());
		monthsRow.addStyleName(AON.CSS.aonBold());
		Arrays.stream(MONTHS).forEach( m -> monthsRow.addCell(new InlineLabel( m ), AON.CSS.aonBorderBottom(),AON.CSS.aonTextCenter()) );
		return monthsTable;
	}

	private AonDisplayTable getQuarsTable() {
		AonDisplayTable quarsTable = new AonDisplayTable();
		quarsTable.addStyleName(AON.CSS.aonWidthAll());
		quarsTable.getElement().getStyle().setProperty(TABLE_LAYOUT, FIXED);
		AonDisplayTableRow quarsRow = quarsTable.addRow();
		quarsRow.addStyleName(AON.CSS.aonFontSmaller());
		quarsRow.addStyleName(AON.CSS.aonBold());
		Arrays.stream(QUARS).forEach( q -> quarsRow.addCell(new InlineLabel( q ), AON.CSS.aonBorderBottom(),AON.CSS.aonTextCenter(),AON.CSS.aonBold()) );
		return quarsTable;
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
		statusLegendTab.setStyleName(AON.CSS.aonMarginTop());
		statusLegendTab.setStyleName(AON.CSS.aonMarginBottom());
		FiscalStatus[] statuses = new FiscalStatus[] {FiscalStatus.MISSING,FiscalStatus.PENDING,FiscalStatus.CUSTOMER_CHECK,FiscalStatus.FINISHED,FiscalStatus.SENT};
		for (FiscalStatus st : statuses) {
			InlineLabel statusLabel = new InlineLabel(st == FiscalStatus.MISSING? "No realizado" : st.getName());
			statusLabel.setStyleName(AON.CSS.aonMarginLeft());
			statusLabel.getElement().getStyle().setPadding(3, Unit.PX);
			AonDisplayTableRow legendRow = statusLegendTab.addRow();
			AonDisplayTableCell cell0 = legendRow.addCell();
			cell0.setWidth("200px");
			cell0.add(statusLabel);
			AonDisplayTableCell cell = legendRow.addCell();
			cell.addStyleName(AON.CSS.aonBorder());
			cell.setWidth("20px");
			cell.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB( st ));
			cell.add(new InlineLabel());
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

	private FiscalModel cloneModel( IFiscalModel fm) {
		return new FiscalModel()
				.setYear(fm.getYear())
				.setAdministration(fm.getAdministration())
				.setModel( fm.getModel() )
				.setPeriod( fm.getPeriod() )
				.setStatus( fm.getStatus() )
				.setDomain(fm.getDomain())
				.setDomainName(fm.getDomainName());
	}

}
