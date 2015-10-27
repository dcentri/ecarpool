package com.dicentrix.ecarpool.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Akash on 10/3/2015.
 */
public class Tree<T> {
    private T t;
    private int level;
    private ArrayList<T> values = new ArrayList<>();

    public void set(T t) { this.t = t; }
    public T get() { return t; }


}
