package com.example.superprojet.robobo2.genome;

import com.mytechia.robobo.framework.RoboboManager;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Quentin on 2017/04/05.
 */

public class RoboboDNA {
    // It's called genotype but actually also reflects the phenotype
    
    RoboboManager roboboManager;
    private ArrayList<RoboboGene> genotype = new ArrayList<>();
    public int selectionCounter = 0;

    public RoboboDNA(RoboboManager roboboManager){
        this.roboboManager = roboboManager;

    }

    public void mutate(){
        int currSize = this.genotype.size();
        int idealSize = 10;
        ArrayList<RoboboGene> newGenotype = new ArrayList<RoboboGene>();
        float mutaProba = 1f/currSize;
        float lossProba = Math.min(1f, currSize/(2*idealSize));
        float r = 0;
        Random random = new Random();
        RoboboGene currGene = null;

        for(int i = 0; i<currSize; i++){
            r = random.nextFloat();
            if(r>=lossProba){
                currGene = this.getGenotype().get(i);
                currGene = currGene.mutate();
                newGenotype.add(currGene);
            }
        }

        r = random.nextFloat();
        if(r >= lossProba){
            newGenotype.add(new RoboboGene(this.roboboManager, RoboboGene.MvmtType.FORWARD).mutate());
        }
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
