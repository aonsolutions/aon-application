import {webkitRequestMobile} from '../services/service.js';

import * as AON_TAG from "../../environments/aonTag.js";

export class AonElement extends HTMLElement{
  ROOT_PANEL;

  constructor () {
    super();
    this.ROOT_PANEL = 'rootPanel';
  }

  isMobile() {
    return this.iOS() || this.android() || this.blackBerry() || this.windowsPhone()  || this.isAppMobile() || (window.innerWidth <= 850 && window.innerHeight <= 912);
  }

  iOS() {
    return navigator.platform.toLowerCase().includes('ipad')
      || navigator.userAgent.toLowerCase().includes('ipad')
      || navigator.platform.toLowerCase().includes('iphone')
      || navigator.userAgent.toLowerCase().includes('iphone')
      || navigator.platform.toLowerCase().includes('ipod')
      || navigator.userAgent.toLowerCase().includes('ipod');
  }

  isAppMobile(){
    return webkitRequestMobile();
  }

  android() {
    return navigator.platform.toLowerCase().includes('android')
      || navigator.userAgent.toLowerCase().includes('android');
  }

  blackBerry() {
    return navigator.platform.toLowerCase().includes('blackberry')
      || navigator.userAgent.toLowerCase().includes('blackberry');
  }

  windowsPhone() {
    return navigator.platform.toLowerCase().includes('windows phone')
      || navigator.userAgent.toLowerCase().includes('windows phone');
  }

  getElement(id) {
    return document.getElementById(id);
  }

  createElement(tag){
    return document.createElement(tag);
  }

  clear() {
    this.clearElement(this);
  }

  clearElement(elem) {
    if(elem) elem.innerHTML = '';
  }

  clearElementById(id) {
    const elem = document.getElementById(id);
    this.clearElement(elem);
  }

  hideElement(id) {
    const elem = document.getElementById(id);
    if(elem) elem.style.display = 'none';
  }

  showElement(id) {
    const elem = document.getElementById(id);
    if(elem) elem.style.display = 'block';
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
    return document.querySelector(AON_TAG.AON_APPLICATION);
  }
  
  getParent(){
    return document.querySelector(AON_TAG.AON_APPLICATION).getParent();
  }
}
