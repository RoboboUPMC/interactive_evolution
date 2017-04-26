package com.example.superprojet.robobo2.genome;

import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.rob.MoveMTMode;

import java.util.Random;

/**
 * Created by Quentin on 2017/04/04.
 */

abstract class RoboboMvmt {


    long duration;
    int leftVelocity;
    int rightVelocity;

    MoveMTMode direction;

    RoboboGene.MvmtType mvmtType;

    RoboboMvmt(int left, int right, long duration){
        this.leftVelocity = left;
        this.rightVelocity = right;
        this.duration = duration;
    }


    public MoveMTMode getDirection() {
        return direction;
    }

    public int getLeftVelocity() {
        return leftVelocity;
    }

    public int getRightVelocity() {
        return rightVelocity;
    }

    public long getDuration() {
        return duration;
    }
}
