export class AonElement extends HTMLElement{
  ROOT_PANEL;

  constructor () {
    super();
    this.ROOT_PANEL = 'rootPanel';
  }

  isMobile() {
    return window.innerWidth <= 850 && window.innerHeight <= 850;
  }

  getElement(id) {
    return document.getElementById(id);
  }

  createElement(tag){
    return document.createElement(tag);
  }

  clearElement(id) {
    document.getElementById(id).innerHTML = '';
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
