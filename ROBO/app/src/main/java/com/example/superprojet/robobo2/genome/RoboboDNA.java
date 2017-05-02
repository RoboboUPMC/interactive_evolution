package com.example.superprojet.robobo2.genome;

import com.mytechia.robobo.framework.RoboboManager;

import java.util.ArrayList;

/**
 * Created by Quentin on 2017/04/05.
 */

public class RoboboDNA {
    // It's called genotype but actually also reflects the phenotype
    private ArrayList<RoboboGene> genotype = new ArrayList<>();
    public int selectionCounter = 0;

    public RoboboDNA(){

    }

    /**
     * Creates a RoboboDNA from the list made by crossover
     * @param child
     */
    public RoboboDNA(RoboboManager rm, ArrayList<Integer> child){

        for(Integer  mvmt : child){
            genotype.add(new RoboboGene(rm, RoboboGene.MvmtType.values()[mvmt]));
        }

    }

    public ArrayList<RoboboGene> getGenotype() {
        return genotype;
    }
}
