package Interfaces;
import Structures.VectorClock;
import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 *
 * @author gamel
 */
public interface ItfGenRep extends Remote{
    void printFirst() throws RemoteException;
    void printItAllOut() throws RemoteException;
    void printChange() throws RemoteException;
    Object[] addCanvasTotal(VectorClock vector) throws RemoteException;
    Object[] remCanvasFromRoom(int room, int value, VectorClock vector) throws RemoteException;
    Object[] setMuseumRooms(int[] canvas, int[] dist, VectorClock vector) throws RemoteException;
    Object[] setMTState(int state, VectorClock vector) throws RemoteException;
    Object[] setTState(int id, int state, VectorClock vector) throws RemoteException;
    Object[] setTStateSituation(int id, int state, char sit, VectorClock vector) throws RemoteException;
    Object[] setTmaxDisp(int id, int mp, VectorClock vector) throws RemoteException;
    Object[] setPartyRoom(int party, int room, VectorClock vector) throws RemoteException;
    Object[] setThiefPosition(int id, int position, VectorClock vector) throws RemoteException;
    Object[] setTCanvasStatus(int id, int status, VectorClock vector) throws RemoteException;
    void terminate() throws RemoteException;
}
