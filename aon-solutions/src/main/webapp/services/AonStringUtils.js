String.prototype.initCap = function () {
  return this.charAt(0).toUpperCase() + this.slice(1).toLowerCase();
};

String.prototype.isEmpty = function() {
    return (this.length === 0 || !this.trim());
};

Date.prototype.isValid = function () { 
              
  // If the date object is invalid it 
  // will return 'NaN' on getTime()  
  // and NaN is never equal to itself. 
  return this.getTime() === this.getTime(); 
}; 

function initialize(element_id, lat, lng, zoom) {
  zoom  = zoom || 10;

  var mapLocation = new google.maps.LatLng(lat, lng);
  var mapOptions = 
  {
      center: mapLocation,
      zoom: zoom
  };

  var map = new google.maps.Map(document.getElementById(element_id), mapOptions);
}