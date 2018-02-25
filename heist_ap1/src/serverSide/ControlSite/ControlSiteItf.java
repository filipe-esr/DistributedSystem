/**
 * heist_tp2 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency and sockets.
 */
package serverSide.ControlSite;

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
public class ControlSiteItf implements ServerInterface{
    private MControlSite controlSite;
    private boolean serviceEnded;
    
    /**
     * Instanciação do interface do ControlSite.
     * 
     * @param controlSite 
     */
    public ControlSiteItf(MControlSite controlSite){
        this.controlSite = controlSite;
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
                
            case Message.REQSTARTOP:
                outMessage = new Message(Message.RESPSTARTOP, controlSite.startOperations());
                break;
                
            case Message.REQTAKEREST:
              outMessage = new Message(Message.RESPTAKEREST, controlSite.takeARest());
              break;
              
            case Message.REQSUMUPRESULTS:
                outMessage = new Message(Message.RESPSUMUPRESULTS, controlSite.sumUpResults());
                break;
            
            case Message.REQCOLLECTCANVAS:
                outMessage = new Message(Message.RESPCOLLECTCANVAS, controlSite.collectCanvas());
                break;
                
            case Message.REQHANDACANVAS:
                if(inMessage.getThiefID() < 0 || inMessage.getThiefID() >= GlobalInfo.nThieves)
                    throw new MessageException ("Thief inválido!", inMessage);
                if(inMessage.getThiefParty() < 0 || inMessage.getThiefParty() >= GlobalInfo.nParties)
                    throw new MessageException ("Party inválida!", inMessage);
                outMessage = new Message(Message.RESPHANDACANVAS, controlSite.handCanvas(inMessage.getCanvasCollected(), inMessage.getThiefID(), inMessage.getThiefParty()));
                break;
                
            default:
                throw new MessageException("Wrong Message",inMessage);
        }
        return (outMessage);
    }
    
    /**
     * Service End, return value.
     * @return 
     */
    @Override
    public boolean serviceEnded() {
        return serviceEnded;
    }
}