package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRDirStaffPanel.AonRDirStaffPanelCallback;
import com.esferalia.aon.occam.api.model.registry.RDirStaff;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class RDirStaffTable extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(RDirStaffTable.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private SimplePanel container;
	private ScrollPanel scrollPanel;
	private AonCustomTable tab;
	private int lastScrollPos = 0;
	
	private String domainName;
	private Integer domain;
	private String user;
	private Integer registry;
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private static enum COLS {
		  DOC("NIF"					   				,"6rem", ""  )
		, NAM("Nombre"								,"-moz-available", "min-width: 8rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" )
		, RPL("R. Legal"							,"5rem", ""  )
		, RPP("R. Laboral"							,"5rem", ""  )
		, SOC("Socio"								,"4rem", ""  )
		, ADM("Admin."								,"4rem", ""  )
		, PAC("% Acciones"							,"6rem", "max-width: 6rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"  )
		, NAC("N. Acciones"							,"6rem", "max-width: 6rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"  )
		, VAL("V. Nominal"							,"6rem", "max-width: 6rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"  )
		, CAD("Caducidad Cargo"						,"8rem", "max-width: 8rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"  )
		, BUT(AonStringUtils.EMPTY					,"3rem", ""  )
		;

		String headerLabel;
		String colWidth;
		String styles;

		private COLS(String headerLabel,String colWidth,String styles) {
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
	
	public RDirStaffTable(String domainName, int domain, String user, Integer registry) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.domainName = domainName;
		this.domain = domain;
		this.user = user;
		this.registry = registry;
		
		container = new SimplePanel();
		container.getElement().getStyle().setProperty("max-height", "200px");
		container.getElement().getStyle().setProperty("padding-left", "1px");
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
	
	public void onSearch() {
		enableMoreData();
		search();
	}

	private void search() {
		container.clear();
		tab = new AonCustomTable();
		tab.setMaxHeight("160x");
		scrollPanel = new ScrollPanel(tab);
		
		paintHeader();
		container.setWidget(scrollPanel);
		searchData();
	}
	
	private void paintHeader() {
		tab.createHeader();
		for ( COLS col : COLS.values()) {
			if(col.equals(COLS.BUT)) {
				FlowPanel buttonContainer = new FlowPanel();
				buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
				
				AonTableButton button = new AonTableButton("Nuevo Representante", AON.CSS.aonIconAdd());
				button.addStyleName(AON.CSS.aonCustomRowButtom());
				button.addClickHandler(e -> createRDirStaff());
				buttonContainer.add(button);
				
				tab.addHeader(buttonContainer, col.getColWidth(), col.getStyles());
			} else
				tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
		
		}
	}
	
	private void searchData() {
		if (!isMoreData()) return;
		
		getList(rDirStaffs -> {
			boolean something = false;
			
			for(RDirStaff rDirStaff : rDirStaffs) {
				something = true;
				paintRow(rDirStaff);
			}
			
			if (rDirStaffs.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + rDirStaffs.size() - 1);
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
	
	private void paintRow(RDirStaff rDirStaff) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		
		AonTableButton button;
		button = new AonTableButton("Borrar Representate", AON.CSS.aonIconDelete());
		button.addStyleName(AON.CSS.aonCustomRowButtom());
		button.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				button.setEnabled(false);
				AonDialog dialog = new AonDialog("Eliminaci\u00f3n Representate",
						new HTML("Se va a proceder a eliminar el representate <b>" + rDirStaff.getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
				
				dialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						button.setEnabled(true);
					}

					@Override
					public void onAccept() {
						delete(rDirStaff);
					}
				});
			}
		});
		buttonContainer.add(button);
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onUpdateRDirStaff(rDirStaff), ClickEvent.getType());
		
		Label nif = new Label(rDirStaff.getDocument());
		nif.setTitle(rDirStaff.getDocument());
		tab.addInlineStyle(nif, COLS.DOC.getStyles());
		tab.addRow(row, nif, COLS.DOC.getColWidth());
		
		Label name = new Label(rDirStaff.getName());
		name.setTitle(rDirStaff.getName());
		tab.addInlineStyle(name, COLS.NAM.getStyles());
		tab.addRow(row, name, COLS.NAM.getColWidth());
		
		AonTableButton legal = rDirStaff.getRepresentative() ? new AonTableButton("Activo", AON.CSS.aonIconCheckCircle()) : new AonTableButton("Inactivo", AON.CSS.aonIconBlock());
		legal.setTitle(rDirStaff.getRepresentative() ? "Activo" : "Inactivo");
		tab.addInlineStyle(legal, COLS.RPL.getStyles());
		tab.addRow(row, legal, COLS.RPL.getColWidth());
		
		AonTableButton laboral = rDirStaff.getRepresentativeLabor() ? new AonTableButton("Activo", AON.CSS.aonIconCheckCircle()) : new AonTableButton("Inactivo", AON.CSS.aonIconBlock());
		laboral.setTitle(rDirStaff.getRepresentativeLabor() ? "Activo" : "Inactivo");
		tab.addInlineStyle(laboral, COLS.RPP.getStyles());
		tab.addRow(row, laboral, COLS.RPP.getColWidth());
		
		AonTableButton socio = rDirStaff.getShareHolder() ? new AonTableButton("Activo", AON.CSS.aonIconCheckCircle()) : new AonTableButton("Inactivo", AON.CSS.aonIconBlock());
		socio.setTitle(rDirStaff.getShareHolder() ? "Activo" : "Inactivo");
		tab.addInlineStyle(socio, COLS.SOC.getStyles());
		tab.addRow(row, socio, COLS.SOC.getColWidth());
		
		AonTableButton admin = rDirStaff.getDirector() ? new AonTableButton("Activo", AON.CSS.aonIconCheckCircle()) : new AonTableButton("Inactivo", AON.CSS.aonIconBlock());
		admin.setTitle(rDirStaff.getDirector() ? "Activo" : "Inactivo");
		tab.addInlineStyle(admin, COLS.ADM.getStyles());
		tab.addRow(row, admin, COLS.ADM.getColWidth());
		
		Label accionesPer = new Label(formatNumber(rDirStaff.getPercentShare()));
		accionesPer.setTitle(formatNumber(rDirStaff.getPercentShare()));
		tab.addInlineStyle(accionesPer, COLS.PAC.getStyles());
		tab.addRow(row, accionesPer, COLS.PAC.getColWidth());
		
		Label accionesNum = new Label(null != rDirStaff.getShareNumber() ? rDirStaff.getShareNumber().toString() : "0");
		accionesNum.setTitle(null != rDirStaff.getShareNumber() ? rDirStaff.getShareNumber().toString() : "0");
		tab.addInlineStyle(accionesNum, COLS.NAC.getStyles());
		tab.addRow(row, accionesNum, COLS.NAC.getColWidth());
		
		Label nominal = new Label(formatNumber(rDirStaff.getNominalValue()));
		nominal.setTitle(formatNumber(rDirStaff.getNominalValue()));
		tab.addInlineStyle(nominal, COLS.VAL.getStyles());
		tab.addRow(row, nominal, COLS.VAL.getColWidth());
		
		Label caducidad = new Label(null != rDirStaff.getDueDate() ? formatDate.format(rDirStaff.getDueDate()) : "");
		caducidad.setTitle(null != rDirStaff.getDueDate() ? formatDate.format(rDirStaff.getDueDate()) : "");
		tab.addInlineStyle(caducidad, COLS.CAD.getStyles());
		tab.addRow(row, caducidad, COLS.CAD.getColWidth());
		
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}
	
	private static String formatNumber(double value) {
        // Round to two decimal places
        long scaledValue = Math.round(value * 100); // Scale to avoid floating-point precision issues
        long integerPart = scaledValue / 100;      // Extract integer part
        long decimalPart = scaledValue % 100;      // Extract decimal part

        // Format the result
        return integerPart + "." + (decimalPart < 10 ? "0" : "") + decimalPart;
    }
	
	private void getList(Consumer<List<RDirStaff>> success) {
		COMMON_SERVICE.getRDirStaffs(domainName, domain, user, registry, new AsyncCallback<List<RDirStaff>>() {
			
			@Override
			public void onSuccess(List<RDirStaff> rDirStaffs) {
				success.accept(rDirStaffs);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
	private void delete(RDirStaff rDirStaff ) {
		COMMON_SERVICE.deleteRDirStaff(domainName, domain, user, rDirStaff.getId(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				onSearch();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error borrado: " + caught.getMessage());
			}
		});
	}
	
	private void onUpdateRDirStaff(RDirStaff rDirStaff) {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption("Editar Representate");
		
		final AonRDirStaffPanel aonRDirStaffPanel = new AonRDirStaffPanel( domainName, domain, user, rDirStaff, new AonRDirStaffPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept(RDirStaff rDirStaff) {
				dialog.hide();
				onSearch();
			}
		});
		
		dialog.add( aonRDirStaffPanel );
		dialog.showLoaded();
	}
	


	private void createRDirStaff() {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption("Nuevo Representante");

		final AonRDirStaffPanel aonRDirStaffPanel = new AonRDirStaffPanel(domainName, domain, user, registry, new AonRDirStaffPanelCallback() {

					@Override
					public void onCancel() {
						dialog.hide();
					}

					@Override
					public void onAccept(RDirStaff rDirStaff) {
						dialog.hide();
						onSearch();
					}
				});

		dialog.add(aonRDirStaffPanel);
		dialog.showLoaded();
		
	}

	protected abstract void onShowErrorMessage(String errorMessage);
	
}

