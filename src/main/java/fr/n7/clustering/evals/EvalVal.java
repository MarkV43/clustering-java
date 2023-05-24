package fr.n7.clustering.evals;

public class EvalVal {
    public double value;
    public Unit hunit;

    //UEs if I do like a mean of users
    enum Unit {PERCENTAGE, UEs, KBPS, NOUNIT};


    public EvalVal(double val, Unit h) {
        this.value= val;
        this.hunit = h;
    }
    public double getValue() {return value;}

    public Unit getUnit() {return hunit;}

}
