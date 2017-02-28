package AngryBirds;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;



public class Images extends Panel{
	
	protected double xs, ys;
	protected double positionX, positionY;
	protected Image image=null;
	
	public Images(String im) throws IOException{
		image=ImageIO.read(new File(im));
		xs=50;
		ys=50;
	}

	public double getXs() {
		return xs;
	}

	public void setXs(double x) {
		this.xs = xs;
	}

	public double getYs() {
		return ys;
	}

	public void setYs(double y) {
		this.ys = ys;
	}

	public double getPositionX() {
		return positionX;
	}

	public void setPositionX(double positionX) {
		this.positionX = positionX;
	}

	public double getPositionY() {
		return positionY;
	}

	public void setPositionY(double positionY) {
		this.positionY = positionY;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(String im) throws IOException {
		this.image=ImageIO.read(new File(im));
	}	
}
