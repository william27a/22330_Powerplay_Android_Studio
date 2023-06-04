package org.firstinspires.ftc.teamcode.classes;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class SideLoader {
    public DcMotor lift;
    public Servo claw;

    private double liftSpeed = 0.6;

    public SideLoader(HardwareMap map) {
        this.lift = (DcMotor) map.get("lift");
        this.claw = (Servo) map.get("claw");

        this.init();
    }

    public SideLoader(DcMotor lift, Servo claw) {
        this.lift = lift;
        this.claw = claw;

        this.init();
    }

    public void init() {
        this.lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.lift.setDirection(DcMotor.Direction.REVERSE);
        this.claw.setDirection(Servo.Direction.FORWARD);
    }

    public void setLiftDirection(DcMotorSimple.Direction direction) {
        this.lift.setDirection(direction);
    }

    public double getLiftSpeed() {
        return this.liftSpeed;
    }

    public void setLiftSpeed(double x) {
        this.liftSpeed = x;
    }

    public double getLiftHeight() {
        return this.lift.getTargetPosition() / Global.LIFT_CPI;
    }

    public void setLiftHeight(double inches, boolean wait) {
        if (inches < 0) {
            inches = 0;
        }

        this.lift.setTargetPosition((int) (inches * Global.LIFT_CPI));

        this.lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        this.lift.setPower(this.liftSpeed);

        if (wait) {
            while (this.lift.isBusy()) {
            }

            this.lift.setPower(0);
        }
    }

    public void liftBrake() {
        this.lift.setPower(this.liftSpeed);
    }

    public void stopLiftBrake() {
        this.lift.setPower(0);
    }

    // Claw systems
    public void openClaw() {
        claw.setPosition(0.15);
    }

    public void closeClaw() {
        claw.setPosition(0.25);
    }

    // Overall systems
    public boolean isBusy() {
        return this.lift.isBusy();
    }

    public void deactivate() {
        this.lift.setPower(0);
    }
}