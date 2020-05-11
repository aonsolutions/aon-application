import { Router } from '@angular/router';
import { Location } from '@angular/common';

export class RootLoader {

  static removeRootPanel() {
    const rootPanel = document.getElementById("rootPanel");
    rootPanel.innerHTML = '';
  }

  static angularPanel(router: Router, location: Location, route: string) : Promise<boolean> {
    this.displayAngularPanel();
    let prom: Promise<boolean> = router.navigateByUrl(route, {skipLocationChange: true});
    location.replaceState('');
    return prom;
  }

  static rootPanel(html: string) : void {
    this.displayRootPanel();
    let rootPanel = document.getElementById("rootPanel");
    rootPanel.innerHTML = html;
  }

  static displayRootPanel() : void {
    let angularPanel = document.getElementById("angularPanel");
    angularPanel.style.display = 'none';
    let rootPanel = document.getElementById("rootPanel");
    rootPanel.style.display = 'block';
  }

  static displayAngularPanel() : void {
    const rootPanel = document.getElementById("rootPanel");
    rootPanel.innerHTML = '';
    rootPanel.style.display = 'none';
    let angularPanel = document.getElementById("angularPanel");
    angularPanel.style.display = 'block';
  }
}

export class GwtLoader {
  static drawChartsCallback(){

  }

  static getToken() {
    return localStorage.getItem("session_id");
  }

  static isTediSnapshot() {
      return false;
  }
  static isTediCenter() {
      return true;
  }

  static preStartModule(module: string) {
    var search = `/${module}.nocache.js`;
    var scripts = window.document.getElementsByTagName("script");
    for (var i = 0; i < scripts.length; ++i) {
      var script = scripts[i];
      if (script.src != null && script.src.indexOf(search) != -1) {
        var parent = script.parentNode;
        parent.removeChild(script);
        break;
      }
    }

    var iframes = window.document.getElementsByTagName("iframe");
    for (var i = 0; i < iframes.length; ++i) {
      var iframe = iframes[i];
      if (iframe.src != null && iframe.id == module) {
        var parent = iframe.parentNode;
        parent.removeChild(iframe);
        break;
      }
    }
  }

  static startModule(module: string, entrypoint: string) {
    RootLoader.displayRootPanel();
    RootLoader.removeRootPanel();
    this.preStartModule(module);
    if (window.document.createElement && window.document.getElementsByTagName) {
      var script = window.document.createElement("script");
      script.type = "text/javascript";
      script.defer = true;
      script.src = `${module}/${module}.nocache.js?entryPoint=${entrypoint}`;
      var heads = window.document.getElementsByTagName("head");
      if (heads && heads[0]) {
        heads[0].appendChild(script);
        this.triggerModuleStart(module);
      }
    }
  }

  static triggerModuleStart(module:any){
    try{
      module.onInjectionDone(module);
          if ( !window.document.createEvent) {
              var evt = window.document.createEvent("HTMLEvents");
              evt.initEvent("DOMContentLoaded", true, true);
              window.document.dispatchEvent(evt);
          }
    } catch ( e ) {
      // window.setTimeout("triggerModuleStart()", 100 );
    }
  }
}
