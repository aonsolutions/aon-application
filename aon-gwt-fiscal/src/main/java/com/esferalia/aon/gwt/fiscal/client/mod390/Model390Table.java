package com.esferalia.aon.gwt.fiscal.client.mod390;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Model390Table extends CellTable<Mod3902014> {

	public static final ProvidesKey<Mod3902014> MOD390_PROVIDES_KEY = new ProvidesKey<Mod3902014>() {
		@Override
		public Object getKey(Mod3902014 mod390) {
			return mod390 == null ? null : mod390.getId();
		}
	};

	private NoSelectionModel<Mod3902014> model;
	
	public Model390Table(SelectionChangeEvent.Handler handler) {
		super(1,AON.AON_CELL_TABLE_STYLE,MOD390_PROVIDES_KEY);
		this.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
		this.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		addSelectorColumn();
		addYearColumn();
		addAdministrationColumn();
		addReplacementColumn();
		addDocumentColumn();
		addNameColumn();
		
		model = new NoSelectionModel<Mod3902014>(MOD390_PROVIDES_KEY);
		model.addSelectionChangeHandler( handler );
		this.setSelectionModel(model);
		this.setEmptyTableWidget(new HTML(AON.MSG.noData()));
	}

	private void addSelectorColumn() {
		final Column<Mod3902014, ImageResource> selectorColumn = new Column<Mod3902014, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod3902014 mod390) {
				return AON.AON_RESOURCES.aonIconRowSelector();
			}
		};
		this.addColumn(selectorColumn);
		this.setColumnWidth(selectorColumn, 20, Unit.PX);
	}

	private void addAdministrationColumn() {
		final Column<Mod3902014, ImageResource> iconColumn = new Column<Mod3902014, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod3902014 mod390) {
				Administration adm = Administration.values()[mod390.getAdministration()]; 
				if (adm ==Administration.ALAVA) {
					return AON.AON_RESOURCES.aonIconAraba();	
				} else if (adm ==Administration.BIZKAIA) {
					return AON.AON_RESOURCES.aonIconBizkaia();
				} else if (adm ==Administration.GIPUZKOA) {
					return AON.AON_RESOURCES.aonIconGipuzkoa();
				} else if (adm ==Administration.NAVARRA) {
					return AON.AON_RESOURCES.aonIconNavarra();
				} 
				return AON.AON_RESOURCES.aonAeat();
			}
		};
		this.addColumn(iconColumn);
		this.setColumnWidth(iconColumn, 20, Unit.PX);
	}

	private void addYearColumn() {
		final TextColumn<Mod3902014> yearColumn = new TextColumn<Mod3902014>() {
			@Override
			public String getValue(Mod3902014 mod390) {
				return Integer.toString(mod390.getYear());
			}
		};
		this.addColumn(yearColumn, AON.MSG.fiscalYear());
		yearColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
		this.setColumnWidth(yearColumn, 100, Unit.PX);
	}
	
	private void addReplacementColumn() {
		Column<Mod3902014, ImageResource> replacementColumn = new Column<Mod3902014, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod3902014 mod180) {
				return mod180.isReplacement() ? AON.AON_RESOURCES.aonIconChecked()
						: AON.AON_RESOURCES.aonIconCheck();
			}
		};
		this.addColumn(replacementColumn, AON.MSG.replacement());
		replacementColumn.setCellStyleNames(AON.AON_CSS.aonDataTableIconColumn());
		this.setColumnWidth(replacementColumn, 100, Unit.PX);
	}

	private void addNameColumn() {
		final TextColumn<Mod3902014> nameColumn = new TextColumn<Mod3902014>() {
			@Override
			public String getValue(Mod3902014 mod390) {
				return AonStringUtils.defaultIfBlank(mod390.getName(),"")
					+ " " + AonStringUtils.defaultIfBlank(mod390.getFirstSurname(),"")
					+ " " + AonStringUtils.defaultIfBlank(mod390.getSecondSurname(),""); 
			}
		};
		this.addColumn(nameColumn, AON.MSG.name());
		this.setColumnWidth(nameColumn, 100, Unit.PCT);
	}

	private void addDocumentColumn() {
		final TextColumn<Mod3902014> documentColumn = new TextColumn<Mod3902014>() {
			@Override
			public String getValue(Mod3902014 mod180) {
				return mod180.getDocument();
			}
		};
		this.addColumn(documentColumn, AON.MSG.document());
		this.setColumnWidth(documentColumn, 150, Unit.PX);
	}

	public Mod3902014 getSelected() {
		return model.getLastSelectedObject();
	}
}
