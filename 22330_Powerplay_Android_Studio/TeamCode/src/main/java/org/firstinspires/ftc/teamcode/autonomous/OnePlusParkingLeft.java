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
import org.firstinspires.ftc.teamcode.classes.Global;
import org.firstinspires.ftc.teamcode.classes.RobotController;
import org.firstinspires.ftc.teamcode.classes.RuntimeType;
import org.firstinspires.ftc.teamcode.classes.Side;

import java.util.List;

@Autonomous(name = "One Plus Parking Left", group = "Experimental")
public class OnePlusParkingLeft extends LinearOpMode {
    private static final String TFOD_MODEL_FILE = "model_20230112_080725.tflite";

    private static final String[] LABELS = {
            "1monkey",
            "2omega",
            "3banana"
    };

    private static final String VUFORIA_KEY =
            "ATZ/F4X/////AAABmSjnLIM91kSRo8TfH6CpvkpQb02HUOXzsAmc9sWr5aQKwBP0+GpVCddkSd7qVIgzYGRsutM1OEr4dRHyoy7G3gE8kovM+mnw5nVVkEJQEOhXlUt8ZN23VxVEMHO9qDIcH4vEv6w105kXo9FLJlikfRmKzVjMF/YAS4bU9UQVYpVzXCrEaoSE67McYRahSc3JfFmVkMqUCS2DDqyBC3MkN/YsO+EPmjz4iDIGz9HkSHkxylCOQ3rSHZQwZoGyrPJfkpl4XJoH+dKIawL3KeEWbMOIwDFR/IECVa8SNEeeaThDF3pvha2lTtdtgh5XLIcdSi27UQVTnaaM+5/G2gHLPMQ4n3DHIg4CQvmChLZTwD65";

    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;

    @Override
    public void runOpMode() {
        Arena arena = new Arena(hardwareMap, RuntimeType.HARDCODED_AUTO, Side.LEFT);
        RobotController robot = arena.getRobot();

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
            double recognitionLimit = 5;
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

            robot.setDriveSpeed(0.7);

            robot.closeClaw();
            sleep(1000);
            robot.setLiftHeight(34, true);
            robot.liftBrake();
            arena.moveClawToPos(new double[]{Global.leftJunction[0], arena.getClawPos(Global.clawOffset)[1]}, true);
            arena.moveClawToPos(new double[]{arena.getClawPos(Global.clawOffset)[0], Global.leftJunction[1]}, true);
            robot.openClaw();
            sleep(1000);

            robot.setDriveSpeed(0.5);

            arena.moveToSquare(2, 3, true);

            if (label.equals("1monkey")) {
                arena.moveToSquare(1, 3, false);
            } else if (label.equals("3banana")) {
                arena.moveToSquare(3, 3, false);
            }
            arena.setRotationDegrees(0, 0.6);
        }
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
