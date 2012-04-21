/*
 * OpenPixi - Open Particle-In-Cell (PIC) Simulator
 * Copyright (C) 2012  OpenPixi.org
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openpixi.pixi.physics.grid;

import java.util.ArrayList;

import org.openpixi.pixi.physics.Debug;
import org.openpixi.pixi.physics.Particle2D;
import org.openpixi.pixi.physics.Simulation;
import org.openpixi.pixi.physics.fields.*;
import org.openpixi.pixi.physics.force.SimpleGridForce;


public class SimpleGrid extends Grid {

	public SimpleGrid(Simulation s) {
		
		super(s);
		interp = new CloudInCell(this);
		SimpleGridForce force = new SimpleGridForce(s);
		s.f.add(force);
		
		numCellsX = 10;
		numCellsY = 10;
		cellWidth = s.width/numCellsX;
		cellHeight = s.height/numCellsY;
		
		jx = new double[numCellsX+3][numCellsY+3];
		jy = new double[numCellsX+3][numCellsY+3];
		rho = new double[numCellsX+3][numCellsY+3];
		Ex = new double[numCellsX+3][numCellsY+3];
		Ey = new double[numCellsX+3][numCellsY+3];
		Bz = new double[numCellsX+3][numCellsY+3];
		initFields();
	}
	
	//a method to change the dimensions of the cells, i.e. the width and the height
	public void changeDimension(double width, double height, int xbox, int ybox)
	{
		numCellsX = xbox;
		numCellsY = ybox;
		
		jx = new double[numCellsX+3][numCellsY+3];
		jy = new double[numCellsX+3][numCellsY+3];
		rho = new double[numCellsX+3][numCellsY+3];
		Ex = new double[numCellsX+3][numCellsY+3];
		Ey = new double[numCellsX+3][numCellsY+3];
		Bz = new double[numCellsX+3][numCellsY+3];
		initFields();
		
		setGrid(width, height);
	}
	
	public void setGrid(double width, double height)
	{
		cellWidth = width / numCellsX;
		cellHeight = height / numCellsY;
		
		for (Particle2D p: s.particles){
			//assuming rectangular particle shape i.e. area weighting
			p.pd.cd = p.charge / (cellWidth * cellHeight);
		}
		
		//include updateGrid() and the first calculation of Fields here
	}
	
	public void updateGrid(ArrayList<Particle2D> particles)
	{
		reset();
		interp.interpolateToGrid(particles);		
		s.fsolver.step(this);
		interp.interpolateToParticle(particles);
	}
	
	public int checkCellX(Particle2D p) {
			int xCellPosition = (int) (p.x / cellWidth) + 1;
			if(xCellPosition >= numCellsX+1) {
				xCellPosition = numCellsX+1;
			} else if(xCellPosition < 0) {
					xCellPosition = 0;
			}
			
			return xCellPosition;
	}
	
	public int checkCellY(Particle2D p) {
			int yCellPosition = (int) (p.y / cellHeight) + 1;
			if(yCellPosition >= numCellsY+1) {
				yCellPosition = numCellsY+1;
			} else if(yCellPosition < 0) {
				yCellPosition = 0;
			}
			
			return yCellPosition;
			
	}

	private void reset() {
		for(int i = 0; i < numCellsX + 3; i++) {
			for(int k = 0; k < numCellsY + 3; k++) {
				jx[i][k] = 0.0;
				jy[i][k] = 0.0;
				rho[i][k] = 0.0;
			}
		}
	}
	
	private void initFields() {
		for (int i = 0; i < numCellsX + 3; i++) {
			for (int j = 0; j < numCellsY + 3; j++) {
				Ex[i][j] = 0.0;
				Ey[i][j] = 0.0;
				Bz[i][j] = 0.0;
			}
		}
	}
	
	/*
	//Dirichlet boundary conditions - will be moved to a different class later on.
	private void initPotentialDirichlet() {
		for (int i = 0; i < numCellsX; i++) {
			phi[i][0] = phi[i][numCellsY-1] = 1;
		}
		for (int i = 1; i < numCellsY-1; i++) {
			phi[0][i] = phi[numCellsX-1][i] = 1;
		}
	
		for (int i = 1; i < (numCellsX-1); i++) {
			for (int j = 1; j < (numCellsY-1); j++) {
				phi[i][j] = 0;
			}
		}
	}*/

}
