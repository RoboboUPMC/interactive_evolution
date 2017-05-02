package com.example.superprojet.robobo2.genome;

import com.mytechia.robobo.rob.MoveMTMode;

/**
 * Created by Quentin on 2017/04/05.
 */

public class MvmtBwdRight extends RoboboMvmt {

    MvmtBwdRight(int left, int right, long duration) {
        super(left, right, duration);
        direction = MoveMTMode.REVERSE_REVERSE;
    }

    public MvmtBwdRight(){
        super(0, 80);
        direction = MoveMTMode.REVERSE_REVERSE;
    }

}
