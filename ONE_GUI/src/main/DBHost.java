package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import playfield.PlayFieldGraphic;
import core.Coord;

public class DBHost {
	public String name;
	public double diameter;
	public Coord centerCoord;
	public int nodecount;

	public DBHost(String name, Coord coord, double radius) {
		this.name = name;
		this.centerCoord = coord;
		this.diameter = radius;
	}
	
	public void setNodeCount(int nodecount) {
		this.nodecount=nodecount;
	}

	public void setRadius(double r) {
		this.diameter = r;
	}

	boolean contains(Coord coord) {
		// System.out.println("true");
		double x = PlayFieldGraphic.scale(coord.getX() - centerCoord.getX());
		double y = PlayFieldGraphic.scale(coord.getY() - centerCoord.getY());
		// System.out.println(x*x+" "+y+"");
		double r = PlayFieldGraphic.scale(diameter / 2);
		if (x * x + y * y <= r * r) {
			return true;
		}
		return false;
	}

	public void draw(Graphics2D g2) {
		Ellipse2D.Double circle = new Ellipse2D.Double(
				scale(this.centerCoord.getX() - 5),
				scale(this.centerCoord.getY() - 5), scale(10), scale(10));
		g2.setColor(Color.RED);
		g2.fill(circle);

		Ellipse2D.Double range = new Ellipse2D.Double(
				scale(this.centerCoord.getX() - this.diameter / 2),
				scale(this.centerCoord.getY() - this.diameter / 2),
				scale(this.diameter), scale(this.diameter));
		g2.setColor(Color.GREEN);
		g2.draw(range);

		g2.setColor(Color.BLACK);
		g2.drawString(this.name, scale(this.centerCoord.getX() - 5),
				scale(this.centerCoord.getY() - 5 - 10));
	}

	int scale(double value) {
		return PlayFieldGraphic.scale(value);
	}

	public void drawHovered(Graphics2D g2) {
		Color tRlackColor = new Color(0.5f, 1f, 0, 0.5f);
		Ellipse2D.Double range = new Ellipse2D.Double(
				scale(this.centerCoord.getX() - this.diameter / 2),
				scale(this.centerCoord.getY() - this.diameter / 2),
				scale(this.diameter), scale(this.diameter));
		g2.setColor(tRlackColor);
		g2.fill(range);
	}
	
	public void drawColorRange(Graphics2D g2,Color color) {
		Color tRlackColor = new Color(0.5f, 1f, 0, 0.5f);
		Ellipse2D.Double range = new Ellipse2D.Double(
				scale(this.centerCoord.getX() - this.diameter / 2),
				scale(this.centerCoord.getY() - this.diameter / 2),
				scale(this.diameter), scale(this.diameter));
		g2.setColor(color);
		g2.fill(range);
	}
	

	Color tblackColor = new Color(0, 0, 0, 0.5f);

	public void drawSelected(Graphics2D g2) {
		Ellipse2D.Double range = new Ellipse2D.Double(
				scale(this.centerCoord.getX() - this.diameter / 2),
				scale(this.centerCoord.getY() - this.diameter / 2),
				scale(this.diameter), scale(this.diameter));
		g2.setColor(tblackColor);
		g2.fill(range);
	}
}
