package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.classes.Arena;
import org.firstinspires.ftc.teamcode.classes.Global;
import org.firstinspires.ftc.teamcode.classes.RobotController;
import org.firstinspires.ftc.teamcode.classes.RuntimeType;

@Autonomous(name = "Test Auto", group = "Experimental")
public class TestAuto extends LinearOpMode {

    @Override
    public void runOpMode() {
        Arena arena = new Arena(hardwareMap, RuntimeType.HARDCODED_AUTO, Global.leftPos);
        RobotController robot = arena.getRobot();

        ElapsedTime time = new ElapsedTime();

        waitForStart();

        double startIMU = robot.getRotationDegrees();

        time.reset();
        robot.chassis.frontLeft.setPower(1);
        robot.chassis.frontRight.setPower(-1);
        robot.chassis.backLeft.setPower(1);
        robot.chassis.backRight.setPower(-1);
        while (time.seconds() < 1) {}
        robot.chassis.frontLeft.setPower(0);
        robot.chassis.frontRight.setPower(0);
        robot.chassis.backLeft.setPower(0);
        robot.chassis.backRight.setPower(0);

        telemetry.addData("RPS", Math.abs(startIMU - robot.getRotationDegrees()));
        telemetry.update();

        sleep(10000);
    }
}