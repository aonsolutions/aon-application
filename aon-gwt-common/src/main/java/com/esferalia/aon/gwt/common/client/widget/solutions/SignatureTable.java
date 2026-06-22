package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog.AonCustomDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSignaturePanel.AonSignaturePanelCallback;
import com.esferalia.aon.occam.api.model.Signature;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class SignatureTable extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(SignatureTable.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private HTMLPanel content;
	
	private SimplePanel container;
	private ScrollPanel scrollPanel;
	private AonCustomTable tab;
	
	private String domainName;
	private Integer domain;
	private String user;
	
	private List<Signature> signatures = new ArrayList<Signature>();
	//private boolean showUserEmails = true;
	private boolean showAdd = true;
	private String searchPattern = null;
	private String typeFilter = "all";
	
	private static enum COLS {
		  TYP("Tipo"								, "5rem"			, "max-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, NAM(AON.MSG.name()						, "-moz-available"	, "min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, BUT(AonStringUtils.EMPTY					, "3rem"			,"")
		;

		String headerLabel;
		String colWidth;
		String styles;

		private COLS(String headerLabel,String colWidth, String styles) {
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
	
	public SignatureTable(String domainName, int domain, String user, boolean showAdd) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.domainName = domainName;
		this.domain = domain;
		this.user = user;
		this.showAdd = showAdd;
		
		content = new HTMLPanel("");
		content.addStyleName(AON.CSS.aonFlexColumn2());

		container = new SimplePanel();
		container.getElement().getStyle().setProperty("padding-left", "1px");
		content.add(container);
		
		setWidget(content);
		
		loadData();
	}
	
	private void loadData() {
		getList(sign -> {
			onSearch();
		});
	}

	public void onSearch() {
		search();
	}

	private void search() {
		container.clear();
		tab = new AonCustomTable();
		tab.setMaxHeight("160x");
		tab.getElement().getStyle().setProperty("padding", "1rem 0");
		scrollPanel = new ScrollPanel(tab);
		
		paintHeader();
		container.setWidget(scrollPanel);
		searchData();
	}
	
	private void paintHeader() {
		tab.createHeader();
		for ( COLS col : COLS.values()) {
			if(this.showAdd && col.equals(COLS.BUT)) {
				FlowPanel buttonContainer = new FlowPanel();
				buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
				
				AonTableButton button = new AonTableButton("Nueva firma", AON.CSS.aonIconAdd());
				button.addStyleName(AON.CSS.aonCustomRowButtom());
				button.getElement().getStyle().setProperty("border", "2px solid #434548");
				button.getElement().getStyle().setProperty("padding", "10px");
				button.getElement().getStyle().setProperty("border-radius", "50%");
				button.addClickHandler(e -> createSignature());
				buttonContainer.add(button);
				
				tab.addHeader(buttonContainer, col.getColWidth(), col.getStyles());
			} 
			/*
			else if (col.equals(COLS.TYP)) {
				FlowPanel buttonContainer = new FlowPanel();
				buttonContainer.getElement().getStyle().setTextAlign(TextAlign.LEFT);

				CheckBox showActive = new CheckBox("Usr.");
				showActive.setTitle("Todos");
				showActive.addStyleName(AON.CSS.aonCustomRowButtom());
				showActive.setValue(showUserEmails);
				showActive.getElement().getStyle().setProperty("flex-direction", "row-reverse");
				showActive.addValueChangeHandler(e -> {
					showUserEmails = !showUserEmails;
					onSearch();
				});
				buttonContainer.add(showActive);

				tab.addHeader(buttonContainer, col.getColWidth(), col.getStyles());
			} 
			*/
			else
				tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
		
		}
	}
	
	private void searchData() {
		boolean something = false;
		
		List<Signature> signatureListTable = signatures.stream()
				//.filter(s -> showUserEmails || (!showUserEmails && null == s.getUserId()) )
				.filter(s -> 
							AonStringUtils.equalsIgnoreCase(typeFilter, "all")
						|| (AonStringUtils.equalsIgnoreCase(typeFilter, "enterprise") && null == s.getUserId()) 
						|| (AonStringUtils.equalsIgnoreCase(typeFilter, "user") && null != s.getUserId())
				)
				.filter(s ->  AonStringUtils.isBlank(searchPattern) || AonStringUtils.containsIgnoreCase(s.getName(), searchPattern) )
				.collect(Collectors.toList());
		
		
		for(Signature signature : signatureListTable) {
			something = true;
			paintRow(signature);
		}
		
		if (!something) {
			HTMLPanel row = tab.createRow();
			
			Label empty = new Label("No exiten datos");
			tab.addInlineStyle(empty, COLS.NAM.getStyles());
			tab.addRow(row, empty, COLS.NAM.getColWidth());
		}
	}
	
	private void paintRow(Signature signature) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		
		AonTableButton button;
		button = new AonTableButton("Borrar firma", AON.CSS.aonIconDelete());
		button.addStyleName(AON.CSS.aonCustomRowButtom());
		button.addClickHandler(e -> {
			e.stopPropagation();
			button.setEnabled(false);
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n Firma",
					new HTML("Se va a proceder a eliminar la firma <b>" + signature.getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					button.setEnabled(true);
				}

				@Override
				public void onAccept() {
					delete(signature);
				}
			});
		});
		buttonContainer.add(button);
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onUpdateSignature(signature), ClickEvent.getType());
		
		Label type = new Label(signature.getUserId() != null ? "Usuario" : "Empresa");
		type.setTitle(signature.getName());
		tab.addInlineStyle(type, COLS.TYP.getStyles());
		tab.addRow(row, type, COLS.TYP.getColWidth());
		
		Label name = new Label(signature.getName());
		name.setTitle(signature.getName());
		tab.addInlineStyle(name, COLS.NAM.getStyles());
		tab.addRow(row, name, COLS.NAM.getColWidth());
		
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}

	private void getList(Consumer<List<Signature>> success) {
		COMMON_SERVICE.getSignatures(domainName, domain, user, new AsyncCallback<LinkedList<Signature>>() {
			
			@Override
			public void onSuccess(LinkedList<Signature> signaturesDB) {
				signaturesDB.sort(
					    Comparator.comparing(
					    	Signature::getUserId,
					        Comparator.nullsFirst((u1, u2) -> 0) // solo agrupa null primero
					    ).thenComparing(Signature::getName)
					);
				
				signatures = signaturesDB;
				success.accept(signatures);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
	private void delete(Signature signature) {
		COMMON_SERVICE.deleteSignature(domainName, domain, user, signature.getId(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				loadData();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error borrado: " + caught.getMessage());
			}
		});
	}
	
	private void onUpdateSignature(Signature signature) {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "Editar Firma" );
		
		final AonSignaturePanel aonSignaturePanel = new AonSignaturePanel( domainName, domain, user, signature, new AonSignaturePanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept(Signature signature) {
				dialog.hide();
				loadData();
			}
		});
		
		dialog.add( aonSignaturePanel );
		dialog.showLoaded();
	}

	public void createSignature() {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption("Nueva Firma");

		final AonSignaturePanel aonSignaturePanel = new AonSignaturePanel(domainName, domain, user, new AonSignaturePanelCallback() {

					@Override
					public void onCancel() {
						dialog.hide();
					}

					@Override
					public void onAccept(Signature signature) {
						dialog.hide();
						loadData();
					}
				});
		
		dialog.add(aonSignaturePanel);
		dialog.showLoadedCB(new AonCustomDialogCallback() {
			@Override public void onEnd() { aonSignaturePanel.name.setFocus(true); }
		});
		
	}
	
	public void setSearchPattern(String searchPattern) {
		this.searchPattern = searchPattern;
	}
	
	public void setTypeFilter(String typeFilter) {
		this.typeFilter = typeFilter;
	}

	protected abstract void onShowErrorMessage(String errorMessage);
	
}

