/**
 * heist_tp2 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency and sockets.
 */
package serverSide.ConcentrationSite;
import Interface.ItfTConcentrationSite;
import Interface.ItfMTConcentrationSite;
/**
 * Class that represents a ConcentrationSite where the threads Thief receive infromation .
 * And executes the functions to be able to interact with the museum with restrictions of movement.
 * 
 *  @author Filipe Rocha - 67432 <p>Luis Gameiro - 67989</p>
 * 
 *  <p>Project for the course MIECT - SD - P3 G04</p>
 */
public class MConcentrationSite implements ItfTConcentrationSite, ItfMTConcentrationSite{
    
    private boolean[] partyNeeded;
    private boolean stopT;
    
    /**
     * Initializes MConcentrationSite.
     * 
     * @param numParties number of parties
     */
    public MConcentrationSite(int numParties) {
        partyNeeded = new boolean[numParties];
        for(int i = 0; i < numParties; i++){
            partyNeeded[i] = false;
        }
        stopT = false;
    }
    
    /**
     * Function that a thread Thief uses to verifie if it is needed. 
     * While control variable is false makes the threads in party wait.
     * 
     * @param party number of the party.
     * @return boolean true if a party is needed.
     */
    public synchronized boolean amINeeded(int party){
        while(!partyNeeded[party]){
            try{
                wait();
                if(stopT)
                    return false;
            }catch(Exception e){
            }
        }
        //GenericIO.writelnString("ESTOU A ESPERA PARTY: "+party);
        return partyNeeded[party];
    }
    
    /**
     * Assigns a value to the respective position in the array of the party when the value changes if the party becomes free or busy.
     * 
     * @param partyFree number of the party.
     * @param free boolean value to assing to the respective party indicating if it is free (true) or busy (false)
     */
    public synchronized void partyNeeded(int partyFree, boolean free){
        partyNeeded[partyFree] = free;
        notifyAll();
    }

    /**
     * When called by the MasterThief notifies all thieves to stop.
     */
    public synchronized void stopT(){
        stopT = true;
        notifyAll();
    }
}
