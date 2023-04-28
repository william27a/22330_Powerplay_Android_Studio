package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.classes.RobotController;
import org.firstinspires.ftc.teamcode.classes.RuntimeType;
import org.firstinspires.ftc.teamcode.classes.Side;

@TeleOp(name = "TestOp", group = "Experimental")
public class TestOp extends LinearOpMode {
    private RobotController robot;

    //private DcMotor shoulder;

    //private DcMotor arm;
    //private boolean armWasStatic = false;

    @Override
    public void runOpMode() {
        robot = new RobotController(hardwareMap, RuntimeType.DRIVER_CONTROLLED_TELEOP, Side.LEFT);

        //shoulder = (DcMotor) hardwareMap.get("shoulder");
        //arm = (DcMotor) hardwareMap.get("arm");

        //arm.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();
        //while (opModeIsActive()) {
            // forwards, right, clockwise
            /*

            shoulder.setPower(gamepad1.right_stick_y / 2);
            telemetry.addData("Shoulder power", gamepad1.right_stick_y/2);
            telemetry.update();

            if (-gamepad1.right_stick_y != 0) {
                if (armWasStatic) {
                    armWasStatic = false;
                    arm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                }
                arm.setPower(-gamepad1.left_stick_y / 2);
            } else if (!armWasStatic) {
                arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                arm.setTargetPosition(0);
                arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                arm.setPower(0.3);
                armWasStatic = true;
            }

            */
        //}
    }
}