/**
 * heist_tp2 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency and sockets.
 */
package comInf;

/**
 *   Este tipo de dados define uma excepção que é lançada se a mensagem for inválida.
 */

public class MessageException extends Exception
{
  /**
   *  Mensagem que originou a excepção
   */
   private Message msg;

  /**
   *  Instanciação de uma mensagem.
   *
   *    @param errorMessage texto sinalizando a condição de erro
   *    @param msg mensagem que está na origem da excepção
   */
   public MessageException (String errorMessage, Message msg)
   {
     super (errorMessage);
     this.msg = msg;
   }

  /**
   *  Obtenção da mensagem que originou a excepção.
   *
   *    @return mensagem
   */
   public Message getMessageVal ()
   {
     return (msg);
   }
}
