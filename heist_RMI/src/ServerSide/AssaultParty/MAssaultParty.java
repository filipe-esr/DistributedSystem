package ServerSide.AssaultParty;

import Interfaces.ItfAssaultParty;
import Interfaces.ItfGenRep;
import Structures.VectorClock;
import java.rmi.RemoteException;
/**
 * Class where the threads Thief are included in a assault party.
 * And executes the functions to be able to interact with the museum with restrictions of movement.
 * 
 *  @author Filipe Rocha - 67432 <p>Luis Gameiro - 67989</p>
 * 
 *  <p>Project for the course MIECT - SD - P3 G04</p>
 */
public class MAssaultParty implements ItfAssaultParty {
    
    private final int elem;
    private int elemsAreReady;  //Thiefs are ready at a certain moment while they're preparing
    private boolean orderToGo;
    private int room;           //tem de ser privadas! todas as publics dentro de uma classe monitora
    private int roomDistance;
    private int movement;       //Value of movement of the party (++ para o interior, -- para o exterior)
    private int[] position;     //Array of the position of the thieves during the movement
    private final int maxSeparation;
    private boolean stopT = false;
    public ItfGenRep log;
  	private VectorClock vc;
    
    /**
     * Initializes the Assault Party.
     * 
     * @param elem int number of elements in a party.
     * @param maxSeparation int maximum value of distance that the party can be separated while moving.
     * @param generalRepository MGeneralRepository class where changes will be documented in a log.
     * @param numThieves int total number of thieves.
     */
    public MAssaultParty(int elem,int maxSeparation, ItfGenRep generalRepository, int numThieves) {
        this.elem = elem;
        this.elemsAreReady = 0;
        this.movement = 0;
        this.roomDistance = 0;
        this.maxSeparation = maxSeparation;
        this.orderToGo = false;
        position = new int[elem+maxSeparation];
        for(int i = 0; i < elem+maxSeparation; i++)
            position[i] = -1;
        this.log = generalRepository;
      	this.vc = new VectorClock(numThieves+1);
    }
    
    /**
     * Adds a thread Thief to a party. Then he waits until party is full and order to attack room is given.
     * When all members in party are ready thread MasterThief is notified and gives order to attack.
     * 
     * @param id int id of the thread thief
     * @param vector clock vector
     * @return int room to assault
     */
    public synchronized Object[] prepareExcursion(int id, VectorClock vector){
      	vc.update(vector);
        Object tmp[] = new Object[2];
        elemsAreReady +=1;
        for(int i = 0; i < elem;i++){
            if(position[i] == -1){
                position[i] = id;
                break;
            }
        }
        if(elemsAreReady == elem){
            notify();
        }
        
        while(!orderToGo)
        {
            try{
                wait();
            }catch(Exception e){
            }
        }
        tmp[1] = room;
        tmp[0] = vc.getCopy();
        return tmp;
    }
    
    /**
     * Function where the threads thieves move (according to the restrictions) to reach the room to assault.
     * Returns one of 2 states: CrawlingInwards or AtARoom.
     * 
     * @param id int id of the thread thief to make a move
     * @param maxDisplacement int maximum value of distance that the thief can move
     * @param vector clock vector
     * @return string "CrawlingInwards" if still moving to the room, and "AtARoom" if it reached the room
     * @throws RemoteException
     */
    public synchronized Object[] crawlIn(int id, int maxDisplacement, VectorClock vector) throws RemoteException{
        vc.update(vector);
        Object ret[] = new Object[2];
      	int last = -1; //Last thief in the party
        int otherThief = -1; //Other besides the last
        int first = -1;
        int myPosition = 0;
        
        for(int i = 0; i < position.length;i++){
            if(last == -1 && position[i] != -1 && position[i] != id){ //Search for the last thief (current thief not included)
                last = i;
            }else if(last != -1 && position[i] != -1 && position[i] != id){ //Search for another thief
                otherThief = i;
            }
            
            if(position[i] == id){ //Search for the current thief position
                myPosition = i;
            }
        }
        int tmp = myPosition;
        if(myPosition > last){
            if(myPosition > otherThief)
                first = myPosition;
            else
                first = otherThief;
        }else if(otherThief > last){
            first = otherThief;
        }else{
            first = last;
        }
        
        for(int k = maxDisplacement; k >= 0; k--){ //Goes to the biggest position it can
            int move = myPosition+k;
            if((move) <= 5){
                if(position[move] == -1 && (((move) - last) <= 3)){
                    position[move] = id;
                    position[myPosition] = -1;
                    myPosition+=k;
                    break;
                }
            }
        }
        
        if(myPosition > first){ //Increase in the distance moved to reach the room
            movement += myPosition-first;
            ret = log.setThiefPosition(id,movement, vc.getCopy());
            vc.update((VectorClock) ret[0]);
        }else if(movement-(first-myPosition) <= roomDistance && movement-(first-myPosition) >= 0){
            ret = log.setThiefPosition(id,movement-(first-myPosition), vc.getCopy());
            vc.update((VectorClock) ret[0]);
        }else{   
            ret = log.setThiefPosition(id,0,vc.getCopy());
            vc.update((VectorClock) ret[0]);
        }
        
        while(position[0] == -1){ //Reorganization of the array into 5 positions
            for(int i=0;i<position.length-1;i++){
                position[i] = position[i+1];
            }
            position[5] = -1;  
        }
        
        if(movement >= roomDistance){
            movement = roomDistance;
            ret = log.setThiefPosition(id,roomDistance,vc.getCopy());
            vc.update((VectorClock) ret[0]);
            ret[1] = "AtARoom";
            ret[0] = vc.getCopy();
            return ret;
        }
        
        if(tmp != myPosition)   //Avoids repetition on log without change
            log.printChange();
        
      	ret[1] = "CrawlingInwards";
        ret[0] = vc.getCopy();
        return ret;
    }
    
    /**
     * Waits for all threads thief in the party to be ready to leave the room.
     * Returns the state CrawlingOutwards.
     * 
     * @param vector clock vector
     * @return string "CrawlingOutwards"
     */
    public synchronized Object[] reverseDirection(VectorClock vector){
        vc.update(vector);
        Object tmp[] = new Object[2];
      	elemsAreReady += 1;
        while(elemsAreReady != elem){ //Waits for all thieves to be ready before exiting the room
            try{
                wait();
            }catch(Exception e){
            }
        }
        notifyAll();
      	tmp[1] = "CrawlingOutwards";
        tmp[0] = vc.getCopy();
        return tmp;
    }
    
    /**
     * Function where the threads thieves move (according to the restrictions) to get out of the museum and reach the outside.
     * Returns one of 2 states: CrawlingOutwards or Outside.
     * 
     * @param id int id of the thread thief to make a move
     * @param maxDisplacement int maximum value of distance that the thief can move
     * @param vector clock vector
     * @return string "CrawlingOutwards" if still moving to the outside, and "Outside" if it reached the outside.
     * @throws RemoteException
     */
    public synchronized Object[] crawlOut(int id, int maxDisplacement, VectorClock vector) throws RemoteException{
        vc.update(vector);
        Object ret[] = new Object[2];
      	int last = -1;
        int otherThief = -1;
        int first = -1;
        int myPosition = 0;
        
        for(int i = 0; i < position.length;i++){
            if(last == -1 && position[i] != -1 && position[i] != id){
                last = i;
            }else if(last != -1 && position[i] != -1 && position[i] != id){
                otherThief = i;
            }
            
            if(position[i] == id){
                myPosition = i;
            }
        }
        int tmp = myPosition;
        if(myPosition > last){
            if(myPosition > otherThief)
                first = myPosition;
            else
                first = otherThief;
        }else if(otherThief > last){
            first = otherThief;
        }else{
            first = last;
        }
        
        for(int k = maxDisplacement; k >= 0; k--){
            int move = myPosition+k;
            if((move) <= 5){
                if(position[move] == -1 && (((move) - last) <= 3)){
                    position[move] = id;
                    position[myPosition] = -1;
                    myPosition+=k;
                    break;
                }
            }
        }
        
        if(myPosition > first){ // Decrease of the distance walked to exit the museum
            movement -= myPosition-first;
            ret = log.setThiefPosition(id,movement,vc.getCopy());
            vc.update((VectorClock) ret[0]);
        }else if(movement+(first-myPosition) >= 0 && movement+(first-myPosition) <= roomDistance){
            ret = log.setThiefPosition(id,movement+(first-myPosition),vc.getCopy());
            vc.update((VectorClock) ret[0]);
        }else{
            ret = log.setThiefPosition(id,roomDistance,vc.getCopy());
            vc.update((VectorClock) ret[0]);
        }
        
        while(position[0] == -1){
            for(int i=0;i<position.length-1;i++){
                position[i] = position[i+1];
            }
            position[5] = -1;  
        }
        
        if(movement <= 0){
            movement = 0;
            ret = log.setThiefPosition(id,0,vc.getCopy());
            vc.update((VectorClock) ret[0]);
            ret[1] = "Outside";
            ret[0] = vc.getCopy();
            return ret;
        }
        
        if(tmp != myPosition)
            log.printChange();
        
      	ret[1] = "CrawlingOutwards";
        ret[0] = vc.getCopy();
        return ret;
    }
    
    /**
     * Thread MasterThief gives AssaultParty information about next target room.
     * 
     * @param roomToAttack int number of the room to attack
     * @param roomDist int distance needed to reach the room
     * @param vector clock vector
     */
    public synchronized Object[] prepareAssaultParty(int roomToAttack, int roomDist, VectorClock vector){
        vc.update(vector);
        Object tmp[] = new Object[2];
      	this.orderToGo = false;
        this.room = roomToAttack;
        this.roomDistance = roomDist; //Number of the party as argument
        for(int i = 0; i < elem+maxSeparation; i++)
            position[i] = -1;
        elemsAreReady = 0;
      	tmp[0] = vc.getCopy();
        return tmp;
    }
    
    /**
     * Thread MasterThief notifies the threads Thieves in AssaultParty they are needed and waits. 
     * When all are ready gives order to Assault.
     * 
     * @param vector clock vector
     */
    public synchronized Object[] sendAssaultParty(VectorClock vector){
        /*FORÇA AMIGOS! TRAGAM TODA ESSA FORTUNA*/
        vc.update(vector);
        Object tmp[] = new Object[2];
      	notifyAll();
        while(elemsAreReady != elem){
            try{
                wait();
            }catch(Exception e){  
            }
        }
        orderToGo = true;
        this.elemsAreReady = 0;
        notifyAll();
      	tmp[0] = vc.getCopy();
        return tmp;
    }
    
    public synchronized void terminate() throws RemoteException{
        MAssaultPartyClient.shutdown();
    }
}
