package com.esferalia.aon.gwt.fiscal.client.mod369;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.mod369.Model369.Model369Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod369;
import com.esferalia.aon.occam.api.model.fiscal.Mod369DetailCorrection;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

//public class Model369CorrectionTable extends SimpleLayoutPanel implements HasSelectionHandlers<Mod369DetailCorrection> {
//
//	private Mod369 model;
//	private ScrollPanel tablePanel;
//	private Integer selectionIndex;
//	private AonTextBox filterBox;
//	
//	public Model369CorrectionTable(Model369Callback cbk, Mod369 model, Integer selectedIndex) {
//		this.model = model;
//		DockLayoutPanel tableDockLayout = new DockLayoutPanel(Unit.PX);
//		tableDockLayout.setStyleName(AON.CSS.aonBorderRight());
//		tableDockLayout.addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
//		tablePanel = new ScrollPanel();
//		tablePanel.addStyleName(AON.CSS.aonScrollArea());
//		tableDockLayout.add(tablePanel);
//		setWidget(tableDockLayout);
//		
//		if (model.getCorrections() == null || model.getCorrections().isEmpty()) {
//			refresh();
//			newPerceptor( );	
//		} else if ( selectedIndex != null) {
//			selectionIndex = selectedIndex;
//			refresh();
//		} else {
//			selectionIndex = 0;
//			refresh();
//		}
//	}
//	
//	private Mod369DetailCorrection getSelected() {
//		return model.getCorrections().get(selectionIndex);
//	}
//
//	private Widget getToolbarPanel() {
//		AonToolbar toolbar = new AonToolbar("");
//		
//		AonToolbarButton newDetailButton = new AonToolbarButton(AON.MSG.newPerceptor(),AON.CSS.aonIconAdd());
//		AonToolbarButton deleteDetailButton = new AonToolbarButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
//		AonToolbarButton restoreDeletedButton = new AonToolbarButton(AON.MSG.restoreAction(),AON.CSS.aonIconRestore());
//
//		addSelectionHandler(event -> {
//			deleteDetailButton.setVisible(!event.getSelectedItem().isDeleted());
//			restoreDeletedButton.setVisible(event.getSelectedItem().isDeleted());
//		});
//
//		newDetailButton.addClickHandler(event -> newPerceptor( ));
//		toolbar.add(newDetailButton);
//		
//		restoreDeletedButton.addClickHandler(event -> {
//			getSelected().setDeleted(false);
//			if (!getSelected().isDirty()) {
//				restoreDeletedButton.setVisible(false);
//				deleteDetailButton.setVisible(true);
//				refresh();
//			}
//		});
//		toolbar.add(restoreDeletedButton);
//		
//		deleteDetailButton.addClickHandler(event -> {
//			getSelected().setDeleted(true);
//			restoreDeletedButton.setVisible(true);
//			deleteDetailButton.setVisible(false);
//			refresh();
//		});
//		toolbar.add(deleteDetailButton);
//		
//		FlowPanel searchPanel = new FlowPanel();
//		searchPanel.setStyleName(AON.CSS.aonFlexBlockInline());
//		searchPanel.addStyleName(AON.CSS.aonMarginLeft());
//		
//		InlineLabel searchIcon = new InlineLabel("Filtro:");
//		searchIcon.setStyleName(AON.CSS.aonInnerLabel());
//		searchPanel.add(searchIcon);
//		filterBox = new AonTextBox( );
//		filterBox.setVisibleLength(10);
//		filterBox.addValueChangeHandler(event -> refresh());
//		searchPanel.add(filterBox);
//		
//		AonToolbarButton cleanFilterButton = new AonToolbarButton(AON.MSG.clean(),AON.CSS.aonIconClear());
//		cleanFilterButton.addClickHandler(event -> {
//			filterBox.setValue("");
//			refresh();	
//		});
//		searchPanel.add(cleanFilterButton);
//		toolbar.add(searchPanel);
//		
//		return toolbar;
//	}
//	
//	public void styleTable(AonDisplayGrid tab) {
//		for ( int i = 0 ; i < tab.getWidgetCount(); i++) {
//			if (i == selectionIndex) {
//				tab.getWidget(i).addStyleName(AON.CSS.aonBackgroundLigthBlue());
//			} else {
//				tab.getWidget(i).removeStyleName(AON.CSS.aonBackgroundLigthBlue());
//			}
//		}
//	}
//
//	public void refresh() {
//		AonDisplayGrid tab = new AonDisplayGrid();
//		tab.addStyleName(AON.CSS.aonNoPadding());
//		tab.addStyleName(AON.CSS.aonBlockCenter());
//		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
//		tablePanel.clear();
//		tablePanel.setWidget(tab);
//		int i = 0;
//		boolean resetSelection = false;
//		int firstMatch = -1;
//		for (Mod369DetailCorrection detail : this.model.getCorrections()) {
//			AonDisplayGridRow row = tab.addRow();
//			boolean visible = true;
////			boolean visible = AonStringUtils.isBlank(filterBox.getValue()) ||
////					AonStringUtils.isBlank(detail.getName()) ||
////					AonStringUtils.containsIgnoreCase(detail.getName(), filterBox.getValue());
//			row.addStyleName(AON.CSS.aonClickable()); 
//			row.setVisible(visible);
//			if (firstMatch == -1 && visible) {
//				firstMatch = i;
//			}
//			if (i == Model369CorrectionTable.this.selectionIndex) {
//				row.addStyleName(AON.CSS.aonBackgroundLigthBlue());
//				resetSelection = !visible; 
//			}
//			final int idx = i;
//			row.addClickHandler( event ->  {
//				Model369CorrectionTable.this.selectionIndex = idx;
//				styleTable( tab );				
//				SelectionEvent.fire(Model369CorrectionTable.this, detail);	
//			});
//			String name = AonStringUtils.abbreviate(AonStringUtils.defaultIfBlank(Country.safeIso2(detail.getCountry()), AON.MSG.newPerceptor()) , 30 );
//			InlineLabel nameLabel = new InlineLabel( name );
//			if (detail.isDirty()) {
//				nameLabel.setText("* " + name);
//			} else {
//				nameLabel.setText(name);
//			}
//			if (detail.isDeleted()) {
//				nameLabel.addStyleName(AON.CSS.aonTextLineThrough());
//			} else {
//				nameLabel.removeStyleName(AON.CSS.aonTextLineThrough());
//			}
////			row.addCell( new InlineLabel( detail.getKey() ) , AON.CSS.aonTextCenter(),AON.CSS.aonWidth20() )
////			.addCell( new InlineLabel( AonStringUtils.defaultIfBlank(detail.getSubKey())), AON.CSS.aonTextCenter(),AON.CSS.aonWidth20())
//			row.addCell( nameLabel ,AON.CSS.aonWidthAuto());
//			i++;
//		}
//		if (resetSelection) {
//			selectionIndex = firstMatch;
//			SelectionEvent.fire(Model369CorrectionTable.this, getSelected() );
//		}
//	}
//	
//	private void newPerceptor() {
//		Mod369DetailCorrection detail = new Mod369DetailCorrection()
//				.setDirty(true)
////				.setKey("A")
////				.setSubKey("01")
//				.setTempId((model.getCorrections().size() * (-1)));
//		model.getCorrections().add(detail);
//		selectionIndex = model.getCorrections().size() - 1;
//		refresh();
//		tablePanel.scrollToBottom();
//		SelectionEvent.fire(Model369CorrectionTable.this, detail);
//	}
//
//	public Integer getSelectionIndex() {
//		return selectionIndex;
//	}
//
//	public void duplicate(Model369Callback cbk, Mod369 model, Mod369DetailCorrection partner) {
//		Mod369DetailCorrection part  = new Mod369DetailCorrection()
//				.setDirty(true)
////				.setKey("A")
////				.setSubKey("01")				
//				.setTempId((model.getCorrections().size() * (-1)))
//					
////				.setDocument(partner.getDocument())
////				.setRepresentativeDocument(partner.getRepresentativeDocument())
////				.setName(partner.getName())
////				.setProvince(partner.getProvince())
//				.setCountry(partner.getCountry())
////				.setPartType(partner.getPartType())
//				
////				.setMemberEndOfYear(partner.isMemberEndOfYear())
////				.setMemberDays(partner.getMemberDays())
////				.setPartPercent(partner.getPartPercent())
////				.setAddress(partner.getAddress())
//				; 
//		model.getCorrections().add( part );
//		selectionIndex = model.getCorrections().size() - 1;
//		refresh();
//		tablePanel.scrollToBottom();
//		SelectionEvent.fire(Model369CorrectionTable.this, part ); 
//	}
//
//	@Override
//	public HandlerRegistration addSelectionHandler(SelectionHandler<Mod369DetailCorrection> handler) {
//		HandlerRegistration hr = super.addHandler(handler, SelectionEvent.getType());
//		if (selectionIndex != null) {
//			SelectionEvent.fire(Model369CorrectionTable.this, getSelected());
//		}
//		return hr;
//	}
//	
//}
