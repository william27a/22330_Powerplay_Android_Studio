package org.firstinspires.ftc.teamcode.agentControlled;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.tensorflow.lite.Interpreter;

import java.io.File;

@Autonomous(name = "AgentAuto", group = "Agent")
public class AgentAuto extends LinearOpMode {
    private File agentFile = new File("org/firstinspires/ftc/teamcode/testModel.onnx");
    Interpreter interpreter = new Interpreter(agentFile);


    @Override
    public void runOpMode() {
        waitForStart();
        telemetry.addLine(interpreter.getSignatureKeys()[0]);
        telemetry.update();
        sleep(10000);

        // fl = forward - right + rotation
        // fr = forward + right - rotation
        // bl = forward + right + rotation
        // br = forward - right - rotation

        // forward = (fl + fr) / 2
        // right = ((fr + bl) / 2) - forward
        // rotation = fl - forward + right
    }
}