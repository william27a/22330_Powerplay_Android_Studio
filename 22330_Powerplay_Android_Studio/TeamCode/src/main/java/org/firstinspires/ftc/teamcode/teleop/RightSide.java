package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.classes.Arena;
import org.firstinspires.ftc.teamcode.classes.RobotController;

@TeleOp(name = "Right Side", group = "Experimental")
public class RightSide extends LinearOpMode {

    private RobotController robot;
    private Arena arena;

    private boolean upGearPressed = false;
    private boolean downGearPressed = false;

    @Override
    public void runOpMode() {
        arena = new Arena(hardwareMap, false);
        robot = arena.getRobot();

        robot.readyTeleOp();

        robot.chassis.frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        robot.chassis.frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        robot.chassis.backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        robot.chassis.backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        waitForStart();
        while (opModeIsActive()) {
            robot.handleMovement();

            if (gamepad1.y) {
                if (!upGearPressed && !gamepad1.b) {
                    upGearPressed = true;
                    robot.upGear();
                }
            } else {
                upGearPressed = false;
            }

            if (gamepad1.b) {
                if (!downGearPressed && !gamepad1.y) {
                    downGearPressed = true;
                    robot.downGear();
                }
            } else {
                downGearPressed = false;
            }
        }
    }
}
