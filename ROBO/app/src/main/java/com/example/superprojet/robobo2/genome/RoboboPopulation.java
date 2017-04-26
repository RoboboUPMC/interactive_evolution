package com.example.superprojet.robobo2.genome;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Quentin on 2017/04/05.
 */

public class RoboboPopulation {

    ArrayList<RoboboDNA> pop = new ArrayList<>();

    public void xOver(){


    }

    private class Edge {
        RoboboGene g;

        public Edge(RoboboGene g, boolean b){
            this.g = g;
        }
    }


    public RoboboDNA[] choisirParents(){
        RoboboDNA[] parents = {null, null};
        //choix
        return parents;
    }

}
