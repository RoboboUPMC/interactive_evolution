package com.example.superprojet.robobo2.genome;

import com.mytechia.robobo.rob.MoveMTMode;

/**
 * Created by Quentin on 2017/04/05.
 */

public class MvmtFwd extends RoboboMvmt {



    MvmtFwd(int speed, long duration) {
        super(speed, speed, duration);
        direction = MoveMTMode.FORWARD_FORWARD;
    }

    public MvmtFwd(){
        super(80, 80);
        direction = MoveMTMode.FORWARD_FORWARD;
    }

}
