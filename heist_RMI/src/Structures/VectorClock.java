package Structures;

import java.io.Serializable;
import java.util.Arrays;

/**
 * This class is used to represent a vector clock.
 * @author Filipe Rocha - 67432 and Luis Gameiro - 67989
 * 
 * Project for the course MIECT - SD - P3 G04
 */
public class VectorClock implements Serializable, Cloneable {
    private static final long serialVersionUID = 1001L;
    
    public int[] vc;
    private int idx;
    
    /**
     * Constructor to create an object of type VectorTimestamp
     * @param size size of the vector clock
     * @param idx local index of the vector clock
     */
    public VectorClock(int size, int idx) {
        this.idx = idx;
        this.vc = new int[size];
        
        for(int i = 0; i < size; i++)
            this.vc[i] = 0;
    }
    
    public VectorClock(int size) {
        this.idx = -1;
        this.vc = new int[size];
        
        for(int i = 0; i < size; i++)
            this.vc[i] = 0;
    }
    
    /**
     * Increments the local index declared on the constructor.
     */
    public synchronized void increment() {
        vc[idx]++;
    }
    
    /**
     * Updates the vector clock.
     * @param vector the Vector clock
     */
    public synchronized void update(VectorClock vector) {
        for (int i = 0; i < vc.length; i++) {
            vc[i] = Math.max(vector.vc[i], this.vc[i]);
        }
    }
    
    /**
     * Returns a deep copy of the object.
     * @return deep copy of the object
     */
    public synchronized VectorClock getCopy() {
        return this.clone();
    }
    
    /**
     * Returns the vector clock as an integer array.
     * @return integer array containing the vector clock
     */
    public synchronized int[] toIntArray() {
        return vc;
    }
    
    @Override
    public synchronized VectorClock clone() {
        VectorClock copy = null;        
        try { 
            copy = (VectorClock) super.clone ();
        } catch (CloneNotSupportedException e) {   
            System.err.println(Arrays.toString(e.getStackTrace()));
            System.exit(1);
        }
        copy.idx = idx;
        copy.vc = vc.clone();
        return copy;
    }
}

