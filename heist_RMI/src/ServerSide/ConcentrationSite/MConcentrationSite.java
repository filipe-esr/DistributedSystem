package ServerSide.ConcentrationSite;

import Interfaces.ItfConcentrationSite;
import Structures.VectorClock;
import java.rmi.RemoteException;

/**
 * Class that represents a ConcentrationSite where the threads Thief receive infromation .
 * And executes the functions to be able to interact with the museum with restrictions of movement.
 * 
 *  @author Filipe Rocha - 67432 <p>Luis Gameiro - 67989</p>
 * 
 *  <p>Project for the course MIECT - SD - P3 G04</p>
 */
public class MConcentrationSite implements ItfConcentrationSite {
    
    private boolean[] partyNeeded;
    private boolean stopT;
    private VectorClock vc;
    
    public MConcentrationSite(int numParties, int numThieves) {
        partyNeeded = new boolean[numParties];
        for(int i = 0; i < numParties; i++){
            partyNeeded[i] = false;
        }
        stopT = false;
      	this.vc = new VectorClock(numThieves+1);
    }
    
    /**
     * Function that a thread Thief uses to verifie if it is needed. 
     * While control variable is false makes the threads in party wait.
     * 
     * @param party number of the party
     * @param vector clock vector
     * @return boolean true if a party is needed
     */
    public synchronized Object[] amINeeded(int party, VectorClock vector){
      	vc.update(vector);
      	Object tmp[] = new Object[2];
      	while(!partyNeeded[party]){
            try{
                wait();
              if(stopT){
                tmp[1] = false;
                tmp[0] = vc.getCopy();
                return tmp;
              }
            }catch(Exception e){
            }
        }
      	tmp[1] = partyNeeded[party];
      	tmp[0] = vc.getCopy();
        return tmp;
    }
    
    /**
     * Assigns a value to the respective position in the array of the party when the value changes if the party becomes free or busy.
     * 
     * @param partyFree number of the party.
     * @param free boolean value to assing to the respective party indicating if it is free (true) or busy (false)
     */
    public synchronized Object[] partyNeeded(int partyFree, boolean free, VectorClock vector){
        vc.update(vector);
      	Object tmp[] = new Object[2];
      	partyNeeded[partyFree] = free;
        notifyAll();
      	tmp[0] = vc.getCopy();
      	return tmp;
    }

    /**
     * When called by the MasterThief notifies all thieves to stop.
     */
    public synchronized Object[] stopT(VectorClock vector){
        vc.update(vector);
      	Object tmp[] = new Object[2];
      	stopT = true;
        notifyAll();
      	tmp[0] = vc.getCopy();
      	return tmp;
    }
    
    public synchronized void terminate() throws RemoteException{
        MConcentrationSiteClient.shutdown();
    }
}