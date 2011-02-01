var isIE = false;				// global flag
var req;						// global request and XML document objects
var SERVLET = "/field.lookup"; 	// Lookup URL
var formToSubmit;				// form to submit in findLookupAndSubmit method
var BEAN_SERVLET = "/field.lookupBean"; // Lookup URL

function findLookup(evt, ctx, pojo) {
    // equalize W3C/IE event models to get event object
    evt = (evt) ? evt : ((window.event) ? window.event : null);
    if (evt) {
        // equalize W3C/IE models to get event target reference
        var elem = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null);
        if (elem) {
            try {
				url = location.protocol + '://' + location.host + ctx + SERVLET;
				sendLookup(url + '?name=' + elem.name + '&value=' + elem.value + '&pojo=' + pojo);
            } catch(e) {
                var msg = (typeof e == "string") ? e : ((e.message) ? e.message : "Unknown Error");
                alert("Unable to get XML data:\n" + msg);
                return;
            }
        }
    }
}

function findLookupAndSubmit(formSubmit, evt, ctx, pojo) {
	findLookup(evt,ctx,pojo);
	mustSubmitForm = true;
	formToSubmit = formSubmit;
}

function getValues( valueIds ) {
    var result = "";
    var name = "";
	var start = 0;
	while ( start < valueIds.length ) {
		var end = valueIds.indexOf(',', start );
		if ( end == -1 ) {
			name = valueIds.substring( start );
			start = valueIds.length;
		} else {
			name = valueIds.substring( start, end );
			start = end + 1;
		}
   		var node = document.getElementById(name);
   		if ( node ) {
   			result += node.value + ",";
   		}
	}
    return result;
}

function findBeanLookup(evt, ctx, bean, values, ids) {
    // equalize W3C/IE event models to get event object
    evt = (evt) ? evt : ((window.event) ? window.event : null);
    if (evt) {
        // equalize W3C/IE models to get event target reference
        var elem = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null);
        if (elem) {
            try {
				var url = location.protocol + '://' + location.host + ctx + BEAN_SERVLET;
				var fullURL = url + '?bean=' + bean + '&value=' + getValues(values);
                if ( ids ) {
                    fullURL += '&ids=' + ids;
                } 
				sendLookup( fullURL );
            } catch(e) {
                var msg = (typeof e == "string") ? e : ((e.message) ? e.message : "Unknown Error");
                alert("Unable to get XML data:\n" + msg);
                return;
            }
        }
    }
}

function findBeanLookupAndSubmit(formSubmit, evt, ctx, pojo) {
	findBeanLookup(evt,ctx,pojo);
	mustSubmitForm = true;
	formToSubmit = formSubmit;
}


function sendLookup(url) {
    // branch for native XMLHttpRequest object
    if (window.XMLHttpRequest) {
        req = new XMLHttpRequest();
        req.onreadystatechange = processReqChange;
        req.open("GET", url, true);
        req.send(null);
    // branch for IE/Windows ActiveX version
    } else if (window.ActiveXObject) {
        isIE = true;
        req = new ActiveXObject("Microsoft.XMLHTTP");
        if (req) {
            req.onreadystatechange = processReqChange;
            req.open("GET", url, true);
            req.setRequestHeader( "If-Modified-Since", "Sat, 1 Jan 2000 00:00:00 GMT" );
            req.send(null);
        }
    }
}

// handle onreadystatechange event of req object
function processReqChange() {
    // only if req shows "loaded"
    if (req.readyState == 4) {
        // only if "OK"
        if (req.status == 200) {
            showLookup();
            if (mustSubmitForm) {
				formToSubmit.submit();
			}
         } else {
            alert("There was a problem retrieving the XML data:\n Status: " + req.status + " - " + req.statusText);
         }
    }
}

// fill Lookup list with items from the current XML document
function showLookup() {
    try {
		var items = req.responseXML.getElementsByTagName("item")[0];
		// loop through <item> elements, and update each lookup element
	    for (var i = 0; i < items.childNodes.length; i++) {
	    	try {
        	    var child = items.childNodes[i];
        		var nodeName = child.getAttribute('name');
        		var node = document.getElementById(nodeName);
        		if ( node ) {
        			node.value = child.getAttribute('value');
					// MyFaces disabledOnClientSide
					node.onchange();
                }
	    	} catch(e) { }
	    }
    } catch(e) {
    	alert("An error ocurred while reading response.");
    }
}
