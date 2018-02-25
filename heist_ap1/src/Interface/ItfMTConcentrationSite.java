/**
 * heist_tp1 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency.
 */
package Interface;

/**
 * Interface that has the methods used by the thread MasterThief int the class ConcentrationSite.
 * 
 * @author Filipe and Luis
 */
public interface ItfMTConcentrationSite {
    void partyNeeded(int partyFree, boolean free);
    void stopT();
}
