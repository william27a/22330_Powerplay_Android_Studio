package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Autonomous(name = "Color Sensor Test")
public class ColorSensor extends LinearOpMode {
    ModernRoboticsI2cColorSensor colorSensor;

    @Override
    public void runOpMode() throws InterruptedException {
        colorSensor = (ModernRoboticsI2cColorSensor)hardwareMap.get("colorSensor");
        while (opModeIsActive()) {
            telemetry.addData("Color Number", colorSensor.readUnsignedByte(ModernRoboticsI2cColorSensor.Register.COLOR_NUMBER));
            telemetry.update();
        }
    }
}
