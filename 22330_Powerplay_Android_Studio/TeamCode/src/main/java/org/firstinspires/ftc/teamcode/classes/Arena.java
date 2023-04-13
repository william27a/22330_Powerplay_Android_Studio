package org.firstinspires.ftc.teamcode.classes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

public class Arena extends LinearOpMode {
    public RobotController robot;

    private double xPosition;
    private double yPosition;

    private double calibrateYawDegrees;
    private double calibrateYawRadians;

    public Arena(RobotController robot, double[] pos) {
        this.robot = robot;

        this.xPosition = pos[0];
        this.yPosition = pos[1];

        this.calibrateYawDegrees = -this.robot.getRotationDegrees();
        this.calibrateYawRadians = Math.toRadians(calibrateYawDegrees);
    }

    public Arena(HardwareMap map, RuntimeType type, double[] pos) {
        this.robot = new RobotController(map, type);

        this.xPosition = pos[0];
        this.yPosition = pos[1];

        this.calibrateYawDegrees = -this.robot.getRotationDegrees();
        this.calibrateYawRadians = Math.toRadians(calibrateYawDegrees);
    }

    // Static and non-static get position methods
    public static double[] getInches(double squareX, double squareY) {
        double[] inches = new double[2];
        inches[0] = (Global.TILE_LENGTH * (squareX - 0.5));
        inches[1] = (Global.TILE_LENGTH * (squareY - 0.5));
        return inches;
    }

    public double[] getClawPos(double[] offset) {
        double x = offset[0];
        double y = offset[1];

        double newX = Math.tan(this.getRotationRadians())*y;
        double newY = x/Math.tan(this.getRotationRadians());

        offset[0] = newX;
        offset[1] = newY;

        return offset;
    }

    public RobotController getRobot() { return this.robot; }

    // Re-inits*
    private void update(double inchesFL, double inchesFR, double inchesBL, double inchesBR) {
        double forward = (inchesFL + inchesFR)/2;
        double right = (inchesFR - inchesBR)/2;
        // double rotation = inchesFL - forward + right;

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
        this.update(inchesFL, inchesFR, inchesBL, inchesBR);

        this.robot.move(inchesFL, inchesFR, inchesBL, inchesBR);
    }

    public void moveToSquare(double x, double y, boolean calibrate) {
        double[] newInches = Arena.getInches(x, y);
        double moveUp = newInches[1] - yPosition;
        double moveRight = newInches[0] - xPosition;

        double a = (Math.cos(this.getRotationRadians()) * moveUp) - (Math.sin(this.getRotationRadians()) * moveRight);
        double b = (Math.sin(this.getRotationRadians()) * moveUp) + (Math.cos(this.getRotationRadians()) * moveRight);

        this.move(a + b, a - b, a - b, a + b);

        if (calibrate) {
            this.setRotationDegrees(0, 0.6);
        }

        /*} else {
            double cleanRight = moveRight % Global.TILE_LENGTH;
            moveRight -= cleanRight;

            double a = Math.sin(this.getRotationRadians()) * cleanRight;
            double b = Math.cos(this.getRotationRadians()) * cleanRight;

            this.move(-a + b, -a - b, -a - b, -a + b);

            a = Math.cos(this.getRotationRadians()) * moveUp;
    https://www.tensorflow.org/lite/guide/inference        b = Math.sin(this.getRotationRadians()) * moveUp;

            this.move(a + b, a - b, a - b, a + b);

            a = Math.sin(this.getRotationRadians()) * moveRight;
            b = Math.cos(this.getRotationRadians()) * moveRight;

            this.move(-a + b, -a - b, -a - b, -a + b);
        }*/
    }

    public void moveToPos(double[] pos, boolean calibrate) {
        double[] newInches = pos;
        double moveUp = newInches[1] - yPosition;
        double moveRight = newInches[0] - xPosition;

        double a = (Math.cos(this.getRotationRadians()) * moveUp) - (Math.sin(this.getRotationRadians()) * moveRight);
        double b = (Math.sin(this.getRotationRadians()) * moveUp) + (Math.cos(this.getRotationRadians()) * moveRight);

        this.move(a + b, a - b, a - b, a + b);

        if (calibrate) {
            this.setRotationDegrees(0, 0.6);
        }

        /*} else {
            double cleanRight = moveRight % Global.TILE_LENGTH;
            moveRight -= cleanRight;

            double a = Math.sin(this.getRotationRadians()) * cleanRight;
            double b = Math.cos(this.getRotationRadians()) * cleanRight;

            this.move(-a + b, -a - b, -a - b, -a + b);

            a = Math.cos(this.getRotationRadians()) * moveUp;
            https://www.tensorflow.org/lite/guide/inference        b = Math.sin(this.getRotationRadians()) * moveUp;

            this.move(a + b, a - b, a - b, a + b);

            a = Math.sin(this.getRotationRadians()) * moveRight;
            b = Math.cos(this.getRotationRadians()) * moveRight;

            this.move(-a + b, -a - b, -a - b, -a + b);
        }*/
    }

    public void moveClawToSquare(double x, double y, boolean calibrate) {
        double[] newInches = Arena.getInches(x, y);
        double[] realOffset = this.getClawPos(Global.clawOffset);
        double moveUp = newInches[1] - yPosition - realOffset[1];
        double moveRight = newInches[0] - xPosition - realOffset[0];

        double a = (Math.cos(this.getRotationRadians()) * moveUp) - (Math.sin(this.getRotationRadians()) * moveRight);
        double b = (Math.sin(this.getRotationRadians()) * moveUp) + (Math.cos(this.getRotationRadians()) * moveRight);

        this.move(a + b, a - b, a - b, a + b);

        if (calibrate) {
            this.setRotationDegrees(0, 0.6);
        }

        /*} else {
            double cleanRight = moveRight % Global.TILE_LENGTH;
            moveRight -= cleanRight;

            double a = Math.sin(this.getRotationRadians()) * cleanRight;
            double b = Math.cos(this.getRotationRadians()) * cleanRight;

            this.move(-a + b, -a - b, -a - b, -a + b);

            a = Math.cos(this.getRotationRadians()) * moveUp;
    https://www.tensorflow.org/lite/guide/inference        b = Math.sin(this.getRotationRadians()) * moveUp;

            this.move(a + b, a - b, a - b, a + b);

            a = Math.sin(this.getRotationRadians()) * moveRight;
            b = Math.cos(this.getRotationRadians()) * moveRight;

            this.move(-a + b, -a - b, -a - b, -a + b);
        }*/
    }

    public void moveClawToPos(double[] pos, boolean calibrate) {
        double[] newInches = pos;
        double[] realOffset = this.getClawPos(Global.clawOffset);
        double moveUp = newInches[1] - yPosition - realOffset[1];
        double moveRight = newInches[0] - xPosition - realOffset[0];

        double a = (Math.cos(this.getRotationRadians()) * moveUp) - (Math.sin(this.getRotationRadians()) * moveRight);
        double b = (Math.sin(this.getRotationRadians()) * moveUp) + (Math.cos(this.getRotationRadians()) * moveRight);

        this.move(a + b, a - b, a - b, a + b);

        if (calibrate) {
            this.setRotationDegrees(0, 0.6);
        }

        /*} else {
            double cleanRight = moveRight % Global.TILE_LENGTH;
            moveRight -= cleanRight;

            double a = Math.sin(this.getRotationRadians()) * cleanRight;
            double b = Math.cos(this.getRotationRadians()) * cleanRight;

            this.move(-a + b, -a - b, -a - b, -a + b);

            a = Math.cos(this.getRotationRadians()) * moveUp;
            https://www.tensorflow.org/lite/guide/inference        b = Math.sin(this.getRotationRadians()) * moveUp;

            this.move(a + b, a - b, a - b, a + b);

            a = Math.sin(this.getRotationRadians()) * moveRight;
            b = Math.cos(this.getRotationRadians()) * moveRight;

            this.move(-a + b, -a - b, -a - b, -a + b);
        }*/
    }

    @Override
    public void runOpMode() {
    }
}