import {webkitRequestMobile} from '../services/service.js';
import { CONSTANT, EVENT, MSG, TAG } from "../environments/environments.js";
import { DomainUserRoles } from '../models/DomainUserRoles.js';
import { getDomainUserRoles } from '../services/companyService.js';

export class AonElement extends HTMLElement{

  ROOT_PANEL = 'rootPanel';
  dur;

  constructor () {
    super();
  }

  isMobile() {
    const reg = new RegExp(/mobile/i);
    return this.navigatorPlatform().match(reg) || this.navigatorUserAgent().match(reg) || this.isAppMobile();
  }

  iOS() {
    const reg = new RegExp(/iphone|ipad|ipod/i);
    return this.navigatorPlatform().match(reg) || this.navigatorUserAgent().match(reg);
  }

  android() {
    const reg =  new RegExp(/android/i);
    return this.navigatorPlatform().match(reg) || this.navigatorUserAgent().match(reg);
  }

  blackBerry() {
    const reg =  new RegExp(/blackberry/i);
    return this.navigatorPlatform().match(reg) || this.navigatorUserAgent().match(reg);
  }

  windowsPhone() {
    const reg =  new RegExp(/windows phone/i);
    return this.navigatorPlatform().match(reg) || this.navigatorUserAgent().match(reg);
  }

  isAppMobile(){
    return webkitRequestMobile();
  }

  navigatorPlatform(){
    return navigator.platform.toLowerCase();
  }

  navigatorUserAgent(){
    return navigator.userAgent.toLowerCase();
  }

  getElement(id) {
    return document.getElementById(id);
  }

  createElement(tag, id, className){
    let el = document.createElement(tag);
    if(id) el.id = id;
    if(className) el.className = className;
    return el;
  }

  createAonElement(el, id, title, main){
    el.id = id || '';
    el.title = title || '';
    el.description = title || '';
    if(main)
      el.main = true;
    return el;
  }

  clear() {
    this.clearElement(this);
  }

  clearElement(el) {
    if(el) el.innerHTML = '';
  }

  clearElementById(id) {
    const elem = this.getElement(id);
    this.clearElement(elem);
  }

  hideElement(id) {
    const elem = this.getElement(id);
    if(elem) elem.style.display = 'none';
  }

  showElement(id) {
    const elem = this.getElement(id);
    if(elem) elem.style.display = 'block';
  }

  isSab() {
    const sab = getComputedStyle(document.documentElement).getPropertyValue("--sab");
    return sab.split(" ").join("") !== '0px';
  }
  
  getRootPanel() {
    return this.getElement(this.ROOT_PANEL);
  }


  // rootPanel(element) {
  //   let rp = this.getRootPanel();
  //   if(rp) {
  //     this.clearElement(rp);
  //     rp.appendChild(element);
  //   }
  // }

  rootPanelHtml(html) {
    let rp = this.getRootPanel();
    if(rp) {
      this.clearElement(rp);
      rp.innerHTML = html;
    }
  }

  rootPanel(element) {
    this.clearElementById(this.ROOT_PANEL);
    this.getElement(this.ROOT_PANEL).appendChild(element);
  }

  rootPanelHtml(html) {
    this.clearElementById(this.ROOT_PANEL);
    this.getElement(this.ROOT_PANEL).innerHTML = html;
  }

  getApplication() {
    return document.querySelector(TAG.AON_APPLICATION);
  }

  isClasic(){
    return !this.getModule();
  }

  getModule() {
    return document.querySelector(TAG.AON_MODULE);
  }

  createApplication(id, title, application, main) {
    const app = this.createAonElement(application, id, title, main);
    this.appendChild(app);
    return app;
  }

  getApplicationParent(){
    return this.getApplication() ? this.getApplication().getParent() : null;
  }

  buildDur() {
    return new Promise((resolve, reject) => {
      getDomainUserRoles({}).then(r => {
        this.dur = new DomainUserRoles(r);
        resolve(this.dur);
      }).catch(e => reject(e));
    });
  }

  getDur() {
    return this.dur;
  }

  isLocal(){
    const href = window.location.href;
    return href.includes('localhost') || href.includes('8080') ||  href.includes('ngrok.io');
  }

	isBeta(){
    const href = window.location.href;
		return href.includes('aonsolutions.org') || this.isLocal();
	}

  isSig(){
    const href = window.location.href;
		return href.includes('sig.aonsolutions.org');
	}

  showMessage(msg) {
    this.showToast({
      type: CONSTANT.SUCCESS,
      message: msg || MSG.SAVED_DATA
    });
  }

  showMessageError(msg) {
    this.showToast({
      type: CONSTANT.ERROR,
      message: msg
    });
  }

  showError(e) {
    this.showToast(e);
  }

  /**
   *
   * @param {string or obj, obj = {message, type}} error error del catch
   * @returns obj{message, type}
   */
  showToast(obj) {
    try {
      if(typeof obj === "string")  obj = JSON.parse(obj);
      const toast = this.getElement(this.getApplication().TOAST);
      if(toast){
        toast.start(obj);
      }
    } catch (error) {}
  }

  onChange(fn) {
    this.addEventListener(EVENT.CHANGE, fn);
  }

  onClick(fn) {
    this.addEventListener(EVENT.CLICK, fn);
  }

  isString(obj) {
		return Object.prototype.toString.call(obj) === '[object String]';
	}

}
