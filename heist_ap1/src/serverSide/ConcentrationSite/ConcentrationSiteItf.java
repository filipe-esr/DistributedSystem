/**
 * heist_tp2 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency and sockets.
 */
package serverSide.ConcentrationSite;
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
public class ConcentrationSiteItf implements ServerInterface{
    private MConcentrationSite concentrationSite;
    private boolean serviceEnded;
    
    /**
     * Instanciação do interface do ConcentrationSite.
     * 
     * @param concentrationSite concentrationSite
     */
    public ConcentrationSiteItf(MConcentrationSite concentrationSite){
        this.concentrationSite = concentrationSite;
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
                
            case Message.REQAMINEEDED:
                if ((inMessage.getThiefParty()< 0) || (inMessage.getThiefParty() >= GlobalInfo.nParties))
                    throw new MessageException ("Party inválida!", inMessage);
                outMessage = new Message(Message.RESPAMINEEDED,concentrationSite.amINeeded(inMessage.getThiefParty()));
                break;
            
            case Message.REQPARTYNEEDED:
                if(inMessage.getPartyFree() < 0 || inMessage.getPartyFree() >= GlobalInfo.nParties)
                    throw new MessageException ("Party inválida!", inMessage);
                concentrationSite.partyNeeded(inMessage.getPartyFree(), inMessage.getPartyIsNeeded());
                outMessage = new Message(Message.ACK);
                break;
                
            case Message.REQSTOPT:
                concentrationSite.stopT();
                outMessage = new Message(Message.ACK);
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
