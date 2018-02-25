package Interfaces;

import Structures.VectorClock;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface that has the methods used by the thread Thief int the class ControlSite.
 * 
 * @author Filipe and Luis
 */
public interface ItfTControlSite extends Remote{
    Object[] handCanvas(boolean collectedCanvas, int idThief, int party, VectorClock vector) throws RemoteException;
}
