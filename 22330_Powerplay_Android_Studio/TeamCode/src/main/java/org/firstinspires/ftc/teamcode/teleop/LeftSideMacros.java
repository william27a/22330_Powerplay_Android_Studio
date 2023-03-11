package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.classes.Arena;
import org.firstinspires.ftc.teamcode.classes.Global;
import org.firstinspires.ftc.teamcode.classes.RobotController;

@TeleOp(name = "Left Side Macros", group = "Experimental")
public class LeftSideMacros extends LinearOpMode {
    // Drive systems
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    
    private double x;
    private double y;
    private double pivot;
    private double normal;

    // Shoulder systems
    private DcMotor shoulder;
    
    // Arm systems
    private DcMotor arm;

    // Hand systems
    private Servo hand;
    
    // Lift systems
    private DcMotor lift;
    private boolean liftWasStatic = false;
    
    // Claw systems
    private Servo claw;
    
    // Gyroscope systems
    private static RevHubOrientationOnRobot.LogoFacingDirection[] logoFacingDirections
        = RevHubOrientationOnRobot.LogoFacingDirection.values();
    private static RevHubOrientationOnRobot.UsbFacingDirection[] usbFacingDirections
        = RevHubOrientationOnRobot.UsbFacingDirection.values();
    
    private IMU imu;
    private IMU.Parameters parameters;

    private RobotController robot;
    private Arena arena;
    
    // Macros
    private int leftStackCones = 5;
    private int rightStackCones = 5;
    private int junction = 3;
    
    /*private void extendToStack(int addCones) {
        double leftDistance = (Math.abs(arena.getRotationDegrees() - 90)) % 360;
        double rightDistance = (Math.abs(arena.getRotationDegrees() - 270)) % 360;
        
        if (leftDistance < rightDistance) {
            leftStackCones += addCones;
            
            robot.openHand();
        
            double height = Global.coneHeightFromStack(leftStackCones) - Global.SHOULDER_OFF_THE_GROUND + 2;
            double length = ((Global.TILE_LENGTH - Global.ROBOT_LENGTH)/2) + (Global.SHOULDER_BACK_FROM_CENTER + (Global.ROBOT_LENGTH/2)) + Global.CORNER_TILE_LENGTH - Global.CONE_RADIUS;
            double width = Global.SHOULDER_LEFT_FROM_CENTER;
            double topDownHypotenuse = Global.getHypotenuse(length, width);
            double totalHypotenuse = Global.getHypotenuse(height, topDownHypotenuse);
            
            arena.setShoulderRadians(Math.atan(height/topDownHypotenuse), true);
            robot.setHandZ(totalHypotenuse, true);
            
            leftStackCones--;
        } else {
            rightStackCones += addCones;
            robot.openHand();
        
            double height = Global.coneHeightFromStack(rightStackCones) - Global.SHOULDER_OFF_THE_GROUND + 2;
            double length = ((Global.TILE_LENGTH - Global.ROBOT_LENGTH)/2) + (Global.SHOULDER_BACK_FROM_CENTER + (Global.ROBOT_LENGTH/2)) + Global.CORNER_TILE_LENGTH - Global.CONE_RADIUS;
            double width = Global.SHOULDER_LEFT_FROM_CENTER;
            double topDownHypotenuse = Global.getHypotenuse(length, width);
            double totalHypotenuse = Global.getHypotenuse(height, topDownHypotenuse);
            
            arena.setShoulderRadians(Math.atan(height/topDownHypotenuse), true);
            robot.setHandZ(totalHypotenuse, true);
            
            rightStackCones--;
        }
    }*/
    
    /*private void extendToJunction(int junction) {
        double width = (Global.TILE_LENGTH / 2) - Global.SHOULDER_LEFT_FROM_CENTER;
        double length  = (Global.TILE_LENGTH / 2);
        
        double topDownHypotenuse = Global.getHypotenuse(width, length);
        
        double height = -Global.SHOULDER_OFF_THE_GROUND + 2;
        
        if (junction == 1) {
            height += Global.LOW_JUNCTION_HEIGHT;
        } else if (junction == 2) {
            height += Global.MEDIUM_JUNCTION_HEIGHT;
        } else {
            height += Global.HIGH_JUNCTION_HEIGHT;
        }
        
        double totalHypotenuse = Global.getHypotenuse(height, topDownHypotenuse);
        
        arena.setShoulderRadians(Math.atan(height/topDownHypotenuse), true);
        robot.setHandZ(totalHypotenuse, true);
    }*/

    @Override
    public void runOpMode() {
        frontLeft = (DcMotor)hardwareMap.get("frontLeft");
        frontRight = (DcMotor)hardwareMap.get("frontRight");
        backLeft = (DcMotor)hardwareMap.get("backLeft");
        backRight = (DcMotor)hardwareMap.get("backRight");
        
        shoulder = (DcMotor)hardwareMap.get("shoulder");
        arm = (DcMotor)hardwareMap.get("arm");
        hand = (Servo)hardwareMap.get("hand");
        
        lift = (DcMotor)hardwareMap.get("lift");
        claw = (Servo)hardwareMap.get("claw");
        
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        
        shoulder.setDirection(DcMotor.Direction.REVERSE);
        arm.setDirection(DcMotor.Direction.REVERSE);
        hand.setDirection(Servo.Direction.FORWARD);
        
        lift.setDirection(DcMotor.Direction.FORWARD);
        claw.setDirection(Servo.Direction.FORWARD);
        
        hand.scaleRange(0.4, 0.55);
        claw.scaleRange(0.15, 0.25);
        
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot.LogoFacingDirection logo = logoFacingDirections[0]; // Up
        RevHubOrientationOnRobot.UsbFacingDirection usb = usbFacingDirections[5]; // Right
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logo, usb);
        imu.initialize(new IMU.Parameters(orientationOnRobot));

        arena = new Arena(hardwareMap, false);
        robot = arena.getRobot();

        robot.setWheelMode(DcMotor.RunMode.RUN_USING_ENCODER);
        
        waitForStart();
        
        while (opModeIsActive()) {
            /*if (gamepad1.y) {
                extendToStack(0);
                while (!gamepad1.right_bumper) {
                    pivot = gamepad1.right_trigger - gamepad1.left_trigger;
                    x = gamepad1.left_stick_x;
                    y = -gamepad1.left_stick_y;
                    normal = Math.abs(pivot) + Math.abs(x) + Math.abs(y);
                    if (normal < 1) {
                        normal = 1;
                    }

                    // set motor powers
                    frontLeft.setPower((y+x+pivot)/normal);
                    frontRight.setPower((y-x-pivot)/normal);
                    backLeft.setPower((y-x+pivot)/normal);
                    backRight.setPower((y+x-pivot)/normal);

                    if (gamepad1.left_bumper) {
                        if (!gamepad1.right_bumper) {
                            claw.setPosition(1);
                        }
                    } else if (gamepad1.right_bumper) {
                        claw.setPosition(0);
                    }

                    if (-gamepad1.right_stick_y != 0) {
                        if (liftWasStatic) {
                            liftWasStatic = false;
                            lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                        }
                        lift.setPower(-gamepad1.right_stick_y);
                    } else if (!liftWasStatic) {
                        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                        lift.setTargetPosition((int) 0);
                        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                        lift.setPower(0.3);
                        liftWasStatic = true;
                    }
                    
                    if (gamepad1.x) {
                        extendToStack(2);
                    }
                    if (gamepad1.b) {
                        extendToStack(0);
                    }
                }
                robot.closeHand();
                arena.setShoulderDegrees(45, false);
                robot.setHandZ(robot.getHandZ()-10, false);
                
                while (!gamepad1.b) {}
                
                robot.deactivate();
            }*/
            
            /*if (gamepad1.a) {
                extendToJunction(junction);
                while (!gamepad1.left_bumper) {
                    pivot = gamepad1.right_trigger - gamepad1.left_trigger;
                    x = gamepad1.left_stick_x;
                    y = -gamepad1.left_stick_y;
                    normal = Math.abs(pivot) + Math.abs(x) + Math.abs(y);
                    if (normal < 1) {
                        normal = 1;
                    }
                    
                    // set motor powers
                    frontLeft.setPower((y+x+pivot)/normal);
                    frontRight.setPower((y-x-pivot)/normal);
                    backLeft.setPower((y-x+pivot)/normal);
                    backRight.setPower((y+x-pivot)/normal);

                    if (gamepad1.left_bumper) {
                        if (!gamepad1.right_bumper) {
                            claw.setPosition(1);
                        }
                    } else if (gamepad1.right_bumper) {
                        claw.setPosition(0);
                    }

                    if (-gamepad1.right_stick_y != 0) {
                        if (liftWasStatic) {
                            liftWasStatic = false;
                            lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                        }
                        lift.setPower(-gamepad1.right_stick_y);
                    } else if (!liftWasStatic) {
                        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                        lift.setTargetPosition((int) 0);
                        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                        lift.setPower(0.3);
                        liftWasStatic = true;
                    }
                }
                hand.setPosition(0);
            }*/
            
            if (gamepad1.b) {
                robot.setArmZ(0, false);
                robot.setShoulderDegrees(0, false);
            }
            
            // assign values to each variable included in chassis math
            pivot = gamepad1.right_trigger - gamepad1.left_trigger;
            x = gamepad1.left_stick_x;
            y = -gamepad1.left_stick_y;
            normal = Math.abs(pivot) + Math.abs(x) + Math.abs(y);
            if (normal < 1) {
                normal = 1;
            }
            
            // set motor powers
            frontLeft.setPower((y+x+pivot)/normal);
            frontRight.setPower((y-x-pivot)/normal);
            backLeft.setPower((y-x+pivot)/normal);
            backRight.setPower((y+x-pivot)/normal);

            if (gamepad1.left_bumper) {
                if (!gamepad1.right_bumper) {
                    claw.setPosition(1);
                }
            } else if (gamepad1.right_bumper) {
                claw.setPosition(0);
            }

            if (-gamepad1.right_stick_y != 0) {
                if (liftWasStatic) {
                    liftWasStatic = false;
                    lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                }
                lift.setPower(-gamepad1.right_stick_y);
            } else if (!liftWasStatic) {
                lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                lift.setTargetPosition((int) 0);
                lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                lift.setPower(0.3);
                liftWasStatic = true;
            }
        }
    }
}
