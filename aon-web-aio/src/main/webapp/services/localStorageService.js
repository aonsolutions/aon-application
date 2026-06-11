import { CONSTANT } from "../environments/environments.js";

export const COMPANY = 'company'; // TIENE QUE DESAPARECER!!
export const ROOT_PANEL = 'rootPanel';

export const AON_DOMAIN_NAME = 'aon_domain_name';
export const AON_DOMAIN_ID = 'aon_domain_id';
export const AON_DOMAIN_LOGIN = 'aon_domain_login';
export const AON_DOMAIN_DOCUMENT = 'aon_domain_document';
export const AON_SESSION_ID = 'aon_session_id';
export const AON_APPLICATION_TOP = 'aon_application_top'; // ¿?
export const AON_SOLUTIONS = 'aon_solutions';
export const AON_LANGUAGE = 'aon_language';
export const ONLY_ONE = 'onlyOne';
export const NEW_THEME = 'new_theme';
export const SUITE = 'suite';
export const FIXED_BUTTON = 'fixedButton';
export const HIDE_SIDENAV = 'hideSidenav';
export const RIGHT_PANEL = 'rightPanel';
export const TOP_MENU = 'aonMenuTopnav';
export const LEFT_MENU = 'aonMenuSidenav';
export const APP_MENU = 'aonConfigSwitchApps';
export const DARK_MENU = 'aonConfigDarkSwitch';
export const WHITE_BRAND = 'aonConfigWhiteBrandSwitch';
export const COMPANY_SELECTED = 'companySelected';
export const PORTAL_CHECKED = "portalChecked"

export const APP = 'AONAPP';
export const THEME = 'AONTHEME';
export const AON_MOBILE_THEME = '/css/theme/aon-mobile.css';
export const AON_MOBILE_ANDROID = 'css/theme/aon-mobile-android.css';
export const AON_MOBILE_ANDROID_35 = 'css/theme/aon-mobile-android-35.css';
export const AON_THEME = '/css/theme/aon.css';
export const DARK_THEME = '/css/theme/dark.css';
export const DARK_BETA_THEME = '/css/theme/darkBeta.css';
export const FUTURE_THEME = '/css/theme/future.css';
export const FUTURE_DARK_THEME = '/css/theme/future-dark.css';
export const CUSTOM_THEME = '/css/theme/customview.css';

export const IS_FUTURE_THEME = 'isFutureTheme';

export const BETA = 'beta';

export const BETADOC = 'betadoc';


export const get = (item) => {
	let value = getParam(item);
	if ( value == null ){
		value = getCookie(item);
	} 
	if ( value == null ){
		value = localStorage.getItem(item);
	} 
    if ( value == null ){ 
        value = getComputedStyle(document.body).getPropertyValue(`--${item}`);
    } 
    return value;
}

export const set = (item, value) => {
	localStorage.setItem(item, value);
}

export const remove = (item) => {
    localStorage.removeItem(item);
}

export const closeSession = () => {
    let theme = localStorage.getItem(THEME);
    let topMenu = localStorage.getItem(TOP_MENU);
    let leftMenu = localStorage.getItem(LEFT_MENU);
	let language = localStorage.getItem(AON_LANGUAGE);    
    let portalChecked = localStorage.getItem(PORTAL_CHECKED);
    let hideSideNav = localStorage.getItem(HIDE_SIDENAV);
    
	removeCookie(APP);
    localStorage.clear();
	
	if (theme)
    	localStorage.setItem(THEME,theme);
    
	if (language)
        localStorage.setItem(AON_LANGUAGE, language);
    
	if (topMenu)
        localStorage.setItem(TOP_MENU, topMenu);
    
	if (leftMenu)
        localStorage.setItem(LEFT_MENU, leftMenu);
    
	if (portalChecked)
        localStorage.setItem(PORTAL_CHECKED, portalChecked);
        
    if (hideSideNav)
        localStorage.setItem(HIDE_SIDENAV, hideSideNav);
}

export const getLanguage = () => get(AON_LANGUAGE) || 'es';

export const setLanguage = (value) => {
    set(AON_LANGUAGE, value);
    location.reload();
}

export const isTopMenu= () => {
    let aon = getTopMenu();
    return  CONSTANT.TRUE == aon;
}

export const isLeftMenu = () => {
    let aon = getLeftMenu();
    return CONSTANT.TRUE == aon;
}

export const getTopMenu = () => {
    return get(TOP_MENU);
}

export const setTopMenu = (value) => {
    set(TOP_MENU, value);
}

export const getLeftMenu = () => {
    return get(LEFT_MENU);
}

export const setLeftMenu = (value) => {
    set(LEFT_MENU,value);
} 

export const isCompanySelected = () => {
    let aon = getCompanySelected();
    return CONSTANT.TRUE == aon;
}

export const getCompanySelected = () => {
    return get(COMPANY_SELECTED);
}

export const setCompanySelected = (value) => {
    set(COMPANY_SELECTED ,value);
} 

export const isPortalChecked = () => {
    let aon = getPortalChecked();
    return CONSTANT.TRUE == aon;
}

export const getPortalChecked = () => {
    return get(PORTAL_CHECKED);
}

export const setPortalChecked = (value) => {
    set(PORTAL_CHECKED ,value);
} 

export const isAppMenu = () => {
    let aon = getAppMenu();
    return CONSTANT.TRUE == aon;
}

export const getAppMenu = () => {
    return get(APP_MENU);
}

export const setAppMenu = (value) => {
    set(APP_MENU, value);
}

export const getTheme = () => {
    return get(THEME);
}

export const setTheme = (theme) => {
	let cookie = getCookie(THEME);
	if ( cookie && theme ){
		setCookie(THEME, theme);
	} else if( theme ){
        set(THEME, theme); 
    } else{
        remove(THEME);
		removeCookie(THEME);
    }
    location.reload();
}

export const isDarkTheme = () => {
    let theme = get(THEME);
    return DARK_THEME == theme;
}

export const isDarkBetaTheme = () => {
    let theme = get(THEME);
    return DARK_BETA_THEME == theme;
}

export const setDarkTheme = (value) => {
    if (value == CONSTANT.TRUE) {
        setTheme(DARK_THEME);
    } else {
        remove(THEME);
    }
}

export const isFutureTheme = () => {
	let isFutureTheme = get(IS_FUTURE_THEME);
	if ( isFutureTheme == CONSTANT.TRUE ){ 
		return true;
	} else if ( isFutureTheme == CONSTANT.FALSE ){ 
		return false;
	} else {
		let theme = get(THEME);
		return theme && FUTURE_THEME == theme;
    }
	
}

export const isWhiteBrand = () => {
    let aon = getWhiteBrand();
    return CONSTANT.TRUE == aon;
}

export const getWhiteBrand = () => {
    return get (WHITE_BRAND);
}

export const setWhiteBrand = (value) => {
    set(WHITE_BRAND, value);
}

export const isRightPanel= () => {
    let aon = getRightPanel();
    return  aon;
}

export const getRightPanel = () => {
    return get(RIGHT_PANEL);
}

export const setRightPanel = (panel) => {
    set(RIGHT_PANEL,panel);
}

export const getHideSidenav = () => {
    return get(HIDE_SIDENAV) || 'off';
}

export const setHideSidenav = (value) => {
    set(HIDE_SIDENAV,value);
}

export const getFixedButton = () => {
    return get(FIXED_BUTTON) || 'off';
}

export const setFixedButton = (value) => {
    set(FIXED_BUTTON,value);
}

export const removeRightPanel = () => {
    remove(RIGHT_PANEL);
}

export const removeLanguage = () => {
    remove(AON_LANGUAGE);
}

export const getToken = () => get(AON_SESSION_ID);

export const setToken = (value) => {
    set(AON_SESSION_ID, value);
}

export const removeToken = () => {
    remove(AON_SESSION_ID);
}

export const getDomainName = () => get(AON_DOMAIN_NAME);


export const setDomainName = (value) => {
    set(AON_DOMAIN_NAME, value);
}

export const removeDomainName = () => {
    remove(AON_DOMAIN_NAME);
}

export const getDomainId = () =>  get(AON_DOMAIN_ID);

export const setDomainId = (value) => {
    set(AON_DOMAIN_ID, value);
}

export const removeDomainId = () => {
    remove(AON_DOMAIN_ID);
}

export const getDomainLogin = () =>  get(AON_DOMAIN_LOGIN);

export const setDomainLogin = (value) => {
    set(AON_DOMAIN_LOGIN, value);
}

export const removeDomainLogin = () => {
    remove(AON_DOMAIN_LOGIN);
}

export const getDomainDocument = () => get(AON_DOMAIN_DOCUMENT);


export const setDomainDocument = (value) => {
    set(AON_DOMAIN_DOCUMENT, value);
}

export const removeDomainDocument = () => {
    remove(AON_DOMAIN_DOCUMENT);
}

export const getApplicationTop = () =>  get(AON_APPLICATION_TOP);

export const setApplicationTop = (value) => {
    set(AON_APPLICATION_TOP, value);
}

export const removeApplicationTop = () => {
    remove(AON_APPLICATION_TOP);
}

export const getAonSolutions = () => get(AON_SOLUTIONS);

export const isAonSolutions = () => {
    const aon = get(AON_SOLUTIONS);
    return  aon && CONSTANT.FALSE !== aon;
}

export const setAonSolutions = (value) => {
    set(AON_SOLUTIONS, value);
}

export const removeAonSolutions = () => {
    remove(AON_SOLUTIONS);
}

export const getRootPanel = () =>  get(ROOT_PANEL);

export const setRootPanel = (value) => {
    set(ROOT_PANEL, value);
}

export const removeRootPanel = () => {
    remove(ROOT_PANEL);
}

export const getCompany = () =>  get(COMPANY) ? JSON.parse(get(COMPANY)) : null;

export const setCompany = (value) => {
   set(COMPANY, value);
}

export const removeCompany = () => {
    remove(COMPANY);
}

export const isOnlyOne = () => {
    const aon = get(ONLY_ONE);
    return  aon && CONSTANT.FALSE !== aon;
}

export const setOnlyOne = (onlyOne) => {
    set(ONLY_ONE, onlyOne);
}

export const isNewTheme = () => {
    const newTheme = get(NEW_THEME);
    return newTheme &&  CONSTANT.FALSE !== newTheme;
}

export const setNewTheme = (newTheme, reload) => {
    set(NEW_THEME, newTheme);
    if(reload) location.reload();
}

export const isSuite = () => {
    const suite = get(SUITE);
    return suite &&  CONSTANT.TRUE === suite;
}

export const setSuite = (suite) => {
    set(SUITE, suite);
    location.reload();
}

export const isBeta = () => {
    const beta = get(BETA);
    return CONSTANT.TRUE == beta;
}

export const isBetaDoc = () => {
    const betadoc = get(BETADOC);
    return CONSTANT.TRUE == betadoc;
}
export const setBetaDoc = (betadoc) => {
  set(BETADOC, betadoc);
}

export const removeDomain = () => {
    removeDomainId();
    removeDomainName();
    removeDomainLogin();
    removeDomainDocument();
}

const getParam = (paramName) => {
	const queryString = window.location.search;
	const searchParams = new URLSearchParams(queryString);
	return searchParams.get(paramName);
}

const getCookie = (cookieName) => {
	const cookieValue = decodeURIComponent(document.cookie)
    .split(';')
	.map((row) => row.trimStart() )
    .find((row) => row.toUpperCase().startsWith(`${cookieName.toUpperCase()}=`))
    ?.split('=')[1];
	
	return cookieValue;  
} 


const removeCookie = (cookieName) => {
	decodeURIComponent(document.cookie)
	.split(';')
	.map((row) => row.trimStart() )
	.filter((row) => row.toUpperCase().startsWith(`${cookieName.toUpperCase()}=`))
	?.map((row) => row.split('=')[0])
	.forEach((cookie) => {
		document.cookie = cookie + '=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
	});

 }

const setCookie = (cookieName, cookieValue, expiresDate) => {
  removeCookie(cookieName);
  expiresDate = expiresDate || new Date(new Date().setUTCHours(23,59,59));	
  let expires = "expires="+ new Date(expiresDate).toUTCString();
  document.cookie = cookieName + "=" + cookieValue + ";" + expires + ";path=/";
}


