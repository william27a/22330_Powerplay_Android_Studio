package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

@TeleOp(name = "TestOp", group = "Experimental")
public class TestOp extends LinearOpMode {
    private DcMotor shoulder;

    private DcMotor arm;
    private boolean armWasStatic = false;
    
    @Override
    public void runOpMode() {
        shoulder = (DcMotor)hardwareMap.get("shoulder");
        arm = (DcMotor)hardwareMap.get("arm");

        arm.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();
        while (opModeIsActive()) {
            // Wat duh hek
            // Y values are negative when stick points up
            // positive when stick points down?!?!?!
            
            shoulder.setPower(gamepad1.right_stick_y/2);

            if (-gamepad1.right_stick_y != 0) {
                if (armWasStatic) {
                    armWasStatic = false;
                    arm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                }
                arm.setPower(-gamepad1.left_stick_y/2);
            } else if (!armWasStatic) {
                arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                arm.setTargetPosition((int) 0);
                arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                arm.setPower(0.3);
                armWasStatic = true;
            }
        }
    }
}