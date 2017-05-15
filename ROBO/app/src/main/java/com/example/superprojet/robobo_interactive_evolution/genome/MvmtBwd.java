package com.example.superprojet.robobo_interactive_evolution.genome;

import com.mytechia.robobo.rob.MoveMTMode;

/**
 * Created by Quentin on 2017/04/05.
 */

public class MvmtBwd extends RoboboMvmt {



    MvmtBwd(int speed, long duration) {
        super(speed, speed, duration);
        direction = MoveMTMode.REVERSE_REVERSE;
    }

    public MvmtBwd(){
        super(80, 80);
        direction = MoveMTMode.REVERSE_REVERSE;
    }

}
