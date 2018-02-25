package ClientSide.Thief;

import Interfaces.ItfGenRep;
import Interfaces.ItfTMuseum;
import Interfaces.ItfTControlSite;
import Interfaces.ItfTAssaultParty;
import Interfaces.ItfTConcentrationSite;
import Structures.VectorClock;
import java.util.Random;
import java.rmi.RemoteException;

/**
 * Thread thief that is controlled by the thread Master Thief.
 * 
 *  @author Filipe Rocha - 67432 <p>Luis Gameiro - 67989</p>
 * 
 *  <p>Project for the course MIECT - SD - P3 G04</p>
 */
public class TThief extends Thread {
    
    private final int id;
    private final int party;
    private int roomToAttack;
    private String currentState;
    private boolean rolledCanvas;    //If thief has rolled a canvas
    private boolean canvasCollected; //If thief has canvas
    private int maximumDisplacement;
    private final ItfTControlSite TControlSite;
    private final ItfTMuseum TMuseum;
    private final ItfTAssaultParty TAssaultParty;
    private final ItfTConcentrationSite TConcentrationSite;
    private Random rMD;
    private boolean change = false;
    public ItfGenRep log;
    private VectorClock vc;
    private Object[] obj;
    
    /**
     * Initializes the thread Thief
     * 
     * @param id id of the thief
     * @param maxPartySeparation maximum distance that the threads can be from each other
     * @param party party that the thief belongs to
     * @param controlSite interface controlSite to access
     * @param museum interface museum to access
     * @param assaultParty interface assaultParty to access
     * @param concentrationSite interface concetrationSite to access
     * @param generalRepository interface where changes will be documented in a log
     * @param numThieves total number of thieves
     * @throws RemoteException 
     */
    public TThief(int id, int maxPartySeparation, int party, ItfTControlSite controlSite,ItfTMuseum museum,ItfTAssaultParty assaultParty,ItfTConcentrationSite concentrationSite,ItfGenRep generalRepository, int numThieves) throws RemoteException{
        this.id = id;
        this.party = party;
        this.roomToAttack = -1;
        this.rolledCanvas = false;
        this.canvasCollected=false;
        currentState = "Outside";
        this.TControlSite = controlSite;
        this.TMuseum = museum;
        this.TAssaultParty = assaultParty;
        this.TConcentrationSite = concentrationSite;
        rMD = new Random();
        maxPartySeparation+=1; //calibration of maximum distance (dependes on the size of the array)
        this.maximumDisplacement= rMD.nextInt(maxPartySeparation) + 1; //If maxSeparation is 3 it can move 5 distance units.
        this.log = generalRepository;
      	vc = new VectorClock(numThieves+1,id+1);  
      	obj = new Object[2];
        obj = log.setTStateSituation(id,1000,'W', vc.getCopy());
      	vc.update((VectorClock) obj[0]);
        obj = log.setTmaxDisp(id,this.maximumDisplacement, vc.getCopy());
      	vc.update((VectorClock) obj[0]);
    }
    
    /**
     * Executes the life cycle of the Thief thread. 
     * 4 states: Outside, CrawlingInwards, AtARoom and CrawlingOutwards.
     * 
     */
    @Override
    public void run() {
        boolean go = true;
        while(go){
            try{
                switch(currentState){
                    case "Outside":
                        if(rolledCanvas){
                            vc.increment();
                            obj = TControlSite.handCanvas(canvasCollected, id, party, vc.getCopy());
                            rolledCanvas = (boolean) obj[1];
                            vc.update((VectorClock) obj[0]);
                            canvasCollected = false;
                            vc.increment();
                            obj = log.setTStateSituation(id, 1000, 'W', vc.getCopy());
                            vc.update((VectorClock) obj[0]);
                            change = true;
                        }else{
                          	vc.increment();
                          	obj = TConcentrationSite.amINeeded(party, vc.getCopy());
                          	vc.update((VectorClock) obj[0]);
                                boolean liro = (boolean) obj[1];
                            if(liro){
                                vc.increment();
                              	obj = TAssaultParty.prepareExcursion(id, vc.getCopy());
                                roomToAttack = (int) obj[1];
                              	vc.update((VectorClock) obj[0]);
                              	currentState = "CrawlingInwards";
                              	vc.increment();
                                obj = log.setTStateSituation(id, 2000, 'P', vc.getCopy());
                                vc.update((VectorClock) obj[0]);
                              	change = true;
                            }else{
                                go = false;
                            }
                        }
                        break;
                    case "CrawlingInwards":
                        vc.increment();
                  			obj = TAssaultParty.crawlIn(id,maximumDisplacement, vc.getCopy());
                  			currentState = (String) obj[1];
                  			vc.update((VectorClock) obj[0]);
                        sleep(20);
                        if(currentState.equals("AtARoom")){
                            vc.increment();
                          	obj = log.setTState(id,3000, vc.getCopy());
                          	vc.update((VectorClock) obj[0]);
                            change = true;
                        }
                        break;
                    case "AtARoom":
                  			vc.increment();
                        obj = TMuseum.rollACanvas(roomToAttack, vc.getCopy());
                  			canvasCollected = (boolean) obj[1];
                  			vc.update((VectorClock) obj[0]);
                        rolledCanvas = true;
                  			vc.increment();
                        obj = TAssaultParty.reverseDirection(vc.getCopy());
                  			currentState = (String) obj[1];
                  			vc.update((VectorClock) obj[0]);
                        if(currentState.equals("CrawlingOutwards")){
                          	vc.increment();
                            obj = log.setTState(id,4000, vc.getCopy());
                          	vc.update((VectorClock) obj[0]);
                            change = true;
                        }
                        
                  			if(canvasCollected){
                          	vc.increment();
                            obj = log.setTCanvasStatus(id,1, vc.getCopy());
                  					vc.update((VectorClock) obj[0]);
                        }else{
                          	vc.increment();
                            obj = log.setTCanvasStatus(id,0, vc.getCopy());
                          	vc.update((VectorClock) obj[0]);
                        }
                        change = true;
                        break;
                    case "CrawlingOutwards":
                  			vc.increment();
                        obj = TAssaultParty.crawlOut(id,maximumDisplacement, vc.getCopy());
                  			currentState = (String) obj[1];
                  			vc.update((VectorClock) obj[0]);
                        sleep(20);
                        if(currentState.equals("Outside")){
                            vc.increment();
                          	obj = log.setTState(id,1000,vc.getCopy());
                            vc.update((VectorClock) obj[0]);
                            change = true;
                        }
                        break;
                }   
                if(change){
                   log.printChange();
                   change = false;
                }
            }catch(Exception e){
            }
        }
    }
}
