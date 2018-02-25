/**
 * heist_tp1 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency.
 */
package Interface;

/**
 * Interface that has the methods used by the thread Thief in the class AssaultParty.
 * 
 * @author Filipe and Luis
 */
public interface ItfTAssaultParty {
    int prepareExcursion(int id);
    String crawlIn(int id, int maxDisplacement);
    String reverseDirection();
    String crawlOut(int id, int maxDisplacement);
}
