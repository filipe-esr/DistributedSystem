package ServerSide.ControlSite;

import Interfaces.ItfControlSite;
import Interfaces.ItfGenRep;
import Structures.VectorClock;
import java.rmi.RemoteException;
/**
 * Class where the thread MasterThief and the threads Thief exchange information retrived from the class museum.
 * 
 *  @author Filipe Rocha - 67432 <p>Luis Gameiro - 67989</p>
 * 
 *  <p>Project for the course MIECT - SD - P3 G04</p>
 */
public class MControlSite implements ItfControlSite {
    
    private boolean needToWait;
    private int numCanvasCollected; //Number of canvas collected
    private boolean canvasAccepted; //MT has a canvas to accept
    private boolean roomHasCanvas;  //MT notified if room has canvas
    private int partyNumber;        //Number of the Party of the Thief that hands the canvas
    private int[] listaDeEspera = new int[6];
    public ItfGenRep log;
    private VectorClock vc; 
  
    /**
     * Initializes the Control Site.
     * 
     * @param log MGeneralRepository class where changes will be documented in a log
     * @param numThieves total number of thieves
     */
    public MControlSite(ItfGenRep log, int numThieves) {
        this.needToWait = false;
        this.numCanvasCollected = 0;
        this.canvasAccepted = false;
        this.roomHasCanvas = true;
        for(int i = 0; i < listaDeEspera.length; i++){
          listaDeEspera[i] = -1;
        }
        this.log = log;
        partyNumber = -1;
        this.vc = new VectorClock(numThieves+1);
    }
    
    /**
     * Thread MasterThief starts the Assault on the Museum. 
     * Returns a state: DecidingWhatToDo.
     * 
     * @param vector clock vector
     * @return string "DecidingWhatToDo"
     */
    public Object[] startOperations(VectorClock vector){
        vc.update(vector);
        Object tmp[] = new Object[2];
        tmp[1] = "DecidingWhatToDo";
        tmp[0] = vc.getCopy();
        return tmp;
    }
    
    /**
     * The thread MasterThief enters in a wait state when it needs to wait for the notification from the thief threads that they have returned from the museum.
     * 
     * @param vector clock vector
     * @return int p that is the party which the thread Thief that woke up the masterThief belongs to
     */
    public synchronized Object[] takeARest(VectorClock vector){
        vc.update(vector);
        Object tmp[] = new Object[2];
        needToWait = true;
        while(needToWait==true){
            try{
                notify();
                wait();
            }catch(Exception e){
            }
        }
        tmp[1] = partyNumber;
        tmp[0] = vc.getCopy();
        return tmp;
    }
    
    /**
     * Calls the log print function that prints all the information results of the Assault. 
     * And return a state: PresentingTheReport.
     * 
     * @param vector clock vector
     * @return string "PresentingTheReport"
     */
    public Object[] sumUpResults(VectorClock vector){
        vc.update(vector);
        Object tmp[] = new Object[2];
        tmp[1] = "PresentingTheReport";
        tmp[0] = vc.getCopy();
        return tmp;
    }
    
    /**
     * Function where the thread MasterThief gets the value of the canvas variable from the thief. 
     * If this value is true then it means there is a canvas being handed and the number of total canvas collect is increased by 1.
     * 
     * @param vector clock vector
     * @return boolean true if room has canvas and a canvas was accepted and false if room is empty and no canvas was accepted
     */
    public synchronized Object[] collectCanvas(VectorClock vector) throws RemoteException{
        vc.update(vector);
        Object tmp[] = new Object[2];
        if(roomHasCanvas){
              numCanvasCollected += 1;
              tmp = log.addCanvasTotal(vc.getCopy());
              vc.update((VectorClock) tmp[0]);
              canvasAccepted = true;
              notify();
              tmp[1] = true;
              tmp[0] = vc.getCopy();
              return tmp;
        }else{
              canvasAccepted = true;
              notify();
              tmp[1] = false;
              tmp[0] = vc.getCopy();
              return tmp;
        }
    }
    
    /**
     * Function where the thread Thief notifies the thread MasterThief that it has arrived and, if it has, hands a canvas.
     * 
     * @param collectedCanvas boolean that indicates that thief has a canvas (true) or if the room was empty (false)
     * @param idThief int id of the thread Thief
     * @param party int number of the party that the thread Thief belongs to
     * @param vector clock vector
     * @return boolean false to change the value in the thread Thief indicating that this has already completed this function.
     */
    public synchronized Object[] handCanvas(boolean collectedCanvas, int idThief, int party, VectorClock vector){
        vc.update(vector);
      	Object tmp[] = new Object[2];
      	for(int i = 0; i < listaDeEspera.length; i++){
            if(listaDeEspera[i] == -1){
                listaDeEspera[i] = idThief;
                break;
            } 
        }
        while(listaDeEspera[0] != idThief){
            try{
                notifyAll();
                wait();
            }catch(Exception e){   
            }
        }
        canvasAccepted = false;
        roomHasCanvas = collectedCanvas;
        while(!canvasAccepted){
            try{
                if(needToWait == true){
                    needToWait = false; 
                }
                partyNumber = party;
                notify(); //MT!
                wait();
            }catch(Exception e){
            }
        }
        
        for(int i = 0; i < listaDeEspera.length-1; i++){
            listaDeEspera[i] = listaDeEspera[i+1];
        }
        listaDeEspera[5] = -1;
        notify();
        tmp[1] = false;
        tmp[0] = vc.getCopy();
        return tmp;
    }
    
    public synchronized void terminate() throws RemoteException{
        MControlSiteClient.shutdown();
    }
}