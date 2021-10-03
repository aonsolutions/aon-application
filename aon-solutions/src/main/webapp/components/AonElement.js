import {webkitRequestMobile} from '../services/service.js';
import { CONSTANT, MSG, TAG } from "../environments/environments.js";

export class AonElement extends HTMLElement{
  ROOT_PANEL = 'rootPanel';
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

  createElement(tag){
    return document.createElement(tag);
  }

  createAonElement(elem, id, title){
    elem.id = id || '';
    elem.title = title || '';
    return elem;
  }

  clear() {
    this.clearElement(this);
  }

  clearElement(elem) {
    if(elem) elem.innerHTML = '';
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

  getModule() {
    return document.querySelector(TAG.AON_MODULE);
  }

  createApplication(id, title, application) {
    this.appendChild(this.createAonElement(application, id, title));
  }

  getApplicationParent(){
    return this.getApplication().getParent();
  }

	isBeta(){
    const href = window.location.href;
		return href.includes('aonsolutions.org') || href.includes('localhost') || href.includes('8080');
	}

  showMessage(msg) {
    msg = {
      type: CONSTANT.SUCCESS,
      message: msg || MSG.SAVED_DATA
    }
    this.showToast(msg);
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
}
