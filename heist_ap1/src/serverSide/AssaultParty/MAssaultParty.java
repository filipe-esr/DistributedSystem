/**
 * heist_tp2 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency and sockets.
 */
package serverSide.AssaultParty;

import Interface.*;
import clientSide.ClientCom;
import comInf.CommPorts;
import comInf.Message;
import static java.lang.Thread.sleep;
import genclass.GenericIO;
/**
 * Class where the threads Thief are included in a assault party.
 * And executes the functions to be able to interact with the museum with restrictions of movement.
 * 
 *  @author Filipe Rocha - 67432 <p>Luis Gameiro - 67989</p>
 * 
 *  <p>Project for the course MIECT - SD - P3 G04</p>
 */
public class MAssaultParty implements ItfTAssaultParty, ItfMTAssaultParty{
    
    private final int elem;
    private int elemsAreReady;  //Thiefs are ready at a certain moment while they're preparing
    private boolean orderToGo;
    private int room;           //tem de ser privadas! todas as publics dentro de uma classe monitora
    private int roomDistance;
    private int movement;       //Value of movement of the party (++ para o interior, -- para o exterior)
    private int[] position;     //Array of the position of the thieves during the movement
    private final int maxSeparation;
    private boolean stopT = false;
    
    private String logName = CommPorts.genRepServerName;
    private int logPort = CommPorts.genRepServerPort;
    /**
     * Initializes the Assault Party.
     * 
     * @param elem int number of elements in a party.
     * @param maxSeparation int maximum value of distance that the party can be separated while moving.
     */
    public MAssaultParty(int elem,int maxSeparation) {
        this.elem = elem;
        this.elemsAreReady = 0;
        this.movement = 0;
        this.roomDistance = 0;
        this.maxSeparation = maxSeparation;
        this.orderToGo = false;
        position = new int[elem+maxSeparation];
        for(int i = 0; i < elem+maxSeparation; i++)
            position[i] = -1;
    }
    
    /**
     * Adds a thread Thief to a party. Then he waits until party is full and order to attack room is given.
     * When all members in party are ready thread MasterThief is notified and gives order to attack.
     * 
     * @param id int id of the thread thief.
     * @return int room to assault.
     */
    public synchronized int prepareExcursion(int id){
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
        
        return room;
    }
    
    /**
     * Function where the threads thieves move (according to the restrictions) to reach the room to assault.
     * Returns one of 2 states: CrawlingInwards or AtARoom.
     * 
     * @param id int id of the thread thief to make a move.
     * @param maxDisplacement int maximum value of distance that the thief can move.
     * @return string "CrawlingInwards" if still moving to the room, and "AtARoom" if it reached the room.
     */
    public synchronized String crawlIn(int id, int maxDisplacement){
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
            LOGsetThiefPosition(id, movement);
        }else if(movement-(first-myPosition) <= roomDistance && movement-(first-myPosition) >= 0){
            LOGsetThiefPosition(id, movement-(first-myPosition));
        }else{
            LOGsetThiefPosition(id, 0);
        }
        
        while(position[0] == -1){ //Reorganization of the array into 5 positions
            for(int i=0;i<position.length-1;i++){
                position[i] = position[i+1];
            }
            position[5] = -1;  
        }
        
        if(movement >= roomDistance){
            movement = roomDistance;
            LOGsetThiefPosition(id, roomDistance);
            return "AtARoom";
        }
        
        if(tmp != myPosition)   //Avoids repetition on log without change
            LOGprint("Change");
        
        return "CrawlingInwards";
    }
    
    /**
     * Waits for all threads thief in the party to be ready to leave the room.
     * Returns the state CrawlingOutwards.
     * 
     * @return string "CrawlingOutwards"
     */
    public synchronized String reverseDirection(){
        elemsAreReady += 1;
        while(elemsAreReady != elem){ //Waits for all thieves to be ready before exiting the room
            try{
                wait();
            }catch(Exception e){
            }
        }
        notifyAll();
        return "CrawlingOutwards";
    }
    
    /**
     * Function where the threads thieves move (according to the restrictions) to get out of the museum and reach the outside.
     * Returns one of 2 states: CrawlingOutwards or Outside.
     * 
     * @param id int id of the thread thief to make a move.
     * @param maxDisplacement int maximum value of distance that the thief can move.
     * @return string "CrawlingOutwards" if still moving to the outside, and "Outside" if it reached the outside.
     */
    public synchronized String crawlOut(int id, int maxDisplacement){
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
            LOGsetThiefPosition(id, movement);
        }else if(movement+(first-myPosition) >= 0 && movement+(first-myPosition) <= roomDistance){
            LOGsetThiefPosition(id, movement+(first-myPosition));
        }else{
            LOGsetThiefPosition(id, roomDistance);
        }
        
        while(position[0] == -1){
            for(int i=0;i<position.length-1;i++){
                position[i] = position[i+1];
            }
            position[5] = -1;  
        }
        
        if(movement <= 0){
            movement = 0;
            LOGsetThiefPosition(id, 0);
            return "Outside";
        }
        
        if(tmp != myPosition)
            LOGprint("Change");
        
        return "CrawlingOutwards";
    }
    
    /**
     * Thread MasterThief gives AssaultParty information about next target room.
     * 
     * @param roomToAttack int number of the room to attack.
     * @param roomDist int distance needed to reach the room.
     */
    public synchronized void prepareAssaultParty(int roomToAttack, int roomDist){
        this.orderToGo = false;
        this.room = roomToAttack;
        this.roomDistance = roomDist; //Number of the party as argument
        for(int i = 0; i < elem+maxSeparation; i++)
            position[i] = -1;
        elemsAreReady = 0;
    }
    
    /**
     * Thread MasterThief notifies the threads Thieves in AssaultParty they are needed and waits. 
     * When all are ready gives order to Assault.
     * 
     */
    @Override
    public synchronized void sendAssaultParty(){
        /*FORÇA AMIGOS! TRAGAM TODA ESSA FORTUNA*/
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
    }
    
    /**
     * Set the thief position in the LOG.
     * 
     * @param id
     * @param movement 
     */
    private void LOGsetThiefPosition(int id, int movement){
        ClientCom con = new ClientCom (logName, logPort);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQSETTHIEFPOS, id, movement);
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
     * Prints cahnge in the LOG.
     * 
     * @param txt 
     */
    private void LOGprint(String txt){
        ClientCom con = new ClientCom (logName, logPort);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        if(txt.equals("Change"))
            outMessage = new Message (Message.REQPRINTCHANGE);     
        else
            outMessage = new Message (Message.REQSTOPT);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.ACK))
           { GenericIO.writelnString ("Thread " + Thread.currentThread().getName() + ": Tipo inválido!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
    }
}
