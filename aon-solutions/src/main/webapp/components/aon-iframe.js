import { CONSTANT, TAG } from "../environments/environments.js";
import { waitEl } from "../services/utils.js";
import { AonElement } from "./AonElement.js";

export class AonIframe extends AonElement {
  DOC;
  WD;

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }


  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {
    this.id = this.id || "aonIframe";
  }

  setDocument(doc){
    this.DOC = doc;
  }

  setWindow(wd){
    this.WD = wd;
  }

  getDocument(){
    return this.DOC;
  }

  getWindow(){
    return this.WD;
  }

  getBody(){
    return this.getDocument().body;
  }

  getGoogle(){
    return this.getWindow().google;
  }
  
  build() {
    let iframe = this.createElement(TAG.IFRAME);
    iframe.id = this.id+"Iframe";
    iframe.frameBorder = "0";
    iframe.scrolling = "no";
    iframe.style = `
      position: relative; 
      height: 100%; 
      width: 100%;
    `;

    iframe.onload = () => {
      this.setDocument(iframe.contentDocument);
      this.setWindow(iframe.contentWindow);
    }; //onload
    this.appendChild(iframe);
  }

  async load(){

    let promises = [
      this.waitForValue(this.getDocument()),
      this.waitForValue(this.getWindow()),
      this.loadCss()
    ];
    await Promise.all(promises);
  }

  async loadCss(){
    // /css/aon-gwt.css
    let promises = [
      this.loadLink("../dist/app.min.css")
    ];

    await Promise.all(promises);
  }

  async loadChart(){
    await Promise.all([
      this.loadScript("https://www.google.com/jsapi"),
      this.loadScript("https://www.gstatic.com/charts/loader.js")
    ]);
    await waitEl("script[src*='jsapi']", this.DOC);
    await waitEl("script[src*='loader.js']", this.DOC);
  }

  clearContent(){
    this.getDocument().body.innerHTML = "";
  }

  setContent(element){
    this.clearContent();
    this.getDocument().body.appendChild(element);
  }

  addContent(element){
    this.getDocument().body.appendChild(element);
  }

  loadScript(url, module=false) {
    return new Promise((resolve, reject) => {
      let script = this.DOC.querySelector(`script[src="${url}"]`);
      if(!script){
          script = this.DOC.createElement('script');
          this.DOC.head.appendChild(script);
          script.onload = resolve;
          script.onerror = reject;
          script.src = url;
          if(module) {
            script.type = "module";
          }
      } else resolve(true);
    });
  }

  loadLink(url, rel, type) {
    return new Promise((resolve, reject) => {
      let link = this.DOC.querySelector(`link[href="${url}"]`);
      if(!link){
        link = this.DOC.createElement('link');
        this.DOC.head.appendChild(link);
        link.onload = resolve;
        link.onerror = reject;
        link.href = url;
        link.rel = rel || "stylesheet";
        link.type = type || "text/css";
      }
    });
  }

  waitForValue(value){
    return new Promise((resolve,reject)=>{
      let i = 0;
      let interval = setInterval(()=> {
        i++;
        if (value) {
          clearInterval(interval);
          resolve(value);
        } else if(i >= 100){ // 10 seg
          clearInterval(interval);
          reject("Value is empty");
        }
      }, 100); // check every 100ms
    });
  }
}
if (!window.customElements.get("aon-iframe")) 
  window.customElements.define("aon-iframe", AonIframe);

