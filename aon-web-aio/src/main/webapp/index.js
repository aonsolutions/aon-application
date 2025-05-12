import * as LS from './services/localStorageService.js';
import * as UA from './services/userAgentService.js';
import { AonModule } from './modules/aon-module.js';
import { setPosition } from './services/maps.js';
import { waitEl } from './services/utils.js';
import { EVENT, TAG } from './environments/environments.js';
import { saveAuthDevice } from './services/authDeviceService.js';
import {favicon, title, loadLink } from './css/aon-customView.js';

import './css/aon-css-utils.css';
import './css/aon-grid.css';
import './css/aon-mobile.css';
import './css/aon-figma.css';
import { getThemeUrl } from './services/companyService.js';

window.setPosition = (pos) => setPosition(pos);
window.setTokenFCM = (token) => {
    window.tokenFCM = token;
    saveAuthDevice({tokenFCM:token});
};

window.setNotificationAction = (data) =>  {
    window.dispatchEvent( new CustomEvent(EVENT.RECEIVED_NOTIFICATION, {detail:data}));
};

window.setResumeApp = (data) =>  {
    window.dispatchEvent( new CustomEvent(EVENT.RESUME_APP, {detail:data}));
};

const load = () => {
	// Estamos cargando el estilo nuevo
    const isNew = window.location.pathname.includes('/new');
    localStorage.setItem('sass', isNew ? 'true' : 'false');
    
	console.debug("Start loading aonSolutions.");
	console.debug("Keep your fingers crossed!" );
	console.debug("We need all the luck we can get.");
	
    LS.setAonSolutions(true);
    // TODO: Skip reload
	LS.set(LS.NEW_THEME, true);
	
	loadScripts();
	loadTheme().then(
      () => {
          favicon();
          title();
          document.body.appendChild(new AonModule());
      },
      (err) => {
        console.log(document.body);
        console.log(new AonModule());
        console.log(err);
        document.body.appendChild(new AonModule());
      }
	);

	// TODO: loadScriptFirebase();
    window.loadScripts = () => loadScripts();

	console.debug("Fantastic aonSolutions loaded :-).");
};

export const loadTheme = async  () => {
    // let themeUrl = UA.isMobile() ? LS.AON_MOBILE_THEME
	// 	: getParam("theme") || LS.getTheme() || getCookie("theme") || LS.AON_THEME; 
	
	let params = {
		domain: document.domain, 
		paramTheme: getParam("theme") || '', 
		lsTheme: LS.getTheme() || '', 
		cookieTheme: getCookie("theme") || '', 
		lsAonTheme: LS.AON_THEME || ''
	};
	
	let themeUrl = UA.isMobile() 
        ? LS.AON_MOBILE_ANDROID 
        : await getThemeUrl(params);
	console.log(themeUrl);

    /* location.origin + '/customview?domain=' + document.domain || getParam("theme") || LS.getTheme() || getCookie("theme") || LS.AON_THEME;*/

	return new Promise((resolve, reject) => {
		try {
			const aonThemeSpan = document.createElement(TAG.SPAN);
			aonThemeSpan.className = 'aonTheme';
			aonThemeSpan.style.display = 'none';
			document.body.appendChild(aonThemeSpan);
			
			loadLink(themeUrl, 'stylesheet', 'text/css');
			let tries = 0;
			let interval = setInterval(() => {
				const aonThemeStyle = getComputedStyle(aonThemeSpan);
				const aonThemeProperty = aonThemeStyle.getPropertyValue('--aon-theme');
				if ( ( tries++ > 5 ) || aonThemeProperty ) {
					resolve();
					aonThemeSpan.remove();
					clearInterval(interval);
				}
			}, 200);
			
		} catch ( err ) {
			reject(new Error(`Something was wrong with theme '${themeUrl}'`));
		}
	});
};

/*const favicon = () => {
	let favicon = getComputedStyle(document.body).getPropertyValue('--favicon');
	if ( favicon ) {
		loadLink('', 'icon', 'image/x-icon')
		.then( faviconLink  => {
			faviconLink.href = favicon;
		});
	}
}

const loadLink = (url, rel, type) => new Promise((resolve, reject) => {
    const link = document.createElement('link');
    document.head.appendChild(link);
    link.onload = resolve(link);
    link.onerror = reject;
    link.href = url;
    link.rel = rel || "stylesheet";
    link.type = type || "text/css";
});*/

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
};

const loadScriptFirebase = async() =>{
    await loadScript("https://www.gstatic.com/firebasejs/8.2.6/firebase-app.js");
    await loadScript("https://www.gstatic.com/firebasejs/8.2.6/firebase-messaging.js");
    setWindowApp();
};

const isBeta = () => {
    const href = window.location.href;
	return href.includes('aonsolutions.org') || isLocal();
};

const isBetaDoc = () => {
  return isBeta();
};

const isLocal =  () => {
    const href = window.location.href;
    return href.includes('localhost') || href.includes('8080') ||  href.includes('ngrok.io');
};

const getParam = (paramName) => {
	const queryString = window.location.search;
	const searchParams = new URLSearchParams(queryString);
	return searchParams.get(paramName);
};

const getCookie = (cookieName) => {
	const cookieValue = decodeURIComponent(document.cookie)
    .split(';')
	.map((row) => row.trimStart() )
    .find((row) => row.startsWith(`${cookieName}=`))
    ?.split('=')[1];

	return cookieValue;
};

// Cargado el DOM iniciamos la aplicacion
document.addEventListener('DOMContentLoaded', function () {
  load();
});
