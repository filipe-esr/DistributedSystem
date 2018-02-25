/**
 * heist_tp2 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency and sockets.
 */
package serverSide.GeneralRepository;

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
public class GeneralRepositoryItf implements ServerInterface{
    private MGeneralRepository generalRep;
    private boolean serviceEnded;
    
    /**
     * Instanciação do interface do General Repository.
     * 
     * @param genRep MGeneralRepository
     */
    public GeneralRepositoryItf(MGeneralRepository genRep){
        this.generalRep = genRep;
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
                
            case Message.REQPRINTFIRST:
                generalRep.printFirst();
                break;
            
            case Message.REQPRINTCHANGE:
                generalRep.printChange();
                break;
                
            case Message.REQPRINTALL:
                generalRep.printItAllOut();
                break;
            
            case Message.REQADDCANVASTOTAL:
                generalRep.addCanvasTotal();
                break;
                
            case Message.REQREMOVECANVASFROMROOM:
                if(inMessage.getRoom() < 0 || inMessage.getRoom() >= GlobalInfo.nRooms)
                    throw new MessageException ("Room inválido!", inMessage);
                if(inMessage.getCanvasRoom() < 0 || inMessage.getCanvasRoom() > GlobalInfo.maxCanvas)
                    throw new MessageException ("Canvas inválido!", inMessage);
                generalRep.remCanvasFromRoom(inMessage.getRoom(), inMessage.getCanvasRoom());
                break;
            
            case Message.REQSETMUSEUMROOMS:
                if(inMessage.getCanvasRooms().length == 0 || inMessage.getCanvasRooms().length > GlobalInfo.nRooms)
                    throw new MessageException ("Array de Canvas inválido!", inMessage);
                if(inMessage.getDistRooms().length == 0 || inMessage.getDistRooms().length > GlobalInfo.nRooms)
                    throw new MessageException ("Array de Distancias inválido!", inMessage);
                generalRep.setMuseumRooms(inMessage.getCanvasRooms(), inMessage.getDistRooms());
                break;
                
            case Message.REQSETMTSTATE:
                if(inMessage.getMTStateLog() != 1000 && inMessage.getMTStateLog() != 2000 && inMessage.getMTStateLog() != 3000 && inMessage.getMTStateLog() != 4000 && inMessage.getMTStateLog() != 5000)
                    throw new MessageException ("Estado MT inválido!", inMessage);
                generalRep.setMTState(inMessage.getMTStateLog());
                break;
                
            case Message.REQSETTSTATE:
                if(inMessage.getThiefID() < 0 || inMessage.getThiefID() >= GlobalInfo.nThieves)
                    throw new MessageException ("ID inválido!", inMessage);
                if(inMessage.getThiefStateLog() != 1000 && inMessage.getThiefStateLog() != 2000 && inMessage.getThiefStateLog() != 3000 && inMessage.getThiefStateLog() != 4000)
                    throw new MessageException ("Estado T inválido!", inMessage);
                generalRep.setTState(inMessage.getThiefID(),inMessage.getThiefStateLog());
                break;
                
            case Message.REQSETTSITUATION:
                if(inMessage.getThiefID() < 0 || inMessage.getThiefID() >= GlobalInfo.nThieves)
                    throw new MessageException ("ID inválido!", inMessage);
                if(inMessage.getThiefStateLog() != 1000 && inMessage.getThiefStateLog() != 2000 && inMessage.getThiefStateLog() != 3000 && inMessage.getThiefStateLog() != 4000)
                    throw new MessageException ("Estado T inválido!", inMessage);
                if(inMessage.getSituation() != 'W' && inMessage.getSituation() != 'P')
                    throw new MessageException ("Situação T inválida!", inMessage);
                generalRep.setTStateSituation(inMessage.getThiefID(),inMessage.getThiefStateLog(),inMessage.getSituation());
                break;
                
            case Message.REQSETTMAXDISP:
                if(inMessage.getThiefID() < 0 || inMessage.getThiefID() >= GlobalInfo.nThieves)
                    throw new MessageException ("ID inválido!", inMessage);
                if(inMessage.getMaxDisplacement() < 1)
                    throw new MessageException ("MaxDisp T inválido!", inMessage);
                generalRep.setTmaxDisp(inMessage.getThiefID(), inMessage.getMaxDisplacement());
                break;
                
            case Message.REQSETPARTYROOM:
                if(inMessage.getPartyFree()< 0 || inMessage.getPartyFree() >= GlobalInfo.nParties)
                    throw new MessageException ("Party inválida!", inMessage);
                if(inMessage.getRoom() < 0 || inMessage.getRoom() > GlobalInfo.nRooms)
                    throw new MessageException ("Room inválido!", inMessage);
                generalRep.setPartyRoom(inMessage.getPartyFree(), inMessage.getRoom());
                break;
            
            case Message.REQSETTHIEFPOS:
                if(inMessage.getThiefID()< 0 || inMessage.getThiefID()>= GlobalInfo.nThieves)
                    throw new MessageException ("ID inválido!", inMessage);
                if( inMessage.getMovementLog()> GlobalInfo.maxDist) // estava maxRoomDist // inMessage.getMovementLog()< 0 ||
                    throw new MessageException ("Posição inválida!", inMessage);
                generalRep.setThiefPosition(inMessage.getThiefID(), inMessage.getMovementLog());
                break;
                
            case Message.REQSETTCANVASSTATUS:
                if(inMessage.getThiefID()< 0 || inMessage.getThiefID()>= GlobalInfo.nThieves)
                    throw new MessageException ("ID inválido!", inMessage);
                if(inMessage.getCanvasStatusBitLog() != 0 && inMessage.getCanvasStatusBitLog() != 1)
                    throw new MessageException ("Estado inválido!", inMessage);
                generalRep.setTCanvasStatus(inMessage.getThiefID(), inMessage.getCanvasStatusBitLog());
                break;
                
            default:
                throw new MessageException("Wrong Message",inMessage);
        }
        outMessage = new Message(Message.ACK);
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
