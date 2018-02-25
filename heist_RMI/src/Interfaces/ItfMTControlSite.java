package Interfaces;

import Structures.VectorClock;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface that has the methods used by the thread MasterThief int the class ControlSite.
 * 
 * @author Filipe and Luis
 */
public interface ItfMTControlSite extends Remote{
    Object[] startOperations(VectorClock vector) throws RemoteException;
    Object[] takeARest(VectorClock vector) throws RemoteException;
    Object[] sumUpResults(VectorClock vector) throws RemoteException;
    Object[] collectCanvas(VectorClock vector) throws RemoteException;
    void terminate() throws RemoteException;
}
