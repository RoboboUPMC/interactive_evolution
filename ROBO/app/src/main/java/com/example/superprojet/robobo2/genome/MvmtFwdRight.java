package com.example.superprojet.robobo2.genome;

import com.mytechia.robobo.rob.MoveMTMode;

/**
 * Created by Quentin on 2017/04/05.
 */

public class MvmtFwdRight extends RoboboMvmt {

    MvmtFwdRight(int left, int right, long duration) {
        super(left, right, duration);
        direction = MoveMTMode.FORWARD_FORWARD;
    }

    public MvmtFwdRight(){
        super(0, 80);
        direction = MoveMTMode.FORWARD_FORWARD;
    }

}
