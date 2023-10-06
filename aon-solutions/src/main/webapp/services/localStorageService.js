import { CONSTANT } from "../environments/environments.js";

export const COMPANY = 'company'; // TIENE QUE DESAPARECER!!
export const ROOT_PANEL = 'rootPanel';

export const AON_DOMAIN_NAME = 'aon_domain_name';
export const AON_DOMAIN_ID = 'aon_domain_id';
export const AON_DOMAIN_LOGIN = 'aon_domain_login';
export const AON_SESSION_ID = 'aon_session_id';
export const AON_APPLICATION_TOP = 'aon_application_top'; // ¿?
export const AON_SOLUTIONS = 'aon_solutions';
export const AON_LANGUAGE = 'aon_language';
export const ONLY_ONE = 'onlyOne';
export const NEW_THEME = 'new_theme';

export const get = (item) => localStorage.getItem(item);

export const set = (item, value) => {
    localStorage.setItem(item, value);
}

export const remove = (item) => {
    localStorage.removeItem(item);
}

export const getLanguage = () => get(AON_LANGUAGE);

export const setLanguage = (value) => {
    set(AON_LANGUAGE, value);
    location.reload();
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

export const setNewTheme = (newTheme) => {
    set(NEW_THEME, newTheme);
}

export const removeDomain = () => {
    removeDomainId();
    removeDomainName();
    removeDomainLogin();
}