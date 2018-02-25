package Interfaces;

import Structures.VectorClock;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface that has the methods used by the thread MasterThief int the class ControlSite.
 * 
 * @author Filipe and Luis
 */
public interface ItfMTConcentrationSite extends Remote{
    Object[] partyNeeded(int partyFree, boolean free, VectorClock vector) throws RemoteException;
    Object[] stopT(VectorClock Vector) throws RemoteException;
    void terminate() throws RemoteException;
}
