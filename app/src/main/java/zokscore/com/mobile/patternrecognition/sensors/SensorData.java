package zokscore.com.mobile.patternrecognition.sensors;

import android.content.Context;

import java.util.Calendar;

/**
 * Created by pafgoncalves on 22-03-2018.
 */

public class SensorData {

    private Point3D acc = new Point3D();
    private boolean hasAcc = false;

    private Point3D magneticField = new Point3D();
    private boolean hasMagneticField = false;

    private Point3D orientation = new Point3D();
    private boolean hasOrientation = false;

    private Point3D gyroscope = new Point3D();
    private boolean hasGyroscope = false;

    private Point3D light = new Point3D();
    private boolean hasLight = false;

    private Point3D proximity = new Point3D();
    private boolean hasProximity = false;

    private Point3D grav = new Point3D();
    private boolean hasGrav = false;

    private Float stepDetector = null;
    private boolean hasStepDetector = false;

    private Point3D linearAcceleration = new Point3D();
    private boolean hasLinearAcceleration = false;

    private Point6D rotationVector = new Point6D(5); //tem 5 eixos
    private boolean hasRotationVector = false;

    private Point6D magneticFieldUncalibrated = new Point6D(); //tem 6 eixos
    private boolean hasMagneticFieldUncalibrated = false;

    private Point6D gameRotationVector = new Point6D(4); //tem 4 eixos
    private boolean hasGameRotationVector = false;

    private Point6D gyroscopeUncalibrated = new Point6D(); //tem 6 eixos
    private boolean hasGyroscopeUncalibrated = false;

    private Float significantMotion = null;    //TODO: has never been occurred, so we do not know how many axes
    private boolean hasSignificantMotion = false;

    private Float stepCounter = null;
    private boolean hasStepCounter = false;

    private Float tiltDetector = null;
    private boolean hasTiltDetector = false;

    private long timestamp;
    private StringBuilder stringBuilder;

    public Point3D getMagneticFieldData() {
        return magneticField;
    }

    private Context context = null;


    public void setMagneticFieldData(double x, double y, double z) {
        magneticField.setX(x);
        magneticField.setY(y);
        magneticField.setZ(z);
        hasMagneticField = true;
    }

    public Point3D getOrientationData() {
        return orientation;
    }

    public void setOrientationData(double x, double y, double z) {
        orientation.setX(x);
        orientation.setY(y);
        orientation.setZ(z);
        hasOrientation = true;
    }

    public Point3D getGyroscopeData() {
        return gyroscope;
    }

    public void setGyroscopeData(double x, double y, double z) {
        gyroscope.setX(x);
        gyroscope.setY(y);
        gyroscope.setZ(z);
        hasGyroscope = true;
    }

    public Point3D getProximityData() {
        return proximity;
    }

    public void setProximityData(double x, double y, double z) {
        proximity.setX(x);
        proximity.setY(y);
        proximity.setZ(z);
        hasProximity = true;
    }

    public Point3D getLinearAccelerationData() {
        return linearAcceleration;
    }

    public void setLinearAccelerationData(double x, double y, double z) {
        linearAcceleration.setX(x);
        linearAcceleration.setY(y);
        linearAcceleration.setZ(z);
        hasLinearAcceleration = true;
    }

    public Point6D getRotationVectorData() {
        return rotationVector;
    }

    public void setRotationVectorData(double a, double b, double c, double d, double e) {
        rotationVector.setA(a);
        rotationVector.setB(b);
        rotationVector.setC(c);
        rotationVector.setD(d);
        rotationVector.setE(e);
        hasRotationVector = true;
    }

    public Point6D getMagneticFieldUncalibratedData() {
        return magneticFieldUncalibrated;
    }

    public void setMagneticFieldUncalibratedData(double a, double b, double c, double d, double e, double f) {
        magneticFieldUncalibrated.setA(a);
        magneticFieldUncalibrated.setB(b);
        magneticFieldUncalibrated.setC(c);
        magneticFieldUncalibrated.setD(d);
        magneticFieldUncalibrated.setE(e);
        magneticFieldUncalibrated.setF(f);
        hasMagneticFieldUncalibrated = true;
    }

    public Point6D getGameRotationVectorData() {
        return gameRotationVector;
    }

    public void setGameRotationVectorData(double a, double b, double c, double d) {
        gameRotationVector.setA(a);
        gameRotationVector.setB(b);
        gameRotationVector.setC(c);
        gameRotationVector.setD(d);
        hasGameRotationVector = true;
    }

    public Point6D getGyroscopeUncalibratedData() {
        return gyroscopeUncalibrated;
    }

    public void setGyroscopeUncalibratedData(double a, double b, double c, double d, double e, double f) {
        gyroscopeUncalibrated.setA(a);
        gyroscopeUncalibrated.setB(b);
        gyroscopeUncalibrated.setC(c);
        gyroscopeUncalibrated.setD(d);
        gyroscopeUncalibrated.setE(e);
        gyroscopeUncalibrated.setF(f);
        hasGyroscopeUncalibrated = true;
    }

    public float getSignificantMotionData() {
        return significantMotion==null?0:significantMotion;
    }

    public void setSignificantMotionData(float significantMotion) {
        this.significantMotion = significantMotion;
        hasSignificantMotion = true;
    }

    public float getStepCounterData() {
        return stepCounter==null?0:stepCounter;
    }

    public void setStepCounterData(float stepCounter) {

        this.stepCounter = stepCounter;
        hasStepCounter = true;
    }

    public float getTiltDetectorData() {
        return tiltDetector==null?0:tiltDetector;
    }

    public void setTiltDetectorData(float tiltDetector) {

        this.tiltDetector = tiltDetector;
        hasTiltDetector = true;
    }

    public SensorData(Context context) {

        this.context = context;
    }

    public void setAccelerometerData(double x, double y, double z) {
        acc.setX(x);
        acc.setY(y);
        acc.setZ(z);
        hasAcc = true;
    }

    public Point3D getAccelerometerData() {
        return acc;
    }

    public void setGravityData(double x, double y, double z) {
        grav.setX(x);
        grav.setY(y);
        grav.setZ(z);
        hasGrav = true;
    }

    public Point3D getGravityData() {
        return grav;
    }

    public void setStepDetectorData(float v) {
        stepDetector = v;
        hasStepDetector = true;
    }

    public float getStepDetectorData() {
        return stepDetector==null?0:stepDetector;
    }

    public void setLightData(double x, double y, double z) {
        light.setX(x);
        light.setY(y);
        light.setZ(z);
        hasLight = true;
    }

    public Point3D getLightData() {
        return light;
    }

    public void updateTimestamp() {
        timestamp = Calendar.getInstance().getTimeInMillis();
    }

    public long getTimestamp() { return timestamp; }

    public boolean isComplete() {

        //return true;

        return hasAcc
            && hasMagneticField
            && hasOrientation
            && hasGyroscope
            && hasLight
            && hasProximity
            && hasGrav
            && hasLinearAcceleration
            && hasRotationVector
            && hasMagneticFieldUncalibrated
            && hasGameRotationVector
            && hasGyroscopeUncalibrated;
    }

    public boolean isCompleteForSelectedSensors() {

        //return true;

        return hasAcc
                && hasProximity
                && hasGrav
                && hasMagneticField
                && hasOrientation
                && hasRotationVector
                && hasGameRotationVector;
    }

    public boolean isCompleteForClassifySensors() {

        return hasAcc && hasGyroscope;
    }

    public String getSensorsStatus() {

        return "Sensors status: "
                + "\nAccelerometer: ............ " + hasAcc
                + "\nMagnetic Field: ........... " + hasMagneticField
                + "\nOrientation: .............. " + hasOrientation
                + "\nGyroscope: ................ " + hasGyroscope
                + "\nLight: .................... " + hasLight
                + "\nProximity: ................ " + hasProximity
                + "\nGravity: .................. " + hasGrav
                + "\nLinear Acceleration: ...... " + hasLinearAcceleration
                + "\nRotation Vector: .......... " + hasRotationVector
                + "\nMag. Field Uncalibrated: .. " + hasMagneticFieldUncalibrated
                + "\nGame Rotation: ............ " + hasGameRotationVector
                + "\nGyrosc. Uncalibrated: ..... " + hasGyroscopeUncalibrated;
    }

    public static String getHeader() {

        StringBuilder sb = new StringBuilder();

        sb.append("timestamp").append(",");
        sb.append("location.x").append(",").append("location.y").append(",").append("location.z").append(",").append("location.prov").append(",").append("location.cached").append(",");
        sb.append("acc.x").append(",").append("acc.y").append(",").append("acc.z").append(",");
        sb.append("light.x").append(",").append("light.y").append(",").append("light.z").append(",");
        sb.append("proximity.x").append(",").append("proximity.y").append(",").append("proximity.z").append(",");
        sb.append("grav.x").append(",").append("grav.y").append(",").append("grav.z").append(",");
        sb.append("magneticField.x").append(",").append("magneticField.y").append(",").append("magneticField.z").append(",");
        sb.append("magneticFieldUncalibrated.a").append(",").append("magneticFieldUncalibrated.b").append(",").append("magneticFieldUncalibrated.c").append(",").append("magneticFieldUncalibrated.d").append(",").append("magneticFieldUncalibrated.e").append(",").append("magneticFieldUncalibrated.f").append(",");
        sb.append("orientation.x").append(",").append("orientation.y").append(",").append("orientation.z").append(",");
        sb.append("gyroscope.x").append(",").append("gyroscope.y").append(",").append("gyroscope.z").append(",");
        sb.append("gyroscopeUncalibrated.a").append(",").append("gyroscopeUncalibrated.b").append(",").append("gyroscopeUncalibrated.c").append(",").append("gyroscopeUncalibrated.d").append(",").append("gyroscopeUncalibrated.e").append(",").append("gyroscopeUncalibrated.f").append(",");
        sb.append("linearAcceleration.x").append(",").append("linearAcceleration.y").append(",").append("linearAcceleration.z").append(",");
        sb.append("rotationVector.a").append(",").append("rotationVector.b").append(",").append("rotationVector.c").append(",").append("rotationVector.d").append(",").append("rotationVector.e").append(",");
        sb.append("gameRotationVector.a").append(",").append("gameRotationVector.b").append(",").append("gameRotationVector.c").append(",").append("gameRotationVector.d").append(",");
        sb.append("significantMotion").append(",");
        sb.append("stepDetector").append(",");
        sb.append("stepCounter").append(",");
        sb.append("tiltDetector").append(",");
        sb.append("activity");
        sb.append("\n");

        return sb.toString();
    }
    
    public String toCSV(String actividade) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(timestamp).append(",");
        stringBuilder.append(acc.toCSV());
        stringBuilder.append(light.toCSV());
        stringBuilder.append(proximity.toCSV());
        stringBuilder.append(grav.toCSV());
        stringBuilder.append(magneticField.toCSV());
        stringBuilder.append(magneticFieldUncalibrated.toCSV());
        stringBuilder.append(orientation.toCSV());
        stringBuilder.append(gyroscope.toCSV());
        stringBuilder.append(gyroscopeUncalibrated.toCSV());
        stringBuilder.append(linearAcceleration.toCSV());
        stringBuilder.append(rotationVector.toCSV());
        stringBuilder.append(gameRotationVector.toCSV());
        stringBuilder.append(significantMotion==null?"":significantMotion).append(",");
        stringBuilder.append(stepDetector==null?"":stepDetector).append(",");
        stringBuilder.append(stepCounter==null?"":stepCounter).append(",");
        stringBuilder.append(tiltDetector==null?"":tiltDetector).append(",");
        stringBuilder.append(actividade);
        stringBuilder.append("\n");

        return stringBuilder.toString();
    }

    public static long getTimeInMillis() {

        return Calendar.getInstance().getTimeInMillis();
    }
}
