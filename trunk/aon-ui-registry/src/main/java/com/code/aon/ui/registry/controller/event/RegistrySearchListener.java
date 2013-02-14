package com.code.aon.ui.registry.controller.event;

import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Segment;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.form.event.ControllerSearchListenerEx;

public class RegistrySearchListener extends ControllerSearchListenerEx {
	
	private static final GeoZone EMPTY_GEOZONE = new GeoZone();
	
	private static final Segment EMPTY_SEGMENT = new Segment();
	
	private String preffix;

	private List<MediaType> mediaTypes;
	
	private GeoZone[] geoZones;
	
	private Segment[] segments;
	
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
	
	@Override
	protected void init() throws ManagerBeanException {
		setMediaTypes(new LinkedList<MediaType>());
		getMediaTypes().add(null);
		setGeoZones(new GeoZone[]{EMPTY_GEOZONE});
		setSegments(new Segment[]{EMPTY_SEGMENT});
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
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		String mediaType = resolveAlias("medias_mediaType");
		addEnumToCriteria(criteria, mediaType, getMediaTypes().toArray());
		String geozone = resolveAlias("addresses_geozone_id");
		addEnumToCriteria(criteria, geozone, getGeoZonesIds().toArray());		
		String segment = resolveAlias("segments_segment_id");
		addEnumToCriteria(criteria, segment, getSegmentsIds().toArray());
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
	
}