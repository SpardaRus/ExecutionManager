package com.spardarus.context;

import java.util.concurrent.CopyOnWriteArrayList;

public interface ContextThread extends Context {

    void setTreads(CopyOnWriteArrayList<Thread> threads);

}
