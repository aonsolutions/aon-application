package com.code.aon.ui.help.controller;

import static com.esferalia.aon.watson.util.AonStringUtils.containsIgnoreCase;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.help.HelpData;
import com.code.aon.ui.help.pdf.PdfSearcher;
import com.esferalia.aon.watson.util.AonStringUtils; 

public class HelpSwitcher implements Serializable {
	
	public static final String HELP_SWITCHER = "helpSwitcher";

	private static final List<HelpData> HELP_DATA = getHelpData();
	
	private String filter;
	private String beanName;
	private int pageLimit ;
	private transient DataModel model;
	
	public DataModel getModel() {
		if (StringUtils.isBlank(filter))
			return new SerializableListDataModel(HELP_DATA);
		
		if (model == null) {
		
			
			Instant before = Instant.now();
			
			List<HelpData> filteredList = 
			HELP_DATA.stream()
			.filter(d -> containsIgnoreCase(d.getTitle(), filter))
			.limit(pageLimit)
			.collect(Collectors.toList());
			
			// if nothing is here
			if(filteredList.isEmpty()) {
				filteredList.addAll(
					HELP_DATA.stream()
					.filter(d -> AonStringUtils.containsMatching(d.getTitle(), filter.trim()))
					.limit(pageLimit)
					.collect(Collectors.toList())
				);
			}
			
			Instant after = Instant.now();
			long delta = Duration.between(before, after).toMillis();
			System.out.println("Help loaded in: " + delta + "ms");
			
			this.model = new SerializableListDataModel(filteredList);			
		}
		
		return model;
	}
	
	public void setFilter(String filter) {
		if ( AonStringUtils.equalsIgnoreCase(this.filter, filter)) 
			return;
		this.filter = filter;
		this.model = null;
	}
	
	public int getPageLimit() {
		return pageLimit;
	}
	
	public void setPageLimit(int pageLimit) {
		this.pageLimit = pageLimit;
	}
	
	public String getBeanName() {
		return beanName;
	}
	
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
	public void onSelect(ActionEvent event) {
		
	}

	public void onEditSearch(ActionEvent event) {
	}
	
	private static  List<HelpData> getHelpData() {
		try  ( InputStream is = PdfSearcher.class.getResourceAsStream("index.pdf");
		   PDDocument document = Loader.loadPDF(is.readAllBytes()) ) {
			
			return 
			PdfSearcher.search(document, "").stream()
			.filter( i -> i.getTitle().trim().length() > 0)
			.map(HelpSwitcher::map)
			.collect(Collectors.toList());
					
		} catch (IOException e) {
			return Collections.emptyList();
		}
		
	}
	
	private static HelpData map( PDOutlineItem item) {
		HelpData helpData = 
		new HelpData()
		.setTitle(item.getTitle());
		
		try {
			PDActionURI actionURI = (PDActionURI)item.getAction();
			helpData.setURI(actionURI.getURI());
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		return helpData;
	
	}
}
