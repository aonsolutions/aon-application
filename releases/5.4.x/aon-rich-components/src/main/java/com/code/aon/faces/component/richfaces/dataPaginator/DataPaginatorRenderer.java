package com.code.aon.faces.component.richfaces.dataPaginator;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;

import org.richfaces.renderkit.html.DatascrollerTemplate;

public class DataPaginatorRenderer extends DatascrollerTemplate {

	protected void setVariables(FacesContext facescontext,
			HtmlDataPaginator scroller) throws IOException {

		UIData data = scroller.getDataTable();
		Map requestMap = facescontext.getExternalContext().getRequestMap();

        String rowsCountVar = scroller.getRowsCountVar();
        if (rowsCountVar != null) {
        	requestMap.put( rowsCountVar, scroller.getRowCount() );
        }

        String displayedRowsCountVar = scroller.getDisplayedRowsCountVar();
        if (displayedRowsCountVar != null) {
			int displayedRowsCount = scroller.getRows(data);
			int max = scroller.getRowCount(data) - scroller.getFirstRow(data);
			if (displayedRowsCount > max) {
				displayedRowsCount = max;
			}
			requestMap.put(displayedRowsCountVar, displayedRowsCount);
        }

        String firstRowIndexVar = scroller.getFirstRowIndexVar();
        if (firstRowIndexVar != null) {
			int firstRowIndex = scroller.getFirstRow(data);
			if (scroller.getRowCount() > 0) {
				firstRowIndex += 1;
			}
			requestMap.put(firstRowIndexVar, firstRowIndex);
        }

        String lastRowIndexVar = scroller.getLastRowIndexVar();
        if (lastRowIndexVar != null) {
			int lastRowIndex = scroller.getFirstRow(data) + scroller.getRows(data);
			int count = scroller.getRowCount();
			if (lastRowIndex > count) {
				lastRowIndex = count;
			}
			requestMap.put(lastRowIndexVar, lastRowIndex);
        }
	}

	public void removeVariables(FacesContext facescontext,
			HtmlDataPaginator scroller) throws IOException {
		Map requestMap = facescontext.getExternalContext().getRequestMap();

        String rowsCountVar = scroller.getRowsCountVar();
        if (rowsCountVar != null) {
        	requestMap.remove( rowsCountVar );
        }
        String displayedRowsCountVar = scroller.getDisplayedRowsCountVar();
        if (displayedRowsCountVar != null) {
        	requestMap.remove(displayedRowsCountVar);
        }
        String firstRowIndexVar = scroller.getFirstRowIndexVar();
        if (firstRowIndexVar != null) {
            requestMap.remove(firstRowIndexVar);
        }
        String lastRowIndexVar = scroller.getLastRowIndexVar();
        if (lastRowIndexVar != null) {
            requestMap.remove(lastRowIndexVar);
        }
	}

	@Override
	public void encodeBegin(FacesContext facescontext, UIComponent component)
			throws IOException {
		super.encodeBegin(facescontext, component);
		if ( component.isRendered() ) {
			setVariables(facescontext, (HtmlDataPaginator) component);
		}
	}

	@Override
	public void encodeEnd(FacesContext facescontext, UIComponent component)
			throws IOException {
		super.encodeEnd(facescontext, component);
		if ( component.isRendered() ) {		
			removeVariables(facescontext, (HtmlDataPaginator) component);
		}
	}

}
