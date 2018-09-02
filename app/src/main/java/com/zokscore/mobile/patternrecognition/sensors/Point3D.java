package com.zokscore.mobile.patternrecognition.sensors;

/**
 * Created by pafgoncalves on 22-03-2018.
 */

public class Point3D {

    private Double x=null;
    private Double y=null;
    private Double z=null;

    private int size = 3;

    public Point3D() {
    }

    public Point3D(int size) {
        if( size>0 && size<=3 ) {
            this.size = size;
        }
    }

    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return x==null?0:x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y==null?0:y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getZ() {
        return z==null?0:z;
    }

    public String toCSV() {
        return toCSV(false);
    }

    public String toCSV(boolean last) {
        StringBuilder sb = new StringBuilder();
        sb.append(x==null?"":x);
        if( size>1 ) {
            sb.append(",");
            sb.append(y==null?"":y);
        }
        if( size>2 ) {
            sb.append(",");
            sb.append(z==null?"":z);
        }
        if(!last) {
            sb.append(",");
        }
        return sb.toString();
    }

}
