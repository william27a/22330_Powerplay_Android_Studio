package org.firstinspires.ftc.teamcode.agentControlled;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.classes.Arena;
import org.firstinspires.ftc.teamcode.classes.RobotController;
import org.firstinspires.ftc.teamcode.classes.RuntimeType;

import java.io.File;

@TeleOp(name = "AgentTeleOp", group = "Agent")
public class AgentTeleOp extends LinearOpMode {
    private File file;
    private AgentHandler agentHandler;

    private Arena arena;
    private RobotController robot;
    private ElapsedTime time;

    @Override
    public void runOpMode() {
        // file = new File("org/firstinspires/ftc/teamcode/____.tflite");
        // agentHandler = new AgentHandler(file);
        agentHandler = new AgentHandler();
        agentHandler.initVuforia(hardwareMap);

        arena = new Arena(hardwareMap, RuntimeType.AGENT_CONTROLLED_AUTO, false);
        robot = arena.getRobot();

        waitForStart();

        time.reset();

        while (true) {
            // // alter arena.getRotationDegrees() to return value from 0 to 360
            robot.handleRL(agentHandler.forward(agentHandler.getFrame(), arena.getRotationDegrees(), time.seconds()));

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