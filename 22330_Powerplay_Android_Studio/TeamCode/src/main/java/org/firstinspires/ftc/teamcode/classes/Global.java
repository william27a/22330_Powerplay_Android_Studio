package org.firstinspires.ftc.teamcode.classes;

import com.qualcomm.robotcore.hardware.DcMotor;

public class Global {
    // Drive systems
    public final static double DRIVE_CPI = 44.4274370516;
    public final static double SIDE_TUNE = 1.20954907162;

    public final static double DEGREE_TOLERANCE = 0.2;
    public final static double RADIAN_TOLERANCE = Math.toRadians(DEGREE_TOLERANCE);
    // Shoulder systems
    public final static double SHOULDER_CPD = 29.32222222218357;
    public final static double SHOULDER_CPR = 1680.03957928;
    public final static double SHOULDER_OFF_THE_GROUND = 6.25;
    public final static double SHOULDER_LEFT_FROM_CENTER = 1.25;
    public final static double SHOULDER_BACK_FROM_CENTER = (13.75 / 2) + 0.25;
    public final static double SHOULDER_TOLERANCE = 4;
    // Arm systems
    public final static double ARM_CPI = 252.407302413;
    public final static double ARM_BOTTOM = 0;
    public final static double ARM_LENGTH = 32;
    // Hand systems
    public final static double HAND_AWAY_FROM_ARM = 13;
    public final static double HAND_LENGTH = 4.5;
    // Lift systems
    public final static double LIFT_CPI = 100;
    public final static double LIFT_BOTTOM = 0.0;
    public final static double LIFT_LENGTH = 38.5;
    // Claw systems
    public final static double MIN_CLAW_HEIGHT = 1.25;
    public final static double MAX_CLAW_HEIGHT = 39.75;
    public final static double SAFE_CLAW_HEIGHT = 6.0;
    // Robot
    public final static double ROBOT_LENGTH = 13.75; // tunable
    public final static double ROBOT_WIDTH = 11; // tunable
    public final static double OFF_THE_BACK = (ROBOT_LENGTH / 2) + (2.75);
    public final static double ON_THE_LEFT = -1.5;
    // Tiles
    public final static double TILE_LENGTH = 23.75;
    public final static double CORNER_TILE_LENGTH = 22 + (7 / 8);
    public final static double TILE_DIFFERENCE = TILE_LENGTH - CORNER_TILE_LENGTH;
    // Junctions
    public final static double LOW_JUNCTION_HEIGHT = 11.0;
    public final static double MEDIUM_JUNCTION_HEIGHT = 22.0;
    public final static double HIGH_JUNCTION_HEIGHT = 33.0;
    public final static double SAFE_JUNCTION_HEIGHT = 3.0;
    public final static double LENGTH_TO_JUNCTION = 24;
    public final static double HEIGHT_TO_JUNCTION = 32;
    public final static double DEGREES_TO_JUNCTION = -13;
    // Cone stacks
    public final static double CONE_TO_CONE_HEIGHT = 1.25;
    public final static double DEGREES_TO_STACK = -2;
    public final static double LENGTH_TO_STACK = 24;

    public static double fitToRange(double x, double start, double end) {
        double length = end - start;

        while (x <= start) {
            x += length;
        }

        while (x > end) {
            x -= length;
        }

        return x;
    }

    public static boolean isClose(DcMotor motor, double tolerance) {
        double current = motor.getCurrentPosition();
        double target = motor.getTargetPosition();

        return (current < target && (current + tolerance) > target) || (current > target && (current - tolerance) < target);
    }

    public static double coneHeightFromStack(int cones) {
        return ((cones - 1) * CONE_TO_CONE_HEIGHT) + 7;
    }

    // Getting length
    public static double getHypotenuse(double height, double length) {
        return Math.sqrt((height * height) + (length * length));
    }
}
