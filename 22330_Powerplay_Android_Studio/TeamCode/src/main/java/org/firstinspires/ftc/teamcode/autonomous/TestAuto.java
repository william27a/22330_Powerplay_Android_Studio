package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.classes.Arena;
import org.firstinspires.ftc.teamcode.classes.RobotController;

@Autonomous(name = "Test Auto", group = "Experimental")
public class TestAuto extends LinearOpMode {
    private RobotController robot;
    private Arena arena;

    @Override
    public void runOpMode() {
        arena = new Arena(hardwareMap, false);
        robot = arena.getRobot();

        waitForStart();
    }
}