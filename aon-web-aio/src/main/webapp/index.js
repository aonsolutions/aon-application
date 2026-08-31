
import * as LS from './services/localStorageService.js';
import * as UA from './services/userAgentService.js';
import { AonModule } from './modules/aon-module.js';
import { setPosition } from './services/maps.js';
import { waitEl } from './services/utils.js';
import { EVENT, TAG } from './environments/environments.js';
import { saveAuthDevice } from './services/authDeviceService.js';
import { favicon, title, loadLink } from './css/aon-customView.js';


import './css/noto-sans.css';
import './css/material-symbols-outlined.css';
import './css/aon-symbols-outlined.css';

import './css/aon-css-utils.css';
import './css/aon-css-utils.css';
import './css/aon-grid.css';
import './css/aon-mobile.css';
import './css/aon-figma.css';
import './css/aon-singleton-access.css';

window.setPosition = (pos) => setPosition(pos);
window.setTokenFCM =  (token) => {
    window.tokenFCM = token;
    saveAuthDevice({tokenFCM:token});
}

window.setNotificationAction = (data) =>  {
    window.dispatchEvent( new CustomEvent(EVENT.RECEIVED_NOTIFICATION, {detail:data}));
}

window.setResumeApp = (data) =>  {
    window.dispatchEvent( new CustomEvent(EVENT.RESUME_APP, {detail:data}));
}

const load = () => {
	
	console.debug("Start loading aonSolutions.")
	console.debug("Keep your fingers crossed!" );
	console.debug("We need all the luck we can get.");
	
    LS.setAonSolutions(true);

	loadScripts();

	loadTheme()
	.finally(loadIsReadOnly)
	.finally( () =>  {
		loadModule(); 
	} ) ;  

	// TODO: loadScriptFirebase();
    window.loadScripts = () => loadScripts();

	console.debug("Fantastic aonSolutions loaded :-).")
}

export const loadModule = () => {
	title();
	favicon(); 
	document.body.appendChild(new AonModule()) ;
}

export const loadTheme = async () => {	
	let paramCss = getParam(LS.THEME) || LS.getTheme() ;
	let mobileCss = UA.isAndroidOldApp() ? LS.AON_MOBILE_ANDROID : LS.AON_MOBILE_THEME;
	if(UA.isAndroid35App()) mobileCss = LS.AON_MOBILE_ANDROID_35;
	let themeUrl = UA.isMobile() ? mobileCss : (paramCss || LS.CUSTOM_THEME );
	return loadThemeUrl(themeUrl).catch((err) => loadThemeUrl(LS.DEFAULT_THEME));
}

export const loadThemeUrl = async (themeUrl) => {	
	return new Promise((resolve, reject) => {
		try {
			const aonThemeSpan = document.createElement(TAG.SPAN);
			aonThemeSpan.className = 'aonTheme';
			aonThemeSpan.style.display = 'none';
			document.body.appendChild(aonThemeSpan);

			loadLink(themeUrl, 'stylesheet', 'text/css').then(() => {
				resolve();
			}).catch((err) => {
				reject(new Error(`Something was wrong with theme '${themeUrl}' ${err}`));
			}).finally(() => aonThemeSpan.remove());

		} catch (err) {
			reject(new Error(`Something was wrong with theme '${themeUrl}' ${err}`));
		}
	});
}

export const loadIsReadOnly = async  () => {
	
	return new Promise((resolve, reject) => {
		
		if ( isReadOnly() ) { 
			try {
				const readonlyUrl = 'css/readonly.css';
				
				const readonlySpan = document.createElement(TAG.SPAN);
				readonlySpan.className = 'aonTheme';
				readonlySpan.style.display = 'none';
				document.body.appendChild(readonlySpan);
	
				loadLink(readonlyUrl, 'stylesheet', 'text/css').then(() => {
					resolve();
				}).catch((err) => {
		            reject(new Error(`Something was wrong with readonly stylesheet ${err}`));
		        }).finally( () => readonlySpan.remove() );
			} catch ( err ) {
				reject(new Error(`Something was wrong with readonly stylesheet ${err}`));
			}
			
		} else {
			reject(new Error(`Read only it's not activate at this moment.`));
		}
	});
}


const loadScript = (url, module=false) => new Promise((resolve, reject) => {
    let script = document.querySelector(`script[src="${url}"]`);
    if(!script){
        script = document.createElement('script');
        document.head.appendChild(script);
        script.onload = resolve;
        script.onerror = reject;
        script.src = url;
        if(module) script.type = "module";
    } else resolve(true);
});

const setWindowApp = () => {
    waitEl(TAG.AON_NOTIFICATION_ICON).then(aonNotificationIcon=>{
        aonNotificationIcon.initializeFB();
        aonNotificationIcon.getTotalNotification();
    });  
} 

const loadScripts = () => {
    let promises = [
        loadScript("https://www.google.com/jsapi"),
        loadScript("https://www.gstatic.com/charts/loader.js"),
        loadScript("aon_gwt_aio/bower_components/webcomponentsjs/webcomponents-lite.js")
    ];
    Promise.all(promises);
}

const loadScriptFirebase = async() =>{
    await loadScript("https://www.gstatic.com/firebasejs/8.2.6/firebase-app.js");
    await loadScript("https://www.gstatic.com/firebasejs/8.2.6/firebase-messaging.js");
    setWindowApp()
}

const isBeta = () => {
    const href = window.location.href;
	return href.includes('aonsolutions.org') || isLocal();
}

const isBetaDoc = () => {
  return isBeta();
}

const isLocal =  () => {
    const href = window.location.href;
    return href.includes('localhost') || href.includes('8080') ||  href.includes('ngrok.io');
}

const isReadOnly =  () => {
    const host = window.location.host;
    return host.startsWith('readonly') || host.startsWith('sololectura') ;
}

const getParam = (paramName) => {
	const queryString = window.location.search;
	const searchParams = new URLSearchParams(queryString);
	return searchParams.get(paramName);
}

 
load();

window.isReadOnly = () => isReadOnly();
