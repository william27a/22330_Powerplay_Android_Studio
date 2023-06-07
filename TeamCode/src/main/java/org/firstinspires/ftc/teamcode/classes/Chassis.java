package org.firstinspires.ftc.teamcode.classes;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;

public class Chassis {
    private static final RevHubOrientationOnRobot.LogoFacingDirection[] logoFacingDirections
            = RevHubOrientationOnRobot.LogoFacingDirection.values();
    private static final RevHubOrientationOnRobot.UsbFacingDirection[] usbFacingDirections
            = RevHubOrientationOnRobot.UsbFacingDirection.values();
    public DcMotorEx frontLeft;
    public DcMotorEx frontRight;
    public DcMotorEx backLeft;
    public DcMotorEx backRight;
    private final IMU imu;

    PIDController pidFL = new PIDController(2.5, 4, 10);
    PIDController pidFR = new PIDController(2.5, 4, 10);
    PIDController pidBL = new PIDController(2.5, 4, 10);
    PIDController pidBR = new PIDController(2.5, 4, 10);

    private double driveSpeed = 0.6;
    private double rotationSpeed = 0.4;

    public double targetSpeed = 1;

    public Chassis(HardwareMap map) {
        this.frontLeft = (DcMotorEx) map.get("frontLeft");
        this.frontRight = (DcMotorEx) map.get("frontRight");
        this.backLeft = (DcMotorEx) map.get("backLeft");
        this.backRight = (DcMotorEx) map.get("backRight");

        this.imu = (IMU) map.get("imu");

        this.init();
    }

    public Chassis(DcMotorEx frontLeft, DcMotorEx frontRight, DcMotorEx backLeft, DcMotorEx backRight, IMU imu) {
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;

        this.imu = imu;

        this.init();
    }

    public void init() {
        this.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        this.frontLeft.setDirection(DcMotor.Direction.REVERSE);
        this.frontRight.setDirection(DcMotor.Direction.FORWARD);
        this.backLeft.setDirection(DcMotor.Direction.REVERSE);
        this.backRight.setDirection(DcMotor.Direction.FORWARD);

        RevHubOrientationOnRobot.LogoFacingDirection logo = logoFacingDirections[0]; // Up
        RevHubOrientationOnRobot.UsbFacingDirection usb = usbFacingDirections[5]; // Right
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logo, usb);
        this.imu.initialize(new IMU.Parameters(orientationOnRobot));
    }

    public void reverso() {
        this.frontLeft.setDirection(DcMotor.Direction.FORWARD);
        this.frontRight.setDirection(DcMotor.Direction.REVERSE);
        this.backLeft.setDirection(DcMotor.Direction.FORWARD);
        this.backRight.setDirection(DcMotor.Direction.REVERSE);
    }

    public double getDriveSpeed() {
        return this.driveSpeed;
    }

    public void setDriveSpeed(double driveSpeed) {
        this.driveSpeed = driveSpeed;
    }

    public double getRotationSpeed() {
        return this.rotationSpeed;
    }

    public void setRotationSpeed(double rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    public void setMode(DcMotor.RunMode mode) {
        this.frontLeft.setMode(mode);
        this.frontRight.setMode(mode);
        this.backLeft.setMode(mode);
        this.backRight.setMode(mode);
    }

    public void move(double inchesFL, double inchesFR, double inchesBL, double inchesBR) {
        this.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        this.frontLeft.setTargetPosition((int) (inchesFL * Global.DRIVE_CPI));
        this.frontRight.setTargetPosition((int) (inchesFR * Global.DRIVE_CPI));
        this.backLeft.setTargetPosition((int) (inchesBL * Global.DRIVE_CPI));
        this.backRight.setTargetPosition((int) (inchesBR * Global.DRIVE_CPI));

        this.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        this.frontLeft.setPower(this.driveSpeed);
        this.frontRight.setPower(this.driveSpeed);
        this.backLeft.setPower(this.driveSpeed);
        this.backRight.setPower(this.driveSpeed);

        while (this.frontLeft.isBusy() || this.frontRight.isBusy() || this.backLeft.isBusy() || this.backRight.isBusy()) {}

        this.frontLeft.setPower(0);
        this.frontRight.setPower(0);
        this.backLeft.setPower(0);
        this.backRight.setPower(0);
    }

    public void moveWPID(double inchesFL, double inchesFR, double inchesBL, double inchesBR) {
        this.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        inchesFL *= Global.DRIVE_CPI;
        inchesFR *= Global.DRIVE_CPI;
        inchesBL *= Global.DRIVE_CPI;
        inchesBR *= Global.DRIVE_CPI;

        this.pidFL.setSetpoint(inchesFL);
        this.pidFR.setSetpoint(inchesFR);
        this.pidBL.setSetpoint(inchesBL);
        this.pidBR.setSetpoint(inchesBR);

        this.frontLeft.setTargetPosition((int) inchesFL);
        this.frontRight.setTargetPosition((int) inchesFR);
        this.backLeft.setTargetPosition((int) inchesBL);
        this.backRight.setTargetPosition((int) inchesBR);

        this.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        while (this.isBusy()) {
            double powerFL = pidFL.getOutput(this.frontLeft.getCurrentPosition() * targetSpeed);
            double powerFR = pidFR.getOutput(this.frontRight.getCurrentPosition() * targetSpeed);
            double powerBL = pidBL.getOutput(this.backLeft.getCurrentPosition() * targetSpeed);
            double powerBR = pidBR.getOutput(this.backRight.getCurrentPosition() * targetSpeed);

            this.frontLeft.setPower(powerFL);
            this.frontRight.setPower(powerFR);
            this.backLeft.setPower(powerBL);
            this.backRight.setPower(powerBR);
        }

        this.frontLeft.setPower(0);
        this.frontRight.setPower(0);
        this.backLeft.setPower(0);
        this.backRight.setPower(0);
    }

    public void moveWithMap(double inchesFL, double inchesFR, double inchesBL, double inchesBR) {
        this.frontLeft.setTargetPosition((int) (inchesFL * Global.DRIVE_CPI));
        this.frontRight.setTargetPosition((int) (inchesFR * Global.DRIVE_CPI));
        this.backLeft.setTargetPosition((int) (inchesBL * Global.DRIVE_CPI));
        this.backRight.setTargetPosition((int) (inchesBR * Global.DRIVE_CPI));

        this.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        this.frontLeft.setPower(this.driveSpeed);
        this.frontRight.setPower(this.driveSpeed);
        this.backLeft.setPower(this.driveSpeed);
        this.backRight.setPower(this.driveSpeed);

        while (this.frontLeft.isBusy() || this.frontRight.isBusy() || this.backLeft.isBusy() || this.backRight.isBusy()) {}

        this.frontLeft.setPower(0);
        this.frontRight.setPower(0);
        this.backLeft.setPower(0);
        this.backRight.setPower(0);
    }

    public double getRotationDegrees() {
        return this.imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    public void setRotationDegrees(double degrees, double speed) {
        this.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        double currentDegrees = this.getRotationDegrees();
        double relativeDegrees = Global.fitToRange(currentDegrees, degrees - 180, degrees + 180);
        double direction;
        double dif;

        while (!((relativeDegrees < degrees && (relativeDegrees + Global.DEGREE_TOLERANCE) > degrees)
                || (relativeDegrees > degrees && (relativeDegrees - Global.DEGREE_TOLERANCE) < degrees))) {
            dif = degrees - relativeDegrees;
            direction = dif / (Math.abs(dif));

            this.frontLeft.setPower(-direction * this.rotationSpeed * speed);
            this.frontRight.setPower(direction * this.rotationSpeed * speed);
            this.backLeft.setPower(-direction * this.rotationSpeed * speed);
            this.backRight.setPower(direction * this.rotationSpeed * speed);

            currentDegrees = this.getRotationDegrees();
            relativeDegrees = Global.fitToRange(currentDegrees, degrees - 180, degrees + 180);
        }

        this.frontLeft.setPower(0);
        this.frontRight.setPower(0);
        this.backLeft.setPower(0);
        this.backRight.setPower(0);
    }

    public double getRotationRadians() {
        return this.imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
    }

    public void setRotationRadians(double radians, double speed) {
        this.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        double currentRadians = this.getRotationRadians();
        double relativeRadians = Global.fitToRange(currentRadians, radians - 180, radians + 180);
        double direction;
        double dif;

        while (!((relativeRadians < radians && (relativeRadians + Global.RADIAN_TOLERANCE) > radians)
                || (relativeRadians > radians && (relativeRadians - Global.RADIAN_TOLERANCE) < radians))) {
            dif = radians - relativeRadians;
            direction = dif / (Math.abs(dif));

            this.frontLeft.setPower(-direction * this.rotationSpeed * speed);
            this.frontRight.setPower(direction * this.rotationSpeed * speed);
            this.backLeft.setPower(-direction * this.rotationSpeed * speed);
            this.backRight.setPower(direction * this.rotationSpeed * speed);

            currentRadians = this.getRotationRadians();
            relativeRadians = Global.fitToRange(currentRadians, radians - 180, radians + 180);
        }

        this.frontLeft.setPower(0);
        this.frontRight.setPower(0);
        this.backLeft.setPower(0);
        this.backRight.setPower(0);
    }

    public void moveNicely(double inchesFL, double inchesFR, double inchesBL, double inchesBR, int checks) {
        this.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        this.frontLeft.setTargetPosition((int) (inchesFL * Global.DRIVE_CPI));
        this.frontRight.setTargetPosition((int) (inchesFR * Global.DRIVE_CPI));
        this.backLeft.setTargetPosition((int) (inchesBL * Global.DRIVE_CPI));
        this.backRight.setTargetPosition((int) (inchesBR * Global.DRIVE_CPI));

        this.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        this.frontLeft.setPower(this.driveSpeed);
        this.frontRight.setPower(this.driveSpeed);
        this.backLeft.setPower(this.driveSpeed);
        this.backRight.setPower(this.driveSpeed);

        boolean below = this.frontLeft.getCurrentPosition() < this.frontLeft.getTargetPosition();
        while (this.frontLeft.isBusy() || this.frontRight.isBusy() || this.backLeft.isBusy() || this.backRight.isBusy()) {
            if (this.frontLeft.getCurrentPosition() < this.frontLeft.getTargetPosition() != below) {
                below = !below;
                checks--;
            }

            if (checks <= 0) {
                break;
            }
        }

        this.frontLeft.setPower(0);
        this.frontRight.setPower(0);
        this.backLeft.setPower(0);
        this.backRight.setPower(0);
    }

    public double[] getTicks() {
        return new double[]{
                this.frontLeft.getCurrentPosition(), this.frontRight.getCurrentPosition(), this.backLeft.getCurrentPosition(), this.backRight.getCurrentPosition()
        };
    }

    public boolean isBusy() {
        return this.frontLeft.isBusy() || this.frontRight.isBusy() || this.backLeft.isBusy() || this.backRight.isBusy();
    }
}