import { AonElement } from "../../../components/AonElement.js";
import { CreateComponent } from "../../../components/CreateComponent.js";
import { CONSTANT } from "../../../environments/environments.js";
import { NewsEnums } from "../NewsEnums.js";
import { NewsAddUtils } from "./NewsAddUtils.js";
import { saveNews, getNewsType } from "../../../services/newsService.js";
import { ToolbarType } from "../../../models/enums.js";
import { getScopes } from "../../../services/documentalService.js";
import { CategoryService } from "../../../services/categoryService.js";
import { News } from "../../../models/news/News.js"
import * as ACTIONS from "../../actions.js";


export class AonNewsAdd extends AonElement {
  ACTION;
  TITLE;
  TOOLBAR;
  START_DATE;
  news;
  CATEGORYS;
  data;
  static get observedAttributes() {
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
    this.id = NewsEnums.VIEWS_NEWS.AON_NEWS_ADD;
    this.TOOLBAR = this.id + "Toolbar";
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
    this.news = new News(this.data);
    this.CATEGORYS = [];
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
    
    // toolbar.addButton2(ACTIONS.BACK, () => {});
 }

  paintView() {
    const {cardOne, cardTwo} = NewsAddUtils.createForm(this.id, this);

    NewsAddUtils.buildFormGeneral(cardOne.getContent(), this.news);

    NewsAddUtils.buildEditor(cardTwo.getContent(), this.news);
  }

  async initGets() {
    await Promise.all([
        this.getNewsType(),
        this.getScopes(),
        this.getCategorys()
    ]).catch(e=> console.log(e));
  }

  async getScopes(){
    const scopeEl = document.getElementById("scope");
    if(scopeEl){
      const scopes = await getScopes();

      const options = scopes.map(scope => ({...scope, value:scope.id}));

      scopeEl.setOptions(options);
    }
  }


  async getNewsType(){
    const typeEl = document.getElementById("type");
    if(typeEl){
      const options = await getNewsType();

      typeEl.setOptions(options);
    }
  }

  async getCategorys(){
    const categoryEl  = document.getElementById("category");
    if(categoryEl){
      const categorys = await CategoryService.getCategorys({type:"ARTICLE"});
    
      const options   = categorys.map(category => ({...category, value:category.id}));
  
      categoryEl.setOptions(options);
    }
  }

  async save(){
    this.applicationEl.startLoading();

    try {
      const {id} = await saveNews(this.news);
      if(id){
        this.news.setId(id);
      }
      this.showMessage();
    } catch (error) {
      console.log(error);
      this.showError(error);
    }

    this.applicationEl.stopLoading();  
  }
}

window.customElements.define("aon-news-add", AonNewsAdd);
