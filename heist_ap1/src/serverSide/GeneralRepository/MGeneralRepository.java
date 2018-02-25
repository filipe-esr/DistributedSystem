/**
 * heist_tp2 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency and sockets.
 */
package serverSide.GeneralRepository;

import static java.lang.System.*;
import java.io.PrintWriter;

/**
 * Class where all the program information is gathered and printed out (to a .txt file).
 * 
 *  @author Filipe Rocha - 67432 <p>Luis Gameiro - 67989</p>
 * 
 *  <p>Project for the course MIECT - SD - P3 G04</p>
 */
public class MGeneralRepository{
    private int numCanvas;
    private int[] canvasRoom;
    private int[] distRoom;
    private int MTstate;
    private int[] Tstate, TmaxDisp;
    private char[] Tsituation;
    private int[] partyRoom;
    private int[] partyPosition;
    private int[] thiefCanvasStatus;
    PrintWriter writer;
    
    /**
     * Initializes the General Repository class.
     * 
     * @param numRooms number of the rooms in the Museum.
     * @param numThieves number of thieves present in the Assault.
     * @param numParties number of parties in the Assault.
     */
    public MGeneralRepository(int numRooms, int numThieves, int numParties) {
        numCanvas = 0;
        canvasRoom = new int[numRooms];
        distRoom = new int[numRooms];
        for(int i = 0; i < numRooms; i++){
            canvasRoom[i] = 0;
            distRoom[i] = 0;
        }
        MTstate = 0;
        Tstate = new int[numThieves];
        Tsituation = new char[numThieves];
        TmaxDisp = new int[numThieves];
        partyPosition = new int[numThieves];
        thiefCanvasStatus = new int[numThieves];
        for(int i = 0; i < numThieves; i++){
            Tstate[i] = 0;
            Tsituation[i] = 'W';
            TmaxDisp[i] = 0;
            partyPosition[i] = 0; 
            thiefCanvasStatus[i] = 0;
        }
        partyRoom = new int[numParties];
        for(int i = 0; i < numParties; i++){
            partyRoom[i] = -1;
        }
        
        try{
            writer = new PrintWriter("MuseumHeistLog.txt", "UTF-8");
        }catch (Exception e) {
        }
    }
    
    /**
     * Prints the first part of the final log information.
     * 
     */
    public void printFirst(){
        out.println("                            Heist to the Museum - Description of the internal state");
        out.println();
        out.println("MstT    Thief 1    Thief 2     Thief 3     Thief 4     Thief 5     Thief 6");
        out.println("Stat   Stat S MD  Stat S MD   Stat S MD   Stat S MD   Stat S MD   Stat S MD");
        out.println("                  Assault Party 1                    AssaultParty 2                            Museum");
        out.println("         Elem 1      Elem 2     Elem 3         Elem 1     Elem 2     Elem 3    Room 1  Room 2  Room 3  Room 4  Room 5");
        out.println("   RId  Id Pos Cv  Id Pos Cv   Id Pos Cv  RId Id Pos Cv  Id Pos Cv  Id Pos Cv   NP DT   NP DT   NP DT   NP DT   NP DT");
        
        writer.println("                            Heist to the Museum - Description of the internal state");
        writer.println();
        writer.println("MstT    Thief 1    Thief 2     Thief 3     Thief 4     Thief 5     Thief 6");
        writer.println("Stat   Stat S MD  Stat S MD   Stat S MD   Stat S MD   Stat S MD   Stat S MD");
        writer.println("                  Assault Party 1                    AssaultParty 2                            Museum");
        writer.println("         Elem 1      Elem 2     Elem 3         Elem 1     Elem 2     Elem 3    Room 1  Room 2  Room 3  Room 4  Room 5");
        writer.println("   RId  Id Pos Cv  Id Pos Cv   Id Pos Cv  RId Id Pos Cv  Id Pos Cv  Id Pos Cv   NP DT   NP DT   NP DT   NP DT   NP DT");
    }
    
    /**
     * Prints the final part of the log information with the Assault results.
     * 
     */
    public void printItAllOut(){ 
        out.print(MTstate+"    ");
        writer.print(MTstate+"    ");
        for(int i = 0; i < Tstate.length; i++){
            out.print(Tstate[i]+" "+Tsituation[i]+" "+TmaxDisp[i]+"    ");
            writer.print(Tstate[i]+" "+Tsituation[i]+" "+TmaxDisp[i]+"    ");
        }
        out.println();
        writer.println();
        out.print("    "+(partyRoom[0]+1)+"    ");
        writer.print("    "+(partyRoom[0]+1)+"    ");
        for(int i = 0; i < partyPosition.length ; i++){
            if(i!= 0 && i%3 == 0){
                out.print(" "+(partyRoom[1]+1)+"   ");
                writer.print(" "+(partyRoom[1]+1)+"   ");
            }
            out.print(i+1 +"  "+partyPosition[i]+"  "+thiefCanvasStatus[i]+"    ");
            writer.print(i+1 +"  "+partyPosition[i]+"  "+thiefCanvasStatus[i]+"    ");
        }
        out.print(" ");
        writer.print(" ");
        for(int i = 0; i < canvasRoom.length; i++){
            out.print(canvasRoom[i]+" "+distRoom[i]+"    ");
            writer.print(canvasRoom[i]+" "+distRoom[i]+"    ");
        }
        out.println("");
        out.println("");
        out.println("My friends, tonight's effort produced "+numCanvas+" priceless paintings!");
        out.println();
        out.println("Legend:");
        out.println("MstT Stat      - state of the master thief");
        out.println("Thief # Stat   - state of the ordinary thief # (# - 1 .. 6)");
        out.println("Thief # S      - situation of the ordinary thief # (# - 1 .. 6) either 'W' (waiting to join a party) or 'P' (in party)");
        out.println("Thief # MD     - maximum displacement of the ordinary thief # (# - 1 .. 6) a random number between 2 and 6");
        out.println("Assault party # RId        - assault party # (# - 1,2) elem # (# - 1 .. 3) room identification (1 .. 5)");
        out.println("Assault party # Elem # Id  - assault party # (# - 1,2) elem # (# - 1 .. 3) member identification (1 .. 6)");
        out.println("Assault party # Elem # Pos - assault party # (# - 1,2) elem # (# - 1 .. 3) present position (0 .. DT RId)");
        out.println("Assault party # Elem # Cv  - assault party # (# - 1,2) elem # (# - 1 .. 3) carrying a canvas (0,1)");
        out.println("Museum Room # NP - room identification (1..5) number of paintings presently hanging on the walls");
        out.println("Museum Room # DT - room identification (1..5) distance from outside gathering site, a random number between 15 and 30");
        
        writer.println("");
        writer.println("");
        writer.println("My friends, tonight's effort produced "+numCanvas+" priceless paintings!");
        writer.println();
        writer.println("Legend:");
        writer.println("MstT Stat      - state of the master thief");
        writer.println("Thief # Stat   - state of the ordinary thief # (# - 1 .. 6)");
        writer.println("Thief # S      - situation of the ordinary thief # (# - 1 .. 6) either 'W' (waiting to join a party) or 'P' (in party)");
        writer.println("Thief # MD     - maximum displacement of the ordinary thief # (# - 1 .. 6) a random number between 2 and 6");
        writer.println("Assault party # RId        - assault party # (# - 1,2) elem # (# - 1 .. 3) room identification (1 .. 5)");
        writer.println("Assault party # Elem # Id  - assault party # (# - 1,2) elem # (# - 1 .. 3) member identification (1 .. 6)");
        writer.println("Assault party # Elem # Pos - assault party # (# - 1,2) elem # (# - 1 .. 3) present position (0 .. DT RId)");
        writer.println("Assault party # Elem # Cv  - assault party # (# - 1,2) elem # (# - 1 .. 3) carrying a canvas (0,1)");
        writer.println("Museum Room # NP - room identification (1..5) number of paintings presently hanging on the walls");
        writer.println("Museum Room # DT - room identification (1..5) distance from outside gathering site, a random number between 15 and 30");
        writer.close();
    }
    
    /**
     * Prints the current state of the Assault when a change is made.
     * 
     */
    public synchronized void printChange(){
        out.print(MTstate+"    ");
        writer.print(MTstate+"    ");
        for(int i = 0; i < Tstate.length; i++){
            out.print(Tstate[i]+" "+Tsituation[i]+" "+TmaxDisp[i]+"    ");
            writer.print(Tstate[i]+" "+Tsituation[i]+" "+TmaxDisp[i]+"    ");
        }
        out.println();
        writer.println();
        out.print("    "+(partyRoom[0]+1)+"    ");
        writer.print("    "+(partyRoom[0]+1)+"    ");
        for(int i = 0; i < partyPosition.length ; i++){
            if(i!= 0 && i%3 == 0){
                out.print(" "+(partyRoom[1]+1)+"   ");
                writer.print(" "+(partyRoom[1]+1)+"   ");
            }
            out.print(i+1 +"  "+partyPosition[i]+"  "+thiefCanvasStatus[i]+"    ");
            writer.print(i+1 +"  "+partyPosition[i]+"  "+thiefCanvasStatus[i]+"    ");
        }
        out.print(" ");
        writer.print(" ");
        for(int i = 0; i < canvasRoom.length; i++){
            out.print(canvasRoom[i]+" "+distRoom[i]+"    ");
            writer.print(canvasRoom[i]+" "+distRoom[i]+"    ");
        }
        out.println();
        writer.println();
    }
    
    /**
     * Soma o valor de canvas total obtidas. 
     */
    public synchronized void addCanvasTotal(){
        this.numCanvas += 1;
    }
    
    /**
     * Remove canvas do quarto.
     * 
     * @param room number of room
     * @param value value to assing to the room
     */
    public synchronized void remCanvasFromRoom(int room, int value){
        this.canvasRoom[room] = value;
    }
    
    /**
     * Define os canvas por quarto.
     * 
     * @param canvas array of canvas
     * @param dist array of distance
     */
    public synchronized void setMuseumRooms(int[] canvas, int[] dist){
        this.canvasRoom = canvas;
        this.distRoom = dist;
    }
    
    /**
     * Define o estado do MasterThief.
     * 
     * @param state state
     */
    public synchronized void setMTState(int state){
        this.MTstate = state;
    }
    
    /**
     * Define o estado do Thief.
     * 
     * @param id id of thief
     * @param state state
     */
    public synchronized void setTState(int id, int state){
        this.Tstate[id] = state;
    }
    
    /**
     * Define a situação do Thief.
     * 
     * @param id id of thief
     * @param state state
     * @param sit situation
     */
    public synchronized void setTStateSituation(int id, int state, char sit)
    {
        this.Tstate[id] = state;
        this.Tsituation[id] = sit;
    }
    
    /**
     * Define o máximo deslocamento do Thief.
     * 
     * @param id id do thief
     * @param mp max displacement of the thief
     */
    public synchronized void setTmaxDisp(int id, int mp){
        this.TmaxDisp[id] = mp;
    }
    
    /**
     * Define o quarto associado ao grupo.
     * 
     * @param party number of party
     * @param room number of room
     */
    public synchronized void setPartyRoom(int party, int room){
        this.partyRoom[party] = room;
    }
    
    /**
     * Define a Posição do Thief.
     * 
     * @param id id of thief
     * @param position position of thief
     */
    public synchronized void setThiefPosition(int id, int position){
        this.partyPosition[id] = position;
    }
    
    /**
     * Define o estado do canvas com o Thief.
     * 
     * @param id id of thief
     * @param status status of thief
     */
    public synchronized void setTCanvasStatus(int id, int status){
        this.thiefCanvasStatus[id] = status;
    }
}
