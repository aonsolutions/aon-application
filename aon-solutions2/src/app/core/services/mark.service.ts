import { Injectable, Renderer2 } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { ICollection, IFilter, IMark, MarkFactory } from 'libraries/AonSDK/src/aon';
import { CommonService } from './common.service';

@Injectable({
  providedIn: 'root'
})
export class MarkService extends CommonService {

  private singleObjectCrud = new MarkFactory().createSingleObjectCrud();
  private markCollectionCrud = new MarkFactory().createMultipleObjectCrud();

  constructor() {
    super();
  }

  /**
   * Recupera lista de marcajes
   *
   * @param {IFilter} filter - Filtro opcional para aplicar a la lista de marcas.
   * @return {Promise<ICollection<IMark>>} - Una promesa que se resuelve a una colección de marcas.
   */
  async getMarkList(filter?: IFilter): Promise<ICollection<IMark>> {
    return (await this.markCollectionCrud.getCollection(filter)).result;
  }

  /**
   *
   * @param {any} pkey - La clave primaria del marcaje.
   * @return {Promise<IMark>} Una promesa que se resuelve con el objeto del marcaje.
   */
  async getMark(pkey: any): Promise<IMark> {
    return (await this.singleObjectCrud.getElement(pkey)).result;
  }

  /**
   * Crea un nuevo marcaje.
   *
   * @param {IMark} mark - El objeto de marcaje a crear.
   * @return {Promise<IMark>} - El objeto de marcaje creado.
   */
  async createMark(mark: IMark): Promise<IMark> {
    return (await this.singleObjectCrud.createElement(mark)).result;
  }

  /**
   * Actualiza un marcaje
   *
   * @param {IMark} mark - El objeto de marcaje a actualizar.
   * @return {Promise<IMark>} - El objeto de marcaje actualizado.
   */
  async updateMark(mark: IMark): Promise<IMark> {
    return (await this.singleObjectCrud.updateElement(mark)).result;
  }

  /**
   * Elimina un marcaje.
   *
   * @param {any} pkey - La clave primaria del marcaje.
   * @return {Promise<boolean>} - Una promesa que se resuelve con un valor booleano indicando si se elimino el marcaje.
   */
  async deleteMark(pkey: any): Promise<boolean> {
    return (await this.singleObjectCrud.deleteElement(pkey)).result;
  }

}
