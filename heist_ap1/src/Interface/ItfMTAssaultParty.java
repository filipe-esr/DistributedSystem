/**
 * heist_tp1 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency.
 */
package Interface;

/**
 * Interface that has the methods used by the thread MasterThief implemented in the class AssaultParty.
 * 
 * @author Filipe and Luis
 */
public interface ItfMTAssaultParty {
    void prepareAssaultParty(int room, int roomDist);
    void sendAssaultParty();
}
