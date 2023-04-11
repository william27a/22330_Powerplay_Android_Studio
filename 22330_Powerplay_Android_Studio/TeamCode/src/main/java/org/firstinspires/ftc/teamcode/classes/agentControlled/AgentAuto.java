package org.firstinspires.ftc.teamcode.classes.agentControlled;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.classes.Arena;
import org.firstinspires.ftc.teamcode.classes.RobotController;
import org.firstinspires.ftc.teamcode.classes.RuntimeType;

import java.io.File;

@Autonomous(name = "AgentAuto", group = "Agent")
public class AgentAuto extends LinearOpMode {
    private File file;
    private AgentHandler agentHandler;

    private Arena arena;
    private RobotController robot;
    private ElapsedTime time;

    @Override
    public void runOpMode() {
        agentHandler = new AgentHandler();
        agentHandler.initVuforia(hardwareMap);
        agentHandler.initEnvironment("sdcard/FIRST/tflitemodels/SimpleBehavior.onnx");
        arena = new Arena(hardwareMap, RuntimeType.AGENT_CONTROLLED_AUTO, false);
        robot = arena.getRobot();
        time = new ElapsedTime();

        telemetry.addLine("init complete");
        telemetry.update();

        waitForStart();
        time.reset();

        while (true) {
            float rotation = agentHandler.runAuto((float)arena.getRotationDegrees());

            float wheelFL = rotation;
            float wheelFR = -rotation;
            float wheelBL = rotation;
            float wheelBR = -rotation;

            float[] outputs = new float[4];
            outputs[0] = wheelFL;
            outputs[1] = wheelFR;
            outputs[2] = wheelBL;
            outputs[3] = wheelBR;

            // // alter arena.getRotationDegrees() to return value from 0 to 360
            robot.handleRL(outputs);
            //robot.handleRL(agentHandler.runAuto(/*agentHandler.getFrame(), */(float)arena.getRotationDegrees()/*, (float)time.seconds()*/));

            // telemetry.addLine(interpreter.getSignatureKeys()[0]);
            // telemetry.update();
            // sleep(10000);

            // fl = forward - right + rotation
            // fr = forward + right - rotation
            // bl = forward + right + rotation
            // br = forward - right - rotation

            // forward = (fl + fr) / 2
            // right = ((fr + bl) / 2) - forward
            // rotation = fl - forward + right
        }
    }
}