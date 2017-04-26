package com.example.superprojet.robobo2.genome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Quentin on 2017/04/13.
 */

public class DummyPopulation {

    ArrayList<RoboboDNA> pop = new ArrayList<>();

    public void xOver(RoboboDNA p1, RoboboDNA p2){

        // Class : movement type Integer[] : max number of adjacencies, mean number of occurrences
        HashMap<Class, Float[]> maxOcc = new HashMap<>(8);
        ArrayList<Float> mean = new ArrayList<>(8);
        maxOcc.put(MvmtFwd.class, new Float[]{0f, 0f});
        maxOcc.put(MvmtBwd.class, new Float[]{0f, 0f});
        maxOcc.put(MvmtLeft.class, new Float[]{0f, 0f});
        maxOcc.put(MvmtRight.class, new Float[]{0f, 0f});
        maxOcc.put(MvmtFwdLeft.class, new Float[]{0f, 0f});
        maxOcc.put(MvmtFwdRight.class, new Float[]{0f, 0f});
        maxOcc.put(MvmtBwdLeft.class, new Float[]{0f, 0f});
        maxOcc.put(MvmtBwdRight.class, new Float[]{0f, 0f});



        int a = RoboboGene.MvmtType.FORWARD.ordinal();
        int b = RoboboGene.MvmtType.BACKWARDS.ordinal();
        int c = RoboboGene.MvmtType.TURN_LEFT.ordinal();
        int d = RoboboGene.MvmtType.TURN_RIGHT.ordinal();
        int e = RoboboGene.MvmtType.FORWARD_LEFT.ordinal();
        int f = RoboboGene.MvmtType.FORWARD_RIGHT.ordinal();
        int g = RoboboGene.MvmtType.BACKWARDS_LEFT.ordinal();
        int h = RoboboGene.MvmtType.BACKWARDS_RIGHT.ordinal();




        for(RoboboGene mvmt : p1.getGenotype()){
            switch (mvmt.getMvmtType()) {

                case FORWARD:
                    mean.set(a, .5f);
                    break;

                case BACKWARDS:
                    mean.set(b, .5f);
                    break;

                case TURN_LEFT:
                    mean.set(c, .5f);
                    break;

                case TURN_RIGHT:
                    mean.set(d, .5f);
                    break;

                case FORWARD_LEFT:
                    mean.set(e, .5f);
                    break;

                case FORWARD_RIGHT:
                    mean.set(f, .5f);
                    break;

                case BACKWARDS_LEFT:
                    mean.set(g, .5f);
                    break;

                case BACKWARDS_RIGHT:
                    mean.set(h, .5f);
                    break;

            }
        }
        for(RoboboGene mvmt : p2.getGenotype()){
            switch (mvmt.getMvmtType()) {

                case FORWARD:
                    mean.set(a, .5f);
                    break;

                case BACKWARDS:
                    mean.set(b, .5f);
                    break;

                case TURN_LEFT:
                    mean.set(c, .5f);
                    break;

                case TURN_RIGHT:
                    mean.set(d, .5f);
                    break;

                case FORWARD_LEFT:
                    mean.set(e, .5f);
                    break;

                case FORWARD_RIGHT:
                    mean.set(f, .5f);
                    break;

                case BACKWARDS_LEFT:
                    mean.set(g, .5f);
                    break;

                case BACKWARDS_RIGHT:
                    mean.set(h, .5f);
                    break;

            }
        }

        ArrayList<Integer> nbOcc = new ArrayList<>(8);

        Integer[][] adjaTable = new Integer[8][8];
        for(int i = 0 ; i < 8 ; i++){
            for(int j = 0 ; j < 8 ; i++){
                adjaTable[i][j] = 0;
            }
        }
        Float max = Collections.max(mean);

        while(max > 0){
            ArrayList<Integer> indexes = new ArrayList<>();
            int i;
            for(i = 0 ; i < mean.size() ; i++){
                if(mean.get(i) >= max){
                    indexes.add(i);
                    mean.set(i, max-1);
                }
            }
            Random r = new Random();
            i = indexes.get(r.nextInt(indexes.size()));
            nbOcc.set(i, nbOcc.get(i)==null?1:nbOcc.get(i)+1);
            max = Collections.max(mean);
        }


        for(int i = 0 ; i < p1.getGenotype().size() ; i++){
            adjaTable[p1.getGenotype().get(i).getMvmtType().ordinal()][p1.getGenotype().get((i+1) % p1.getGenotype().size()).getMvmtType().ordinal()] +=1;
            adjaTable[p1.getGenotype().get(i).getMvmtType().ordinal()][p1.getGenotype().get((i-1) % p1.getGenotype().size()).getMvmtType().ordinal()] +=1;
        }
        for(int i = 0 ; i < p2.getGenotype().size() ; i++){
            adjaTable[p1.getGenotype().get(i).getMvmtType().ordinal()][p1.getGenotype().get((i+1) % p1.getGenotype().size()).getMvmtType().ordinal()] +=1;
            adjaTable[p1.getGenotype().get(i).getMvmtType().ordinal()][p1.getGenotype().get((i-1) % p1.getGenotype().size()).getMvmtType().ordinal()] +=1;
        }
        for(int i = 0 ; i < nbOcc.size() ; i++){
            if(nbOcc.get(i) == null){
                for(int j = 0 ; j < 8 ; j++){
                    adjaTable[i][j] = 0;
                }
            }
        }

        //
        //A faire : utiliser le tableau d'adjacence pour générer l'enfant
        //

    }

    private class Edge {
        RoboboGene g;

        public Edge(){
            this.g = g;
        }
    }


    public RoboboDNA[] choisirParents(){
        RoboboDNA[] parents = {null, null};
        //choix
        return parents;
    }
}
