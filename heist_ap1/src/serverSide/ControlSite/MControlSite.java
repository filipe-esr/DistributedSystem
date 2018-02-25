/**
 * heist_tp2 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency and sockets.
 */
package serverSide.ControlSite;

import Interface.ItfTControlSite;
import Interface.ItfMTControlSite;
/**
 * Class where the thread MasterThief and the threads Thief exchange information retrived from the class museum.
 * 
 *  @author Filipe Rocha - 67432 <p>Luis Gameiro - 67989</p>
 * 
 *  <p>Project for the course MIECT - SD - P3 G04</p>
 */
public class MControlSite implements ItfTControlSite, ItfMTControlSite{
    
    private boolean needToWait;
    private int numCanvasCollected; //Number of canvas collected
    private boolean canvasAccepted; //MT has a canvas to accept
    private boolean roomHasCanvas;  //MT notified if room has canvas
    private int partyNumber;        //Number of the Party of the Thief that hands the canvas
    private int[] listaDeEspera = new int[6];
    
    /**
     * Initializes the Control Site.
     * 
     */
    public MControlSite() {
       this.needToWait = false;
       this.numCanvasCollected = 0;
       this.canvasAccepted = false;
       this.roomHasCanvas = true;
       for(int i = 0; i < listaDeEspera.length; i++){
           listaDeEspera[i] = -1;
       }
       partyNumber = -1;
    }
    
    /**
     * Thread MasterThief starts the Assault on the Museum. 
     * Returns a state: DecidingWhatToDo.
     * 
     * @return string "DecidingWhatToDo".
     */
    public String startOperations(){
        return "DecidingWhatToDo";
    }
    
    /**
     * The thread MasterThief enters in a wait state when it needs to wait for the notification from the thief threads that they have returned from the museum.
     * 
     * @return int p that is the party which the thread Thief that woke up the masterThief belongs to. 
     */
    public synchronized int takeARest(){
        needToWait = true;
        while(needToWait==true){
            try{
                notify();
                wait();
            }catch(Exception e){
            }
        }
        return partyNumber;
    }
    
    /**
     * Calls the log print function that prints all the information results of the Assault. 
     * And return a state: PresentingTheReport.
     * 
     * @return string "PresentingTheReport".
     */
    public String sumUpResults(){
        return "PresentingTheReport";
    }
    
    /**
     * Function where the thread MasterThief gets the value of the canvas variable from the thief. 
     * If this value is true then it means there is a canvas being handed and the number of total canvas collect is increased by 1.
     * 
     * @return boolean true if room has canvas and a canvas was accepted and false if room is empty and no canvas was accepted.
     */
    public synchronized boolean collectCanvas(){
        if(roomHasCanvas){
            numCanvasCollected += 1;
            canvasAccepted = true;
            notify();
            return true;
        }else{
            canvasAccepted = true;
            notify();
            return false;
        }
    }
    
    /**
     * Function where the thread Thief notifies the thread MasterThief that it has arrived and, if it has, hands a canvas.
     * 
     * @param collectedCanvas boolean that indicates that thief has a canvas (true) or if the room was empty (false).
     * @param idThief int id of the thread Thief.
     * @param party int number of the party that the thread Thief belongs to.
     * @return boolean false to change the value in the thread Thief indicating that this has already completed this function.
     */
    public synchronized boolean handCanvas(boolean collectedCanvas, int idThief, int party){
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
        return false;
    }
}
