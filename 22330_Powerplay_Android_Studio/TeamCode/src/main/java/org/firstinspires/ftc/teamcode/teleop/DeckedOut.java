package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.teamcode.classes.Arena;
import org.firstinspires.ftc.teamcode.classes.Global;
import org.firstinspires.ftc.teamcode.classes.RobotController;

@TeleOp(name = "Decked Out", group = "Experimental")
public class DeckedOut extends LinearOpMode {
    // Drive systems
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    private double frontLeftPower;
    private double frontRightPower;
    private double backLeftPower;
    private double backRightPower;

    private double x;
    private double y;
    private double pivot;
    private double normal;
    private double percent = 0.9;

    // Shoulder systems
    private TouchSensor limitSwitch;
    private DcMotor shoulder;

    // Arm systems
    private DcMotor arm;

    // Hand systems
    private Servo hand;

    // Lift systems
    private DcMotor lift;
    private boolean liftWasStatic = false;

    // Claw systems
    private Servo claw;

    // Gyroscope systems
    private static RevHubOrientationOnRobot.LogoFacingDirection[] logoFacingDirections
            = RevHubOrientationOnRobot.LogoFacingDirection.values();
    private static RevHubOrientationOnRobot.UsbFacingDirection[] usbFacingDirections
            = RevHubOrientationOnRobot.UsbFacingDirection.values();

    private IMU imu;
    private IMU.Parameters parameters;

    // Robot
    private RobotController robot;
    private Arena arena;

    // Macros
    private int leftStackCones = 5;
    private int rightStackCones = 5;
    private int junction = 3;

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

        frontLeft.setPower((y+x+pivot)/normal);
        frontRight.setPower((y-x-pivot)/normal);
        backLeft.setPower((y-x+pivot)/normal);
        backRight.setPower((y+x-pivot)/normal);
    }

    /*private void extendToStack(int addCones) {
        double leftDistance = (Math.abs(arena.getRotationDegrees() - 90)) % 360;
        double rightDistance = (Math.abs(arena.getRotationDegrees() - 270)) % 360;

        if (leftDistance < rightDistance) {
            leftStackCones += addCones;

            robot.openHand();

            double height = Global.coneHeightFromStack(leftStackCones) - Global.SHOULDER_OFF_THE_GROUND + 2;
            double length = 20;
            double totalHypotenuse = Global.getHypotenuse(height, length);

            arena.setShoulderRadians(Math.atan(height/length), true);
            robot.setHandZ(totalHypotenuse, true);

            leftStackCones--;
        } else {
            rightStackCones += addCones;
            robot.openHand();

            double height = Global.coneHeightFromStack(rightStackCones) - Global.SHOULDER_OFF_THE_GROUND + 2;
            double length = ;
            double width = Global.SHOULDER_LEFT_FROM_CENTER;
            double topDownHypotenuse = Global.getHypotenuse(length, width);
            double totalHypotenuse = Global.getHypotenuse(height, topDownHypotenuse);

            arena.setShoulderRadians(Math.atan(height/topDownHypotenuse), true);
            robot.setHandZ(totalHypotenuse, true);

            rightStackCones--;
        }
    }*/

    /*private void extendToJunction(int junction) {
        double length = 24;

        double height = -Global.SHOULDER_OFF_THE_GROUND + 2;

        if (junction == 1) {
            height += Global.LOW_JUNCTION_HEIGHT;
        } else if (junction == 2) {
            height += Global.MEDIUM_JUNCTION_HEIGHT;
        } else {
            height += Global.HIGH_JUNCTION_HEIGHT;
        }

        double totalHypotenuse = Global.getHypotenuse(height, length);

        arena.setShoulderRadians(Math.atan(height/length), true);
        robot.setHandZ(totalHypotenuse, true);
    }*/

    @Override
    public void runOpMode() {
        frontLeft = (DcMotor)hardwareMap.get("frontLeft");
        frontRight = (DcMotor)hardwareMap.get("frontRight");
        backLeft = (DcMotor)hardwareMap.get("backLeft");
        backRight = (DcMotor)hardwareMap.get("backRight");

        limitSwitch = (TouchSensor)hardwareMap.get("limitSwitch");
        shoulder = (DcMotor)hardwareMap.get("shoulder");
        arm = (DcMotor)hardwareMap.get("arm");
        hand = (Servo)hardwareMap.get("hand");

        lift = (DcMotor)hardwareMap.get("lift");
        claw = (Servo)hardwareMap.get("claw");

        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        shoulder.setDirection(DcMotor.Direction.REVERSE);
        arm.setDirection(DcMotor.Direction.REVERSE);
        hand.setDirection(Servo.Direction.FORWARD);

        lift.setDirection(DcMotor.Direction.REVERSE);
        claw.setDirection(Servo.Direction.FORWARD);

        hand.scaleRange(0.4, 0.55);
        claw.scaleRange(0.15, 0.25);

        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot.LogoFacingDirection logo = logoFacingDirections[0]; // Up
        RevHubOrientationOnRobot.UsbFacingDirection usb = usbFacingDirections[5]; // Right
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logo, usb);
        imu.initialize(new IMU.Parameters(orientationOnRobot));

        arena = new Arena(hardwareMap, false);
        robot = arena.getRobot();

        robot.setWheelMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();

        while (opModeIsActive()) {
            /*if (gamepad1.x) {
                extendToStack(0);
                while (!gamepad1.right_bumper) {
                    pivot = gamepad1.right_trigger - gamepad1.left_trigger;
                    x = gamepad1.left_stick_x;
                    y = -gamepad1.left_stick_y;

                    // set motor powers
                    handleMovement(x, y, pivot);

                    if (gamepad1.x) {
                        extendToStack(2);
                    }
                    if (gamepad1.b) {
                        extendToStack(0);
                    }
                }
                robot.closeHand();
                robot.resetShoulderUpDown(1);
                robot.setHandZ(robot.getHandZ()-10, false);

                while (!gamepad1.b) {}

                robot.deactivate();
            }*/

            /*if (gamepad1.b) {
                extendToJunction(junction);
                while (!gamepad1.left_bumper) {
                    pivot = gamepad1.right_trigger - gamepad1.left_trigger;
                    x = gamepad1.left_stick_x;
                    y = -gamepad1.left_stick_y;

                    // set motor powers
                    handleMovement(x, y, pivot);

                    if (gamepad1.x && junction < 3) {
                        junction += 1;
                        extendToJunction(junction);
                    }
                    if (gamepad1.b && junction > 1) {
                        junction -= 1;
                        extendToJunction(junction);
                    }
                }
                hand.setPosition(0);
            }*/

            if (gamepad1.b) {
                robot.setArmZ(0, false);
                robot.setShoulderDegrees(0, false);
            }

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
                lift.setTargetPosition((int) 0);
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