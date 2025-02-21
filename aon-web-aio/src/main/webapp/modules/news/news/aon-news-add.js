import { AonElement } from "../../../components/AonElement.js";
import { CreateComponent } from "../../../components/CreateComponent.js";
import { CONSTANT, MSG } from "../../../environments/environments.js";
import { NewsEnums } from "../NewsEnums.js";
import { NewsAddUtils } from "./NewsAddUtils.js";
import { AonNewsList } from "./aon-news-list.js";
import { saveNews, deleteNews } from "../../../services/newsService.js";
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

    if(this.news.getId()){
      toolbar.addButton2(ACTIONS.DELETE, () => this.delete());
    }
    
    toolbar.addButton2(ACTIONS.SAVE, () => {
      this.save();
    });

    toolbar.addButton2(ACTIONS.BACK, () => this.goBack());
 }

  paintView() {
    const {cardOne, cardTwo} = NewsAddUtils.createForm(this.id, this);

    NewsAddUtils.buildFormGeneral(cardOne.getContent(), this.news);

    NewsAddUtils.buildEditor(cardTwo.getContent(), this.news, this);
  }

  async initGets() {
    await Promise.all([
        // this.getNewsType(),
        this.getScopes(),
        this.getCategorys()
    ]).catch(e=> console.log(e));
  }

  async getScopes(){
    const scopeEl = document.getElementById("scope");
    if(scopeEl){
      const scopeValue = this.news.getScope();

      const scopes = await getScopes();

      const options = scopes.map(scope => ({...scope, value:scope.id}));

      scopeEl.setOptions(options);

      if(scopeValue && scopeValue.id){
        scopeEl.value = scopeValue.id;
      }
    }
  }

  // async getNewsType(){
  //   const typeEl = document.getElementById("type");
  //   if(typeEl){
  //     const options = await getNewsType();

  //     typeEl.setOptions(options);
  //   }
  // }

  async getCategorys(){
    const categoryEl  = document.getElementById("category");
    if(categoryEl){
      const categoryValue = this.news.getCategory();

      const categorys = await CategoryService.getCategorys({type:"ARTICLE"});
    
      const options   = categorys.map(category => ({...category, value:category.id}));
  
      categoryEl.setOptions(options);

      if(categoryValue && categoryValue.id){
        categoryEl.value = categoryValue.id;
      }
    }
  }

  async save(messageSuccess=true){
    this.applicationEl.startLoading();

    try {
      const isUpdate = this.news.getId();
      const news = await saveNews(this.news);

      if(news && news.id){
        if(messageSuccess)
          this.showMessage();

        if(!isUpdate){
          const aonNewsAdd = new AonNewsAdd();
          aonNewsAdd.data = news;
          this.applicationEl.setContent(aonNewsAdd);
        } else {
          this.news.setAttach({})
          this.news.setRattach(news.rattach);  
        }
      }
    } catch (error) {
      console.log(error);
      this.showError(error);
    }

    this.applicationEl.stopLoading();  
  }

  async delete(){
    this.applicationEl.confirmDialog(MSG.DELETE, MSG.DELETE_CONFIRM, async()=>{
      try {
        await deleteNews(this.news);
        this.showToast({message:MSG.DELETED_DATA});
        this.goBack()
      } catch (error) {
        this.showError(error);
      }
    });
  }

  goBack(){
    this.applicationEl.setContent(new AonNewsList())
  }
}

window.customElements.define("aon-news-add", AonNewsAdd);
