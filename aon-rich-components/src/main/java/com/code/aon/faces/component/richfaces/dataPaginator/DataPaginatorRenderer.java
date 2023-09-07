package com.code.aon.faces.component.richfaces.dataPaginator;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.richfaces.component.UIDatascroller;
import org.richfaces.renderkit.html.DatascrollerTemplate;

import jakarta.servlet.ServletRequest;

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
	
	@Override
	public void renderPager(FacesContext context, UIComponent component, int pageIndex, int count)
		throws IOException {
	    try {
		ellipsisRenderPager(context, component, pageIndex, count);
	    } catch (Exception e) {
		super.renderPager(context, component, pageIndex, count);
	    }
	}

	public void ellipsisRenderPager(FacesContext context, UIComponent component, int pageIndex, int count)
		throws IOException {
	    
	    
	    ResponseWriter out = context.getResponseWriter();
	    UIDatascroller scroller = (UIDatascroller) component;
	    int currentPage = pageIndex;

	    int maxPages = scroller.getMaxPages();
	    if (maxPages <= 1) {
		maxPages = 1;
	    }

	    int pageCount = count;
	    if (pageCount <= 1) {
		return;
	    }

	    
	    

	    if ( pageCount <= maxPages ) {
		// we can print all pages.
		for (int page = 1; page <= pageCount; page++) {
		    renderPage(scroller, component, page - 1, page == currentPage, out);
		}
	    } else {
		int otherPages = maxPages - 3;
		int sidePages = otherPages / 2;
		int startPage = currentPage - sidePages;
		int endPage = currentPage + sidePages;
		
		if ( startPage <= 2) {
		    // print all pages on the left side
		    for (int page = 1; page < maxPages; page++) {
			renderPage(scroller, component, page - 1, page == currentPage, out);
		    }
		    renderEllipisis(context, component, out);
		    renderPage(scroller, component, pageCount -1 , pageCount == currentPage, out);
		} else if ( endPage >= ( pageCount -1 ) ){
		    // print all pages on the right side
		    renderPage(scroller, component, 0 , 1 == currentPage, out);
		    renderEllipisis(context, component, out);
		    for (int page = pageCount - otherPages -1 ; page <= pageCount; page++) {
			renderPage(scroller, component, page - 1, page == currentPage, out);
		    }
		} else {
		    renderPage(scroller, component, 0 , false, out);
		    renderEllipisis(context, component, out);
		    
		    
		    for (int page = startPage; page < currentPage; page++) {
			renderPage(scroller, component, page - 1, false, out);
		    }
		    renderPage(scroller, component, currentPage -1 , true, out);
		    for (int page = currentPage+1; page <= endPage; page++) {
			renderPage(scroller, component, page - 1, false, out);
		    }
		    
		    renderEllipisis(context, component, out);
		    renderPage(scroller, component, pageCount -1 , false, out);
		}
	    }

	}

	void renderEllipisis(FacesContext context, UIComponent component, ResponseWriter out) throws IOException {
	    out.startElement("td", component);
	    UIComponent pagesFacet = component.getFacet("ellipsis");
	    if (pagesFacet !=null && pagesFacet.isRendered()) {
	        renderChild(context, pagesFacet);
	    } else {
	        out.writeText("...", null);
	    }
	    out.endElement("td");
	}

	void renderPage(UIDatascroller scroller, UIComponent component, int pageIndex, boolean isCurrentPage,
		ResponseWriter out) throws IOException {
	    String styleClass;
	    String style;
	    if (isCurrentPage) {
	        styleClass = scroller.getSelectedStyleClass();
	        style = scroller.getSelectedStyle();
	    } else {
	        styleClass = scroller.getInactiveStyleClass();
	        style = scroller.getInactiveStyle();
	    }
	    if (styleClass == null) {
	        styleClass = "";
	    }

	    out.startElement("td", component);

	    if (isCurrentPage) {
	        out.writeAttribute("class", "rich-datascr-act " + styleClass, null);
	    } else {
	        out.writeAttribute("class", "rich-datascr-inact " + styleClass, null);
	        out.writeAttribute("onclick", getOnClick(Integer.toString(pageIndex + 1)), null);
	    }
	    if (null != style)
	        out.writeAttribute("style", style, null);
	    out.writeText(Integer.toString(pageIndex + 1), null);
	    // renderChild(context, link);
	    out.endElement("td");
	}	
	
	
}
