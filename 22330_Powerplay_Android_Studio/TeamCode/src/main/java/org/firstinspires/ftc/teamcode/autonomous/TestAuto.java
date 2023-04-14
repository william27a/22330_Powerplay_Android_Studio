package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.classes.Arena;
import org.firstinspires.ftc.teamcode.classes.Global;
import org.firstinspires.ftc.teamcode.classes.RobotController;
import org.firstinspires.ftc.teamcode.classes.RuntimeType;
import org.firstinspires.ftc.teamcode.classes.Side;

@Autonomous(name = "Test Auto", group = "Experimental")
public class TestAuto extends LinearOpMode {

    @Override
    public void runOpMode() {
        Arena arena = new Arena(hardwareMap, RuntimeType.HARDCODED_AUTO, Side.LEFT);
        RobotController robot = arena.getRobot();

        ElapsedTime time = new ElapsedTime();

        waitForStart();
    }
}