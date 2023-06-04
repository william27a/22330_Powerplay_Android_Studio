package org.firstinspires.ftc.teamcode.classes;

public class PIDController {
    private double kP;
    private double kI;
    private double kD;

    private double errorSum, lastError;
    private double lastTime;
    private double setpoint;

    public PIDController(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;

        errorSum = 0;
        lastError = 0;
        lastTime = 0;
    }

    public double getOutput(double input) {
        double error = setpoint - input;
        double dt = System.currentTimeMillis() - lastTime;
        errorSum += error * dt;
        double dError = (error - lastError) / dt;

        double output = kP * error + kI * errorSum + kD * dError;

        lastError = error;
        lastTime = System.currentTimeMillis();

        return output;
    }

    public void setSetpoint(double setpoint) {
        this.setpoint = setpoint;

        reset();
    }

    public void reset() {
        errorSum = 0;
        lastError = 0;
        lastTime = 0;
    }
}