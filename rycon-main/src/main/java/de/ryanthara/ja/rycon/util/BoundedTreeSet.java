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

    public BoundedTreeSet(int maxSize) {
        super();
        this.setMaxSize(maxSize);
    }

    public BoundedTreeSet(int maxSize, Collection<? extends E> c) {
        super(c);
        this.setMaxSize(maxSize);
    }

    public BoundedTreeSet(int maxSize, Comparator<? super E> c) {
        super(c);
        this.setMaxSize(maxSize);
    }

    public BoundedTreeSet(int maxSize, SortedSet<E> s) {
        super(s);
        this.setMaxSize(maxSize);
    }

    public boolean add(E item) {
        boolean out = super.add(item);
        adjust();
        return out;
    }

    public boolean addAll(Collection<? extends E> c) {
        boolean out = super.addAll(c);
        adjust();
        return out;
    }

    public int getMaxSize() {
        return maxSize;
    }

    void setMaxSize(int max) {
        maxSize = max;
        adjust();
    }

    private void adjust() {
        while (maxSize < size()) {
            remove(last());
        }
    }

} // end of BoundedTreeSet
