package Interfaces;

import Structures.VectorClock;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface that has the methods used by the thread Thief int the class Museum.
 * 
 * @author Filipe and Luis
 */
public interface ItfTMuseum extends Remote{
    Object[] rollACanvas(int room, VectorClock vector) throws RemoteException;
}
