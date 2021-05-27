package com.esferalia.aon.gwt.fiscal.client.mod390;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SelectionChangeEvent.HasSelectionChangedHandlers;

public class ErrorPage extends ResizeComposite implements HasSelectionChangedHandlers  {

	interface NavigateButtonTemplate extends SafeHtmlTemplates {
		@Template("<input type=\"button\" value=\"&nbsp;\" class=\"aon-icon-go\" style=\"border: medium none !important;\">")
		SafeHtml render(String option);
	}
	static class NavigateButtonSafeHtmlTemplates implements SafeHtmlRenderer<String> {

		private static NavigateButtonTemplate template;

		protected NavigateButtonSafeHtmlTemplates() {
			template = GWT.create(NavigateButtonTemplate.class);
		}
		
		@Override
		public SafeHtml render(String object) {
			return template.render(object);
		}

		@Override
		public void render(String object, SafeHtmlBuilder builder) {
			builder.append( template.render(object) );
		}
		
	}

	private ListDataProvider<ValidationMessage> dataProvider;
	private NoSelectionModel<ValidationMessage> model;
	
	@UiField(provided = true)
	CellTable<ValidationMessage> table;
	
	
	public ErrorPage( ) {
		CellTable.Resources tableStyle = GWT.create(AonCellTable.class);
		
		table = new CellTable<ValidationMessage>(1, tableStyle);
		table.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		
		addNavigateColumn();
		addPageColumn();
		addBoxColumn();
		addMessageColumn();
		
		table.setEmptyTableWidget(new HTML(AON.MSG.noData()));
		dataProvider= new ListDataProvider<ValidationMessage>();
		dataProvider.addDataDisplay(table);
		
		model = new NoSelectionModel<ValidationMessage>();
		table.setSelectionModel(model);
		
		ScrollPanel sp = new ScrollPanel();
		sp.setStyleName(AON.AON_CSS.aonScrollArea());
		sp.setWidget(table);
		initWidget(sp);
	}
	
	public void addErrorMsg(LinkedList<ValidationMessage> messages) {
		dataProvider.setList(messages);
		table.setPageSize(messages.size());
		table.redraw();
	}
	public void addErrorMsg(String msg) {
		addMessage(msg);
	}
	public void addErrorMsg(Throwable t) {
		String msg = (t.getCause() != null)?t.getCause().getMessage():t.getMessage();
		if (AonStringUtils.isEmpty(msg)) {
			msg = t.getMessage();
		}
		if (AonStringUtils.isEmpty(msg)) {
			msg = "Se ha producido un error inesperado. ";
					
		}
		addMessage(msg);
	}
	private void addMessage(String message) {
		addMessage(new ValidationMessage(-1,null,message,null));
	}
	
	private void addMessage(ValidationMessage msg) {
		dataProvider.getList().add(msg);
		table.setPageSize( dataProvider.getList().size() );
		table.redraw();		    		
	}
	
	private void addPageColumn() {
		Column<ValidationMessage,String> col = new TextColumn<ValidationMessage>() {

			@Override
			public String getValue(ValidationMessage errorMessage) {
				return errorMessage.getPage()>=0?Integer.toString(errorMessage.getPage()+1):"";
			}
			
		};
		table.addColumn(col,AON.MSG.page());
		table.setColumnWidth(col, 70, Unit.PX);
		col.setCellStyleNames(AON.AON_CSS.aonTextLeft());
	}
	
	private void addBoxColumn() {
		Column<ValidationMessage,String> col = new TextColumn<ValidationMessage>() {

			@Override
			public String getValue(ValidationMessage errorMessage) {
				return errorMessage.getKey(); 
			}
			
		};
		table.addColumn(col,AON.MSG.box());
		table.setColumnWidth(col, 70, Unit.PX);
		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
	}
	private void addMessageColumn() {
		Column<ValidationMessage,String> col = new TextColumn<ValidationMessage>() {

			@Override
			public String getValue(ValidationMessage errorMessage) {
				return errorMessage.getMessage();
			}
			
		};
		table.addColumn(col,AON.MSG.message());
		col.setCellStyleNames(AON.AON_CSS.aonIconError());
	}

	private void addNavigateColumn() {
		ButtonCell navigateButton = new ButtonCell( new NavigateButtonSafeHtmlTemplates())  {
			  @Override
			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
			    if (data != null) {
			      sb.append(data);
			    }
			  }
		};
		Column<ValidationMessage,String> col = new Column<ValidationMessage,String>(navigateButton) {
		  public String getValue(ValidationMessage object) {
		    return AON.MSG.goAction();
		  }
		};
		col.setFieldUpdater(new FieldUpdater<ValidationMessage, String>() {
		    public void update(int index, ValidationMessage ca, String value) {
		    	SelectionChangeEvent.fire(ErrorPage.this);
		    }
		});		
		table.addColumn(col);
		table.setColumnWidth(col, 20, Unit.PX);
		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
	}

	@Override
	public HandlerRegistration addSelectionChangeHandler(Handler handler) {
		return model.addSelectionChangeHandler( handler );
	}
	
	public ValidationMessage getSelected() {
		return model.getLastSelectedObject();
	}


}
