package org.firstinspires.ftc.teamcode.classes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Arena extends LinearOpMode {
    public RobotController robot;

    private double xPosition;
    private double yPosition;

    private double calibrateYawDegrees;
    private double calibrateYawRadians;

    public Arena(RobotController robot, Side side) {
        this.robot = robot;

        if (side == Side.LEFT) {
            this.xPosition = Global.leftPos[0];
            this.yPosition = Global.leftPos[1];
        } else {
            this.xPosition = Global.rightPos[0];
            this.yPosition = Global.rightPos[1];
        }
        this.calibrateYawDegrees = -this.robot.getRotationDegrees();
        this.calibrateYawRadians = Math.toRadians(calibrateYawDegrees);
    }

    public Arena(HardwareMap map, RuntimeType type, Side side) {
        this.robot = new RobotController(map, type, side);

        if (side == Side.LEFT) {
            this.xPosition = Global.leftPos[0];
            this.yPosition = Global.leftPos[1];
        } else {
            this.xPosition = Global.rightPos[0];
            this.yPosition = Global.rightPos[1];
        }
        this.calibrateYawDegrees = -this.robot.getRotationDegrees();
        this.calibrateYawRadians = Math.toRadians(calibrateYawDegrees);
    }

    // Static and non-static get position methods
    public static double[] getInches(double squareX, double squareY) {
        squareX-=1;
        squareY-=1;
        return new double[]{Global.TILE_LENGTH * squareX + Global.blSquare[0], Global.TILE_LENGTH * squareY + Global.blSquare[1]};
    }

    public double[] getInches() {
        return new double[]{this.xPosition, this.yPosition};
    }

    public double[] getClawPos(double[] offset) {
        double x = offset[0];
        double y = offset[1];

        double newX = (Math.cos(this.getRotationRadians()) * x) - (Math.sin(this.getRotationRadians()) * y);
        double newY = (Math.cos(this.getRotationRadians()) * y) + (Math.sin(this.getRotationRadians()) * x);

        /*
        older code
        double newX = (Math.sin(this.getRotationRadians()) * y) + (Math.cos(this.getRotationRadians()) * x);
        double newY = (Math.cos(this.getRotationRadians()) * y) - (Math.sin(this.getRotationRadians()) * x);
        */

        offset[0] = newX;
        offset[1] = newY;

        return offset;
    }

    public RobotController getRobot() { return this.robot; }

    private void update(double inchesFL, double inchesFR, double inchesBL, double inchesBR) {
        double forward = (inchesFL + inchesFR)/2;
        double right = (inchesFR - inchesBR)/2;

        this.xPosition -= Math.sin(this.getRotationRadians()) * forward;
        this.yPosition += Math.cos(this.getRotationRadians()) * forward;

        this.xPosition += Math.cos(this.getRotationRadians()) * right;
        this.yPosition -= Math.sin(this.getRotationRadians()) * right;

        /*
        older code

        this.xPosition += Math.sin(this.getRotationRadians()) * forward;
        this.yPosition += Math.cos(this.getRotationRadians()) * forward;

        this.xPosition += Math.cos(this.getRotationRadians()) * right;
        this.yPosition -= Math.sin(this.getRotationRadians()) * right;
        */
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

    public void moveToSquare(double x, double y, int checkpoints, CalibrationType calibrationType, double calibrateTo) {
        double[] newInches = Arena.getInches(x, y);
        double moveUp = newInches[1] - yPosition;
        double moveRight = newInches[0] - xPosition;
        moveUp /= checkpoints;
        moveRight /= checkpoints;

        for (int i = 0; i < checkpoints; i++) {
            double a = (Math.cos(this.getRotationRadians()) * moveUp) + (Math.sin(this.getRotationRadians()) * moveRight);
            double b = (Math.cos(this.getRotationRadians()) * moveRight) - (Math.sin(this.getRotationRadians()) * moveUp);

            /*
            older code
            double a = (Math.cos(this.getRotationRadians()) * moveUp) - (Math.sin(this.getRotationRadians()) * moveRight);
            double b = (Math.sin(this.getRotationRadians()) * moveUp) + (Math.cos(this.getRotationRadians()) * moveRight);
            */

            this.move(a + b, a - b, a - b, a + b);

            if (calibrationType == CalibrationType.REPEAT) {
                this.setRotationDegrees(calibrateTo, 0.6);
            }
        }

        if (calibrationType == CalibrationType.ONCE) {
            this.setRotationDegrees(calibrateTo, 0.6);
        }
    }

    public void moveToPos(double[] pos, int checkpoints, CalibrationType calibrationType, double calibrateTo) {
        double[] newInches = pos;
        double moveUp = newInches[1] - yPosition;
        double moveRight = newInches[0] - xPosition;
        moveUp /= checkpoints;
        moveRight /= checkpoints;

        for (int i = 0; i < checkpoints; i++) {
            double a = (Math.cos(this.getRotationRadians()) * moveUp) + (Math.sin(this.getRotationRadians()) * moveRight);
            double b = (Math.cos(this.getRotationRadians()) * moveRight) - (Math.sin(this.getRotationRadians()) * moveUp);

            /*
            older code
            double a = (Math.cos(this.getRotationRadians()) * moveUp) - (Math.sin(this.getRotationRadians()) * moveRight);
            double b = (Math.sin(this.getRotationRadians()) * moveUp) + (Math.cos(this.getRotationRadians()) * moveRight);
            */

            this.move(a + b, a - b, a - b, a + b);

            if (calibrationType == CalibrationType.REPEAT) {
                this.setRotationDegrees(calibrateTo, 0.6);
            }
        }

        if (calibrationType == CalibrationType.ONCE) {
            this.setRotationDegrees(calibrateTo, 0.6);
        }
    }

    public void moveClawToSquare(double x, double y, int checkpoints, CalibrationType calibrationType, double calibrateTo) {
        double[] newInches = Arena.getInches(x, y);
        double[] realOffset = this.getClawPos(Global.clawOffset);
        double moveUp = newInches[1] - yPosition - realOffset[1];
        double moveRight = newInches[0] - xPosition - realOffset[0];
        moveUp /= checkpoints;
        moveRight /= checkpoints;

        for (int i = 0; i < checkpoints; i++) {
            double a = (Math.cos(this.getRotationRadians()) * moveUp) + (Math.sin(this.getRotationRadians()) * moveRight);
            double b = (Math.cos(this.getRotationRadians()) * moveRight) - (Math.sin(this.getRotationRadians()) * moveUp);

            /*
            older code
            double a = (Math.cos(this.getRotationRadians()) * moveUp) - (Math.sin(this.getRotationRadians()) * moveRight);
            double b = (Math.sin(this.getRotationRadians()) * moveUp) + (Math.cos(this.getRotationRadians()) * moveRight);
            */

            this.move(a + b, a - b, a - b, a + b);

            if (calibrationType == CalibrationType.REPEAT) {
                this.setRotationDegrees(calibrateTo, 0.6);
            }
        }

        if (calibrationType == CalibrationType.ONCE) {
            this.setRotationDegrees(calibrateTo, 0.6);
        }
    }

    public void moveClawToPos(double[] pos, int checkpoints, CalibrationType calibrationType, double calibrateTo) {
        double[] newInches = pos;
        double[] realOffset = this.getClawPos(Global.clawOffset);
        double moveUp = newInches[1] - yPosition - realOffset[1];
        double moveRight = newInches[0] - xPosition - realOffset[0];
        moveUp /= checkpoints;
        moveRight /= checkpoints;

        for (int i = 0; i < checkpoints; i++) {
            double a = (Math.cos(this.getRotationRadians()) * moveUp) + (Math.sin(this.getRotationRadians()) * moveRight);
            double b = (Math.cos(this.getRotationRadians()) * moveRight) - (Math.sin(this.getRotationRadians()) * moveUp);

            /*
            older code
            double a = (Math.cos(this.getRotationRadians()) * moveUp) - (Math.sin(this.getRotationRadians()) * moveRight);
            double b = (Math.sin(this.getRotationRadians()) * moveUp) + (Math.cos(this.getRotationRadians()) * moveRight);
            */

            this.move(a + b, a - b, a - b, a + b);

            if (calibrationType == CalibrationType.REPEAT) {
                this.setRotationDegrees(calibrateTo, 0.6);
            }
        }

        if (calibrationType == CalibrationType.ONCE) {
            this.setRotationDegrees(calibrateTo, 0.6);
        }
    }

    @Override
    public void runOpMode() {
    }
}