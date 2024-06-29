	import { TAG } from '../environments/environments.js'; 
	import * as LS from '../services/localStorageService.js';

	
	export const removeRootPanel = (panel) => {
		panel = panel || 'rootPanel';
		const myNode = window.document.getElementById(panel);
		myNode.innerHTML = '';
	}

	export const rootPanel = (html) => new Promise((resolve)=>{
		const myNode = window.document.getElementById("rootPanel");
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

	export const __load = (gwtOption, rootPanel) => {
		if(gwtOption.subEntryPoint) loadEntryPointsFunctions(gwtOption);
		window.drawChartsCallback = () => {};
		__startModule(gwtOption.module, gwtOption.entryPoint, rootPanel);
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
		localStorage.setItem('aon_solutions', true);
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

		const element = document.getElementById(panel);
		if(element && element.firstChild){
			element.firstChild.style.inset = '0px';
		}
	}

	export const __startModule = (module, entrypoint, rootPanel) => {
		let panel = rootPanel || 'rootPanel';
		if(rootPanel) {
			localStorage.setItem('rootPanel', rootPanel);
		} else {
			localStorage.removeItem('rootPanel');
		}
		localStorage.setItem('aon_solutions', true);
		removeRootPanel(panel);
		if (window.document.createElement && window.document.getElementsByTagName) {
			
			let iframe = window.document.createElement(TAG.IFRAME)
			iframe.style.width = '100%';
			iframe.style.height = '100%';
			iframe.style.border = 'none';
			iframe.style.inset = 'none';
			iframe.src = 'about:blank';
			iframe.onload = () => {
				
				// loadDomainFunctions
				let iwindow = iframe.contentWindow;			
				iwindow.getCurrentDomainNameURL = () => LS.getDomainName();
				iwindow.getCurrentDomainName = () => LS.getDomainName();
				iwindow.getCurrentDomain = () => LS.getDomainId();
				iwindow.getCurrentUser = () => LS.getDomainLogin();

				// inject 'gwt' script 
				let idocument = iframe.document || iframe.contentDocument || iframe.contentWindow.document;		
				
				for (const sheet of document.styleSheets) {
					if ( sheet?.href?.includes('fonts.googleapis.com') ){
						let link = idocument.createElement('link');
						link.rel= 'stylesheet';
						link.type= 'text/css';
						link.href = sheet.href;
						idocument.head.appendChild(link);
					}
					
				}	

				let script = idocument.createElement(TAG.SRIPT);
				script.type = "text/javascript";
				script.defer = "true";
				script.text = `
				//<![CDATA[ 

					function startModule() {
					    if (document.createElement && document.getElementsByTagName) {
					      var script = document.createElement('script');
					      script.type = 'text/javascript';
					      script.src = '${module}/${module}.nocache.js?entryPoint=${entrypoint}&id=${getRamdomId()}';
						  script.defer = true;
						  document.head.appendChild(script);
						  triggerModuleStart();
						}
					}				

					function triggerModuleStart(){
					    try{
						    ${module}.onInjectionDone('${module}');
						    if ( !document.createEventObject ) {
						        var evt = document.createEvent("HTMLEvents");
						        evt.initEvent("DOMContentLoaded", true, true);
						        document.dispatchEvent(evt);
						     }
					    } catch ( e ) {
					    	//window.setTimeout('triggerModuleStart()', 100 );
					   } 
					}
					
					startModule();
				//]]>
				`;

				idocument.head.appendChild(script);
/*	
				let gwtScript = idocument.createElement(TAG.SRIPT);
				gwtScript.type = "text/javascript";
				gwtScript.defer = "true";
				gwtScript.src = `${module}/${module}.nocache.js?entryPoint=${entrypoint}&id=${getRamdomId()}`;
				idocument.head.appendChild(gwtScript);
*/				
				
				fetch('css/gwt.css')
				.then(response => response.text())
				.then((text) => {
					let style = idocument.createElement('style');
					style.textContent = text;
					idocument.body.appendChild(style)
				});

								

			}

			
			document.getElementById(panel)?.appendChild(iframe);

		}

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
	
