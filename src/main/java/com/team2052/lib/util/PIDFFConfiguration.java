package com.team2052.lib.util;

public class PIDFFConfiguration {

    public final PIDConfiguration pid;
    public final FeedForwardConfiguration ff;

    public PIDFFConfiguration(PIDConfiguration pid, FeedForwardConfiguration ff) {
        this.pid = pid;
        this.ff = ff;
    }
}
