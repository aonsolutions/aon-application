var SUP_SERVLET = "/field.supportOrderLookup";

function findSupportOrderLookup(evt, ctx, pojo, ids) {
    // equalize W3C/IE event models to get event object
    evt = (evt) ? evt : ((window.event) ? window.event : null);
    if (evt) {
        // equalize W3C/IE models to get event target reference
        var elem = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null);
        if (elem) {
            try {
				url = location.protocol + '://' + location.host + ctx + SUP_SERVLET;
				var fullURL = url + '?name=' + elem.name + '&value=' + elem.value + '&pojo=' + pojo;
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

function findSupportOrderLookupAndSubmit(formSubmit, evt, ctx, pojo,ids) {
	findSupportOrderLookup(evt,ctx,pojo,ids);
	mustSubmitForm = true;
	formToSubmit = formSubmit;
}