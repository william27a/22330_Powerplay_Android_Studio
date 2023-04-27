package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.classes.Arena;
import org.firstinspires.ftc.teamcode.classes.Global;
import org.firstinspires.ftc.teamcode.classes.RobotController;
import org.firstinspires.ftc.teamcode.classes.RuntimeType;
import org.firstinspires.ftc.teamcode.classes.Side;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@TeleOp(name = "Left Side", group = "Experimental")
public class LeftSide extends LinearOpMode {

    private RobotController robot;
    private Arena arena;

    float pivot;
    float x;
    float y;
    float normal;
    boolean liftWasStatic = true;

    float[] powers = new float[4];

    File file = new File("sdcard/FIRST/tflitemodels/motorPowers.txt");
    FileWriter fileWriter;

    @Override
    public void runOpMode() {
        arena = new Arena(hardwareMap, RuntimeType.DRIVER_CONTROLLED_TELEOP, Side.LEFT);
        robot = arena.getRobot();

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            fileWriter = new FileWriter("sdcard/FIRST/tflitemodels/motorPowers.txt", true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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

            // set motor powers

            powers[0] = (y + x + pivot) / normal;
            powers[1] = (y - x - pivot) / normal;
            powers[2] = (y - x + pivot) / normal;
            powers[3] = (y + x - pivot) / normal;

            robot.chassis.frontLeft.setPower((y + x + pivot) / normal);
            robot.chassis.frontRight.setPower((y - x - pivot) / normal);
            robot.chassis.backLeft.setPower((y - x + pivot) / normal);
            robot.chassis.backRight.setPower((y + x - pivot) / normal);

            if (gamepad1.left_bumper) {
                if (!gamepad1.right_bumper) {
                    robot.sideLoader.claw.setPosition(0);
                }
            } else if (gamepad1.right_bumper) {
                robot.sideLoader.claw.setPosition(1);
                powers[5] = 1;
            } else {
                powers[5] = 0;
            }

            if (-gamepad1.right_stick_y != 0) {
                if (liftWasStatic) {
                    liftWasStatic = false;
                    robot.sideLoader.lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                }
                robot.sideLoader.lift.setPower(-gamepad1.right_stick_y);
                powers[4] = -gamepad1.right_stick_y;
            } else if (!liftWasStatic) {
                robot.sideLoader.lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                robot.sideLoader.lift.setTargetPosition(0);
                robot.sideLoader.lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                robot.sideLoader.lift.setPower(0.3);
                powers[4] = 1;
                liftWasStatic = true;
            }
            try {
                fileWriter.write(powers[0] + " " + powers[1] + " " + powers[2] + " " + powers[3] + " " + powers[4] + " " + powers[5] + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}