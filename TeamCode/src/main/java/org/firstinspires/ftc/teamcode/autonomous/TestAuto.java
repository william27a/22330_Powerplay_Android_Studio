package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.robot.Robot;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.classes.Arena;
import org.firstinspires.ftc.teamcode.classes.CalibrationType;
import org.firstinspires.ftc.teamcode.classes.Global;
import org.firstinspires.ftc.teamcode.classes.RobotController;
import org.firstinspires.ftc.teamcode.classes.RuntimeType;
import org.firstinspires.ftc.teamcode.classes.Side;

@Autonomous(name = "Test Auto", group = "Experimental")
public class TestAuto extends LinearOpMode {

    void macroP1(RobotController robot) {
        robot.closeClaw();
        sleep(1000);
        robot.setLiftHeight(34, true);
        robot.liftBrake();
        robot.setDriveSpeed(0.5);
        robot.moveWithMap(-40.5, -40.5, -40.5, -40.5);
        sleep(100);
        robot.moveWithMap(6-40.5, -6-40.5, -6-40.5, 6-40.5);
        robot.openClaw();
    }

    void macroP2(RobotController robot) {
        robot.moveWithMap(-40.5, -40.5, -40.5, -40.5);
        sleep(100);
        robot.moveWithMap(0, 0, 0, 0);
        robot.setLiftHeight(0, true);
    }

    @Override
    public void runOpMode() {
        Arena arena = new Arena(hardwareMap, RuntimeType.HARDCODED_AUTO, Side.LEFT);
        RobotController robot = arena.getRobot();

        ElapsedTime time = new ElapsedTime();

        waitForStart();
        time.reset();

        macroP1(robot);
    }
}