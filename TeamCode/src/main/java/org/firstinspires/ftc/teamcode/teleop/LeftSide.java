package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.classes.Arena;
import org.firstinspires.ftc.teamcode.classes.RobotController;
import org.firstinspires.ftc.teamcode.classes.RuntimeType;

@TeleOp(name = "Left Side", group = "Experimental")
public class LeftSide extends LinearOpMode {

    private RobotController robot;
    private Arena arena;

    boolean upGearPressed = false;
    boolean downGearPressed = false;

    @Override
    public void runOpMode() {
        arena = new Arena(hardwareMap, RuntimeType.DRIVER_CONTROLLED_TELEOP);
        robot = arena.getRobot();

        robot.setWheelMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.chassis.setDriveSpeed(0.7);

        waitForStart();
        while (opModeIsActive()) {
            robot.handleMovementLeft(gamepad1);

            if (gamepad1.y) {
                if (!upGearPressed) {
                    robot.upGear();
                }
                upGearPressed = true;
            } else {
                upGearPressed = false;
            }

            if (gamepad1.a) {
                if (!downGearPressed) {
                    robot.downGear();
                }
                downGearPressed = true;
            } else {
                downGearPressed = false;
            }
        }
    }
}