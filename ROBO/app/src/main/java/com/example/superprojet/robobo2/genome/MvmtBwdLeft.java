package com.example.superprojet.robobo2.genome;

import com.mytechia.robobo.rob.MoveMTMode;

/**
 * Created by Quentin on 2017/04/05.
 */

public class MvmtBwdLeft extends RoboboMvmt {

    MvmtBwdLeft(int left, int right, long duration) {
        super(left, right, duration);
        direction = MoveMTMode.REVERSE_REVERSE;
    }

    public MvmtBwdLeft(){
        super(80, 0);
        direction = MoveMTMode.REVERSE_REVERSE;
    }

}
