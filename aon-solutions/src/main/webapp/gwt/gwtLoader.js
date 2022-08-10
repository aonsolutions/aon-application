	import * as LS from '../services/localStorageService.js';
	
	export const removeRootPanel = (panel) => {
		panel = panel || 'rootPanel';
		const myNode = document.getElementById(panel);
		myNode.innerHTML = '';
	}

	export const rootPanel = (html) => new Promise((resolve)=>{
		const myNode = document.getElementById("rootPanel");
		if(myNode){
			myNode.innerHTML = '';
			myNode.innerHTML = html;
			resolve(myNode);
		}
	});

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
		// google.load("visualization", "1", {'callback' : 'drawChartsCallback' ,'packages':["corechart","table"], 'language': 'es'});
		let search = `/${module}.nocache.js`;
		let scripts = window.document.getElementsByTagName("script");
		for (let i = 0; i < scripts.length; ++i) {
			let script = scripts[i];
			if (script.src != null && script.src.indexOf(search) != -1) {
				let parent = script.parentNode;
				parent.removeChild(script);
			}
		}

		let iframes = window.document.getElementsByTagName("iframe");
		for (let i = 0; i < iframes.length; ++i) {
			let iframe = iframes[i];
			if (iframe.src != null && iframe.id == module) {
				let parent = iframe.parentNode;
				parent.removeChild(iframe);
			}
		}
	}

	export const load = (gwtOption, rootPanel) => {
		if(gwtOption.subEntryPoint) loadEntryPointsFunctions(gwtOption);
		window.drawChartsCallback = () => {};
		startModule(gwtOption.module, gwtOption.entryPoint, rootPanel);
	}

	const loadEntryPointsFunctions = (gwtOption) => {
		window.getSubEntryPoint = () => gwtOption.subEntryPoint;
	}

	const loadDomainFunctions = () => {
		window.getCurrentDomainNameURL = () => LS.getDomainName();
		window.getCurrentDomainName = () => LS.getDomainName();
		window.getCurrentDomain = () => LS.getDomainId();
		window.getCurrentUser = () => LS.getDomainLogin();
	}

	export const startModule = (module, entrypoint, rootPanel) => {
		loadDomainFunctions();
		let panel = rootPanel || 'rootPanel';
		if(rootPanel) {
			localStorage.setItem('rootPanel', rootPanel);
		} else {
			localStorage.removeItem('rootPanel');
		}
		removeRootPanel(panel);
		preStartModule(module);
		if (window.document.createElement && window.document.getElementsByTagName) {
			let script = window.document.createElement("script");
			script.type = "text/javascript";
			script.defer = "true";
			script.src = `${module}/${module}.nocache.js?entryPoint=${entrypoint}&id=${getRamdomId()}`;
			let heads = window.document.getElementsByTagName("head");
			if (heads && heads[0]) {
				heads[0].appendChild(script);
				triggerModuleStart(module);
			}
		}
		if(rootPanel && rootPanel.childNodes && rootPanel.childNodes.length > 0)
			rootPanel.childNodes[0].style.inset = '0px';
	}



	export const triggerModuleStart = (module) => {
		try{
			module.onInjectionDone(module);
	        if ( !window.document.createEventObject ) {
	           	let evt = window.document.createEvent("HTMLEvents");
	           	evt.initEvent("DOMContentLoaded", true, true);
	           	window.document.dispatchEvent(evt);
	        }
		} catch ( e ) {
			// window.setTimeout("triggerModuleStart()", 100 );
		}
	}

	export const getRamdomId = () => {
		return Math.floor(Math.random() * 10000000) + 1;
	};
