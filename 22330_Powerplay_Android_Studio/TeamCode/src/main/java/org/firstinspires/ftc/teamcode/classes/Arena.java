package org.firstinspires.ftc.teamcode.classes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

public class Arena extends LinearOpMode {
    public RobotController robot;
    public TouchSensor limitSwitch;

    private double xPosition;
    private double yPosition;

    private double calibrateYawDegrees;
    private double calibrateYawRadians;

    private final double shoulderOffsetDegrees;
    private final double shoulderOffsetRadians;

    public Arena(RobotController robot, TouchSensor limitSwitch, double xPosition, double yPosition, double startingDegrees, double shoulderStartingDegrees, boolean realMatch) {
        this.robot = robot;
        this.limitSwitch = limitSwitch;

        this.xPosition = xPosition;
        this.yPosition = yPosition;

        this.calibrateYawDegrees = startingDegrees - this.robot.getRotationDegrees();
        this.calibrateYawRadians = Math.toRadians(startingDegrees) - this.robot.getRotationRadians();

        this.shoulderOffsetDegrees = shoulderStartingDegrees;
        this.shoulderOffsetRadians = Math.toRadians(this.shoulderOffsetDegrees);

        if (realMatch) {
            this.xPosition += Math.sin(this.getRotationRadians()) * Global.OFF_THE_BACK;
            this.yPosition += Math.cos(this.getRotationRadians()) * Global.OFF_THE_BACK;

            this.xPosition += Math.cos(this.getRotationRadians()) * Global.ON_THE_LEFT;
            this.yPosition += Math.sin(this.getRotationRadians()) * Global.ON_THE_LEFT;
        }
    }

    public Arena(HardwareMap map, double xPosition, double yPosition, double startingDegrees, double shoulderStartingDegrees, boolean realMatch) {
        this.robot = new RobotController(map);
        this.limitSwitch = (TouchSensor) map.get("limitSwitch");

        this.xPosition = xPosition;
        this.yPosition = yPosition;

        this.calibrateYawDegrees = startingDegrees - this.robot.getRotationDegrees();
        this.calibrateYawRadians = Math.toRadians(startingDegrees) - this.robot.getRotationRadians();

        this.shoulderOffsetDegrees = shoulderStartingDegrees;
        this.shoulderOffsetRadians = Math.toRadians(this.shoulderOffsetDegrees);

        if (realMatch) {
            this.xPosition += Math.sin(this.getRotationRadians()) * Global.OFF_THE_BACK;
            this.yPosition += Math.cos(this.getRotationRadians()) * Global.OFF_THE_BACK;

            this.xPosition += Math.cos(this.getRotationRadians()) * Global.ON_THE_LEFT;
            this.yPosition += Math.sin(this.getRotationRadians()) * Global.ON_THE_LEFT;
        }
    }

    public Arena(HardwareMap map, boolean leftSide) {
        this.robot = new RobotController(map);
        this.limitSwitch = (TouchSensor) map.get("limitSwitch");

        if (leftSide) {
            this.xPosition = Arena.getInches(2, 0.5)[0] - Global.ON_THE_LEFT;
            this.yPosition = Arena.getInches(2, 0.5)[1] + Global.OFF_THE_BACK;
        }

        this.calibrateYawDegrees = -this.robot.getRotationDegrees();
        this.calibrateYawRadians = -this.robot.getRotationRadians();

        this.shoulderOffsetDegrees = 52;
        this.shoulderOffsetRadians = Math.toRadians(this.shoulderOffsetDegrees);
    }

    // Static and non-static get position methods
    public static double[] getInches(double squareX, double squareY) {
        double[] inches = new double[2];
        inches[0] = (Global.TILE_LENGTH * (squareX - 0.5));
        inches[1] = (Global.TILE_LENGTH * (squareY - 0.5));
        return inches;
    }

    public static double[] getSquare(double inchesX, double inchesY) {
        double[] square = new double[2];
        square[0] = (inchesX / Global.TILE_LENGTH) + 0.5;
        square[1] = (inchesY / Global.TILE_LENGTH) + 0.5;
        return square;
    }

    public RobotController getRobot() {
        return this.robot;
    }

    public double[] getSquare() {
        double[] square = new double[2];
        square[0] = (xPosition / Global.TILE_LENGTH) + 0.5;
        square[1] = (yPosition / Global.TILE_LENGTH) + 0.5;
        return square;
    }

    // Re-inits*
    private void updatePosition(double inchesFL, double inchesFR, double inchesBL, double inchesBR) {
        double forward = (inchesFL + inchesFR) / 2;
        double right = inchesFL - forward;

        this.xPosition += Math.sin(this.getRotationRadians()) * forward;
        this.yPosition += Math.cos(this.getRotationRadians()) * forward;

        this.xPosition += Math.cos(this.getRotationRadians()) * right;
        this.yPosition += Math.sin(this.getRotationRadians()) * right;
    }

    public void recalibrateYaw(double degrees) {
        this.calibrateYawDegrees = degrees - this.robot.getRotationDegrees();
        this.calibrateYawRadians = Math.toRadians(degrees) - this.robot.getRotationRadians();
    }

    // Drive systems
    public double getRotationDegrees() {
        return this.robot.getRotationDegrees() + this.calibrateYawDegrees;
    }

    public void setRotationDegrees(double degrees, double speed) {
        this.robot.setRotationDegrees(degrees - this.calibrateYawDegrees, speed);
    }

    public double getRotationRadians() {
        return this.robot.getRotationRadians() + this.calibrateYawRadians;
    }

    public void setRotationRadians(double radians, double speed) {
        this.robot.setRotationRadians(radians - this.calibrateYawRadians, speed);
    }

    public void move(double inchesFL, double inchesFR, double inchesBL, double inchesBR) {
        this.updatePosition(inchesFL, inchesFR, inchesBL, inchesBR);

        this.robot.move(inchesFL, inchesFR, inchesBL, inchesBR);
    }

    public void moveToSquare(double x, double y, boolean speed) {
        double[] newInches = Arena.getInches(x, y);
        double moveUp = newInches[1] - yPosition;
        double moveRight = newInches[0] - xPosition;

        if (speed) {
            double a = (Math.cos(this.getRotationRadians()) * moveUp) - (Math.sin(this.getRotationRadians()) * moveRight);
            double b = (Math.sin(this.getRotationRadians()) * moveUp) + (Math.cos(this.getRotationRadians()) * moveRight);

            this.move(a + b, a - b, a - b, a + b);
        } else {
            double cleanRight = moveRight % Global.TILE_LENGTH;
            moveRight -= cleanRight;

            double a = Math.sin(this.getRotationRadians()) * cleanRight;
            double b = Math.cos(this.getRotationRadians()) * cleanRight;

            this.move(-a + b, -a - b, -a - b, -a + b);

            a = Math.cos(this.getRotationRadians()) * moveUp;
            b = Math.sin(this.getRotationRadians()) * moveUp;

            this.move(a + b, a - b, a - b, a + b);

            a = Math.sin(this.getRotationRadians()) * moveRight;
            b = Math.cos(this.getRotationRadians()) * moveRight;

            this.move(-a + b, -a - b, -a - b, -a + b);
        }
    }

    // Shoulder systems
    public double getShoulderDegrees() {
        return this.robot.getShoulderDegrees() + this.shoulderOffsetDegrees;
    }

    public void setShoulderDegrees(double degrees, boolean wait) {
        this.robot.setShoulderDegrees(degrees - this.shoulderOffsetDegrees, wait);
    }

    public double getShoulderRadians() {
        return this.robot.getShoulderRadians() + this.shoulderOffsetRadians;
    }

    public void setShoulderRadians(double radians, boolean wait) {
        this.robot.setShoulderRadians(radians - this.shoulderOffsetRadians, wait);
    }

    // Highest-level methods
    public void grabConeOnStack(double degrees, int cone) {
        this.robot.stopArmBrake();
        this.robot.openHand();

        double height = Global.coneHeightFromStack(cone) - Global.SHOULDER_OFF_THE_GROUND;
        double length = Global.LENGTH_TO_STACK;
        double hypotenuse = Global.getHypotenuse(height, length);

        this.setRotationDegrees(degrees + Global.DEGREES_TO_STACK, 1);
        this.setRotationDegrees(degrees + Global.DEGREES_TO_STACK, 0.5);

        this.setShoulderDegrees(Math.toDegrees(Math.atan(height / length)), true);
        this.robot.setHandZ(hypotenuse, true);
        this.robot.closeHand();
        sleep(1000);

        this.robot.resetShoulderUpDown(1);
        this.robot.setHandZ(hypotenuse - 10, false);

        this.robot.waitForCompletion();
        this.robot.deactivate();
    }

    public void placeConeOnJunction(double degrees) {
        double length = Global.LENGTH_TO_JUNCTION;
        double height = Global.HEIGHT_TO_JUNCTION;
        double hypotenuse = Global.getHypotenuse(length, height);

        this.setRotationDegrees(degrees + Global.DEGREES_TO_JUNCTION, 1);
        this.setRotationDegrees(degrees + Global.DEGREES_TO_JUNCTION, 0.5);

        this.setShoulderDegrees(Math.toDegrees(Math.atan(height / length)), false);
        this.robot.setHandZ(hypotenuse, true);
        this.robot.armBrake();
        this.robot.openHand();
        sleep(1000);
    }

    @Override
    public void runOpMode() {
    }
}