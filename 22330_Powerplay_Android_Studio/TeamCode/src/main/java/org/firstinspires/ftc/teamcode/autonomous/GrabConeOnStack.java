package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.classes.Arena;
import org.firstinspires.ftc.teamcode.classes.RobotController;

import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "Grab Cone On Stack", group = "Experimental")
public class GrabConeOnStack extends LinearOpMode {
    private RobotController robot;
    private Arena arena;

    @Override
    public void runOpMode() {
        arena = new Arena(hardwareMap, false);
        robot = arena.getRobot();
        
        waitForStart();

        robot.setDriveSpeed(0.7);

        arena.moveToSquare(5, 3, true);
        
        arena.moveToSquare(6, 3, true);
        
        arena.grabConeOnStack(270, 5);
        
        arena.placeConeOnJunction(45);
    }
}