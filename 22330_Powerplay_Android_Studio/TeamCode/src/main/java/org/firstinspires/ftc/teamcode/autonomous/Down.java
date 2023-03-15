package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;

@Autonomous(name = "Down", group = "Inits")
public class Down extends LinearOpMode {

    @Override
    public void runOpMode() {
        TouchSensor limitSwitch = (TouchSensor) hardwareMap.get("limitSwitch");
        DcMotor shoulder = (DcMotor) hardwareMap.get("shoulder");

        waitForStart();
        shoulder.setPower(0.4);
        while (!limitSwitch.isPressed()) {}
        while (limitSwitch.isPressed()) {}
        shoulder.setPower(0);
    }
}