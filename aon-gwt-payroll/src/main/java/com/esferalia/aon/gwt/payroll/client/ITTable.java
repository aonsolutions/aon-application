package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.ItParams;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class ITTable extends ScrollPanel {

	private static DomainEnterprisesServiceAsync service = DomainEnterprisesServiceAsync.newInstance();
	
	private static final Logger LOGGER = Logger.getLogger(ITTable.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private SimplePanel container;
	private ScrollPanel scrollPanel;
	private AonCustomTable tab;
	private int lastScrollPos = 0;
	
	private ItParams params;
	
	private boolean isTablet = false;
	private ArrayList<COLS> colTabletHidden = new ArrayList<>();
	
	private static enum COLS {
		  DES(AON.MSG.description()					,"-moz-available"  	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, BAJ("F. Baja"								,"5rem" 			,"")
		, CBA("Causa Baja"							,"15rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, ALT("F. Alta"								,"5rem" 			,"")
		, CAA("Causa Alta"							,"15rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, STR("Ini. Contrato"						,"6rem" 			,"")
		, END("Fin Contrato"						,"6rem" 			,"")
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

	public ITTable(ItParams params) {
		this.params = params;
		this.isTablet = Window.getClientWidth() <= 980;
		initHiddenColumns();

		container = new SimplePanel();
		container.getElement().getStyle().setProperty("padding", "0 1rem");
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
						searchData();
					}
				}
			}
		});
		
		onSearch();
		
	}
	
	private void initHiddenColumns() {
		colTabletHidden.clear();
		colTabletHidden.add(COLS.STR);
		colTabletHidden.add(COLS.END);
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

	private void search() {
		container.clear();
		tab = new AonCustomTable();
		tab.setMaxHeight((Window.getClientHeight() - 200) + "px");
		scrollPanel = new ScrollPanel(tab);
		
		paintHeader();
		container.setWidget(scrollPanel);
		searchData();
	}
	
	private void paintHeader() {
		tab.createHeader();
		for ( COLS col : COLS.values()) 
			if(!isTablet || !colTabletHidden.contains(col))
				tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
	}
	
	private void searchData() {
		if (!isMoreData()) return;
		
		params.setOffset(offset.intValue());
		params.setLimit(limit);
		
		getList(ITEmployees -> {
			boolean something = false;
			for(ITEmployee itEmployee : ITEmployees) {
				something = true;
				paintRow(itEmployee);
			}
			
			if (ITEmployees.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + ITEmployees.size() - 1);
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
	
	private void paintRow(ITEmployee itEmployee) {
		
		itEmployee.getIts().forEach(it -> {
			HTMLPanel row = tab.createRow();
			row.addDomHandler(e -> onITOpen(itEmployee.getContractInfo().getContractId(), it.getId()), ClickEvent.getType());
			
			Label name = new Label(itEmployee.getEmployeeInfo().getFullName());
			String title = itEmployee.getEmployeeInfo().getFullName();
			if (checkOutOfContractA(it)) {
				title = "IT posterior a la fecha fin contrat";
				name.getElement().getStyle().setColor("red");
			} else if (checkOutOfContractB(it)) {
				title = "IT anterior a la fecha inicio contrato";
				name.getElement().getStyle().setColor("orange");
			} else if (checkOutOfContractBtw(it)) {
				title = "IT abierta sin fecha alta o fecha alta posterior al fin del contrato";
				name.getElement().getStyle().setColor("green");
			}
			name.setTitle(title);
			tab.addInlineStyle(name, COLS.DES.getCellStyleClass());
			tab.addRow(row, name, COLS.DES.getColWidth());
			
			
			
			tab.addRow(row, new Label(it.getStartDate() == null ? "" : formatDate.format(it.getStartDate())), COLS.BAJ.getColWidth());
			
			Label baja = new Label(parseLowCause(it.getTypeLowPart()));
			baja.setTitle(parseLowCause(it.getTypeLowPart()));
			tab.addInlineStyle(baja, COLS.CBA.getCellStyleClass());
			tab.addRow(row, baja, COLS.CBA.getColWidth());
			
			tab.addRow(row, new Label(it.getEndDate() == null ? "" : formatDate.format(it.getEndDate())), COLS.ALT.getColWidth());
			
			Label alta = new Label(parseHighCause(it.getTypeHighPart()));
			alta.setTitle(parseHighCause(it.getTypeHighPart()));
			tab.addInlineStyle(alta, COLS.CAA.getCellStyleClass());
			tab.addRow(row, alta, COLS.CAA.getColWidth());
			
			if(!isTablet)
				tab.addRow(row, new Label(itEmployee.getContractInfo().getStartDate() == null ? "" : formatDate.format(itEmployee.getContractInfo().getStartDate())), COLS.STR.getColWidth());
				tab.addRow(row, new Label(itEmployee.getContractInfo().getEndDate() == null ? "" : formatDate.format(itEmployee.getContractInfo().getEndDate())), COLS.END.getColWidth());
			
		});
	}
	
	private void getList(Consumer<List<ITEmployee>> success) {
		service.getEmployeeItList(params, new AsyncCallback<List<ITEmployee>>() {
			
			@Override
			public void onSuccess(List<ITEmployee> employeesInfoList) {
				success.accept(employeesInfoList);			
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
	}
	
	private String parseLowCause(Byte typeLowPart) {
		switch (typeLowPart) {
		case (byte) 0:
			return "Enfermedad Com\u00FAn";
		case (byte) 1:
			return "Accidente de trabajo";
		case (byte) 2:
			return "Maternidad";
		case (byte) 3:
			return "Paternidad";
		case (byte) 4:
			return "Riesgo para el embarazo";
		case (byte) 5:
			return "Riesgo durante la lactancia";
		case (byte) 6:
			return "Accidente no laboral";
		case (byte) 7:
			return "Enfermedad com\u00FAn periodo de carencia";
		case (byte) 8:
			return "Enfermedad com\u00FAn, prestaci\u00F3n profesional (COVID-19)";
		case (byte) 9:
			return "Periodo de Observaci\u00f3n por Enfermedad Profesional";
		default:
			return "";
		}
	}

	private String parseHighCause(Byte typeHighPart) {
		if (null == typeHighPart)
			return "";

		switch (typeHighPart) {
		case (byte) 0:
			return "Curaci\u00F3n";
		case (byte) 1:
			return "Fallecimiento";
		case (byte) 2:
			return "Inspecci\u00F3n m\u00E9dica";
		case (byte) 3:
			return "Propuesta incapacidad";
		case (byte) 4:
			return "Agotamiento de plazo";
		case (byte) 5:
			return "Mejor\u00EDa que permite realizar el trabajo habitual";
		case (byte) 6:
			return "Incomparecencia";
		case (byte) 7:
			return "Control INSS duraci\u00F3n 12 meses";
		case (byte) 8:
			return "Recuperaci\u00F3n capacidad profesional";
		case (byte) 9:
			return "Incomparecencia contratos de formaci\u00F3n";
		default:
			return "";
		}
	}
	
	private boolean checkOutOfContractA(IT it) {
		return null != it.getContractEndDate() && it.getStartDate().after(it.getContractEndDate());
	}

	private boolean checkOutOfContractB(IT it) {
		return null != it.getEndDate() && it.getEndDate() != it.getContractStartDate()
				&& it.getEndDate().before(it.getContractStartDate());
	}

	private boolean checkOutOfContractBtw(IT it) {
		return it.getStartDate().after(it.getContractStartDate()) && (null == it.getEndDate()
				|| (null != it.getContractEndDate() && it.getEndDate().after(it.getContractEndDate())));
	}
	
	public void resetSearchOffset() {
		offset.setValue(0);
	}

	protected abstract void onShowErrorMessage(String errorMessage);
	protected abstract void onShowLoadingMessage(String loadingMessage);
	protected abstract void onShowSuccessMessage(String successMessage);
	protected abstract void onITOpen(Integer contractId, Integer itId);
	
}

