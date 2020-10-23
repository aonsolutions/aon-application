	export const removeRootPanel = () => {
		const myNode = document.getElementById("rootPanel");
		myNode.innerHTML = '';
	}

	export const rootPanel = (html) => {
		const myNode = document.getElementById("rootPanel");
		myNode.innerHTML = '';
		myNode.innerHTML = html;
		componentHandler.upgradeDom();
	}

	export const drawChartsCallback = () => {

	}

	export const getToken = () => {
		return localStorage.getItem("aon_session_id");
	}

	export const isTediSnapshot = () => {
	   	return false;
	}

	export const isTediCenter = () => {
	  	return true;
	}

	export const preStartModule = (module) => {
		google.load("visualization", "1", {'callback' : 'drawChartsCallback' ,'packages':["corechart","table"], 'language': 'es'});
		var search = `/${module}.nocache.js`;
		var scripts = window.document.getElementsByTagName("script");
		for (var i = 0; i < scripts.length; ++i) {
			var script = scripts[i];
			if (script.src != null && script.src.indexOf(search) != -1) {
				var parent = script.parentNode;
				parent.removeChild(script);
				break;
			}
		}

		var iframes = window.document.getElementsByTagName("iframe");
		for (var i = 0; i < iframes.length; ++i) {
			var iframe = iframes[i];
			if (iframe.src != null && iframe.id == module) {
				var parent = iframe.parentNode;
				parent.removeChild(iframe);
				break;
			}
		}
	}

	export const startModule = (module, entrypoint) => {
		removeRootPanel();
		preStartModule(module);
		if (window.document.createElement && window.document.getElementsByTagName) {
			var script = window.document.createElement("script");
			script.type = "text/javascript";
			script.defer = "true";
			script.src = `${module}/${module}.nocache.js?entryPoint=${entrypoint}`;
			var heads = window.document.getElementsByTagName("head");
			if (heads && heads[0]) {
				heads[0].appendChild(script);
				triggerModuleStart(module);
			}
		}
	}

	export const triggerModuleStart = (module) => {
		try{
			module.onInjectionDone(module);
	        if ( !window.document.createEventObject ) {
	           	var evt = window.document.createEvent("HTMLEvents");
	           	evt.initEvent("DOMContentLoaded", true, true);
	           	window.document.dispatchEvent(evt);
	        }
		} catch ( e ) {
			// window.setTimeout("triggerModuleStart()", 100 );
		}
	}
