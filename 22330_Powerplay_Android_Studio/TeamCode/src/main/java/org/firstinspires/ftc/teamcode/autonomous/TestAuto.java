package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.classes.Arena;
import org.firstinspires.ftc.teamcode.classes.RobotController;
import org.firstinspires.ftc.teamcode.classes.RuntimeType;

@Autonomous(name = "Test Auto", group = "Experimental")
public class TestAuto extends LinearOpMode {

    @Override
    public void runOpMode() {
        Arena arena = new Arena(hardwareMap, RuntimeType.HARDCODED_AUTO, false);
        RobotController robot = arena.getRobot();

        waitForStart();
    }
}