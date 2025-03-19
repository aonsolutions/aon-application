package com.esferalia.aon.gwt.fiscal.client.rawdoc;

import java.util.LinkedList;
import java.util.Objects;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.fiscal.client.rawdoc.RawdocModuleNew.RawdocCallback;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.type.RawdocNature.RawdocNatureVisitor;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class RawdocTable extends ScrollPanel {
	
	private static final int LIMIT = 100;

	private final FlowPanel container;
	private final AonDisplayGrid grid;
	
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 ); 
	private int lastScrollPos = 0;

	RawdocTable(RawdocModuleOptions opt, RawdocCallback cbk) {
		this.setStyleName(AON.CSS.aonScrollArea());
		
		container = new FlowPanel();			
		this.setWidget( container );
		
		grid = new AonDisplayGrid();
		grid.addStyleName( AON.CSS.aonWidthAlmostAll() );
		
		this.addScrollHandler(event -> {
			// ------------------------------------ Ignore scroll up.
			int oldScrollPos = lastScrollPos;
			lastScrollPos = this.getVerticalScrollPosition();
			if (oldScrollPos >= lastScrollPos) {
				return;
			}
			// -----------------------------------------------------
			if (isSearchEnabled()) {
				int maxScrollTop = this.getWidget().getOffsetHeight() - this.getOffsetHeight();
				if (lastScrollPos >= maxScrollTop) {
					disableSearch();
					search(opt, cbk, offset.getValue());
				}
			}
		});
		search( opt, cbk );
	}
	
	public void disableMoreData() {
		moreData.setValue(-1);
	}
	public void enableMoreData() {
		moreData.setValue(0);
	}
	public boolean isMoreData() {
		return (moreData.getValue() == 0 );
	}
	public void enableSearch() {
		searchEnabled.setValue(0);
	}
	public boolean isSearchEnabled() {
		return (searchEnabled.getValue() == 0 );
	}
	public void disableSearch() {
		searchEnabled.setValue(-1);
	}
	
	private void search(RawdocModuleOptions opt, RawdocCallback cbk) {
		enableMoreData();
		container.clear();
		container.add(grid);
		grid.clear();
		paintHeader();
		offset.setValue(0);
		search( opt, cbk, offset.getValue());
	}
	
	private void search(RawdocModuleOptions opt, RawdocCallback cbk, final int ofs) {
		if (!isMoreData()) return;
		RawdocModuleNew.RAWDOC_SERVICE.getRawdocs(opt.getOccam(), opt.getParams(), ofs, LIMIT
			, new AsyncCallback<LinkedList<Rawdoc>>() {
				@Override
				public void onSuccess(LinkedList<Rawdoc> rawdocs) {
					paintRows( opt, cbk, ofs, rawdocs );
				}
				
				@Override
				public void onFailure(Throwable caught) {
					cbk.showError(caught.getMessage());
				}
			}
		);
	}
	
	private void paintHeader() {
		grid.addHeaderRow()
			.addCell( new Label(), AON.CSS.aonWidth40() ,AON.CSS.aonTextCenter())
			.addCell( new Label(), AON.CSS.aonWidth40() ,AON.CSS.aonTextCenter())
			.addCell( new Label(AON.MSG.status()), AON.CSS.aonWidth40() ,AON.CSS.aonTextCenter())
			.addCell( new Label("N\u00BA Factura"), AON.CSS.aonWidth150() ,AON.CSS.aonTextLeft())
			.addCell( new Label("F. Fra."), AON.CSS.aonWidth80() ,AON.CSS.aonTextCenter())
			.addCell( new Label("Titular"), AON.CSS.aonWidth100() ,AON.CSS.aonTextLeft())
			.addCell( new Label(), AON.CSS.aonWidthAuto() ,AON.CSS.aonTextLeft())
			.addCell( new Label("Importe"), AON.CSS.aonWidth80() ,AON.CSS.aonTextRight())
			.addCell( new Label(), AON.CSS.aonWidth40() ,AON.CSS.aonTextCenter())
			.addCell( new Label(), AON.CSS.aonWidth40() ,AON.CSS.aonTextCenter())
			.addCell( new Label(), AON.CSS.aonWidth40() ,AON.CSS.aonTextCenter())
			.addCell( new Label(), AON.CSS.aonWidth40() ,AON.CSS.aonTextCenter())
			.addCell( new Label(), AON.CSS.aonWidth40() ,AON.CSS.aonTextCenter())
			.addCell( new Label(), AON.CSS.aonWidth40() ,AON.CSS.aonTextCenter())
			.addCell( new Label(), AON.CSS.aonWidth40() ,AON.CSS.aonTextCenter())
		;
	}
	
	private void paintRows(RawdocModuleOptions opt, RawdocCallback cbk, final int ofs, LinkedList<Rawdoc> rawdocs) {
		if (AonCollectionUtils.isEmpty( rawdocs )) {
			Label label = new Label(AON.MSG.noData());
			label.setStyleName(AON.CSS.aonBlockMessage());
			label.setStyleName(AON.CSS.aonBlockInfoMessage());
			label.setStyleName(AON.CSS.aonMarginTop());
			container.add( label );
			disableMoreData();
		} else {
			AonCollectionUtils.stream( rawdocs )
				.filter(rawdoc -> rawdoc.getNature() != null )
				.map( rawdoc -> paintRow(opt, cbk, rawdoc) )
				.filter( Objects::nonNull )
				.forEach( grid::add );
			offset.setValue(ofs + rawdocs.size());
			enableMoreData();
		}
		enableSearch();
	}

	private AonDisplayGridRow paintRow(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		return rawdoc.getNature().visit( new RawdocNatureVisitor<AonDisplayGridRow>() {

			@Override
			public AonDisplayGridRow visitInvoice() {
				return new RawdocTableRowInvoice(opt, cbk, rawdoc);
			}

			@Override
			public AonDisplayGridRow visitOtherIncomes() {
				return new RawdocTableRowIncome(opt, cbk, rawdoc);
			}
		});
	}
	
}
