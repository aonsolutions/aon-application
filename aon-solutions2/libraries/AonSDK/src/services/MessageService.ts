import { IMessage } from "../interfaces/modelsInterfaces";
import { IMessageSpecificMethodsRepository } from "../interfaces/repositoryInterfaces";
import { IMessageSpecificMethods } from "../interfaces/serviceInterfaces";
import { IResponse, IFilter } from "../interfaces/utilitiesInterfaces";
import { Message } from "../models/Message";
import { ErrorResponse, Response } from "../utils/Response";

export class MessageSpecificMethods implements IMessageSpecificMethods {

    repository: IMessageSpecificMethodsRepository;

    constructor(repository: IMessageSpecificMethodsRepository) {
        this.repository = repository;
    }

    /**
     * Archiva un mensaje.
     *
     * @param {Message} element - El mensaje que se va a archivar.
     * @return {Promise<IResponse<IMessage>>} Una promesa que se resuelve en un objeto IResponse<IMessage>.
     */
    async archiveMessage(element: Message): Promise<IResponse<IMessage>> {
      try {
        return new Response<IMessage>(await this.repository.archiveMessage(element));
      } catch (error) {
        throw error instanceof ErrorResponse ?  error : new ErrorResponse('0401');
      }
    }

    /**
     * Reabre un mensaje.
     *
     * @param {Message} element - El mensaje que se va a reabrir.
     * @return {Promise<IResponse<IMessage>>} - Una promesa que se resuelve en un IResponse del mensaje reabierto.
     */
    async reopenMessage(element: Message): Promise<IResponse<IMessage>> {
      try {
        return new Response<IMessage>(await this.repository.reopenMessage(element));
      }catch(error) {
        throw error instanceof ErrorResponse ?  error : new ErrorResponse('0402');
      }
    }

    /**
     * Obtiene el número de mensajes.
     *
     * @param {IFilter} filter - (opcional) El filtro que se aplicará a los mensajes.
     * @return {Promise<IResponse<number>>} Una promesa que se resuelve con el número de mensajes.
     */
    async getMessageCount(filter?: IFilter): Promise<IResponse<number>> {
      try {
      return new Response<number>(await this.repository.getMessageCount(filter));
      } catch(error) {
        throw error instanceof ErrorResponse ?  error : new ErrorResponse('0403');
      }
    }

    /**
     * Marca una notificación como leída.
     *
     * @param {Message} message - El objeto de mensaje que representa la notificación.
     * @return {Promise<IResponse<boolean>>} Una promesa que se resuelve a un objeto de respuesta indicando si la operación fue exitosa.
     */
    async markAsReadNotification(message: Message): Promise<IResponse<boolean>> {
      try {
        return new Response<boolean>(await this.repository.markAsReadNotification(message));
      } catch(error) {
        throw error instanceof ErrorResponse ?  error : new ErrorResponse('0404');
      }
    }

}
