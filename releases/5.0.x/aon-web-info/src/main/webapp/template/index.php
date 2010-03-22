<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <title>Google Maps JavaScript API Example</title>
    <script src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=ABQIAAAAgPdwUr5sG-vEBBCX4Dxu7xSD2ZVDwZ9yVfP_FDbWvPbpQjCX6xT_FIcAY5B4yHs2JcJE3rvT_r3XRg"
      type="text/javascript"></script>
    <script type="text/javascript">
    //<![CDATA[

    var map = null;
    var geocoder = null;

    function initialize() {
      if (GBrowserIsCompatible()) {
        map = new GMap2(document.getElementById("map"));
        map.addControl(new GSmallMapControl());
        map.addControl(new GMapTypeControl());
	 map.addControl(new GOverviewMapControl(new GSize(90,90)));
	 geocoder = new GClientGeocoder();
        if (geocoder) {
          address = "<? echo $_GET{dir} ?>";
	  geocoder.getLatLng(address,
	    function(point) {
	      if (!point) {
	        alert(address + " no encontrada");
	      } else {
	        map.setCenter(point, 15);
	        var marker = new GMarker(point);
	        map.addOverlay(marker);
	        //marker.openInfoWindowHtml(address);
	      }
	    }
	  );
        }
      }
    }
    //]]>
    </script>
  </head>
  <body onload="initialize()" onunload="GUnload()" style="margin:0em">
    <div id="map" style="width: 320px; height: 200px"></div>
  </body>
</html>