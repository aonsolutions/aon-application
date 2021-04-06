import {webkitRequestMobile} from '../services/service.js';

import * as AON_TAG from "../../environments/aonTag.js";

export class AonElement extends HTMLElement{
  ROOT_PANEL;

  constructor () {
    super();
    this.ROOT_PANEL = 'rootPanel';
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
  
  getApplicationParent(){
    return this.getApplication().getParent();
  }

}
