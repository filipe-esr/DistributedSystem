/**
 * heist_tp2 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency and sockets.
 */
package serverSide.Museum;

import comInf.GlobalInfo;
import comInf.Message;
import comInf.MessageException;
import serverSide.ServerCom;
import serverSide.ServerInterface;
/**
 *
 * @author Filipe Rocha - 67432 and Luis Gameiro - 67989
 * 
 * Project for the course MIECT - SD - P3 G04
 */
public class MuseumItf implements ServerInterface{
    private MMuseum museum;
    private boolean serviceEnded;
    /**
     * Instanciação do interface do Museu.
     * 
     * @param museum museum
     */
    public MuseumItf(MMuseum museum){
        this.museum = museum;
        this.serviceEnded = false;
    }
    
    /**
     *  Processamento das mensagens através da execução da tarefa correspondente.
     *  Geração de uma mensagem de resposta.
     *
     *    @param inMessage mensagem com o pedido
     *    @return mensagem de resposta
     *    @throws MessageException se a mensagem com o pedido for considerada inválida
     */
    @Override
    public Message processAndReply (Message inMessage, ServerCom scon) throws MessageException
    {
        Message outMessage = null;
        
        switch(inMessage.getType()){
            case Message.END:
                outMessage = new Message(Message.ACK);
                this.serviceEnded = true;
                break;
                
            case Message.REQROLLACANVAS:
                if ((inMessage.getRoom () < 0) || (inMessage.getRoom () >= GlobalInfo.nRooms))
                    throw new MessageException ("Room inválido!", inMessage);
                outMessage = new Message(Message.RESPROLLACANVAS,museum.rollACanvas(inMessage.getRoom()));
                break;  
                
            case Message.REQDIST:
                outMessage = new Message(Message.RESPDIST,museum.getDist());
                break;
                
            default:
                throw new MessageException("Wrong Message",inMessage);    
        }
        return (outMessage);
    }
    
    /**
     * Service End, return value.
     * @return serviceEnded
     */
    @Override
    public boolean serviceEnded() {
        return serviceEnded;
    }
}
