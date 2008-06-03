package com.code.aon.ui.cvitae.controller;

import java.util.Date;
import java.util.StringTokenizer;

import javax.faces.event.ActionEvent;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.cvitae.Curriculum;
import com.code.aon.cvitae.enumeration.DriverLicense;
import com.code.aon.geozone.GeoZone;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.employee.util.Constants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.util.AonUtil;

public class CurriculumController extends BasicController {

	private static final long serialVersionUID = 3850759963483739573L;

	public static final String MANAGER_BEAN_NAME = "curriculum";

	private DriverLicense[] driverLicenses;
	
	private UploadedFile file;
	
	private RegistryAttachment attach;
	
	public String getLicensesAsString() {
		String licenses  = new String();
		for(int i = 0;i < driverLicenses.length;i++){
			licenses = licenses.concat((licenses.length() > 0?"+":Constants.EMPTY_STRING) + driverLicenses[i].toString());
		}
		return licenses;
	}
	
	public DriverLicense[] getDriverLicenses(){
		String licenses = ((Curriculum)this.getTo()).getDriverLicenses();
		if(licenses == null){
			return driverLicenses = new DriverLicense[0];
		}
		StringTokenizer tokenizer = new StringTokenizer(licenses,"+");
		driverLicenses = new DriverLicense[tokenizer.countTokens()];
		int tokens = tokenizer.countTokens();
		for(int i = 0;i < tokens;i++){
			String license = tokenizer.nextToken();
			driverLicenses[i] = DriverLicense.valueOf(license);
		}
		return driverLicenses;
	}
	
	public void setDriverLicenses(DriverLicense[] driverLicenses) {
		this.driverLicenses = driverLicenses;
	}
	
	 public UploadedFile getFile() {
			return this.file;
	}

	
	public void setFile(UploadedFile file) {
		this.file = file;
	}
	
	public RegistryAttachment getAttach() {
		return attach;
	}

	public void setAttach(RegistryAttachment attach) {
		this.attach = attach;
	}

	@SuppressWarnings("unused")
	public void onReset(MenuEvent event){
		super.onEditSearch(null);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.BasicController#onReset(javax.faces.event.ActionEvent)
	 */
	@Override
	public void onReset(ActionEvent event) {
		super.onReset(event);
		Curriculum cv = (Curriculum) getTo();
		cv.setEntryDate( new Date() );
		cv.setGeoZone( new GeoZone() );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.BasicController#onSelect(javax.faces.event.ActionEvent)
	 */
	@Override
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		StudiesController sc = (StudiesController) AonUtil.getController( StudiesController.MANAGER_BEAN_NAME );
		KnowledgeController kc = 
			(KnowledgeController) AonUtil.getController( KnowledgeController.MANAGER_BEAN_NAME );
		WorkExperienceController wec = 
			(WorkExperienceController) AonUtil.getController( WorkExperienceController.MANAGER_BEAN_NAME );
		LanguageController lc = (LanguageController) AonUtil.getController( LanguageController.MANAGER_BEAN_NAME );
		EvaluateController oc = (EvaluateController) AonUtil.getController( EvaluateController.MANAGER_BEAN_NAME );
		try {
			sc.onStudies(null);
			kc.onKnowledges(null);
			wec.onWorkExperience(null);
			lc.onLanguages(null);
			oc.onEvaluation(null);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isImageAttached(){
		if(this.attach != null){
			return true;
		}
		return false;
	}
	
	public String getUrl(){
		return ((Curriculum)this.getTo()).getId() + ".photo";
	}
}
