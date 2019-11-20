package com.esferalia.aon.gwt.fiscal.client.tedi;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.occam.api.model.tedi.TediCompanyResult;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;

public class TediCompanyTable extends CellTable<TediCompanyResult> {
	private static final CellTable.Resources TABLE_STYLE = GWT.create(AonCellTable.class);

	private NoSelectionModel<TediCompanyResult> model;
	
	private static TediServiceAsync SERVICE;
	
	String domainName;
	Integer domainId;
	String user;
	Boolean snapshot;
	
	public TediCompanyTable(String domainName, Integer domainId, String user, Boolean snapshot, ProvidesKey<TediCompanyResult> providesKey) {
		super(1, TABLE_STYLE, providesKey);
		
		TediServiceAsync serviceRaw = GWT.create(TediService.class);
		SERVICE = new TediServiceAsyncDecorator(serviceRaw);
		
		this.domainName = domainName;
		this.domainId = domainId;
		this.user = user;
		this.snapshot = snapshot;
		this.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
		this.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		addDocumentColumn();
		addNameColumn();
		addInboxCountColumn();

		model = new NoSelectionModel<TediCompanyResult>(providesKey);
		this.setSelectionModel(model);
		this.setEmptyTableWidget(new HTML(AON.MSG.noData()));
	}

	public void addSelectionChangeHandler(SelectionChangeEvent.Handler handler) {
		model.addSelectionChangeHandler(handler);
	}

	private void addDocumentColumn() {
		final TextColumn<TediCompanyResult> documentColumn = new TextColumn<TediCompanyResult>() {
			@Override
			public String getValue(TediCompanyResult result) {
				return result.getCompany().getDocument();
			}
		};
		this.addColumn(documentColumn, AON.MSG.document());
		this.setColumnWidth(documentColumn, 100, Unit.PX);
	}

	private void addNameColumn() {
		final TextColumn<TediCompanyResult> nameColumn = new TextColumn<TediCompanyResult>() {
			@Override
			public String getValue(TediCompanyResult result) {
				return result.getCompany().getName();
			}
		};
		this.addColumn(nameColumn, AON.MSG.name());
		this.setColumnWidth(nameColumn, "auto");
	}
	
	private void addInboxCountColumn() {
		List<HasCell<TediCompanyResult, ?>> cells = new LinkedList<HasCell<TediCompanyResult, ?>>();
		cells.add(new ActionHasCell(new Delegate<TediCompanyResult>() {

			@Override
			public void execute(TediCompanyResult object) {
				addTediCompany(object);
			}
		}));
		
		CompositeCell<TediCompanyResult> cell = new CompositeCell<TediCompanyResult>(cells);

		final Column<TediCompanyResult,TediCompanyResult> inboxCountColumn = 	new Column<TediCompanyResult, TediCompanyResult>(cell){

			@Override
			public TediCompanyResult getValue(TediCompanyResult object) {
				return object;
			}
		};
		this.addColumn(inboxCountColumn, "Pendientes");
		this.setColumnWidth(inboxCountColumn, 100, Unit.PX);
	}
	
	private class ActionHasCell implements HasCell<TediCompanyResult, TediCompanyResult> {
	    private ActionCell<TediCompanyResult> cell;
	    
	    public ActionHasCell(Delegate<TediCompanyResult> delegate) {
	        cell = new ActionCell<TediCompanyResult>("", delegate){
	        	@Override
	        	public void render(com.google.gwt.cell.client.Cell.Context context, TediCompanyResult value, SafeHtmlBuilder sb) {
	        		if(value.getTedi() != null && !value.getTedi()) {
						sb.appendHtmlConstant("<button type=\"button\"  class=\"aon-editDataTable-button aon-icon-new\" tabindex=\"-1\">");
						sb.appendHtmlConstant("</button>");		
					} else if(value.getInboxCount() != null){
						sb.appendHtmlConstant("<span>"+value.getInboxCount()+"</span>");
					} else {
						sb.appendHtmlConstant("<div class='aon-loader'>&nbsp;</div>");
					}
	        	}
	        };

	    }

	    @Override
	    public Cell<TediCompanyResult> getCell() {
	        return cell;
	    }

	    @Override
	    public FieldUpdater<TediCompanyResult, TediCompanyResult> getFieldUpdater() {
	        return null;
	    }

	    @Override
	    public TediCompanyResult getValue(TediCompanyResult object) {
	        return object;
	    }
	}
	
	public void addTediCompany(TediCompanyResult object) {
		if(object.getTedi() != null && !object.getTedi()) {
			SERVICE.addTediCompany(domainName, user, domainId, snapshot, object, new AsyncCallback<Void>() {
		
				@Override public void onFailure(Throwable caught) {}
				@Override public void onSuccess(Void result) {}
			});
		}
	}
	
	public TediCompanyResult getSelected() {
		return model.getLastSelectedObject();
	}
}
