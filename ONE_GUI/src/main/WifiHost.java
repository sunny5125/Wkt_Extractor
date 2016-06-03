package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

import playfield.PlayFieldGraphic;
import core.Coord;

public class WifiHost {
	public String name;
	public double radius;
	public double size;
	public Coord coord;

	public WifiHost(String name, Coord coord, double radius, double size) {
		this.name = name;
		this.coord = coord;
		this.radius = 10;
		this.size = size;
	}

	public void setRadius(double r) {
		this.radius = r;
	}
	
	public void draw(Graphics2D g2) {
		java.net.URL imgURL = getClass().getResource("images/wifi.jpg");
		try {
			Image image = ImageIO.read(this.getClass().getResource(
					"images/wifi.jpg"));
			// Image img1 =
			// Toolkit.getDefaultToolkit().getImage("images/wifi.jpg");
			g2.drawImage(image, scale(this.coord.getX() - this.size / 2),
					scale(this.coord.getY() - this.size / 2),
					scale(this.size), scale(this.size), null);

			g2.setColor(Color.BLACK);
			g2.drawString(this.name, scale(this.coord.getX()),
					scale(this.coord.getY() - 10));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	int scale(double value) {
		return PlayFieldGraphic.scale(value);
	}
}
