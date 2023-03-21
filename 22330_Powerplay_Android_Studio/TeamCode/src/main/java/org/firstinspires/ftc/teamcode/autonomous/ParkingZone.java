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
import org.firstinspires.ftc.teamcode.classes.RuntimeType;

import java.util.List;

@Autonomous(name = "Parking Zone", group = "Experimental")
public class ParkingZone extends LinearOpMode {

    // update to new tfod model when available
    private static final String TFOD_MODEL_FILE = "model_20230112_080725.tflite";

    // update labels when model is updated
    private static final String[] LABELS = {"1monkey", "2omega", "3banana"};

    private static final String VUFORIA_KEY = "ATZ/F4X/////AAABmSjnLIM91kSRo8TfH6CpvkpQb02HUOXzsAmc9sWr5aQKwBP0+GpVCddkSd7qVIgzYGRsutM1OEr4dRHyoy7G3gE8kovM+mnw5nVVkEJQEOhXlUt8ZN23VxVEMHO9qDIcH4vEv6w105kXo9FLJlikfRmKzVjMF/YAS4bU9UQVYpVzXCrEaoSE67McYRahSc3JfFmVkMqUCS2DDqyBC3MkN/YsO+EPmjz4iDIGz9HkSHkxylCOQ3rSHZQwZoGyrPJfkpl4XJoH+dKIawL3KeEWbMOIwDFR/IECVa8SNEeeaThDF3pvha2lTtdtgh5XLIcdSi27UQVTnaaM+5/G2gHLPMQ4n3DHIg4CQvmChLZTwD65";

    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;

    @Override
    public void runOpMode() {
        Arena arena = new Arena(hardwareMap, RuntimeType.HARDCODED_AUTO, false);

        initVuforia();
        initTfod();
        if (tfod != null) {
            tfod.activate();
            tfod.setZoom(1.0, 16.0 / 9.0);
        }
        telemetry.addLine(">Press Play to start op mode");
        telemetry.update();
        waitForStart();

        ElapsedTime time = new ElapsedTime();

        if (opModeIsActive()) {
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            double recognitionLimit = 2;
            while (opModeIsActive() && time.seconds() < recognitionLimit && (updatedRecognitions == null || updatedRecognitions.size() == 0)) {
                updatedRecognitions = tfod.getUpdatedRecognitions();
            }

            String label;
            if (time.seconds() >= recognitionLimit || updatedRecognitions.size() == 0) {
                label = LABELS[2];
            } else {
                label = updatedRecognitions.get(0).getLabel();
            }

            telemetry.addData("recognition: ", label);
            telemetry.update();

            arena.moveToSquare(5, 3, false);

            // if label is 2omega the robot is already in the right place
            if (label.equals("1monkey")) {
                arena.moveToSquare(4, 3, false);
            } else if (label.equals("3banana")) {
                arena.moveToSquare(6, 3, false);
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