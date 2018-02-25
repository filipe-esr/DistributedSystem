/**
 * heist_tp2 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency and sockets.
 */
package serverSide.Museum;

import Interface.ItfTMuseum;
import Interface.ItfMTMuseum;
import clientSide.ClientCom;
import comInf.CommPorts;
import comInf.Message;
import static java.lang.Thread.sleep;

import java.util.Random;
import genclass.GenericIO;
/**
 * Class where the information about the rooms and canvas are created and accessed.
 * 
 *  @author Filipe Rocha - 67432 <p>Luis Gameiro - 67989</p>
 * 
 *  <p>Project for the course MIECT - SD - P3 G04</p>
 */
public class MMuseum implements ItfTMuseum, ItfMTMuseum{ //"extends Thread" to be able to use getName() but is not a Thread
    
    private final int[] canvasRooms;    // array of rooms to save number of canvas per room
    private final int[] distRooms;      // array of rooms to save distance between the room and the outside
    private final int minCanvas = 8;
    private final int maxCanvas = 16;
    private final int minDist = 15;
    private final int maxDist = 30;
    private Random rC, rD;
    
    private String logName = CommPorts.genRepServerName;
    private int logPort = CommPorts.genRepServerPort;
    
    /**
     * Initializes the Museum. Random generates the number of canvas in a room and the distance to it.
     * 
     * @param rooms number of rooms in the museum.
     */
    public MMuseum(int rooms) {
        this.canvasRooms = new int[rooms];
        rC = new Random();
        this.distRooms = new int[rooms];
        rD = new Random();
        for(int i = 0; i<this.canvasRooms.length; i++){
            int canvas = rC.nextInt(maxCanvas-minCanvas) + minCanvas; /*random between 8 and 16*/
            this.canvasRooms[i] = canvas;
            int dist = rD.nextInt(maxDist-minDist) + minDist; /*random between 15 and 30*/
            this.distRooms[i] = dist;
        }
        LOGsetMuseumRooms(canvasRooms,distRooms);
    }
    
    
    /**
     * Allows the thread Thief to get a canvas from the specific room given as argument. 
     * Decrements 1 to the total of canvas in the room.
     * 
     * @param room int number of the room to roll a canvas. 
     * @return boolean value true if room has canvas
     */
    public synchronized boolean rollACanvas(int room){
        if(canvasRooms[room] > 0){
            canvasRooms[room] -= 1;
            LOGremCanvasFromRoom(room, canvasRooms[room]);
            return true;
        }
        return false;
    }
    
    /**
     * Returns the distance to all rooms in the museum.
     * 
     * @return distRooms int[] to save the distance to the rooms.
     */
    public synchronized int[] getDist(){
        return distRooms;
    }
    
    /**
     * Set Museum Rooms in the log.
     * 
     * @param canvasRooms
     * @param distRooms 
     */
    private void LOGsetMuseumRooms(int[] canvasRooms, int[] distRooms){
        ClientCom con = new ClientCom (logName, logPort);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQSETMUSEUMROOMS, canvasRooms, distRooms);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.ACK))
           { GenericIO.writelnString ("Thread " + Thread.currentThread().getName() + ": Tipo inválido!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
      }
    
    /**
     * Remove canvas from room in the log to print.
     * 
     * @param room
     * @param canvas 
     */
    private void LOGremCanvasFromRoom(int room, int canvas){
        ClientCom con = new ClientCom (logName, logPort);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQREMOVECANVASFROMROOM, room, canvas);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.ACK))
           { GenericIO.writelnString ("Thread " + Thread.currentThread().getName () + ": Tipo inválido!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
      }
}
