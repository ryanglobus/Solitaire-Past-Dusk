package com.ryanglobus.solitaire.ui;

import java.awt.Color;

import acm.graphics.GPolygon;

public class GDiamond extends GPolygon {
	private static final long serialVersionUID = -8128176321941331555L;
	
	public GDiamond(double width, double height) {
		this(0, 0, width, height);
	}
	
	public GDiamond(double x, double y, double width, double height) {
		super(x, y);
		if (width < 0 || height < 0)
			throw new IllegalArgumentException("Diamond with and height must be >0.");
		addVertex(width / 2, 0);
		addEdge(-1 * width / 2, height / 2);
		addEdge(width / 2, height / 2);
		addEdge(width / 2, -1 * height / 2);
		addEdge(-1 * width / 2, -1 * height / 2);
		setColor(Color.RED);
		setFillColor(Color.RED);
		setFilled(true);
	}
}
