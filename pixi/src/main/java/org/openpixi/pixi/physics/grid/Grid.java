package org.openpixi.pixi.physics.grid;

import java.util.ArrayList;
import org.openpixi.pixi.physics.Simulation;
import org.openpixi.pixi.physics.interpolation.Interpolator;
import org.openpixi.pixi.physics.grid.*;

public class Grid {
	
	public Simulation s;
	public Interpolator interp;
	
	/**Contains interpolated fields for each particle*/
	public ArrayList<Particle2DData> particledata;
	
	/**Electric current in x-Direction*/
	public double [][] jx;
	/**Electric current in y-Direction*/
	public double [][] jy;

	/**Electric charge sum of a cell*/
	public double [][] rho;
	
	/**Electric field in x direction*/
	public double [][] Ex;
	/**Electric field in y direction*/
	public double [][] Ey;
	/**Magnetic field in z direction*/
	public double [][] Bz;
	
	/**Number of cells in x direction*/
	public int numCellsX;
	/**Number of cells in x direction*/
	public int numCellsY;
	
	public double cellWidth;
	public double cellHeight;
	
	public Grid(Simulation s) {
		
		this.s = s;
		
	}
	
	public void changeDimension(int x, int y) {
		
	}
	
	public void updateGrid() {
		
	}

}