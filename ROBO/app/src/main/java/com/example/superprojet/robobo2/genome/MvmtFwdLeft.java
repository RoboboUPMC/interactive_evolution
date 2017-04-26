package com.example.superprojet.robobo2.genome;

import com.mytechia.robobo.rob.MoveMTMode;

/**
 * Created by Quentin on 2017/04/05.
 */

public class MvmtFwdLeft extends RoboboMvmt {



    MvmtFwdLeft(int left, int right, long duration) {
        super(left, right, duration);
        direction = MoveMTMode.FORWARD_FORWARD;
    }

    public MvmtFwdLeft(){
        super(80, 0, 200L);
        direction = MoveMTMode.FORWARD_FORWARD;
    }
}
