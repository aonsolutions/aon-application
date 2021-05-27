package com.code.aon.ui.registry.controller.event;

import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Scope;
import com.code.aon.geozone.GeoZone;
import com.code.aon.project.ActivityType;
import com.code.aon.project.Project;
import com.code.aon.project.ProjectActivity;
import com.code.aon.project.ProjectType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Question;
import com.code.aon.registry.QuestionValue;
import com.code.aon.registry.Registry;
import com.code.aon.registry.Segment;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerSearchListenerEx;
import com.code.aon.ui.registry.controller.RegistryCollectionsController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class RegistrySearchListener extends ControllerSearchListenerEx {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final GeoZone EMPTY_GEOZONE = new GeoZone();
	
	private static final Segment EMPTY_SEGMENT = new Segment();
	
	private String preffix;

	private List<MediaType> mediaTypes;
	
	private GeoZone[] geoZones;
	
	private Segment[] segments;
	
	private Scope[] scopes;
	
	private Question question;
	
	private QuestionValue questionValue;
	
	private List<SelectItem> questionValues;
	
	private Integer questionValueId;	
	
	private Registry registrySeller;
	
	private ProjectType projectType;
	
	private ActivityType activityType;
	
	private String address;
	
	public Scope[] getScopes() {
		if (ArrayUtils.isEmpty(scopes)) {
			scopes = new Scope[]{getEmptyScope()};
		}
		return scopes;
	}

	public void setScopes(Scope[] scopes) {
		this.scopes = scopes;
	}

	public int getScopesSize() {
		return ArrayUtils.getLength(scopes);
	}
	
	public List<Integer> getScopesIds() {
		List<Integer> ids = new LinkedList<Integer>();
		for( Scope scope : getScopes() ) {
			if ((scope != null) && (scope.getId() != null)) {
				ids.add(scope.getId());
			}
		}
		return ids;
	}			

	public List<MediaType> getMediaTypes() {
		if (mediaTypes == null) {
			mediaTypes = new LinkedList<MediaType>();
			mediaTypes.add(null);
		}
		return mediaTypes;
	}

	public void setMediaTypes(List<MediaType> mediaTypes) {
		this.mediaTypes = mediaTypes;
	}

	public int getMediaTypesSize() {
		return mediaTypes.size();
	}
	
	public GeoZone[] getGeoZones() {
		if (geoZones == null) {
			geoZones = new GeoZone[]{EMPTY_GEOZONE};
		}
		return geoZones;
	}

	public void setGeoZones(GeoZone[] geoZones) {
		this.geoZones = geoZones;
	}

	public int getGeoZonesSize() {
		return ArrayUtils.getLength(geoZones);
	}
	
	public List<Integer> getGeoZonesIds() {
		List<Integer> ids = new LinkedList<Integer>();
		for(GeoZone geozone : getGeoZones()) {
			if ((geozone != null) && (geozone.getId() != null)) {
				ids.add(geozone.getId());
			}
		}
		return ids;
	}	
	
	public GeoZone getEmptyGeoZone() {
		return EMPTY_GEOZONE;
	}
	
	public Segment[] getSegments() {
		if (segments == null) {
			segments = new Segment[]{EMPTY_SEGMENT};
		}
		return segments;
	}

	public void setSegments(Segment[] segments) {
		this.segments = segments;
	}
	
	public int getSegmentsSize() {
		return ArrayUtils.getLength(segments);
	}	

	public Segment getEmptySegment() {
		return EMPTY_SEGMENT;
	}
	
	public List<Integer> getSegmentsIds() {
		List<Integer> ids = new LinkedList<Integer>();
		for(Segment segment : getSegments()) {
			if ((segment != null) && (segment.getId() != null)) {
				ids.add(segment.getId());
			}
		}
		return ids;
	}
	
	public Registry getRegistrySeller() {
		return registrySeller;
	}

	public void setRegistrySeller(Registry registrySeller) {
		this.registrySeller = registrySeller;
	}

	public Integer getQuestionValueId() {
		return questionValueId;
	}

	public void setQuestionValueId(Integer questionValueId) {
		this.questionValueId = questionValueId;
	}

	public List<SelectItem> getQuestionValues() {
		return questionValues;
	}
	
	public void setQuestionValues(List<SelectItem> questionValues) {
		this.questionValues = questionValues;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public boolean isQuestionResolved() {
		return (getQuestion() != null) && (getQuestion().getId() != null);
	}
	
	public QuestionValue getQuestionValue() {
		return questionValue;
	}

	public void setQuestionValue(QuestionValue questionValue) {
		this.questionValue = questionValue;
	}


	public void questionChanged( LookupChangeEvent event ) throws ManagerBeanException {
		Question question = (Question) event.getNewValue();
		if ( event.getNewValue() != null ) {
			QuestionValue qv = new QuestionValue();
			qv.setQuestion(question);
			setQuestionValue( qv );
			questionValues = RegistryCollectionsController.getQuestionValues(question);
		} else {
			resetQuestionValue();
		}
	}
	
	private void resetQuestionValue() {
		setQuestionValue(null);
		setQuestionValueId(null);
		setQuestionValues(null);		
	}
	
	public ProjectType getProjectType() {
		return projectType;
	}

	public void setProjectType(ProjectType projectType) {
		this.projectType = projectType;
	}
	
	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}
	
	public List<SelectItem> getAvailableActivityTypes() throws ManagerBeanException {
		List<SelectItem> activityTypes = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(ActivityType.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACTIVITY_TYPE_ACTIVE), true);
		if ( (getProjectType() != null) && (getProjectType().getId() != null) ) {		
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACTIVITY_TYPE_PROJECT_TYPE_ID), getProjectType().getId());
		} else {
			criteria.addNullExpression(bean.getFieldName(IEntityAlias.ACTIVITY_TYPE_PROJECT_TYPE_ID));
		}
		criteria.addOrder(bean.getFieldName(IEntityAlias.ACTIVITY_TYPE_DESCRIPTION));
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to:list) {
			ActivityType activityType = (ActivityType) to;
			SelectItem item = new SelectItem(activityType, activityType.getDescription());
			activityTypes.add(item);
		}
		return activityTypes;
	}	

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setMediaTypes(new LinkedList<MediaType>());
		getMediaTypes().add(null);
		setGeoZones(new GeoZone[]{EMPTY_GEOZONE});
		setSegments(new Segment[]{EMPTY_SEGMENT});
		setScopes( new Scope[]{getEmptyScope()} );
		IManagerBean registryBean = BeanManager.getManagerBean(Registry.class);
		setRegistrySeller( (Registry) registryBean.createNewTo() );
		setQuestion( (Question) BeanManager.getManagerBean(Question.class).createNewTo() );
		resetQuestionValue();
		setProjectType(null);
		setActivityType(null);
		setAddress(null);
	}
	
	public String getPreffix() throws ManagerBeanException {
		if (preffix == null) {
			Class<?> pojoClass = getController().getManagerBean().getPOJOClass();
			preffix = ClassUtils.getShortClassName(pojoClass) + "_registry_";
		}
		return preffix;
	}
	
	protected String resolveAlias(String alias) throws ManagerBeanException {
		return getController().resolveAlias(getPreffix() + alias);
	}

	private String getPojoShortName() {
		BasicController controller = (BasicController) getController();
		return controller.getPojoShortName();
	}
		
	private void completeCriteria( Criteria criteria, QuestionValue qv ) {
		switch ( qv.getQuestion().getType() ) {
			case BOOLEAN:
			case NUMBER:
				criteria.addEqualExpression(getPojoShortName()+".profiles.number", qv.getNumber());
				break;
			case DATE:
				criteria.addEqualExpression(getPojoShortName()+".profiles.date", qv.getDate());
				break;
			case TEXT:
				Expression exp = ExpressionUtilities.getLikeExpression(getPojoShortName()+".profiles.text", "%"+qv.getText()+"%");
				criteria.addExpression(exp);
				break;
		}		
	}	
	
	public void addProjectTypeSubQuery(ProjectType type, Criteria criteria) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Project.class);			
		Criteria subCriteria = new Criteria();
		subCriteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_PROJECT_TYPE_ID), type.getId());
		String idAlias = bean.getFieldName(IEntityAlias.PROJECT_REGISTRY_ID);
		ProjectionList pl = new ProjectionList( Projection.property(idAlias) );
		Expression exp = ExpressionUtilities.getSubQueryExpression(Project.class, subCriteria, pl);
		criteria.addInExpression(getPojoShortName()+".id", exp);			
	}

	public void addActivityTypeSubQuery(ActivityType type, Criteria criteria) throws ManagerBeanException {
		IManagerBean paBean = BeanManager.getManagerBean(ProjectActivity.class);
		Criteria paSubCriteria = new Criteria();
		paSubCriteria.addEqualExpression(paBean.getFieldName(IEntityAlias.PROJECT_ACTIVITY_ACTIVITY_TYPE_ID), type.getId());
		String paIdAlias = paBean.getFieldName(IEntityAlias.PROJECT_ACTIVITY_PROJECT_ID);
		ProjectionList paPL = new ProjectionList( Projection.property(paIdAlias) );
		Expression paExp = ExpressionUtilities.getSubQueryExpression(ProjectActivity.class, paSubCriteria, paPL);
		
		IManagerBean bean = BeanManager.getManagerBean(Project.class);			
		Criteria subCriteria = new Criteria();
		subCriteria.addInExpression(bean.getFieldName(IEntityAlias.PROJECT_ID), paExp);
		String idAlias = bean.getFieldName(IEntityAlias.PROJECT_REGISTRY_ID);
		ProjectionList pl = new ProjectionList( Projection.property(idAlias) );
		Expression exp = ExpressionUtilities.getSubQueryExpression(Project.class, subCriteria, pl);
		criteria.addInExpression(getPojoShortName()+".id", exp);			
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		String mediaType = resolveAlias("medias_mediaType");
		addEnumToCriteria(criteria, mediaType, getMediaTypes().toArray());
		String geozone = resolveAlias("addresses_geozone_id");
		addEnumToCriteria(criteria, geozone, getGeoZonesIds().toArray());		
		String segment = resolveAlias("segments_segment_id");
		addEnumToCriteria(criteria, segment, getSegmentsIds().toArray());
		if ( getScopesSize() > 0 ) {
			addEnumToCriteria(criteria, getPojoShortName()+".scope<id", getScopesIds().toArray());	
		}		
		if ( (getRegistrySeller() != null) && (getRegistrySeller().getId() != null) ) {
			String seller = getController().resolveAlias(getPojoShortName()+"_sellers_seller_id");
			criteria.addEqualExpression(seller, getRegistrySeller().getId());			
		}
		if ( isQuestionResolved() ) {
			criteria.addEqualExpression(getPojoShortName()+".profiles.question.id", getQuestion().getId());	
			if ( getQuestionValueId() != null ) {
				IManagerBean bean = BeanManager.getManagerBean(QuestionValue.class);
				QuestionValue qv = (QuestionValue) bean.get(getQuestionValueId());
				if ( qv != null ) {
					completeCriteria(criteria, qv);
				}
			} else if (! getQuestionValue().isNotFilled() ) {
				completeCriteria(criteria, getQuestionValue());
			}
		}
		if ( (getActivityType() != null) && (getActivityType().getId() != null) ) {
			addActivityTypeSubQuery(getActivityType(), criteria);
		} else if ( (getProjectType() != null) && (getProjectType().getId() != null) ) {
			addProjectTypeSubQuery(getProjectType(), criteria);			
		}	
		if (! StringUtils.isEmpty(address) ) {
			String pojo = ((BasicController) getController()).getPojo();
			Expression exp1 = FormUtil.getExpression(criteria, pojo, resolveAlias("addresses_address"), address);
			Expression exp2 = FormUtil.getExpression(criteria, pojo, resolveAlias("addresses_address2"), address);
			Expression exp3 = FormUtil.getExpression(criteria, pojo, resolveAlias("addresses_address3"), address);
			Expression expOr = ExpressionUtilities.getOrExpression(exp1, exp2);
			criteria.addExpression(ExpressionUtilities.getOrExpression(expOr, exp3));
		}
	}
	
	public void onAddMediaType(ActionEvent event) {
		this.mediaTypes.add(null);
	}
	
	public void onRemoveMediaType(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("index"));		
		this.mediaTypes.remove(index);
		if (this.mediaTypes.isEmpty()) {
			this.mediaTypes.add(null);
		}
	}

	public void onAddGeoZone(ActionEvent event) {
		this.geoZones = (GeoZone[]) ArrayUtils.add(this.geoZones, EMPTY_GEOZONE);
	}
	
	public void onRemoveGeoZone(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("index"));		
		this.geoZones = (GeoZone[]) ArrayUtils.remove(this.geoZones, index);
		if ( ArrayUtils.isEmpty(this.geoZones) ) {
			setGeoZones(new GeoZone[]{EMPTY_GEOZONE});
		}
	}	
	
	public void onAddSegment(ActionEvent event) {
		this.segments = (Segment[]) ArrayUtils.add(this.segments, EMPTY_SEGMENT);
	}
	
	public void onRemoveSegment(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("index"));		
		this.segments = (Segment[]) ArrayUtils.remove(this.segments, index);
		if ( ArrayUtils.isEmpty(this.segments) ) {
			setSegments(new Segment[]{EMPTY_SEGMENT});
		}
	}

	public void onAddScope(ActionEvent event) {
		this.scopes = (Scope[]) ArrayUtils.add(this.scopes, getEmptyScope());
	}
	
	public void onRemoveScope(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("index"));		
		this.scopes = (Scope[]) ArrayUtils.remove(this.scopes, index);
		if ( ArrayUtils.isEmpty(this.scopes) ) {
			setScopes(new Scope[]{getEmptyScope()});
		}
	}		

	private ConfigCollectionsController getCollectionsController() {
		return (ConfigCollectionsController)AonUtil.getRegisteredBean(ConfigConstants.CONFIG_COLLECTIONS);
	}
	
	private Scope getEmptyScope() {
		return getCollectionsController().getEmptyScope();
	}
		
}