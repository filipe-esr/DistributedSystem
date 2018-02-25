/**
 * heist_tp2 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency and sockets.
 */
package clientSide.Thief;

import clientSide.ClientCom;
import comInf.Message;
import comInf.CommPorts;
import static java.lang.Thread.sleep;
import java.util.Random;
import genclass.GenericIO;

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
    private Random rMD;
    private boolean change = false;
    
    private String controlSiteName = CommPorts.controlSiteServerName;
    private int controlSitePort = CommPorts.controlSiteServerPort;
    private String assaultPartyName = null;
    private int assaultPartyPort;
    private String museumName = CommPorts.museumServerName;
    private int museumPort = CommPorts.museumServerPort;
    private String concentrationSiteName = CommPorts.concentrationSiteServerName;
    private int concentrationSitePort = CommPorts.concentrationSiteServerPort;
    private String logName = CommPorts.genRepServerName;
    private int logPort = CommPorts.genRepServerPort;
    
    /**
     * Initializes the thread Thief.
     * 
     * @param id id of the thief.
     * @param maxPartySeparation maximum distance that the threads can be from each other.
     * @param party party that the thief belongs to.
     */
    public TThief(int id,int maxPartySeparation, int party) {
        this.id = id;
        this.party = party;
        this.roomToAttack = -1;
        this.rolledCanvas = false;
        this.canvasCollected=false;
        currentState = "Outside";
        rMD = new Random();
        maxPartySeparation+=1; //calibration of maximum distance (dependes on the size of the array)
        this.maximumDisplacement= rMD.nextInt(maxPartySeparation) + 1; //If maxSeparation is 3 it can move 5 distance units.
        assaultPartyName = CommPorts.assaultPartyServerName[party];
        assaultPartyPort = CommPorts.assaultPartyServerPort[party];
        LOGsetTStateSituation(id,1000,'W');
        LOGsetTmaxDisp(id,this.maximumDisplacement);
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
                            rolledCanvas = controlSiteHandCanvas(canvasCollected, id, party);
                            canvasCollected = false;
                            LOGsetTCanvasStatus(id,0);
                            LOGsetTStateSituation(id, 1000, 'W');
                            change = true;
                        }else{
                            if(concentrationSiteAmINeeded(party)){
                                roomToAttack = assaultPartyPrepareExcursion(id);
                                currentState = "CrawlingInwards";
                                LOGsetTStateSituation(id, 2000, 'P');
                                change = true;
                            }else{
                                go = false;
                            }
                        }
                        break;
                    case "CrawlingInwards":
                        currentState = assaultPartyCrawlIn(id, maximumDisplacement);
                        sleep(20);
                        if(currentState.equals("AtARoom")){
                            LOGsetTState(id, 3000);
                            change = true;
                        }
                        break;
                    case "AtARoom":
                        canvasCollected = museumRollCanvas(roomToAttack);
                        rolledCanvas = true;
                        
                        if(canvasCollected)
                            LOGsetTCanvasStatus(id,1);
                        else
                            LOGsetTCanvasStatus(id,0);
                        
                        currentState = assaultPartyReverseDirection();
                        if(currentState.equals("CrawlingOutwards")){
                            LOGsetTState(id,4000);
                            change = true;
                        }
                        change = true;
                        break;
                    case "CrawlingOutwards":
                        currentState = assaultPartyCrawlOut(id,maximumDisplacement);
                        sleep(20);
                        if(currentState.equals("Outside")){
                            LOGsetTState(id,1000);
                            change = true;
                        }
                        break;
                }   
                if(change){
                   LOGprint("Change");
                   change = false;
                }
            }catch(Exception e){
            }
        }
    }
    
    /**
     * Set the Theif situation in the log.
     * 
     * @param id
     * @param state
     * @param c 
     */
    private void LOGsetTStateSituation(int id, int state, char c){
        ClientCom con = new ClientCom (logName, logPort);
        Message inMessage, outMessage;

        while (!con.open ())    // aguarda ligação
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQSETTSITUATION, id, state, c);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.ACK))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
      }
    
    /**
     * Set the thief maximum displacement in the log
     * 
     * @param id
     * @param maxDisp 
     */
    private void LOGsetTmaxDisp(int id, int maxDisp){
        ClientCom con = new ClientCom (logName, logPort);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQSETTMAXDISP, id, maxDisp);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.ACK))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
      }
    
    /**
     * Set the thief state in the log.
     * 
     * @param id
     * @param state 
     */
    private void LOGsetTState(int id, int state){
        ClientCom con = new ClientCom (logName, logPort);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQSETTSTATE, id, state);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.ACK))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
      }
    
    /**
     * Set the thief canvas status in the log.
     * 
     * @param id
     * @param status 
     */
    private void LOGsetTCanvasStatus(int id, int status){
        ClientCom con = new ClientCom (logName, logPort);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQSETTCANVASSTATUS, id, status);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.ACK))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
      }
    
    /**
     * Print txt in the log.
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
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
      }
    
    /**
     * Hands canvas in the control site.
     * 
     * @param canvasCollected
     * @param id
     * @param party
     * @return 
     */
    private boolean controlSiteHandCanvas(boolean canvasCollected, int id, int party){
        ClientCom con = new ClientCom (controlSiteName, controlSitePort);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQHANDACANVAS, canvasCollected, id, party);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.RESPHANDACANVAS))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
        return inMessage.getRolledCanvas();
      }
    
    /**
     * Returns if thief is needed.
     * 
     * @param party
     * @return 
     */
    private boolean concentrationSiteAmINeeded(int party){
        ClientCom con = new ClientCom (concentrationSiteName, concentrationSitePort);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQAMINEEDED, party);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.RESPAMINEEDED))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
        return inMessage.getIsNeeded();
      }
    
    /**
     * Assault Party prepares excursion.
     * 
     * @param id
     * @return 
     */
    private int assaultPartyPrepareExcursion(int id){
        ClientCom con = new ClientCom (assaultPartyName, assaultPartyPort);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQPREPAREEXCURSION, id);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.RESPPREPAREEXCURSION))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
        return inMessage.getRoom();
      }
    
    /**
     * Assault party crawls in.
     * 
     * @param id
     * @param maxDisp
     * @return 
     */
    private String assaultPartyCrawlIn(int id, int maxDisp){
        ClientCom con = new ClientCom (assaultPartyName, assaultPartyPort);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQCRAWLIN, id, maxDisp);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.RESPCRAWLIN))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
        return inMessage.getThiefState();
      }
    
    /**
     * Rolls a canvas in the museum.
     * 
     * @param room
     * @return 
     */
    private boolean museumRollCanvas(int room){
        ClientCom con = new ClientCom (museumName, museumPort);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQROLLACANVAS, room);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.RESPROLLACANVAS))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
        return inMessage.getCanvasCollected();
      }
    
    /**
     * Assault party reverses direction.
     * 
     * @return 
     */
    private String assaultPartyReverseDirection(){
        ClientCom con = new ClientCom (assaultPartyName, assaultPartyPort);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQREVERSEDIRECTION);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.RESPREVERSEDIRECTION))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
        return inMessage.getThiefState();
      }
    
    /**
     * Assault party crawls out.
     * 
     * @param id
     * @param maxDisp
     * @return 
     */
    private String assaultPartyCrawlOut(int id, int maxDisp){
        ClientCom con = new ClientCom (assaultPartyName, assaultPartyPort);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQCRAWLOUT, id, maxDisp);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.RESPCRAWLOUT))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
        return inMessage.getThiefState();
      }
}
