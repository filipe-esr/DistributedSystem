/**
 * heist_tp2 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency and sockets.
 */
package clientSide.Thief;

import comInf.GlobalInfo;
import genclass.GenericIO;
import java.util.ArrayList;

/**
 *
 * @author Filipe Rocha - 67432 and Luis Gameiro - 67989
 * 
 * Project for the course MIECT - SD - P3 G04
 */
public class ThiefExec {
    /**
     * ThiefExec main function to start the Thief client.
     * 
     * @param args no need
     */
    public static void main(String [] args) {
        ArrayList<TThief> thief = new ArrayList<>(GlobalInfo.nThieves);
        
        int k = -1;
        for(int i = 0; i < GlobalInfo.nThieves; i++){
            if (i % GlobalInfo.thievesPerParty == 0)
                k+=1;
            thief.add(new TThief(i,GlobalInfo.maxDisp,k));
        }
        
        for (TThief t : thief)
            t.start();

        for (TThief t : thief) {
            try {
                t.join ();
            } catch (InterruptedException e) {}
        }
        GenericIO.writelnString("Client (Thief) operations done!");
    }
}
