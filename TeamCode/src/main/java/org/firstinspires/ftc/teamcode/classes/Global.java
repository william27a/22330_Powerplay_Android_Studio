package org.firstinspires.ftc.teamcode.classes;

import com.qualcomm.robotcore.hardware.DcMotor;

public class Global {
    // Positions
    public final static double[] leftPos = new double[]{33.5, 8.25};
    public final static double[] rightPos = new double[]{105, 8.25};
    public final static double[] blSquare = new double[]{12.25, 12.25};

    public final static double[] leftStack = new double[]{58.875, 2};
    public final static double[] rightStack = new double[]{58.875, 139};

    public final static double[] leftJunction = new double[]{47,70.625};
    public final static double[] rightJunction = new double[]{94.5,70.5};

    public final static double[] clawOffset = new double[]{10.5, 1.5};

    // Drive systems
    public final static double DRIVE_CPI = 44.4274370516*40/40.25;
    public final static double SIDE_TUNE = 1.20954907162;

    public final static double DEGREE_TOLERANCE = 0.2;
    public final static double RADIAN_TOLERANCE = Math.toRadians(DEGREE_TOLERANCE);

    // Lift systems
    public final static double LIFT_CPI = 81;
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
    public final static double CONE_TO_CONE_HEIGHT = 1.375;

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
