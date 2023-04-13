package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.classes.Arena;
import org.firstinspires.ftc.teamcode.classes.Global;
import org.firstinspires.ftc.teamcode.classes.RobotController;
import org.firstinspires.ftc.teamcode.classes.RuntimeType;
import org.firstinspires.ftc.teamcode.classes.Side;

@TeleOp(name = "Right Side", group = "Experimental")
public class RightSide extends LinearOpMode {

    private RobotController robot;
    private Arena arena;

    private boolean upGearPressed = false;
    private boolean downGearPressed = false;

    @Override
    public void runOpMode() {
        arena = new Arena(hardwareMap, RuntimeType.DRIVER_CONTROLLED_TELEOP, Side.RIGHT);
        robot = arena.getRobot();

        waitForStart();
        while (opModeIsActive()) {
            robot.handleMovement(gamepad1);

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
