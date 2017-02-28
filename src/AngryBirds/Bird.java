package AngryBirds;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;

public class Bird extends Images{
	 
	private double initX, initY;
	private double  velocityX, velocityY;

	public Bird(String im) throws IOException{
		super(im);
	}
	
	public double getInitX() {
		return initX;
	}

	public void setInitX(double initX) {
		this.initX = initX;
	}

	public double getInitY() {
		return initY;
	}

	public void setInitY(double initY) {
		this.initY = initY;
	}

	public double getVelocityX() {
		return velocityX;
	}

	public void setVelocityX(double velocityX) {
		this.velocityX = velocityX;
	}

	public double getVelocityY() {
		return velocityY;
	}

	public void setVelocityY(double velocityY) {
		this.velocityY = velocityY;
	}
}
