package com.perqin.gandear;

/**
 * Author   : perqin
 * Date     : 17-4-13
 */

public class QueryHelper {
    private String mQuery = "";
    private QueryChangeListener mListener;

    public void setListener(QueryChangeListener listener) {
        mListener = listener;
    }

    public void clearQuery() {
        setQuery("");
    }

    public void appendToQuery(String s) {
        setQuery(mQuery + s);
    }

    public String getQuery() {
        return mQuery;
    }

    public void setQuery(String s) {
        if (s == null) s = "";
        if (mQuery.equals(s)) return;
        String old = mQuery;
        mQuery = s;
        if (mListener != null) {
            mListener.onQueryChange(old, mQuery);
        }
    }

    public interface QueryChangeListener {
        void onQueryChange(String oldQuery, String newQuery);
    }
}
