package Interfaces;

import Structures.VectorClock;
import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * Interface that has the methods used by the thread MasterThief int the class Museum.
 * 
 * @author Filipe and Luis
 */
public interface ItfMTMuseum extends Remote{
    Object[] getDist(VectorClock vector) throws RemoteException;
    void terminate() throws RemoteException;
}
