package com.code.aon.ui.help.controller;

import static com.esferalia.aon.watson.util.AonStringUtils.containsIgnoreCase;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
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

	
	private String filter;
	private String beanName;
	private DataModel model;
	private DataModel filteredModel;
	
	public HelpSwitcher() {
		try {
			this.model = getAllModel();
		} catch (IOException e) {
			this.model = getEmptyModel();
		}
	}
	
	public DataModel getModel() {
		if (StringUtils.isBlank(filter))
			return model;
		
		if (filteredModel == null) {
			List<HelpData> filteredList = 
			((List<HelpData>) model.getWrappedData()).stream()
			.filter(d -> containsIgnoreCase(d.getTitle(), filter))
			.collect(Collectors.toList());
			
			// if nothing is here
			if(filteredList.isEmpty()) {
				filteredList.addAll(((List<HelpData>) model.getWrappedData()).stream()
				.filter(d -> AonStringUtils.containsMatching(d.getTitle(), filter))
				.collect(Collectors.toList()));
			}
			
			this.filteredModel = new SerializableListDataModel(filteredList);			
		}
		
		return filteredModel;
	}
	
	public void setModel(DataModel model) {
		this.model = model;
	}
	
	public void setFilter(String filter) {
		if ( AonStringUtils.equalsIgnoreCase(this.filter, filter)) 
			return;
		this.filter = filter;
		this.filteredModel = null;
	}
	
	public void setFilteredModel(DataModel filteredModel) {
		this.filteredModel = filteredModel;
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
		setModel(null);
		setFilter(null);
		setFilteredModel(null);
	}
	

	private static  DataModel getAllModel() throws IOException {
		try  ( InputStream is = PdfSearcher.class.getResourceAsStream("index.pdf");
		   PDDocument document = Loader.loadPDF(is) ) {
			
			List<HelpData> list = 
			PdfSearcher.search(document, "").stream()
			.filter( i -> i.getTitle().trim().length() > 0)
			.map(HelpSwitcher::map)
			.collect(Collectors.toList());
					
			return new SerializableListDataModel( list );
		}
		
	}
	
	private static DataModel getEmptyModel() {
		return new SerializableListDataModel(Collections.emptyList());
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
