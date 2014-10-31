package com.esferalia.aon.gwt.document.shared;

import java.util.Vector;

import com.google.gwt.cell.client.Cell;

public class Column<C, T> extends com.google.gwt.user.cellview.client.Column<T, C>{

	public Column(Vector<Cell<C>> cells) {
		super(cells.get(0));
		// TODO Apéndice de constructor generado automáticamente
	}

	@Override
	public C getValue(T object) {
		return null;
	}

}
