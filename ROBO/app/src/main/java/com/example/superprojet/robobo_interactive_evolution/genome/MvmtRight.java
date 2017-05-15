package com.example.superprojet.robobo_interactive_evolution.genome;

import com.mytechia.robobo.rob.MoveMTMode;

/**
 * Created by Quentin on 2017/04/05.
 */

public class MvmtRight extends RoboboMvmt {



    MvmtRight(int speed, long duration) {
        super(speed, speed, duration);
        direction = MoveMTMode.FORWARD_REVERSE;
    }

    public MvmtRight(){
        super(80, 80);
        direction = MoveMTMode.FORWARD_REVERSE;
    }

}
