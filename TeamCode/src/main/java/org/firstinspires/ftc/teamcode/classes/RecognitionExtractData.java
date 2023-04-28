package org.firstinspires.ftc.teamcode.classes;

import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

public class RecognitionExtractData {
    public double center_x;
    public double center_y;
    public String label;

    public RecognitionExtractData(Recognition recognition) {
        this.center_x = Math.abs(recognition.getLeft() + recognition.getRight()) / 2;
        this.center_y = Math.abs(recognition.getTop() + recognition.getBottom()) / 2;
        this.label = recognition.getLabel();
    }
}