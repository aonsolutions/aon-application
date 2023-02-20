package com.esferalia.aon.gwt.payroll.client;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTrashAgreementsTreeToolbar;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class TrashAgreements extends ResizeComposite implements 
	TrashAgreementsTree.Listener, AonTrashAgreementsTreeToolbar.Listener {

	interface Listener {
		
		void onCollapseMenuButtonClick();

		void onShowMenuButtonClick();
		
		void onBackButtonClick();
		
		void onAgreementSelected(Agreement agreement);

		void onAgreementSupr(Agreement agreement);
	}
	
	interface Toolbar {
		
		void onAgreementDelete4Ever(Agreement agreement);

		void onAgreementRestore(Agreement agreement);
	}

	interface Binder extends UiBinder<Widget, TrashAgreements> {}

	private static final Binder BINDER = GWT.create(Binder.class);

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String treeItem();
		String rotate();
		String paddingLeft();
	}
	
	@UiField
	AonTrashAgreementsTreeToolbar toolbar;
	
	@UiField
	TrashAgreementsTree agreementsTree;

	private Integer domain;
	
	private List<Listener> listeners;
	private List<Toolbar> toolbars;

	public TrashAgreements() {

		initWidget(BINDER.createAndBindUi(this));
		
		this.listeners = new LinkedList<>();
		this.toolbars = new LinkedList<>();
		
		agreementsTree.addListener(this);
		toolbar.addListener(this);
		
		agreementsTree.getEnterpriseService().getDomain(
				new AsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(Integer result) {
						TrashAgreements.this.domain = result;
						getTrashAgreements(s -> {});
					}
				});
	}
	
	// ---------------------------------------------------- TrashAgreements.Listener

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	// ---------------------------------------------------- TrashAgreements.Toolbar
	
	public void addToolbar(Toolbar toolbar) {
		toolbars.add(toolbar);
	}
	
	public void removeToolbar(Toolbar toolbar) {
		toolbars.remove(toolbar);
	}
	
	// ---------------------------------------------------- Auxiliar Methods
	
	public Integer getDomain() {
		return this.domain;
	}
	
	public TrashAgreementsTree getAgreementsTree() {
		return agreementsTree;
	}
	
	public void reloadAgreements(Consumer<Void> consumer) {
		getTrashAgreements(s -> {
			if(agreementsTree.getTree().getItemCount() > 0)
				agreementsTree.getTree().setSelectedItem(agreementsTree.getTree().getItem(0), true);
			
			consumer.accept(null);
		});
	}
	
	public void getTrashAgreements(Consumer<Agreement> success) {
		agreementsTree.clearTree();
		getTrashAgreements(success, 0, 1000);
	}
	
	private void getTrashAgreements (Consumer<Agreement> success, int offset, int limit) {
		
		agreementsTree.getEnterpriseService().getTrashAgreements(offset, limit,
				new AsyncCallback<List<Agreement>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(List<Agreement> agreements) {
						
						for (int i = 0; i < agreements.size(); i++) {
							Agreement agreement = agreements.get(i);
							addAgreementItem(agreement);
						}
						
						// Select the first one.
//						if (offset == 0 && agreementsTree.getTree().getItemCount() > 0)
//							agreementsTree.getTree().setSelectedItem(agreementsTree.getTree().getItem(0), false);
						
						// Get remainning
						if ( agreements.size() == limit )
							getTrashAgreements(success, offset + limit, limit);
						else
							success.accept(null);

					}
				});
	}
	
	public TreeItem addAgreementItem(Agreement agreement) {
		String description = agreement.getDescription();
		TreeItem agreementTreeItem = null;
		
		if (AonNumberUtils.notEquals(0, agreement.getDomain()) && AonNumberUtils.equals(domain, agreement.getDomain()))
			agreementTreeItem = new TreeItem(getNewOwnAgreementRow(description));
		else 
			agreementTreeItem = new TreeItem(TrashAgreementsTree.imageItemSafeHtml(description,
					TrashAgreementsTree.getImageResource(agreement, domain)));

		agreementTreeItem.setUserObject(agreement);
		
		agreementTreeItem.addStyleName(style.paddingLeft());

		agreementsTree.getTree().addItem(agreementTreeItem);
		
		agreementTreeItem.ensureDebugId(getId(agreement));

		return agreementTreeItem;
	}


	private Widget getNewOwnAgreementRow(String description) {
		HTMLPanel panel = new HTMLPanel("");
		panel.addStyleName(style.treeItem()); 
		AonTableButton arrow = new AonTableButton("", AON.CSS.aonIconBack());
		arrow.addStyleName(style.rotate());
		Label descriptionL = new Label(description);
		panel.add(arrow);
		panel.add(descriptionL);
		return panel;
	}

	// -------------------------------------------------------- Private methods

	@Override
	public void getAgreements(Consumer<Agreement> success) {
		getTrashAgreements(success);
	}
	
	@Override
	public void onTreeItemSelected(SelectionEvent<TreeItem> event) {
		TreeItem selectedItem = event.getSelectedItem();
		Object object = selectedItem.getUserObject();
		fireAgreementSelected((Agreement) object);
	}
	
	private void fireAgreementSelected(Agreement agreement) {
		for (Listener listener : listeners)
			listener.onAgreementSelected(agreement);
	}
	
	@Override
	public void onAgreementDelete4Ever(Agreement agreement) {
		if(agreement.getId() < 0) {
			for(Toolbar toolbarIt : toolbars)
				toolbarIt.onAgreementDelete4Ever(agreement);
		}
	}

	@Override
	public void onAgreementRestore(Agreement agreement) {
		if(agreement.getId() < 0) {
			for(Toolbar toolbarIt : toolbars)
				toolbarIt.onAgreementRestore(agreement);
		}
	}
	
	public void filter(String pattern) {
		Tree tree = agreementsTree.tree;
		for ( int i = 0; i < tree.getItemCount(); i++ ) {
			TreeItem item = tree.getItem(i);
			Agreement agreement = (Agreement) item.getUserObject();
			
			boolean visible = AonStringUtils.isBlank(pattern) || 
					( agreement.getDescription().toUpperCase().indexOf(pattern.trim().toUpperCase()) >= 0 );
			
			item.setVisible(visible);
		}
	}
	
	private static String getId(Agreement agreement) {
		return agreement.getDescription()
				.toLowerCase()
				.replaceAll("\\s+", "_")
				;
	}

	@Override
	public void onKeyUpSearchTrashTextBox(KeyUpEvent event) {
		filter(toolbar.getSearchTextBox().getValue());
	}

	@Override
	public void onDelete4EverButtonClick(ClickEvent event) {
		Object object = getAgreementsTree().getTree().getSelectedItem().getUserObject();
		if(object instanceof Agreement) {
			for(Toolbar toolbarIt : toolbars)
				toolbarIt.onAgreementDelete4Ever((Agreement) object);
		}
	}

	@Override
	public void onRestoreButtonClick(ClickEvent event) {
		Object object = getAgreementsTree().getTree().getSelectedItem().getUserObject();
		if(object instanceof Agreement) {
			for(Toolbar toolbarIt : toolbars)
				toolbarIt.onAgreementRestore((Agreement) object);
		}
	}

	public void setVisibleDraft4EverButton(boolean visible) {
		toolbar.setVisibleDraft4EverButton(visible);
	}

	public void setVisibleRestoreButton(boolean visible) {
		toolbar.setVisibleRestoreButton(visible);
	}

}
