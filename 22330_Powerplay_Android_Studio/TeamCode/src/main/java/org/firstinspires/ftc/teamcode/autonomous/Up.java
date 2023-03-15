package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;

@Autonomous(name = "Up", group = "Inits")
public class Up extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor shoulder = (DcMotor) hardwareMap.get("shoulder");
        TouchSensor limitSwitch = (TouchSensor) hardwareMap.get("limitSwitch");

        waitForStart();
        shoulder.setPower(-0.4);
        while (!limitSwitch.isPressed()) {}
        shoulder.setPower(0);
    }
}