package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;


@Autonomous(name = "Reset Servo", group = "Inits")
public class ResetServo extends LinearOpMode {
    // Claw systems
    private Servo claw;

    @Override
    public void runOpMode() {
        claw = (Servo)hardwareMap.get("claw");
        
        waitForStart();
        claw.setPosition(0);
        sleep(200000);
    }
}
