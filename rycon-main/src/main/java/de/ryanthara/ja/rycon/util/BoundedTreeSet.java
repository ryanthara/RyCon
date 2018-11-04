/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.util
 *
 * This package is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This package is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this package. If not, see <http://www.gnu.org/licenses/>.
 */
package de.ryanthara.ja.rycon.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This {@code TreeSet} has boundaries and ensures, that it never grows beyond a maximum size.
 *
 * <p>
 * The last element ist removed if the size of the {@code TreeSet} gets bigger than the maximum size.
 *
 * @author sebastian
 * @version 1
 * @see <a href="http://www.java2s.com/Code/Java/Collections-Data-Structure/ATreeSetthatensuresitnevergrowsbeyondamaxsize.htm">www.java2s.com</a>
 * @since 25
 */
public class BoundedTreeSet<E> extends TreeSet<E> {

    private int maxSize = Integer.MAX_VALUE;

    /**
     * Creates a new tree set with a given maximum size.
     *
     * @param maxSize the maximum size of the tree set
     */
    public BoundedTreeSet(int maxSize) {
        super();
        this.setMaxSize(maxSize);
    }

    /**
     * Creates a new tree set with a given maximum size and initializes it with a collection.
     *
     * @param maxSize    the maximum size of the tree set
     * @param collection the collection to be added
     */
    public BoundedTreeSet(int maxSize, Collection<? extends E> collection) {
        super(collection);
        this.setMaxSize(maxSize);
    }

    public BoundedTreeSet(int maxSize, Comparator<? super E> comparator) {
        super(comparator);
        this.setMaxSize(maxSize);
    }

    /**
     * Creates a new tree set with a given maximum size and initializes it with a sorted set.
     *
     * @param maxSize   the maximum size of the tree set
     * @param sortedSet the sorted set to be added
     */
    public BoundedTreeSet(int maxSize, SortedSet<E> sortedSet) {
        super(sortedSet);
        this.setMaxSize(maxSize);
    }

    /**
     * Adds an item to the tree set and adjust it afterwards to it maximum size.
     *
     * @param item item to be added
     * @return true if the item was added
     */
    public boolean add(E item) {
        boolean out = super.add(item);

        adjust();

        return out;
    }

    /**
     * Adds all elements of a collection to the tree set and adjust it afterwards to its maximum size.
     *
     * @param collection collection to be added
     * @return true if all elements were added
     */
    public boolean addAll(Collection<? extends E> collection) {
        boolean out = super.addAll(collection);

        adjust();

        return out;
    }

    /**
     * Returns the maximum size.
     *
     * @return maximum size
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Sets the maximum size and adjust the tree set afterwards to the maximum value.
     *
     * @param max the maximum size of the tree set
     */
    void setMaxSize(int max) {
        maxSize = max;

        adjust();
    }

    /**
     * Removes all the elements until the tree set has its maximum size.
     */
    private void adjust() {
        while (maxSize < size()) {
            remove(last());
        }
    }

}
