package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.classes.Arena;
import org.firstinspires.ftc.teamcode.classes.RobotController;

import java.util.List;

@Autonomous(name = "Preload", group = "Experimental")
public class Preload extends LinearOpMode {
    // update to new tfod model when available
    private static final String TFOD_MODEL_FILE = "model_20230112_080725.tflite";
    // update labels when model is updated
    private static final String[] LABELS = {
            "1monkey",
            "2omega",
            "3banana"
    };
    private static final String VUFORIA_KEY =
            "ATZ/F4X/////AAABmSjnLIM91kSRo8TfH6CpvkpQb02HUOXzsAmc9sWr5aQKwBP0+GpVCddkSd7qVIgzYGRsutM1OEr4dRHyoy7G3gE8kovM+mnw5nVVkEJQEOhXlUt8ZN23VxVEMHO9qDIcH4vEv6w105kXo9FLJlikfRmKzVjMF/YAS4bU9UQVYpVzXCrEaoSE67McYRahSc3JfFmVkMqUCS2DDqyBC3MkN/YsO+EPmjz4iDIGz9HkSHkxylCOQ3rSHZQwZoGyrPJfkpl4XJoH+dKIawL3KeEWbMOIwDFR/IECVa8SNEeeaThDF3pvha2lTtdtgh5XLIcdSi27UQVTnaaM+5/G2gHLPMQ4n3DHIg4CQvmChLZTwD65";
    private RobotController robot;
    private Arena arena;
    private String label;
    private ElapsedTime time;
    private final double recognition_max = 2;
    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;

    @Override
    public void runOpMode() {
        arena = new Arena(hardwareMap, false);
        robot = arena.getRobot();

        initVuforia();
        initTfod();
        if (tfod != null) {
            tfod.activate();
            tfod.setZoom(1.0, 16.0 / 9.0);
        }
        telemetry.addLine(">Press Play to start op mode");
        telemetry.update();
        waitForStart();


        robot.setShoulderDegrees(0, false);
        robot.openHand();

        time = new ElapsedTime();

        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
        while (opModeIsActive() && time.seconds() < recognition_max && (updatedRecognitions == null || updatedRecognitions.size() == 0)) {
            updatedRecognitions = tfod.getUpdatedRecognitions();
        }

        String label;
        if (time.seconds() >= recognition_max || updatedRecognitions.size() == 0 || updatedRecognitions == null) {
            label = LABELS[2]; // 3banana
        } else {
            label = updatedRecognitions.get(0).getLabel();
        }

        telemetry.addData("Detected label", label);
        telemetry.update();
    }

    private void initVuforia() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        vuforia = ClassFactory.getInstance().createVuforia(parameters);
    }

    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.75f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 300;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);

        tfod.loadModelFromFile(TFOD_MODEL_FILE, LABELS);
    }
}