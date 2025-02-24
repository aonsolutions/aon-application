import { CONSTANT, TAG } from "../environments/environments.js";
import { waitEl } from "../services/utils.js";
import { AonElement } from "./AonElement.js";

export class AonIframe extends AonElement {
  DOC;
  WD;
  IFRAME;
  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {
    this.id  = this.id || "aonIframe";
    this.DOC = this.DOC || null;
    this.WD  = this.WD || null;
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
    this.IFRAME = this.createElement(TAG.IFRAME);
    this.IFRAME.id = this.id+"Iframe";
    this.IFRAME.frameBorder = "0";
    this.IFRAME.scrolling = "no";
    this.IFRAME.style = `
      position: relative; 
      height: 100%; 
      width: 100%;
    `;
 
    this.appendChild(this.IFRAME);
  }

  async load(){
    try{
      await Promise.all([
        this.waitForValue(this.IFRAME.contentDocument),
        this.waitForValue(this.IFRAME.contentWindow)
      ]);

      this.setDocument(this.IFRAME.contentDocument);
      this.setWindow(this.IFRAME.contentWindow);

      await this.loadCss();

      this.setFontFamily('Roboto, Helvetica, Arial, sans-serif !important');

    } catch (err) {
      console.log(err);
    }
  }

  async loadCss(){
    // /css/aon-gwt.css
    let promises = [
      this.loadLink("https://fonts.googleapis.com/css2?family=Roboto:ital,wght@0,100;0,300;0,400;0,500;0,700;0,900;1,100;1,300;1,400;1,500;1,700;1,900&family=Rubik:ital,wght@0,300;0,400;0,500;0,700;0,900;1,300;1,400;1,500;1,700;1,900&display=swap"),
      this.loadLink("https://fonts.googleapis.com/icon?family=Material+Icons|Material+Icons+Outlined"),
      this.loadLink("../dist/app.min.css"),
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
    this.getBody().innerHTML = "";
  }

  setContent(element){
    this.clearContent();
    this.getBody().appendChild(element);
  }

  addContent(element){
    this.getBody().appendChild(element);
  }

  setFontFamily(fontFamily){
    this.getBody().style = `
      font-family:${fontFamily};
    `;
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

