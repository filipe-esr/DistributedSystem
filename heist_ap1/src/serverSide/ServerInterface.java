/**
 * heist_tp2 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency and sockets.
 */
package serverSide;

import comInf.Message;
import comInf.MessageException;

import java.net.SocketException;

/**
 * This file defines the server interface.
 *
 * @author Filipe Rocha - 67432 and Luis Gameiro - 67989
 * 
 * Project for the course MIECT - SD - P3 G04
 */
public interface ServerInterface {
    /**
     * Processes the received messages and replies to the entity that sent it.
     * 
     * @param inMessage The received message.
     * @param scon Server communication.
     * @return Returns the reply to the received message.
     * @throws MessageException messageException
     * @throws SocketException socketException
     */
    public Message processAndReply(Message inMessage, ServerCom scon) throws MessageException, SocketException;
    
    /**
     * Tell the service if it is allowed to end or not.
     * @return True if the system can terminate, false otherwise.
     */
    public boolean serviceEnded();
}
