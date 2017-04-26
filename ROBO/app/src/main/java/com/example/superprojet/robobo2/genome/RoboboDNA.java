package com.example.superprojet.robobo2.genome;

import java.util.ArrayList;

/**
 * Created by Quentin on 2017/04/05.
 */

public class RoboboDNA {
    // It's called genotype but actually also reflects the phenotype
    private ArrayList<RoboboGene> genotype = new ArrayList<>();

    public RoboboDNA(){

    }

    public ArrayList<RoboboGene> getGenotype() {
        return genotype;
    }
}
