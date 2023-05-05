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

    void macroP1(Arena arena, RobotController robot) {
        robot.closeClaw();
        sleep(1000);
        robot.setLiftHeight(34, true);
        robot.liftBrake();
        //robot.moveWithMap(-40.5, -40.5, -40.5, -40.5);
        robot.move(-40.5, -40.5, -40.5, -40.5);
        arena.setRotationDegrees(0, 0.6);
        sleep(100);
        //robot.moveWithMap(3.3-40.5, -3.3-40.5, -3.3-40.5, 3.3-40.5);
        robot.move(3.3, -3.3, -3.3, 3.3);
        arena.setRotationDegrees(0, 0.6);
        robot.openClaw();
        sleep(100);
    }

    void macroP2(Arena arena, RobotController robot) {
        //robot.moveWithMap(-40.5, -40.5, -40.5, -40.5);
        robot.move(-3.3, 3.3, 3.3, -3.3);
        arena.setRotationDegrees(0, 0.6);
        sleep(100);
        //robot.moveWithMap(0, 0, 0, 0);
        robot.move(40.5, 40.5, 40.5, 40.5);
        arena.setRotationDegrees(0, 0.6);
        robot.setLiftHeight(0, false);
    }

    @Override
    public void runOpMode() {
        Arena arena = new Arena(hardwareMap, RuntimeType.HARDCODED_AUTO, Side.LEFT);
        RobotController robot = arena.getRobot();

        ElapsedTime time = new ElapsedTime();

        waitForStart();
        time.reset();

        robot.setDriveSpeed(0.4);
        macroP1(arena, robot);
        macroP2(arena, robot);
    }
}