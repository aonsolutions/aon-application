package com.code.aon.ui.fiscal.controller.mod347;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.Province;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class Mod347DetailAssetController extends LinesController  {
	
	private List<SelectItem> assetLocations;
	private List<SelectItem> provinces;
	
	public List<SelectItem> getAssetLocations() {
		if (assetLocations == null) {
			assetLocations = new LinkedList<SelectItem>();
			assetLocations.add(new SelectItem("1"));
			assetLocations.add(new SelectItem("2"));
			assetLocations.add(new SelectItem("3"));
			assetLocations.add(new SelectItem("4"));
		}
		return assetLocations;
	}

	public List<SelectItem> getProvinces(){
    	if ( provinces == null ) {
    		Locale locale = AonUtil.getCurrentLocale();
    		provinces = new LinkedList<SelectItem>();
    		for( Province province : Province.values() ) {
	            String name = province.getName(locale); 
	            SelectItem item = new SelectItem(
	            		StringUtils.leftPad(Integer.toString( province.ordinal() ),2,'0')
	            		,StringUtils.abbreviate(name,25));
	            provinces.add( item );
    		}
        }
        return provinces;
	}
	
}
