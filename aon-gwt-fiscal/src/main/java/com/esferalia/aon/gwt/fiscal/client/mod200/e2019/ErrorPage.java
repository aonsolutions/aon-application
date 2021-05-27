package com.esferalia.aon.gwt.fiscal.client.mod200.e2019;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod200.Model200Table;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.ValidationMessage2019;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.ValidationMessage2019.MessageType;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;

public class ErrorPage extends ResizeComposite implements HasSelectionHandlers<ValidationMessage2019>{

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

	private ListDataProvider<ValidationMessage2019> dataProvider;
	private CellTable<ValidationMessage2019> table;
	
	public ErrorPage( ) {
		ScrollPanel container = new ScrollPanel();
		container.setStyleName(AON.AON_CSS.aonScrollArea());
		FlowPanel basePanel = new FlowPanel();
		container.setWidget(basePanel);
		table = new CellTable<ValidationMessage2019>(1, Model200Table.TABLE_STYLE);
		table.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		NoSelectionModel<ValidationMessage2019> model = new NoSelectionModel<ValidationMessage2019>();
		model.addSelectionChangeHandler(new SelectionChangeEvent.Handler(){

			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				SelectionEvent.fire(ErrorPage.this, model.getLastSelectedObject());
			}
		});
		table.setSelectionModel(model);
		
		addPageColumn();
		addBoxColumn();
		addIconColumn();
		addMessageColumn();
		
		table.setEmptyTableWidget(new HTML(AON.MSG.noData()));
		dataProvider= new ListDataProvider<ValidationMessage2019>();
		dataProvider.addDataDisplay(table);
		basePanel.add(table);
		initWidget(container);
	}
	
	public void clearMessages() {
		dataProvider.setList( new LinkedList<ValidationMessage2019>());
		table.setPageSize(10);
		table.redraw();
	}
	public void addErrorMsg(List<ValidationMessage2019> messages) {
		dataProvider.setList(messages);
		table.setPageSize(messages.size());
		table.redraw();
	}
	public void addErrorMsg(String msg) {
		addMessage(msg,MessageType.ERROR);
	}
	public void addInfoMsg(String msg) {
		addMessage(msg,MessageType.INFO);
	}
	public void addErrorMsg(Throwable t) {
		String msg = (t.getCause() != null)?t.getCause().getMessage():t.getMessage();
		if (AonStringUtils.isEmpty(msg)) {
			msg = t.getMessage();
		}
		if (AonStringUtils.isEmpty(msg)) {
			msg = "Se ha producido un error inesperado. ";
					
		}
		addMessage(msg,MessageType.ERROR);
	}
	
	private void addMessage(String message,MessageType type) {
		addMessage(new ValidationMessage2019(-1,null,message,null,type));
	}
	
	private void addMessage(ValidationMessage2019 msg) {
		dataProvider.getList().add(msg);
		table.setPageSize( dataProvider.getList().size() );
		table.redraw();		    		
	}
	
	private void addPageColumn() {
		Column<ValidationMessage2019,String> col = new TextColumn<ValidationMessage2019>() {

			@Override
			public String getValue(ValidationMessage2019 errorMessage) {
				return errorMessage.getPage()>=0?Integer.toString(errorMessage.getPage()+1):"";
			}
			
		};
		table.addColumn(col,AON.MSG.page());
		table.setColumnWidth(col, 60, Unit.PX);
		col.setCellStyleNames(AON.AON_CSS.aonTextLeft());
	}
	
	private void addBoxColumn() {
		Column<ValidationMessage2019,String> col = new TextColumn<ValidationMessage2019>() {

			@Override
			public String getValue(ValidationMessage2019 errorMessage) {
				return errorMessage.getKey() != null
						? ("[" + AonStringUtils.leftPad(errorMessage.getKey().getCode( Administration.COMMON_TERRITORY)
								, Model2002019.BOX_LENGTH
								, AonStringUtils.ZERO) + "]")
						: AonStringUtils.EMPTY; 
			}
			
		};
		table.addColumn(col,AON.MSG.box());
		table.setColumnWidth(col, 60, Unit.PX);
		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
	}

	private void addIconColumn() {
		final Column<ValidationMessage2019, ImageResource> iconColumn = new Column<ValidationMessage2019, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(ValidationMessage2019 model) {
				if (model.getMessageType() == MessageType.WARNING) {
					return AON.AON_RESOURCES.aonIconWarn();	
				} else if (model.getMessageType() == MessageType.INFO) {
					return AON.AON_RESOURCES.aonIconInfo();
				}  
				return AON.AON_RESOURCES.aonIconError();
			}
		};
		table.addColumn(iconColumn, " " );
		table.setColumnWidth(iconColumn, 20, Unit.PX);
	}

	private void addMessageColumn() {
		Column<ValidationMessage2019,String> col = new TextColumn<ValidationMessage2019>() {

			@Override
			public String getValue(ValidationMessage2019 errorMessage) {
				return errorMessage.getMessage();
			}
			
		};
		table.addColumn(col,AON.MSG.message());
		col.setCellStyleNames(AON.AON_CSS.aonPadding2Left());
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<ValidationMessage2019> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
	
}
