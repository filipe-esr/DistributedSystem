/**
 * heist_tp1 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency.
 */
package Interface;

/**
 * Interface that has the methods used by the thread Thief int the class ConcentrationSite.
 * 
 * @author Filipe and Luis
 */
public interface ItfTConcentrationSite {
    boolean amINeeded(int party);
}
