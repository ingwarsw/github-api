package org.kohsuke.github;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Iterator over a pagenated data source.
 *
 * Aside from the normal iterator operation, this method exposes {@link #nextPage()}
 * that allows the caller to retrieve items per page.
 *
 * @author Kohsuke Kawaguchi
 */
public class PagedIterator<T> implements Iterator<T> {
    private final Iterator<T[]> base;

    /**
     * Current batch that we retrieved but haven't returned to the caller.
     */
    private T[] current;
    private int pos;

    public PagedIterator(Iterator<T[]> base) {
        this.base = base;
    }

    public boolean hasNext() {
        return (current!=null && pos<current.length) || base.hasNext();
    }

    public T next() {
        fetch();

        return current[pos++];
    }

    private void fetch() {
        while (current==null || current.length<=pos) {
            current = base.next();
            pos = 0;
        }
        // invariant at the end: there's some data to retrieve
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the next page worth of data.
     */
    public List<T> nextPage() {
        fetch();
        List<T> r = Arrays.asList(current);
        r = r.subList(pos,r.size());
        current = null;
        pos = 0;
        return r;
    }
}
