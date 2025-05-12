package com.esferalia.aon.gwt.fiscal.client.target;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistrySeller;
import com.esferalia.aon.occam.api.model.registry.TargetFull;
import com.esferalia.aon.occam.api.model.target.TargetParams;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.RegistrySellerType;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class TargetEnterpriseList extends AonCustomDockLayout {
	
	private static CommonServiceAsync COMMON_SERVICE;
	
	// TariffList UI

	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private AonCustomDateBox date = new AonCustomDateBox("Fecha Creaci\u00f3n");
	
	private AonCustomListBox sort = new AonCustomListBox("Ordenar Por");
	private AonCustomListBox asc = new AonCustomListBox("Orden");
	
	private SimplePanel tableContainer;
	private ScrollPanel tableScrollPanel;
	private AonCustomTable tab;
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private RegistryModuleOptions options;
	
	private TargetParams params;
	
	// Table UI
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private int lastScrollPos = 0;

	private static enum COLS {
		  DOC("Document"							,"6rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, NAM("Nombre / Raz\u00f3n Social"			,"-moz-available"  	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, SEL("A. Soporte"							,"10rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, MAI("Email"								,"13rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, PHO(AON.MSG.phone()						,"6rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, CRU("Creado Por"							,"6rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, CRD("Creado (Fecha)"						,"7rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, BUT(AonStringUtils.EMPTY					,"3rem" 			,"")
		;

		String headerLabel;
		String colWidth;
		String cellStyleClass;

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
	
	// Constructor
	public TargetEnterpriseList(RegistryModuleOptions options) {
		super("Crear Empresa (C. Poteciales)");
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.options = options;
		
		hideToolbarFilterMessages();
		setSearchPlaceholder("Busque por nombre, documento ...");
		addKeyUpHandler(e -> {
			String value = getSearchTextBox().getValue();
			if(AonStringUtils.isNotBlank(value) && value.length() > 3) {
				onSearch();
			} else if(AonStringUtils.isBlank(value)) {
				onSearch();
			}
		});
		
		date.addValueChangeHandler(e -> onSearch());
		addFilterWidget(date);
		
		sort.addItem("Fecha", "date");
		sort.addItem("Nombre", "name");
		sort.addItem("Documento", "document");
		sort.setValue("name");
		sort.getListBox().addChangeHandler(event -> onSearch());
		
		asc.addItem("Ascendente", "true");
		asc.addItem("Descendete", "false");
		asc.getListBox().addChangeHandler(event -> onSearch());
		
		addSortWidget(sort);
		addSortWidget(asc);
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(messagePanel);
	
		tableContainer = new SimpleLayoutPanel();
		tableContainer.setHeight("100%");
		tableContainer.getElement().getStyle().setProperty("margin-left", "1rem");
		
		container.add(tableContainer);
		
		add(container);
		onSearch();
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	getSearchTextBox().setFocus(true);
	        }
	    });		
	}

	@Override
	protected void onClearFilter() {
		getSearchTextBox().setValue(null, false);
		date.setValue(null);
		sort.setValue("name");
		resetSearchOffset();
		onSearch();
	}
	
	private void resetSearchOffset() {
		offset.setValue(0);
	}

	public void onSearch() {
		getWidgetParams();
		resetSearchOffset();
		onSearchData();
	}

	public void getWidgetParams() {
		params = new TargetParams()
				.setDomainName(options.getDomainName())
				.setDomain(options.getDomain())
				.setUser(options.getUser())
				.setDescription(getSearchTextBox().getValue())
				.setDate(date.getValue())
				.setOrderBy(sort.getValue())
				.setAsc(Boolean.parseBoolean(asc.getValue()))
				;
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
	
	private void onSearchData() {
		enableMoreData();
		searchData();
	}
	
	private void searchData() {
		tableContainer.clear();
		tab = new AonCustomTable();
		tableScrollPanel = new ScrollPanel(tab);
		tableScrollPanel.getElement().getStyle().setProperty("margin", "0 1rem");
		
		tableScrollPanel.addScrollHandler(e -> {
			// ------------------------------------ Ignore scroll up.
			int oldScrollPos = lastScrollPos;
			lastScrollPos = tableScrollPanel.getVerticalScrollPosition();
			if (oldScrollPos >= lastScrollPos) {
				return;
			}
			// -----------------------------------------------------
			if (isSearchEnabled()) {
				int maxScrollTop = tableScrollPanel.getWidget().getOffsetHeight() - tableScrollPanel.getOffsetHeight();
				if (lastScrollPos >= maxScrollTop) {
					disableSearch();
					searchDataList();
				}
			}
		});
		
		paintHeader();
		tableContainer.add(tableScrollPanel);
		searchDataList();
	}
	
	private void paintHeader() {
		tab.createHeader();
		for ( COLS col : COLS.values()) 
			tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
	}
	
	private void searchDataList() {
		if (!isMoreData()) return;
		
		params.setOffset(offset.intValue());
		params.setLimit(limit);
		
		getList(targets -> {
			boolean something = false;
			
			for( Entry<TargetFull, List<RegistrySeller>> targetFull : targets.entrySet()) {
				something = true;
				paintRow(targetFull.getKey(), targetFull.getValue());
			}
			
			if (targets.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + targets.size() - 1);
				enableMoreData();
			}
			
			if (!something) {
				FlowPanel line = new FlowPanel();
				InlineLabel label = new InlineLabel(AON.MSG.noData());
				line.add(label);
				tableContainer.clear();
				tableContainer.add(line);
				disableMoreData();
			}
			enableSearch();
			
		});
	}
	
	private void paintRow(TargetFull target, List<RegistrySeller> sellers) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		AonTableButton processButton = new AonTableButton("Crear Empresa", AON.CSS.aonIconDomainAdd());
		processButton.addStyleName(AON.CSS.aonCustomRowButtom());
		processButton.addClickHandler(event -> {
			event.stopPropagation();
			
			new ProcessTargetEnterpriseDialog(target, sellers, params) {
				@Override
				public void onSaleProcess() { onSearch(); }
			};
			
		});
		buttonContainer.add(processButton);
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> {}, ClickEvent.getType());
		
		Label document = new Label(target.getRegistry().getDocument());
		tab.addInlineStyle(document, COLS.DOC.getCellStyleClass());
		tab.addRow(row, document, COLS.DOC.getColWidth());
		
		Label name = new Label(target.getRegistry().getName());
		tab.addInlineStyle(name, COLS.NAM.getCellStyleClass());
		tab.addRow(row, name, COLS.NAM.getColWidth());
		
		Optional<RegistrySeller> supportSellerOpt = sellers.stream().filter(seller -> seller.getType().equals(RegistrySellerType.SOPORTE)).findFirst();
		
		Label supportSeller = new Label(supportSellerOpt.isEmpty() ? "" : supportSellerOpt.get().getSeller().getName());
		tab.addInlineStyle(supportSeller, COLS.SEL.getCellStyleClass());
		tab.addRow(row, supportSeller, COLS.SEL.getColWidth());
		
		Optional<RegistryMedia> mailOpt = target.getMedias().stream().filter(media -> media.getMedia().equals(MediaType.EMAIL)).findFirst();
		
		Label mail = new Label(mailOpt.isEmpty() ? "" : mailOpt.get().getValue());
		tab.addInlineStyle(mail, COLS.MAI.getCellStyleClass());
		tab.addRow(row, mail, COLS.MAI.getColWidth());
		
		Optional<RegistryMedia> phoneOpt = target.getMedias().stream().filter(media -> media.getMedia().equals(MediaType.CELLULAR)).findFirst();
		
		Label phone = new Label(phoneOpt.isEmpty() ? "" : phoneOpt.get().getValue());
		tab.addInlineStyle(phone, COLS.PHO.getCellStyleClass());
		tab.addRow(row, phone, COLS.PHO.getColWidth());
		
		Label creationUser = new Label(target.getRegistry().getCreationUser());
		tab.addInlineStyle(creationUser, COLS.CRU.getCellStyleClass());
		tab.addRow(row, creationUser, COLS.CRU.getColWidth());
		
		Date creationDateValue = target.getRegistry().getCreationDate();
		
		Label creationDate = new Label(null == creationDateValue ? "" : formatDate.format(creationDateValue));
		tab.addInlineStyle(creationDate, COLS.CRD.getCellStyleClass());
		tab.addRow(row, creationDate, COLS.CRD.getColWidth());
		
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}
	
	private void getList(Consumer<Map<TargetFull, List<RegistrySeller>>> success) {
		COMMON_SERVICE.getTargetNotUserFull(params, new AsyncCallback<Map<TargetFull, List<RegistrySeller>>>() {
			
			@Override
			public void onSuccess(Map<TargetFull, List<RegistrySeller>> targetFullDb) {
				success.accept(targetFullDb);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error targets: " + caught.getMessage());
			}
		});
	}
	
}
