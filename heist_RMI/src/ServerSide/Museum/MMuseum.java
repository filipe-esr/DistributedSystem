package ServerSide.Museum;

import Interfaces.ItfGenRep;
import Interfaces.ItfMuseum;
import Structures.VectorClock;
import java.util.Random;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Class where the information about the rooms and canvas are created and accessed.
 * 
 *  @author Filipe Rocha - 67432 <p>Luis Gameiro - 67989</p>
 * 
 *  <p>Project for the course MIECT - SD - P3 G04</p>
 */
public class MMuseum implements ItfMuseum, Remote{
    
    private final int[] canvasRooms;    // array of rooms to save number of canvas per room
    private final int[] distRooms;      // array of rooms to save distance between the room and the outside
    private final int minCanvas = 8;
    private final int maxCanvas = 16;
    private final int minDist = 15;
    private final int maxDist = 30;
    private Random rC, rD;
    public ItfGenRep log;
    private VectorClock vc;
    
    /**
     * Initializes the Museum. Random generates the number of canvas in a room and the distance to it.
     * 
     * @param rooms number of rooms in the museum
     * @param generalRepository MGeneralRepository interface where changes will be documented in a log
     * @param numThieves number of thieves
     * @throws RemoteException 
     */
    public MMuseum(int rooms, ItfGenRep generalRepository, int numThieves) throws RemoteException {
        this.canvasRooms = new int[rooms];
        rC = new Random();
        this.distRooms = new int[rooms];
        rD = new Random();
        this.log = generalRepository;
        for(int i = 0; i<this.canvasRooms.length; i++){
            int canvas = rC.nextInt(maxCanvas-minCanvas) + minCanvas; /*random between 8 and 16*/
            this.canvasRooms[i] = canvas;
            int dist = rD.nextInt(maxDist-minDist) + minDist; /*random between 15 and 30*/
            this.distRooms[i] = dist;
        }
        this.vc = new VectorClock(numThieves+1);
        log.setMuseumRooms(canvasRooms, distRooms, vc.getCopy());    	
    }
    
    /**
     * Allows the thread Thief to get a canvas from the specific room given as argument. 
     * Decrements 1 to the total of canvas in the room.
     * 
     * @param room int number of the room to roll a canvas
     * @param vector clock vector
     * @return boolean value true if room has canvas
     * @throws RemoteException 
     */
    public synchronized Object[] rollACanvas(int room, VectorClock vector) throws RemoteException{
        vc.update(vector);
        Object tmp[] = new Object[2];
      	if(canvasRooms[room] > 0){
            canvasRooms[room] -= 1;
            log.remCanvasFromRoom(room, canvasRooms[room],vc.getCopy());
            tmp[1] = true;
            tmp[0] = vc.getCopy();
            return tmp;
        }
      	tmp[1] = false;
        tmp[0] = vc.getCopy();
        return tmp;
    }
    
    /**
     * Returns the distance to all rooms in the museum.
     * 
     * @param vector clock vector
     * @return distRooms int[] to save the distance to the rooms.
     */
    public synchronized Object[] getDist(VectorClock vector){
        vc.update(vector);
        Object tmp[] = new Object[2];
      	tmp[1] = distRooms;
        tmp[0] = vc.getCopy();
        return tmp;
    }
    
    public synchronized void terminate() throws RemoteException{
        MMuseumClient.shutdown();
    }
}
