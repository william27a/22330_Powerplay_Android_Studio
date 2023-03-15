package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Manual Orientation", group = "Experimental")
public class ManualOrientation extends LinearOpMode {
    // Drive systems
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    private double frontLeftPower;
    private double frontRightPower;
    private double backLeftPower;
    private double backRightPower;

    private double fLX = 1;
    private double fRX = 1;
    private double bLX = 1;
    private double bRX = 1;

    private double fLY = 1;
    private double fRY = 1;
    private double bLY = 1;
    private double bRY = 1;

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

    private void handleMovement(double x, double y, double pivot) {
        normal = Math.abs(x) + Math.abs(y) + Math.abs(pivot);
        if (normal < 1) {
            normal = 1;
        }
        normal /= percent;

        frontLeft.setPower(((fLY * y) + (fLX * x) + pivot) / normal);
        frontRight.setPower(((fRY * y) - (fRX * x) - pivot) / normal);
        backLeft.setPower(((bLY * y) - (bLX * x) + pivot) / normal);
        backRight.setPower(((bRY * y) + (bRX * x) - pivot) / normal);
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

        claw.scaleRange(0.16, 0.25);

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

            if (gamepad1.y) {
                fLX = 1;
                fRX = 1;
                bLX = 1;
                bRX = 1;

                fLY = 1;
                fRY = 1;
                bLY = 1;
                bRY = 1;
            }
            if (gamepad1.a) {
                fLX = -1;
                fRX = -1;
                bLX = -1;
                bRX = -1;

                fLY = -1;
                fRY = -1;
                bLY = -1;
                bRY = -1;
            }
            if (gamepad1.x) {
                fLX = -1;
                fRX = 1;
                bLX = 1;
                bRX = -1;

                fLY = 1;
                fRY = -1;
                bLY = -1;
                bRY = 1;
            }
            if (gamepad1.b) {
                fLX = 1;
                fRX = -1;
                bLX = -1;
                bRX = 1;

                fLY = -1;
                fRY = 1;
                bLY = 1;
                bRY = -1;
            }
        }
    }
}