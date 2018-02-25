package Interfaces;

import Structures.VectorClock;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface that has the methods used by the thread MasterThief int the class ControlSite.
 * 
 * @author Filipe and Luis
 */
public interface ItfTConcentrationSite extends Remote{
    Object[] amINeeded(int party, VectorClock vector) throws RemoteException;
}
