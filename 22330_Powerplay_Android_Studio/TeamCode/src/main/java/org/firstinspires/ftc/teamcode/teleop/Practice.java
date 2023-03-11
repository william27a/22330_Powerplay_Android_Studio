package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "90% Practice", group = "Experimental")
public class Practice extends LinearOpMode {
    // Drive systems
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    
    private double x;
    private double y;
    private double pivot;
    private double normal;
    private double percent = 0.6;
    
    // Lift systems
    private DcMotor lift;
    private boolean liftWasStatic = false;
    
    // Claw systems
    private Servo claw;
    
    @Override
    public void runOpMode() {
        frontLeft = (DcMotor)hardwareMap.get("frontLeft");
        frontRight = (DcMotor)hardwareMap.get("frontRight");
        backLeft = (DcMotor)hardwareMap.get("backLeft");
        backRight = (DcMotor)hardwareMap.get("backRight");
        
        lift = (DcMotor)hardwareMap.get("lift");
        claw = (Servo)hardwareMap.get("claw");
        
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        
        lift.setDirection(DcMotor.Direction.FORWARD);
        claw.setDirection(Servo.Direction.FORWARD);

        claw.scaleRange(0.15, 0.25);

        waitForStart();
        while (opModeIsActive()) {
            // assign values to each variable included in chassis math
            pivot = gamepad1.right_trigger - gamepad1.left_trigger;
            x = gamepad1.left_stick_x;
            y = -gamepad1.left_stick_y;
            normal = Math.abs(pivot) + Math.abs(x) + Math.abs(y);
            if (normal < 1) {
                normal = 1;
            }
            normal /= percent;
            
            // set motor powers
            frontLeft.setPower((y+x+pivot)/normal);
            frontRight.setPower((y-x-pivot)/normal);
            backLeft.setPower((y-x+pivot)/normal);
            backRight.setPower((y+x-pivot)/normal);
            
            // left bumper opens
            // positive power closes
            if (gamepad1.left_bumper) {
                if (!gamepad1.right_bumper) {
                    claw.setPosition(1);
                }
            } else if (gamepad1.right_bumper) {
                claw.setPosition(0);
            }

            // if the lift has no power, have it constantly move to its current location
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
        }
    }
}
