import { AonElement } from "../../../components/AonElement.js";
import { CreateComponent } from "../../../components/CreateComponent.js";
import { CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from "../../../environments/environments.js";
import { RssEnums } from "../RssEnums.js";
import { RssAddUtils } from "./RssAddUtils.js";
import { getRss, saveNews } from "../../../services/newsService.js";
import { ToolbarType } from "../../../models/enums.js";
import { serializeForm } from "../../../services/utils.js";
import { getScopes } from "../../../services/documentalService.js";
import { CategoryService } from "../../../services/categoryService.js";
import { News } from "../../../models/news/News.js"
import * as ACTIONS from "../../actions.js";


export class AonRssAdd extends AonElement {
  ACTION;
  TITLE;
  TOOLBAR;
  START_DATE;
  nvedAttributes() {
    return [CONSTANT.DATA];
  }

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  get data() {
    return JSON.parse(this.getAttribute(CONSTANT.DATA));
  }

  set data(value) {
    if (value) this.setAttribute(CONSTANT.DATA, JSON.stringify(value));
  }

  attributeChangedCallback(name, oldValue, newValue) {}

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
    
    if (this.getApplication()) {
      this.getApplication().removeFloatOption();
    }
  }

  disconnectedCallback() {}

  initialize(){
    this.id = this.id || RssEnums.RSS_VIEWS.AON_RSS_ADD;
    this.TOOLBAR = this.id + "Toolbar";
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
    this.news = new News();
  }


  build() {
    this.buildToolbar();
    this.paintView();
    this.initGets();
  }

  buildToolbar() {
    const toolbar = CreateComponent.createAonToolbar({ id: this.TOOLBAR, type: ToolbarType.SECONDARY}, this);
    toolbar.removeButtons();

    toolbar.addButton2(ACTIONS.SAVE, () => {
      this.save();
    });
    
    toolbar.addButton2(ACTIONS.BACK, () => {});
 }

  paintView() {
    const {cardOne, cardTwo} = RssAddUtils.createForm(this.id, this);

    RssAddUtils.createRssForm(cardOne.getContent(), this);

    RssAddUtils.createEditor(cardTwo.getContent(), this);
  }


  async initGets() {
    await Promise.all([
        this.getScopes(),
        this.getCategorys()
    ]).catch(e=> console.log(e));
  }

  async getScopes(){
    const scopes = await getScopes();
    if(scopes.length){
      const options = scopes.map(scope => ({...scope, value:scope.id}));
      let scopeEl = document.getElementById("scope");
      scopeEl.setOptions(options);
    }
  }

  async getCategorys(){
    const categorys = await CategoryService.getCategorys();
    if(categorys.length){
      const options = categorys.map(category => ({...category, value:category.id}));
      let categoryEl = document.getElementById("category");
      categoryEl.setOptions(options);
    }
  }

  getForm(){
    let form = document.getElementById(this.id+"Form");
    if(form){
      const formSerialize = serializeForm(form);
      let content = form.querySelector("#aonTextAreaEditor").value;
      return { ...formSerialize, content };
    }
    return null;
  }

  async openRss(){
    try {
      let rss = await getRss();
      RssAddUtils.openRss(rss);
    } catch (error) {
      console.log(error);
      this.showError(error);
    }
  }

  async save(){
    let form = this.getForm();
    if(form){
      const resp = await saveNews(form);
      console.log(resp);
    }
  }
}

window.customElements.define("aon-rss-add", AonRssAdd);
