package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.classes.Arena;
import org.firstinspires.ftc.teamcode.classes.RobotController;

@Autonomous(name = "Grab Cone On Stack", group = "Experimental")
public class GrabConeOnStack extends LinearOpMode {

    @Override
    public void runOpMode() {
        Arena arena = new Arena(hardwareMap, false);
        RobotController robot = arena.getRobot();

        waitForStart();

        robot.setDriveSpeed(0.7);

        arena.moveToSquare(5, 3, true);

        arena.moveToSquare(6, 3, true);

        arena.grabConeOnStack(270, 5);

        arena.placeConeOnJunction(45);
    }
}