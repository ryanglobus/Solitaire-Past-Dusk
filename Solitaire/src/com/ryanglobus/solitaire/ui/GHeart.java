package com.ryanglobus.solitaire.ui;

import java.awt.Color;

import acm.graphics.GPolygon;

public class GHeart extends GPolygon {
	private static final long serialVersionUID = 4420900231424415599L;

	public GHeart(double width, double height) {
		this(0, 0, width, height);
	}
	
	public GHeart(double x, double y, double width, double height) {
		super(x, y);
		if (width < 0 || height < 0)
			throw new IllegalArgumentException("Diamond with and height must be >0.");
		addVertex(0, height / 3);
		addEdge(width / 2, 2.0 * height / 3);
		addEdge(width / 2, -2.0 * height / 3);
		addArc(width / 2, 2.0 * height / 3, 0, 180);
		addArc(width / 2, 2.0 * height / 3, 0, 180);
		setColor(Color.RED);
		setFillColor(Color.RED);
		setFilled(true);
	}
}
