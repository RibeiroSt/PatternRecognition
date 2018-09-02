package com.zokscore.mobile.patternrecognition.sensors;

/**
 * Created by pafgoncalves on 14-04-2018.
 */

public class Point6D {

    private Double a=null;
    private Double b=null;
    private Double c=null;
    private Double d=null;
    private Double e=null;
    private Double f=null;

    private int size = 6;

    public Point6D() {
    }

    public Point6D(int size) {
        if( size>0 && size<=6 ) {
            this.size = size;
        }
    }

    public Point6D(double a, double b, double c, double d, double e, double f) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getA() {
        return a==null?0:a;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getB() {
        return b==null?0:b;
    }

    public void setC(double c) {
        this.c = c;
    }

    public double getC() {
        return c==null?0:c;
    }

    public void setD(double d) {
        this.d = d;
    }

    public double getD() {
        return d==null?0:d;
    }

    public void setE(double e) {
        this.e = e;
    }

    public double getE() {
        return e==null?0:e;
    }

    public void setF(double f) {
        this.f = f;
    }

    public double getF() {
        return f==null?0:f;
    }

    public String toCSV() {
        return toCSV(false);
    }

    public String toCSV(boolean last) {
        StringBuilder sb = new StringBuilder();
        sb.append(a==null?"":a);
        if( size>1 ) {
            sb.append(",");
            sb.append(b==null?"":b);
        }
        if( size>2 ) {
            sb.append(",");
            sb.append(c==null?"":c);
        }
        if( size>3 ) {
            sb.append(",");
            sb.append(d==null?"":d);
        }
        if( size>4 ) {
            sb.append(",");
            sb.append(e==null?"":e);
        }
        if( size>5 ) {
            sb.append(",");
            sb.append(f==null?"":f);
        }
        if(!last) {
            sb.append(",");
        }
        return sb.toString();
    }
}
