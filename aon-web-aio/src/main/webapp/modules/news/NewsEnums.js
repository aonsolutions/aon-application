import { MATERIAL_ICONS } from "../../environments/environments.js";


const VIEWS_NEWS = {
  AON_NEWS_ADD: 'aonNewsAdd',
  AON_NEWS_PANEL: 'aonNewsPanel',
  AON_NEWS_LIST: 'aonNewsList',
};

const ACTION_NEWS = {
  PUBLISH_RSS:{
    name: 'Publicar RSS',
    icon: "rss_feed",
    id: 'publishRssIcon',
  },
  BACK: {
    id: 'Previous',
    name: 'Volver',
    icon: MATERIAL_ICONS.ARROW_BACK
  }
}

const NewsSidenav = {
  ADD: {
    name: "Agregar",
    icon: MATERIAL_ICONS.ADD,
    id: MATERIAL_ICONS.ADD,
  },
  FILTER:{
    name: "Filter",
    icon: "tune",
    id: "filter",
  },
  MORE:{
    name: "Ver",
    icon: MATERIAL_ICONS.MORE_VERT,
    id: MATERIAL_ICONS.MORE_VERT,
  }
};



export const NewsEnums = {
  VIEWS_NEWS,
  ACTION_NEWS,
  NewsSidenav
};