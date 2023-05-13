package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.classes.Arena;
import org.firstinspires.ftc.teamcode.classes.RobotController;
import org.firstinspires.ftc.teamcode.classes.RuntimeType;

import java.util.List;

@Autonomous(name = "One Plus Parking Right")
public class OnePlusParkingRight extends LinearOpMode {
    private static final String TFOD_MODEL_FILE = "model_20230112_080725.tflite";

    private static final String[] LABELS = {"1monkey", "2omega", "3banana"};

    private static final String VUFORIA_KEY = "ATZ/F4X/////AAABmSjnLIM91kSRo8TfH6CpvkpQb02HUOXzsAmc9sWr5aQKwBP0+GpVCddkSd7qVIgzYGRsutM1OEr4dRHyoy7G3gE8kovM+mnw5nVVkEJQEOhXlUt8ZN23VxVEMHO9qDIcH4vEv6w105kXo9FLJlikfRmKzVjMF/YAS4bU9UQVYpVzXCrEaoSE67McYRahSc3JfFmVkMqUCS2DDqyBC3MkN/YsO+EPmjz4iDIGz9HkSHkxylCOQ3rSHZQwZoGyrPJfkpl4XJoH+dKIawL3KeEWbMOIwDFR/IECVa8SNEeeaThDF3pvha2lTtdtgh5XLIcdSi27UQVTnaaM+5/G2gHLPMQ4n3DHIg4CQvmChLZTwD65";

    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;

    String detected = new String("3banana");

    @Override
    public void runOpMode() {
        Arena arena = new Arena(hardwareMap, RuntimeType.HARDCODED_AUTO);
        RobotController robot = arena.getRobot();
        robot.chassis.reverso();

        initVuforia();
        initTfod();
        if (tfod != null) {
            tfod.activate();
            tfod.setZoom(1.0, 16.0 / 9.0);
        }

        List<Recognition> updatedRecognitions;
        while (!isStarted() && !isStopRequested()) {
            updatedRecognitions = tfod.getUpdatedRecognitions();

            if (updatedRecognitions != null && updatedRecognitions.size() > 0) {
                detected = updatedRecognitions.get(0).getLabel();
            } else if (updatedRecognitions != null) {
                detected = new String("3banana");
            }

            telemetry.addData("detected", detected);
            telemetry.update();
        }

        if (opModeIsActive()) {
            robot.closeClaw();
            sleep(1000);
            robot.setLiftHeight(35, true);
            robot.liftBrake();
            robot.move(2, 2, 2, 2);
            arena.setRotationDegrees(0, 0.4);
            robot.move(-24, 24, 24, -24);
            arena.setRotationDegrees(0, 0.2);
            robot.move(61, 61, 61, 61);
            arena.setRotationDegrees(0, 0.4);
            robot.move(6.25, -6.25, -6.25, 6.25);
            arena.setRotationDegrees(0, 0.4);
            robot.openClaw();
            sleep(1000);
            robot.move(-4.25, 4.25, 4.25, -4.25);
            arena.setRotationDegrees(0, 0.4);
            robot.move(-14, -14, -14, -14);
            arena.setRotationDegrees(0, 0.4);

            // if label is 1monkey the robot is already in the right place
            if (detected.equals("2omega")) {
                robot.move(20, -20, -20, 20);
                arena.setRotationDegrees(0, 0.2);
            } else if (detected.equals("3banana")) {
                robot.move(45, -45, -45, 45);
                arena.setRotationDegrees(0, 0.2);
            }
        }
    }

    private void initVuforia() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        vuforia = ClassFactory.getInstance().createVuforia(parameters);
    }

    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.75f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 300;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);

        tfod.loadModelFromFile(TFOD_MODEL_FILE, LABELS);
    }
}