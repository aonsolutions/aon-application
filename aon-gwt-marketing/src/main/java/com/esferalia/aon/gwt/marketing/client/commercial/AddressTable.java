package com.esferalia.aon.gwt.marketing.client.commercial;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class AddressTable extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(AddressTable.class.getName());
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
	
	private static enum COLS {
		  STR(""									, "3rem"	)
		, ADD(AON.MSG.address()						, "-moz-available"	)
		, NMB("N\u00b0"								, "5rem"	)
		, ZIP("C.P."								, "5rem"	)
		, PRO(AON.MSG.province()					, "20rem"	)
		, CIT("Localidad"							, "15rem"	)
		, BUT(AonStringUtils.EMPTY					, "3rem"	)
		;

		String headerLabel;
		String colWidth;

		private COLS(String headerLabel,String colWidth) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
	}
	
	public AddressTable(String domainName, int domain, String user, Integer registry) {
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
	
	private void onSearch() {
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
		for ( COLS col : COLS.values()) 
			tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth());
	}
	
	private void searchData() {
		if (!isMoreData()) return;
		
		getList(registryAddresses -> {
			boolean something = false;
			
			for(RegistryAddress registryAddress : registryAddresses) {
				something = true;
				paintRow(registryAddress);
			}
			
			if (registryAddresses.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + registryAddresses.size() - 1);
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
	
	private void paintRow(RegistryAddress registryAddress) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		
		AonTableButton button;
		button = new AonTableButton("Borrar direcci\u00f3n", AON.CSS.aonIconDelete());
		button.addStyleName(AON.CSS.aonCustomRowButtom());
		button.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				button.setEnabled(false);
				AonDialog dialog = new AonDialog("Eliminaci\u00f3n Direcci\u00f3n",
						new HTML("Se va a proceder a eliminar la direcci\u00f3n <b>" + registryAddress.getAddress() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
				
				dialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						button.setEnabled(true);
					}

					@Override
					public void onAccept() {
						delete(registryAddress);
					}
				});
			}
		});
		buttonContainer.add(button);
		
		HTMLPanel row = tab.createRow();
		tab.addRow(row, new Label(registryAddress.getStreetType().getAeatCode() + "."), COLS.STR.getColWidth());
		tab.addRow(row, new Label(registryAddress.getAddress()), COLS.ADD.getColWidth());
		tab.addRow(row, new Label(registryAddress.getNumber()), COLS.NMB.getColWidth());
		tab.addRow(row, new Label(registryAddress.getZip()), COLS.ZIP.getColWidth());
		tab.addRow(row, new Label(registryAddress.getGeozoneName()), COLS.PRO.getColWidth());
		tab.addRow(row, new Label(registryAddress.getCity()), COLS.CIT.getColWidth());
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}
	
	private void getList(Consumer<List<RegistryAddress>> success) {
		COMMON_SERVICE.getRegistryAddresses(domainName, domain, user, registry, new AsyncCallback<List<RegistryAddress>>() {
			
			@Override
			public void onSuccess(List<RegistryAddress> registryAddresses) {
				success.accept(registryAddresses);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
	private void delete(RegistryAddress registryAddress ) {
		COMMON_SERVICE.deleteRegistryAddress(domainName, domain, user, registryAddress.getId(), new AsyncCallback<Void>() {
			
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

	protected abstract void onShowErrorMessage(String errorMessage);
	
}

