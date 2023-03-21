package org.firstinspires.ftc.teamcode.classes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class RobotController extends LinearOpMode {
    public Chassis chassis;
    private final LongGrabber longGrabber;
    private final SideLoader sideLoader;

    // TeleOp
    private double x;
    private double y;
    private double pivot;
    private double normal;
    private boolean liftWasStatic;

    public RobotController(Chassis chassis, LongGrabber longGrabber, SideLoader sideLoader, RuntimeType type) {
        this.chassis = chassis;
        this.longGrabber = longGrabber;
        this.sideLoader = sideLoader;
    }

    public RobotController(HardwareMap map, RuntimeType type) {
        this.chassis = new Chassis(map);
        this.longGrabber = new LongGrabber(map);
        this.sideLoader = new SideLoader(map);

        if (type == RuntimeType.DRIVER_CONTROLLED_TELEOP) {
            this.readyTeleOp();
        } else if (type == RuntimeType.AGENT_CONTROLLED_TELEOP || type == RuntimeType.AGENT_CONTROLLED_AUTO) {
            this.prepareRL();
        }
    }

    public void readyTeleOp() {
        this.chassis.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void prepareRL() {
        this.chassis.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        this.chassis.prepareRL();
    }

    // Set variables
    public void setDriveSpeed(double x) {
        this.chassis.setDriveSpeed(x);
    }

    public void setRotationSpeed(double x) {
        this.chassis.setRotationSpeed(x);
    }

    public void setShoulderSpeed(double x) {
        this.longGrabber.setShoulderSpeed(x);
    }

    public void setArmSpeed(double x) {
        this.longGrabber.setArmSpeed(x);
    }

    public void setLiftSpeed(double x) {
        this.sideLoader.setLiftSpeed(x);
    }

    // Drive systems
    public void setWheelMode(DcMotor.RunMode mode) {
        this.chassis.setMode(mode);
    }

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

    public double getRotationDegrees() {
        return this.chassis.getRotationDegrees();
    }

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
    public void setShoulderDegrees(double degrees, boolean wait) {
        this.longGrabber.setShoulderDegrees(degrees, wait);
    }

    public double getShoulderDegrees() {
        return this.longGrabber.getShoulderDegrees();
    }

    public void setShoulderRadians(double radians, boolean wait) {
        this.longGrabber.setShoulderRadians(radians, wait);
    }

    public double getShoulderRadians() {
        return this.longGrabber.getShoulderRadians();
    }

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
    public void armBrake() {
        this.longGrabber.armBrake();
    }

    public void stopArmBrake() {
        this.longGrabber.stopArmBrake();
    }

    public double getArmZ() {
        return this.longGrabber.getArmZ();
    }

    public void setArmZ(double z, boolean wait) {
        this.longGrabber.setArmZ(z, wait);
    }

    public double getHandZ() {
        return this.getArmZ() + Global.HAND_AWAY_FROM_ARM + (Global.HAND_LENGTH / 2);
    }

    public void setHandZ(double inches, boolean wait) {
        this.setArmZ(inches - Global.HAND_AWAY_FROM_ARM - (Global.HAND_LENGTH / 2), wait);
    }

    public void openHand() {
        this.longGrabber.openHand();
    }

    public void openHandFully() {
        this.longGrabber.openHandFully();
    }

    public void closeHand() {
        this.longGrabber.closeHand();
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
        while (
                (this.longGrabber != null && this.longGrabber.isBusy()) ||
                        (this.sideLoader != null && this.sideLoader.isBusy())
        ) {}
    }

    public void deactivate() {
        this.longGrabber.deactivate();
        this.sideLoader.deactivate();
    }

    @Override
    public void runOpMode() {
    }

    public void handleMovement(Gamepad gamepad1) {
        if (gamepad1.b) {
            this.setArmZ(0, false);
            this.setShoulderDegrees(0, false);
        }

        // assign values to each variable included in chassis math
        pivot = gamepad1.right_trigger - gamepad1.left_trigger;
        x = gamepad1.left_stick_x;
        y = -gamepad1.left_stick_y;
        normal = Math.abs(pivot) + Math.abs(x) + Math.abs(y);
        if (normal < 1) {
            normal = 1;
        }

        // set motor powers
        this.chassis.frontLeft.setPower((y + x + pivot) / normal);
        this.chassis.frontRight.setPower((y - x - pivot) / normal);
        this.chassis.backLeft.setPower((y - x + pivot) / normal);
        this.chassis.backRight.setPower((y + x - pivot) / normal);

        if (gamepad1.left_bumper) {
            if (!gamepad1.right_bumper) {
                this.sideLoader.claw.setPosition(1);
            }
        } else if (gamepad1.right_bumper) {
            this.sideLoader.claw.setPosition(0);
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

    public void handleMovementBackwards(Gamepad gamepad1) {
        if (gamepad1.b) {
            this.setArmZ(0, false);
            this.setShoulderDegrees(0, false);
        }

        // assign values to each variable included in chassis math
        pivot = gamepad1.right_trigger - gamepad1.left_trigger;
        x = -gamepad1.left_stick_x;
        y = gamepad1.left_stick_y;
        normal = Math.abs(pivot) + Math.abs(x) + Math.abs(y);
        if (normal < 1) {
            normal = 1;
        }

        // set motor powers
        this.chassis.frontLeft.setPower((y + x + pivot) / normal);
        this.chassis.frontRight.setPower((y - x - pivot) / normal);
        this.chassis.backLeft.setPower((y - x + pivot) / normal);
        this.chassis.backRight.setPower((y + x - pivot) / normal);

        if (gamepad1.left_bumper) {
            if (!gamepad1.right_bumper) {
                this.sideLoader.claw.setPosition(1);
            }
        } else if (gamepad1.right_bumper) {
            this.sideLoader.claw.setPosition(0);
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

    public void setMotorPowers(double fl, double fr, double bl, double br) {
        this.chassis.frontLeft.setPower(fl);
        this.chassis.frontRight.setPower(fr);
        this.chassis.backLeft.setPower(bl);
        this.chassis.backRight.setPower(br);
    }
}