package org.firstinspires.ftc.teamcode.classes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Arena extends LinearOpMode {
    public RobotController robot;

    private double xPosition;
    private double yPosition;

    private double calibrateYawDegrees;
    private double calibrateYawRadians;

    public Arena(HardwareMap map, RuntimeType type) {
        this.robot = new RobotController(map, type);

        this.calibrateYawDegrees = -this.robot.getRotationDegrees();
        this.calibrateYawRadians = Math.toRadians(calibrateYawDegrees);
    }

    // Static and non-static get position methods
    public static double[] getInches(double squareX, double squareY) {
        squareX -= 1;
        squareY -= 1;
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

        offset[0] = newX;
        offset[1] = newY;

        return offset;
    }

    public RobotController getRobot() { return this.robot; }

    private double[] axisRotation(double x, double y, double angle) {
        double newX = (Math.cos(angle) * x) - (Math.sin(angle) * y);
        double newY = (Math.cos(angle) * y) + (Math.sin(angle) * x);

        return new double[]{newX, newY};
    }

    public double[] relationFromTicks(double[] ticks) {
        return new double[]{(ticks[0] - ticks[1])/2, (ticks[0] + ticks[1])/2};
    }

    private double[] directionToRelation(double right, double up) {
        double[] newVector = this.axisRotation(right, up, this.getRotationRadians());
        double strafe = newVector[0];
        double forward = newVector[1];
        return new double[]{strafe, forward};
    }

    private double[] relationToDirection(double[] ticks) {
        double[] relation = relationFromTicks(ticks);
        double strafe = relation[0];
        double forward = relation[1];

        return this.axisRotation(strafe, forward, -this.getRotationRadians());
    }

    private void update(double ticksFL, double ticksFR, double ticksBL, double ticksBR) {
        double[] direction = relationToDirection(new double[]{ticksFL, ticksFR, ticksBL, ticksBR});
        double right = direction[0];
        double up = direction[1];

        this.xPosition += right;
        this.yPosition += up;
    }

    public void recalibrateYaw() {
        this.calibrateYawDegrees = -this.robot.getRotationDegrees();
        this.calibrateYawRadians = Math.toRadians(calibrateYawDegrees);
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

    public void move(double inchesFL, double inchesFR, double inchesBL, double inchesBR) {
        this.update(inchesFL, inchesFR, inchesBL, inchesBR);

        this.robot.move(inchesFL, inchesFR, inchesBL, inchesBR);
    }

    public void moveNicely(double inchesFL, double inchesFR, double inchesBL, double inchesBR, int checks) {
        this.robot.moveNicely(inchesFL, inchesFR, inchesBL, inchesBR, checks);
        double[] ticks = robot.chassis.getTicks();
        this.update(ticks[0], ticks[1], ticks[2], ticks[3]);
    }

    public void moveToSquare(double x, double y, int checkpoints, CalibrationType calibrationType, double calibrateTo) {
        double[] newInches = Arena.getInches(x, y);
        double moveUp = newInches[1] - yPosition;
        double moveRight = newInches[0] - xPosition;
        moveUp /= checkpoints;
        moveRight /= checkpoints;

        for (int i = 0; i < checkpoints; i++) {
            double[] relation = directionToRelation(moveUp, moveRight);
            double strafe = relation[0];
            double forward = relation[1];

            this.move(forward - strafe, forward + strafe, forward + strafe, forward - strafe);

            if (calibrationType == CalibrationType.REPEAT) {
                this.setRotationDegrees(calibrateTo, 0.6);
            }
        }

        if (calibrationType == CalibrationType.ONCE) {
            this.setRotationDegrees(calibrateTo, 0.6);
        }
    }

    public void moveToPos(double[] pos, int checkpoints, CalibrationType calibrationType, double calibrateTo) {
        double moveUp = pos[1] - yPosition;
        double moveRight = pos[0] - xPosition;
        moveUp /= checkpoints;
        moveRight /= checkpoints;

        for (int i = 0; i < checkpoints; i++) {
            double[] relation = directionToRelation(moveUp, moveRight);
            double strafe = relation[0];
            double forward = relation[1];

            this.move(forward - strafe, forward + strafe, forward + strafe, forward - strafe);

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
            double[] relation = directionToRelation(moveUp, moveRight);
            double strafe = relation[0];
            double forward = relation[1];

            this.move(forward - strafe, forward + strafe, forward + strafe, forward - strafe);

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
            double[] relation = directionToRelation(moveUp, moveRight);
            double strafe = relation[0];
            double forward = relation[1];

            this.move(forward - strafe, forward + strafe, forward + strafe, forward - strafe);

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