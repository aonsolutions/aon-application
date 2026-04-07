package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMediaPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMediaPanel.AonMediaPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class MediaTable extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(MediaTable.class.getName());
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
		  TYP(AON.MSG.type()						,"4rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" )
		, VAL("Valor"								,"-moz-available", "min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" )
		, ADM("Admin."								,"4rem", ""  )
		, COM("Comercial"							,"5rem", ""  )
		, TEC("Tecnico"								,"4rem", ""  )
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
	
	public MediaTable(String domainName, int domain, String user, Integer registry) {
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
		for ( COLS col : COLS.values()) 
			tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
	}
	
	private void searchData() {
		if (!isMoreData()) return;
		
		getList(registryMedias -> {
			boolean something = false;
			
			for(RegistryMedia registryMedia : registryMedias) {
				something = true;
				paintRow(registryMedia);
			}
			
			if (registryMedias.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + registryMedias.size() - 1);
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
	
	private void paintRow(RegistryMedia registryMedia) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		
		AonTableButton button;
		button = new AonTableButton("Borrar media", AON.CSS.aonIconDelete());
		button.addStyleName(AON.CSS.aonCustomRowButtom());
		button.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				button.setEnabled(false);
				AonDialog dialog = new AonDialog("Eliminaci\u00f3n Media",
						new HTML("Se va a proceder a eliminar el medio <b>" + registryMedia.getValue() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
				
				dialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						button.setEnabled(true);
					}

					@Override
					public void onAccept() {
						delete(registryMedia);
					}
				});
			}
		});
		buttonContainer.add(button);
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onUpdateRMedia(registryMedia), ClickEvent.getType());
		
		Label mediaType = new Label(registryMedia.getMedia().getDescription());
		mediaType.setTitle(registryMedia.getMedia().getDescription());
		tab.addInlineStyle(mediaType, COLS.TYP.getStyles());
		tab.addRow(row, mediaType, COLS.TYP.getColWidth());
		
		Label value = new Label(registryMedia.getValue());
		value.setTitle(registryMedia.getValue());
		tab.addInlineStyle(value, COLS.VAL.getStyles());
		tab.addRow(row, value, COLS.VAL.getColWidth());
		
		Button administrative = new Button();
		getEnableDisableButton(administrative, registryMedia.isAdministrative());
		tab.addRow(row, administrative, COLS.ADM.getColWidth());
		
		Button commercial = new Button();
		getEnableDisableButton(commercial, registryMedia.isCommercial());
		tab.addRow(row, commercial, COLS.COM.getColWidth());
		
		Button technical = new Button();
		getEnableDisableButton(technical, registryMedia.isTechnical());
		tab.addRow(row, technical, COLS.TEC.getColWidth());
		
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}
	
	private void getEnableDisableButton(Button button, boolean disabled) {
		button.removeStyleName(disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
		button.removeStyleName(AON.AON_NO_MARGIN);
		button.removeStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);
		
		button.setStyleName(!disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE );
		button.setStyleName(AON.AON_NO_MARGIN, true);
		button.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
	}
	
	private void getList(Consumer<List<RegistryMedia>> success) {
		COMMON_SERVICE.getRegistryMedias(domainName, domain, user, registry, new AsyncCallback<List<RegistryMedia>>() {
			
			@Override
			public void onSuccess(List<RegistryMedia> registryMedias) {
				success.accept(registryMedias);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
	private void delete(RegistryMedia registryMedia ) {
		COMMON_SERVICE.deleteRegistryMedia(domainName, domain, user, registryMedia.getId(), new AsyncCallback<Void>() {
			
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
	
	private void onUpdateRMedia(RegistryMedia registryMedia) {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption("Editar Contacto");
		
		final AonMediaPanel marketingCampaignPanel = new AonMediaPanel( domainName, domain, user, registryMedia, new AonMediaPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept(RegistryMedia media) {
				dialog.hide();
				onSearch();
			}
		});
		
		dialog.add( marketingCampaignPanel );
		dialog.showLoaded();
	}

	protected abstract void onShowErrorMessage(String errorMessage);
	
}

