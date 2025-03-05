import { CONSTANT, EVENT, TAG } from "../environments/environments.js";
import { getPosition } from "../services/maps.js";
import { waitEl } from "../services/utils.js";
import { AonElement } from "./AonElement.js";

export class AonMap extends AonElement {
  MAP;
  POSITION;
  GEOCODER;
  MARKER;
  ZOOM;
  DOC;
  WD;

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  get geocoder() {
    return this.getAttribute("geocoder") == "true";
  }

  set geocoder(geocoder) {
    this.setAttribute("geocoder", geocoder);
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {
    this.id = this.id || "aonMap";
    this.ZOOM = this.ZOOM || 16;
  }

  build() {
    let iframe = this.createElement(TAG.IFRAME);
    iframe.id = this.id;
    iframe.frameborder = 0;
    iframe.style.border = 0;
    iframe.style.height = "400px";
    iframe.style.width = "100%";
    iframe.onload = async () => {
        this.DOC = iframe.contentDocument;
        this.WD = iframe.contentWindow;

        await Promise.all([
            this.loadLink("https://unpkg.com/leaflet@1.7.1/dist/leaflet.css"),
            this.loadScript("https://unpkg.com/leaflet@1.7.1/dist/leaflet.js")
        ]);

        await waitEl("link[href*='leaflet.css']", this.DOC);
        await waitEl("script[src*='leaflet.js']", this.DOC);

        if(!this.POSITION)
          this.POSITION = await getPosition().then(({ latitude, longitude }) => ({ lat:latitude, lng:longitude })).catch((e) => null);
        
        this.POSITION = { lat: this.POSITION.latitude || this.POSITION.lat, lng: this.POSITION.longitude || this.POSITION.lng };

        this.initMap();
    }; //onload
    this.appendChild(iframe);
  }

  initMap() {
  
    let mapContainer = this.DOC.createElement(TAG.DIV);
    mapContainer.style.width = "100%";
    mapContainer.style.height = "100%";

    this.DOC.body.appendChild(mapContainer);

    this.MAP = this.WD.L.map(mapContainer, { attributionControl: false, zoomControl: false }).setView(this.POSITION, this.ZOOM);
    
    //ZOOM
    this.WD.L.control.zoom({ position: 'bottomright' }).addTo(this.MAP);

    new this.WD.L.tileLayer('https://{s}.google.com/vt/lyrs=m&x={x}&y={y}&z={z}',{  subdomains: ["mt0", "mt1", "mt2", "mt3"] }).addTo(this.MAP);

    this.MARKER = this.addMarker(this.POSITION);

    this.setCoordinates(this.POSITION);

    if(this.geocoder){
        Promise.all([
            this.loadScript("https://unpkg.com/leaflet-control-geocoder/dist/Control.Geocoder.js"),
            this.loadLink("https://unpkg.com/leaflet-control-geocoder/dist/Control.Geocoder.css")
        ]).then(()=>{
            Promise.all([
                waitEl("link[href*='Geocoder.css']", this.DOC),
                waitEl("script[src*='Geocoder.js']", this.DOC)
            ]).then(()=>{
                this.initGeocoder();
            });
        });   
    }

  }

  addMarker({lat, lng}){
    let marker = this.WD.L.marker([lat, lng], { draggable: this.geocoder  ? true : false }).addTo(this.MAP);
    marker.on('dragend',  () =>{
      this.setCoordinates(marker.getLatLng());
      if(this.geocoder)
        this.geocodeReverse(marker.getLatLng());
    });
    return marker;
  } 

  setCoordinates({lat, lng}) {
    if (lat && lng) 
      this.dispatchEvent(new CustomEvent(EVENT.COORDINATES, { detail: {lat, lng} }));
  }

  initGeocoder(){
    this.GEOCODER = this.WD.L.Control.Geocoder.nominatim({geocodingQueryParams: { countrycodes: "es" }, });
    this.geocodeReverse(this.POSITION);

    this.WD.L.Control.geocoder({
        placeholder: "Dirección",
        position: "topright",
        errorMessage:"Dirección no encontrada. Arrastre manualmente el marcador <br> a la ubicación (puede acercar o alejar la imagen)",
        defaultMarkGeocode: false,
        collapsed: false,
        geocoder: this.GEOCODER,
    })
    .on("markgeocode", (result) => {
        const geocode = result.geocode;

        if (this.MARKER) this.MAP.removeLayer(this.MARKER);

        const latlng = geocode.center;

        this.setCoordinates(latlng);

        this.MARKER = this.addMarker(latlng).bindPopup(geocode.name).openPopup();

        this.MAP.fitBounds(geocode.bbox);
        this.MAP.invalidateSize();
    })
    .addTo(this.MAP);
  }

  async geocodeReverse({ lat, lng }) {
    this.geocodeLoading(true);
    let data = await new Promise((resolve, reject) => {
      this.GEOCODER.reverse({ lat, lng }, this.MAP.options.crs.scale(this.MAP.getZoom()),
        (results) => {
          try {
            let r = results[0];
            if (r && r.name) resolve(r.name);
          } catch (error) {}
          reject(null);
        }
      );
    });

    if (data){
        this.DOC.querySelector(".leaflet-control-geocoder-form > input").value = data;
        this.dispatchEvent(new CustomEvent(EVENT.GEOCODE, { detail: {lat, lng, name:data} }));
    }

    this.geocodeLoading(false);
  }

  geocodeLoading(load = false) {
    try {
      const classLoad = "leaflet-control-geocoder-throbber";
      let buttonParent = this.DOC.querySelector( "button.leaflet-control-geocoder-icon").parentNode;
      if (buttonParent) load ? buttonParent.classList.add(classLoad) : buttonParent.classList.remove(classLoad);
    } catch (error) {}
  }

  loadLink(url){ 
    return new Promise((resolve, reject) => {
      const link = this.DOC.createElement('link');
      this.DOC.head.appendChild(link);
      link.onload = resolve;
      link.onerror = reject;
      link.href = url;
      link.rel = "stylesheet";
      link.type = "text/css";
  });
 }

  loadScript(url) {
    return new Promise((resolve, reject) => {
      let script = this.DOC.querySelector(`script[src="${url}"]`);
      if(!script){
          script = this.DOC.createElement('script');
          this.DOC.head.appendChild(script);
          script.onload = resolve;
          script.onerror = reject;
          script.src = url;
      } else resolve(true);
    });
  }
}

if (!window.customElements.get("aon-map")) 
  window.customElements.define("aon-map", AonMap);

