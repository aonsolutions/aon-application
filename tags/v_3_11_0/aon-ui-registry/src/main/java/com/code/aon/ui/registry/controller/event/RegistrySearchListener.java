package com.code.aon.ui.registry.controller.event;

import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ClassUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class RegistrySearchListener extends ControllerSearchListener {
	
	private static final GeoZone EMPTY_GEOZONE = new GeoZone();
	
	private String preffix;

	private List<MediaType> mediaTypes;
	
	private List<GeoZone> geoZones;
	
	public List<MediaType> getMediaTypes() {
		if (mediaTypes == null) {
			mediaTypes = new LinkedList<MediaType>();
			mediaTypes.add( null );
		}
		return mediaTypes;
	}

	public void setMediaTypes(List<MediaType> mediaTypes) {
		this.mediaTypes = mediaTypes;
	}

	public int getMediaTypesSize() {
		return mediaTypes.size();
	}
	
	public List<GeoZone> getGeoZones() {
		if (geoZones == null ) {
			geoZones = new LinkedList<GeoZone>();
			geoZones.add( EMPTY_GEOZONE );
		}
		return geoZones;
	}

	public void setGeoZones(List<GeoZone> geoZones) {
		this.geoZones = geoZones;
	}

	public int getGeoZonesSize() {
		return geoZones.size();
	}
	
	public List<Integer> getGeoZonesIds() {
		List<Integer> ids = new LinkedList<Integer>();
		for( GeoZone geozone : getGeoZones() ) {
			if ( (geozone != null) && (geozone.getId() != null) ) {
				ids.add( geozone.getId() );
			}
		}
		return ids;
	}	
	
	public GeoZone getEmptyGeoZone() {
		return EMPTY_GEOZONE;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		setMediaTypes( new LinkedList<MediaType>() );
		getMediaTypes().add( null );
		setGeoZones( new LinkedList<GeoZone>() );
		getGeoZones().add( EMPTY_GEOZONE );
	}
	
	public String getPreffix() throws ManagerBeanException {
		if ( preffix == null ) {
			Class<?> pojoClass = getController().getManagerBean().getPOJOClass();
			preffix = ClassUtils.getShortClassName(pojoClass) + "_registry_";
		}
		return preffix;
	}
	
	private String resolveAlias( String alias ) throws ManagerBeanException {
		return getController().resolveAlias( getPreffix() + alias );
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		String mediaType = resolveAlias("medias_mediaType");
		addEnumToCriteria( criteria, mediaType, getMediaTypes().toArray() );
		String geozone = resolveAlias("addresses_geozone_id");
		addEnumToCriteria( criteria, geozone, getGeoZonesIds().toArray() );		
	}
	
	public void onAddMediaType( ActionEvent event ) {
		this.mediaTypes.add( null );
	}
	
	public void onRemoveMediaType( ActionEvent event ) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf( context.getExternalContext().getRequestParameterMap().get("index") );		
		this.mediaTypes.remove( index );
		if ( this.mediaTypes.isEmpty() ) {
			this.mediaTypes.add( null );
		}
	}

	public void onAddGeoZone( ActionEvent event ) {
		getGeoZones().add( EMPTY_GEOZONE );
	}
	
	public void onRemoveGeoZone( ActionEvent event ) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf( context.getExternalContext().getRequestParameterMap().get("index") );		
		getGeoZones().remove( index );
		if ( getGeoZones().isEmpty() ) {
			getGeoZones().add( EMPTY_GEOZONE );
		}
	}	
	
}