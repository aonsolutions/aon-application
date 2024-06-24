
import * as LS from 'aonsolutions/services/localStorageService.js';
import { AonModule } from './modules/aon-module.js';
import { setPosition } from 'aonsolutions/services/maps.js';
import { waitEl } from 'aonsolutions/services/utils.js';
import { EVENT, TAG } from 'aonsolutions/environments/environments.js';
import { saveAuthDevice } from 'aonsolutions/services/authDeviceService.js';

import 'aonsolutions/css/aon-css-utils.css';
import 'aonsolutions/css/aon-grid.css';
import 'aonsolutions/css/aon-mobile.css';
import './css/aon.css';
import 'aonsolutions/css/aon-figma.css';

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
    // TODO: Skip reload
	LS.set(LS.NEW_THEME, true);
    favicon();  
    // TODO: loadScriptFirebase();
    document.body.appendChild(new AonModule());
    loadScripts(); 
    window.loadScripts = () => loadScripts();
    loadTheme();

	console.debug("Fantastic aonSolutions loaded :-).")
}

const loadTheme = () => {
	let params = new URLSearchParams(document.location.search);
	let theme = params.get('theme');
	if (theme) {
		let url = `css/theme/${theme}.css`;
	    loadLink(url, 'stylesheet', 'text/css');
    }
}

const favicon = () => {
	let url = 'assets/favicon.ico';
    loadLink(url, 'icon', 'image/x-icon');
}

const loadLink = (url, rel, type) => new Promise((resolve, reject) => {
    const link = document.createElement('link');
    document.head.appendChild(link);
    link.onload = resolve;
    link.onerror = reject;
    link.href = url;
    link.rel = rel || "stylesheet";
    link.type = type || "text/css";
});

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

const isLocal =  () => {
    const href = window.location.href;
    return href.includes('localhost') || href.includes('8080') ||  href.includes('ngrok.io');
  }
load();

