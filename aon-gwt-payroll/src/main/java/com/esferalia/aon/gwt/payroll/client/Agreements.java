package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAgreementsTreeToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
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

public class Agreements extends ResizeComposite implements AgreementsTree.Listener, AonAgreementsTreeToolbar.Listener {

	// ------------------------------------------- Listener
	
	interface Listener {

		void onAgreementSupr(Agreement agreement);

		void onAgreementContextMenu(Agreement agreement, ContextMenuEvent event);

		void onAgreementSelected(Agreement agreement);

		void onCollapseMenuButtonClick();

		void onShowMenuButtonClick();
	}
	
	// ------------------------------------------- Toolbar
	
	interface Toolbar {
		
		void onAgreementCtrlC(Agreement agreement);

		void onAgreementCtrlV(Agreement agreement);
		
		void onNewAgreement(Agreement agreement);
		
		void onMoveToTrash(Agreement agreement);
		
		void onCopyAgreement(Agreement agreement);
		
		void onPasteAgreement(Agreement agreement);
		
		void onCollapseMenuClick(ClickEvent event);
		
		void onViewAgreements(Agreement agreement, Boolean allAgreements);
	}
	
	// ------------------------------------------- Images

	private static final Images IMAGES = GWT.create(Images.class);

	// ------------------------------------------- UiBinder
	
	interface Binder extends UiBinder<Widget, Agreements> {}
	
	private static final Binder BINDER = GWT.create(Binder.class);

	// ------------------------------------------- UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String treeItem();
		String rotate();
		String paddingLeft();
	}
	
	@UiField
	AonAgreementsTreeToolbar toolbar;
	
	@UiField
	AgreementsTree agreementsTree;
	
	// ------------------------------------------- Variables
	
	private Integer domain;
	private List<Listener> listeners;
	private List<Toolbar> toolbars;
	
	// ------------------------------------------- Constructor

	public Agreements() {

		initWidget(BINDER.createAndBindUi(this));
		
		this.listeners = new LinkedList<>();
		this.toolbars = new LinkedList<>();
		
		agreementsTree.addListener(this);
		toolbar.addListener(this);
		
		agreementsTree.getEnterpriseService().getDomain(new AsyncCallback<Integer>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(Integer result) {
				Agreements.this.domain = result;
				toolbar.setEnabledViewAgreementsButton(false);
				getAgreements(s -> {});
			}
		});
	}
	
	// ------------------------------------------- AgreementsTree
	
	@Override
	public void getAgreements(Consumer<Void> finish) {
		agreementsTree.clearTree();
		getAgreements(false, s -> {
			agreementsTree.scrollToTop();
			toolbar.setEnabledViewAgreementsButton(true);
			finish.accept(null);
		}, f -> {});
	}
	
	public void getAgreementsAndSelectImported(Integer agreementId, Consumer<Boolean> success) {
		agreementsTree.clearTree();
		getAgreements(true, s -> {
			selectImportAgreement(agreementId, su -> success.accept(true));
			agreementsTree.scrollToTop();
			toolbar.setEnabledViewAgreementsButton(true);
		}, f -> {});
	}
	
	// ------------------------------------------- Listener

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	public void addToolbar(Toolbar toolbar) {
		toolbars.add(toolbar);
	}
	
	public void removeToolbar(Toolbar toolbar) {
		toolbars.remove(toolbar);
	}

	public AgreementsTree getAgreementsTree() {
		return agreementsTree;
	}

	public void reloadAgreements(Consumer<Void> finish) {
		getAgreements(s -> finish.accept(null));
	}
	
	public Integer getDomain() {
		return this.domain;
	}
	
	private void fireAgreementSelected(Agreement agreement) {
		for (Listener listener : listeners)
			listener.onAgreementSelected(agreement);
	}
	
	// ------------------------------------------- New item
	
	public void addNewItemTree() {
		List<Integer> selectedDates = new ArrayList<>();
		selectedDates.add(2015); // Change this if own XML change dates
		getAgreementsTree().getEnterpriseService().getServiAgreement("a0000001", selectedDates,
				new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(Integer importedAgreementId) {
				getAgreementsAndSelectImported(importedAgreementId, s -> {});
			}
		});
	}

	// ------------------------------------------- Private methods
	
	public TreeItem addAgreementItem(Agreement agreement) {
		String description = agreement.getDescription();
		if (agreement.isRedefined())
			description = "*" + description;

		List<ImageResource> marks = new ArrayList<>();
		if (AonNumberUtils.notEquals(0, agreement.getDomain()) && AonNumberUtils.notEquals(domain, agreement.getDomain()) )
			marks.add(IMAGES.parent());
		
		TreeItem agreementTreeItem = null;
		
		if (AonNumberUtils.notEquals(0, agreement.getDomain()) && AonNumberUtils.equals(domain, agreement.getDomain()))
			agreementTreeItem = new TreeItem(getNewOwnAgreementRow(description));
		else 
			agreementTreeItem = new TreeItem(AgreementsTree.imageItemSafeHtml(description,
				AgreementsTree.getImageResource(agreement, domain)));

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

	// ------------------------------------------- Abstract methods
	
	@Override
	public boolean evaluateId(Agreement agreement) {
		return agreement.getId() >= 0;
	}

	@Override
	public void onAgreementCtrlC(Agreement agreement) {
		if(agreement.getId() >= 0)
			for(Toolbar itToolbar : toolbars)
				itToolbar.onAgreementCtrlC(agreement);
	}

	@Override
	public void onAgreementCtrlV(Agreement agreement) {
		for(Toolbar itToolbar : toolbars)
			itToolbar.onAgreementCtrlV(agreement);
	}

	@Override
	public void onAgreementSupr(Agreement agreement) {
		for (Listener listener : listeners)
			listener.onAgreementSupr(agreement);
	}
	
	@Override
	public void onTreeItemSelected(SelectionEvent<TreeItem> event) {
		TreeItem selectedItem = event.getSelectedItem();
		Object object = selectedItem.getUserObject();
		
		if(object instanceof Agreement)
			fireAgreementSelected((Agreement) object);
	}

	@Override
	public void onAgreementContextMenu(Agreement agreement, ContextMenuEvent event) {
		for (Listener listener : listeners)
			listener.onAgreementContextMenu(agreement, event);
	}
	
	private void filter(String pattern) {
		Tree tree = agreementsTree.tree;
		for ( int i = 0; i < tree.getItemCount(); i++ ) {
			TreeItem item = tree.getItem(i);
			Agreement agreement = (Agreement) item.getUserObject();
			
			boolean visible = AonStringUtils.isBlank(pattern) || ( agreement.getDescription().toUpperCase().indexOf(pattern.trim().toUpperCase()) >= 0 );
			item.setVisible(visible);
		}
	}
	
	private void getAgreements (boolean allAgreements, Consumer<List<Agreement>> success, Consumer<Throwable> failure) {
		toolbar.setVisibleLoadingButton(true);
		agreementsTree.getEnterpriseService().getAgreements(allAgreements, new AsyncCallback<List<Agreement>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						failure.accept(caught);
					}

					@Override
					public void onSuccess(List<Agreement> agreements) {
						Agreement selectedAgreement = null == agreementsTree.getTree().getSelectedItem()
								? null : (Agreement) agreementsTree.getTree().getSelectedItem().getUserObject();
						
						agreementsTree.getTree().clear();
						if(null != selectedAgreement && !existAgreement(agreements, selectedAgreement))
							addAgreementItem(selectedAgreement);
						
						for (int i = 0; i < agreements.size(); i++) {
							Agreement agreement = agreements.get(i);
							if (evaluateId(agreement))
								addAgreementItem(agreement);
						}
						
						// Select the first one.
						if (agreementsTree.getTree().getItemCount() > 0 && null == selectedAgreement)
							agreementsTree.getTree().setSelectedItem(
									agreementsTree.getTree().getItem(0), 
									true);
						else
							selectAgreementSelected(selectedAgreement);
						
						toolbar.setVisibleLoadingButton(false);
						success.accept(agreements);
					}

				});
	}

	private boolean existAgreement(List<Agreement> agreements, Agreement selectedAgreement) {
		Optional<Agreement> agreementFind = agreements.stream().filter(agreement -> agreement.getId().equals(selectedAgreement.getId())).findAny();
		return agreementFind.isPresent();
	}
	
	private void selectAgreementSelected(Agreement selectedAgreement) {
		if(null == selectedAgreement)
			agreementsTree.getTree().setSelectedItem(
					agreementsTree.getTree().getItem(0), 
					true);
		else
			for ( int i = 0; i < agreementsTree.getTree().getItemCount(); i++ ) {
				TreeItem item = agreementsTree.getTree().getItem(i);
				Agreement agreement = (Agreement) item.getUserObject();
				
				if(AonNumberUtils.equals(selectedAgreement.getId(), agreement.getId())) {
					agreementsTree.tree.setSelectedItem(item, true);
					return;
				}
			}
	}
	
	private static String getId(Agreement agreement) {
		return agreement.getDescription()
				.toLowerCase()
				.replaceAll("\\s+", "_")
				;
	}

	@Override
	public void onKeyUpSearchTextBox(KeyUpEvent event) {
		filter(toolbar.getSearchTextBox().getValue());
	}

	@Override
	public void onNewButtonClick(ClickEvent event) {
		addNewItemTree();
	}

	@Override
	public void onDraftButtonClick(ClickEvent event) {
		Object object = getAgreementsTree().getTree().getSelectedItem().getUserObject();
		if(object instanceof Agreement) {
			for(Toolbar itToolbar : toolbars)
				itToolbar.onMoveToTrash((Agreement) object);
		}
	}

	@Override
	public void onCollapseAllButtonClick(ClickEvent event) {
		Object object = getAgreementsTree().getTree().getSelectedItem().getUserObject();
		
		if(object instanceof Agreement) {
			for(Toolbar itToolbar : toolbars)
				itToolbar.onCollapseMenuClick(event);
		}
	}
	
	public AonAgreementsTreeToolbar getToolbar() {
		return toolbar;
	}

	@Override
	public void onViewAgreementsButtonClick(ClickEvent event, Boolean allAgreements) {
		getAgreements(allAgreements, s -> {
			agreementsTree.scrollToTop();
			toolbar.setEnabledViewAgreementsButton(true);
		}, f -> {});
	}
	
	private void selectImportAgreement(Integer agreementId, Consumer<Boolean> success) {
		Tree tree = agreementsTree.tree;
		boolean isSelected = false;
		for ( int i = 0; i < tree.getItemCount(); i++ ) {
			TreeItem item = tree.getItem(i);
			Agreement agreement = (Agreement) item.getUserObject();
			
			if(AonNumberUtils.equals(agreementId, agreement.getId()) && !isSelected) {
				agreementsTree.tree.setSelectedItem(item, true);
				isSelected = true;
				success.accept(true);
			}
		}
	}

	public void resetTypeView() {
		getToolbar().resetTypeView();
	}

	public void setViewAgreements(boolean viewAgreements) {
		toolbar.setViewAgreements(viewAgreements);
	}
	
	public void setVisibleDraftButton(boolean visible) {
		toolbar.setVisibleDraftButton(visible);
	}

}
