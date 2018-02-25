package Interfaces;
import Structures.VectorClock;
import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * Interface that has the methods used by the thread Master Thief implemented in the class AssaultParty.
 * 
 * @author Filipe and Luis
 */
public interface ItfMTAssaultParty extends Remote{
    Object[] prepareAssaultParty(int room, int roomDist, VectorClock vector) throws RemoteException;
    Object[] sendAssaultParty(VectorClock vector) throws RemoteException;
    void terminate() throws RemoteException;
}
