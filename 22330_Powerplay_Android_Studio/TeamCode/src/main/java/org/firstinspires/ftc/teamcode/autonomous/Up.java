package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "Up", group = "Inits")
public class Up extends LinearOpMode {
    private TouchSensor limitSwitch;
    private DcMotor shoulder;

    @Override
    public void runOpMode() {
        shoulder = (DcMotor)hardwareMap.get("shoulder");
        limitSwitch = (TouchSensor) hardwareMap.get("limitSwitch");

        waitForStart();
        shoulder.setPower(-0.4);
        while (!limitSwitch.isPressed()) {}
        shoulder.setPower(0);
    }
}