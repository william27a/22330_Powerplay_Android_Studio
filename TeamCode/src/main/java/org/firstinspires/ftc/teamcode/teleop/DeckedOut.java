package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.classes.Arena;
import org.firstinspires.ftc.teamcode.classes.Global;
import org.firstinspires.ftc.teamcode.classes.RobotController;
import org.firstinspires.ftc.teamcode.classes.RuntimeType;
import org.firstinspires.ftc.teamcode.classes.Side;

@TeleOp(name = "Decked Out", group = "Experimental")
public class DeckedOut extends LinearOpMode {
    // Gyroscope systems
    private static final RevHubOrientationOnRobot.LogoFacingDirection[] logoFacingDirections
            = RevHubOrientationOnRobot.LogoFacingDirection.values();
    private static final RevHubOrientationOnRobot.UsbFacingDirection[] usbFacingDirections
            = RevHubOrientationOnRobot.UsbFacingDirection.values();
    // Drive systems
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private double x;
    private double y;
    private double pivot;
    private double normal;
    private double percent = 0.9;

    // Lift systems
    private DcMotor lift;
    private boolean liftWasStatic = false;

    // Claw systems
    private Servo claw;
    private IMU imu;
    private IMU.Parameters parameters;

    // Robot
    private RobotController robot;
    private Arena arena;

    private void handleMovement(double x, double y, double pivot) {
        double up = (Math.cos(arena.getRotationRadians()) * y) - (Math.sin(arena.getRotationRadians()) * x);
        double right = (Math.sin(arena.getRotationRadians()) * y) + (Math.cos(arena.getRotationRadians()) * x);

        x = right;
        y = up;

        normal = Math.abs(x) + Math.abs(y) + Math.abs(pivot);
        if (normal < 1) {
            normal = 1;
        }
        normal /= percent;

        frontLeft.setPower((y + x + pivot) / normal);
        frontRight.setPower((y - x - pivot) / normal);
        backLeft.setPower((y - x + pivot) / normal);
        backRight.setPower((y + x - pivot) / normal);
    }

    @Override
    public void runOpMode() {
        frontLeft = (DcMotor) hardwareMap.get("frontLeft");
        frontRight = (DcMotor) hardwareMap.get("frontRight");
        backLeft = (DcMotor) hardwareMap.get("backLeft");
        backRight = (DcMotor) hardwareMap.get("backRight");

        lift = (DcMotor) hardwareMap.get("lift");
        claw = (Servo) hardwareMap.get("claw");

        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        lift.setDirection(DcMotor.Direction.REVERSE);
        claw.setDirection(Servo.Direction.FORWARD);

        claw.scaleRange(0.15, 0.25);

        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot.LogoFacingDirection logo = logoFacingDirections[0]; // Up
        RevHubOrientationOnRobot.UsbFacingDirection usb = usbFacingDirections[5]; // Right
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logo, usb);
        imu.initialize(new IMU.Parameters(orientationOnRobot));

        arena = new Arena(hardwareMap, RuntimeType.DRIVER_CONTROLLED_TELEOP, Side.LEFT);
        robot = arena.getRobot();

        robot.setWheelMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();

        while (opModeIsActive()) {
            // assign values to each variable included in chassis math
            pivot = gamepad1.right_trigger - gamepad1.left_trigger;
            x = gamepad1.left_stick_x;
            y = -gamepad1.left_stick_y;

            // set motor powers
            handleMovement(x, y, pivot);

            if (-gamepad1.right_stick_y != 0) {
                if (liftWasStatic) {
                    liftWasStatic = false;
                    lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                }
                lift.setPower(-gamepad1.right_stick_y);
            } else if (!liftWasStatic) {
                lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                lift.setTargetPosition(0);
                lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                lift.setPower(0.3);
                liftWasStatic = true;
            }

            if (gamepad1.left_bumper) {
                if (!gamepad1.right_bumper) {
                    claw.setPosition(0);
                }
            } else if (gamepad1.right_bumper) {
                claw.setPosition(1);
            }

            if (gamepad1.y) {
                percent = 1;
            }
            if (gamepad1.a) {
                percent = 0.9;
            }
        }
    }
}