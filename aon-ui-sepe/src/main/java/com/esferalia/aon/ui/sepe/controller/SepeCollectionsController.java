package com.esferalia.aon.ui.sepe.controller;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.AonVersion;
import com.esferalia.aon.payroll.enumeration.ContrataFileType;
import com.esferalia.aon.payroll.enumeration.SepeBatchAttachmentType;
import com.esferalia.aon.payroll.enumeration.SuspensionCause;


public class SepeCollectionsController implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private List<SelectItem> contrataFileTypeList;
	private List<SelectItem> sepeBatchAttachTypes;
	private List<SelectItem> suspensionCauses;

	public List<SelectItem> getContrataFileTypeList() {
		if (contrataFileTypeList == null) {
			contrataFileTypeList = new LinkedList<SelectItem>();
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			for (ContrataFileType obj : ContrataFileType.values()) {
				if(ArrayUtils.contains(ISepeConstants.AVAILABLE_CONTRATA_FILE_TYPES,obj)){
					SelectItem item = new SelectItem(obj, obj.getName(locale));
					contrataFileTypeList.add(item);
				}
			}
		}
		return contrataFileTypeList;
	}
	
	public List<SelectItem> getSepeBatchAttachTypes() {
		if (sepeBatchAttachTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			sepeBatchAttachTypes = new LinkedList<SelectItem>();
			for( SepeBatchAttachmentType type : SepeBatchAttachmentType.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				sepeBatchAttachTypes.add(item);			
			}
		}
		return sepeBatchAttachTypes;
	}
	
	public List<SelectItem> getSuspensionCauses() {
		if (suspensionCauses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			suspensionCauses = new LinkedList<SelectItem>();
			SuspensionCause[] causes = SuspensionCause.values();
			for (SuspensionCause c : causes) {
				String name = c.getName(locale);
				SelectItem item = new SelectItem(c, name);
				suspensionCauses.add(item);
			}
		}
		return suspensionCauses;
	}

}