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

    @Override
    public void runOpMode() {
        arena = new Arena(hardwareMap, RuntimeType.DRIVER_CONTROLLED_TELEOP, Side.LEFT);
        robot = arena.getRobot();

        waitForStart();
        while (opModeIsActive()) {
            robot.handleMovement(gamepad1);
        }
    }
}