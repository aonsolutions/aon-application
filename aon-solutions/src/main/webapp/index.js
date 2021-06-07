import * as LS from './services/localStorageService.js';
import { AonModule } from './modules/aon-module.js';
import { setPosition } from './services/maps.js';
import { waitEl } from './services/utils.js';
import { EVENT, TAG } from './environments/environments.js';
import { saveAuthDevice } from './services/authDeviceService.js';
import './css/aon-css-utils.css';
import './css/aon-grid.css';
import './css/aon-input.css';
import './css/aon-input-loading.css';
import './css/aon-loader.css';
import './css/aon-mobile.css';
import './css/aon-slider.css';
import './css/aon-switch.css';
import './css/aon-tabs.css';
import './css/aon-textarea.css';
import './css/aon.css';


const load = () => {
    window.setPosition = (pos) => setPosition(pos);
    LS.setAonSolutions(true);
    favicon();  
    loadScriptFirebase();
    document.body.appendChild(new AonModule());
    loadScripts(); 
    window.loadScripts = () => loadScripts();
}

const favicon = () => {
    let url = 'assets/favicon-black.ico';
    if (window.location.href.includes('ayudat')) {
		url = 'assets/ayudat-favicon.png';
	} else if (window.location.href.includes('translogia') || window.location.href.includes('tedi')
    		|| window.location.href.includes('aonsolutions.org')) {
		url = 'assets/ayudat-favicon.png';
	} 
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
    window.setTokenFCM =  (token) => {
        window.tokenFCM = token;
        saveAuthDevice({tokenFCM:token});
    }

    window.setNotificationAction = (data) =>  {
        window.dispatchEvent( new CustomEvent(EVENT.RECEIVED_NOTIFICATION, {detail:data}));
    }
    
    waitEl(TAG.AON_NOTIFICATION_ICON).then(aonNotificationIcon=>{
        aonNotificationIcon.initializeFB();
        aonNotificationIcon.getTotalNotification();
    });  
} 

const loadScripts = () => {
    if(LS.getToken()) {
        Promise.all([
            loadScript("//mozilla.github.io/pdf.js/build/pdf.js"),
            loadScript("https://www.google.com/jsapi"),
            loadScript("aon_gwt_aio/bower_components/webcomponentsjs/webcomponents-lite.js"),
            loadScript("https://www.gstatic.com/charts/loader.js")
        ])
        .then(()=>setWindowApp());    
    }
}

const loadScriptFirebase = () => Promise.all([
    loadScript("https://www.gstatic.com/firebasejs/8.2.6/firebase-app.js"),
    loadScript("https://www.gstatic.com/firebasejs/8.2.6/firebase-messaging.js")
]);  

load();


