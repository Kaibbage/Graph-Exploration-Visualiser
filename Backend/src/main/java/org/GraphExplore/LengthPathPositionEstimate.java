package org.GraphExplore;

import java.util.ArrayList;
import java.util.List;

public class LengthPathPositionEstimate {
    int length;
    List<int[]> path; //0 is r, 1 is c
    int r;
    int c;
    int estimate;

    public LengthPathPositionEstimate(int length, List<int[]> path, int r, int c, int estimate){
        this.length = length;
        this.path = new ArrayList<>(path);
        this.path.add(new int[]{r, c});

        this.r = r;
        this.c = c;
        this.estimate = estimate;
    }
}
