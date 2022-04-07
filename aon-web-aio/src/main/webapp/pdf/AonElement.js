import { EVENT } from "./environments.js";

export class AonElement extends HTMLElement{

  ROOT_PANEL = 'rootPanel';
  dur;

  constructor () {
    super();
  }

  isMobile() {
    const reg = new RegExp(/mobile/i);
    return this.navigatorPlatform().match(reg) || this.navigatorUserAgent().match(reg);
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

  createAonElement(el, id, title){
    el.id = id || '';
    el.title = title || '';
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
  
	isBeta(){
    const href = window.location.href;
		return href.includes('aonsolutions.org') || href.includes('localhost') || href.includes('8080') ||  href.includes('ngrok.io');
	}

  onChange(fn) {
    this.addEventListener(EVENT.CHANGE, fn);
  }

  onClick(fn) {
    this.addEventListener(EVENT.CLICK, fn);
  }
}
