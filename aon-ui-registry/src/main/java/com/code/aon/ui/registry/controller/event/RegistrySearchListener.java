package com.code.aon.ui.registry.controller.event;

import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;

public class RegistrySearchListener extends ControllerSearchListener {

	private static final String LOOKUP_LISTENER = "LookupListener";
	
	private static final String MEDIA_SUFFIX = "Media";

	private static final String ADDRESS_SUFFIX = "Address";

	private static final Logger LOGGER = LoggerFactory.getLogger(RegistrySearchListener.class);
	
	private static final GeoZone EMPTY_GEOZONE = new GeoZone();
	
	private String preffix;

	private List<MediaType> mediaTypes;
	
	private List<GeoZone> geoZones;
	
	private List<String> segments;
	
	private RegistryLookupListener formListener;
	
	private LinesController registryAddress;
	
	private LinesController registryMedia;
	
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
	
	public List<GeoZone> getGeoZones() {
		if (geoZones == null) {
			geoZones = new LinkedList<GeoZone>();
			geoZones.add(EMPTY_GEOZONE);
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
	
	public List<String> getSegments() {
		if (segments == null) {
			segments = new LinkedList<String>();
			segments.add(null);
		}
		return segments;
	}

	public void setSegments(List<String> segments) {
		this.segments = segments;
	}
	
	public int getSegmentsSize() {
		return this.segments.size();
	}	

	public List<Integer> getSegmentsIds() {
		List<Integer> ids = new LinkedList<Integer>();
		for(String segment : getSegments()) {
			if (! StringUtils.isBlank(segment)) {
				ids.add(Integer.valueOf(segment));
			}
		}
		return ids;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		setMediaTypes(new LinkedList<MediaType>());
		getMediaTypes().add(null);
		setGeoZones(new LinkedList<GeoZone>());
		getGeoZones().add(EMPTY_GEOZONE);
		setSegments(new LinkedList<String>());
		getSegments().add(null);
	}
	
	public String getPreffix() throws ManagerBeanException {
		if (preffix == null) {
			Class<?> pojoClass = getController().getManagerBean().getPOJOClass();
			preffix = ClassUtils.getShortClassName(pojoClass) + "_registry_";
		}
		return preffix;
	}
	
	private String resolveAlias(String alias) throws ManagerBeanException {
		return getController().resolveAlias(getPreffix() + alias);
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
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
		getGeoZones().add(EMPTY_GEOZONE);
	}
	
	public void onRemoveGeoZone(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("index"));		
		getGeoZones().remove(index);
		if (getGeoZones().isEmpty()) {
			getGeoZones().add(EMPTY_GEOZONE);
		}
	}	
	
	public void onAddSegment(ActionEvent event) {
		getSegments().add(null);
	}
	
	public void onRemoveSegment(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("index"));		
		getSegments().remove(index);
		if (getSegments().isEmpty()) {
			getSegments().add(null);
		}
	}

	private RegistryLookupListener getLookupListener(ControllerEvent event) throws ManagerBeanException {
		if ( this.formListener == null ) {
			BasicController controller = (BasicController) event.getController();
			Class<?> pojoClass = controller.getManagerBean().getPOJOClass();
			if ( IRegistry.class.isAssignableFrom(pojoClass) ) {
				String name = controller.getBeanName();
				initControllers(name);
			}
		}
		return this.formListener; 
	}
	
	protected void initControllers( String name ) {
		this.formListener = (RegistryLookupListener) AonUtil.getRegisteredBean(name + LOOKUP_LISTENER);
		if ( this.formListener == null ) {
			LOGGER.error( "Lookup listener not found for {}", name );
		}			
		this.registryAddress = (LinesController) AonUtil.getRegisteredBean(name + ADDRESS_SUFFIX);
		if ( registryAddress == null ) {
			LOGGER.error( "Registry Address Managed Bean not found for {}", name );
		}			
		this.registryMedia = (LinesController) AonUtil.getRegisteredBean(name + MEDIA_SUFFIX);
		if ( registryMedia == null ) {
			LOGGER.error( "Registry Media Managed Bean not found for {}", name );
		}					
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		try {		
			RegistryLookupListener listener = getLookupListener(event);
			if ( listener != null ) {
				listener.afterBeanCreated(event);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		try {
			RegistryLookupListener listener = getLookupListener(event);
			if ( listener != null ) {
				updateListeners( listener );
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
	protected void updateListeners( IControllerListener controllerListener ) throws ControllerListenerException {
		RegistryLookupListener listener = (RegistryLookupListener) controllerListener;
		try {
			if ( registryAddress != null ) {
				listener.updateRegistryAddress(registryAddress);	
			}
			if ( registryMedia != null ) {
				listener.updateRegistryMedia(registryMedia);	
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
}