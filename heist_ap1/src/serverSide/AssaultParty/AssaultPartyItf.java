/**
 * heist_tp2 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency and sockets.
 */
package serverSide.AssaultParty;

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
public class AssaultPartyItf implements ServerInterface{
    /**
     *  MAssaultParty (representa o serviço a ser prestado)
     *
     *    @serialField assaultParty
     */
    private MAssaultParty assaultParty;

    private boolean serviceEnded;

    /**
     *  Instanciação do interface da AssaultParty.
     *
     *    @param assaultParty assaultParty
     */
    public AssaultPartyItf (MAssaultParty assaultParty)
    {
        this.assaultParty = assaultParty;
        serviceEnded = false;
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
        Message outMessage = null;  // mensagem de resposta

        switch (inMessage.getType ())
        {
            case Message.END:
                outMessage = new Message(Message.ACK);
                this.serviceEnded = true;
                break;

            /* MasterThief */
            case Message.REQPREPAREASSAULTPARTY:
                if ((inMessage.getRoom () < 0) || (inMessage.getRoom () > GlobalInfo.nRooms))
                    throw new MessageException ("Room inválido!", inMessage);
                if ((inMessage.getRoomDist()< GlobalInfo.minDist) || (inMessage.getRoomDist () > GlobalInfo.maxDist))
                    throw new MessageException ("RoomDist inválido!", inMessage);
                assaultParty.prepareAssaultParty(inMessage.getRoom(),inMessage.getRoomDist());
                outMessage = new Message(Message.ACK);
                break;

            case Message.REQSENDASSAULTPARTY:
                assaultParty.sendAssaultParty();
                outMessage = new Message(Message.ACK);
                break;

            /* Thief */
            case Message.REQPREPAREEXCURSION:
                if ((inMessage.getThiefID()< 0) || (inMessage.getThiefID()>= GlobalInfo.nThieves))
                    throw new MessageException ("Thief ID inválido!", inMessage);
                outMessage = new Message(Message.RESPPREPAREEXCURSION,assaultParty.prepareExcursion(inMessage.getThiefID()));
                break;

            case Message.REQCRAWLIN:
                if ((inMessage.getThiefID() < 0) || (inMessage.getThiefID() >= GlobalInfo.nThieves))
                    throw new MessageException ("Thief ID inválido!", inMessage);
                if ((inMessage.getMaxDisplacement() < GlobalInfo.minDisp) || (inMessage.getMaxDisplacement() > 5))
                    throw new MessageException ("Displacement value inválido!", inMessage);
                outMessage = new Message(Message.RESPCRAWLIN,assaultParty.crawlIn(inMessage.getThiefID(),inMessage.getMaxDisplacement()));
                break;

            case Message.REQREVERSEDIRECTION: 
                outMessage = new Message(Message.RESPREVERSEDIRECTION,  assaultParty.reverseDirection());
                break;

            case Message.REQCRAWLOUT:
                if ((inMessage.getThiefID() < 0) || (inMessage.getThiefID() >= GlobalInfo.nThieves))
                    throw new MessageException ("Thief ID inválido!", inMessage);
                if ((inMessage.getMaxDisplacement() < GlobalInfo.minDisp) || (inMessage.getMaxDisplacement() > 5))
                    throw new MessageException ("Displacement value inválido!", inMessage);
                outMessage = new Message(Message.RESPCRAWLOUT, assaultParty.crawlOut(inMessage.getThiefID(),inMessage.getMaxDisplacement()));
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

