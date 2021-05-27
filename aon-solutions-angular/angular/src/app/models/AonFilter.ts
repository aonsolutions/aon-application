
export interface CompanyFilter {
  value?: string,
  document?: string;
  name?: string;
  user?: string;
  filter?: string;
  parent?: boolean;
  active?: boolean;
  inactive?: boolean;
  shared?: boolean;
}

export interface UserFilter {
  value?: string,
  filter?: string;
  last?: string;
  admin?: boolean;
  gestor?: boolean;
  user?: string;
}
