package com.spardarus.manager;

import com.spardarus.context.Context;

public interface ExecutionManager {

    Context execute(Runnable... tasks);

}
