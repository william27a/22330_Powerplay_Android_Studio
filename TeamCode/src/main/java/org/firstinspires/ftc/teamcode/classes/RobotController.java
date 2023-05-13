package org.firstinspires.ftc.teamcode.classes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.HashMap;

public class RobotController extends LinearOpMode {
    public Chassis chassis;
    public SideLoader sideLoader;

    // TeleOp
    private double x;
    private double y;
    private double pivot;
    private double normal;
    private boolean liftWasStatic;

    public RobotController(Chassis chassis, SideLoader sideLoader, RuntimeType type) {
        this.chassis = chassis;
        this.sideLoader = sideLoader;
    }

    public RobotController(HardwareMap map, RuntimeType type) {
        this.chassis = new Chassis(map);
        this.sideLoader = new SideLoader(map);

        if (type == RuntimeType.DRIVER_CONTROLLED_TELEOP || type == RuntimeType.AGENT_CONTROLLED_TELEOP
                || type == RuntimeType.AGENT_CONTROLLED_AUTO) {
            this.chassis.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        if (type == RuntimeType.DRIVER_CONTROLLED_TELEOP) {
            chassis.reverso();
        }
    }

    // Set variables
    public void setDriveSpeed(double x) { this.chassis.setDriveSpeed(x); }

    public void setRotationSpeed(double x) { this.chassis.setRotationSpeed(x); }

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

    public void moveWithMap(double inchesFL, double inchesFR, double inchesBL, double inchesBR) {
        double forward = (inchesFL + inchesFR) / 2;
        double right = inchesFL - forward;
        right *= Global.SIDE_TUNE;

        inchesFL = forward + right;
        inchesFR = forward - right;
        inchesBL = forward - right;
        inchesBR = forward + right;

        this.chassis.moveWithMap(inchesFL, inchesFR, inchesBL, inchesBR);
    }

    public void moveNicely(double inchesFL, double inchesFR, double inchesBL, double inchesBR, int checks) {
        double forward = (inchesFL + inchesFR) / 2;
        double right = inchesFL - forward;
        right *= Global.SIDE_TUNE;

        inchesFL = forward + right;
        inchesFR = forward - right;
        inchesBL = forward - right;
        inchesBR = forward + right;

        this.chassis.moveNicely(inchesFL, inchesFR, inchesBL, inchesBR, checks);
    }

    public double getRotationDegrees() { return this.chassis.getRotationDegrees(); }

    public void setRotationDegrees(double degrees, double speed) { this.chassis.setRotationDegrees(degrees, speed); }

    public double getRotationRadians() { return this.chassis.getRotationRadians(); }

    public void setRotationRadians(double radians, double speed) {
        this.chassis.setRotationRadians(radians, speed);
    }

    // Lift systems
    public double getLiftHeight() {
        return this.sideLoader.getLiftHeight();
    }

    public void setLiftHeight(double inches, boolean wait) {
        this.sideLoader.setLiftHeight(inches, wait);
    }

    public void liftBrake() {
        this.sideLoader.liftBrake();
    }

    public void stopLiftBrake() {
        this.sideLoader.stopLiftBrake();
    }

    // Claw systems
    public double getClawHeight() {
        return this.getLiftHeight() + Global.MIN_CLAW_HEIGHT;
    }

    public void setClawHeight(double inches, boolean wait) {
        if (inches >= Global.MIN_CLAW_HEIGHT && inches <= Global.MAX_CLAW_HEIGHT) {
            this.setLiftHeight(inches - Global.MIN_CLAW_HEIGHT, wait);
        }
    }

    public void openClaw() {
        this.sideLoader.openClaw();
    }

    public void closeClaw() {
        this.sideLoader.closeClaw();
    }

    // All systems
    public void waitForCompletion() {
        while (this.sideLoader != null && this.sideLoader.isBusy()) {}
    }

    public void deactivate() {
        this.sideLoader.deactivate();
    }

    @Override
    public void runOpMode() {
    }

    public void handleMovementLeft(Gamepad gamepad1) {
        // assign values to each variable included in chassis math
        pivot = gamepad1.right_trigger - gamepad1.left_trigger;

        x = gamepad1.left_stick_x;
        y = -gamepad1.left_stick_y;
        normal = Math.abs(pivot) + Math.abs(x) + Math.abs(y);
        if (normal < 1) {
            normal = 1;
        }

        // set motor powers
        this.chassis.frontLeft.setPower((y + x + pivot) * this.chassis.getDriveSpeed() / normal);
        this.chassis.frontRight.setPower((y - x - pivot) * this.chassis.getDriveSpeed() / normal);
        this.chassis.backLeft.setPower((y - x + pivot) * this.chassis.getDriveSpeed() / normal);
        this.chassis.backRight.setPower((y + x - pivot) * this.chassis.getDriveSpeed() / normal);

        if (gamepad1.left_bumper) {
            if (!gamepad1.right_bumper) {
                this.sideLoader.claw.setPosition(0);
            }
        } else if (gamepad1.right_bumper) {
            this.sideLoader.claw.setPosition(1);
        }

        if (-gamepad1.right_stick_y != 0) {
            if (liftWasStatic) {
                liftWasStatic = false;
                this.sideLoader.lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }
            this.sideLoader.lift.setPower(-gamepad1.right_stick_y);
        } else if (!liftWasStatic) {
            this.sideLoader.lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            this.sideLoader.lift.setTargetPosition(0);
            this.sideLoader.lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            this.sideLoader.lift.setPower(0.3);
            liftWasStatic = true;
        }
    }

    public void handleMovementRight(Gamepad gamepad1) {
        // assign values to each variable included in chassis math
        pivot = gamepad1.right_trigger - gamepad1.left_trigger;

        x = -gamepad1.left_stick_x;
        y = gamepad1.left_stick_y;

        normal = Math.abs(pivot) + Math.abs(x) + Math.abs(y);
        if (normal < 1) {
            normal = 1;
        }

        // set motor powers
        this.chassis.frontLeft.setPower((y + x + pivot) * this.chassis.getDriveSpeed() / normal);
        this.chassis.frontRight.setPower((y - x - pivot) * this.chassis.getDriveSpeed() / normal);
        this.chassis.backLeft.setPower((y - x + pivot) * this.chassis.getDriveSpeed() / normal);
        this.chassis.backRight.setPower((y + x - pivot) * this.chassis.getDriveSpeed() / normal);

        if (gamepad1.left_bumper) {
            if (!gamepad1.right_bumper) {
                this.sideLoader.claw.setPosition(0);
            }
        } else if (gamepad1.right_bumper) {
            this.sideLoader.claw.setPosition(1);
        }

        if (-gamepad1.right_stick_y != 0) {
            if (liftWasStatic) {
                liftWasStatic = false;
                this.sideLoader.lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }
            this.sideLoader.lift.setPower(-gamepad1.right_stick_y);
        } else if (!liftWasStatic) {
            this.sideLoader.lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            this.sideLoader.lift.setTargetPosition(0);
            this.sideLoader.lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            this.sideLoader.lift.setPower(0.3);
            liftWasStatic = true;
        }
    }

    public void handleAgentMovement(double wheelFL, double wheelFR, double wheelBL, double wheelBR, double lift, int claw) {
        this.chassis.frontLeft.setPower(wheelFL);
        this.chassis.frontRight.setPower(wheelFR);
        this.chassis.backLeft.setPower(wheelBL);
        this.chassis.backRight.setPower(wheelBR);

        this.sideLoader.lift.setPower(lift);
        this.sideLoader.claw.setPosition(claw);
    }

    public void downGear() {
        if (this.chassis.getDriveSpeed() > 0) {
            this.chassis.setDriveSpeed(this.chassis.getDriveSpeed() - 0.1);
        }
    }

    public void upGear() {
        if (this.chassis.getDriveSpeed() < 1) {
            this.chassis.setDriveSpeed(this.chassis.getDriveSpeed() + 0.1);
        }
    }

    public void handleRL(float[] outputs) {
        this.chassis.frontLeft.setPower((double) outputs[0]);
        this.chassis.frontRight.setPower((double) outputs[1]);
        this.chassis.backLeft.setPower((double) outputs[2]);
        this.chassis.backRight.setPower((double) outputs[3]);
    }
}