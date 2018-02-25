package ServerSide.GeneralRepository;

import Structures.*;
import Interfaces.ItfGenRep;
import static java.lang.System.*;
import java.io.PrintWriter;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Class where all the program information is gathered and printed out (to a .txt file).
 * 
 *  @author Filipe Rocha - 67432 <p>Luis Gameiro - 67989</p>
 * 
 *  <p>Project for the course MIECT - SD - P3 G04</p>
 */
public class MGeneralRepository implements ItfGenRep, Remote{
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
    private VectorClock vc;
    private Object[] obj;
    
    /**
     * Initializes the General Repository class.
     * 
     * @param numRooms int number of the rooms in the Museum.
     * @param numThieves int number of thieves present in the Assault.
     * @param numParties int number of parties in the Assault.
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
        partyPosition = new int[numThieves]; //ARRAY COM POSIÇÕES DE TODOS OS THIEVES -> Eles estao fixos nas parties (por ordem)
        thiefCanvasStatus = new int[numThieves]; //ARRAY COM ESTADO DE POSSE DE TODOS OS THIEVES -> Eles estão fixos nas parties (por ordem)
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
        obj = new Object[2];
      	this.vc = new VectorClock(numThieves+1);
    }
    
    /**
     * Prints the first part of the final log information.
     * 
     * @throws RemoteException 
     */
    public void printFirst() throws RemoteException{
        out.println("                            Heist to the Museum - Description of the internal state");
        out.println();
        out.println("MstT    Thief 1    Thief 2     Thief 3     Thief 4     Thief 5     Thief 6			   Vck");
        out.println("Stat   Stat S MD  Stat S MD   Stat S MD   Stat S MD   Stat S MD   Stat S MD        0   1   2   3   4   5	6");
        out.println("                  Assault Party 1                    AssaultParty 2                            Museum");
        out.println("         Elem 1      Elem 2     Elem 3         Elem 1     Elem 2     Elem 3    Room 1  Room 2  Room 3  Room 4  Room 5");
        out.println("   RId  Id Pos Cv  Id Pos Cv   Id Pos Cv  RId Id Pos Cv  Id Pos Cv  Id Pos Cv   NP DT   NP DT   NP DT   NP DT   NP DT");
        
        writer.println("                            Heist to the Museum - Description of the internal state");
        writer.println();
        writer.println("MstT    Thief 1    Thief 2     Thief 3     Thief 4     Thief 5     Thief 6			   Vck");
        writer.println("Stat   Stat S MD  Stat S MD   Stat S MD   Stat S MD   Stat S MD   Stat S MD        0   1   2   3   4   5    6");
        writer.println("                  Assault Party 1                    AssaultParty 2                            Museum");
        writer.println("         Elem 1      Elem 2     Elem 3         Elem 1     Elem 2     Elem 3    Room 1  Room 2  Room 3  Room 4  Room 5");
        writer.println("   RId  Id Pos Cv  Id Pos Cv   Id Pos Cv  RId Id Pos Cv  Id Pos Cv  Id Pos Cv   NP DT   NP DT   NP DT   NP DT   NP DT");
    }
    
    /**
     * Prints the final part of the log information with the Assault results.
     * 
     */
    public void printItAllOut() throws RemoteException{ 
        //out.print("THE CAKE IS A LIE!");
        out.print(MTstate+"    ");
        writer.print(MTstate+"    ");
        for(int i = 0; i < Tstate.length; i++){
            out.print(Tstate[i]+" "+Tsituation[i]+" "+TmaxDisp[i]+"   ");
            writer.print(Tstate[i]+" "+Tsituation[i]+" "+TmaxDisp[i]+"   ");
        }
        for(int i = 0; i < vc.vc.length;i++){
          out.print(vc.vc[i]+"	");
          writer.print(vc.vc[i]+"	 ");
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
    public synchronized void printChange() throws RemoteException{
        out.print(MTstate+"    ");
        writer.print(MTstate+"    ");
        for(int i = 0; i < Tstate.length; i++){
            out.print(Tstate[i]+" "+Tsituation[i]+" "+TmaxDisp[i]+"      ");
            writer.print(Tstate[i]+" "+Tsituation[i]+" "+TmaxDisp[i]+"      ");
        }
        for(int i = 0; i < vc.vc.length;i++){
          out.print(vc.vc[i]+"   ");
          writer.print(vc.vc[i]+"   ");
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
    
    public synchronized Object[] addCanvasTotal(VectorClock vector) throws RemoteException{
        vc.update(vector);
        this.numCanvas += 1;
        obj[0] = vc.getCopy();
        return obj;
    }
    
    public synchronized Object[] remCanvasFromRoom(int room, int value, VectorClock vector) throws RemoteException{
        vc.update(vector);
        this.canvasRoom[room] = value;
        obj[0] = vc.getCopy();
        return obj;
    }
    
    public synchronized Object[] setMuseumRooms(int[] canvas, int[] dist, VectorClock vector) throws RemoteException{
        vc.update(vector);
        this.canvasRoom = canvas;
        this.distRoom = dist;
        obj[0] = vc.getCopy();
        return obj;
    }
    
    public synchronized Object[] setMTState(int state, VectorClock vector) throws RemoteException{
        vc.update(vector);
        this.MTstate = state;
        obj[0] = vc.getCopy();
        return obj;
    }
    
    public synchronized Object[] setTState(int id, int state, VectorClock vector) throws RemoteException{
        vc.update(vector);
        this.Tstate[id] = state;
        obj[0] = vc.getCopy();
        return obj;
    }
    
    public synchronized Object[] setTStateSituation(int id, int state, char sit, VectorClock vector) throws RemoteException
    {
        vc.update(vector);
        this.Tstate[id] = state;
        this.Tsituation[id] = sit;
        obj[0] = vc.getCopy();
        return obj;
    }
    
    public synchronized Object[] setTmaxDisp(int id, int mp, VectorClock vector) throws RemoteException{
        vc.update(vector);
        this.TmaxDisp[id] = mp;
        obj[0] = vc.getCopy();
        return obj;
    }
    
    public synchronized Object[] setPartyRoom(int party, int room, VectorClock vector) throws RemoteException{
        vc.update(vector);
        this.partyRoom[party] = room;
        obj[0] = vc.getCopy();
        return obj;
    }
    
    public synchronized Object[] setThiefPosition(int id, int position, VectorClock vector) throws RemoteException{
        vc.update(vector);
        this.partyPosition[id] = position;
        obj[0] = vc.getCopy();
        return obj;
    }
    
    public synchronized Object[] setTCanvasStatus(int id, int status, VectorClock vector) throws RemoteException{
        vc.update(vector);
        this.thiefCanvasStatus[id] = status;
        obj[0] = vc.getCopy();
        return obj;
    }
    
    public synchronized void terminate() throws RemoteException{
        MGeneralRepositoryClient.shutdown();
    }
}
