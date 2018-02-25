/**
 * heist_tp2 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency and sockets.
 */
package clientSide.MasterThief;

import clientSide.ClientCom;
import comInf.*;
import genclass.GenericIO;
import static java.lang.Thread.sleep;
import java.util.Arrays;

/**
 *
 * @author Filipe Rocha - 67432 and Luis Gameiro - 67989
 * 
 * Project for the course MIECT - SD - P3 G04
 */
public class MasterThiefExec {
    /**
     * MasterThiefExec main function to start the MasterThief client.
     * 
     * @param args no need
     */
    public static void main(String[] args) {
        TMasterThief mt = new TMasterThief(GlobalInfo.nRooms, GlobalInfo.nThieves, GlobalInfo.nParties);
        mt.start();
        try {
            mt.join();
        } catch (InterruptedException e) {
        }
        
        GenericIO.writelnString("Sending message END to GeneralRepository");
        GenericIO.writelnString("Client (MasterThief) operations done!");
        
        Message inMessage, outMessage;
        ClientCom con = new ClientCom(CommPorts.genRepServerName, CommPorts.genRepServerPort);
        while (!con.open()) {
            try {
                sleep((long) (10));
            } catch (InterruptedException e) {
            }
        }
        outMessage = new Message(Message.END);
        con.writeObject(outMessage);
        inMessage = (Message) con.readObject();
        if (inMessage.getType() != Message.ACK) {
            GenericIO.writelnString("Invalid Type. Message:" + inMessage.toString());
            GenericIO.writelnString(Arrays.toString(Thread.currentThread().getStackTrace()));
            System.exit(1);
        }
        con.close();
    }
}
