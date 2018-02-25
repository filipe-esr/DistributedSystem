package Interfaces;

import Structures.VectorClock;
import java.rmi.RemoteException;

/**
 * Interface that has the methods used by the thread Thief in the class AssaultParty.
 * 
 * @author Filipe and Luis
 */
public interface ItfTAssaultParty {
    Object[] prepareExcursion(int id, VectorClock vector) throws RemoteException;
    Object[] crawlIn(int id, int maxDisplacement, VectorClock vector) throws RemoteException;
    Object[] reverseDirection(VectorClock vector) throws RemoteException;
    Object[] crawlOut(int id, int maxDisplacement, VectorClock vector) throws RemoteException; 
}
