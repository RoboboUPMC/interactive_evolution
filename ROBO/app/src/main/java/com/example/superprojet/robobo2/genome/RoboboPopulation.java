package com.example.superprojet.robobo2.genome;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Quentin on 2017/04/05.
 */

public class RoboboPopulation {

    ArrayList<RoboboDNA> pop = new ArrayList<>();

    public void xOver(){


    }

    public int distLevenshtein(RoboboDNA individu_1, RoboboDNA individu_2) {

        int long1 = individu_1.getGenotype().size();
        int long2 = individu_2.getGenotype().size();

        int[][] d = new int[long1 + 1][long2 + 1];
        int substitutionCost;
        int deletionCost = 1;
        int insertionCost = 1;

        for (int i = 0; i < d.length; i++) {
            d[i][0] = i;
        }

        for (int j = 0; j < d[0].length; j++) {
            d[0][j] = j;
        }

        for (int i = 0; i < d.length; i++) {
            for (int j = 0; j < d[0].length; j++) {
                if (individu_1.getGenotype().get(i).equals(individu_1.getGenotype().get(j))) {
                    substitutionCost = 0;
                } else {
                    substitutionCost = 1;
                }
                d[i][j] = Math.min(d[i - 1][j] + deletionCost, Math.min(d[i][j - 1] + insertionCost, d[i - 1][j - 1] + substitutionCost));
            }
        }

        return d[long1][long2];
    }

    private class Edge {
        RoboboGene g;

        public Edge(RoboboGene g, boolean b){
            this.g = g;
        }
    }


    public RoboboDNA choisirParent(){
        RoboboDNA parent = null;
        Random random = new Random();
        ArrayList<Float> proba = new ArrayList<Float>(this.pop.size());
        float sumProba = 0;
        float cumulSum = 0;

        for(int i=0; i<this.pop.size(); i++){
            float currProba = ( 1f / (this.pop.get(i).selectionCounter + 1) * this.pop.size());
            proba.set(i, currProba);
            sumProba += currProba;
        }

        for(int i=0; i<this.pop.size(); i++){
            float normalizedProba =  proba.get(i) * (1f / sumProba);
            cumulSum += normalizedProba;
            proba.set(i, cumulSum);
        }

        float r = random.nextFloat();
        for(int i=0; i<this.pop.size(); i++){
            if(proba.get(i)<r){
                parent= this.pop.get(i);
                break;
            }
        }
        parent.selectionCounter++;
        return parent;
    }


}
