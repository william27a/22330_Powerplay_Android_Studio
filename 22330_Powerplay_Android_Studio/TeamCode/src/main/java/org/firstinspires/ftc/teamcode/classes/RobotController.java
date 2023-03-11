package org.firstinspires.ftc.teamcode.classes;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class RobotController extends LinearOpMode {
    private Chassis chassis;
    private LongGrabber longGrabber;
    private SideLoader sideLoader;

    public RobotController(Chassis chassis, LongGrabber longGrabber, SideLoader sideLoader) {
        this.chassis = chassis;
        this.longGrabber = longGrabber;
        this.sideLoader = sideLoader;
    }

    public RobotController(HardwareMap map) {
        this.chassis = new Chassis(map);
        this.longGrabber = new LongGrabber(map);
        this.sideLoader = new SideLoader(map);
    }

    // Set variables
    public void setDriveSpeed(double x) { this.chassis.setDriveSpeed(x); }
    
    public void setRotationSpeed(double x) { this.chassis.setRotationSpeed(x); }

    public void setShoulderSpeed(double x) { this.longGrabber.setShoulderSpeed(x); }

    public void setArmSpeed(double x) { this.longGrabber.setArmSpeed(x); }

    public void setLiftSpeed(double x) { this.sideLoader.setLiftSpeed(x); }

    // Drive systems
    public void setWheelMode(DcMotor.RunMode mode) { this.chassis.setMode(mode); }

    public void move(double inchesFL, double inchesFR, double inchesBL, double inchesBR) {
        double forward = (inchesFL + inchesFR) / 2;
        double right = inchesFL - forward;
        right *= Global.SIDE_TUNE;

        inchesFL = forward + right;
        inchesFR = forward - right;
        inchesBL = forward - right;
        inchesBR = forward + right;

        this.chassis.move(inchesFL, inchesFR, inchesBL, inchesBR);
    }

    public double getRotationDegrees() { return this.chassis.getRotationDegrees(); }

    public void setRotationDegrees(double degrees, double speed) {
        this.chassis.setRotationDegrees(degrees, speed);
    }

    public double getRotationRadians() {
        return this.chassis.getRotationRadians();
    }

    public void setRotationRadians(double radians, double speed) {
        this.chassis.setRotationRadians(radians, speed);
    }

    // Shoulder systems
    public void setShoulderDegrees(double degrees, boolean wait) { this.longGrabber.setShoulderDegrees(degrees, wait); }

    public double getShoulderDegrees() { return this.longGrabber.getShoulderDegrees(); }

    public void setShoulderRadians(double radians, boolean wait) { this.longGrabber.setShoulderRadians(radians, wait); }

    public double getShoulderRadians() { return this.longGrabber.getShoulderRadians(); }

    public void resetShoulderUp(double speed) {
        this.longGrabber.resetShoulderUp(speed);
    }

    public void resetShoulderDown(double speed) {
        this.longGrabber.resetShoulderDown(speed);
    }

    public void resetShoulderUpDown(double speed) {
        this.longGrabber.resetShoulderUpDown(speed);
    }

    // Arm systems
    public void armBrake() { this.longGrabber.armBrake(); }

    public void stopArmBrake() { this.longGrabber.stopArmBrake(); }

    public double getArmZ() { return this.longGrabber.getArmZ(); }

    public void setArmZ(double z, boolean wait) {
        this.longGrabber.setArmZ(z, wait);
    }
    
    public double getHandZ() {
        return this.getArmZ() + Global.HAND_AWAY_FROM_ARM + (Global.HAND_LENGTH/2);
    }
    
    public void setHandZ(double inches, boolean wait) {
        this.setArmZ(inches-Global.HAND_AWAY_FROM_ARM-(Global.HAND_LENGTH/2), wait);
    }
    
    public void openHand() { this.longGrabber.openHand(); }
    
    public void openHandFully() { this.longGrabber.openHandFully(); }

    public void closeHand() { this.longGrabber.closeHand(); }

    // Lift systems
    public double getLiftHeight() { return this.sideLoader.getLiftHeight(); }

    public void setLiftHeight(double inches, boolean wait) {
        this.sideLoader.setLiftHeight(inches, wait);
    }

    public void liftBrake() { this.sideLoader.liftBrake(); }

    public void stopLiftBrake() { this.sideLoader.stopLiftBrake(); }

    // Claw systems
    public double getClawHeight() { return this.getLiftHeight() + Global.MIN_CLAW_HEIGHT; }

    public void setClawHeight(double inches, boolean wait) {
        if (inches >= Global.MIN_CLAW_HEIGHT && inches <= Global.MAX_CLAW_HEIGHT) {
            this.setLiftHeight(inches - Global.MIN_CLAW_HEIGHT, wait);
        }
    }

    public void openClaw() { this.sideLoader.openClaw(); }

    public void closeClaw() { this.sideLoader.closeClaw(); }
    
    // All systems
    public void waitForCompletion() {
        while (
            (this.longGrabber != null && this.longGrabber.isBusy()) ||
            (this.sideLoader != null && this.sideLoader.isBusy())
        ) {}
    }
    
    public void deactivate() {
        this.longGrabber.deactivate();
        this.sideLoader.deactivate();
    }

    // LinearOpMode requirements
    @Override
    public void runOpMode() {}
}