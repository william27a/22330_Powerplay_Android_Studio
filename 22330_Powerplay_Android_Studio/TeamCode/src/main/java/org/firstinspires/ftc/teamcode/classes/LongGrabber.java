package org.firstinspires.ftc.teamcode.classes;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

public class LongGrabber {
    public DcMotor shoulder;
    public DcMotor arm;
    public Servo hand;

    private TouchSensor limitSwitch;

    private double shoulderSpeed = 1;
    private double armSpeed = 0.6;

    public LongGrabber(HardwareMap map) {
        this.shoulder = (DcMotor)map.get("shoulder");
        this.arm = (DcMotor)map.get("arm");
        this.hand = (Servo)map.get("hand");

        this.limitSwitch = (TouchSensor)map.get("limitSwitch");

        this.init();
    }

    public LongGrabber(DcMotor shoulder, DcMotor arm, Servo hand) {
        this.shoulder = shoulder;
        this.arm = arm;
        this.hand = hand;

        this.init();
    }

    public LongGrabber(DcMotor shoulder, DcMotor arm, Servo hand, TouchSensor limitSwitch) {
        this.shoulder = shoulder;
        this.arm = arm;
        this.hand = hand;

        this.limitSwitch = limitSwitch;

        this.init();
    }

    public void init() {
        this.shoulder.setDirection(DcMotor.Direction.REVERSE);
        this.arm.setDirection(DcMotor.Direction.REVERSE);
        this.hand.setDirection(Servo.Direction.FORWARD);

        this.hand.scaleRange(0.4, 0.55);
    }

    public double getShoulderSpeed() { return this.shoulderSpeed; }

    public void setShoulderSpeed(double x) { this.shoulderSpeed = x; }

    public double getArmSpeed() { return this.armSpeed; }

    public void setArmSpeed(double x) { this.armSpeed = x; }

    public double getShoulderDegrees() {
        return this.shoulder.getTargetPosition() / Global.SHOULDER_CPD;
    }

    public void setShoulderDegrees(double degrees, boolean wait) {
        // * Pi / 180
        // I know it's weird, but it should work
        this.shoulder.setTargetPosition((int) (degrees * Global.SHOULDER_CPD));

        this.shoulder.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        this.shoulder.setPower(this.shoulderSpeed);

        if (wait) {
            while (!Global.isClose(this.shoulder, (Global.SHOULDER_TOLERANCE * Global.SHOULDER_CPD))) {}

            this.shoulder.setPower(0);
        }
    }

    public double getShoulderRadians() { return this.shoulder.getTargetPosition() / Global.SHOULDER_CPR; }

    public void setShoulderRadians(double radians, boolean wait) {
        this.shoulder.setTargetPosition((int) (radians * Global.SHOULDER_CPR));

        this.shoulder.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        this.shoulder.setPower(this.shoulderSpeed);

        if (wait) {
            while (!Global.isClose(this.shoulder, (Global.SHOULDER_TOLERANCE * Global.SHOULDER_CPD))) {}

            this.shoulder.setPower(0);
        }
    }

    public void resetShoulderUp(double speed) {
        this.shoulder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        this.shoulder.setPower(-speed);
        while (!this.limitSwitch.isPressed()) {}
        this.shoulder.setPower(0);

        this.shoulder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void resetShoulderDown(double speed) {
        this.shoulder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        this.shoulder.setPower(speed);
        while (!this.limitSwitch.isPressed()) {}
        while (this.limitSwitch.isPressed()) {}
        this.shoulder.setPower(0);

        this.shoulder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void resetShoulderUpDown(double speed) {
        this.resetShoulderUp(speed);
        this.resetShoulderDown(speed);
    }

    public double getArmZ() { return this.arm.getTargetPosition() / Global.ARM_CPI; }

    public void setArmZ(double z, boolean wait) {
        if (z < 0) {
            z = 0;
        }

        this.arm.setTargetPosition((int) (z * Global.ARM_CPI));

        this.arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        this.arm.setPower(this.armSpeed);

        if (wait) {
            while (this.arm.isBusy()) {}

            this.arm.setPower(0);
        }
    }

    public void armBrake() { this.arm.setPower(this.armSpeed); }

    public void stopArmBrake() { this.arm.setPower(0); }

    public void openHand() { hand.setPosition(0.4); }

    public void openHandFully() { hand.setPosition(0.3); }

    public void closeHand() { hand.setPosition(0.55); }

    public boolean isBusy() { return this.shoulder.isBusy() || this.arm.isBusy(); }

    public void deactivate() {
        this.shoulder.setPower(0);

        this.arm.setPower(0);
    }
}