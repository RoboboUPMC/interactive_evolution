package com.example.superprojet.robobo2.genome;

import static com.example.superprojet.robobo2.genome.RoboboGene.MvmtType.FORWARD;
import static com.example.superprojet.robobo2.genome.RoboboGene.MvmtType.FORWARD_LEFT;
import static com.example.superprojet.robobo2.genome.RoboboGene.MvmtType.FORWARD_RIGHT;

/**
 * Created by meron on 03/05/2017.
 */

public class RoboboDNASim {

    public double vmax, braquage;
    private double alpha_c, alpha_f, beta_f;
    public double vitesse;
    public Vecteur position;
    public Vecteur direction;

    public RoboboDNASim(){
        this.vitesse = 0;
        this.vmax = 1;

        this.vmax = 0.9;
        this.alpha_c = 0.005;
        this.braquage = 0.1;
        this.alpha_f = 0.0002;
        this.beta_f = 0.0005;

        this.position = new Vecteur((double) 100, (double) 100);
        this.direction = new Vecteur((double) 0, (double) -1);

        //System.out.println("Le robobo est en" + this.position.toString());
    }

    public double[] getPosition(){
        double[] ret = {this.position.x, this.position.y};
        return ret;
    }

    public void execAtion(double[] action){
        direction = direction.rotation(action[1] * braquage);
        direction = direction.normaliser();
        this.vitesse -= alpha_f;
        this.vitesse -= beta_f*this.vitesse;
        this.vitesse += action[0] * alpha_c;
        this.vitesse = Math.min(vmax, this.vitesse);
        position = position.add(direction.multscalaire(this.vitesse));
    }

    public double[] mvmtToAction(RoboboGene.MvmtType mv){
        double[] action = {0,0};
        switch (mv){
            case FORWARD:
                action[0] = 10;
                action[1] = 0;
                break;
            case FORWARD_LEFT:
                action[0] = 10;
                action[1] = -Math.PI;
                break;
            case FORWARD_RIGHT:
                action[0] = 10;
                action[1] = Math.PI;
                break;
            case BACKWARDS:
                action[0] = -10;
                action[1] = 0;
                break;
            case BACKWARDS_LEFT:
                action[0] = -10;
                action[1] = -Math.PI;
                break;
            case BACKWARDS_RIGHT:
                action[0] = -10;
                action[1] = Math.PI;
                break;
            case TURN_LEFT:
                action[0] = 0;
                action[1] = -Math.PI;
                break;
            case TURN_RIGHT:
                action[0] = 0;
                action[1] = Math.PI;
                break;
            default:
                break;

        }
        return action;
    }

    private class Vecteur{
        public final double x;
        public final double y;

        public Vecteur(Vecteur cp){
            this.x = cp.x;
            this.y = cp.y;
        }

        public Vecteur(double x, double y) {
            this.x = x;
            this.y = y;
        }
        public Vecteur(){
            this.x = 0;
            this.y = 0;
        }
        public Vecteur(Vecteur u, Vecteur v){
            this.x = v.x - u.x;
            this.y = v.y - u.y;
        }

        public Vecteur clone(){
            return new Vecteur(this);
        }

        public double angle(Vecteur u){
            return  Math.signum(produitvectoriel(u)) *
                    Math.acos( produitScalaire(u) / (norme() * u.norme()));
        }

        public double produitScalaire(Vecteur u){
            return (this.x*u.x) + (this.y*u.y) ;
        }

        public Vecteur rotation(double angle){
            return new Vecteur(  this.x * Math.cos(angle) - this.y * Math.sin(angle) ,
                    this.y * Math.cos(angle) + this.x * Math.sin(angle));
        }

        public double produitvectoriel(Vecteur u){
            return this.x * u.y - this.y * u.x;
        }

        public double norme(){
            return Math.sqrt(this.x * this.x + this.y * this.y);
        }

        public Vecteur multscalaire(double scalaire){
            return new Vecteur(this.x * scalaire, this.y * scalaire);
        }

        public Vecteur normaliser(){
            return multscalaire(1/norme());
        }

        public Vecteur add(Vecteur u){
            return new Vecteur( this.x + u.x, this.y + u.y );
        }

        public Vecteur add(Vecteur v, Vecteur u){
            return v.add(u);
        }

        public Vecteur sub(Vecteur u){
            return new Vecteur(u, this);
        }



        public String toString() {
            return "Vecteur [x=" + this.x + ", y=" + this.y + "]";
        }


        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Vecteur other = (Vecteur) obj;
            if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
                return false;
            if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
                return false;
            return true;


        }
    }
}
