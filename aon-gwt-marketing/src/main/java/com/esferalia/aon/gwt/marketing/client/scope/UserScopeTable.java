package com.esferalia.aon.gwt.marketing.client.scope;

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
import com.esferalia.aon.occam.api.model.scope.UserScopeFull;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class UserScopeTable extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(UserScopeTable.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private SimplePanel container;
	private ScrollPanel scrollPanel;
	private AonCustomTable tab;
	
	private String domainName;
	private Integer domain;
	private String user;
	
	private Scope scope;
	
	private static enum COLS {
		  USR("Usuario"								,"-moz-available"	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, BUT(AonStringUtils.EMPTY					,"2rem"				,"")
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
	
	public UserScopeTable(String domainName, int domain, String user, Scope scope) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.domainName = domainName;
		this.domain = domain;
		this.user = user;
		this.scope = scope;
		
		container = new SimplePanel();
		container.getElement().getStyle().setProperty("padding-left", "1px");
		setWidget(container);
		
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
		getUserScopes(this.scope.getId(), userScopes -> {
			if(userScopes.isEmpty()) {
				FlowPanel line = new FlowPanel();
				InlineLabel label = new InlineLabel("No existen usuarios para el \u00c1mbito seleccionado");
				line.add(label);
				container.clear();
				container.add(line);
			} else 
				userScopes.forEach(userScope -> paintRow(userScope) );
			
		});
	}
	
	private void paintRow(UserScopeFull userScope) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		AonTableButton deleteButton = new AonTableButton("Borrar Usuario", AON.CSS.aonIconDelete());
		deleteButton.addStyleName(AON.CSS.aonCustomRowButtom());
		deleteButton.addClickHandler(e -> {
			e.stopPropagation();
			deleteButton.setEnabled(false);
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n Usuario",
					new HTML("Se va a proceder a eliminar el \u00c1mbito <b>" + userScope.getScope().getDescription() + "</b> del usuarui <b>" + userScope.getUser().getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					deleteButton.setEnabled(true);
				}

				@Override
				public void onAccept() {
					deleteUserScope(userScope.getId(), deleted -> onDelete());
				}
			});
		});
		buttonContainer.add(deleteButton);
		
		HTMLPanel row = tab.createRow();
		
		String user = userScope.getUser().getName();
		Label type = new Label(user);
		type.setTitle(user);
		tab.addInlineStyle(type, COLS.USR.getStyles());
		tab.addRow(row, type, COLS.USR.getColWidth());
		
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}

	private void deleteUserScope(Integer userScopeId, Consumer<Void> success) {
		COMMON_SERVICE.deleteUserScope(domainName, domain, user, userScopeId, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void deleted) {
				success.accept(deleted);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}
	
	private void getUserScopes(Integer scopeId, Consumer<List<UserScopeFull>> success) {
		COMMON_SERVICE.getUserScopeList(domainName, domain, user, scopeId, new AsyncCallback<List<UserScopeFull>>() {
			
			@Override
			public void onSuccess(List<UserScopeFull> userScopesDB) {
				success.accept(userScopesDB);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}

	protected abstract void onDelete();
	protected abstract void onUserScopeAdd();
	
}

