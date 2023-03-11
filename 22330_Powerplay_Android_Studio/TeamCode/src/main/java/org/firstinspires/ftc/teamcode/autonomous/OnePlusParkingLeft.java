package org.firstinspires.ftc.teamcode.autonomous;

import java.util.*;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.classes.Arena;
import org.firstinspires.ftc.teamcode.classes.Global;
import org.firstinspires.ftc.teamcode.classes.RobotController;

import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "One Plus Parking Left", group = "Experimental")
public class OnePlusParkingLeft extends LinearOpMode {
    private double recognitionLimit = 5;
    
    private RobotController robot;
    private ElapsedTime time;
    private Arena arena;
    
    // update to new tfod model when available
    private static final String TFOD_MODEL_FILE  = "model_20230112_080725.tflite";
    
    // update labels when model is updated
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
        arena = new Arena(hardwareMap, true);
        robot = arena.getRobot();
        
        initVuforia();
        initTfod();
        if (tfod != null) {
            tfod.activate();
            tfod.setZoom(1.0, 16.0/9.0);
        }
        telemetry.addLine(">Press Play to start op mode");
        telemetry.update();
        waitForStart();
        
        time = new ElapsedTime();

        if (opModeIsActive()) {
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            while (opModeIsActive() && time.seconds() < recognitionLimit && (updatedRecognitions == null || updatedRecognitions.size() == 0)) {
                updatedRecognitions = tfod.getUpdatedRecognitions();
            }

            String label;
            if (time.seconds() >= recognitionLimit || updatedRecognitions.size() == 0 || updatedRecognitions == null) {
                label = LABELS[2];
            } else {
                label = updatedRecognitions.get(0).getLabel();
            }

            telemetry.addData("recognition: ", label);
            telemetry.update();
            
            robot.setDriveSpeed(0.7);
        
            robot.closeClaw();
            sleep(1000);
            robot.setClawHeight(4, true);
            robot.liftBrake();
            arena.moveToSquare(5, 1, true);
            arena.setRotationDegrees(0, 0.6);
            arena.moveToSquare(5, 3.521, true);
            arena.setRotationDegrees(0, 0.6);
            robot.setClawHeight(Global.HIGH_JUNCTION_HEIGHT+5, true);
            robot.liftBrake();
            arena.move(3.5, -3.5, -3.5, 3.5);
            arena.setRotationDegrees(0, 0.6);
            robot.openClaw();
            sleep(1000);
            
            robot.setDriveSpeed(0.5);
            
            arena.moveToSquare(5.15, 3, true);

            if (label == "1monkey") {
                arena.moveToSquare(4, 3, false);
            } else if (label == "3banana") {
                arena.moveToSquare(6, 3, false);
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
