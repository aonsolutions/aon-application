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
import com.esferalia.aon.occam.api.model.attachment.Attach;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class RattachTable extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(RattachTable.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private static final String DOWNLOADURL = GWT.getModuleBaseURL() + "attach/download/";
	
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
		  VAL(AON.MSG.description()					,"-moz-available" )
		, ADM("Visibilidad"							,"10rem")
		, COM("Fecha"								,"10rem")
		, TEC(AON.MSG.scope()						,"30rem")
		, BUT(AonStringUtils.EMPTY					,"5rem" )
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
	
	public RattachTable(String domainName, int domain, String user, Integer registry) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.domainName = domainName;
		this.domain = domain;
		this.user = user;
		this.registry = registry;

		container = new SimplePanel();
		container.setHeight((Window.getClientHeight() - 310) + "px");
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
		tab.setMaxHeight((Window.getClientHeight() - 300) + "px");
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
		
		getList(attaches -> {
			boolean something = false;
			
			for(Attach attach : attaches) {
				something = true;
				paintRow(attach);
			}
			
			if (attaches.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + attaches.size() - 1);
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
	
	private void paintRow(Attach attach) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		
		AonTableButton download;
		download = new AonTableButton("Descargar documento", AON.CSS.aonIconDownload());
		download.addStyleName(AON.CSS.aonCustomRowButtom());
		download.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				download.setEnabled(false);
				createDownloadForm(attach);
			}

			private void createDownloadForm(Attach attach) {
				// Hiddens
				Hidden userLoginHidden = new Hidden("login", user);
				Hidden currentDomainHidden = new Hidden("domain", domainName);
				Hidden attachIdHidden = new Hidden("attachId", attach.getId() + "");
				Hidden attachTypeHidden = new Hidden("attachType", "registry");
				
				// Create Form Panel
				FormPanel form = new FormPanel();
				form.setAction(DOWNLOADURL);
				form.setEncoding(FormPanel.ENCODING_MULTIPART);
				form.setMethod(FormPanel.METHOD_POST);
				form.addSubmitCompleteHandler(e -> buttonContainer.remove(form));
				
				// Add all to FlowPanel to add to FormPanel
				HTMLPanel flowFormPanel = new HTMLPanel("");
				flowFormPanel.add(userLoginHidden);
				flowFormPanel.add(currentDomainHidden);
				flowFormPanel.add(attachIdHidden);
				flowFormPanel.add(attachTypeHidden);
				form.add(flowFormPanel);
				buttonContainer.add(form);
				
				form.submit();
			}
		});
		buttonContainer.add(download);
		
		AonTableButton button;
		button = new AonTableButton("Borrar documento", AON.CSS.aonIconDelete());
		button.addStyleName(AON.CSS.aonCustomRowButtom());
		button.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				button.setEnabled(false);
				AonDialog dialog = new AonDialog("Eliminaci\u00f3n Documento",
						new HTML("Se va a proceder a eliminar el documento <b>" + attach.getDescription() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
				
				dialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						button.setEnabled(true);
					}

					@Override
					public void onAccept() {
						delete(attach);
					}
				});
			}
		});
		buttonContainer.add(button);
		
		HTMLPanel row = tab.createRow();
		tab.addRow(row, new Label(attach.getDescription()), COLS.VAL.getColWidth());
		tab.addRow(row, new AonTableButton(attach.isConfidential() ? "Confidencial" : "Publico", attach.isConfidential() ? AON.CSS.aonIconLock() : AON.CSS.aonIconUnLock()), COLS.ADM.getColWidth());
		tab.addRow(row, new Label(attach.getDate() == null ? "" : formatDate.format(attach.getDate())), COLS.COM.getColWidth());
		tab.addRow(row, new Label(attach.getFullScope() == null ? "" : attach.getFullScope().getDescription()), COLS.TEC.getColWidth());
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}
	
	private void getList(Consumer<List<Attach>> success) {
		COMMON_SERVICE.getRegistryAttaches(domainName, domain, user, registry, new AsyncCallback<List<Attach>>() {
			
			@Override
			public void onSuccess(List<Attach> attaches) {
				success.accept(attaches);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
	private void delete(Attach attach ) {
		COMMON_SERVICE.deleteRegistryAttach(domainName, domain, user, attach.getId(), new AsyncCallback<Void>() {
			
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

