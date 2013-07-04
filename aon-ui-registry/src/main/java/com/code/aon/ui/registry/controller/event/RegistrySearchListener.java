package com.code.aon.ui.registry.controller.event;

import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Scope;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Question;
import com.code.aon.registry.QuestionValue;
import com.code.aon.registry.Registry;
import com.code.aon.registry.Segment;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.event.ControllerSearchListenerEx;
import com.code.aon.ui.registry.controller.RegistryCollectionsController;

public class RegistrySearchListener extends ControllerSearchListenerEx {
	
	private static final GeoZone EMPTY_GEOZONE = new GeoZone();
	
	private static final Segment EMPTY_SEGMENT = new Segment();
	
	private static final Scope EMPTY_SCOPE = new Scope();
	
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
	
	public Scope[] getScopes() {
		if (ArrayUtils.isEmpty(scopes)) {
			scopes = new Scope[]{EMPTY_SCOPE};
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
	
	@Override
	protected void init() throws ManagerBeanException {
		setMediaTypes(new LinkedList<MediaType>());
		getMediaTypes().add(null);
		setGeoZones(new GeoZone[]{EMPTY_GEOZONE});
		setSegments(new Segment[]{EMPTY_SEGMENT});
		setScopes( new Scope[]{EMPTY_SCOPE} );
		IManagerBean registryBean = BeanManager.getManagerBean(Registry.class);
		setRegistrySeller( (Registry) registryBean.createNewTo() );
		setQuestion( (Question) BeanManager.getManagerBean(Question.class).createNewTo() );
		resetQuestionValue();
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
	
	private void completeCriteria( Criteria criteria, QuestionValue qv ) {
		switch ( qv.getQuestion().getType() ) {
			case BOOLEAN:
			case NUMBER:
				criteria.addEqualExpression("Registry.profiles.number", qv.getNumber());
				break;
			case DATE:
				criteria.addEqualExpression("Registry.profiles.date", qv.getDate());
				break;
			case TEXT:
				Expression exp = ExpressionUtilities.getLikeExpression("Registry.profiles.text", "%"+qv.getText()+"%");
				criteria.addExpression(exp);
				break;
		}		
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
			addEnumToCriteria(criteria, "Registry.scope<id", getScopesIds().toArray());	
		}		
		if ( (getRegistrySeller() != null) && (getRegistrySeller().getId() != null) ) {
			String seller = getController().resolveAlias("Registry_sellers_seller_id");
			criteria.addEqualExpression(seller, getRegistrySeller().getId());			
		}
		if ( isQuestionResolved() ) {
			criteria.addEqualExpression("Registry.profiles.question.id", getQuestion().getId());	
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
		this.scopes = (Scope[]) ArrayUtils.add(this.scopes, EMPTY_SCOPE);
	}
	
	public void onRemoveScope(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("index"));		
		this.scopes = (Scope[]) ArrayUtils.remove(this.scopes, index);
		if ( ArrayUtils.isEmpty(this.scopes) ) {
			setScopes(new Scope[]{EMPTY_SCOPE});
		}
	}		
	
}