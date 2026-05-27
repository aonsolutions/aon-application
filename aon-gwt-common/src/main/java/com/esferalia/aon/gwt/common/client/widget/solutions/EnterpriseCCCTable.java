package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonEnterpriseCCCPanel.AonEnterpriseCCCPanelCallback;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.occam.api.model.payroll.Activity;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class EnterpriseCCCTable extends ScrollPanel {

	private static final Logger LOGGER = Logger.getLogger(EnterpriseCCCTable.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}

	private SimplePanel container;
	private ScrollPanel scrollPanel;
	private AonCustomTable tab;

	private Integer domain;
	private Activity enterpriseActivity;

	private static enum COLS {
		TYP("Tipo", "-moz-available",
				"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		ADD("Cuenta", "9rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		STA(AonStringUtils.EMPTY, "3rem", ""),
		PRO(AON.MSG.province(), "7rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		BUT(AonStringUtils.EMPTY, "5rem", "");

		String headerLabel;
		String colWidth;
		String styles;

		private COLS(String headerLabel, String colWidth, String styles) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.styles = styles;
		}

		public String getColWidth() {
			return colWidth;
		}

		public String getHeaderLabel() {
			return headerLabel;
		}

		public String getStyles() {
			return styles;
		}
	}

	public EnterpriseCCCTable(int domain, Activity enterpriseActivity) {
		this.domain = domain;
		this.enterpriseActivity = enterpriseActivity;

		container = new SimplePanel();
		container.getElement().getStyle().setProperty("max-height", "200px");
		container.getElement().getStyle().setProperty("padding-left", "1px");
		setWidget(container);
		search();
	}

	private void search() {
		container.clear();
		tab = new AonCustomTable();
		tab.setMaxHeight("160x");
		tab.getElement().getStyle().setProperty("padding-top", "0");
		scrollPanel = new ScrollPanel(tab);

		paintHeader();
		container.setWidget(scrollPanel);
		searchData();
	}

	private void paintHeader() {
		tab.createHeader();
		for (COLS col : COLS.values()) {
			if (col.equals(COLS.BUT)) {
				FlowPanel buttonContainer = new FlowPanel();
				buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);

				AonTableButton button = new AonTableButton("Nuevo CCC", AON.CSS.aonIconAdd());
				button.addStyleName(AON.CSS.aonCustomRowButtom());
				button.getElement().getStyle().setProperty("border", "2px solid #434548");
				button.getElement().getStyle().setProperty("padding", "10px");
				button.getElement().getStyle().setProperty("border-radius", "50%");
				button.addClickHandler(e -> createEnterpriseCCC());
				buttonContainer.add(button);

				tab.addHeader(buttonContainer, col.getColWidth(), col.getStyles());
			} else
				tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());

		}
	}

	private void searchData() {
		if (this.enterpriseActivity.getCccs().isEmpty()) {
			paintEmptyRow();
		} else {
			for (EnterpriseCCC ccc : this.enterpriseActivity.getCccs()) {
				if (ccc.isDeleted())
					continue;
				paintRow(ccc);
			}
		}
	}

	private void paintEmptyRow() {
		HTMLPanel row = tab.createRow();
		Label empty = new Label("No existen cuentas de cotizaci\u00f3n");
		tab.addInlineStyle(empty, COLS.TYP.getStyles());
		tab.addRow(row, empty, COLS.TYP.getColWidth());
	}

	private void paintRow(EnterpriseCCC ccc) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);

		AonTableButton button;
		button = new AonTableButton("Borrar CCC", AON.CSS.aonIconDelete());
		button.addStyleName(AON.CSS.aonCustomRowButtom());
		button.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				button.setEnabled(false);
				
				if(Boolean.TRUE.equals(ccc.isUseByContracts())) 
					onShowErrorMessage("No se puede eliminar una cuenta de cotizaci\u00F3n que esta siendo usada por un centro de trabajo y/o por un contrato");
				else if(Boolean.TRUE.equals(ccc.isUseByCra())) 
					onShowErrorMessage("No se puede eliminar una cuenta de cotizaci\u00F3n que esta  siendo referenciada desde un CRA existente");
				else {
					AonDialog dialog = new AonDialog("Eliminaci\u00f3n CCC",
							new HTML("Se va a proceder a eliminar la cuenta de cotizaci\u00f3n <b>" + ccc.getCcc()
									+ "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
	
					dialog.confirm(new AonAcceptDialogCallback() {
	
						@Override
						public void onCancel() {
							button.setEnabled(true);
						}
	
						@Override
						public void onAccept() {
							ccc.setDeleted(true);
							search();
						}
					});
				}
			}
		});
		buttonContainer.add(button);

		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onUpdateEnterpriseCCC(ccc), ClickEvent.getType());

		Label regime = new Label(getRegime(ccc.getType()));
		tab.addInlineStyle(regime, COLS.TYP.getStyles());
		tab.addRow(row, regime, COLS.TYP.getColWidth());

		tab.addRow(row, new Label(getCCCRegimeCode(ccc.getType()) + ccc.getCcc()), COLS.ADD.getColWidth());

		AonTableButton statusBtn = checkCCC(ccc.getCcc())
				? new AonTableButton("Correcto", AON.CSS.aonIconCheckCircle())
				: new AonTableButton("CCC Incorrecto", AON.CSS.aonIconBlock());
		tab.addRow(row, statusBtn, COLS.STA.getColWidth());

		tab.addRow(row, new Label(ccc.getGeozoneDescription()), COLS.PRO.getColWidth());

		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}

	// -------------------------------------------- Auxiliar Methods

	private boolean checkCCC(String ccc) {
		if (AonStringUtils.isBlank(ccc) || ccc.length() != 11)
			return false;

		String code = ccc.substring(ccc.length() - 2, ccc.length());
		Integer codeInt = Integer.parseInt(code);

		String cccStr = ccc.substring(2, ccc.length() - 2);
		if (cccStr.startsWith("0"))
			cccStr = ccc.substring(3, ccc.length() - 2);
		cccStr = ccc.substring(0, 2) + cccStr;

		Integer cccInt = Integer.parseInt(cccStr);

		return cccInt % 97 == codeInt;
	}

	public static String getCCCRegimeCode(Byte cccRegime) {
		switch (cccRegime) {
		case 0:
			return "0111";
		case 1:
			return "0111";
		case 2:
			return "0111";
		case 3:
			return "0111";
		case 4:
			return "0111";
		case 5:
			return "0111";
		case 6:
			return "0138";
		case 7:
			return "0163";
		case 8:
			return "0112";
		default:
			return "0111";
		}
	}

	private String getRegime(Byte cccRegime) {
		switch (cccRegime) {
		case 0:
			return "Principal";
		case 1:
			return "Formacion y aprendizaje";
		case 2:
			return "Aprendizaje";
		case 3:
			return "Representantes de comercio";
		case 4:
			return "Asimilados R.General";
		case 5:
			return "Becarios";
		case 6:
			return "Emplead@s de hogar";
		case 7:
			return "Trabajadores cuenta ajena agrarios";
		case 8:
			return "Artistas";
		default:
			return "Principal";
		}
	}

	private void onUpdateEnterpriseCCC(EnterpriseCCC ccc) {
		
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption("Editar CCC");

		final AonEnterpriseCCCPanel marketingCampaignPanel = new AonEnterpriseCCCPanel(ccc, new AonEnterpriseCCCPanelCallback() {

					@Override
					public void onCancel() {
						dialog.hide();
					}

					@Override
					public void onAccept(EnterpriseCCC ccc) {
						dialog.hide();
						search();
					}
				});

		dialog.add(marketingCampaignPanel);
		dialog.showLoaded();
	}

	private void createEnterpriseCCC() {
		
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption("Nuevo CCC");

		final AonEnterpriseCCCPanel marketingCampaignPanel = new AonEnterpriseCCCPanel(domain, enterpriseActivity.getId(), new AonEnterpriseCCCPanelCallback() {

					@Override
					public void onCancel() {
						dialog.hide();
					}

					@Override
					public void onAccept(EnterpriseCCC ccc) {
						Window.alert("enterpriseActivity.getCccs() BEFORE : " +  enterpriseActivity.getCccs().size());
						enterpriseActivity.getCccs().add(ccc);
						Window.alert("enterpriseActivity.getCccs() AFTER : " +  enterpriseActivity.getCccs().size());
						dialog.hide();
						search();
					}
				});

		dialog.add(marketingCampaignPanel);
		dialog.showLoaded();
		
	}

	protected abstract void onShowErrorMessage(String errorMessage);

}
