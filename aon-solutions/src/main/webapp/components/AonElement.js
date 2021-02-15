export class AonElement extends HTMLElement{
  ROOT_PANEL;

  constructor () {
    super();
    this.ROOT_PANEL = 'rootPanel';
  }

  isMobile() {
    return this.iOS() || this.android() || this.blackBerry() || this.windowsPhone() || (window.innerWidth <= 850 && window.innerHeight <= 850);
  }

  iOS() {
    return navigator.platform.toLowerCase().includes('ipad')
      || navigator.platform.toLowerCase().includes('iphone')
      || navigator.platform.toLowerCase().includes('ipod');
  }

  android() {
    return navigator.platform.toLowerCase().includes('android');
  }

  blackBerry() {
    return navigator.platform.toLowerCase().includes('blackberry');
  }

  windowsPhone() {
    return navigator.platform.toLowerCase().includes('windows phone');
  }

  getElement(id) {
    return document.getElementById(id);
  }

  createElement(tag){
    return document.createElement(tag);
  }

  clearElement(id) {
    const elem = document.getElementById(id);
    if(elem) elem.innerHTML = '';
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
    this.clearElement(this.ROOT_PANEL);
    this.getElement(this.ROOT_PANEL).appendChild(element);
  }

  rootPanelHtml(html) {
    this.clearElement(this.ROOT_PANEL);
    this.getElement(this.ROOT_PANEL).innerHTML = html;
  }
}
